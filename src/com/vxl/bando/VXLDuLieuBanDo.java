package com.vxl.bando;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.tienich.VXLTienIch;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class VXLDuLieuBanDo {
    public static ArrayList<MapDataEntry> entrys = new ArrayList<>();
    public static ArrayList<MapBrickEntry> brickEntrys = new ArrayList<>();
    public static final short[] undestroyTile;

    public static boolean isTileDestroy(int ma) {
        for (int i = 0; i < undestroyTile.length; ++i) {
            if (ma != undestroyTile[i]) continue;
            return true;
        }
        return false;
    }

    public static MapBrickEntry getMapBrickEntry(int ma) {
        if (brickEntrys == null) {
            return null;
        }
        for (MapBrickEntry me : brickEntrys) {
            if (me != null && me.ma == ma) {
                return me;
            }
        }
        return null;
    }

    public static void loadMapBrick(int ma) {
        if (brickEntrys == null) {
            brickEntrys = new ArrayList<>();
        }
        if (existsMapBrick(ma)) {
            return;
        }
        try {
            int resourceId = switch (ma) {
                case 166 -> 161;
                case 167 -> 20;
                case 168 -> 19;
                default -> ma;
            };
            String[] paths = new String[]{
                "res/icon/map/" + ma + ".png",
                "res/icon/map/" + resourceId + ".png",
                "res/icon/map/b" + ma + ".png",
                "res/data/1/b" + ma + ".png",
                "res/data/1/b" + String.format("%02d", ma) + ".png",
                "res/data/1/" + ma + ".png",
                "res/data/2/b" + ma + ".png",
                "res/data/3/b" + ma + ".png",
                "res/data/4/b" + ma + ".png",
                "res/map/b" + ma + ".png"
            };
            BufferedImage img = null;
            for (String path : paths) {
                File f = new File(path);
                if (f.exists() && f.isFile()) {
                    img = ImageIO.read(f);
                    if (img != null) {
                        break;
                    }
                }
            }
            if (img == null) {
                return;
            }
            int W = img.getWidth();
            int H = img.getHeight();
            int[] argb = new int[W * H];
            img.getRGB(0, 0, W, H, argb, 0, W);
            MapBrickEntry me = new MapBrickEntry(ma, argb, W, H);
            brickEntrys.add(me);
        }
        catch (Exception ignored) {
        }
    }

    public static boolean existsMapBrick(int ma) {
        if (brickEntrys == null) {
            return false;
        }
        for (MapBrickEntry me : brickEntrys) {
            if (me != null && me.ma == ma) {
                return true;
            }
        }
        return false;
    }

    static {
        undestroyTile = new short[]{70, 71, 73, 74, 75, 77, 78, 79, 97, 121, 122, 123, 124, 130, 131, 132, 135, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168};
    }

    public static final class MapBrickEntry {
        public int ma;
        public int[] dat;
        public int Width;
        public int Height;

        MapBrickEntry(int ma, int[] dat, int W, int H) {
            this.ma = ma;
            this.dat = dat;
            this.Width = W;
            this.Height = H;
        }
    }

    public static final class MapDataEntry {
        public short backGroundID;
        public byte bgID;
        public byte[] duLieu;
        public short iconID;
        public boolean isCheckFilter;
        public boolean isWaterClass;
        public int mapH;
        public byte mapID;
        public String mapName;
        public int mapW;
        public short[] values = new short[5];
        public short water_class;
        public short yBackGround;
        public short yCloud;
        public short yWater;

        public MapDataEntry(byte[] duLieu, byte mapID, String mapName, short icon, byte bgID) {
            this.duLieu = duLieu == null ? new byte[0] : duLieu;
            this.mapID = mapID;
            this.bgID = bgID;
            this.iconID = icon;
            this.mapName = mapName;
            this.mapW = this.duLieu.length >= 4 ? VXLTienIch.getShort(0, this.duLieu) / 24 : 0;
            this.mapH = this.duLieu.length >= 4 ? VXLTienIch.getShort(2, this.duLieu) / 24 : 0;
            this.khoiTao();
        }

        public void khoiTao() {
            this.backGroundID = this.values[0];
            this.yCloud = this.values[2];
            this.yBackGround = this.values[1];
            this.water_class = this.values[4];
            this.yWater = this.values[3];
            this.isWaterClass = this.water_class != -1;
        }
    }
}

