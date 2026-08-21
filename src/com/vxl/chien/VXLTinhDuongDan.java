package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;

final class VXLTinhDuongDan {
    private static final int BAN_KINH_TRUNG = 42;
    private static final int NUA_RONG_HITBOX_NHAN_VAT = 12;
    private static final int CHIEU_CAO_HITBOX_NHAN_VAT = 24;
    private static final int DO_CAO_BUOC_LEN_TOI_DA = 4;
    private static final int LECH_DIEM_VA_CHAM_DI_BO = 5;
    private static final int KHE_NHO_CO_THE_VUOT_TOI_DA = 16;
    private static final int SO_DIEM_TOI_DA = 160;
    private static final int SO_DIEM_TOI_DA_DAN_IT_ROI = 340;
    private static final double BUOC_THOI_GIAN = 0.5D;
    private static final double HE_SO_TOC_DO = 0.85D;
    private static final double TRONG_LUC = 0.33D;
    private static final double KHOANG_CACH_DAU_SUNG = 16D;
    private static final double DO_CAO_DAU_SUNG = 12D;
    private static final double GIAM_KHOANG_CACH_DAU_SPIDER_MAN = 12D;
    private static final double TANG_DO_CAO_DAU_SPIDER_MAN = 3D;
    private static final byte LUC_TOI_THIEU = 12;
    private static final byte LUC_TOI_DA = 30;
    private final VXLQuanLyBanDo banDo;
    private final VXLChienBinh[] chienBinhs;
    private final VXLHeThongDan heThongDan;

    record CachBanBot(short goc, byte luc, int satThuongDuKien) {
    }

    VXLTinhDuongDan(VXLQuanLyBanDo banDo, VXLChienBinh[] chienBinhs) {
        this(banDo, chienBinhs, null);
    }

    VXLTinhDuongDan(VXLQuanLyBanDo banDo, VXLChienBinh[] chienBinhs,
            VXLHeThongDan.BoKiemTraVungVoiRong boKiemTraVungVoiRong) {
        this.banDo = banDo;
        this.chienBinhs = chienBinhs;
        this.heThongDan = new VXLHeThongDan(banDo, this::timChiSoMucTieuTaiDiem,
                boKiemTraVungVoiRong);
    }

    short[][] tao(short batDauX, short batDauY, short goc, byte luc) {
        VXLHeThongDan.KetQuaPhatBan ketQua = this.heThongDan.taoPhatBan(batDauX, batDauY,
                goc, luc, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, -1,
                BUOC_THOI_GIAN, SO_DIEM_TOI_DA);
        return new short[][]{ketQua.duongX[0], ketQua.duongY[0]};
    }

    VXLHeThongDan.KetQuaPhatBan taoPhatBan(VXLChienBinh nguoiBan, byte loaiDan, byte chiMang,
            byte avengerDan, short goc, byte luc, byte lucTach, byte gioX, byte gioY) {
        return this.taoPhatBan(nguoiBan, loaiDan, chiMang, avengerDan, goc, luc,
                lucTach, gioX, gioY, false);
    }

    VXLHeThongDan.KetQuaPhatBan taoPhatBan(VXLChienBinh nguoiBan, byte loaiDan, byte chiMang,
            byte avengerDan, short goc, byte luc, byte lucTach, byte gioX, byte gioY,
            boolean epXuyenDiaHinh) {
        short batDauX = nguoiBan.x;
        short batDauY = nguoiBan.y;
        if (avengerDan == 9) {
            double radian = Math.toRadians(goc);
            batDauX = (short)Math.round(nguoiBan.x
                    - Math.cos(radian) * GIAM_KHOANG_CACH_DAU_SPIDER_MAN);
            batDauY = (short)Math.round(nguoiBan.y
                    + Math.sin(radian) * GIAM_KHOANG_CACH_DAU_SPIDER_MAN
                    - TANG_DO_CAO_DAU_SPIDER_MAN);
        }
        return this.heThongDan.taoPhatBan(batDauX, batDauY, goc, luc, lucTach, loaiDan,
                chiMang, avengerDan, gioX, gioY, Byte.toUnsignedInt(nguoiBan.chiSo), BUOC_THOI_GIAN,
                this.laySoDiemToiDa(loaiDan, avengerDan), epXuyenDiaHinh);
    }

    private int laySoDiemToiDa(byte loaiDan, byte avengerDan) {
        VXLHoSoDan hoSoDan = VXLCauHinhVatPhamChienDau.layHoSoDan(loaiDan, avengerDan);
        VXLHoSoDan.VatLy vatLy = hoSoDan.vatLy();
        if (hoSoDan.dungTrongLuc() && !vatLy.dungVatLyTheoKhung()
                && vatLy.heSoTrongLuc() <= 0.25D) {
            return SO_DIEM_TOI_DA_DAN_IT_ROI;
        }
        return SO_DIEM_TOI_DA;
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

    private int timChiSoMucTieuTaiDiem(int x, int y, int leTrung, int mucTieuBoQua) {
        int nuaRong = NUA_RONG_HITBOX_NHAN_VAT + Math.max(0, leTrung);
        int le = Math.max(0, leTrung);
        for (int i = 0; i < this.chienBinhs.length; i++) {
            VXLChienBinh mucTieu = this.chienBinhs[i];
            if (i == mucTieuBoQua || mucTieu == null || mucTieu.chet || mucTieu.daRoiTran
                    || mucTieu.luotVoHinh > 0) {
                continue;
            }
            if (x >= mucTieu.x - nuaRong && x < mucTieu.x + nuaRong
                    && y >= mucTieu.y - CHIEU_CAO_HITBOX_NHAN_VAT - le
                    && y <= mucTieu.y + le) {
                return i;
            }
        }
        return -1;
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

    CachBanBot timCachBanBot(VXLChienBinh nguoiBan, VXLChienBinh mucTieu,
            byte loaiDan, byte gioX, byte gioY, boolean uuTienSieuCao) {
        if (nguoiBan == null || mucTieu == null) {
            return new CachBanBot((short)0, LUC_TOI_THIEU, 0);
        }
        byte lucMacDinh = this.lucCanThietToiMucTieu(nguoiBan, mucTieu);
        short gocMacDinh = this.gocDanDaoToiMucTieu(
                nguoiBan, mucTieu, lucMacDinh);
        CachBanBot cachTotNhat = new CachBanBot(gocMacDinh, lucMacDinh, 0);
        CachBanBot cachGaySatThuongThuong = null;
        CachBanBot cachGaySatThuongSieuCao = null;
        CachBanBot cachSieuCaoTotNhat = null;
        int satThuongThuongTotNhat = 0;
        int satThuongSieuCaoTotNhat = 0;
        long diemTotNhat = Long.MAX_VALUE;
        long diemSieuCaoTotNhat = Long.MAX_VALUE;
        boolean banSangPhai = mucTieu.x >= nguoiBan.x;
        int gocBatDau = banSangPhai ? 0 : 91;
        int gocKetThuc = banSangPhai ? 89 : 180;
        for (int luc = 10; luc <= Byte.toUnsignedInt(LUC_TOI_DA); luc += 2) {
            for (int goc = gocBatDau; goc <= gocKetThuc; goc += 2) {
                VXLHeThongDan.KetQuaPhatBan phatBan = this.taoPhatBan(
                        nguoiBan, loaiDan, (byte)0, (byte)0, (short)goc, (byte)luc,
                        (byte)0, gioX, gioY);
                boolean sieuCao = VXLCauHinhVatPhamChienDau.timDiemSieuCao(
                        nguoiBan.y, phatBan.duongX, phatBan.duongY,
                        loaiDan, (byte)0).kichHoat();
                int satThuong = this.tinhSatThuongDuKien(
                        phatBan, mucTieu, loaiDan);
                CachBanBot cachBan = new CachBanBot((short)goc, (byte)luc, satThuong);
                if (sieuCao && satThuong > satThuongSieuCaoTotNhat) {
                    satThuongSieuCaoTotNhat = satThuong;
                    cachGaySatThuongSieuCao = cachBan;
                    if (uuTienSieuCao && satThuong >= 100) {
                        return cachGaySatThuongSieuCao;
                    }
                } else if (!sieuCao && satThuong > satThuongThuongTotNhat) {
                    satThuongThuongTotNhat = satThuong;
                    cachGaySatThuongThuong = cachBan;
                    if (!uuTienSieuCao && satThuong >= 100) {
                        return cachGaySatThuongThuong;
                    }
                }
                long diemLech = this.tinhDiemLechMucTieu(
                        phatBan.duongX, phatBan.duongY, mucTieu);
                if (sieuCao && diemLech < diemSieuCaoTotNhat) {
                    diemSieuCaoTotNhat = diemLech;
                    cachSieuCaoTotNhat = cachBan;
                }
                if (diemLech < diemTotNhat) {
                    diemTotNhat = diemLech;
                    cachTotNhat = cachBan;
                }
            }
        }
        if (uuTienSieuCao && cachGaySatThuongSieuCao != null) {
            return cachGaySatThuongSieuCao;
        }
        if (!uuTienSieuCao && cachGaySatThuongThuong != null) {
            return cachGaySatThuongThuong;
        }
        if (cachGaySatThuongThuong != null) {
            return cachGaySatThuongThuong;
        }
        if (cachGaySatThuongSieuCao != null) {
            return cachGaySatThuongSieuCao;
        }
        return uuTienSieuCao && cachSieuCaoTotNhat != null
                ? cachSieuCaoTotNhat : cachTotNhat;
    }

    private int tinhSatThuongDuKien(VXLHeThongDan.KetQuaPhatBan phatBan,
            VXLChienBinh mucTieu, byte loaiDan) {
        int satThuongMoiVien = VXLCauHinhVatPhamChienDau.tinhSatThuongMoiVien(
                100, loaiDan, (byte)0, (byte)0);
        int tranSatThuong = VXLCauHinhVatPhamChienDau.layTranPhanTramSatThuong(
                loaiDan, (byte)0);
        int satThuongTrucTiep = Math.min(tranSatThuong,
                phatBan.demSoVienTrung(Byte.toUnsignedInt(mucTieu.chiSo))
                        * satThuongMoiVien);
        int satThuongNo = VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                phatBan.vaChamDiaHinhX, phatBan.vaChamDiaHinhY,
                mucTieu.x, mucTieu.y, loaiDan, (byte)0,
                satThuongMoiVien, tranSatThuong);
        return Math.max(satThuongTrucTiep, satThuongNo);
    }

    private long tinhDiemLechMucTieu(short[][] cacDuongX, short[][] cacDuongY,
            VXLChienBinh mucTieu) {
        long diemTotNhat = Long.MAX_VALUE;
        int soQuyDao = Math.min(cacDuongX.length, cacDuongY.length);
        for (int i = 0; i < soQuyDao; i++) {
            short[] xs = cacDuongX[i];
            short[] ys = cacDuongY[i];
            if (xs == null || ys == null) {
                continue;
            }
            int soDiem = Math.min(xs.length, ys.length);
            for (int j = 0; j < soDiem; j++) {
                int dx = Math.max(0, Math.abs(xs[j] - mucTieu.x) - NUA_RONG_HITBOX_NHAN_VAT);
                int dy = 0;
                if (ys[j] < mucTieu.y - CHIEU_CAO_HITBOX_NHAN_VAT) {
                    dy = mucTieu.y - CHIEU_CAO_HITBOX_NHAN_VAT - ys[j];
                } else if (ys[j] > mucTieu.y) {
                    dy = ys[j] - mucTieu.y;
                }
                diemTotNhat = Math.min(diemTotNhat, (long)dx * dx + (long)dy * dy);
            }
        }
        return diemTotNhat;
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

    short[] gioiHanDiChuyenNguoiChoi(short xCu, short yCu, short xMoi, short yMoi,
            int khoangCachToiDa, boolean duocPhepBay) {
        if (duocPhepBay) {
            return this.gioiHanDiChuyenBay(xCu, yCu, xMoi, yMoi, khoangCachToiDa);
        }
        int xBatDau = this.gioiHan(xCu, 0, this.banDo.getWidth() - 1);
        int yBatDau = this.gioiHan(yCu, 0, this.banDo.getHeight() - 1);
        int xDich = this.gioiHan(xMoi, 0, this.banDo.getWidth() - 1);
        int huong = Integer.compare(xDich, xBatDau);
        int khoangCach = Math.min(Math.abs(xDich - xBatDau), Math.max(0, khoangCachToiDa));
        if (huong == 0 || khoangCach == 0) {
            return new short[]{(short)xBatDau, (short)yBatDau};
        }

        int xServer = xBatDau;
        int yServer = yBatDau;
        int soCotTrongLienTiep = 0;
        for (int buoc = 0; buoc < khoangCach; buoc++) {
            int xKeTiep = xServer + huong;
            if (this.coVatCanThanTaiCot((short)xKeTiep, (short)yServer)) {
                break;
            }
            short yKeTiep = this.timViTriDatChoBuocKeTiep((short)xKeTiep, (short)yServer);
            xServer = xKeTiep;
            if (yKeTiep == Short.MIN_VALUE) {
                soCotTrongLienTiep++;
                if (soCotTrongLienTiep > KHE_NHO_CO_THE_VUOT_TOI_DA) {
                    break;
                }
                continue;
            }
            yServer = yKeTiep;
            soCotTrongLienTiep = 0;
        }
        return new short[]{(short)xServer, (short)yServer};
    }

    short timViTriRoiThang(short x, short yHienTai) {
        if (x < 0 || x >= this.banDo.getWidth()
                || yHienTai < 0 || yHienTai >= this.banDo.getHeight()) {
            return Short.MIN_VALUE;
        }
        if (!this.banDo.coVaCham(x, yHienTai)
                && (yHienTai == this.banDo.getHeight() - 1
                        || this.banDo.coVaCham(x, (short)(yHienTai + 1)))) {
            return yHienTai;
        }
        int batDau = Math.min(this.banDo.getHeight() - 1, yHienTai + 1);
        for (int y = batDau; y < this.banDo.getHeight(); y++) {
            if (this.banDo.coVaCham(x, (short)y)) {
                return (short)Math.max(0, y - 1);
            }
        }
        return Short.MIN_VALUE;
    }

    private short[] gioiHanDiChuyenBay(short xCu, short yCu, short xMoi, short yMoi,
            int khoangCachToiDa) {
        int xBatDau = this.gioiHan(xCu, 0, this.banDo.getWidth() - 1);
        int yBatDau = this.gioiHan(yCu, 0, this.banDo.getHeight() - 1);
        int xDich = this.gioiHan(xMoi, 0, this.banDo.getWidth() - 1);
        int yDich = this.gioiHan(yMoi, 0, this.banDo.getHeight() - 1);
        int dx = xDich - xBatDau;
        int dy = yDich - yBatDau;
        double khoangCach = Math.hypot(dx, dy);
        int gioiHan = Math.max(0, khoangCachToiDa);
        if (khoangCach > gioiHan && khoangCach > 0D) {
            double tiLe = gioiHan / khoangCach;
            xDich = (int)Math.round(xBatDau + dx * tiLe);
            yDich = (int)Math.round(yBatDau + dy * tiLe);
        }
        return new short[]{(short)xDich, (short)yDich};
    }

    private short timViTriDatChoBuocKeTiep(short x, short yHienTai) {
        int batDauY = Math.max(0, yHienTai - DO_CAO_BUOC_LEN_TOI_DA + 1);
        for (int y = batDauY; y < this.banDo.getHeight(); y++) {
            if (this.banDo.coVaCham(x, (short)y)) {
                return (short)Math.max(0, y - 1);
            }
        }
        return Short.MIN_VALUE;
    }

    private boolean coVatCanThanTaiCot(short x, short yChan) {
        int tuY = Math.max(0, yChan - CHIEU_CAO_HITBOX_NHAN_VAT);
        int denY = Math.min(this.banDo.getHeight() - 1,
                yChan - LECH_DIEM_VA_CHAM_DI_BO);
        for (int y = tuY; y <= denY; y++) {
            if (this.banDo.coVaCham(x, (short)y)) {
                return true;
            }
        }
        return false;
    }

    private int gioiHan(int giaTri, int min, int max) {
        return Math.max(min, Math.min(max, giaTri));
    }
}
