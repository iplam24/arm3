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

    void guiLuotTiepTheo(byte luotHienTai, short x, short y, byte giay) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiLuotDauTiep(luotHienTai, x, y, this.chienBinhs, giay));
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
                mucTieu.chiSo, mucTieu.hp, mucTieu.phanTramMau(), mucTieu.chet ? (byte)2 : (byte)0));
    }

    void guiDungVatPham(VXLChienBinh nguoiDung, byte maHieuUng, short icon) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(nguoiDung.chiSo, maHieuUng, icon));
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