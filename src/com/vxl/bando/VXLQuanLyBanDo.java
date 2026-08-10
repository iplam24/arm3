package com.vxl.bando;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.tienich.VXLTienIch;
import java.util.ArrayList;

public class VXLQuanLyBanDo {
    private static final short[] DEFAULT_SPAWN_X = new short[]{220, 600, 320, 720, 150, 850, 460, 980};
    private static final short[] DEFAULT_SPAWN_Y = new short[]{300, 300, 260, 260, 320, 320, 280, 280};
    private final ArrayList<MapEntry> mucs = new ArrayList<>();
    private short[] spawnX = DEFAULT_SPAWN_X;
    private short[] spawnY = DEFAULT_SPAWN_Y;
    private byte maBanDo;
    private byte maNen;
    private int chieuRong = 1200;
    private int chieuCao = 700;

    public VXLQuanLyBanDo(int mapID) {
        this.setMapId(mapID);
    }

    public void setMapId(int mapID) {
        this.maBanDo = (byte)mapID;
        this.maNen = 0;
        this.chieuRong = 1200;
        this.chieuCao = 700;
        this.spawnX = DEFAULT_SPAWN_X;
        this.spawnY = DEFAULT_SPAWN_Y;
        this.mucs.clear();
        VXLDuLieuBanDo.MapDataEntry muc = this.findEntry(mapID);
        if (muc == null || muc.duLieu == null || muc.duLieu.length < 5) {
            return;
        }
        this.maNen = muc.bgID;
        this.chieuRong = VXLTienIch.getShort(0, muc.duLieu);
        this.chieuCao = VXLTienIch.getShort(2, muc.duLieu);
        this.phanTichBanDo(muc.duLieu);
    }

    public byte layMaBanDo() {
        return this.maBanDo;
    }

    public byte layMaNen() {
        return this.maNen;
    }

    public int getWidth() {
        return this.chieuRong;
    }

    public int getHeight() {
        return this.chieuCao;
    }

    public short laySinhX(int chiSo) {
        int chiSoAnToan = Math.floorMod(chiSo, this.spawnX.length);
        if (chiSo >= 0 && chiSo < this.spawnX.length) {
            return this.spawnX[chiSo];
        }
        return this.spawnX[chiSoAnToan];
    }

    public short laySinhY(int chiSo) {
        short rawX = laySinhX(chiSo);
        short rawY;
        int chiSoAnToan = Math.floorMod(chiSo, this.spawnY.length);
        if (chiSo >= 0 && chiSo < this.spawnY.length) {
            rawY = this.spawnY[chiSo];
        } else {
            rawY = this.spawnY[chiSoAnToan];
        }
        return timViTriDat(rawX, rawY);
    }

    public short timViTriDat(short x, short yBatDau) {
        short testY = (short)Math.max(0, Math.min(this.chieuCao - 1, (int)yBatDau));
        if (this.coVaCham(x, testY)) {
            while (testY > 0 && this.coVaCham(x, testY)) {
                testY--;
            }
            return testY;
        } else {
            while (testY < this.chieuCao - 1 && !this.coVaCham(x, (short)(testY + 1))) {
                testY++;
            }
            return testY;
        }
    }

    public boolean coVaCham(short x, short y) {
        if (x < 0 || y < 0 || x >= this.chieuRong || y >= this.chieuCao) {
            return true;
        }
        for (MapEntry muc : this.mucs) {
            if (muc.coVaCham(x, y)) {
                return true;
            }
        }
        return false;
    }

    private VXLDuLieuBanDo.MapDataEntry findEntry(int mapID) {
        if (VXLDuLieuBanDo.entrys == null) {
            return null;
        }
        for (VXLDuLieuBanDo.MapDataEntry muc : VXLDuLieuBanDo.entrys) {
            if (muc != null && muc.mapID == mapID) {
                return muc;
            }
        }
        return null;
    }

    private void phanTichBanDo(byte[] duLieu) {
        try {
            if (duLieu.length < 5) {
                return;
            }
            int offset = 4;
            int len = duLieu[offset++] & 0xFF;
            for (int i = 0; i < len && offset + 4 < duLieu.length; i++) {
                int brickId = duLieu[offset] & 0xFF;
                short x = (short)VXLTienIch.getShort(offset + 1, duLieu);
                short y = (short)VXLTienIch.getShort(offset + 3, duLieu);
                if (!VXLDuLieuBanDo.existsMapBrick(brickId)) {
                    VXLDuLieuBanDo.loadMapBrick(brickId);
                }
                VXLDuLieuBanDo.MapBrickEntry brick = VXLDuLieuBanDo.getMapBrickEntry(brickId);
                if (brick != null) {
                    this.mucs.add(new MapEntry(x, y, (short)brick.Width, (short)brick.Height, brick.dat));
                }
                offset += 5;
            }
            if (offset >= duLieu.length) {
                return;
            }
            int spawnCount = duLieu[offset++] & 0xFF;
            if (spawnCount <= 0 || spawnCount > 64 || offset + spawnCount * 4 > duLieu.length) {
                return;
            }
            this.spawnX = new short[spawnCount];
            this.spawnY = new short[spawnCount];
            for (int i = 0; i < spawnCount; i++) {
                this.spawnX[i] = (short)VXLTienIch.getShort(offset, duLieu);
                offset += 2;
                this.spawnY[i] = (short)VXLTienIch.getShort(offset, duLieu);
                offset += 2;
            }
        }
        catch (Exception ignored) {
            this.spawnX = DEFAULT_SPAWN_X;
            this.spawnY = DEFAULT_SPAWN_Y;
        }
    }

    private static final class MapEntry {
        private final short x;
        private final short y;
        private final short chieuRong;
        private final short chieuCao;
        private final int[] argb;

        private MapEntry(short x, short y, short chieuRong, short chieuCao, int[] argb) {
            this.x = x;
            this.y = y;
            this.chieuRong = chieuRong;
            this.chieuCao = chieuCao;
            this.argb = argb;
        }

        private boolean coVaCham(short px, short py) {
            int localX = px - this.x;
            int localY = py - this.y;
            if (localX < 0 || localY < 0 || localX >= this.chieuRong || localY >= this.chieuCao) {
                return false;
            }
            if (this.argb == null || this.argb.length <= localY * this.chieuRong + localX) {
                return true;
            }
            int mau = this.argb[localY * this.chieuRong + localX];
            int alpha = mau >>> 24;
            return alpha > 30;
        }
    }
}
