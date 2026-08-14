package com.vxl.loi;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.dulieu.VXLTieuDeCap;
import com.vxl.dulieu.VXLBoPhan;
import com.vxl.dulieu.VXLAnhBoPhan;
import com.vxl.dulieu.VXLAnhNho;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import com.vxl.vatpham.VXLMauThuocTinhVatPham;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.bando.VXLDuLieuBanDo;
import com.vxl.mang.VXLPhien;
import com.vxl.mang.kenh.VXLMayChuNetty;
import com.vxl.phong.VXLQuanLyPhong;
import com.vxl.nhapvai.VXLBanDoRPG;
import com.vxl.cuahang.VXLCuaHang;
import com.vxl.tienich.VXLDuLieuJson;
import com.vxl.tienich.VXLTienIch;
import com.vxl.luyentap.VXLDatLaiPhienQuanHangNgay;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLQuanLyMayChu {
    public static final String GAME_NAME = "Lọ Chéo 3";
    private static boolean goLoi;
    protected static String mayChu;
    protected static short cong;
    protected static String mysql_host;
    protected static String mysql_user;
    protected static String mysql_pass;
    protected static String mysql_database;
    protected static int numClients;
    protected static ArrayList<VXLPhien> clients;
    protected static VXLMayChuNetty nettyServer;
    protected static boolean batDau;
    protected static int ma;
    public static int dailyGold;
    public static int dailyGem;
    public static int clanCreateGold;
    public static int wheelGemCost;
    public static int eventIntervalMinutes;
    public static int eventDurationMinutes;
    public static int worldTreasureIntervalMinutes;
    public static int worldBossHp;
    public static byte vBig;
    public static byte vData;
    public static byte vItem;
    public static byte vMap;
    public static int[] nBig;
    public static String[] dataSize;
    public static HashMap<Integer, VXLMauThuocTinhVatPham> iOptionTemplates;
    public static HashMap<Integer, VXLMauVatPham> itemTemplates;
    public static HashMap<Integer, VXLBoPhan> parts;
    public static final VXLCuaHang SHOP_EQUIP;
    public static final VXLCuaHang SHOP_ITEM;
    public static byte maxElementFight;
    public static byte maxPlayers;
    protected static byte nPlayersInitRoom;
    protected static byte initMap;
    protected static byte initMapBoss;

    private static void loadDataItem() {
        try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `item_options`");
                 ResultSet res = stmt.executeQuery()) {
                while (res.next()) {
                    VXLMauThuocTinhVatPham optionTemplate = new VXLMauThuocTinhVatPham();
                    optionTemplate.ma = res.getInt("id");
                    optionTemplate.ten = res.getString("name");
                    optionTemplate.loai = res.getInt("type");
                    iOptionTemplates.put(optionTemplate.ma, optionTemplate);
                }
            }
            ArrayList<VXLMauVatPham> weapons = new ArrayList<VXLMauVatPham>();
            ArrayList<VXLMauVatPham> clothes = new ArrayList<VXLMauVatPham>();
            ArrayList<VXLMauVatPham> hairs = new ArrayList<VXLMauVatPham>();
            ArrayList<VXLMauVatPham> balos = new ArrayList<VXLMauVatPham>();
            ArrayList<VXLMauVatPham> ngocs = new ArrayList<VXLMauVatPham>();
            ArrayList<VXLMauVatPham> vatPhams = new ArrayList<VXLMauVatPham>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `items`");
                 ResultSet res = stmt.executeQuery()) {
                while (res.next()) {
                    int ma = res.getInt("id");
                    String ten = res.getString("name");
                    String desc = res.getString("description");
                    byte loai = res.getByte("type");
                    byte cap = res.getByte("level");
                    short icon = res.getShort("icon");
                    short part = res.getShort("part_id");
                    int require = res.getInt("strength_required");
                    byte gioiTinh = res.getByte("gender");
                    int vang = res.getInt("buy_gold");
                    int ngoc = res.getInt("buy_gem");
                    VXLMauVatPham vatPham = new VXLMauVatPham((short)ma, loai, gioiTinh, ten, desc, cap, require, icon, part, false);
                    JSONArray arr = (JSONArray)JSON.parse((String)res.getString("options"));
                    for (int i = 0; i < arr.size(); ++i) {
                        VXLDuLieuJson p = new VXLDuLieuJson((JSONObject)arr.get(i));
                        vatPham.thuocTinhs.add(new VXLThuocTinhVatPham(p.getInt("id"), p.getInt("param")));
                    }
                    vatPham.buyGem = ngoc;
                    vatPham.buyGold = vang;
                    itemTemplates.put(ma, vatPham);
                    if (vang + ngoc <= 0) continue;
                    if (loai == 10) {
                        vatPhams.add(vatPham);
                        continue;
                    }
                    if (loai == 12) {
                        ngocs.add(vatPham);
                        continue;
                    }
                    if (loai == 1 || loai == 2) {
                        clothes.add(vatPham);
                        continue;
                    }
                    if (loai == 0 || loai == 3) {
                        hairs.add(vatPham);
                        continue;
                    }
                    if (loai == 4 || ma == 349 || ma == 399 || ma == 350 || ma == 351 || ma == 352) {
                        balos.add(vatPham);
                        continue;
                    }
                    if (loai != 5) continue;
                    weapons.add(vatPham);
                }
            }
            VXLCuaHang.SHOP_ITEM.themTab("vật\nPhẩm", vatPhams);
            VXLCuaHang.SHOP_ITEM.themTab("Ngọc", ngocs);
            VXLCuaHang.SHOP_EQUIP.themTab("Giáp", clothes);
            VXLCuaHang.SHOP_EQUIP.themTab("Nón\nTóc", hairs);
            VXLCuaHang.SHOP_EQUIP.themTab("Hỗ\nTrợ", balos);
            VXLCuaHang.SHOP_EQUIP.themTab("Súng", weapons);
        }
        catch (SQLException | RuntimeException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadDataMap() {
        try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `game_maps` ORDER BY `id`");
             ResultSet res = stmt.executeQuery()) {
            VXLDuLieuBanDo.entrys = new ArrayList();
            VXLDuLieuBanDo.brickEntrys = new ArrayList();
            while (res.next()) {
                byte maBanDo = res.getByte("id");
                String ten = res.getString("name");
                short icon = res.getShort("icon");
                byte background = res.getByte("background");
                byte[] ab = VXLTienIch.layTep("res/map/" + maBanDo);
                VXLDuLieuBanDo.MapDataEntry map = new VXLDuLieuBanDo.MapDataEntry(ab, maBanDo, ten, icon, background);
                VXLDuLieuBanDo.entrys.add(map);
            }
        }
        catch (SQLException | RuntimeException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadDataCaptionLevel() {
        try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `caption_levels` ORDER BY `id`");
             ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                int kinhNghiem = res.getInt("exp");
                String ten = res.getString("name");
                short icon = chuanHoaBieuTuongCap(res.getShort("icon"));
                int ma = res.getInt("id");
                VXLTieuDeCap cap = new VXLTieuDeCap();
                cap.kinhNghiem = kinhNghiem;
                cap.ten = ten;
                cap.icon = icon;
                VXLTieuDeCap.levels.put(ma, cap);
            }
        }
        catch (SQLException | RuntimeException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static short chuanHoaBieuTuongCap(short icon) {
        if (icon >= 1302 && icon <= 1304) {
            return (short)(1248 + icon - 1302);
        }
        return icon;
    }

    private static void loadDataPart() {
        try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `avatar_parts`");
             ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                int ma = res.getInt("id");
                String duLieu = res.getString("part_data");
                byte loai = res.getByte("type");
                JSONArray jArr = duLieu == null ? new JSONArray() : (JSONArray)JSON.parse(duLieu);
                VXLBoPhan part = new VXLBoPhan(loai);
                if (jArr.size() < part.pi.length) {
                    throw new IllegalArgumentException("Avatar part data is incomplete: " + ma);
                }
                for (int i = 0; i < part.pi.length; ++i) {
                    Object giaTri = jArr.get(i);
                    if (!(giaTri instanceof JSONObject doiTuong)) {
                        throw new IllegalArgumentException("Avatar part entry is invalid: " + ma + "/" + i);
                    }
                    part.pi[i] = new VXLAnhBoPhan();
                    part.pi[i].ma = Short.parseShort(doiTuong.get("id").toString());
                    part.pi[i].dx = Byte.parseByte(doiTuong.get("dx").toString());
                    part.pi[i].dy = Byte.parseByte(doiTuong.get("dy").toString());
                }
                parts.put(ma, part);
            }
        }
        catch (SQLException | RuntimeException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadDataImage() {
        try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `sprite_images` ORDER BY `id`");
             ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                int ma = res.getInt("id");
                int maAnh = res.getInt("image_id");
                int x = res.getInt("x");
                int y = res.getInt("y");
                int w = res.getInt("width");
                int h = res.getInt("height");
                VXLAnhNho small = new VXLAnhNho();
                small.maAnh = (byte)maAnh;
                small.x = x;
                small.y = y;
                small.w = w;
                small.h = h;
                VXLAnhNho.smallImg.put(ma, small);
            }
        }
        catch (SQLException | RuntimeException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadConfigFile() {
        byte[] ab = VXLTienIch.layTep("config.conf");
        if (ab == null) {
            throw new IllegalStateException("Config file not found: config.conf");
        }
        String duLieu = new String(ab, StandardCharsets.UTF_8);
        HashMap<String, String> configMap = new HashMap<String, String>();
        StringBuilder sbd = new StringBuilder();
        boolean bo = false;
        for (int i = 0; i <= duLieu.length(); ++i) {
            char es;
            if (i == duLieu.length() || (es = duLieu.charAt(i)) == '\n') {
                int j;
                bo = false;
                String sbf = sbd.toString().trim();
                if (sbf != null && !sbf.equals("") && sbf.charAt(0) != '#' && (j = sbf.indexOf(58)) > 0) {
                    String khoa = sbf.substring(0, j).trim();
                    String giaTri = sbf.substring(j + 1).trim();
                    configMap.put(khoa, giaTri);
                    String giaTriHienThi = khoa.toLowerCase().contains("password") ? "******" : giaTri;
                    System.out.println("config: " + khoa + "-" + giaTriHienThi);
                }
                sbd.setLength(0);
                continue;
            }
            if (es == '#') {
                bo = true;
            }
            if (bo) continue;
            sbd.append(es);
        }
        goLoi = VXLQuanLyMayChu.cfgBool(configMap, false, "debug-mode", "debug");
        mayChu = VXLQuanLyMayChu.cfgStr(configMap, "localhost", "server-host", "host");
        cong = VXLQuanLyMayChu.cfgShort(configMap, (short)14445, "server-port", "port");
        if (cong <= 0) {
            throw new IllegalArgumentException("server-port must be between 1 and 65535");
        }
        mysql_host = VXLQuanLyMayChu.cfgStr(configMap, "127.0.0.1", "database-host", "mysql-host");
        mysql_user = VXLQuanLyMayChu.cfgStr(configMap, "root", "database-user", "mysql-user");
        mysql_pass = VXLQuanLyMayChu.cfgStr(configMap, "", "database-password", "mysql-password");
        mysql_database = VXLQuanLyMayChu.cfgStr(configMap, "locheo3", "database-name", "mysql-database");
        vBig = VXLQuanLyMayChu.cfgByte(configMap, (byte)0, "client-version-big", "vBig");
        vData = VXLQuanLyMayChu.cfgByte(configMap, (byte)0, "client-version-data", "vData");
        vItem = VXLQuanLyMayChu.cfgByte(configMap, (byte)0, "client-version-item", "vItem");
        vMap = VXLQuanLyMayChu.cfgByte(configMap, (byte)0, "client-version-map", "vMap");
        dailyGold = VXLQuanLyMayChu.cfgInt(configMap, dailyGold, "reward-daily-gold", "daily-gold");
        dailyGem = VXLQuanLyMayChu.cfgInt(configMap, dailyGem, "reward-daily-gem", "daily-gem");
        eventIntervalMinutes = VXLQuanLyMayChu.cfgInt(configMap, eventIntervalMinutes, "event-rotation-minutes", "event-interval-minutes");
        eventDurationMinutes = VXLQuanLyMayChu.cfgInt(configMap, eventDurationMinutes, "event-duration-minutes");
        clanCreateGold = VXLQuanLyMayChu.cfgInt(configMap, clanCreateGold, "clan-create-cost-gold", "clan-create-gold");
        wheelGemCost = VXLQuanLyMayChu.cfgInt(configMap, wheelGemCost, "lucky-wheel-gem-cost", "wheel-gem-cost");
        worldTreasureIntervalMinutes = VXLQuanLyMayChu.cfgInt(configMap, worldTreasureIntervalMinutes, "world-treasure-spawn-minutes", "world-treasure-interval-minutes");
        worldBossHp = VXLQuanLyMayChu.cfgInt(configMap, worldBossHp, "world-boss-hp");
    }

    private static String cfgStr(HashMap<String, String> map, String def, String... keys) {
        for (String khoa : keys) {
            if (map.containsKey(khoa)) {
                return map.get(khoa);
            }
        }
        return def;
    }

    private static int cfgInt(HashMap<String, String> map, int def, String... keys) {
        String v = VXLQuanLyMayChu.cfgStr(map, null, keys);
        try {
            return v != null ? Integer.parseInt(v) : def;
        }
        catch (NumberFormatException ex) {
            return def;
        }
    }

    private static short cfgShort(HashMap<String, String> map, short def, String... keys) {
        String v = VXLQuanLyMayChu.cfgStr(map, null, keys);
        try {
            return v != null ? Short.parseShort(v) : def;
        }
        catch (NumberFormatException ex) {
            return def;
        }
    }

    private static byte cfgByte(HashMap<String, String> map, byte def, String... keys) {
        String v = VXLQuanLyMayChu.cfgStr(map, null, keys);
        try {
            return v != null ? Byte.parseByte(v) : def;
        }
        catch (NumberFormatException ex) {
            return def;
        }
    }

    private static boolean cfgBool(HashMap<String, String> map, boolean def, String... keys) {
        String v = VXLQuanLyMayChu.cfgStr(map, null, keys);
        return v != null ? Boolean.parseBoolean(v) : def;
    }

    public static int getOnlineCount() {
        return numClients;
    }

    public static boolean isDebug() {
        return goLoi;
    }

    public static void khoiTao() {
        for (int i = 0; i < 4; ++i) {
            int kichThuoc = 0;
            int numberBig = 0;
            File[] files = new File("res/data/" + (i + 1)).listFiles(File::isFile);
            if (files != null) {
                for (File file : files) {
                    kichThuoc = (int)((long)kichThuoc + file.length());
                    ++numberBig;
                }
            }
            VXLQuanLyMayChu.dataSize[i] = VXLTienIch.doiThanhChuoiRutGon(kichThuoc);
            VXLQuanLyMayChu.nBig[i] = numberBig;
        }
        batDau = false;
        VXLQuanLyMayChu.loadConfigFile();
        VXLCoSoDuLieu.khoiTao(mysql_host, mysql_database, mysql_user, mysql_pass);
        iOptionTemplates.clear();
        itemTemplates.clear();
        parts.clear();
        VXLTieuDeCap.levels.clear();
        VXLAnhNho.smallImg.clear();
        VXLQuanLyMayChu.loadDataItem();
        VXLQuanLyMayChu.setDataItem();
        VXLQuanLyMayChu.loadDataMap();
        VXLQuanLyMayChu.setDataMap();
        VXLQuanLyMayChu.loadDataCaptionLevel();
        VXLQuanLyMayChu.setDataCaptionLevel();
        VXLQuanLyMayChu.loadDataPart();
        VXLQuanLyMayChu.setDataPart();
        VXLQuanLyMayChu.loadDataImage();
        VXLQuanLyMayChu.setDataImage();
        VXLBanDoRPG.khoiTaoKhu();
        VXLQuanLyPhong.khoiTao();
    }

    public static void setDataItem() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            ds.writeByte(vItem);
            ds.writeByte(iOptionTemplates.size());
            for (VXLMauThuocTinhVatPham option : iOptionTemplates.values()) {
                ds.writeUTF(option.ten);
                ds.writeByte(option.loai);
            }
            ds.writeShort(itemTemplates.size());
            for (VXLMauVatPham mau : itemTemplates.values()) {
                ds.writeByte(mau.loai);
                ds.writeByte(mau.gioiTinh);
                ds.writeUTF(mau.ten);
                ds.writeUTF(mau.moTa);
                ds.writeByte(mau.cap);
                ds.writeByte(mau.strRequire);
                ds.writeShort(mau.iconID);
                ds.writeShort(mau.part);
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            VXLTienIch.luuTep("cache/dataItem", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataMap() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            int len = VXLDuLieuBanDo.entrys.size();
            ds.writeByte(len);
            for (int i = 0; i < len; ++i) {
                VXLDuLieuBanDo.MapDataEntry map = VXLDuLieuBanDo.entrys.get(i);
                ds.writeByte(map.mapID);
                ds.writeShort(map.duLieu.length);
                ds.write(map.duLieu);
                ds.writeUTF(map.mapName);
                ds.writeShort(map.iconID);
                ds.writeByte(map.bgID);
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            VXLTienIch.luuTep("cache/dataMap", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataCaptionLevel() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            ds.writeByte(VXLTieuDeCap.levels.size());
            for (int ma = 0; ma < VXLTieuDeCap.levels.size(); ma++) {
                VXLTieuDeCap cap = VXLTieuDeCap.levels.get(ma);
                if (cap == null) {
                    throw new IOException("Thieu caption_levels id=" + ma);
                }
                ds.writeUTF(cap.ten);
                ds.writeInt(cap.kinhNghiem);
                ds.writeShort(cap.icon);
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            VXLTienIch.luuTep("cache/dataLevel", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataPart() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            ds.writeShort(parts.size());
            for (VXLBoPhan part : parts.values()) {
                ds.writeByte(part.loai);
                for (VXLAnhBoPhan p : part.pi) {
                    ds.writeShort(p.ma);
                    ds.writeByte(p.dx);
                    ds.writeByte(p.dy);
                }
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            VXLTienIch.luuTep("cache/dataPart", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void setDataImage() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            int soAnh = VXLAnhNho.smallImg.size();
            ds.writeShort(soAnh);
            for (int ma = 0; ma < soAnh; ma++) {
                VXLAnhNho small = VXLAnhNho.smallImg.get(ma);
                if (small == null) {
                    throw new IOException("Thieu sprite_images id=" + ma);
                }
                ds.writeByte(small.maAnh);
                ds.writeShort(small.x);
                ds.writeShort(small.y);
                ds.writeShort(small.w);
                ds.writeShort(small.h);
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            VXLTienIch.luuTep("cache/dataImage", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized int nextClientId() {
        return ++ma;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void onClientConnected(VXLPhien cl) {
        if (cl == null) {
            return;
        }
        synchronized (VXLQuanLyMayChu.class) {
            if (clients == null || clients.contains(cl)) {
                return;
            }
            clients.add(cl);
            numClients = clients.size();
            VXLQuanLyMayChu.logConnection("Ket noi " + cl.moTa() + " | online=" + numClients);
        }
    }

    public static void onClientLoggedIn(VXLPhien cl) {
        VXLQuanLyMayChu.logConnection("Dang nhap " + cl.moTa() + " | online=" + numClients);
    }

    public static void batDau() {
        System.out.println(GAME_NAME + " — Netty port=" + cong);
        try {
            clients = new ArrayList();
            ma = 0;
            numClients = 0;
            batDau = true;
            VXLDatLaiPhienQuanHangNgay.khoiDong();
            nettyServer = new VXLMayChuNetty();
            nettyServer.batDau(mayChu, cong);
            VXLQuanLyMayChu.log(GAME_NAME + " start OK!");
            if (nettyServer != null) {
                nettyServer.choDong();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            Logger.getLogger(VXLQuanLyMayChu.class.getName()).log(Level.SEVERE, "Server stopped unexpectedly", e);
            VXLQuanLyMayChu.dung();
        }
    }

    public static synchronized void dung() {
        if (!batDau && nettyServer == null) {
            return;
        }
        batDau = false;
        VXLQuanLyMayChu.close();
    }

    protected static void close() {
        try {
            if (nettyServer != null) {
                nettyServer.dung();
                nettyServer = null;
            }
            synchronized (VXLQuanLyMayChu.class) {
                if (clients != null) {
                    ArrayList<VXLPhien> connectedClients = new ArrayList<>(clients);
                    for (VXLPhien c : connectedClients) {
                        c.close();
                    }
                    clients.clear();
                    numClients = 0;
                    clients = null;
                }
            }
            VXLDatLaiPhienQuanHangNgay.dung();
            VXLCoSoDuLieu.close();
            System.out.println("VXLMayChu stopped");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void log(String s) {
        if (goLoi) {
            System.out.println(s);
        }
    }

    public static void logConnection(String s) {
        System.out.println("[Client] " + s);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void disconnect(VXLPhien cl) {
        if (cl == null) {
            return;
        }
        synchronized (VXLQuanLyMayChu.class) {
            if (clients == null || !clients.remove(cl)) {
                return;
            }
            numClients = clients.size();
            VXLQuanLyMayChu.logConnection("Ngat ket noi " + cl.moTa() + " | online=" + numClients);
        }
    }

    static {
        dailyGold = 5000;
        dailyGem = 10;
        clanCreateGold = 500000;
        wheelGemCost = 5;
        eventIntervalMinutes = 90;
        eventDurationMinutes = 30;
        worldTreasureIntervalMinutes = 8;
        worldBossHp = 50000;
        maxElementFight = 8;
        maxPlayers = 8;
        nPlayersInitRoom = 2;
        initMap = 1;
        initMapBoss = 30;
        nBig = new int[4];
        dataSize = new String[4];
        iOptionTemplates = new HashMap();
        itemTemplates = new HashMap();
        parts = new HashMap();
        SHOP_EQUIP = new VXLCuaHang();
        SHOP_ITEM = new VXLCuaHang();
    }
}
