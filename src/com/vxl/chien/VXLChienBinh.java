package com.vxl.chien;

import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLVatPham;

public class VXLChienBinh {
    private static final int GIOI_HAN_CHI_SO = 30000;
    public final VXLNguoiChoi nguoiChoi;
    public final byte chiSo;
    public final boolean bot;
    public final boolean camTu;
    public final String ten;
    public final int ma;
    public final short maVuKhi;
    public final byte avenger;
    public final int tanCong;
    public final int giap;
    public short x;
    public short y;
    public int hp;
    public int mauToiDa;
    public int khien;
    public int tongSatThuong;
    public int haGucTrongTran;
    public int haCamTuTrongTran;
    public int luotDoc;
    public int satThuongDoc;
    public int luotDongBang;
    public int luotMu;
    public int luotVoHinh;
    public int luotMaCaRong;
    public int vatPhamDanDacBiet = -1;
    public int heSoPhatBan = 100;
    public int heSoDiChuyen = 100;
    public boolean daDungVatPhamTrongLuot;
    public boolean chet;
    public boolean daRoiTran;
    public boolean daQuyetToan;
    public VXLChienBinh nguoiGaySatThuongCuoi;
    public VXLChienBinh nguonDoc;

    public VXLChienBinh(VXLNguoiChoi nguoiChoi, byte chiSo, short x, short y) {
        this.nguoiChoi = nguoiChoi;
        this.chiSo = chiSo;
        this.bot = false;
        this.camTu = false;
        this.ten = nguoiChoi.ten;
        this.ma = nguoiChoi.ma;
        this.maVuKhi = nguoiChoi.wp;
        this.avenger = nguoiChoi.avenger;
        this.x = x;
        this.y = y;

        int hpGoc = Math.max(100, layDiemCong(nguoiChoi, 0, 100));
        int tanCongGoc = 20 + Math.max(0, layDiemCong(nguoiChoi, 1, 0));
        int giapGoc = Math.max(0, layDiemCong(nguoiChoi, 2, 0));
        int hpCong = 0;
        int tanCongCong = 0;
        int giapCong = 0;
        int hpPhanTram = 0;
        int tanCongPhanTram = 0;
        int giapPhanTram = 0;
        int tatCaPhanTram = 0;

        for (VXLVatPham vatPham : nguoiChoi.itemBody) {
            if (vatPham == null || vatPham.mau == null || vatPham.HP <= 0) {
                continue;
            }
            hpCong += vatPham.tongThamSoTheoMa(0);
            tanCongCong += vatPham.tongThamSoTheoMa(1);
            giapCong += vatPham.tongThamSoTheoMa(2);
            hpPhanTram += vatPham.tongThamSoTheoMa(6);
            tanCongPhanTram += vatPham.tongThamSoTheoMa(7);
            giapPhanTram += vatPham.tongThamSoTheoMa(8);
            tatCaPhanTram += vatPham.tongThamSoTheoMa(18);
            tatCaPhanTram += Math.max(0, vatPham.tongThamSoTheoMa(17)) * 2;
        }

        this.mauToiDa = gioiHan((hpGoc + hpCong) * (100 + hpPhanTram + tatCaPhanTram) / 100, 100, GIOI_HAN_CHI_SO);
        this.tanCong = gioiHan((tanCongGoc + tanCongCong) * (100 + tanCongPhanTram + tatCaPhanTram) / 100, 1, GIOI_HAN_CHI_SO);
        this.giap = gioiHan((giapGoc + giapCong) * (100 + giapPhanTram + tatCaPhanTram) / 100, 0, GIOI_HAN_CHI_SO);
        this.hp = this.mauToiDa;
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger) {
        this(chiSo, x, y, ten, maVuKhi, avenger, false);
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger, boolean camTu) {
        this.nguoiChoi = null;
        this.chiSo = chiSo;
        this.bot = true;
        this.camTu = camTu;
        this.ten = ten;
        this.ma = -9000 - chiSo;
        this.maVuKhi = maVuKhi;
        this.avenger = avenger;
        this.x = x;
        this.y = y;
        this.mauToiDa = camTu ? 140 : 220;
        this.tanCong = camTu ? 45 : 35;
        this.giap = camTu ? 8 : 15;
        this.hp = this.mauToiDa;
    }

    public boolean coPhien() {
        return this.nguoiChoi != null && this.nguoiChoi.dichVu != null;
    }

    public byte phanTramMau() {
        if (this.mauToiDa <= 0) {
            return 0;
        }
        return (byte)Math.max(0, Math.min(100, this.hp * 100 / this.mauToiDa));
    }

    public int hoiMau(int soMau) {
        if (this.chet || soMau <= 0) {
            return 0;
        }
        int truoc = this.hp;
        this.hp = Math.min(this.mauToiDa, this.hp + soMau);
        return this.hp - truoc;
    }

    public int themKhien(int soKhien) {
        if (this.chet || soKhien <= 0) {
            return 0;
        }
        int gioiHanKhien = Math.max(1, this.mauToiDa / 2);
        int truoc = this.khien;
        this.khien = Math.min(gioiHanKhien, this.khien + soKhien);
        return this.khien - truoc;
    }

    private static int layDiemCong(VXLNguoiChoi nguoiChoi, int chiSo, int macDinh) {
        if (nguoiChoi.pointAdd == null || chiSo < 0 || chiSo >= nguoiChoi.pointAdd.length) {
            return macDinh;
        }
        return nguoiChoi.pointAdd[chiSo];
    }

    private static int gioiHan(int giaTri, int nhoNhat, int lonNhat) {
        return Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }
}