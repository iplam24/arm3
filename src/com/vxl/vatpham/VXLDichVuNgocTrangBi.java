package com.vxl.vatpham;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.quantri.VXLMenuQuanTri;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class VXLDichVuNgocTrangBi {
    private static final int MA_BAO_HIEM = 353;
    private static final int MA_NGOC_DAU = 299;
    private static final int MA_NGOC_CUOI = 348;
    private static final int SO_NGOC_DE_GHEP = 5;
    private static final Map<Integer, LuaChonTrangBi> LUA_CHON = new ConcurrentHashMap<>();

    private VXLDichVuNgocTrangBi() {
    }

    public static boolean laLenhMenuDangCho(VXLNguoiChoi nguoiChoi) {
        return nguoiChoi != null && LUA_CHON.containsKey(nguoiChoi.ma);
    }

    public static void huyMenu(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
            LUA_CHON.remove(nguoiChoi.ma);
        }
    }

    public static void moMenu(VXLNguoiChoi nguoiChoi, boolean trongTui, int chiSo,
            VXLVatPham trangBi) throws IOException {
        if (nguoiChoi == null || trangBi == null || trangBi.mau == null
                || !trangBi.isTypeBody()) {
            return;
        }
        VXLMenuQuanTri.huyMenu(nguoiChoi);
        if (trangBi.hoanTatDucLoNeuDenHan()) {
            VXLTienTrinhDucLo.capNhatThongTinBua(nguoiChoi);
            lamMoiTrangBi(nguoiChoi, !trongTui);
        }
        Vector<String> tenHanhDong = new Vector<>();
        List<HanhDong> hanhDongs = new ArrayList<>();
        if (trangBi.isSocketing) {
            int chiPhiNgoc = VXLTienTrinhDucLo.layChiPhiHoanThanhNgay(trangBi);
            tenHanhDong.add("Xem thời gian còn lại");
            hanhDongs.add(HanhDong.XEM_TIEN_TRINH);
            tenHanhDong.add("Hoàn thành ngay (" + chiPhiNgoc + " ngọc)");
            hanhDongs.add(HanhDong.HOAN_THANH_NGAY);
        } else {
            if (trangBi.nSocket < 3) {
                tenHanhDong.add("Đục lỗ");
                hanhDongs.add(HanhDong.DUC_LO);
            }
            if (trangBi.nGem < trangBi.nSocket) {
                tenHanhDong.add("Đính ngọc");
                hanhDongs.add(HanhDong.DINH_NGOC);
            }
            if (trangBi.nGem > 0) {
                tenHanhDong.add("Tháo ngọc");
                hanhDongs.add(HanhDong.THAO_NGOC);
            }
        }
        if (hanhDongs.isEmpty()) {
            nguoiChoi.startOKDlg2("Trang bị này không còn thao tác ngọc phù hợp.");
            return;
        }
        LUA_CHON.put(nguoiChoi.ma, new LuaChonTrangBi(trongTui, chiSo, hanhDongs));
        nguoiChoi.dichVu.moDanhSach("Bạn muốn làm gì?", tenHanhDong);
    }

    public static void xuLyMenu(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        int chiSoHanhDong = ms.boDoc().readUnsignedByte();
        LuaChonTrangBi luaChon = LUA_CHON.remove(nguoiChoi.ma);
        if (luaChon == null || chiSoHanhDong < 0 || chiSoHanhDong >= luaChon.hanhDongs.size()) {
            return;
        }
        VXLVatPham trangBi = layTrangBi(nguoiChoi, luaChon.trongTui, luaChon.chiSo);
        if (trangBi == null || trangBi.mau == null || !trangBi.isTypeBody()) {
            nguoiChoi.startOKDlg2("Không tìm thấy trang bị đã chọn.");
            return;
        }
        switch (luaChon.hanhDongs.get(chiSoHanhDong)) {
            case DUC_LO -> ducLo(nguoiChoi, trangBi, luaChon.trongTui);
            case XEM_TIEN_TRINH -> xemTienTrinh(nguoiChoi, trangBi, luaChon.trongTui);
            case HOAN_THANH_NGAY -> hoanThanhNgay(nguoiChoi, trangBi, luaChon.trongTui);
            case DINH_NGOC -> nguoiChoi.startOKDlg2(
                    "Mở mục Ghép ngọc tại NPC Quân nhu, chọn trang bị và ngọc rồi bấm Đính ngọc.");
            case THAO_NGOC -> thaoNgoc(nguoiChoi, trangBi, luaChon.trongTui);
        }
    }

    public static void khamNgoc(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        if (nguoiChoi == null || nguoiChoi.itemBag == null || ms.boDoc().available() <= 0) {
            return;
        }
        int chiSoTrangBi = ms.boDoc().readUnsignedByte();
        VXLVatPham trangBi = layTrangBi(nguoiChoi, true, chiSoTrangBi);
        if (trangBi != null && trangBi.hoanTatDucLoNeuDenHan()) {
            VXLTienTrinhDucLo.capNhatThongTinBua(nguoiChoi);
        }
        if (trangBi == null || trangBi.mau == null || !trangBi.isTypeBody()
                || trangBi.isSocketing) {
            nguoiChoi.startOKDlg2("Trang bị khảm ngọc không hợp lệ.");
            return;
        }
        List<Integer> cacChiSoNgoc = new ArrayList<>();
        while (ms.boDoc().available() > 0 && cacChiSoNgoc.size() < 3) {
            cacChiSoNgoc.add(ms.boDoc().readUnsignedByte());
        }
        int soLoTrong = trangBi.nSocket - trangBi.nGem;
        if (cacChiSoNgoc.isEmpty() || cacChiSoNgoc.size() > soLoTrong) {
            nguoiChoi.startOKDlg2("Số ngọc vượt quá số lỗ trống của trang bị.");
            return;
        }
        Map<Integer, Integer> soLuongTheoViTri = new HashMap<>();
        for (int chiSoNgoc : cacChiSoNgoc) {
            if (chiSoNgoc == chiSoTrangBi) {
                nguoiChoi.startOKDlg2("Vị trí ngọc không hợp lệ.");
                return;
            }
            VXLVatPham ngoc = layVatPhamTrongTui(nguoiChoi, chiSoNgoc);
            if (ngoc == null || ngoc.mau == null || ngoc.mau.loai != 12) {
                nguoiChoi.startOKDlg2("Chỉ có thể đính vật phẩm loại ngọc.");
                return;
            }
            int can = soLuongTheoViTri.merge(chiSoNgoc, 1, Integer::sum);
            if (ngoc.soLuong < can) {
                nguoiChoi.startOKDlg2("Không đủ số lượng ngọc đã chọn.");
                return;
            }
        }
        List<Integer> cacMaNgoc = new ArrayList<>();
        for (int chiSoNgoc : cacChiSoNgoc) {
            cacMaNgoc.add(nguoiChoi.itemBag[chiSoNgoc].ma);
        }
        for (int maNgoc : cacMaNgoc) {
            if (!trangBi.dinhNgoc(maNgoc)) {
                nguoiChoi.startOKDlg2("Không còn lỗ trống để đính ngọc.");
                return;
            }
        }
        for (Map.Entry<Integer, Integer> muc : soLuongTheoViTri.entrySet()) {
            nguoiChoi.removeItem(muc.getKey(), muc.getValue());
        }
        lamMoiTrangBi(nguoiChoi, false);
        nguoiChoi.startOKDlg2("Đính " + cacMaNgoc.size() + " viên ngọc thành công.");
    }

    public static void ghepNgoc(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        List<Integer> cacMa = new ArrayList<>(7);
        while (ms.boDoc().available() >= 2 && cacMa.size() < 7) {
            int ma = ms.boDoc().readShort();
            if (ma >= 0) {
                cacMa.add(ma);
            }
        }
        int maNgoc = -1;
        int maDaMayMan = -1;
        boolean coBaoHiem = false;
        for (int ma : cacMa) {
            VXLMauVatPham mau = VXLQuanLyMayChu.itemTemplates != null
                    ? VXLQuanLyMayChu.itemTemplates.get(ma) : null;
            if (mau == null) {
                continue;
            }
            if (mau.loai == 12) {
                if (maNgoc >= 0 && maNgoc != ma) {
                    thatBaiGhep(nguoiChoi, "Các viên ngọc phải cùng loại và cùng cấp.");
                    return;
                }
                maNgoc = ma;
            } else if (mau.loai == 14) {
                maDaMayMan = ma;
            } else if (mau.loai == 15 && ma == MA_BAO_HIEM) {
                coBaoHiem = true;
            }
        }
        VXLVatPham ngocNguon = timVatPhamTheoMa(nguoiChoi, maNgoc);
        if (maNgoc < MA_NGOC_DAU || maNgoc > MA_NGOC_CUOI || ngocNguon == null
                || ngocNguon.soLuong < SO_NGOC_DE_GHEP) {
            thatBaiGhep(nguoiChoi, "Cần đủ 5 viên ngọc cùng loại và cùng cấp.");
            return;
        }
        int maNgocTiepTheo = maNgoc + 5;
        VXLMauVatPham mauTiepTheo = VXLQuanLyMayChu.itemTemplates.get(maNgocTiepTheo);
        if (mauTiepTheo == null || mauTiepTheo.loai != 12) {
            thatBaiGhep(nguoiChoi, "Ngọc đã đạt cấp tối đa.");
            return;
        }
        VXLVatPham daMayMan = timVatPhamTheoMa(nguoiChoi, maDaMayMan);
        VXLVatPham baoHiem = coBaoHiem ? timVatPhamTheoMa(nguoiChoi, MA_BAO_HIEM) : null;
        if (maDaMayMan >= 0 && daMayMan == null) {
            thatBaiGhep(nguoiChoi, "Không tìm thấy đá may mắn đã chọn.");
            return;
        }
        if (coBaoHiem && baoHiem == null) {
            thatBaiGhep(nguoiChoi, "Không tìm thấy bảo hiểm đã chọn.");
            return;
        }
        int capNgoc = Math.max(1, Byte.toUnsignedInt(ngocNguon.mau.cap));
        int tiLe = Math.max(20, 100 - capNgoc * 8);
        if (daMayMan != null) {
            tiLe += Math.max(0, daMayMan.mau.thuocTinhs.isEmpty()
                    ? 0 : daMayMan.getParamById(9));
        }
        tiLe = Math.max(5, Math.min(95, tiLe));
        boolean thanhCong = ThreadLocalRandom.current().nextInt(100) < tiLe;
        if (daMayMan != null) {
            nguoiChoi.removeItem(daMayMan.chiSo, 1);
        }
        if (baoHiem != null) {
            nguoiChoi.removeItem(baoHiem.chiSo, 1);
        }
        if (thanhCong) {
            nguoiChoi.removeItem(ngocNguon.chiSo, SO_NGOC_DE_GHEP);
            VXLVatPham ngocMoi = new VXLVatPham(maNgocTiepTheo);
            ngocMoi.thayMau(mauTiepTheo);
            if (!nguoiChoi.themVatPhamVaoTui(ngocMoi)) {
                nguoiChoi.startOKDlg2("Ghép thành công nhưng túi đồ đã đầy; hãy đăng nhập lại để kiểm tra.");
            } else {
                nguoiChoi.startOKDlg2("Ghép thành công " + mauTiepTheo.ten + " (" + tiLe + "%).");
            }
        } else {
            if (baoHiem == null) {
                nguoiChoi.removeItem(ngocNguon.chiSo, 1);
            }
            nguoiChoi.startOKDlg2(baoHiem == null
                    ? "Ghép thất bại, mất 1 viên ngọc. Tỉ lệ " + tiLe + "%"
                    : "Ghép thất bại, bảo hiểm đã giữ lại ngọc. Tỉ lệ " + tiLe + "%");
        }
        guiKetQuaGhep(nguoiChoi, thanhCong);
        nguoiChoi.dichVu.guiTuiDo();
        nguoiChoi.dichVu.capNhat();
        nguoiChoi.flushCache();
    }

    private static void ducLo(VXLNguoiChoi nguoiChoi, VXLVatPham trangBi,
            boolean trongTui) throws IOException {
        if (trangBi.hoanTatDucLoNeuDenHan()) {
            VXLTienTrinhDucLo.capNhatThongTinBua(nguoiChoi);
        }
        if (trangBi.isSocketing) {
            nguoiChoi.startOKDlg2("Trang bị đang đục lỗ, không thể bắt đầu lỗ tiếp theo.");
            return;
        }
        VXLTienTrinhDucLo.capNhat(nguoiChoi);
        if (Byte.toUnsignedInt(nguoiChoi.nHammer) <= 0) {
            nguoiChoi.startOKDlg2("Bạn chưa kích hoạt Búa chuyên dụng.");
            return;
        }
        if (!VXLTienTrinhDucLo.coBuaRanh(nguoiChoi)) {
            nguoiChoi.startOKDlg2("Tất cả Búa chuyên dụng đang bận đục trang bị khác.");
            return;
        }
        int loTiepTheo = trangBi.nSocket + 1;
        int chiPhi = VXLTienTrinhDucLo.layChiPhiVang(loTiepTheo);
        long thoiGian = VXLTienTrinhDucLo.layThoiGianDucLo(loTiepTheo);
        if (nguoiChoi.vang < chiPhi) {
            nguoiChoi.startOKDlg2("Không đủ " + chiPhi + " vàng để đục lỗ.");
            return;
        }
        if (!trangBi.batDauDucLo(thoiGian)) {
            nguoiChoi.startOKDlg2("Không thể bắt đầu đục lỗ cho trang bị này.");
            return;
        }
        nguoiChoi.vang -= chiPhi;
        VXLTienTrinhDucLo.capNhatThongTinBua(nguoiChoi);
        lamMoiTrangBi(nguoiChoi, !trongTui);
        nguoiChoi.startOKDlg2("Bắt đầu đục lỗ thứ " + loTiepTheo + ". Thời gian: "
                + VXLTienTrinhDucLo.dinhDangThoiGianConLai(trangBi)
                + ". Búa không bị tiêu hao.");
    }

    private static void xemTienTrinh(VXLNguoiChoi nguoiChoi, VXLVatPham trangBi,
            boolean trongTui) throws IOException {
        if (trangBi.hoanTatDucLoNeuDenHan()) {
            VXLTienTrinhDucLo.capNhatThongTinBua(nguoiChoi);
            lamMoiTrangBi(nguoiChoi, !trongTui);
            nguoiChoi.startOKDlg2("Đục lỗ đã hoàn thành.");
            return;
        }
        if (!trangBi.isSocketing) {
            nguoiChoi.startOKDlg2("Trang bị hiện không có tiến trình đục lỗ.");
            return;
        }
        int chiPhiNgoc = VXLTienTrinhDucLo.layChiPhiHoanThanhNgay(trangBi);
        nguoiChoi.startOKDlg2("Còn " + VXLTienTrinhDucLo.dinhDangThoiGianConLai(trangBi)
                + ". Có thể hoàn thành ngay bằng " + chiPhiNgoc + " ngọc.");
    }

    private static void hoanThanhNgay(VXLNguoiChoi nguoiChoi, VXLVatPham trangBi,
            boolean trongTui) throws IOException {
        if (trangBi.hoanTatDucLoNeuDenHan()) {
            VXLTienTrinhDucLo.capNhatThongTinBua(nguoiChoi);
            lamMoiTrangBi(nguoiChoi, !trongTui);
            nguoiChoi.startOKDlg2("Đục lỗ đã hoàn thành, không tốn ngọc.");
            return;
        }
        if (!trangBi.isSocketing) {
            nguoiChoi.startOKDlg2("Trang bị hiện không có tiến trình đục lỗ.");
            return;
        }
        int chiPhiNgoc = VXLTienTrinhDucLo.layChiPhiHoanThanhNgay(trangBi);
        if (nguoiChoi.ngoc < chiPhiNgoc) {
            nguoiChoi.startOKDlg2("Không đủ " + chiPhiNgoc + " ngọc để hoàn thành ngay.");
            return;
        }
        if (!trangBi.hoanTatDucLoNgay()) {
            nguoiChoi.startOKDlg2("Không thể hoàn thành tiến trình đục lỗ.");
            return;
        }
        nguoiChoi.ngoc -= chiPhiNgoc;
        VXLTienTrinhDucLo.capNhatThongTinBua(nguoiChoi);
        lamMoiTrangBi(nguoiChoi, !trongTui);
        nguoiChoi.startOKDlg2("Hoàn thành đục lỗ, đã dùng " + chiPhiNgoc + " ngọc.");
    }

    private static void thaoNgoc(VXLNguoiChoi nguoiChoi, VXLVatPham trangBi,
            boolean trongTui) throws IOException {
        List<Integer> cacMaNgoc = trangBi.layCacMaNgocDaDinh();
        if (cacMaNgoc.isEmpty()) {
            nguoiChoi.startOKDlg2("Trang bị không có ngọc để tháo.");
            return;
        }
        if (!coDuChoTrongTui(nguoiChoi, cacMaNgoc)) {
            nguoiChoi.startOKDlg2("Túi đồ không đủ chỗ để nhận ngọc tháo ra.");
            return;
        }
        trangBi.thaoTatCaNgoc();
        for (int maNgoc : cacMaNgoc) {
            VXLMauVatPham mau = VXLQuanLyMayChu.itemTemplates.get(maNgoc);
            VXLVatPham ngoc = new VXLVatPham(maNgoc);
            ngoc.thayMau(mau);
            nguoiChoi.themVatPhamVaoTui(ngoc);
        }
        lamMoiTrangBi(nguoiChoi, !trongTui);
        nguoiChoi.startOKDlg2("Đã tháo " + cacMaNgoc.size() + " viên ngọc về túi.");
    }

    private static boolean coDuChoTrongTui(VXLNguoiChoi nguoiChoi, List<Integer> cacMaNgoc) {
        Set<Integer> canOTrong = new HashSet<>();
        for (int maNgoc : cacMaNgoc) {
            if (timVatPhamTheoMa(nguoiChoi, maNgoc) == null) {
                canOTrong.add(maNgoc);
            }
        }
        int oTrong = 0;
        for (VXLVatPham vatPham : nguoiChoi.itemBag) {
            if (vatPham == null) {
                oTrong++;
            }
        }
        return oTrong >= canOTrong.size();
    }

    private static void lamMoiTrangBi(VXLNguoiChoi nguoiChoi, boolean trenNguoi)
            throws IOException {
        nguoiChoi.dichVu.guiTuiDo();
        if (trenNguoi) {
            nguoiChoi.dichVu.guiDoTrenNguoi();
            nguoiChoi.dichVu.doiTrangBi();
        }
        nguoiChoi.dichVu.capNhat();
        nguoiChoi.flushCache();
    }

    private static void thatBaiGhep(VXLNguoiChoi nguoiChoi, String thongBao)
            throws IOException {
        guiKetQuaGhep(nguoiChoi, false);
        nguoiChoi.startOKDlg2(thongBao);
    }

    private static void guiKetQuaGhep(VXLNguoiChoi nguoiChoi, boolean thanhCong)
            throws IOException {
        VXLTinNhan tinNhan = new VXLTinNhan(-66);
        DataOutputStream boGhi = tinNhan.boGhi();
        boGhi.writeByte(thanhCong ? 1 : 0);
        boGhi.flush();
        nguoiChoi.dichVu.guiTin(tinNhan);
    }

    private static VXLVatPham layTrangBi(VXLNguoiChoi nguoiChoi, boolean trongTui,
            int chiSo) {
        if (trongTui) {
            return layVatPhamTrongTui(nguoiChoi, chiSo);
        }
        return nguoiChoi.itemBody != null && chiSo >= 0 && chiSo < nguoiChoi.itemBody.length
                ? nguoiChoi.itemBody[chiSo] : null;
    }

    private static VXLVatPham layVatPhamTrongTui(VXLNguoiChoi nguoiChoi, int chiSo) {
        return nguoiChoi.itemBag != null && chiSo >= 0 && chiSo < nguoiChoi.itemBag.length
                ? nguoiChoi.itemBag[chiSo] : null;
    }

    private static VXLVatPham timVatPhamTheoMa(VXLNguoiChoi nguoiChoi, int ma) {
        if (ma < 0 || nguoiChoi == null || nguoiChoi.itemBag == null) {
            return null;
        }
        for (VXLVatPham vatPham : nguoiChoi.itemBag) {
            if (vatPham != null && vatPham.ma == ma && vatPham.soLuong > 0) {
                return vatPham;
            }
        }
        return null;
    }

    private enum HanhDong {
        DUC_LO,
        XEM_TIEN_TRINH,
        HOAN_THANH_NGAY,
        DINH_NGOC,
        THAO_NGOC
    }

    private record LuaChonTrangBi(boolean trongTui, int chiSo,
            List<HanhDong> hanhDongs) {
    }
}
