package com.vxl.chien;

public final class VXLTinhSatThuong {
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
}
