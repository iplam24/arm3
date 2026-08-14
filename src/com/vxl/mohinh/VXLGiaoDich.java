package com.vxl.mohinh;

import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.VXLTinNhan;
import com.vxl.vatpham.VXLTienTrinhDucLo;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import com.vxl.vatpham.VXLVatPham;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VXLGiaoDich {
    private static final byte LENH_RPG_GIAO_DICH = 9;
    private static final int SO_LUONG_TOI_DA_MOI_MUC = 127;
    private static final long THOI_HAN_LOI_MOI_MILLIS = 30_000L;
    private static final long THOI_HAN_GIAO_DICH_MILLIS = 180_000L;
    private static final Object KHOA = new Object();
    private static final Map<Integer, PhienGiaoDich> PHIEN_THEO_NGUOI_CHOI = new ConcurrentHashMap<>();

    private VXLGiaoDich() {
    }

    public static void xuLy(VXLNguoiChoi nguoiChoi, VXLTinNhan tinNhan) throws IOException {
        if (nguoiChoi == null || tinNhan == null) {
            return;
        }
        int hanhDong = tinNhan.boDoc().readUnsignedByte();
        switch (hanhDong) {
            case 0 -> moi(nguoiChoi, tinNhan.boDoc().readInt());
            case 1 -> chapNhan(nguoiChoi, tinNhan.boDoc().readInt());
            case 2 -> datDeNghi(nguoiChoi, tinNhan.boDoc().readByte(), tinNhan.boDoc().readInt());
            case 3 -> moKhoa(nguoiChoi);
            case 4 -> xoaVatPham(nguoiChoi, tinNhan.boDoc().readByte());
            case 5 -> khoa(nguoiChoi);
            case 7 -> xacNhan(nguoiChoi);
            default -> nguoiChoi.moHopThoaiOK("Hành động giao dịch không hợp lệ.");
        }
    }

    public static void khiRoiKhu(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        synchronized (KHOA) {
            PhienGiaoDich phien = PHIEN_THEO_NGUOI_CHOI.get(nguoiChoi.ma);
            if (phien == null) {
                return;
            }
            VXLNguoiChoi doiTac = phien.doiTac(nguoiChoi);
            xoaPhien(phien);
            guiDong(nguoiChoi);
            guiDong(doiTac);
            if (doiTac != null) {
                doiTac.moHopThoaiOK("Giao dịch đã hủy vì đối phương rời khu vực.");
            }
        }
    }

    private static void moi(VXLNguoiChoi nguoiMoi, int maNguoiNhan) {
        synchronized (KHOA) {
            VXLNguoiChoi nguoiNhan = VXLNguoiChoi.layNguoiChoiTheoMa(maNguoiNhan);
            if (!coTheBatDau(nguoiMoi, nguoiNhan)) {
                nguoiMoi.moHopThoaiOK("Không thể giao dịch với người chơi này.");
                return;
            }
            if (!donPhienMoiCuNeuCo(nguoiMoi) || !donPhienMoiCuNeuCo(nguoiNhan)) {
                nguoiMoi.moHopThoaiOK("Một trong hai người chơi đang có giao dịch khác.");
                return;
            }
            PhienGiaoDich phien = new PhienGiaoDich(nguoiMoi, nguoiNhan);
            PHIEN_THEO_NGUOI_CHOI.put(nguoiMoi.ma, phien);
            PHIEN_THEO_NGUOI_CHOI.put(nguoiNhan.ma, phien);
            guiMoi(nguoiNhan, nguoiMoi.ma);
        }
    }

    private static void chapNhan(VXLNguoiChoi nguoiNhan, int maNguoiMoi) {
        synchronized (KHOA) {
            PhienGiaoDich phien = layPhien(nguoiNhan);
            if (phien == null || phien.trangThai != TrangThai.MOI
                    || phien.nguoiNhan != nguoiNhan || phien.nguoiMoi.ma != maNguoiMoi) {
                nguoiNhan.moHopThoaiOK("Lời mời giao dịch không còn hiệu lực.");
                return;
            }
            if (!coTheBatDau(phien.nguoiMoi, phien.nguoiNhan)) {
                huyPhien(phien, "Không thể bắt đầu giao dịch lúc này.");
                return;
            }
            phien.trangThai = TrangThai.DANG_GIAO_DICH;
            phien.datLaiDeNghi();
            phien.cham();
            guiBatDau(phien.nguoiMoi, phien.nguoiNhan.ma);
            guiBatDau(phien.nguoiNhan, phien.nguoiMoi.ma);
        }
    }

    private static void datDeNghi(VXLNguoiChoi nguoiChoi, int chiSo, int soLuongHoacVang) {
        synchronized (KHOA) {
            PhienGiaoDich phien = layPhienDangGiaoDich(nguoiChoi);
            if (phien == null) {
                return;
            }
            DeNghi deNghi = phien.deNghiCua(nguoiChoi);
            if (deNghi.daKhoa || deNghi.daXacNhan) {
                nguoiChoi.moHopThoaiOK("Hãy mở khóa giao dịch trước khi thay đổi đề nghị.");
                return;
            }
            if (chiSo == -1) {
                if (soLuongHoacVang < 0 || soLuongHoacVang > nguoiChoi.vang) {
                    nguoiChoi.moHopThoaiOK("Số vàng giao dịch không hợp lệ.");
                    return;
                }
                deNghi.vang = soLuongHoacVang;
                phien.cham();
                return;
            }
            synchronized (nguoiChoi) {
                if (chiSo < 0 || nguoiChoi.itemBag == null || chiSo >= nguoiChoi.itemBag.length) {
                    nguoiChoi.moHopThoaiOK("Vị trí vật phẩm giao dịch không hợp lệ.");
                    guiXoaVatPham(nguoiChoi, chiSo);
                    return;
                }
                VXLVatPham vatPham = nguoiChoi.itemBag[chiSo];
                if (vatPham == null || vatPham.mau == null || soLuongHoacVang <= 0
                        || soLuongHoacVang > vatPham.soLuong || soLuongHoacVang > SO_LUONG_TOI_DA_MOI_MUC
                        || nguoiChoi.vatPhamCoTrongBalo(vatPham)) {
                    nguoiChoi.moHopThoaiOK("Vật phẩm hoặc số lượng giao dịch không hợp lệ.");
                    guiXoaVatPham(nguoiChoi, chiSo);
                    return;
                }
                deNghi.vatPhams.put(chiSo, new MucDeNghi(chiSo, soLuongHoacVang));
                phien.cham();
            }
        }
    }

    private static void xoaVatPham(VXLNguoiChoi nguoiChoi, int chiSo) {
        synchronized (KHOA) {
            PhienGiaoDich phien = layPhienDangGiaoDich(nguoiChoi);
            if (phien == null) {
                return;
            }
            DeNghi deNghi = phien.deNghiCua(nguoiChoi);
            if (deNghi.daKhoa || deNghi.daXacNhan) {
                nguoiChoi.moHopThoaiOK("Hãy mở khóa giao dịch trước khi bỏ vật phẩm.");
                return;
            }
            deNghi.vatPhams.remove(chiSo);
            phien.cham();
        }
    }

    private static void khoa(VXLNguoiChoi nguoiChoi) {
        synchronized (KHOA) {
            PhienGiaoDich phien = layPhienDangGiaoDich(nguoiChoi);
            if (phien == null) {
                return;
            }
            DeNghi deNghi = phien.deNghiCua(nguoiChoi);
            if (deNghi.daKhoa) {
                return;
            }
            String loi;
            synchronized (nguoiChoi) {
                VXLTienTrinhDucLo.capNhat(nguoiChoi);
                loi = kiemTraVaChotDeNghi(nguoiChoi, deNghi);
            }
            if (loi != null) {
                datLaiPhien(phien);
                nguoiChoi.moHopThoaiOK(loi);
                return;
            }
            deNghi.daKhoa = true;
            deNghi.daXacNhan = false;
            phien.cham();
            guiDoiPhuongDaKhoa(phien.doiTac(nguoiChoi), nguoiChoi, deNghi);
        }
    }

    private static void moKhoa(VXLNguoiChoi nguoiChoi) {
        synchronized (KHOA) {
            PhienGiaoDich phien = layPhienDangGiaoDich(nguoiChoi);
            if (phien == null) {
                return;
            }
            datLaiPhien(phien);
        }
    }

    private static void xacNhan(VXLNguoiChoi nguoiChoi) {
        synchronized (KHOA) {
            PhienGiaoDich phien = layPhienDangGiaoDich(nguoiChoi);
            if (phien == null) {
                return;
            }
            DeNghi deNghi = phien.deNghiCua(nguoiChoi);
            DeNghi deNghiDoiTac = phien.deNghiCua(phien.doiTac(nguoiChoi));
            if (!deNghi.daKhoa || !deNghiDoiTac.daKhoa) {
                nguoiChoi.moHopThoaiOK("Cả hai người chơi phải khóa giao dịch trước.");
                return;
            }
            deNghi.daXacNhan = true;
            phien.cham();
            if (deNghiDoiTac.daXacNhan) {
                hoanTat(phien);
            }
        }
    }

    private static void hoanTat(PhienGiaoDich phien) {
        VXLNguoiChoi nguoiThuNhat = phien.nguoiMoi.ma <= phien.nguoiNhan.ma
                ? phien.nguoiMoi : phien.nguoiNhan;
        VXLNguoiChoi nguoiThuHai = nguoiThuNhat == phien.nguoiMoi
                ? phien.nguoiNhan : phien.nguoiMoi;
        synchronized (nguoiThuNhat) {
            synchronized (nguoiThuHai) {
                if (!coTheBatDau(phien.nguoiMoi, phien.nguoiNhan)) {
                    huyPhien(phien, "Giao dịch đã hủy vì hai người chơi không còn ở cùng khu vực.");
                    return;
                }
                VXLTienTrinhDucLo.capNhat(phien.nguoiMoi);
                VXLTienTrinhDucLo.capNhat(phien.nguoiNhan);
                String loiNguoiMoi = kiemTraDeNghiDaKhoa(phien.nguoiMoi, phien.deNghiNguoiMoi);
                String loiNguoiNhan = kiemTraDeNghiDaKhoa(phien.nguoiNhan, phien.deNghiNguoiNhan);
                if (loiNguoiMoi != null || loiNguoiNhan != null) {
                    datLaiPhien(phien);
                    phien.nguoiMoi.moHopThoaiOK(loiNguoiMoi != null ? loiNguoiMoi : "Đề nghị của đối phương đã thay đổi.");
                    phien.nguoiNhan.moHopThoaiOK(loiNguoiNhan != null ? loiNguoiNhan : "Đề nghị của đối phương đã thay đổi.");
                    return;
                }

                KetQuaChuyen ketQua = taoKetQuaChuyen(phien);
                if (ketQua.loi != null) {
                    datLaiPhien(phien);
                    phien.nguoiMoi.moHopThoaiOK(ketQua.loi);
                    phien.nguoiNhan.moHopThoaiOK(ketQua.loi);
                    return;
                }

                try {
                    VXLCoSoDuLieu.withTransaction(conn -> {
                        phien.nguoiMoi.luuTrangThaiGiaoDich(conn, ketQua.vangNguoiMoi, ketQua.tuiNguoiMoi);
                        phien.nguoiNhan.luuTrangThaiGiaoDich(conn, ketQua.vangNguoiNhan, ketQua.tuiNguoiNhan);
                    });
                }
                catch (SQLException | RuntimeException ex) {
                    Logger.getLogger(VXLGiaoDich.class.getName()).log(Level.SEVERE,
                            "Không thể lưu giao dịch giữa " + phien.nguoiMoi.ma + " và " + phien.nguoiNhan.ma + ".", ex);
                    huyPhien(phien, "Không thể lưu giao dịch lúc này. Vật phẩm và vàng chưa bị thay đổi.");
                    return;
                }

                phien.nguoiMoi.vang = ketQua.vangNguoiMoi;
                phien.nguoiMoi.itemBag = ketQua.tuiNguoiMoi;
                phien.nguoiNhan.vang = ketQua.vangNguoiNhan;
                phien.nguoiNhan.itemBag = ketQua.tuiNguoiNhan;
                xoaPhien(phien);
            }
        }

        guiDong(phien.nguoiMoi);
        guiDong(phien.nguoiNhan);
        guiLaiTaiSan(phien.nguoiMoi);
        guiLaiTaiSan(phien.nguoiNhan);
        phien.nguoiMoi.moHopThoaiOK("Giao dịch thành công.");
        phien.nguoiNhan.moHopThoaiOK("Giao dịch thành công.");
        VXLQuanLyMayChu.log("[TRADE] success " + phien.nguoiMoi.ma + " <-> " + phien.nguoiNhan.ma);
    }

    private static KetQuaChuyen taoKetQuaChuyen(PhienGiaoDich phien) {
        long vangNguoiMoi = (long)phien.nguoiMoi.vang - phien.deNghiNguoiMoi.vang + phien.deNghiNguoiNhan.vang;
        long vangNguoiNhan = (long)phien.nguoiNhan.vang - phien.deNghiNguoiNhan.vang + phien.deNghiNguoiMoi.vang;
        if (vangNguoiMoi < 0L || vangNguoiNhan < 0L
                || vangNguoiMoi > Integer.MAX_VALUE || vangNguoiNhan > Integer.MAX_VALUE) {
            return KetQuaChuyen.loi("Số vàng sau giao dịch vượt giới hạn.");
        }

        VXLVatPham[] tuiNguoiMoi = saoChepTui(phien.nguoiMoi.itemBag);
        VXLVatPham[] tuiNguoiNhan = saoChepTui(phien.nguoiNhan.itemBag);
        if (tuiNguoiMoi == null || tuiNguoiNhan == null) {
            return KetQuaChuyen.loi("Không thể sao chép túi đồ để giao dịch.");
        }
        truVatPham(tuiNguoiMoi, phien.deNghiNguoiMoi);
        truVatPham(tuiNguoiNhan, phien.deNghiNguoiNhan);
        if (!themVatPham(tuiNguoiMoi, phien.nguoiNhan, phien.deNghiNguoiNhan)
                || !themVatPham(tuiNguoiNhan, phien.nguoiMoi, phien.deNghiNguoiMoi)) {
            return KetQuaChuyen.loi("Túi đồ của một trong hai người chơi không đủ chỗ.");
        }
        if (coBua(phien.deNghiNguoiNhan) && vuotGioiHanBua(phien.nguoiMoi, tuiNguoiMoi)) {
            return KetQuaChuyen.loi("Người nhận đã đạt giới hạn Búa chuyên dụng.");
        }
        if (coBua(phien.deNghiNguoiMoi) && vuotGioiHanBua(phien.nguoiNhan, tuiNguoiNhan)) {
            return KetQuaChuyen.loi("Người nhận đã đạt giới hạn Búa chuyên dụng.");
        }
        return new KetQuaChuyen((int)vangNguoiMoi, (int)vangNguoiNhan, tuiNguoiMoi, tuiNguoiNhan, null);
    }

    private static String kiemTraVaChotDeNghi(VXLNguoiChoi nguoiChoi, DeNghi deNghi) {
        if (deNghi.vang < 0 || deNghi.vang > nguoiChoi.vang) {
            return "Số vàng giao dịch không còn hợp lệ.";
        }
        for (MucDeNghi muc : deNghi.vatPhams.values()) {
            if (muc.chiSo < 0 || nguoiChoi.itemBag == null || muc.chiSo >= nguoiChoi.itemBag.length) {
                return "Vị trí vật phẩm giao dịch không còn hợp lệ.";
            }
            VXLVatPham vatPham = nguoiChoi.itemBag[muc.chiSo];
            if (vatPham == null || vatPham.mau == null || muc.soLuong <= 0
                    || muc.soLuong > vatPham.soLuong || muc.soLuong > SO_LUONG_TOI_DA_MOI_MUC
                    || nguoiChoi.vatPhamCoTrongBalo(vatPham)) {
                return "Vật phẩm giao dịch đã thay đổi hoặc không còn đủ số lượng.";
            }
            muc.vatPham = vatPham;
            muc.dauVanTay = dauVanTay(vatPham);
        }
        return null;
    }

    private static String kiemTraDeNghiDaKhoa(VXLNguoiChoi nguoiChoi, DeNghi deNghi) {
        if (!deNghi.daKhoa || !deNghi.daXacNhan || deNghi.vang < 0 || deNghi.vang > nguoiChoi.vang) {
            return "Đề nghị giao dịch không còn hợp lệ.";
        }
        for (MucDeNghi muc : deNghi.vatPhams.values()) {
            if (muc.chiSo < 0 || nguoiChoi.itemBag == null || muc.chiSo >= nguoiChoi.itemBag.length) {
                return "Vật phẩm giao dịch không còn tồn tại.";
            }
            VXLVatPham vatPham = nguoiChoi.itemBag[muc.chiSo];
            if (vatPham == null || vatPham != muc.vatPham || muc.soLuong > vatPham.soLuong
                    || nguoiChoi.vatPhamCoTrongBalo(vatPham)
                    || !dauVanTay(vatPham).equals(muc.dauVanTay)) {
                return "Vật phẩm giao dịch đã bị thay đổi sau khi khóa.";
            }
        }
        return null;
    }

    private static boolean themVatPham(VXLVatPham[] tuiNhan, VXLNguoiChoi nguoiCho, DeNghi deNghiCho) {
        for (MucDeNghi muc : deNghiCho.vatPhams.values()) {
            VXLVatPham vatPhamGoc = nguoiCho.itemBag[muc.chiSo];
            VXLVatPham vatPhamNhan = saoChepVatPham(vatPhamGoc);
            if (vatPhamNhan == null) {
                return false;
            }
            vatPhamNhan.soLuong = muc.soLuong;
            if (!themVaoTui(tuiNhan, vatPhamNhan)) {
                return false;
            }
        }
        return true;
    }

    private static boolean themVaoTui(VXLVatPham[] tui, VXLVatPham vatPham) {
        if (tui == null || vatPham == null || vatPham.mau == null || vatPham.soLuong <= 0) {
            return false;
        }
        if (vatPham.mau.loai > 5) {
            for (int i = 0; i < tui.length; i++) {
                VXLVatPham hienTai = tui[i];
                if (hienTai == null || hienTai.ma != vatPham.ma) {
                    continue;
                }
                long soLuongMoi = (long)hienTai.soLuong + vatPham.soLuong;
                if (soLuongMoi > Integer.MAX_VALUE) {
                    return false;
                }
                hienTai.soLuong = (int)soLuongMoi;
                return true;
            }
        }
        for (int i = 0; i < tui.length; i++) {
            if (tui[i] != null) {
                continue;
            }
            vatPham.chiSo = i;
            tui[i] = vatPham;
            return true;
        }
        return false;
    }

    private static void truVatPham(VXLVatPham[] tui, DeNghi deNghi) {
        for (MucDeNghi muc : deNghi.vatPhams.values()) {
            VXLVatPham vatPham = tui[muc.chiSo];
            vatPham.soLuong -= muc.soLuong;
            if (vatPham.soLuong <= 0) {
                tui[muc.chiSo] = null;
            }
        }
    }

    private static VXLVatPham[] saoChepTui(VXLVatPham[] tuiGoc) {
        if (tuiGoc == null) {
            return null;
        }
        VXLVatPham[] ketQua = new VXLVatPham[tuiGoc.length];
        for (int i = 0; i < tuiGoc.length; i++) {
            if (tuiGoc[i] == null) {
                continue;
            }
            ketQua[i] = saoChepVatPham(tuiGoc[i]);
            if (ketQua[i] == null) {
                return null;
            }
            ketQua[i].chiSo = i;
        }
        return ketQua;
    }

    private static VXLVatPham saoChepVatPham(VXLVatPham vatPham) {
        try {
            return vatPham == null ? null : new VXLVatPham(vatPham.toJSONObject());
        }
        catch (RuntimeException ex) {
            Logger.getLogger(VXLGiaoDich.class.getName()).log(Level.WARNING,
                    "Không thể sao chép vật phẩm giao dịch.", ex);
            return null;
        }
    }

    private static boolean coBua(DeNghi deNghi) {
        for (MucDeNghi muc : deNghi.vatPhams.values()) {
            if (muc.vatPham != null && muc.vatPham.ma == VXLTienTrinhDucLo.MA_BUA_DUC_LO) {
                return true;
            }
        }
        return false;
    }

    private static boolean vuotGioiHanBua(VXLNguoiChoi nguoiChoi, VXLVatPham[] tuiMoi) {
        int tong = Byte.toUnsignedInt(nguoiChoi.nHammer)
                + demBua(tuiMoi) + demBua(nguoiChoi.itemBody) + demBua(nguoiChoi.itemBox);
        return tong > VXLTienTrinhDucLo.SO_BUA_TOI_DA;
    }

    private static int demBua(VXLVatPham[] vatPhams) {
        int tong = 0;
        if (vatPhams != null) {
            for (VXLVatPham vatPham : vatPhams) {
                if (vatPham != null && vatPham.ma == VXLTienTrinhDucLo.MA_BUA_DUC_LO) {
                    tong += Math.max(0, vatPham.soLuong);
                }
            }
        }
        return tong;
    }

    private static String dauVanTay(VXLVatPham vatPham) {
        return vatPham == null ? "" : vatPham.toJSONObject().toJSONString();
    }

    private static PhienGiaoDich layPhienDangGiaoDich(VXLNguoiChoi nguoiChoi) {
        PhienGiaoDich phien = layPhien(nguoiChoi);
        if (phien == null || phien.trangThai != TrangThai.DANG_GIAO_DICH) {
            nguoiChoi.moHopThoaiOK("Bạn không có phiên giao dịch đang hoạt động.");
            return null;
        }
        if (!coTheBatDau(phien.nguoiMoi, phien.nguoiNhan)) {
            huyPhien(phien, "Giao dịch đã hủy vì hai người chơi không còn ở cùng khu vực.");
            return null;
        }
        return phien;
    }

    private static PhienGiaoDich layPhien(VXLNguoiChoi nguoiChoi) {
        PhienGiaoDich phien = PHIEN_THEO_NGUOI_CHOI.get(nguoiChoi.ma);
        if (phien != null && phien.hetHan()) {
            huyPhien(phien, "Phiên giao dịch đã hết hạn.");
            return null;
        }
        return phien;
    }

    private static boolean donPhienMoiCuNeuCo(VXLNguoiChoi nguoiChoi) {
        PhienGiaoDich phien = layPhien(nguoiChoi);
        if (phien == null) {
            return true;
        }
        if (phien.trangThai != TrangThai.MOI) {
            return false;
        }
        VXLNguoiChoi doiTac = phien.doiTac(nguoiChoi);
        xoaPhien(phien);
        guiDong(nguoiChoi);
        guiDong(doiTac);
        return true;
    }

    private static boolean coTheBatDau(VXLNguoiChoi nguoiThuNhat, VXLNguoiChoi nguoiThuHai) {
        return nguoiThuNhat != null && nguoiThuHai != null && nguoiThuNhat != nguoiThuHai
                && VXLNguoiChoi.layNguoiChoiTheoMa(nguoiThuNhat.ma) == nguoiThuNhat
                && VXLNguoiChoi.layNguoiChoiTheoMa(nguoiThuHai.ma) == nguoiThuHai
                && nguoiThuNhat.zone != null && nguoiThuNhat.zone == nguoiThuHai.zone
                && nguoiThuNhat.zone.players_id.get(nguoiThuNhat.ma) == nguoiThuNhat
                && nguoiThuNhat.zone.players_id.get(nguoiThuHai.ma) == nguoiThuHai
                && !nguoiThuNhat.inTraining && !nguoiThuHai.inTraining;
    }

    private static void datLaiPhien(PhienGiaoDich phien) {
        phien.datLaiDeNghi();
        phien.cham();
        guiBatDau(phien.nguoiMoi, phien.nguoiNhan.ma);
        guiBatDau(phien.nguoiNhan, phien.nguoiMoi.ma);
    }

    private static void huyPhien(PhienGiaoDich phien, String thongBao) {
        if (phien == null) {
            return;
        }
        xoaPhien(phien);
        guiDong(phien.nguoiMoi);
        guiDong(phien.nguoiNhan);
        if (thongBao != null && !thongBao.isBlank()) {
            phien.nguoiMoi.moHopThoaiOK(thongBao);
            phien.nguoiNhan.moHopThoaiOK(thongBao);
        }
    }

    private static void xoaPhien(PhienGiaoDich phien) {
        PHIEN_THEO_NGUOI_CHOI.remove(phien.nguoiMoi.ma, phien);
        PHIEN_THEO_NGUOI_CHOI.remove(phien.nguoiNhan.ma, phien);
        phien.trangThai = TrangThai.KET_THUC;
    }

    private static void guiMoi(VXLNguoiChoi nguoiNhan, int maNguoiMoi) {
        guiTinCoMaNguoiChoi(nguoiNhan, 0, maNguoiMoi);
    }

    private static void guiBatDau(VXLNguoiChoi nguoiNhan, int maDoiTac) {
        guiTinCoMaNguoiChoi(nguoiNhan, 1, maDoiTac);
    }

    private static void guiXoaVatPham(VXLNguoiChoi nguoiNhan, int chiSo) {
        try {
            VXLTinNhan tinNhan = taoTin(2);
            tinNhan.boGhi().writeByte(chiSo);
            tinNhan.boGhi().flush();
            nguoiNhan.dichVu.guiTin(tinNhan);
        }
        catch (Exception ex) {
            ghiLoiGuiTin(nguoiNhan, ex);
        }
    }

    private static void guiDoiPhuongDaKhoa(VXLNguoiChoi nguoiNhan, VXLNguoiChoi nguoiCho, DeNghi deNghi) {
        if (nguoiNhan == null || nguoiCho == null) {
            return;
        }
        try {
            VXLTinNhan tinNhan = taoTin(6);
            DataOutputStream boGhi = tinNhan.boGhi();
            boGhi.writeInt(deNghi.vang);
            boGhi.writeByte(deNghi.vatPhams.size());
            for (MucDeNghi muc : deNghi.vatPhams.values()) {
                ghiVatPham(boGhi, nguoiCho.itemBag[muc.chiSo], muc.soLuong);
            }
            boGhi.flush();
            nguoiNhan.dichVu.guiTin(tinNhan);
        }
        catch (Exception ex) {
            ghiLoiGuiTin(nguoiNhan, ex);
        }
    }

    private static void ghiVatPham(DataOutputStream boGhi, VXLVatPham vatPham, int soLuong) throws IOException {
        boGhi.writeShort(vatPham.ma);
        boGhi.writeByte(soLuong);
        boGhi.writeByte(vatPham.HP);
        boGhi.writeUTF("");
        boGhi.writeUTF("");
        Vector thuocTinhs = vatPham.layThuocTinhHieuLuc();
        boGhi.writeByte(thuocTinhs.size());
        for (Object giaTri : thuocTinhs) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)giaTri;
            boGhi.writeByte(thuocTinh.optionTemplate.ma);
            if (thuocTinh.optionTemplate.ma == 15) {
                boGhi.writeShort(Math.max(1, vatPham.laySoGioDucLoConLai()));
            } else {
                boGhi.writeShort(thuocTinh.thamSo);
            }
        }
    }

    private static void guiDong(VXLNguoiChoi nguoiNhan) {
        if (nguoiNhan == null || nguoiNhan.dichVu == null) {
            return;
        }
        try {
            VXLTinNhan tinNhan = taoTin(7);
            tinNhan.boGhi().flush();
            nguoiNhan.dichVu.guiTin(tinNhan);
        }
        catch (Exception ex) {
            ghiLoiGuiTin(nguoiNhan, ex);
        }
    }

    private static void guiTinCoMaNguoiChoi(VXLNguoiChoi nguoiNhan, int hanhDong, int maNguoiChoi) {
        if (nguoiNhan == null || nguoiNhan.dichVu == null) {
            return;
        }
        try {
            VXLTinNhan tinNhan = taoTin(hanhDong);
            tinNhan.boGhi().writeInt(maNguoiChoi);
            tinNhan.boGhi().flush();
            nguoiNhan.dichVu.guiTin(tinNhan);
        }
        catch (Exception ex) {
            ghiLoiGuiTin(nguoiNhan, ex);
        }
    }

    private static VXLTinNhan taoTin(int hanhDong) throws IOException {
        VXLTinNhan tinNhan = new VXLTinNhan(-98);
        tinNhan.boGhi().writeByte(LENH_RPG_GIAO_DICH);
        tinNhan.boGhi().writeByte(hanhDong);
        return tinNhan;
    }

    private static void guiLaiTaiSan(VXLNguoiChoi nguoiChoi) {
        try {
            nguoiChoi.dichVu.guiTuiDo();
            nguoiChoi.dichVu.guiBalo();
            nguoiChoi.dichVu.capNhat();
        }
        catch (IOException ex) {
            Logger.getLogger(VXLGiaoDich.class.getName()).log(Level.WARNING,
                    "Không thể gửi lại tài sản sau giao dịch cho người chơi " + nguoiChoi.ma + ".", ex);
        }
    }

    private static void ghiLoiGuiTin(VXLNguoiChoi nguoiChoi, Exception ex) {
        Logger.getLogger(VXLGiaoDich.class.getName()).log(Level.FINE,
                "Không thể gửi tin giao dịch cho người chơi " + (nguoiChoi == null ? -1 : nguoiChoi.ma) + ".", ex);
    }

    private enum TrangThai {
        MOI,
        DANG_GIAO_DICH,
        KET_THUC
    }

    private static final class PhienGiaoDich {
        private final VXLNguoiChoi nguoiMoi;
        private final VXLNguoiChoi nguoiNhan;
        private final DeNghi deNghiNguoiMoi = new DeNghi();
        private final DeNghi deNghiNguoiNhan = new DeNghi();
        private TrangThai trangThai = TrangThai.MOI;
        private long capNhatLuc = System.currentTimeMillis();

        private PhienGiaoDich(VXLNguoiChoi nguoiMoi, VXLNguoiChoi nguoiNhan) {
            this.nguoiMoi = nguoiMoi;
            this.nguoiNhan = nguoiNhan;
        }

        private VXLNguoiChoi doiTac(VXLNguoiChoi nguoiChoi) {
            if (nguoiChoi == nguoiMoi) {
                return nguoiNhan;
            }
            return nguoiChoi == nguoiNhan ? nguoiMoi : null;
        }

        private DeNghi deNghiCua(VXLNguoiChoi nguoiChoi) {
            return nguoiChoi == nguoiMoi ? deNghiNguoiMoi : deNghiNguoiNhan;
        }

        private void datLaiDeNghi() {
            deNghiNguoiMoi.datLai();
            deNghiNguoiNhan.datLai();
        }

        private void cham() {
            capNhatLuc = System.currentTimeMillis();
        }

        private boolean hetHan() {
            long thoiHan = trangThai == TrangThai.MOI
                    ? THOI_HAN_LOI_MOI_MILLIS : THOI_HAN_GIAO_DICH_MILLIS;
            return trangThai == TrangThai.KET_THUC || System.currentTimeMillis() - capNhatLuc > thoiHan;
        }
    }

    private static final class DeNghi {
        private final Map<Integer, MucDeNghi> vatPhams = new LinkedHashMap<>();
        private int vang;
        private boolean daKhoa;
        private boolean daXacNhan;

        private void datLai() {
            vatPhams.clear();
            vang = 0;
            daKhoa = false;
            daXacNhan = false;
        }
    }

    private static final class MucDeNghi {
        private final int chiSo;
        private final int soLuong;
        private VXLVatPham vatPham;
        private String dauVanTay;

        private MucDeNghi(int chiSo, int soLuong) {
            this.chiSo = chiSo;
            this.soLuong = soLuong;
        }
    }

    private static final class KetQuaChuyen {
        private final int vangNguoiMoi;
        private final int vangNguoiNhan;
        private final VXLVatPham[] tuiNguoiMoi;
        private final VXLVatPham[] tuiNguoiNhan;
        private final String loi;

        private KetQuaChuyen(int vangNguoiMoi, int vangNguoiNhan,
                VXLVatPham[] tuiNguoiMoi, VXLVatPham[] tuiNguoiNhan, String loi) {
            this.vangNguoiMoi = vangNguoiMoi;
            this.vangNguoiNhan = vangNguoiNhan;
            this.tuiNguoiMoi = tuiNguoiMoi;
            this.tuiNguoiNhan = tuiNguoiNhan;
            this.loi = loi;
        }

        private static KetQuaChuyen loi(String loi) {
            return new KetQuaChuyen(0, 0, null, null, loi);
        }
    }
}