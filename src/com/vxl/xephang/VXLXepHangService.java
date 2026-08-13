package com.vxl.xephang;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class VXLXepHangService {
    private static final int SO_NGUOI_TOI_DA = 30;

    private VXLXepHangService() {
    }

    public static void guiDanhMucXepHang(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        byte loai = yeuCau.boDoc().readByte();
        if (yeuCau.boDoc().available() > 0) {
            yeuCau.boDoc().readByte();
        }
        VXLTinNhan tin = new VXLTinNhan(-14);
        DataOutputStream ghi = tin.boGhi();
        if (loai == -1) {
            ghi.writeByte(-1);
            ghi.writeByte(5);
            ghi.writeUTF("Bạn bè");
            ghi.writeUTF("Tin nhắn");
            ghi.writeUTF("Xếp hạng");
            ghi.writeUTF("Biệt đội");
            ghi.writeUTF("Thành tích");
        } else {
            ghi.writeByte(loai);
            ghi.writeByte(0);
            ghi.writeUTF("Bảng xếp hạng");
        }
        ghi.flush();
        nguoiChoi.dichVu.guiTin(tin);
    }

    public static void guiBangXepHang(VXLNguoiChoi nguoiChoi) throws Exception {
        List<HangNguoiChoi> tatCa = docNguoiChoi();
        List<NhomXepHang> cacBang = List.of(
                new NhomXepHang("Cúp", Comparator.comparingInt(HangNguoiChoi::cup).reversed(), h -> h.cup + " Cúp"),
                new NhomXepHang("Kinh nghiệm", Comparator.comparingInt(HangNguoiChoi::kinhNghiem).reversed(), h -> h.kinhNghiem + " KN"),
                new NhomXepHang("Hạ gục", Comparator.comparingInt(HangNguoiChoi::haGuc).reversed(), h -> h.haGuc + " mạng")
        );
        VXLTinNhan tin = new VXLTinNhan(-57);
        DataOutputStream ghi = tin.boGhi();
        ghi.writeByte(cacBang.size());
        for (NhomXepHang bang : cacBang) {
            ghi.writeUTF(bang.ten());
        }
        for (NhomXepHang bang : cacBang) {
            List<HangNguoiChoi> danhSach = new ArrayList<>(tatCa);
            danhSach.sort(bang.sapXep().thenComparingInt(HangNguoiChoi::ma));
            if (danhSach.size() > SO_NGUOI_TOI_DA) {
                danhSach = danhSach.subList(0, SO_NGUOI_TOI_DA);
            }
            ghi.writeByte(danhSach.size());
            for (int i = 0; i < danhSach.size(); i++) {
                HangNguoiChoi hang = danhSach.get(i);
                ghi.writeInt(i + 1);
                ghi.writeInt(hang.ma);
                ghi.writeUTF(hang.ten);
                ghi.writeShort(hang.hinhDang.dau());
                ghi.writeShort(hang.hinhDang.mu());
                ghi.writeShort(hang.hinhDang.than());
                ghi.writeShort(hang.hinhDang.chan());
                ghi.writeShort(hang.hinhDang.canh());
                ghi.writeShort(hang.hinhDang.vuKhi());
                ghi.writeInt(hang.kinhNghiem);
                ghi.writeByte(VXLNguoiChoi.layNguoiChoiTheoMa(hang.ma) == null ? 2 : 1);
                ghi.writeUTF(bang.hienThi().giaTri(hang));
                ghi.writeShort(hang.bieuTuongClan);
            }
        }
        ghi.flush();
        nguoiChoi.dichVu.guiTin(tin);
    }

    private static List<HangNguoiChoi> docNguoiChoi() throws Exception {
        String sql = "SELECT p.id,p.name,p.cup,p.stats_json,p.equipped_json,COALESCE(c.icon_id,-1) clan_icon "
                + "FROM players p LEFT JOIN clan_members cm ON cm.player_id=p.id "
                + "LEFT JOIN clans c ON c.id=cm.clan_id";
        List<HangNguoiChoi> ketQua = new ArrayList<>();
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(sql);
             ResultSet duLieu = lenh.executeQuery()) {
            while (duLieu.next()) {
                int ma = duLieu.getInt("id");
                VXLNguoiChoi online = VXLNguoiChoi.layNguoiChoiTheoMa(ma);
                JSONObject thongKe = docJson(duLieu.getString("stats_json"));
                int kinhNghiem = online != null ? online.kinhNghiem : Math.max(0, thongKe.getIntValue("exp"));
                int haGuc = online != null ? online.kill : Math.max(0, thongKe.getIntValue("kill"));
                int cup = online != null ? online.cup : Math.max(0, duLieu.getInt("cup"));
                VXLHinhDangNguoiChoi hinhDang = online != null
                        ? VXLHinhDangNguoiChoi.tuNguoiChoi(online)
                        : VXLHinhDangNguoiChoi.tuJson(duLieu.getString("equipped_json"));
                ketQua.add(new HangNguoiChoi(ma, duLieu.getString("name"), cup, kinhNghiem,
                        haGuc, duLieu.getShort("clan_icon"), hinhDang));
            }
        }
        return ketQua;
    }

    private static JSONObject docJson(String giaTri) {
        try {
            JSONObject ketQua = JSON.parseObject(giaTri);
            return ketQua == null ? new JSONObject() : ketQua;
        } catch (RuntimeException ignored) {
            return new JSONObject();
        }
    }

    private record HangNguoiChoi(int ma, String ten, int cup, int kinhNghiem, int haGuc,
            short bieuTuongClan, VXLHinhDangNguoiChoi hinhDang) {
    }

    private record NhomXepHang(String ten, Comparator<HangNguoiChoi> sapXep, HienThi hienThi) {
    }

    @FunctionalInterface
    private interface HienThi {
        String giaTri(HangNguoiChoi hang);
    }
}
