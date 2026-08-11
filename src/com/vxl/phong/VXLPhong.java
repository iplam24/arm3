package com.vxl.phong;

public class VXLPhong {
    public final byte ma;
    public final byte loai;
    public final String ten;
    public final VXLChoDau[] banChos;

    public VXLPhong(int ma, int boardCount, byte loai, byte maxPlayers, byte maBanDo) {
        if (boardCount <= 0) {
            throw new IllegalArgumentException("boardCount must be positive.");
        }
        if (maxPlayers <= 0) {
            throw new IllegalArgumentException("maxPlayers must be positive.");
        }
        this.ma = (byte)ma;
        this.loai = loai;
        this.ten = VXLLoaiPhong.layTen(loai);
        this.banChos = new VXLChoDau[boardCount];
        for (int i = 0; i < boardCount; i++) {
            this.banChos[i] = new VXLChoDau(this, (byte)i, maxPlayers, maBanDo);
        }
    }

    public byte layDoDay() {
        int current = 0;
        int lonNhat = 0;
        for (VXLChoDau banCho : this.banChos) {
            current += banCho.laySoNguoiChoi();
            lonNhat += banCho.maxPlayers;
        }
        if (lonNhat <= 0) {
            return 2;
        }
        int phanTram = current * 100 / lonNhat;
        if (phanTram < 50) {
            return 2;
        }
        return (byte)(phanTram < 75 ? 1 : 0);
    }
}
