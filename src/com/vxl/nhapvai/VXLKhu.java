package com.vxl.nhapvai;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLTinNhan;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLKhu {
    public byte zoneId;
    public volatile int pts;
    public volatile int numPlayer;
    public int maxPlayer;
    public final Map<Integer, VXLNguoiChoi> players_index = new ConcurrentHashMap<>();
    public final Map<Integer, VXLNguoiChoi> players_id = new ConcurrentHashMap<>();

    public VXLKhu(int ma) {
        this.zoneId = (byte)ma;
        this.maxPlayer = 24;
    }

    public synchronized boolean vao(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || nguoiChoi.ma < 0) {
            return false;
        }
        if (this.players_id.get(nguoiChoi.ma) != null) {
            return true;
        }
        if (nguoiChoi.zone != null && nguoiChoi.zone != this) {
            nguoiChoi.zone.roi(nguoiChoi);
        }
        if (this.players_id.get(nguoiChoi.ma) == null) {
            for (int i = 0; i < this.maxPlayer; ++i) {
                if (this.players_index.get(i) != null) continue;
                this.players_index.put(i, nguoiChoi);
                this.players_id.put(nguoiChoi.ma, nguoiChoi);
                nguoiChoi.chiSo = i;
                nguoiChoi.zoneId = this.zoneId;
                nguoiChoi.zone = this;
                this.numPlayer = this.players_id.size();
                this.datDiem();
                this.guiNguoiChoiTrongKhu(nguoiChoi);
                nguoiChoi.dichVu.guiNhanVatPhu();
                return true;
            }
        }
        return false;
    }

    public synchronized boolean roi(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi != null && this.players_id.get(nguoiChoi.ma) == nguoiChoi) {
            this.players_index.remove(nguoiChoi.chiSo);
            this.players_id.remove(nguoiChoi.ma);
            this.numPlayer = Math.max(0, this.players_id.size());
            int chiSo = nguoiChoi.chiSo;
            nguoiChoi.chiSo = -1;
            nguoiChoi.zoneId = (byte)-1;
            nguoiChoi.zone = null;
            this.datDiem();
            this.guiNguoiChoiRoiKhu(chiSo);
            return true;
        }
        return false;
    }

    public void datDiem() {
        this.pts = this.numPlayer > 20 ? 2 : (this.numPlayer > 15 ? 1 : 0);
    }

    public void guiTatCaNguoiChoi(VXLTinNhan ms) {
        for (VXLNguoiChoi nguoiChoi : this.players_id.values()) {
            if (nguoiChoi != null && nguoiChoi.dichVu != null) {
                nguoiChoi.dichVu.guiTin(ms);
            }
        }
    }

    public void guiNguoiChoiTrongKhu(VXLNguoiChoi nguoiChoi) {
        try {
            for (VXLNguoiChoi pl : this.players_id.values()) {
                if (pl != null && pl.dichVu != null) {
                    pl.dichVu.vaoCho(nguoiChoi);
                }
            }
            for (VXLNguoiChoi pl : this.players_id.values()) {
                if (pl == null || pl == nguoiChoi || nguoiChoi.dichVu == null) continue;
                nguoiChoi.dichVu.vaoCho(pl);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(VXLKhu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiNguoiChoiRoiKhu(int chiSo) {
        for (VXLNguoiChoi pl : this.players_id.values()) {
            if (pl != null && pl.dichVu != null) {
                pl.dichVu.roi(chiSo);
            }
        }
    }
}

