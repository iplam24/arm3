package com.vxl.quantri;

import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.DataOutputStream;
import java.io.IOException;

public final class VXLThongBaoServer {
    private VXLThongBaoServer() {
    }

    public static void guiModalOK(String tieuDe, String noiDung) {
        try {
            VXLTinNhan tinNhan = new VXLTinNhan(-72);
            DataOutputStream boGhi = tinNhan.boGhi();
            boGhi.writeUTF(tieuDe);
            boGhi.writeUTF(noiDung);
            boGhi.flush();
            VXLNguoiChoi.guiMayChu(tinNhan);
        }
        catch (IOException ignored) {
        }
    }

    public static void guiMayBay(String noiDung) {
        try {
            VXLTinNhan tinNhan = new VXLTinNhan(-73);
            DataOutputStream boGhi = tinNhan.boGhi();
            boGhi.writeUTF(noiDung);
            boGhi.flush();
            VXLNguoiChoi.guiMayChu(tinNhan);
        }
        catch (IOException ignored) {
        }
    }
}
