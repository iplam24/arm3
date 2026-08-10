package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;

public final class VXLHeThongDan {
    private static final double HE_SO_TOC_DO = 0.85D;
    private static final double TRONG_LUC = 0.33D;
    private static final double HE_SO_GIO = 0.0035D;
    private static final double KHOANG_CACH_DAU_SUNG = 30D;
    private static final double DO_CAO_DAU_SUNG = 17D;
    private static final int LE_TRUNG_MAC_DINH = 5;
    private static final int[] LECH_GOC_APACHE = new int[]{-15, 0, 15};
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
        return this.taoPhatBan(batDauX, batDauY, goc, luc, lucTach, loaiDan,
                chiMang, avenger, gioX, gioY, mucTieuBoQua, buocThoiGian,
                soDiemToiDa, false);
    }

    public KetQuaPhatBan taoPhatBan(short batDauX, short batDauY, short goc, byte luc,
            byte lucTach, byte loaiDan, byte chiMang, byte avenger, byte gioX, byte gioY,
            int mucTieuBoQua, double buocThoiGian, int soDiemToiDa,
            boolean epXuyenDiaHinh) {
        VXLHoSoDan hoSoDan = VXLCauHinhVatPhamChienDau.layHoSoDan(loaiDan, avenger);
        if (epXuyenDiaHinh && !hoSoDan.xuyenDiaHinh()) {
            hoSoDan = new VXLHoSoDan(hoSoDan.ten(), hoSoDan.loaiClient(),
                    hoSoDan.kieuBan(), hoSoDan.soVienThuong(), hoSoDan.soVienChiMang(),
                    hoSoDan.khoangLechGoc(), hoSoDan.vatLy(), true,
                    hoSoDan.xuyenNguoi(), hoSoDan.phanTramSatThuongMoiVien(),
                    hoSoDan.tranPhanTramSatThuong(), hoSoDan.quayVe(), hoSoDan.tarzan());
        }
        int soQuyDao = hoSoDan.laySoVien(chiMang);
        short[][] cacDuongX = new short[soQuyDao][];
        short[][] cacDuongY = new short[soQuyDao][];
        int[][] cacMucTieu = new int[soQuyDao][];

        if (Byte.toUnsignedInt(loaiDan) == 17) {
            return this.taoDanApache(batDauX, batDauY, goc, luc, lucTach, loaiDan, hoSoDan,
                    gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
        }
        if (Byte.toUnsignedInt(loaiDan) == 19 && soQuyDao > 1) {
            return this.taoDanGa(batDauX, batDauY, goc, luc, lucTach, loaiDan, hoSoDan,
                    gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.MAGENTA) {
            return this.taoDanMagenta(batDauX, batDauY, goc, luc, hoSoDan,
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
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.NHAN_VAT_LAO) {
            QuyDao nhanVatLao = this.taoQuyDaoNhanVatLao(batDauX, batDauY, goc, luc,
                    mucTieuBoQua, buocThoiGian, soDiemToiDa);
            return new KetQuaPhatBan(new short[][]{nhanVatLao.x}, new short[][]{nhanVatLao.y},
                    new int[][]{nhanVatLao.cacMucTieu}, -1);
        }

        double[] cacDoLech = Byte.toUnsignedInt(loaiDan) == 2
                ? taoDoLechBaTia(soQuyDao, hoSoDan.khoangLechGoc())
                : hoSoDan.taoDoLechGoc(chiMang);
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

    private QuyDao taoQuyDaoNhanVatLao(short batDauX, short batDauY, short goc,
            byte luc, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        int gioiHanDiem = Math.max(80, soDiemToiDa);
        double dt = Math.max(0.1D, buocThoiGian);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double radian = Math.toRadians(goc);
        double buocX = Math.cos(radian) * Math.max(5D, lucBan * HE_SO_TOC_DO) * dt;
        double buocY = -Math.sin(radian) * Math.max(5D, lucBan * HE_SO_TOC_DO) * dt;
        double tamBayToiDa = Math.max(90D, 80D + lucBan * 4.5D);
        double xHienTai = batDauX;
        double yHienTai = batDauY - DO_CAO_DAU_SUNG;
        double quangDuong = 0D;
        java.util.ArrayList<Short> xs = new java.util.ArrayList<>();
        java.util.ArrayList<Short> ys = new java.util.ArrayList<>();
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrung = new java.util.HashSet<>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        xs.add((short)xTruoc);
        ys.add((short)yTruoc);

        for (int i = 1; i < gioiHanDiem / 2; i++) {
            double xMoiThuc = xHienTai + buocX;
            double yMoiThuc = yHienTai + buocY;
            quangDuong += Math.hypot(xMoiThuc - xHienTai, yMoiThuc - yHienTai);
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            boolean raNgoai = xMoi < 0 || xMoi >= this.banDo.getWidth()
                    || yMoi < -200 || yMoi >= this.banDo.getHeight();
            xMoi = Math.max(0, Math.min(this.banDo.getWidth() - 1, xMoi));
            yMoi = Math.max(-200, Math.min(this.banDo.getHeight() - 1, yMoi));
            KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(xTruoc, yTruoc,
                    xMoi, yMoi, false, false, mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
            }
            if (xs.get(xs.size() - 1) != (short)xMoi || ys.get(ys.size() - 1) != (short)yMoi) {
                xs.add((short)xMoi);
                ys.add((short)yMoi);
            }
            if (vaCham != null || raNgoai || quangDuong >= tamBayToiDa) {
                break;
            }
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
            xTruoc = xMoi;
            yTruoc = yMoi;
        }

        int doDaiBayRa = xs.size();
        for (int i = doDaiBayRa - 2; i >= 0 && xs.size() < gioiHanDiem; i--) {
            short xVe = xs.get(i);
            short yVe = ys.get(i);
            if (xs.get(xs.size() - 1) != xVe || ys.get(ys.size() - 1) != yVe) {
                xs.add(xVe);
                ys.add(yVe);
            }
        }
        return new QuyDao(chuyenDanhSachDiem(xs), chuyenDanhSachDiem(ys),
                chuyenDanhSachMucTieu(cacMucTieu));
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

    private KetQuaPhatBan taoDanMagenta(short batDauX, short batDauY, short goc,
            byte luc, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua,
            double buocThoiGian, int soDiemToiDa) {
        VXLHoSoDan.VatLy vatLy = hoSoDan.vatLy();
        int gioiHanDiem = Math.max(24, soDiemToiDa);
        double dt = Math.max(0.1D, buocThoiGian);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double radian = Math.toRadians(goc);
        double xHienTai = batDauX + Math.cos(radian) * KHOANG_CACH_DAU_SUNG;
        double yHienTai = batDauY - DO_CAO_DAU_SUNG
                - Math.sin(radian) * KHOANG_CACH_DAU_SUNG;
        double vanTocX = Math.cos(radian) * lucBan * vatLy.heSoTocDoTheoKhung();
        double vanTocY = -Math.sin(radian) * lucBan * vatLy.heSoTocDoTheoKhung();
        double giaTocX = gioX * vatLy.heSoGioTheoKhung();
        double giaTocY = vatLy.giaTocTrongLucTheoKhung()
                + gioY * vatLy.heSoGioTheoKhung();
        java.util.ArrayList<Short> cacDiemX = new java.util.ArrayList<>();
        java.util.ArrayList<Short> cacDiemY = new java.util.ArrayList<>();
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrung = new java.util.HashSet<>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        cacDiemX.add((short)xTruoc);
        cacDiemY.add((short)yTruoc);
        boolean datDinh = false;
        boolean daVaCham = false;

        for (int chiSo = 1; chiSo < gioiHanDiem; chiSo++) {
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(xTruoc, yTruoc,
                    xMoi, yMoi, false, false, mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
                daVaCham = true;
            }
            if (cacDiemX.get(cacDiemX.size() - 1) != (short)xMoi
                    || cacDiemY.get(cacDiemY.size() - 1) != (short)yMoi) {
                cacDiemX.add((short)xMoi);
                cacDiemY.add((short)yMoi);
            }
            boolean raNgoai = xMoi < -200 || xMoi > this.banDo.getWidth() + 200
                    || yMoi < -600 || yMoi > this.banDo.getHeight() + 200;
            if (daVaCham || raNgoai) {
                break;
            }
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
            xTruoc = xMoi;
            yTruoc = yMoi;
            vanTocX += giaTocX * dt;
            vanTocY += giaTocY * dt;
            if (vanTocY >= 0D) {
                datDinh = true;
                break;
            }
        }

        if (datDinh && !daVaCham && cacDiemX.size() >= 2) {
            int chiSoDinh = cacDiemX.size() - 1;
            double doLechX = cacDiemX.get(chiSoDinh) - cacDiemX.get(0);
            double doLechY = cacDiemY.get(chiSoDinh) - cacDiemY.get(0);
            double doDaiHuong = Math.hypot(doLechX, doLechY);
            if (doDaiHuong > 0.001D) {
                double buocLaserX = doLechX / doDaiHuong * lucBan;
                double buocLaserY = doLechY / doDaiHuong * lucBan;
                if (Math.abs(buocLaserX) < 1D) {
                    buocLaserX = Math.copySign(1D,
                            Math.abs(doLechX) > 0.001D ? doLechX : Math.cos(radian));
                }
                if (Math.abs(buocLaserY) < 1D) {
                    buocLaserY = Math.copySign(1D,
                            Math.abs(doLechY) > 0.001D ? doLechY : -Math.sin(radian));
                }
                double xLaser = cacDiemX.get(chiSoDinh);
                double yLaser = cacDiemY.get(chiSoDinh);
                int xLaserTruoc = (int)Math.round(xLaser);
                int yLaserTruoc = (int)Math.round(yLaser);
                int gioiHanLaser = Math.max(40,
                        (this.banDo.getWidth() + this.banDo.getHeight())
                                / Math.max(1, lucBan) + 40);
                for (int chiSo = 0; chiSo < gioiHanLaser; chiSo++) {
                    double xLaserMoiThuc = xLaser + buocLaserX;
                    double yLaserMoiThuc = yLaser - buocLaserY;
                    int xLaserMoi = (int)Math.round(xLaserMoiThuc);
                    int yLaserMoi = (int)Math.round(yLaserMoiThuc);
                    KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(
                            xLaserTruoc, yLaserTruoc, xLaserMoi, yLaserMoi,
                            false, false, mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
                    if (vaCham != null) {
                        xLaserMoi = vaCham.x;
                        yLaserMoi = vaCham.y;
                    }
                    xLaser = xLaserMoiThuc;
                    yLaser = yLaserMoiThuc;
                    xLaserTruoc = xLaserMoi;
                    yLaserTruoc = yLaserMoi;
                    boolean raNgoai = xLaserMoi < -100
                            || xLaserMoi > this.banDo.getWidth() + 100
                            || yLaserMoi < -600
                            || yLaserMoi > this.banDo.getHeight() + 100;
                    if (vaCham != null || raNgoai) {
                        break;
                    }
                }
                short xCuoi = (short)xLaserTruoc;
                short yCuoi = (short)yLaserTruoc;
                if (cacDiemX.get(cacDiemX.size() - 1) != xCuoi
                        || cacDiemY.get(cacDiemY.size() - 1) != yCuoi) {
                    cacDiemX.add(xCuoi);
                    cacDiemY.add(yCuoi);
                }
            }
        }

        return new KetQuaPhatBan(
                new short[][]{chuyenDanhSachDiem(cacDiemX)},
                new short[][]{chuyenDanhSachDiem(cacDiemY)},
                new int[][]{chuyenDanhSachMucTieu(cacMucTieu)}, -1);
    }

    private KetQuaPhatBan taoDanApache(short batDauX, short batDauY, short goc, byte luc,
            byte lucTach, byte loaiDan, VXLHoSoDan hoSoDan, byte gioX, byte gioY,
            int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        short[][] cacDuongX = new short[4][];
        short[][] cacDuongY = new short[4][];
        int[][] cacMucTieu = new int[4][];
        QuyDao danMe = this.taoQuyDao(batDauX, batDauY, goc, luc, hoSoDan,
                gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
        cacDuongX[0] = danMe.x;
        cacDuongY[0] = danMe.y;

        int khungTach = Math.max(4, Byte.toUnsignedInt(lucTach));
        int chiSoTach = Math.min(Math.max(0, khungTach - 1),
                Math.max(0, danMe.x.length - 1));
        boolean tachTruocVaCham = danMe.x.length > 2 && chiSoTach < danMe.x.length - 1;
        if (!tachTruocVaCham) {
            cacMucTieu[0] = danMe.cacMucTieu;
            for (int i = 1; i < 4; i++) {
                cacDuongX[i] = new short[]{danMe.x[danMe.x.length - 1]};
                cacDuongY[i] = new short[]{danMe.y[danMe.y.length - 1]};
                cacMucTieu[i] = new int[0];
            }
            return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, khungTach);
        }

        short xTach = danMe.x[chiSoTach];
        short yTach = danMe.y[chiSoTach];
        short gocChuan = chuanHoaGoc(goc);
        short gocVeNguoiBan = chuanHoaGoc(Math.toDegrees(
                Math.atan2(batDauY - yTach, batDauX - xTach)));
        short gocRiuGiua = chuanHoaGoc(gocChuan + gocVeNguoiBan);
        if (gocChuan < 90) {
            gocRiuGiua = chuanHoaGoc(180D - gocRiuGiua);
        }
        byte lucRiuCon = (byte)Math.max(1, Byte.toUnsignedInt(luc) / 2);
        for (int i = 0; i < LECH_GOC_APACHE.length; i++) {
            short gocRiu = chuanHoaGoc(gocRiuGiua + LECH_GOC_APACHE[i]);
            double radianRiu = Math.toRadians(gocRiu);
            short xRiu = (short)Math.round(xTach + Math.cos(radianRiu) * 20D);
            short yRiu = (short)Math.round(yTach - 12D - Math.sin(radianRiu) * 20D);
            QuyDao danCon = this.taoQuyDao(xRiu, yRiu, gocRiu, lucRiuCon,
                    hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian,
                    Math.max(20, soDiemToiDa - chiSoTach), false);
            cacDuongX[i + 1] = danCon.x;
            cacDuongY[i + 1] = danCon.y;
            cacMucTieu[i + 1] = danCon.cacMucTieu;
        }
        cacMucTieu[0] = new int[0];
        return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, khungTach);
    }

    private KetQuaPhatBan taoDanGa(short batDauX, short batDauY, short goc, byte luc,
            byte lucTach, byte loaiDan, VXLHoSoDan hoSoDan, byte gioX, byte gioY,
            int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        short[][] cacDuongX = new short[2][];
        short[][] cacDuongY = new short[2][];
        int[][] cacMucTieu = new int[2][];
        QuyDao danMe = this.taoQuyDao(batDauX, batDauY, goc, luc, hoSoDan,
                gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
        cacDuongX[0] = danMe.x;
        cacDuongY[0] = danMe.y;
        cacMucTieu[0] = danMe.cacMucTieu;

        int khungDeTrung = Math.max(4, Byte.toUnsignedInt(lucTach));
        if (danMe.x.length <= khungDeTrung) {
            cacDuongX[1] = new short[]{danMe.x[danMe.x.length - 1]};
            cacDuongY[1] = new short[]{danMe.y[danMe.y.length - 1]};
            cacMucTieu[1] = new int[0];
            return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, khungDeTrung);
        }

        int chiSoDeTrung = Math.min(khungDeTrung, danMe.x.length - 1);
        short xTrung = danMe.x[chiSoDeTrung];
        short yTrung = (short)(danMe.y[chiSoDeTrung] + 8);
        VXLHoSoDan.VatLy vatLyTrung =
                VXLCauHinhVatPhamChienDau.layVatLyDanCon(loaiDan);
        QuyDao quaTrung = this.taoQuyDaoTheoKhungTuVanToc(xTrung, yTrung,
                0D, 0D, hoSoDan, vatLyTrung, gioX, gioY, mucTieuBoQua,
                buocThoiGian, Math.max(20, soDiemToiDa / 2));
        cacDuongX[1] = quaTrung.x;
        cacDuongY[1] = quaTrung.y;
        cacMucTieu[1] = quaTrung.cacMucTieu;
        return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, khungDeTrung);
    }

    private QuyDao taoQuyDao(short batDauX, short batDauY, short goc, byte luc,
            VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua,
            double buocThoiGian, int soDiemToiDa, boolean dungDauSung) {
        VXLHoSoDan.VatLy vatLy = hoSoDan.vatLy();
        if (vatLy.dungVatLyTheoKhung()) {
            return this.taoQuyDaoTheoKhung(batDauX, batDauY, goc, luc,
                    hoSoDan, vatLy, gioX, gioY, mucTieuBoQua,
                    buocThoiGian, soDiemToiDa, dungDauSung);
        }
        int gioiHanDiem = Math.max(1, soDiemToiDa);
        short[] xs = new short[gioiHanDiem];
        short[] ys = new short[gioiHanDiem];
        double radian = Math.toRadians(goc);
        double trongLuong = Math.max(0.25D, vatLy.trongLuong());
        double tocDo = Math.max(8, Byte.toUnsignedInt(luc)) * HE_SO_TOC_DO
                / Math.sqrt(trongLuong);
        double trongLuc = TRONG_LUC * Math.sqrt(trongLuong) * vatLy.heSoTrongLuc();
        double heSoGio = HE_SO_GIO * vatLy.heSoGio() / trongLuong;
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
        return new QuyDao(java.util.Arrays.copyOf(xs, Math.max(1, doDai)),
                java.util.Arrays.copyOf(ys, Math.max(1, doDai)),
                chuyenDanhSachMucTieu(cacMucTieu));
    }

    private QuyDao taoQuyDaoTheoKhung(short batDauX, short batDauY, short goc,
            byte luc, VXLHoSoDan hoSoDan, VXLHoSoDan.VatLy vatLy,
            byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian,
            int soDiemToiDa, boolean dungDauSung) {
        double radian = Math.toRadians(goc);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double xGoc = batDauX;
        double yGoc = batDauY;
        if (dungDauSung) {
            xGoc += Math.cos(radian) * KHOANG_CACH_DAU_SUNG;
            yGoc -= DO_CAO_DAU_SUNG + Math.sin(radian) * KHOANG_CACH_DAU_SUNG;
        }
        double vanTocX = Math.cos(radian) * lucBan * vatLy.heSoTocDoTheoKhung();
        double vanTocY = -Math.sin(radian) * lucBan * vatLy.heSoTocDoTheoKhung();
        return this.taoQuyDaoTheoKhungTuVanToc((short)Math.round(xGoc),
                (short)Math.round(yGoc), vanTocX, vanTocY, hoSoDan, vatLy,
                gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
    }

    private QuyDao taoQuyDaoTheoKhungTuVanToc(short batDauX, short batDauY,
            double vanTocX, double vanTocY, VXLHoSoDan hoSoDan,
            VXLHoSoDan.VatLy vatLy, byte gioX, byte gioY, int mucTieuBoQua,
            double buocThoiGian, int soDiemToiDa) {
        int gioiHanDiem = Math.max(1, soDiemToiDa);
        double dt = Math.max(0.1D, buocThoiGian);
        short[] xs = new short[gioiHanDiem];
        short[] ys = new short[gioiHanDiem];
        double xHienTai = batDauX;
        double yHienTai = batDauY;
        double giaTocX = gioX * vatLy.heSoGioTheoKhung();
        double giaTocY = vatLy.giaTocTrongLucTheoKhung()
                + gioY * vatLy.heSoGioTheoKhung();
        int doDai = 1;
        int xTruoc = batDauX;
        int yTruoc = batDauY;
        xs[0] = batDauX;
        ys[0] = batDauY;
        java.util.ArrayList<Integer> cacMucTieu = new java.util.ArrayList<>();
        java.util.HashSet<Integer> mucTieuDaTrung = new java.util.HashSet<>();
        for (int chiSo = 1; chiSo < gioiHanDiem; chiSo++) {
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(xTruoc, yTruoc,
                    xMoi, yMoi, hoSoDan.xuyenDiaHinh(), hoSoDan.xuyenNguoi(),
                    mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
            }
            xs[doDai] = (short)xMoi;
            ys[doDai] = (short)yMoi;
            doDai++;
            boolean raNgoai = xMoi < -200 || xMoi > this.banDo.getWidth() + 200
                    || yMoi < -600 || yMoi > this.banDo.getHeight() + 200;
            if (vaCham != null || raNgoai) {
                break;
            }
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
            xTruoc = xMoi;
            yTruoc = yMoi;
            vanTocX += giaTocX * dt;
            vanTocY += giaTocY * dt;
        }
        return new QuyDao(java.util.Arrays.copyOf(xs, Math.max(1, doDai)),
                java.util.Arrays.copyOf(ys, Math.max(1, doDai)),
                chuyenDanhSachMucTieu(cacMucTieu));
    }

    private QuyDao taoQuyDaoTarzan(short batDauX, short batDauY, short goc, byte luc,
            VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian,
            int soDiemToiDa) {
        int gioiHanDiem = Math.max(600, soDiemToiDa);
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
            KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(xTruoc, yTruoc, xMoi, yMoi,
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

    private KetQuaVaCham timVaChamTrongBanDoTrenDoan(int x1, int y1, int x2, int y2,
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

    private static double[] taoDoLechBaTia(int soVien, double khoangLechGoc) {
        double[] ketQua = new double[Math.max(1, soVien)];
        for (int i = 1; i < ketQua.length; i++) {
            int bac = (i + 1) / 2;
            ketQua[i] = (i % 2 == 1 ? 1D : -1D) * bac * khoangLechGoc;
        }
        return ketQua;
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
