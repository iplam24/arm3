package com.vxl.vatpham;

import com.vxl.mohinh.VXLNguoiChoi;

public final class VXLTienTrinhDucLo {
    public static final int MA_BUA_DUC_LO = 349;
    public static final int SO_BUA_TOI_DA = 3;
    private static final long MOT_GIO_MILLIS = 60L * 60L * 1000L;
    private static final long MOT_NGAY_MILLIS = 24L * MOT_GIO_MILLIS;
    private static final long[] THOI_GIAN_DUC_LO = {
        MOT_GIO_MILLIS,
        MOT_NGAY_MILLIS,
        3L * MOT_NGAY_MILLIS
    };
    private static final int NGOC_MOI_GIO = 1;

    private VXLTienTrinhDucLo() {
    }

    public static long layThoiGianDucLo(int soLo) {
        if (soLo < 1 || soLo > THOI_GIAN_DUC_LO.length) {
            return 0L;
        }
        return THOI_GIAN_DUC_LO[soLo - 1];
    }

    public static int layChiPhiVang(int soLo) {
        return soLo < 1 || soLo > THOI_GIAN_DUC_LO.length
                ? Integer.MAX_VALUE : soLo * soLo * 5000;
    }

    public static int layChiPhiHoanThanhNgay(VXLVatPham trangBi) {
        if (trangBi == null || !trangBi.isSocketing) {
            return 0;
        }
        return Math.max(1, trangBi.laySoGioDucLoConLai() * NGOC_MOI_GIO);
    }

    public static String dinhDangThoiGianConLai(VXLVatPham trangBi) {
        if (trangBi == null || !trangBi.isSocketing) {
            return "đã hoàn thành";
        }
        long conLai = trangBi.layThoiGianDucLoConLaiMillis();
        long tongPhut = Math.max(1L, (conLai + 60000L - 1L) / 60000L);
        long ngay = tongPhut / (24L * 60L);
        long gio = tongPhut % (24L * 60L) / 60L;
        long phut = tongPhut % 60L;
        StringBuilder ketQua = new StringBuilder();
        if (ngay > 0L) {
            ketQua.append(ngay).append(" ngày");
        }
        if (gio > 0L) {
            if (!ketQua.isEmpty()) {
                ketQua.append(' ');
            }
            ketQua.append(gio).append(" giờ");
        }
        if (phut > 0L || ketQua.isEmpty()) {
            if (!ketQua.isEmpty()) {
                ketQua.append(' ');
            }
            ketQua.append(phut).append(" phút");
        }
        return ketQua.toString();
    }

    public static boolean capNhat(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return false;
        }
        boolean coThayDoi = hoanTatCacTienTrinh(nguoiChoi.itemBag);
        coThayDoi |= hoanTatCacTienTrinh(nguoiChoi.itemBody);
        coThayDoi |= hoanTatCacTienTrinh(nguoiChoi.itemBox);
        capNhatThongTinBua(nguoiChoi);
        return coThayDoi;
    }

    public static boolean coBuaRanh(VXLNguoiChoi nguoiChoi) {
        capNhat(nguoiChoi);
        return Byte.toUnsignedInt(nguoiChoi.nHammer) > Byte.toUnsignedInt(nguoiChoi.busyHammer);
    }

    public static void capNhatThongTinBua(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        int tongBua = Math.min(SO_BUA_TOI_DA, Byte.toUnsignedInt(nguoiChoi.nHammer));
        int buaDangBan = demTienTrinh(nguoiChoi.itemBag)
                + demTienTrinh(nguoiChoi.itemBody)
                + demTienTrinh(nguoiChoi.itemBox);
        nguoiChoi.nHammer = (byte)tongBua;
        nguoiChoi.busyHammer = (byte)Math.min(tongBua, buaDangBan);
    }

    public static int laySoLuongBuaCoTheNhan(VXLNguoiChoi nguoiChoi, VXLVatPham buaDangThem) {
        if (nguoiChoi == null || buaDangThem == null
                || buaDangThem.ma != MA_BUA_DUC_LO || buaDangThem.soLuong <= 0) {
            return Integer.MAX_VALUE;
        }
        int tongBua = Math.min(SO_BUA_TOI_DA, Byte.toUnsignedInt(nguoiChoi.nHammer))
                + demBua(nguoiChoi.itemBag)
                + demBua(nguoiChoi.itemBody)
                + demBua(nguoiChoi.itemBox);
        if (chuaCungVatPham(nguoiChoi.itemBag, buaDangThem)
                || chuaCungVatPham(nguoiChoi.itemBody, buaDangThem)
                || chuaCungVatPham(nguoiChoi.itemBox, buaDangThem)) {
            tongBua -= buaDangThem.soLuong;
        }
        return Math.max(0, SO_BUA_TOI_DA - Math.max(0, tongBua));
    }

    public static int demTongBuaSoHuu(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return 0;
        }
        return Math.min(SO_BUA_TOI_DA, Byte.toUnsignedInt(nguoiChoi.nHammer))
                + demBua(nguoiChoi.itemBag)
                + demBua(nguoiChoi.itemBody)
                + demBua(nguoiChoi.itemBox);
    }

    private static boolean chuaCungVatPham(VXLVatPham[] vatPhams, VXLVatPham canTim) {
        if (vatPhams == null || canTim == null) {
            return false;
        }
        for (VXLVatPham vatPham : vatPhams) {
            if (vatPham == canTim) {
                return true;
            }
        }
        return false;
    }

    private static boolean hoanTatCacTienTrinh(VXLVatPham[] vatPhams) {
        if (vatPhams == null) {
            return false;
        }
        boolean coThayDoi = false;
        for (VXLVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                coThayDoi |= vatPham.hoanTatDucLoNeuDenHan();
            }
        }
        return coThayDoi;
    }

    private static int demBua(VXLVatPham[] vatPhams) {
        if (vatPhams == null) {
            return 0;
        }
        int tong = 0;
        for (VXLVatPham vatPham : vatPhams) {
            if (vatPham != null && vatPham.ma == MA_BUA_DUC_LO && vatPham.soLuong > 0) {
                tong += vatPham.soLuong;
            }
        }
        return tong;
    }

    private static int demTienTrinh(VXLVatPham[] vatPhams) {
        if (vatPhams == null) {
            return 0;
        }
        int tong = 0;
        for (VXLVatPham vatPham : vatPhams) {
            if (vatPham != null && vatPham.isSocketing) {
                tong++;
            }
        }
        return tong;
    }
}
