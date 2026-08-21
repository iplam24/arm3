package com.vxl.quantri;

import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mohinh.VXLNguoiDung;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class VXLBaoTriMayChu {
    private static final String LY_DO_MAC_DINH = "Máy chủ đang được bảo trì. Vui lòng quay lại sau.";
    private static final DateTimeFormatter DINH_DANG_THOI_GIAN = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    private static final ScheduledExecutorService BO_HEN_GIO =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread luong = new Thread(runnable, "lich-bao-tri-may-chu");
                luong.setDaemon(true);
                return luong;
            });
    private static volatile boolean dangBaoTri;
    private static volatile String lyDo = LY_DO_MAC_DINH;
    private static volatile String nguoiBat = "";
    private static volatile Instant batLuc;
    private static volatile Instant baoTriLuc;
    private static volatile ScheduledFuture<?> lichBaoTri;
    private static volatile long phienLich;

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
        huyLichNoiBo();
        return batNoiBo(tenQuanTri, lyDoMoi);
    }

    public static synchronized String datLich(String tenQuanTri, int soPhut, String lyDoMoi) {
        if (soPhut < 1 || soPhut > 7 * 24 * 60) {
            throw new IllegalArgumentException("Số phút bảo trì phải từ 1 đến 10080.");
        }
        if (dangBaoTri) {
            return "Máy chủ đang ở chế độ bảo trì. Hãy tắt bảo trì trước khi đặt lịch mới.";
        }
        huyLichNoiBo();
        String lyDoHen = chuanHoaLyDo(lyDoMoi);
        String nguoiHen = chuanHoaTenQuanTri(tenQuanTri);
        Instant thoiDiemBaoTri = Instant.now().plusSeconds(TimeUnit.MINUTES.toSeconds(soPhut));
        long phienMoi = ++phienLich;
        lyDo = lyDoHen;
        nguoiBat = nguoiHen;
        baoTriLuc = thoiDiemBaoTri;
        lichBaoTri = BO_HEN_GIO.schedule(
                () -> batTheoLich(phienMoi, nguoiHen, lyDoHen), soPhut, TimeUnit.MINUTES);

        String thongBao = "Máy chủ sẽ bảo trì sau " + soPhut + " phút. " + lyDoHen;
        VXLThongBaoServer.guiThongBaoChat("HỆ THỐNG", thongBao);
        VXLThongBaoServer.guiMayBay(thongBao);
        return "Đã đặt lịch bảo trì sau " + soPhut + " phút."
                + "\nBắt đầu lúc: " + DINH_DANG_THOI_GIAN.format(thoiDiemBaoTri)
                + "\nLý do: " + lyDoHen;
    }

    private static String batNoiBo(String tenQuanTri, String lyDoMoi) {
        boolean daBat = dangBaoTri;
        lyDo = chuanHoaLyDo(lyDoMoi);
        nguoiBat = chuanHoaTenQuanTri(tenQuanTri);
        batLuc = Instant.now();
        baoTriLuc = null;
        dangBaoTri = true;

        String thongBao = "Máy chủ bắt đầu bảo trì. " + lyDo;
        VXLThongBaoServer.guiThongBaoChat("HỆ THỐNG", thongBao);
        int soNguoiBiNgat = ngatNguoiDungThuong(thongBaoDangNhap());
        return (daBat ? "Đã cập nhật chế độ bảo trì." : "Đã bật chế độ bảo trì.")
                + "\nLý do: " + lyDo
                + "\nĐã ngắt " + soNguoiBiNgat + " tài khoản thường."
                + "\nAdmin vẫn có thể đăng nhập.";
    }

    public static synchronized String tat(String tenQuanTri) {
        if (!dangBaoTri) {
            if (coLichBaoTri()) {
                huyLichNoiBo();
                return "Đã hủy lịch bảo trì bởi " + chuanHoaTenQuanTri(tenQuanTri) + ".";
            }
            return "Máy chủ hiện không ở chế độ bảo trì.";
        }
        dangBaoTri = false;
        batLuc = null;
        String nguoiTat = chuanHoaTenQuanTri(tenQuanTri);
        VXLNguoiChoi.onChatFromToAllPlayer("HỆ THỐNG",
                "Bảo trì đã kết thúc. Người chơi có thể đăng nhập bình thường.");
        return "Đã tắt chế độ bảo trì bởi " + nguoiTat + ".";
    }

    public static synchronized String trangThai() {
        if (!dangBaoTri) {
            if (coLichBaoTri()) {
                return "BẢO TRÌ: ĐÃ HẸN"
                        + "\nLý do: " + lyDo
                        + "\nNgười đặt: " + nguoiBat
                        + "\nBắt đầu lúc: " + DINH_DANG_THOI_GIAN.format(baoTriLuc);
            }
            return "BẢO TRÌ: TẮT";
        }
        Instant thoiDiemBat = batLuc;
        return "BẢO TRÌ: BẬT"
                + "\nLý do: " + lyDo
                + "\nNgười bật: " + nguoiBat
                + (thoiDiemBat == null ? "" : "\nBật lúc: " + DINH_DANG_THOI_GIAN.format(thoiDiemBat));
    }

    public static synchronized String trangThaiNgan() {
        if (coLichBaoTri()) {
            return "HẸN " + DINH_DANG_THOI_GIAN.format(baoTriLuc) + " - " + lyDo;
        }
        return dangBaoTri ? "BẬT - " + lyDo : "TẮT";
    }

    private static synchronized void batTheoLich(long phien, String tenQuanTri, String lyDoHen) {
        if (phien != phienLich || !coLichBaoTri() || dangBaoTri) {
            return;
        }
        lichBaoTri = null;
        baoTriLuc = null;
        batNoiBo(tenQuanTri, lyDoHen);
    }

    private static boolean coLichBaoTri() {
        ScheduledFuture<?> lich = lichBaoTri;
        return lich != null && !lich.isCancelled() && !lich.isDone() && baoTriLuc != null;
    }

    private static void huyLichNoiBo() {
        phienLich++;
        ScheduledFuture<?> lich = lichBaoTri;
        lichBaoTri = null;
        baoTriLuc = null;
        if (lich != null) {
            lich.cancel(false);
        }
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

    private static String chuanHoaTenQuanTri(String tenQuanTri) {
        return tenQuanTri == null || tenQuanTri.isBlank() ? "admin" : tenQuanTri.trim();
    }
}
