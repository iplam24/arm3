package com.vxl.chien;

import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLVatPham;

public class VXLChienBinh {
    private static final int GIOI_HAN_CHI_SO = 30000;
    private static final int MA_THUOC_TINH_THOI_GIAN_NAP_DAN = 14;
    private static final int THOI_GIAN_NAP_DAN_MAC_DINH = 300;
    public final VXLNguoiChoi nguoiChoi;
    public final byte chiSo;
    public final boolean bot;
    public final boolean camTu;
    public final String ten;
    public final int ma;
    public short maVuKhi;
    public final byte avenger;
    public int tanCong;
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
    public int no;
    public boolean daDungVatPhamTrongLuot;
    public boolean kyNangDacBiet;
    public boolean chet;
    public boolean daRoiTran;
    public boolean daQuyetToan;
    public VXLChienBinh nguoiGaySatThuongCuoi;
    public VXLChienBinh nguonDoc;
    private long thoiDiemSanSangBan;

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
        long hpCong = 0;
        long tanCongCong = 0;
        long giapCong = 0;
        long hpPhanTram = 0;
        long tanCongPhanTram = 0;
        long giapPhanTram = 0;
        long tatCaPhanTram = 0;

        VXLVatPham[] itemBody = nguoiChoi.itemBody;
        if (itemBody != null) {
            for (VXLVatPham vatPham : itemBody) {
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
        }

        this.mauToiDa = gioiHan((hpGoc + hpCong) * (100L + hpPhanTram + tatCaPhanTram) / 100L, 100, GIOI_HAN_CHI_SO);
        this.tanCong = gioiHan((tanCongGoc + tanCongCong) * (100L + tanCongPhanTram + tatCaPhanTram) / 100L, 1, GIOI_HAN_CHI_SO);
        this.giap = gioiHan((giapGoc + giapCong) * (100L + giapPhanTram + tatCaPhanTram) / 100L, 0, GIOI_HAN_CHI_SO);
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

    public void capNhatTanCongTheoTrangBi() {
        if (this.nguoiChoi == null || this.bot) {
            return;
        }
        int tanCongGoc = 20 + Math.max(0, layDiemCong(this.nguoiChoi, 1, 0));
        long tanCongCong = 0;
        long tanCongPhanTram = 0;
        long tatCaPhanTram = 0;
        VXLVatPham[] itemBody = this.nguoiChoi.itemBody;
        if (itemBody != null) {
            for (VXLVatPham vatPham : itemBody) {
                if (vatPham == null || vatPham.mau == null || vatPham.HP <= 0) {
                    continue;
                }
                tanCongCong += vatPham.tongThamSoTheoMa(1);
                tanCongPhanTram += vatPham.tongThamSoTheoMa(7);
                tatCaPhanTram += vatPham.tongThamSoTheoMa(18);
                tatCaPhanTram += Math.max(0, vatPham.tongThamSoTheoMa(17)) * 2L;
            }
        }
        this.tanCong = gioiHan((tanCongGoc + tanCongCong)
                * (100L + tanCongPhanTram + tatCaPhanTram) / 100L,
                1, GIOI_HAN_CHI_SO);
    }

    public int batDauNapDan() {
        int thoiGianNap = this.layThoiGianNapDan();
        this.thoiDiemSanSangBan = System.currentTimeMillis() + thoiGianNap;
        return thoiGianNap;
    }

    public int layThoiGianNapDan() {
        if (this.nguoiChoi != null && this.nguoiChoi.itemBody != null
                && this.nguoiChoi.itemBody.length > 5) {
            VXLVatPham vuKhi = this.nguoiChoi.itemBody[5];
            if (vuKhi != null && vuKhi.mau != null && vuKhi.HP > 0) {
                int thoiGianNap = vuKhi.getParamById(MA_THUOC_TINH_THOI_GIAN_NAP_DAN);
                if (thoiGianNap > 0) {
                    return Math.max(50, Math.min(5000, thoiGianNap));
                }
            }
        }
        return THOI_GIAN_NAP_DAN_MAC_DINH;
    }

    public boolean daNapDan() {
        return System.currentTimeMillis() >= this.thoiDiemSanSangBan;
    }

    public byte phanTramMau() {
        if (this.mauToiDa <= 0) {
            return 0;
        }
        return (byte)Math.max(0, Math.min(100, (long)this.hp * 100L / this.mauToiDa));
    }

    public boolean tangNo(int giaTri) {
        if (this.bot || this.chet || giaTri <= 0) {
            return false;
        }
        int truoc = this.no;
        this.no = Math.max(0, Math.min(100, this.no + giaTri));
        return this.no != truoc;
    }

    public boolean kichHoatKyNangDacBiet() {
        if (this.bot || this.chet || this.no < 100 || this.kyNangDacBiet) {
            return false;
        }
        this.no = 0;
        this.kyNangDacBiet = true;
        return true;
    }

    public int hoiMau(int soMau) {
        if (this.chet || soMau <= 0) {
            return 0;
        }
        int truoc = Math.max(0, Math.min(this.mauToiDa, this.hp));
        this.hp = (int)Math.min(this.mauToiDa, (long)truoc + soMau);
        return this.hp - truoc;
    }

    public int themKhien(int soKhien) {
        if (this.chet || soKhien <= 0) {
            return 0;
        }
        int gioiHanKhien = Math.max(1, this.mauToiDa / 2);
        int truoc = Math.max(0, Math.min(gioiHanKhien, this.khien));
        this.khien = (int)Math.min(gioiHanKhien, (long)truoc + soKhien);
        return this.khien - truoc;
    }

    private static int layDiemCong(VXLNguoiChoi nguoiChoi, int chiSo, int macDinh) {
        if (nguoiChoi.pointAdd == null || chiSo < 0 || chiSo >= nguoiChoi.pointAdd.length) {
            return macDinh;
        }
        return nguoiChoi.pointAdd[chiSo];
    }

    private static int gioiHan(long giaTri, int nhoNhat, int lonNhat) {
        return (int)Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }
}
