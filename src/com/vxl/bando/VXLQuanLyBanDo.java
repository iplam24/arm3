package com.vxl.bando;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.tienich.VXLTienIch;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

public class VXLQuanLyBanDo {
    private static final int SO_LO_TOI_DA = 512;
    private static final int SO_VAT_CAN_TAM_THOI_TOI_DA = 96;
    private static final int LECH_X_TO_NHEN = 21;
    private static final int LECH_Y_TO_NHEN = 20;
    private static final ConcurrentHashMap<String, MatNaLo> BO_NHO_MAT_NA_LO =
            new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, MatNaVatCan> BO_NHO_MAT_NA_VAT_CAN =
            new ConcurrentHashMap<>();
    private static final short[] DEFAULT_SPAWN_X = new short[]{220, 600, 320, 720, 150, 850, 460, 980};
    private static final short[] DEFAULT_SPAWN_Y = new short[]{300, 300, 260, 260, 320, 320, 280, 280};
    private final ArrayList<MapEntry> mucs = new ArrayList<>();
    private volatile VungLo[] cacLoDaPha = new VungLo[0];
    private volatile VatCanTamThoi[] cacVatCanTamThoi = new VatCanTamThoi[0];
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
        this.cacLoDaPha = new VungLo[0];
        this.cacVatCanTamThoi = new VatCanTamThoi[0];
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
        for (VatCanTamThoi vatCan : this.cacVatCanTamThoi) {
            if (vatCan.coVaCham(x, y)) {
                return true;
            }
        }
        for (VungLo lo : this.cacLoDaPha) {
            if (lo.chua(x, y)) {
                return false;
            }
        }
        for (MapEntry muc : this.mucs) {
            if (muc.coVaCham(x, y)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void taoLo(short tamX, short tamY, int chieuRongLo, int chieuCaoLo) {
        int nuaRong = Math.max(1, chieuRongLo / 2);
        int nuaCao = Math.max(1, chieuCaoLo / 2);
        this.phaVatCanTheoHinhElip(tamX, tamY, nuaRong, nuaCao);
        this.themLo(new VungLo(tamX, tamY, nuaRong, nuaCao));
    }

    public synchronized void taoLoTheoMatNa(short tamX, short tamY, String tenTepMatNa) {
        MatNaLo matNa = BO_NHO_MAT_NA_LO.computeIfAbsent(tenTepMatNa,
                VXLQuanLyBanDo::taiMatNaLo);
        if (matNa == MatNaLo.RONG) {
            this.taoLo(tamX, tamY, 32, 26);
            return;
        }
        this.phaVatCanTheoMatNa(tamX, tamY, matNa);
        this.themLo(new VungLo(tamX, tamY, matNa));
    }

    public synchronized void taoToNhen(short diemVaChamX, short diemVaChamY) {
        MatNaVatCan matNa = BO_NHO_MAT_NA_VAT_CAN.computeIfAbsent("mangnhen.png",
                VXLQuanLyBanDo::taiMatNaVatCan);
        if (matNa == MatNaVatCan.RONG) {
            return;
        }
        VatCanTamThoi vatCan = new VatCanTamThoi(
                diemVaChamX - LECH_X_TO_NHEN,
                diemVaChamY - LECH_Y_TO_NHEN, matNa);
        VatCanTamThoi[] hienTai = this.cacVatCanTamThoi;
        int viTriBatDau = hienTai.length >= SO_VAT_CAN_TAM_THOI_TOI_DA
                ? hienTai.length - SO_VAT_CAN_TAM_THOI_TOI_DA + 1 : 0;
        VatCanTamThoi[] capNhat = new VatCanTamThoi[hienTai.length - viTriBatDau + 1];
        System.arraycopy(hienTai, viTriBatDau, capNhat, 0,
                hienTai.length - viTriBatDau);
        capNhat[capNhat.length - 1] = vatCan;
        this.cacVatCanTamThoi = capNhat;
    }

    private void themLo(VungLo loMoi) {
        VungLo[] hienTai = this.cacLoDaPha;
        int viTriBatDau = hienTai.length >= SO_LO_TOI_DA
                ? hienTai.length - SO_LO_TOI_DA + 1 : 0;
        VungLo[] capNhat = new VungLo[hienTai.length - viTriBatDau + 1];
        System.arraycopy(hienTai, viTriBatDau, capNhat, 0, hienTai.length - viTriBatDau);
        capNhat[capNhat.length - 1] = loMoi;
        this.cacLoDaPha = capNhat;
    }

    private static MatNaLo taiMatNaLo(String tenTepMatNa) {
        try {
            File tep = new File("res/icon/hole/" + tenTepMatNa);
            BufferedImage anh = tep.isFile() ? ImageIO.read(tep) : null;
            if (anh == null) {
                return MatNaLo.RONG;
            }
            int chieuRong = anh.getWidth();
            int chieuCao = anh.getHeight();
            boolean[] diemBiPha = new boolean[chieuRong * chieuCao];
            boolean coDiemBiPha = false;
            for (int y = 0; y < chieuCao; y++) {
                for (int x = 0; x < chieuRong; x++) {
                    int mau = anh.getRGB(x, y);
                    boolean biPha = (mau >>> 24) > 0 && (mau & 0x00FFFFFF) == 0;
                    diemBiPha[y * chieuRong + x] = biPha;
                    coDiemBiPha |= biPha;
                }
            }
            if (!coDiemBiPha) {
                return MatNaLo.RONG;
            }
            boolean[] diemMoRong = diemBiPha.clone();
            for (int y = 0; y < chieuCao; y++) {
                for (int x = 0; x < chieuRong; x++) {
                    if (!diemBiPha[y * chieuRong + x]) {
                        continue;
                    }
                    for (int lechY = -1; lechY <= 1; lechY++) {
                        for (int lechX = -1; lechX <= 1; lechX++) {
                            int xMoi = x + lechX;
                            int yMoi = y + lechY;
                            if (xMoi >= 0 && yMoi >= 0 && xMoi < chieuRong
                                    && yMoi < chieuCao) {
                                diemMoRong[yMoi * chieuRong + xMoi] = true;
                            }
                        }
                    }
                }
            }
            return new MatNaLo(chieuRong, chieuCao, diemMoRong);
        }
        catch (Exception ignored) {
            return MatNaLo.RONG;
        }
    }

    private static MatNaVatCan taiMatNaVatCan(String tenTepAnh) {
        try {
            File tep = new File("res/icon/hole/" + tenTepAnh);
            BufferedImage anh = tep.isFile() ? ImageIO.read(tep) : null;
            if (anh == null) {
                return MatNaVatCan.RONG;
            }
            int chieuRong = anh.getWidth();
            int chieuCao = anh.getHeight();
            boolean[] diemVaCham = new boolean[chieuRong * chieuCao];
            boolean coVaCham = false;
            for (int y = 0; y < chieuCao; y++) {
                for (int x = 0; x < chieuRong; x++) {
                    int mau = anh.getRGB(x, y);
                    boolean laVatCan = (mau >>> 24) > 0
                            && (mau & 0x00FFFFFF) != 0x00FFFFFF;
                    diemVaCham[y * chieuRong + x] = laVatCan;
                    coVaCham |= laVatCan;
                }
            }
            return coVaCham
                    ? new MatNaVatCan(chieuRong, chieuCao, diemVaCham)
                    : MatNaVatCan.RONG;
        }
        catch (Exception ignored) {
            return MatNaVatCan.RONG;
        }
    }

    private void phaVatCanTheoMatNa(short tamX, short tamY, MatNaLo matNa) {
        VatCanTamThoi[] hienTai = this.cacVatCanTamThoi;
        ArrayList<VatCanTamThoi> conLai = new ArrayList<>(hienTai.length);
        for (VatCanTamThoi vatCan : hienTai) {
            VatCanTamThoi daPha = vatCan.phaTheoMatNa(tamX, tamY, matNa);
            if (daPha.conVaCham()) {
                conLai.add(daPha);
            }
        }
        this.cacVatCanTamThoi = conLai.toArray(new VatCanTamThoi[0]);
    }

    private void phaVatCanTheoHinhElip(short tamX, short tamY,
            int nuaRong, int nuaCao) {
        VatCanTamThoi[] hienTai = this.cacVatCanTamThoi;
        ArrayList<VatCanTamThoi> conLai = new ArrayList<>(hienTai.length);
        for (VatCanTamThoi vatCan : hienTai) {
            VatCanTamThoi daPha = vatCan.phaTheoHinhElip(tamX, tamY,
                    nuaRong, nuaCao);
            if (daPha.conVaCham()) {
                conLai.add(daPha);
            }
        }
        this.cacVatCanTamThoi = conLai.toArray(new VatCanTamThoi[0]);
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
            int rgb = mau & 0x00FFFFFF;
            return alpha > 0 && rgb != 0x00FFFFFF;
        }
    }
    private static final class MatNaLo {
        private static final MatNaLo RONG = new MatNaLo(0, 0, new boolean[0]);
        private final int chieuRong;
        private final int chieuCao;
        private final boolean[] diemBiPha;

        private MatNaLo(int chieuRong, int chieuCao, boolean[] diemBiPha) {
            this.chieuRong = chieuRong;
            this.chieuCao = chieuCao;
            this.diemBiPha = diemBiPha;
        }
    }

    private static final class MatNaVatCan {
        private static final MatNaVatCan RONG = new MatNaVatCan(0, 0,
                new boolean[0]);
        private final int chieuRong;
        private final int chieuCao;
        private final boolean[] diemVaCham;

        private MatNaVatCan(int chieuRong, int chieuCao, boolean[] diemVaCham) {
            this.chieuRong = chieuRong;
            this.chieuCao = chieuCao;
            this.diemVaCham = diemVaCham;
        }
    }

    private static final class VatCanTamThoi {
        private final int x;
        private final int y;
        private final int chieuRong;
        private final int chieuCao;
        private final boolean[] diemVaCham;

        private VatCanTamThoi(int x, int y, MatNaVatCan matNa) {
            this(x, y, matNa.chieuRong, matNa.chieuCao,
                    matNa.diemVaCham.clone());
        }

        private VatCanTamThoi(int x, int y, int chieuRong, int chieuCao,
                boolean[] diemVaCham) {
            this.x = x;
            this.y = y;
            this.chieuRong = chieuRong;
            this.chieuCao = chieuCao;
            this.diemVaCham = diemVaCham;
        }

        private boolean coVaCham(int px, int py) {
            int localX = px - this.x;
            int localY = py - this.y;
            return localX >= 0 && localY >= 0 && localX < this.chieuRong
                    && localY < this.chieuCao
                    && this.diemVaCham[localY * this.chieuRong + localX];
        }

        private VatCanTamThoi phaTheoMatNa(short tamX, short tamY,
                MatNaLo matNa) {
            boolean[] capNhat = this.diemVaCham.clone();
            int matNaX = tamX - matNa.chieuRong / 2;
            int matNaY = tamY - matNa.chieuCao / 2;
            for (int localY = 0; localY < this.chieuCao; localY++) {
                int yTheGioi = this.y + localY;
                int yMatNa = yTheGioi - matNaY;
                if (yMatNa < 0 || yMatNa >= matNa.chieuCao) {
                    continue;
                }
                for (int localX = 0; localX < this.chieuRong; localX++) {
                    int viTri = localY * this.chieuRong + localX;
                    if (!capNhat[viTri]) {
                        continue;
                    }
                    int xMatNa = this.x + localX - matNaX;
                    if (xMatNa >= 0 && xMatNa < matNa.chieuRong
                            && matNa.diemBiPha[yMatNa * matNa.chieuRong + xMatNa]) {
                        capNhat[viTri] = false;
                    }
                }
            }
            return new VatCanTamThoi(this.x, this.y, this.chieuRong,
                    this.chieuCao, capNhat);
        }

        private VatCanTamThoi phaTheoHinhElip(short tamX, short tamY,
                int nuaRong, int nuaCao) {
            boolean[] capNhat = this.diemVaCham.clone();
            long binhPhuongRong = (long)nuaRong * nuaRong;
            long binhPhuongCao = (long)nuaCao * nuaCao;
            for (int localY = 0; localY < this.chieuCao; localY++) {
                for (int localX = 0; localX < this.chieuRong; localX++) {
                    int viTri = localY * this.chieuRong + localX;
                    if (!capNhat[viTri]) {
                        continue;
                    }
                    long dx = this.x + localX - tamX;
                    long dy = this.y + localY - tamY;
                    if (dx * dx * binhPhuongCao + dy * dy * binhPhuongRong
                            <= binhPhuongRong * binhPhuongCao) {
                        capNhat[viTri] = false;
                    }
                }
            }
            return new VatCanTamThoi(this.x, this.y, this.chieuRong,
                    this.chieuCao, capNhat);
        }

        private boolean conVaCham() {
            for (boolean diem : this.diemVaCham) {
                if (diem) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final class VungLo {
        private final int tamX;
        private final int tamY;
        private final int nuaRong;
        private final int nuaCao;
        private final MatNaLo matNa;

        private VungLo(int tamX, int tamY, int nuaRong, int nuaCao) {
            this.tamX = tamX;
            this.tamY = tamY;
            this.nuaRong = nuaRong;
            this.nuaCao = nuaCao;
            this.matNa = null;
        }

        private VungLo(int tamX, int tamY, MatNaLo matNa) {
            this.tamX = tamX;
            this.tamY = tamY;
            this.nuaRong = 0;
            this.nuaCao = 0;
            this.matNa = matNa;
        }

        private boolean chua(int x, int y) {
            if (this.matNa != null) {
                int localX = x - (this.tamX - this.matNa.chieuRong / 2);
                int localY = y - (this.tamY - this.matNa.chieuCao / 2);
                return localX >= 0 && localY >= 0 && localX < this.matNa.chieuRong
                        && localY < this.matNa.chieuCao
                        && this.matNa.diemBiPha[localY * this.matNa.chieuRong + localX];
            }
            long dx = x - this.tamX;
            long dy = y - this.tamY;
            long binhPhuongRong = (long)this.nuaRong * this.nuaRong;
            long binhPhuongCao = (long)this.nuaCao * this.nuaCao;
            return dx * dx * binhPhuongCao + dy * dy * binhPhuongRong
                    <= binhPhuongRong * binhPhuongCao;
        }
    }

}
