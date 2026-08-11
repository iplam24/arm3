package com.vxl.luyentap;

import com.alibaba.fastjson2.JSONObject;
import com.vxl.chien.VXLCauHinhVatPhamChienDau;
import com.vxl.chien.VXLChienBinh;
import com.vxl.chien.VXLHeThongDan;
import com.vxl.chien.VXLHoSoDan;
import com.vxl.chien.VXLTinhSatThuong;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLVatPham;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VXLQuanLyLuyenTap {
    private static final int SO_PHIEN_QUAN = 1;
    private static final byte MA_BAN_DO_LUYEN_TAP = 0;
    private static final short DAU_PHIEN_QUAN = 59;
    private static final short QUAN_PHIEN_QUAN = 157;
    private static final short AO_PHIEN_QUAN = 158;
    private static final short NON_PHIEN_QUAN = 159;
    private static final short CANH_PHIEN_QUAN = 160;
    private static final short VU_KHI_PHIEN_QUAN = 27;
    private static final int KHOANG_BAY_PHIEN_QUAN = 280;
    private static final int LE_BIEN_BAY_PHIEN_QUAN = 48;
    private static final int BUOC_TIM_DIEM_DAP = 12;
    private static final int NUA_RONG_THAN_PHIEN_QUAN = 7;
    private static final int CHIEU_CAO_THAN_PHIEN_QUAN = 34;
    private static final long TRE_CHUAN_BI_DAN_BOT = 450L;
    private static final long TRE_BAY_PHIEN_QUAN_TOI_THIEU = 800L;
    private static final long TRE_BAY_PHIEN_QUAN_TOI_DA = 2000L;
    private static final long THOI_GIAN_MOI_DIEM_DAN_BOT = 22L;
    private static final long TRE_SAU_VA_CHAM_DAN_BOT = 700L;
    private static final long TRE_KET_THUC_DAN_BOT_TOI_THIEU = 1700L;
    private static final long TRE_KET_THUC_DAN_BOT_TOI_DA = 6000L;
    private static final long TRE_NGAM_PHIEN_QUAN_TOI_THIEU = 1500L;
    private static final long TRE_NGAM_PHIEN_QUAN_TOI_DA = 2400L;
    private static final long TRE_XU_LY_VA_CHAM_NGUOI_CHOI = 400L;
    private static final long TRE_HIEN_KET_QUA_SAU_HIEU_UNG = 2200L;
    private static final long TRE_TU_DONG_THOAT_KET_QUA = 12000L;
    private static final int TI_LE_TRUNG_PHIEN_QUAN_TOI_THIEU = 30;
    private static final int TI_LE_TRUNG_PHIEN_QUAN_TOI_DA = 52;
    private static final int TI_LE_SIEU_CAO_PHIEN_QUAN_TOI_THIEU = 35;
    private static final int TI_LE_SIEU_CAO_PHIEN_QUAN_TOI_DA = 70;
    private static final int KHOANG_TRUOT_DAT_TOI_THIEU = 24;
    private static final int KHOANG_TRUOT_DAT_TOI_DA = 54;
    private static final int NO_TANG_MOI_LAN_DOI_LUOT = 10;
    private static final ScheduledExecutorService BO_LAP_LICH = Executors.newSingleThreadScheduledExecutor(tacVu -> {
        Thread thread = new Thread(tacVu, "vxl-phien-quan-luyen-tap");
        thread.setDaemon(true);
        return thread;
    });
    private final VXLNguoiChoi nguoiChoi;
    private final int[] mauPhienQuan = new int[SO_PHIEN_QUAN];
    private final int[] satThuongDuKienPhienQuan = new int[SO_PHIEN_QUAN];
    private final short[] phienQuanX = new short[SO_PHIEN_QUAN];
    private final short[] phienQuanY = new short[SO_PHIEN_QUAN];
    private final boolean[] phienQuanDaChet = new boolean[SO_PHIEN_QUAN];
    private final VXLTinhDuongDanLuyenTap tinhDuongDan;
    private VXLChienBinh chiSoNguoiChoi;
    private VXLChiSoPhienQuan chiSoPhienQuan;
    private volatile int soPhienQuanDaHa;
    private int capPhienQuanHienTai = 1;
    private int mauToiDaPhienQuan = 1000;
    private int mauNguoiChoi = 100;
    private int tanCongPhienQuan;
    private int giapPhienQuan;
    private int khienPhienQuan;
    private short nguoiChoiX = 220;
    private short nguoiChoiY = 300;
    private int phienQuanDangBiBan = -1;
    private int luotPhienQuan = -1;
    private long lanBanCuoi;
    private boolean dangChoKetThucPhatBan;
    private boolean duKienTrungPhienQuan;
    private boolean daGuiLuotDau;
    private boolean phienQuanDangHoatDong;
    private boolean choKyNangDacBiet;
    private boolean kyNangDacBietPhatToi;
    private boolean phatBanKyNangDacBiet;
    private boolean phatBanSieuCao;
    private boolean danManhPhienQuan;
    private byte lucPhatBanNguoiChoi = 10;
    private byte loaiDanPhatBanNguoiChoi;
    private int soVienTrungPhienQuan;
    private int satThuongNguocNguoiChoi;
    private int noKyNangDacBiet;
    private short vuKhiPhienQuanHienTai = VU_KHI_PHIEN_QUAN;
    private short[] boVuKhiPhienQuanTrongTran = new short[]{VU_KHI_PHIEN_QUAN};
    private byte gioX;
    private byte gioY;
    private int soLuotPhienQuan;
    private ScheduledFuture<?> tacVuPhienQuan;
    private ScheduledFuture<?> tacVuKetThucPhienQuan;
    private ScheduledFuture<?> tacVuSatThuongNguoiChoi;
    private ScheduledFuture<?> tacVuDuPhongPhatBan;
    private ScheduledFuture<?> tacVuKetQuaTran;
    private ScheduledFuture<?> tacVuThoatKetQua;

    public VXLQuanLyLuyenTap(VXLNguoiChoi nguoiChoi) {
        this.nguoiChoi = nguoiChoi;
        this.tinhDuongDan = new VXLTinhDuongDanLuyenTap(this.phienQuanX, this.phienQuanY, this.phienQuanDaChet);
    }

    public void tai(JSONObject duLieu) {
        Object giaTri = duLieu != null ? duLieu.get("trainingRebelDefeated") : null;
        int daHa = 0;
        if (giaTri != null) {
            try {
                daHa = Integer.parseInt(giaTri.toString());
            }
            catch (NumberFormatException ex) {
                daHa = 0;
            }
        } else if (duLieu != null) {
            Object capLuyenTapCu = duLieu.get("trainingSuccess");
            if (capLuyenTapCu != null) {
                try {
                    daHa = Math.max(0, Integer.parseInt(capLuyenTapCu.toString()) - 1);
                }
                catch (NumberFormatException ex) {
                    daHa = 0;
                }
            }
        }
        this.soPhienQuanDaHa = Math.max(0, Math.min(VXLCauHinhPhienQuan.CAP_TOI_DA, daHa));
        this.nguoiChoi.trainingSuccess = (byte)Math.min(VXLCauHinhPhienQuan.CAP_TOI_DA, this.soPhienQuanDaHa + 1);
    }

    public void ghiVao(JSONObject duLieu) {
        duLieu.put("trainingRebelDefeated", this.soPhienQuanDaHa);
    }

    public synchronized void vao() {
        try {
            if (this.soPhienQuanDaHa >= VXLCauHinhPhienQuan.CAP_TOI_DA) {
                this.nguoiChoi.startOKDlg2("Bạn đã hoàn thành toàn bộ 40 Phiến quân luyện tập.");
                return;
            }
            this.dungTacVu();
            short vuKhiNguoiChoi = this.nguoiChoi.wp > 0 ? this.nguoiChoi.wp : 5;
            this.nguoiChoi.wp = vuKhiNguoiChoi;
            this.capPhienQuanHienTai = VXLCauHinhPhienQuan.gioiHanCap(this.soPhienQuanDaHa + 1);
            byte maBanDo = VXLCauHinhPhienQuan.layBanDoChoTran(this.capPhienQuanHienTai);
            this.tinhDuongDan.datBanDo(maBanDo);
            this.chiSoPhienQuan = VXLCauHinhPhienQuan.taoChiSo(this.capPhienQuanHienTai);
            this.vuKhiPhienQuanHienTai = VXLCauHinhPhienQuan.chonVuKhiChoTran(
                    this.capPhienQuanHienTai);
            this.boVuKhiPhienQuanTrongTran = new short[]{this.vuKhiPhienQuanHienTai};
            this.mauToiDaPhienQuan = this.chiSoPhienQuan.mauToiDa;
            this.tanCongPhienQuan = this.chiSoPhienQuan.tanCong;
            this.giapPhienQuan = this.chiSoPhienQuan.giap;
            this.nguoiChoiX = 220;
            this.nguoiChoiY = this.tinhDuongDan.layBanDo().timViTriDat(this.nguoiChoiX, (short)250);
            this.chiSoNguoiChoi = new VXLChienBinh(this.nguoiChoi, (byte)0, this.nguoiChoiX, this.nguoiChoiY);
            this.nguoiChoi.trainingSuccess = (byte)this.capPhienQuanHienTai;
            this.nguoiChoi.isReady = true;
            this.nguoiChoi.chiSo = 0;
            this.nguoiChoi.pointSeat = 0;
            this.nguoiChoi.inTraining = true;
            this.mauNguoiChoi = this.chiSoNguoiChoi.mauToiDa;
            this.dangChoKetThucPhatBan = false;
            this.duKienTrungPhienQuan = false;
            this.daGuiLuotDau = false;
            this.phienQuanDangHoatDong = false;
            this.choKyNangDacBiet = false;
            this.khienPhienQuan = 0;
            this.danManhPhienQuan = false;
            this.lucPhatBanNguoiChoi = 10;
            this.noKyNangDacBiet = 0;
            this.soLuotPhienQuan = 0;
            this.taoGioMoi();
            this.phienQuanDangBiBan = -1;
            this.luotPhienQuan = -1;
            java.util.Arrays.fill(this.satThuongDuKienPhienQuan, 0);
            this.mauPhienQuan[0] = this.mauToiDaPhienQuan;
            this.phienQuanX[0] = 600;
            this.phienQuanY[0] = this.tinhDuongDan.layBanDo().timViTriDat(this.phienQuanX[0], (short)250);
            this.phienQuanDaChet[0] = false;

            short vuKhiPhienQuan = this.vuKhiPhienQuanHienTai;
            this.nguoiChoi.dichVu.guiThongTinLuyenTap();
            this.nguoiChoi.dichVu.guiChonBanDoLuyenTap(maBanDo);
            this.nguoiChoi.dichVu.guiNguoiChoiLuyenTap((byte)0, this.nguoiChoi.ma, this.nguoiChoi.ten,
                    this.nguoiChoi.head, this.nguoiChoi.leg, this.nguoiChoi.body, this.nguoiChoi.hat,
                    this.nguoiChoi.wing, vuKhiNguoiChoi, this.nguoiChoi.avenger, this.nguoiChoi.ma);
            this.nguoiChoi.dichVu.guiNguoiChoiLuyenTap((byte)1, -12000 - this.capPhienQuanHienTai,
                    VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), DAU_PHIEN_QUAN,
                    QUAN_PHIEN_QUAN, AO_PHIEN_QUAN, NON_PHIEN_QUAN,
                    CANH_PHIEN_QUAN, vuKhiPhienQuan, (byte)0, this.nguoiChoi.ma);
            this.nguoiChoi.dichVu.guiBatDauLuyenTap(maBanDo, vuKhiNguoiChoi,
                    this.mauNguoiChoi, this.chiSoNguoiChoi.layTamDiChuyen(50),
                    this.phienQuanX, this.phienQuanY, this.mauPhienQuan,
                    this.boVuKhiPhienQuanTrongTran);
        }
        catch (Exception ex) {
            this.resetTrangThai();
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Không thể bắt đầu luyện tập.", ex);
        }
    }

    public void diChuyen(VXLTinNhan tinNhan) throws IOException {
        short x = tinNhan.boDoc().readShort();
        short y = tinNhan.boDoc().readShort();
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        this.nguoiChoiX = this.tinhDuongDan.gioiHan(x, 0, 1200);
        this.nguoiChoiY = this.tinhDuongDan.gioiHan(y, 0, 700);
        this.nguoiChoi.dichVu.guiCapNhatXYLuyenTap((byte)0, this.nguoiChoiX, this.nguoiChoiY);
    }

    public void ban(VXLTinNhan tinNhan) throws IOException {
        if (!this.nguoiChoi.inTraining || this.phienQuanDangHoatDong) {
            return;
        }
        if (this.chiSoNguoiChoi == null || !this.chiSoNguoiChoi.daNapDan()) {
            return;
        }
        long hienTai = System.currentTimeMillis();
        if (hienTai - this.lanBanCuoi < 250L) {
            return;
        }
        this.lanBanCuoi = hienTai;
        this.huyTacVuPhienQuan();
        byte loaiDanGui = tinNhan.boDoc().readByte();
        short x = tinNhan.boDoc().readShort();
        short y = tinNhan.boDoc().readShort();
        short goc = tinNhan.boDoc().readShort();
        byte luc = tinNhan.boDoc().readByte();
        byte lucTach = 0;
        if (this.laDanDoi(loaiDanGui)) {
            lucTach = tinNhan.boDoc().readByte();
        }
        byte soPhat = (byte)Math.max(1, Math.min(2,
                Byte.toUnsignedInt(tinNhan.boDoc().readByte())));
        luc = (byte)Math.max(10, Math.min(30, luc));
        if (this.laDanDoi(loaiDanGui)) {
            lucTach = (byte)Math.max(4, Math.min(30, Byte.toUnsignedInt(lucTach)));
        }
        this.lucPhatBanNguoiChoi = luc;
        this.nguoiChoiX = this.tinhDuongDan.gioiHan(x, 0, 1200);
        this.nguoiChoiY = this.tinhDuongDan.gioiHan(y, 0, 700);
        byte avengerDan = this.chiSoNguoiChoi.avengerDan;
        byte loaiDan = avengerDan > 0
                ? VXLCauHinhVatPhamChienDau.layLoaiDanTheoAvenger(
                        avengerDan, loaiDanGui)
                : VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                        this.chiSoNguoiChoi.maVuKhi, loaiDanGui);
        this.chiSoNguoiChoi.batDauNapDan();
        if (avengerDan > 0) {
            soPhat = 1;
        }
        boolean kyNangDacBiet = this.kyNangDacBietPhatToi;
        byte chiMang = (byte)(kyNangDacBiet ? 1 : 0);
        this.kyNangDacBietPhatToi = false;
        VXLHeThongDan.KetQuaPhatBan phatBan = this.tinhDuongDan.taoPhatBanNguoiChoi(
                this.nguoiChoiX, this.nguoiChoiY, goc, luc, lucTach, loaiDan, chiMang,
                avengerDan);
        VXLCauHinhVatPhamChienDau.DiemSieuCao diemSieuCao =
                VXLCauHinhVatPhamChienDau.timDiemSieuCao(this.nguoiChoiY,
                        phatBan.duongX, phatBan.duongY, loaiDan, avengerDan);
        this.phienQuanDangBiBan = this.layMucTieuTrungNhieuNhat(
                phatBan.layTatCaMucTieuTrung());
        boolean sieuCaoTrungMucTieu = diemSieuCao.kichHoat()
                && this.phienQuanDangBiBan >= 0;
        this.loaiDanPhatBanNguoiChoi = loaiDan;
        this.phatBanKyNangDacBiet = kyNangDacBiet;
        this.phatBanSieuCao = sieuCaoTrungMucTieu;
        int satThuongCoBan = VXLTinhSatThuong.tinhPhatBan(this.chiSoNguoiChoi.tanCong,
                luc, VXLCauHinhPhienQuan.HE_SO_DAN_THUONG);
        satThuongCoBan = Math.max(1, satThuongCoBan
                * VXLCauHinhVatPhamChienDau.layHeSoSatThuongTrangThai(
                        this.phatBanSieuCao, this.phatBanKyNangDacBiet) / 100);
        int satThuongMoiVien = VXLCauHinhVatPhamChienDau.tinhSatThuongMoiVien(
                satThuongCoBan, loaiDan, chiMang, avengerDan);
        int tranSatThuong = phatBan.truotRaNgoaiBanDo ? 0 : satThuongCoBan
                * VXLCauHinhVatPhamChienDau.layTranPhanTramSatThuong(
                        loaiDan, avengerDan) / 100;
        java.util.Arrays.fill(this.satThuongDuKienPhienQuan, 0);
        int mucTieuSatThuongCaoNhat = -1;
        int satThuongCaoNhat = 0;
        for (int i = 0; i < SO_PHIEN_QUAN; i++) {
            if (this.phienQuanDaChet[i]) {
                continue;
            }
            int soVienTrung = phatBan.demSoVienTrung(i);
            int satThuongTrucTiep = Math.min(tranSatThuong,
                    satThuongMoiVien * soVienTrung);
            int satThuongNo = VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                    phatBan.duongX, phatBan.duongY, this.phienQuanX[i], this.phienQuanY[i],
                    loaiDan, avengerDan, satThuongMoiVien, tranSatThuong);
            int satThuongDuKien = Math.max(satThuongTrucTiep, satThuongNo);
            this.satThuongDuKienPhienQuan[i] = satThuongDuKien;
            if (satThuongDuKien > satThuongCaoNhat) {
                satThuongCaoNhat = satThuongDuKien;
                mucTieuSatThuongCaoNhat = i;
            }
        }
        this.phienQuanDangBiBan = mucTieuSatThuongCaoNhat;
        this.soVienTrungPhienQuan = this.phienQuanDangBiBan >= 0
                ? phatBan.demSoVienTrung(this.phienQuanDangBiBan) : 0;
        this.satThuongNguocNguoiChoi = phatBan.truotRaNgoaiBanDo ? 0
                : VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                        phatBan.duongX, phatBan.duongY, this.nguoiChoiX, this.nguoiChoiY,
                        loaiDan, avengerDan, satThuongMoiVien, tranSatThuong);
        this.duKienTrungPhienQuan = mucTieuSatThuongCaoNhat >= 0;
        this.dangChoKetThucPhatBan = true;
        this.choKyNangDacBiet = false;
        System.out.println(String.format("[TRAINING-FIRE] %s bắn | LoaiDan=%d | Goc=%d | Luc=%d | Pos=(%d,%d) | Points=%d | Target=%s",
                this.nguoiChoi.ten, loaiDan, goc, luc, this.nguoiChoiX, this.nguoiChoiY,
                phatBan.duongX[0].length,
                this.duKienTrungPhienQuan ? ("Phiến quân #" + (this.phienQuanDangBiBan + 1)) : "Trượt/Map"));

        this.nguoiChoi.dichVu.guiKetQuaBanLuyenTapNangCao(chiMang, (byte)0,
                this.layLoaiDanAnToan(loaiDan),
                this.nguoiChoiX, this.nguoiChoiY, goc, luc,
                this.layChiSoTachClient(loaiDan, lucTach, phatBan),
                phatBan.duongX, phatBan.duongY, soPhat,
                (byte)(sieuCaoTrungMucTieu ? 1 : 0),
                sieuCaoTrungMucTieu ? diemSieuCao.x() : (short)-1,
                sieuCaoTrungMucTieu ? diemSieuCao.y() : (short)-1);
        this.capNhatViTriHulkSauPhatBan(loaiDan, avengerDan, phatBan);
        this.lapDuPhongPhatBan(this.duKienTrungPhienQuan, loaiDan == 80 ? 3000L : 10000L);
    }

    private void capNhatViTriHulkSauPhatBan(byte loaiDan, byte avenger,
            VXLHeThongDan.KetQuaPhatBan phatBan) {
        if (phatBan == null || phatBan.duongX.length == 0 || phatBan.duongY.length == 0
                || phatBan.duongX[0].length == 0 || phatBan.duongY[0].length == 0
                || VXLCauHinhVatPhamChienDau.layHoSoDan(loaiDan,
                        avenger).kieuBan() != VXLHoSoDan.KieuBan.NHAN_VAT_LAO) {
            return;
        }
        int chiSoCuoi = Math.min(phatBan.duongX[0].length, phatBan.duongY[0].length) - 1;
        this.nguoiChoiX = phatBan.duongX[0][chiSoCuoi];
        this.nguoiChoiY = phatBan.duongY[0][chiSoCuoi];
        if (this.chiSoNguoiChoi != null) {
            this.chiSoNguoiChoi.x = this.nguoiChoiX;
            this.chiSoNguoiChoi.y = this.nguoiChoiY;
        }
        VXLQuanLyMayChu.log("[TRAINING-FIRE] Hulk landed player=" + this.nguoiChoi.ten
                + " x=" + this.nguoiChoiX + " y=" + this.nguoiChoiY);
    }

    public synchronized void dungVatPham(VXLTinNhan tinNhan) throws IOException {
        int vatPham = Byte.toUnsignedInt(tinNhan.boDoc().readByte());
        while (tinNhan.boDoc().available() > 0) {
            tinNhan.boDoc().readByte();
        }
        if (vatPham != 100 || !this.nguoiChoi.inTraining || this.phienQuanDangHoatDong
                || this.dangChoKetThucPhatBan || this.kyNangDacBietPhatToi
                || this.noKyNangDacBiet < 100) {
            return;
        }
        this.noKyNangDacBiet = 0;
        this.kyNangDacBietPhatToi = true;
        this.nguoiChoi.dichVu.guiNoDau((byte)0, (byte)0);
        this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)0, (byte)100, (short)0);
    }

    public synchronized void doiSung(VXLTinNhan tinNhan) throws IOException {
        int chiSoBalo = tinNhan.boDoc().readUnsignedByte();
        while (tinNhan.boDoc().available() > 0) {
            tinNhan.boDoc().readByte();
        }
        if (!this.nguoiChoi.inTraining || this.chiSoNguoiChoi.avengerDan > 0
                || this.phienQuanDangHoatDong || this.dangChoKetThucPhatBan) {
            return;
        }
        VXLVatPham vuKhi = this.nguoiChoi.layVuKhiTrongBalo(chiSoBalo);
        if (vuKhi == null || this.chiSoNguoiChoi == null
                || this.chiSoNguoiChoi.maVuKhi == vuKhi.mau.part) {
            return;
        }
        VXLVatPham vuKhiCu = this.nguoiChoi.doiVuKhiTrongBalo(chiSoBalo);
        if (vuKhiCu == null) {
            return;
        }
        this.chiSoNguoiChoi.maVuKhi = this.nguoiChoi.wp;
        this.chiSoNguoiChoi.capNhatTanCongTheoTrangBi();
        int thoiGianNapDan = this.chiSoNguoiChoi.batDauNapDan();
        this.nguoiChoi.dichVu.guiTuiDo();
        this.nguoiChoi.dichVu.guiDoTrenNguoi();
        this.nguoiChoi.dichVu.guiBalo();
        this.nguoiChoi.dichVu.doiTrangBi();
        this.nguoiChoi.flushCache();
        this.nguoiChoi.dichVu.guiDoiSungLuyenTap((byte)0, this.nguoiChoi.wp,
                vuKhiCu.mau.iconID, thoiGianNapDan);
    }

    public synchronized void xuLyFocusSkill(VXLTinNhan tinNhan) throws IOException {
        byte hanhDong = tinNhan.boDoc().readByte();
        int chiSoMucTieu = Byte.toUnsignedInt(tinNhan.boDoc().readByte());
        int chiSoPhienQuan = chiSoMucTieu - 1;
        if (hanhDong != 1 || !this.nguoiChoi.inTraining || !this.choKyNangDacBiet
                || this.phienQuanDangHoatDong || this.dangChoKetThucPhatBan
                || chiSoPhienQuan < 0 || chiSoPhienQuan >= SO_PHIEN_QUAN
                || this.phienQuanDaChet[chiSoPhienQuan]) {
            return;
        }
        this.choKyNangDacBiet = false;
        this.phienQuanDangHoatDong = true;
        short mucTieuX = this.phienQuanX[chiSoPhienQuan];
        short mucTieuY = this.phienQuanY[chiSoPhienQuan];
        short[] xs = new short[]{(short)(mucTieuX - 20), (short)(mucTieuX - 5),
                (short)(mucTieuX + 5), (short)(mucTieuX + 20)};
        short[] ys = new short[]{mucTieuY, mucTieuY, mucTieuY, mucTieuY};
        this.nguoiChoi.dichVu.guiSkillHawkeye((byte)0, (byte)9, xs, ys);

        int satThuongMoiMui = VXLTinhSatThuong.tinhSauGiap(20 + this.chiSoNguoiChoi.tanCong,
                this.giapPhienQuan);
        satThuongMoiMui = Math.max(8, satThuongMoiMui);
        int satThuong = satThuongMoiMui * 4;
        if (this.khienPhienQuan > 0) {
            int hapThu = Math.min(this.khienPhienQuan, satThuong);
            this.khienPhienQuan -= hapThu;
            satThuong -= hapThu;
        }
        this.mauPhienQuan[chiSoPhienQuan] = Math.max(0,
                this.mauPhienQuan[chiSoPhienQuan] - satThuong);
        this.phienQuanDaChet[chiSoPhienQuan] = this.mauPhienQuan[chiSoPhienQuan] <= 0;
        this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)(chiSoPhienQuan + 1),
                this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan,
                this.phienQuanDaChet[chiSoPhienQuan] ? (byte)2 : (byte)0);
        this.phienQuanDangHoatDong = false;
        if (this.daHaPhienQuan()) {
            this.hoanThanhPhienQuan();
        } else {
            this.batDauLuotPhienQuan();
        }
    }

    public void xuLyVaCham(VXLTinNhan tinNhan) throws IOException {
        int soVuNo = tinNhan.boDoc().readUnsignedByte();
        if (soVuNo > 32) {
            throw new IllegalArgumentException("Số vụ nổ không hợp lệ: " + soVuNo);
        }
        for (int i = 0; i < soVuNo; i++) {
            tinNhan.boDoc().readInt();
            tinNhan.boDoc().readInt();
        }
        if (!this.nguoiChoi.inTraining || !this.dangChoKetThucPhatBan) {
            return;
        }
        this.lapDuPhongPhatBan(this.duKienTrungPhienQuan, TRE_XU_LY_VA_CHAM_NGUOI_CHOI);
    }

    public void yeuCauDatLaiHo() throws IOException {
        if (this.nguoiChoi.inTraining) {
            this.nguoiChoi.dichVu.guiDatLaiHoLuyenTap();
        }
    }

    public void sanSang() throws IOException {
        this.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
        if (this.nguoiChoi.inTraining && !this.daGuiLuotDau) {
            this.daGuiLuotDau = true;
            this.nguoiChoi.dichVu.guiGioLuyenTap(this.gioX, this.gioY);
            this.nguoiChoi.dichVu.guiLuotLuyenTapTiep((byte)0, this.nguoiChoiX, this.nguoiChoiY);
        }
    }

    public synchronized void dong() {
        this.resetTrangThai();
        this.dungTacVu();
    }

    public synchronized boolean roiNeuDangLuyenTap() {
        if (!this.nguoiChoi.inTraining && this.tacVuKetQuaTran == null
                && this.tacVuThoatKetQua == null) {
            return false;
        }
        this.resetTrangThai();
        this.dungTacVu();
        return true;
    }

    private synchronized void lapDuPhongPhatBan(boolean trung, long tre) {
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
        }
        this.tacVuDuPhongPhatBan = BO_LAP_LICH.schedule(() -> {
            try {
                this.ketThucPhatBanNguoiChoi(trung);
            }
            catch (Exception ex) {
                Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi xử lý dự phòng phát bắn luyện tập.", ex);
            }
        }, tre, TimeUnit.MILLISECONDS);
    }

    private synchronized void ketThucPhatBanNguoiChoi(boolean trung) throws IOException {
        if (!this.nguoiChoi.inTraining || !this.dangChoKetThucPhatBan) {
            return;
        }
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
            this.tacVuDuPhongPhatBan = null;
        }
        if (this.apDungSatThuongNguocNguoiChoi()) {
            this.dangChoKetThucPhatBan = false;
            this.thuaLuyenTap(TRE_HIEN_KET_QUA_SAU_HIEU_UNG);
            return;
        }
        if (trung) {
            for (int chiSoPhienQuan = 0; chiSoPhienQuan < SO_PHIEN_QUAN; chiSoPhienQuan++) {
                int satThuongGoc = this.satThuongDuKienPhienQuan[chiSoPhienQuan];
                if (satThuongGoc <= 0 || this.phienQuanDaChet[chiSoPhienQuan]) {
                    continue;
                }
                int satThuong = VXLTinhSatThuong.tinhSauGiap(satThuongGoc, this.giapPhienQuan);
                if (this.khienPhienQuan > 0) {
                    int hapThu = Math.min(this.khienPhienQuan, satThuong);
                    this.khienPhienQuan -= hapThu;
                    satThuong -= hapThu;
                }
                int mauTruoc = this.mauPhienQuan[chiSoPhienQuan];
                this.mauPhienQuan[chiSoPhienQuan] = Math.max(0,
                        this.mauPhienQuan[chiSoPhienQuan] - satThuong);
                if (this.mauPhienQuan[chiSoPhienQuan] <= 0) {
                    this.mauPhienQuan[chiSoPhienQuan] = 0;
                    this.phienQuanDaChet[chiSoPhienQuan] = true;
                }

                System.out.println(String.format("[TRAINING-HIT] %s bắn trúng %s | Sát thương gốc=%d | Giáp=%d | Sát thương thực=%d | HP: %d -> %d/%d",
                        this.nguoiChoi.ten, VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai),
                        satThuongGoc, this.giapPhienQuan, satThuong, mauTruoc,
                        this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan));

                this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)(chiSoPhienQuan + 1),
                        this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan,
                        this.phienQuanDaChet[chiSoPhienQuan] ? (byte)2 : (byte)0);
            }
        }
        this.dangChoKetThucPhatBan = false;
        this.duKienTrungPhienQuan = false;
        this.phienQuanDangBiBan = -1;
        this.soVienTrungPhienQuan = 0;
        java.util.Arrays.fill(this.satThuongDuKienPhienQuan, 0);
        this.phatBanKyNangDacBiet = false;
        this.phatBanSieuCao = false;
        if (this.daHaPhienQuan()) {
            this.hoanThanhPhienQuan();
            return;
        }
        this.batDauLuotPhienQuan();
    }

    private void batDauLuotPhienQuan() throws IOException {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        int chiSoPhienQuan = this.tinhDuongDan.timBotSongGanNhat(this.nguoiChoiX, this.nguoiChoiY);
        if (chiSoPhienQuan < 0) {
            this.hoanThanhPhienQuan();
            return;
        }
        this.tangNoTheoDoiLuot();
        this.nguoiChoi.dichVu.guiLuotLuyenTapTiep((byte)(chiSoPhienQuan + 1),
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]);
        this.lapLichPhienQuanBan(this.tinhTreNgamPhienQuan());
    }

    private long tinhTreNgamPhienQuan() {
        return ThreadLocalRandom.current().nextLong(TRE_NGAM_PHIEN_QUAN_TOI_THIEU,
                TRE_NGAM_PHIEN_QUAN_TOI_DA + 1L);
    }

    private synchronized void lapLichPhienQuanBan(long tre) {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        this.huyTacVuPhienQuan();
        this.tacVuPhienQuan = BO_LAP_LICH.schedule(this::choPhienQuanBan, tre, TimeUnit.MILLISECONDS);
    }

    private synchronized void huyTacVuPhienQuan() {
        if (this.tacVuPhienQuan != null) {
            this.tacVuPhienQuan.cancel(false);
            this.tacVuPhienQuan = null;
        }
    }

    private synchronized void dungTacVu() {
        this.huyTacVuPhienQuan();
        if (this.tacVuKetThucPhienQuan != null) {
            this.tacVuKetThucPhienQuan.cancel(false);
            this.tacVuKetThucPhienQuan = null;
        }
        if (this.tacVuSatThuongNguoiChoi != null) {
            this.tacVuSatThuongNguoiChoi.cancel(false);
            this.tacVuSatThuongNguoiChoi = null;
        }
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
            this.tacVuDuPhongPhatBan = null;
        }
        if (this.tacVuKetQuaTran != null) {
            this.tacVuKetQuaTran.cancel(false);
            this.tacVuKetQuaTran = null;
        }
        if (this.tacVuThoatKetQua != null) {
            this.tacVuThoatKetQua.cancel(false);
            this.tacVuThoatKetQua = null;
        }
        this.phienQuanDangHoatDong = false;
    }

    private void choPhienQuanBan() {
        try {
            synchronized (this) {
                this.tacVuPhienQuan = null;
                if (!this.nguoiChoi.inTraining || this.dangChoKetThucPhatBan
                        || this.phienQuanDangHoatDong) {
                    return;
                }
                int chiSoPhienQuan = this.layPhienQuanTiepTheo();
                if (chiSoPhienQuan < 0) {
                    this.hoanThanhPhienQuan();
                    return;
                }
                this.phienQuanDangHoatDong = true;
                long treBay = this.phienQuanDungVatPhamNeuCan(chiSoPhienQuan);
                if (treBay > 0L) {
                    this.tacVuPhienQuan = BO_LAP_LICH.schedule(
                            () -> this.phienQuanBanSauKhiBay(chiSoPhienQuan),
                            treBay, TimeUnit.MILLISECONDS);
                    return;
                }
                this.phienQuanBanVaLapLichKetThuc(chiSoPhienQuan);
            }
        }
        catch (Exception ex) {
            this.phienQuanDangHoatDong = false;
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                    "Lỗi Phiến quân luyện tập bắn trả.", ex);
        }
    }

    private void phienQuanBanSauKhiBay(int chiSoPhienQuan) {
        try {
            synchronized (this) {
                this.tacVuPhienQuan = null;
                if (!this.nguoiChoi.inTraining || this.dangChoKetThucPhatBan) {
                    this.phienQuanDangHoatDong = false;
                    return;
                }
                this.phienQuanBanVaLapLichKetThuc(chiSoPhienQuan);
            }
        }
        catch (Exception ex) {
            this.phienQuanDangHoatDong = false;
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                    "Lỗi Phiến quân bắn sau khi bay.", ex);
        }
    }

    private void phienQuanBanVaLapLichKetThuc(int chiSoPhienQuan) throws IOException {
        long treKetThucLuot = this.phienQuanBanTra(chiSoPhienQuan);
        if (!this.nguoiChoi.inTraining) {
            this.phienQuanDangHoatDong = false;
            return;
        }
        if (this.tacVuKetThucPhienQuan != null) {
            this.tacVuKetThucPhienQuan.cancel(false);
        }
        this.tacVuKetThucPhienQuan = BO_LAP_LICH.schedule(this::ketThucLuotPhienQuan,
                treKetThucLuot, TimeUnit.MILLISECONDS);
    }

    private void ketThucLuotPhienQuan() {
        try {
            synchronized (this) {
                this.tacVuKetThucPhienQuan = null;
                if (!this.nguoiChoi.inTraining) {
                    this.phienQuanDangHoatDong = false;
                    return;
                }
                this.phienQuanDangHoatDong = false;
                if (this.mauNguoiChoi <= 0) {
                    this.thuaLuyenTap(TRE_HIEN_KET_QUA_SAU_HIEU_UNG);
                    return;
                }
                this.taoGioMoi();
                this.tangNoTheoDoiLuot();
                this.nguoiChoi.dichVu.guiGioLuyenTap(this.gioX, this.gioY);
                this.nguoiChoi.dichVu.guiLuotLuyenTapTiep((byte)0, this.nguoiChoiX, this.nguoiChoiY);
                if (this.nguoiChoi.avenger == 7 && this.soLuotPhienQuan > 0
                        && this.soLuotPhienQuan % 4 == 0) {
                    this.choKyNangDacBiet = true;
                    this.nguoiChoi.dichVu.guiYeuCauSkill((byte)1);
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi kết thúc lượt Phiến quân luyện tập.", ex);
        }
    }

    private long phienQuanBanTra(int chiSoPhienQuan) throws IOException {
        byte loaiDan = VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                this.vuKhiPhienQuanHienTai, (byte)0);
        boolean duKienNgamTrung = this.phienQuanDuKienNgamTrung();
        boolean uuTienSieuCao = duKienNgamTrung && this.phienQuanUuTienSieuCao();
        short[] diemNgam = this.taoDiemNgamPhienQuan(chiSoPhienQuan, duKienNgamTrung);
        VXLTinhDuongDanLuyenTap.CachBanBot cachBan = this.tinhDuongDan.timCachBanBot(
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan],
                diemNgam[0], diemNgam[1], loaiDan, chiSoPhienQuan, uuTienSieuCao);
        short goc = cachBan.goc;
        byte luc = cachBan.luc;
        VXLHeThongDan.KetQuaPhatBan phatBan = cachBan.phatBan;
        int soVienTrungNguoiChoi = 0;
        if (duKienNgamTrung) {
            for (int i = 0; i < phatBan.duongX.length && i < phatBan.duongY.length; i++) {
                if (Byte.toUnsignedInt(loaiDan) == 17 && phatBan.duongX.length > 1 && i == 0) {
                    continue;
                }
                if (!this.tinhDuongDan.duongDanTrungNguoiChoi(phatBan.duongX[i],
                        phatBan.duongY[i], this.nguoiChoiX, this.nguoiChoiY)) {
                    continue;
                }
                short[][] duongDaDung = this.tinhDuongDan.dungDuongDanTaiNguoiChoi(
                        phatBan.duongX[i], phatBan.duongY[i], this.nguoiChoiX,
                        this.nguoiChoiY);
                phatBan.duongX[i] = duongDaDung[0];
                phatBan.duongY[i] = duongDaDung[1];
                soVienTrungNguoiChoi++;
            }
        }
        boolean trungTrucTiepNguoiChoi = soVienTrungNguoiChoi > 0;
        VXLCauHinhVatPhamChienDau.DiemSieuCao diemSieuCao =
                VXLCauHinhVatPhamChienDau.timDiemSieuCao(
                        this.phienQuanY[chiSoPhienQuan], phatBan.duongX,
                        phatBan.duongY, loaiDan, (byte)0);
        boolean sieuCaoTrungNguoiChoi = trungTrucTiepNguoiChoi && diemSieuCao.kichHoat();

        System.out.println(String.format("[TRAINING-BOT-FIRE] Phiến quân %s bắn trả | Goc=%d | Luc=%d | Pos=(%d,%d)",
                VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), goc, luc,
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]));

        this.nguoiChoi.dichVu.guiKetQuaBanLuyenTapNangCao((byte)0,
                (byte)(chiSoPhienQuan + 1), loaiDan,
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan], goc, luc,
                this.layChiSoTachClient(loaiDan, luc, phatBan),
                phatBan.duongX, phatBan.duongY, (byte)1,
                (byte)(sieuCaoTrungNguoiChoi ? 1 : 0),
                sieuCaoTrungNguoiChoi ? diemSieuCao.x() : (short)-1,
                sieuCaoTrungNguoiChoi ? diemSieuCao.y() : (short)-1);
        int soDiemDan = this.layDoDaiQuyDaoLonNhat(phatBan.duongX, phatBan.duongY);
        long treKetThucDan = this.tinhTreKetThucDanBot(soDiemDan);
        boolean danManh = this.danManhPhienQuan;
        this.danManhPhienQuan = false;
        int heSoDan = danManh
                ? VXLCauHinhPhienQuan.HE_SO_DAN_MANH
                : VXLCauHinhPhienQuan.HE_SO_DAN_THUONG;
        int satThuongCoBan = VXLTinhSatThuong.tinhPhatBan(
                this.tanCongPhienQuan, luc, heSoDan);
        satThuongCoBan = Math.max(1, satThuongCoBan
                * VXLCauHinhVatPhamChienDau.layHeSoSatThuongTrangThai(
                        sieuCaoTrungNguoiChoi, false) / 100);
        int satThuongMoiVien = VXLCauHinhVatPhamChienDau.tinhSatThuongMoiVien(
                satThuongCoBan, loaiDan, (byte)0, (byte)0);
        int tranSatThuong = phatBan.truotRaNgoaiBanDo ? 0 : satThuongCoBan
                * VXLCauHinhVatPhamChienDau.layTranPhanTramSatThuong(loaiDan, (byte)0) / 100;
        int satThuongTrucTiep = Math.min(tranSatThuong,
                satThuongMoiVien * soVienTrungNguoiChoi);
        int satThuongNo = VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                phatBan.duongX, phatBan.duongY, this.nguoiChoiX, this.nguoiChoiY,
                loaiDan, (byte)0, satThuongMoiVien, tranSatThuong);
        int satThuongGoc = Math.max(satThuongTrucTiep, satThuongNo);
        if (satThuongGoc > 0) {
            int satThuong = VXLTinhSatThuong.tinhSauGiap(
                    satThuongGoc, this.chiSoNguoiChoi.giap);
            this.lapLichSatThuongNguoiChoi(satThuongGoc, satThuong,
                    this.tinhTreVaChamDanBot(soDiemDan));
        }
        return treKetThucDan;
    }

    private synchronized void lapLichSatThuongNguoiChoi(int satThuongGoc, int satThuong,
            long tre) {
        if (this.tacVuSatThuongNguoiChoi != null) {
            this.tacVuSatThuongNguoiChoi.cancel(false);
        }
        this.tacVuSatThuongNguoiChoi = BO_LAP_LICH.schedule(() -> {
            synchronized (this) {
                this.tacVuSatThuongNguoiChoi = null;
                if (!this.nguoiChoi.inTraining || this.mauNguoiChoi <= 0) {
                    return;
                }
                try {
                    int mauTruoc = this.mauNguoiChoi;
                    this.mauNguoiChoi = Math.max(0, this.mauNguoiChoi - satThuong);
                    boolean nguoiChoiGucNga = this.mauNguoiChoi <= 0;
                    System.out.println(String.format("[TRAINING-BOT-HIT] %s hit %s | base=%d | armor=%d | damage=%d | HP: %d -> %d/%d",
                            VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), this.nguoiChoi.ten,
                            satThuongGoc, this.chiSoNguoiChoi.giap, satThuong, mauTruoc,
                            this.mauNguoiChoi, this.chiSoNguoiChoi.mauToiDa));
                    this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0, this.mauNguoiChoi,
                            this.chiSoNguoiChoi.mauToiDa, nguoiChoiGucNga ? (byte)2 : (byte)0);
                }
                catch (Exception ex) {
                    Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                            "Khong the ap dung sat thuong cua Phien quan.", ex);
                }
            }
        }, Math.max(0L, tre), TimeUnit.MILLISECONDS);
    }

    private long tinhTreVaChamDanBot(int soDiem) {
        return TRE_CHUAN_BI_DAN_BOT + Math.max(1, soDiem) * THOI_GIAN_MOI_DIEM_DAN_BOT;
    }

    private long tinhTreKetThucDanBot(int soDiem) {
        long treVaCham = this.tinhTreVaChamDanBot(soDiem);
        long tre = treVaCham + TRE_SAU_VA_CHAM_DAN_BOT;
        return Math.max(TRE_KET_THUC_DAN_BOT_TOI_THIEU,
                Math.min(TRE_KET_THUC_DAN_BOT_TOI_DA, tre));
    }

    private boolean apDungSatThuongNguocNguoiChoi() throws IOException {
        int satThuongGoc = this.satThuongNguocNguoiChoi;
        this.satThuongNguocNguoiChoi = 0;
        if (satThuongGoc <= 0 || this.mauNguoiChoi <= 0) {
            return false;
        }
        int satThuong = VXLTinhSatThuong.tinhSauGiap(satThuongGoc,
                this.chiSoNguoiChoi.giap);
        if (this.chiSoNguoiChoi.khien > 0) {
            int hapThu = Math.min(this.chiSoNguoiChoi.khien, satThuong);
            this.chiSoNguoiChoi.khien -= hapThu;
            satThuong -= hapThu;
        }
        if (satThuong <= 0) {
            return false;
        }
        this.mauNguoiChoi = Math.max(0, this.mauNguoiChoi - satThuong);
        boolean gucNga = this.mauNguoiChoi <= 0;
        this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0, this.mauNguoiChoi,
                this.chiSoNguoiChoi.mauToiDa, gucNga ? (byte)2 : (byte)0);
        return gucNga;
    }

    private int layDoDaiQuyDaoLonNhat(short[][] cacDuongX, short[][] cacDuongY) {
        int lonNhat = 1;
        int soQuyDao = Math.min(cacDuongX.length, cacDuongY.length);
        for (int i = 0; i < soQuyDao; i++) {
            if (cacDuongX[i] == null || cacDuongY[i] == null) {
                continue;
            }
            lonNhat = Math.max(lonNhat,
                    Math.min(cacDuongX[i].length, cacDuongY[i].length));
        }
        return lonNhat;
    }

    private boolean phienQuanDuKienNgamTrung() {
        int tiLeTrung = Math.min(TI_LE_TRUNG_PHIEN_QUAN_TOI_DA,
                TI_LE_TRUNG_PHIEN_QUAN_TOI_THIEU + this.capPhienQuanHienTai * 11 / 20);
        return ThreadLocalRandom.current().nextInt(100) < tiLeTrung;
    }

    private boolean phienQuanUuTienSieuCao() {
        int tiLeSieuCao = Math.min(TI_LE_SIEU_CAO_PHIEN_QUAN_TOI_DA,
                TI_LE_SIEU_CAO_PHIEN_QUAN_TOI_THIEU
                + this.capPhienQuanHienTai * 7 / 8);
        return ThreadLocalRandom.current().nextInt(100) < tiLeSieuCao;
    }

    private void tangNoTheoDoiLuot() throws IOException {
        int truoc = this.noKyNangDacBiet;
        this.noKyNangDacBiet = Math.min(100,
                this.noKyNangDacBiet + NO_TANG_MOI_LAN_DOI_LUOT);
        if (this.noKyNangDacBiet != truoc) {
            this.nguoiChoi.dichVu.guiNoDau((byte)0, (byte)this.noKyNangDacBiet);
        }
    }

    private void taoGioMoi() {
        ThreadLocalRandom ngauNhien = ThreadLocalRandom.current();
        byte gioMoiX = this.gioX;
        byte gioMoiY = this.gioY;
        for (int lan = 0; lan < 4 && gioMoiX == this.gioX && gioMoiY == this.gioY; lan++) {
            gioMoiX = (byte)ngauNhien.nextInt(-4, 5);
            gioMoiY = (byte)ngauNhien.nextInt(-1, 2);
        }
        if (gioMoiX == 0 && gioMoiY == 0) {
            gioMoiX = (byte)(ngauNhien.nextBoolean() ? 2 : -2);
        }
        this.gioX = gioMoiX;
        this.gioY = gioMoiY;
        this.tinhDuongDan.capNhatGio(this.gioX, this.gioY);
    }

    private short[] taoDiemNgamPhienQuan(int chiSoPhienQuan, boolean duKienNgamTrung) {
        ThreadLocalRandom ngauNhien = ThreadLocalRandom.current();
        int dichX = this.nguoiChoiX;
        int dichY = this.nguoiChoiY - ngauNhien.nextInt(12, 29);
        if (duKienNgamTrung) {
            dichX += ngauNhien.nextInt(-6, 7);
        } else {
            int huongVePhienQuan = Integer.compare(this.phienQuanX[chiSoPhienQuan], this.nguoiChoiX);
            if (huongVePhienQuan == 0) {
                huongVePhienQuan = ngauNhien.nextBoolean() ? 1 : -1;
            }
            int khoangTruot = ngauNhien.nextInt(KHOANG_TRUOT_DAT_TOI_THIEU,
                    KHOANG_TRUOT_DAT_TOI_DA + 1);
            dichX += huongVePhienQuan * khoangTruot;
        }
        int chieuRongBanDo = this.tinhDuongDan.layBanDo().getWidth();
        int chieuCaoBanDo = this.tinhDuongDan.layBanDo().getHeight();
        short xDaGioiHan = (short)Math.max(0, Math.min(chieuRongBanDo - 1, dichX));
        if (!duKienNgamTrung) {
            dichY = this.tinhDuongDan.layBanDo().timViTriDat(xDaGioiHan, this.nguoiChoiY);
        }
        return new short[]{xDaGioiHan, (short)Math.max(0, Math.min(chieuCaoBanDo - 1, dichY))};
    }

    private void thuaLuyenTap(long tre) {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        this.lapLichKetQuaTran((byte)0, 0, 0, 0, tre);
    }

    private int layPhienQuanTiepTheo() {
        for (int buoc = 1; buoc <= SO_PHIEN_QUAN; buoc++) {
            int chiSo = (this.luotPhienQuan + buoc + SO_PHIEN_QUAN) % SO_PHIEN_QUAN;
            if (!this.phienQuanDaChet[chiSo]) {
                this.luotPhienQuan = chiSo;
                return chiSo;
            }
        }
        return -1;
    }

    private boolean daHaPhienQuan() {
        for (boolean daChet : this.phienQuanDaChet) {
            if (!daChet) {
                return false;
            }
        }
        return true;
    }

    private void hoanThanhPhienQuan() throws IOException {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        int capDaHa = this.capPhienQuanHienTai;
        this.soPhienQuanDaHa = Math.max(this.soPhienQuanDaHa, capDaHa);
        this.nguoiChoi.trainingSuccess = (byte)Math.min(VXLCauHinhPhienQuan.CAP_TOI_DA, this.soPhienQuanDaHa + 1);
        int kinhNghiem = 100 + capDaHa * 15;
        int vang = VXLCauHinhPhienQuan.tinhPhanThuongVang(capDaHa);
        int kinhNghiemThucNhan = this.nguoiChoi.congKinhNghiem(kinhNghiem);
        this.nguoiChoi.vang += vang;

        this.nguoiChoi.dichVu.capNhat();
        this.nguoiChoi.flushCache();

        this.lapLichKetQuaTran((byte)1, kinhNghiemThucNhan, vang, 1, TRE_HIEN_KET_QUA_SAU_HIEU_UNG);
    }

    private synchronized void lapLichKetQuaTran(byte pheThang, int kinhNghiem, int vang, int ngoc,
            long tre) {
        this.resetTrangThai();
        this.dungTacVu();
        this.tacVuKetQuaTran = BO_LAP_LICH.schedule(() -> {
            synchronized (this) {
                this.tacVuKetQuaTran = null;
                try {
                    this.nguoiChoi.dichVu.guiKetThucDau(pheThang, kinhNghiem, vang, ngoc);
                }
                catch (Exception ex) {
                    Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                            "Lá»—i gá»­i káº¿t quáº£ luyá»‡n táº­p.", ex);
                }
                this.lapLichThoatKetQua();
            }
        }, Math.max(TRE_HIEN_KET_QUA_SAU_HIEU_UNG, tre), TimeUnit.MILLISECONDS);
    }

    private synchronized void lapLichThoatKetQua() {
        if (this.tacVuThoatKetQua != null) {
            this.tacVuThoatKetQua.cancel(false);
        }
        this.tacVuThoatKetQua = BO_LAP_LICH.schedule(() -> {
            synchronized (this) {
                this.tacVuThoatKetQua = null;
                try {
                    this.nguoiChoi.dichVu.guiThoatManHinhLuyenTap();
                }
                catch (Exception ex) {
                    Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.FINE,
                            "Khong the tu dong thoat man hinh ket qua luyen tap.", ex);
                }
            }
        }, TRE_TU_DONG_THOAT_KET_QUA, TimeUnit.MILLISECONDS);
    }

    private void diChuyenPhienQuan(int chiSoPhienQuan) throws IOException {
        int dichChuyen = chiSoPhienQuan % 2 == 0 ? 22 : -22;
        this.phienQuanX[chiSoPhienQuan] = this.tinhDuongDan.gioiHan(
                (short)(this.phienQuanX[chiSoPhienQuan] + dichChuyen), 80, 1120);
        this.phienQuanY[chiSoPhienQuan] = this.tinhDuongDan.layBanDo().timViTriDat(
                this.phienQuanX[chiSoPhienQuan], (short)250);
        this.nguoiChoi.dichVu.guiCapNhatXYLuyenTap((byte)(chiSoPhienQuan + 1),
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]);
    }

    private long phienQuanDungVatPhamNeuCan(int chiSoPhienQuan) throws IOException {
        this.soLuotPhienQuan++;
        int khoangCach = Math.abs(this.phienQuanX[chiSoPhienQuan] - this.nguoiChoiX);
        if (khoangCach <= 150 && this.soLuotPhienQuan % 2 == 0) {
            short[] diemDap = this.timDiemDapBayPhienQuan(chiSoPhienQuan);
            if (diemDap != null) {
                short xCu = this.phienQuanX[chiSoPhienQuan];
                short yCu = this.phienQuanY[chiSoPhienQuan];
                this.phienQuanX[chiSoPhienQuan] = diemDap[0];
                this.phienQuanY[chiSoPhienQuan] = diemDap[1];
                this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(
                        (byte)(chiSoPhienQuan + 1), (byte)20, (short)0);
                this.nguoiChoi.dichVu.guiBayLuyenTap((byte)(chiSoPhienQuan + 1),
                        this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]);
                VXLQuanLyMayChu.log("[TRAINING-ITEM] rebel fly from=(" + xCu + ',' + yCu
                        + ") to=(" + this.phienQuanX[chiSoPhienQuan] + ','
                        + this.phienQuanY[chiSoPhienQuan] + ") map="
                        + this.tinhDuongDan.layBanDo().getWidth() + 'x'
                        + this.tinhDuongDan.layBanDo().getHeight());
                return this.tinhTreBayPhienQuan(xCu, yCu,
                        this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]);
            }
        }
        int nguongHoiMau = this.mauToiDaPhienQuan * 60 / 100;
        if (this.mauPhienQuan[chiSoPhienQuan] <= nguongHoiMau && this.soLuotPhienQuan % 2 == 1) {
            this.mauPhienQuan[chiSoPhienQuan] = Math.min(this.mauToiDaPhienQuan,
                    this.mauPhienQuan[chiSoPhienQuan] + VXLCauHinhPhienQuan.tinhLuongHoiMau(this.chiSoPhienQuan));
            this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)(chiSoPhienQuan + 1), (byte)10, (short)0);
            this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)(chiSoPhienQuan + 1),
                    this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan, (byte)0);
            return 0L;
        }
        if (this.soLuotPhienQuan % 3 == 0) {
            this.khienPhienQuan = Math.min(this.mauToiDaPhienQuan / 2,
                    this.khienPhienQuan + VXLCauHinhPhienQuan.tinhLuongKhien(this.chiSoPhienQuan));
            this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)(chiSoPhienQuan + 1), (byte)0, (short)0);
            return 0L;
        }
        if (this.soLuotPhienQuan % 2 == 0) {
            this.danManhPhienQuan = true;
            this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)(chiSoPhienQuan + 1), (byte)5, (short)0);
        }
        return 0L;
    }

    private long tinhTreBayPhienQuan(int xBatDau, int yBatDau, int xDap, int yDap) {
        long treTheoKhoangCach = Math.round(Math.hypot(xDap - xBatDau,
                yDap - yBatDau) * 6D);
        return Math.max(TRE_BAY_PHIEN_QUAN_TOI_THIEU,
                Math.min(TRE_BAY_PHIEN_QUAN_TOI_DA, treTheoKhoangCach));
    }

    private short[] timDiemDapBayPhienQuan(int chiSoPhienQuan) {
        int chieuRong = this.tinhDuongDan.layBanDo().getWidth();
        int chieuCao = this.tinhDuongDan.layBanDo().getHeight();
        if (chieuRong < 40 || chieuCao < CHIEU_CAO_THAN_PHIEN_QUAN + 4) {
            return null;
        }
        int leBien = Math.min(LE_BIEN_BAY_PHIEN_QUAN,
                Math.max(12, (chieuRong - 1) / 4));
        int xNhoNhat = leBien;
        int xLonNhat = chieuRong - 1 - leBien;
        if (xLonNhat <= xNhoNhat) {
            return null;
        }
        int xHienTai = this.phienQuanX[chiSoPhienQuan];
        int yHienTai = this.phienQuanY[chiSoPhienQuan];
        int huong = xHienTai >= this.nguoiChoiX ? 1 : -1;
        int xMongMuon = Math.max(xNhoNhat, Math.min(xLonNhat,
                xHienTai + huong * KHOANG_BAY_PHIEN_QUAN));
        for (int khoangLech = 0; khoangLech <= chieuRong; khoangLech += BUOC_TIM_DIEM_DAP) {
            int xCungHuong = xMongMuon + huong * khoangLech;
            short[] diemCungHuong = this.kiemTraDiemDapBay(xHienTai, yHienTai,
                    xCungHuong, huong, xNhoNhat, xLonNhat);
            if (diemCungHuong != null) {
                return diemCungHuong;
            }
            if (khoangLech == 0) {
                continue;
            }
            int xNguocLai = xMongMuon - huong * khoangLech;
            short[] diemNguocLai = this.kiemTraDiemDapBay(xHienTai, yHienTai,
                    xNguocLai, huong, xNhoNhat, xLonNhat);
            if (diemNguocLai != null) {
                return diemNguocLai;
            }
        }
        return null;
    }

    private short[] kiemTraDiemDapBay(int xBatDau, int yBatDau, int xDap,
            int huong, int xNhoNhat, int xLonNhat) {
        if (xDap < xNhoNhat || xDap > xLonNhat
                || (xDap - xBatDau) * huong < 40) {
            return null;
        }
        short xDapNgan = (short)xDap;
        short yDap = this.tinhDuongDan.layBanDo().timViTriDat(xDapNgan, (short)0);
        if (!this.laDiemDapBayAnToan(xDap, yDap)
                || !this.duongBayKhongBiChan(xBatDau, yBatDau, xDap, yDap)) {
            return null;
        }
        return new short[]{xDapNgan, yDap};
    }

    private boolean duongBayKhongBiChan(int xBatDau, int yBatDau, int xDap, int yDap) {
        int dx = xDap - xBatDau;
        int dy = yDap - yBatDau;
        int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
        for (int buoc = 1; buoc <= soBuoc; buoc++) {
            int x = xBatDau + dx * buoc / soBuoc;
            int y = yBatDau + dy * buoc / soBuoc;
            if (this.thanPhienQuanChamDiaHinh(x, y)) {
                return false;
            }
        }
        return true;
    }

    private boolean laDiemDapBayAnToan(int x, int y) {
        int chieuCao = this.tinhDuongDan.layBanDo().getHeight();
        if (y < CHIEU_CAO_THAN_PHIEN_QUAN || y >= chieuCao - 2
                || !this.tinhDuongDan.layBanDo().coVaCham((short)x, (short)(y + 1))) {
            return false;
        }
        return !this.thanPhienQuanChamDiaHinh(x, y);
    }

    private boolean thanPhienQuanChamDiaHinh(int x, int y) {
        int[] cacLechX = new int[]{-NUA_RONG_THAN_PHIEN_QUAN, 0,
                NUA_RONG_THAN_PHIEN_QUAN};
        int[] cacLechY = new int[]{0, -CHIEU_CAO_THAN_PHIEN_QUAN / 2,
                -CHIEU_CAO_THAN_PHIEN_QUAN};
        for (int lechX : cacLechX) {
            for (int lechY : cacLechY) {
                if (this.tinhDuongDan.layBanDo().coVaCham(
                        (short)(x + lechX), (short)(y + lechY))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean laDanDoi(byte loaiDan) {
        return VXLCauHinhVatPhamChienDau.laDanTach(loaiDan);
    }

    private byte layChiSoTachClient(byte loaiDan, byte giaTriMacDinh,
            VXLHeThongDan.KetQuaPhatBan phatBan) {
        int loai = Byte.toUnsignedInt(loaiDan);
        if ((loai == 17 || loai == 19) && phatBan.chiSoTach >= 0) {
            return (byte)Math.max(4, Math.min(127, phatBan.chiSoTach));
        }
        return giaTriMacDinh;
    }

    private int layMucTieuTrungNhieuNhat(int[] cacMucTieu) {
        int[] soLanTrung = new int[SO_PHIEN_QUAN];
        int ketQua = -1;
        int nhieuNhat = 0;
        for (int mucTieu : cacMucTieu) {
            if (mucTieu < 0 || mucTieu >= SO_PHIEN_QUAN) {
                continue;
            }
            soLanTrung[mucTieu]++;
            if (soLanTrung[mucTieu] > nhieuNhat) {
                nhieuNhat = soLanTrung[mucTieu];
                ketQua = mucTieu;
            }
        }
        return ketQua;
    }

    private byte layLoaiDanAnToan(byte loaiDan) {
        switch (loaiDan) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 13:
            case 17:
            case 19:
            case 21:
            case 22:
            case 25:
            case 30:
            case 33:
            case 34:
            case 35:
            case 42:
            case 45:
            case 49:
            case 50:
            case 51:
            case 52:
            case 54:
            case 55:
            case 57:
            case 58:
            case 79:
            case 80:
            case 82:
            case 83:
                return loaiDan;
            default:
                return 0;
        }
    }

    private void resetTrangThai() {
        this.nguoiChoi.inTraining = false;
        this.nguoiChoi.isReady = false;
        this.nguoiChoi.chiSo = -1;
        this.nguoiChoi.pointSeat = 0;
        this.dangChoKetThucPhatBan = false;
        this.duKienTrungPhienQuan = false;
        this.soVienTrungPhienQuan = 0;
        java.util.Arrays.fill(this.satThuongDuKienPhienQuan, 0);
        this.satThuongNguocNguoiChoi = 0;
        this.phienQuanDangBiBan = -1;
        this.luotPhienQuan = -1;
        this.phienQuanDangHoatDong = false;
        this.choKyNangDacBiet = false;
        this.kyNangDacBietPhatToi = false;
        this.phatBanKyNangDacBiet = false;
        this.phatBanSieuCao = false;
    }
}
