package com.vxl.chien;

public class VXLKetQuaDan {
    public final byte loaiDan;
    public final short batDauX;
    public final short batDauY;
    public final short goc;
    public final byte luc;
    public final short[] duongX;
    public final short[] duongY;
    public final VXLChienBinh mucTieu;
    public final int satThuong;

    public VXLKetQuaDan(byte loaiDan, short batDauX, short batDauY, short goc, byte luc,
            short[] duongX, short[] duongY, VXLChienBinh mucTieu, int satThuong) {
        this.loaiDan = loaiDan;
        this.batDauX = batDauX;
        this.batDauY = batDauY;
        this.goc = goc;
        this.luc = luc;
        this.duongX = duongX;
        this.duongY = duongY;
        this.mucTieu = mucTieu;
        this.satThuong = satThuong;
    }
}
