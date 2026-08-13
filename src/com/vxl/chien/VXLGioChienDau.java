package com.vxl.chien;

import java.util.concurrent.ThreadLocalRandom;

public final class VXLGioChienDau {
    private static final int GIO_NGANG_TOI_DA = 10;
    private static final int GIO_DOC_TOI_DA = 3;
    private static final int GIO_NGANG_TOI_THIEU = 3;

    public record HuongGio(byte x, byte y) {
    }

    private VXLGioChienDau() {
    }

    public static HuongGio taoMoi(byte gioHienTaiX, byte gioHienTaiY) {
        ThreadLocalRandom ngauNhien = ThreadLocalRandom.current();
        for (int lan = 0; lan < 12; lan++) {
            byte gioMoiX = (byte)ngauNhien.nextInt(-GIO_NGANG_TOI_DA,
                    GIO_NGANG_TOI_DA + 1);
            byte gioMoiY = (byte)ngauNhien.nextInt(-GIO_DOC_TOI_DA,
                    GIO_DOC_TOI_DA + 1);
            if (Math.abs(gioMoiX) < GIO_NGANG_TOI_THIEU
                    || gioMoiX == gioHienTaiX && gioMoiY == gioHienTaiY) {
                continue;
            }
            return new HuongGio(gioMoiX, gioMoiY);
        }
        byte gioDuPhongX = (byte)(gioHienTaiX >= 0
                ? -GIO_NGANG_TOI_THIEU : GIO_NGANG_TOI_THIEU);
        return new HuongGio(gioDuPhongX, (byte)0);
    }
}
