package com.vxl.quantri;

import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLDichVuNgocTrangBi;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public final class VXLMenuQuanTri {
    private static final int MA_BUA_DUC_LO = 349;
    private static final int MA_BAO_HIEM = 353;
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
            case THONG_BAO -> xuLyMenuThongBao(quanTri, chiSo);
            case BAO_TRI -> xuLyMenuBaoTri(quanTri, chiSo);
            case TI_LE_EXP -> xuLyMenuTiLeExp(quanTri, chiSo);
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
            case 3 -> moMenuThongBao(quanTri);
            case 4 -> moMenuBaoTri(quanTri);
            case 5 -> hienKetQua(quanTri, "/menu help", VXLBoLenhQuanTri::huongDan);
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
            case 1 -> hienKetQua(quanTri, "/menu gold " + mucTieu.ten + " +1000000",
                    () -> VXLKhoQuanTri.congVang(mucTieu.ten, 1000000L));
            case 2 -> hienKetQua(quanTri, "/menu gold " + mucTieu.ten + " -100000",
                    () -> VXLKhoQuanTri.congVang(mucTieu.ten, -100000L));
            case 3 -> hienKetQua(quanTri, "/menu gem " + mucTieu.ten + " +100",
                    () -> VXLKhoQuanTri.congNgoc(mucTieu.ten, 100L));
            case 4 -> hienKetQua(quanTri, "/menu gem " + mucTieu.ten + " +1000",
                    () -> VXLKhoQuanTri.congNgoc(mucTieu.ten, 1000L));
            case 5 -> hienKetQua(quanTri, "/menu gem " + mucTieu.ten + " -100",
                    () -> VXLKhoQuanTri.congNgoc(mucTieu.ten, -100L));
            case 6 -> hienKetQua(quanTri, "/menu exp " + mucTieu.ten + " +10000",
                    () -> VXLKhoQuanTri.congKinhNghiem(mucTieu.ten, 10000L));
            case 7 -> hienKetQua(quanTri, "/menu exp " + mucTieu.ten + " +100000",
                    () -> VXLKhoQuanTri.congKinhNghiem(mucTieu.ten, 100000L));
            case 8 -> hienKetQua(quanTri, "/menu point " + mucTieu.ten + " +100",
                    () -> VXLKhoQuanTri.congDiemTiemNang(mucTieu.ten, 100L));
            case 9 -> hienKetQua(quanTri, "/menu point " + mucTieu.ten + " +1000",
                    () -> VXLKhoQuanTri.congDiemTiemNang(mucTieu.ten, 1000L));
            case 10 -> moMenuNguoiChoi(quanTri, maMucTieu);
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
            case 1 -> hienKetQua(quanTri, "/menu level " + mucTieu.ten + " +5",
                    () -> VXLKhoQuanTri.datCap(mucTieu.ten, mucTieu.cap + 5));
            case 2 -> hienKetQua(quanTri, "/menu level " + mucTieu.ten + " 50",
                    () -> VXLKhoQuanTri.datCap(mucTieu.ten, 50));
            case 3 -> hienKetQua(quanTri, "/menu level " + mucTieu.ten + " 100",
                    () -> VXLKhoQuanTri.datCap(mucTieu.ten, 100));
            case 4 -> hienKetQua(quanTri, "/menu rebel " + mucTieu.ten + " +1",
                    () -> VXLKhoQuanTri.datCapPhienQuan(mucTieu.ten,
                            Byte.toUnsignedInt(mucTieu.trainingSuccess) + 1));
            case 5 -> hienKetQua(quanTri, "/menu rebel " + mucTieu.ten + " 255",
                    () -> VXLKhoQuanTri.datCapPhienQuan(mucTieu.ten, 255));
            case 6 -> hienKetQua(quanTri, "/menu doublexp " + mucTieu.ten,
                    () -> {
                        mucTieu.kichHoatNhanDoiKinhNghiem();
                        return "Đã kích hoạt x2 EXP 24h cho " + mucTieu.ten + ".";
                    });
            case 7 -> moMenuNguoiChoi(quanTri, maMucTieu);
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
            case 0 -> hienKetQua(quanTri, "/menu item " + mucTieu.ten + " " + MA_BUA_DUC_LO + " 1",
                    () -> VXLKhoQuanTri.themVatPham(mucTieu.ten, MA_BUA_DUC_LO, 1));
            case 1 -> hienKetQua(quanTri, "/menu item " + mucTieu.ten + " " + MA_BUA_DUC_LO + " 10",
                    () -> VXLKhoQuanTri.themVatPham(mucTieu.ten, MA_BUA_DUC_LO, 10));
            case 2 -> hienKetQua(quanTri, "/menu item " + mucTieu.ten + " " + MA_BUA_DUC_LO + " 50",
                    () -> VXLKhoQuanTri.themVatPham(mucTieu.ten, MA_BUA_DUC_LO, 50));
            case 3 -> hienKetQua(quanTri, "/menu item " + mucTieu.ten + " " + MA_BAO_HIEM + " 10",
                    () -> VXLKhoQuanTri.themVatPham(mucTieu.ten, MA_BAO_HIEM, 10));
            case 4 -> moMenuNguoiChoi(quanTri, maMucTieu);
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
            case 4 -> hienKetQua(quanTri, "/menu gc", () -> {
                Runtime rt = Runtime.getRuntime();
                long truoc = rt.totalMemory() - rt.freeMemory();
                System.gc();
                long sau = rt.totalMemory() - rt.freeMemory();
                long giam = Math.max(0L, truoc - sau);
                return "Đã dọn dẹp bộ nhớ (GC)."
                        + "\nGiải phóng: " + String.format(Locale.ROOT, "%.2f MB", giam / 1024D / 1024D)
                        + "\nRAM đang dùng: " + String.format(Locale.ROOT, "%.2f MB", sau / 1024D / 1024D)
                        + " / " + String.format(Locale.ROOT, "%.2f MB", rt.maxMemory() / 1024D / 1024D);
            });
            case 5 -> moMenuTiLeExp(quanTri);
            case 6 -> moMenuChinh(quanTri);
            default -> {
            }
        }
    }

    private static void xuLyMenuThongBao(VXLNguoiChoi quanTri, int chiSo) {
        switch (chiSo) {
            case 0 -> hienKetQua(quanTri, "/menu fly Máy chủ sẽ bảo trì sau ít phút", () -> {
                String noiDung = "Máy chủ sẽ bảo trì sau ít phút. Các xạ thủ vui lòng hoàn tất trận đấu!";
                VXLThongBaoServer.guiMayBay(noiDung);
                return "Đã phát máy bay: " + noiDung;
            });
            case 1 -> hienKetQua(quanTri, "/menu fly Chào mừng xạ thủ", () -> {
                String noiDung = "Chào mừng các xạ thủ đến với thế giới Mobi Army 3!";
                VXLThongBaoServer.guiMayBay(noiDung);
                return "Đã phát máy bay: " + noiDung;
            });
            case 2 -> hienKetQua(quanTri, "/menu fly Sự kiện EXP", () -> {
                String noiDung = VXLQuanLyMayChu.expRate == 1
                        ? "Sự kiện Kinh Nghiệm đang diễn ra!"
                        : "Sự kiện x" + VXLQuanLyMayChu.expRate + " Kinh Nghiệm đang diễn ra!";
                VXLThongBaoServer.guiMayBay(noiDung);
                return "Đã phát máy bay: " + noiDung;
            });
            case 3 -> hienKetQua(quanTri, "/menu announce Bảo trì sắp diễn ra", () -> {
                String noiDung = "Máy chủ sẽ bảo trì trong ít phút nữa để nâng cấp hệ thống.";
                VXLNguoiChoi.onChatFromToAllPlayer("HỆ THỐNG", noiDung);
                return "Đã gửi chat hệ thống: " + noiDung;
            });
            case 4 -> hienKetQua(quanTri, "/menu announce Sự kiện EXP", () -> {
                String noiDung = VXLQuanLyMayChu.expRate == 1
                        ? "Sự kiện Kinh Nghiệm toàn máy chủ đang diễn ra!"
                        : "Sự kiện x" + VXLQuanLyMayChu.expRate + " EXP toàn máy chủ đang diễn ra, chúc các bạn chơi game vui vẻ!";
                VXLNguoiChoi.onChatFromToAllPlayer("HỆ THỐNG", noiDung);
                return "Đã gửi chat hệ thống: " + noiDung;
            });
            case 5 -> hienKetQua(quanTri, "/menu announce Cảnh báo bảo mật", () -> {
                String noiDung = "CẢNH BÁO: BQT không bao giờ hỏi mật khẩu của bạn. Tuyệt đối không chia sẻ tài khoản!";
                VXLNguoiChoi.onChatFromToAllPlayer("HỆ THỐNG", noiDung);
                return "Đã gửi chat hệ thống: " + noiDung;
            });
            case 6 -> hienKetQua(quanTri, "/menu modal Thông báo BQT", () -> {
                String tieuDe = "THÔNG BÁO TỪ BQT";
                String noiDung = "Chào các xạ thủ! Chúc các bạn có những giây phút trải nghiệm tuyệt vời cùng Mobi Army 3!";
                VXLThongBaoServer.guiModalOK(tieuDe, noiDung);
                return "Đã gửi thông báo popup toàn máy chủ.";
            });
            case 7 -> moMenuChinh(quanTri);
            default -> {
            }
        }
    }

    private static void xuLyMenuTiLeExp(VXLNguoiChoi quanTri, int chiSo) {
        if (chiSo == 8) {
            hienKetQua(quanTri, "/menu exprate",
                    () -> "Tỉ lệ EXP server hiện tại: x" + VXLQuanLyMayChu.expRate);
            return;
        }
        if (chiSo == 9) {
            moMenuMayChu(quanTri);
            return;
        }
        int heSo = switch (chiSo) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 5;
            case 5 -> 6;
            case 6 -> 8;
            case 7 -> 10;
            default -> -1;
        };
        if (heSo == -1) {
            moMenuMayChu(quanTri);
            return;
        }
        int heSoCu = VXLQuanLyMayChu.expRate;
        VXLQuanLyMayChu.expRate = heSo;
        String thongBao = heSo == 1
                ? "Máy chủ đã kết thúc sự kiện nhân EXP (Tỉ lệ x1)."
                : "Máy chủ đang áp dụng sự kiện x" + heSo + " EXP toàn server!";
        VXLThongBaoServer.guiMayBay(thongBao);
        hienKetQua(quanTri, "/menu exprate " + heSo,
                () -> "Đã đổi tỉ lệ EXP server từ x" + heSoCu + " -> x" + heSo + ".");
    }

    private static void moMenuTiLeExp(VXLNguoiChoi quanTri) {
        moDanhSach(quanTri, "TỈ LỆ EXP SERVER (Hiện: x" + VXLQuanLyMayChu.expRate + ")",
                List.of("Đặt x1 (Bình thường)", "Đặt x2 EXP", "Đặt x3 EXP", "Đặt x4 EXP",
                        "Đặt x5 EXP", "Đặt x6 EXP", "Đặt x8 EXP", "Đặt x10 EXP",
                        "Xem tỉ lệ hiện tại", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.TI_LE_EXP, -1, List.of()));
    }

    private static void xuLyMenuBaoTri(VXLNguoiChoi quanTri, int chiSo) {
        switch (chiSo) {
            case 0 -> hienKetQua(quanTri, "/menu baotri status", VXLBaoTriMayChu::trangThai);
            case 1 -> hienKetQua(quanTri, "/menu baotri on",
                    () -> VXLBaoTriMayChu.bat(quanTri.ten, "Bảo trì nâng cấp hệ thống"));
            case 2 -> hienKetQua(quanTri, "/menu baotri off",
                    () -> VXLBaoTriMayChu.tat(quanTri.ten));
            case 3 -> hienKetQua(quanTri, "/menu baotri 5",
                    () -> VXLBaoTriMayChu.datLich(quanTri.ten, 5, "Bảo trì nâng cấp hệ thống sau 5 phút"));
            case 4 -> hienKetQua(quanTri, "/menu baotri 10",
                    () -> VXLBaoTriMayChu.datLich(quanTri.ten, 10, "Bảo trì nâng cấp hệ thống sau 10 phút"));
            case 5 -> hienKetQua(quanTri, "/menu baotri 15",
                    () -> VXLBaoTriMayChu.datLich(quanTri.ten, 15, "Bảo trì nâng cấp hệ thống sau 15 phút"));
            case 6 -> hienKetQua(quanTri, "/menu baotri 30",
                    () -> VXLBaoTriMayChu.datLich(quanTri.ten, 30, "Bảo trì nâng cấp hệ thống sau 30 phút"));
            case 7 -> moMenuChinh(quanTri);
            default -> {
            }
        }
    }

    private static void moMenuChinh(VXLNguoiChoi quanTri) {
        moDanhSach(quanTri, "MENU ADMIN",
                List.of("Nhân vật của tôi", "Người chơi online", "Máy chủ & Hệ thống",
                        "Thông báo toàn server", "Bảo trì máy chủ", "Hướng dẫn lệnh nâng cao"),
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
        moDanhSach(quanTri, "NGƯỜI CHƠI ONLINE (" + nguoiChois.size() + ")", tenMuc,
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
                List.of("Thông tin chi tiết", "Tài nguyên (vàng/ngọc/exp)",
                        "Tiến trình (cấp/phiến quân)", "Vật phẩm", "Tài khoản",
                        "Lưu nhân vật", "Kick khỏi server", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.NGUOI_CHOI, maMucTieu, List.of()));
    }

    private static void moMenuTaiNguyen(VXLNguoiChoi quanTri, int maMucTieu) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        moDanhSach(quanTri, "TÀI NGUYÊN: " + mucTieu.ten,
                List.of("+100.000 vàng", "+1.000.000 vàng", "-100.000 vàng",
                        "+100 ngọc", "+1.000 ngọc", "-100 ngọc",
                        "+10.000 EXP", "+100.000 EXP",
                        "+100 điểm tiềm năng", "+1.000 điểm tiềm năng", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.TAI_NGUYEN, maMucTieu, List.of()));
    }

    private static void moMenuTienTrinh(VXLNguoiChoi quanTri, int maMucTieu) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        moDanhSach(quanTri, "TIẾN TRÌNH: " + mucTieu.ten,
                List.of("Tăng 1 cấp", "Tăng 5 cấp", "Đặt cấp 50", "Đặt cấp 100",
                        "Tăng 1 mốc phiến quân", "Đặt tối đa phiến quân (255)",
                        "Bật x2 EXP 24h (buff cá nhân)", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.TIEN_TRINH, maMucTieu, List.of()));
    }

    private static void moMenuVatPham(VXLNguoiChoi quanTri, int maMucTieu) {
        VXLNguoiChoi mucTieu = timNguoiChoi(maMucTieu);
        if (mucTieu == null) {
            baoMucTieuDaThoat(quanTri);
            return;
        }
        moDanhSach(quanTri, "VẬT PHẨM: " + mucTieu.ten,
                List.of("Thêm búa đục lỗ x1 (349)", "Thêm búa đục lỗ x10 (349)",
                        "Thêm búa đục lỗ x50 (349)", "Thêm bảo hiểm x10 (353)", "Quay lại"),
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
        moDanhSach(quanTri, "MÁY CHỦ & HỆ THỐNG",
                List.of("Danh sách online chi tiết", "Thông tin server (RAM, Uptime)",
                        "Thông tin luồng (Threads)", "Lưu tất cả nhân vật",
                        "Dọn rác bộ nhớ (GC RAM)",
                        "Tỉ lệ EXP server (x" + VXLQuanLyMayChu.expRate + ") -> Điều chỉnh", "Quay lại"),
                new TrangThaiMenu(LoaiMenu.MAY_CHU, -1, List.of()));
    }

    private static void moMenuThongBao(VXLNguoiChoi quanTri) {
        int heSo = VXLQuanLyMayChu.expRate;
        String expText = heSo == 1 ? "Không có sự kiện EXP" : "Sự kiện x" + heSo + " EXP";
        moDanhSach(quanTri, "THÔNG BÁO TOÀN SERVER",
                List.of("[Máy bay] Sắp bảo trì",
                        "[Máy bay] Chào mừng tân thủ",
                        "[Máy bay] " + expText,
                        "[Chat] Thông báo bảo trì",
                        "[Chat] " + expText,
                        "[Chat] Cảnh báo bảo mật tài khoản",
                        "[Popup] Gửi modal thông báo toàn server",
                        "Quay lại"),
                new TrangThaiMenu(LoaiMenu.THONG_BAO, -1, List.of()));
    }

    private static void moMenuBaoTri(VXLNguoiChoi quanTri) {
        moDanhSach(quanTri, "BẢO TRÌ MÁY CHỦ",
                List.of("Xem trạng thái bảo trì",
                        "Bật bảo trì ngay",
                        "Tắt bảo trì / Hủy hẹn giờ",
                        "Hẹn bảo trì sau 5 phút",
                        "Hẹn bảo trì sau 10 phút",
                        "Hẹn bảo trì sau 15 phút",
                        "Hẹn bảo trì sau 30 phút",
                        "Quay lại"),
                new TrangThaiMenu(LoaiMenu.BAO_TRI, -1, List.of()));
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
        MAY_CHU,
        THONG_BAO,
        BAO_TRI,
        TI_LE_EXP
    }

    private record TrangThaiMenu(LoaiMenu loai, int maMucTieu,
            List<Integer> maNguoiChoi) {
    }
}