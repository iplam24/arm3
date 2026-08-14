package com.vxl.chien;

public record VXLPhanThuongTranDau(int kinhNghiem, int vang, int ngoc, int cupThayDoi) {
    public static VXLPhanThuongTranDau tinh(VXLChienBinh chienBinh, byte ketQua,
            boolean cheDoBoss, boolean cheDoCamTu) {
        int kinhNghiem = 0;
        int vang = 0;
        int cupThayDoi = cheDoBoss ? 0 : -1;
        if (!chienBinh.daRoiTran) {
            if (cheDoBoss) {
                if (ketQua == 1) {
                    kinhNghiem = 450;
                    vang = 1800;
                } else {
                    kinhNghiem = 80;
                    vang = 300;
                }
                kinhNghiem += chienBinh.haGucTrongTran * 55
                        + chienBinh.tongSatThuong / 18;
                vang += chienBinh.haGucTrongTran * 180
                        + chienBinh.tongSatThuong / 8;
            } else {
                if (ketQua == 1) {
                    kinhNghiem = 150;
                    vang = 800;
                    cupThayDoi = 3;
                } else if (ketQua == 2) {
                    kinhNghiem = 80;
                    vang = 400;
                    cupThayDoi = 0;
                } else {
                    kinhNghiem = 50;
                    vang = 250;
                }
                kinhNghiem += chienBinh.haGucTrongTran * 35
                        + chienBinh.tongSatThuong / 25;
                vang += chienBinh.haGucTrongTran * 120
                        + chienBinh.tongSatThuong / 10;
            }
        }
        if (cheDoCamTu && chienBinh.haCamTuTrongTran > 0) {
            int soCamTu = chienBinh.haCamTuTrongTran;
            kinhNghiem += 200 * soCamTu + 5 * soCamTu * (soCamTu - 1);
            vang += soCamTu * 100;
        }
        return new VXLPhanThuongTranDau(kinhNghiem, vang, 0, cupThayDoi);
    }
}