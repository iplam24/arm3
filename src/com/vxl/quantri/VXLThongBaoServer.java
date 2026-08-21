package com.vxl.quantri;

import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class VXLThongBaoServer {
    private static final int DO_DAI_TOI_DA = 200;
    private static final long KHOANG_CHO_THONG_BAO_ADMIN = 10_000L;
    private static final Map<String, Long> LAN_THONG_BAO_CUOI = new ConcurrentHashMap<>();

    private VXLThongBaoServer() {
    }

    public static boolean guiThongBaoChat(String nguon, String noiDung) {
        String tenNguon = chuanHoa(nguon, "HỆ THỐNG", 40);
        String noiDungHopLe = chuanHoa(noiDung, "", DO_DAI_TOI_DA);
        if (noiDungHopLe.isEmpty()) {
            return false;
        }
        try {
            VXLTinNhan tinNhan = new VXLTinNhan(5);
            DataOutputStream boGhi = tinNhan.boGhi();
            boGhi.writeInt(-1);
            boGhi.writeUTF(tenNguon);
            boGhi.writeUTF(noiDungHopLe);
            boGhi.flush();
            VXLNguoiChoi.guiMayChu(tinNhan);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean guiThongBaoAdmin(String nguoiGui, String noiDung) {
        String khoa = chuanHoa(nguoiGui, "admin", 40).toLowerCase();
        long hienTai = System.currentTimeMillis();
        Long lanCuoi = LAN_THONG_BAO_CUOI.putIfAbsent(khoa, hienTai);
        if (lanCuoi != null && hienTai - lanCuoi < KHOANG_CHO_THONG_BAO_ADMIN) {
            return false;
        }
        LAN_THONG_BAO_CUOI.put(khoa, hienTai);
        return guiThongBaoChat("HỆ THỐNG", noiDung);
    }

    private static String chuanHoa(String noiDung, String macDinh, int doDai) {
        if (noiDung == null) {
            return macDinh;
        }
        String ketQua = noiDung.replace('\u0000', ' ').trim();
        return ketQua.length() <= doDai ? ketQua : ketQua.substring(0, doDai);
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
