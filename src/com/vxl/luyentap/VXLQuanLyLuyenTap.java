package com.vxl.luyentap;

import com.alibaba.fastjson2.JSONObject;
import com.vxl.chien.VXLCauHinhVatPhamChienDau;
import com.vxl.chien.VXLChienBinh;
import com.vxl.chien.VXLTinhSatThuong;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VXLQuanLyLuyenTap {
    private static final int SO_PHIEN_QUAN = 1;
    private static final byte MA_BAN_DO_LUYEN_TAP = 0;
    private static final short DAU_PHIEN_QUAN = 59;
    private static final short QUAN_PHIEN_QUAN = 157;
    private static final short AO_PHIEN_QUAN = 158;
    private static final short NON_PHIEN_QUAN = 159;
    private static final short CANH_PHIEN_QUAN = 160;
    private static final short VU_KHI_PHIEN_QUAN = 27;
    private static final long TRE_CHUAN_BI_DAN_BOT = 450L;
    private static final long THOI_GIAN_MOI_DIEM_DAN_BOT = 22L;
    private static final long TRE_SAU_VA_CHAM_DAN_BOT = 700L;
    private static final long TRE_KET_THUC_DAN_BOT_TOI_THIEU = 1700L;
    private static final long TRE_KET_THUC_DAN_BOT_TOI_DA = 6000L;
    private static final long TRE_NGAM_PHIEN_QUAN_TOI_THIEU = 1500L;
    private static final long TRE_NGAM_PHIEN_QUAN_TOI_DA = 2400L;
    private static final long TRE_HIEN_KET_QUA_SAU_HIEU_UNG = 1000L;
    private static final int TI_LE_TRUNG_PHIEN_QUAN_TOI_THIEU = 22;
    private static final int TI_LE_TRUNG_PHIEN_QUAN_TOI_DA = 30;
    private static final int KHOANG_TRUOT_DAT_TOI_THIEU = 75;
    private static final int KHOANG_TRUOT_DAT_TOI_DA = 145;
    private static final ScheduledExecutorService BO_LAP_LICH = Executors.newSingleThreadScheduledExecutor(tacVu -> {
        Thread thread = new Thread(tacVu, "vxl-phien-quan-luyen-tap");
        thread.setDaemon(true);
        return thread;
    });
    private final VXLNguoiChoi nguoiChoi;
    private final int[] mauPhienQuan = new int[SO_PHIEN_QUAN];
    private final short[] phienQuanX = new short[SO_PHIEN_QUAN];
    private final short[] phienQuanY = new short[SO_PHIEN_QUAN];
    private final boolean[] phienQuanDaChet = new boolean[SO_PHIEN_QUAN];
    private final VXLTinhDuongDanLuyenTap tinhDuongDan;
    private VXLChienBinh chiSoNguoiChoi;
    private VXLChiSoPhienQuan chiSoPhienQuan;
    private volatile int soPhienQuanDaHa;
    private int capPhienQuanHienTai = 1;
    private int mauToiDaPhienQuan = 1000;
    private int mauNguoiChoi = 100;
    private int tanCongPhienQuan;
    private int giapPhienQuan;
    private int khienPhienQuan;
    private short nguoiChoiX = 220;
    private short nguoiChoiY = 300;
    private int phienQuanDangBiBan = -1;
    private int luotPhienQuan = -1;
    private long lanBanCuoi;
    private boolean dangChoKetThucPhatBan;
    private boolean duKienTrungPhienQuan;
    private boolean daGuiLuotDau;
    private boolean phienQuanDangHoatDong;
    private boolean choKyNangDacBiet;
    private boolean danManhPhienQuan;
    private byte lucPhatBanNguoiChoi = 10;
    private int soLuotPhienQuan;
    private ScheduledFuture<?> tacVuPhienQuan;
    private ScheduledFuture<?> tacVuKetThucPhienQuan;
    private ScheduledFuture<?> tacVuDuPhongPhatBan;
    private ScheduledFuture<?> tacVuKetQuaTran;

    public VXLQuanLyLuyenTap(VXLNguoiChoi nguoiChoi) {
        this.nguoiChoi = nguoiChoi;
        this.tinhDuongDan = new VXLTinhDuongDanLuyenTap(this.phienQuanX, this.phienQuanY, this.phienQuanDaChet);
    }

    public void tai(JSONObject duLieu) {
        Object giaTri = duLieu != null ? duLieu.get("trainingRebelDefeated") : null;
        int daHa = 0;
        if (giaTri != null) {
            try {
                daHa = Integer.parseInt(giaTri.toString());
            }
            catch (NumberFormatException ex) {
                daHa = 0;
            }
        } else if (duLieu != null) {
            Object capLuyenTapCu = duLieu.get("trainingSuccess");
            if (capLuyenTapCu != null) {
                try {
                    daHa = Math.max(0, Integer.parseInt(capLuyenTapCu.toString()) - 1);
                }
                catch (NumberFormatException ex) {
                    daHa = 0;
                }
            }
        }
        this.soPhienQuanDaHa = Math.max(0, Math.min(VXLCauHinhPhienQuan.CAP_TOI_DA, daHa));
        this.nguoiChoi.trainingSuccess = (byte)Math.min(VXLCauHinhPhienQuan.CAP_TOI_DA, this.soPhienQuanDaHa + 1);
    }

    public void ghiVao(JSONObject duLieu) {
        duLieu.put("trainingRebelDefeated", this.soPhienQuanDaHa);
    }

    public synchronized void vao() {
        try {
            if (this.soPhienQuanDaHa >= VXLCauHinhPhienQuan.CAP_TOI_DA) {
                this.nguoiChoi.startOKDlg2("Bạn đã hoàn thành toàn bộ 40 Phiến quân luyện tập.");
                return;
            }
            this.dungTacVu();
            short vuKhiNguoiChoi = this.nguoiChoi.wp > 0 ? this.nguoiChoi.wp : 5;
            this.nguoiChoi.wp = vuKhiNguoiChoi;
            this.capPhienQuanHienTai = VXLCauHinhPhienQuan.gioiHanCap(this.soPhienQuanDaHa + 1);
            this.chiSoPhienQuan = VXLCauHinhPhienQuan.taoChiSo(this.capPhienQuanHienTai);
            this.mauToiDaPhienQuan = this.chiSoPhienQuan.mauToiDa;
            this.tanCongPhienQuan = this.chiSoPhienQuan.tanCong;
            this.giapPhienQuan = this.chiSoPhienQuan.giap;
            this.nguoiChoiX = 220;
            this.nguoiChoiY = this.tinhDuongDan.layBanDo().timViTriDat(this.nguoiChoiX, (short)250);
            this.chiSoNguoiChoi = new VXLChienBinh(this.nguoiChoi, (byte)0, this.nguoiChoiX, this.nguoiChoiY);
            this.nguoiChoi.trainingSuccess = (byte)this.capPhienQuanHienTai;
            this.nguoiChoi.isReady = true;
            this.nguoiChoi.chiSo = 0;
            this.nguoiChoi.pointSeat = 0;
            this.nguoiChoi.inTraining = true;
            this.mauNguoiChoi = this.chiSoNguoiChoi.mauToiDa;
            this.dangChoKetThucPhatBan = false;
            this.duKienTrungPhienQuan = false;
            this.daGuiLuotDau = false;
            this.phienQuanDangHoatDong = false;
            this.choKyNangDacBiet = false;
            this.khienPhienQuan = 0;
            this.danManhPhienQuan = false;
            this.lucPhatBanNguoiChoi = 10;
            this.soLuotPhienQuan = 0;
            this.phienQuanDangBiBan = -1;
            this.luotPhienQuan = -1;
            this.mauPhienQuan[0] = this.mauToiDaPhienQuan;
            this.phienQuanX[0] = 600;
            this.phienQuanY[0] = this.tinhDuongDan.layBanDo().timViTriDat(this.phienQuanX[0], (short)250);
            this.phienQuanDaChet[0] = false;

            byte maBanDo = MA_BAN_DO_LUYEN_TAP;
            short vuKhiPhienQuan = VU_KHI_PHIEN_QUAN;
            this.nguoiChoi.dichVu.guiThongTinLuyenTap();
            this.nguoiChoi.dichVu.guiChonBanDoLuyenTap(maBanDo);
            this.nguoiChoi.dichVu.guiNguoiChoiLuyenTap((byte)0, this.nguoiChoi.ma, this.nguoiChoi.ten,
                    this.nguoiChoi.head, this.nguoiChoi.leg, this.nguoiChoi.body, this.nguoiChoi.hat,
                    this.nguoiChoi.wing, vuKhiNguoiChoi, this.nguoiChoi.avenger, this.nguoiChoi.ma);
            this.nguoiChoi.dichVu.guiNguoiChoiLuyenTap((byte)1, -12000 - this.capPhienQuanHienTai,
                    VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), DAU_PHIEN_QUAN,
                    QUAN_PHIEN_QUAN, AO_PHIEN_QUAN, NON_PHIEN_QUAN,
                    CANH_PHIEN_QUAN, vuKhiPhienQuan, (byte)0, this.nguoiChoi.ma);
            this.nguoiChoi.dichVu.guiBatDauLuyenTap(maBanDo, vuKhiNguoiChoi,
                    this.mauNguoiChoi, this.phienQuanX, this.phienQuanY,
                    this.mauPhienQuan, new short[]{vuKhiPhienQuan});
        }
        catch (Exception ex) {
            this.resetTrangThai();
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Không thể bắt đầu luyện tập.", ex);
        }
    }

    public void diChuyen(VXLTinNhan tinNhan) throws IOException {
        short x = tinNhan.boDoc().readShort();
        short y = tinNhan.boDoc().readShort();
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        this.nguoiChoiX = this.tinhDuongDan.gioiHan(x, 0, 1200);
        this.nguoiChoiY = this.tinhDuongDan.gioiHan(y, 0, 700);
        this.nguoiChoi.dichVu.guiCapNhatXYLuyenTap((byte)0, this.nguoiChoiX, this.nguoiChoiY);
    }

    public void ban(VXLTinNhan tinNhan) throws IOException {
        if (!this.nguoiChoi.inTraining || this.phienQuanDangHoatDong) {
            return;
        }
        this.huyTacVuPhienQuan();
        long hienTai = System.currentTimeMillis();
        if (hienTai - this.lanBanCuoi < 250L) {
            return;
        }
        this.lanBanCuoi = hienTai;
        byte loaiDanGui = tinNhan.boDoc().readByte();
        short x = tinNhan.boDoc().readShort();
        short y = tinNhan.boDoc().readShort();
        short goc = tinNhan.boDoc().readShort();
        byte luc = tinNhan.boDoc().readByte();
        if (this.laDanDoi(loaiDanGui)) {
            tinNhan.boDoc().readByte();
        }
        byte soPhat = tinNhan.boDoc().readByte();
        luc = (byte)Math.max(10, Math.min(30, luc));
        this.lucPhatBanNguoiChoi = luc;
        this.nguoiChoiX = this.tinhDuongDan.gioiHan(x, 0, 1200);
        this.nguoiChoiY = this.tinhDuongDan.gioiHan(y, 0, 700);
        byte loaiDan = loaiDanGui;
        if (this.nguoiChoi.avenger == 3) {
            loaiDan = 82;
        } else if (this.nguoiChoi.avenger == 5) {
            loaiDan = 83;
        } else if (this.nguoiChoi.avenger == 6) {
            loaiDan = 80;
            soPhat = 1;
        } else if (this.nguoiChoi.avenger == 7) {
            loaiDan = 9;
            soPhat = 1;
        }
        short[][] duongDan = this.tinhDuongDan.taoDuongDanCong(this.nguoiChoiX,
                this.nguoiChoiY, goc, luc, loaiDan);
        short[] duongX = duongDan[0];
        short[] duongY = duongDan[1];
        this.phienQuanDangBiBan = this.tinhDuongDan.timBotTrungDuong(duongX, duongY);
        if (this.phienQuanDangBiBan < 0 && loaiDan == 80) {
            this.phienQuanDangBiBan = this.tinhDuongDan.timBotTrungDuong(duongX, duongY, 85);
        }
        this.duKienTrungPhienQuan = this.phienQuanDangBiBan >= 0;
        this.dangChoKetThucPhatBan = true;
        this.choKyNangDacBiet = false;
        System.out.println(String.format("[TRAINING-FIRE] %s bắn | LoaiDan=%d | Goc=%d | Luc=%d | Pos=(%d,%d) | Points=%d | Target=%s",
                this.nguoiChoi.ten, loaiDan, goc, luc, this.nguoiChoiX, this.nguoiChoiY, duongX.length,
                this.duKienTrungPhienQuan ? ("Phiến quân #" + (this.phienQuanDangBiBan + 1)) : "Trượt/Map"));

        this.nguoiChoi.dichVu.guiKetQuaBanLuyenTap((byte)0, this.layLoaiDanAnToan(loaiDan),
                this.nguoiChoiX, this.nguoiChoiY, goc, luc, duongX, duongY, soPhat);
        this.lapDuPhongPhatBan(this.duKienTrungPhienQuan, loaiDan == 80 ? 3000L : 10000L);
    }

    public synchronized void xuLyFocusSkill(VXLTinNhan tinNhan) throws IOException {
        byte hanhDong = tinNhan.boDoc().readByte();
        int chiSoMucTieu = Byte.toUnsignedInt(tinNhan.boDoc().readByte());
        int chiSoPhienQuan = chiSoMucTieu - 1;
        if (hanhDong != 1 || !this.nguoiChoi.inTraining || !this.choKyNangDacBiet
                || this.phienQuanDangHoatDong || this.dangChoKetThucPhatBan
                || chiSoPhienQuan < 0 || chiSoPhienQuan >= SO_PHIEN_QUAN
                || this.phienQuanDaChet[chiSoPhienQuan]) {
            return;
        }
        this.choKyNangDacBiet = false;
        this.phienQuanDangHoatDong = true;
        short mucTieuX = this.phienQuanX[chiSoPhienQuan];
        short mucTieuY = this.phienQuanY[chiSoPhienQuan];
        short[] xs = new short[]{(short)(mucTieuX - 20), (short)(mucTieuX - 5),
                (short)(mucTieuX + 5), (short)(mucTieuX + 20)};
        short[] ys = new short[]{mucTieuY, mucTieuY, mucTieuY, mucTieuY};
        this.nguoiChoi.dichVu.guiSkillHawkeye((byte)0, (byte)9, xs, ys);

        int satThuongMoiMui = VXLTinhSatThuong.tinhSauGiap(20 + this.chiSoNguoiChoi.tanCong,
                this.giapPhienQuan);
        satThuongMoiMui = Math.max(8, satThuongMoiMui);
        int satThuong = satThuongMoiMui * 4;
        if (this.khienPhienQuan > 0) {
            int hapThu = Math.min(this.khienPhienQuan, satThuong);
            this.khienPhienQuan -= hapThu;
            satThuong -= hapThu;
        }
        this.mauPhienQuan[chiSoPhienQuan] = Math.max(0,
                this.mauPhienQuan[chiSoPhienQuan] - satThuong);
        this.phienQuanDaChet[chiSoPhienQuan] = this.mauPhienQuan[chiSoPhienQuan] <= 0;
        this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)(chiSoPhienQuan + 1),
                this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan,
                this.phienQuanDaChet[chiSoPhienQuan] ? (byte)2 : (byte)0);
        this.phienQuanDangHoatDong = false;
        if (this.daHaPhienQuan()) {
            this.hoanThanhPhienQuan();
        } else {
            this.batDauLuotPhienQuan();
        }
    }

    public void xuLyVaCham(VXLTinNhan tinNhan) throws IOException {
        int soVuNo = tinNhan.boDoc().readUnsignedByte();
        if (soVuNo > 32) {
            throw new IllegalArgumentException("Số vụ nổ không hợp lệ: " + soVuNo);
        }
        for (int i = 0; i < soVuNo; i++) {
            tinNhan.boDoc().readInt();
            tinNhan.boDoc().readInt();
        }
        if (!this.nguoiChoi.inTraining || !this.dangChoKetThucPhatBan) {
            return;
        }
        this.ketThucPhatBanNguoiChoi(this.duKienTrungPhienQuan);
    }

    public void yeuCauDatLaiHo() throws IOException {
        if (this.nguoiChoi.inTraining) {
            this.nguoiChoi.dichVu.guiDatLaiHoLuyenTap();
        }
    }

    public void sanSang() throws IOException {
        this.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
        if (this.nguoiChoi.inTraining && !this.daGuiLuotDau) {
            this.daGuiLuotDau = true;
            this.nguoiChoi.dichVu.guiLuotLuyenTapTiep((byte)0, this.nguoiChoiX, this.nguoiChoiY);
        }
    }

    public synchronized void dong() {
        this.resetTrangThai();
        this.dungTacVu();
    }

    private synchronized void lapDuPhongPhatBan(boolean trung, long tre) {
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
        }
        this.tacVuDuPhongPhatBan = BO_LAP_LICH.schedule(() -> {
            try {
                this.ketThucPhatBanNguoiChoi(trung);
            }
            catch (Exception ex) {
                Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi xử lý dự phòng phát bắn luyện tập.", ex);
            }
        }, tre, TimeUnit.MILLISECONDS);
    }

    private synchronized void ketThucPhatBanNguoiChoi(boolean trung) throws IOException {
        if (!this.nguoiChoi.inTraining || !this.dangChoKetThucPhatBan) {
            return;
        }
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
            this.tacVuDuPhongPhatBan = null;
        }
        if (trung) {
            int satThuongGoc = VXLTinhSatThuong.tinhPhatBan(this.chiSoNguoiChoi.tanCong,
                    this.lucPhatBanNguoiChoi, VXLCauHinhPhienQuan.HE_SO_DAN_THUONG);
            int satThuong = VXLTinhSatThuong.tinhSauGiap(satThuongGoc, this.giapPhienQuan);
            if (this.khienPhienQuan > 0) {
                int hapThu = Math.min(this.khienPhienQuan, satThuong);
                this.khienPhienQuan -= hapThu;
                satThuong -= hapThu;
            }
            int chiSoPhienQuan = this.phienQuanDangBiBan >= 0
                    ? this.phienQuanDangBiBan
                    : this.tinhDuongDan.timBotSongGanNhat(this.nguoiChoiX, this.nguoiChoiY);
            if (chiSoPhienQuan >= 0) {
                int mauTruoc = this.mauPhienQuan[chiSoPhienQuan];
                this.mauPhienQuan[chiSoPhienQuan] = Math.max(0,
                        this.mauPhienQuan[chiSoPhienQuan] - satThuong);
                if (this.mauPhienQuan[chiSoPhienQuan] <= 0) {
                    this.mauPhienQuan[chiSoPhienQuan] = 0;
                    this.phienQuanDaChet[chiSoPhienQuan] = true;
                }

                System.out.println(String.format("[TRAINING-HIT] %s bắn trúng %s | Sát thương gốc=%d | Giáp=%d | Sát thương thực=%d | HP: %d -> %d/%d",
                        this.nguoiChoi.ten, VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai),
                        satThuongGoc, this.giapPhienQuan, satThuong, mauTruoc,
                        this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan));

                this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)(chiSoPhienQuan + 1),
                        this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan,
                        this.phienQuanDaChet[chiSoPhienQuan] ? (byte)2 : (byte)0);
            }
        }
        this.dangChoKetThucPhatBan = false;
        this.duKienTrungPhienQuan = false;
        this.phienQuanDangBiBan = -1;
        if (this.daHaPhienQuan()) {
            this.hoanThanhPhienQuan();
            return;
        }
        this.batDauLuotPhienQuan();
    }

    private void batDauLuotPhienQuan() throws IOException {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        int chiSoPhienQuan = this.tinhDuongDan.timBotSongGanNhat(this.nguoiChoiX, this.nguoiChoiY);
        if (chiSoPhienQuan < 0) {
            this.hoanThanhPhienQuan();
            return;
        }
        this.nguoiChoi.dichVu.guiLuotLuyenTapTiep((byte)(chiSoPhienQuan + 1),
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]);
        this.lapLichPhienQuanBan(this.tinhTreNgamPhienQuan());
    }

    private long tinhTreNgamPhienQuan() {
        return ThreadLocalRandom.current().nextLong(TRE_NGAM_PHIEN_QUAN_TOI_THIEU,
                TRE_NGAM_PHIEN_QUAN_TOI_DA + 1L);
    }

    private synchronized void lapLichPhienQuanBan(long tre) {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        this.huyTacVuPhienQuan();
        this.tacVuPhienQuan = BO_LAP_LICH.schedule(this::choPhienQuanBan, tre, TimeUnit.MILLISECONDS);
    }

    private synchronized void huyTacVuPhienQuan() {
        if (this.tacVuPhienQuan != null) {
            this.tacVuPhienQuan.cancel(false);
            this.tacVuPhienQuan = null;
        }
    }

    private synchronized void dungTacVu() {
        this.huyTacVuPhienQuan();
        if (this.tacVuKetThucPhienQuan != null) {
            this.tacVuKetThucPhienQuan.cancel(false);
            this.tacVuKetThucPhienQuan = null;
        }
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
            this.tacVuDuPhongPhatBan = null;
        }
        if (this.tacVuKetQuaTran != null) {
            this.tacVuKetQuaTran.cancel(false);
            this.tacVuKetQuaTran = null;
        }
        this.phienQuanDangHoatDong = false;
    }

    private void choPhienQuanBan() {
        try {
            synchronized (this) {
                this.tacVuPhienQuan = null;
                if (!this.nguoiChoi.inTraining || this.dangChoKetThucPhatBan || this.phienQuanDangHoatDong) {
                    return;
                }
                this.phienQuanDangHoatDong = true;
                long treKetThucLuot = this.phienQuanBanTra();
                if (!this.nguoiChoi.inTraining) {
                    this.phienQuanDangHoatDong = false;
                    return;
                }
                if (this.tacVuKetThucPhienQuan != null) {
                    this.tacVuKetThucPhienQuan.cancel(false);
                }
                this.tacVuKetThucPhienQuan = BO_LAP_LICH.schedule(this::ketThucLuotPhienQuan,
                        treKetThucLuot, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi Phiến quân luyện tập bắn trả.", ex);
        }
    }

    private void ketThucLuotPhienQuan() {
        try {
            synchronized (this) {
                this.tacVuKetThucPhienQuan = null;
                if (!this.nguoiChoi.inTraining) {
                    this.phienQuanDangHoatDong = false;
                    return;
                }
                this.phienQuanDangHoatDong = false;
                this.nguoiChoi.dichVu.guiLuotLuyenTapTiep((byte)0, this.nguoiChoiX, this.nguoiChoiY);
                if (this.nguoiChoi.avenger == 7 && this.soLuotPhienQuan > 0
                        && this.soLuotPhienQuan % 4 == 0) {
                    this.choKyNangDacBiet = true;
                    this.nguoiChoi.dichVu.guiYeuCauSkill((byte)1);
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi kết thúc lượt Phiến quân luyện tập.", ex);
        }
    }

    private long phienQuanBanTra() throws IOException {
        int chiSoPhienQuan = this.layPhienQuanTiepTheo();
        if (chiSoPhienQuan < 0) {
            this.hoanThanhPhienQuan();
            return TRE_KET_THUC_DAN_BOT_TOI_THIEU;
        }
        this.phienQuanDungVatPhamNeuCan(chiSoPhienQuan);
        byte loaiDan = VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(VU_KHI_PHIEN_QUAN, (byte)0);
        boolean duKienNgamTrung = this.phienQuanDuKienNgamTrung();
        short[] diemNgam = this.taoDiemNgamPhienQuan(chiSoPhienQuan, duKienNgamTrung);
        int lucCoBan = Byte.toUnsignedInt(this.tinhDuongDan.lucCanThietToiMucTieu(
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan],
                diemNgam[0], diemNgam[1]));
        byte luc = (byte)Math.min(30, Math.max(20,
                lucCoBan + ThreadLocalRandom.current().nextInt(3, 8)));
        short goc = this.tinhDuongDan.gocDanDaoCaoToiMucTieu(this.phienQuanX[chiSoPhienQuan],
                this.phienQuanY[chiSoPhienQuan], diemNgam[0], diemNgam[1], luc);
        short[][] duongDan = this.tinhDuongDan.taoDuongDanCong(this.phienQuanX[chiSoPhienQuan],
                this.phienQuanY[chiSoPhienQuan], goc, luc, loaiDan, chiSoPhienQuan);
        boolean trungNguoiChoi = duKienNgamTrung && this.tinhDuongDan.duongDanTrungNguoiChoi(
                duongDan[0], duongDan[1], this.nguoiChoiX, this.nguoiChoiY);
        if (trungNguoiChoi) {
            duongDan = this.tinhDuongDan.dungDuongDanTaiNguoiChoi(
                    duongDan[0], duongDan[1], this.nguoiChoiX, this.nguoiChoiY);
        }

        System.out.println(String.format("[TRAINING-BOT-FIRE] Phiến quân %s bắn trả | Goc=%d | Luc=%d | Pos=(%d,%d)",
                VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), goc, luc,
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]));

        this.nguoiChoi.dichVu.guiKetQuaBanLuyenTap((byte)(chiSoPhienQuan + 1), loaiDan,
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan], goc, luc,
                duongDan[0], duongDan[1], (byte)1);
        long treKetThucDan = this.tinhTreKetThucDanBot(duongDan[0].length);
        if (trungNguoiChoi) {
            int heSoDan = this.danManhPhienQuan
                    ? VXLCauHinhPhienQuan.HE_SO_DAN_MANH
                    : VXLCauHinhPhienQuan.HE_SO_DAN_THUONG;
            int satThuongGoc = VXLTinhSatThuong.tinhPhatBan(this.tanCongPhienQuan, luc, heSoDan);
            int satThuong = VXLTinhSatThuong.tinhSauGiap(satThuongGoc, this.chiSoNguoiChoi.giap);
            this.danManhPhienQuan = false;
            int mauTruoc = this.mauNguoiChoi;
            this.mauNguoiChoi = Math.max(0, this.mauNguoiChoi - satThuong);
            boolean nguoiChoiGucNga = this.mauNguoiChoi <= 0;
            if (nguoiChoiGucNga) {
                this.mauNguoiChoi = 0;
            }

            System.out.println(String.format("[TRAINING-BOT-HIT] Phiến quân %s bắn trúng %s | Sát thương gốc=%d | Giáp người chơi=%d | Sát thương thực=%d | HP người chơi: %d -> %d/%d",
                    VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), this.nguoiChoi.ten,
                    satThuongGoc, this.chiSoNguoiChoi.giap, satThuong, mauTruoc,
                    this.mauNguoiChoi, this.chiSoNguoiChoi.mauToiDa));

            this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0, this.mauNguoiChoi,
                    this.chiSoNguoiChoi.mauToiDa, nguoiChoiGucNga ? (byte)2 : (byte)0);
            if (nguoiChoiGucNga) {
                this.thuaLuyenTap(treKetThucDan + TRE_HIEN_KET_QUA_SAU_HIEU_UNG);
            }
        }
        return treKetThucDan;
    }

    private long tinhTreKetThucDanBot(int soDiem) {
        long treVaCham = TRE_CHUAN_BI_DAN_BOT
                + Math.max(1, soDiem) * THOI_GIAN_MOI_DIEM_DAN_BOT;
        long tre = treVaCham + TRE_SAU_VA_CHAM_DAN_BOT;
        return Math.max(TRE_KET_THUC_DAN_BOT_TOI_THIEU,
                Math.min(TRE_KET_THUC_DAN_BOT_TOI_DA, tre));
    }

    private boolean phienQuanDuKienNgamTrung() {
        int tiLeTrung = Math.min(TI_LE_TRUNG_PHIEN_QUAN_TOI_DA,
                TI_LE_TRUNG_PHIEN_QUAN_TOI_THIEU + this.capPhienQuanHienTai / 3);
        return ThreadLocalRandom.current().nextInt(100) < tiLeTrung;
    }

    private short[] taoDiemNgamPhienQuan(int chiSoPhienQuan, boolean duKienNgamTrung) {
        ThreadLocalRandom ngauNhien = ThreadLocalRandom.current();
        int dichX = this.nguoiChoiX;
        int dichY = this.nguoiChoiY - ngauNhien.nextInt(12, 29);
        if (duKienNgamTrung) {
            dichX += ngauNhien.nextInt(-6, 7);
        } else {
            int huongVePhienQuan = Integer.compare(this.phienQuanX[chiSoPhienQuan], this.nguoiChoiX);
            if (huongVePhienQuan == 0) {
                huongVePhienQuan = ngauNhien.nextBoolean() ? 1 : -1;
            }
            int khoangTruot = ngauNhien.nextInt(KHOANG_TRUOT_DAT_TOI_THIEU,
                    KHOANG_TRUOT_DAT_TOI_DA + 1);
            dichX += huongVePhienQuan * khoangTruot;
        }
        int chieuRongBanDo = this.tinhDuongDan.layBanDo().getWidth();
        int chieuCaoBanDo = this.tinhDuongDan.layBanDo().getHeight();
        short xDaGioiHan = (short)Math.max(0, Math.min(chieuRongBanDo - 1, dichX));
        if (!duKienNgamTrung) {
            dichY = this.tinhDuongDan.layBanDo().timViTriDat(xDaGioiHan, this.nguoiChoiY);
        }
        return new short[]{xDaGioiHan, (short)Math.max(0, Math.min(chieuCaoBanDo - 1, dichY))};
    }

    private void thuaLuyenTap(long tre) {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        this.lapLichKetQuaTran((byte)0, 0, 0, 0, tre);
    }

    private int layPhienQuanTiepTheo() {
        for (int buoc = 1; buoc <= SO_PHIEN_QUAN; buoc++) {
            int chiSo = (this.luotPhienQuan + buoc + SO_PHIEN_QUAN) % SO_PHIEN_QUAN;
            if (!this.phienQuanDaChet[chiSo]) {
                this.luotPhienQuan = chiSo;
                return chiSo;
            }
        }
        return -1;
    }

    private boolean daHaPhienQuan() {
        for (boolean daChet : this.phienQuanDaChet) {
            if (!daChet) {
                return false;
            }
        }
        return true;
    }

    private void hoanThanhPhienQuan() throws IOException {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        int capDaHa = this.capPhienQuanHienTai;
        this.soPhienQuanDaHa = Math.max(this.soPhienQuanDaHa, capDaHa);
        this.nguoiChoi.trainingSuccess = (byte)Math.min(VXLCauHinhPhienQuan.CAP_TOI_DA, this.soPhienQuanDaHa + 1);
        this.nguoiChoi.ghiNhanHaBoss(1);

        int kinhNghiem = 100 + capDaHa * 15;
        int vang = 500 + capDaHa * 50;
        this.nguoiChoi.congKinhNghiem(kinhNghiem);
        this.nguoiChoi.vang += vang;

        this.nguoiChoi.dichVu.capNhat();
        this.nguoiChoi.flushCache();

        this.lapLichKetQuaTran((byte)1, kinhNghiem, vang, 1, TRE_HIEN_KET_QUA_SAU_HIEU_UNG);
    }

    private synchronized void lapLichKetQuaTran(byte pheThang, int kinhNghiem, int vang, int ngoc,
            long tre) {
        this.resetTrangThai();
        this.dungTacVu();
        this.tacVuKetQuaTran = BO_LAP_LICH.schedule(() -> {
            synchronized (this) {
                this.tacVuKetQuaTran = null;
                try {
                    this.nguoiChoi.dichVu.guiKetThucDau(pheThang, kinhNghiem, vang, ngoc);
                }
                catch (Exception ex) {
                    Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                            "Lá»—i gá»­i káº¿t quáº£ luyá»‡n táº­p.", ex);
                }
            }
        }, Math.max(TRE_HIEN_KET_QUA_SAU_HIEU_UNG, tre), TimeUnit.MILLISECONDS);
    }

    private void diChuyenPhienQuan(int chiSoPhienQuan) throws IOException {
        int dichChuyen = chiSoPhienQuan % 2 == 0 ? 22 : -22;
        this.phienQuanX[chiSoPhienQuan] = this.tinhDuongDan.gioiHan(
                (short)(this.phienQuanX[chiSoPhienQuan] + dichChuyen), 80, 1120);
        this.phienQuanY[chiSoPhienQuan] = this.tinhDuongDan.layBanDo().timViTriDat(
                this.phienQuanX[chiSoPhienQuan], (short)250);
        this.nguoiChoi.dichVu.guiCapNhatXYLuyenTap((byte)(chiSoPhienQuan + 1),
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]);
    }

    private void phienQuanDungVatPhamNeuCan(int chiSoPhienQuan) throws IOException {
        this.soLuotPhienQuan++;
        int nguongHoiMau = this.mauToiDaPhienQuan * 60 / 100;
        if (this.mauPhienQuan[chiSoPhienQuan] <= nguongHoiMau && this.soLuotPhienQuan % 2 == 1) {
            this.mauPhienQuan[chiSoPhienQuan] = Math.min(this.mauToiDaPhienQuan,
                    this.mauPhienQuan[chiSoPhienQuan] + VXLCauHinhPhienQuan.tinhLuongHoiMau(this.chiSoPhienQuan));
            this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)(chiSoPhienQuan + 1), (byte)10, (short)0);
            this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)(chiSoPhienQuan + 1),
                    this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan, (byte)0);
            return;
        }
        if (this.soLuotPhienQuan % 3 == 0) {
            this.khienPhienQuan = Math.min(this.mauToiDaPhienQuan / 2,
                    this.khienPhienQuan + VXLCauHinhPhienQuan.tinhLuongKhien(this.chiSoPhienQuan));
            this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)(chiSoPhienQuan + 1), (byte)0, (short)0);
            return;
        }
        if (this.soLuotPhienQuan % 2 == 0) {
            this.danManhPhienQuan = true;
            this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)(chiSoPhienQuan + 1), (byte)5, (short)0);
        }
    }

    private boolean laDanDoi(byte loaiDan) {
        return loaiDan == 17 || loaiDan == 19;
    }

    private byte layLoaiDanAnToan(byte loaiDan) {
        switch (loaiDan) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 13:
            case 21:
            case 22:
            case 25:
            case 30:
            case 34:
            case 35:
            case 42:
            case 45:
            case 50:
            case 51:
            case 52:
            case 54:
            case 55:
            case 57:
            case 58:
            case 79:
            case 80:
            case 82:
            case 83:
                return loaiDan;
            default:
                return 0;
        }
    }

    private void resetTrangThai() {
        this.nguoiChoi.inTraining = false;
        this.nguoiChoi.isReady = false;
        this.nguoiChoi.chiSo = -1;
        this.nguoiChoi.pointSeat = 0;
        this.dangChoKetThucPhatBan = false;
        this.duKienTrungPhienQuan = false;
        this.phienQuanDangBiBan = -1;
        this.luotPhienQuan = -1;
        this.phienQuanDangHoatDong = false;
        this.choKyNangDacBiet = false;
    }
}
