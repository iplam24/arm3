package com.vxl.luyentap;

import com.alibaba.fastjson2.JSONObject;
import com.vxl.chien.VXLCauHinhVatPhamChienDau;
import com.vxl.chien.VXLDiaHinhPhatBan;
import com.vxl.chien.VXLChienBinh;
import com.vxl.chien.VXLHeThongDan;
import com.vxl.chien.VXLGioChienDau;
import com.vxl.chien.VXLHangDoiNapDan;
import com.vxl.chien.VXLHoSoDan;
import com.vxl.chien.VXLKyNangAvenger;
import com.vxl.chien.VXLTinhSatThuong;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.tienich.VXLThoiGianLuot;
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
    private static final long TRE_BAY_PHIEN_QUAN_TOI_THIEU = 800L;
    private static final long TRE_BAY_PHIEN_QUAN_TOI_DA = 2000L;
    private static final long TRE_SAU_XAC_NHAN_DAN_BOT = 450L;
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
    private static final int CHIEU_RONG_NHAN_VAT = 24;
    private static final int CHIEU_CAO_NHAN_VAT = 24;
    private static final int SO_DIEM_DAN_NGUOI_CHOI_TOI_DA = 159;
    private static final double BUOC_THOI_GIAN_DAN_NGUOI_CHOI = 1.0D;
    private static final double GIAM_KHOANG_CACH_DAU_SPIDER_MAN = 12D;
    private static final double TANG_DO_CAO_DAU_SPIDER_MAN = 3D;
    private static final int GIOI_HAN_BAN_SAO_ULTRON = 3;
    private static final byte LOAI_DAN_HAWKEYE_SKILL = 9;
    private static final byte LOAI_DAN_THOR_SKILL = 0;
    private static final byte LOAI_DAN_ULTRON = 49;
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
    private final int[] napDan = new int[2 + GIOI_HAN_BAN_SAO_ULTRON];
    private final long[] thuTuHanhDongNapDan = new long[2 + GIOI_HAN_BAN_SAO_ULTRON];
    private final VXLTinhDuongDanLuyenTap tinhDuongDan;
    private final VXLChienBinh[] banSaoUltron = new VXLChienBinh[GIOI_HAN_BAN_SAO_ULTRON];
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
    private byte maBanDoSpawnTruoc = -1;
    private short nguoiChoiSpawnTruocX = Short.MIN_VALUE;
    private short phienQuanSpawnTruocX = Short.MIN_VALUE;
    private int phienQuanDangBiBan = -1;
    private int luotPhienQuan = -1;
    private long lanBanCuoi;
    private long maPhatBanNguoiChoi;
    private long phienBanDuPhongPhatBan;
    private long boDemThuTuHanhDongNapDan;
    private int napDanSauHanhDongNguoiChoi = -1;
    private boolean dangChoKetThucPhatBan;
    private boolean duKienTrungPhienQuan;
    private boolean daGuiLuotDau;
    private boolean phienQuanDangHoatDong;
    private boolean dangChoKetThucDanPhienQuan;
    private boolean choKyNangDacBiet;
    private boolean kyNangDacBietPhatToi;
    private boolean skillRiengPhatToi;
    private boolean phatBanKyNangDacBiet;
    private boolean phatBanSieuCao;
    private boolean phatBanXuyenGiap;
    private boolean danManhPhienQuan;
    private byte lucPhatBanNguoiChoi = 10;
    private byte loaiDanPhatBanNguoiChoi;
    private int soVienTrungPhienQuan;
    private int satThuongNguocNguoiChoi;
    private int satThuongGocPhienQuanDangCho;
    private int satThuongPhienQuanDangCho;
    private int noKyNangDacBiet;
    private short vuKhiPhienQuanHienTai = VU_KHI_PHIEN_QUAN;
    private short[] boVuKhiPhienQuanTrongTran = new short[]{VU_KHI_PHIEN_QUAN};
    private byte gioX;
    private byte gioY;
    private int soLuotPhienQuan;
    private long maPhatBanPhienQuan;
    private byte chiSoLuotHienTai = -1;
    private long hanLuot;
    private long phienBanLuot;
    private ScheduledFuture<?> tacVuHetLuot;
    private ScheduledFuture<?> tacVuPhienQuan;
    private ScheduledFuture<?> tacVuKetThucPhienQuan;
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

    public synchronized void datLaiHangNgay() {
        boolean dangTrongLuyenTap = this.nguoiChoi.inTraining
                || this.tacVuKetQuaTran != null || this.tacVuThoatKetQua != null;
        this.resetTrangThai();
        this.dungTacVu();
        this.soPhienQuanDaHa = 0;
        this.capPhienQuanHienTai = 1;
        this.nguoiChoi.trainingSuccess = 1;
        if (dangTrongLuyenTap) {
            this.nguoiChoi.dichVu.guiThoatManHinhLuyenTap();
            this.nguoiChoi.startOKDlg2("Đã 7 giờ sáng. Tiến độ Phiến quân được đặt về Phiến quân 1.");
        }
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
            boolean tranhLapSpawn = this.maBanDoSpawnTruoc == maBanDo;
            VXLBoChonSpawnLuyenTap.KetQua viTriSpawn = VXLBoChonSpawnLuyenTap.chon(
                    this.tinhDuongDan.layBanDo(), tranhLapSpawn,
                    this.nguoiChoiSpawnTruocX, this.phienQuanSpawnTruocX);
            this.nguoiChoiX = viTriSpawn.nguoiChoiX;
            this.nguoiChoiY = viTriSpawn.nguoiChoiY;
            this.phienQuanX[0] = viTriSpawn.phienQuanX;
            this.phienQuanY[0] = viTriSpawn.phienQuanY;
            this.maBanDoSpawnTruoc = maBanDo;
            this.nguoiChoiSpawnTruocX = this.nguoiChoiX;
            this.phienQuanSpawnTruocX = this.phienQuanX[0];
            VXLQuanLyMayChu.log("[TRAINING-SPAWN] map=" + Byte.toUnsignedInt(maBanDo)
                    + " player=(" + this.nguoiChoiX + ',' + this.nguoiChoiY + ")"
                    + " rebel=(" + this.phienQuanX[0] + ',' + this.phienQuanY[0] + ")");
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
            this.skillRiengPhatToi = false;
            java.util.Arrays.fill(this.banSaoUltron, null);
            this.soLuotPhienQuan = 0;
            java.util.Arrays.fill(this.napDan, 0);
            java.util.Arrays.fill(this.thuTuHanhDongNapDan, 0L);
            this.boDemThuTuHanhDongNapDan = 0L;
            this.napDanSauHanhDongNguoiChoi = -1;
            this.taoGioMoi();
            this.phienQuanDangBiBan = -1;
            this.luotPhienQuan = -1;
            java.util.Arrays.fill(this.satThuongDuKienPhienQuan, 0);
            this.mauPhienQuan[0] = this.mauToiDaPhienQuan;
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

    public synchronized void diChuyen(VXLTinNhan tinNhan) throws IOException {
        short x = tinNhan.boDoc().readShort();
        short y = tinNhan.boDoc().readShort();
        if (!this.laLuotNguoiChoiConHan()) {
            return;
        }
        this.nguoiChoiX = this.tinhDuongDan.gioiHan(x, 0, 1200);
        this.nguoiChoiY = this.tinhDuongDan.gioiHan(y, 0, 700);
        if (this.chiSoNguoiChoi != null) {
            this.chiSoNguoiChoi.x = this.nguoiChoiX;
            this.chiSoNguoiChoi.y = this.nguoiChoiY;
            this.chiSoNguoiChoi.heSoDiChuyen = 100;
        }
        this.nguoiChoi.dichVu.guiCapNhatXYLuyenTap((byte)0, this.nguoiChoiX, this.nguoiChoiY);
    }

    public synchronized void capNhatXYSauRoi(VXLTinNhan tinNhan) throws IOException {
        short x = tinNhan.boDoc().readShort();
        short y = tinNhan.boDoc().readShort();
        int chieuRongBanDo = this.tinhDuongDan.layBanDo().getWidth();
        int chieuCaoBanDo = this.tinhDuongDan.layBanDo().getHeight();
        if (!this.nguoiChoi.inTraining
                || x >= 0 && x < chieuRongBanDo && y >= 0 && y < chieuCaoBanDo) {
            return;
        }
        this.nguoiChoiX = x;
        this.nguoiChoiY = y;
        this.mauNguoiChoi = 0;
        VXLQuanLyMayChu.log("[TRAINING-FALL-OUT] player=" + this.nguoiChoi.ten
                + " x=" + x + " y=" + y);
        this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0, 0,
                this.chiSoNguoiChoi.mauToiDa, (byte)2);
        this.thuaLuyenTap(TRE_HIEN_KET_QUA_SAU_HIEU_UNG);
    }

    public synchronized void ban(VXLTinNhan tinNhan) throws IOException {
        if (!this.laLuotNguoiChoiConHan() || this.dangChoKetThucPhatBan
                || this.phienQuanDangHoatDong || this.tacVuPhienQuan != null
                || this.tacVuKetThucPhienQuan != null) {
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
        byte loaiDanGui = tinNhan.boDoc().readByte();
        short x = tinNhan.boDoc().readShort();
        short y = tinNhan.boDoc().readShort();
        short goc = tinNhan.boDoc().readShort();
        byte luc = tinNhan.boDoc().readByte();
        byte lucTach = 0;
        if (this.laDanDoi(loaiDanGui)) {
            lucTach = tinNhan.boDoc().readByte();
        }
        int soPhat = Math.max(1, Math.min(2,
                Byte.toUnsignedInt(tinNhan.boDoc().readByte())));
        luc = (byte)Math.max(10, Math.min(30, luc));
        if (this.laDanDoi(loaiDanGui)) {
            lucTach = (byte)Math.max(4, Math.min(30, Byte.toUnsignedInt(lucTach)));
        }
        this.lucPhatBanNguoiChoi = luc;
        this.nguoiChoiX = this.tinhDuongDan.gioiHan(x, 0, 1200);
        this.nguoiChoiY = this.tinhDuongDan.gioiHan(y, 0, 700);
        int maVatPhamDan = this.chiSoNguoiChoi.vatPhamDanDacBiet;
        this.chiSoNguoiChoi.vatPhamDanDacBiet = -1;
        soPhat = Math.max(soPhat, this.chiSoNguoiChoi.soPhatToiThieu);
        VXLKyNangAvenger kyNangAvenger = this.chiSoNguoiChoi.kyNangAvenger;
        byte avengerDan = maVatPhamDan >= 0 ? 0 : kyNangAvenger != null
                ? kyNangAvenger.layAvengerDan(this.chiSoNguoiChoi.avengerDan)
                : this.chiSoNguoiChoi.avengerDan;
        short vuKhi = kyNangAvenger != null
                ? kyNangAvenger.layVuKhi(this.chiSoNguoiChoi.maVuKhi)
                : this.chiSoNguoiChoi.maVuKhi;
        boolean skillRieng = this.skillRiengPhatToi;
        boolean kyNangDacBiet = this.kyNangDacBietPhatToi;
        byte loaiDanCoBan = maVatPhamDan >= 0
                ? VXLCauHinhVatPhamChienDau.layLoaiDan(maVatPhamDan, loaiDanGui)
                : avengerDan > 0
                        ? VXLCauHinhVatPhamChienDau.layLoaiDanTheoAvenger(avengerDan, loaiDanGui)
                        : VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                                vuKhi, loaiDanGui);
        byte loaiDan = kyNangAvenger != null
                ? kyNangAvenger.layLoaiDan(loaiDanCoBan, skillRieng)
                : loaiDanCoBan;
        this.napDanSauHanhDongNguoiChoi = this.chiSoNguoiChoi.batDauNapDan();
        if (avengerDan > 0 && maVatPhamDan < 0) {
            soPhat = 1;
        }
        byte chiMang = (byte)(kyNangDacBiet ? 1 : 0);
        this.kyNangDacBietPhatToi = false;
        this.skillRiengPhatToi = false;
        this.phatBanXuyenGiap = this.chiSoNguoiChoi.luotXuyenGiap > 0;
        VXLHeThongDan.KetQuaPhatBan phatBan = this.taoPhatBanNguoiChoi(
                goc, luc, lucTach, loaiDan, chiMang, avengerDan);
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
        int heSoDan = VXLCauHinhVatPhamChienDau.layHeSoSatThuong(maVatPhamDan);
        int heSoTrangThai = VXLCauHinhVatPhamChienDau.layHeSoSatThuongTrangThai(
                this.phatBanSieuCao, this.phatBanKyNangDacBiet);
        long heSoTich = (long)heSoDan * Math.max(100, this.chiSoNguoiChoi.heSoPhatBan)
                * VXLCauHinhPhienQuan.HE_SO_DAN_THUONG * heSoTrangThai;
        int heSoTong = (int)Math.max(1L, heSoTich / 1000000L);
        int satThuongCoBan = VXLTinhSatThuong.tinhPhatBanCoDaoDong(this.chiSoNguoiChoi.tanCong, luc, heSoTong);
        int satThuongMoiVien = VXLCauHinhVatPhamChienDau.tinhSatThuongMoiVien(
                satThuongCoBan, loaiDan, chiMang, avengerDan);
        int tranSatThuong = phatBan.truotRaNgoaiBanDo ? 0 : satThuongCoBan
                * VXLCauHinhVatPhamChienDau.layTranPhanTramSatThuong(
                        loaiDan, avengerDan) / 100;
        if (skillRieng && VXLCauHinhVatPhamChienDau.laDanToNhen(loaiDan)) {
            satThuongMoiVien = 0;
            tranSatThuong = 0;
        }
        if (maVatPhamDan == 221) {
            satThuongMoiVien = 0;
            tranSatThuong = 0;
        }
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
                    phatBan.vaChamDiaHinhX, phatBan.vaChamDiaHinhY, this.phienQuanX[i], this.phienQuanY[i],
                    loaiDan, avengerDan, satThuongMoiVien, tranSatThuong);
            int satThuongDuKien = Byte.toUnsignedInt(avengerDan) == 5
                    ? satThuongTrucTiep + satThuongNo
                    : Math.max(satThuongTrucTiep, satThuongNo);
            this.satThuongDuKienPhienQuan[i] = satThuongDuKien;
            if (satThuongDuKien > satThuongCaoNhat) {
                satThuongCaoNhat = satThuongDuKien;
                mucTieuSatThuongCaoNhat = i;
            }
        }
        this.phienQuanDangBiBan = mucTieuSatThuongCaoNhat;
        this.soVienTrungPhienQuan = this.phienQuanDangBiBan >= 0
                ? phatBan.demSoVienTrung(this.phienQuanDangBiBan) : 0;
        boolean danNhanVatLao = VXLCauHinhVatPhamChienDau.layHoSoDan(
                loaiDan, avengerDan).kieuBan() == VXLHoSoDan.KieuBan.NHAN_VAT_LAO;
        this.satThuongNguocNguoiChoi = phatBan.truotRaNgoaiBanDo || danNhanVatLao ? 0
                : VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                        phatBan.vaChamDiaHinhX, phatBan.vaChamDiaHinhY, this.nguoiChoiX, this.nguoiChoiY,
                        loaiDan, avengerDan, satThuongMoiVien, tranSatThuong);
        this.duKienTrungPhienQuan = mucTieuSatThuongCaoNhat >= 0;
        this.dangChoKetThucPhatBan = true;
        long maPhatBan = ++this.maPhatBanNguoiChoi;
        this.choKyNangDacBiet = false;
        System.out.println(String.format("[TRAINING-FIRE] %s bắn | LoaiDan=%d | Goc=%d | Luc=%d | Pos=(%d,%d) | Points=%d | Target=%s",
                this.nguoiChoi.ten, loaiDan, goc, luc, this.nguoiChoiX, this.nguoiChoiY,
                phatBan.duongX[0].length,
                this.duKienTrungPhienQuan ? ("Phiến quân #" + (this.phienQuanDangBiBan + 1)) : "Trượt/Map"));

        this.chiSoNguoiChoi.heSoPhatBan = 100;
        this.chiSoNguoiChoi.ketThucPhatBan();
        this.lapDuPhongPhatBan(maPhatBan, this.duKienTrungPhienQuan,
                this.tinhTreDuPhongPhatBan(phatBan, loaiDan));
        this.huyTacVuHetLuot();
        this.nguoiChoi.dichVu.guiKetQuaBanLuyenTapNangCao(chiMang, (byte)0,
                this.layLoaiDanAnToan(loaiDan),
                this.nguoiChoiX, this.nguoiChoiY, goc, luc,
                this.layChiSoTachClient(loaiDan, lucTach, phatBan),
                phatBan.duongX, phatBan.duongY, (byte)Math.min(4, soPhat),
                (byte)(sieuCaoTrungMucTieu ? 1 : 0),
                sieuCaoTrungMucTieu ? diemSieuCao.x() : (short)-1,
                sieuCaoTrungMucTieu ? diemSieuCao.y() : (short)-1);
        this.ghiNhanDiaHinhPhatBan(loaiDan, avengerDan, phatBan);
        this.apDungDichChuyenTheoDiemRoi(maVatPhamDan, phatBan);
        if (kyNangAvenger != null) {
            kyNangAvenger.ghiNhanPhatBan(maVatPhamDan < 0
                    && !kyNangDacBiet && !skillRieng);
        }
        this.capNhatViTriHulkSauPhatBan(loaiDan, avengerDan, phatBan);
    }

    private VXLHeThongDan.KetQuaPhatBan taoPhatBanNguoiChoi(short goc, byte luc,
            byte lucTach, byte loaiDan, byte chiMang, byte avengerDan) {
        short xDauDan = this.nguoiChoiX;
        short yDauDan = this.nguoiChoiY;
        if (Byte.toUnsignedInt(avengerDan) == 9) {
            double radian = Math.toRadians(goc);
            xDauDan = (short)Math.round(this.nguoiChoiX
                    - Math.cos(radian) * GIAM_KHOANG_CACH_DAU_SPIDER_MAN);
            yDauDan = (short)Math.round(this.nguoiChoiY
                    + Math.sin(radian) * GIAM_KHOANG_CACH_DAU_SPIDER_MAN
                    - TANG_DO_CAO_DAU_SPIDER_MAN);
        }
        VXLHeThongDan heThongDan = new VXLHeThongDan(
                this.tinhDuongDan.layBanDo(), (x, y, leTrung, mucTieuBoQua) -> {
                    for (int i = 0; i < SO_PHIEN_QUAN; i++) {
                        if (i == mucTieuBoQua || this.phienQuanDaChet[i]) {
                            continue;
                        }
                        int nuaRong = CHIEU_RONG_NHAN_VAT / 2 + Math.max(0, leTrung);
                        int le = Math.max(0, leTrung);
                        if (x >= this.phienQuanX[i] - nuaRong
                                && x < this.phienQuanX[i] + nuaRong
                                && y >= this.phienQuanY[i] - CHIEU_CAO_NHAN_VAT - le
                                && y < this.phienQuanY[i] + le) {
                            return i;
                        }
                    }
                    return -1;
                });
        byte gioApDungX = this.chiSoNguoiChoi.luotNgungGio > 0 ? 0 : this.gioX;
        byte gioApDungY = this.chiSoNguoiChoi.luotNgungGio > 0 ? 0 : this.gioY;
        return heThongDan.taoPhatBan(xDauDan, yDauDan, goc, luc, lucTach,
                loaiDan, chiMang, avengerDan, gioApDungX, gioApDungY, -1,
                BUOC_THOI_GIAN_DAN_NGUOI_CHOI, SO_DIEM_DAN_NGUOI_CHOI_TOI_DA,
                this.chiSoNguoiChoi.luotXuyenDiaHinh > 0);
    }

    private void ghiNhanDiaHinhPhatBan(byte loaiDan, byte avengerDan,
            VXLHeThongDan.KetQuaPhatBan phatBan) {
        int loai = Byte.toUnsignedInt(loaiDan);
        if (loai == 5 || loai == 13 || loai == 51 || loai == 53
                || loai == 54 || loai == 55 || loai == 57 || loai == 58) {
            return;
        }
        VXLDiaHinhPhatBan.ghiNhanLo(this.tinhDuongDan.layBanDo(),
                loaiDan, avengerDan, phatBan);
    }

    private void apDungDichChuyenTheoDiemRoi(int maVatPhamDan,
            VXLHeThongDan.KetQuaPhatBan phatBan) throws IOException {
        if (maVatPhamDan != 221 || phatBan == null) {
            return;
        }
        int soQuyDao = Math.min(phatBan.duongX.length, phatBan.duongY.length);
        for (int i = soQuyDao - 1; i >= 0; i--) {
            int chiSoCuoi = Math.min(phatBan.duongX[i].length,
                    phatBan.duongY[i].length) - 1;
            if (chiSoCuoi < 0) {
                continue;
            }
            short x = phatBan.duongX[i][chiSoCuoi];
            short y = phatBan.duongY[i][chiSoCuoi];
            int chieuRongBanDo = this.tinhDuongDan.layBanDo().getWidth();
            int chieuCaoBanDo = this.tinhDuongDan.layBanDo().getHeight();
            if (x < 0 || x >= chieuRongBanDo || y < 0 || y >= chieuCaoBanDo) {
                return;
            }
            this.nguoiChoiX = x;
            this.nguoiChoiY = y;
            this.chiSoNguoiChoi.x = x;
            this.chiSoNguoiChoi.y = y;
            this.nguoiChoi.dichVu.guiCapNhatXYLuyenTap((byte)0, x, y);
            VXLQuanLyMayChu.log("[TRAINING-ITEM] teleport player="
                    + this.nguoiChoi.ten + " x=" + x + " y=" + y);
            return;
        }
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
        if (phatBan.duongY[0][chiSoCuoi] > this.tinhDuongDan.layBanDo().getHeight()) {
            return;
        }
        this.nguoiChoiX = phatBan.duongX[0][chiSoCuoi];
        this.nguoiChoiY = this.tinhDuongDan.layBanDo().timViTriDat(
                this.nguoiChoiX, phatBan.duongY[0][chiSoCuoi]);
        if (this.chiSoNguoiChoi != null) {
            this.chiSoNguoiChoi.x = this.nguoiChoiX;
            this.chiSoNguoiChoi.y = this.nguoiChoiY;
        }
        VXLQuanLyMayChu.log("[TRAINING-FIRE] Hulk landed player=" + this.nguoiChoi.ten
                + " x=" + this.nguoiChoiX + " y=" + this.nguoiChoiY);
    }

    public synchronized void dungVatPham(VXLTinNhan tinNhan) throws IOException {
        int yeuCau = Byte.toUnsignedInt(tinNhan.boDoc().readByte());
        while (tinNhan.boDoc().available() > 0) {
            tinNhan.boDoc().readByte();
        }
        VXLQuanLyMayChu.log("[TRAINING-ITEM] request player=" + this.nguoiChoi.ten
                + " slot=" + yeuCau + " turn=" + this.chiSoLuotHienTai);
        if (!this.laLuotNguoiChoiConHan() || this.phienQuanDangHoatDong
                || this.dangChoKetThucPhatBan || this.kyNangDacBietPhatToi
                || this.chiSoNguoiChoi == null
                || this.chiSoNguoiChoi.daDungVatPhamTrongLuot) {
            VXLQuanLyMayChu.log("[TRAINING-ITEM] reject player=" + this.nguoiChoi.ten
                    + " slot=" + yeuCau + " turn=" + this.chiSoLuotHienTai
                    + " rebelActive=" + this.phienQuanDangHoatDong
                    + " waitingShot=" + this.dangChoKetThucPhatBan
                    + " nextSpecial=" + this.kyNangDacBietPhatToi
                    + " used=" + (this.chiSoNguoiChoi != null
                            && this.chiSoNguoiChoi.daDungVatPhamTrongLuot));
            return;
        }
        if (yeuCau != 100) {
            this.dungVatPhamTrongBalo(yeuCau);
            return;
        }
        VXLKyNangAvenger kyNangAvenger = this.chiSoNguoiChoi.kyNangAvenger;
        if (kyNangAvenger != null && kyNangAvenger.laSkillRieng()) {
            return;
        }
        if (this.noKyNangDacBiet < 100) {
            return;
        }
        this.noKyNangDacBiet = 0;
        this.kyNangDacBietPhatToi = true;
        this.chiSoNguoiChoi.daDungVatPhamTrongLuot = true;
        this.nguoiChoi.dichVu.guiNoDau((byte)0, (byte)0);
        this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)0, (byte)100,
                this.chiSoNguoiChoi.layIconKyNangDacBiet());
    }

    private void dungVatPhamTrongBalo(int chiSoBalo) throws IOException {
        if (this.nguoiChoi.itemBalo == null || this.nguoiChoi.itemBag == null
                || chiSoBalo < 0 || chiSoBalo >= this.nguoiChoi.itemBalo.length) {
            this.nguoiChoi.startOKDlg2("Vật phẩm không có trong balo chiến đấu.");
            return;
        }
        int chiSoTui = this.nguoiChoi.itemBalo[chiSoBalo];
        if (chiSoTui < 0 || chiSoTui >= this.nguoiChoi.itemBag.length) {
            this.nguoiChoi.startOKDlg2("Vật phẩm không có trong balo chiến đấu.");
            return;
        }
        VXLVatPham vatPham = this.nguoiChoi.itemBag[chiSoTui];
        if (vatPham == null || vatPham.mau == null || vatPham.mau.loai != 10
                || vatPham.soLuong <= 0) {
            this.nguoiChoi.startOKDlg2("Vật phẩm chiến đấu không hợp lệ.");
            return;
        }

        boolean daApDung = true;
        int mauHoi = 0;
        switch (vatPham.ma) {
            case 220, 230 -> mauHoi = Math.max(1,
                    this.chiSoNguoiChoi.mauToiDa * 30 / 100);
            case 251 -> mauHoi = Math.max(1,
                    this.chiSoNguoiChoi.mauToiDa * 50 / 100);
            case 252 -> mauHoi = this.chiSoNguoiChoi.mauToiDa;
            case 222 -> {
                this.chiSoNguoiChoi.soPhatToiThieu = Math.max(
                        this.chiSoNguoiChoi.soPhatToiThieu, 2);
                this.chiSoNguoiChoi.heSoPhatBan = Math.max(
                        this.chiSoNguoiChoi.heSoPhatBan,
                        VXLCauHinhVatPhamChienDau.layHeSoSatThuong(222));
            }
            case 223 -> this.chiSoNguoiChoi.heSoDiChuyen = 200;
            case 225 -> this.chiSoNguoiChoi.luotNgungGio = Math.max(
                    this.chiSoNguoiChoi.luotNgungGio, 3);
            case 297 -> this.chiSoNguoiChoi.luotNapNhanh = Math.max(
                    this.chiSoNguoiChoi.luotNapNhanh, 3);
            case 298 -> this.chiSoNguoiChoi.luotXuyenGiap = Math.max(
                    this.chiSoNguoiChoi.luotXuyenGiap, 3);
            case 389 -> this.chiSoNguoiChoi.luotXuyenDiaHinh = Math.max(
                    this.chiSoNguoiChoi.luotXuyenDiaHinh, 3);
            case 390 -> {
                this.nguoiChoi.startOKDlg2(
                        "Luyện tập chưa có bom để gỡ, vật phẩm không bị tiêu hao.");
                daApDung = false;
            }
            case 236, 250 -> {
                this.nguoiChoi.startOKDlg2(
                        "Vật phẩm này chưa có đủ cơ chế trong luyện tập, vật phẩm không bị tiêu hao.");
                daApDung = false;
            }
            default -> {
                if (VXLCauHinhVatPhamChienDau.laDanDacBiet(vatPham.ma)) {
                    this.chiSoNguoiChoi.vatPhamDanDacBiet = vatPham.ma;
                } else {
                    this.nguoiChoi.startOKDlg2(
                            "Vật phẩm này chưa hỗ trợ an toàn trong chế độ luyện tập.");
                    daApDung = false;
                }
            }
        }
        if (mauHoi > 0) {
            if (this.chiSoNguoiChoi.hoiMau(mauHoi) <= 0) {
                this.nguoiChoi.startOKDlg2("Máu của bạn đang đầy.");
                return;
            }
            this.mauNguoiChoi = this.chiSoNguoiChoi.hp;
            this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0,
                    this.mauNguoiChoi, this.chiSoNguoiChoi.mauToiDa, (byte)0);
        }
        if (!daApDung) {
            return;
        }
        this.chiSoNguoiChoi.daDungVatPhamTrongLuot = true;
        this.nguoiChoi.tieuThuVatPhamTrongBalo(chiSoBalo);
        this.nguoiChoi.dichVu.guiDungVatPhamLuyenTap((byte)0,
                vatPham.mau.gioiTinh, vatPham.mau.iconID);
        VXLQuanLyMayChu.log("[TRAINING-ITEM] use player=" + this.nguoiChoi.ten
                + " item=" + vatPham.ma + " effect="
                + Byte.toUnsignedInt(vatPham.mau.gioiTinh));
    }

    public synchronized void doiSung(VXLTinNhan tinNhan) throws IOException {
        int chiSoBalo = tinNhan.boDoc().readUnsignedByte();
        while (tinNhan.boDoc().available() > 0) {
            tinNhan.boDoc().readByte();
        }
        if (!this.laLuotNguoiChoiConHan() || this.chiSoNguoiChoi.avengerDan > 0
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
        this.chiSoNguoiChoi.batDauNapDan();
        this.nguoiChoi.dichVu.guiTuiDo();
        this.nguoiChoi.dichVu.guiDoTrenNguoi();
        this.nguoiChoi.dichVu.guiBalo();
        this.nguoiChoi.dichVu.doiTrangBi();
        this.nguoiChoi.flushCache();
        this.nguoiChoi.dichVu.guiDoiSungLuyenTap((byte)0, this.nguoiChoi.wp,
                vuKhiCu.mau.iconID);
    }

    public synchronized void xuLyFocusSkill(VXLTinNhan tinNhan) throws IOException {
        byte hanhDong = tinNhan.boDoc().readByte();
        int chiSoMucTieu = tinNhan.boDoc().available() > 0
                ? Byte.toUnsignedInt(tinNhan.boDoc().readByte()) : 0;
        VXLKyNangAvenger kyNang = this.chiSoNguoiChoi != null
                ? this.chiSoNguoiChoi.kyNangAvenger : null;
        if (kyNang == null || !this.laLuotNguoiChoiConHan()
                || !this.choKyNangDacBiet || this.phienQuanDangHoatDong
                || this.dangChoKetThucPhatBan || this.skillRiengPhatToi) {
            return;
        }
        int chiSoPhienQuan = chiSoMucTieu - 1;
        if ((kyNang.laLoki() || kyNang.laHawkeye())
                && !this.laPhienQuanHopLe(chiSoPhienQuan)) {
            return;
        }
        int soBanSaoUltron = this.demBanSaoUltron();
        if ((chiSoPhienQuan < 0 && (kyNang.laLoki() || kyNang.laHawkeye()
                ))
                || !kyNang.kichHoatSkill(hanhDong, this.chiSoNguoiChoi,
                        soBanSaoUltron)) {
            return;
        }
        this.choKyNangDacBiet = false;
        if (kyNang.laSpiderMan()) {
            this.skillRiengPhatToi = true;
            this.nguoiChoi.dichVu.guiXacNhanSkillSpiderMan();
            VXLQuanLyMayChu.log("[SPIDER-SKILL] armed training player="
                    + this.nguoiChoi.ten);
            return;
        }
        if (kyNang.laLoki()) {
            VXLChienBinh phienQuan = this.taoHoSoPhienQuan(chiSoPhienQuan);
            kyNang.saoChepLoki(this.chiSoNguoiChoi, phienQuan);
            this.mauNguoiChoi = this.chiSoNguoiChoi.hp;
            byte loaiDanSaoChep = VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                    this.vuKhiPhienQuanHienTai, (byte)0);
            byte nhomSungSaoChep = VXLCauHinhVatPhamChienDau.layNhomSungClientTheoVuKhi(
                    this.vuKhiPhienQuanHienTai);
            this.nguoiChoi.dichVu.guiLokiGiaDang((byte)0,
                    (byte)(chiSoPhienQuan + 1), DAU_PHIEN_QUAN,
                    QUAN_PHIEN_QUAN, AO_PHIEN_QUAN, this.vuKhiPhienQuanHienTai,
                    NON_PHIEN_QUAN, CANH_PHIEN_QUAN, (byte)0,
                    this.mauPhienQuan[chiSoPhienQuan], this.mauToiDaPhienQuan,
                    loaiDanSaoChep, nhomSungSaoChep);
            this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0,
                    this.mauNguoiChoi, this.chiSoNguoiChoi.mauToiDa, (byte)0);
            return;
        }
        this.huyTacVuHetLuot();
        this.phienQuanDangHoatDong = true;
        if (kyNang.laHawkeye()) {
            this.xuLySkillHawkeye(chiSoPhienQuan);
            return;
        }
        if (kyNang.laThor()) {
            this.xuLySkillThor();
            return;
        }
        if (kyNang.laUltron()) {
            this.taoBanSaoUltron();
        }
    }

    private boolean laPhienQuanHopLe(int chiSoPhienQuan) {
        return chiSoPhienQuan >= 0 && chiSoPhienQuan < SO_PHIEN_QUAN
                && !this.phienQuanDaChet[chiSoPhienQuan];
    }

    private int timPhienQuanSongDauTien() {
        for (int i = 0; i < SO_PHIEN_QUAN; i++) {
            if (!this.phienQuanDaChet[i]) {
                return i;
            }
        }
        return -1;
    }

    private VXLChienBinh taoHoSoPhienQuan(int chiSoPhienQuan) {
        VXLChienBinh phienQuan = new VXLChienBinh((byte)(chiSoPhienQuan + 1),
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan],
                VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai),
                this.vuKhiPhienQuanHienTai, (byte)0, this.mauToiDaPhienQuan,
                this.tanCongPhienQuan, this.giapPhienQuan);
        phienQuan.hp = this.mauPhienQuan[chiSoPhienQuan];
        return phienQuan;
    }

    private void xuLySkillHawkeye(int chiSoPhienQuan) throws IOException {
        short mucTieuX = this.phienQuanX[chiSoPhienQuan];
        short mucTieuY = this.phienQuanY[chiSoPhienQuan];
        int soMuiTen = 5;
        short[] xs = new short[soMuiTen];
        short[] ys = new short[soMuiTen];
        for (int i = 0; i < soMuiTen; i++) {
            xs[i] = (short)Math.max(4, Math.min(
                    this.tinhDuongDan.layBanDo().getWidth() - 5, mucTieuX));
            ys[i] = this.tinhDuongDan.layBanDo().timViTriDat(xs[i], mucTieuY);
        }
        this.tinhDuongDan.layBanDo().taoLoTheoMatNa(xs[0], ys[0], "hgrenade.png");
        this.nguoiChoi.dichVu.guiSkillHawkeye((byte)0,
                LOAI_DAN_HAWKEYE_SKILL, xs, ys);
        int satThuongMoiMui = VXLTinhSatThuong.tinhSauGiap(20 + this.chiSoNguoiChoi.tanCong,
                this.giapPhienQuan);
        satThuongMoiMui = Math.max(8, satThuongMoiMui);
        this.apDungSatThuongSkillPhienQuan(chiSoPhienQuan, satThuongMoiMui * soMuiTen);
        this.lapLichKetThucSkillNguoiChoi(1800L);
    }

    private void xuLySkillThor() throws IOException {
        int[] lechX = new int[]{-30, -10, 10, 30};
        short[] xs = new short[lechX.length];
        short[] ys = new short[lechX.length];
        for (int i = 0; i < lechX.length; i++) {
            xs[i] = (short)Math.max(4, Math.min(
                    this.tinhDuongDan.layBanDo().getWidth() - 5,
                    this.nguoiChoiX + lechX[i]));
            ys[i] = this.tinhDuongDan.layBanDo().timViTriDat(xs[i],
                    this.nguoiChoiY);
            this.tinhDuongDan.layBanDo().taoLoTheoMatNa(xs[i], ys[i],
                    "h36x30.png");
        }
        this.nguoiChoi.dichVu.guiSkillHawkeye((byte)0,
                LOAI_DAN_THOR_SKILL, xs, ys);
        int satThuongMoiDiem = Math.max(12, 18 + this.chiSoNguoiChoi.tanCong / 2);
        for (int chiSoPhienQuan = 0; chiSoPhienQuan < SO_PHIEN_QUAN;
                chiSoPhienQuan++) {
            if (this.phienQuanDaChet[chiSoPhienQuan]) {
                continue;
            }
            int soDiemTrung = 0;
            for (int i = 0; i < xs.length; i++) {
                int dx = this.phienQuanX[chiSoPhienQuan] - xs[i];
                int dy = this.phienQuanY[chiSoPhienQuan] - ys[i];
                if (dx * dx + dy * dy <= 52 * 52) {
                    soDiemTrung++;
                }
            }
            if (soDiemTrung > 0) {
                int satThuong = VXLTinhSatThuong.tinhSauGiap(
                        satThuongMoiDiem * soDiemTrung, this.giapPhienQuan);
                this.apDungSatThuongSkillPhienQuan(chiSoPhienQuan, satThuong);
            }
        }
        this.lapLichKetThucSkillNguoiChoi(1800L);
    }

    private void apDungSatThuongSkillPhienQuan(int chiSoPhienQuan, int satThuong)
            throws IOException {
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
    }

    private void taoBanSaoUltron() throws IOException {
        int viTri = -1;
        for (int i = 0; i < this.banSaoUltron.length; i++) {
            if (this.banSaoUltron[i] == null) {
                viTri = i;
                break;
            }
        }
        if (viTri < 0) {
            this.ketThucSkillNguoiChoi(++this.phienBanDuPhongPhatBan);
            return;
        }
        int huong = viTri % 2 == 0 ? 1 : -1;
        short x = (short)Math.max(16, Math.min(
                this.tinhDuongDan.layBanDo().getWidth() - 17,
                this.nguoiChoiX + huong * (28 + viTri * 12)));
        short y = this.tinhDuongDan.layBanDo().timViTriDat(x, this.nguoiChoiY);
        VXLChienBinh banSao = VXLChienBinh.taoBanSaoUltron((byte)(viTri + 2),
                (byte)0, x, y, this.nguoiChoi.ten,
                Math.max(80, this.chiSoNguoiChoi.mauToiDa / 3),
                Math.max(12, this.chiSoNguoiChoi.tanCong / 2),
                Math.max(0, this.chiSoNguoiChoi.giap / 2));
        this.banSaoUltron[viTri] = banSao;
        int viTriLuot = viTri + 2;
        this.napDan[viTriLuot] = 0;
        this.boDemThuTuHanhDongNapDan = VXLHangDoiNapDan.ghiNhanHanhDong(
                this.thuTuHanhDongNapDan, viTriLuot,
                this.boDemThuTuHanhDongNapDan);
        this.nguoiChoi.dichVu.guiThemBanSaoUltron(banSao, (byte)0);
        this.lapLichKetThucSkillNguoiChoi(900L);
    }

    private int demBanSaoUltron() {
        int soLuong = 0;
        for (VXLChienBinh banSao : this.banSaoUltron) {
            if (banSao != null && !banSao.chet) {
                soLuong++;
            }
        }
        return soLuong;
    }

    private void lapLichKetThucSkillNguoiChoi(long tre) {
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
        }
        long phienBan = ++this.phienBanDuPhongPhatBan;
        this.tacVuDuPhongPhatBan = BO_LAP_LICH.schedule(
                () -> this.ketThucSkillNguoiChoi(phienBan), tre,
                TimeUnit.MILLISECONDS);
    }

    private void ketThucSkillNguoiChoi(long phienBan) {
        synchronized (this) {
            if (!this.nguoiChoi.inTraining || phienBan != this.phienBanDuPhongPhatBan) {
                return;
            }
            this.tacVuDuPhongPhatBan = null;
            this.dangChoKetThucPhatBan = false;
            this.phienQuanDangHoatDong = false;
            try {
                if (this.daHaPhienQuan()) {
                    this.hoanThanhPhienQuan();
                } else {
                    this.chuyenLuotSauHanhDong((byte)0,
                            VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
                }
            }
            catch (Exception ex) {
                Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                        "Khong the ket thuc skill Avenger luyen tap.", ex);
            }
        }
    }

    public synchronized void xuLyVaCham(VXLTinNhan tinNhan) throws IOException {
        int soVuNo = tinNhan.boDoc().readUnsignedByte();
        if (soVuNo > 32) {
            throw new IllegalArgumentException("Số vụ nổ không hợp lệ: " + soVuNo);
        }
        for (int i = 0; i < soVuNo; i++) {
            tinNhan.boDoc().readInt();
            tinNhan.boDoc().readInt();
        }
        int nguoiBanXacNhan = tinNhan.boDoc().available() > 0
                ? Byte.toUnsignedInt(tinNhan.boDoc().readByte()) : -1;
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        if (this.dangChoKetThucPhatBan) {
            if (nguoiBanXacNhan >= 0 && nguoiBanXacNhan != 0) {
                VXLQuanLyMayChu.log("[TRAINING-SHOT-ACK] ignore stale shooter="
                        + nguoiBanXacNhan + " expected=0");
                return;
            }
            this.lapDuPhongPhatBan(this.maPhatBanNguoiChoi,
                    this.duKienTrungPhienQuan, TRE_XU_LY_VA_CHAM_NGUOI_CHOI);
            return;
        }
        if (this.dangChoKetThucDanPhienQuan && this.phienQuanDangHoatDong) {
            int nguoiBanDangCho = Byte.toUnsignedInt(this.chiSoLuotHienTai);
            if (nguoiBanXacNhan >= 0 && nguoiBanXacNhan != nguoiBanDangCho) {
                VXLQuanLyMayChu.log("[TRAINING-BOT-SHOT-ACK] ignore stale shooter="
                        + nguoiBanXacNhan + " expected=" + nguoiBanDangCho
                        + " shot=" + this.maPhatBanPhienQuan);
                return;
            }
            VXLQuanLyMayChu.log("[TRAINING-BOT-SHOT-ACK] client da xu ly het dan phien quan"
                    + " | shooter=" + nguoiBanDangCho
                    + " | explosions=" + soVuNo + " | shot=" + this.maPhatBanPhienQuan);
            this.hoanTatDanPhienQuan(this.maPhatBanPhienQuan, true);
        }
    }

    public void yeuCauDatLaiHo() throws IOException {
        if (this.nguoiChoi.inTraining) {
            this.nguoiChoi.dichVu.guiDatLaiHoLuyenTap();
        }
    }

    public synchronized void sanSang() throws IOException {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        this.nguoiChoi.dichVu.guiBalo();
        this.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
        if (!this.daGuiLuotDau) {
            this.daGuiLuotDau = true;
            this.batDauLuotNguoiChoi();
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

    private boolean laLuotNguoiChoiConHan() {
        return this.nguoiChoi.inTraining && this.chiSoLuotHienTai == 0
                && this.hanLuot > 0 && System.currentTimeMillis() <= this.hanLuot;
    }

    private void batDauDemLuot(byte chiSoLuot, short x, short y) throws IOException {
        this.huyTacVuHetLuot();
        this.chiSoLuotHienTai = chiSoLuot;
        this.hanLuot = System.currentTimeMillis() + VXLThoiGianLuot.MILLI_GIAY;
        long phienBan = ++this.phienBanLuot;
        long tre = Math.max(0L, this.hanLuot - System.currentTimeMillis());
        this.tacVuHetLuot = BO_LAP_LICH.schedule(
                () -> this.xuLyHetGioLuot(phienBan, chiSoLuot),
                tre, TimeUnit.MILLISECONDS);
        this.nguoiChoi.dichVu.guiLuotLuyenTapTiep(chiSoLuot, x, y,
                this.napDan, this.thuTuHanhDongNapDan, this.banSaoUltron,
                (byte)VXLThoiGianLuot.SO_GIAY);
    }

    private void huyTacVuHetLuot() {
        if (this.tacVuHetLuot != null) {
            this.tacVuHetLuot.cancel(false);
            this.tacVuHetLuot = null;
        }
        this.chiSoLuotHienTai = -1;
        this.hanLuot = 0L;
        this.phienBanLuot++;
    }

    private synchronized int huyPhatBanNguoiChoiDangCho() {
        int thoiGianNapDan = this.napDanSauHanhDongNguoiChoi >= 0
                ? this.napDanSauHanhDongNguoiChoi
                : VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU;
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
            this.tacVuDuPhongPhatBan = null;
        }
        this.maPhatBanNguoiChoi++;
        this.phienBanDuPhongPhatBan++;
        this.dangChoKetThucPhatBan = false;
        this.duKienTrungPhienQuan = false;
        this.phienQuanDangBiBan = -1;
        this.soVienTrungPhienQuan = 0;
        this.satThuongNguocNguoiChoi = 0;
        this.napDanSauHanhDongNguoiChoi = -1;
        this.phatBanKyNangDacBiet = false;
        this.phatBanSieuCao = false;
        java.util.Arrays.fill(this.satThuongDuKienPhienQuan, 0);
        return thoiGianNapDan;
    }

    private synchronized void phucHoiLuotNguoiChoiSauLoi() {
        if (!this.nguoiChoi.inTraining || this.chiSoLuotHienTai >= 0
                || this.tacVuHetLuot != null) {
            return;
        }
        int thoiGianNapDan = this.huyPhatBanNguoiChoiDangCho();
        if (this.mauNguoiChoi <= 0) {
            this.thuaLuyenTap(TRE_HIEN_KET_QUA_SAU_HIEU_UNG);
            return;
        }
        try {
            this.chuyenLuotSauHanhDong((byte)0, thoiGianNapDan);
        }
        catch (Exception ex) {
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                    "Khong the phuc hoi luot nguoi choi sau loi phat ban.", ex);
        }
    }

    private void xuLyHetGioLuot(long phienBan, byte chiSoLuot) {
        synchronized (this) {
            if (!this.nguoiChoi.inTraining || phienBan != this.phienBanLuot
                    || chiSoLuot != this.chiSoLuotHienTai) {
                return;
            }
            long hienTai = System.currentTimeMillis();
            if (hienTai < this.hanLuot) {
                this.tacVuHetLuot = BO_LAP_LICH.schedule(
                        () -> this.xuLyHetGioLuot(phienBan, chiSoLuot),
                        this.hanLuot - hienTai, TimeUnit.MILLISECONDS);
                return;
            }
            this.tacVuHetLuot = null;
            this.chiSoLuotHienTai = -1;
            this.hanLuot = 0L;
            this.phienBanLuot++;
            VXLQuanLyMayChu.log("[TRAINING] turn timeout index=" + chiSoLuot
                    + " player=" + this.nguoiChoi.ten);
            if (chiSoLuot == 0) {
                int thoiGianNapDan = this.huyPhatBanNguoiChoiDangCho();
                try {
                    this.chuyenLuotSauHanhDong((byte)0, thoiGianNapDan);
                }
                catch (IOException ex) {
                    Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.WARNING,
                            "Khong the chuyen sang luot Phien quan khi het gio.", ex);
                }
                return;
            }
            if (Byte.toUnsignedInt(chiSoLuot) >= 2) {
                this.huyTacVuPhienQuan();
                this.ketThucLuotBanSaoUltron(chiSoLuot,
                        VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
                return;
            }
            this.huyTacVuPhienQuan();
            if (this.tacVuKetThucPhienQuan != null) {
                this.tacVuKetThucPhienQuan.cancel(false);
                this.tacVuKetThucPhienQuan = null;
            }
            this.maPhatBanPhienQuan++;
            this.xoaSatThuongPhienQuanDangCho();
            this.ketThucLuotPhienQuan(VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
        }
    }

    private synchronized void lapDuPhongPhatBan(long maPhatBan, boolean trung, long tre) {
        if (this.tacVuDuPhongPhatBan != null) {
            this.tacVuDuPhongPhatBan.cancel(false);
        }
        long phienBanDuPhong = ++this.phienBanDuPhongPhatBan;
        this.tacVuDuPhongPhatBan = BO_LAP_LICH.schedule(() -> {
            try {
                this.ketThucPhatBanNguoiChoi(maPhatBan, phienBanDuPhong, trung);
            }
            catch (Exception ex) {
                this.phucHoiLuotNguoiChoiSauLoi();
                Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi xử lý dự phòng phát bắn luyện tập.", ex);
            }
        }, tre, TimeUnit.MILLISECONDS);
    }

    private long tinhTreDuPhongPhatBan(VXLHeThongDan.KetQuaPhatBan phatBan, byte loaiDan) {
        if (phatBan == null) {
            return 3000L;
        }
        return VXLThoiLuongPhatBan.tinh(loaiDan,
                phatBan.duongX, phatBan.duongY).treDuPhongKetThuc();
    }

    private synchronized void ketThucPhatBanNguoiChoi(long maPhatBan,
            long phienBanDuPhong, boolean trung) throws IOException {
        if (!this.nguoiChoi.inTraining || !this.dangChoKetThucPhatBan
                || maPhatBan != this.maPhatBanNguoiChoi
                || phienBanDuPhong != this.phienBanDuPhongPhatBan) {
            return;
        }
        this.tacVuDuPhongPhatBan = null;
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
        this.chuyenLuotSauHanhDong((byte)0, this.napDanSauHanhDongNguoiChoi);
    }

    private void chuyenLuotSauHanhDong(byte viTriDaHanhDong, int thoiGianNapDan)
            throws IOException {
        this.capNhatViTriPhienQuanSauPhaDiaHinh();
        if (this.daHaPhienQuan()) {
            this.hoanThanhPhienQuan();
            return;
        }
        int viTri = Byte.toUnsignedInt(viTriDaHanhDong);
        if (viTri == 0 && this.chiSoNguoiChoi != null
                && this.chiSoNguoiChoi.kyNangAvenger != null) {
            this.chiSoNguoiChoi.kyNangAvenger.ghiNhanKetThucLuot();
        }
        this.napDan[viTri] = Math.max(VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU,
                thoiGianNapDan >= 0
                        ? thoiGianNapDan : VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
        this.boDemThuTuHanhDongNapDan = VXLHangDoiNapDan.ghiNhanHanhDong(
                this.thuTuHanhDongNapDan, viTri, this.boDemThuTuHanhDongNapDan);
        if (viTri == 0) {
            this.napDanSauHanhDongNguoiChoi = -1;
        }

        int luotTiepTheo = VXLHangDoiNapDan.timViTriTiepTheo(
                this.napDan, this.thuTuHanhDongNapDan, viTri, viTriKiemTra -> {
                    if (viTriKiemTra == 0) {
                        return this.nguoiChoi.inTraining && this.mauNguoiChoi > 0;
                    }
                    if (viTriKiemTra == 1) {
                        return this.nguoiChoi.inTraining && !this.daHaPhienQuan();
                    }
                    int chiSoBanSao = viTriKiemTra - 2;
                    return this.nguoiChoi.inTraining
                            && chiSoBanSao >= 0 && chiSoBanSao < this.banSaoUltron.length
                            && this.banSaoUltron[chiSoBanSao] != null
                            && !this.banSaoUltron[chiSoBanSao].chet;
                });
        if (luotTiepTheo == 0) {
            this.batDauLuotNguoiChoi();
        } else if (luotTiepTheo == 1) {
            this.batDauLuotPhienQuan();
        } else if (luotTiepTheo >= 2) {
            this.batDauLuotBanSaoUltron(luotTiepTheo);
        }
    }

    private void batDauLuotNguoiChoi() throws IOException {
        this.phienQuanDangHoatDong = false;
        if (this.chiSoNguoiChoi != null) {
            this.chiSoNguoiChoi.daDungVatPhamTrongLuot = false;
        }
        VXLKyNangAvenger kyNang = this.chiSoNguoiChoi != null
                ? this.chiSoNguoiChoi.kyNangAvenger : null;
        if (kyNang != null) {
            kyNang.batDauLuot();
        }
        this.taoGioMoi();
        this.tangNoTheoDoiLuot();
        this.nguoiChoi.dichVu.guiGioLuyenTap(this.gioX, this.gioY);
        this.batDauDemLuot((byte)0, this.nguoiChoiX, this.nguoiChoiY);
        if (kyNang != null) {
            byte maMenu = kyNang.layMaMenuSkill(this.chiSoNguoiChoi,
                    this.demBanSaoUltron());
            if (maMenu >= 0 && kyNang.canHienNutSkill(this.chiSoNguoiChoi,
                    this.demBanSaoUltron())) {
                this.choKyNangDacBiet = true;
                this.nguoiChoi.dichVu.guiYeuCauSkill(maMenu);
            }
        }
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
        this.phienQuanDangHoatDong = true;
        try {
            this.batDauDemLuot((byte)(chiSoPhienQuan + 1),
                    this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]);
            this.lapLichPhienQuanBan(this.tinhTreNgamPhienQuan());
        }
        catch (IOException ex) {
            this.phienQuanDangHoatDong = false;
            throw ex;
        }
    }

    private void batDauLuotBanSaoUltron(int viTriLuot) throws IOException {
        int chiSoBanSao = viTriLuot - 2;
        if (!this.nguoiChoi.inTraining || chiSoBanSao < 0
                || chiSoBanSao >= this.banSaoUltron.length) {
            return;
        }
        VXLChienBinh banSao = this.banSaoUltron[chiSoBanSao];
        if (banSao == null || banSao.chet) {
            this.chuyenLuotSauHanhDong((byte)viTriLuot,
                    VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
            return;
        }
        this.phienQuanDangHoatDong = true;
        this.batDauDemLuot((byte)viTriLuot, banSao.x, banSao.y);
        this.huyTacVuPhienQuan();
        long treNgam = ThreadLocalRandom.current().nextLong(900L, 1401L);
        this.tacVuPhienQuan = BO_LAP_LICH.schedule(
                () -> this.banSaoUltronBan(viTriLuot), treNgam,
                TimeUnit.MILLISECONDS);
    }

    private void banSaoUltronBan(int viTriLuot) {
        try {
            synchronized (this) {
                this.tacVuPhienQuan = null;
                int chiSoBanSao = viTriLuot - 2;
                if (!this.nguoiChoi.inTraining || this.chiSoLuotHienTai != viTriLuot
                        || chiSoBanSao < 0 || chiSoBanSao >= this.banSaoUltron.length) {
                    return;
                }
                VXLChienBinh banSao = this.banSaoUltron[chiSoBanSao];
                int chiSoPhienQuan = this.timPhienQuanSongDauTien();
                if (banSao == null || banSao.chet || chiSoPhienQuan < 0) {
                    this.ketThucLuotBanSaoUltron((byte)viTriLuot,
                            VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
                    return;
                }
                short dichY = (short)Math.max(0, this.phienQuanY[chiSoPhienQuan] - 12);
                short batDauY = (short)Math.max(0, banSao.y - 12);
                short[][] duongDan = this.tinhDuongDan.taoDuongDanThang(
                        banSao.x, batDauY, this.phienQuanX[chiSoPhienQuan], dichY);
                short goc = this.tinhDuongDan.tinhGocToiMucTieu(
                        banSao.x, batDauY, this.phienQuanX[chiSoPhienQuan], dichY);
                this.nguoiChoi.dichVu.guiKetQuaBanLuyenTapNangCao((byte)0,
                        banSao.chiSo, LOAI_DAN_ULTRON, banSao.x, banSao.y,
                        goc, (byte)30, (byte)0,
                        new short[][]{duongDan[0]}, new short[][]{duongDan[1]},
                        (byte)1, (byte)0, (short)-1, (short)-1);
                int satThuong = VXLTinhSatThuong.tinhSauGiap(
                        Math.max(10, banSao.tanCong + 10), this.giapPhienQuan);
                this.apDungSatThuongSkillPhienQuan(chiSoPhienQuan, satThuong);
                if (this.phienQuanDaChet[chiSoPhienQuan]) {
                    this.hoanThanhPhienQuan();
                    return;
                }
                this.tacVuPhienQuan = BO_LAP_LICH.schedule(
                        () -> this.ketThucLuotBanSaoUltron((byte)viTriLuot,
                                banSao.layThoiGianNapDan()),
                        1000L, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception ex) {
            this.ketThucLuotBanSaoUltron((byte)viTriLuot,
                    VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                    "Khong the xu ly luot ban sao Ultron.", ex);
        }
    }

    private void ketThucLuotBanSaoUltron(byte viTriLuot, int thoiGianNapDan) {
        try {
            synchronized (this) {
                this.tacVuPhienQuan = null;
                if (!this.nguoiChoi.inTraining) {
                    this.phienQuanDangHoatDong = false;
                    return;
                }
                this.phienQuanDangHoatDong = false;
                this.chuyenLuotSauHanhDong(viTriLuot, thoiGianNapDan);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                    "Khong the ket thuc luot ban sao Ultron.", ex);
        }
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
        this.huyTacVuHetLuot();
        this.huyTacVuPhienQuan();
        if (this.tacVuKetThucPhienQuan != null) {
            this.tacVuKetThucPhienQuan.cancel(false);
            this.tacVuKetThucPhienQuan = null;
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
        this.maPhatBanPhienQuan++;
        this.xoaSatThuongPhienQuanDangCho();
    }

    private void choPhienQuanBan() {
        try {
            synchronized (this) {
                this.tacVuPhienQuan = null;
                if (!this.nguoiChoi.inTraining || this.dangChoKetThucPhatBan
                        || !this.phienQuanDangHoatDong) {
                    return;
                }
                int chiSoPhienQuan = this.layPhienQuanTiepTheo();
                if (chiSoPhienQuan < 0) {
                    this.hoanThanhPhienQuan();
                    return;
                }
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
            this.ketThucLuotPhienQuan(VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
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
            this.ketThucLuotPhienQuan(VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                    "Lỗi Phiến quân bắn sau khi bay.", ex);
        }
    }

    private void phienQuanBanVaLapLichKetThuc(int chiSoPhienQuan) throws IOException {
        long maPhatBan = ++this.maPhatBanPhienQuan;
        long treDuPhongKetThuc = this.phienQuanBanTra(chiSoPhienQuan);
        if (!this.nguoiChoi.inTraining || !this.dangChoKetThucDanPhienQuan) {
            if (!this.nguoiChoi.inTraining) {
                this.phienQuanDangHoatDong = false;
                this.xoaSatThuongPhienQuanDangCho();
            }
            return;
        }
        if (this.tacVuKetThucPhienQuan != null) {
            this.tacVuKetThucPhienQuan.cancel(false);
        }
        this.tacVuKetThucPhienQuan = BO_LAP_LICH.schedule(
                () -> this.hoanTatDanPhienQuan(maPhatBan, false),
                treDuPhongKetThuc, TimeUnit.MILLISECONDS);
    }

    private void ketThucLuotPhienQuan() {
        this.ketThucLuotPhienQuan(
                VXLChienBinh.layThoiGianNapDanTheoMaVuKhi(this.vuKhiPhienQuanHienTai));
    }

    private void ketThucLuotPhienQuan(int thoiGianNapDan) {
        try {
            synchronized (this) {
                this.tacVuKetThucPhienQuan = null;
                this.dangChoKetThucDanPhienQuan = false;
                this.satThuongGocPhienQuanDangCho = 0;
                this.satThuongPhienQuanDangCho = 0;
                if (!this.nguoiChoi.inTraining) {
                    this.phienQuanDangHoatDong = false;
                    return;
                }
                this.phienQuanDangHoatDong = false;
                if (this.mauNguoiChoi <= 0) {
                    this.thuaLuyenTap(TRE_HIEN_KET_QUA_SAU_HIEU_UNG);
                    return;
                }
                this.chuyenLuotSauHanhDong((byte)1, thoiGianNapDan);
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
                int chiSoVaCham = Math.min(duongDaDung[0].length,
                        duongDaDung[1].length) - 1;
                if (chiSoVaCham >= 0) {
                    phatBan.vaChamDiaHinhX[i] = duongDaDung[0][chiSoVaCham];
                    phatBan.vaChamDiaHinhY[i] = duongDaDung[1][chiSoVaCham];
                }
                soVienTrungNguoiChoi++;
            }
        }
        boolean trungTrucTiepNguoiChoi = soVienTrungNguoiChoi > 0;
        VXLDiaHinhPhatBan.ghiNhanLo(this.tinhDuongDan.layBanDo(), loaiDan,
                (byte)0, phatBan);
        VXLCauHinhVatPhamChienDau.DiemSieuCao diemSieuCao =
                VXLCauHinhVatPhamChienDau.timDiemSieuCao(
                        this.phienQuanY[chiSoPhienQuan], phatBan.duongX,
                        phatBan.duongY, loaiDan, (byte)0);
        boolean sieuCaoTrungNguoiChoi = trungTrucTiepNguoiChoi && diemSieuCao.kichHoat();

        VXLThoiLuongPhatBan.KetQua thoiLuong = VXLThoiLuongPhatBan.tinh(
                loaiDan, phatBan.duongX, phatBan.duongY);
        boolean danManh = this.danManhPhienQuan;
        this.danManhPhienQuan = false;
        int heSoDan = danManh
                ? VXLCauHinhPhienQuan.HE_SO_DAN_MANH
                : VXLCauHinhPhienQuan.HE_SO_DAN_THUONG;
        int heSoTrangThaiPhienQuan = VXLCauHinhVatPhamChienDau.layHeSoSatThuongTrangThai(
                sieuCaoTrungNguoiChoi, false);
        int heSoTongPhienQuan = (int)Math.max(1L, (long)heSoDan * heSoTrangThaiPhienQuan / 100L);
        int satThuongCoBan = VXLTinhSatThuong.tinhPhatBanCoDaoDong(
                this.tanCongPhienQuan, luc, heSoTongPhienQuan);
        int satThuongMoiVien = VXLCauHinhVatPhamChienDau.tinhSatThuongMoiVien(
                satThuongCoBan, loaiDan, (byte)0, (byte)0);
        int tranSatThuong = phatBan.truotRaNgoaiBanDo ? 0 : satThuongCoBan
                * VXLCauHinhVatPhamChienDau.layTranPhanTramSatThuong(loaiDan, (byte)0) / 100;
        int satThuongTrucTiep = Math.min(tranSatThuong,
                satThuongMoiVien * soVienTrungNguoiChoi);
        int satThuongNo = VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                phatBan.vaChamDiaHinhX, phatBan.vaChamDiaHinhY, this.nguoiChoiX, this.nguoiChoiY,
                loaiDan, (byte)0, satThuongMoiVien, tranSatThuong);
        int satThuongGoc = Math.max(satThuongTrucTiep, satThuongNo);
        this.satThuongGocPhienQuanDangCho = Math.max(0, satThuongGoc);
        this.satThuongPhienQuanDangCho = satThuongGoc > 0
                ? VXLTinhSatThuong.tinhSauGiap(satThuongGoc, this.chiSoNguoiChoi.giap)
                : 0;
        this.dangChoKetThucDanPhienQuan = true;

        VXLQuanLyMayChu.log(String.format(
                "[TRAINING-BOT-FIRE] %s | type=%d | angle=%d | force=%d | pos=(%d,%d)"
                        + " | paths=%d | maxPoints=%d | prepareMs=%d | burstMs=%d"
                        + " | flightMs=%d | fallbackMs=%d",
                VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai),
                Byte.toUnsignedInt(loaiDan), goc, Byte.toUnsignedInt(luc),
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan],
                thoiLuong.soDuongDan(), thoiLuong.soDiemLonNhat(),
                thoiLuong.treChuanBi(), thoiLuong.treSinhLoat(),
                thoiLuong.treBayToiDa(), thoiLuong.treDuPhongKetThuc()));

        try {
            this.nguoiChoi.dichVu.guiKetQuaBanLuyenTapNangCao((byte)0,
                    (byte)(chiSoPhienQuan + 1), loaiDan,
                    this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan], goc, luc,
                    this.layChiSoTachClient(loaiDan, luc, phatBan),
                    phatBan.duongX, phatBan.duongY, (byte)1,
                    (byte)(sieuCaoTrungNguoiChoi ? 1 : 0),
                    sieuCaoTrungNguoiChoi ? diemSieuCao.x() : (short)-1,
                    sieuCaoTrungNguoiChoi ? diemSieuCao.y() : (short)-1);
        }
        catch (IOException ex) {
            this.xoaSatThuongPhienQuanDangCho();
            throw ex;
        }
        return thoiLuong.treDuPhongKetThuc();
    }

    private void hoanTatDanPhienQuan(long maPhatBan, boolean xacNhanTuClient) {
        synchronized (this) {
            if (!this.nguoiChoi.inTraining || !this.phienQuanDangHoatDong
                    || !this.dangChoKetThucDanPhienQuan
                    || maPhatBan != this.maPhatBanPhienQuan) {
                return;
            }
            ScheduledFuture<?> tacVuHienTai = this.tacVuKetThucPhienQuan;
            this.tacVuKetThucPhienQuan = null;
            if (xacNhanTuClient && tacVuHienTai != null) {
                tacVuHienTai.cancel(false);
            }
            this.dangChoKetThucDanPhienQuan = false;
            try {
                this.apDungSatThuongPhienQuanDangCho();
            }
            catch (Exception ex) {
                Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE,
                        "Khong the ap dung sat thuong cua Phien quan.", ex);
            }
            if (!this.nguoiChoi.inTraining || !this.phienQuanDangHoatDong) {
                return;
            }
            long treKetThucLuot = xacNhanTuClient ? TRE_SAU_XAC_NHAN_DAN_BOT : 0L;
            VXLQuanLyMayChu.log("[TRAINING-BOT-SHOT-END] shot=" + maPhatBan
                    + " | source=" + (xacNhanTuClient ? "client-ack" : "timeout")
                    + " | nextTurnDelayMs=" + treKetThucLuot);
            this.tacVuKetThucPhienQuan = BO_LAP_LICH.schedule(
                    () -> this.ketThucLuotPhienQuan(), treKetThucLuot,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void apDungSatThuongPhienQuanDangCho() throws IOException {
        int satThuongGoc = this.satThuongGocPhienQuanDangCho;
        int satThuong = this.satThuongPhienQuanDangCho;
        this.satThuongGocPhienQuanDangCho = 0;
        this.satThuongPhienQuanDangCho = 0;
        if (satThuongGoc <= 0 || satThuong <= 0 || this.mauNguoiChoi <= 0) {
            return;
        }
        int mauTruoc = this.mauNguoiChoi;
        this.mauNguoiChoi = Math.max(0, this.mauNguoiChoi - satThuong);
        this.chiSoNguoiChoi.hp = this.mauNguoiChoi;
        boolean nguoiChoiGucNga = this.mauNguoiChoi <= 0;
        System.out.println(String.format(
                "[TRAINING-BOT-HIT] %s hit %s | base=%d | armor=%d | damage=%d | HP: %d -> %d/%d",
                VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), this.nguoiChoi.ten,
                satThuongGoc, this.chiSoNguoiChoi.giap, satThuong, mauTruoc,
                this.mauNguoiChoi, this.chiSoNguoiChoi.mauToiDa));
        this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0, this.mauNguoiChoi,
                this.chiSoNguoiChoi.mauToiDa, nguoiChoiGucNga ? (byte)2 : (byte)0);
    }

    private void xoaSatThuongPhienQuanDangCho() {
        this.dangChoKetThucDanPhienQuan = false;
        this.satThuongGocPhienQuanDangCho = 0;
        this.satThuongPhienQuanDangCho = 0;
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
        this.chiSoNguoiChoi.hp = this.mauNguoiChoi;
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
        if (this.chiSoNguoiChoi != null && this.chiSoNguoiChoi.kyNangAvenger != null
                && this.chiSoNguoiChoi.kyNangAvenger.laSkillRieng()) {
            return;
        }
        int truoc = this.noKyNangDacBiet;
        this.noKyNangDacBiet = Math.min(100,
                this.noKyNangDacBiet + NO_TANG_MOI_LAN_DOI_LUOT);
        if (this.noKyNangDacBiet != truoc) {
            this.nguoiChoi.dichVu.guiNoDau((byte)0, (byte)this.noKyNangDacBiet);
        }
    }

    private void taoGioMoi() {
        VXLGioChienDau.HuongGio gioMoi = VXLGioChienDau.taoMoi(this.gioX, this.gioY);
        this.gioX = gioMoi.x();
        this.gioY = gioMoi.y();
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

    private void capNhatViTriPhienQuanSauPhaDiaHinh() throws IOException {
        int chieuCaoBanDo = this.tinhDuongDan.layBanDo().getHeight();
        for (int chiSoPhienQuan = 0; chiSoPhienQuan < SO_PHIEN_QUAN; chiSoPhienQuan++) {
            if (this.phienQuanDaChet[chiSoPhienQuan]) {
                continue;
            }
            short x = this.phienQuanX[chiSoPhienQuan];
            short yCu = this.phienQuanY[chiSoPhienQuan];
            short yMoi = this.timViTriDatChoPhienQuan(x, yCu);
            if (yMoi <= yCu) {
                continue;
            }
            this.phienQuanY[chiSoPhienQuan] = yMoi;
            this.nguoiChoi.dichVu.guiBayLuyenTap(
                    (byte)(chiSoPhienQuan + 1), x, yMoi);
            if (yMoi >= chieuCaoBanDo - 1) {
                this.mauPhienQuan[chiSoPhienQuan] = 0;
                this.phienQuanDaChet[chiSoPhienQuan] = true;
                this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap(
                        (byte)(chiSoPhienQuan + 1), 0,
                        this.mauToiDaPhienQuan, (byte)2);
                VXLQuanLyMayChu.log("[TRAINING-FALL] rebel fell out index="
                        + chiSoPhienQuan + " x=" + x + " fromY=" + yCu
                        + " toY=" + yMoi);
                continue;
            }
            VXLQuanLyMayChu.log("[TRAINING-FALL] rebel landed index="
                    + chiSoPhienQuan + " x=" + x + " fromY=" + yCu
                    + " toY=" + yMoi);
        }
    }

    private short timViTriDatChoPhienQuan(short x, short yBatDau) {
        int chieuRongBanDo = this.tinhDuongDan.layBanDo().getWidth();
        int chieuCaoBanDo = this.tinhDuongDan.layBanDo().getHeight();
        int[] cacLechX = new int[]{-NUA_RONG_THAN_PHIEN_QUAN, 0,
                NUA_RONG_THAN_PHIEN_QUAN};
        int yGanNhat = chieuCaoBanDo - 1;
        for (int lechX : cacLechX) {
            short xChan = (short)Math.max(0, Math.min(chieuRongBanDo - 1, x + lechX));
            short yDat = this.tinhDuongDan.layBanDo().timViTriDat(xChan, yBatDau);
            yGanNhat = Math.min(yGanNhat, yDat);
        }
        return (short)yGanNhat;
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
            case 56:
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
        this.maPhatBanNguoiChoi++;
        this.phienBanDuPhongPhatBan++;
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
        this.skillRiengPhatToi = false;
        this.phatBanKyNangDacBiet = false;
        this.phatBanSieuCao = false;
        java.util.Arrays.fill(this.banSaoUltron, null);
    }
}
