package com.vxl.chien;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLMauVatPham;

public final class VXLCauHinhVatPhamChienDau {
    private VXLCauHinhVatPhamChienDau() {
    }

    public static int layHeSoSatThuong(int maVatPham) {
        return switch (maVatPham) {
            case 222 -> 175;
            case 227, 231, 237 -> 130;
            case 228, 235, 239 -> 140;
            case 238, 240, 241, 245 -> 155;
            case 243, 244, 247, 248, 249, 250 -> 90;
            default -> 100;
        };
    }

    public static byte layLoaiDan(int maVatPham, byte loaiDanMacDinh) {
        if (maVatPham < 0) {
            return chuanHoaLoaiDan(loaiDanMacDinh);
        }
        if (VXLQuanLyMayChu.itemTemplates == null) {
            return chuanHoaLoaiDan(loaiDanMacDinh);
        }
        VXLMauVatPham mau = VXLQuanLyMayChu.itemTemplates.get(maVatPham);
        return mau != null ? chuanHoaLoaiDan((byte)mau.part) : chuanHoaLoaiDan(loaiDanMacDinh);
    }

    public static byte layLoaiDanTheoVuKhi(int maVuKhi, byte loaiDanMacDinh) {
        return switch (maVuKhi) {
            case 5 -> 0;
            case 27, 37, 132, 133, 156 -> 1;
            case 28, 54, 143, 144, 145 -> 2;
            case 29, 55, 153, 154, 155 -> 3;
            case 30, 56, 146, 147, 148 -> 4;
            case 31, 57, 134, 135 -> 5;
            case 32, 58, 140, 141, 142 -> 6;
            default -> chuanHoaLoaiDan(loaiDanMacDinh);
        };
    }

    public static short layMaAnhDanMacDinh(byte loaiDan) {
        return switch (chuanHoaLoaiDan(loaiDan)) {
            case 1 -> 10;
            case 2 -> 73;
            case 3 -> 19;
            case 4 -> 28;
            case 5 -> 24;
            case 6 -> 37;
            default -> 1;
        };
    }

    public static short layMaAnhDanTheoVuKhi(int maVuKhi) {
        return switch (maVuKhi) {
            case 5 -> 5;
            case 27 -> 10;
            case 37 -> 11;
            case 156 -> 12;
            case 132 -> 13;
            case 133 -> 14;
            case 55 -> 19;
            case 29 -> 20;
            case 153 -> 21;
            case 154 -> 22;
            case 155 -> 23;
            case 57 -> 24;
            case 31 -> 25;
            case 134 -> 26;
            case 135 -> 27;
            case 56 -> 28;
            case 30 -> 29;
            case 146 -> 30;
            case 147 -> 31;
            case 148 -> 32;
            case 58 -> 37;
            case 140 -> 38;
            case 32 -> 39;
            case 141 -> 40;
            case 142 -> 41;
            case 54 -> 73;
            case 28 -> 74;
            case 143 -> 75;
            case 145 -> 76;
            case 144 -> 77;
            default -> layMaAnhDanMacDinh(layLoaiDanTheoVuKhi(maVuKhi, (byte)0));
        };
    }

    public static boolean laDanDacBiet(int maVatPham) {
        return switch (maVatPham) {
            case 222, 227, 228, 231, 235, 236, 237, 238, 239, 240, 241,
                    243, 244, 245, 247, 248, 249, 250 -> true;
            default -> false;
        };
    }

    private static byte chuanHoaLoaiDan(byte loaiDan) {
        return Byte.toUnsignedInt(loaiDan) <= 58 ? loaiDan : 0;
    }
}
