package com.vxl.phong;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.chien.VXLQuanLyChien;
import com.vxl.bando.VXLDuLieuBanDo;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLTinNhan;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VXLQuanLyPhong {
    private static final int ROOM_COUNT = 4;
    private static final int BOARD_PER_ROOM = 10;
    private static final Map<Integer, VXLChoDau> playerBoards = new HashMap<>();
    private static byte[] fightMaps = new byte[]{1};
    public static VXLPhong[] phongs = new VXLPhong[0];

    public static void khoiTao() {
        byte maxPlayers = VXLQuanLyMayChu.maxPlayers > 0 ? VXLQuanLyMayChu.maxPlayers : 8;
        fightMaps = taiBanDoDau();
        phongs = new VXLPhong[ROOM_COUNT];
        for (int i = 0; i < ROOM_COUNT; i++) {
            phongs[i] = new VXLPhong(i, BOARD_PER_ROOM, (byte)0, maxPlayers, fightMaps[0]);
            for (int j = 0; j < phongs[i].banChos.length; j++) {
                phongs[i].banChos[j].maBanDo = fightMaps[(i * BOARD_PER_ROOM + j) % fightMaps.length];
            }
        }
    }

    public static void gan(VXLNguoiChoi nguoiChoi, VXLChoDau banCho) {
        synchronized (playerBoards) {
            playerBoards.put(nguoiChoi.ma, banCho);
        }
    }

    public static void boGan(VXLNguoiChoi nguoiChoi) {
        synchronized (playerBoards) {
            playerBoards.remove(nguoiChoi.ma);
        }
    }

    public static VXLChoDau layBanCho(VXLNguoiChoi nguoiChoi) {
        synchronized (playerBoards) {
            return playerBoards.get(nguoiChoi.ma);
        }
    }

    public static void roiBanCho(VXLNguoiChoi nguoiChoi) {
        VXLChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.roi(nguoiChoi);
        }
    }

    public static void yeuCauDanhSachPhong(VXLNguoiChoi nguoiChoi) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(6);
        DataOutputStream ds = ms.boGhi();
        for (VXLPhong phong : phongs) {
            ds.writeByte(phong.ma);
            ds.writeByte(phong.layDoDay());
            ds.writeByte(0);
            ds.writeByte(phong.loai);
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(ms);
        nguoiChoi.dichVu.guiTieuDePhongDau();
        guiPhongTisEmpty(nguoiChoi);
    }

    public static void guiPhongTisEmpty(VXLNguoiChoi nguoiChoi) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-28);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(-1);
        ds.writeUTF("Đấu thường");
        for (VXLPhong phong : phongs) {
            for (VXLChoDau banCho : phong.banChos) {
                if (banCho.started || banCho.laySoNguoiChoi() >= banCho.maxPlayers) {
                    continue;
                }
                ds.writeByte(phong.ma);
                ds.writeByte(banCho.ma);
                ds.writeByte(banCho.maBanDo);
                ds.writeByte(banCho.laySoNguoiChoi());
                ds.writeByte(banCho.maxPlayers);
                ds.writeInt(banCho.tien);
            }
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(ms);
    }

    public static void yeuCauDanhSachBan(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
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
        byte maPhong = ms.boDoc().readByte();
        byte maBan = ms.boDoc().readByte();
        String matKhau = ms.docUTF(32, "mật khẩu phòng");
        VXLPhong phong = layPhong(maPhong);
        if (phong == null || maBan < 0 || maBan >= phong.banChos.length) {
            nguoiChoi.startOKDlg2("Khu vực không tồn tại.");
            return;
        }
        phong.banChos[maBan].vao(nguoiChoi, matKhau);
    }

    public static void sanSang(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
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

    public static void dauKiemTraVaCham(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.kiemTraVaCham(nguoiChoi, ms);
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
        VXLChoDau banCho = layBanCho(nguoiChoi);
        return banCho != null ? banCho.layTranDau() : null;
    }

    private static VXLPhong layPhong(byte maPhong) {
        if (maPhong < 0 || maPhong >= phongs.length) {
            return null;
        }
        return phongs[maPhong];
    }

    private static byte[] taiBanDoDau() {
        ArrayList<Byte> maps = new ArrayList<>();
        if (VXLDuLieuBanDo.entrys != null) {
            for (VXLDuLieuBanDo.MapDataEntry muc : VXLDuLieuBanDo.entrys) {
                maps.add(muc.mapID);
            }
        }
        if (maps.isEmpty()) {
            maps.add((byte)1);
        }
        byte[] ketQua = new byte[maps.size()];
        for (int i = 0; i < maps.size(); i++) {
            ketQua[i] = maps.get(i);
        }
        return ketQua;
    }

    private static byte chuanHoaBanDo(byte maBanDo) {
        if (fightMaps.length == 0) {
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
