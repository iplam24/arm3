package com.vxl.nhapvai;

import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLTinNhan;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public final class VXLMenuDoiTruong {
    private static final ConcurrentHashMap<Integer, Boolean> DANG_CHO_MENU =
            new ConcurrentHashMap<>();

    private VXLMenuDoiTruong() {}

    public static boolean laMenuDangCho(VXLNguoiChoi nguoiChoi) {
        return nguoiChoi != null && DANG_CHO_MENU.containsKey(nguoiChoi.ma);
    }

    public static void huyMenu(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
            DANG_CHO_MENU.remove(nguoiChoi.ma);
        }
    }

    public static void mo(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || nguoiChoi.dichVu == null) {
            return;
        }
        huyMenu(nguoiChoi);
        DANG_CHO_MENU.put(nguoiChoi.ma, Boolean.TRUE);
        Vector<String> menu = new Vector<>();
        menu.add("Luyện tập");
        menu.add("Giftcode");
        nguoiChoi.dichVu.moDanhSach("ĐỘI TRƯỞNG", menu);
    }

    public static void xuLyMenu(VXLNguoiChoi nguoiChoi, VXLTinNhan tinNhan) throws IOException {
        if (nguoiChoi == null || tinNhan == null) {
            return;
        }
        DANG_CHO_MENU.remove(nguoiChoi.ma);
        int chiSo = tinNhan.boDoc().readUnsignedByte();
        switch (chiSo) {
            case 0 -> {
                nguoiChoi.vaoLuyenTap();
            }
            case 1 -> {
                if (nguoiChoi.dichVu != null) {
                    nguoiChoi.dichVu.moHopThoaiNhapGiftcode("Nhập Giftcode");
                }
            }
        }
    }
}
