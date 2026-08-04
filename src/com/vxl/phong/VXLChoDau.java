package com.vxl.phong;

import com.vxl.chien.VXLQuanLyChien;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.IOException;

public class VXLChoDau {
    private static final String[] SOLO_BOT_NAMES = new String[]{"Iron Bot", "Hulk Bot", "Thor Bot", "Captain Bot", "Ultron Bot"};
    private static final byte[] SOLO_BOT_AVENGERS = new byte[]{1, 2, 3, 5, 8};
    private static final short[] SOLO_BOT_WEAPONS = new short[]{5, 27, 54, 55, 58};
    public final VXLPhong phong;
    public final byte ma;
    public final byte maxPlayers;
    public byte maBanDo;
    public int tien;
    public String ten;
    public boolean started;
    private final VXLNguoiChoi[] nguoiChois;
    private final boolean[] sanSang;
    private VXLNguoiChoi chuPhong;
    private VXLQuanLyChien fight;

    public VXLChoDau(VXLPhong phong, byte ma, byte maxPlayers, byte maBanDo) {
        this.phong = phong;
        this.ma = ma;
        this.maxPlayers = maxPlayers;
        this.maBanDo = maBanDo;
        this.tien = 0;
        this.ten = "Khu vực " + (ma + 1);
        this.nguoiChois = new VXLNguoiChoi[maxPlayers];
        this.sanSang = new boolean[maxPlayers];
    }

    public synchronized int laySoNguoiChoi() {
        int dem = 0;
        for (VXLNguoiChoi nguoiChoi : this.nguoiChois) {
            if (nguoiChoi != null) {
                dem++;
            }
        }
        return dem;
    }

    public synchronized VXLNguoiChoi layChuPhong() {
        return this.chuPhong;
    }

    public synchronized VXLNguoiChoi[] chupNguoiChoi() {
        return this.nguoiChois.clone();
    }

    public synchronized boolean vao(VXLNguoiChoi nguoiChoi, String matKhau) throws IOException {
        if (nguoiChoi == null) {
            return false;
        }
        if (this.started) {
            nguoiChoi.startOKDlg2("Bàn đang thi đấu.");
            return false;
        }
        if (VXLQuanLyPhong.layBanCho(nguoiChoi) != null) {
            VXLQuanLyPhong.roiBanCho(nguoiChoi);
        }
        int o = this.oTrongDauTien();
        if (o < 0) {
            nguoiChoi.startOKDlg2("Khu vực đã đầy.");
            return false;
        }
        this.nguoiChois[o] = nguoiChoi;
        this.sanSang[o] = false;
        nguoiChoi.chiSo = o;
        nguoiChoi.pointSeat = (byte)o;
        nguoiChoi.isReady = false;
        if (this.chuPhong == null) {
            this.chuPhong = nguoiChoi;
        }
        VXLQuanLyPhong.gan(nguoiChoi, this);

        nguoiChoi.dichVu.guiThongTinChoDau(this.phong.ma, this.ma, this.ten, this.phong.loai);
        nguoiChoi.dichVu.guiChonBanDoDau(this.maBanDo);
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null) {
                nguoiChoi.dichVu.guiNguoiChoiVaoDau(existing, this.chuPhong, this.phong.ma, this.ma);
            }
        }
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null && existing != nguoiChoi) {
                existing.dichVu.guiNguoiChoiVaoDau(nguoiChoi, this.chuPhong, this.phong.ma, this.ma);
            }
        }
        this.phatTien();
        return true;
    }

    public synchronized void roi(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        int o = this.chiSoCua(nguoiChoi);
        if (o < 0) {
            return;
        }
        this.nguoiChois[o] = null;
        this.sanSang[o] = false;
        nguoiChoi.isReady = false;
        nguoiChoi.chiSo = -1;
        nguoiChoi.pointSeat = 0;
        VXLQuanLyPhong.boGan(nguoiChoi);
        if (this.fight != null) {
            this.fight.khiNguoiChoiRoi(nguoiChoi);
        }
        if (nguoiChoi == this.chuPhong) {
            this.chuPhong = this.nguoiChoiDauTien();
        }
        int ownerId = this.chuPhong != null ? this.chuPhong.ma : -1;
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null) {
                existing.dichVu.guiRoiDau(nguoiChoi.ma, ownerId);
            }
        }
        nguoiChoi.dichVu.guiRoiDau(nguoiChoi.ma, ownerId);
        if (this.laySoNguoiChoi() == 0) {
            this.started = false;
            if (this.fight != null) {
                this.fight.dungBot();
            }
            this.fight = null;
            this.chuPhong = null;
        }
    }

    public synchronized void datSanSang(VXLNguoiChoi nguoiChoi, boolean giaTri) throws IOException {
        int o = this.chiSoCua(nguoiChoi);
        if (o < 0 || this.started) {
            return;
        }
        this.sanSang[o] = giaTri;
        nguoiChoi.isReady = giaTri;
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null) {
                existing.dichVu.guiSanSangDau(nguoiChoi.ma, giaTri);
            }
        }
    }

    public synchronized void datBanDo(VXLNguoiChoi nguoiChoi, byte maBanDo) throws IOException {
        if (nguoiChoi != this.chuPhong || this.started) {
            return;
        }
        this.maBanDo = maBanDo;
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null) {
                existing.dichVu.guiChonBanDoDau(maBanDo);
            }
        }
    }

    public synchronized void batDau(VXLNguoiChoi nguoiChoi) throws IOException {
        if (this.started || nguoiChoi != this.chuPhong) {
            return;
        }
        if (this.laySoNguoiChoi() == 0) {
            nguoiChoi.startOKDlg2("Chưa có người chơi.");
            return;
        }
        this.started = true;
        byte[] soloBotSlots = this.damBaoBotSolo();
        this.fight = new VXLQuanLyChien(this, this.chupNguoiChoi(), this.maBanDo);
        for (int i = 0; i < soloBotSlots.length; i++) {
            byte soloBotSlot = soloBotSlots[i];
            if (soloBotSlot >= 0) {
                this.fight.themBot(soloBotSlot, SOLO_BOT_NAMES[i], SOLO_BOT_WEAPONS[i], SOLO_BOT_AVENGERS[i]);
            }
        }
        this.fight.batDau();
    }

    public synchronized VXLQuanLyChien layTranDau() {
        return this.fight;
    }

    public synchronized void ketThucDau() {
        this.started = false;
        if (this.fight != null) {
            this.fight.dungBot();
        }
        this.fight = null;
        for (int i = 0; i < this.sanSang.length; i++) {
            this.sanSang[i] = false;
            if (this.nguoiChois[i] != null) {
                this.nguoiChois[i].isReady = false;
            }
        }
    }

    private int oTrongDauTien() {
        for (int i = 0; i < this.nguoiChois.length; i++) {
            if (this.nguoiChois[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private byte[] damBaoBotSolo() throws IOException {
        byte[] slots = new byte[]{-1, -1, -1, -1, -1};
        if (this.laySoNguoiChoi() != 1) {
            return slots;
        }
        for (int i = 0; i < SOLO_BOT_NAMES.length; i++) {
            int o = this.oTrongDauTienTru(slots);
            if (o < 0) {
                break;
            }
            VXLNguoiChoi bot = new VXLNguoiChoi(null);
            bot.ma = -9000 - o;
            bot.ten = SOLO_BOT_NAMES[i];
            bot.kinhNghiem = 1000;
            bot.clan = -1;
            bot.head = this.chuPhong != null ? this.chuPhong.head : 0;
            bot.leg = this.chuPhong != null ? this.chuPhong.leg : 10;
            bot.body = this.chuPhong != null ? this.chuPhong.body : 35;
            bot.hat = this.chuPhong != null ? this.chuPhong.hat : 60;
            bot.wing = this.chuPhong != null ? this.chuPhong.wing : 0;
            bot.wp = SOLO_BOT_WEAPONS[i];
            bot.avenger = SOLO_BOT_AVENGERS[i];
            bot.chiSo = o;
            bot.pointSeat = (byte)o;
            for (VXLNguoiChoi existing : this.nguoiChois) {
                if (existing != null) {
                    existing.dichVu.guiNguoiChoiVaoDau(bot, this.chuPhong, this.phong.ma, this.ma);
                }
            }
            slots[i] = (byte)o;
        }
        return slots;
    }

    private int oTrongDauTienTru(byte[] daGiu) {
        for (int i = 0; i < this.nguoiChois.length; i++) {
            if (this.nguoiChois[i] != null) {
                continue;
            }
            boolean used = false;
            for (byte o : daGiu) {
                if (o == i) {
                    used = true;
                    break;
                }
            }
            if (!used) {
                return i;
            }
        }
        return -1;
    }

    private int chiSoCua(VXLNguoiChoi nguoiChoi) {
        for (int i = 0; i < this.nguoiChois.length; i++) {
            if (this.nguoiChois[i] == nguoiChoi) {
                return i;
            }
        }
        return -1;
    }

    private VXLNguoiChoi nguoiChoiDauTien() {
        for (VXLNguoiChoi nguoiChoi : this.nguoiChois) {
            if (nguoiChoi != null) {
                return nguoiChoi;
            }
        }
        return null;
    }

    private void phatTien() throws IOException {
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null) {
                existing.dichVu.guiTienDau(this.tien, this.phong.loai);
            }
        }
    }
}
