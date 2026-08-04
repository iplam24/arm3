package com.vxl.luyentap;

import com.alibaba.fastjson2.JSONObject;
import com.vxl.chien.VXLChienBinh;
import com.vxl.chien.VXLTinhSatThuong;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VXLQuanLyLuyenTap {
    private static final int SO_PHIEN_QUAN = 1;
    private static final short VI_TRI_PHIEN_QUAN_X = 760;
    private static final short VI_TRI_PHIEN_QUAN_Y = 300;
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
    private boolean danManhPhienQuan;
    private byte lucPhatBanNguoiChoi = 10;
    private int soLuotPhienQuan;
    private ScheduledFuture<?> tacVuPhienQuan;
    private ScheduledFuture<?> tacVuKetThucPhienQuan;
    private ScheduledFuture<?> tacVuDuPhongPhatBan;

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
            this.chiSoNguoiChoi = new VXLChienBinh(this.nguoiChoi, (byte)0, (short)220, (short)300);
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
            this.khienPhienQuan = 0;
            this.danManhPhienQuan = false;
            this.lucPhatBanNguoiChoi = 10;
            this.soLuotPhienQuan = 0;
            this.nguoiChoiX = 220;
            this.nguoiChoiY = 300;
            this.phienQuanDangBiBan = -1;
            this.luotPhienQuan = -1;
            this.mauPhienQuan[0] = this.mauToiDaPhienQuan;
            this.phienQuanX[0] = VI_TRI_PHIEN_QUAN_X;
            this.phienQuanY[0] = VI_TRI_PHIEN_QUAN_Y;
            this.phienQuanDaChet[0] = false;

            byte maBanDo = 1;
            short vuKhiPhienQuan = this.chiSoPhienQuan.vuKhi;
            this.nguoiChoi.dichVu.guiThongTinLuyenTap();
            this.nguoiChoi.dichVu.guiChonBanDoLuyenTap(maBanDo);
            this.nguoiChoi.dichVu.guiNguoiChoiLuyenTap((byte)0, this.nguoiChoi.ma, this.nguoiChoi.ten,
                    this.nguoiChoi.head, this.nguoiChoi.leg, this.nguoiChoi.body, this.nguoiChoi.hat,
                    this.nguoiChoi.wing, vuKhiNguoiChoi, this.nguoiChoi.avenger, this.nguoiChoi.ma);
            this.nguoiChoi.dichVu.guiNguoiChoiLuyenTap((byte)1, -12000 - this.capPhienQuanHienTai,
                    VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), this.chiSoPhienQuan.head,
                    this.chiSoPhienQuan.leg, this.chiSoPhienQuan.body, this.chiSoPhienQuan.hat,
                    this.chiSoPhienQuan.wing, vuKhiPhienQuan, (byte)0, this.nguoiChoi.ma);
            this.nguoiChoi.dichVu.guiBatDauLuyenTap(maBanDo, vuKhiNguoiChoi,
                    this.mauNguoiChoi, this.chiSoNguoiChoi.mauToiDa, this.phienQuanX,
                    this.phienQuanY, this.mauPhienQuan, new short[]{vuKhiPhienQuan});
            this.nguoiChoi.startOKDlg2(VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai)
                    + " | HP " + this.mauToiDaPhienQuan
                    + " | Công " + this.tanCongPhienQuan
                    + " | Giáp " + this.giapPhienQuan
                    + " | Súng " + this.chiSoPhienQuan.tenVuKhi);
        }
        catch (Exception ex) {
            this.nguoiChoi.inTraining = false;
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
        byte loaiDan = tinNhan.boDoc().readByte();
        short x = tinNhan.boDoc().readShort();
        short y = tinNhan.boDoc().readShort();
        short goc = tinNhan.boDoc().readShort();
        byte luc = tinNhan.boDoc().readByte();
        if (this.laDanDoi(loaiDan)) {
            tinNhan.boDoc().readByte();
        }
        tinNhan.boDoc().readByte();
        luc = (byte)Math.max(10, Math.min(30, luc));
        this.lucPhatBanNguoiChoi = luc;
        this.nguoiChoiX = this.tinhDuongDan.gioiHan(x, 0, 1200);
        this.nguoiChoiY = this.tinhDuongDan.gioiHan(y, 0, 700);
        short[][] duongDan = this.tinhDuongDan.taoDuongDanCong(this.nguoiChoiX, this.nguoiChoiY, goc, luc);
        short[] duongX = duongDan[0];
        short[] duongY = duongDan[1];
        this.phienQuanDangBiBan = this.tinhDuongDan.timBotTrungDuong(duongX, duongY);
        this.duKienTrungPhienQuan = this.phienQuanDangBiBan >= 0;
        this.dangChoKetThucPhatBan = true;
        this.nguoiChoi.dichVu.guiKetQuaBanLuyenTap((byte)0, this.layLoaiDanAnToan(loaiDan),
                this.nguoiChoiX, this.nguoiChoiY, goc, luc, duongX, duongY);
        this.lapDuPhongPhatBan(this.duKienTrungPhienQuan, 1500L);
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
        this.nguoiChoi.inTraining = false;
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
                this.mauPhienQuan[chiSoPhienQuan] = Math.max(0,
                        this.mauPhienQuan[chiSoPhienQuan] - satThuong);
                if (this.mauPhienQuan[chiSoPhienQuan] <= 0) {
                    this.mauPhienQuan[chiSoPhienQuan] = 0;
                    this.phienQuanDaChet[chiSoPhienQuan] = true;
                }
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
        this.lapLichPhienQuanBan(700L);
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
                this.phienQuanBanTra();
                if (!this.nguoiChoi.inTraining) {
                    this.phienQuanDangHoatDong = false;
                    return;
                }
                if (this.tacVuKetThucPhienQuan != null) {
                    this.tacVuKetThucPhienQuan.cancel(false);
                }
                this.tacVuKetThucPhienQuan = BO_LAP_LICH.schedule(this::ketThucLuotPhienQuan, 1100L, TimeUnit.MILLISECONDS);
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
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi kết thúc lượt Phiến quân luyện tập.", ex);
        }
    }

    private void phienQuanBanTra() throws IOException {
        int chiSoPhienQuan = this.layPhienQuanTiepTheo();
        if (chiSoPhienQuan < 0) {
            this.hoanThanhPhienQuan();
            return;
        }
        this.diChuyenPhienQuan(chiSoPhienQuan);
        this.phienQuanDungVatPhamNeuCan(chiSoPhienQuan);
        byte loaiDan = 0;
        byte luc = 18;
        short goc = this.tinhDuongDan.tinhGocToiMucTieu(this.phienQuanX[chiSoPhienQuan],
                this.phienQuanY[chiSoPhienQuan], this.nguoiChoiX, this.nguoiChoiY);
        short[][] duongDan = this.tinhDuongDan.taoDuongDanThang(this.phienQuanX[chiSoPhienQuan],
                this.phienQuanY[chiSoPhienQuan], this.nguoiChoiX, this.nguoiChoiY);
        this.nguoiChoi.dichVu.guiKetQuaBanLuyenTap((byte)(chiSoPhienQuan + 1), loaiDan,
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan], goc, luc,
                duongDan[0], duongDan[1]);
        if (this.tinhDuongDan.duongDanTrungNguoiChoi(duongDan[0], duongDan[1], this.nguoiChoiX, this.nguoiChoiY)) {
            int heSoDan = this.danManhPhienQuan
                    ? VXLCauHinhPhienQuan.HE_SO_DAN_MANH
                    : VXLCauHinhPhienQuan.HE_SO_DAN_THUONG;
            int satThuongGoc = VXLTinhSatThuong.tinhPhatBan(this.tanCongPhienQuan, luc, heSoDan);
            int satThuong = VXLTinhSatThuong.tinhSauGiap(satThuongGoc, this.chiSoNguoiChoi.giap);
            this.danManhPhienQuan = false;
            this.mauNguoiChoi = Math.max(0, this.mauNguoiChoi - satThuong);
            if (this.mauNguoiChoi <= 0) {
                this.mauNguoiChoi = this.chiSoNguoiChoi.mauToiDa;
            }
            this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0, this.mauNguoiChoi,
                    this.chiSoNguoiChoi.mauToiDa, (byte)0);
        }
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
        this.nguoiChoi.inTraining = false;
        this.dungTacVu();
        this.nguoiChoi.dichVu.capNhat();
        this.nguoiChoi.flushCache();
        if (this.soPhienQuanDaHa >= VXLCauHinhPhienQuan.CAP_TOI_DA) {
            this.nguoiChoi.startOKDlg2("Bạn đã hạ Phiến quân 40 và hoàn thành toàn bộ luyện tập.");
        } else {
            int capTiepTheo = this.soPhienQuanDaHa + 1;
            this.nguoiChoi.startOKDlg2("Bạn đã hạ " + VXLCauHinhPhienQuan.layTen(capDaHa)
                    + ". Lần vào sau sẽ gặp " + VXLCauHinhPhienQuan.layTen(capTiepTheo)
                    + " với " + VXLCauHinhPhienQuan.taoChiSo(capTiepTheo).mauToiDa + " HP.");
        }
    }

    private void diChuyenPhienQuan(int chiSoPhienQuan) throws IOException {
        int dichChuyen = this.soLuotPhienQuan % 2 == 0 ? 22 : -22;
        this.phienQuanX[chiSoPhienQuan] = this.tinhDuongDan.gioiHan(
                (short)(this.phienQuanX[chiSoPhienQuan] + dichChuyen), 80, 1120);
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
            case 7:
            case 8:
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
                return loaiDan;
            default:
                return 0;
        }
    }
}