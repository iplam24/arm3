package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;

final class VXLTinhDuongDan {
    private static final int BAN_KINH_TRUNG = 42;
    private static final int SO_DIEM_TOI_DA = 160;
    private static final double BUOC_THOI_GIAN = 0.5D;
    private static final double HE_SO_TOC_DO = 0.85D;
    private static final double TRONG_LUC = 0.33D;
    private static final double KHOANG_CACH_DAU_SUNG = 16D;
    private static final double DO_CAO_DAU_SUNG = 12D;
    private static final byte LUC_TOI_THIEU = 12;
    private static final byte LUC_TOI_DA = 30;
    private final VXLQuanLyBanDo banDo;
    private final VXLChienBinh[] chienBinhs;

    VXLTinhDuongDan(VXLQuanLyBanDo banDo, VXLChienBinh[] chienBinhs) {
        this.banDo = banDo;
        this.chienBinhs = chienBinhs;
    }

    short[][] tao(short batDauX, short batDauY, short goc, byte luc) {
        final int soDiemToiDa = 36;
        short[] xs = new short[soDiemToiDa];
        short[] ys = new short[soDiemToiDa];
        double rad = Math.toRadians(goc);
        double tocDo = Math.max(8, luc) * 0.85D;
        double trongLuc = 0.33D;
        int doDai = 0;
        for (int i = 0; i < soDiemToiDa; i++) {
            int px = (int)Math.round(batDauX + Math.cos(rad) * tocDo * i);
            int py = (int)Math.round(batDauY - Math.sin(rad) * tocDo * i + trongLuc * i * i);
            boolean vuotBien = px < 0 || px > this.banDo.getWidth() || py < 0 || py > this.banDo.getHeight();
            px = Math.max(0, Math.min(this.banDo.getWidth(), px));
            py = Math.max(0, Math.min(this.banDo.getHeight(), py));
            xs[i] = (short)px;
            ys[i] = (short)py;
            doDai = i + 1;
            if (vuotBien || this.banDo.coVaCham(xs[i], ys[i])) {
                break;
            }
        }
        doDai = Math.max(1, Math.min(doDai, xs.length));
        short[] xRutGon = new short[doDai];
        short[] yRutGon = new short[doDai];
        System.arraycopy(xs, 0, xRutGon, 0, doDai);
        System.arraycopy(ys, 0, yRutGon, 0, doDai);
        return new short[][]{xRutGon, yRutGon};
    }

    private boolean coVaChamTrenDoan(short x1, short y1, short x2, short y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int buoc = Math.max(Math.abs(dx), Math.abs(dy));
        if (buoc == 0) {
            return this.banDo.coVaCham(x1, y1);
        }
        for (int step = 0; step <= buoc; step++) {
            short px = (short)(x1 + dx * step / buoc);
            short py = (short)(y1 + dy * step / buoc);
            if (this.banDo.coVaCham(px, py)) {
                return true;
            }
        }
        return false;
    }

    VXLChienBinh timMucTieuTrung(VXLChienBinh nguoiBan, short[] xs, short[] ys) {
        int banKinhBinhPhuong = BAN_KINH_TRUNG * BAN_KINH_TRUNG;
        for (int i = 0; i < xs.length; i++) {
            VXLChienBinh ganNhat = null;
            int khoangCachGanNhat = Integer.MAX_VALUE;
            for (VXLChienBinh mucTieu : this.chienBinhs) {
                if (mucTieu == null || mucTieu == nguoiBan || mucTieu.chet || mucTieu.luotVoHinh > 0) {
                    continue;
                }
                int dx = xs[i] - mucTieu.x;
                int dy = ys[i] - mucTieu.y;
                int khoangCach = dx * dx + dy * dy;
                if (khoangCach <= banKinhBinhPhuong && khoangCach < khoangCachGanNhat) {
                    khoangCachGanNhat = khoangCach;
                    ganNhat = mucTieu;
                }
            }
            if (ganNhat != null) {
                return ganNhat;
            }
        }
        return null;
    }

    short gocToiMucTieu(VXLChienBinh nguoiBan, VXLChienBinh mucTieu) {
        if (nguoiBan == null || mucTieu == null) {
            return 0;
        }
        double radians = Math.atan2(nguoiBan.y - mucTieu.y, mucTieu.x - nguoiBan.x);
        int degrees = (int)Math.round(Math.toDegrees(radians));
        return (short)((degrees % 360 + 360) % 360);
    }

    byte lucCanThietToiMucTieu(VXLChienBinh nguoiBan, VXLChienBinh mucTieu) {
        if (nguoiBan == null || mucTieu == null) {
            return LUC_TOI_THIEU;
        }
        double khoangCach = Math.hypot(mucTieu.x - nguoiBan.x, mucTieu.y - nguoiBan.y);
        double lucLyThuyet = Math.sqrt(2D * TRONG_LUC * Math.max(1D, khoangCach)) / HE_SO_TOC_DO;
        int luc = (int)Math.ceil(lucLyThuyet) + 2;
        return (byte)Math.max(LUC_TOI_THIEU, Math.min(LUC_TOI_DA, luc));
    }

    short gocDanDaoToiMucTieu(VXLChienBinh nguoiBan, VXLChienBinh mucTieu, byte luc) {
        if (nguoiBan == null || mucTieu == null) {
            return 0;
        }
        double tocDo = Math.max(8, Byte.toUnsignedInt(luc)) * HE_SO_TOC_DO;
        double goc = Math.toRadians(this.gocToiMucTieu(nguoiBan, mucTieu));
        double xDauSung = nguoiBan.x + Math.cos(goc) * KHOANG_CACH_DAU_SUNG;
        double yDauSung = nguoiBan.y - DO_CAO_DAU_SUNG - Math.sin(goc) * KHOANG_CACH_DAU_SUNG;
        for (int lan = 0; lan < 2; lan++) {
            double dx = mucTieu.x - xDauSung;
            double dy = mucTieu.y - yDauSung;
            double khoangCachNgang = Math.abs(dx);
            if (khoangCachNgang < 1D) {
                return this.gocToiMucTieu(nguoiBan, mucTieu);
            }
            double heSoTrongLuc = TRONG_LUC * khoangCachNgang * khoangCachNgang / (tocDo * tocDo);
            double dinhThuc = khoangCachNgang * khoangCachNgang
                    - 4D * heSoTrongLuc * (heSoTrongLuc - dy);
            if (dinhThuc < 0D || heSoTrongLuc < 1.0E-9D) {
                return this.gocToiMucTieu(nguoiBan, mucTieu);
            }
            double tanGoc = (khoangCachNgang - Math.sqrt(dinhThuc)) / (2D * heSoTrongLuc);
            double gocNgang = Math.atan(tanGoc);
            goc = dx >= 0D ? gocNgang : Math.PI - gocNgang;
            xDauSung = nguoiBan.x + Math.cos(goc) * KHOANG_CACH_DAU_SUNG;
            yDauSung = nguoiBan.y - DO_CAO_DAU_SUNG - Math.sin(goc) * KHOANG_CACH_DAU_SUNG;
        }
        int degrees = (int)Math.round(Math.toDegrees(goc));
        return (short)((degrees % 360 + 360) % 360);
    }

    short[] gioiHanDiChuyen(short xCu, short yCu, short xMoi, short yMoi, int khoangCachToiDa) {
        int xBatDau = Math.max(0, Math.min(this.banDo.getWidth(), xCu));
        int yBatDau = Math.max(0, Math.min(this.banDo.getHeight(), yCu));
        int xDich = Math.max(0, Math.min(this.banDo.getWidth(), xMoi));
        int yDich = Math.max(0, Math.min(this.banDo.getHeight(), yMoi));
        int dx = xDich - xBatDau;
        int dy = yDich - yBatDau;
        int gioiHan = Math.max(0, khoangCachToiDa);
        double khoangCach = Math.hypot(dx, dy);
        if (khoangCach > gioiHan && khoangCach > 0) {
            double tiLe = gioiHan / khoangCach;
            xDich = (int)Math.round(xBatDau + dx * tiLe);
            yDich = (int)Math.round(yBatDau + dy * tiLe);
        }
        int soBuoc = Math.max(Math.abs(xDich - xBatDau), Math.abs(yDich - yBatDau));
        for (int buoc = 1; buoc <= soBuoc; buoc++) {
            int x = xBatDau + (xDich - xBatDau) * buoc / soBuoc;
            int y = yBatDau + (yDich - yBatDau) * buoc / soBuoc;
            if (this.banDo.coVaCham((short)x, (short)y)) {
                return new short[]{(short)(xBatDau + (xDich - xBatDau) * (buoc - 1) / soBuoc),
                        (short)(yBatDau + (yDich - yBatDau) * (buoc - 1) / soBuoc)};
            }
        }
        return new short[]{(short)xDich, (short)yDich};
    }
}
