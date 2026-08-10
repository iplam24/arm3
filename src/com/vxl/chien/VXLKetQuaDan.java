package com.vxl.chien;

public class VXLKetQuaDan {
    public final byte loaiDan;
    public final short batDauX;
    public final short batDauY;
    public final short goc;
    public final byte luc;
    public final byte lucTach;
    public final byte chiMang;
    public final byte loaiSieuCao;
    public final short xSieuCao;
    public final short ySieuCao;
    public final short[] duongX;
    public final short[] duongY;
    public final short[][] cacDuongX;
    public final short[][] cacDuongY;
    public final VXLChienBinh mucTieu;
    public final int satThuong;
    public final VXLChienBinh[] mucTieuTheoQuyDao;
    public final int[] satThuongTheoQuyDao;
    public final int satThuongMoiVien;
    public final int tranSatThuong;
    public final byte avengerDan;

    public VXLKetQuaDan(byte loaiDan, short batDauX, short batDauY, short goc, byte luc,
            short[] duongX, short[] duongY, VXLChienBinh mucTieu, int satThuong) {
        this(loaiDan, batDauX, batDauY, goc, luc, (byte)0,
                (byte)0, (byte)0, (short)-1, (short)-1,
                new short[][]{duongX}, new short[][]{duongY},
                new VXLChienBinh[]{mucTieu}, new int[]{satThuong}, satThuong, satThuong,
                (byte)0);
    }

    public VXLKetQuaDan(byte loaiDan, short batDauX, short batDauY, short goc, byte luc,
            byte lucTach, byte chiMang, byte loaiSieuCao, short xSieuCao, short ySieuCao,
            short[][] cacDuongX, short[][] cacDuongY,
            VXLChienBinh[] mucTieuTheoQuyDao, int[] satThuongTheoQuyDao,
            int satThuongMoiVien, int tranSatThuong, byte avengerDan) {
        this.loaiDan = loaiDan;
        this.batDauX = batDauX;
        this.batDauY = batDauY;
        this.goc = goc;
        this.luc = luc;
        this.lucTach = lucTach;
        this.chiMang = chiMang;
        this.loaiSieuCao = loaiSieuCao;
        this.xSieuCao = xSieuCao;
        this.ySieuCao = ySieuCao;
        this.cacDuongX = cacDuongX;
        this.cacDuongY = cacDuongY;
        this.duongX = cacDuongX.length > 0 ? cacDuongX[0] : new short[0];
        this.duongY = cacDuongY.length > 0 ? cacDuongY[0] : new short[0];
        this.mucTieuTheoQuyDao = mucTieuTheoQuyDao;
        this.satThuongTheoQuyDao = satThuongTheoQuyDao;
        this.satThuongMoiVien = satThuongMoiVien;
        this.tranSatThuong = tranSatThuong;
        this.avengerDan = avengerDan;
        VXLChienBinh mucTieuDauTien = null;
        int tongSatThuong = 0;
        for (int i = 0; i < mucTieuTheoQuyDao.length; i++) {
            if (mucTieuTheoQuyDao[i] != null && mucTieuDauTien == null) {
                mucTieuDauTien = mucTieuTheoQuyDao[i];
            }
            if (i < satThuongTheoQuyDao.length && mucTieuTheoQuyDao[i] == mucTieuDauTien) {
                tongSatThuong += Math.max(0, satThuongTheoQuyDao[i]);
            }
        }
        this.mucTieu = mucTieuDauTien;
        this.satThuong = tongSatThuong;
    }

    public VXLKetQuaDan nhanBanDuongDanHienThi(int soLan) {
        int soBan = Math.max(1, soLan);
        if (soBan == 1 || this.cacDuongX.length == 0) {
            return this;
        }
        int soQuyDaoMoi = this.cacDuongX.length * soBan;
        short[][] duongXMoi = new short[soQuyDaoMoi][];
        short[][] duongYMoi = new short[soQuyDaoMoi][];
        VXLChienBinh[] mucTieuMoi = new VXLChienBinh[soQuyDaoMoi];
        int[] satThuongMoi = new int[soQuyDaoMoi];
        for (int lan = 0; lan < soBan; lan++) {
            int batDau = lan * this.cacDuongX.length;
            for (int i = 0; i < this.cacDuongX.length; i++) {
                int dich = batDau + i;
                duongXMoi[dich] = this.cacDuongX[i].clone();
                duongYMoi[dich] = this.cacDuongY[i].clone();
                if (lan == 0 && i < this.mucTieuTheoQuyDao.length) {
                    mucTieuMoi[dich] = this.mucTieuTheoQuyDao[i];
                    satThuongMoi[dich] = i < this.satThuongTheoQuyDao.length
                            ? this.satThuongTheoQuyDao[i] : 0;
                }
            }
        }
        return new VXLKetQuaDan(this.loaiDan, this.batDauX, this.batDauY, this.goc,
                this.luc, this.lucTach, this.chiMang, this.loaiSieuCao,
                this.xSieuCao, this.ySieuCao, duongXMoi, duongYMoi, mucTieuMoi,
                satThuongMoi, this.satThuongMoiVien, this.tranSatThuong,
                this.avengerDan);
    }
}
