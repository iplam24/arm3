package com.vxl.chien;

import java.util.concurrent.ThreadLocalRandom;

public final class VXLTinhSatThuong {
    private static final int PHAN_TRAM_DAO_DONG = 5;

    private VXLTinhSatThuong() {
    }

    public static int tinhPhatBan(int tanCong, byte luc, int heSoPhanTram) {
        long satThuongCoBan = Math.max(1, tanCong) + Math.max(0, luc) / 2L;
        long ketQua = satThuongCoBan * Math.max(1, heSoPhanTram) / 100L;
        return (int)Math.min(Integer.MAX_VALUE, Math.max(1L, ketQua));
    }

    public static int tinhSauGiap(int satThuongGoc, int giap) {
        if (satThuongGoc <= 0) {
            return 0;
        }
        long mauSo = 100L + Math.max(0, (long)giap);
        long ketQua = (long)satThuongGoc * 100L / mauSo;
        return (int)Math.max(1L, Math.min(Integer.MAX_VALUE, ketQua));
    }

    public static int tinhPhatBanCoDaoDong(int tanCong, byte luc, int heSoPhanTram) {
        long satThuongCoBan = Math.max(1, tanCong) + Math.max(0, luc) / 2L;
        long ketQua = satThuongCoBan * Math.max(1, heSoPhanTram) / 100L;
        int satThuong = (int)Math.min(Integer.MAX_VALUE, Math.max(1L, ketQua));
        int daoDong = ThreadLocalRandom.current().nextInt(-PHAN_TRAM_DAO_DONG, PHAN_TRAM_DAO_DONG + 1);
        long ketQuaDaoDong = (long)satThuong * (100L + daoDong) / 100L;
        return (int)Math.max(1L, Math.min(Integer.MAX_VALUE, ketQuaDaoDong));
    }
}
