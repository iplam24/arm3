package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class VXLDieuKhienBotTranDau {
    private static final int TAM_NO_CAM_TU = 72;
    private static final int KHOANG_CACH_NGANG_KICH_NO_CAM_TU = 26;
    private static final int KHOANG_CACH_DOC_KICH_NO_CAM_TU = 34;
    private static final int BUOC_DI_CHUYEN_CAM_TU = 72;
    private static final int BUOC_NHO_DI_CHUYEN_CAM_TU = 6;
    private static final int CHENH_CAO_TOI_DA_MOI_BUOC = 40;
    private static final int NUA_RONG_HITBOX_CAM_TU = 8;
    private static final byte HIEU_UNG_BOM_TU_SAT = 24;
    private static final short ICON_BOM_TU_SAT = 1007;
    private static final long TRE_BOT_TOI_THIEU = 200L;
    private static final long TRE_BOT_TOI_DA = 450L;
    private static final long TRE_SAU_DI_CHUYEN_CAM_TU = 50L;
    private static final long TRE_SAU_NO_CAM_TU = 200L;
    private static final long TRE_SAU_DAN_BAN_SAO_ULTRON = 1000L;
    private static final int TI_LE_PHIEN_QUAN_BAN_GOC_CAO = 70;
    private static final int SO_LUONG_LUONG_BOT = Math.max(2,
            Math.min(4, Runtime.getRuntime().availableProcessors()));
    private static final ScheduledExecutorService BO_LAP_LICH = Executors.newScheduledThreadPool(
            SO_LUONG_LUONG_BOT, tacVu -> {
        Thread thread = new Thread(tacVu, "vxl-bot-tran-dau");
        thread.setDaemon(true);
        return thread;
    });
    private final VXLQuanLyChien tranDau;
    private final VXLChienBinh[] chienBinhs;
    private final VXLQuanLyBanDo banDo;
    private final VXLTinhDuongDan tinhDuongDan;
    private ScheduledFuture<?> tacVuBot;
    private byte luotDangTheoDoi = -1;
    private long hanLuotDangTheoDoi = Long.MIN_VALUE;
    private long thoiDiemBotHanhDong;

    VXLDieuKhienBotTranDau(VXLQuanLyChien tranDau, VXLChienBinh[] chienBinhs, VXLQuanLyBanDo banDo, VXLTinhDuongDan tinhDuongDan) {
        this.tranDau = tranDau;
        this.chienBinhs = chienBinhs;
        this.banDo = banDo;
        this.tinhDuongDan = tinhDuongDan;
    }

    synchronized void batDau() {
        this.dung();
        this.luotDangTheoDoi = -1;
        this.hanLuotDangTheoDoi = Long.MIN_VALUE;
        this.thoiDiemBotHanhDong = 0L;
        this.tacVuBot = BO_LAP_LICH.scheduleWithFixedDelay(() -> {
            try {
                this.nhip();
            }
            catch (Exception ex) {
                Logger.getLogger(VXLDieuKhienBotTranDau.class.getName()).log(Level.WARNING, "Lỗi vòng lặp chiến đấu của bot.", ex);
            }
        }, 150L, 150L, TimeUnit.MILLISECONDS);
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

    void thucHienTriHoan(Runnable tacVu, long doTreMillis) {
        BO_LAP_LICH.schedule(tacVu, Math.max(0L, doTreMillis), TimeUnit.MILLISECONDS);
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
                this.tranDau.sangLuot(chiSoLuot);
                return;
            }
            if (this.tranDau.dangChoKetThucPhatBan(chiSoLuot)) {
                return;
            }
            long bayGio = System.currentTimeMillis();
            long hanLuot = this.tranDau.layHanLuot();
            if (chiSoLuot != this.luotDangTheoDoi || hanLuot != this.hanLuotDangTheoDoi) {
                this.luotDangTheoDoi = chiSoLuot;
                this.hanLuotDangTheoDoi = hanLuot;
                this.thoiDiemBotHanhDong = luot.camTu || this.tranDau.laCheDoCamTu()
                        ? bayGio + 50L
                        : bayGio + ThreadLocalRandom.current().nextLong(
                                TRE_BOT_TOI_THIEU, TRE_BOT_TOI_DA + 1L);
            }
            if (luot.bot && bayGio >= this.thoiDiemBotHanhDong && bayGio <= hanLuot) {
                if (this.tranDau.xuLyLuotBossDacBiet(luot)) {
                    return;
                }
                if (luot.camTu) {
                    this.xuLyLuotCamTu(luot);
                } else {
                    this.xuLyLuotBotBan(luot);
                }
            } else if (bayGio > hanLuot) {
                this.tranDau.sangLuot(chiSoLuot);
            }
        }
    }

    private void xuLyLuotBotBan(VXLChienBinh bot) throws IOException {
        if (!bot.laBanSaoUltron() && !bot.coDinh && !this.tranDau.laCheDoCamTu()) {
            this.diChuyenTruocKhiBan(bot);
        }
        VXLChienBinh mucTieu = this.timMucTieuGanNhat(bot);
        VXLKetQuaDan ketQua = null;
        if (this.tranDau.laCheDoBoss()) {
            System.out.println("[BOT-BOSS] be=" + bot.chiSo + " tgt="
                    + (mucTieu == null ? -1 : mucTieu.chiSo) + " wp=" + bot.maVuKhi
                    + " x=" + bot.x + " y=" + bot.y + " d="
                    + (mucTieu == null ? -1 : (mucTieu.x - bot.x)) + ","
                    + (mucTieu == null ? -1 : (mucTieu.y - bot.y)));
        }
        if (mucTieu == null) {
            System.out.println("[BOT] " + bot.ten + " không tìm thấy mục tiêu, bỏ lượt.");
        }
        if (mucTieu != null) {
            byte loaiDan = bot.avengerDan > 0
                    ? VXLCauHinhVatPhamChienDau.layLoaiDanTheoAvenger(
                            bot.avengerDan, (byte)0)
                    : VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                            bot.maVuKhi, (byte)0);
            byte luc;
            short goc;
            if (this.tranDau.laCheDoCamTu() || this.tranDau.laCheDoBoss()) {
                boolean banGocCao = this.tranDau.laCheDoCamTu()
                        && ThreadLocalRandom.current().nextInt(100)
                        < TI_LE_PHIEN_QUAN_BAN_GOC_CAO;
                VXLTinhDuongDan.CachBanBot cachBan = this.tinhDuongDan.timCachBanBot(
                        bot, mucTieu, loaiDan, this.tranDau.layGioX(),
                        this.tranDau.layGioY(), banGocCao);
                if (cachBan.satThuongDuKien() <= 0) {
                    VXLChienBinh mucTieuKhac = this.timMucTieuGanNhat(bot, mucTieu);
                    if (mucTieuKhac != null) {
                        VXLTinhDuongDan.CachBanBot cachBanKhac =
                                this.tinhDuongDan.timCachBanBot(
                                        bot, mucTieuKhac, loaiDan,
                                        this.tranDau.layGioX(), this.tranDau.layGioY(),
                                        banGocCao);
                        if (cachBanKhac.satThuongDuKien() > cachBan.satThuongDuKien()) {
                            mucTieu = mucTieuKhac;
                            cachBan = cachBanKhac;
                        }
                    }
                }
                luc = cachBan.luc();
                goc = cachBan.goc();
            } else {
                luc = this.tinhDuongDan.lucCanThietToiMucTieu(bot, mucTieu);
                goc = this.tinhDuongDan.gocDanDaoToiMucTieu(bot, mucTieu, luc);
            }
            ketQua = this.tranDau.xuLyPhatBan(bot, loaiDan, goc, luc, -1);
            this.tranDau.ghiNhanDiaHinhPhatBan(ketQua);
            this.tranDau.phatBan(bot, ketQua, (byte)1);
            this.tranDau.apDungSatThuongPhatBan(bot, ketQua, -1);
            this.tranDau.ghiNhanNapDanSauPhatBan(bot);
        }
        if (!this.tranDau.kiemTraKetThuc()) {
            if (ketQua == null) {
                this.tranDau.sangLuot(bot.chiSo);
            } else if (bot.laBanSaoUltron()) {
                this.tranDau.chuyenLuotBotSauHanhDong(bot,
                        TRE_SAU_DAN_BAN_SAO_ULTRON);
            } else if (this.tranDau.laCheDoCamTu() || this.tranDau.laCheDoBoss()) {
                this.tranDau.chuyenLuotBotSauPhatBan(bot, ketQua);
            } else {
                this.tranDau.sangLuot(bot.chiSo);
            }
        }
    }

    private void xuLyLuotCamTu(VXLChienBinh camTu) throws IOException {
        VXLChienBinh mucTieu = this.timNguoiChoiGanNhat(camTu);
        if (mucTieu == null) {
            this.tranDau.kiemTraKetThuc();
            return;
        }
        if (!this.daApSatNguoiChoi(camTu, mucTieu)) {
            this.diChuyenCamTu(camTu, mucTieu);
        }
        boolean daKichNo = this.daApSatNguoiChoi(camTu, mucTieu);
        if (daKichNo) {
            this.tranDau.phatDungVatPham(camTu, HIEU_UNG_BOM_TU_SAT, ICON_BOM_TU_SAT);
            this.tranDau.satThuong(camTu, camTu, camTu.mauToiDa, true, true, false);
            for (VXLChienBinh chienBinh : this.chienBinhs) {
                if (chienBinh == null || chienBinh.chet || chienBinh.daRoiTran
                        || this.tranDau.cungDoi(camTu, chienBinh)) {
                    continue;
                }
                int noX = chienBinh.x - camTu.x;
                int noY = chienBinh.y - camTu.y;
                if (noX * noX + noY * noY <= TAM_NO_CAM_TU * TAM_NO_CAM_TU) {
                    int satThuong = Math.max(camTu.tanCong, chienBinh.mauToiDa * 28 / 100);
                    this.tranDau.satThuong(camTu, chienBinh, satThuong, true, true, false);
                }
            }
        }
        this.tranDau.ghiNhanNapDanSauPhatBan(camTu);
        if (!this.tranDau.kiemTraKetThuc()) {
            this.tranDau.chuyenLuotBotSauHanhDong(camTu,
                    daKichNo ? TRE_SAU_NO_CAM_TU : TRE_SAU_DI_CHUYEN_CAM_TU);
        }
    }

    private void diChuyenCamTu(VXLChienBinh camTu, VXLChienBinh mucTieu) {
        int chenhX = mucTieu.x - camTu.x;
        int chenhY = mucTieu.y - camTu.y;
        int huong = Integer.compare(chenhX, 0);
        int khoangCachDungNgang = Math.abs(chenhY) <= KHOANG_CACH_DOC_KICH_NO_CAM_TU
                ? KHOANG_CACH_NGANG_KICH_NO_CAM_TU : 0;
        int khoangCanDi = Math.min(BUOC_DI_CHUYEN_CAM_TU,
                Math.max(0, Math.abs(chenhX) - khoangCachDungNgang));
        if (huong == 0 && Math.abs(chenhY) > KHOANG_CACH_DOC_KICH_NO_CAM_TU) {
            huong = this.chonHuongLeoDiaHinh(camTu, mucTieu);
            khoangCanDi = BUOC_DI_CHUYEN_CAM_TU;
        }
        if (huong == 0 || khoangCanDi <= 0) {
            return;
        }
        short xHienTai = camTu.x;
        short yHienTai = camTu.y;
        int daDi = 0;
        while (daDi < khoangCanDi) {
            int buoc = Math.min(BUOC_NHO_DI_CHUYEN_CAM_TU, khoangCanDi - daDi);
            short xTiepTheo = this.gioiHan((short)(xHienTai + huong * buoc),
                    12, this.banDo.getWidth() - 12);
            short yTiepTheo = this.timViTriDatCamTu(xTiepTheo, yHienTai);
            if (Math.abs(yTiepTheo - yHienTai) > CHENH_CAO_TOI_DA_MOI_BUOC
                    || !this.viTriCamTuHopLe(camTu, xTiepTheo, yTiepTheo)) {
                break;
            }
            xHienTai = xTiepTheo;
            yHienTai = yTiepTheo;
            daDi += buoc;
        }
        if (xHienTai != camTu.x || yHienTai != camTu.y) {
            camTu.x = xHienTai;
            camTu.y = yHienTai;
            this.tranDau.phatDiChuyen(camTu);
        }
    }

    private int chonHuongLeoDiaHinh(VXLChienBinh camTu, VXLChienBinh mucTieu) {
        short xTrai = this.gioiHan((short)(camTu.x - 18), 12, this.banDo.getWidth() - 12);
        short xPhai = this.gioiHan((short)(camTu.x + 18), 12, this.banDo.getWidth() - 12);
        short yTrai = this.timViTriDatCamTu(xTrai, camTu.y);
        short yPhai = this.timViTriDatCamTu(xPhai, camTu.y);
        if (mucTieu.y < camTu.y && yTrai != yPhai) {
            return yTrai < yPhai ? -1 : 1;
        }
        if (mucTieu.y > camTu.y && yTrai != yPhai) {
            return yTrai > yPhai ? -1 : 1;
        }
        return camTu.chiSo % 2 == 0 ? -1 : 1;
    }

    private short timViTriDatCamTu(short x, short yBatDau) {
        int yDat = this.banDo.getHeight() - 1;
        for (int lechX = -NUA_RONG_HITBOX_CAM_TU;
                lechX <= NUA_RONG_HITBOX_CAM_TU; lechX += NUA_RONG_HITBOX_CAM_TU) {
            short diemX = this.gioiHan((short)(x + lechX), 0, this.banDo.getWidth() - 1);
            yDat = Math.min(yDat, this.banDo.timViTriDat(diemX, yBatDau));
        }
        return (short)yDat;
    }

    private boolean viTriCamTuHopLe(VXLChienBinh camTu, short x, short y) {
        for (int lechX = -NUA_RONG_HITBOX_CAM_TU;
                lechX <= NUA_RONG_HITBOX_CAM_TU; lechX += NUA_RONG_HITBOX_CAM_TU) {
            short diemX = (short)(x + lechX);
            if (this.banDo.coVaCham(diemX, (short)Math.max(0, y - 4))
                    || this.banDo.coVaCham(diemX, (short)Math.max(0, y - 16))
                    || this.banDo.coVaCham(diemX, (short)Math.max(0, y - 28))) {
                return false;
            }
        }
        return true;
    }

    private boolean daApSatNguoiChoi(VXLChienBinh camTu, VXLChienBinh mucTieu) {
        return Math.abs(mucTieu.x - camTu.x) <= KHOANG_CACH_NGANG_KICH_NO_CAM_TU
                && Math.abs(mucTieu.y - camTu.y) <= KHOANG_CACH_DOC_KICH_NO_CAM_TU;
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
        return this.timMucTieuGanNhat(nguoiBan, null);
    }

    private VXLChienBinh timMucTieuGanNhat(VXLChienBinh nguoiBan,
            VXLChienBinh mucTieuBoQua) {
        VXLChienBinh ganNhat = null;
        int khoangCachGanNhat = Integer.MAX_VALUE;
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu == nguoiBan || mucTieu == mucTieuBoQua
                    || mucTieu.chet || mucTieu.daRoiTran
                    || this.tranDau.cungDoi(nguoiBan, mucTieu)) {
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
            if (mucTieu == null || mucTieu.chet || mucTieu.daRoiTran
                    || this.tranDau.cungDoi(nguon, mucTieu)) {
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
