package com.vxl.phong;

import com.vxl.chien.VXLQuanLyChien;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.nhapvai.VXLBanDoRPG;
import java.io.IOException;

public class VXLChoDau {

    public final VXLPhong phong;
    public final byte ma;
    public volatile byte maxPlayers;
    public volatile byte maBanDo;
    public int tien;
    public String ten;
    public volatile boolean started;
    private final VXLNguoiChoi[] nguoiChois;
    private final boolean[] sanSang;
    private VXLNguoiChoi chuPhong;
    private VXLQuanLyChien fight;
    private final VXLQuanLyNguoiChoiAo quanLyNguoiChoiAo;

    public VXLChoDau(VXLPhong phong, byte ma, byte maxPlayers, byte maBanDo) {
        if (phong == null) {
            throw new IllegalArgumentException("Phong must not be null.");
        }
        if (maxPlayers <= 0) {
            throw new IllegalArgumentException("maxPlayers must be positive.");
        }
        this.phong = phong;
        this.ma = ma;
        this.maxPlayers = maxPlayers;
        this.maBanDo = maBanDo;
        this.tien = 0;
        this.ten = "Khu vực " + (ma + 1);
        this.nguoiChois = new VXLNguoiChoi[maxPlayers];
        this.sanSang = new boolean[maxPlayers];
        this.quanLyNguoiChoiAo = new VXLQuanLyNguoiChoiAo(this.nguoiChois);
    }

    public synchronized int laySoNguoiChoi() {
        int dem = 0;
        for (VXLNguoiChoi nguoiChoi : this.nguoiChois) {
            if (nguoiChoi != null) {
                dem++;
            }
        }
        return dem;
    }

    public synchronized VXLNguoiChoi layChuPhong() {
        return this.chuPhong;
    }

    public synchronized VXLNguoiChoi[] chupNguoiChoi() {
        return this.nguoiChois.clone();
    }

    public synchronized boolean vao(VXLNguoiChoi nguoiChoi, String matKhau) throws IOException {
        if (nguoiChoi == null || nguoiChoi.dichVu == null) {
            return false;
        }
        if (this.started) {
            nguoiChoi.startOKDlg2("Bàn đang thi đấu.");
            return false;
        }
        if (this.chiSoCua(nguoiChoi) >= 0) {
            VXLQuanLyPhong.gan(nguoiChoi, this);
            return true;
        }
        if (VXLQuanLyPhong.layBanCho(nguoiChoi) != null) {
            VXLQuanLyPhong.roiBanCho(nguoiChoi);
        }
        int o = this.oTrongDauTien();
        if (o < 0) {
            nguoiChoi.startOKDlg2("Khu vực đã đầy.");
            return false;
        }
        nguoiChoi.roiLuyenTapNeuCan();
        VXLBanDoRPG.roi(nguoiChoi);
        this.nguoiChois[o] = nguoiChoi;
        this.sanSang[o] = false;
        nguoiChoi.chiSo = o;
        nguoiChoi.pointSeat = (byte)o;
        nguoiChoi.isReady = false;
        if (this.chuPhong == null) {
            this.chuPhong = nguoiChoi;
        }
        VXLQuanLyPhong.gan(nguoiChoi, this);

        nguoiChoi.dichVu.guiThongTinChoDau(this.phong.ma, this.ma, this.ten, this.phong.loai);
        nguoiChoi.dichVu.guiNguoiChoiVaoDau(nguoiChoi, this.chuPhong, this.phong.ma, this.ma);
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null && existing != nguoiChoi && existing.dichVu != null) {
                nguoiChoi.dichVu.guiNguoiChoiVaoDau(existing, this.chuPhong, this.phong.ma, this.ma);
            }
        }
        nguoiChoi.dichVu.guiChonBanDoDau(this.maBanDo);
        nguoiChoi.dichVu.guiSoNguoiDau(this.maxPlayers);
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null && existing != nguoiChoi && existing.dichVu != null) {
                existing.dichVu.guiNguoiChoiVaoDau(nguoiChoi, this.chuPhong, this.phong.ma, this.ma);
            }
        }
        this.phatTien();
        return true;
    }

    public synchronized void roi(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        int o = this.chiSoCua(nguoiChoi);
        if (o < 0) {
            VXLQuanLyPhong.boGan(nguoiChoi, this);
            return;
        }
        this.nguoiChois[o] = null;
        this.sanSang[o] = false;
        nguoiChoi.isReady = false;
        nguoiChoi.chiSo = -1;
        nguoiChoi.pointSeat = 0;
        VXLQuanLyPhong.boGan(nguoiChoi, this);
        if (this.fight != null) {
            this.fight.khiNguoiChoiRoi(nguoiChoi);
        }
        if (nguoiChoi == this.chuPhong) {
            this.chuPhong = this.nguoiChoiDauTien();
        }
        int ownerId = this.chuPhong != null ? this.chuPhong.ma : -1;
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null && existing.dichVu != null) {
                existing.dichVu.guiRoiDau(nguoiChoi.ma, ownerId);
            }
        }
        if (nguoiChoi.dichVu != null) {
            nguoiChoi.dichVu.guiRoiDau(nguoiChoi.ma, ownerId);
        }
        if (this.laySoNguoiChoi() == 0) {
            this.started = false;
            if (this.fight != null) {
                this.fight.dungBot();
            }
            this.fight = null;
            this.quanLyNguoiChoiAo.datLai();
            this.chuPhong = null;
        }
    }

    public synchronized void datSanSang(VXLNguoiChoi nguoiChoi, boolean giaTri) throws IOException {
        if (nguoiChoi == null) {
            return;
        }
        int o = this.chiSoCua(nguoiChoi);
        if (o < 0 || this.started) {
            return;
        }
        this.sanSang[o] = giaTri;
        nguoiChoi.isReady = giaTri;
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null && existing.dichVu != null) {
                existing.dichVu.guiSanSangDau(nguoiChoi.ma, giaTri);
            }
        }
    }

    public synchronized void datBanDo(VXLNguoiChoi nguoiChoi, byte maBanDo) throws IOException {
        if (nguoiChoi == null || nguoiChoi != this.chuPhong || this.started) {
            return;
        }
        this.maBanDo = maBanDo;
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null && existing.dichVu != null) {
                existing.dichVu.guiChonBanDoDau(maBanDo);
            }
        }
    }

    public synchronized void datSoNguoiChoi(VXLNguoiChoi nguoiChoi, byte soNguoi) throws IOException {
        int gioiHan = Byte.toUnsignedInt(soNguoi);
        if (nguoiChoi != this.chuPhong || this.started
                || (gioiHan != 2 && gioiHan != 4 && gioiHan != 6 && gioiHan != 8)
                || gioiHan > this.nguoiChois.length) {
            return;
        }
        for (int i = gioiHan; i < this.nguoiChois.length; i++) {
            if (this.nguoiChois[i] != null) {
                return;
            }
        }
        this.maxPlayers = soNguoi;
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null && existing.dichVu != null) {
                existing.dichVu.guiSoNguoiDau(soNguoi);
            }
        }
    }

    public synchronized void batDau(VXLNguoiChoi nguoiChoi) throws IOException {
        if (nguoiChoi == null || this.started || nguoiChoi != this.chuPhong) {
            return;
        }
        if (this.laySoNguoiChoi() == 0) {
            nguoiChoi.startOKDlg2("Chưa có người chơi.");
            return;
        }
        this.started = true;
        this.quanLyNguoiChoiAo.datLai();
        try {
            boolean cheDoCamTu = this.maBanDo == VXLQuanLyChien.MA_BAN_DO_HAI_TOA_THAP;
            this.fight = new VXLQuanLyChien(this, this.chupNguoiChoi(), this.maBanDo);
            this.quanLyNguoiChoiAo.boSungChoTran(this.fight, cheDoCamTu, this.chuPhong,
                    this.phong, this.ma, Byte.toUnsignedInt(this.maxPlayers));
            this.fight.batDau();
        }
        catch (IOException | RuntimeException ex) {
            this.started = false;
            if (this.fight != null) {
                this.fight.dungBot();
                this.fight = null;
            }
            this.quanLyNguoiChoiAo.xoa(this.chuPhong);
            throw ex;
        }
    }

    public synchronized VXLQuanLyChien layTranDau() {
        return this.fight;
    }

    public synchronized void ketThucDau() {
        if (!this.started && this.fight == null) {
            return;
        }
        this.started = false;
        if (this.fight != null) {
            this.fight.dungBot();
        }
        this.fight = null;
        this.quanLyNguoiChoiAo.xoa(this.chuPhong);
        for (int i = 0; i < this.sanSang.length; i++) {
            this.sanSang[i] = false;
            if (this.nguoiChois[i] != null) {
                this.nguoiChois[i].isReady = false;
            }
        }
    }

    private int oTrongDauTien() {
        int gioiHan = Math.min(Byte.toUnsignedInt(this.maxPlayers), this.nguoiChois.length);
        for (int i = 0; i < gioiHan; i++) {
            if (this.nguoiChois[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private int chiSoCua(VXLNguoiChoi nguoiChoi) {
        for (int i = 0; i < this.nguoiChois.length; i++) {
            if (this.nguoiChois[i] == nguoiChoi) {
                return i;
            }
        }
        return -1;
    }

    private VXLNguoiChoi nguoiChoiDauTien() {
        for (VXLNguoiChoi nguoiChoi : this.nguoiChois) {
            if (nguoiChoi != null) {
                return nguoiChoi;
            }
        }
        return null;
    }

    private void phatTien() throws IOException {
        for (VXLNguoiChoi existing : this.nguoiChois) {
            if (existing != null && existing.dichVu != null) {
                existing.dichVu.guiTienDau(this.tien, this.phong.loai);
            }
        }
    }
}
