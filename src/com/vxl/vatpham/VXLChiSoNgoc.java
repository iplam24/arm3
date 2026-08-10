package com.vxl.vatpham;

import com.vxl.loi.VXLQuanLyMayChu;

public final class VXLChiSoNgoc {
    private static final int MA_NGOC_DAU = 299;
    private static final int MA_NGOC_CUOI = 348;

    private VXLChiSoNgoc() {
    }

    public static int tongThamSo(VXLVatPham trangBi, int maThuocTinh) {
        if (trangBi == null || trangBi.mau == null || !trangBi.isTypeBody()) {
            return 0;
        }
        int tong = 0;
        for (int maNgoc : trangBi.layCacMaNgocDaDinh()) {
            VXLMauVatPham mauNgoc = VXLQuanLyMayChu.itemTemplates != null
                    ? VXLQuanLyMayChu.itemTemplates.get(maNgoc) : null;
            if (mauNgoc == null || mauNgoc.loai != 12) {
                continue;
            }
            int giaTri = 0;
            for (Object giaTriThuocTinh : mauNgoc.thuocTinhs) {
                if (!(giaTriThuocTinh instanceof VXLThuocTinhVatPham thuocTinh)
                        || thuocTinh.optionTemplate == null
                        || thuocTinh.optionTemplate.ma != maThuocTinh) {
                    continue;
                }
                giaTri += thuocTinh.thamSo;
            }
            if (giaTri == 0 && maNgoc >= MA_NGOC_DAU && maNgoc <= MA_NGOC_CUOI
                    && maThuocTinh == maThuocTinhTheoMau(maNgoc)) {
                giaTri = 1 + (maNgoc - MA_NGOC_DAU) / 5;
            }
            tong += giaTri;
        }
        return tong;
    }

    private static int maThuocTinhTheoMau(int maNgoc) {
        return switch ((maNgoc - MA_NGOC_DAU) % 5) {
            case 0 -> 6;
            case 1 -> 8;
            case 2 -> 9;
            case 3 -> 10;
            case 4 -> 7;
            default -> -1;
        };
    }
}
