package com.vxl.luyentap;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    static int tinhLuongHoiMau(VXLChiSoPhienQuan chiSo) {
        return Math.max(1, chiSo.mauToiDa / 10);
    }

    static int tinhLuongKhien(VXLChiSoPhienQuan chiSo) {
        return Math.max(1, chiSo.mauToiDa / 10);
    }

    static String layTen(int cap) {
        return "Phiến quân " + gioiHanCap(cap);
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
                || mau.ma >= MA_AVENGER_DAU && mau.ma <= MA_AVENGER_CUOI) {
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