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

    void guiDongBoMauBanDau() {
        int soOClient = Math.min(8, this.chienBinhs.length);
        for (int i = 0; i < soOClient; i++) {
            VXLChienBinh chienBinh = this.chienBinhs[i];
            if (chienBinh != null) {
                this.guiMau(chienBinh);
            }
        }
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

    void guiKyNangBossRua(VXLChienBinh boss, short[] cacX, short[] cacY,
            byte[][] cacMucTieu, short[][] cacXMoi, short[][] cacYMoi) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiKyNangBossRua(
                boss.chiSo, cacX, cacY, cacMucTieu, cacXMoi, cacYMoi));
    }

    void guiKyNangBossRongCan(VXLChienBinh boss, VXLChienBinh mucTieu) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiKyNangBossRongCan(
                boss.chiSo, mucTieu.chiSo));
    }

    void guiKyNangBossRongGapTha(VXLChienBinh boss, VXLChienBinh mucTieu,
            short xCu, short yCu, short xTha, short yTha) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiKyNangBossRongGapTha(
                boss.chiSo, xCu, yCu, mucTieu.chiSo, xTha, yTha));
    }
    void guiDiChuyen(VXLChienBinh daDiChuyen) {
        this.guiTungNguoi(chienBinh -> {
            if (chienBinh != daDiChuyen) {
                chienBinh.nguoiChoi.dichVu.guiDiChuyenDau(daDiChuyen.chiSo, daDiChuyen.x, daDiChuyen.y);
            }
        });
    }

    void guiCapNhatXY(VXLChienBinh daDiChuyen) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiCapNhatXYLuyenTap(daDiChuyen.chiSo, daDiChuyen.x, daDiChuyen.y));
    }

    void guiPhatBan(VXLChienBinh nguoiBan, VXLKetQuaDan ketQua, byte soPhat) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiKetQuaBanDau(nguoiBan.chiSo, ketQua, soPhat));
    }

    void guiMau(VXLChienBinh mucTieu) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiCapNhatMauDau(
                mucTieu.chiSo, mucTieu.hp, mucTieu.mauToiDa,
                mucTieu.chet ? (mucTieu.laBanSaoUltron() ? (byte)3 : (byte)2)
                        : (byte)0));
    }

    void guiNo(VXLChienBinh chienBinh) {
        this.guiTungNguoi(nguoiNhan -> nguoiNhan.nguoiChoi.dichVu.guiNoDau(
                chienBinh.chiSo, (byte)chienBinh.no));
    }

    void guiDungVatPham(VXLChienBinh nguoiDung, byte maHieuUng, short icon) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(nguoiDung.chiSo, maHieuUng, icon));
    }

    void guiKetThucTangHinh(VXLChienBinh chienBinh) {
        this.guiTungNguoi(nguoiNhan -> nguoiNhan.nguoiChoi.dichVu.guiKetThucTangHinh(
                chienBinh.chiSo));
    }

    void guiTrangThaiMu(VXLChienBinh chienBinh, boolean biMu) {
        this.guiTungNguoi(nguoiNhan -> nguoiNhan.nguoiChoi.dichVu.guiTrangThaiMu(
                chienBinh.chiSo, biMu));
    }

    void guiTrangThaiBatDong(VXLChienBinh chienBinh, boolean batDong) {
        this.guiTungNguoi(nguoiNhan -> nguoiNhan.nguoiChoi.dichVu.guiTrangThaiBatDong(
                chienBinh.chiSo, batDong));
    }

    void guiDatBomHenGio(byte maBom, short x, short y, byte luotConLai) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiDatBomHenGio(
                maBom, x, y, luotConLai));
    }

    void guiNoBomHenGio(byte maBom) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiNoBomHenGio(maBom));
    }

    void guiTienDoGoBom(byte maBom, byte phanTram) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiTienDoGoBom(
                maBom, phanTram));
    }

    void guiGoBomHenGio(byte maBom) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiGoBomHenGio(maBom));
    }

    void guiCapNhatBomHenGio(byte maBom, byte luotConLai) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiCapNhatBomHenGio(
                maBom, luotConLai));
    }

    void guiDoiSung(VXLChienBinh nguoiDoi, short iconVuKhiCu) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiDoiSungLuyenTap(
                nguoiDoi.chiSo, nguoiDoi.maVuKhi,
                chienBinh == nguoiDoi ? iconVuKhiCu : (short)-1));
    }

    void guiLokiGiaDang(VXLChienBinh nguoiDung, VXLChienBinh mucTieu) {
        byte avengerHieuLuc = nguoiDung.kyNangAvenger != null
                ? nguoiDung.kyNangAvenger.layAvengerDan(nguoiDung.avengerDan)
                : nguoiDung.avengerDan;
        short vuKhiHieuLuc = nguoiDung.kyNangAvenger != null
                ? nguoiDung.kyNangAvenger.layVuKhi(nguoiDung.maVuKhi)
                : nguoiDung.maVuKhi;
        byte loaiDanHieuLuc = avengerHieuLuc > 0
                ? VXLCauHinhVatPhamChienDau.layLoaiDanTheoAvenger(avengerHieuLuc, (byte)0)
                : VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(vuKhiHieuLuc, (byte)0);
        byte nhomSungHieuLuc = avengerHieuLuc > 0
                ? (byte)(avengerHieuLuc + 10)
                : VXLCauHinhVatPhamChienDau.layNhomSungClientTheoVuKhi(vuKhiHieuLuc);
        this.guiTungNguoi(chienBinh -> {
            if (mucTieu.nguoiChoi == null) {
                chienBinh.nguoiChoi.dichVu.guiLokiGiaDang(
                        nguoiDung.chiSo, mucTieu.chiSo,
                        loaiDanHieuLuc, nhomSungHieuLuc);
                return;
            }
            chienBinh.nguoiChoi.dichVu.guiLokiGiaDang(
                    nguoiDung.chiSo, mucTieu.chiSo,
                    mucTieu.nguoiChoi.head, mucTieu.nguoiChoi.leg,
                    mucTieu.nguoiChoi.body, vuKhiHieuLuc,
                    mucTieu.nguoiChoi.hat, mucTieu.nguoiChoi.wing,
                    avengerHieuLuc, mucTieu.hp, mucTieu.mauToiDa,
                    loaiDanHieuLuc, nhomSungHieuLuc);
        });
    }

    void guiDiemRoiSkill(byte chiSoNguoiDung, byte loaiDan, short[] cacX,
            short[] cacY) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiSkillHawkeye(
                chiSoNguoiDung, loaiDan, cacX, cacY));
    }

    void guiThemBanSaoUltron(VXLChienBinh banSao, byte chiSoChu) {
        this.guiTungNguoi(chienBinh -> chienBinh.nguoiChoi.dichVu.guiThemBanSaoUltron(
                banSao, chiSoChu));
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
