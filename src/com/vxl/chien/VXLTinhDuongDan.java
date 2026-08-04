package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;

final class VXLTinhDuongDan {
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

    VXLChienBinh timMucTieuTrung(VXLChienBinh nguoiBan, short[] xs, short[] ys) {
        VXLChienBinh ganNhat = null;
        int khoangCachGanNhat = Integer.MAX_VALUE;
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu == nguoiBan || mucTieu.chet || mucTieu.luotVoHinh > 0) {
                continue;
            }
            for (int i = 0; i < xs.length; i++) {
                int dx = xs[i] - mucTieu.x;
                int dy = ys[i] - mucTieu.y;
                int khoangCach = dx * dx + dy * dy;
                if (khoangCach < khoangCachGanNhat) {
                    khoangCachGanNhat = khoangCach;
                    ganNhat = mucTieu;
                }
            }
        }
        return khoangCachGanNhat <= 60 * 60 ? ganNhat : null;
    }

    short gocToiMucTieu(VXLChienBinh nguoiBan, VXLChienBinh mucTieu) {
        double radians = Math.atan2(nguoiBan.y - mucTieu.y, mucTieu.x - nguoiBan.x);
        int degrees = (int)Math.round(Math.toDegrees(radians));
        if (degrees < 0) {
            degrees += 360;
        }
        return (short)degrees;
    }

    short[] gioiHanDiChuyen(short xCu, short yCu, short xMoi, short yMoi, int khoangCachToiDa) {
        int xDich = Math.max(0, Math.min(this.banDo.getWidth(), xMoi));
        int yDich = Math.max(0, Math.min(this.banDo.getHeight(), yMoi));
        int dx = xDich - xCu;
        int dy = yDich - yCu;
        double khoangCach = Math.hypot(dx, dy);
        if (khoangCach > khoangCachToiDa && khoangCach > 0) {
            double tiLe = khoangCachToiDa / khoangCach;
            xDich = (int)Math.round(xCu + dx * tiLe);
            yDich = (int)Math.round(yCu + dy * tiLe);
        }
        return new short[]{(short)xDich, (short)yDich};
    }
}