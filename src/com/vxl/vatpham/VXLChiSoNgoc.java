package com.vxl.vatpham;

import com.vxl.loi.VXLQuanLyMayChu;
import java.util.Vector;

public final class VXLChiSoNgoc {
    private static final int MA_NGOC_DAU = 299;
    private static final int MA_NGOC_CUOI = 348;

    private VXLChiSoNgoc() {
    }

    public static Vector layThuocTinh(VXLVatPham trangBi) {
        Vector thuocTinhs = new Vector();
        if (trangBi == null || trangBi.mau == null || !trangBi.isTypeBody()) {
            return thuocTinhs;
        }
        for (int maNgoc : trangBi.layCacMaNgocDaDinh()) {
            VXLMauVatPham mauNgoc = VXLQuanLyMayChu.itemTemplates != null
                    ? VXLQuanLyMayChu.itemTemplates.get(maNgoc) : null;
            if (mauNgoc == null || mauNgoc.loai != 12) {
                continue;
            }
            boolean coThuocTinh = false;
            for (Object giaTri : mauNgoc.thuocTinhs) {
                if (!(giaTri instanceof VXLThuocTinhVatPham thuocTinh)
                        || thuocTinh.optionTemplate == null) {
                    continue;
                }
                thuocTinhs.add(new VXLThuocTinhVatPham(
                        thuocTinh.optionTemplate.ma, thuocTinh.thamSo));
                coThuocTinh = true;
            }
            if (!coThuocTinh && maNgoc >= MA_NGOC_DAU && maNgoc <= MA_NGOC_CUOI) {
                thuocTinhs.add(new VXLThuocTinhVatPham(
                        maThuocTinhTheoMau(maNgoc), 1 + (maNgoc - MA_NGOC_DAU) / 5));
            }
        }
        return thuocTinhs;
    }

    public static int tongThamSo(VXLVatPham trangBi, int maThuocTinh) {
        int tong = 0;
        Vector thuocTinhs = layThuocTinh(trangBi);
        for (Object giaTri : thuocTinhs) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)giaTri;
            if (thuocTinh.optionTemplate != null
                    && thuocTinh.optionTemplate.ma == maThuocTinh) {
                tong += thuocTinh.thamSo;
            }
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
