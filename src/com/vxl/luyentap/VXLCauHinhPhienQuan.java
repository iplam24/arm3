package com.vxl.luyentap;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

final class VXLCauHinhPhienQuan {
    static final int CAP_TOI_DA = 40;
    static final int HE_SO_DAN_THUONG = 100;
    static final int HE_SO_DAN_MANH = 150;
    private static final int MAU_CAP_DAU = 1000;
    private static final int GIOI_HAN_CHI_SO = 30000;
    private static final int SO_LOAI_TRANG_BI = 6;
    private static final int LOAI_VU_KHI = 5;
    private static final int MA_AVENGER_DAU = 391;
    private static final int MA_AVENGER_CUOI = 400;
    private static final int MA_SPIDER_MAN = 413;
    private static final byte BAN_DO_CO_BAN_DAU_TRAN = 1;
    private static final byte[] BAN_DO_CO_BAN_AN_TOAN = new byte[]{3, 1};

    private VXLCauHinhPhienQuan() {
    }

    static int gioiHanCap(int cap) {
        return Math.max(1, Math.min(CAP_TOI_DA, cap));
    }

    static VXLChiSoPhienQuan taoChiSo(int cap) {
        int capHopLe = gioiHanCap(cap);
        List<List<VXLMauVatPham>> mauTheoLoai = taoDanhSachMauTheoLoai();
        VXLMauVatPham[] boCapDau = chonBoTrangBi(mauTheoLoai, 1);
        VXLMauVatPham[] boHienTai = chonBoTrangBi(mauTheoLoai, capHopLe);
        VXLMauVatPham vuKhi = boHienTai[LOAI_VU_KHI];
        if (vuKhi == null) {
            throw new IllegalStateException("Không có dữ liệu súng cho Phiến quân luyện tập.");
        }

        int mauMoc = tinhMauTheoTrangBi(boCapDau);
        int mauLonNhat = mauMoc;
        int tanCongLonNhat = 1;
        int giapLonNhat = 0;
        for (int capDangXet = 1; capDangXet <= capHopLe; capDangXet++) {
            VXLMauVatPham[] boDangXet = chonBoTrangBi(mauTheoLoai, capDangXet);
            mauLonNhat = Math.max(mauLonNhat, tinhMauTheoTrangBi(boDangXet));
            tanCongLonNhat = Math.max(tanCongLonNhat, tinhTanCongTheoTrangBi(boDangXet));
            giapLonNhat = Math.max(giapLonNhat, tinhGiapTheoTrangBi(boDangXet));
        }

        int mauToiDa = gioiHanChiSo(MAU_CAP_DAU + Math.max(0, mauLonNhat - mauMoc), MAU_CAP_DAU);
        return new VXLChiSoPhienQuan(mauToiDa, tanCongLonNhat, giapLonNhat,
                layPart(boHienTai[0]), layPart(boHienTai[1]), layPart(boHienTai[2]),
                layPart(boHienTai[3]), layPart(boHienTai[4]), vuKhi.part, vuKhi.ten);
    }

    static byte layBanDoChoTran(int cap) {
        int capHopLe = gioiHanCap(cap);
        if (capHopLe <= 4) {
            return BAN_DO_CO_BAN_DAU_TRAN;
        }
        int nhomHaiCap = (capHopLe - 5) / 2;
        return BAN_DO_CO_BAN_AN_TOAN[nhomHaiCap % BAN_DO_CO_BAN_AN_TOAN.length];
    }

    static int tinhPhanThuongVang(int cap) {
        int capHopLe = gioiHanCap(cap);
        if (capHopLe <= 3) {
            return 100;
        }
        if (capHopLe == 4) {
            return 2000;
        }
        return 500 + capHopLe * 50;
    }

    static int tinhLuongHoiMau(VXLChiSoPhienQuan chiSo) {
        return Math.max(1, chiSo.mauToiDa / 10);
    }

    static int tinhLuongKhien(VXLChiSoPhienQuan chiSo) {
        return Math.max(1, chiSo.mauToiDa / 10);
    }

    static String layTen(int cap) {
        return "Phiến quân " + gioiHanCap(cap);
    }

    static short chonVuKhiChoTran(int cap) {
        return chonBoVuKhiChoTran(cap)[0];
    }

    static short[] chonBoVuKhiChoTran(int cap) {
        int capHopLe = gioiHanCap(cap);
        int capVuKhi = 1 + (capHopLe - 1) * 9 / (CAP_TOI_DA - 1);
        List<Short> ungVienGanCap = new ArrayList<>();
        List<Short> ungVien = new ArrayList<>();
        if (VXLQuanLyMayChu.itemTemplates != null) {
            for (VXLMauVatPham mau : VXLQuanLyMayChu.itemTemplates.values()) {
                if (mau == null || mau.loai != LOAI_VU_KHI
                        || !laVuKhiDanHoTro(mau.part) || tongThamSo(mau, 14) <= 0) {
                    continue;
                }
                int capMau = Math.max(1, Byte.toUnsignedInt(mau.cap));
                byte loaiDan = com.vxl.chien.VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                        mau.part, (byte)0);
                if (capMau > capVuKhi || capHopLe < capMoKhoaLoaiDan(loaiDan)) {
                    continue;
                }
                if (!ungVien.contains(mau.part)) {
                    ungVien.add(mau.part);
                }
                if (capMau >= Math.max(1, capVuKhi - 1)
                        && !ungVienGanCap.contains(mau.part)) {
                    ungVienGanCap.add(mau.part);
                }
            }
        }
        if (ungVien.isEmpty()) {
            return new short[]{27};
        }
        Collections.shuffle(ungVienGanCap, ThreadLocalRandom.current());
        Collections.shuffle(ungVien, ThreadLocalRandom.current());
        int soLuong = capHopLe >= 24 ? 4 : capHopLe >= 9 ? 3 : 2;
        List<Short> ketQua = new ArrayList<>(soLuong);
        themVuKhiDaDang(ketQua, ungVienGanCap, soLuong);
        themVuKhiDaDang(ketQua, ungVien, soLuong);
        short[] mangKetQua = new short[Math.max(1, ketQua.size())];
        if (ketQua.isEmpty()) {
            mangKetQua[0] = 27;
            return mangKetQua;
        }
        for (int i = 0; i < ketQua.size(); i++) {
            mangKetQua[i] = ketQua.get(i);
        }
        return mangKetQua;
    }

    private static void themVuKhiDaDang(List<Short> ketQua, List<Short> ungVien,
            int soLuongToiDa) {
        for (int lan = 0; lan < 2 && ketQua.size() < soLuongToiDa; lan++) {
            for (short maVuKhi : ungVien) {
                if (ketQua.size() >= soLuongToiDa || ketQua.contains(maVuKhi)) {
                    continue;
                }
                byte loaiDan = com.vxl.chien.VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                        maVuKhi, (byte)0);
                boolean trungLoaiDan = false;
                for (short vuKhiDaChon : ketQua) {
                    if (com.vxl.chien.VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                            vuKhiDaChon, (byte)0) == loaiDan) {
                        trungLoaiDan = true;
                        break;
                    }
                }
                if (lan == 0 && trungLoaiDan) {
                    continue;
                }
                ketQua.add(maVuKhi);
            }
        }
    }

    private static int capMoKhoaLoaiDan(byte loaiDan) {
        return switch (Byte.toUnsignedInt(loaiDan)) {
            case 0, 1 -> 1;
            case 2, 11 -> 5;
            case 9, 10 -> 9;
            case 19 -> 13;
            case 17 -> 18;
            case 21 -> 24;
            case 49 -> 30;
            default -> CAP_TOI_DA + 1;
        };
    }

    private static boolean laVuKhiDanHoTro(short maVuKhi) {
        return switch (maVuKhi) {
            case 5, 27, 28, 29, 30, 31, 32, 37, 54, 55, 56, 57, 58,
                    120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131,
                    132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143,
                    144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155,
                    156 -> true;
            default -> false;
        };
    }

    private static List<List<VXLMauVatPham>> taoDanhSachMauTheoLoai() {
        List<List<VXLMauVatPham>> ketQua = new ArrayList<>(SO_LOAI_TRANG_BI);
        for (int loai = 0; loai < SO_LOAI_TRANG_BI; loai++) {
            ketQua.add(new ArrayList<>());
        }
        if (VXLQuanLyMayChu.itemTemplates == null) {
            return ketQua;
        }
        for (VXLMauVatPham mau : VXLQuanLyMayChu.itemTemplates.values()) {
            if (!laMauHopLe(mau)) {
                continue;
            }
            ketQua.get(mau.loai).add(mau);
        }
        for (int loai = 0; loai < SO_LOAI_TRANG_BI; loai++) {
            List<VXLMauVatPham> danhSach = ketQua.get(loai);
            if (loai == LOAI_VU_KHI) {
                danhSach.sort(Comparator.comparingInt(VXLCauHinhPhienQuan::tinhTanCongRieng)
                        .thenComparingInt(mau -> mau.strRequire)
                        .thenComparingInt(mau -> mau.ma));
            } else {
                danhSach.sort(Comparator.comparingInt((VXLMauVatPham mau) -> mau.strRequire)
                        .thenComparingInt(mau -> Byte.toUnsignedInt(mau.cap))
                        .thenComparingInt(mau -> mau.ma));
            }
        }
        return ketQua;
    }

    private static boolean laMauHopLe(VXLMauVatPham mau) {
        if (mau == null || mau.loai < 0 || mau.loai >= SO_LOAI_TRANG_BI || mau.part < 0
                || mau.ma >= MA_AVENGER_DAU && mau.ma <= MA_AVENGER_CUOI
                || mau.ma == MA_SPIDER_MAN) {
            return false;
        }
        if (mau.loai == LOAI_VU_KHI) {
            return tinhTanCongRieng(mau) > 0;
        }
        return coThuocTinhChienDau(mau);
    }

    private static boolean coThuocTinhChienDau(VXLMauVatPham mau) {
        return tongThamSo(mau, 0) != 0 || tongThamSo(mau, 1) != 0 || tongThamSo(mau, 2) != 0
                || tongThamSo(mau, 6) != 0 || tongThamSo(mau, 7) != 0
                || tongThamSo(mau, 8) != 0 || tongThamSo(mau, 18) != 0;
    }

    private static VXLMauVatPham[] chonBoTrangBi(List<List<VXLMauVatPham>> mauTheoLoai, int cap) {
        VXLMauVatPham[] ketQua = new VXLMauVatPham[SO_LOAI_TRANG_BI];
        int capHopLe = gioiHanCap(cap);
        for (int loai = 0; loai < SO_LOAI_TRANG_BI; loai++) {
            List<VXLMauVatPham> danhSach = mauTheoLoai.get(loai);
            if (danhSach.isEmpty()) {
                continue;
            }
            int chiSo = (int)((long)(capHopLe - 1) * (danhSach.size() - 1) / (CAP_TOI_DA - 1));
            ketQua[loai] = danhSach.get(chiSo);
        }
        return ketQua;
    }

    private static int tinhMauTheoTrangBi(VXLMauVatPham[] boTrangBi) {
        int congThem = tongTheoBo(boTrangBi, 0);
        int phanTram = tongTheoBo(boTrangBi, 6) + tongTheoBo(boTrangBi, 18);
        return tinhChiSo(MAU_CAP_DAU, congThem, phanTram, MAU_CAP_DAU);
    }

    private static int tinhTanCongTheoTrangBi(VXLMauVatPham[] boTrangBi) {
        int congThem = tongTheoBo(boTrangBi, 1);
        int phanTram = tongTheoBo(boTrangBi, 7) + tongTheoBo(boTrangBi, 18);
        return tinhChiSo(0, congThem, phanTram, 1);
    }

    private static int tinhGiapTheoTrangBi(VXLMauVatPham[] boTrangBi) {
        int congThem = tongTheoBo(boTrangBi, 2);
        int phanTram = tongTheoBo(boTrangBi, 8) + tongTheoBo(boTrangBi, 18);
        return tinhChiSo(0, congThem, phanTram, 0);
    }

    private static int tinhTanCongRieng(VXLMauVatPham mau) {
        int congThem = tongThamSo(mau, 1);
        int phanTram = tongThamSo(mau, 7) + tongThamSo(mau, 18);
        return tinhChiSo(0, congThem, phanTram, 0);
    }

    private static int tongTheoBo(VXLMauVatPham[] boTrangBi, int maThuocTinh) {
        int tong = 0;
        for (VXLMauVatPham mau : boTrangBi) {
            tong += tongThamSo(mau, maThuocTinh);
        }
        return tong;
    }

    private static int tongThamSo(VXLMauVatPham mau, int maThuocTinh) {
        if (mau == null || mau.thuocTinhs == null) {
            return 0;
        }
        int tong = 0;
        for (Object giaTri : mau.thuocTinhs) {
            if (!(giaTri instanceof VXLThuocTinhVatPham thuocTinh)
                    || thuocTinh.optionTemplate == null
                    || thuocTinh.optionTemplate.ma != maThuocTinh) {
                continue;
            }
            tong += thuocTinh.thamSo;
        }
        return tong;
    }

    private static int tinhChiSo(int coBan, int congThem, int phanTram, int nhoNhat) {
        long giaTri = (long)Math.max(0, coBan + congThem) * Math.max(0, 100 + phanTram) / 100L;
        return gioiHanChiSo(giaTri, nhoNhat);
    }

    private static int gioiHanChiSo(long giaTri, int nhoNhat) {
        return (int)Math.max(nhoNhat, Math.min(GIOI_HAN_CHI_SO, giaTri));
    }

    private static short layPart(VXLMauVatPham mau) {
        return mau != null ? mau.part : (short)-1;
    }
}
