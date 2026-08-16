package com.vxl.luyentap;

import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VXLDatLaiPhienQuanHangNgay {
    private static final Logger LOGGER = Logger.getLogger(VXLDatLaiPhienQuanHangNgay.class.getName());
    private static final ZoneId MUI_GIO_VIET_NAM = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final LocalTime GIO_DAT_LAI = LocalTime.of(7, 0);
    private static final Path TEP_MOC_DAT_LAI = Path.of("cache", "phien-quan-reset-date.txt");
    private static final ScheduledExecutorService BO_LAP_LICH =
            Executors.newSingleThreadScheduledExecutor(tacVu -> {
                Thread thread = new Thread(tacVu, "vxl-dat-lai-phien-quan-hang-ngay");
                thread.setDaemon(true);
                return thread;
            });
    private static ScheduledFuture<?> tacVuKiemTra;
    private static LocalDate ngayDaDatLai;

    private VXLDatLaiPhienQuanHangNgay() {
    }

    public static synchronized void khoiDong() {
        if (tacVuKiemTra != null && !tacVuKiemTra.isCancelled()) {
            return;
        }
        ngayDaDatLai = docNgayDaDatLai();
        if (ngayDaDatLai == null) {
            ZonedDateTime hienTai = ZonedDateTime.now(MUI_GIO_VIET_NAM);
            ngayDaDatLai = hienTai.toLocalTime().isBefore(GIO_DAT_LAI)
                    ? hienTai.toLocalDate().minusDays(1L)
                    : hienTai.toLocalDate();
            luuNgayDaDatLai(ngayDaDatLai);
        }
        tacVuKiemTra = BO_LAP_LICH.scheduleAtFixedRate(
                VXLDatLaiPhienQuanHangNgay::kiemTraAnToan, 0L, 30L, TimeUnit.SECONDS);
    }

    public static synchronized void dung() {
        if (tacVuKiemTra != null) {
            tacVuKiemTra.cancel(false);
            tacVuKiemTra = null;
        }
    }

    public static synchronized String datLaiToanBoNguoiChoi() throws Exception {
        int soNguoiChoi = datLaiTrongCoSoDuLieu();
        ArrayList<VXLNguoiChoi> nguoiChoiTrucTuyen =
                new ArrayList<>(VXLNguoiChoi.players_id.values());
        for (VXLNguoiChoi nguoiChoi : nguoiChoiTrucTuyen) {
            if (nguoiChoi != null) {
                nguoiChoi.datLaiTienDoPhienQuanHangNgay();
                nguoiChoi.flushCache();
            }
        }
        ZonedDateTime hienTai = ZonedDateTime.now(MUI_GIO_VIET_NAM);
        LocalDate ngay = hienTai.toLocalDate();
        ngayDaDatLai = ngay;
        luuNgayDaDatLai(ngay);
        VXLQuanLyMayChu.log("[RESET-BOSS-ADMIN] Admin da reset phiến quân, 2 tòa tháp và các boss liên quan: DB="
                + soNguoiChoi + ", Online=" + nguoiChoiTrucTuyen.size());
        return "Đã reset mốc Bot/Phiến quân, 2 Tòa Tháp và tất cả Boss về mốc 1 cho toàn bộ người chơi!\n"
                + "- Đã cập nhật Database: " + soNguoiChoi + " tài khoản\n"
                + "- Đã cập nhật Online: " + nguoiChoiTrucTuyen.size() + " người chơi.";
    }

    private static void kiemTraAnToan() {
        try {
            kiemTraVaDatLaiNeuCan();
        }
        catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Không thể kiểm tra reset Phiến quân và Boss hằng ngày.", ex);
        }
    }

    private static synchronized void kiemTraVaDatLaiNeuCan() throws Exception {
        ZonedDateTime hienTai = ZonedDateTime.now(MUI_GIO_VIET_NAM);
        LocalDate ngayCanDatLai = hienTai.toLocalTime().isBefore(GIO_DAT_LAI)
                ? hienTai.toLocalDate().minusDays(1L)
                : hienTai.toLocalDate();
        if (ngayDaDatLai != null && !ngayCanDatLai.isAfter(ngayDaDatLai)) {
            return;
        }

        int soNguoiChoi = datLaiTrongCoSoDuLieu();
        ArrayList<VXLNguoiChoi> nguoiChoiTrucTuyen =
                new ArrayList<>(VXLNguoiChoi.players_id.values());
        for (VXLNguoiChoi nguoiChoi : nguoiChoiTrucTuyen) {
            if (nguoiChoi != null) {
                nguoiChoi.datLaiTienDoPhienQuanHangNgay();
            }
        }
        ngayDaDatLai = ngayCanDatLai;
        luuNgayDaDatLai(ngayCanDatLai);
        VXLQuanLyMayChu.log("[RESET-BOSS-AUTO] Đã reset Phiến quân, 2 Tòa Tháp & Boss lúc "
                + hienTai + ", dữ liệu=" + soNguoiChoi
                + ", online=" + nguoiChoiTrucTuyen.size() + '.');
    }

    private static int datLaiTrongCoSoDuLieu() throws Exception {
        final int[] soDong = new int[1];
        VXLCoSoDuLieu.withTransaction(conn -> {
            String sql = "UPDATE players SET stats_json = JSON_SET("
                    + "CASE WHEN JSON_VALID(stats_json) THEN stats_json ELSE JSON_OBJECT() END, "
                    + "'$.trainingRebelDefeated', 0, '$.trainingSuccess', 1, "
                    + "'$.kamikazeKills', 0, '$.bossKills', 0, "
                    + "'$.dailyKamikazeKills', 0, '$.dailyBossKills', 0, "
                    + "'$.dailyKamikazeClaimed', false, '$.dailyBossClaimed', false, '$.towerElo', 0)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                soDong[0] = stmt.executeUpdate();
            }
        });
        return soDong[0];
    }

    private static LocalDate docNgayDaDatLai() {
        if (!Files.isRegularFile(TEP_MOC_DAT_LAI)) {
            return null;
        }
        try {
            String giaTri = Files.readString(TEP_MOC_DAT_LAI, StandardCharsets.UTF_8).trim();
            return giaTri.isEmpty() ? null : LocalDate.parse(giaTri);
        }
        catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Không thể đọc mốc reset Phiến quân.", ex);
            return null;
        }
    }

    private static void luuNgayDaDatLai(LocalDate ngay) {
        try {
            Path thuMuc = TEP_MOC_DAT_LAI.getParent();
            if (thuMuc != null) {
                Files.createDirectories(thuMuc);
            }
            Files.writeString(TEP_MOC_DAT_LAI, ngay.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        }
        catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Không thể lưu mốc reset Phiến quân.", ex);
        }
    }
}