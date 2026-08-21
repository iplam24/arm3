package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;

final class VXLDichVuHaiToaThap {
    private static final int SO_NGUOI_CHOI_TOI_DA = 8;
    private static final int CHI_SO_DICH_DAU_TIEN = 8;
    private static final int SO_PHIEN_QUAN = 2;
    private static final int SO_CAM_TU_CO_BAN = 6;
    private static final int SO_CAM_TU_THEM_MOI_NGUOI = 2;
    private static final int ELO_MOI_BAC = 3;
    private static final int MAU_BOSS_MAC_DINH = 1000;
    private static final int MAU_TANG_MOI_BAC = 100;
    private static final int TAN_CONG_PHIEN_QUAN_MAC_DINH = 35;
    private static final int TAN_CONG_CAM_TU_MAC_DINH = 45;
    private static final int TAN_CONG_TANG_MOI_BAC = 4;
    private static final int GIAP_PHIEN_QUAN_MAC_DINH = 15;
    private static final int GIAP_CAM_TU_MAC_DINH = 8;
    private static final int GIAP_TANG_MOI_BAC = 2;
    private static final int GIOI_HAN_CHI_SO_BOSS = 30000;
    private static final int KHOANG_CACH_SPAWN_CAM_TU_TOI_THIEU = 20;
    private static final int NUA_RONG_HITBOX_CAM_TU = 8;
    private static final int BUOC_TIM_SPAWN_CAM_TU = 18;
    private static final int SO_LAN_TIM_SPAWN_CAM_TU = 36;
    private static final int[] VI_TRI_PHIEN_QUAN = new int[]{8, 9};
    private static final int[] VI_TRI_CAM_TU_HAI_TOA_THAP = new int[]{11, 12, 16, 13, 17, 15, 18, 14};
    private static final int[] VI_TRI_CAM_TU_BAO_VAY = new int[]{10, 11, 12, 13, 14, 15};
    private static final short DAU_PHIEN_QUAN = -1;
    private static final short QUAN_PHIEN_QUAN = 157;
    private static final short AO_PHIEN_QUAN = 158;
    private static final short NON_PHIEN_QUAN = 159;
    private static final short CANH_PHIEN_QUAN = 160;
    private static final short VU_KHI_PHIEN_QUAN = 27;
    private static final short DAU_CAM_TU = -1;
    private static final short QUAN_CAM_TU = 177;
    private static final short AO_CAM_TU = 178;
    private static final short NON_CAM_TU = 179;
    private static final short BALO_BOM_CAM_TU = 180;
    private static final short VU_KHI_VO_HINH_CAM_TU = 193;
    private static final byte LOAI_BOSS_THUONG = 2;

    private final VXLQuanLyBanDo banDo;
    private final VXLChienBinh[] chienBinhs;
    private final boolean baoVay;
    private final VXLChienBinh[] dich;
    private int soDich;
    private boolean daKhoiTao;

    VXLDichVuHaiToaThap(byte maBanDo, VXLQuanLyBanDo banDo, VXLChienBinh[] chienBinhs) {
        this.banDo = banDo;
        this.chienBinhs = chienBinhs;
        this.baoVay = maBanDo == VXLQuanLyChien.MA_BAN_DO_BAO_VAY;
        this.dich = new VXLChienBinh[SO_PHIEN_QUAN
                + SO_CAM_TU_CO_BAN
                + (SO_NGUOI_CHOI_TOI_DA - 1) * SO_CAM_TU_THEM_MOI_NGUOI];
    }

    void khoiTao() {
        if (this.daKhoiTao) {
            return;
        }
        this.daKhoiTao = true;
        int soNguoiChoi = this.demNguoiChoi();
        if (soNguoiChoi <= 0) {
            return;
        }
        int soCamTu = SO_CAM_TU_CO_BAN
                + (soNguoiChoi - 1) * SO_CAM_TU_THEM_MOI_NGUOI;
        int tongSoDich = SO_PHIEN_QUAN + soCamTu;
        if (CHI_SO_DICH_DAU_TIEN + tongSoDich > this.chienBinhs.length) {
            throw new IllegalStateException("Khong du slot chien binh cho map Cam Tu.");
        }
        int bacElo = this.layEloCaoNhat() / ELO_MOI_BAC;
        int mauBoss = gioiHanChiSo(MAU_BOSS_MAC_DINH + (long)bacElo * MAU_TANG_MOI_BAC);
        int tanCongPhienQuan = gioiHanChiSo(TAN_CONG_PHIEN_QUAN_MAC_DINH
                + (long)bacElo * TAN_CONG_TANG_MOI_BAC);
        int tanCongCamTu = gioiHanChiSo(TAN_CONG_CAM_TU_MAC_DINH
                + (long)bacElo * TAN_CONG_TANG_MOI_BAC);
        int giapPhienQuan = gioiHanChiSo(GIAP_PHIEN_QUAN_MAC_DINH
                + (long)bacElo * GIAP_TANG_MOI_BAC);
        int giapCamTu = gioiHanChiSo(GIAP_CAM_TU_MAC_DINH
                + (long)bacElo * GIAP_TANG_MOI_BAC);

        for (int i = 0; i < SO_PHIEN_QUAN; i++) {
            int chiSo = CHI_SO_DICH_DAU_TIEN + this.soDich;
            int viTriMap = VI_TRI_PHIEN_QUAN[i];
            this.themDich(new VXLChienBinh((byte)chiSo,
                    this.banDo.laySinhX(viTriMap), this.banDo.laySinhY(viTriMap),
                    "Phi\u1ebfn qu\u00e2n " + (i + 1), VU_KHI_PHIEN_QUAN, (byte)0,
                    false, mauBoss, tanCongPhienQuan, giapPhienQuan));
        }
        for (int i = 0; i < soCamTu; i++) {
            int chiSo = CHI_SO_DICH_DAU_TIEN + this.soDich;
            short[] viTri = this.layViTriCamTu(i);
            this.themDich(new VXLChienBinh((byte)chiSo, viTri[0], viTri[1],
                    "C\u1ea3m t\u1eed " + (i + 1), VU_KHI_VO_HINH_CAM_TU, (byte)0,
                    true, mauBoss, tanCongCamTu, giapCamTu));
        }
    }

    void guiDoiQuan(VXLPhatTinTranDau phatTin) {
        for (int i = 0; i < this.soDich; i++) {
            VXLChienBinh boss = this.dich[i];
            if (boss.camTu) {
                phatTin.guiThemBoss(boss, DAU_CAM_TU, QUAN_CAM_TU, AO_CAM_TU,
                        NON_CAM_TU, BALO_BOM_CAM_TU, LOAI_BOSS_THUONG);
            } else {
                phatTin.guiThemBoss(boss, DAU_PHIEN_QUAN, QUAN_PHIEN_QUAN,
                        AO_PHIEN_QUAN, NON_PHIEN_QUAN, CANH_PHIEN_QUAN,
                        LOAI_BOSS_THUONG);
            }
        }
    }

    int demNguoiChoiSong() {
        int soLuong = 0;
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && !chienBinh.bot && !chienBinh.chet
                    && !chienBinh.daRoiTran) {
                soLuong++;
            }
        }
        return soLuong;
    }

    int demDichSong() {
        int soLuong = 0;
        for (int i = 0; i < this.soDich; i++) {
            if (!this.dich[i].chet) {
                soLuong++;
            }
        }
        return soLuong;
    }

    private int demNguoiChoi() {
        int soLuong = 0;
        for (int i = 0; i < SO_NGUOI_CHOI_TOI_DA && i < this.chienBinhs.length; i++) {
            VXLChienBinh chienBinh = this.chienBinhs[i];
            if (chienBinh != null && !chienBinh.bot) {
                soLuong++;
            }
        }
        return soLuong;
    }

    private int layEloCaoNhat() {
        int eloCaoNhat = 0;
        for (int i = 0; i < SO_NGUOI_CHOI_TOI_DA && i < this.chienBinhs.length; i++) {
            VXLChienBinh chienBinh = this.chienBinhs[i];
            if (chienBinh != null && !chienBinh.bot && chienBinh.nguoiChoi != null) {
                eloCaoNhat = Math.max(eloCaoNhat, Math.max(0, chienBinh.nguoiChoi.towerElo));
            }
        }
        return eloCaoNhat;
    }

    private void themDich(VXLChienBinh chienBinh) {
        this.chienBinhs[Byte.toUnsignedInt(chienBinh.chiSo)] = chienBinh;
        this.dich[this.soDich++] = chienBinh;
    }

    private int[] viTriCamTu() {
        return this.baoVay ? VI_TRI_CAM_TU_BAO_VAY : VI_TRI_CAM_TU_HAI_TOA_THAP;
    }

    private short[] layViTriCamTu(int thuTu) {
        int viTriMap = this.viTriCamTu()[thuTu % this.viTriCamTu().length];
        int xGoc = this.banDo.laySinhX(viTriMap);
        short yGoc = this.banDo.laySinhY(viTriMap);
        int vongLap = thuTu / this.viTriCamTu().length;
        int huongVaoGiua = xGoc < this.banDo.getWidth() / 2 ? 1 : -1;
        int xBatDau = xGoc + huongVaoGiua * vongLap * BUOC_TIM_SPAWN_CAM_TU;
        for (int lan = 0; lan < SO_LAN_TIM_SPAWN_CAM_TU; lan++) {
            int bacLech = (lan + 1) / 2;
            int huongLech = lan == 0 ? 0 : lan % 2 == 1 ? huongVaoGiua : -huongVaoGiua;
            int x = xBatDau + huongLech * bacLech * BUOC_TIM_SPAWN_CAM_TU;
            short xDaGioiHan = (short)Math.max(24,
                    Math.min(this.banDo.getWidth() - 24, x));
            short y = this.timViTriDatCamTu(xDaGioiHan, yGoc);
            if (this.viTriSpawnCamTuHopLe(xDaGioiHan, y)) {
                return new short[]{xDaGioiHan, y};
            }
        }
        short xDuPhong = (short)Math.max(24,
                Math.min(this.banDo.getWidth() - 24, xBatDau));
        return new short[]{xDuPhong, this.timViTriDatCamTu(xDuPhong, yGoc)};
    }

    private short timViTriDatCamTu(short x, short yBatDau) {
        int yDat = this.banDo.getHeight() - 1;
        for (int lechX = -NUA_RONG_HITBOX_CAM_TU;
                lechX <= NUA_RONG_HITBOX_CAM_TU; lechX += NUA_RONG_HITBOX_CAM_TU) {
            short diemX = (short)Math.max(0,
                    Math.min(this.banDo.getWidth() - 1, x + lechX));
            yDat = Math.min(yDat, this.banDo.timViTriDat(diemX, yBatDau));
        }
        return (short)yDat;
    }

    private boolean viTriSpawnCamTuHopLe(short x, short y) {
        for (int lechX = -NUA_RONG_HITBOX_CAM_TU;
                lechX <= NUA_RONG_HITBOX_CAM_TU; lechX += NUA_RONG_HITBOX_CAM_TU) {
            short diemX = (short)(x + lechX);
            if (this.banDo.coVaCham(diemX, (short)Math.max(0, y - 4))
                    || this.banDo.coVaCham(diemX, (short)Math.max(0, y - 16))
                    || this.banDo.coVaCham(diemX, (short)Math.max(0, y - 28))) {
                return false;
            }
        }
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh == null || chienBinh.chet) {
                continue;
            }
            if (Math.abs(chienBinh.x - x) < KHOANG_CACH_SPAWN_CAM_TU_TOI_THIEU
                    && Math.abs(chienBinh.y - y) < 36) {
                return false;
            }
        }
        return true;
    }

    private static int gioiHanChiSo(long giaTri) {
        return (int)Math.max(1, Math.min(GIOI_HAN_CHI_SO_BOSS, giaTri));
    }
}
