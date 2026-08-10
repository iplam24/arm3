package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class VXLDieuKhienBotTranDau {
    private static final int TAM_NO_CAM_TU = 145;
    private static final ScheduledExecutorService BO_LAP_LICH = Executors.newSingleThreadScheduledExecutor(tacVu -> {
        Thread thread = new Thread(tacVu, "vxl-bot-tran-dau");
        thread.setDaemon(true);
        return thread;
    });
    private final VXLQuanLyChien tranDau;
    private final VXLChienBinh[] chienBinhs;
    private final VXLQuanLyBanDo banDo;
    private final VXLTinhDuongDan tinhDuongDan;
    private ScheduledFuture<?> tacVuBot;

    VXLDieuKhienBotTranDau(VXLQuanLyChien tranDau, VXLChienBinh[] chienBinhs, VXLQuanLyBanDo banDo, VXLTinhDuongDan tinhDuongDan) {
        this.tranDau = tranDau;
        this.chienBinhs = chienBinhs;
        this.banDo = banDo;
        this.tinhDuongDan = tinhDuongDan;
    }

    synchronized void batDau() {
        this.dung();
        this.tacVuBot = BO_LAP_LICH.scheduleWithFixedDelay(() -> {
            try {
                this.nhip();
            }
            catch (Exception ex) {
                Logger.getLogger(VXLDieuKhienBotTranDau.class.getName()).log(Level.WARNING, "Lỗi vòng lặp chiến đấu của bot.", ex);
            }
        }, 700L, 700L, TimeUnit.MILLISECONDS);
    }

    synchronized void dung() {
        if (this.tacVuBot != null) {
            this.tacVuBot.cancel(false);
            this.tacVuBot = null;
        }
    }

    void thucHienBatDongBo(Runnable tacVu) {
        BO_LAP_LICH.execute(tacVu);
    }

    private void nhip() throws IOException {
        synchronized (this.tranDau) {
            if (this.tranDau.daKetThuc()) {
                this.dung();
                return;
            }
            byte chiSoLuot = this.tranDau.layLuotHienTai();
            VXLChienBinh luot = chiSoLuot >= 0 && chiSoLuot < this.chienBinhs.length ? this.chienBinhs[chiSoLuot] : null;
            if (luot == null || luot.chet || luot.daRoiTran) {
                this.tranDau.sangLuot();
                return;
            }
            if (luot.bot && System.currentTimeMillis() + 900L >= this.tranDau.layHanLuot()) {
                if (luot.camTu) {
                    this.xuLyLuotCamTu(luot);
                } else {
                    this.xuLyLuotBotBan(luot);
                }
            } else if (!luot.bot && System.currentTimeMillis() > this.tranDau.layHanLuot()) {
                this.tranDau.sangLuot();
            }
        }
    }

    private void xuLyLuotBotBan(VXLChienBinh bot) throws IOException {
        this.diChuyenTruocKhiBan(bot);
        VXLChienBinh mucTieu = this.timMucTieuGanNhat(bot);
        if (mucTieu == null) {
            System.out.println("[BOT] " + bot.ten + " không tìm thấy mục tiêu, bỏ lượt.");
        }
        if (mucTieu != null) {
            byte luc = this.tinhDuongDan.lucCanThietToiMucTieu(bot, mucTieu);
            short goc = this.tinhDuongDan.gocDanDaoToiMucTieu(bot, mucTieu, luc);
            VXLKetQuaDan ketQua = this.tranDau.xuLyPhatBan(bot, (byte)0, goc, luc, -1);
            this.tranDau.phatBan(bot, ketQua, (byte)1);
            if (ketQua.mucTieu != null && ketQua.satThuong > 0) {
                this.tranDau.satThuong(bot, ketQua.mucTieu, ketQua.satThuong, false, false, false);
            }
        }
        if (!this.tranDau.kiemTraKetThuc()) {
            this.tranDau.sangLuot();
        }
    }

    private void xuLyLuotCamTu(VXLChienBinh camTu) throws IOException {
        VXLChienBinh mucTieu = this.timNguoiChoiGanNhat(camTu);
        if (mucTieu == null) {
            this.tranDau.kiemTraKetThuc();
            return;
        }
        int dx = mucTieu.x - camTu.x;
        int dy = mucTieu.y - camTu.y;
        double khoangCach = Math.max(1D, Math.hypot(dx, dy));
        if (khoangCach > 85D) {
            double tiLe = Math.min(95D, khoangCach) / khoangCach;
            camTu.x = this.gioiHan((short)Math.round(camTu.x + dx * tiLe), 0, this.banDo.getWidth());
            camTu.y = this.gioiHan((short)Math.round(camTu.y + dy * tiLe), 0, this.banDo.getHeight());
            this.tranDau.phatDiChuyen(camTu);
        }
        dx = mucTieu.x - camTu.x;
        dy = mucTieu.y - camTu.y;
        if (dx * dx + dy * dy <= 95 * 95) {
            camTu.chet = true;
            camTu.hp = 0;
            this.tranDau.phatCapNhatMau(camTu);
            for (VXLChienBinh chienBinh : this.chienBinhs) {
                if (chienBinh == null || chienBinh.bot || chienBinh.chet) {
                    continue;
                }
                int noX = chienBinh.x - camTu.x;
                int noY = chienBinh.y - camTu.y;
                if (noX * noX + noY * noY <= TAM_NO_CAM_TU * TAM_NO_CAM_TU) {
                    int satThuong = Math.max(camTu.tanCong, chienBinh.mauToiDa * 28 / 100);
                    this.tranDau.satThuong(camTu, chienBinh, satThuong, false, true, false);
                }
            }
        }
        if (!this.tranDau.kiemTraKetThuc()) {
            this.tranDau.sangLuot();
        }
    }

    private void diChuyenTruocKhiBan(VXLChienBinh bot) {
        int dichChuyen = bot.chiSo % 2 == 0 ? 28 : -28;
        short mucTieuX = this.gioiHan((short)(bot.x + dichChuyen), 40, this.banDo.getWidth() - 40);
        short[] toaDo = this.tinhDuongDan.gioiHanDiChuyen(bot.x, bot.y, mucTieuX, bot.y, 40);
        bot.x = toaDo[0];
        bot.y = this.banDo.timViTriDat(bot.x, toaDo[1]);
        this.tranDau.phatDiChuyen(bot);
    }

    private VXLChienBinh timMucTieuGanNhat(VXLChienBinh nguoiBan) {
        VXLChienBinh ganNhat = null;
        int khoangCachGanNhat = Integer.MAX_VALUE;
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu == nguoiBan || mucTieu.chet || (nguoiBan.bot && mucTieu.bot)) {
                continue;
            }
            int dx = mucTieu.x - nguoiBan.x;
            int dy = mucTieu.y - nguoiBan.y;
            int khoangCach = dx * dx + dy * dy;
            if (khoangCach < khoangCachGanNhat) {
                khoangCachGanNhat = khoangCach;
                ganNhat = mucTieu;
            }
        }
        return ganNhat;
    }

    private VXLChienBinh timNguoiChoiGanNhat(VXLChienBinh nguon) {
        VXLChienBinh ganNhat = null;
        int khoangCachGanNhat = Integer.MAX_VALUE;
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu.bot || mucTieu.chet) {
                continue;
            }
            int dx = mucTieu.x - nguon.x;
            int dy = mucTieu.y - nguon.y;
            int khoangCach = dx * dx + dy * dy;
            if (khoangCach < khoangCachGanNhat) {
                khoangCachGanNhat = khoangCach;
                ganNhat = mucTieu;
            }
        }
        return ganNhat;
    }

    private short gioiHan(short giaTri, int nhoNhat, int lonNhat) {
        int ketQua = giaTri;
        if (ketQua < nhoNhat) {
            ketQua = nhoNhat;
        }
        if (ketQua > lonNhat) {
            ketQua = lonNhat;
        }
        return (short)ketQua;
    }
}