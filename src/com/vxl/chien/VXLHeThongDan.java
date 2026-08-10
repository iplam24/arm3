package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;

public final class VXLHeThongDan {
    private static final double HE_SO_TOC_DO = 0.85D;
    private static final double TRONG_LUC = 0.33D;
    private static final double HE_SO_GIO = 0.0035D;
    private static final double KHOANG_CACH_DAU_SUNG = 30D;
    private static final double DO_CAO_DAU_SUNG = 17D;
    private static final int LE_TRUNG_MAC_DINH = 5;
    private static final int[] LECH_GOC_DAN_TACH = new int[]{-10, 0, 10};
    private static final double[] MAU_LECH_GOC_MG = new double[]{-0.90D, 0.35D, -0.20D, 0.80D, -0.45D};

    @FunctionalInterface
    public interface BoTimMucTieu {
        int timMucTieu(int x, int y, int leTrung, int mucTieuBoQua);
    }

    public static final class KetQuaPhatBan {
        public final short[][] duongX;
        public final short[][] duongY;
        public final int[] mucTieuTheoQuyDao;
        public final int[][] cacMucTieuTheoQuyDao;
        public final int chiSoTach;

        private KetQuaPhatBan(short[][] duongX, short[][] duongY, int[][] cacMucTieuTheoQuyDao,
                int chiSoTach) {
            this.duongX = duongX;
            this.duongY = duongY;
            this.cacMucTieuTheoQuyDao = cacMucTieuTheoQuyDao;
            this.mucTieuTheoQuyDao = new int[cacMucTieuTheoQuyDao.length];
            java.util.Arrays.fill(this.mucTieuTheoQuyDao, -1);
            for (int i = 0; i < cacMucTieuTheoQuyDao.length; i++) {
                if (cacMucTieuTheoQuyDao[i] != null && cacMucTieuTheoQuyDao[i].length > 0) {
                    this.mucTieuTheoQuyDao[i] = cacMucTieuTheoQuyDao[i][0];
                }
            }
            this.chiSoTach = chiSoTach;
        }

        public int demSoVienTrung(int mucTieu) {
            int soVien = 0;
            for (int[] cacMucTieu : this.cacMucTieuTheoQuyDao) {
                if (cacMucTieu == null) {
                    continue;
                }
                for (int mucTieuTrung : cacMucTieu) {
                    if (mucTieuTrung == mucTieu) {
                        soVien++;
                    }
                }
            }
            return soVien;
        }

        public int[] layTatCaMucTieuTrung() {
            java.util.ArrayList<Integer> ketQua = new java.util.ArrayList<>();
            for (int[] cacMucTieu : this.cacMucTieuTheoQuyDao) {
                if (cacMucTieu == null) {
                    continue;
                }
                for (int mucTieu : cacMucTieu) {
                    ketQua.add(mucTieu);
                }
            }
            int[] mangKetQua = new int[ketQua.size()];
            for (int i = 0; i < ketQua.size(); i++) {
                mangKetQua[i] = ketQua.get(i);
            }
            return mangKetQua;
        }
    }

    private static final class QuyDao {
        private final short[] x;
        private final short[] y;
        private final int[] cacMucTieu;

        private QuyDao(short[] x, short[] y, int[] cacMucTieu) {
            this.x = x;
            this.y = y;
            this.cacMucTieu = cacMucTieu;
        }
    }

    private final VXLQuanLyBanDo banDo;
    private final BoTimMucTieu boTimMucTieu;

    public VXLHeThongDan(VXLQuanLyBanDo banDo, BoTimMucTieu boTimMucTieu) {
        this.banDo = banDo;
        this.boTimMucTieu = boTimMucTieu;
    }

    public KetQuaPhatBan taoPhatBan(short batDauX, short batDauY, short goc, byte luc,
            byte lucTach, byte loaiDan, byte chiMang, byte avenger, byte gioX, byte gioY,
            int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        VXLHoSoDan hoSoDan = VXLCauHinhVatPhamChienDau.layHoSoDan(loaiDan, avenger);
        int soQuyDao = hoSoDan.laySoVien(chiMang);
        short[][] cacDuongX = new short[soQuyDao][];
        short[][] cacDuongY = new short[soQuyDao][];
        int[][] cacMucTieu = new int[soQuyDao][];

        if (Byte.toUnsignedInt(loaiDan) == 17) {
            return this.taoDanApache(batDauX, batDauY, goc, luc, lucTach, loaiDan, hoSoDan,
                    gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
        }
        if (Byte.toUnsignedInt(loaiDan) == 9 && soQuyDao > 1) {
            return this.taoDanChuoi(batDauX, batDauY, goc, luc, hoSoDan, gioX, gioY,
                    mucTieuBoQua, buocThoiGian, soDiemToiDa);
        }
        if (Byte.toUnsignedInt(loaiDan) == 19 && soQuyDao > 1) {
            return this.taoDanGa(batDauX, batDauY, goc, luc, lucTach, loaiDan, hoSoDan,
                    gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.LASER) {
            return this.taoDanLaser(batDauX, batDauY, goc, hoSoDan, mucTieuBoQua);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.VONG_TARZAN) {
            QuyDao tarzan = this.taoQuyDaoTarzan(batDauX, batDauY, goc, luc, hoSoDan,
                    gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
            return new KetQuaPhatBan(new short[][]{tarzan.x}, new short[][]{tarzan.y},
                    new int[][]{tarzan.cacMucTieu}, -1);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.QUAY_VE) {
            QuyDao quayVe = this.taoQuyDaoQuayVe(batDauX, batDauY, goc, luc, hoSoDan,
                    gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
            return new KetQuaPhatBan(new short[][]{quayVe.x}, new short[][]{quayVe.y},
                    new int[][]{quayVe.cacMucTieu}, -1);
        }

        double[] cacDoLech = hoSoDan.taoDoLechGoc(chiMang);
        if (Byte.toUnsignedInt(loaiDan) == 11 && soQuyDao == MAU_LECH_GOC_MG.length) {
            cacDoLech = new double[soQuyDao];
            for (int i = 0; i < soQuyDao; i++) {
                cacDoLech[i] = MAU_LECH_GOC_MG[i] * hoSoDan.khoangLechGoc();
            }
        }
        QuyDao quyDaoGoc = null;
        for (int i = 0; i < soQuyDao; i++) {
            QuyDao quyDao;
            if ((hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.DAN_KEP
                    || hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.LIEN_THANH)
                    && hoSoDan.khoangLechGoc() <= 0D && quyDaoGoc != null) {
                quyDao = saoChepQuyDao(quyDaoGoc);
            } else {
                short gocVien = chuanHoaGoc(goc + cacDoLech[i]);
                quyDao = this.taoQuyDao(batDauX, batDauY, gocVien, luc, hoSoDan, gioX, gioY,
                        mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
                if (quyDaoGoc == null) {
                    quyDaoGoc = quyDao;
                }
            }
            cacDuongX[i] = quyDao.x;
            cacDuongY[i] = quyDao.y;
            cacMucTieu[i] = quyDao.cacMucTieu;
        }
        return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, -1);
    }

    private KetQuaPhatBan taoDanLaser(short batDauX, short batDauY, short goc,
            VXLHoSoDan hoSoDan, int mucTieuBoQua) {
        double radian = Math.toRadians(goc);
        double huongX = Math.cos(radian);
        double huongY = -Math.sin(radian);
        double xGoc = batDauX + huongX * KHOANG_CACH_DAU_SUNG;
        double yGoc = batDauY - DO_CAO_DAU_SUNG + huongY * KHOANG_CACH_DAU_SUNG;
        int xDau = (int)Math.round(Math.max(0D,
                Math.min(this.banDo.getWidth() - 1D, xGoc)));
        int yDau = (int)Math.round(Math.max(0D,
                Math.min(this.banDo.getHeight() - 1D, yGoc)));
        double khoangCachToiBien = Double.POSITIVE_INFINITY;
        if (huongX > 0.0001D) {
            khoangCachToiBien = Math.min(khoangCachToiBien,
                    (this.banDo.getWidth() - 1D - xDau) / huongX);
        } else if (huongX < -0.0001D) {
            khoangCachToiBien = Math.min(khoangCachToiBien, (0D - xDau) / huongX);
        }
        if (huongY > 0.0001D) {
            khoangCachToiBien = Math.min(khoangCachToiBien,
                    (this.banDo.getHeight() - 1D - yDau) / huongY);
        } else if (huongY < -0.0001D) {
            khoangCachToiBien = Math.min(khoangCachToiBien, (0D - yDau) / huongY);
        }
        if (!Double.isFinite(khoangCachToiBien) || khoangCachToiBien < 0D) {
            khoangCachToiBien = 0D;
        }
        int xCuoi = (int)Math.round(xDau + huongX * khoangCachToiBien);
        int yCuoi = (int)Math.round(yDau + huongY * khoangCachToiBien);
        xCuoi = Math.max(0, Math.min(this.banDo.getWidth() - 1, xCuoi));
        yCuoi = Math.max(0, Math.min(this.banDo.getHeight() - 1, yCuoi));
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrung = new java.util.HashSet<>();
        KetQuaVaCham vaCham = this.timVaChamTrenDoan(xDau, yDau, xCuoi, yCuoi,
                hoSoDan.xuyenDiaHinh(), hoSoDan.xuyenNguoi(), mucTieuBoQua,
                mucTieuDaTrung, cacMucTieu);
        if (vaCham != null) {
            xCuoi = vaCham.x;
            yCuoi = vaCham.y;
        }
        return new KetQuaPhatBan(
                new short[][]{new short[]{(short)xDau, (short)xCuoi}},
                new short[][]{new short[]{(short)yDau, (short)yCuoi}},
                new int[][]{chuyenDanhSachMucTieu(cacMucTieu)}, -1);
    }

    private KetQuaPhatBan taoDanChuoi(short batDauX, short batDauY, short goc, byte luc,
            VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua,
            double buocThoiGian, int soDiemToiDa) {
        int soVien = hoSoDan.laySoVien((byte)0);
        short[][] cacDuongX = new short[soVien][];
        short[][] cacDuongY = new short[soVien][];
        int[][] cacMucTieu = new int[soVien][];
        QuyDao danDanDuong = this.taoQuyDao(batDauX, batDauY, goc, luc, hoSoDan,
                gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
        if (danDanDuong.x.length < 4) {
            for (int i = 0; i < soVien; i++) {
                cacDuongX[i] = java.util.Arrays.copyOf(danDanDuong.x, danDanDuong.x.length);
                cacDuongY[i] = java.util.Arrays.copyOf(danDanDuong.y, danDanDuong.y.length);
                cacMucTieu[i] = i == 0 ? danDanDuong.cacMucTieu : new int[0];
            }
            return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, -1);
        }
        int chiSoTach = timChiSoDinh(danDanDuong.y);
        chiSoTach = Math.max(2, Math.min(chiSoTach, danDanDuong.x.length - 2));
        short xTach = danDanDuong.x[chiSoTach];
        short yTach = danDanDuong.y[chiSoTach];
        short gocTiepTuyen = tinhGocTiepTuyen(danDanDuong.x, danDanDuong.y, chiSoTach);
        double[] lechGoc = new double[]{-9D, -3D, 3D, 9D};
        byte lucDanCon = (byte)Math.max(5,
                Math.min(24, Math.round(Byte.toUnsignedInt(luc) * 0.58F)));
        for (int i = 0; i < soVien; i++) {
            QuyDao danCon = this.taoQuyDao(xTach, yTach,
                    chuanHoaGoc(gocTiepTuyen + lechGoc[Math.min(i, lechGoc.length - 1)]),
                    lucDanCon, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian,
                    Math.max(20, soDiemToiDa - chiSoTach), false);
            cacDuongX[i] = ghepDuongDan(danDanDuong.x, danCon.x, chiSoTach);
            cacDuongY[i] = ghepDuongDan(danDanDuong.y, danCon.y, chiSoTach);
            cacMucTieu[i] = danCon.cacMucTieu;
        }
        return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, chiSoTach);
    }

    private KetQuaPhatBan taoDanApache(short batDauX, short batDauY, short goc, byte luc,
            byte lucTach, byte loaiDan, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua,
            double buocThoiGian, int soDiemToiDa) {
        short[][] cacDuongX = new short[4][];
        short[][] cacDuongY = new short[4][];
        int[][] cacMucTieu = new int[4][];
        QuyDao danMe = this.taoQuyDao(batDauX, batDauY, goc, luc, hoSoDan, gioX, gioY,
                mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
        cacDuongX[0] = danMe.x;
        cacDuongY[0] = danMe.y;

        int diemTachYeuCau = Math.max(4, Byte.toUnsignedInt(lucTach));
        int chiSoTach = Math.min(diemTachYeuCau, Math.max(0, danMe.x.length - 1));
        boolean tachTruocVaCham = danMe.x.length > 2 && chiSoTach < danMe.x.length - 1;
        if (!tachTruocVaCham) {
            cacMucTieu[0] = danMe.cacMucTieu;
            for (int i = 1; i < 4; i++) {
                cacDuongX[i] = new short[]{danMe.x[danMe.x.length - 1]};
                cacDuongY[i] = new short[]{danMe.y[danMe.y.length - 1]};
                cacMucTieu[i] = new int[0];
            }
            return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, chiSoTach);
        }

        short xTach = danMe.x[chiSoTach];
        short yTach = danMe.y[chiSoTach];
        short gocTiepTuyen = tinhGocTiepTuyen(danMe.x, danMe.y, chiSoTach);
        byte lucDanCon = (byte)Math.max(12, Math.min(30, Byte.toUnsignedInt(luc)));
        for (int i = 0; i < LECH_GOC_DAN_TACH.length; i++) {
            QuyDao danCon = this.taoQuyDao(xTach, yTach,
                    chuanHoaGoc(gocTiepTuyen + LECH_GOC_DAN_TACH[i]), lucDanCon, hoSoDan,
                    gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, false);
            cacDuongX[i + 1] = danCon.x;
            cacDuongY[i + 1] = danCon.y;
            cacMucTieu[i + 1] = danCon.cacMucTieu;
        }
        cacMucTieu[0] = new int[0];
        return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, chiSoTach);
    }

    private KetQuaPhatBan taoDanGa(short batDauX, short batDauY, short goc, byte luc,
            byte lucTach, byte loaiDan, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua,
            double buocThoiGian, int soDiemToiDa) {
        short[][] cacDuongX = new short[2][];
        short[][] cacDuongY = new short[2][];
        int[][] cacMucTieu = new int[2][];
        QuyDao danMe = this.taoQuyDao(batDauX, batDauY, goc, luc, hoSoDan, gioX, gioY,
                mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
        cacDuongX[0] = danMe.x;
        cacDuongY[0] = danMe.y;
        int chiSoTach = Math.min(Math.max(2, Byte.toUnsignedInt(lucTach)),
                Math.max(1, danMe.x.length - 2));
        if (danMe.x.length < 3) {
            cacMucTieu[0] = danMe.cacMucTieu;
            cacDuongX[1] = new short[]{danMe.x[danMe.x.length - 1]};
            cacDuongY[1] = new short[]{danMe.y[danMe.y.length - 1]};
            cacMucTieu[1] = new int[0];
        } else {
            byte lucTrung = (byte)Math.max(10, Math.min(30, Byte.toUnsignedInt(lucTach)));
            QuyDao quaTrung = this.taoQuyDao(danMe.x[chiSoTach], danMe.y[chiSoTach], (short)270,
                    lucTrung, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian,
                    Math.max(20, soDiemToiDa / 2), false);
            cacDuongX[1] = quaTrung.x;
            cacDuongY[1] = quaTrung.y;
            cacMucTieu[0] = danMe.cacMucTieu;
            cacMucTieu[1] = quaTrung.cacMucTieu;
        }
        return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, chiSoTach);
    }

    private static int timChiSoDinh(short[] duongY) {
        int chiSoDinh = 0;
        for (int i = 1; i < duongY.length; i++) {
            if (duongY[i] < duongY[chiSoDinh]) {
                chiSoDinh = i;
            }
        }
        return chiSoDinh;
    }

    private static short[] ghepDuongDan(short[] danMe, short[] danCon, int chiSoTach) {
        int doDaiDau = Math.min(danMe.length, chiSoTach + 1);
        int doDaiSau = Math.max(0, danCon.length - 1);
        short[] ketQua = new short[doDaiDau + doDaiSau];
        System.arraycopy(danMe, 0, ketQua, 0, doDaiDau);
        if (doDaiSau > 0) {
            System.arraycopy(danCon, 1, ketQua, doDaiDau, doDaiSau);
        }
        return ketQua;
    }

    private QuyDao taoQuyDao(short batDauX, short batDauY, short goc, byte luc, VXLHoSoDan hoSoDan,
            byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa,
            boolean dungDauSung) {
        int gioiHanDiem = Math.max(1, soDiemToiDa);
        short[] xs = new short[gioiHanDiem];
        short[] ys = new short[gioiHanDiem];
        double radian = Math.toRadians(goc);
        double trongLuong = Math.max(0.25D, hoSoDan.trongLuong());
        double tocDo = Math.max(8, Byte.toUnsignedInt(luc)) * HE_SO_TOC_DO
                / Math.sqrt(trongLuong);
        double trongLuc = TRONG_LUC * Math.sqrt(trongLuong) * hoSoDan.heSoTrongLuc();
        double heSoGio = HE_SO_GIO * hoSoDan.heSoGio() / trongLuong;
        double xGoc = batDauX;
        double yGoc = batDauY;
        if (dungDauSung) {
            xGoc += Math.cos(radian) * KHOANG_CACH_DAU_SUNG;
            yGoc -= DO_CAO_DAU_SUNG + Math.sin(radian) * KHOANG_CACH_DAU_SUNG;
        }
        int xTruoc = (int)Math.round(xGoc);
        int yTruoc = (int)Math.round(yGoc);
        int doDai = 0;
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrung = new java.util.HashSet<>();
        for (int i = 0; i < gioiHanDiem; i++) {
            double thoiGian = i * Math.max(0.1D, buocThoiGian);
            int x = (int)Math.round(xGoc + Math.cos(radian) * tocDo * thoiGian
                    + gioX * heSoGio * thoiGian * thoiGian);
            int y = (int)Math.round(yGoc - Math.sin(radian) * tocDo * thoiGian
                    + (trongLuc - gioY * heSoGio) * thoiGian * thoiGian);
            boolean raNgoai = x < 0 || x >= this.banDo.getWidth()
                    || y < -600 || y >= this.banDo.getHeight();
            x = Math.max(0, Math.min(this.banDo.getWidth() - 1, x));
            y = Math.max(-600, Math.min(this.banDo.getHeight() - 1, y));
            KetQuaVaCham vaCham = this.timVaChamTrenDoan(xTruoc, yTruoc, x, y,
                    hoSoDan.xuyenDiaHinh(), hoSoDan.xuyenNguoi(), mucTieuBoQua,
                    mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                x = vaCham.x;
                y = vaCham.y;
            }
            xs[i] = (short)x;
            ys[i] = (short)y;
            doDai = i + 1;
            if (raNgoai || vaCham != null) {
                break;
            }
            xTruoc = x;
            yTruoc = y;
        }
        short[] xRutGon = java.util.Arrays.copyOf(xs, Math.max(1, doDai));
        short[] yRutGon = java.util.Arrays.copyOf(ys, Math.max(1, doDai));
        return new QuyDao(xRutGon, yRutGon, chuyenDanhSachMucTieu(cacMucTieu));
    }

    private QuyDao taoQuyDaoTarzan(short batDauX, short batDauY, short goc, byte luc,
            VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian,
            int soDiemToiDa) {
        int gioiHanDiem = Math.max(240, soDiemToiDa);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double dt = Math.max(0.1D, buocThoiGian);
        short[] xs = new short[gioiHanDiem];
        short[] ys = new short[gioiHanDiem];
        double radian = Math.toRadians(goc);
        double trongLuong = Math.max(0.25D, hoSoDan.trongLuong());
        double tocDoBan = lucBan * HE_SO_TOC_DO / Math.sqrt(trongLuong);
        double vanTocX = Math.cos(radian) * tocDoBan;
        double vanTocY = -Math.sin(radian) * tocDoBan;
        double giaTocTrongLuc = 2D * TRONG_LUC * Math.sqrt(trongLuong)
                * hoSoDan.heSoTrongLuc();
        double heSoGio = 2D * HE_SO_GIO * hoSoDan.heSoGio() / trongLuong;
        double giaTocGioX = gioX * heSoGio;
        double giaTocGioY = -gioY * heSoGio;
        double xHienTai = batDauX + Math.cos(radian) * KHOANG_CACH_DAU_SUNG;
        double yHienTai = batDauY - DO_CAO_DAU_SUNG
                - Math.sin(radian) * KHOANG_CACH_DAU_SUNG;
        VXLHoSoDan.Tarzan tarzan = hoSoDan.tarzan() != null
                ? hoSoDan.tarzan()
                : new VXLHoSoDan.Tarzan(1D, 2D, 90);
        double huongNgoat = vanTocX <= 0D ? 1D : -1D;
        int trangThaiNgoat = -1;
        int leNgoaiBanDo = Math.max(0, tarzan.leNgoaiBanDo());
        int doDai = 1;
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrung = new java.util.HashSet<>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        xs[0] = (short)xTruoc;
        ys[0] = (short)yTruoc;
        for (int chiSoDiem = 1; chiSoDiem < gioiHanDiem; chiSoDiem++) {
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            boolean raNgoai = xMoi < -leNgoaiBanDo
                    || xMoi > this.banDo.getWidth() + leNgoaiBanDo
                    || yMoi > this.banDo.getHeight() + 100;
            yMoi = Math.max(-600, Math.min(this.banDo.getHeight() + 100, yMoi));
            KetQuaVaCham vaCham = this.timVaChamTarzanTrenDoan(xTruoc, yTruoc, xMoi, yMoi,
                    hoSoDan.xuyenDiaHinh(), hoSoDan.xuyenNguoi(), mucTieuBoQua,
                    mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
            }
            xs[doDai] = (short)xMoi;
            ys[doDai] = (short)yMoi;
            doDai++;
            if (raNgoai || vaCham != null) {
                break;
            }
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
            xTruoc = xMoi;
            yTruoc = yMoi;
            vanTocX += giaTocGioX * dt;
            vanTocY += (giaTocTrongLuc + giaTocGioY) * dt;
            if (trangThaiNgoat == 0) {
                vanTocX += huongNgoat * tarzan.giaTocNgoatBanDau() * dt;
                trangThaiNgoat = 1;
            } else if (trangThaiNgoat == 1) {
                vanTocX += huongNgoat * tarzan.giaTocNgoatLienTuc() * dt;
            } else if (vanTocY > 0D) {
                trangThaiNgoat = 0;
            }
        }
        return new QuyDao(java.util.Arrays.copyOf(xs, Math.max(1, doDai)),
                java.util.Arrays.copyOf(ys, Math.max(1, doDai)),
                chuyenDanhSachMucTieu(cacMucTieu));
    }

    private KetQuaVaCham timVaChamTarzanTrenDoan(int x1, int y1, int x2, int y2,
            boolean xuyenDiaHinh, boolean xuyenNguoi, int mucTieuBoQua,
            java.util.Set<Integer> mucTieuDaTrung, java.util.List<Integer> cacMucTieu) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
        for (int buoc = 0; buoc <= soBuoc; buoc++) {
            int x = x1 + dx * buoc / soBuoc;
            int y = y1 + dy * buoc / soBuoc;
            boolean trongBanDo = x >= 0 && x < this.banDo.getWidth()
                    && y >= 0 && y < this.banDo.getHeight();
            if (!xuyenDiaHinh && trongBanDo && this.banDo.coVaCham((short)x, (short)y)) {
                return new KetQuaVaCham((short)x, (short)y);
            }
            if (trongBanDo && this.boTimMucTieu != null) {
                int mucTieu = this.boTimMucTieu.timMucTieu(x, y, LE_TRUNG_MAC_DINH,
                        mucTieuBoQua);
                if (mucTieu >= 0 && mucTieuDaTrung.add(mucTieu)) {
                    cacMucTieu.add(mucTieu);
                    if (!xuyenNguoi) {
                        return new KetQuaVaCham((short)x, (short)y);
                    }
                }
            }
        }
        return null;
    }

    private QuyDao taoQuyDaoQuayVe(short batDauX, short batDauY, short goc, byte luc,
            VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian,
            int soDiemToiDa) {
        int gioiHanDiem = Math.max(24, soDiemToiDa);
        int gioiHanBayRa = Math.max(12, gioiHanDiem / 2);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double dt = Math.max(0.1D, buocThoiGian);
        double radian = Math.toRadians(goc);
        double trongLuong = Math.max(0.25D, hoSoDan.trongLuong());
        double tocDoBan = lucBan * HE_SO_TOC_DO / Math.sqrt(trongLuong);
        double vanTocX = Math.cos(radian) * tocDoBan;
        double vanTocY = -Math.sin(radian) * tocDoBan;
        double giaTocTrongLuc = 2D * TRONG_LUC * Math.sqrt(trongLuong)
                * hoSoDan.heSoTrongLuc();
        double heSoGio = 2D * HE_SO_GIO * hoSoDan.heSoGio() / trongLuong;
        double giaTocGioX = gioX * heSoGio;
        double giaTocGioY = -gioY * heSoGio;
        double xThuVe = batDauX;
        double yThuVe = batDauY - DO_CAO_DAU_SUNG;
        double xHienTai = batDauX + Math.cos(radian) * KHOANG_CACH_DAU_SUNG;
        double yHienTai = batDauY - DO_CAO_DAU_SUNG
                - Math.sin(radian) * KHOANG_CACH_DAU_SUNG;
        VXLHoSoDan.QuayVe quayVe = hoSoDan.quayVe() != null
                ? hoSoDan.quayVe()
                : new VXLHoSoDan.QuayVe(70D, 6D, 15D, 1.10D, 26D, 18);
        double tamBayToiDa = Math.max(60D,
                quayVe.tamBayCoBan() + lucBan * quayVe.tamBayTheoLuc());
        double tocDoQuayVe = Math.max(10D, tocDoBan * quayVe.heSoTocDoQuayVe());
        double quangDuongBayRa = 0D;
        double thoiGianBayRa = 0D;
        boolean chamDiaHinh = false;
        boolean raNgoaiBanDo = false;
        java.util.ArrayList<Short> xs = new java.util.ArrayList<>();
        java.util.ArrayList<Short> ys = new java.util.ArrayList<>();
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrungBayRa = new java.util.HashSet<>();
        java.util.HashSet<Integer> mucTieuDaTrungQuayVe = new java.util.HashSet<>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        xs.add((short)xTruoc);
        ys.add((short)yTruoc);
        for (int i = 1; i < gioiHanBayRa; i++) {
            vanTocX += giaTocGioX * dt;
            vanTocY += (giaTocTrongLuc + giaTocGioY) * dt;
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            quangDuongBayRa += Math.hypot(xMoiThuc - xHienTai, yMoiThuc - yHienTai);
            thoiGianBayRa += dt;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            raNgoaiBanDo = xMoi < 0 || xMoi >= this.banDo.getWidth()
                    || yMoi < -600 || yMoi >= this.banDo.getHeight();
            xMoi = Math.max(0, Math.min(this.banDo.getWidth() - 1, xMoi));
            yMoi = Math.max(-600, Math.min(this.banDo.getHeight() - 1, yMoi));
            KetQuaVaCham vaCham = this.timVaChamTrenDoan(xTruoc, yTruoc, xMoi, yMoi,
                    false, true, mucTieuBoQua, mucTieuDaTrungBayRa, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
                chamDiaHinh = true;
            }
            xs.add((short)xMoi);
            ys.add((short)yMoi);
            if (raNgoaiBanDo || chamDiaHinh) {
                break;
            }
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
            xTruoc = xMoi;
            yTruoc = yMoi;
            if (quangDuongBayRa >= tamBayToiDa
                    || thoiGianBayRa >= quayVe.thoiGianBayRaToiDa()) {
                break;
            }
        }

        int doDaiBayRa = xs.size();
        boolean daTaoVongVe = false;
        if (!chamDiaHinh && !raNgoaiBanDo) {
            daTaoVongVe = this.themVongQuayVeMuon(xs, ys, xThuVe, yThuVe,
                    tocDoQuayVe * dt,
                    Math.toRadians(quayVe.tocDoXoayDoMoiGiay()) * dt,
                    gioiHanDiem, mucTieuBoQua, mucTieuDaTrungQuayVe, cacMucTieu);
        }
        if (!daTaoVongVe) {
            this.themDuongQuayNguocMem(xs, ys, doDaiBayRa, xThuVe, yThuVe,
                    tocDoQuayVe * dt, gioiHanDiem, mucTieuBoQua,
                    mucTieuDaTrungQuayVe, cacMucTieu);
        }
        return new QuyDao(chuyenDanhSachDiem(xs), chuyenDanhSachDiem(ys),
                chuyenDanhSachMucTieu(cacMucTieu));
    }

    private void themDuongQuayNguocMem(java.util.ArrayList<Short> xs,
            java.util.ArrayList<Short> ys, int doDaiBayRa, double xThuVe, double yThuVe,
            double buocDiem, int gioiHanDiem, int mucTieuBoQua,
            java.util.Set<Integer> mucTieuDaTrung, java.util.List<Integer> cacMucTieu) {
        java.util.ArrayList<Double> duongNguocX = new java.util.ArrayList<>();
        java.util.ArrayList<Double> duongNguocY = new java.util.ArrayList<>();
        duongNguocX.add((double)xs.get(xs.size() - 1));
        duongNguocY.add((double)ys.get(ys.size() - 1));
        for (int chiSo = doDaiBayRa - 2; chiSo >= 0; chiSo--) {
            duongNguocX.add((double)xs.get(chiSo));
            duongNguocY.add((double)ys.get(chiSo));
        }
        if (Math.hypot(duongNguocX.get(duongNguocX.size() - 1) - xThuVe,
                duongNguocY.get(duongNguocY.size() - 1) - yThuVe) > 0.5D) {
            duongNguocX.add(xThuVe);
            duongNguocY.add(yThuVe);
        }
        double[] tongDoDai = new double[duongNguocX.size()];
        for (int chiSo = 1; chiSo < duongNguocX.size(); chiSo++) {
            tongDoDai[chiSo] = tongDoDai[chiSo - 1]
                    + Math.hypot(duongNguocX.get(chiSo) - duongNguocX.get(chiSo - 1),
                            duongNguocY.get(chiSo) - duongNguocY.get(chiSo - 1));
        }
        double doDaiToanBo = tongDoDai[tongDoDai.length - 1];
        if (doDaiToanBo <= 0.001D) {
            return;
        }
        int xTruoc = xs.get(xs.size() - 1);
        int yTruoc = ys.get(ys.size() - 1);
        if (xs.size() < gioiHanDiem - 1) {
            xs.add((short)xTruoc);
            ys.add((short)yTruoc);
        }
        double buocOnDinh = Math.max(6D, Math.min(12D, buocDiem * 0.65D));
        double quangDuongDaDi = 0D;
        int chiSoDoan = 1;
        int khungTangToc = 0;
        while (xs.size() < gioiHanDiem && quangDuongDaDi < doDaiToanBo) {
            int soDiemConLai = Math.max(1, gioiHanDiem - xs.size());
            double buocCanThiet = (doDaiToanBo - quangDuongDaDi) / soDiemConLai;
            double buocTangToc = Math.min(buocOnDinh, 2D + khungTangToc * 2D);
            quangDuongDaDi = Math.min(doDaiToanBo,
                    quangDuongDaDi + Math.max(buocTangToc, buocCanThiet));
            while (chiSoDoan < tongDoDai.length - 1
                    && tongDoDai[chiSoDoan] < quangDuongDaDi) {
                chiSoDoan++;
            }
            int chiSoTruoc = Math.max(0, chiSoDoan - 1);
            double doDaiDoan = tongDoDai[chiSoDoan] - tongDoDai[chiSoTruoc];
            double tiLe = doDaiDoan <= 0.0001D ? 0D
                    : (quangDuongDaDi - tongDoDai[chiSoTruoc]) / doDaiDoan;
            int xMoi = (int)Math.round(duongNguocX.get(chiSoTruoc)
                    + (duongNguocX.get(chiSoDoan) - duongNguocX.get(chiSoTruoc)) * tiLe);
            int yMoi = (int)Math.round(duongNguocY.get(chiSoTruoc)
                    + (duongNguocY.get(chiSoDoan) - duongNguocY.get(chiSoTruoc)) * tiLe);
            this.timVaChamTrenDoan(xTruoc, yTruoc, xMoi, yMoi, true, true,
                    mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            xs.add((short)xMoi);
            ys.add((short)yMoi);
            xTruoc = xMoi;
            yTruoc = yMoi;
            khungTangToc++;
        }
        xs.set(xs.size() - 1, (short)Math.round(xThuVe));
        ys.set(ys.size() - 1, (short)Math.round(yThuVe));
    }

    private boolean themVongQuayVeMuon(java.util.ArrayList<Short> xs,
            java.util.ArrayList<Short> ys, double xThuVe, double yThuVe, double buocDiem,
            double gocXoayToiDa, int gioiHanDiem, int mucTieuBoQua,
            java.util.Set<Integer> mucTieuDaTrung, java.util.List<Integer> cacMucTieu) {
        if (xs.size() < 2 || xs.size() >= gioiHanDiem - 1) {
            return false;
        }
        int chiSoCuoi = xs.size() - 1;
        double incomingX = xs.get(chiSoCuoi) - xs.get(chiSoCuoi - 1);
        double incomingY = ys.get(chiSoCuoi) - ys.get(chiSoCuoi - 1);
        if (Math.hypot(incomingX, incomingY) < 0.001D) {
            return false;
        }
        double buocOnDinh = Math.max(6D, Math.min(12D, buocDiem * 0.65D));
        double gocXoayOnDinh = Math.max(Math.toRadians(8D),
                Math.min(Math.toRadians(18D), gocXoayToiDa));
        int soDiemConLai = gioiHanDiem - xs.size();
        int huongUonLen = incomingX >= 0D ? -1 : 1;
        short[][] vongVe = this.taoVongQuayVeTheoHuong(xs.get(chiSoCuoi),
                ys.get(chiSoCuoi), incomingX, incomingY, xThuVe, yThuVe,
                buocOnDinh, gocXoayOnDinh, huongUonLen, soDiemConLai);
        if (vongVe == null) {
            vongVe = this.taoVongQuayVeTheoHuong(xs.get(chiSoCuoi),
                    ys.get(chiSoCuoi), incomingX, incomingY, xThuVe, yThuVe,
                    buocOnDinh, gocXoayOnDinh, -huongUonLen, soDiemConLai);
        }
        if (vongVe == null) {
            return false;
        }
        int xTruoc = xs.get(chiSoCuoi);
        int yTruoc = ys.get(chiSoCuoi);
        for (int chiSo = 0; chiSo < vongVe[0].length; chiSo++) {
            int xMoi = vongVe[0][chiSo];
            int yMoi = vongVe[1][chiSo];
            this.timVaChamTrenDoan(xTruoc, yTruoc, xMoi, yMoi, true, true,
                    mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            xs.add((short)xMoi);
            ys.add((short)yMoi);
            xTruoc = xMoi;
            yTruoc = yMoi;
        }
        return true;
    }

    private short[][] taoVongQuayVeTheoHuong(double xBatDau, double yBatDau,
            double incomingX, double incomingY, double xThuVe, double yThuVe,
            double buocDiem, double gocXoayToiDa, int huongUonBanDau,
            int soDiemToiDa) {
        java.util.ArrayList<Short> duongX = new java.util.ArrayList<>();
        java.util.ArrayList<Short> duongY = new java.util.ArrayList<>();
        double xHienTai = xBatDau;
        double yHienTai = yBatDau;
        double gocHienTai = Math.atan2(incomingY, incomingX);
        for (int chiSo = 0; chiSo < soDiemToiDa; chiSo++) {
            double denChuX = xThuVe - xHienTai;
            double denChuY = yThuVe - yHienTai;
            double khoangCachDenChu = Math.hypot(denChuX, denChuY);
            if (khoangCachDenChu <= buocDiem) {
                duongX.add((short)Math.round(xThuVe));
                duongY.add((short)Math.round(yThuVe));
                return new short[][]{chuyenDanhSachDiem(duongX), chuyenDanhSachDiem(duongY)};
            }
            double gocMucTieu = Math.atan2(denChuY, denChuX);
            double doLech = Math.atan2(Math.sin(gocMucTieu - gocHienTai),
                    Math.cos(gocMucTieu - gocHienTai));
            if (Math.abs(doLech) > gocXoayToiDa) {
                int huongXoay = doLech >= 0D ? 1 : -1;
                if (chiSo == 0 && Math.PI - Math.abs(doLech) <= gocXoayToiDa * 2D) {
                    huongXoay = huongUonBanDau;
                }
                gocHienTai += huongXoay * gocXoayToiDa;
            } else {
                gocHienTai = gocMucTieu;
            }
            double xMoiThuc = xHienTai + Math.cos(gocHienTai) * buocDiem;
            double yMoiThuc = yHienTai + Math.sin(gocHienTai) * buocDiem;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            if (xMoi < 0 || xMoi >= this.banDo.getWidth()
                    || yMoi < -600 || yMoi >= this.banDo.getHeight()
                    || this.doanChamDiaHinh((int)Math.round(xHienTai),
                            (int)Math.round(yHienTai), xMoi, yMoi)) {
                return null;
            }
            duongX.add((short)xMoi);
            duongY.add((short)yMoi);
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
        }
        return null;
    }

    private boolean doanChamDiaHinh(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
        for (int buoc = 1; buoc <= soBuoc; buoc++) {
            int x = x1 + dx * buoc / soBuoc;
            int y = y1 + dy * buoc / soBuoc;
            if (y >= 0 && this.banDo.coVaCham((short)x, (short)y)) {
                return true;
            }
        }
        return false;
    }

    private KetQuaVaCham timVaChamTrenDoan(int x1, int y1, int x2, int y2,
            boolean xuyenDiaHinh, boolean xuyenNguoi, int mucTieuBoQua,
            java.util.Set<Integer> mucTieuDaTrung, java.util.List<Integer> cacMucTieu) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
        for (int buoc = 0; buoc <= soBuoc; buoc++) {
            int x = x1 + dx * buoc / soBuoc;
            int y = y1 + dy * buoc / soBuoc;
            if (!xuyenDiaHinh && y >= 0 && this.banDo.coVaCham((short)x, (short)y)) {
                return new KetQuaVaCham((short)x, (short)y);
            }
            if (this.boTimMucTieu != null) {
                int mucTieu = this.boTimMucTieu.timMucTieu(x, y, LE_TRUNG_MAC_DINH,
                        mucTieuBoQua);
                if (mucTieu >= 0 && mucTieuDaTrung.add(mucTieu)) {
                    cacMucTieu.add(mucTieu);
                    if (!xuyenNguoi) {
                        return new KetQuaVaCham((short)x, (short)y);
                    }
                }
            }
        }
        return null;
    }

    private static QuyDao saoChepQuyDao(QuyDao quyDao) {
        return new QuyDao(quyDao.x.clone(), quyDao.y.clone(), quyDao.cacMucTieu.clone());
    }

    private static int[] chuyenDanhSachMucTieu(java.util.List<Integer> cacMucTieu) {
        int[] ketQua = new int[cacMucTieu.size()];
        for (int i = 0; i < cacMucTieu.size(); i++) {
            ketQua[i] = cacMucTieu.get(i);
        }
        return ketQua;
    }

    private static short[] chuyenDanhSachDiem(java.util.List<Short> cacDiem) {
        short[] ketQua = new short[cacDiem.size()];
        for (int i = 0; i < cacDiem.size(); i++) {
            ketQua[i] = cacDiem.get(i);
        }
        return ketQua;
    }

    private static short tinhGocTiepTuyen(short[] xs, short[] ys, int chiSo) {
        int truoc = Math.max(0, chiSo - 1);
        int sau = Math.min(xs.length - 1, chiSo + 1);
        double radian = Math.atan2(ys[truoc] - ys[sau], xs[sau] - xs[truoc]);
        return chuanHoaGoc(Math.toDegrees(radian));
    }

    private static short chuanHoaGoc(double goc) {
        int ketQua = (int)Math.round(goc) % 360;
        if (ketQua < 0) {
            ketQua += 360;
        }
        return (short)ketQua;
    }

    private static final class KetQuaVaCham {
        private final short x;
        private final short y;

        private KetQuaVaCham(short x, short y) {
            this.x = x;
            this.y = y;
        }
    }
}
