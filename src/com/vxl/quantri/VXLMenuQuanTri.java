package com.vxl.quantri;

import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLDichVuNgocTrangBi;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public final class VXLMenuQuanTri {
    private static final int MA_BUA_DUC_LO = 349;
    private static final ConcurrentHashMap<Integer, TrangThaiMenu> TRANG_THAI =
            new ConcurrentHashMap<>();

    private VXLMenuQuanTri() {
    }

    public static boolean laMenuDangCho(VXLNguoiChoi nguoiChoi) {
        return nguoiChoi != null && TRANG_THAI.containsKey(nguoiChoi.ma);
    }

    public static void huyMenu(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
            TRANG_THAI.remove(nguoiChoi.ma);
        }
    }

    public static void mo(VXLNguoiChoi quanTri) {
        if (!coQuyen(quanTri)) {
            return;
        }
        VXLDichVuNgocTrangBi.huyMenu(quanTri);
        moMenuChinh(quanTri);
    }

    public static void xuLyMenu(VXLNguoiChoi quanTri, VXLTinNhan tinNhan) throws IOException {
        int chiSo = tinNhan.boDoc().readUnsignedByte();
        TrangThaiMenu trangThai = quanTri == null ? null : TRANG_THAI.remove(quanTri.ma);
        if (trangThai == null || !coQuyen(quanTri)) {
            return;
        }
        switch (trangThai.loai) {
            case CHINH -> xuLyMenuChinh(quanTri, chiSo);
            case DANH_SACH_NGUOI_CHOI -> xuLyDanhSachNguoiChoi(quanTri, trangThai, chiSo);
            case NGUOI_CHOI -> xuLyMenuNguoiChoi(quanTri, trangThai.maMucTieu, chiSo);
            case TAI_NGUYEN -> xuLyMenuTaiNguyen(quanTri, trangThai.maMucTieu, chiSo);
            case TIEN_TRINH -> xuLyMenuTienTrinh(quanTri, trangThai.maMucTieu, chiSo);
            case VAT_PHAM -> xuLyMenuVatPham(quanTri, trangThai.maMucTieu, chiSo);
            case TAI_KHOAN -> xuLyMenuTaiKhoan(quanTri, trangThai.maMucTieu, chiSo);
            case MAY_CHU -> xuLyMenuMayChu(quanTri, chiSo);
        }
    }

    private static boolean coQuyen(VXLNguoiChoi quanTri) {
        if (quanTri != null && quanTri.quanTri) {
            return true;
        }
        if (quanTri != null) {
            quanTri.moHopThoaiOK("Bạn không có quyền sử dụng menu admin.");
        }
        return false;
    }

    private static void xuLyMenuChinh(VXLNguoiChoi quanTri, int chiSo) {
        switch (chiSo) {
            case 0 -> moMenuNguoiChoi(quanTri, quanTri.ma);
            case 1 -> moDanhSachNguoiChoi(quanTri);
            case 2 -> moMenuMayChu(quanTri);
            case 3 -> hienKetQua(quanTri, "/menu help", () -> VXLBoLenhQuanTri.huongDan());
            default -> {
            }
        }
    }

    private static void xuLyDanhSachNguoiChoi(VXLNguoiChoi quanTri,
            TrangThaiMenu trangThai, int chiSo) {
        if (chiSo == trangThai.maNguoiChoi.size()) {
            moMenuChinh(quanTri);
            return;
        }
        if (chiSo < 0 || chiSo >= trangThai.maNguoiChoi.size()) {
            return;
        }
        moMenuNguoiChoi(quanTri, trangThai.maNguoiChoi.get(chiSo));
    }

    private static void xuLyMenuNguoiChoi(VXLNguoiChoi quanTri, int maMucTieu, int chiSo) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        switch (chiSo) {
            case 0 -> hienKetQua(quanTri, "/menu inspect " + mucTieu.ten,
                    () -> VXLKhoQuanTri.thongTinNguoiChoi(mucTieu.ten));
            case 1 -> moMenuTaiNguyen(quanTri, maMucTieu);
            case 2 -> moMenuTienTrinh(quanTri, maMucTieu);
            case 3 -> moMenuVatPham(quanTri, maMucTieu);
            case 4 -> moMenuTaiKhoan(quanTri, maMucTieu);
            case 5 -> hienKetQua(quanTri, "/menu save " + mucTieu.ten,
                    () -> VXLKhoQuanTri.luuNguoiChoi(mucTieu.ten));
            case 6 -> hienKetQua(quanTri, "/menu kick " + mucTieu.ten,
                    () -> VXLBoLenhQuanTri.kick(quanTri, mucTieu.ten));
            case 7 -> moDanhSachNguoiChoi(quanTri);
            default -> {
            }
        }
    }

    private static void xuLyMenuTaiNguyen(VXLNguoiChoi quanTri, int maMucTieu, int chiSo) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        switch (chiSo) {
            case 0 -> hienKetQua(quanTri, "/menu gold " + mucTieu.ten + " +100000",
                    () -> VXLKhoQuanTri.congVang(mucTieu.ten, 100000L));
            case 1 -> hienKetQua(quanTri, "/menu gold " + mucTieu.ten + " -100000",
                    () -> VXLKhoQuanTri.congVang(mucTieu.ten, -100000L));
            case 2 -> hienKetQua(quanTri, "/menu gem " + mucTieu.ten + " +100",
                    () -> VXLKhoQuanTri.congNgoc(mucTieu.ten, 100L));
            case 3 -> hienKetQua(quanTri, "/menu gem " + mucTieu.ten + " -100",
                    () -> VXLKhoQuanTri.congNgoc(mucTieu.ten, -100L));
            case 4 -> hienKetQua(quanTri, "/menu exp " + mucTieu.ten + " +10000",
                    () -> VXLKhoQuanTri.congKinhNghiem(mucTieu.ten, 10000L));
            case 5 -> hienKetQua(quanTri, "/menu point " + mucTieu.ten + " +100",
                    () -> VXLKhoQuanTri.congDiemTiemNang(mucTieu.ten, 100L));
            case 6 -> moMenuNguoiChoi(quanTri, maMucTieu);
            default -> {
            }
        }
    }

    private static void xuLyMenuTienTrinh(VXLNguoiChoi quanTri, int maMucTieu, int chiSo) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        switch (chiSo) {
            case 0 -> hienKetQua(quanTri, "/menu level " + mucTieu.ten + " +1",
                    () -> VXLKhoQuanTri.datCap(mucTieu.ten, mucTieu.cap + 1));
            case 1 -> hienKetQua(quanTri, "/menu rebel " + mucTieu.ten + " +1",
                    () -> VXLKhoQuanTri.datCapPhienQuan(mucTieu.ten,
                            Byte.toUnsignedInt(mucTieu.trainingSuccess) + 1));
            case 2 -> moMenuNguoiChoi(quanTri, maMucTieu);
            default -> {
            }
        }
    }

    private static void xuLyMenuVatPham(VXLNguoiChoi quanTri, int maMucTieu, int chiSo) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        switch (chiSo) {
            case 0 -> hienKetQua(quanTri, "/menu item " + mucTieu.ten + " " + MA_BUA_DUC_LO,
                    () -> VXLKhoQuanTri.themVatPham(mucTieu.ten, MA_BUA_DUC_LO, 1));
            case 1 -> moMenuNguoiChoi(quanTri, maMucTieu);
            default -> {
            }
        }
    }

    private static void xuLyMenuTaiKhoan(VXLNguoiChoi quanTri, int maMucTieu, int chiSo) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        if ((chiSo == 1 || chiSo == 2) && mucTieu == quanTri) {
            quanTri.moHopThoaiOK("Không thể tự tắt quyền admin hoặc tự khóa tài khoản từ menu.");
            return;
        }
        switch (chiSo) {
            case 0 -> hienKetQua(quanTri, "/menu grant " + mucTieu.ten + " on",
                    () -> VXLKhoQuanTri.datQuyenQuanTri(mucTieu.ten, true));
            case 1 -> hienKetQua(quanTri, "/menu grant " + mucTieu.ten + " off",
                    () -> VXLKhoQuanTri.datQuyenQuanTri(mucTieu.ten, false));
            case 2 -> hienKetQua(quanTri, "/menu ban " + mucTieu.ten + " on",
                    () -> VXLKhoQuanTri.datKhoaTaiKhoan(mucTieu.ten, true));
            case 3 -> hienKetQua(quanTri, "/menu ban " + mucTieu.ten + " off",
                    () -> VXLKhoQuanTri.datKhoaTaiKhoan(mucTieu.ten, false));
            case 4 -> moMenuNguoiChoi(quanTri, maMucTieu);
            default -> {
            }
        }
    }

    private static void xuLyMenuMayChu(VXLNguoiChoi quanTri, int chiSo) {
        switch (chiSo) {
            case 0 -> hienKetQua(quanTri, "/menu online", VXLKhoQuanTri::danhSachTrucTuyen);
            case 1 -> hienKetQua(quanTri, "/menu server", VXLBoLenhQuanTri::thongTinMayChu);
            case 2 -> hienKetQua(quanTri, "/menu threads", VXLBoLenhQuanTri::thongTinLuong);
            case 3 -> hienKetQua(quanTri, "/menu save all",
                    () -> VXLKhoQuanTri.luuNguoiChoi("all"));
            case 4 -> moMenuChinh(quanTri);
            default -> {
            }
        }
    }

    private static void moMenuChinh(VXLNguoiChoi quanTri) {
        moDanhSach(quanTri, "MENU ADMIN",
                List.of("Nhân vật của tôi", "Người chơi online", "Máy chủ",
                        "Hướng dẫn lệnh nâng cao"),
                new TrangThaiMenu(LoaiMenu.CHINH, -1, List.of()));
    }

    private static void moDanhSachNguoiChoi(VXLNguoiChoi quanTri) {
        List<VXLNguoiChoi> nguoiChois = new ArrayList<>();
        for (VXLNguoiChoi nguoiChoi : VXLNguoiChoi.players_id.values()) {
            if (nguoiChoi != null && nguoiChoi.ten != null) {
                nguoiChois.add(nguoiChoi);
            }
        }
        nguoiChois.sort(Comparator.comparing(nguoiChoi -> nguoiChoi.ten,
                String.CASE_INSENSITIVE_ORDER));
        int gioiHan = Math.min(100, nguoiChois.size());
        List<String> tenMuc = new ArrayList<>(gioiHan + 1);
        List<Integer> cacMa = new ArrayList<>(gioiHan);
        for (int i = 0; i < gioiHan; i++) {
            VXLNguoiChoi nguoiChoi = nguoiChois.get(i);
            tenMuc.add((nguoiChoi.quanTri ? "[AD] " : "") + nguoiChoi.ten
                    + " | cấp " + nguoiChoi.cap);
            cacMa.add(nguoiChoi.ma);
        }
        tenMuc.add("Quay lại");
        moDanhSach(quanTri, "NGƯỜI CHƠI ONLINE", tenMuc,
                new TrangThaiMenu(LoaiMenu.DANH_SACH_NGUOI_CHOI, -1,
                        List.copyOf(cacMa)));
    }

    private static void moMenuNguoiChoi(VXLNguoiChoi quanTri, int maMucTieu) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        moDanhSach(quanTri, "QUẢN TRỊ: " + mucTieu.ten,
                List.of("Thông tin", "Tài nguyên", "Tiến trình", "Vật phẩm",
                        "Tài khoản", "Lưu nhân vật", "Kick", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.NGUOI_CHOI, maMucTieu, List.of()));
    }

    private static void moMenuTaiNguyen(VXLNguoiChoi quanTri, int maMucTieu) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        moDanhSach(quanTri, "TÀI NGUYÊN: " + mucTieu.ten,
                List.of("+100.000 vàng", "-100.000 vàng", "+100 ngọc", "-100 ngọc",
                        "+10.000 EXP", "+100 điểm tiềm năng", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.TAI_NGUYEN, maMucTieu, List.of()));
    }

    private static void moMenuTienTrinh(VXLNguoiChoi quanTri, int maMucTieu) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        moDanhSach(quanTri, "TIẾN TRÌNH: " + mucTieu.ten,
                List.of("Tăng 1 cấp", "Tăng 1 mốc phiến quân", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.TIEN_TRINH, maMucTieu, List.of()));
    }

    private static void moMenuVatPham(VXLNguoiChoi quanTri, int maMucTieu) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        moDanhSach(quanTri, "VẬT PHẨM: " + mucTieu.ten,
                List.of("Thêm búa đục lỗ (349)", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.VAT_PHAM, maMucTieu, List.of()));
    }

    private static void moMenuTaiKhoan(VXLNguoiChoi quanTri, int maMucTieu) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        moDanhSach(quanTri, "TÀI KHOẢN: " + mucTieu.ten,
                List.of("Bật quyền admin", "Tắt quyền admin", "Khóa tài khoản",
                        "Mở khóa tài khoản", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.TAI_KHOAN, maMucTieu, List.of()));
    }

    private static void moMenuMayChu(VXLNguoiChoi quanTri) {
        moDanhSach(quanTri, "MÁY CHỦ",
                List.of("Danh sách online", "Thông tin server", "Thông tin luồng",
                        "Lưu tất cả", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.MAY_CHU, -1, List.of()));
    }

    private static void moDanhSach(VXLNguoiChoi quanTri, String tieuDe,
            List<String> cacMuc, TrangThaiMenu trangThai) {
        VXLDichVuNgocTrangBi.huyMenu(quanTri);
        TRANG_THAI.put(quanTri.ma, trangThai);
        quanTri.dichVu.moDanhSach(tieuDe, new Vector<>(cacMuc));
    }

    private static VXLNguoiChoi timNguoiChoi(int maNguoiChoi) {
        return VXLNguoiChoi.players_id.get(maNguoiChoi);
    }

    private static void baoMucTieuDaThoat(VXLNguoiChoi quanTri) {
        quanTri.moHopThoaiOK("Người chơi đã thoát. Hãy mở /menu và chọn lại.");
    }

    private static void hienKetQua(VXLNguoiChoi quanTri, String hanhDong,
            BoHanhDong boHanhDong) {
        boolean thanhCong = true;
        String ketQua;
        try {
            ketQua = boHanhDong.thucHien();
        }
        catch (SQLException | RuntimeException ex) {
            thanhCong = false;
            ketQua = "Không thể thực hiện: " + ex.getMessage();
        }
        quanTri.moHopThoaiOK(ketQua);
        VXLKhoQuanTri.ghiNhatKy(quanTri, hanhDong, thanhCong, ketQua);
    }

    @FunctionalInterface
    private interface BoHanhDong {
        String thucHien() throws SQLException;
    }

    private enum LoaiMenu {
        CHINH,
        DANH_SACH_NGUOI_CHOI,
        NGUOI_CHOI,
        TAI_NGUYEN,
        TIEN_TRINH,
        VAT_PHAM,
        TAI_KHOAN,
        MAY_CHU
    }

    private record TrangThaiMenu(LoaiMenu loai, int maMucTieu,
            List<Integer> maNguoiChoi) {
    }
}
