package com.vxl.chien;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import com.vxl.vatpham.VXLVatPham;

public class VXLChienBinh {
    private static final int GIOI_HAN_CHI_SO = 30000;
    private static final int MA_THUOC_TINH_THOI_GIAN_NAP_DAN = 14;
    public static final int THOI_GIAN_NAP_DAN_TOI_THIEU = 250;
    public static final int THOI_GIAN_NAP_DAN_MAC_DINH = THOI_GIAN_NAP_DAN_TOI_THIEU;
    public static final int THOI_GIAN_NAP_DAN_TOI_DA = Short.MAX_VALUE;
    public final VXLNguoiChoi nguoiChoi;
    public final byte chiSo;
    public final boolean bot;
    public final boolean camTu;
    public final String ten;
    public final int ma;
    public short maVuKhi;
    public final byte avenger;
    public final byte avengerDan;
    public final VXLKyNangAvenger kyNangAvenger;
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
    public int luotMacTo;
    public int luotMaCaRong;
    public int vatPhamDanDacBiet = -1;
    public int heSoPhatBan = 100;
    public int heSoDiChuyen = 100;
    public int heSoTangNo = 100;
    public int luotNgungGio;
    public int luotNapNhanh;
    public int luotXuyenGiap;
    public int luotXuyenDiaHinh;
    public int heSoGoBom = 100;
    public int soPhatToiThieu = 1;
    private int heSoDiChuyenTrangBi = 100;
    public int no;
    public boolean daDungVatPhamTrongLuot;
    public boolean kyNangDacBiet;
    public boolean skillRiengPhatToi;
    public boolean chet;
    public boolean daRoiTran;
    public boolean daQuyetToan;
    public VXLChienBinh nguoiGaySatThuongCuoi;
    public VXLChienBinh nguonDoc;
    private long thoiDiemSanSangBan;

    public VXLChienBinh(VXLNguoiChoi nguoiChoi, byte chiSo, short x, short y) {
        nguoiChoi.dongBoTrangBiNhanVat();
        this.nguoiChoi = nguoiChoi;
        this.chiSo = chiSo;
        this.bot = false;
        this.camTu = false;
        this.ten = nguoiChoi.ten;
        this.ma = nguoiChoi.ma;
        this.maVuKhi = nguoiChoi.wp;
        this.avenger = nguoiChoi.avenger;
        this.avengerDan = nguoiChoi.layAvengerDan();
        this.kyNangAvenger = VXLKyNangAvenger.tao(this.avengerDan);
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
        long diChuyenPhanTram = 0;

        VXLVatPham[] itemBody = nguoiChoi.itemBody;
        if (itemBody != null) {
            for (VXLVatPham vatPham : itemBody) {
                if (vatPham == null || vatPham.mau == null || vatPham.HP <= 0) {
                    continue;
                }
                hpCong += vatPham.tongThamSoHieuLucTheoMa(0);
                tanCongCong += vatPham.tongThamSoHieuLucTheoMa(1);
                giapCong += vatPham.tongThamSoHieuLucTheoMa(2);
                hpPhanTram += vatPham.tongThamSoHieuLucTheoMa(6);
                tanCongPhanTram += vatPham.tongThamSoHieuLucTheoMa(7);
                giapPhanTram += vatPham.tongThamSoHieuLucTheoMa(8);
                tatCaPhanTram += vatPham.tongThamSoHieuLucTheoMa(18);
                diChuyenPhanTram += vatPham.tongThamSoHieuLucTheoMa(26);
                tatCaPhanTram += Math.max(0, vatPham.tongThamSoHieuLucTheoMa(17)) * 2;
            }
        }

        this.mauToiDa = gioiHan((hpGoc + hpCong) * (100L + hpPhanTram + tatCaPhanTram) / 100L, 100, GIOI_HAN_CHI_SO);
        this.tanCong = gioiHan((tanCongGoc + tanCongCong) * (100L + tanCongPhanTram + tatCaPhanTram) / 100L, 1, GIOI_HAN_CHI_SO);
        this.giap = gioiHan((giapGoc + giapCong) * (100L + giapPhanTram + tatCaPhanTram) / 100L, 0, GIOI_HAN_CHI_SO);
        this.heSoDiChuyenTrangBi = gioiHan(100L + diChuyenPhanTram, 100, 300);
        this.hp = this.mauToiDa;
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger) {
        this(chiSo, x, y, ten, maVuKhi, avenger, false);
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger, boolean camTu) {
        this(chiSo, x, y, ten, maVuKhi, avenger, camTu,
                camTu ? 140 : 220, camTu ? 45 : 35, camTu ? 8 : 15);
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger,
            int mauToiDa, int tanCong, int giap) {
        this(chiSo, x, y, ten, maVuKhi, avenger, false, mauToiDa, tanCong, giap);
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger,
            boolean camTu, int mauToiDa, int tanCong, int giap) {
        this.nguoiChoi = null;
        this.chiSo = chiSo;
        this.bot = true;
        this.camTu = camTu;
        this.ten = ten;
        this.ma = -9000 - chiSo;
        this.maVuKhi = maVuKhi;
        this.avenger = avenger;
        this.avengerDan = avenger;
        this.kyNangAvenger = VXLKyNangAvenger.tao(this.avengerDan);
        this.x = x;
        this.y = y;
        this.mauToiDa = gioiHan(mauToiDa, 1, GIOI_HAN_CHI_SO);
        this.tanCong = gioiHan(tanCong, 1, GIOI_HAN_CHI_SO);
        this.giap = gioiHan(giap, 0, GIOI_HAN_CHI_SO);
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
                tanCongCong += vatPham.tongThamSoHieuLucTheoMa(1);
                tanCongPhanTram += vatPham.tongThamSoHieuLucTheoMa(7);
                tatCaPhanTram += vatPham.tongThamSoHieuLucTheoMa(18);
                tatCaPhanTram += Math.max(0, vatPham.tongThamSoHieuLucTheoMa(17)) * 2L;
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

    public void ketThucPhatBan() {
        if (this.luotNapNhanh > 0) {
            this.luotNapNhanh--;
        }
        if (this.luotNgungGio > 0) {
            this.luotNgungGio--;
        }
        if (this.luotXuyenGiap > 0) {
            this.luotXuyenGiap--;
        }
        if (this.luotXuyenDiaHinh > 0) {
            this.luotXuyenDiaHinh--;
        }
        this.soPhatToiThieu = 1;
    }

    public int layThoiGianNapDan() {
        int thoiGianNap = layThoiGianNapDanTheoMaVuKhi(this.maVuKhi);
        if (this.nguoiChoi != null && this.nguoiChoi.itemBody != null
                && this.nguoiChoi.itemBody.length > 5) {
            VXLVatPham vuKhi = this.nguoiChoi.itemBody[5];
            if (vuKhi != null && vuKhi.mau != null && vuKhi.HP > 0) {
                int theoVatPham = layThamSoNapDan(vuKhi.itemOptions);
                int theoMau = layThamSoNapDan(vuKhi.mau.thuocTinhs);
                int theoCauHinh = theoVatPham > 0 ? theoVatPham : theoMau;
                thoiGianNap = theoCauHinh > 0
                        ? gioiHanNapDan(theoCauHinh) : THOI_GIAN_NAP_DAN_MAC_DINH;
            }
        }
        if (this.luotNapNhanh > 0) {
            thoiGianNap = gioiHanNapDan((long)thoiGianNap * 80L / 100L);
        }
        return thoiGianNap;
    }

    public static int layThoiGianNapDanTheoMaVuKhi(short maVuKhi) {
        if (VXLQuanLyMayChu.itemTemplates == null) {
            return THOI_GIAN_NAP_DAN_MAC_DINH;
        }
        for (VXLMauVatPham mau : VXLQuanLyMayChu.itemTemplates.values()) {
            if (mau == null || mau.loai != 5 || mau.part != maVuKhi) {
                continue;
            }
            int theoMau = layThamSoNapDan(mau.thuocTinhs);
            return theoMau > 0 ? gioiHanNapDan(theoMau) : THOI_GIAN_NAP_DAN_MAC_DINH;
        }
        return THOI_GIAN_NAP_DAN_MAC_DINH;
    }

    private static int layThamSoNapDan(java.util.Vector thuocTinhs) {
        if (thuocTinhs == null) {
            return -1;
        }
        for (Object giaTri : thuocTinhs) {
            if (giaTri instanceof VXLThuocTinhVatPham thuocTinh
                    && thuocTinh.optionTemplate != null
                    && thuocTinh.optionTemplate.ma == MA_THUOC_TINH_THOI_GIAN_NAP_DAN
                    && thuocTinh.thamSo > 0) {
                return thuocTinh.thamSo;
            }
        }
        return -1;
    }

    private static int gioiHanNapDan(long giaTri) {
        if (giaTri <= 0L) {
            return THOI_GIAN_NAP_DAN_MAC_DINH;
        }
        return (int)Math.max(THOI_GIAN_NAP_DAN_TOI_THIEU,
                Math.min(THOI_GIAN_NAP_DAN_TOI_DA, giaTri));
    }

    public int layTamDiChuyen(int tamCoBan) {
        long tam = (long)Math.max(0, tamCoBan) * this.heSoDiChuyenTrangBi
                * Math.max(100, this.heSoDiChuyen) / 10000L;
        return gioiHan(tam, 0, 2000);
    }

    public short layIconKyNangDacBiet() {
        if (this.nguoiChoi != null && this.nguoiChoi.itemBody != null
                && this.nguoiChoi.itemBody.length > 5) {
            VXLVatPham vuKhi = this.nguoiChoi.itemBody[5];
            if (vuKhi != null && vuKhi.mau != null) {
                return vuKhi.mau.iconID;
            }
        }
        return 0;
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
        if (this.bot || this.chet || giaTri <= 0
                || this.kyNangAvenger != null && this.kyNangAvenger.laSkillRieng()) {
            return false;
        }
        int truoc = this.no;
        int giaTriThuc = Math.max(1, giaTri * Math.max(100, this.heSoTangNo) / 100);
        this.no = Math.max(0, Math.min(100, this.no + giaTriThuc));
        return this.no != truoc;
    }

    public boolean kichHoatKyNangDacBiet() {
        if (this.bot || this.chet || this.kyNangDacBiet) {
            return false;
        }
        if (this.kyNangAvenger != null && this.kyNangAvenger.laSkillRieng()) {
            return false;
        }
        if (this.no < 100) {
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
