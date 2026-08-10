package com.vxl.luyentap;

import com.vxl.bando.VXLQuanLyBanDo;

final class VXLTinhDuongDanLuyenTap {
    private static final int CHIEU_RONG_NHAN_VAT = 20;
    private static final int CHIEU_CAO_NHAN_VAT = 35;
    private static final int LE_TRUNG_MAC_DINH = 5;
    private static final int SO_DIEM_TOI_DA = 360;
    private static final double BUOC_THOI_GIAN_DAN_BOT = 0.45D;
    private static final double BUOC_THOI_GIAN_DAN_NGUOI_CHOI = 0.8D;
    private static final double HE_SO_TOC_DO = 0.85D;
    private static final double TRONG_LUC = 0.33D;
    private static final double KHOANG_CACH_DAU_SUNG = 15D;
    private static final double DO_CAO_DAU_SUNG = 12D;
    private static final byte LUC_TOI_THIEU = 12;
    private static final byte LUC_TOI_DA = 30;
    private final short[] botX;
    private final short[] botY;
    private final boolean[] botDaChet;
    private final VXLQuanLyBanDo banDo;

    VXLTinhDuongDanLuyenTap(short[] botX, short[] botY, boolean[] botDaChet) {
        this.botX = botX;
        this.botY = botY;
        this.botDaChet = botDaChet;
        this.banDo = new VXLQuanLyBanDo(0);
    }

    public VXLQuanLyBanDo layBanDo() {
        return this.banDo;
    }

    short[][] taoDuongDanCong(short batDauX, short batDauY, short goc, byte luc, byte loaiDan) {
        return this.taoDuongDanCong(batDauX, batDauY, goc, luc, loaiDan, -1,
                BUOC_THOI_GIAN_DAN_NGUOI_CHOI);
    }

    short[][] taoDuongDanCong(short batDauX, short batDauY, short goc, byte luc, byte loaiDan,
            int botBoQua) {
        return this.taoDuongDanCong(batDauX, batDauY, goc, luc, loaiDan, botBoQua,
                BUOC_THOI_GIAN_DAN_BOT);
    }

    private short[][] taoDuongDanCong(short batDauX, short batDauY, short goc, byte luc, byte loaiDan,
            int botBoQua, double buocThoiGian) {
        int soDiemToiDa = Math.max(1, Math.min(SO_DIEM_TOI_DA,
                (int)Math.ceil(SO_DIEM_TOI_DA * BUOC_THOI_GIAN_DAN_BOT / buocThoiGian)));
        short[] xs = new short[soDiemToiDa];
        short[] ys = new short[soDiemToiDa];
        double radian = Math.toRadians(goc);
        double tocDo = Math.max(8, Byte.toUnsignedInt(luc)) * HE_SO_TOC_DO;
        double trongLuc = loaiDan == 80 ? 0D : TRONG_LUC;
        boolean xuyenDiaHinh = loaiDan == 80;
        double dauSungX = batDauX + Math.cos(radian) * KHOANG_CACH_DAU_SUNG;
        double dauSungY = batDauY - DO_CAO_DAU_SUNG - Math.sin(radian) * KHOANG_CACH_DAU_SUNG;
        int chieuRongBanDo = this.banDo.getWidth();
        int chieuCaoBanDo = this.banDo.getHeight();
        int xTruoc = (int)Math.round(dauSungX);
        int yTruoc = (int)Math.round(dauSungY);
        int doDai = 0;
        for (int i = 0; i < soDiemToiDa; i++) {
            double thoiGian = i * buocThoiGian;
            int x = (int)Math.round(dauSungX + Math.cos(radian) * tocDo * thoiGian);
            int y = (int)Math.round(dauSungY - Math.sin(radian) * tocDo * thoiGian
                    + trongLuc * thoiGian * thoiGian);
            boolean raNgoaiBanDo = x < 0 || x >= chieuRongBanDo || y < 0 || y >= chieuCaoBanDo;
            x = Math.max(0, Math.min(chieuRongBanDo - 1, x));
            y = Math.max(0, Math.min(chieuCaoBanDo - 1, y));
            short[] diemDung = this.timDiemDungTrenDoan(xTruoc, yTruoc, x, y, xuyenDiaHinh, botBoQua);
            if (diemDung != null) {
                x = diemDung[0];
                y = diemDung[1];
            }
            xs[i] = (short)x;
            ys[i] = (short)y;
            doDai = i + 1;
            if (raNgoaiBanDo || diemDung != null) {
                break;
            }
            xTruoc = x;
            yTruoc = y;
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

    private boolean coVaChamTheoNguon(int x, int y) {
        if (y >= this.banDo.getHeight()) {
            return true;
        }
        if (x < 0 || x >= this.banDo.getWidth() || y < 0) {
            return false;
        }
        return this.banDo.coVaCham((short)x, (short)y);
    }

    int timBotTrungDuong(short[] xs, short[] ys) {
        return this.timBotTrungDuong(xs, ys, LE_TRUNG_MAC_DINH);
    }

    int timBotTrungDuong(short[] xs, short[] ys, int leTrung) {
        if (xs == null || ys == null) {
            return -1;
        }
        int soDiem = Math.min(xs.length, ys.length);
        for (int i = 0; i < soDiem; i++) {
            int xTruoc = i == 0 ? xs[i] : xs[i - 1];
            int yTruoc = i == 0 ? ys[i] : ys[i - 1];
            int dx = xs[i] - xTruoc;
            int dy = ys[i] - yTruoc;
            int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
            for (int buoc = 0; buoc <= soBuoc; buoc++) {
                int x = xTruoc + dx * buoc / soBuoc;
                int y = yTruoc + dy * buoc / soBuoc;
                int chiSoBot = this.timBotTrungDiem(x, y, leTrung, -1);
                if (chiSoBot >= 0) {
                    return chiSoBot;
                }
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
        if (xs == null || ys == null) {
            return false;
        }
        int soDiem = Math.min(xs.length, ys.length);
        for (int i = 0; i < soDiem; i++) {
            int xTruoc = i == 0 ? xs[i] : xs[i - 1];
            int yTruoc = i == 0 ? ys[i] : ys[i - 1];
            int dx = xs[i] - xTruoc;
            int dy = ys[i] - yTruoc;
            int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
            for (int buoc = 0; buoc <= soBuoc; buoc++) {
                int x = xTruoc + dx * buoc / soBuoc;
                int y = yTruoc + dy * buoc / soBuoc;
                if (this.diemTrungNhanVat(x, y, nguoiChoiX, nguoiChoiY, LE_TRUNG_MAC_DINH)) {
                    return true;
                }
            }
        }
        return false;
    }

    short[][] dungDuongDanTaiNguoiChoi(short[] xs, short[] ys, short nguoiChoiX, short nguoiChoiY) {
        if (xs == null || ys == null) {
            return new short[][]{new short[0], new short[0]};
        }
        int soDiem = Math.min(xs.length, ys.length);
        for (int i = 0; i < soDiem; i++) {
            int xTruoc = i == 0 ? xs[i] : xs[i - 1];
            int yTruoc = i == 0 ? ys[i] : ys[i - 1];
            int dx = xs[i] - xTruoc;
            int dy = ys[i] - yTruoc;
            int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
            for (int buoc = 0; buoc <= soBuoc; buoc++) {
                int x = xTruoc + dx * buoc / soBuoc;
                int y = yTruoc + dy * buoc / soBuoc;
                if (!this.diemTrungNhanVat(x, y, nguoiChoiX, nguoiChoiY, LE_TRUNG_MAC_DINH)) {
                    continue;
                }
                short[][] ketQua = this.catDuongDan(xs, ys, i + 1);
                ketQua[0][i] = (short)x;
                ketQua[1][i] = (short)y;
                return ketQua;
            }
        }
        return this.catDuongDan(xs, ys, soDiem);
    }

    byte lucCanThietToiMucTieu(short batDauX, short batDauY, short dichX, short dichY) {
        double khoangCach = Math.hypot(dichX - batDauX, dichY - batDauY);
        double lucLyThuyet = Math.sqrt(2D * TRONG_LUC * Math.max(1D, khoangCach)) / HE_SO_TOC_DO;
        int luc = (int)Math.ceil(lucLyThuyet) + 2;
        return (byte)Math.max(LUC_TOI_THIEU, Math.min(LUC_TOI_DA, luc));
    }

    short gocDanDaoToiMucTieu(short batDauX, short batDauY, short dichX, short dichY, byte luc) {
        return this.tinhGocDanDaoToiMucTieu(batDauX, batDauY, dichX, dichY, luc, false);
    }

    short gocDanDaoCaoToiMucTieu(short batDauX, short batDauY, short dichX, short dichY, byte luc) {
        return this.tinhGocDanDaoToiMucTieu(batDauX, batDauY, dichX, dichY, luc, true);
    }

    private short tinhGocDanDaoToiMucTieu(short batDauX, short batDauY, short dichX, short dichY,
            byte luc, boolean quyDaoCao) {
        double tocDo = Math.max(8, Byte.toUnsignedInt(luc)) * HE_SO_TOC_DO;
        double goc = Math.toRadians(this.tinhGocToiMucTieu(batDauX, batDauY, dichX, dichY));
        double xDauSung = batDauX + Math.cos(goc) * KHOANG_CACH_DAU_SUNG;
        double yDauSung = batDauY - DO_CAO_DAU_SUNG - Math.sin(goc) * KHOANG_CACH_DAU_SUNG;
        for (int lan = 0; lan < 2; lan++) {
            double dx = dichX - xDauSung;
            double dy = dichY - yDauSung;
            double khoangCachNgang = Math.abs(dx);
            if (khoangCachNgang < 1D) {
                return this.tinhGocToiMucTieu(batDauX, batDauY, dichX, dichY);
            }
            double heSoTrongLuc = TRONG_LUC * khoangCachNgang * khoangCachNgang / (tocDo * tocDo);
            double dinhThuc = khoangCachNgang * khoangCachNgang
                    - 4D * heSoTrongLuc * (heSoTrongLuc - dy);
            if (dinhThuc < 0D || heSoTrongLuc < 1.0E-9D) {
                int gocCaoMacDinh = dx >= 0D ? 65 : 115;
                return quyDaoCao ? (short)gocCaoMacDinh
                        : this.tinhGocToiMucTieu(batDauX, batDauY, dichX, dichY);
            }
            double canDinhThuc = Math.sqrt(dinhThuc);
            double tanGoc = (khoangCachNgang + (quyDaoCao ? canDinhThuc : -canDinhThuc))
                    / (2D * heSoTrongLuc);
            double gocNgang = Math.atan(tanGoc);
            goc = dx >= 0D ? gocNgang : Math.PI - gocNgang;
            xDauSung = batDauX + Math.cos(goc) * KHOANG_CACH_DAU_SUNG;
            yDauSung = batDauY - DO_CAO_DAU_SUNG - Math.sin(goc) * KHOANG_CACH_DAU_SUNG;
        }
        int degrees = (int)Math.round(Math.toDegrees(goc));
        return (short)((degrees % 360 + 360) % 360);
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
        return this.timBotTrungDiem(x, y, LE_TRUNG_MAC_DINH, -1);
    }

    private int timBotTrungDiem(int x, int y, int leTrung, int botBoQua) {
        for (int i = 0; i < this.botX.length; i++) {
            if (this.botDaChet[i] || i == botBoQua) {
                continue;
            }
            if (this.diemTrungNhanVat(x, y, this.botX[i], this.botY[i], leTrung)) {
                return i;
            }
        }
        return -1;
    }

    private short[] timDiemDungTrenDoan(int x1, int y1, int x2, int y2, boolean xuyenDiaHinh,
            int botBoQua) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
        for (int buoc = 0; buoc <= soBuoc; buoc++) {
            int x = x1 + dx * buoc / soBuoc;
            int y = y1 + dy * buoc / soBuoc;
            if (!xuyenDiaHinh && this.coVaChamTheoNguon(x, y)) {
                return new short[]{(short)x, (short)y};
            }
            if (this.timBotTrungDiem(x, y, LE_TRUNG_MAC_DINH, botBoQua) >= 0) {
                return new short[]{(short)x, (short)y};
            }
        }
        return null;
    }

    private boolean diemTrungNhanVat(int x, int y, short nhanVatX, short nhanVatY, int leTrung) {
        int nuaRong = CHIEU_RONG_NHAN_VAT / 2 + Math.max(0, leTrung);
        int le = Math.max(0, leTrung);
        return x >= nhanVatX - nuaRong && x < nhanVatX + nuaRong
                && y >= nhanVatY - CHIEU_CAO_NHAN_VAT - le && y < nhanVatY + le;
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
