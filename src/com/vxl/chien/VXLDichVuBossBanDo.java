package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;

final class VXLDichVuBossBanDo {
    static final byte MA_BAN_DO_BAO_VAY = 50;
    static final byte MA_BAN_DO_HAI_TOA_THAP = 51;
    static final byte MA_BAN_DO_KHI_CAU = 52;
    static final byte MA_BAN_DO_DAT_BOM = 53;
    static final byte MA_BAN_DO_RUA = 54;
    static final byte MA_BAN_DO_RONG = 55;

    private static final int CHI_SO_BOSS_DAU_TIEN = 8;
    private static final int GIOI_HAN_CHI_SO_BOSS = 30000;
    private static final byte LOAI_BOSS_THUONG = 2;
    private static final byte LOAI_BOSS_KHI_CAU = 3;
    private static final byte LOAI_SUNG_KHI_CAU = 4;
    private static final short KHONG_CO_PART = -1;

    private final byte maBanDo;
    private final VXLQuanLyBanDo banDo;
    private final VXLChienBinh[] chienBinhs;
    private final BossHienThi[] hienThis = new BossHienThi[12];
    private int soBoss;
    private boolean daKhoiTao;

    VXLDichVuBossBanDo(byte maBanDo, VXLQuanLyBanDo banDo,
            VXLChienBinh[] chienBinhs) {
        this.maBanDo = maBanDo;
        this.banDo = banDo;
        this.chienBinhs = chienBinhs;
    }

    static boolean laBanDoBoss(byte maBanDo) {
        int ma = Byte.toUnsignedInt(maBanDo);
        return ma >= Byte.toUnsignedInt(MA_BAN_DO_BAO_VAY)
                && ma <= Byte.toUnsignedInt(MA_BAN_DO_RONG);
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
        int bacElo = this.layEloCaoNhat() / 3;
        switch (Byte.toUnsignedInt(this.maBanDo)) {
            case MA_BAN_DO_BAO_VAY -> this.taoBossBaoVay(soNguoiChoi, bacElo);
            case MA_BAN_DO_KHI_CAU -> this.taoBossKhiCau(soNguoiChoi, bacElo);
            case MA_BAN_DO_DAT_BOM -> this.taoBossDatBom(soNguoiChoi, bacElo);
            case MA_BAN_DO_RUA -> this.taoBossRua(soNguoiChoi, bacElo);
            case MA_BAN_DO_RONG -> this.taoBossRong(soNguoiChoi, bacElo);
            default -> {
            }
        }
    }

    void guiDoiQuan(VXLPhatTinTranDau phatTin) {
        for (int i = 0; i < this.soBoss; i++) {
            BossHienThi hienThi = this.hienThis[i];
            phatTin.guiThemBoss(hienThi.chienBinh(), hienThi.head(), hienThi.leg(),
                    hienThi.body(), hienThi.hat(), hienThi.wing(), hienThi.loaiClient());
        }
    }

    private void taoBossBaoVay(int soNguoiChoi, int bacElo) {
        String[] ten = new String[]{
            "Spider Machine", "T-Rex Boss", "UFO Boss", "Balloon Boss",
            "Big Boom Boss", "Monkey Boss", "Ghost Boss", "Robot Boss"
        };
        short[] vuKhi = new short[]{40, 42, 45, 31, 32, 36, 44, 10};
        for (int i = 0; i < ten.length; i++) {
            int hp = this.tinhMau(720, soNguoiChoi, bacElo, 28);
            int tanCong = this.tinhTanCong(38 + i * 2, soNguoiChoi, bacElo);
            this.themBoss(CHI_SO_BOSS_DAU_TIEN + i, ten[i], 0, vuKhi[i], false,
                    VXLChienBinh.LOAI_BOSS_KHONG_CO, hp, tanCong, 12 + i,
                    false, LOAI_BOSS_THUONG, null);
        }
    }

    private void taoBossKhiCau(int soNguoiChoi, int bacElo) {
        short x = this.banDo.laySinhX(CHI_SO_BOSS_DAU_TIEN);
        short y = this.banDo.laySinhY(CHI_SO_BOSS_DAU_TIEN);
        int hpThan = this.tinhMau(5200, soNguoiChoi, bacElo, 36);
        int hpSung = this.tinhMau(1700, soNguoiChoi, bacElo, 28);
        this.themBoss(8, "Boss KhÃ­ cáº§u", 0, (short)17, false,
                VXLChienBinh.LOAI_BOSS_KHONG_CO, hpThan,
                this.tinhTanCong(58, soNguoiChoi, bacElo), 24,
                true, LOAI_BOSS_KHI_CAU, new short[]{x, y});
        this.themBoss(9, "PhÃ¡o trÃ¡i KhÃ­ cáº§u", 0, (short)18, false,
                VXLChienBinh.LOAI_BOSS_KHONG_CO, hpSung,
                this.tinhTanCong(48, soNguoiChoi, bacElo), 16,
                true, LOAI_SUNG_KHI_CAU, new short[]{(short)(x - 70), y});
        this.themBoss(10, "PhÃ¡o pháº£i KhÃ­ cáº§u", 0, (short)19, false,
                VXLChienBinh.LOAI_BOSS_KHONG_CO, hpSung,
                this.tinhTanCong(48, soNguoiChoi, bacElo), 16,
                true, LOAI_SUNG_KHI_CAU, new short[]{(short)(x + 70), y});
    }

    private void taoBossDatBom(int soNguoiChoi, int bacElo) {
        this.themBoss(8, "Big Boom Boss", 0, (short)32, false,
                VXLChienBinh.LOAI_BOSS_KHONG_CO,
                this.tinhMau(4300, soNguoiChoi, bacElo, 34),
                this.tinhTanCong(62, soNguoiChoi, bacElo), 20,
                false, LOAI_BOSS_THUONG, null);
        for (int i = 0; i < 6; i++) {
            this.themBoss(9 + i, "Small Boom " + (i + 1), 0, (short)35, true,
                    VXLChienBinh.LOAI_BOSS_KHONG_CO,
                    this.tinhMau(520, soNguoiChoi, bacElo, 24),
                    this.tinhTanCong(52, soNguoiChoi, bacElo), 6,
                    false, LOAI_BOSS_THUONG, null);
        }
    }

    private void taoBossRua(int soNguoiChoi, int bacElo) {
        this.themBoss(8, "Boss RÃ¹a", -54, (short)57, false,
                VXLChienBinh.LOAI_BOSS_RUA,
                this.tinhMau(5600, soNguoiChoi, bacElo, 38),
                this.tinhTanCong(68, soNguoiChoi, bacElo), 32,
                false, LOAI_BOSS_THUONG, null);
    }

    private void taoBossRong(int soNguoiChoi, int bacElo) {
        this.themBoss(8, "Boss Rá»“ng", -55, (short)55, false,
                VXLChienBinh.LOAI_BOSS_RONG,
                this.tinhMau(6800, soNguoiChoi, bacElo, 42),
                this.tinhTanCong(76, soNguoiChoi, bacElo), 28,
                false, LOAI_BOSS_THUONG, null);
    }

    private void themBoss(int chiSo, String ten, int ma, short maVuKhi, boolean camTu,
            byte loaiBossDacBiet, int mau, int tanCong, int giap, boolean coDinh,
            byte loaiClient, short[] toaDo) {
        if (chiSo < 0 || chiSo >= this.chienBinhs.length || this.chienBinhs[chiSo] != null
                || this.soBoss >= this.hienThis.length) {
            return;
        }
        short x = toaDo != null ? toaDo[0] : this.banDo.laySinhX(chiSo);
        short y = toaDo != null ? this.banDo.timViTriDat(toaDo[0], toaDo[1]) : this.banDo.laySinhY(chiSo);
        int maHieuLuc = ma != 0 ? ma : -9000 - chiSo;
        VXLChienBinh boss = new VXLChienBinh((byte)chiSo, x, y, ten, maHieuLuc,
                maVuKhi, (byte)0, camTu, loaiBossDacBiet,
                gioiHanChiSo(mau), gioiHanChiSo(tanCong), gioiHanChiSo(giap));
        boss.coDinh = coDinh;
        this.chienBinhs[chiSo] = boss;
        this.hienThis[this.soBoss++] = new BossHienThi(boss, KHONG_CO_PART,
                KHONG_CO_PART, KHONG_CO_PART, KHONG_CO_PART, KHONG_CO_PART,
                loaiClient);
    }

    private int demNguoiChoi() {
        int soLuong = 0;
        for (int i = 0; i < CHI_SO_BOSS_DAU_TIEN && i < this.chienBinhs.length; i++) {
            if (this.chienBinhs[i] != null && !this.chienBinhs[i].bot) {
                soLuong++;
            }
        }
        return soLuong;
    }

    private int layEloCaoNhat() {
        int eloCaoNhat = 0;
        for (int i = 0; i < CHI_SO_BOSS_DAU_TIEN && i < this.chienBinhs.length; i++) {
            VXLChienBinh chienBinh = this.chienBinhs[i];
            if (chienBinh != null && !chienBinh.bot && chienBinh.nguoiChoi != null) {
                eloCaoNhat = Math.max(eloCaoNhat, Math.max(0, chienBinh.nguoiChoi.cup));
            }
        }
        return eloCaoNhat;
    }

    private int tinhMau(int coBan, int soNguoiChoi, int bacElo, int phanTramMoiNguoi) {
        long heSoNguoi = 100L + Math.max(0, soNguoiChoi - 1) * phanTramMoiNguoi;
        return gioiHanChiSo(coBan * heSoNguoi / 100L + (long)bacElo * 90L);
    }

    private int tinhTanCong(int coBan, int soNguoiChoi, int bacElo) {
        return gioiHanChiSo(coBan + Math.max(0, soNguoiChoi - 1) * 2L
                + (long)bacElo * 3L);
    }

    private static int gioiHanChiSo(long giaTri) {
        return (int)Math.max(1, Math.min(GIOI_HAN_CHI_SO_BOSS, giaTri));
    }

    private record BossHienThi(VXLChienBinh chienBinh, short head, short leg,
            short body, short hat, short wing, byte loaiClient) {
    }
}

