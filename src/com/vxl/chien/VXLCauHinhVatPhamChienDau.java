package com.vxl.chien;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLMauVatPham;

public final class VXLCauHinhVatPhamChienDau {
    private static final int DO_CAO_SIEU_CAO_TOI_THIEU = 180;
    private static final int HE_SO_SAT_THUONG_SIEU_CAO = 125;
    private static final int HE_SO_SAT_THUONG_KY_NANG_DAC_BIET = 150;
    private static final VXLHoSoDan.VatLy VAT_LY_NANG_IT_GIO = vatLy(2.15D, 0.60D, 0.16D);
    private static final VXLHoSoDan.VatLy VAT_LY_THANG_IT_ROI = vatLy(1.80D, 0.25D, 0.10D);
    private static final VXLHoSoDan.VatLy VAT_LY_NHE_TAN_XA = vatLy(0.65D, 0.55D, 0.45D);
    private static final VXLHoSoDan.VatLy VAT_LY_NHE_CONG = vatLy(1.40D, 0.82D, 0.55D);
    private static final VXLHoSoDan.VatLy VAT_LY_TRUNG_BINH_CONG = vatLy(1.35D, 1D, 1D);
    private static final VXLHoSoDan.VatLy VAT_LY_LIEN_THANH = vatLy(0.55D, 1D, 1D);
    private static final VXLHoSoDan.VatLy VAT_LY_SINH_VAT = vatLy(1.20D, 0.90D, 0.65D);
    private static final VXLHoSoDan.VatLy VAT_LY_QUAY_VE = vatLy(0.95D, 0.20D, 0.06D);
    private static final VXLHoSoDan.VatLy VAT_LY_TARZAN = vatLy(0.72D, 0.90D, 0.10D);
    private static final VXLHoSoDan.VatLy VAT_LY_DAN_TACH = vatLy(0.95D, 1D, 1D);
    private static final VXLHoSoDan.VatLy VAT_LY_TUC_THOI = vatLy(0D, 0D, 0D);
    private static final VXLHoSoDan.VatLy VAT_LY_NANG_PARABOL_NHE = vatLy(1.80D, 0.70D, 0.15D);
    private static final VXLHoSoDan.VatLy VAT_LY_CUC_NANG = vatLy(2.20D, 1D, 0.50D);
    private static final VXLHoSoDan.VatLy VAT_LY_TRUNG_BINH_NHAY_GIO = vatLy(1.10D, 0.85D, 0.60D);
    private static final VXLHoSoDan.VatLy VAT_LY_QUAY_VE_IT_GIO = vatLy(1.60D, 0.20D, 0.25D);
    private static final VXLHoSoDan.VatLy VAT_LY_XUYEN_NANG = vatLy(2D, 0.65D, 0.25D);
    private static final VXLHoSoDan.VatLy VAT_LY_TEN_NHE = vatLy(0.80D, 1.10D, 0.80D);
    private static final VXLHoSoDan.Tarzan VONG_TARZAN = tarzan(1D, 2D, 90);
    private static final VXLHoSoDan.QuayVe QUAY_VE_CAPTAIN = quayVe(
            80D, 6.5D, 15D, 1.18D, 30D, 18);

    public record DiemSieuCao(boolean kichHoat, short x, short y) {
    }

    private static final VXLHoSoDan DAN_AT = new VXLHoSoDan("AT", (byte)0,
            VXLHoSoDan.KieuBan.DAN_DON, 1, 1, 0D,
            VAT_LY_NANG_IT_GIO, false, false, 100, 100);
    private static final VXLHoSoDan DAN_K98 = new VXLHoSoDan("K98", (byte)1,
            VXLHoSoDan.KieuBan.DAN_KEP, 2, 6, 0D,
            VAT_LY_THANG_IT_ROI, false, false, 65, 130);
    private static final VXLHoSoDan DAN_HOA_CAI = new VXLHoSoDan("Hoa cai", (byte)2,
            VXLHoSoDan.KieuBan.DAN_CHUM, 3, 7, 4D,
            VAT_LY_NHE_TAN_XA, false, false, 48, 145);
    private static final VXLHoSoDan DAN_CHUOI = new VXLHoSoDan("Chuoi", (byte)9,
            VXLHoSoDan.KieuBan.DAN_CHUM, 4, 4, 3.5D,
            VAT_LY_NHE_CONG, false, false, 50, 200);
    private static final VXLHoSoDan DAN_COI = new VXLHoSoDan("Coi", (byte)10,
            VXLHoSoDan.KieuBan.LIEN_THANH, 3, 3, 0D,
            VAT_LY_TRUNG_BINH_CONG, false, false, 55, 165);
    private static final VXLHoSoDan DAN_MG = new VXLHoSoDan("MG", (byte)11,
            VXLHoSoDan.KieuBan.LIEN_THANH, 5, 5, 1D,
            VAT_LY_LIEN_THANH, false, false, 35, 175);
    private static final VXLHoSoDan DAN_GA = new VXLHoSoDan("Ga", (byte)19,
            VXLHoSoDan.KieuBan.DAN_TACH, 2, 2, 0D,
            VAT_LY_SINH_VAT, false, false, 50, 100);
    private static final VXLHoSoDan DAN_TARZAN = new VXLHoSoDan("Tarzan", (byte)21,
            VXLHoSoDan.KieuBan.VONG_TARZAN, 1, 1, 0D,
            VAT_LY_TARZAN, false, true, 100, 100, null, VONG_TARZAN);
    private static final VXLHoSoDan DAN_APACHE = new VXLHoSoDan("Apache", (byte)17,
            VXLHoSoDan.KieuBan.DAN_TACH, 4, 4, 0D,
            VAT_LY_DAN_TACH, false, false, 100, 300);
    private static final VXLHoSoDan DAN_LASER = new VXLHoSoDan("Laser", (byte)49,
            VXLHoSoDan.KieuBan.LASER, 1, 1, 0D,
            VAT_LY_TUC_THOI, false, false, 100, 100);
    private static final VXLHoSoDan DAN_IRON_MAN = new VXLHoSoDan("Iron Man", (byte)1,
            VXLHoSoDan.KieuBan.DAN_CHUM, 2, 2, 5D,
            VAT_LY_NANG_PARABOL_NHE, false, false, 60, 120);
    private static final VXLHoSoDan DAN_HULK = new VXLHoSoDan("Hulk", (byte)0,
            VXLHoSoDan.KieuBan.NHAN_VAT_LAO, 1, 1, 0D,
            VAT_LY_TUC_THOI, false, false, 100, 100);
    private static final VXLHoSoDan DAN_THOR = new VXLHoSoDan("Thor", (byte)82,
            VXLHoSoDan.KieuBan.DAN_DON, 1, 1, 0D,
            VAT_LY_CUC_NANG, false, false, 100, 100);
    private static final VXLHoSoDan DAN_LOKI = new VXLHoSoDan("Loki", (byte)33,
            VXLHoSoDan.KieuBan.DAN_CHUM, 2, 2, 4D,
            VAT_LY_TRUNG_BINH_NHAY_GIO, false, false, 60, 120);
    private static final VXLHoSoDan DAN_CAPTAIN = new VXLHoSoDan("Captain", (byte)21,
            VXLHoSoDan.KieuBan.VONG_TARZAN, 1, 1, 0D,
            VAT_LY_TARZAN, false, true, 100, 200, null, VONG_TARZAN);
    private static final VXLHoSoDan DAN_WINTER_SOLDIER = new VXLHoSoDan("Winter Soldier", (byte)80,
            VXLHoSoDan.KieuBan.DAN_KEP, 2, 2, 3D,
            VAT_LY_XUYEN_NANG, true, false, 60, 120);
    private static final VXLHoSoDan DAN_HAWKEYE = new VXLHoSoDan("Hawkeye", (byte)79,
            VXLHoSoDan.KieuBan.DAN_CHUM, 4, 4, 3D,
            VAT_LY_TEN_NHE, false, false, 35, 140);
    private static final VXLHoSoDan DAN_ULTRON = new VXLHoSoDan("Ultron", (byte)49,
            VXLHoSoDan.KieuBan.LASER, 1, 1, 0D,
            VAT_LY_TUC_THOI, false, false, 100, 100);
    private VXLCauHinhVatPhamChienDau() {
    }

    public static int layHeSoSatThuong(int maVatPham) {
        return switch (maVatPham) {
            case 222 -> 175;
            case 227, 231, 237 -> 130;
            case 228, 235, 239 -> 140;
            case 238, 240, 241, 245 -> 155;
            case 243, 244, 247, 248, 249, 250 -> 90;
            default -> 100;
        };
    }

    public static byte layLoaiDan(int maVatPham, byte loaiDanMacDinh) {
        if (maVatPham < 0) {
            return chuanHoaLoaiDan(loaiDanMacDinh);
        }
        if (VXLQuanLyMayChu.itemTemplates == null) {
            return chuanHoaLoaiDan(loaiDanMacDinh);
        }
        VXLMauVatPham mau = VXLQuanLyMayChu.itemTemplates.get(maVatPham);
        return mau != null ? chuyenLoaiDanCauHinhSangClient(mau.gioiTinh)
                : chuanHoaLoaiDan(loaiDanMacDinh);
    }

    public static byte layLoaiDanTheoVuKhi(int maVuKhi, byte loaiDanMacDinh) {
        return switch (maVuKhi) {
            case 5, 31, 57, 134, 135 -> 0;
            case 27, 37, 132, 133, 156 -> 1;
            case 123, 124, 125, 126, 127 -> 2;
            case 32, 58, 140, 141, 142 -> 9;
            case 30, 56, 146, 147, 148 -> 10;
            case 28, 54, 143, 144, 145 -> 11;
            case 29, 55, 153, 154, 155 -> 19;
            case 120, 136, 137, 138, 139 -> 21;
            case 121, 128, 129, 130, 131 -> 17;
            case 122, 149, 150, 151, 152 -> 49;
            default -> chuanHoaLoaiDan(loaiDanMacDinh);
        };
    }

    public static byte layNhomSungClientTheoVuKhi(int maVuKhi) {
        return switch (Byte.toUnsignedInt(layLoaiDanTheoVuKhi(maVuKhi, (byte)0))) {
            case 1 -> 1;
            case 2 -> 2;
            case 9 -> 3;
            case 10 -> 4;
            case 11 -> 5;
            case 19 -> 6;
            case 21 -> 7;
            case 17 -> 8;
            case 49 -> 9;
            default -> 0;
        };
    }

    public static short layMaAnhDanMacDinh(byte loaiDan) {
        return switch (chuanHoaLoaiDan(loaiDan)) {
            case 1 -> 10;
            case 2 -> 24;
            case 9 -> 19;
            case 10 -> 28;
            case 11 -> 73;
            case 17 -> 55;
            case 19 -> 37;
            case 21 -> 46;
            case 49 -> 64;
            default -> 1;
        };
    }

    public static short layMaAnhDanTheoVuKhi(int maVuKhi) {
        return switch (maVuKhi) {
            case 57 -> 1;
            case 31 -> 2;
            case 5 -> 3;
            case 134 -> 4;
            case 135 -> 5;
            case 27 -> 10;
            case 37 -> 11;
            case 156 -> 12;
            case 132 -> 13;
            case 133 -> 14;
            case 58 -> 19;
            case 140 -> 20;
            case 32 -> 21;
            case 141 -> 22;
            case 142 -> 23;
            case 123 -> 24;
            case 124 -> 25;
            case 125 -> 26;
            case 126 -> 27;
            case 127 -> 27;
            case 56 -> 28;
            case 30 -> 29;
            case 146 -> 30;
            case 147 -> 31;
            case 148 -> 32;
            case 55 -> 37;
            case 29 -> 38;
            case 153 -> 39;
            case 154 -> 40;
            case 155 -> 41;
            case 120 -> 46;
            case 136 -> 47;
            case 137 -> 48;
            case 138 -> 49;
            case 139 -> 50;
            case 121 -> 55;
            case 128 -> 56;
            case 129 -> 57;
            case 130 -> 58;
            case 131 -> 59;
            case 122 -> 64;
            case 149 -> 65;
            case 150 -> 66;
            case 151 -> 67;
            case 152 -> 68;
            case 54 -> 73;
            case 28 -> 74;
            case 143 -> 75;
            case 145 -> 76;
            case 144 -> 77;
            default -> layMaAnhDanMacDinh(layLoaiDanTheoVuKhi(maVuKhi, (byte)0));
        };
    }

    public static int laySoQuyDao(byte loaiDan, byte chiMang) {
        return laySoQuyDao(loaiDan, chiMang, (byte)0);
    }

    public static int laySoQuyDao(byte loaiDan, byte chiMang, byte avenger) {
        return layHoSoDan(loaiDan, avenger).laySoVien(chiMang);
    }

    public static double[] layDoLechGoc(byte loaiDan, byte chiMang) {
        return layDoLechGoc(loaiDan, chiMang, (byte)0);
    }

    public static double[] layDoLechGoc(byte loaiDan, byte chiMang, byte avenger) {
        return layHoSoDan(loaiDan, avenger).taoDoLechGoc(chiMang);
    }

    public static int layPhanTramSatThuongMoiVien(byte loaiDan) {
        return layPhanTramSatThuongMoiVien(loaiDan, (byte)0);
    }

    public static int layPhanTramSatThuongMoiVien(byte loaiDan, byte avenger) {
        int soVien = layHoSoDan(loaiDan, avenger).laySoVienGaySatThuong((byte)0);
        return Math.max(1, (100 + soVien - 1) / soVien);
    }

    public static int layTranPhanTramSatThuong(byte loaiDan) {
        return layTranPhanTramSatThuong(loaiDan, (byte)0);
    }

    public static int layTranPhanTramSatThuong(byte loaiDan, byte avenger) {
        return Byte.toUnsignedInt(avenger) == 5 ? 200 : 100;
    }

    public static int laySoVienGaySatThuong(byte loaiDan, byte chiMang, byte avenger) {
        return layHoSoDan(loaiDan, avenger).laySoVienGaySatThuong(chiMang);
    }

    public static int tinhSatThuongMoiVien(int tongSatThuong, byte loaiDan,
            byte chiMang, byte avenger) {
        return layHoSoDan(loaiDan, avenger).tinhSatThuongMoiVien(tongSatThuong, chiMang);
    }

    public static int layBanKinhNo(byte loaiDan, byte avenger) {
        VXLHoSoDan hoSoDan = layHoSoDan(loaiDan, avenger);
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.LASER
                || hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.NHAN_VAT_LAO) {
            return 0;
        }
        return switch (Byte.toUnsignedInt(hoSoDan.loaiClient())) {
            case 0 -> 46;
            case 1 -> 24;
            case 2 -> 22;
            case 9 -> 28;
            case 10 -> 38;
            case 11 -> 20;
            case 17 -> 28;
            case 19 -> 38;
            case 21 -> 10;
            case 33, 79, 80, 82, 83 -> 30;
            default -> 32;
        };
    }

    public static int tinhSatThuongNoTaiViTri(short[][] cacDuongX, short[][] cacDuongY,
            short mucTieuX, short mucTieuY, byte loaiDan, byte avenger,
            int satThuongMoiVien, int tranSatThuong) {
        if (Byte.toUnsignedInt(loaiDan) == 21 && (avenger == 0 || avenger == 5)) {
            return 0;
        }
        int banKinh = layBanKinhNo(loaiDan, avenger);
        if (banKinh <= 0 || satThuongMoiVien <= 0 || tranSatThuong <= 0
                || cacDuongX == null || cacDuongY == null) {
            return 0;
        }
        int tongSatThuong = 0;
        int soQuyDao = Math.min(cacDuongX.length, cacDuongY.length);
        for (int i = 0; i < soQuyDao && tongSatThuong < tranSatThuong; i++) {
            if (Byte.toUnsignedInt(loaiDan) == 17 && soQuyDao > 1 && i == 0) {
                continue;
            }
            short[] duongX = cacDuongX[i];
            short[] duongY = cacDuongY[i];
            if (duongX == null || duongY == null || duongX.length == 0 || duongY.length == 0) {
                continue;
            }
            int chiSoCuoi = Math.min(duongX.length, duongY.length) - 1;
            double khoangCach = Math.hypot(duongX[chiSoCuoi] - mucTieuX,
                    duongY[chiSoCuoi] - (mucTieuY - 18));
            if (khoangCach > banKinh) {
                continue;
            }
            int phanTram = Math.max(25, 100 - (int)Math.round(khoangCach * 75D / banKinh));
            int satThuong = Math.max(1, satThuongMoiVien * phanTram / 100);
            tongSatThuong += Math.min(satThuong, tranSatThuong - tongSatThuong);
        }
        return tongSatThuong;
    }

    public static DiemSieuCao timDiemSieuCao(short batDauY, short[][] cacDuongX,
            short[][] cacDuongY, byte loaiDan, byte avenger) {
        VXLHoSoDan hoSoDan = layHoSoDan(loaiDan, avenger);
        if (!hoSoDan.dungTrongLuc() || cacDuongX == null || cacDuongY == null
                || cacDuongX.length == 0 || cacDuongY.length == 0
                || cacDuongX[0] == null || cacDuongY[0] == null
                || cacDuongX[0].length == 0 || cacDuongX[0].length != cacDuongY[0].length) {
            return new DiemSieuCao(false, (short)-1, (short)-1);
        }
        short[] duongX = cacDuongX[0];
        short[] duongY = cacDuongY[0];
        int chiSoDinh = 0;
        for (int i = 1; i < duongY.length; i++) {
            if (duongY[i] < duongY[chiSoDinh]) {
                chiSoDinh = i;
            }
        }
        if (batDauY - duongY[chiSoDinh] < DO_CAO_SIEU_CAO_TOI_THIEU) {
            return new DiemSieuCao(false, (short)-1, (short)-1);
        }
        return new DiemSieuCao(true, duongX[chiSoDinh], duongY[chiSoDinh]);
    }

    public static int layHeSoSatThuongTrangThai(boolean sieuCao, boolean kyNangDacBiet) {
        long heSo = 100L;
        if (sieuCao) {
            heSo = heSo * HE_SO_SAT_THUONG_SIEU_CAO / 100L;
        }
        if (kyNangDacBiet) {
            heSo = heSo * HE_SO_SAT_THUONG_KY_NANG_DAC_BIET / 100L;
        }
        return (int)Math.max(100L, Math.min(300L, heSo));
    }

    public static boolean laDanTach(byte loaiDan) {
        int loai = Byte.toUnsignedInt(chuanHoaLoaiDan(loaiDan));
        return loai == 17 || loai == 19;
    }

    public static boolean laDanChum(byte loaiDan) {
        return layHoSoDan(loaiDan).kieuBan() == VXLHoSoDan.KieuBan.DAN_CHUM;
    }

    public static boolean laDanLienThanh(byte loaiDan) {
        VXLHoSoDan.KieuBan kieuBan = layHoSoDan(loaiDan).kieuBan();
        return kieuBan == VXLHoSoDan.KieuBan.DAN_KEP
                || kieuBan == VXLHoSoDan.KieuBan.LIEN_THANH;
    }

    public static boolean suDungTrongLuc(byte loaiDan) {
        return layHoSoDan(loaiDan).dungTrongLuc();
    }

    public static boolean suDungGio(byte loaiDan) {
        return layHoSoDan(loaiDan).dungGio();
    }

    public static boolean xuyenDiaHinh(byte loaiDan) {
        return layHoSoDan(loaiDan).xuyenDiaHinh();
    }

    public static byte layLoaiDanTheoAvenger(byte avenger, byte loaiDanMacDinh) {
        return switch (Byte.toUnsignedInt(avenger)) {
            case 1 -> 1;
            case 2 -> 0;
            case 3 -> 82;
            case 4 -> 33;
            case 5 -> 21;
            case 6 -> 80;
            case 7 -> 79;
            case 8 -> 49;
            default -> chuanHoaLoaiDan(loaiDanMacDinh);
        };
    }

    public static VXLHoSoDan layHoSoDan(byte loaiDan) {
        return layHoSoDan(loaiDan, (byte)0);
    }

    public static VXLHoSoDan layHoSoDan(byte loaiDan, byte avenger) {
        switch (Byte.toUnsignedInt(avenger)) {
            case 1:
                return DAN_IRON_MAN;
            case 2:
                return DAN_HULK;
            case 3:
                return DAN_THOR;
            case 4:
                return DAN_LOKI;
            case 5:
                return DAN_CAPTAIN;
            case 6:
                return DAN_WINTER_SOLDIER;
            case 7:
                return DAN_HAWKEYE;
            case 8:
                return DAN_ULTRON;
            default:
                break;
        }
        return switch (Byte.toUnsignedInt(chuanHoaLoaiDan(loaiDan))) {
            case 1 -> DAN_K98;
            case 2 -> DAN_HOA_CAI;
            case 9 -> DAN_CHUOI;
            case 10 -> DAN_COI;
            case 11 -> DAN_MG;
            case 17 -> DAN_APACHE;
            case 19 -> DAN_GA;
            case 21 -> DAN_TARZAN;
            case 49 -> DAN_LASER;
            default -> DAN_AT;
        };
    }

    public static boolean laDanDacBiet(int maVatPham) {
        return switch (maVatPham) {
            case 222, 227, 228, 231, 235, 236, 237, 238, 239, 240, 241,
                    243, 244, 245, 247, 248, 249, 250 -> true;
            default -> false;
        };
    }

    private static byte chuyenLoaiDanCauHinhSangClient(byte loaiDanCauHinh) {
        return switch (Byte.toUnsignedInt(loaiDanCauHinh)) {
            case 0 -> 0;
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 9;
            case 4 -> 10;
            case 5 -> 11;
            case 6 -> 19;
            case 7 -> 21;
            case 8 -> 17;
            case 9 -> 49;
            default -> chuanHoaLoaiDan(loaiDanCauHinh);
        };
    }

    private static byte chuanHoaLoaiDan(byte loaiDan) {
        int loai = Byte.toUnsignedInt(loaiDan);
        return loai <= 58 || loai == 79 || loai == 80 || loai == 82 || loai == 83
                ? loaiDan : 0;
    }

    private static VXLHoSoDan.VatLy vatLy(double trongLuong, double heSoTrongLuc,
            double heSoGio) {
        return new VXLHoSoDan.VatLy(trongLuong, heSoTrongLuc, heSoGio);
    }

    private static VXLHoSoDan.QuayVe quayVe(double tamBayCoBan, double tamBayTheoLuc,
            double thoiGianBayRaToiDa, double heSoTocDoQuayVe,
            double tocDoXoayDoMoiGiay, int banKinhThuVe) {
        return new VXLHoSoDan.QuayVe(tamBayCoBan, tamBayTheoLuc,
                thoiGianBayRaToiDa, heSoTocDoQuayVe,
                tocDoXoayDoMoiGiay, banKinhThuVe);
    }

    private static VXLHoSoDan.Tarzan tarzan(double giaTocNgoatBanDau,
            double giaTocNgoatLienTuc, int leNgoaiBanDo) {
        return new VXLHoSoDan.Tarzan(giaTocNgoatBanDau,
                giaTocNgoatLienTuc, leNgoaiBanDo);
    }
}
