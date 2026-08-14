package com.vxl.chien;

public final class VXLKyNangAvenger {
    public static final byte MA_IRON_MAN = 1;
    public static final byte MA_HULK = 2;
    public static final byte MA_THOR = 3;
    public static final byte MA_LOKI = 4;
    public static final byte MA_CAPTAIN = 5;
    public static final byte MA_WINTER_SOLDIER = 6;
    public static final byte MA_HAWKEYE = 7;
    public static final byte MA_ULTRON = 8;
    public static final byte MA_SPIDER_MAN = 9;

    public static final byte HANH_DONG_LOKI = 0;
    public static final byte HANH_DONG_HAWKEYE = 1;
    public static final byte HANH_DONG_ULTRON = 2;
    public static final byte HANH_DONG_THOR = 3;

    public static final byte MENU_HAWKEYE = 1;
    public static final byte MENU_ULTRON = 2;
    public static final byte MENU_THOR = 3;
    public static final byte MENU_LOKI = 5;
    public static final byte MENU_SPIDER_MAN = 6;

    private static final byte LOAI_DAN_WEB_PRISON = 56;
    private static final int SO_PHAT_SPIDER_MAN_CAN_TICH = 3;
    private static final int SO_LUOT_THOR_CAN_TICH = 2;
    private static final int SO_LUOT_HAWKEYE_CAN_TICH = 2;
    private static final int LUOT_ULTRON_BAT_DAU_NHAN_BAN = 3;
    private static final int GIOI_HAN_BAN_SAO_ULTRON = 3;
    private static final int LUOT_MAC_TO_SPIDER_MAN = 1;

    private final byte maAvenger;
    private int soPhatDaTich;
    private int soLuotDaTich;
    private int soLuotDaBatDau;
    private boolean spiderChoLuotMoi;
    private boolean spiderSanSang;
    private boolean daGuiNutSkill;
    private boolean daDungSkillTrongLuot;
    private boolean daDungSkillMotLan;
    private byte avengerSaoChep;
    private short vuKhiSaoChep = -1;

    private VXLKyNangAvenger(byte maAvenger) {
        this.maAvenger = maAvenger;
    }

    public static VXLKyNangAvenger tao(byte maAvenger) {
        return switch (Byte.toUnsignedInt(maAvenger)) {
            case MA_IRON_MAN, MA_HULK, MA_THOR, MA_LOKI, MA_CAPTAIN,
                    MA_WINTER_SOLDIER, MA_HAWKEYE, MA_ULTRON, MA_SPIDER_MAN ->
                    new VXLKyNangAvenger(maAvenger);
            default -> null;
        };
    }

    public boolean laSkillRieng() {
        return this.laSpiderMan();
    }

    public boolean laIronMan() {
        return this.maAvenger == MA_IRON_MAN;
    }

    public boolean laHulk() {
        return this.maAvenger == MA_HULK;
    }

    public boolean laThor() {
        return this.maAvenger == MA_THOR;
    }

    public boolean laLoki() {
        return this.maAvenger == MA_LOKI;
    }

    public boolean laCaptain() {
        return this.maAvenger == MA_CAPTAIN;
    }

    public boolean laWinterSoldier() {
        return this.maAvenger == MA_WINTER_SOLDIER;
    }

    public boolean laHawkeye() {
        return this.maAvenger == MA_HAWKEYE;
    }

    public boolean laUltron() {
        return this.maAvenger == MA_ULTRON;
    }

    public boolean laSpiderMan() {
        return this.maAvenger == MA_SPIDER_MAN;
    }

    public boolean laSkillChuDong() {
        return this.laThor() || this.laLoki() || this.laHawkeye()
                || this.laUltron() || this.laSpiderMan();
    }

    public void batDauLuot() {
        this.soLuotDaBatDau++;
        if (this.laSpiderMan() && this.spiderChoLuotMoi) {
            this.spiderChoLuotMoi = false;
            this.spiderSanSang = true;
        }
        this.daGuiNutSkill = false;
        this.daDungSkillTrongLuot = false;
    }

    public void ghiNhanKetThucLuot() {
        if (this.daDungSkillTrongLuot) {
            return;
        }
        if (this.laThor()) {
            this.soLuotDaTich = Math.min(SO_LUOT_THOR_CAN_TICH,
                    this.soLuotDaTich + 1);
        } else if (this.laHawkeye()) {
            this.soLuotDaTich = Math.min(SO_LUOT_HAWKEYE_CAN_TICH,
                    this.soLuotDaTich + 1);
        }
    }

    public boolean sanSang() {
        return this.laSpiderMan() && this.spiderSanSang;
    }

    public boolean kichHoatSkill() {
        if (!this.sanSang()) {
            return false;
        }
        this.soPhatDaTich = 0;
        this.spiderSanSang = false;
        this.spiderChoLuotMoi = false;
        this.daGuiNutSkill = false;
        return true;
    }

    public boolean canHienNutSkill() {
        if (!this.sanSang() || this.daGuiNutSkill) {
            return false;
        }
        this.daGuiNutSkill = true;
        return true;
    }

    public boolean sanSang(VXLChienBinh chienBinh, int soBanSaoUltron) {
        if (chienBinh == null || chienBinh.chet || chienBinh.daRoiTran
                || this.daDungSkillTrongLuot) {
            return false;
        }
        if (this.laSpiderMan()) {
            return this.spiderSanSang;
        }
        if (this.laThor()) {
            return this.soLuotDaTich >= SO_LUOT_THOR_CAN_TICH;
        }
        if (this.laLoki()) {
            return !this.daDungSkillMotLan && chienBinh.hp > 0
                    && (long)chienBinh.hp * 2L <= chienBinh.mauToiDa;
        }
        if (this.laHawkeye()) {
            return this.soLuotDaTich >= SO_LUOT_HAWKEYE_CAN_TICH;
        }
        if (this.laUltron()) {
            return this.soLuotDaBatDau >= LUOT_ULTRON_BAT_DAU_NHAN_BAN
                    && soBanSaoUltron < GIOI_HAN_BAN_SAO_ULTRON;
        }
        return false;
    }

    public byte layMaMenuSkill(VXLChienBinh chienBinh, int soBanSaoUltron) {
        if (!this.sanSang(chienBinh, soBanSaoUltron)) {
            return -1;
        }
        if (this.laLoki()) {
            return MENU_LOKI;
        }
        if (this.laUltron()) {
            return MENU_ULTRON;
        }
        if (this.laThor()) {
            return MENU_THOR;
        }
        if (this.laSpiderMan()) {
            return MENU_SPIDER_MAN;
        }
        return MENU_HAWKEYE;
    }

    public boolean canHienNutSkill(VXLChienBinh chienBinh, int soBanSaoUltron) {
        if (this.daGuiNutSkill || this.layMaMenuSkill(chienBinh, soBanSaoUltron) < 0) {
            return false;
        }
        this.daGuiNutSkill = true;
        return true;
    }

    public boolean kichHoatSkill(byte hanhDong, VXLChienBinh chienBinh,
            int soBanSaoUltron) {
        if (!this.sanSang(chienBinh, soBanSaoUltron)
                || !this.hanhDongHopLe(hanhDong)) {
            return false;
        }
        this.daDungSkillTrongLuot = true;
        this.daGuiNutSkill = false;
        if (this.laSpiderMan()) {
            this.soPhatDaTich = 0;
            this.spiderSanSang = false;
            this.spiderChoLuotMoi = false;
        } else if (this.laThor() || this.laHawkeye()) {
            this.soLuotDaTich = 0;
        } else if (this.laLoki()) {
            this.daDungSkillMotLan = true;
        }
        return true;
    }

    public byte layLoaiDan(byte loaiDanThuong, boolean dungSkill) {
        if (this.laSpiderMan()) {
            return dungSkill ? LOAI_DAN_WEB_PRISON : loaiDanThuong;
        }
        return loaiDanThuong;
    }

    public byte layAvengerDan(byte avengerMacDinh) {
        if (this.laLoki() && this.avengerSaoChep > 0) {
            return this.avengerSaoChep;
        }
        return avengerMacDinh;
    }

    public short layVuKhi(short vuKhiMacDinh) {
        if (this.laLoki() && this.avengerSaoChep == 0 && this.vuKhiSaoChep >= 0) {
            return this.vuKhiSaoChep;
        }
        return vuKhiMacDinh;
    }

    public void saoChepLoki(VXLChienBinh nguoiDung, VXLChienBinh mucTieu) {
        if (!this.laLoki() || nguoiDung == null || mucTieu == null) {
            return;
        }
        VXLKyNangAvenger kyNangMucTieu = mucTieu.kyNangAvenger;
        this.avengerSaoChep = kyNangMucTieu != null
                ? kyNangMucTieu.layAvengerDan(mucTieu.avengerDan)
                : mucTieu.avengerDan;
        this.vuKhiSaoChep = kyNangMucTieu != null
                ? kyNangMucTieu.layVuKhi(mucTieu.maVuKhi)
                : mucTieu.maVuKhi;
        nguoiDung.tanCong = Math.max(1, mucTieu.tanCong);
        nguoiDung.mauToiDa = Math.max(1, mucTieu.mauToiDa);
        nguoiDung.hp = Math.max(1, Math.min(nguoiDung.mauToiDa, mucTieu.hp));
    }

    public int ghiNhanPhatBan(boolean phatBanThuong) {
        if (!this.laSpiderMan()) {
            return -1;
        }
        if (phatBanThuong) {
            this.soPhatDaTich = Math.min(SO_PHAT_SPIDER_MAN_CAN_TICH,
                    this.soPhatDaTich + 1);
            if (this.soPhatDaTich >= SO_PHAT_SPIDER_MAN_CAN_TICH
                    && !this.spiderSanSang) {
                this.spiderChoLuotMoi = true;
            }
        }
        return this.layPhanTramTichLuy();
    }

    public void apDungHieuUngTrungDan(VXLKetQuaDan ketQua, VXLChienBinh mucTieu) {
        if (this.laSpiderMan()) {
            this.apDungWebPrison(ketQua, mucTieu);
        }
    }

    private int layPhanTramTichLuy() {
        return this.soPhatDaTich * 100 / SO_PHAT_SPIDER_MAN_CAN_TICH;
    }

    private void apDungWebPrison(VXLKetQuaDan ketQua, VXLChienBinh mucTieu) {
        if (ketQua != null && mucTieu != null
                && Byte.toUnsignedInt(ketQua.loaiDan)
                        == Byte.toUnsignedInt(LOAI_DAN_WEB_PRISON)) {
            mucTieu.luotMacTo = Math.max(mucTieu.luotMacTo, LUOT_MAC_TO_SPIDER_MAN);
        }
    }

    private boolean hanhDongHopLe(byte hanhDong) {
        if (this.laLoki()) {
            return hanhDong == HANH_DONG_LOKI;
        }
        if (this.laHawkeye() || this.laSpiderMan()) {
            return hanhDong == HANH_DONG_HAWKEYE;
        }
        if (this.laUltron()) {
            return hanhDong == HANH_DONG_ULTRON;
        }
        return this.laThor() && hanhDong == HANH_DONG_THOR;
    }
}
