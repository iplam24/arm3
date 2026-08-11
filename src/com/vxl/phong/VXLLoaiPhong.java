package com.vxl.phong;

public final class VXLLoaiPhong {
    public static final byte TAP_SU = 0;
    public static final byte TRUNG_CAP = 1;
    public static final byte CAO_CAP = 2;
    public static final byte TU_DO = 3;
    public static final byte BOSS = 4;

    private static final byte[] THU_TU = new byte[]{
        TAP_SU, TRUNG_CAP, CAO_CAP, TU_DO, BOSS
    };

    private VXLLoaiPhong() {
    }

    public static byte[] layThuTu() {
        return THU_TU.clone();
    }

    public static String layTen(byte loai) {
        return switch (loai) {
            case TAP_SU -> "Ph\u00f2ng T\u1eadp s\u1ef1";
            case TRUNG_CAP -> "Ph\u00f2ng Trung c\u1ea5p";
            case CAO_CAP -> "Ph\u00f2ng Cao c\u1ea5p";
            case TU_DO -> "Ph\u00f2ng T\u1ef1 do";
            case BOSS -> "Ph\u00f2ng Boss";
            default -> "Ph\u00f2ng kh\u00e1c";
        };
    }

    public static boolean laBoss(byte loai) {
        return loai == BOSS;
    }
}
