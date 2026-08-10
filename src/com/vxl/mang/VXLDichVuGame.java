package com.vxl.mang;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLVatPham;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.chien.VXLChienBinh;
import com.vxl.chien.VXLCauHinhVatPhamChienDau;
import com.vxl.chien.VXLKetQuaDan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.IVXLDichVuGame;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mang.VXLPhien;
import com.vxl.nhapvai.VXLBanDoRPG;
import com.vxl.nhapvai.VXLNhanVatPhu;
import com.vxl.cuahang.VXLTrang;
import com.vxl.cuahang.VXLCuaHang;
import com.vxl.tienich.VXLTienIch;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLDichVuGame
implements IVXLDichVuGame {
    private static final short DIEM_DI_CHUYEN_TOI_DA = 180;
    private VXLPhien khach;
    private VXLNguoiChoi nguoiChoi;

    public VXLDichVuGame(VXLPhien khach) {
        this.khach = khach;
    }

    public void datNguoiChoi(VXLNguoiChoi nguoiChoi) {
        this.nguoiChoi = nguoiChoi;
    }

    public void ping() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-102);
        this.guiTin(ms);
    }

    public void hienTaiXuong() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-60);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(VXLQuanLyMayChu.vBig);
        ds.writeUTF(VXLQuanLyMayChu.dataSize[this.khach.mucPhong - 1]);
        ds.flush();
        this.guiTin(ms);
    }

    public void taiXuong() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-60);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1);
        ds.writeByte(VXLQuanLyMayChu.vBig);
        ds.writeShort(VXLQuanLyMayChu.nBig[this.khach.mucPhong - 1]);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiPhienBan() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-30);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(VXLQuanLyMayChu.vData);
        ds.writeByte(VXLQuanLyMayChu.vItem);
        ds.writeByte(VXLQuanLyMayChu.vMap);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiVatPham() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-32);
        DataOutputStream ds = ms.boGhi();
        ds.write(VXLTienIch.layTep("cache/dataItem"));
        ds.flush();
        this.guiTin(ms);
    }

    public void guiBanDo() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-38);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(VXLQuanLyMayChu.vMap);
        byte[] map = VXLTienIch.layTep("cache/dataMap");
        ds.writeInt(map.length);
        ds.write(map);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDuLieu() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-31);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(VXLQuanLyMayChu.vData);
        byte[] anh = VXLTienIch.layTep("cache/dataImage");
        ds.writeInt(anh.length);
        ds.write(anh);
        byte[] part = VXLTienIch.layTep("cache/dataPart");
        ds.writeInt(part.length);
        ds.write(part);
        byte[] cap = VXLTienIch.layTep("cache/dataLevel");
        ds.writeInt(cap.length);
        ds.write(cap);
        ds.flush();
        this.guiTin(ms);
    }

    public void choDangNhap(short second) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(122);
        DataOutputStream ds = ms.boGhi();
        ds.writeShort(second);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiCapNhatCup(byte loai, int cup) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-24);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(loai);
        ds.writeInt(cup);
        ds.flush();
        this.guiTin(ms);
    }

    public void moHopThoaiOK(String noiDung) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(45);
        DataOutputStream ds = ms.boGhi();
        ds.writeUTF(noiDung);
        ds.flush();
        this.guiTin(ms);
    }

    public void baoLoiTien(String noiDung) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(10);
        DataOutputStream ds = ms.boGhi();
        ds.writeUTF(noiDung);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiThongTin() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(3);
        DataOutputStream ds = ms.boGhi();
        ds.writeInt(this.nguoiChoi.ma);
        ds.writeUTF(this.nguoiChoi.ten);
        ds.writeInt(this.nguoiChoi.vang);
        ds.writeInt(this.nguoiChoi.ngoc);
        ds.writeInt(this.nguoiChoi.kinhNghiem);
        ds.writeInt(this.nguoiChoi.cup);
        ds.writeShort(this.nguoiChoi.clan);
        ds.writeInt(0);
        ds.writeInt(0);
        ds.writeInt(0);
        ds.writeByte(this.nguoiChoi.trainingSuccess);
        ds.writeByte(this.nguoiChoi.busyHammer);
        ds.writeByte(this.nguoiChoi.nHammer);
        ds.flush();
        this.guiTin(ms);
    }

    public void vaoCho(VXLNguoiChoi nguoiChoi) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-98);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(nguoiChoi.chiSo);
        ds.writeInt(nguoiChoi.ma);
        ds.writeUTF(nguoiChoi.ten);
        ds.writeShort(nguoiChoi.clan);
        ds.writeInt(nguoiChoi.kinhNghiem);
        ds.writeShort(nguoiChoi.head);
        ds.writeShort(nguoiChoi.leg);
        ds.writeShort(nguoiChoi.body);
        ds.writeShort(nguoiChoi.hat);
        ds.writeShort(nguoiChoi.wing);
        ds.writeShort(nguoiChoi.wp);
        ds.writeByte(nguoiChoi.avenger);
        ds.writeByte(nguoiChoi.isReady ? 1 : 0);
        ds.writeByte(nguoiChoi.zoneId);
        ds.writeInt(nguoiChoi.clan);
        if (nguoiChoi.clan != -1) {
            ds.writeShort(0);
        }
        ds.writeByte(nguoiChoi.pointSeat);
        ds.writeShort(nguoiChoi.x);
        ds.writeShort(nguoiChoi.y);
        ds.flush();
        this.guiTin(ms);
    }

    public void yeuCauIcon(VXLTinNhan ms) throws IOException {
        short ma = ms.boDoc().readShort();
        VXLTinNhan mss = new VXLTinNhan(-41);
        DataOutputStream ds = mss.boGhi();
        ds.writeShort(ma);
        byte[] ab = VXLTienIch.layTep("res/icon/item/" + this.khach.mucPhong + "/Small" + ma + ".png");
        if (ab == null || ab.length == 0) {
            ab = VXLTienIch.layTep("res/icon/item/1/Small" + ma + ".png");
        }
        if (ab == null) {
            ab = new byte[0];
        }
        ds.writeInt(ab.length);
        ds.write(ab);
        ds.flush();
        this.guiTin(mss);
    }

    public void yeuCauNguyenLieu(short ma) throws IOException {
        byte[] ab;
        VXLTinNhan mss = new VXLTinNhan(126);
        DataOutputStream ds = mss.boGhi();
        ab = VXLTienIch.layTep("res/icon/map/" + ma + ".png");
        if (ab == null) {
            ab = new byte[0];
        }
        ds.writeShort(ma);
        ds.writeShort(ab.length);
        ds.write(ab);
        ds.flush();
        this.guiTin(mss);
    }

    public void xemCuaHang(VXLCuaHang store) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(103);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(store.typeShop);
        int tabNumber = store.shopTabName.size();
        ds.writeByte(tabNumber);
        for (int i = 0; i < tabNumber; ++i) {
            ArrayList<VXLTrang> pages = store.tabs.get(i);
            ds.writeByte(i);
            ds.writeUTF(store.shopTabName.get(i));
            ds.writeByte(pages.size());
            ArrayList<VXLMauVatPham> vatPhams = pages.get((int)0).vatPhams;
            int numberItem = vatPhams.size();
            ds.writeByte(numberItem);
            for (int a = 0; a < numberItem; ++a) {
                VXLMauVatPham vatPham = vatPhams.get(a);
                ds.writeShort(vatPham.ma);
                ds.writeInt(vatPham.buyGold);
                ds.writeInt(vatPham.buyGem);
                int numberOption = vatPham.thuocTinhs.size();
                ds.writeByte(numberOption);
                for (int b = 0; b < numberOption; ++b) {
                    VXLThuocTinhVatPham option = (VXLThuocTinhVatPham)vatPham.thuocTinhs.get(b);
                    ds.writeByte(option.optionTemplate.ma);
                    ds.writeShort(option.thamSo);
                }
            }
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDoTrenNguoi() throws IOException {
        VXLVatPham[] vatPhams = this.nguoiChoi.itemBody;
        VXLTinNhan ms = new VXLTinNhan(-34);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(vatPhams.length);
        for (VXLVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                ds.writeShort(vatPham.ma);
                ds.writeByte(vatPham.soLuong);
                ds.writeByte(vatPham.HP);
                ds.writeUTF("");
                ds.writeUTF("");
                int len = vatPham.itemOptions.size();
                ds.writeByte(len);
                for (int i = 0; i < len; ++i) {
                    VXLThuocTinhVatPham option = (VXLThuocTinhVatPham)vatPham.itemOptions.get(i);
                    ds.writeByte(option.optionTemplate.ma);
                    if (option.optionTemplate.ma == 15) {
                        int thamSo = (int)(((long)option.thamSo - System.currentTimeMillis() / 1000L) / 60L / 60L);
                        ds.writeShort(thamSo);
                        continue;
                    }
                    ds.writeShort(option.thamSo);
                }
                continue;
            }
            ds.writeShort(-1);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiRuongDo() throws IOException {
        VXLVatPham[] vatPhams = this.nguoiChoi.itemBox;
        VXLTinNhan ms = new VXLTinNhan(-36);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(vatPhams.length);
        for (VXLVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                ds.writeShort(vatPham.ma);
                ds.writeByte(vatPham.soLuong);
                ds.writeByte(vatPham.HP);
                ds.writeUTF("");
                ds.writeUTF("");
                int len = vatPham.itemOptions.size();
                ds.writeByte(len);
                for (int i = 0; i < len; ++i) {
                    VXLThuocTinhVatPham option = (VXLThuocTinhVatPham)vatPham.itemOptions.get(i);
                    ds.writeByte(option.optionTemplate.ma);
                    if (option.optionTemplate.ma == 15) {
                        int thamSo = (int)(((long)option.thamSo - System.currentTimeMillis() / 1000L) / 60L / 60L);
                        ds.writeShort(thamSo);
                        continue;
                    }
                    ds.writeShort(option.thamSo);
                }
                continue;
            }
            ds.writeShort(-1);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiTuiDo() throws IOException {
        VXLVatPham[] vatPhams = this.nguoiChoi.itemBag;
        VXLTinNhan ms = new VXLTinNhan(-35);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(vatPhams.length);
        for (VXLVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                ds.writeShort(vatPham.ma);
                ds.writeByte(vatPham.soLuong);
                ds.writeByte(vatPham.HP);
                ds.writeUTF("");
                ds.writeUTF("");
                int len = vatPham.itemOptions.size();
                ds.writeByte(len);
                for (int i = 0; i < len; ++i) {
                    VXLThuocTinhVatPham option = (VXLThuocTinhVatPham)vatPham.itemOptions.get(i);
                    ds.writeByte(option.optionTemplate.ma);
                    if (option.optionTemplate.ma == 15) {
                        int thamSo = (int)(((long)option.thamSo - System.currentTimeMillis() / 1000L) / 60L / 60L);
                        ds.writeShort(thamSo);
                        continue;
                    }
                    ds.writeShort(option.thamSo);
                }
                continue;
            }
            ds.writeShort(-1);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiBalo() throws IOException {
        int[] vatPhams = this.nguoiChoi.itemBalo;
        VXLTinNhan ms = new VXLTinNhan(-42);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(vatPhams.length);
        for (int chiSo : vatPhams) {
            VXLVatPham vatPham = null;
            if (chiSo != -1) {
                vatPham = this.nguoiChoi.itemBag[chiSo];
            }
            if (vatPham != null) {
                ds.writeShort(vatPham.ma);
                ds.writeByte(vatPham.soLuong);
                ds.writeByte(vatPham.HP);
                ds.writeUTF("");
                ds.writeUTF("");
                int len = vatPham.itemOptions.size();
                ds.writeByte(len);
                for (int i = 0; i < len; ++i) {
                    VXLThuocTinhVatPham option = (VXLThuocTinhVatPham)vatPham.itemOptions.get(i);
                    ds.writeByte(option.optionTemplate.ma);
                    if (option.optionTemplate.ma == 15) {
                        int thamSo = (int)(((long)option.thamSo - System.currentTimeMillis() / 1000L) / 60L / 60L);
                        ds.writeShort(thamSo);
                        continue;
                    }
                    ds.writeShort(option.thamSo);
                }
                ds.writeByte(vatPham.chiSo);
                continue;
            }
            ds.writeShort(-1);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void capNhatTuiDo(int chiSo, int soLuong) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-35);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(2);
        ds.writeByte(chiSo);
        ds.writeByte(soLuong);
        ds.flush();
        this.guiTin(ms);
    }

    public void capNhatRuongDo(int chiSo, int soLuong) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-36);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(2);
        ds.writeByte(chiSo);
        ds.writeByte(soLuong);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiTep(String tenTep, byte[] duLieu) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-60);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(2);
        ds.writeUTF(tenTep);
        ds.writeInt(duLieu.length);
        ds.write(duLieu);
        ds.flush();
        this.guiTin(ms);
    }

    public void doiTrangBi() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-90);
        DataOutputStream ds = ms.boGhi();
        ds.writeInt(this.nguoiChoi.ma);
        ds.writeShort(this.nguoiChoi.head);
        ds.writeShort(this.nguoiChoi.leg);
        ds.writeShort(this.nguoiChoi.body);
        ds.writeShort(this.nguoiChoi.hat);
        ds.writeShort(this.nguoiChoi.wing);
        ds.writeShort(this.nguoiChoi.wp);
        ds.writeByte(this.nguoiChoi.avenger);
        ds.flush();
        this.guiTin(ms);
    }

    public void roi(int chiSo) {
        try {
            VXLTinNhan ms = new VXLTinNhan(-98);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(1);
            ds.writeByte(chiSo);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiNhanVatPhu() {
        try {
            VXLTinNhan ms = new VXLTinNhan(-98);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(10);
            ds.writeByte(VXLBanDoRPG.npcs.size());
            for (VXLNhanVatPhu npc : VXLBanDoRPG.npcs) {
                ds.writeByte(npc.trangThai);
                ds.writeShort(npc.x);
                ds.writeShort(npc.y);
                ds.writeByte(npc.templateId);
                ds.writeShort(npc.anhDaiDien);
                ds.writeShort(npc.head);
                ds.writeShort(npc.body);
                ds.writeShort(npc.leg);
            }
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capNhatKDVaKDA() {
        try {
            VXLTinNhan ms = new VXLTinNhan(-59);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(0);
            ds.writeUTF(String.format("%.1f", Float.valueOf(this.nguoiChoi.layKD())));
            ds.writeUTF(String.format("%.1f", Float.valueOf(this.nguoiChoi.layKDA())));
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capNhatAvenger() {
        try {
            VXLTinNhan ms = new VXLTinNhan(-59);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(1);
            ds.writeByte(this.nguoiChoi.powerAvenger);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taoNguoiDungAo(String user) {
        try {
            VXLTinNhan ms = new VXLTinNhan(-58);
            DataOutputStream ds = ms.boGhi();
            ds.writeUTF(user);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taoNhanVat() {
        this.guiTin(new VXLTinNhan(-99));
    }

    public void moDanhSach(String tieuDe, Vector v) {
        try {
            VXLTinNhan ms = new VXLTinNhan(-47);
            DataOutputStream ds = ms.boGhi();
            ds.writeUTF(tieuDe);
            int kichThuoc = v.size();
            ds.writeByte(kichThuoc);
            for (int i = 0; i < kichThuoc; ++i) {
                ds.writeUTF((String)v.get(i));
            }
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capNhatSucManh() {
        try {
            VXLTinNhan ms = new VXLTinNhan(-59);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(2);
            ds.writeByte(this.nguoiChoi.power);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capNhat() {
        try {
            VXLTinNhan ms = new VXLTinNhan(105);
            DataOutputStream ds = ms.boGhi();
            ds.writeInt(this.nguoiChoi.vang);
            ds.writeInt(this.nguoiChoi.ngoc);
            ds.writeByte(this.nguoiChoi.busyHammer);
            ds.writeByte(this.nguoiChoi.nHammer);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiTieuDePhongDau() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(88);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1);
        ds.writeUTF("Phòng đấu");
        ds.flush();
        this.guiTin(ms);
    }

    public void guiThongTinChoDau(byte maPhong, byte maBan, String boardName, byte cap) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(76);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maPhong);
        ds.writeByte(maBan);
        ds.writeUTF(boardName);
        ds.writeByte(cap);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiChonBanDoDau(byte maBanDo) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(75);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maBanDo);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiNguoiChoiVaoDau(VXLNguoiChoi joined, VXLNguoiChoi chuPhong, byte maPhong, byte maBan) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(8);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(joined.chiSo);
        ds.writeInt(joined.ma);
        ds.writeUTF(joined.ten);
        ds.writeShort(joined.clan);
        ds.writeInt(joined.kinhNghiem);
        ds.writeShort(joined.head);
        ds.writeShort(joined.leg);
        ds.writeShort(joined.body);
        ds.writeShort(joined.hat);
        ds.writeShort(joined.wing);
        ds.writeShort(joined.wp);
        ds.writeByte(joined.avenger);
        ds.writeInt(chuPhong != null ? chuPhong.ma : joined.ma);
        ds.writeByte(maPhong);
        ds.writeByte(maBan);
        ds.writeInt(joined.clan);
        if (joined.clan != -1) {
            ds.writeShort(0);
        }
        ds.writeByte(joined.pointSeat);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiRoiDau(int playerId, int ownerId) {
        try {
            VXLTinNhan ms = new VXLTinNhan(14);
            DataOutputStream ds = ms.boGhi();
            ds.writeInt(playerId);
            ds.writeInt(ownerId);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiSanSangDau(int playerId, boolean sanSang) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(16);
        DataOutputStream ds = ms.boGhi();
        ds.writeInt(playerId);
        ds.writeBoolean(sanSang);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiTienDau(int tien, byte cap) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(19);
        DataOutputStream ds = ms.boGhi();
        ds.writeShort(0);
        ds.writeInt(tien);
        ds.writeByte(cap);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiBatDauDau(byte maBanDo, VXLChienBinh[] chienBinhs, byte maNen) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(20);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maBanDo);
        ds.writeByte(25);
        for (int i = 0; i < 8; i++) {
            VXLChienBinh chienBinh = i < chienBinhs.length ? chienBinhs[i] : null;
            if (chienBinh == null) {
                ds.writeShort(-1);
                continue;
            }
            ds.writeShort(chienBinh.x);
            ds.writeShort(chienBinh.y);
            ds.writeShort(chienBinh.hp);
            ds.writeShort(chienBinh.mauToiDa);
        }
        ds.writeByte(maNen);
        ds.writeByte(this.demSungDau(chienBinhs));
        for (short maVuKhi : this.gomSungDau(chienBinhs)) {
            if (maVuKhi > 0) {
                ds.writeShort(maVuKhi);
            }
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDiChuyenDau(byte chiSo, short x, short y) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(21);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiKetQuaBanDau(byte whoShoot, VXLKetQuaDan ketQua, byte numShoot) throws IOException {
        this.guiKetQuaBan(22, ketQua.chiMang, whoShoot, ketQua.loaiDan, ketQua.batDauX,
                ketQua.batDauY, ketQua.goc, ketQua.lucTach, numShoot,
                ketQua.cacDuongX, ketQua.cacDuongY, ketQua.loaiSieuCao,
                ketQua.xSieuCao, ketQua.ySieuCao);
    }

    public void guiNoDau(byte chiSo, byte no) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(113);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeByte(Math.max(0, Math.min(100, Byte.toUnsignedInt(no))));
        ds.flush();
        this.guiTin(ms);
    }

    public void guiCapNhatMauDau(byte chiSo, int hp, byte phanTram, byte trangThaiChet) throws IOException {
        if (hp < 0) {
            hp = 0;
        }
        if (hp > 65535) {
            hp = 65535;
        }
        VXLTinNhan ms = new VXLTinNhan(51);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(hp);
        ds.writeByte(phanTram);
        ds.writeByte(trangThaiChet);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiLuotDauTiep(byte whoNext, short x, short y, VXLChienBinh[] chienBinhs, byte giay) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(24);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(whoNext);
        ds.writeShort(x);
        ds.writeShort(y);
        int alive = 0;
        for (VXLChienBinh chienBinh : chienBinhs) {
            if (chienBinh != null && !chienBinh.chet) {
                alive++;
            }
        }
        ds.writeByte(alive);
        for (VXLChienBinh chienBinh : chienBinhs) {
            if (chienBinh != null && !chienBinh.chet) {
                ds.writeByte(chienBinh.chiSo);
                ds.writeShort(100);
            }
        }
        ds.writeByte(giay);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiKetThucDau(byte pheThang, int kinhNghiem, int vang, int ngoc) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(50);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(pheThang);
        ds.writeShort(kinhNghiem);
        ds.writeInt(vang);
        ds.writeShort(ngoc);
        ds.writeByte(0);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiGioLuyenTap(byte gioX, byte gioY) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(25);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(gioX);
        ds.writeByte(gioY);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiThoatManHinhLuyenTap() {
        this.guiTin(new VXLTinNhan(83));
    }

    public void capNhatCup(byte loai, int cup) throws IOException {
        this.guiCapNhatCup(loai, cup);
    }

    private byte demSungDau(VXLChienBinh[] chienBinhs) {
        byte dem = 0;
        for (short maVuKhi : this.gomSungDau(chienBinhs)) {
            if (maVuKhi > 0) {
                dem++;
            }
        }
        return dem;
    }

    private short[] gomSungDau(VXLChienBinh[] chienBinhs) {
        short[] weapons = new short[chienBinhs.length];
        int kichThuoc = 0;
        for (VXLChienBinh chienBinh : chienBinhs) {
            if (chienBinh == null || chienBinh.maVuKhi <= 0) {
                continue;
            }
            boolean exists = false;
            for (int i = 0; i < kichThuoc; i++) {
                if (weapons[i] == chienBinh.maVuKhi) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                weapons[kichThuoc++] = chienBinh.maVuKhi;
            }
        }
        return weapons;
    }

    public void guiTin(VXLTinNhan ms) {
        this.khach.guiTin(ms);
    }

    public void guiDuLieuBanDo(int maBanDo) throws IOException {
        VXLTinNhan msg = new VXLTinNhan(-6);
        DataOutputStream ds = msg.boGhi();
        byte[] mapData = VXLTienIch.layTep("cache/dataMap");
        ds.writeByte(VXLQuanLyMayChu.vMap);
        ds.writeShort(mapData.length);
        ds.write(mapData);
        ds.flush();
        this.guiTin(msg);
    }

    public void guiChonBanDoLuyenTap(byte maBanDo) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(75);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maBanDo);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiThongTinLuyenTap() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(76);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(0);
        ds.writeUTF("Luyện tập");
        ds.writeByte(0);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiNguoiChoiLuyenTap(byte chiSo, int ma, String ten, short head, short leg, short body,
            short hat, short wing, short wp, byte avenger, int ownerId) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(8);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeInt(ma);
        ds.writeUTF(ten);
        ds.writeShort(-1);
        ds.writeInt(0);
        ds.writeShort(head);
        ds.writeShort(leg);
        ds.writeShort(body);
        ds.writeShort(hat);
        ds.writeShort(wing);
        ds.writeShort(wp);
        ds.writeByte(avenger);
        ds.writeInt(ownerId);
        ds.writeByte(0);
        ds.writeByte(0);
        ds.writeInt(-1);
        ds.writeByte(chiSo);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiBatDauLuyenTap(byte maBanDo, short maVuKhi, int hpNguoiChoi,
            short[] botX, short[] botY, int[] botHp, short[] botWeapons) throws IOException {
        ArrayList<Short> cacVuKhiCanTai = new ArrayList<>();
        cacVuKhiCanTai.add(maVuKhi);
        if (botWeapons != null) {
            for (short botWeapon : botWeapons) {
                if (!cacVuKhiCanTai.contains(botWeapon)) {
                    cacVuKhiCanTai.add(botWeapon);
                }
            }
        }
        VXLTinNhan ms = new VXLTinNhan(20);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maBanDo);
        ds.writeByte(25);
        for (int i = 0; i < 8; ++i) {
            if (i == 0) {
                ds.writeShort(220);
                ds.writeShort(300);
                ds.writeShort(gioiHanMauLuyenTap(hpNguoiChoi));
                ds.writeShort(50);
            } else if (i - 1 >= 0 && i - 1 < botX.length) {
                int botIndex = i - 1;
                ds.writeShort(botX[botIndex]);
                ds.writeShort(botY[botIndex]);
                int mauBot = botIndex < botHp.length ? botHp[botIndex] : 0;
                ds.writeShort(gioiHanMauLuyenTap(mauBot));
                ds.writeShort(100);
            } else {
                ds.writeShort(-1);
            }
        }
        ds.writeByte(0);
        ds.writeByte(cacVuKhiCanTai.size());
        for (short vuKhiCanTai : cacVuKhiCanTai) {
            ds.writeShort(vuKhiCanTai);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiHienManHinhGameLuyenTap() {
        this.guiTin(new VXLTinNhan(-67));
    }

    public void guiDoiSungLuyenTap(byte chiSo, short maVuKhi, short iconVuKhiCu,
            int thoiGianNapDan) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-45);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(maVuKhi);
        ds.writeShort(iconVuKhiCu);
        ds.writeShort(Math.max(50, Math.min(5000, thoiGianNapDan)));
        ds.flush();
        this.guiTin(ms);
    }

    public void yeuCauDanLuyenTap(VXLTinNhan ms) throws IOException {
        short ma = ms.boDoc().readShort();
        VXLTinNhan mss = new VXLTinNhan(-40);
        DataOutputStream ds = mss.boGhi();
        byte[] img = this.layAnhDanLuyenTap(ma);
        ds.writeShort(ma);
        ds.writeByte(this.layLoaiDanLuyenTap(ma));
        ds.writeShort(img.length);
        ds.write(img);
        ds.flush();
        this.guiTin(mss);
    }

    private byte layLoaiDanLuyenTap(short maVuKhi) {
        return VXLCauHinhVatPhamChienDau.layNhomSungClientTheoVuKhi(maVuKhi);
    }

    private byte[] layAnhDanLuyenTap(short maVuKhi) {
        short maAnhDan = VXLCauHinhVatPhamChienDau.layMaAnhDanTheoVuKhi(maVuKhi);
        int mucPhong = Math.max(1, Math.min(4, Byte.toUnsignedInt(this.khach.mucPhong)));
        String[] paths = new String[]{
            "res/icon/bullet/x" + mucPhong + "/" + maAnhDan + ".png",
            "res/icon/bullet/" + maAnhDan + ".png",
            "res/icon/bullet/x1/" + maAnhDan + ".png"
        };
        for (String duongDan : paths) {
            byte[] duLieu = this.docTepNeuCo(duongDan);
            if (duLieu != null && duLieu.length > 0) {
                return duLieu;
            }
        }
        return this.layPngDanLuyenTapDuPhong();
    }

    private byte[] layPngDanLuyenTapDuPhong() {
        return new byte[]{
            -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
            0, 0, 0, 8, 0, 0, 0, 8, 8, 6, 0, 0, 0, -60, 15, -66, -117,
            0, 0, 0, 25, 73, 68, 65, 84, 120, -100, 99, -4, -49, -64, -16,
            -97, -127, -127, -127, 33, 48, 50, 50, -4, 79, 6, 0, 39, 79,
            4, 2, 74, 56, 83, 55, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66,
            96, -126
        };
    }

    private byte[] docTepNeuCo(String duongDan) {
        File file = new File(duongDan);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            return bos.toByteArray();
        } catch (Exception ignored) {
            return null;
        }
    }

    public void guiKetQuaBanLuyenTap(byte whoShoot, byte loaiDan, short x, short y, short goc,
            byte luc, short[] duongX, short[] duongY, byte soPhat) throws IOException {
        this.guiKetQuaBanLuyenTap(whoShoot, loaiDan, x, y, goc, luc,
                new short[][]{duongX}, new short[][]{duongY}, soPhat);
    }

    public void guiKetQuaBanLuyenTap(byte whoShoot, byte loaiDan, short x, short y, short goc,
            byte lucTach, short[][] cacDuongX, short[][] cacDuongY, byte soPhat) throws IOException {
        this.guiKetQuaBan(84, (byte)0, whoShoot, loaiDan, x, y, goc, lucTach, soPhat,
                cacDuongX, cacDuongY, (byte)0, (short)-1, (short)-1);
    }

    public void guiKetQuaBanLuyenTapNangCao(byte chiMang, byte whoShoot, byte loaiDan,
            short x, short y, short goc, byte lucTach, short[][] cacDuongX,
            short[][] cacDuongY, byte soPhat, byte loaiSieuCao, short xSieuCao,
            short ySieuCao) throws IOException {
        this.guiKetQuaBan(84, chiMang, whoShoot, loaiDan, x, y, goc, lucTach, soPhat,
                cacDuongX, cacDuongY, loaiSieuCao, xSieuCao, ySieuCao);
    }

    private void guiKetQuaBan(int maLenh, byte chiMang, byte whoShoot, byte loaiDan,
            short x, short y, short goc, byte lucTach, byte soPhat,
            short[][] cacDuongX, short[][] cacDuongY, byte loaiSieuCao,
            short xSieuCao, short ySieuCao) throws IOException {
        if (cacDuongX == null || cacDuongY == null || cacDuongX.length == 0
                || cacDuongX.length != cacDuongY.length) {
            return;
        }
        for (int i = 0; i < cacDuongX.length; i++) {
            if (cacDuongX[i] == null || cacDuongY[i] == null || cacDuongX[i].length == 0
                    || cacDuongX[i].length != cacDuongY[i].length) {
                return;
            }
        }
        boolean nenDuongDan = loaiDan != 49 && coTheNenDuongDan(cacDuongX, cacDuongY);
        VXLTinNhan ms = new VXLTinNhan(maLenh);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(nenDuongDan ? 0 : 1);
        ds.writeByte(chiMang);
        ds.writeByte(whoShoot);
        ds.writeByte(loaiDan);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeShort(goc);
        if (loaiDan == 17 || loaiDan == 19) {
            ds.writeByte(Math.max(4, Byte.toUnsignedInt(lucTach)));
        }
        ds.writeByte(soPhat <= 0 ? 1 : soPhat);
        ds.writeByte(cacDuongX.length);
        for (int quyDao = 0; quyDao < cacDuongX.length; quyDao++) {
            ds.writeShort(cacDuongX[quyDao].length);
            for (int i = 0; i < cacDuongX[quyDao].length; i++) {
                if (!nenDuongDan || i == 0) {
                    ds.writeShort(cacDuongX[quyDao][i]);
                    ds.writeShort(cacDuongY[quyDao][i]);
                } else {
                    ds.writeByte(cacDuongX[quyDao][i] - cacDuongX[quyDao][i - 1]);
                    ds.writeByte(cacDuongY[quyDao][i] - cacDuongY[quyDao][i - 1]);
                }
            }
        }
        ds.writeByte(loaiSieuCao);
        if (loaiSieuCao == 1 || loaiSieuCao == 2) {
            ds.writeShort(xSieuCao);
            ds.writeShort(ySieuCao);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiCapNhatMauLuyenTap(byte chiSo, int hp, byte trangThaiChet) throws IOException {
        guiCapNhatMauLuyenTap(chiSo, hp, 100, trangThaiChet);
    }

    public void guiCapNhatMauLuyenTap(byte chiSo, int hp, int hpToiDa, byte trangThaiChet) throws IOException {
        int mau = gioiHanMauLuyenTap(hp);
        int mauToiDa = Math.max(1, gioiHanMauLuyenTap(hpToiDa));
        VXLTinNhan ms = new VXLTinNhan(51);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(mau);
        ds.writeByte((byte)Math.max(0, Math.min(25, mau * 25L / mauToiDa)));
        ds.writeByte(trangThaiChet);
        ds.flush();
        this.guiTin(ms);
    }

    private static int gioiHanMauLuyenTap(int hp) {
        return Math.max(0, Math.min(65535, hp));
    }

    public void guiLuotLuyenTapTiep(byte whoNext, short x, short y) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(24);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(whoNext);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeByte(2);
        ds.writeByte(0);
        ds.writeShort(100);
        ds.writeByte(1);
        ds.writeShort(50);
        ds.writeByte(25);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDungVatPhamLuyenTap(byte whoUse, byte itemId, short iconUse) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(26);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(whoUse);
        ds.writeByte(itemId);
        ds.writeShort(iconUse);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiCapNhatXYLuyenTap(byte chiSo, short x, short y) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(53);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDatLaiHoLuyenTap() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-92);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1);
        ds.flush();
        this.guiTin(ms);
    }

    private static boolean coTheNenDuongDan(short[][] cacDuongX, short[][] cacDuongY) {
        for (int quyDao = 0; quyDao < cacDuongX.length; quyDao++) {
            for (int i = 1; i < cacDuongX[quyDao].length; i++) {
                int lechX = cacDuongX[quyDao][i] - cacDuongX[quyDao][i - 1];
                int lechY = cacDuongY[quyDao][i] - cacDuongY[quyDao][i - 1];
                if (lechX < Byte.MIN_VALUE || lechX > Byte.MAX_VALUE
                        || lechY < Byte.MIN_VALUE || lechY > Byte.MAX_VALUE) {
                    return false;
                }
            }
        }
        return true;
    }

    public void guiYeuCauSkill(byte hanhDong) throws IOException {
        VXLTinNhan ms = new VXLTinNhan(-91);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(hanhDong);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiSkillHawkeye(byte chiSoNguoiBan, byte loaiDan, short[] mucTieuX,
            short[] mucTieuY) throws IOException {
        int soMucTieu = Math.min(mucTieuX == null ? 0 : mucTieuX.length,
                mucTieuY == null ? 0 : mucTieuY.length);
        VXLTinNhan ms = new VXLTinNhan(-91);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(4);
        ds.writeByte(chiSoNguoiBan);
        ds.writeByte(loaiDan);
        ds.writeByte(soMucTieu);
        for (int i = 0; i < soMucTieu; i++) {
            ds.writeShort(mucTieuX[i]);
            ds.writeShort(mucTieuY[i]);
        }
        ds.flush();
        this.guiTin(ms);
    }
}
