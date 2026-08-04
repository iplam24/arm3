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
        VXLMauVatPham mau = VXLQuanLyMayChu.itemTemplates.get(maVatPham);
        return mau != null ? chuanHoaLoaiDan((byte)mau.part) : 0;
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