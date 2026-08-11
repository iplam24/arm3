package com.vxl.phong;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.chien.VXLQuanLyChien;
import com.vxl.bando.VXLDuLieuBanDo;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLTinNhan;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.nio.file.Files;
import javax.imageio.ImageIO;

public class VXLQuanLyPhong {
    private static final int BOARD_PER_ROOM = 10;
    private static final ConcurrentMap<Integer, VXLChoDau> playerBoards = new ConcurrentHashMap<>();
    private static byte[] fightMaps = new byte[]{1};
    private static byte[] normalMaps = new byte[]{1};
    private static byte[] bossMaps = new byte[]{1};
    public static volatile VXLPhong[] phongs = new VXLPhong[0];

    public static void khoiTao() {
        int configuredMaxPlayers = VXLQuanLyMayChu.maxPlayers;
        if (configuredMaxPlayers <= 0) {
            configuredMaxPlayers = 8;
        }
        if (configuredMaxPlayers > Byte.MAX_VALUE) {
            configuredMaxPlayers = Byte.MAX_VALUE;
        }
        byte maxPlayers = (byte)configuredMaxPlayers;
        playerBoards.clear();
        fightMaps = taiBanDoDau();
        normalMaps = locBanDoTheoLoai(false);
        bossMaps = locBanDoTheoLoai(true);
        byte[] cacLoaiPhong = VXLLoaiPhong.layThuTu();
        phongs = new VXLPhong[cacLoaiPhong.length];
        for (int i = 0; i < cacLoaiPhong.length; i++) {
            byte loaiPhong = cacLoaiPhong[i];
            byte[] cacBanDo = chonDanhSachBanDo(loaiPhong);
            phongs[i] = new VXLPhong(i, BOARD_PER_ROOM, loaiPhong, maxPlayers, cacBanDo[0]);
            for (int j = 0; j < phongs[i].banChos.length; j++) {
                phongs[i].banChos[j].maBanDo = cacBanDo[j % cacBanDo.length];
            }
        }
    }

    public static void gan(VXLNguoiChoi nguoiChoi, VXLChoDau banCho) {
        if (nguoiChoi == null || banCho == null) {
            return;
        }
        playerBoards.put(nguoiChoi.ma, banCho);
    }

    public static void boGan(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
            playerBoards.remove(nguoiChoi.ma);
        }
    }

    static void boGan(VXLNguoiChoi nguoiChoi, VXLChoDau banCho) {
        if (nguoiChoi != null && banCho != null) {
            playerBoards.remove(nguoiChoi.ma, banCho);
        }
    }

    public static VXLChoDau layBanCho(VXLNguoiChoi nguoiChoi) {
        return nguoiChoi == null ? null : playerBoards.get(nguoiChoi.ma);
    }

    public static void roiBanCho(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        VXLChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.roi(nguoiChoi);
        }
    }

    public static void yeuCauDanhSachPhong(VXLNguoiChoi nguoiChoi) throws IOException {
        if (nguoiChoi == null || nguoiChoi.dichVu == null) {
            return;
        }
        VXLPhong[] danhSachPhong = phongs;
        VXLTinNhan ms = new VXLTinNhan(6);
        DataOutputStream ds = ms.boGhi();
        for (VXLPhong phong : danhSachPhong) {
            ds.writeByte(phong.ma);
            ds.writeByte(phong.layDoDay());
            ds.writeByte(0);
            ds.writeByte(phong.loai);
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(ms);
    }

    public static void guiPhongTisEmpty(VXLNguoiChoi nguoiChoi) throws IOException {
        if (nguoiChoi == null || nguoiChoi.dichVu == null) {
            return;
        }
        VXLPhong[] danhSachPhong = phongs;
        VXLTinNhan ms = new VXLTinNhan(-28);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        for (VXLPhong phong : danhSachPhong) {
            ds.writeByte(-1);
            ds.writeUTF(phong.ten);
            for (VXLChoDau banCho : phong.banChos) {
                int soNguoiChoi = banCho.laySoNguoiChoi();
                if (banCho.started || soNguoiChoi >= banCho.maxPlayers) {
                    continue;
                }
                ds.writeByte(phong.ma);
                ds.writeByte(banCho.ma);
                ds.writeByte(banCho.maBanDo);
                ds.writeByte(soNguoiChoi);
                ds.writeByte(banCho.maxPlayers);
                ds.writeInt(banCho.tien);
            }
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(ms);
    }

    public static void xuLyYeuCauPhongTrong(VXLNguoiChoi nguoiChoi, VXLTinNhan ms)
            throws IOException {
        if (nguoiChoi == null || ms == null || ms.layDuLieu().length == 0) {
            guiPhongTisEmpty(nguoiChoi);
            return;
        }
        DataInputStream ds = ms.boDoc();
        int loaiYeuCau = ds.readUnsignedByte();
        if (loaiYeuCau == 1 && ds.available() > 0) {
            vaoPhongTrongTheoLoai(nguoiChoi, ds.readByte());
            return;
        }
        if (loaiYeuCau == 2 && ds.available() > 0) {
            vaoPhongTheoMaTimKiem(nguoiChoi, ds.readUTF());
            return;
        }
        guiPhongTisEmpty(nguoiChoi);
    }

    private static void vaoPhongTrongTheoLoai(VXLNguoiChoi nguoiChoi, byte loaiPhong)
            throws IOException {
        VXLPhong phong = layPhongTheoLoai(loaiPhong);
        if (phong == null) {
            nguoiChoi.startOKDlg2("Lo\u1ea1i ph\u00f2ng kh\u00f4ng t\u1ed3n t\u1ea1i.");
            return;
        }
        for (VXLChoDau banCho : phong.banChos) {
            if (!banCho.started && banCho.laySoNguoiChoi() == 0) {
                banCho.vao(nguoiChoi, "");
                return;
            }
        }
        nguoiChoi.startOKDlg2("Kh\u00f4ng c\u00f2n khu v\u1ef1c tr\u1ed1ng trong " + phong.ten + ".");
    }

    private static void vaoPhongTheoMaTimKiem(VXLNguoiChoi nguoiChoi, String maTimKiem)
            throws IOException {
        try {
            int ma = Integer.parseInt(maTimKiem);
            int maPhong = ma / 1000;
            int maBan = ma % 1000;
            VXLPhong phong = maPhong >= 0 && maPhong < phongs.length ? phongs[maPhong] : null;
            if (phong != null && maBan >= 0 && maBan < phong.banChos.length) {
                phong.banChos[maBan].vao(nguoiChoi, "");
                return;
            }
        } catch (NumberFormatException ignored) {
        }
        nguoiChoi.startOKDlg2("Kh\u00f4ng t\u00ecm th\u1ea5y khu v\u1ef1c.");
    }

    public static void yeuCauDanhSachBan(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        if (nguoiChoi == null || nguoiChoi.dichVu == null || ms == null || ms.layDuLieu().length < 1) {
            return;
        }
        byte maPhong = ms.boDoc().readByte();
        VXLPhong phong = layPhong(maPhong);
        if (phong == null) {
            nguoiChoi.startOKDlg2("Phòng không tồn tại.");
            return;
        }
        VXLTinNhan out = new VXLTinNhan(7);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(phong.ma);
        for (VXLChoDau banCho : phong.banChos) {
            if (banCho.started || banCho.laySoNguoiChoi() >= banCho.maxPlayers) {
                continue;
            }
            ds.writeByte(banCho.ma);
            ds.writeByte(banCho.laySoNguoiChoi());
            ds.writeByte(banCho.maxPlayers);
            ds.writeBoolean(false);
            ds.writeInt(banCho.tien);
            ds.writeBoolean(true);
            ds.writeUTF(banCho.ten);
            ds.writeByte(0);
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(out);
    }

    public static void vaoBan(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        if (nguoiChoi == null || nguoiChoi.dichVu == null || ms == null || ms.layDuLieu().length < 4) {
            return;
        }
        byte maPhong = ms.boDoc().readByte();
        byte maBan = ms.boDoc().readByte();
        String matKhau = ms.docUTF(32, "mật khẩu phòng");
        VXLPhong phong = layPhong(maPhong);
        if (phong == null || Byte.toUnsignedInt(maBan) >= phong.banChos.length) {
            nguoiChoi.startOKDlg2("Khu vực không tồn tại.");
            return;
        }
        phong.banChos[Byte.toUnsignedInt(maBan)].vao(nguoiChoi, matKhau);
    }

    public static void sanSang(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        if (nguoiChoi == null || ms == null || ms.layDuLieu().length < 1) {
            return;
        }
        VXLChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.datSanSang(nguoiChoi, ms.boDoc().readBoolean());
        }
    }

    public static void batDau(VXLNguoiChoi nguoiChoi) throws IOException {
        VXLChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.batDau(nguoiChoi);
        }
    }

    public static void chonBanDo(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        if (nguoiChoi == null || ms == null || ms.layDuLieu().length < 1) {
            return;
        }
        VXLChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.datBanDo(nguoiChoi, chuanHoaBanDo(ms.boDoc().readByte()));
        }
    }

    public static void dauDiChuyen(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.diChuyen(nguoiChoi, ms);
        }
    }

    public static void dauCapNhatXY(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.capNhatXY(nguoiChoi, ms);
        }
    }

    public static void dauBan(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.ban(nguoiChoi, ms);
        }
    }

    public static void dauDoiSung(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.doiSung(nguoiChoi, ms);
        }
    }

    public static void dauKiemTraVaCham(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.kiemTraVaCham(nguoiChoi, ms);
        }
    }

    public static void dauFocusSkill(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.focusSkill(nguoiChoi, ms);
        }
    }

    public static void boLuot(VXLNguoiChoi nguoiChoi) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.boLuot(nguoiChoi);
        }
    }

    public static boolean dungVatPhamTrongTran(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight == null) {
            return false;
        }
        fight.dungVatPham(nguoiChoi, ms);
        return true;
    }

    private static VXLQuanLyChien layTranDau(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return null;
        }
        VXLChoDau banCho = layBanCho(nguoiChoi);
        return banCho != null ? banCho.layTranDau() : null;
    }

    private static VXLPhong layPhong(byte maPhong) {
        VXLPhong[] danhSachPhong = phongs;
        int chiSo = Byte.toUnsignedInt(maPhong);
        if (chiSo >= danhSachPhong.length) {
            return null;
        }
        return danhSachPhong[chiSo];
    }

    private static VXLPhong layPhongTheoLoai(byte loaiPhong) {
        for (VXLPhong phong : phongs) {
            if (phong != null && phong.loai == loaiPhong) {
                return phong;
            }
        }
        return null;
    }

    private static byte[] chonDanhSachBanDo(byte loaiPhong) {
        if (VXLLoaiPhong.laBoss(loaiPhong)) {
            return bossMaps.length > 0 ? bossMaps : fightMaps;
        }
        if (loaiPhong == VXLLoaiPhong.TU_DO) {
            return fightMaps;
        }
        return normalMaps.length > 0 ? normalMaps : fightMaps;
    }

    private static byte[] locBanDoTheoLoai(boolean boss) {
        ArrayList<Byte> ketQua = new ArrayList<>();
        if (VXLDuLieuBanDo.entrys != null) {
            for (VXLDuLieuBanDo.MapDataEntry muc : VXLDuLieuBanDo.entrys) {
                if (muc == null || !chuaBanDo(fightMaps, muc.mapID)) {
                    continue;
                }
                boolean laBoss = muc.mapName != null
                        && muc.mapName.trim().toLowerCase().startsWith("boss");
                if (laBoss == boss) {
                    ketQua.add(muc.mapID);
                }
            }
        }
        if (ketQua.isEmpty()) {
            return fightMaps.clone();
        }
        byte[] mang = new byte[ketQua.size()];
        for (int i = 0; i < ketQua.size(); i++) {
            mang[i] = ketQua.get(i);
        }
        return mang;
    }

    private static boolean chuaBanDo(byte[] cacBanDo, byte maBanDo) {
        for (byte banDo : cacBanDo) {
            if (banDo == maBanDo) {
                return true;
            }
        }
        return false;
    }

    private static byte[] taiBanDoDau() {
        ArrayList<Byte> maps = new ArrayList<>();
        Map<Integer, Boolean> taiNguyenDaKiemTra = new HashMap<>();
        if (VXLDuLieuBanDo.entrys != null) {
            for (VXLDuLieuBanDo.MapDataEntry muc : VXLDuLieuBanDo.entrys) {
                if (banDoHopLe(muc, taiNguyenDaKiemTra)) {
                    maps.add(muc.mapID);
                } else {
                    System.err.println("Bo qua ban do loi hoac thieu tai nguyen: " + Byte.toUnsignedInt(muc.mapID));
                }
            }
        }
        if (maps.isEmpty()) {
            taiBanDoTuTep(maps);
        }
        if (maps.isEmpty()) {
            System.err.println("Khong co ban do dau hop le, dung ban do mac dinh id=1.");
            maps.add((byte)1);
        }
        byte[] ketQua = new byte[maps.size()];
        for (int i = 0; i < maps.size(); i++) {
            ketQua[i] = maps.get(i);
        }
        return ketQua;
    }

    private static void taiBanDoTuTep(ArrayList<Byte> maps) {
        File thuMuc = new File("res/map");
        File[] tepBanDo = thuMuc.listFiles(File::isFile);
        if (tepBanDo == null) {
            return;
        }
        Arrays.sort(tepBanDo, Comparator.comparing(File::getName));
        ArrayList<VXLDuLieuBanDo.MapDataEntry> entries = VXLDuLieuBanDo.entrys;
        if (entries == null) {
            entries = new ArrayList<>();
            VXLDuLieuBanDo.entrys = entries;
        }
        Map<Integer, Boolean> taiNguyenDaKiemTra = new HashMap<>();
        for (File tep : tepBanDo) {
            try {
                int maBanDo = Integer.parseInt(tep.getName());
                if (maBanDo < 0 || maBanDo > 0xFF) {
                    continue;
                }
                byte[] duLieu = Files.readAllBytes(tep.toPath());
                VXLDuLieuBanDo.MapDataEntry muc = new VXLDuLieuBanDo.MapDataEntry(
                        duLieu, (byte)maBanDo, "Map " + maBanDo, (short)0, (byte)0);
                if (banDoHopLe(muc, taiNguyenDaKiemTra)) {
                    entries.add(muc);
                    maps.add((byte)maBanDo);
                }
            }
            catch (IOException | NumberFormatException ex) {
                System.err.println("Bo qua tep ban do loi: " + tep.getName());
            }
        }
    }

    private static boolean banDoHopLe(VXLDuLieuBanDo.MapDataEntry muc, Map<Integer, Boolean> taiNguyenDaKiemTra) {
        if (muc == null) {
            return false;
        }
        byte[] duLieu = muc.duLieu;
        if (duLieu == null || duLieu.length < 5) {
            return false;
        }
        try (DataInputStream ds = new DataInputStream(new ByteArrayInputStream(duLieu))) {
            if (ds.readUnsignedShort() == 0 || ds.readUnsignedShort() == 0) {
                return false;
            }
            int soMienDat = ds.readUnsignedByte();
            if (soMienDat == 0 || duLieu.length < 5L + (long)soMienDat * 5L) {
                return false;
            }
            for (int i = 0; i < soMienDat; i++) {
                int maNguyenLieu = ds.readUnsignedByte();
                ds.readShort();
                ds.readShort();
                if (!nguyenLieuHopLe(maNguyenLieu, taiNguyenDaKiemTra)) {
                    return false;
                }
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private static boolean nguyenLieuHopLe(int ma, Map<Integer, Boolean> taiNguyenDaKiemTra) {
        Boolean ketQuaDaCo = taiNguyenDaKiemTra.get(ma);
        if (ketQuaDaCo != null) {
            return ketQuaDaCo;
        }
        VXLDuLieuBanDo.loadMapBrick(ma);
        boolean hopLe = VXLDuLieuBanDo.existsMapBrick(ma);
        taiNguyenDaKiemTra.put(ma, hopLe);
        return hopLe;
    }

    private static byte chuanHoaBanDo(byte maBanDo) {
        if (fightMaps == null || fightMaps.length == 0) {
            return 1;
        }
        for (byte fightMap : fightMaps) {
            if (fightMap == maBanDo) {
                return maBanDo;
            }
        }
        if (maBanDo == 100) {
            return fightMaps[(int)(System.currentTimeMillis() % fightMaps.length)];
        }
        return fightMaps[0];
    }
}
