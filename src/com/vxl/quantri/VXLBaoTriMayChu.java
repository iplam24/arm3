package com.vxl.quantri;

import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mohinh.VXLNguoiDung;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class VXLBaoTriMayChu {
    private static final String LY_DO_MAC_DINH = "Máy chủ đang được bảo trì. Vui lòng quay lại sau.";
    private static final DateTimeFormatter DINH_DANG_THOI_GIAN = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    private static volatile boolean dangBaoTri;
    private static volatile String lyDo = LY_DO_MAC_DINH;
    private static volatile String nguoiBat = "";
    private static volatile Instant batLuc;

    private VXLBaoTriMayChu() {
    }

    public static boolean dangBaoTri() {
        return dangBaoTri;
    }

    public static String thongBaoDangNhap() {
        return "MÁY CHỦ ĐANG BẢO TRÌ\n" + lyDo;
    }

    public static synchronized VXLNguoiDung dangKyDangNhap(String khoaNguoiDung,
            VXLNguoiDung nguoiDungMoi) {
        if (nguoiDungMoi == null) {
            throw new IllegalArgumentException("Người dùng đăng nhập không hợp lệ.");
        }
        if (dangBaoTri && !nguoiDungMoi.laQuanTri()) {
            return nguoiDungMoi;
        }
        return VXLNguoiDung.users.put(khoaNguoiDung, nguoiDungMoi);
    }

    public static synchronized String bat(String tenQuanTri, String lyDoMoi) {
        boolean daBat = dangBaoTri;
        lyDo = chuanHoaLyDo(lyDoMoi);
        nguoiBat = tenQuanTri == null || tenQuanTri.isBlank() ? "admin" : tenQuanTri.trim();
        batLuc = Instant.now();
        dangBaoTri = true;

        String thongBao = "Máy chủ bắt đầu bảo trì. " + lyDo;
        VXLNguoiChoi.onChatFromToAllPlayer("HỆ THỐNG", thongBao);
        int soNguoiBiNgat = ngatNguoiDungThuong(thongBaoDangNhap());
        return (daBat ? "Đã cập nhật chế độ bảo trì." : "Đã bật chế độ bảo trì.")
                + "\nLý do: " + lyDo
                + "\nĐã ngắt " + soNguoiBiNgat + " tài khoản thường."
                + "\nAdmin vẫn có thể đăng nhập.";
    }

    public static synchronized String tat(String tenQuanTri) {
        if (!dangBaoTri) {
            return "Máy chủ hiện không ở chế độ bảo trì.";
        }
        dangBaoTri = false;
        String nguoiTat = tenQuanTri == null || tenQuanTri.isBlank() ? "admin" : tenQuanTri.trim();
        VXLNguoiChoi.onChatFromToAllPlayer("HỆ THỐNG",
                "Bảo trì đã kết thúc. Người chơi có thể đăng nhập bình thường.");
        return "Đã tắt chế độ bảo trì bởi " + nguoiTat + ".";
    }

    public static String trangThai() {
        if (!dangBaoTri) {
            return "BẢO TRÌ: TẮT";
        }
        Instant thoiDiemBat = batLuc;
        return "BẢO TRÌ: BẬT"
                + "\nLý do: " + lyDo
                + "\nNgười bật: " + nguoiBat
                + (thoiDiemBat == null ? "" : "\nBật lúc: " + DINH_DANG_THOI_GIAN.format(thoiDiemBat));
    }

    public static String trangThaiNgan() {
        return dangBaoTri ? "BẬT - " + lyDo : "TẮT";
    }

    private static int ngatNguoiDungThuong(String thongBao) {
        List<VXLNguoiDung> nguoiDungOnline = new ArrayList<>(VXLNguoiDung.users.values());
        int soNguoiBiNgat = 0;
        for (VXLNguoiDung nguoiDung : nguoiDungOnline) {
            if (nguoiDung == null || nguoiDung.laQuanTri()) {
                continue;
            }
            soNguoiBiNgat++;
            nguoiDung.thongBao(thongBao);
            CompletableFuture.delayedExecutor(500L, TimeUnit.MILLISECONDS).execute(() -> {
                try {
                    nguoiDung.dongKetNoi();
                }
                catch (RuntimeException ignored) {
                }
            });
        }
        return soNguoiBiNgat;
    }

    private static String chuanHoaLyDo(String noiDung) {
        if (noiDung == null || noiDung.isBlank()) {
            return LY_DO_MAC_DINH;
        }
        String ketQua = noiDung.trim();
        return ketQua.length() <= 160 ? ketQua : ketQua.substring(0, 160);
    }
}