package com.vxl.chien;

import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLVatPham;
import java.io.IOException;

final class VXLXuLyVatPhamTrongTran {
    private final VXLQuanLyChien tranDau;

    VXLXuLyVatPhamTrongTran(VXLQuanLyChien tranDau) {
        this.tranDau = tranDau;
    }

    boolean xuLy(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.tranDau.layChienBinh(nguoiChoi);
        if (!this.tranDau.coTheHanhDong(chienBinh) || chienBinh.daDungVatPhamTrongLuot) {
            return false;
        }
        int yeuCau = Byte.toUnsignedInt(ms.boDoc().readByte());
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
        if (yeuCau == 100) {
            if (!chienBinh.kichHoatKyNangDacBiet()) {
                if (chienBinh.coPhien()) {
                    chienBinh.nguoiChoi.startOKDlg2("Nộ chưa đầy hoặc kỹ năng đặc biệt đã được kích hoạt.");
                }
                return true;
            }
            chienBinh.daDungVatPhamTrongLuot = true;
            this.tranDau.phatNo(chienBinh);
            this.tranDau.phatDungVatPham(chienBinh, (byte)100,
                    chienBinh.layIconKyNangDacBiet());
            return true;
        }
        int[] viTri = this.timVatPhamTrongBalo(nguoiChoi, yeuCau);
        if (viTri == null) {
            nguoiChoi.startOKDlg2("Vật phẩm không có trong balo chiến đấu.");
            return true;
        }
        VXLVatPham vatPham = nguoiChoi.itemBag[viTri[1]];
        if (vatPham == null || vatPham.mau == null || vatPham.mau.loai != 10 || vatPham.soLuong <= 0) {
            nguoiChoi.startOKDlg2("Vật phẩm chiến đấu không hợp lệ.");
            return true;
        }
        if (!this.apDung(chienBinh, vatPham)) {
            return true;
        }
        chienBinh.daDungVatPhamTrongLuot = true;
        nguoiChoi.tieuThuVatPhamTrongBalo(viTri[0]);
        this.tranDau.phatDungVatPham(chienBinh, vatPham.mau.gioiTinh, vatPham.mau.iconID);
        VXLQuanLyMayChu.log("[ITEM] use player=" + chienBinh.ten
                + " item=" + vatPham.ma + " effect="
                + Byte.toUnsignedInt(vatPham.mau.gioiTinh));
        this.tranDau.kiemTraKetThuc();
        return true;
    }

    private boolean apDung(VXLChienBinh chienBinh, VXLVatPham vatPham) throws IOException {
        switch (vatPham.ma) {
            case 220:
                return this.hoiMau(chienBinh, Math.max(1, chienBinh.mauToiDa * 30 / 100));
            case 222:
                chienBinh.soPhatToiThieu = Math.max(chienBinh.soPhatToiThieu, 2);
                chienBinh.heSoPhatBan = Math.max(chienBinh.heSoPhatBan, 175);
                return true;
            case 223:
                chienBinh.heSoDiChuyen = 200;
                return true;
            case 224:
                chienBinh.luotTangHinh = Math.max(chienBinh.luotTangHinh, 2);
                return true;
            case 225:
                chienBinh.luotNgungGio = Math.max(chienBinh.luotNgungGio, 3);
                return true;
            case 230:
                return this.hoiMauDongDoi(chienBinh);
            case 251:
                return this.hoiMau(chienBinh, Math.max(1, chienBinh.mauToiDa * 50 / 100));
            case 252:
                return this.hoiMau(chienBinh, chienBinh.mauToiDa);
            case 253:
                chienBinh.luotVoHinh = Math.max(chienBinh.luotVoHinh, 2);
                return true;
            case 254:
                chienBinh.luotMaCaRong = Math.max(chienBinh.luotMaCaRong, 3);
                return true;
            case 257:
                return chienBinh.nguoiChoi != null && chienBinh.nguoiChoi.kichHoatNhanDoiKinhNghiem();
            case 296:
                chienBinh.heSoTangNo = Math.max(chienBinh.heSoTangNo, 150);
                return true;
            case 297:
                chienBinh.luotNapNhanh = Math.max(chienBinh.luotNapNhanh, 3);
                return true;
            case 298:
                chienBinh.luotXuyenGiap = Math.max(chienBinh.luotXuyenGiap, 3);
                return true;
            case 389:
                chienBinh.luotXuyenDiaHinh = Math.max(chienBinh.luotXuyenDiaHinh, 3);
                return true;
            case 390:
                chienBinh.heSoGoBom = Math.max(chienBinh.heSoGoBom, 200);
                return true;
            default:
                if (VXLCauHinhVatPhamChienDau.laDanDacBiet(vatPham.ma)) {
                    chienBinh.vatPhamDanDacBiet = vatPham.ma;
                    return true;
                }
                chienBinh.nguoiChoi.startOKDlg2("Vật phẩm này chưa hỗ trợ trong trận đấu.");
                return false;
        }
    }

    private boolean hoiMauDongDoi(VXLChienBinh nguoiDung) throws IOException {
        boolean daHoi = false;
        for (VXLChienBinh dongDoi : this.tranDau.layDanhSachChienBinh()) {
            if (dongDoi == null || dongDoi.bot || dongDoi.chet || !this.cungDoi(nguoiDung, dongDoi)) {
                continue;
            }
            int soMau = dongDoi.hoiMau(Math.max(1, dongDoi.mauToiDa * 30 / 100));
            if (soMau > 0) {
                daHoi = true;
                this.tranDau.phatCapNhatMau(dongDoi);
            }
        }
        if (!daHoi && nguoiDung.coPhien()) {
            nguoiDung.nguoiChoi.startOKDlg2("Máu của toàn đội đang đầy.");
        }
        return daHoi;
    }

    private boolean hoiMau(VXLChienBinh chienBinh, int soMau) throws IOException {
        int daHoi = chienBinh.hoiMau(soMau);
        if (daHoi <= 0) {
            if (chienBinh.coPhien()) {
                chienBinh.nguoiChoi.startOKDlg2("Máu của bạn đang đầy.");
            }
            return false;
        }
        this.tranDau.phatCapNhatMau(chienBinh);
        return true;
    }

    private int[] timVatPhamTrongBalo(VXLNguoiChoi nguoiChoi, int yeuCau) {
        if (nguoiChoi.itemBalo == null || nguoiChoi.itemBag == null) {
            return null;
        }
        if (yeuCau >= 0 && yeuCau < nguoiChoi.itemBalo.length) {
            int chiSoTui = nguoiChoi.itemBalo[yeuCau];
            if (this.vatPhamBaloHopLe(nguoiChoi, chiSoTui)) {
                return new int[]{yeuCau, chiSoTui};
            }
        }
        if (yeuCau >= 0 && yeuCau < nguoiChoi.itemBag.length && this.vatPhamBaloHopLe(nguoiChoi, yeuCau)) {
            for (int i = 0; i < nguoiChoi.itemBalo.length; i++) {
                if (nguoiChoi.itemBalo[i] == yeuCau) {
                    return new int[]{i, yeuCau};
                }
            }
        }
        for (int i = 0; i < nguoiChoi.itemBalo.length; i++) {
            int chiSoTui = nguoiChoi.itemBalo[i];
            if (!this.vatPhamBaloHopLe(nguoiChoi, chiSoTui)) {
                continue;
            }
            VXLVatPham vatPham = nguoiChoi.itemBag[chiSoTui];
            if (vatPham.ma == yeuCau || (vatPham.ma & 0xFF) == yeuCau
                    || Byte.toUnsignedInt((byte)vatPham.mau.part) == yeuCau) {
                return new int[]{i, chiSoTui};
            }
        }
        return null;
    }

    private boolean vatPhamBaloHopLe(VXLNguoiChoi nguoiChoi, int chiSoTui) {
        return chiSoTui >= 0 && chiSoTui < nguoiChoi.itemBag.length && nguoiChoi.itemBag[chiSoTui] != null;
    }

    private boolean cungDoi(VXLChienBinh nguoiDung, VXLChienBinh dongDoi) {
        if (this.tranDau.laCheDoCamTu()) {
            return !nguoiDung.bot && !dongDoi.bot;
        }
        return nguoiDung.chiSo % 2 == dongDoi.chiSo % 2;
    }

    private short kep(int giaTri, int nhoNhat, int lonNhat) {
        return (short)Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }
}
