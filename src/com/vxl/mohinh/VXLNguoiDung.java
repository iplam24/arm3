package com.vxl.mohinh;

// Code by Lọ Thánh Chí Tôn
import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLVatPham;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLDichVuGame;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mang.VXLPhien;
import com.vxl.tienich.VXLDuLieuJson;
import com.vxl.tienich.VXLTienIch;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLNguoiDung {
    public static final ConcurrentMap<String, VXLNguoiDung> users = new ConcurrentHashMap<>();
    private VXLPhien khach;
    public VXLDichVuGame dichVu;
    private int user_id;
    private String tenDangNhap;
    private String matKhau;
    private boolean ban;
    public VXLNguoiChoi nguoiChoi;
    private static final int[] ID_TEMPLATE_BALO = new int[]{85, 90, 95, 100, 105};
    private static final int[] ID_TEMPLATE_BODY = new int[]{35, 40, 45, 50, 55};
    private static final int[] ID_TEMPLATE_LEG = new int[]{10, 15, 20, 25, 30};
    private static final int[] ID_TEMPLATE_WEAPON = new int[]{110, 120, 130, 140, 150, 160, 190, 200};
    private static final int[] ID_TEMPLATE_HEAD = new int[]{0, 1, 2, 3, 4};
    private static final int[] ID_TEMPLATE_HAT = new int[]{60, 65, 70, 75, 80};

    private static boolean laPartHopLe(int[] templateIds, short part) {
        if (VXLQuanLyMayChu.itemTemplates == null) {
            return false;
        }
        for (int templateId : templateIds) {
            VXLMauVatPham template = VXLQuanLyMayChu.itemTemplates.get(templateId);
            if (template != null && template.part == part) {
                return true;
            }
        }
        return false;
    }

    private static final String DEFAULT_STATS_JSON = "{\"power\":100,\"avenger\":100,\"kill\":0,\"dead\":1,\"assist\":0,\"trainingSuccess\":1,\"trainingRebelDefeated\":0,\"busyHammer\":0,\"nHammer\":2,\"exp\":1000,\"point\":0,\"pointAdd\":[1000,0,0,0,0,0],\"pvpWins\":0,\"kamikazeKills\":0,\"bossKills\":0,\"pvpDamage\":0,\"dailyDate\":\"\",\"dailyPvpWins\":0,\"dailyKamikazeKills\":0,\"dailyBossKills\":0,\"dailyPvpClaimed\":false,\"dailyKamikazeClaimed\":false,\"dailyBossClaimed\":false,\"achievementPvpClaimed\":false,\"achievementKamikazeClaimed\":false,\"achievementBossClaimed\":false,\"doubleExpUntil\":0}";

    public VXLNguoiDung(VXLPhien khach, VXLDichVuGame dichVu) {
        this.khach = khach;
        this.dichVu = dichVu;
    }

    private static String khoaNguoiDung(String ten) {
        return ten == null ? "" : ten.trim().toLowerCase(Locale.ROOT);
    }

    public static VXLNguoiDung timNguoiDungTheoTen(String ten) {
        String khoa = khoaNguoiDung(ten);
        return khoa.isEmpty() ? null : users.get(khoa);
    }

    public static VXLNguoiDung dangNhap(VXLPhien s, String tenDangNhap, String matKhau, String phienBan, byte loai) {
        if (s == null || tenDangNhap == null || matKhau == null || tenDangNhap.trim().isEmpty()) {
            return null;
        }
        tenDangNhap = tenDangNhap.trim();
        VXLNguoiDung us = new VXLNguoiDung(s, (VXLDichVuGame)s.layDichVu());
        try {
            if (tenDangNhap.startsWith("nvn_") && matKhau.equals("a")) {
                matKhau = "";
            }
            try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `accounts` WHERE `username` = ? AND `password` = ? LIMIT 1;")) {
                stmt.setString(1, tenDangNhap);
                stmt.setString(2, matKhau);
                ResultSet res = stmt.executeQuery();
                if (res != null && res.next()) {
                    us.user_id = res.getInt("id");
                    us.ban = res.getBoolean("is_banned");
                    if (us.ban) {
                        us.dichVu.moHopThoaiOK("Tài khoản đã bị khóa.");
                        res.close();
                        return null;
                    }
                    us.tenDangNhap = res.getString("username");
                    us.matKhau = res.getString("password");
                    res.close();
                    String userKey = khoaNguoiDung(us.tenDangNhap);
                    VXLNguoiDung user = users.putIfAbsent(userKey, us);
                    if (user != null) {
                        us.dichVu.moHopThoaiOK("Tài khoản này đang được đăng nhập ở nơi khác.");
                        user.khach.guiMaPhien(0);
                        return null;
                    }
                    return us;
                }
                if (res != null) {
                    res.close();
                }
            }
            us.dichVu.moHopThoaiOK("Tài khoản hoặc mật khẩu không chính xác.");
        }
        catch (Exception ex) {
            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING,
                    "Khong the dang nhap tai khoan " + tenDangNhap + ".", ex);
            try {
                us.dichVu.moHopThoaiOK("");
            }
            catch (Exception exception) {
                Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.FINE,
                        "Khong the gui thong bao loi dang nhap.", exception);
            }
        }
        return null;
    }

    public static void dangNhap2(VXLPhien s, String tenDangNhap) {
        if (s != null && tenDangNhap != null && tenDangNhap.isEmpty()) {
            try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO `accounts`(`username`, `password`, `is_banned`, `is_online`) VALUES (?,?,?,?);")) {
                String user = "nvn_" + System.currentTimeMillis();
                stmt.setString(1, user);
                stmt.setString(2, "");
                stmt.setInt(3, 0);
                stmt.setInt(4, 0);
                stmt.execute();
                VXLDichVuGame dichVu = (VXLDichVuGame)s.layDichVu();
                dichVu.taoNguoiDungAo(user);
            }
            catch (SQLException ex) {
                Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void taoNhanVat(VXLTinNhan ms) throws IOException {
        try {
            String ten = ms.docUTF(20, "tên nhân vật").trim();
            short head = ms.boDoc().readShort();
            short leg = ms.boDoc().readShort();
            short body = ms.boDoc().readShort();
            short wing = ms.boDoc().readShort();
            short weapon = ms.boDoc().readShort();
            short hat = ms.boDoc().readShort();
            System.out.println("head: " + head);
            System.out.println("body: " + body);
            System.out.println("leg: " + leg);
            System.out.println("wing: " + wing);
            System.out.println("wp: " + weapon);
            System.out.println("hat: " + hat);
            if (ten.isEmpty()) {
                this.dichVu.moHopThoaiOK("Tên không hợp lệ!");
                return;
            }
            if (!laPartHopLe(ID_TEMPLATE_HEAD, head)
                    || !laPartHopLe(ID_TEMPLATE_LEG, leg)
                    || !laPartHopLe(ID_TEMPLATE_BODY, body)
                    || !laPartHopLe(ID_TEMPLATE_BALO, wing)
                    || !laPartHopLe(ID_TEMPLATE_WEAPON, weapon)
                    || !laPartHopLe(ID_TEMPLATE_HAT, hat)) {
                this.dichVu.moHopThoaiOK("Dữ liệu tạo nhân vật không hợp lệ.");
                return;
            }
            try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `name` = ?;")) {
                stmt.setString(1, ten);
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) {
                        this.dichVu.moHopThoaiOK("Tên nhân vật đã tồn tại.");
                        return;
                    }
                }
                try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO `players` (`account_id`, `name`, `gold`, `cup`, `gem`, `stats_json`, `inventory_json`, `equipped_json`, `pocket_json`, `storage_json`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                    stmt2.setInt(1, this.user_id);
                    stmt2.setString(2, ten);
                    stmt2.setInt(3, 1000000);
                    stmt2.setInt(4, 0);
                    stmt2.setInt(5, 1000);
                    stmt2.setString(6, DEFAULT_STATS_JSON);
                    stmt2.setString(7, "[]");
                    stmt2.setString(8, "[]");
                    stmt2.setString(9, "[]");
                    stmt2.setString(10, "[]");
                    stmt2.executeUpdate();
                }
                try (PreparedStatement stmt3 = conn.prepareStatement("SELECT * FROM `players` WHERE `account_id` = ? LIMIT 1;")) {
                    stmt3.setInt(1, this.user_id);
                    try (ResultSet res2 = stmt3.executeQuery()) {
                        if (res2.next()) {
                            this.nguoiChoi = new VXLNguoiChoi(this.dichVu);
                            this.nguoiChoi.ma = res2.getInt("id");
                            this.nguoiChoi.ten = ten;
                            this.nguoiChoi.vang = 1000000;
                            this.nguoiChoi.ngoc = 1000;
                            this.nguoiChoi.cup = 0;
                            this.nguoiChoi.x = (short)70;
                            this.nguoiChoi.y = (short)360;
                            this.nguoiChoi.head = head;
                            this.nguoiChoi.hat = hat;
                            this.nguoiChoi.leg = leg;
                            this.nguoiChoi.body = body;
                            this.nguoiChoi.wing = wing;
                            this.nguoiChoi.wp = weapon;
                            JSONObject stats = (JSONObject)JSON.parse(res2.getString("stats_json"));
                            VXLDuLieuJson p = new VXLDuLieuJson(stats);
                            this.nguoiChoi.kinhNghiem = Math.max(0, p.getInt("exp"));
                            this.nguoiChoi.cap = VXLTienIch.layCap(this.nguoiChoi.kinhNghiem);
                            this.nguoiChoi.point = p.getShort("point");
                            this.nguoiChoi.trainingSuccess = p.getByte("trainingSuccess");
                            this.nguoiChoi.busyHammer = p.getByte("busyHammer");
                            this.nguoiChoi.nHammer = p.getByte("nHammer");
                            this.nguoiChoi.kill = p.getInt("kill");
                            this.nguoiChoi.chet = p.getInt("dead");
                            this.nguoiChoi.assist = p.getInt("assist");
                            this.nguoiChoi.powerAvenger = p.getByte("avenger");
                            this.nguoiChoi.power = p.getByte("power");
                            JSONArray jArr = p.getJSONArray("pointAdd");
                            this.nguoiChoi.pointAdd = new short[]{1000, 0, 0, 0, 0, 0};
                            if (jArr != null) {
                                for (int i = 0; i < Math.min(6, jArr.size()); ++i) {
                                    this.nguoiChoi.pointAdd[i] = safeShort(jArr.get(i), this.nguoiChoi.pointAdd[i]);
                                }
                            }
                            this.nguoiChoi.taiTienTrinhGame(stats);
                            int headId = -1;
                            int legId = -1;
                            int bodyId = -1;
                            int wingId = -1;
                            int maVuKhi = -1;
                            int hatId = -1;
                            for (int ma : ID_TEMPLATE_BODY) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t != null && t.part == body) {
                                    bodyId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_LEG) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t != null && t.part == leg) {
                                    legId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_WEAPON) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t != null && t.part == weapon) {
                                    maVuKhi = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_BALO) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t != null && t.part == wing) {
                                    wingId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_HEAD) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t != null && t.part == head) {
                                    headId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_HAT) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t != null && t.part == hat) {
                                    hatId = t.ma;
                                    break;
                                }
                            }
                            if (headId < 0 || legId < 0 || bodyId < 0 || wingId < 0 || maVuKhi < 0 || hatId < 0) {
                                this.dichVu.moHopThoaiOK("Dữ liệu trang bị khởi tạo không hợp lệ.");
                                return;
                            }
                            VXLVatPham vatPham = new VXLVatPham(headId);
                            vatPham.chiSo = vatPham.mau.loai;
                            vatPham.thayMau(vatPham.mau);
                            this.nguoiChoi.itemBody[vatPham.chiSo] = vatPham;
                            VXLVatPham item2 = new VXLVatPham(legId);
                            item2.chiSo = item2.mau.loai;
                            item2.thayMau(item2.mau);
                            this.nguoiChoi.itemBody[item2.chiSo] = item2;
                            VXLVatPham item3 = new VXLVatPham(bodyId);
                            item3.chiSo = item3.mau.loai;
                            item3.thayMau(item3.mau);
                            this.nguoiChoi.itemBody[item3.chiSo] = item3;
                            VXLVatPham item4 = new VXLVatPham(wingId);
                            item4.chiSo = item4.mau.loai;
                            item4.thayMau(item4.mau);
                            this.nguoiChoi.itemBody[item4.chiSo] = item4;
                            int thamSo = item4.getParamById(13);
                            this.nguoiChoi.itemBalo = new int[thamSo];
                            for (int i = 0; i < thamSo; ++i) {
                                this.nguoiChoi.itemBalo[i] = -1;
                            }
                            VXLVatPham item5 = new VXLVatPham(maVuKhi);
                            item5.chiSo = item5.mau.loai;
                            item5.thayMau(item5.mau);
                            this.nguoiChoi.itemBody[item5.chiSo] = item5;
                            VXLVatPham item6 = new VXLVatPham(hatId);
                            item6.chiSo = item6.mau.loai;
                            item6.thayMau(item6.mau);
                            this.nguoiChoi.itemBody[item6.chiSo] = item6;
                            this.nguoiChoi.dichVu.datNguoiChoi(this.nguoiChoi);
                            this.khach.guiThongTin();
                        } else {
                            this.dichVu.moHopThoaiOK("Có lỗi xảy ra.");
                        }
                    }
                }
            }
        }
        catch (IOException ex) {
            throw ex;
        }
        catch (IllegalArgumentException ex) {
            throw ex;
        }
        catch (Exception ex) {
            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taiDuLieuNguoiChoi() {
        try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `account_id` = ? LIMIT 1;");
             ResultSet res = moKetQuaNguoiChoi(stmt, this.user_id)) {
            if (res != null && res.next()) {
                this.nguoiChoi = new VXLNguoiChoi(this.dichVu);
                this.nguoiChoi.ma = res.getInt("id");
                this.nguoiChoi.ten = res.getString("name");
                this.nguoiChoi.vang = res.getInt("gold");
                this.nguoiChoi.ngoc = res.getInt("gem");
                this.nguoiChoi.cup = res.getInt("cup");
                this.nguoiChoi.x = (short)70;
                this.nguoiChoi.y = (short)360;
                this.nguoiChoi.head = (short)-1;
                this.nguoiChoi.hat = (short)-1;
                this.nguoiChoi.leg = (short)-1;
                this.nguoiChoi.body = (short)-1;
                this.nguoiChoi.wing = (short)-1;
                this.nguoiChoi.wp = (short)-1;
                JSONObject stats = docJsonObject(res.getString("stats_json"));
                VXLDuLieuJson p = new VXLDuLieuJson(stats);
                this.nguoiChoi.kinhNghiem = p.getInt("exp");
                this.nguoiChoi.cap = VXLTienIch.layCap(this.nguoiChoi.kinhNghiem);
                this.nguoiChoi.point = p.getShort("point");
                this.nguoiChoi.trainingSuccess = p.getByte("trainingSuccess");
                this.nguoiChoi.busyHammer = p.getByte("busyHammer");
                this.nguoiChoi.nHammer = p.getByte("nHammer");
                this.nguoiChoi.kill = p.getInt("kill");
                this.nguoiChoi.chet = p.getInt("dead");
                this.nguoiChoi.assist = p.getInt("assist");
                this.nguoiChoi.powerAvenger = p.getByte("avenger");
                this.nguoiChoi.power = p.getByte("power");
                JSONArray pointAdds = p.getJSONArray("pointAdd");
                this.nguoiChoi.pointAdd = new short[]{1000, 0, 0, 0, 0, 0};
                if (pointAdds != null) {
                    for (int i = 0; i < Math.min(this.nguoiChoi.pointAdd.length, pointAdds.size()); ++i) {
                        try {
                            this.nguoiChoi.pointAdd[i] = Short.parseShort(pointAdds.get(i).toString());
                        }
                        catch (RuntimeException valueEx) {
                            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING, "Bỏ qua pointAdd bị lỗi tại vị trí " + i, valueEx);
                        }
                    }
                }
                this.nguoiChoi.taiTienTrinhGame(stats);
                JSONArray bags = docJsonArray(res.getString("inventory_json"));
                for (int i = 0; bags != null && i < bags.size(); ++i) {
                    try {
                        VXLVatPham vatPham = new VXLVatPham((JSONObject)bags.get(i));
                        if (vatPham.chiSo >= 0 && vatPham.chiSo < this.nguoiChoi.itemBag.length) {
                            this.nguoiChoi.itemBag[vatPham.chiSo] = vatPham;
                        } else {
                            Logger.getLogger(VXLNguoiDung.class.getName()).warning("Bỏ qua vật phẩm trong túi có chỉ số lỗi: " + vatPham.chiSo);
                        }
                    }
                    catch (RuntimeException itemEx) {
                        Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING, "Bỏ qua vật phẩm trong túi bị lỗi.", itemEx);
                    }
                }
                JSONArray bodys = docJsonArray(res.getString("equipped_json"));
                for (int i = 0; bodys != null && i < bodys.size(); ++i) {
                    try {
                        VXLVatPham vatPham = new VXLVatPham((JSONObject)bodys.get(i));
                        if (vatPham.chiSo < 0 || vatPham.chiSo >= this.nguoiChoi.itemBody.length) {
                            Logger.getLogger(VXLNguoiDung.class.getName()).warning("Bỏ qua trang bị có chỉ số lỗi: " + vatPham.chiSo);
                            continue;
                        }
                        int soOBalo = -1;
                        if (vatPham.mau.loai == 4) {
                            soOBalo = vatPham.getParamById(13);
                            if (soOBalo < 0 || soOBalo > this.nguoiChoi.itemBag.length) {
                                throw new IllegalArgumentException("Số ô ba lô không hợp lệ: " + soOBalo);
                            }
                        }
                        this.nguoiChoi.itemBody[vatPham.chiSo] = vatPham;
                        this.nguoiChoi.datTrangBiChoNhanVat(vatPham);
                        if (soOBalo >= 0) {
                            this.nguoiChoi.itemBalo = new int[soOBalo];
                            for (int slot = 0; slot < soOBalo; ++slot) {
                                this.nguoiChoi.itemBalo[slot] = -1;
                            }
                        }
                    }
                    catch (RuntimeException itemEx) {
                        Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING, "Bỏ qua trang bị bị lỗi.", itemEx);
                    }
                }
                JSONArray balos = docJsonArray(res.getString("pocket_json"));
                int soOCanTai = balos == null ? 0 : Math.min(balos.size(), this.nguoiChoi.itemBalo.length);
                for (int i = 0; i < soOCanTai; ++i) {
                    try {
                        int chiSo = Integer.parseInt(balos.get(i).toString());
                        if (chiSo >= 0 && chiSo < this.nguoiChoi.itemBag.length && this.nguoiChoi.itemBag[chiSo] != null) {
                            this.nguoiChoi.itemBalo[i] = chiSo;
                        }
                    }
                    catch (RuntimeException valueEx) {
                        Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING, "Bỏ qua ô ba lô bị lỗi tại vị trí " + i, valueEx);
                    }
                }
                JSONArray box = docJsonArray(res.getString("storage_json"));
                for (int i = 0; box != null && i < box.size(); ++i) {
                    try {
                        VXLVatPham vatPham = new VXLVatPham((JSONObject)box.get(i));
                        if (vatPham.chiSo >= 0 && vatPham.chiSo < this.nguoiChoi.itemBox.length) {
                            this.nguoiChoi.itemBox[vatPham.chiSo] = vatPham;
                        } else {
                            Logger.getLogger(VXLNguoiDung.class.getName()).warning("Bỏ qua vật phẩm trong rương có chỉ số lỗi: " + vatPham.chiSo);
                        }
                    }
                    catch (RuntimeException itemEx) {
                        Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING, "Bỏ qua vật phẩm trong rương bị lỗi.", itemEx);
                    }
                }
                this.nguoiChoi.dichVu.datNguoiChoi(this.nguoiChoi);
                return;
            }
        }
        catch (Exception ex) {
            this.nguoiChoi = null;
            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.SEVERE, "Không thể tải dữ liệu người chơi.", ex);
            throw new IllegalStateException("Không thể tải dữ liệu người chơi.", ex);
        }
    }

    public void thanhTich(VXLTinNhan mss) {
        try {
            if (this.nguoiChoi != null) {
                this.nguoiChoi.nhiemVu.guiThanhTich();
            }
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING, "Không thể gửi bảng thành tích.", ex);
        }
    }
    public void close() {
        if (this.tenDangNhap != null) {
            users.remove(khoaNguoiDung(this.tenDangNhap), this);
        }
        if (this.nguoiChoi != null) {
            this.nguoiChoi.close();
        }
    }

    public String toString() {
        return this.tenDangNhap;
    }

    private static ResultSet moKetQuaNguoiChoi(PreparedStatement stmt, int maTaiKhoan) throws SQLException {
        stmt.setInt(1, maTaiKhoan);
        return stmt.executeQuery();
    }

    private static short safeShort(Object value, short macDinh) {
        if (value == null) {
            return macDinh;
        }
        try {
            int ketQua = Integer.parseInt(value.toString());
            return (short)Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, ketQua));
        }
        catch (NumberFormatException ex) {
            return macDinh;
        }
    }

    private static JSONObject docJsonObject(String json) {
        if (json == null || json.isBlank()) {
            return new JSONObject();
        }
        try {
            Object value = JSON.parse(json);
            return value instanceof JSONObject ? (JSONObject)value : new JSONObject();
        }
        catch (RuntimeException ex) {
            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING, "Bỏ qua JSON object bị lỗi.", ex);
            return new JSONObject();
        }
    }

    private static JSONArray docJsonArray(String json) {
        if (json == null || json.isBlank()) {
            return new JSONArray();
        }
        try {
            Object value = JSON.parse(json);
            return value instanceof JSONArray ? (JSONArray)value : new JSONArray();
        }
        catch (RuntimeException ex) {
            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.WARNING, "Bỏ qua JSON array bị lỗi.", ex);
            return new JSONArray();
        }
    }
}
