package com.vxl.quantri;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class VXLBoLenhQuanTri {
    private static final String HUONG_DAN = """
            LỆNH ADMIN
            /admin online
            /admin server
            /admin threads
            /admin inspect <tên>
            /admin gold <tên> <số +/- >
            /admin gem <tên> <số +/- >
            /admin exp <tên> <số +/- >
            /admin point <tên> <số +/- >
            /admin level <tên> <cấp>
            /admin rebel <tên> <mốc>
            /admin item <tên> <itemId> [số lượng]
            /admin save [tên|all]
            /admin announce <nội dung>
            /admin fly <nội dung>
            /admin baotri <on [lý do]|off|status|<số phút> [lý do]>
            /admin kick <tên>
            /admin ban <tên> <on|off>
            /admin grant <tên> <on|off>
            """;

    private VXLBoLenhQuanTri() {
    }

    static String huongDan() {
        return HUONG_DAN;
    }

    public static boolean xuLy(VXLNguoiChoi nguoiChoi, String noiDung) {
        String lenh = noiDung == null ? "" : noiDung.trim();
        boolean lenhMoMenu = "/menu".equalsIgnoreCase(lenh)
                || "menu".equalsIgnoreCase(lenh)
                || "!menu".equalsIgnoreCase(lenh);
        String phanConLai = lenhMoMenu ? "menu" : layPhanLenh(lenh);
        if (phanConLai == null) {
            return false;
        }
        if (nguoiChoi == null || !nguoiChoi.quanTri) {
            if (nguoiChoi != null) {
                nguoiChoi.moHopThoaiOK("Bạn không có quyền sử dụng lệnh admin.");
            }
            return true;
        }
        if (lenhMoMenu || phanConLai.isBlank() || "menu".equalsIgnoreCase(phanConLai)) {
            VXLMenuQuanTri.mo(nguoiChoi);
            VXLKhoQuanTri.ghiNhatKy(nguoiChoi, lenh, true, "Đã mở menu admin.");
            return true;
        }

        boolean thanhCong = true;
        String ketQua;
        try {
            ketQua = thucHien(nguoiChoi, phanConLai);
        }
        catch (IllegalArgumentException ex) {
            thanhCong = false;
            ketQua = ex.getMessage() + "\n\n" + HUONG_DAN;
        }
        catch (SQLException ex) {
            thanhCong = false;
            ketQua = "Lỗi cơ sở dữ liệu: " + ex.getMessage();
        }
        catch (RuntimeException ex) {
            thanhCong = false;
            ketQua = "Không thể thực hiện lệnh: " + ex.getMessage();
        }
        nguoiChoi.moHopThoaiOK(ketQua);
        VXLKhoQuanTri.ghiNhatKy(nguoiChoi, lenh, thanhCong, ketQua);
        return true;
    }

    private static String thucHien(VXLNguoiChoi quanTri, String phanConLai) throws SQLException {
        if (phanConLai.isBlank()) {
            return HUONG_DAN;
        }
        String[] thamSo = phanConLai.split("\s+");
        String lenh = thamSo[0].toLowerCase(Locale.ROOT);
        return switch (lenh) {
            case "help" -> HUONG_DAN;
            case "menu" -> HUONG_DAN;
            case "online" -> VXLKhoQuanTri.danhSachTrucTuyen();
            case "server" -> thongTinMayChu();
            case "threads", "thread", "luong" -> thongTinLuong();
            case "inspect", "info" -> VXLKhoQuanTri.thongTinNguoiChoi(lay(thamSo, 1, "tên người chơi"));
            case "gold", "vang" -> VXLKhoQuanTri.congVang(lay(thamSo, 1, "tên người chơi"),
                    docSo(lay(thamSo, 2, "số vàng"), "số vàng"));
            case "gem", "ngoc" -> VXLKhoQuanTri.congNgoc(lay(thamSo, 1, "tên người chơi"),
                    docSo(lay(thamSo, 2, "số ngọc"), "số ngọc"));
            case "exp" -> VXLKhoQuanTri.congKinhNghiem(lay(thamSo, 1, "tên người chơi"),
                    docSo(lay(thamSo, 2, "số EXP"), "số EXP"));
            case "point", "tiemnang" -> VXLKhoQuanTri.congDiemTiemNang(lay(thamSo, 1, "tên người chơi"),
                    docSo(lay(thamSo, 2, "số điểm"), "số điểm"));
            case "level", "cap" -> VXLKhoQuanTri.datCap(lay(thamSo, 1, "tên người chơi"),
                    docInt(lay(thamSo, 2, "cấp"), "cấp"));
            case "rebel", "phienquan" -> VXLKhoQuanTri.datCapPhienQuan(lay(thamSo, 1, "tên người chơi"),
                    docInt(lay(thamSo, 2, "mốc phiến quân"), "mốc phiến quân"));
            case "item" -> VXLKhoQuanTri.themVatPham(lay(thamSo, 1, "tên người chơi"),
                    docInt(lay(thamSo, 2, "item ID"), "item ID"),
                    thamSo.length >= 4 ? docInt(thamSo[3], "số lượng") : 1);
            case "save", "luu" -> VXLKhoQuanTri.luuNguoiChoi(thamSo.length >= 2 ? thamSo[1] : "all");
            case "announce", "thongbao" -> thongBao(phanConLai);
            case "fly", "maybay" -> {
                String noiDungFly = layNoiDungSauThamSo(phanConLai, 1);
                if (noiDungFly.isBlank()) {
                    throw new IllegalArgumentException("Thiếu nội dung máy bay thông báo.");
                }
                VXLThongBaoServer.guiMayBay(noiDungFly);
                yield "Đã cho máy bay bay qua mang thông báo: " + noiDungFly;
            }
            case "baotri", "maintenance" -> baoTri(quanTri, phanConLai, thamSo);
            case "kick" -> kick(quanTri, lay(thamSo, 1, "tên người chơi"));
            case "ban", "khoa" -> VXLKhoQuanTri.datKhoaTaiKhoan(lay(thamSo, 1, "tên người chơi"),
                    docBatTat(lay(thamSo, 2, "on/off")));
            case "grant", "admin" -> VXLKhoQuanTri.datQuyenQuanTri(lay(thamSo, 1, "tên người chơi"),
                    docBatTat(lay(thamSo, 2, "on/off")));
            default -> throw new IllegalArgumentException("Lệnh admin không tồn tại: " + lenh);
        };
    }

    private static String thongBao(String phanConLai) {
        int viTri = phanConLai.indexOf(' ');
        if (viTri < 0 || phanConLai.substring(viTri + 1).isBlank()) {
            throw new IllegalArgumentException("Thiếu nội dung thông báo.");
        }
        String noiDung = phanConLai.substring(viTri + 1).trim();
        VXLNguoiChoi.onChatFromToAllPlayer("HỆ THỐNG", noiDung);
        return "Đã gửi thông báo tới toàn máy chủ.";
    }

    private static String baoTri(VXLNguoiChoi quanTri, String phanConLai, String[] thamSo) {
        if (thamSo.length < 2 || "status".equalsIgnoreCase(thamSo[1])
                || "trangthai".equalsIgnoreCase(thamSo[1])) {
            return VXLBaoTriMayChu.trangThai();
        }
        String hanhDong = thamSo[1].toLowerCase(Locale.ROOT);
        if (isSo(thamSo[1])) {
            int soPhut = docInt(thamSo[1], "số phút");
            return VXLBaoTriMayChu.datLich(quanTri.ten, soPhut,
                    layNoiDungSauThamSo(phanConLai, 2));
        }
        return switch (hanhDong) {
            case "on", "bat", "1", "true" -> VXLBaoTriMayChu.bat(quanTri.ten,
                    layNoiDungSauThamSo(phanConLai, 2));
            case "off", "tat", "0", "false" -> VXLBaoTriMayChu.tat(quanTri.ten);
            default -> throw new IllegalArgumentException(
                    "Cú pháp: /admin baotri <on [lý do]|off|status|<số phút> [lý do]>.");
        };
    }

    static String kick(VXLNguoiChoi quanTri, String ten) {
        VXLNguoiChoi mucTieu = VXLKhoQuanTri.timNguoiChoiTrucTuyen(ten);
        if (mucTieu == null) {
            return "Người chơi " + ten + " không trực tuyến.";
        }
        if (mucTieu == quanTri) {
            return "Không thể tự kick chính mình.";
        }
        String tenMucTieu = mucTieu.ten;
        mucTieu.dichVu.dongKetNoi();
        return "Đã ngắt kết nối " + tenMucTieu + ".";
    }

    static String thongTinMayChu() {
        Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long boNhoDaDung = runtime.totalMemory() - runtime.freeMemory();
        return "SERVER\n"
                + "Kết nối: " + VXLQuanLyMayChu.getOnlineCount() + "\n"
                + "Nhân vật online: " + VXLNguoiChoi.players_id.size() + "\n"
                + "Bảo trì: " + VXLBaoTriMayChu.trangThaiNgan() + "\n"
                + "Uptime: " + dinhDangThoiGian(runtimeBean.getUptime()) + "\n"
                + "RAM: " + dinhDangDungLuong(boNhoDaDung) + " / "
                + dinhDangDungLuong(runtime.maxMemory()) + "\n"
                + "Luồng: " + Thread.getAllStackTraces().size();
    }

    static String thongTinLuong() {
        List<Thread> luongs = new ArrayList<>(Thread.getAllStackTraces().keySet());
        luongs.sort(Comparator.comparing(Thread::getName, String.CASE_INSENSITIVE_ORDER));
        Map<Thread.State, Integer> theoTrangThai = new EnumMap<>(Thread.State.class);
        for (Thread luong : luongs) {
            theoTrangThai.merge(luong.getState(), 1, Integer::sum);
        }
        StringBuilder ketQua = new StringBuilder("Tổng luồng: ").append(luongs.size());
        for (Map.Entry<Thread.State, Integer> muc : theoTrangThai.entrySet()) {
            ketQua.append("\n").append(muc.getKey()).append(": ").append(muc.getValue());
        }
        ketQua.append("\n\nDanh sách:");
        int gioiHan = Math.min(25, luongs.size());
        for (int i = 0; i < gioiHan; i++) {
            Thread luong = luongs.get(i);
            ketQua.append("\n- ").append(luong.getName()).append(" [")
                    .append(luong.getState()).append(luong.isDaemon() ? ", daemon]" : "]");
        }
        if (luongs.size() > gioiHan) {
            ketQua.append("\n... và ").append(luongs.size() - gioiHan).append(" luồng khác");
        }
        return ketQua.toString();
    }

    private static String lay(String[] thamSo, int chiSo, String ten) {
        if (chiSo >= thamSo.length || thamSo[chiSo].isBlank()) {
            throw new IllegalArgumentException("Thiếu " + ten + ".");
        }
        return thamSo[chiSo];
    }

    private static String layNoiDungSauThamSo(String phanConLai, int soThamSoBoQua) {
        int viTri = 0;
        int soThamSo = 0;
        while (viTri < phanConLai.length() && soThamSo < soThamSoBoQua) {
            while (viTri < phanConLai.length() && Character.isWhitespace(phanConLai.charAt(viTri))) {
                viTri++;
            }
            while (viTri < phanConLai.length() && !Character.isWhitespace(phanConLai.charAt(viTri))) {
                viTri++;
            }
            soThamSo++;
        }
        while (viTri < phanConLai.length() && Character.isWhitespace(phanConLai.charAt(viTri))) {
            viTri++;
        }
        return viTri < phanConLai.length() ? phanConLai.substring(viTri).trim() : "";
    }

    private static int docInt(String giaTri, String ten) {
        long so = docSo(giaTri, ten);
        if (so < Integer.MIN_VALUE || so > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(ten + " vượt giới hạn.");
        }
        return (int)so;
    }

    private static long docSo(String giaTri, String ten) {
        try {
            return Long.parseLong(giaTri.replace("+", ""));
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException(ten + " không hợp lệ.");
        }
    }

    private static boolean docBatTat(String giaTri) {
        return switch (giaTri.toLowerCase(Locale.ROOT)) {
            case "1", "on", "true", "bat" -> true;
            case "0", "off", "false", "tat" -> false;
            default -> throw new IllegalArgumentException("Giá trị phải là on hoặc off.");
        };
    }

    private static boolean isSo(String giaTri) {
        if (giaTri == null || giaTri.isBlank()) {
            return false;
        }
        for (int i = 0; i < giaTri.length(); i++) {
            char kyTu = giaTri.charAt(i);
            if (kyTu < '0' || kyTu > '9') {
                return false;
            }
        }
        return true;
    }

    private static String layPhanLenh(String noiDung) {
        String chuThuong = noiDung.toLowerCase(Locale.ROOT);
        for (String tienTo : new String[]{"/admin", "/ad", "!admin", "admin", "ad"}) {
            if (chuThuong.equals(tienTo)) {
                return "";
            }
            if (chuThuong.startsWith(tienTo + " ")) {
                return noiDung.substring(tienTo.length()).trim();
            }
        }
        return null;
    }

    private static String dinhDangThoiGian(long miliGiay) {
        long giay = Math.max(0L, miliGiay / 1000L);
        long ngay = giay / 86400L;
        long gio = giay % 86400L / 3600L;
        long phut = giay % 3600L / 60L;
        return ngay + "d " + gio + "h " + phut + "m";
    }

    private static String dinhDangDungLuong(long byteCount) {
        return String.format(Locale.ROOT, "%.1f MB", byteCount / 1024D / 1024D);
    }
}
