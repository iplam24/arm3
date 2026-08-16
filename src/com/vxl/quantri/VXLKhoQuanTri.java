package com.vxl.quantri;

import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mohinh.VXLNguoiDung;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.vatpham.VXLVatPham;
import com.vxl.dulieu.VXLTieuDeCap;
import com.vxl.tienich.VXLTienIch;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VXLKhoQuanTri {
    private static final Logger LOGGER = Logger.getLogger(VXLKhoQuanTri.class.getName());

    private VXLKhoQuanTri() {
    }

    public static VXLNguoiChoi timNguoiChoiTrucTuyen(String ten) {
        if (ten == null || ten.isBlank()) {
            return null;
        }
        for (VXLNguoiChoi nguoiChoi : VXLNguoiChoi.players_id.values()) {
            if (nguoiChoi != null && nguoiChoi.ten != null
                    && nguoiChoi.ten.equalsIgnoreCase(ten.trim())) {
                return nguoiChoi;
            }
        }
        return null;
    }

    public static String congVang(String ten, long soLuong) throws SQLException {
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            online.vang = gioiHanInt((long)online.vang + soLuong);
            online.dichVu.capNhat();
            online.flushCache();
            return "Vàng của " + online.ten + " = " + online.vang + ".";
        }
        return suaNgoaiTuyen(ten, duLieu -> duLieu.vang = gioiHanInt((long)duLieu.vang + soLuong),
                duLieu -> "Vàng của " + duLieu.ten + " = " + duLieu.vang + ".");
    }

    public static String congNgoc(String ten, long soLuong) throws SQLException {
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            online.ngoc = gioiHanInt((long)online.ngoc + soLuong);
            online.dichVu.capNhat();
            online.flushCache();
            return "Ngọc của " + online.ten + " = " + online.ngoc + ".";
        }
        return suaNgoaiTuyen(ten, duLieu -> duLieu.ngoc = gioiHanInt((long)duLieu.ngoc + soLuong),
                duLieu -> "Ngọc của " + duLieu.ten + " = " + duLieu.ngoc + ".");
    }

    public static String congKinhNghiem(String ten, long soLuong) throws SQLException {
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            int truoc = online.kinhNghiem;
            if (soLuong >= 0L) {
                online.congKinhNghiem((int)Math.min(Integer.MAX_VALUE, soLuong));
            } else {
                online.kinhNghiem = gioiHanInt((long)online.kinhNghiem + soLuong);
                online.cap = VXLTienIch.layCap(online.kinhNghiem);
            }
            online.flushCache();
            return "EXP của " + online.ten + ": " + truoc + " -> " + online.kinhNghiem
                    + " (cấp " + online.cap + ").";
        }
        return suaNgoaiTuyen(ten, duLieu -> {
            int kinhNghiem = docInt(duLieu.chiSo, "exp", VXLTienIch.layKinhNghiemCapMot());
            duLieu.chiSo.put("exp", gioiHanInt((long)kinhNghiem + soLuong));
        }, duLieu -> {
            int kinhNghiem = docInt(duLieu.chiSo, "exp", VXLTienIch.layKinhNghiemCapMot());
            return "EXP của " + duLieu.ten + " = " + kinhNghiem
                    + " (cấp " + VXLTienIch.layCap(kinhNghiem) + ").";
        });
    }

    public static String congDiemTiemNang(String ten, long soLuong) throws SQLException {
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            online.point = (short)gioiHan((long)online.point + soLuong, 0L, Short.MAX_VALUE);
            online.flushCache();
            return "Điểm tiềm năng của " + online.ten + " = " + online.point + ".";
        }
        return suaNgoaiTuyen(ten, duLieu -> {
            int point = docInt(duLieu.chiSo, "point", 0);
            duLieu.chiSo.put("point", gioiHan((long)point + soLuong, 0L, Short.MAX_VALUE));
        }, duLieu -> "Điểm tiềm năng của " + duLieu.ten + " = "
                + docInt(duLieu.chiSo, "point", 0) + ".");
    }

    public static String datCap(String ten, int cap) throws SQLException {
        VXLTieuDeCap tieuDe = VXLTieuDeCap.levels.get(cap);
        if (tieuDe == null) {
            return "Không tồn tại cấp " + cap + ".";
        }
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            online.kinhNghiem = Math.max(0, tieuDe.kinhNghiem);
            online.cap = VXLTienIch.layCap(online.kinhNghiem);
            online.flushCache();
            return "Đã đặt " + online.ten + " về cấp " + online.cap
                    + " với " + online.kinhNghiem + " EXP.";
        }
        return suaNgoaiTuyen(ten, duLieu -> duLieu.chiSo.put("exp", Math.max(0, tieuDe.kinhNghiem)),
                duLieu -> "Đã đặt " + duLieu.ten + " về cấp " + cap
                        + " với " + tieuDe.kinhNghiem + " EXP.");
    }

    public static String resetBot(String tenHoacAll) throws Exception {
        if (tenHoacAll == null || tenHoacAll.isBlank() || "all".equalsIgnoreCase(tenHoacAll)
                || "tatca".equalsIgnoreCase(tenHoacAll) || "toanbo".equalsIgnoreCase(tenHoacAll)) {
            return com.vxl.luyentap.VXLDatLaiPhienQuanHangNgay.datLaiToanBoNguoiChoi();
        }
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(tenHoacAll);
        if (online != null) {
            online.datLaiTienDoPhienQuanHangNgay();
            online.flushCache();
            return "Đã reset Phiến quân, 2 Tòa Tháp và Boss về mốc 1 cho " + online.ten + ".";
        }
        return suaNgoaiTuyen(tenHoacAll, duLieu -> {
            duLieu.chiSo.put("trainingSuccess", 1);
            duLieu.chiSo.put("trainingRebelDefeated", 0);
            duLieu.chiSo.put("kamikazeKills", 0);
            duLieu.chiSo.put("bossKills", 0);
            duLieu.chiSo.put("dailyKamikazeKills", 0);
            duLieu.chiSo.put("dailyBossKills", 0);
            duLieu.chiSo.put("dailyKamikazeClaimed", false);
            duLieu.chiSo.put("dailyBossClaimed", false);
        }, duLieu -> "Đã reset Phiến quân, 2 Tòa Tháp và Boss về mốc 1 cho " + duLieu.ten + ".");
    }

    public static String datCapPhienQuan(String ten, int cap) throws SQLException {
        int capMoi = (int)gioiHan(cap, 0, 255);
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            online.trainingSuccess = (byte)capMoi;
            return "Mốc phiến quân của " + online.ten + " = " + capMoi + ".";
        }
        return suaNgoaiTuyen(ten, duLieu -> {
            duLieu.chiSo.put("trainingSuccess", capMoi);
            duLieu.chiSo.put("trainingRebelDefeated", Math.max(0, capMoi - 1));
        },
                duLieu -> "Mốc phiến quân của " + duLieu.ten + " = " + capMoi + ".");
    }

    public static String themVatPham(String ten, int maVatPham, int soLuong) {
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online == null) {
            return "Người chơi phải trực tuyến để nhận vật phẩm.";
        }
        VXLMauVatPham mau = VXLQuanLyMayChu.itemTemplates.get(maVatPham);
        if (mau == null) {
            return "Không tồn tại vật phẩm ID " + maVatPham + ".";
        }
        VXLVatPham vatPham = new VXLVatPham(maVatPham);
        vatPham.thayMau(mau);
        vatPham.soLuong = Math.max(1, Math.min(9999, soLuong));
        if (!online.themVatPhamVaoTui(vatPham)) {
            return "Túi của " + online.ten + " đã đầy.";
        }
        online.flushCache();
        return "Đã cấp " + vatPham.soLuong + " x " + mau.ten + " cho " + online.ten + ".";
    }

    public static String datQuyenQuanTri(String ten, boolean bat) throws SQLException {
        try (Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE accounts a JOIN players p ON p.account_id = a.id "
                             + "SET a.is_admin = ? WHERE LOWER(p.name) = LOWER(?) LIMIT 1")) {
            stmt.setBoolean(1, bat);
            stmt.setString(2, ten);
            if (stmt.executeUpdate() != 1) {
                return "Không tìm thấy người chơi " + ten + ".";
            }
        }
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            online.quanTri = bat;
        }
        return "Quyền admin của " + ten + " = " + bat + ".";
    }

    public static String datKhoaTaiKhoan(String ten, boolean khoa) throws SQLException {
        try (Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE accounts a JOIN players p ON p.account_id = a.id "
                             + "SET a.is_banned = ? WHERE LOWER(p.name) = LOWER(?) LIMIT 1")) {
            stmt.setBoolean(1, khoa);
            stmt.setString(2, ten);
            if (stmt.executeUpdate() != 1) {
                return "Không tìm thấy người chơi " + ten + ".";
            }
        }
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            VXLQuanLyMayChu.log("[ADMIN] Admin đã khóa tài khoản của " + online.ten + ".");
            if (online.dichVu != null && online.dichVu.layKhach() != null) { VXLQuanLyMayChu.disconnect(online.dichVu.layKhach()); }
        }
        return "Đã " + (khoa ? "khóa" : "mở khóa") + " tài khoản của " + ten + ".";
    }

    public static String luuNguoiChoi(String tenHoacAll) {
        if (tenHoacAll == null || tenHoacAll.isBlank() || "all".equalsIgnoreCase(tenHoacAll)) {
            int dem = 0;
            for (VXLNguoiChoi online : VXLNguoiChoi.players_id.values()) {
                if (online != null) {
                    online.flushCache();
                    dem++;
                }
            }
            return "Đã lưu " + dem + " người chơi trực tuyến.";
        }
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(tenHoacAll);
        if (online == null) {
            return "Không tìm thấy người chơi " + tenHoacAll + " đang online.";
        }
        online.flushCache();
        return "Đã lưu dữ liệu cho " + online.ten + ".";
    }

    public static String thongTinNguoiChoi(String ten) throws SQLException {
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            return "THÔNG TIN " + online.ten.toUpperCase(Locale.ROOT)
                    + "\n- Cấp: " + online.cap + " (" + online.kinhNghiem + " EXP)"
                    + "\n- Vàng: " + online.vang + " | Ngọc: " + online.ngoc
                    + "\n- Điểm tiềm năng: " + online.point
                    + "\n- Tiến độ: phiến quân=" + Byte.toUnsignedInt(online.trainingSuccess)
                    + "\n- Trạng thái: Trực tuyến (ID " + online.ma + ")"
                    + "\n- Quyền admin: " + (online.quanTri ? "CÓ" : "KHÔNG");
        }

        final String[] ketQua = new String[1];
        suaNgoaiTuyen(ten, duLieu -> {
            int exp = docInt(duLieu.chiSo, "exp", VXLTienIch.layKinhNghiemCapMot());
            int point = docInt(duLieu.chiSo, "point", 0);
            int rebel = docInt(duLieu.chiSo, "trainingSuccess", 1);
            ketQua[0] = "THÔNG TIN " + duLieu.ten.toUpperCase(Locale.ROOT)
                    + "\n- Cấp: " + VXLTienIch.layCap(exp) + " (" + exp + " EXP)"
                    + "\n- Vàng: " + duLieu.vang + " | Ngọc: " + duLieu.ngoc
                    + "\n- Điểm tiềm năng: " + point
                    + "\n- Tiến độ: phiến quân=" + rebel
                    + "\n- Trạng thái: Ngoại tuyến"
                    + "\n- Quyền admin: " + (duLieu.quanTri ? "CÓ" : "KHÔNG");
        }, duLieu -> ketQua[0]);
        return ketQua[0];
    }

    public static String danhSachTrucTuyen() {
        List<VXLNguoiChoi> danhSach = new ArrayList<>(VXLNguoiChoi.players_id.values());
        danhSach.removeIf(Objects::isNull);
        if (danhSach.isEmpty()) {
            return "Hiện không có người chơi nào trực tuyến.";
        }
        danhSach.sort(Comparator.comparingInt((VXLNguoiChoi p) -> p.cap).reversed()
                .thenComparing(p -> p.ten == null ? "" : p.ten));
        StringBuilder sb = new StringBuilder("DANH SÁCH ONLINE (" + danhSach.size() + "):");
        int gioiHan = Math.min(20, danhSach.size());
        for (int i = 0; i < gioiHan; i++) {
            VXLNguoiChoi p = danhSach.get(i);
            sb.append("\n").append(i + 1).append(". ").append(p.ten)
                    .append(" (cấp ").append(p.cap).append(", vàng ").append(p.vang)
                    .append(", ngọc ").append(p.ngoc).append(")");
        }
        if (danhSach.size() > gioiHan) {
            sb.append("\n... và ").append(danhSach.size() - gioiHan).append(" người chơi khác.");
        }
        return sb.toString();
    }

    public static void ghiNhatKy(VXLNguoiChoi quanTri, String lenh, boolean thanhCong, String ketQua) {
        String nguoiDung = quanTri == null ? "SYSTEM" : quanTri.ten;
        String dong = "[" + (thanhCong ? "OK" : "ERR") + "] Admin " + nguoiDung
                + " thực hiện: " + lenh + " => " + ketQua.replace("\n", " ");
        if (thanhCong) {
            VXLQuanLyMayChu.log(dong);
        } else {
            LOGGER.log(Level.WARNING, dong);
        }
    }

    private static String suaNgoaiTuyen(String ten, Consumer<DuLieuNguoiChoi> boCapNhat,
            Function<DuLieuNguoiChoi, String> taoThongBao) throws SQLException {
        DuLieuNguoiChoi duLieu = docNguoiChoiNgoaiTuyen(ten);
        if (duLieu == null) {
            return "Không tìm thấy người chơi " + ten + ".";
        }
        boCapNhat.accept(duLieu);
        ghiNguoiChoiNgoaiTuyen(duLieu);
        return taoThongBao.apply(duLieu);
    }

    private static DuLieuNguoiChoi docNguoiChoiNgoaiTuyen(String ten) throws SQLException {
        String sql = "SELECT p.id, p.name, p.gold, p.gem, p.stats_json, a.is_admin "
                + "FROM players p JOIN accounts a ON a.id = p.account_id "
                + "WHERE LOWER(p.name) = LOWER(?) LIMIT 1";
        try (Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ten);
            try (ResultSet res = stmt.executeQuery()) {
                if (!res.next()) {
                    return null;
                }
                DuLieuNguoiChoi duLieu = new DuLieuNguoiChoi();
                duLieu.ma = res.getInt("id");
                duLieu.ten = res.getString("name");
                duLieu.vang = res.getInt("gold");
                duLieu.ngoc = res.getInt("gem");
                duLieu.quanTri = res.getBoolean("is_admin");
                String statsRaw = res.getString("stats_json");
                duLieu.chiSo = statsRaw == null || statsRaw.isBlank()
                        ? new JSONObject()
                        : JSON.parseObject(statsRaw);
                return duLieu;
            }
        }
    }

    private static void ghiNguoiChoiNgoaiTuyen(DuLieuNguoiChoi duLieu) throws SQLException {
        String sql = "UPDATE players SET gold = ?, gem = ?, stats_json = ? WHERE id = ? LIMIT 1";
        try (Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, duLieu.vang);
            stmt.setInt(2, duLieu.ngoc);
            stmt.setString(3, duLieu.chiSo == null ? "{}" : duLieu.chiSo.toJSONString());
            stmt.setInt(4, duLieu.ma);
            stmt.executeUpdate();
        }
    }

    private static int docInt(JSONObject json, String khoa, int macDinh) {
        if (json == null || !json.containsKey(khoa)) {
            return macDinh;
        }
        try {
            return json.getIntValue(khoa);
        } catch (Exception e) {
            return macDinh;
        }
    }

    private static int gioiHanInt(long giaTri) {
        return (int)gioiHan(giaTri, 0L, Integer.MAX_VALUE);
    }

    private static long gioiHan(long giaTri, long toiThieu, long toiDa) {
        return Math.max(toiThieu, Math.min(toiDa, giaTri));
    }

    private static class DuLieuNguoiChoi {
        int ma;
        String ten;
        int vang;
        int ngoc;
        boolean quanTri;
        JSONObject chiSo;
    }
}