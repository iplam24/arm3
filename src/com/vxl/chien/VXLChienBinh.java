package com.vxl.chien;

import com.vxl.mohinh.VXLNguoiChoi;

public class VXLChienBinh {
    public final VXLNguoiChoi nguoiChoi;
    public final byte chiSo;
    public final boolean bot;
    public final String ten;
    public final int ma;
    public final short maVuKhi;
    public final byte avenger;
    public short x;
    public short y;
    public int hp;
    public int mauToiDa;
    public boolean chet;

    public VXLChienBinh(VXLNguoiChoi nguoiChoi, byte chiSo, short x, short y) {
        this.nguoiChoi = nguoiChoi;
        this.chiSo = chiSo;
        this.bot = false;
        this.ten = nguoiChoi.ten;
        this.ma = nguoiChoi.ma;
        this.maVuKhi = nguoiChoi.wp;
        this.avenger = nguoiChoi.avenger;
        this.x = x;
        this.y = y;
        this.mauToiDa = Math.max(100, nguoiChoi.pointAdd != null ? nguoiChoi.pointAdd[0] : 100);
        this.hp = this.mauToiDa;
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger) {
        this.nguoiChoi = null;
        this.chiSo = chiSo;
        this.bot = true;
        this.ten = ten;
        this.ma = -9000 - chiSo;
        this.maVuKhi = maVuKhi;
        this.avenger = avenger;
        this.x = x;
        this.y = y;
        this.mauToiDa = 160;
        this.hp = this.mauToiDa;
    }

    public boolean coPhien() {
        return this.nguoiChoi != null && this.nguoiChoi.dichVu != null;
    }

    public byte phanTramMau() {
        if (this.mauToiDa <= 0) {
            return 0;
        }
        return (byte)Math.max(0, Math.min(100, this.hp * 100 / this.mauToiDa));
    }
}
