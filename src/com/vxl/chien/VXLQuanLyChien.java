package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLTinNhan;
import com.vxl.phong.VXLChoDau;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class VXLQuanLyChien {
    private static final ScheduledExecutorService BOT_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "lo-chien-bot");
        thread.setDaemon(true);
        return thread;
    });
    private static final int MAX_FIGHTERS = 8;
    private static final int TURN_SECONDS = 8;
    private final VXLChoDau wait;
    private final VXLChienBinh[] chienBinhs = new VXLChienBinh[MAX_FIGHTERS];
    private final VXLQuanLyBanDo map;
    private byte luotHienTai = -1;
    private boolean daKetThuc;
    private ScheduledFuture<?> tacVuBot;
    private long hanLuot;

    public VXLQuanLyChien(VXLChoDau wait, VXLNguoiChoi[] nguoiChois, byte maBanDo) {
        this.wait = wait;
        this.map = new VXLQuanLyBanDo(maBanDo);
        for (int i = 0; i < nguoiChois.length && i < this.chienBinhs.length; i++) {
            VXLNguoiChoi nguoiChoi = nguoiChois[i];
            if (nguoiChoi == null) {
                continue;
            }
            short x = this.map.laySinhX(i);
            short y = this.map.laySinhY(i);
            this.chienBinhs[i] = new VXLChienBinh(nguoiChoi, (byte)i, x, y);
        }
    }

    public void themBot(byte chiSo, String ten, short maVuKhi, byte avenger) {
        if (chiSo < 0 || chiSo >= this.chienBinhs.length || this.chienBinhs[chiSo] != null) {
            return;
        }
        this.chienBinhs[chiSo] = new VXLChienBinh(chiSo, this.map.laySinhX(chiSo), this.map.laySinhY(chiSo), ten, maVuKhi, avenger);
    }

    public void batDau() throws IOException {
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiBatDauDau(this.map.layMaBanDo(), this.chienBinhs, this.map.layMaNen());
            }
        }
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
            }
        }
        this.luotHienTai = this.nguoiSongTiepTu((byte)-1);
        this.sendNextTurn();
        this.lapLichBotBan();
    }

    public void diChuyen(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || chienBinh.chet || chienBinh.chiSo != this.luotHienTai || this.daKetThuc) {
            return;
        }
        short x = ms.boDoc().readShort();
        short y = ms.boDoc().readShort();
        chienBinh.x = this.kepShort(x, 0, this.map.getWidth());
        chienBinh.y = this.kepShort(y, 0, this.map.getHeight());
        this.phatDiChuyen(chienBinh);
    }

    public void capNhatXY(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || this.daKetThuc) {
            return;
        }
        chienBinh.x = this.kepShort(ms.boDoc().readShort(), 0, this.map.getWidth());
        chienBinh.y = this.kepShort(ms.boDoc().readShort(), 0, this.map.getHeight());
        this.phatCapNhatXY(chienBinh);
    }

    public void ban(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh shooter = this.layChienBinh(nguoiChoi);
        if (shooter == null || shooter.chet || shooter.chiSo != this.luotHienTai || this.daKetThuc) {
            return;
        }
        byte loaiDan = ms.boDoc().readByte();
        short x = ms.boDoc().readShort();
        short y = ms.boDoc().readShort();
        short goc = ms.boDoc().readShort();
        byte luc = ms.boDoc().readByte();
        if (loaiDan == 17 || loaiDan == 19) {
            ms.boDoc().readByte();
        }
        byte numShoot = ms.boDoc().readByte();
        if (luc <= 0) {
            luc = 10;
        }
        if (luc > 30) {
            luc = 30;
        }
        shooter.x = this.kepShort(x, 0, this.map.getWidth());
        shooter.y = this.kepShort(y, 0, this.map.getHeight());
        VXLKetQuaDan ketQua = this.xuLyPhatBan(shooter, this.layLoaiDanAnToan(loaiDan), goc, luc);
        this.phatBan(shooter, ketQua, numShoot);
        if (ketQua.mucTieu != null && ketQua.satThuong > 0) {
            this.satThuong(ketQua.mucTieu, ketQua.satThuong);
        }
        if (!this.daKetThuc) {
            this.sangLuot();
        }
    }

    public void kiemTraVaCham(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
    }

    public void boLuot(VXLNguoiChoi nguoiChoi) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh != null && !chienBinh.chet && chienBinh.chiSo == this.luotHienTai && !this.daKetThuc) {
            this.sangLuot();
        }
    }

    public void khiNguoiChoiRoi(VXLNguoiChoi nguoiChoi) {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh != null) {
            chienBinh.chet = true;
            chienBinh.hp = 0;
        }
    }

    public void dungBot() {
        if (this.tacVuBot != null) {
            this.tacVuBot.cancel(false);
            this.tacVuBot = null;
        }
    }

    private VXLKetQuaDan xuLyPhatBan(VXLChienBinh shooter, byte loaiDan, short goc, byte luc) {
        short[][] duongDan = this.taoDuongDan(shooter.x, shooter.y, goc, luc);
        VXLChienBinh mucTieu = this.timMucTieuTrung(shooter, duongDan[0], duongDan[1]);
        int satThuong = mucTieu != null ? Math.max(15, 20 + luc / 2) : 0;
        return new VXLKetQuaDan(loaiDan, shooter.x, shooter.y, goc, luc, duongDan[0], duongDan[1], mucTieu, satThuong);
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

    private void lapLichBotBan() {
        this.dungBot();
        this.tacVuBot = BOT_EXECUTOR.scheduleWithFixedDelay(() -> {
            try {
                this.nhipBot();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 700L, 700L, TimeUnit.MILLISECONDS);
    }

    private synchronized void nhipBot() throws IOException {
        if (this.daKetThuc) {
            this.dungBot();
            return;
        }
        VXLChienBinh turn = this.luotHienTai >= 0 && this.luotHienTai < this.chienBinhs.length ? this.chienBinhs[this.luotHienTai] : null;
        if (turn == null || turn.chet) {
            this.sangLuot();
            return;
        }
        if (turn.bot) {
            this.diChuyenBotTruocKhiBan(turn);
            VXLChienBinh mucTieu = this.timMucTieuGanNhat(turn);
            if (mucTieu != null) {
                short goc = this.gocToiMucTieu(turn, mucTieu);
                VXLKetQuaDan ketQua = this.xuLyPhatBan(turn, (byte)0, goc, (byte)18);
                this.phatBan(turn, ketQua, (byte)1);
                if (ketQua.mucTieu != null && ketQua.satThuong > 0) {
                    this.satThuong(ketQua.mucTieu, ketQua.satThuong);
                }
            }
            if (!this.daKetThuc) {
                this.sangLuot();
            }
        } else if (System.currentTimeMillis() > this.hanLuot) {
            this.sangLuot();
        }
    }

    private void diChuyenBotTruocKhiBan(VXLChienBinh bot) throws IOException {
        int shift = bot.chiSo % 2 == 0 ? 28 : -28;
        bot.x = this.kepShort((short)(bot.x + shift), 40, this.map.getWidth() - 40);
        this.phatDiChuyen(bot);
    }

    private VXLChienBinh timMucTieuGanNhat(VXLChienBinh shooter) {
        VXLChienBinh best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu == shooter || mucTieu.chet || shooter.bot && mucTieu.bot) {
                continue;
            }
            int dx = mucTieu.x - shooter.x;
            int dy = mucTieu.y - shooter.y;
            int distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = mucTieu;
            }
        }
        return best;
    }

    private short gocToiMucTieu(VXLChienBinh shooter, VXLChienBinh mucTieu) {
        double radians = Math.atan2(shooter.y - mucTieu.y, mucTieu.x - shooter.x);
        int degrees = (int)Math.round(Math.toDegrees(radians));
        if (degrees < 0) {
            degrees += 360;
        }
        return (short)degrees;
    }

    private VXLChienBinh timMucTieuTrung(VXLChienBinh shooter, short[] xs, short[] ys) {
        VXLChienBinh best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu == shooter || mucTieu.chet) {
                continue;
            }
            for (int i = 0; i < xs.length; i++) {
                int dx = xs[i] - mucTieu.x;
                int dy = ys[i] - mucTieu.y;
                int distance = dx * dx + dy * dy;
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = mucTieu;
                }
            }
        }
        return bestDistance <= 60 * 60 ? best : null;
    }

    private void satThuong(VXLChienBinh mucTieu, int satThuong) throws IOException {
        mucTieu.hp -= satThuong;
        if (mucTieu.hp <= 0) {
            mucTieu.hp = 0;
            mucTieu.chet = true;
        }
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiCapNhatMauDau(mucTieu.chiSo, mucTieu.hp, mucTieu.phanTramMau(), mucTieu.chet ? (byte)2 : (byte)0);
            }
        }
        this.kiemTraThang();
    }

    private void kiemTraThang() throws IOException {
        int alive = 0;
        byte pheThang = 0;
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && !chienBinh.chet) {
                alive++;
                pheThang = (byte)(chienBinh.chiSo % 2);
            }
        }
        if (alive > 1) {
            return;
        }
        this.daKetThuc = true;
        this.dungBot();
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                if (!chienBinh.chet) {
                    chienBinh.nguoiChoi.kill++;
                    chienBinh.nguoiChoi.cup += 1;
                } else {
                    chienBinh.nguoiChoi.chet++;
                }
                chienBinh.nguoiChoi.dichVu.guiKetThucDau(pheThang, 10, 100, 0);
                chienBinh.nguoiChoi.dichVu.capNhatCup((byte)0, chienBinh.nguoiChoi.cup);
                chienBinh.nguoiChoi.dichVu.capNhatKDVaKDA();
            }
        }
        this.wait.ketThucDau();
    }

    private void sangLuot() throws IOException {
        this.luotHienTai = this.nguoiSongTiepTu(this.luotHienTai);
        this.sendNextTurn();
    }

    private byte nguoiSongTiepTu(byte from) {
        for (int step = 1; step <= this.chienBinhs.length; step++) {
            int chiSo = (from + step + this.chienBinhs.length) % this.chienBinhs.length;
            VXLChienBinh chienBinh = this.chienBinhs[chiSo];
            if (chienBinh != null && !chienBinh.chet) {
                return (byte)chiSo;
            }
        }
        return -1;
    }

    private void sendNextTurn() throws IOException {
        if (this.daKetThuc || this.luotHienTai < 0) {
            return;
        }
        VXLChienBinh next = this.chienBinhs[this.luotHienTai];
        this.hanLuot = System.currentTimeMillis() + TURN_SECONDS * 1000L;
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiLuotDauTiep(this.luotHienTai, next.x, next.y, this.chienBinhConSong(), (byte)TURN_SECONDS);
            }
        }
    }

    private VXLChienBinh[] chienBinhConSong() {
        return this.chienBinhs;
    }

    private void phatDiChuyen(VXLChienBinh moved) throws IOException {
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiDiChuyenDau(moved.chiSo, moved.x, moved.y);
            }
        }
    }

    private void phatCapNhatXY(VXLChienBinh moved) throws IOException {
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiCapNhatXYLuyenTap(moved.chiSo, moved.x, moved.y);
            }
        }
    }

    private void phatBan(VXLChienBinh shooter, VXLKetQuaDan ketQua, byte numShoot) throws IOException {
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiKetQuaBanDau(shooter.chiSo, ketQua, numShoot);
            }
        }
    }

    private VXLChienBinh layChienBinh(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return null;
        }
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.nguoiChoi == nguoiChoi) {
                return chienBinh;
            }
        }
        return null;
    }

    private short[][] taoDuongDan(short batDauX, short batDauY, short goc, byte luc) {
        final int maxPoints = 36;
        short[] xs = new short[maxPoints];
        short[] ys = new short[maxPoints];
        double rad = Math.toRadians(goc);
        double speed = Math.max(8, luc) * 0.85D;
        double gravity = 0.33D;
        int len = 0;
        for (int i = 0; i < maxPoints; i++) {
            int px = (int)Math.round(batDauX + Math.cos(rad) * speed * i);
            int py = (int)Math.round(batDauY - Math.sin(rad) * speed * i + gravity * i * i);
            boolean outOfBounds = px < 0 || px > this.map.getWidth() || py < 0 || py > this.map.getHeight();
            px = Math.max(0, Math.min(this.map.getWidth(), px));
            py = Math.max(0, Math.min(this.map.getHeight(), py));
            xs[i] = (short)px;
            ys[i] = (short)py;
            len = i + 1;
            if (outOfBounds || this.map.coVaCham(xs[i], ys[i])) {
                break;
            }
        }
        len = Math.max(1, Math.min(len, xs.length));
        short[] trimX = new short[len];
        short[] trimY = new short[len];
        System.arraycopy(xs, 0, trimX, 0, len);
        System.arraycopy(ys, 0, trimY, 0, len);
        return new short[][]{trimX, trimY};
    }

    private short kepShort(short giaTri, int nhoNhat, int lonNhat) {
        int v = giaTri;
        if (v < nhoNhat) {
            v = nhoNhat;
        }
        if (v > lonNhat) {
            v = lonNhat;
        }
        return (short)v;
    }
}
