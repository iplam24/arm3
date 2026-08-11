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

    private static final byte LOAI_DAN_WEB_PRISON = 56;
    private static final int SO_PHAT_SPIDER_MAN_CAN_TICH = 3;
    private static final int LUOT_MAC_TO_SPIDER_MAN = 1;

    private final byte maAvenger;
    private int soPhatDaTich;
    private boolean daGuiNutSkill;

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

    public boolean sanSang() {
        return this.laSpiderMan() && this.soPhatDaTich >= SO_PHAT_SPIDER_MAN_CAN_TICH;
    }

    public boolean kichHoatSkill() {
        if (!this.sanSang()) {
            return false;
        }
        this.soPhatDaTich = 0;
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

    public byte layLoaiDan(byte loaiDanThuong, boolean dungSkill) {
        if (this.laSpiderMan()) {
            return dungSkill ? LOAI_DAN_WEB_PRISON : loaiDanThuong;
        }
        return loaiDanThuong;
    }

    public int ghiNhanPhatBan(boolean phatBanThuong) {
        if (!this.laSpiderMan()) {
            return -1;
        }
        if (phatBanThuong) {
            this.soPhatDaTich = Math.min(SO_PHAT_SPIDER_MAN_CAN_TICH,
                    this.soPhatDaTich + 1);
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

    private boolean laSpiderMan() {
        return this.maAvenger == MA_SPIDER_MAN;
    }
}
