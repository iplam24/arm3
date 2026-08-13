package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;

public final class VXLDiaHinhPhatBan {
    private static final int CHIEU_CAO_THAN_HULK = 35;

    private VXLDiaHinhPhatBan() {
    }

    public static void ghiNhanLo(VXLQuanLyBanDo banDo, byte loaiDan, byte avenger,
            VXLHeThongDan.KetQuaPhatBan phatBan) {
        if (phatBan == null) {
            return;
        }
        ghiNhanLo(banDo, loaiDan, avenger, phatBan.vaChamDiaHinhX,
                phatBan.vaChamDiaHinhY);
    }
    public static void ghiNhanLo(VXLQuanLyBanDo banDo, VXLKetQuaDan ketQua) {
        if (ketQua == null) {
            return;
        }
        ghiNhanLo(banDo, ketQua.loaiDan, ketQua.avengerDan,
                ketQua.vaChamDiaHinhX, ketQua.vaChamDiaHinhY);
    }

    private static void ghiNhanLo(VXLQuanLyBanDo banDo, byte loaiDan, byte avenger,
            short[] vaChamDiaHinhX, short[] vaChamDiaHinhY) {
        if (banDo == null || vaChamDiaHinhX == null || vaChamDiaHinhY == null) {
            return;
        }
        VXLHoSoDan hoSoDan = VXLCauHinhVatPhamChienDau.layHoSoDan(loaiDan, avenger);
        int loaiDanClient = Byte.toUnsignedInt(hoSoDan.loaiClient());
        if (loaiDanClient == 8 || loaiDanClient == 56) {
            ghiNhanToNhen(banDo, vaChamDiaHinhX, vaChamDiaHinhY);
            return;
        }
        String tenMatNa = layTenMatNa(hoSoDan.loaiClient());
        int soQuyDao = Math.min(vaChamDiaHinhX.length, vaChamDiaHinhY.length);
        for (int i = 0; i < soQuyDao; i++) {
            short xVaCham = vaChamDiaHinhX[i];
            short yVaCham = vaChamDiaHinhY[i];
            if (xVaCham == VXLHeThongDan.KHONG_CO_VA_CHAM_DIA_HINH
                    || yVaCham == VXLHeThongDan.KHONG_CO_VA_CHAM_DIA_HINH) {
                continue;
            }
            if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.NHAN_VAT_LAO) {
                ghiNhanLoHulk(banDo, xVaCham, yVaCham);
            } else {
                banDo.taoLoTheoMatNa(xVaCham, yVaCham, tenMatNa);
            }
        }
    }

    private static void ghiNhanToNhen(VXLQuanLyBanDo banDo,
            short[] vaChamDiaHinhX, short[] vaChamDiaHinhY) {
        int soDiem = Math.min(vaChamDiaHinhX.length, vaChamDiaHinhY.length);
        for (int i = 0; i < soDiem; i++) {
            short x = vaChamDiaHinhX[i];
            short y = vaChamDiaHinhY[i];
            if (x != VXLHeThongDan.KHONG_CO_VA_CHAM_DIA_HINH
                    && y != VXLHeThongDan.KHONG_CO_VA_CHAM_DIA_HINH) {
                banDo.taoToNhen(x, y);
            }
        }
    }

    private static void ghiNhanLoHulk(VXLQuanLyBanDo banDo, short x, short yChan) {
        banDo.taoLoTheoMatNa(x, yChan, "hrangcua.png");
        banDo.taoLoTheoMatNa(x, (short)Math.max(0, yChan - CHIEU_CAO_THAN_HULK / 2),
                "hrangcua.png");
        banDo.taoLoTheoMatNa(x, (short)Math.max(0, yChan - CHIEU_CAO_THAN_HULK),
                "hrangcua.png");
    }

    private static String layTenMatNa(byte loaiDanClient) {
        return switch (Byte.toUnsignedInt(loaiDanClient)) {
            case 0, 32, 24, 48, 52 -> "h36x30.png";
            case 1, 11, 17, 18, 19, 21, 27, 44, 58 -> "smallhole.png";
            case 2, 20, 30 -> "h32x26.png";
            case 3 -> "h55x50.png";
            case 6, 12 -> "hrangcua.png";
            case 7, 25, 31, 37, 47 -> "h14x12.png";
            case 9 -> "rangehole.png";
            case 10 -> "rocket.png";
            case 15, 22, 42, 43, 45, 57 -> "hgrenade.png";
            default -> "h32x26.png";
        };
    }
}
