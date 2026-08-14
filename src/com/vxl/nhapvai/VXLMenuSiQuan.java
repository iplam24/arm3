package com.vxl.nhapvai;

import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.phong.VXLQuanLyPhong;
import com.vxl.mang.VXLTinNhan;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public final class VXLMenuSiQuan {
    private static final ConcurrentHashMap<Integer, Boolean> DANG_CHO_MENU =
            new ConcurrentHashMap<>();

    private VXLMenuSiQuan() {}

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
        menu.add("Phòng tập luyện");
        menu.add("Nhập Giftcode");
        nguoiChoi.dichVu.moDanhSach("SĨ QUAN", menu);
    }

    public static void xuLyMenu(VXLNguoiChoi nguoiChoi, VXLTinNhan tinNhan) throws IOException {
        if (nguoiChoi == null || tinNhan == null) {
            return;
        }
        DANG_CHO_MENU.remove(nguoiChoi.ma);
        int chiSo = tinNhan.boDoc().readUnsignedByte();
        switch (chiSo) {
            case 0 -> VXLQuanLyPhong.guiPhongTisEmpty(nguoiChoi);
            case 1 -> {
                nguoiChoi.moHopThoaiOK("Hãy nhập mã Giftcode bằng cách gõ vào ô chat:\n/code <mã>\nVí dụ: /code VIP2026");
            }
        }
    }
}
