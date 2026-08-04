package com.vxl.nhapvai;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.nhapvai.VXLNhanVatPhu;
import com.vxl.nhapvai.VXLKhu;
import java.util.ArrayList;

public class VXLBanDoRPG {
    public static ArrayList<VXLKhu> zones = new ArrayList();
    public static ArrayList<VXLNhanVatPhu> npcs = new ArrayList();

    public static void khoiTaoKhu() {
        for (int i = 0; i < 100; ++i) {
            zones.add(new VXLKhu(i));
        }
        npcs.add(new VXLNhanVatPhu(0, 0, (short) 100, (short) 360, (byte) 1, (short) 1900, (short) 237, (short) 238, (short) 239));
        npcs.add(new VXLNhanVatPhu(1, 0, (short) 200, (short) 360, (byte) 2, (short) 1908, (short) 240, (short) 241, (short) 242));
        npcs.add(new VXLNhanVatPhu(2, 0, (short) 300, (short) 360, (byte) 3, (short) 1907, (short) 243, (short) 244, (short) 245));
        npcs.add(new VXLNhanVatPhu(3, 0, (short) 400, (short) 360, (byte) 0, (short) 1896, (short) 234, (short) 235, (short) 236));
    }

    public static void vao(int zoneId, VXLNguoiChoi nguoiChoi) {
        VXLKhu z = zones.get(zoneId);
        if (z != null) {
            if (!z.vao(nguoiChoi)) {
                nguoiChoi.moHopThoaiOK("Khu vực đã đầy.");
            }
        } else {
            nguoiChoi.moHopThoaiOK("Có lỗi xảy ra.");
        }
    }

    public static void vao(VXLNguoiChoi nguoiChoi) {
        for (VXLKhu z : zones) {
            if (z != null && z.vao(nguoiChoi)) break;
        }
    }

    public static void roi(VXLNguoiChoi nguoiChoi) {
        VXLKhu z = zones.get(nguoiChoi.zoneId);
        z.roi(nguoiChoi);
    }
}

