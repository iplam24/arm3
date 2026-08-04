package com.vxl.mohinh;

// Vũ Xuân Lâm đẹp trai VCL
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLNguoiDung {
    public static HashMap<String, VXLNguoiDung> users = new HashMap();
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
    private static final String DEFAULT_STATS_JSON = "{\"power\":100,\"avenger\":100,\"kill\":0,\"dead\":1,\"assist\":0,\"trainingSuccess\":1,\"busyHammer\":0,\"nHammer\":2,\"exp\":1000,\"point\":0,\"pointAdd\":[1000,0,0,0,0,0]}";

    public VXLNguoiDung(VXLPhien khach, VXLDichVuGame dichVu) {
        this.khach = khach;
        this.dichVu = dichVu;
    }

    public static VXLNguoiDung timNguoiDungTheoTen(String ten) {
        return users.get(ten);
    }

    public static VXLNguoiDung dangNhap(VXLPhien s, String tenDangNhap, String matKhau, String phienBan, byte loai) {
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
                    VXLNguoiDung user = VXLNguoiDung.timNguoiDungTheoTen(tenDangNhap);
                    if (user != null) {
                        us.dichVu.moHopThoaiOK("Tai khoan nay co nguoi su dung.");
                        user.khach.guiMaPhien(0);
                        res.close();
                        return null;
                    }
                    us.user_id = res.getInt("id");
                    us.ban = res.getBoolean("is_banned");
                    if (us.ban) {
                        us.dichVu.moHopThoaiOK("Tai khoan da bi khoa.");
                        res.close();
                        return null;
                    }
                    us.tenDangNhap = res.getString("username");
                    us.matKhau = res.getString("password");
                    res.close();
                    users.put(us.tenDangNhap, us);
                    return us;
                }
                if (res != null) {
                    res.close();
                }
            }
            us.dichVu.moHopThoaiOK("Tai khoan hoac mat khau khong chinh xac.");
        }
        catch (Exception ex) {
            try {
                us.dichVu.moHopThoaiOK("Loi dang nhap.");
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static void dangNhap2(VXLPhien s, String tenDangNhap) {
        if (tenDangNhap.isEmpty()) {
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

    public void taoNhanVat(VXLTinNhan ms) {
        try {
            String ten = ms.boDoc().readUTF();
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
            if (ten.equals("")) {
                this.dichVu.moHopThoaiOK("Tên không hợp lệ!");
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
                            this.nguoiChoi.kinhNghiem = 1000000;
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
                            this.nguoiChoi.pointAdd = new short[6];
                            for (int i = 0; i < 6; ++i) {
                                this.nguoiChoi.pointAdd[i] = Short.parseShort(jArr.get(i).toString());
                            }
                            int headId = -1;
                            int legId = -1;
                            int bodyId = -1;
                            int wingId = -1;
                            int maVuKhi = -1;
                            int hatId = -1;
                            for (int ma : ID_TEMPLATE_BODY) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == body) {
                                    bodyId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_LEG) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == leg) {
                                    legId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_WEAPON) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == weapon) {
                                    maVuKhi = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_BALO) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == wing) {
                                    wingId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_HEAD) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == head) {
                                    headId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_HAT) {
                                VXLMauVatPham t = VXLQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == hat) {
                                    hatId = t.ma;
                                    break;
                                }
                            }
                            VXLVatPham vatPham = new VXLVatPham(headId);
                            vatPham.chiSo = vatPham.mau.loai;
                            vatPham.itemOptions = vatPham.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[vatPham.chiSo] = vatPham;
                            VXLVatPham item2 = new VXLVatPham(legId);
                            item2.chiSo = item2.mau.loai;
                            item2.itemOptions = item2.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item2.chiSo] = item2;
                            VXLVatPham item3 = new VXLVatPham(bodyId);
                            item3.chiSo = item3.mau.loai;
                            item3.itemOptions = item3.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item3.chiSo] = item3;
                            VXLVatPham item4 = new VXLVatPham(wingId);
                            item4.chiSo = item4.mau.loai;
                            item4.itemOptions = item4.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item4.chiSo] = item4;
                            int thamSo = item4.getParamById(13);
                            this.nguoiChoi.itemBalo = new int[thamSo];
                            for (int i = 0; i < thamSo; ++i) {
                                this.nguoiChoi.itemBalo[i] = -1;
                            }
                            VXLVatPham item5 = new VXLVatPham(maVuKhi);
                            item5.chiSo = item5.mau.loai;
                            item5.itemOptions = item5.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item5.chiSo] = item5;
                            VXLVatPham item6 = new VXLVatPham(hatId);
                            item6.chiSo = item6.mau.loai;
                            item6.itemOptions = item6.mau.thuocTinhs;
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
        catch (Exception ex) {
            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taiDuLieuNguoiChoi() {
        try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `account_id` = ? LIMIT 1;")) {
            stmt.setInt(1, this.user_id);
            ResultSet res = stmt.executeQuery();
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
                VXLDuLieuJson p = new VXLDuLieuJson((JSONObject)JSON.parse(res.getString("stats_json")));
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
                JSONArray jArr = p.getJSONArray("pointAdd");
                this.nguoiChoi.pointAdd = new short[6];
                for (int i = 0; i < 6; ++i) {
                    this.nguoiChoi.pointAdd[i] = Short.parseShort(jArr.get(i).toString());
                }
                JSONArray bags = (JSONArray)JSON.parse(res.getString("inventory_json"));
                for (int i = 0; i < bags.size(); ++i) {
                    VXLVatPham vatPham = new VXLVatPham((JSONObject)bags.get(i));
                    this.nguoiChoi.itemBag[vatPham.chiSo] = vatPham;
                }
                JSONArray bodys = (JSONArray)JSON.parse(res.getString("equipped_json"));
                for (int i = 0; i < bodys.size(); ++i) {
                    VXLVatPham vatPham = new VXLVatPham((JSONObject)bodys.get(i));
                    this.nguoiChoi.itemBody[vatPham.chiSo] = vatPham;
                    this.nguoiChoi.datTrangBiChoNhanVat(vatPham);
                    if (vatPham.mau.loai == 4) {
                        int thamSo = vatPham.getParamById(13);
                        this.nguoiChoi.itemBalo = new int[thamSo];
                        for (int a = 0; a < thamSo; ++a) {
                            this.nguoiChoi.itemBalo[a] = -1;
                        }
                    }
                }
                JSONArray balos = (JSONArray)JSON.parse(res.getString("pocket_json"));
                for (int i = 0; i < balos.size(); ++i) {
                    int chiSo = Integer.parseInt(balos.get(i).toString());
                    if (chiSo != -1 && this.nguoiChoi.itemBag[chiSo] != null) {
                        this.nguoiChoi.itemBalo[i] = chiSo;
                    }
                }
                JSONArray box = (JSONArray)JSON.parse(res.getString("storage_json"));
                for (int i = 0; i < box.size(); ++i) {
                    VXLVatPham vatPham = new VXLVatPham((JSONObject)box.get(i));
                    this.nguoiChoi.itemBox[vatPham.chiSo] = vatPham;
                }
                res.close();
                this.nguoiChoi.dichVu.datNguoiChoi(this.nguoiChoi);
                return;
            }
            res.close();
        }
        catch (SQLException ex) {
            Logger.getLogger(VXLNguoiDung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void thanhTich(VXLTinNhan mss) {
        try {
            System.out.println("📥 Player yêu cầu xem thành tích");
            VXLTinNhan ms = new VXLTinNhan(88);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(0);
            ds.writeUTF("🎖 BẢNG THÀNH TÍCH CÁ NHÂN");
            ds.writeInt(1);
            ds.writeInt(1);
            ds.writeInt(1);
            ds.writeInt(1);
            ds.writeByte(3);
            ds.writeByte(1);
            ds.writeBoolean(false);
            ds.writeByte(2);
            ds.writeBoolean(true);
            ds.writeByte(3);
            ds.writeBoolean(false);
            ds.flush();
            this.dichVu.guiTin(ms);
            System.out.println("📤 Đã gửi bảng thành tích  ");
        }
        catch (Exception e) {
            System.out.println("❌ Lỗi gửi thành tích ");
            e.printStackTrace();
        }
    }

    public void close() {
        users.remove(this.tenDangNhap);
        if (this.nguoiChoi != null) {
            this.nguoiChoi.close();
        }
    }

    public String toString() {
        return this.tenDangNhap;
    }
}
