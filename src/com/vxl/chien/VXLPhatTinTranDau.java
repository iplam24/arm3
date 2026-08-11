package com.vxl.chien;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

final class VXLPhatTinTranDau {
    private final VXLChienBinh[] chienBinhs;

    VXLPhatTinTranDau(VXLChienBinh[] chienBinhs) {
        this.chienBinhs = chienBinhs;
    }

    void guiBatDau(byte maBanDo, byte maNen) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiBatDauDau(maBanDo, this.chienBinhs, maNen));
    }

    void guiManHinhChienDau() {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap());
    }

    void guiThemBoss(VXLChienBinh boss, short head, short leg, short body,
            short hat, short wing, byte loaiBoss) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiThemBossDau(
                boss, head, leg, body, hat, wing, loaiBoss));
    }

    void guiLuotTiepTheo(byte luotHienTai, short x, short y, int[] napDan,
            long[] thuTuHanhDongNapDan, byte giay) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiLuotDauTiep(
                luotHienTai, x, y, this.chienBinhs, napDan, thuTuHanhDongNapDan, giay));
    }

    void guiGio(byte gioX, byte gioY) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiGioLuyenTap(gioX, gioY));
    }

    void guiDiChuyen(VXLChienBinh daDiChuyen) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiDiChuyenDau(daDiChuyen.chiSo, daDiChuyen.x, daDiChuyen.y));
    }

    void guiCapNhatXY(VXLChienBinh daDiChuyen) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiCapNhatXYLuyenTap(daDiChuyen.chiSo, daDiChuyen.x, daDiChuyen.y));
    }

    void guiPhatBan(VXLChienBinh nguoiBan, VXLKetQuaDan ketQua, byte soPhat) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiKetQuaBanDau(nguoiBan.chiSo, ketQua, soPhat));
    }

    void guiMau(VXLChienBinh mucTieu) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiCapNhatMauDau(
                mucTieu.chiSo, mucTieu.hp, mucTieu.mauToiDa, mucTieu.chet ? (byte)2 : (byte)0));
    }

    void guiNo(VXLChienBinh chienBinh) {
        this.guiTungNguoi(nguoiNhan -> nguoiNhan.nguoiChoi.dichVu.guiNoDau(
                chienBinh.chiSo, (byte)chienBinh.no));
    }

    void guiDungVatPham(VXLChienBinh nguoiDung, byte maHieuUng, short icon) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(nguoiDung.chiSo, maHieuUng, icon));
    }

    void guiDoiSung(VXLChienBinh nguoiDoi, short iconVuKhiCu) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiDoiSungLuyenTap(
                nguoiDoi.chiSo, nguoiDoi.maVuKhi,
                chienBinh == nguoiDoi ? iconVuKhiCu : (short)-1));
    }

    private void guiTungNguoi(HanhDongGui hanhDong) {
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh == null || !chienBinh.coPhien() || chienBinh.daRoiTran) {
                continue;
            }
            try {
                hanhDong.gui(chienBinh);
            }
            catch (IOException ex) {
                Logger.getLogger(VXLPhatTinTranDau.class.getName()).log(Level.FINE, "Không thể gửi packet trận cho " + chienBinh.ten, ex);
            }
        }
    }

    @FunctionalInterface
    private interface HanhDongGui {
        void gui(VXLChienBinh chienBinh) throws IOException;
    }
}
