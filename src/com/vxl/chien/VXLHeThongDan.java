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
                        break;
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
        double xChu = batDauX;
        double yChu = batDauY - DO_CAO_DAU_SUNG;
        VXLHoSoDan.Tarzan tarzan = hoSoDan.tarzan() != null
                ? hoSoDan.tarzan()
                : new VXLHoSoDan.Tarzan(45D, 4.2D, 12D, 165D, 38D, 180D, 72);
        boolean banXuoi = Math.cos(radian) >= 0D;
        double tamBayTruocNgoat = Math.max(40D,
                tarzan.tamBayTruocNgoatCoBan()
                        + lucBan * tarzan.tamBayTruocNgoatTheoLuc());
        double tocDoXoay = Math.toRadians(banXuoi
                ? tarzan.tocDoXoayXuoiDoMoiGiay()
                : tarzan.tocDoXoayNguocDoMoiGiay());
        double tongGocXoayToiDa = Math.toRadians(banXuoi
                ? tarzan.tongGocXoayXuoi()
                : tarzan.tongGocXoayNguoc());
        double quangDuongDaBay = 0D;
        double tongGocDaXoay = 0D;
        double banKinhAnToanChu = Math.max(48D, tarzan.banKinhAnToanChu());
        boolean daRaKhoiVungChu = false;
        int doDai = 1;
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrung = new java.util.HashSet<>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        xs[0] = (short)xTruoc;
        ys[0] = (short)yTruoc;
        for (int i = 1; i < gioiHanDiem; i++) {
            vanTocX += giaTocGioX * dt;
            vanTocY += (giaTocTrongLuc + giaTocGioY) * dt;
            if (quangDuongDaBay >= tamBayTruocNgoat
                    && tongGocDaXoay < tongGocXoayToiDa) {
                double doXoay = Math.min(tocDoXoay * dt,
                        tongGocXoayToiDa - tongGocDaXoay);
                double tocDoHienTai = Math.hypot(vanTocX, vanTocY);
                double gocHienTai = Math.atan2(vanTocY, vanTocX) + doXoay;
                vanTocX = Math.cos(gocHienTai) * tocDoHienTai;
                vanTocY = Math.sin(gocHienTai) * tocDoHienTai;
                tongGocDaXoay += doXoay;
            }
            if (tongGocDaXoay >= tongGocXoayToiDa - 0.0001D) {
                vanTocY += 0.38D * dt;
            }
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            quangDuongDaBay += Math.hypot(xMoiThuc - xHienTai, yMoiThuc - yHienTai);
            double cachChuX = xMoiThuc - xChu;
            double cachChuY = yMoiThuc - yChu;
            double khoangCachChu = Math.hypot(cachChuX, cachChuY);
            if (!daRaKhoiVungChu && khoangCachChu >= banKinhAnToanChu + 20D) {
                daRaKhoiVungChu = true;
            }
            if (daRaKhoiVungChu && khoangCachChu < banKinhAnToanChu) {
                double phapTuyenX = khoangCachChu > 0.001D
                        ? cachChuX / khoangCachChu : 1D;
                double phapTuyenY = khoangCachChu > 0.001D
                        ? cachChuY / khoangCachChu : 0D;
                xMoiThuc = xChu + phapTuyenX * banKinhAnToanChu;
                yMoiThuc = yChu + phapTuyenY * banKinhAnToanChu;
                double tocDoHienTai = Math.max(1D, Math.hypot(vanTocX, vanTocY));
                double thanhPhanHuongVao = vanTocX * phapTuyenX + vanTocY * phapTuyenY;
                if (thanhPhanHuongVao < 0D) {
                    vanTocX -= thanhPhanHuongVao * phapTuyenX;
                    vanTocY -= thanhPhanHuongVao * phapTuyenY;
                }
                double tiepTuyenX = -phapTuyenY;
                double tiepTuyenY = phapTuyenX;
                if (vanTocX * tiepTuyenX + vanTocY * tiepTuyenY < 0D) {
                    tiepTuyenX = -tiepTuyenX;
                    tiepTuyenY = -tiepTuyenY;
                }
                vanTocX += tiepTuyenX * tocDoHienTai * 0.35D
                        + phapTuyenX * tocDoHienTai * 0.20D;
                vanTocY += tiepTuyenY * tocDoHienTai * 0.35D
                        + phapTuyenY * tocDoHienTai * 0.20D;
                double tocDoSauLech = Math.max(0.001D, Math.hypot(vanTocX, vanTocY));
                vanTocX = vanTocX / tocDoSauLech * tocDoHienTai;
                vanTocY = vanTocY / tocDoSauLech * tocDoHienTai;
            }
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            boolean raNgoai = xMoi < 0 || xMoi >= this.banDo.getWidth()
                    || yMoi < -600 || yMoi >= this.banDo.getHeight();
            xMoi = Math.max(0, Math.min(this.banDo.getWidth() - 1, xMoi));
            yMoi = Math.max(-600, Math.min(this.banDo.getHeight() - 1, yMoi));
            KetQuaVaCham vaCham = this.timVaChamTrenDoan(xTruoc, yTruoc, xMoi, yMoi,
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
        }
        return new QuyDao(java.util.Arrays.copyOf(xs, Math.max(1, doDai)),
                java.util.Arrays.copyOf(ys, Math.max(1, doDai)),
                chuyenDanhSachMucTieu(cacMucTieu));
    }

    private QuyDao taoQuyDaoQuayVe(short batDauX, short batDauY, short goc, byte luc,
            VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian,
            int soDiemToiDa) {
        int gioiHanDiem = Math.max(24, soDiemToiDa);
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
        double gocXoayToiDa = Math.toRadians(Math.max(1D,
                quayVe.tocDoXoayDoMoiGiay())) * dt;
        double quangDuongBayRa = 0D;
        double thoiGianBayRa = 0D;
        boolean dangQuayVe = false;
        int doDai = 1;
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrung = new java.util.HashSet<>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        xs[0] = (short)xTruoc;
        ys[0] = (short)yTruoc;
        for (int i = 1; i < gioiHanDiem; i++) {
            if (dangQuayVe) {
                double denChuX = xThuVe - xHienTai;
                double denChuY = yThuVe - yHienTai;
                double khoangCachDenChu = Math.hypot(denChuX, denChuY);
                if (khoangCachDenChu <= quayVe.banKinhThuVe()) {
                    xs[doDai] = (short)Math.round(xThuVe);
                    ys[doDai] = (short)Math.round(yThuVe);
                    doDai++;
                    break;
                }
                double gocHienTai = Math.atan2(vanTocY, vanTocX);
                double gocMucTieu = Math.atan2(denChuY, denChuX);
                double gocMoi = xoayGocToi(gocHienTai, gocMucTieu, gocXoayToiDa);
                vanTocX = Math.cos(gocMoi) * tocDoQuayVe;
                vanTocY = Math.sin(gocMoi) * tocDoQuayVe;
            } else {
                vanTocX += giaTocGioX * dt;
                vanTocY += (giaTocTrongLuc + giaTocGioY) * dt;
            }
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            if (!dangQuayVe) {
                quangDuongBayRa += Math.hypot(xMoiThuc - xHienTai, yMoiThuc - yHienTai);
                thoiGianBayRa += dt;
            }
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            boolean raNgoai = xMoi < 0 || xMoi >= this.banDo.getWidth()
                    || yMoi < -600 || yMoi >= this.banDo.getHeight();
            xMoi = Math.max(0, Math.min(this.banDo.getWidth() - 1, xMoi));
            yMoi = Math.max(-600, Math.min(this.banDo.getHeight() - 1, yMoi));
            KetQuaVaCham vaCham = this.timVaChamTrenDoan(xTruoc, yTruoc, xMoi, yMoi,
                    hoSoDan.xuyenDiaHinh(),
                    hoSoDan.xuyenNguoi(), mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
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
            if (!dangQuayVe && (quangDuongBayRa >= tamBayToiDa
                    || thoiGianBayRa >= quayVe.thoiGianBayRaToiDa())) {
                dangQuayVe = true;
            }
        }
        return new QuyDao(java.util.Arrays.copyOf(xs, Math.max(1, doDai)),
                java.util.Arrays.copyOf(ys, Math.max(1, doDai)),
                chuyenDanhSachMucTieu(cacMucTieu));
    }

    private static double xoayGocToi(double gocHienTai, double gocMucTieu,
            double gocXoayToiDa) {
        double doLech = Math.atan2(Math.sin(gocMucTieu - gocHienTai),
                Math.cos(gocMucTieu - gocHienTai));
        double doXoay = Math.max(-gocXoayToiDa, Math.min(gocXoayToiDa, doLech));
        return gocHienTai + doXoay;
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
