package com.vxl.clan;

import com.vxl.chien.VXLCauHinhVatPhamChienDau;

public final class VXLHieuUngClan {
    public static final VXLHieuUngClan KHONG_CO = new VXLHieuUngClan();

    int sinhLuc;
    int hoaLuc;
    int phongThu;
    int mayMan;
    int dongDoi;
    int tocDo;
    int tatCa;
    int danChongTang;
    int danSungTruong;
    int danTieuLien;
    int danChuoi;
    int danHoaCai;

    public int phanTramSinhLuc() {
        return sinhLuc + dongDoi + tatCa;
    }

    public int phanTramHoaLuc() {
        return hoaLuc + dongDoi + tatCa;
    }

    public int phanTramPhongThu() {
        return phongThu + dongDoi + tatCa;
    }

    public int phanTramTocDo() {
        return tocDo;
    }

    public int phanTramSatThuong(short maVuKhi) {
        int theoSung = switch (VXLCauHinhVatPhamChienDau.layNhomSungClientTheoVuKhi(maVuKhi)) {
            case 0 -> laSungChongTang(maVuKhi) ? danChongTang : 0;
            case 1 -> danSungTruong;
            case 2 -> danHoaCai;
            case 3 -> danChuoi;
            case 5 -> danTieuLien;
            default -> 0;
        };
        return mayMan + theoSung;
    }

    private static boolean laSungChongTang(short maVuKhi) {
        return switch (maVuKhi) {
            case 5, 31, 57, 134, 135 -> true;
            default -> false;
        };
    }
}
