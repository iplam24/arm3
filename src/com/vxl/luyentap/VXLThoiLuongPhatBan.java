package com.vxl.luyentap;

final class VXLThoiLuongPhatBan {
    private static final long THOI_GIAN_MOI_KHUNG = 34L;
    private static final long SO_KHUNG_CHUAN_BI_BAN = 36L;
    private static final long TRE_HIEU_UNG_SAU_VA_CHAM = 1800L;
    private static final long TRE_DU_PHONG_TOI_THIEU = 3000L;
    private static final long TRE_DU_PHONG_TOI_DA = 20000L;

    private VXLThoiLuongPhatBan() {
    }

    static KetQua tinh(byte loaiDan, short[][] duongX, short[][] duongY) {
        int soDuongDan = Math.min(doDai(duongX), doDai(duongY));
        int soDiemLonNhat = 1;
        int khoangCachSinhDanTheoKhung = layKhoangCachSinhDanTheoKhung(loaiDan);
        long treSinhLoat = 0L;
        long treBayToiDa = THOI_GIAN_MOI_KHUNG;

        for (int i = 0; i < soDuongDan; i++) {
            int soDiem = Math.max(1, Math.min(doDai(duongX[i]), doDai(duongY[i])));
            soDiemLonNhat = Math.max(soDiemLonNhat, soDiem);
            long treSinhVien = khoangCachSinhDanTheoKhung > 0
                    ? (long)i * khoangCachSinhDanTheoKhung * THOI_GIAN_MOI_KHUNG
                    : 0L;
            long treBayVien = (long)soDiem * THOI_GIAN_MOI_KHUNG;
            treSinhLoat = Math.max(treSinhLoat, treSinhVien);
            treBayToiDa = Math.max(treBayToiDa, treSinhVien + treBayVien);
        }

        long treChuanBi = SO_KHUNG_CHUAN_BI_BAN * THOI_GIAN_MOI_KHUNG;
        long treDuPhong = treChuanBi + treBayToiDa + TRE_HIEU_UNG_SAU_VA_CHAM;
        treDuPhong = Math.max(TRE_DU_PHONG_TOI_THIEU,
                Math.min(TRE_DU_PHONG_TOI_DA, treDuPhong));
        return new KetQua(soDuongDan, soDiemLonNhat, treChuanBi,
                treSinhLoat, treBayToiDa, treDuPhong);
    }

    private static int layKhoangCachSinhDanTheoKhung(byte loaiDan) {
        return switch (Byte.toUnsignedInt(loaiDan)) {
            case 1, 10, 11 -> 10;
            case 33, 37 -> 5;
            case 32, 40, 41, 44, 48, 80 -> 3;
            case 43 -> 11;
            case 47 -> 2;
            default -> 0;
        };
    }

    private static int doDai(short[][] mang) {
        return mang == null ? 0 : mang.length;
    }

    private static int doDai(short[] mang) {
        return mang == null ? 0 : mang.length;
    }

    record KetQua(int soDuongDan, int soDiemLonNhat, long treChuanBi,
            long treSinhLoat, long treBayToiDa, long treDuPhongKetThuc) {
    }
}