package com.vxl.quantri;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.vxl.dulieu.VXLTieuDeCap;
import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.tienich.VXLTienIch;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.vatpham.VXLVatPham;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
        return datCapPhienQuan(tenHoacAll, 1);
    }

    public static String datCapPhienQuan(String ten, int cap) throws SQLException {
        int capMoi = (int)gioiHan(cap, 0, 255);
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            online.trainingSuccess = (byte)capMoi;
            online.flushCache();
            return "Mốc phiến quân của " + online.ten + " = " + capMoi + ".";
        }
        return suaNgoaiTuyen(ten, duLieu -> duLieu.chiSo.put("trainingSuccess", capMoi),
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
        if (online != null && khoa) {
            online.dichVu.dongKetNoi();
        }
        return "Trạng thái khóa của " + ten + " = " + khoa + ".";
    }

    public static String thongTinNguoiChoi(String ten) throws SQLException {
        VXLNguoiChoi online = timNguoiChoiTrucTuyen(ten);
        if (online != null) {
            return "[ONLINE] " + online.ten + " | ID=" + online.ma + " | cấp=" + online.cap
                    + " | EXP=" + online.kinhNghiem + " | vàng=" + online.vang
                    + " | ngọc=" + online.ngoc + " | điểm=" + online.point
                    + " | phiến quân=" + Byte.toUnsignedInt(online.trainingSuccess)
                    + " | admin=" + online.quanTri;
        }
        try (Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.*, a.username, a.is_admin, a.is_banned FROM players p "
                             + "JOIN accounts a ON a.id = p.account_id "
                             + "WHERE LOWER(p.name) = LOWER(?) LIMIT 1")) {
            stmt.setString(1, ten);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return "Không tìm thấy người chơi " + ten + ".";
                }
                JSONObject chiSo = docJson(rs.getString("stats_json"));
                int kinhNghiem = docInt(chiSo, "exp", VXLTienIch.layKinhNghiemCapMot());
                return "[OFFLINE] " + rs.getString("name") + " | ID=" + rs.getInt("id")
                        + " | tài khoản=" + rs.getString("username")
                        + " | cấp=" + VXLTienIch.layCap(kinhNghiem) + " | EXP=" + kinhNghiem
                        + " | vàng=" + rs.getInt("gold") + " | ngọc=" + rs.getInt("gem")
                        + " | điểm=" + docInt(chiSo, "point", 0)
                        + " | admin=" + rs.getBoolean("is_admin")
                        + " | khóa=" + rs.getBoolean("is_banned");
            }
        }
    }

    public static String danhSachTrucTuyen() {
        List<VXLNguoiChoi> danhSach = new ArrayList<>();
        for (VXLNguoiChoi nguoiChoi : VXLNguoiChoi.players_id.values()) {
            if (nguoiChoi != null) {
                danhSach.add(nguoiChoi);
            }
        }
        danhSach.sort(Comparator.comparing(nguoiChoi -> nguoiChoi.ten == null ? "" : nguoiChoi.ten,
                String.CASE_INSENSITIVE_ORDER));
        StringBuilder ketQua = new StringBuilder("Kết nối: ")
                .append(VXLQuanLyMayChu.getOnlineCount())
                .append(" | Đã vào nhân vật: ").append(danhSach.size());
        for (VXLNguoiChoi nguoiChoi : danhSach) {
            ketQua.append("\n- ").append(nguoiChoi.ten)
                    .append(" (ID ").append(nguoiChoi.ma)
                    .append(", cấp ").append(nguoiChoi.cap).append(')');
        }
        return ketQua.toString();
    }

    public static String luuNguoiChoi(String ten) {
        if (ten == null || ten.isBlank() || "all".equalsIgnoreCase(ten)) {
            int daLuu = 0;
            for (VXLNguoiChoi nguoiChoi : VXLNguoiChoi.players_id.values()) {
                if (nguoiChoi != null) {
                    nguoiChoi.flushCache();
                    daLuu++;
                }
            }
            return "Đã lưu " + daLuu + " người chơi trực tuyến.";
        }
        VXLNguoiChoi nguoiChoi = timNguoiChoiTrucTuyen(ten);
        if (nguoiChoi == null) {
            return "Người chơi " + ten + " không trực tuyến.";
        }
        nguoiChoi.flushCache();
        return "Đã lưu " + nguoiChoi.ten + ".";
    }

    public static void ghiNhatKy(VXLNguoiChoi quanTri, String lenh, boolean thanhCong, String ketQua) {
        try (Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO admin_audit_log(admin_player_id, command_text, success, result_text) "
                             + "VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, quanTri == null ? 0 : quanTri.ma);
            stmt.setString(2, rutGon(lenh, 500));
            stmt.setBoolean(3, thanhCong);
            stmt.setString(4, rutGon(ketQua, 1000));
            stmt.executeUpdate();
        }
        catch (SQLException ex) {
            LOGGER.log(Level.FINE, "Không thể ghi nhật ký admin.", ex);
        }
    }

    private static String suaNgoaiTuyen(String ten, BoSuaDuLieu boSua,
            BoTaoThongBao boTaoThongBao) throws SQLException {
        try (Connection conn = VXLCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement doc = conn.prepareStatement(
                    "SELECT id, name, gold, gem, stats_json FROM players "
                            + "WHERE LOWER(name) = LOWER(?) LIMIT 1 FOR UPDATE")) {
                doc.setString(1, ten);
                try (ResultSet rs = doc.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return "Không tìm thấy người chơi " + ten + ".";
                    }
                    DuLieuNguoiChoi duLieu = new DuLieuNguoiChoi(rs.getInt("id"),
                            rs.getString("name"), rs.getInt("gold"), rs.getInt("gem"),
                            docJson(rs.getString("stats_json")));
                    boSua.sua(duLieu);
                    try (PreparedStatement ghi = conn.prepareStatement(
                            "UPDATE players SET gold = ?, gem = ?, stats_json = ? WHERE id = ? LIMIT 1")) {
                        ghi.setInt(1, duLieu.vang);
                        ghi.setInt(2, duLieu.ngoc);
                        ghi.setString(3, duLieu.chiSo.toJSONString());
                        ghi.setInt(4, duLieu.ma);
                        ghi.executeUpdate();
                    }
                    conn.commit();
                    return boTaoThongBao.tao(duLieu);
                }
            }
            catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static JSONObject docJson(String json) {
        if (json == null || json.isBlank()) {
            return new JSONObject();
        }
        try {
            Object giaTri = JSON.parse(json);
            return giaTri instanceof JSONObject ? (JSONObject)giaTri : new JSONObject();
        }
        catch (RuntimeException ex) {
            return new JSONObject();
        }
    }

    private static int docInt(JSONObject doiTuong, String khoa, int macDinh) {
        Object giaTri = doiTuong == null ? null : doiTuong.get(khoa);
        if (giaTri == null) {
            return macDinh;
        }
        try {
            return Integer.parseInt(giaTri.toString());
        }
        catch (NumberFormatException ex) {
            return macDinh;
        }
    }

    private static int gioiHanInt(long giaTri) {
        return (int)gioiHan(giaTri, 0L, Integer.MAX_VALUE);
    }

    private static long gioiHan(long giaTri, long nhoNhat, long lonNhat) {
        return Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }

    private static String rutGon(String giaTri, int doDai) {
        if (giaTri == null) {
            return "";
        }
        String chuoi = giaTri.trim();
        return chuoi.length() <= doDai ? chuoi : chuoi.substring(0, doDai);
    }

    private static final class DuLieuNguoiChoi {
        private final int ma;
        private final String ten;
        private int vang;
        private int ngoc;
        private final JSONObject chiSo;

        private DuLieuNguoiChoi(int ma, String ten, int vang, int ngoc, JSONObject chiSo) {
            this.ma = ma;
            this.ten = ten;
            this.vang = vang;
            this.ngoc = ngoc;
            this.chiSo = chiSo;
        }
    }

    @FunctionalInterface
    private interface BoSuaDuLieu {
        void sua(DuLieuNguoiChoi duLieu);
    }

    @FunctionalInterface
    private interface BoTaoThongBao {
        String tao(DuLieuNguoiChoi duLieu);
    }
}
