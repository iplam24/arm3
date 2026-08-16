package com.vxl.chien;

import com.vxl.mohinh.VXLNguoiChoi;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

final class VXLXuLyKetThucTranDau {
    private static final byte KET_QUA_THUA = 0;
    private static final byte KET_QUA_THANG = 1;
    private static final byte KET_QUA_HOA = 2;
    private final boolean cheDoBoss;
    private final boolean cheDoCamTu;
    private final VXLChienBinh[] chienBinhs;

    VXLXuLyKetThucTranDau(boolean cheDoBoss, boolean cheDoCamTu, VXLChienBinh[] chienBinhs) {
        this.cheDoBoss = cheDoBoss;
        this.cheDoCamTu = cheDoCamTu;
        this.chienBinhs = chienBinhs;
    }

    void quyetToanTatCa(VXLChienBinh nguoiThang, byte ketQuaDoi) {
        String bangKetQua = this.taoBangKetQua();
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh == null || chienBinh.bot || chienBinh.daQuyetToan) {
                continue;
            }
            byte ketQua;
            if (chienBinh.daRoiTran) {
                ketQua = KET_QUA_THUA;
            } else if (this.cheDoBoss) {
                ketQua = ketQuaDoi;
            } else if (ketQuaDoi == KET_QUA_HOA) {
                ketQua = KET_QUA_HOA;
            } else {
                ketQua = chienBinh == nguoiThang ? KET_QUA_THANG : KET_QUA_THUA;
            }
            this.quyetToan(chienBinh, ketQua, bangKetQua);
        }
    }

    void quyetToanRoiTran(VXLChienBinh chienBinh) {
        this.quyetToan(chienBinh, KET_QUA_THUA, "Người chơi đã rời trận.");
    }

    private void quyetToan(VXLChienBinh chienBinh, byte ketQua, String bangKetQua) {
        if (chienBinh == null || chienBinh.bot || chienBinh.daQuyetToan) {
            return;
        }
        chienBinh.daQuyetToan = true;
        VXLNguoiChoi nguoiChoi = chienBinh.nguoiChoi;
        VXLPhanThuongTranDau phanThuong = VXLPhanThuongTranDau.tinh(chienBinh, ketQua, this.cheDoBoss, this.cheDoCamTu);
        if (this.cheDoCamTu && chienBinh.haCamTuTrongTran > 0) {
            nguoiChoi.ghiNhanHaCamTu(chienBinh.haCamTuTrongTran);
        }
        if (this.cheDoBoss && ketQua == KET_QUA_THANG && !chienBinh.daRoiTran) {
            nguoiChoi.ghiNhanHaBoss(1);
            nguoiChoi.towerElo = Math.max(0, nguoiChoi.towerElo + 3);
        } else if (this.cheDoCamTu && ketQua == KET_QUA_THANG && !chienBinh.daRoiTran) {
            nguoiChoi.towerElo = Math.max(0, nguoiChoi.towerElo + 3);
        }
        if (!this.cheDoBoss && ketQua == KET_QUA_THANG && !chienBinh.daRoiTran) {
            nguoiChoi.ghiNhanThangPvp();
        }

        int kinhNghiemThucNhan = nguoiChoi.congKinhNghiem(phanThuong.kinhNghiem());
        nguoiChoi.vang = Math.max(0, nguoiChoi.vang + phanThuong.vang());
        nguoiChoi.ngoc = Math.max(0, nguoiChoi.ngoc + phanThuong.ngoc());
        nguoiChoi.cup = Math.max(0, nguoiChoi.cup + phanThuong.cupThayDoi());
        String tenKetQua = ketQua == KET_QUA_THANG ? "THẮNG" : (ketQua == KET_QUA_HOA ? "HÒA" : "THUA");
        String thongBao = "KẾT QUẢ: " + tenKetQua
                + "\nHạ gục: " + chienBinh.haGucTrongTran
                + " | Sát thương: " + chienBinh.tongSatThuong
                + (this.cheDoCamTu ? " | Cảm tử: " + chienBinh.haCamTuTrongTran : "")
                + "\nNhận: " + kinhNghiemThucNhan + " EXP, " + phanThuong.vang() + " vàng"
                + "\n\n" + bangKetQua;
        try {
            if (chienBinh.coPhien()) {
                if (!this.cheDoBoss && !this.cheDoCamTu) {
                    nguoiChoi.dichVu.guiKetThucDau(ketQua, kinhNghiemThucNhan, phanThuong.vang(), phanThuong.ngoc());
                }
                nguoiChoi.dichVu.capNhatCup((byte)0, nguoiChoi.cup);
                nguoiChoi.dichVu.capNhatKDVaKDA();
                nguoiChoi.dichVu.capNhat();
            }
        }
        catch (IOException ex) {
            Logger.getLogger(VXLXuLyKetThucTranDau.class.getName()).log(Level.FINE, "Không thể gửi kết quả trận cho " + nguoiChoi.ten, ex);
        }
        nguoiChoi.flushCache();
    }

    private String taoBangKetQua() {
        StringBuilder bang = new StringBuilder("BẢNG KẾT QUẢ");
        int thuTu = 1;
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh == null || chienBinh.laBanSaoUltron()) {
                continue;
            }
            bang.append('\n').append(thuTu++).append(". ").append(chienBinh.ten)
                    .append(" | HP ").append(chienBinh.hp).append('/').append(chienBinh.mauToiDa)
                    .append(" | Hạ ").append(chienBinh.haGucTrongTran)
                    .append(" | ST ").append(chienBinh.tongSatThuong);
        }
        return bang.toString();
    }
}
