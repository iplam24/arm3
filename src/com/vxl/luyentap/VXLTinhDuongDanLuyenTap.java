package com.vxl.luyentap;

final class VXLTinhDuongDanLuyenTap {
    private static final int CHIEU_RONG_BAN_DO = 1200;
    private static final int CHIEU_CAO_BAN_DO = 700;
    private static final int BAN_KINH_TRUNG = 42;
    private final short[] botX;
    private final short[] botY;
    private final boolean[] botDaChet;

    VXLTinhDuongDanLuyenTap(short[] botX, short[] botY, boolean[] botDaChet) {
        this.botX = botX;
        this.botY = botY;
        this.botDaChet = botDaChet;
    }

    short[][] taoDuongDanCong(short batDauX, short batDauY, short goc, byte luc) {
        int soDiemToiDa = 36;
        short[] xs = new short[soDiemToiDa];
        short[] ys = new short[soDiemToiDa];
        double radian = Math.toRadians(goc);
        double tocDo = Math.max(8, luc) * 0.85D;
        double trongLuc = 0.33D;
        int doDai = 0;
        for (int i = 0; i < soDiemToiDa; i++) {
            double thoiGian = i;
            int x = (int)Math.round(batDauX + Math.cos(radian) * tocDo * thoiGian);
            int y = (int)Math.round(batDauY - Math.sin(radian) * tocDo * thoiGian + trongLuc * thoiGian * thoiGian);
            boolean raNgoaiBanDo = x < 0 || x > CHIEU_RONG_BAN_DO || y < 0 || y > CHIEU_CAO_BAN_DO;
            x = Math.max(0, Math.min(CHIEU_RONG_BAN_DO, x));
            y = Math.max(0, Math.min(CHIEU_CAO_BAN_DO, y));
            xs[i] = (short)x;
            ys[i] = (short)y;
            doDai = i + 1;
            if (raNgoaiBanDo || this.diemTrungBot(x, y)) {
                break;
            }
        }
        return this.catDuongDan(xs, ys, doDai);
    }

    short[][] taoDuongDanThang(short batDauX, short batDauY, short dichX, short dichY) {
        int dx = dichX - batDauX;
        int dy = dichY - batDauY;
        int soBuoc = Math.max(8, Math.min(24, Math.max(Math.abs(dx), Math.abs(dy)) / 24));
        short[] xs = new short[soBuoc];
        short[] ys = new short[soBuoc];
        for (int i = 0; i < soBuoc; i++) {
            double tiLe = (double)i / (double)(soBuoc - 1);
            xs[i] = (short)Math.round(batDauX + dx * tiLe);
            ys[i] = (short)Math.round(batDauY + dy * tiLe);
        }
        return new short[][]{xs, ys};
    }

    int timBotTrungDuong(short[] xs, short[] ys) {
        for (int i = 0; i < xs.length; i++) {
            int chiSoBot = this.timBotTrungDiem(xs[i], ys[i]);
            if (chiSoBot >= 0) {
                return chiSoBot;
            }
        }
        return -1;
    }

    boolean diemTrungBot(int x, int y) {
        return this.timBotTrungDiem(x, y) >= 0;
    }

    int timBotSongGanNhat(short nguoiChoiX, short nguoiChoiY) {
        int ganNhat = -1;
        int khoangCachGanNhat = Integer.MAX_VALUE;
        for (int i = 0; i < this.botX.length; i++) {
            if (this.botDaChet[i]) {
                continue;
            }
            int dx = this.botX[i] - nguoiChoiX;
            int dy = this.botY[i] - nguoiChoiY;
            int khoangCach = dx * dx + dy * dy;
            if (khoangCach < khoangCachGanNhat) {
                khoangCachGanNhat = khoangCach;
                ganNhat = i;
            }
        }
        return ganNhat;
    }

    short tinhGocToiMucTieu(short batDauX, short batDauY, short dichX, short dichY) {
        double radian = Math.atan2(batDauY - dichY, dichX - batDauX);
        int goc = (int)Math.round(Math.toDegrees(radian));
        if (goc < 0) {
            goc += 360;
        }
        return (short)goc;
    }

    boolean duongDanTrungNguoiChoi(short[] xs, short[] ys, short nguoiChoiX, short nguoiChoiY) {
        for (int i = 0; i < xs.length; i++) {
            int dx = xs[i] - nguoiChoiX;
            int dy = ys[i] - nguoiChoiY;
            if (dx * dx + dy * dy <= BAN_KINH_TRUNG * BAN_KINH_TRUNG) {
                return true;
            }
        }
        return false;
    }

    short gioiHan(short giaTri, int nhoNhat, int lonNhat) {
        int ketQua = giaTri;
        if (ketQua < nhoNhat) {
            ketQua = nhoNhat;
        }
        if (ketQua > lonNhat) {
            ketQua = lonNhat;
        }
        return (short)ketQua;
    }

    private int timBotTrungDiem(int x, int y) {
        for (int i = 0; i < this.botX.length; i++) {
            if (this.botDaChet[i]) {
                continue;
            }
            int dx = x - this.botX[i];
            int dy = y - this.botY[i];
            if (dx * dx + dy * dy <= BAN_KINH_TRUNG * BAN_KINH_TRUNG) {
                return i;
            }
        }
        return -1;
    }

    private short[][] catDuongDan(short[] xs, short[] ys, int doDai) {
        int soDiem = Math.max(1, Math.min(doDai, xs.length));
        short[] ketQuaX = new short[soDiem];
        short[] ketQuaY = new short[soDiem];
        System.arraycopy(xs, 0, ketQuaX, 0, soDiem);
        System.arraycopy(ys, 0, ketQuaY, 0, soDiem);
        return new short[][]{ketQuaX, ketQuaY};
    }
}