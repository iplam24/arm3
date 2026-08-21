/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.vxl.bando.VXLQuanLyBanDo
 *  com.vxl.chien.VXLCauHinhVatPhamChienDau
 *  com.vxl.chien.VXLHoSoDan
 *  com.vxl.chien.VXLHoSoDan$KieuBan
 *  com.vxl.chien.VXLHoSoDan$QuayVe
 *  com.vxl.chien.VXLHoSoDan$Tarzan
 *  com.vxl.chien.VXLHoSoDan$VatLy
 */
package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;
import com.vxl.chien.VXLCauHinhVatPhamChienDau;
import com.vxl.chien.VXLHoSoDan;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class VXLHeThongDan {
    private static final double HE_SO_TOC_DO = 0.85;
    private static final double TRONG_LUC = 0.33;
    private static final double HE_SO_GIO = 0.008;
    private static final double DO_NHAY_GIO_TOI_THIEU = 0.35;
    private static final double DO_NHAY_GIO_TOI_DA = 1.25;
    private static final double HE_SO_GIO_THEO_KHUNG = 1.5;
    private static final double KHOANG_CACH_DAU_SUNG = 30.0;
    private static final double DO_CAO_DAU_SUNG = 17.0;
    private static final double LUC_KEO_VOI_RONG_MOI_KHUNG = 2.0;
    public static final short KHONG_CO_VA_CHAM_DIA_HINH = Short.MIN_VALUE;
    private static final int LE_TRUNG_MAC_DINH = 0;
    private static final int NUA_RONG_THAN_HULK = 10;
    private static final int CHIEU_CAO_THAN_HULK = 35;
    private static final int DO_CAO_CAT_CANH_AN_TOAN_HULK = 14;
    private static final int DO_CAO_TOI_THIEU_TRUOC_KHI_TRUNG_HULK = 26;
    private static final int SO_DIEM_TOI_THIEU_HULK = 220;
    private static final int BUOC_QUET_THAN_HULK = 5;
    private static final int[] LECH_GOC_APACHE = new int[]{-15, 0, 15};
    private static final double[] MAU_LECH_GOC_MG = new double[]{-0.9, 0.35, -0.2, 0.8, -0.45};
    private final VXLQuanLyBanDo banDo;
    private final BoTimMucTieu boTimMucTieu;
    private final BoKiemTraVungVoiRong boKiemTraVungVoiRong;

    private static short[] taoMangKhongVaCham(int doDai) {
        short[] ketQua = new short[Math.max(0, doDai)];
        Arrays.fill(ketQua, (short)Short.MIN_VALUE);
        return ketQua;
    }

    public VXLHeThongDan(VXLQuanLyBanDo banDo, BoTimMucTieu boTimMucTieu) {
        this(banDo, boTimMucTieu, null);
    }

    public VXLHeThongDan(VXLQuanLyBanDo banDo, BoTimMucTieu boTimMucTieu,
            BoKiemTraVungVoiRong boKiemTraVungVoiRong) {
        this.banDo = banDo;
        this.boTimMucTieu = boTimMucTieu;
        this.boKiemTraVungVoiRong = boKiemTraVungVoiRong;
    }

    public KetQuaPhatBan taoPhatBan(short batDauX, short batDauY, short goc, byte luc, byte lucTach, byte loaiDan, byte chiMang, byte avenger, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        return this.taoPhatBan(batDauX, batDauY, goc, luc, lucTach, loaiDan, chiMang, avenger, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, false);
    }

    public KetQuaPhatBan taoPhatBan(short batDauX, short batDauY, short goc, byte luc, byte lucTach, byte loaiDan, byte chiMang, byte avenger, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa, boolean epXuyenDiaHinh) {
        double[] cacDoLech;
        VXLHoSoDan hoSoDan = VXLCauHinhVatPhamChienDau.layHoSoDan((byte)loaiDan, (byte)avenger);
        if (epXuyenDiaHinh && !hoSoDan.xuyenDiaHinh()) {
            hoSoDan = new VXLHoSoDan(hoSoDan.ten(), hoSoDan.loaiClient(), hoSoDan.kieuBan(), hoSoDan.soVienThuong(), hoSoDan.soVienChiMang(), hoSoDan.khoangLechGoc(), hoSoDan.vatLy(), true, hoSoDan.xuyenNguoi(), hoSoDan.phanTramSatThuongMoiVien(), hoSoDan.tranPhanTramSatThuong(), hoSoDan.quayVe(), hoSoDan.tarzan());
        }
        int soQuyDao = hoSoDan.laySoVien(chiMang);
        short[][] cacDuongX = new short[soQuyDao][];
        short[][] cacDuongY = new short[soQuyDao][];
        int[][] cacMucTieu = new int[soQuyDao][];
        short[] vaChamDiaHinhX = VXLHeThongDan.taoMangKhongVaCham(soQuyDao);
        short[] vaChamDiaHinhY = VXLHeThongDan.taoMangKhongVaCham(soQuyDao);
        if (Byte.toUnsignedInt(loaiDan) == 17) {
            return this.hoanTatKetQua(this.taoDanApache(batDauX, batDauY, goc, luc, lucTach, loaiDan, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa), hoSoDan);
        }
        if (Byte.toUnsignedInt(loaiDan) == 19 && soQuyDao > 1) {
            return this.hoanTatKetQua(this.taoDanGa(batDauX, batDauY, goc, luc, lucTach, loaiDan, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa), hoSoDan);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.MAGENTA) {
            return this.hoanTatKetQua(this.taoDanMagenta(batDauX, batDauY, goc, luc, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa), hoSoDan);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.LASER) {
            return this.hoanTatKetQua(this.taoDanLaser(batDauX, batDauY, goc, hoSoDan, mucTieuBoQua), hoSoDan);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.VONG_TARZAN) {
            QuyDao tarzan = this.taoQuyDaoTarzan(batDauX, batDauY, goc, luc, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
            return this.hoanTatKetQua(new KetQuaPhatBan(new short[][]{tarzan.x}, new short[][]{tarzan.y}, new int[][]{tarzan.cacMucTieu}, new short[]{tarzan.vaChamDiaHinhX}, new short[]{tarzan.vaChamDiaHinhY}, -1), hoSoDan);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.QUAY_VE) {
            QuyDao quayVe = this.taoQuyDaoQuayVe(batDauX, batDauY, goc, luc, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
            return this.hoanTatKetQua(new KetQuaPhatBan(new short[][]{quayVe.x}, new short[][]{quayVe.y}, new int[][]{quayVe.cacMucTieu}, new short[]{quayVe.vaChamDiaHinhX}, new short[]{quayVe.vaChamDiaHinhY}, -1), hoSoDan);
        }
        if (hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.NHAN_VAT_LAO) {
            QuyDao nhanVatLao = this.taoQuyDaoNhanVatLao(batDauX, batDauY, goc, luc, mucTieuBoQua, buocThoiGian, soDiemToiDa);
            return this.hoanTatKetQua(new KetQuaPhatBan(new short[][]{nhanVatLao.x}, new short[][]{nhanVatLao.y}, new int[][]{nhanVatLao.cacMucTieu}, new short[]{nhanVatLao.vaChamDiaHinhX}, new short[]{nhanVatLao.vaChamDiaHinhY}, -1, nhanVatLao.truotRaNgoaiBanDo), hoSoDan);
        }
        double[] dArray = cacDoLech = Byte.toUnsignedInt(loaiDan) == 2 ? VXLHeThongDan.taoDoLechBaTia(soQuyDao, hoSoDan.khoangLechGoc()) : hoSoDan.taoDoLechGoc(chiMang);
        if (Byte.toUnsignedInt(loaiDan) == 11 && soQuyDao == MAU_LECH_GOC_MG.length) {
            cacDoLech = new double[soQuyDao];
            for (int i = 0; i < soQuyDao; ++i) {
                cacDoLech[i] = MAU_LECH_GOC_MG[i] * hoSoDan.khoangLechGoc();
            }
        }
        QuyDao quyDaoGoc = null;
        for (int i = 0; i < soQuyDao; ++i) {
            QuyDao quyDao;
            if ((hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.DAN_KEP || hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.LIEN_THANH) && hoSoDan.khoangLechGoc() <= 0.0 && quyDaoGoc != null) {
                quyDao = VXLHeThongDan.saoChepQuyDao(quyDaoGoc);
            } else {
                short gocVien = VXLHeThongDan.chuanHoaGoc((double)goc + cacDoLech[i]);
                quyDao = this.taoQuyDao(batDauX, batDauY, gocVien, luc, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
                if (quyDaoGoc == null) {
                    quyDaoGoc = quyDao;
                }
            }
            cacDuongX[i] = quyDao.x;
            cacDuongY[i] = quyDao.y;
            cacMucTieu[i] = quyDao.cacMucTieu;
            vaChamDiaHinhX[i] = quyDao.vaChamDiaHinhX;
            vaChamDiaHinhY[i] = quyDao.vaChamDiaHinhY;
        }
        return this.hoanTatKetQua(new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, vaChamDiaHinhX, vaChamDiaHinhY, -1), hoSoDan);
    }

    private KetQuaPhatBan hoanTatKetQua(KetQuaPhatBan ketQua, VXLHoSoDan hoSoDan) {
        if (ketQua == null) {
            return ketQua;
        }
        int soQuyDao = Math.min(ketQua.duongX.length, ketQua.duongY.length);
        for (int i = 0; i < soQuyDao; ++i) {
            boolean danMeApacheChuaTach = Byte.toUnsignedInt(hoSoDan.loaiClient()) == 17
                    && i == 0 && ketQua.chiSoTach > 0 && ketQua.duongX.length > 1
                    && ketQua.duongX[0] != null
                    && ketQua.chiSoTach - 1 < ketQua.duongX[0].length - 1;
            boolean daCoDiemNo = ketQua.vaChamDiaHinhX[i] != Short.MIN_VALUE
                    && ketQua.vaChamDiaHinhY[i] != Short.MIN_VALUE;
            if (danMeApacheChuaTach || daCoDiemNo) {
                continue;
            }
            short[] duongX = ketQua.duongX[i];
            short[] duongY = ketQua.duongY[i];
            if (duongX == null || duongY == null) {
                continue;
            }
            int soDiem = Math.min(duongX.length, duongY.length);
            boolean chamDiaHinh = false;
            if (!hoSoDan.xuyenDiaHinh()) {
                for (int j = 1; j < soDiem; ++j) {
                    short x = duongX[j];
                    short y = duongY[j];
                    if (x < 0 || x >= this.banDo.getWidth() || y < 0
                            || y >= this.banDo.getHeight() || !this.banDo.coVaCham(x, y)) {
                        continue;
                    }
                    ketQua.vaChamDiaHinhX[i] = x;
                    ketQua.vaChamDiaHinhY[i] = y;
                    chamDiaHinh = true;
                    break;
                }
            }
            boolean coMucTieuTrucTiep = i < ketQua.cacMucTieuTheoQuyDao.length
                    && ketQua.cacMucTieuTheoQuyDao[i] != null
                    && ketQua.cacMucTieuTheoQuyDao[i].length > 0;
            if (!coMucTieuTrucTiep || hoSoDan.kieuBan() == VXLHoSoDan.KieuBan.LASER) {
                continue;
            }
            if (Byte.toUnsignedInt(hoSoDan.loaiClient()) == 83) {
                short[] diemNoCaptain = this.timDiemTrungMucTieuDauTien(duongX, duongY,
                        ketQua.cacMucTieuTheoQuyDao[i]);
                if (diemNoCaptain != null) {
                    ketQua.vaChamDiaHinhX[i] = diemNoCaptain[0];
                    ketQua.vaChamDiaHinhY[i] = diemNoCaptain[1];
                }
            } else if (!chamDiaHinh && !hoSoDan.xuyenNguoi() && soDiem > 0) {
                ketQua.vaChamDiaHinhX[i] = duongX[soDiem - 1];
                ketQua.vaChamDiaHinhY[i] = duongY[soDiem - 1];
            }
        }
        return ketQua;
    }

    private short[] timDiemTrungMucTieuDauTien(short[] duongX, short[] duongY,
            int[] cacMucTieuTrucTiep) {
        if (this.boTimMucTieu == null || duongX == null || duongY == null
                || cacMucTieuTrucTiep == null || cacMucTieuTrucTiep.length == 0) {
            return null;
        }
        int soDiem = Math.min(duongX.length, duongY.length);
        for (int i = 0; i < soDiem; i++) {
            int x1 = i == 0 ? duongX[i] : duongX[i - 1];
            int y1 = i == 0 ? duongY[i] : duongY[i - 1];
            int x2 = duongX[i];
            int y2 = duongY[i];
            int x = x1;
            int y = y1;
            int dx = Math.abs(x2 - x1);
            int dy = -Math.abs(y2 - y1);
            int buocX = Integer.compare(x2, x1);
            int buocY = Integer.compare(y2, y1);
            int saiSo = dx + dy;
            while (true) {
                int mucTieu = this.boTimMucTieu.timMucTieu(x, y, LE_TRUNG_MAC_DINH, -1);
                if (chuaMucTieu(cacMucTieuTrucTiep, mucTieu)) {
                    return new short[]{(short)x, (short)y};
                }
                if (x == x2 && y == y2) {
                    break;
                }
                int haiLanSaiSo = saiSo * 2;
                if (haiLanSaiSo >= dy) {
                    saiSo += dy;
                    x += buocX;
                }
                if (haiLanSaiSo <= dx) {
                    saiSo += dx;
                    y += buocY;
                }
            }
        }
        return null;
    }

    private static boolean chuaMucTieu(int[] cacMucTieu, int mucTieu) {
        if (mucTieu < 0) {
            return false;
        }
        for (int mucTieuTrucTiep : cacMucTieu) {
            if (mucTieuTrucTiep == mucTieu) {
                return true;
            }
        }
        return false;
    }

    private QuyDao taoQuyDaoNhanVatLao(short batDauX, short batDauY, short goc, byte luc, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        int gioiHanDiem = Math.max(SO_DIEM_TOI_THIEU_HULK, soDiemToiDa);
        double dt = Math.max(0.2, Math.min(0.35, buocThoiGian));
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double radian = Math.toRadians(goc);
        double tocDo = Math.max(12.0, (double)lucBan * 0.95);
        boolean nhayTaiCho = goc == 89 && lucBan >= 30;
        double vanTocXBanDau = nhayTaiCho ? 0.0 : Math.cos(radian) * tocDo;
        double vanTocYBanDau = -Math.max(8.0, Math.abs(Math.sin(radian)) * tocDo);
        double trongLucHulk = 1.0;
        double doRoiToiDayBanDo = Math.max(0.0, (double)this.banDo.getHeight() + 20.0 - (double)batDauY);
        double thoiGianToiDayBanDo = (-vanTocYBanDau
                + Math.sqrt(vanTocYBanDau * vanTocYBanDau + 2.0 * trongLucHulk * doRoiToiDayBanDo))
                / trongLucHulk;
        gioiHanDiem = Math.max(gioiHanDiem, (int)Math.ceil(thoiGianToiDayBanDo / dt) + 2);
        ArrayList<Short> xs = new ArrayList<Short>();
        ArrayList<Short> ys = new ArrayList<Short>();
        ArrayList<Integer> cacMucTieu = new ArrayList<Integer>();
        HashSet<Integer> mucTieuDaTrung = new HashSet<Integer>();
        int xTruoc = batDauX;
        int yTruoc = batDauY;
        short vaChamDiaHinhX = Short.MIN_VALUE;
        short vaChamDiaHinhY = Short.MIN_VALUE;
        boolean daQuaPhaCatCanh = false;
        boolean daDuDoCaoTrungMucTieu = false;
        xs.add((short)batDauX);
        ys.add((short)batDauY);
        for (int i = 1; i < gioiHanDiem; ++i) {
            KetQuaVaCham vaCham;
            double thoiGian = (double)i * dt;
            double xMoiThuc = (double)batDauX + vanTocXBanDau * thoiGian;
            double yMoiThuc = (double)batDauY + vanTocYBanDau * thoiGian
                    + 0.5 * trongLucHulk * thoiGian * thoiGian;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            if (xMoi < 0 || xMoi >= this.banDo.getWidth()) {
                xMoi = xMoi < 0 ? -20 : this.banDo.getWidth() + 20;
                yMoi = Math.max(-180, Math.min(this.banDo.getHeight() + 20, yMoi));
                if (xs.get(xs.size() - 1) != (short)xMoi || ys.get(ys.size() - 1) != (short)yMoi) {
                    xs.add((short)xMoi);
                    ys.add((short)yMoi);
                }
                return this.taoKetQuaHulkBanTruot(xs, ys, cacMucTieu);
            }
            if (yMoi >= this.banDo.getHeight()) {
                yMoi = this.banDo.getHeight() + 20;
                if (xs.get(xs.size() - 1) != (short)xMoi || ys.get(ys.size() - 1) != (short)yMoi) {
                    xs.add((short)xMoi);
                    ys.add((short)yMoi);
                }
                return this.taoKetQuaHulkBanTruot(xs, ys, cacMucTieu);
            }
            int soMucTieuTruoc = cacMucTieu.size();
            int doCaoDaBat = batDauY - Math.min(yTruoc, yMoi);
            if (!daQuaPhaCatCanh && doCaoDaBat >= 14) {
                daQuaPhaCatCanh = true;
            }
            int doCaoDauDoan = batDauY - yTruoc;
            if (!daDuDoCaoTrungMucTieu && doCaoDauDoan >= 26) {
                daDuDoCaoTrungMucTieu = true;
            }
            if ((vaCham = this.timVaChamThanHulkTrenDoan(xTruoc, yTruoc, xMoi, yMoi, daDuDoCaoTrungMucTieu, daQuaPhaCatCanh, mucTieuBoQua, mucTieuDaTrung, cacMucTieu)) != null) {
                boolean trungMucTieu = cacMucTieu.size() > soMucTieuTruoc;
                short xDap = vaCham.x;
                short yDap = vaCham.y;
                if (!trungMucTieu && vaCham.datChan) {
                    yDap = this.banDo.timViTriDat(xDap, (short)Math.max(0, Math.min(this.banDo.getHeight() - 1, vaCham.y)));
                }
                if (xs.get(xs.size() - 1) != (short)xDap || ys.get(ys.size() - 1) != (short)yDap) {
                    xs.add(xDap);
                    ys.add(yDap);
                }
                if (!vaCham.chamDiaHinh) break;
                vaChamDiaHinhX = xDap;
                vaChamDiaHinhY = yDap;
                break;
            }
            if (xs.get(xs.size() - 1) != (short)xMoi || ys.get(ys.size() - 1) != (short)yMoi) {
                xs.add((short)xMoi);
                ys.add((short)yMoi);
            }
            xTruoc = xMoi;
            yTruoc = yMoi;
        }
        return new QuyDao(VXLHeThongDan.chuyenDanhSachDiem(xs), VXLHeThongDan.chuyenDanhSachDiem(ys), VXLHeThongDan.chuyenDanhSachMucTieu(cacMucTieu), vaChamDiaHinhX, vaChamDiaHinhY);
    }

    private QuyDao taoKetQuaHulkBanTruot(ArrayList<Short> xs, ArrayList<Short> ys,
            ArrayList<Integer> cacMucTieu) {
        cacMucTieu.clear();
        int doDaiBayRa = Math.min(xs.size(), ys.size());
        for (int chiSo = doDaiBayRa - 2; chiSo >= 0; --chiSo) {
            short xVe = xs.get(chiSo);
            short yVe = ys.get(chiSo);
            if (xs.get(xs.size() - 1) == xVe && ys.get(ys.size() - 1) == yVe) continue;
            xs.add(xVe);
            ys.add(yVe);
        }
        short xGoc = xs.get(0);
        short yGoc = ys.get(0);
        xs.add(xGoc);
        ys.add(yGoc);
        xs.add(xGoc);
        ys.add(yGoc);
        return new QuyDao(VXLHeThongDan.chuyenDanhSachDiem(xs),
                VXLHeThongDan.chuyenDanhSachDiem(ys), new int[0], true);
    }

    private KetQuaVaCham timVaChamThanHulkTrenDoan(int x1, int y1, int x2, int y2, boolean kiemTraMucTieu, boolean kiemTraDiaHinh, int mucTieuBoQua, Set<Integer> mucTieuDaTrung, List<Integer> cacMucTieu) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
        for (int buoc = 1; buoc <= soBuoc; ++buoc) {
            int loaiVaChamDiaHinh;
            int mucTieu;
            int x = x1 + dx * buoc / soBuoc;
            int y = y1 + dy * buoc / soBuoc;
            if (kiemTraMucTieu && this.boTimMucTieu != null && (mucTieu = this.timMucTieuChamThanHulk(x, y, mucTieuBoQua)) >= 0 && mucTieuDaTrung.add(mucTieu)) {
                cacMucTieu.add(mucTieu);
                return new KetQuaVaCham((short)x, (short)y);
            }
            if (!kiemTraDiaHinh || (loaiVaChamDiaHinh = this.layLoaiVaChamDiaHinhThanHulk(x, y, dx, dy)) <= 0) continue;
            return new KetQuaVaCham((short)x, (short)y, true, loaiVaChamDiaHinh == 1);
        }
        return null;
    }

    private int timMucTieuChamThanHulk(int x, int yChan, int mucTieuBoQua) {
        for (int lechY = -34; lechY < -1; lechY += 5) {
            for (int lechX = -10; lechX <= 10; lechX += 5) {
                int mucTieu = this.boTimMucTieu.timMucTieu(x + lechX, yChan + lechY, 0, mucTieuBoQua);
                if (mucTieu < 0) continue;
                return mucTieu;
            }
        }
        for (int lechX = -10; lechX <= 10; lechX += 5) {
            int mucTieu = this.boTimMucTieu.timMucTieu(x + lechX, yChan - 1, 0, mucTieuBoQua);
            if (mucTieu < 0) continue;
            return mucTieu;
        }
        return -1;
    }

    private int layLoaiVaChamDiaHinhThanHulk(int x, int yChan, int dx, int dy) {
        int yDau = yChan - 35;
        int yDay = yChan - 1;
        if (dy > 0) {
            for (int lechX = -8; lechX <= 8; ++lechX) {
                if (!this.diemThanHulkChamDiaHinh(x + lechX, yDay)) continue;
                return 1;
            }
        } else if (dy < 0) {
            for (int lechX = -10; lechX <= 10; ++lechX) {
                if (!this.diemThanHulkChamDiaHinh(x + lechX, yDau)) continue;
                return 2;
            }
        }
        if (dx == 0) {
            return 0;
        }
        int xCanhDan = x + (dx > 0 ? 10 : -10);
        int yCanhKetThuc = dy < 0 ? yDau + 17 : yDay - 5;
        for (int y = yDau + 3; y <= yCanhKetThuc; ++y) {
            if (!this.diemThanHulkChamDiaHinh(xCanhDan, y)) continue;
            return 2;
        }
        return 0;
    }

    private boolean diemThanHulkChamDiaHinh(int x, int y) {
        return x >= 0 && x < this.banDo.getWidth() && y >= 0 && y < this.banDo.getHeight() && this.banDo.coVaCham((short)x, (short)y);
    }

    private KetQuaPhatBan taoDanLaser(short batDauX, short batDauY, short goc, VXLHoSoDan hoSoDan, int mucTieuBoQua) {
        double radian = Math.toRadians(goc);
        double huongX = Math.cos(radian);
        double huongY = -Math.sin(radian);
        double xGoc = (double)batDauX + huongX * 30.0;
        double yGoc = (double)batDauY - 17.0 + huongY * 30.0;
        int xDau = (int)Math.round(Math.max(0.0, Math.min((double)this.banDo.getWidth() - 1.0, xGoc)));
        int yDau = (int)Math.round(Math.max(0.0, Math.min((double)this.banDo.getHeight() - 1.0, yGoc)));
        double khoangCachToiBien = Double.POSITIVE_INFINITY;
        if (huongX > 1.0E-4) {
            khoangCachToiBien = Math.min(khoangCachToiBien, ((double)this.banDo.getWidth() - 1.0 - (double)xDau) / huongX);
        } else if (huongX < -1.0E-4) {
            khoangCachToiBien = Math.min(khoangCachToiBien, (0.0 - (double)xDau) / huongX);
        }
        if (huongY > 1.0E-4) {
            khoangCachToiBien = Math.min(khoangCachToiBien, ((double)this.banDo.getHeight() - 1.0 - (double)yDau) / huongY);
        } else if (huongY < -1.0E-4) {
            khoangCachToiBien = Math.min(khoangCachToiBien, (0.0 - (double)yDau) / huongY);
        }
        if (!Double.isFinite(khoangCachToiBien) || khoangCachToiBien < 0.0) {
            khoangCachToiBien = 0.0;
        }
        int xCuoi = (int)Math.round((double)xDau + huongX * khoangCachToiBien);
        int yCuoi = (int)Math.round((double)yDau + huongY * khoangCachToiBien);
        xCuoi = Math.max(0, Math.min(this.banDo.getWidth() - 1, xCuoi));
        yCuoi = Math.max(0, Math.min(this.banDo.getHeight() - 1, yCuoi));
        ArrayList<Integer> cacMucTieu = new ArrayList<Integer>();
        HashSet<Integer> mucTieuDaTrung = new HashSet<Integer>();
        KetQuaVaCham vaCham = this.timVaChamTrenDoan(xDau, yDau, xCuoi, yCuoi, hoSoDan.xuyenDiaHinh(), hoSoDan.xuyenNguoi(), mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
        if (vaCham != null) {
            xCuoi = vaCham.x;
            yCuoi = vaCham.y;
        }
        return new KetQuaPhatBan(new short[][]{{(short)xDau, (short)xCuoi}}, new short[][]{{(short)yDau, (short)yCuoi}}, new int[][]{VXLHeThongDan.chuyenDanhSachMucTieu(cacMucTieu)}, -1);
    }

    private KetQuaPhatBan taoDanMagenta(short batDauX, short batDauY, short goc, byte luc, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        double doLechY;
        int chiSoDinh;
        double doLechX;
        double doDaiHuong;
        VXLHoSoDan.VatLy vatLy = hoSoDan.vatLy();
        int gioiHanDiem = Math.max(24, soDiemToiDa);
        double dt = Math.max(0.1, buocThoiGian);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double radian = Math.toRadians(goc);
        double xHienTai = (double)batDauX + Math.cos(radian) * 30.0;
        double yHienTai = (double)batDauY - 17.0 - Math.sin(radian) * 30.0;
        double vanTocX = Math.cos(radian) * (double)lucBan * vatLy.heSoTocDoTheoKhung();
        double vanTocY = -Math.sin(radian) * (double)lucBan * vatLy.heSoTocDoTheoKhung();
        double heSoGioTheoKhung = VXLHeThongDan.tinhHeSoGioTheoKhung(vatLy);
        double giaTocX = (double)gioX * heSoGioTheoKhung;
        double giaTocY = vatLy.giaTocTrongLucTheoKhung() - (double)gioY * heSoGioTheoKhung;
        ArrayList<Short> cacDiemX = new ArrayList<Short>();
        ArrayList<Short> cacDiemY = new ArrayList<Short>();
        ArrayList<Integer> cacMucTieu = new ArrayList<Integer>();
        HashSet<Integer> mucTieuDaTrung = new HashSet<Integer>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        cacDiemX.add((short)xTruoc);
        cacDiemY.add((short)yTruoc);
        boolean datDinh = false;
        boolean daVaCham = false;
        for (int chiSo = 1; chiSo < gioiHanDiem; ++chiSo) {
            boolean raNgoai;
            int yMoi;
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            int xMoi = (int)Math.round(xMoiThuc);
            KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(xTruoc, yTruoc, xMoi, yMoi = (int)Math.round(yMoiThuc), false, false, mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
                daVaCham = true;
            }
            if (cacDiemX.get(cacDiemX.size() - 1) != (short)xMoi || cacDiemY.get(cacDiemY.size() - 1) != (short)yMoi) {
                cacDiemX.add((short)xMoi);
                cacDiemY.add((short)yMoi);
            }
            boolean bl = raNgoai = xMoi < -200 || xMoi > this.banDo.getWidth() + 200 || yMoi < -600 || yMoi > this.banDo.getHeight() + 200;
            if (daVaCham || raNgoai) break;
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
            xTruoc = xMoi;
            yTruoc = yMoi;
            vanTocX += giaTocX * dt;
            vanTocY += giaTocY * dt;
            vanTocY = this.apDungLucKeoVoiRong(xMoi, yMoi, vanTocY);
            if (vanTocY < 0.0) continue;
            datDinh = true;
            break;
        }
        if (datDinh && !daVaCham && cacDiemX.size() >= 2 && (doDaiHuong = Math.hypot(doLechX = (double)(cacDiemX.get(chiSoDinh = cacDiemX.size() - 1) - cacDiemX.get(0)), doLechY = (double)(cacDiemY.get(chiSoDinh) - cacDiemY.get(0)))) > 0.001) {
            double buocLaserX = doLechX / doDaiHuong * (double)lucBan;
            double buocLaserY = doLechY / doDaiHuong * (double)lucBan;
            if (Math.abs(buocLaserX) < 1.0) {
                buocLaserX = Math.copySign(1.0, Math.abs(doLechX) > 0.001 ? doLechX : Math.cos(radian));
            }
            if (Math.abs(buocLaserY) < 1.0) {
                buocLaserY = Math.copySign(1.0, Math.abs(doLechY) > 0.001 ? doLechY : -Math.sin(radian));
            }
            double xLaser = cacDiemX.get(chiSoDinh).shortValue();
            double yLaser = cacDiemY.get(chiSoDinh).shortValue();
            int xLaserTruoc = (int)Math.round(xLaser);
            int yLaserTruoc = (int)Math.round(yLaser);
            int gioiHanLaser = Math.max(40, (this.banDo.getWidth() + this.banDo.getHeight()) / Math.max(1, lucBan) + 40);
            for (int chiSo = 0; chiSo < gioiHanLaser; ++chiSo) {
                boolean raNgoai;
                int yLaserMoi;
                double xLaserMoiThuc = xLaser + buocLaserX;
                double yLaserMoiThuc = yLaser - buocLaserY;
                int xLaserMoi = (int)Math.round(xLaserMoiThuc);
                KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(xLaserTruoc, yLaserTruoc, xLaserMoi, yLaserMoi = (int)Math.round(yLaserMoiThuc), false, false, mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
                if (vaCham != null) {
                    xLaserMoi = vaCham.x;
                    yLaserMoi = vaCham.y;
                }
                xLaser = xLaserMoiThuc;
                yLaser = yLaserMoiThuc;
                xLaserTruoc = xLaserMoi;
                yLaserTruoc = yLaserMoi;
                boolean bl = raNgoai = xLaserMoi < -100 || xLaserMoi > this.banDo.getWidth() + 100 || yLaserMoi < -600 || yLaserMoi > this.banDo.getHeight() + 100;
                if (vaCham != null || raNgoai) break;
            }
            short xCuoi = (short)xLaserTruoc;
            short yCuoi = (short)yLaserTruoc;
            if (cacDiemX.get(cacDiemX.size() - 1) != xCuoi || cacDiemY.get(cacDiemY.size() - 1) != yCuoi) {
                cacDiemX.add(xCuoi);
                cacDiemY.add(yCuoi);
            }
        }
        return new KetQuaPhatBan(new short[][]{VXLHeThongDan.chuyenDanhSachDiem(cacDiemX)}, new short[][]{VXLHeThongDan.chuyenDanhSachDiem(cacDiemY)}, new int[][]{VXLHeThongDan.chuyenDanhSachMucTieu(cacMucTieu)}, -1);
    }

    private KetQuaPhatBan taoDanApache(short batDauX, short batDauY, short goc, byte luc, byte lucTach, byte loaiDan, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        boolean tachTruocVaCham;
        short[][] cacDuongX = new short[4][];
        short[][] cacDuongY = new short[4][];
        int[][] cacMucTieu = new int[4][];
        QuyDao danMe = this.taoQuyDao(batDauX, batDauY, goc, luc, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
        cacDuongX[0] = danMe.x;
        cacDuongY[0] = danMe.y;
        int khungTach = Math.max(4, Byte.toUnsignedInt(lucTach));
        int chiSoTach = Math.min(Math.max(0, khungTach - 1), Math.max(0, danMe.x.length - 1));
        boolean bl = tachTruocVaCham = danMe.x.length > 2 && chiSoTach < danMe.x.length - 1;
        if (!tachTruocVaCham) {
            cacMucTieu[0] = danMe.cacMucTieu;
            for (int i = 1; i < 4; ++i) {
                cacDuongX[i] = new short[]{danMe.x[danMe.x.length - 1]};
                cacDuongY[i] = new short[]{danMe.y[danMe.y.length - 1]};
                cacMucTieu[i] = new int[0];
            }
            return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, khungTach);
        }
        short xTach = danMe.x[chiSoTach];
        short yTach = danMe.y[chiSoTach];
        short gocChuan = VXLHeThongDan.chuanHoaGoc(goc);
        short gocVeNguoiBan = VXLHeThongDan.chuanHoaGoc(Math.toDegrees(Math.atan2(batDauY - yTach, batDauX - xTach)));
        short gocRiuGiua = VXLHeThongDan.chuanHoaGoc(gocChuan + gocVeNguoiBan);
        if (gocChuan < 90) {
            gocRiuGiua = VXLHeThongDan.chuanHoaGoc(180.0 - (double)gocRiuGiua);
        }
        byte lucRiuCon = (byte)Math.max(1, Byte.toUnsignedInt(luc) / 2);
        for (int i = 0; i < LECH_GOC_APACHE.length; ++i) {
            short gocRiu = VXLHeThongDan.chuanHoaGoc(gocRiuGiua + LECH_GOC_APACHE[i]);
            double radianRiu = Math.toRadians(gocRiu);
            short xRiu = (short)Math.round((double)xTach + Math.cos(radianRiu) * 20.0);
            short yRiu = (short)Math.round((double)yTach - 12.0 - Math.sin(radianRiu) * 20.0);
            QuyDao danCon = this.taoQuyDao(xRiu, yRiu, gocRiu, lucRiuCon, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, Math.max(20, soDiemToiDa - chiSoTach), false);
            cacDuongX[i + 1] = danCon.x;
            cacDuongY[i + 1] = danCon.y;
            cacMucTieu[i + 1] = danCon.cacMucTieu;
        }
        cacMucTieu[0] = new int[0];
        return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, khungTach);
    }

    private KetQuaPhatBan taoDanGa(short batDauX, short batDauY, short goc, byte luc, byte lucTach, byte loaiDan, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        short[][] cacDuongX = new short[2][];
        short[][] cacDuongY = new short[2][];
        int[][] cacMucTieu = new int[2][];
        QuyDao danMe = this.taoQuyDao(batDauX, batDauY, goc, luc, hoSoDan, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, true);
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
        VXLHoSoDan.VatLy vatLyTrung = VXLCauHinhVatPhamChienDau.layVatLyDanCon((byte)loaiDan);
        QuyDao quaTrung = this.taoQuyDaoTheoKhungTuVanToc(xTrung, yTrung, 0.0, 0.0, hoSoDan, vatLyTrung, gioX, gioY, mucTieuBoQua, buocThoiGian, Math.max(20, soDiemToiDa / 2));
        cacDuongX[1] = quaTrung.x;
        cacDuongY[1] = quaTrung.y;
        cacMucTieu[1] = quaTrung.cacMucTieu;
        return new KetQuaPhatBan(cacDuongX, cacDuongY, cacMucTieu, khungDeTrung);
    }

    private QuyDao taoQuyDao(short batDauX, short batDauY, short goc, byte luc, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa, boolean dungDauSung) {
        VXLHoSoDan.VatLy vatLy = hoSoDan.vatLy();
        if (vatLy.dungVatLyTheoKhung()) {
            return this.taoQuyDaoTheoKhung(batDauX, batDauY, goc, luc, hoSoDan, vatLy, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa, dungDauSung);
        }
        int gioiHanDiem = Math.max(1, soDiemToiDa);
        short[] xs = new short[gioiHanDiem];
        short[] ys = new short[gioiHanDiem];
        double radian = Math.toRadians(goc);
        double trongLuong = Math.max(0.25, vatLy.trongLuong());
        double tocDo = (double)Math.max(8, Byte.toUnsignedInt(luc)) * 0.85 / Math.sqrt(trongLuong);
        double trongLuc = 0.33 * Math.sqrt(trongLuong) * vatLy.heSoTrongLuc();
        double heSoGio = VXLHeThongDan.tinhHeSoGio(vatLy);
        double xGoc = batDauX;
        double yGoc = batDauY;
        if (dungDauSung) {
            xGoc += Math.cos(radian) * 30.0;
            yGoc -= 17.0 + Math.sin(radian) * 30.0;
        }
        int xTruoc = (int)Math.round(xGoc);
        int yTruoc = (int)Math.round(yGoc);
        int doDai = 0;
        double dt = Math.max(0.1, buocThoiGian);
        double vanTocCongThemVoiRongY = 0.0;
        double doLechVoiRongY = 0.0;
        ArrayList<Integer> cacMucTieu = new ArrayList<Integer>();
        HashSet<Integer> mucTieuDaTrung = new HashSet<Integer>();
        for (int i = 0; i < gioiHanDiem; ++i) {
            double thoiGian = (double)i * dt;
            int x = (int)Math.round(xGoc + Math.cos(radian) * tocDo * thoiGian + (double)gioX * heSoGio * thoiGian * thoiGian);
            int y = (int)Math.round(yGoc - Math.sin(radian) * tocDo * thoiGian
                    + (trongLuc - (double)gioY * heSoGio) * thoiGian * thoiGian
                    + doLechVoiRongY);
            boolean raNgoai = x < 0 || x >= this.banDo.getWidth() || y < -600 || y >= this.banDo.getHeight();
            x = Math.max(0, Math.min(this.banDo.getWidth() - 1, x));
            KetQuaVaCham vaCham = this.timVaChamTrenDoan(xTruoc, yTruoc, x, y = Math.max(-600, Math.min(this.banDo.getHeight() - 1, y)), hoSoDan.xuyenDiaHinh(), hoSoDan.xuyenNguoi(), mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                x = vaCham.x;
                y = vaCham.y;
            }
            xs[i] = (short)x;
            ys[i] = (short)y;
            doDai = i + 1;
            if (raNgoai || vaCham != null) break;
            xTruoc = x;
            yTruoc = y;
            if (i > 0) {
                vanTocCongThemVoiRongY = this.apDungLucKeoVoiRong(
                        x, y, vanTocCongThemVoiRongY);
                doLechVoiRongY += vanTocCongThemVoiRongY * dt;
            }
        }
        return new QuyDao(Arrays.copyOf(xs, Math.max(1, doDai)), Arrays.copyOf(ys, Math.max(1, doDai)), VXLHeThongDan.chuyenDanhSachMucTieu(cacMucTieu));
    }

    private QuyDao taoQuyDaoTheoKhung(short batDauX, short batDauY, short goc, byte luc, VXLHoSoDan hoSoDan, VXLHoSoDan.VatLy vatLy, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa, boolean dungDauSung) {
        double radian = Math.toRadians(goc);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double xGoc = batDauX;
        double yGoc = batDauY;
        if (dungDauSung) {
            xGoc += Math.cos(radian) * 30.0;
            yGoc -= 17.0 + Math.sin(radian) * 30.0;
        }
        double vanTocX = Math.cos(radian) * (double)lucBan * vatLy.heSoTocDoTheoKhung();
        double vanTocY = -Math.sin(radian) * (double)lucBan * vatLy.heSoTocDoTheoKhung();
        return this.taoQuyDaoTheoKhungTuVanToc((short)Math.round(xGoc), (short)Math.round(yGoc), vanTocX, vanTocY, hoSoDan, vatLy, gioX, gioY, mucTieuBoQua, buocThoiGian, soDiemToiDa);
    }

    private QuyDao taoQuyDaoTheoKhungTuVanToc(short batDauX, short batDauY, double vanTocX, double vanTocY, VXLHoSoDan hoSoDan, VXLHoSoDan.VatLy vatLy, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        int gioiHanDiem = Math.max(1, soDiemToiDa);
        double dt = Math.max(0.1, buocThoiGian);
        short[] xs = new short[gioiHanDiem];
        short[] ys = new short[gioiHanDiem];
        double xHienTai = batDauX;
        double yHienTai = batDauY;
        double heSoGioTheoKhung = VXLHeThongDan.tinhHeSoGioTheoKhung(vatLy);
        double giaTocX = (double)gioX * heSoGioTheoKhung;
        double giaTocY = vatLy.giaTocTrongLucTheoKhung() - (double)gioY * heSoGioTheoKhung;
        int doDai = 1;
        int xTruoc = batDauX;
        int yTruoc = batDauY;
        xs[0] = batDauX;
        ys[0] = batDauY;
        ArrayList<Integer> cacMucTieu = new ArrayList<Integer>();
        HashSet<Integer> mucTieuDaTrung = new HashSet<Integer>();
        for (int chiSo = 1; chiSo < gioiHanDiem; ++chiSo) {
            boolean raNgoai;
            int yMoi;
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            int xMoi = (int)Math.round(xMoiThuc);
            KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(xTruoc, yTruoc, xMoi, yMoi = (int)Math.round(yMoiThuc), hoSoDan.xuyenDiaHinh(), hoSoDan.xuyenNguoi(), mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
            }
            xs[doDai] = (short)xMoi;
            ys[doDai] = (short)yMoi;
            ++doDai;
            boolean bl = raNgoai = xMoi < -200 || xMoi > this.banDo.getWidth() + 200 || yMoi < -600 || yMoi > this.banDo.getHeight() + 200;
            if (vaCham != null || raNgoai) break;
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
            xTruoc = xMoi;
            yTruoc = yMoi;
            vanTocX += giaTocX * dt;
            vanTocY += giaTocY * dt;
            vanTocY = this.apDungLucKeoVoiRong(xMoi, yMoi, vanTocY);
        }
        return new QuyDao(Arrays.copyOf(xs, Math.max(1, doDai)), Arrays.copyOf(ys, Math.max(1, doDai)), VXLHeThongDan.chuyenDanhSachMucTieu(cacMucTieu));
    }

    private QuyDao taoQuyDaoTarzan(short batDauX, short batDauY, short goc, byte luc, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        int gioiHanDiem = Math.max(600, soDiemToiDa);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double dt = Math.max(0.1, buocThoiGian);
        short[] xs = new short[gioiHanDiem];
        short[] ys = new short[gioiHanDiem];
        double radian = Math.toRadians(goc);
        double trongLuong = Math.max(0.25, hoSoDan.trongLuong());
        double tocDoBan = (double)lucBan * 0.85 / Math.sqrt(trongLuong);
        double vanTocX = Math.cos(radian) * tocDoBan;
        double vanTocY = -Math.sin(radian) * tocDoBan;
        double giaTocTrongLuc = 0.66 * Math.sqrt(trongLuong) * hoSoDan.heSoTrongLuc();
        double heSoGio = 2.0 * VXLHeThongDan.tinhHeSoGio(hoSoDan.vatLy());
        double giaTocGioX = (double)gioX * heSoGio;
        double giaTocGioY = (double)(-gioY) * heSoGio;
        double xHienTai = (double)batDauX + Math.cos(radian) * 30.0;
        double yHienTai = (double)batDauY - 17.0 - Math.sin(radian) * 30.0;
        VXLHoSoDan.Tarzan tarzan = hoSoDan.tarzan() != null ? hoSoDan.tarzan() : new VXLHoSoDan.Tarzan(1.0, 2.0, 90);
        double huongNgoat = vanTocX <= 0.0 ? 1.0 : -1.0;
        int trangThaiNgoat = -1;
        int leNgoaiBanDo = Math.max(0, tarzan.leNgoaiBanDo());
        int doDai = 1;
        ArrayList<Integer> cacMucTieu = new ArrayList<Integer>();
        HashSet<Integer> mucTieuDaTrung = new HashSet<Integer>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        xs[0] = (short)xTruoc;
        ys[0] = (short)yTruoc;
        for (int chiSoDiem = 1; chiSoDiem < gioiHanDiem; ++chiSoDiem) {
            double xMoiThuc = xHienTai + vanTocX * dt;
            double yMoiThuc = yHienTai + vanTocY * dt;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            boolean raNgoai = xMoi < -leNgoaiBanDo || xMoi > this.banDo.getWidth() + leNgoaiBanDo || yMoi > this.banDo.getHeight() + 100;
            yMoi = Math.max(-600, Math.min(this.banDo.getHeight() + 100, yMoi));
            KetQuaVaCham vaCham = this.timVaChamTrongBanDoTrenDoan(xTruoc, yTruoc, xMoi, yMoi, hoSoDan.xuyenDiaHinh(), hoSoDan.xuyenNguoi(), mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
            }
            xs[doDai] = (short)xMoi;
            ys[doDai] = (short)yMoi;
            ++doDai;
            if (raNgoai || vaCham != null) break;
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
            } else if (vanTocY > 0.0) {
                trangThaiNgoat = 0;
            }
            vanTocY = this.apDungLucKeoVoiRong(xMoi, yMoi, vanTocY);
        }
        return new QuyDao(Arrays.copyOf(xs, Math.max(1, doDai)), Arrays.copyOf(ys, Math.max(1, doDai)), VXLHeThongDan.chuyenDanhSachMucTieu(cacMucTieu));
    }

    private KetQuaVaCham timVaChamTrongBanDoTrenDoan(int x1, int y1, int x2, int y2, boolean xuyenDiaHinh, boolean xuyenNguoi, int mucTieuBoQua, Set<Integer> mucTieuDaTrung, List<Integer> cacMucTieu) {
        int x = x1;
        int y = y1;
        int dx = Math.abs(x2 - x1);
        int dy = -Math.abs(y2 - y1);
        int buocX = Integer.compare(x2, x1);
        int buocY = Integer.compare(y2, y1);
        int saiSo = dx + dy;
        while (true) {
            int mucTieu;
            boolean trongBanDo;
            boolean bl = trongBanDo = x >= 0 && x < this.banDo.getWidth() && y >= 0 && y < this.banDo.getHeight();
            boolean uuTienMucTieu = this.banDo.layMaBanDo() == VXLQuanLyChien.MA_BAN_DO_BAO_VAY;
            if (uuTienMucTieu && trongBanDo && this.boTimMucTieu != null
                    && (mucTieu = this.boTimMucTieu.timMucTieu(x, y, 0, mucTieuBoQua)) >= 0
                    && mucTieuDaTrung.add(mucTieu)) {
                cacMucTieu.add(mucTieu);
                if (!xuyenNguoi) {
                    return new KetQuaVaCham((short)x, (short)y);
                }
            }
            if (!xuyenDiaHinh && trongBanDo && this.banDo.coVaCham((short)x, (short)y)) {
                return new KetQuaVaCham((short)x, (short)y, true, false);
            }
            if (!uuTienMucTieu && trongBanDo && this.boTimMucTieu != null
                    && (mucTieu = this.boTimMucTieu.timMucTieu(x, y, 0, mucTieuBoQua)) >= 0
                    && mucTieuDaTrung.add(mucTieu)) {
                cacMucTieu.add(mucTieu);
                if (!xuyenNguoi) {
                    return new KetQuaVaCham((short)x, (short)y);
                }
            }
            if (x == x2 && y == y2) break;
            int haiLanSaiSo = saiSo * 2;
            if (haiLanSaiSo >= dy) {
                saiSo += dy;
                x += buocX;
            }
            if (haiLanSaiSo > dx) continue;
            saiSo += dx;
            y += buocY;
        }
        return null;
    }

    private QuyDao taoQuyDaoQuayVe(short batDauX, short batDauY, short goc, byte luc, VXLHoSoDan hoSoDan, byte gioX, byte gioY, int mucTieuBoQua, double buocThoiGian, int soDiemToiDa) {
        int gioiHanDiem = Math.max(24, soDiemToiDa);
        int gioiHanBayRa = Math.max(12, gioiHanDiem / 2);
        int lucBan = Math.max(8, Byte.toUnsignedInt(luc));
        double dt = Math.max(0.1, buocThoiGian);
        double radian = Math.toRadians(goc);
        double trongLuong = Math.max(0.25, hoSoDan.trongLuong());
        double tocDoBan = (double)lucBan * 0.85 / Math.sqrt(trongLuong);
        double vanTocX = Math.cos(radian) * tocDoBan;
        double vanTocY = -Math.sin(radian) * tocDoBan;
        double giaTocTrongLuc = 0.66 * Math.sqrt(trongLuong) * hoSoDan.heSoTrongLuc();
        double heSoGio = 2.0 * VXLHeThongDan.tinhHeSoGio(hoSoDan.vatLy());
        double giaTocGioX = (double)gioX * heSoGio;
        double giaTocGioY = (double)(-gioY) * heSoGio;
        double xThuVe = batDauX;
        double yThuVe = (double)batDauY - 17.0;
        double xHienTai = (double)batDauX + Math.cos(radian) * 30.0;
        double yHienTai = (double)batDauY - 17.0 - Math.sin(radian) * 30.0;
        VXLHoSoDan.QuayVe quayVe = hoSoDan.quayVe() != null ? hoSoDan.quayVe() : new VXLHoSoDan.QuayVe(70.0, 6.0, 15.0, 1.1, 26.0, 18);
        double tamBayToiDa = Math.max(60.0, quayVe.tamBayCoBan() + (double)lucBan * quayVe.tamBayTheoLuc());
        double tocDoQuayVe = Math.max(10.0, tocDoBan * quayVe.heSoTocDoQuayVe());
        double quangDuongBayRa = 0.0;
        double thoiGianBayRa = 0.0;
        boolean chamDiaHinh = false;
        boolean raNgoaiBanDo = false;
        ArrayList<Short> xs = new ArrayList<Short>();
        ArrayList<Short> ys = new ArrayList<Short>();
        ArrayList<Integer> cacMucTieu = new ArrayList<Integer>();
        HashSet<Integer> mucTieuDaTrungBayRa = new HashSet<Integer>();
        HashSet<Integer> mucTieuDaTrungQuayVe = new HashSet<Integer>();
        int xTruoc = (int)Math.round(xHienTai);
        int yTruoc = (int)Math.round(yHienTai);
        xs.add((short)xTruoc);
        ys.add((short)yTruoc);
        for (int i = 1; i < gioiHanBayRa; ++i) {
            double xMoiThuc = xHienTai + (vanTocX += giaTocGioX * dt) * dt;
            double yMoiThuc = yHienTai + (vanTocY += (giaTocTrongLuc + giaTocGioY) * dt) * dt;
            quangDuongBayRa += Math.hypot(xMoiThuc - xHienTai, yMoiThuc - yHienTai);
            thoiGianBayRa += dt;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            raNgoaiBanDo = xMoi < 0 || xMoi >= this.banDo.getWidth() || yMoi < -600 || yMoi >= this.banDo.getHeight();
            xMoi = Math.max(0, Math.min(this.banDo.getWidth() - 1, xMoi));
            KetQuaVaCham vaCham = this.timVaChamTrenDoan(xTruoc, yTruoc, xMoi, yMoi = Math.max(-600, Math.min(this.banDo.getHeight() - 1, yMoi)), false, true, mucTieuBoQua, mucTieuDaTrungBayRa, cacMucTieu);
            if (vaCham != null) {
                xMoi = vaCham.x;
                yMoi = vaCham.y;
                chamDiaHinh = true;
            }
            xs.add((short)xMoi);
            ys.add((short)yMoi);
            if (raNgoaiBanDo || chamDiaHinh) break;
            xHienTai = xMoiThuc;
            yHienTai = yMoiThuc;
            xTruoc = xMoi;
            yTruoc = yMoi;
            vanTocY = this.apDungLucKeoVoiRong(xMoi, yMoi, vanTocY);
            if (quangDuongBayRa >= tamBayToiDa || thoiGianBayRa >= quayVe.thoiGianBayRaToiDa()) break;
        }
        int doDaiBayRa = xs.size();
        this.themDuongQuayNguocMem(xs, ys, doDaiBayRa, xThuVe, yThuVe, tocDoQuayVe * dt, gioiHanDiem, mucTieuBoQua, mucTieuDaTrungQuayVe, cacMucTieu);
        return new QuyDao(VXLHeThongDan.chuyenDanhSachDiem(xs), VXLHeThongDan.chuyenDanhSachDiem(ys), VXLHeThongDan.chuyenDanhSachMucTieu(cacMucTieu));
    }

    private void themDuongQuayNguocMem(ArrayList<Short> xs, ArrayList<Short> ys, int doDaiBayRa, double xThuVe, double yThuVe, double buocDiem, int gioiHanDiem, int mucTieuBoQua, Set<Integer> mucTieuDaTrung, List<Integer> cacMucTieu) {
        ArrayList<Double> duongNguocX = new ArrayList<Double>();
        ArrayList<Double> duongNguocY = new ArrayList<Double>();
        duongNguocX.add(Double.valueOf(xs.get(xs.size() - 1).shortValue()));
        duongNguocY.add(Double.valueOf(ys.get(ys.size() - 1).shortValue()));
        for (int chiSo = doDaiBayRa - 2; chiSo >= 0; --chiSo) {
            duongNguocX.add(Double.valueOf(xs.get(chiSo).shortValue()));
            duongNguocY.add(Double.valueOf(ys.get(chiSo).shortValue()));
        }
        if (Math.hypot((Double)duongNguocX.get(duongNguocX.size() - 1) - xThuVe, (Double)duongNguocY.get(duongNguocY.size() - 1) - yThuVe) > 0.5) {
            duongNguocX.add(xThuVe);
            duongNguocY.add(yThuVe);
        }
        double[] tongDoDai = new double[duongNguocX.size()];
        for (int chiSo = 1; chiSo < duongNguocX.size(); ++chiSo) {
            tongDoDai[chiSo] = tongDoDai[chiSo - 1] + Math.hypot((Double)duongNguocX.get(chiSo) - (Double)duongNguocX.get(chiSo - 1), (Double)duongNguocY.get(chiSo) - (Double)duongNguocY.get(chiSo - 1));
        }
        double doDaiToanBo = tongDoDai[tongDoDai.length - 1];
        if (doDaiToanBo <= 0.001) {
            return;
        }
        int xTruoc = xs.get(xs.size() - 1).shortValue();
        int yTruoc = ys.get(ys.size() - 1).shortValue();
        if (xs.size() < gioiHanDiem - 1) {
            xs.add((short)xTruoc);
            ys.add((short)yTruoc);
        }
        double buocOnDinh = Math.max(6.0, Math.min(12.0, buocDiem * 0.65));
        double quangDuongDaDi = 0.0;
        int chiSoDoan = 1;
        int khungTangToc = 0;
        while (xs.size() < gioiHanDiem && quangDuongDaDi < doDaiToanBo) {
            int soDiemConLai = Math.max(1, gioiHanDiem - xs.size());
            double buocCanThiet = (doDaiToanBo - quangDuongDaDi) / (double)soDiemConLai;
            double buocTangToc = Math.min(buocOnDinh, 2.0 + (double)khungTangToc * 2.0);
            quangDuongDaDi = Math.min(doDaiToanBo, quangDuongDaDi + Math.max(buocTangToc, buocCanThiet));
            while (chiSoDoan < tongDoDai.length - 1 && tongDoDai[chiSoDoan] < quangDuongDaDi) {
                ++chiSoDoan;
            }
            int chiSoTruoc = Math.max(0, chiSoDoan - 1);
            double doDaiDoan = tongDoDai[chiSoDoan] - tongDoDai[chiSoTruoc];
            double tiLe = doDaiDoan <= 1.0E-4 ? 0.0 : (quangDuongDaDi - tongDoDai[chiSoTruoc]) / doDaiDoan;
            int xMoi = (int)Math.round((Double)duongNguocX.get(chiSoTruoc) + ((Double)duongNguocX.get(chiSoDoan) - (Double)duongNguocX.get(chiSoTruoc)) * tiLe);
            int yMoi = (int)Math.round((Double)duongNguocY.get(chiSoTruoc) + ((Double)duongNguocY.get(chiSoDoan) - (Double)duongNguocY.get(chiSoTruoc)) * tiLe);
            this.timVaChamTrenDoan(xTruoc, yTruoc, xMoi, yMoi, true, true, mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            xs.add((short)xMoi);
            ys.add((short)yMoi);
            xTruoc = xMoi;
            yTruoc = yMoi;
            ++khungTangToc;
        }
        xs.set(xs.size() - 1, (short)Math.round(xThuVe));
        ys.set(ys.size() - 1, (short)Math.round(yThuVe));
    }

    private boolean themVongQuayVeMuon(ArrayList<Short> xs, ArrayList<Short> ys, double xThuVe, double yThuVe, double buocDiem, double gocXoayToiDa, int gioiHanDiem, int mucTieuBoQua, Set<Integer> mucTieuDaTrung, List<Integer> cacMucTieu) {
        double incomingY;
        if (xs.size() < 2 || xs.size() >= gioiHanDiem - 1) {
            return false;
        }
        int chiSoCuoi = xs.size() - 1;
        double incomingX = xs.get(chiSoCuoi) - xs.get(chiSoCuoi - 1);
        if (Math.hypot(incomingX, incomingY = (double)(ys.get(chiSoCuoi) - ys.get(chiSoCuoi - 1))) < 0.001) {
            return false;
        }
        double buocOnDinh = Math.max(6.0, Math.min(12.0, buocDiem * 0.65));
        double gocXoayOnDinh = Math.max(Math.toRadians(8.0), Math.min(Math.toRadians(18.0), gocXoayToiDa));
        int soDiemConLai = gioiHanDiem - xs.size();
        int huongUonLen = incomingX >= 0.0 ? -1 : 1;
        short[][] vongVe = this.taoVongQuayVeTheoHuong(xs.get(chiSoCuoi).shortValue(), ys.get(chiSoCuoi).shortValue(), incomingX, incomingY, xThuVe, yThuVe, buocOnDinh, gocXoayOnDinh, huongUonLen, soDiemConLai);
        if (vongVe == null) {
            vongVe = this.taoVongQuayVeTheoHuong(xs.get(chiSoCuoi).shortValue(), ys.get(chiSoCuoi).shortValue(), incomingX, incomingY, xThuVe, yThuVe, buocOnDinh, gocXoayOnDinh, -huongUonLen, soDiemConLai);
        }
        if (vongVe == null) {
            return false;
        }
        short xTruoc = xs.get(chiSoCuoi);
        short yTruoc = ys.get(chiSoCuoi);
        for (int chiSo = 0; chiSo < vongVe[0].length; ++chiSo) {
            short xMoi = vongVe[0][chiSo];
            short yMoi = vongVe[1][chiSo];
            this.timVaChamTrenDoan(xTruoc, yTruoc, xMoi, yMoi, true, true, mucTieuBoQua, mucTieuDaTrung, cacMucTieu);
            xs.add(xMoi);
            ys.add(yMoi);
            xTruoc = xMoi;
            yTruoc = yMoi;
        }
        return true;
    }

    private short[][] taoVongQuayVeTheoHuong(double xBatDau, double yBatDau, double incomingX, double incomingY, double xThuVe, double yThuVe, double buocDiem, double gocXoayToiDa, int huongUonBanDau, int soDiemToiDa) {
        ArrayList<Short> duongX = new ArrayList<Short>();
        ArrayList<Short> duongY = new ArrayList<Short>();
        double xHienTai = xBatDau;
        double yHienTai = yBatDau;
        double gocHienTai = Math.atan2(incomingY, incomingX);
        for (int chiSo = 0; chiSo < soDiemToiDa; ++chiSo) {
            double denChuX = xThuVe - xHienTai;
            double denChuY = yThuVe - yHienTai;
            double khoangCachDenChu = Math.hypot(denChuX, denChuY);
            if (khoangCachDenChu <= buocDiem) {
                duongX.add((short)Math.round(xThuVe));
                duongY.add((short)Math.round(yThuVe));
                return new short[][]{VXLHeThongDan.chuyenDanhSachDiem(duongX), VXLHeThongDan.chuyenDanhSachDiem(duongY)};
            }
            double gocMucTieu = Math.atan2(denChuY, denChuX);
            double doLech = Math.atan2(Math.sin(gocMucTieu - gocHienTai), Math.cos(gocMucTieu - gocHienTai));
            if (Math.abs(doLech) > gocXoayToiDa) {
                int huongXoay;
                int n = huongXoay = doLech >= 0.0 ? 1 : -1;
                if (chiSo == 0 && Math.PI - Math.abs(doLech) <= gocXoayToiDa * 2.0) {
                    huongXoay = huongUonBanDau;
                }
                gocHienTai += (double)huongXoay * gocXoayToiDa;
            } else {
                gocHienTai = gocMucTieu;
            }
            double xMoiThuc = xHienTai + Math.cos(gocHienTai) * buocDiem;
            double yMoiThuc = yHienTai + Math.sin(gocHienTai) * buocDiem;
            int xMoi = (int)Math.round(xMoiThuc);
            int yMoi = (int)Math.round(yMoiThuc);
            if (xMoi < 0 || xMoi >= this.banDo.getWidth() || yMoi < -600 || yMoi >= this.banDo.getHeight() || this.doanChamDiaHinh((int)Math.round(xHienTai), (int)Math.round(yHienTai), xMoi, yMoi)) {
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
        for (int buoc = 1; buoc <= soBuoc; ++buoc) {
            int x = x1 + dx * buoc / soBuoc;
            int y = y1 + dy * buoc / soBuoc;
            if (y < 0 || !this.banDo.coVaCham((short)x, (short)y)) continue;
            return true;
        }
        return false;
    }

    private KetQuaVaCham timVaChamTrenDoan(int x1, int y1, int x2, int y2, boolean xuyenDiaHinh, boolean xuyenNguoi, int mucTieuBoQua, Set<Integer> mucTieuDaTrung, List<Integer> cacMucTieu) {
        int x = x1;
        int y = y1;
        int dx = Math.abs(x2 - x1);
        int dy = -Math.abs(y2 - y1);
        int buocX = Integer.compare(x2, x1);
        int buocY = Integer.compare(y2, y1);
        int saiSo = dx + dy;
        while (true) {
            int mucTieu;
            if (!xuyenDiaHinh && y >= 0 && this.banDo.coVaCham((short)x, (short)y)) {
                return new KetQuaVaCham((short)x, (short)y, true, false);
            }
            if (this.boTimMucTieu != null && (mucTieu = this.boTimMucTieu.timMucTieu(x, y, 0, mucTieuBoQua)) >= 0 && mucTieuDaTrung.add(mucTieu)) {
                cacMucTieu.add(mucTieu);
                if (!xuyenNguoi) {
                    return new KetQuaVaCham((short)x, (short)y);
                }
            }
            if (x == x2 && y == y2) break;
            int haiLanSaiSo = saiSo * 2;
            if (haiLanSaiSo >= dy) {
                saiSo += dy;
                x += buocX;
            }
            if (haiLanSaiSo > dx) continue;
            saiSo += dx;
            y += buocY;
        }
        return null;
    }

    private static double[] taoDoLechBaTia(int soVien, double khoangLechGoc) {
        double[] ketQua = new double[Math.max(1, soVien)];
        for (int i = 1; i < ketQua.length; ++i) {
            int bac = (i + 1) / 2;
            ketQua[i] = (i % 2 == 1 ? 1.0 : -1.0) * (double)bac * khoangLechGoc;
        }
        return ketQua;
    }

    private static QuyDao saoChepQuyDao(QuyDao quyDao) {
        return new QuyDao((short[])quyDao.x.clone(), (short[])quyDao.y.clone(), (int[])quyDao.cacMucTieu.clone());
    }

    private static int[] chuyenDanhSachMucTieu(List<Integer> cacMucTieu) {
        int[] ketQua = new int[cacMucTieu.size()];
        for (int i = 0; i < cacMucTieu.size(); ++i) {
            ketQua[i] = cacMucTieu.get(i);
        }
        return ketQua;
    }

    private static short[] chuyenDanhSachDiem(List<Short> cacDiem) {
        short[] ketQua = new short[cacDiem.size()];
        for (int i = 0; i < cacDiem.size(); ++i) {
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

    private static double tinhHeSoGio(VXLHoSoDan.VatLy vatLy) {
        if (vatLy.heSoGio() <= 0.0) {
            return 0.0;
        }
        double trongLuong = Math.max(0.25, vatLy.trongLuong());
        double doNhayGio = Math.max(DO_NHAY_GIO_TOI_THIEU, Math.min(DO_NHAY_GIO_TOI_DA, vatLy.heSoGio()));
        return HE_SO_GIO * doNhayGio / trongLuong;
    }

    private static double tinhHeSoGioTheoKhung(VXLHoSoDan.VatLy vatLy) {
        if (vatLy.heSoGioTheoKhung() <= 0.0) {
            return 0.0;
        }
        return vatLy.heSoGioTheoKhung() * HE_SO_GIO_THEO_KHUNG;
    }

    private double apDungLucKeoVoiRong(int x, int y, double vanTocY) {
        if (this.boKiemTraVungVoiRong != null
                && this.boKiemTraVungVoiRong.trongVungVoiRong(x, y)) {
            return vanTocY - LUC_KEO_VOI_RONG_MOI_KHUNG;
        }
        return vanTocY;
    }

    @FunctionalInterface
    public static interface BoTimMucTieu {
        public int timMucTieu(int var1, int var2, int var3, int var4);
    }

    @FunctionalInterface
    public static interface BoKiemTraVungVoiRong {
        public boolean trongVungVoiRong(int x, int y);
    }

    public static final class KetQuaPhatBan {
        public final short[][] duongX;
        public final short[][] duongY;
        public final int[] mucTieuTheoQuyDao;
        public final int[][] cacMucTieuTheoQuyDao;
        public final short[] vaChamDiaHinhX;
        public final short[] vaChamDiaHinhY;
        public final int chiSoTach;
        public final boolean truotRaNgoaiBanDo;

        private KetQuaPhatBan(short[][] duongX, short[][] duongY, int[][] cacMucTieuTheoQuyDao, int chiSoTach) {
            this(duongX, duongY, cacMucTieuTheoQuyDao, VXLHeThongDan.taoMangKhongVaCham(duongX.length), VXLHeThongDan.taoMangKhongVaCham(duongX.length), chiSoTach, false);
        }

        private KetQuaPhatBan(short[][] duongX, short[][] duongY, int[][] cacMucTieuTheoQuyDao, int chiSoTach, boolean truotRaNgoaiBanDo) {
            this(duongX, duongY, cacMucTieuTheoQuyDao, VXLHeThongDan.taoMangKhongVaCham(duongX.length), VXLHeThongDan.taoMangKhongVaCham(duongX.length), chiSoTach, truotRaNgoaiBanDo);
        }

        private KetQuaPhatBan(short[][] duongX, short[][] duongY, int[][] cacMucTieuTheoQuyDao, short[] vaChamDiaHinhX, short[] vaChamDiaHinhY, int chiSoTach) {
            this(duongX, duongY, cacMucTieuTheoQuyDao, vaChamDiaHinhX, vaChamDiaHinhY, chiSoTach, false);
        }

        private KetQuaPhatBan(short[][] duongX, short[][] duongY, int[][] cacMucTieuTheoQuyDao, short[] vaChamDiaHinhX, short[] vaChamDiaHinhY, int chiSoTach, boolean truotRaNgoaiBanDo) {
            this.duongX = duongX;
            this.duongY = duongY;
            this.cacMucTieuTheoQuyDao = cacMucTieuTheoQuyDao;
            this.vaChamDiaHinhX = KetQuaPhatBan.chuanHoaDiemVaCham(
                    vaChamDiaHinhX, duongX.length);
            this.vaChamDiaHinhY = KetQuaPhatBan.chuanHoaDiemVaCham(
                    vaChamDiaHinhY, duongX.length);
            this.mucTieuTheoQuyDao = new int[cacMucTieuTheoQuyDao.length];
            Arrays.fill(this.mucTieuTheoQuyDao, -1);
            for (int i = 0; i < cacMucTieuTheoQuyDao.length; ++i) {
                if (cacMucTieuTheoQuyDao[i] == null || cacMucTieuTheoQuyDao[i].length <= 0) continue;
                this.mucTieuTheoQuyDao[i] = cacMucTieuTheoQuyDao[i][0];
            }
            this.chiSoTach = chiSoTach;
            this.truotRaNgoaiBanDo = truotRaNgoaiBanDo;
        }

        public int demSoVienTrung(int mucTieu) {
            int soVien = 0;
            for (int[] cacMucTieu : this.cacMucTieuTheoQuyDao) {
                if (cacMucTieu == null) continue;
                for (int mucTieuTrung : cacMucTieu) {
                    if (mucTieuTrung != mucTieu) continue;
                    ++soVien;
                }
            }
            return soVien;
        }

        public int[] layTatCaMucTieuTrung() {
            ArrayList<Integer> ketQua = new ArrayList<Integer>();
            for (int[] cacMucTieu : this.cacMucTieuTheoQuyDao) {
                if (cacMucTieu == null) continue;
                for (int mucTieu : cacMucTieu) {
                    ketQua.add(mucTieu);
                }
            }
            int[] mangKetQua = new int[ketQua.size()];
            for (int i = 0; i < ketQua.size(); ++i) {
                mangKetQua[i] = (Integer)ketQua.get(i);
            }
            return mangKetQua;
        }

        private static short[] chuanHoaDiemVaCham(short[] diemVaCham, int soQuyDao) {
            short[] ketQua = VXLHeThongDan.taoMangKhongVaCham(soQuyDao);
            if (diemVaCham != null) {
                System.arraycopy(diemVaCham, 0, ketQua, 0, Math.min(diemVaCham.length, ketQua.length));
            }
            return ketQua;
        }
    }

    private static final class QuyDao {
        private final short[] x;
        private final short[] y;
        private final int[] cacMucTieu;
        private final boolean truotRaNgoaiBanDo;
        private final short vaChamDiaHinhX;
        private final short vaChamDiaHinhY;

        private QuyDao(short[] x, short[] y, int[] cacMucTieu) {
            this(x, y, cacMucTieu, false, Short.MIN_VALUE, Short.MIN_VALUE);
        }

        private QuyDao(short[] x, short[] y, int[] cacMucTieu, boolean truotRaNgoaiBanDo) {
            this(x, y, cacMucTieu, truotRaNgoaiBanDo, Short.MIN_VALUE, Short.MIN_VALUE);
        }

        private QuyDao(short[] x, short[] y, int[] cacMucTieu, short vaChamDiaHinhX, short vaChamDiaHinhY) {
            this(x, y, cacMucTieu, false, vaChamDiaHinhX, vaChamDiaHinhY);
        }

        private QuyDao(short[] x, short[] y, int[] cacMucTieu, boolean truotRaNgoaiBanDo, short vaChamDiaHinhX, short vaChamDiaHinhY) {
            this.x = x;
            this.y = y;
            this.cacMucTieu = cacMucTieu;
            this.truotRaNgoaiBanDo = truotRaNgoaiBanDo;
            this.vaChamDiaHinhX = vaChamDiaHinhX;
            this.vaChamDiaHinhY = vaChamDiaHinhY;
        }
    }

    private static final class KetQuaVaCham {
        private final short x;
        private final short y;
        private final boolean chamDiaHinh;
        private final boolean datChan;

        private KetQuaVaCham(short x, short y) {
            this(x, y, false, false);
        }

        private KetQuaVaCham(short x, short y, boolean chamDiaHinh, boolean datChan) {
            this.x = x;
            this.y = y;
            this.chamDiaHinh = chamDiaHinh;
            this.datChan = datChan;
        }
    }
}
