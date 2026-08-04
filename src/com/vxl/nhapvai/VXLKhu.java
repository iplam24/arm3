package com.vxl.nhapvai;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLTinNhan;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLKhu {
    public byte zoneId;
    public int pts;
    public int numPlayer;
    public int maxPlayer;
    public HashMap<Integer, VXLNguoiChoi> players_index = new HashMap();
    public HashMap<Integer, VXLNguoiChoi> players_id = new HashMap();

    public VXLKhu(int ma) {
        this.zoneId = (byte)ma;
        this.maxPlayer = 24;
    }

    public boolean vao(VXLNguoiChoi nguoiChoi) {
        if (this.players_id.get(nguoiChoi.ma) == null) {
            for (int i = 0; i < this.maxPlayer; ++i) {
                if (this.players_index.get(i) != null) continue;
                this.players_index.put(i, nguoiChoi);
                this.players_id.put(nguoiChoi.ma, nguoiChoi);
                nguoiChoi.chiSo = i;
                nguoiChoi.zoneId = this.zoneId;
                nguoiChoi.zone = this;
                ++this.numPlayer;
                this.datDiem();
                this.guiNguoiChoiTrongKhu(nguoiChoi);
                nguoiChoi.dichVu.guiNhanVatPhu();
                return true;
            }
        }
        return false;
    }

    public boolean roi(VXLNguoiChoi nguoiChoi) {
        if (this.players_id.get(nguoiChoi.ma) != null) {
            this.players_index.remove(nguoiChoi.chiSo);
            this.players_id.remove(nguoiChoi.ma);
            --this.numPlayer;
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
            nguoiChoi.dichVu.guiTin(ms);
        }
    }

    public void guiNguoiChoiTrongKhu(VXLNguoiChoi nguoiChoi) {
        try {
            for (VXLNguoiChoi pl : this.players_id.values()) {
                pl.dichVu.vaoCho(nguoiChoi);
            }
            for (VXLNguoiChoi pl : this.players_id.values()) {
                if (pl == nguoiChoi) continue;
                nguoiChoi.dichVu.vaoCho(pl);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(VXLKhu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiNguoiChoiRoiKhu(int chiSo) {
        for (VXLNguoiChoi pl : this.players_id.values()) {
            pl.dichVu.roi(chiSo);
        }
    }
}

