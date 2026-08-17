package com.vxl.chien;

public final class VXLHangDoiNapDan {
    @FunctionalInterface
    public interface KiemTraViTri {
        boolean hopLe(int viTri);
    }

    private VXLHangDoiNapDan() {
    }

    public static long ghiNhanHanhDong(long[] thuTuHanhDong, int viTri,
            long thuTuHienTai) {
        if (thuTuHanhDong == null || viTri < 0 || viTri >= thuTuHanhDong.length) {
            return thuTuHienTai;
        }
        long thuTuMoi = thuTuHienTai + 1L;
        if (thuTuMoi <= 0L) {
            thuTuMoi = 1L;
            java.util.Arrays.fill(thuTuHanhDong, 0L);
        }
        thuTuHanhDong[viTri] = thuTuMoi;
        return thuTuMoi;
    }

    public static int timViTriTiepTheo(int[] napDan, long[] thuTuHanhDong,
            int sauViTri, KiemTraViTri kiemTraViTri) {
        if (napDan == null || thuTuHanhDong == null
                || napDan.length != thuTuHanhDong.length || kiemTraViTri == null) {
            return -1;
        }
        int sanSang = timViTriSanSang(napDan, thuTuHanhDong, sauViTri, kiemTraViTri);
        if (sanSang >= 0) {
            return sanSang;
        }
        int nhoNhat = Integer.MAX_VALUE;
        for (int viTri = 0; viTri < napDan.length; viTri++) {
            if (kiemTraViTri.hopLe(viTri) && napDan[viTri] > 0) {
                nhoNhat = Math.min(nhoNhat, napDan[viTri]);
            }
        }
        if (nhoNhat == Integer.MAX_VALUE) {
            return -1;
        }
        for (int viTri = 0; viTri < napDan.length; viTri++) {
            if (kiemTraViTri.hopLe(viTri)) {
                napDan[viTri] = Math.max(0, napDan[viTri] - nhoNhat);
            }
        }
        return timViTriSanSang(napDan, thuTuHanhDong, sauViTri, kiemTraViTri);
    }

    private static int timViTriSanSang(int[] napDan, long[] thuTuHanhDong,
            int sauViTri, KiemTraViTri kiemTraViTri) {
        int soViTri = napDan.length;
        int batDau = sauViTri < 0 ? 0 : (sauViTri + 1) % soViTri;
        int chon = -1;
        long thuTuNhoNhat = Long.MAX_VALUE;
        for (int buoc = 0; buoc < soViTri; buoc++) {
            int viTri = (batDau + buoc) % soViTri;
            if (!kiemTraViTri.hopLe(viTri) || napDan[viTri] > 0) {
                continue;
            }
            long thuTu = Math.max(0L, thuTuHanhDong[viTri]);
            if (chon < 0 || thuTu < thuTuNhoNhat) {
                chon = viTri;
                thuTuNhoNhat = thuTu;
            }
        }
        return chon;
    }
}
