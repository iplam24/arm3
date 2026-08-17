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

        // Tăng thêm thưởng EXP và Vàng từ chỉ số May mắn và Đồng đội
        int bonusMayManExp = Math.min(100, chienBinh.mayMan / 2);
        int bonusMayManVang = Math.min(150, chienBinh.mayMan);
        int bonusDongDoiExp = Math.min(50, chienBinh.dongDoi / 4);
        int bonusDongDoiVang = Math.min(100, chienBinh.dongDoi / 2);

        kinhNghiem = (int)((long)kinhNghiem * (100L + bonusMayManExp + bonusDongDoiExp) / 100L);
        vang = (int)((long)vang * (100L + bonusMayManVang + bonusDongDoiVang) / 100L);

        return new VXLPhanThuongTranDau(kinhNghiem, vang, 0, cupThayDoi);
    }
}