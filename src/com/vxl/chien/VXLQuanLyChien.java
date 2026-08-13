package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLTinNhan;
import com.vxl.phong.VXLChoDau;
import com.vxl.tienich.VXLThoiGianLuot;
import com.vxl.vatpham.VXLVatPham;
import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLQuanLyChien {
    public static final byte MA_BAN_DO_HAI_TOA_THAP = 51;
    private static final byte KET_QUA_THUA = 0;
    private static final byte KET_QUA_THANG = 1;
    private static final byte KET_QUA_HOA = 2;
    private static final int MAX_FIGHTERS = 40;
    private static final int NO_TANG_MOI_LAN_DOI_LUOT = 10;
    private static final long THOI_GIAN_CHO_KET_THUC_PHAT_BAN = 20_000L;
    private static final long TRE_PHAT_BAN_CO_BAN = 1_200L;
    private static final long TRE_MOI_DIEM_DUONG_DAN = 28L;
    private static final long TRE_PHAT_BAN_TOI_THIEU = 1_800L;
    private static final long TRE_PHAT_BAN_TOI_DA = 7_000L;
    private static final long TRE_KET_THUC_SKILL_ROI = 1_800L;
    private static final byte LOAI_DAN_HAWKEYE_SKILL = 9;
    private static final byte LOAI_DAN_THOR_SKILL = 0;
    private static final int SO_LUONG_LUONG_LAP_LICH_LUOT = Math.max(2,
            Math.min(4, Runtime.getRuntime().availableProcessors()));
    private static final ScheduledExecutorService BO_LAP_LICH_LUOT =
            Executors.newScheduledThreadPool(SO_LUONG_LUONG_LAP_LICH_LUOT, tacVu -> {
                Thread thread = new Thread(tacVu, "vxl-thoi-gian-luot-tran-dau");
                thread.setDaemon(true);
                return thread;
            });
    private final VXLChoDau wait;
    private final VXLChienBinh[] chienBinhs = new VXLChienBinh[MAX_FIGHTERS];
    private final VXLQuanLyBanDo map;
    private final boolean cheDoCamTu;
    private final VXLXuLyVatPhamTrongTran xuLyVatPham;
    private final VXLXuLyKetThucTranDau xuLyKetThuc;
    private final VXLTinhDuongDan tinhDuongDan;
    private final VXLPhatTinTranDau phatTin;
    private final VXLDieuKhienBotTranDau dieuKhienBot;
    private final VXLDichVuHaiToaThap dichVuHaiToaThap;
    private final int[] napDan = new int[MAX_FIGHTERS];
    private final long[] thuTuHanhDongNapDan = new long[MAX_FIGHTERS];
    private long boDemThuTuHanhDongNapDan;
    private int napDanSauHanhDong = -1;
    private byte luotHienTai = -1;
    private boolean daKetThuc;
    private boolean daYeuCauDonTran;
    private long hanLuot;
    private long phienBanLuot;
    private ScheduledFuture<?> tacVuHetLuot;
    private byte phatBanDangChoKetThuc = -1;
    private byte gioX;
    private byte gioY;
    private final List<BomHenGio> bomHenGios = new ArrayList<>();
    private byte chiSoBomTiepTheo;
    private short tornadoX = -1;
    private short tornadoY = -1;
    private int luotTornado;

    private static final int SO_LUOT_BOM_HEN_GIO = 3;
    private static final int SO_LUOT_TORNADO = 3;

    private static final class BomHenGio {
        private final byte ma;
        private final VXLChienBinh nguoiDat;
        private final byte loaiDan;
        private final byte avengerDan;
        private short x;
        private short y;
        private final int satThuongMoiVien;
        private final int tranSatThuong;
        private int luotConLai;

        private BomHenGio(byte ma, VXLChienBinh nguoiDat, byte loaiDan,
                byte avengerDan, short x, short y, int satThuongMoiVien,
                int tranSatThuong, int luotConLai) {
            this.ma = ma;
            this.nguoiDat = nguoiDat;
            this.loaiDan = loaiDan;
            this.avengerDan = avengerDan;
            this.x = x;
            this.y = y;
            this.satThuongMoiVien = satThuongMoiVien;
            this.tranSatThuong = tranSatThuong;
            this.luotConLai = luotConLai;
        }
    }

    public VXLQuanLyChien(VXLChoDau wait, VXLNguoiChoi[] nguoiChois, byte maBanDo) {
        this.wait = wait;
        this.map = new VXLQuanLyBanDo(maBanDo);
        this.cheDoCamTu = maBanDo == MA_BAN_DO_HAI_TOA_THAP;
        this.xuLyVatPham = new VXLXuLyVatPhamTrongTran(this);
        this.xuLyKetThuc = new VXLXuLyKetThucTranDau(this.cheDoCamTu, this.chienBinhs);
        this.tinhDuongDan = new VXLTinhDuongDan(this.map, this.chienBinhs,
                this::danNamTrongVungVoiRong);
        this.phatTin = new VXLPhatTinTranDau(this.chienBinhs);
        this.dieuKhienBot = new VXLDieuKhienBotTranDau(this, this.chienBinhs, this.map, this.tinhDuongDan);
        this.dichVuHaiToaThap = this.cheDoCamTu
                ? new VXLDichVuHaiToaThap(this.map, this.chienBinhs) : null;
        if (nguoiChois == null) {
            return;
        }
        for (int i = 0; i < nguoiChois.length && i < this.chienBinhs.length; i++) {
            VXLNguoiChoi nguoiChoi = nguoiChois[i];
            if (nguoiChoi == null) {
                continue;
            }
            short x = this.map.laySinhX(i);
            short y = this.map.laySinhY(i);
            this.chienBinhs[i] = new VXLChienBinh(nguoiChoi, (byte)i, x, y);
        }
        if (this.dichVuHaiToaThap != null) {
            this.dichVuHaiToaThap.khoiTao();
        }
    }

    public synchronized void themBot(byte chiSo, String ten, short maVuKhi, byte avenger) {
        this.themBot(chiSo, ten, maVuKhi, avenger, 220, 35, 15);
    }

    public synchronized void themBot(byte chiSo, String ten, short maVuKhi, byte avenger,
            int mauToiDa, int tanCong, int giap) {
        if (!this.chiSoHopLe(chiSo) || this.chienBinhs[chiSo] != null) {
            return;
        }
        this.chienBinhs[chiSo] = new VXLChienBinh(chiSo, this.map.laySinhX(chiSo),
                this.map.laySinhY(chiSo), ten, maVuKhi, avenger, mauToiDa, tanCong, giap);
    }

    public synchronized void themCamTu(byte chiSo, String ten, short maVuKhi, byte avenger) {
        if (!this.chiSoHopLe(chiSo) || this.chienBinhs[chiSo] != null) {
            return;
        }
        this.chienBinhs[chiSo] = new VXLChienBinh(chiSo, this.map.laySinhX(chiSo), this.map.laySinhY(chiSo), ten, maVuKhi, avenger, true);
    }

    public synchronized boolean laCheDoCamTu() {
        return this.cheDoCamTu;
    }

    public synchronized void batDau() throws IOException {
        if (this.daKetThuc) {
            return;
        }
        this.phatTin.guiBatDau(this.map.layMaBanDo(), this.map.layMaNen());
        this.phatTin.guiManHinhChienDau();
        this.phatTin.guiDongBoMauBanDau();
        if (this.dichVuHaiToaThap != null) {
            this.dichVuHaiToaThap.guiDoiQuan(this.phatTin);
        }
        if (this.kiemTraKetThuc()) {
            return;
        }
        this.chuanBiLuotTiepTheo((byte)-1);
        this.dieuKhienBot.batDau();
    }

    public synchronized void diChuyen(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (!this.coTheHanhDong(chienBinh)
                || chienBinh.luotDongBang > 0 || chienBinh.luotMacTo > 0) {
            return;
        }
        if (ms == null || ms.layDuLieu().length != 4) {
            return;
        }
        short xYeuCau = ms.boDoc().readShort();
        short yYeuCau = ms.boDoc().readShort();
        int tamDiChuyen = chienBinh.layTamDiChuyen(180);
        boolean duocPhepBay = chienBinh.avenger == 1 || chienBinh.avenger == 8;
        short[] toaDo = this.tinhDuongDan.gioiHanDiChuyenNguoiChoi(
                chienBinh.x, chienBinh.y, xYeuCau, yYeuCau,
                tamDiChuyen, duocPhepBay);
        chienBinh.x = toaDo[0];
        chienBinh.y = toaDo[1];
        chienBinh.heSoDiChuyen = 100;
        this.phatDiChuyen(chienBinh);
    }

    public synchronized void capNhatXY(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || chienBinh.chet || chienBinh.daRoiTran || this.daKetThuc) {
            return;
        }
        if (ms == null || ms.layDuLieu().length != 4) {
            return;
        }
        short xBaoCao = ms.boDoc().readShort();
        short yBaoCao = ms.boDoc().readShort();
        if (xBaoCao < 0 || xBaoCao >= this.map.getWidth()
                || yBaoCao < 0 || yBaoCao >= this.map.getHeight()) {
            this.xuLyRoiKhoiBanDo(chienBinh, xBaoCao, yBaoCao);
            return;
        }
        if (chienBinh.avenger == 1 || chienBinh.avenger == 8) {
            nguoiChoi.dichVu.guiCapNhatXYLuyenTap(
                    chienBinh.chiSo, chienBinh.x, chienBinh.y);
            return;
        }
        short yRoi = this.tinhDuongDan.timViTriRoiThang(chienBinh.x, chienBinh.y);
        if (yRoi == Short.MIN_VALUE) {
            this.xuLyRoiKhoiBanDo(chienBinh, chienBinh.x,
                    (short)(this.map.getHeight() + 1));
            return;
        }
        if (yRoi != chienBinh.y) {
            chienBinh.y = yRoi;
            this.phatCapNhatXY(chienBinh);
            return;
        }
        nguoiChoi.dichVu.guiCapNhatXYLuyenTap(
                chienBinh.chiSo, chienBinh.x, chienBinh.y);
    }

    private void xuLyRoiKhoiBanDo(VXLChienBinh chienBinh, short x, short y) throws IOException {
        chienBinh.x = x;
        chienBinh.y = y;
        chienBinh.hp = 0;
        this.danhDauChet(null, chienBinh);
        VXLQuanLyMayChu.log("[FALL-OUT] player=" + chienBinh.ten + " x=" + x + " y=" + y);
        this.phatCapNhatMau(chienBinh);
        if (!this.kiemTraKetThuc() && this.luotHienTai == chienBinh.chiSo) {
            this.sangLuot(chienBinh.chiSo);
        }
    }

    public synchronized void doiSung(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        int chiSoBalo = ms.boDoc().readUnsignedByte();
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
        if (!this.coTheHanhDong(chienBinh) || chienBinh.avengerDan > 0) {
            return;
        }
        VXLVatPham vuKhi = nguoiChoi.layVuKhiTrongBalo(chiSoBalo);
        if (vuKhi == null || chienBinh.maVuKhi == vuKhi.mau.part) {
            return;
        }
        VXLVatPham vuKhiCu = nguoiChoi.doiVuKhiTrongBalo(chiSoBalo);
        if (vuKhiCu == null) {
            return;
        }
        chienBinh.maVuKhi = nguoiChoi.wp;
        chienBinh.capNhatTanCongTheoTrangBi();
        chienBinh.batDauNapDan();
        nguoiChoi.dichVu.guiTuiDo();
        nguoiChoi.dichVu.guiDoTrenNguoi();
        nguoiChoi.dichVu.guiBalo();
        nguoiChoi.dichVu.doiTrangBi();
        nguoiChoi.flushCache();
        this.phatTin.guiDoiSung(chienBinh, vuKhiCu.mau.iconID);
    }

    public synchronized void ban(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh nguoiBan = this.layChienBinh(nguoiChoi);
        if (!this.coTheHanhDong(nguoiBan)) {
            VXLQuanLyMayChu.log("[FIRE] reject player=" + (nguoiChoi != null ? nguoiChoi.ten : "null")
                    + " fighter=" + (nguoiBan != null ? nguoiBan.chiSo : -1)
                    + " current=" + this.luotHienTai
                    + " ended=" + this.daKetThuc);
            return;
        }
        if (!nguoiBan.daNapDan()) {
            return;
        }
        byte loaiDanKhachGui = ms.boDoc().readByte();
        short x = ms.boDoc().readShort();
        short y = ms.boDoc().readShort();
        short goc = ms.boDoc().readShort();
        byte luc = ms.boDoc().readByte();
        byte lucTach = 0;
        if (loaiDanKhachGui == 17 || loaiDanKhachGui == 19) {
            lucTach = ms.boDoc().readByte();
        }
        int soPhat = Byte.toUnsignedInt(ms.boDoc().readByte());
        soPhat = Math.max(1, Math.min(4, soPhat));
        luc = (byte)Math.max(10, Math.min(30, luc));
        goc = (short)((goc % 360 + 360) % 360);
        VXLQuanLyMayChu.log("[FIRE] accept player=" + nguoiBan.ten
                + " index=" + nguoiBan.chiSo
                + " bullet=" + Byte.toUnsignedInt(loaiDanKhachGui)
                + " x=" + x + " y=" + y
                + " angle=" + goc
                + " force=" + Byte.toUnsignedInt(luc)
                + " splitForce=" + Byte.toUnsignedInt(lucTach)
                + " shots=" + soPhat);
        if (Math.abs(x - nguoiBan.x) > 24 || Math.abs(y - nguoiBan.y) > 24) {
            VXLQuanLyMayChu.log("[FIRE] coordinate mismatch player=" + nguoiBan.ten
                    + " server=" + nguoiBan.x + "," + nguoiBan.y
                    + " client=" + x + "," + y);
        }
        int maVatPhamDan = nguoiBan.vatPhamDanDacBiet;
        nguoiBan.vatPhamDanDacBiet = -1;
        soPhat = Math.max(soPhat, nguoiBan.soPhatToiThieu);
        VXLKyNangAvenger kyNangAvenger = nguoiBan.kyNangAvenger;
        byte avengerDanHieuLuc = kyNangAvenger != null
                ? kyNangAvenger.layAvengerDan(nguoiBan.avengerDan) : nguoiBan.avengerDan;
        short vuKhiHieuLuc = kyNangAvenger != null
                ? kyNangAvenger.layVuKhi(nguoiBan.maVuKhi) : nguoiBan.maVuKhi;
        if (avengerDanHieuLuc == VXLKyNangAvenger.MA_SPIDER_MAN) {
            soPhat = 1;
        }
        boolean skillRieng = nguoiBan.skillRiengPhatToi;
        boolean kyNangDacBiet = nguoiBan.kyNangDacBiet;
        byte loaiDanCoBan = maVatPhamDan >= 0
                ? VXLCauHinhVatPhamChienDau.layLoaiDan(maVatPhamDan, loaiDanKhachGui)
                : avengerDanHieuLuc > 0
                        ? VXLCauHinhVatPhamChienDau.layLoaiDanTheoAvenger(
                                avengerDanHieuLuc, loaiDanKhachGui)
                        : VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                                vuKhiHieuLuc, loaiDanKhachGui);
        byte loaiDan = kyNangAvenger != null
                ? kyNangAvenger.layLoaiDan(loaiDanCoBan, skillRieng)
                : loaiDanCoBan;
        this.napDanSauHanhDong = nguoiBan.batDauNapDan();
        VXLKetQuaDan ketQua = this.xuLyPhatBan(nguoiBan, loaiDan, goc, luc, lucTach,
                maVatPhamDan, kyNangDacBiet, skillRieng);
        this.batDauChoKetThucPhatBan(nguoiBan.chiSo, ketQua);
        this.ghiNhanDiaHinhPhatBan(ketQua);
        VXLKetQuaDan ketQuaHienThi = ketQua.nhanBanDuongDanHienThi(soPhat);
        this.phatBan(nguoiBan, ketQuaHienThi, (byte)soPhat);

        int satThuongThucTe = this.apDungSatThuongPhatBan(nguoiBan, ketQua, maVatPhamDan);
        this.apDungHieuUngDiemRoi(nguoiBan, ketQua, maVatPhamDan);
        if (kyNangAvenger != null) {
            kyNangAvenger.ghiNhanPhatBan(
                    maVatPhamDan < 0 && !kyNangDacBiet && !skillRieng);
        }
        nguoiBan.skillRiengPhatToi = false;
        nguoiBan.kyNangDacBiet = false;
        if (satThuongThucTe > 0 && nguoiBan.luotMaCaRong > 0) {
            int hoiMau = nguoiBan.hoiMau(Math.max(1, satThuongThucTe * 40 / 100));
            if (hoiMau > 0) {
                this.phatCapNhatMau(nguoiBan);
            }
        }
        if (nguoiBan.luotMaCaRong > 0) {
            nguoiBan.luotMaCaRong--;
        }
        nguoiBan.heSoPhatBan = 100;
        nguoiBan.ketThucPhatBan();
        this.kiemTraKetThuc();
    }

    public synchronized void focusSkill(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        byte hanhDong = ms.boDoc().readByte();
        int chiSoMucTieu = ms.boDoc().available() > 0
                ? Byte.toUnsignedInt(ms.boDoc().readByte()) : -1;
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
        if (!this.coTheHanhDong(chienBinh) || chienBinh.skillRiengPhatToi
                || chienBinh.kyNangAvenger == null) {
            return;
        }
        VXLKyNangAvenger kyNang = chienBinh.kyNangAvenger;
        VXLChienBinh mucTieu = kyNang.laThor()
                ? null : this.layMucTieuSkill(chienBinh, chiSoMucTieu);
        if ((kyNang.laLoki() || kyNang.laHawkeye())
                && mucTieu == null) {
            return;
        }
        int soBanSaoUltron = this.demBanSaoUltron(chienBinh);
        if (!kyNang.kichHoatSkill(hanhDong, chienBinh, soBanSaoUltron)) {
            return;
        }
        if (kyNang.laSpiderMan()) {
            chienBinh.skillRiengPhatToi = true;
            chienBinh.nguoiChoi.dichVu.guiXacNhanSkillSpiderMan();
            VXLQuanLyMayChu.log("[SPIDER-SKILL] armed player=" + chienBinh.ten);
            return;
        }
        if (kyNang.laLoki()) {
            kyNang.saoChepLoki(chienBinh, mucTieu);
            this.phatTin.guiLokiGiaDang(chienBinh, mucTieu);
            this.phatCapNhatMau(chienBinh);
            return;
        }
        if (kyNang.laHawkeye()) {
            this.xuLySkillHawkeye(chienBinh, mucTieu);
            return;
        }
        if (kyNang.laThor()) {
            this.xuLySkillThor(chienBinh);
            return;
        }
        if (kyNang.laUltron()) {
            this.taoBanSaoUltron(chienBinh);
        }
    }

    private VXLChienBinh layMucTieuSkill(VXLChienBinh nguoiDung, int chiSoMucTieu) {
        if (chiSoMucTieu < 0 || chiSoMucTieu >= this.chienBinhs.length) {
            return null;
        }
        VXLChienBinh mucTieu = this.chienBinhs[chiSoMucTieu];
        if (mucTieu == null || mucTieu == nguoiDung || mucTieu.chet
                || mucTieu.daRoiTran || this.cungDoi(nguoiDung, mucTieu)) {
            return null;
        }
        return mucTieu;
    }

    private void xuLySkillHawkeye(VXLChienBinh nguoiDung, VXLChienBinh mucTieu)
            throws IOException {
        short[] cacX = new short[]{(short)(mucTieu.x - 20), (short)(mucTieu.x - 5),
                (short)(mucTieu.x + 5), (short)(mucTieu.x + 20)};
        short[] cacY = new short[]{mucTieu.y, mucTieu.y, mucTieu.y, mucTieu.y};
        this.phatTin.guiDiemRoiSkill(nguoiDung.chiSo, LOAI_DAN_HAWKEYE_SKILL, cacX, cacY);
        int satThuongMoiMui = Math.max(8,
                VXLTinhSatThuong.tinhSauGiap(20 + nguoiDung.tanCong, mucTieu.giap));
        this.satThuong(nguoiDung, mucTieu, satThuongMoiMui * cacX.length,
                true, false, false);
        this.batDauChoKetThucSkill(nguoiDung, TRE_KET_THUC_SKILL_ROI);
        this.kiemTraKetThuc();
    }

    private void xuLySkillThor(VXLChienBinh nguoiDung)
            throws IOException {
        int[] lechX = new int[]{-30, -10, 10, 30};
        short[] cacX = new short[lechX.length];
        short[] cacY = new short[lechX.length];
        for (int i = 0; i < lechX.length; i++) {
            cacX[i] = (short)Math.max(4,
                    Math.min(this.map.getWidth() - 5, nguoiDung.x + lechX[i]));
            cacY[i] = this.map.timViTriDat(cacX[i], nguoiDung.y);
            this.map.taoLoTheoMatNa(cacX[i], cacY[i], "h36x30.png");
        }
        this.phatTin.guiDiemRoiSkill(nguoiDung.chiSo, LOAI_DAN_THOR_SKILL, cacX, cacY);
        int satThuongMoiDiem = Math.max(12, 18 + nguoiDung.tanCong / 2);
        Map<VXLChienBinh, Integer> satThuongTheoMucTieu = new LinkedHashMap<>();
        for (int i = 0; i < cacX.length; i++) {
            for (VXLChienBinh chienBinh : this.chienBinhs) {
                if (chienBinh == null || chienBinh.chet || chienBinh.daRoiTran
                        || this.cungDoi(nguoiDung, chienBinh)) {
                    continue;
                }
                int dx = chienBinh.x - cacX[i];
                int dy = chienBinh.y - cacY[i];
                if (dx * dx + dy * dy <= 52 * 52) {
                    satThuongTheoMucTieu.merge(chienBinh, satThuongMoiDiem, Integer::sum);
                }
            }
        }
        for (Map.Entry<VXLChienBinh, Integer> muc : satThuongTheoMucTieu.entrySet()) {
            this.satThuong(nguoiDung, muc.getKey(),
                    Math.min(satThuongMoiDiem * 4, muc.getValue()), false, false, false);
        }
        this.batDauChoKetThucSkill(nguoiDung, TRE_KET_THUC_SKILL_ROI);
        this.kiemTraKetThuc();
    }

    private void taoBanSaoUltron(VXLChienBinh chu) throws IOException {
        int viTri = this.timViTriTrongChoBanSao();
        if (viTri < 0) {
            return;
        }
        int soBanSao = this.demBanSaoUltron(chu);
        int huong = soBanSao % 2 == 0 ? 1 : -1;
        short x = (short)Math.max(16, Math.min(this.map.getWidth() - 17,
                chu.x + huong * (28 + soBanSao * 12)));
        short y = this.map.timViTriDat(x, chu.y);
        VXLChienBinh banSao = VXLChienBinh.taoBanSaoUltron((byte)viTri, chu.chiSo,
                x, y, chu.ten, Math.max(80, chu.mauToiDa / 3),
                Math.max(12, chu.tanCong / 2), Math.max(0, chu.giap / 2));
        this.chienBinhs[viTri] = banSao;
        this.napDan[viTri] = 0;
        this.boDemThuTuHanhDongNapDan = VXLHangDoiNapDan.ghiNhanHanhDong(
                this.thuTuHanhDongNapDan, viTri, this.boDemThuTuHanhDongNapDan);
        this.phatTin.guiThemBanSaoUltron(banSao, chu.chiSo);
        this.batDauChoKetThucSkill(chu, 900L);
    }

    private int timViTriTrongChoBanSao() {
        for (int i = this.chienBinhs.length - 1; i >= 0; i--) {
            if (this.chienBinhs[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private int demBanSaoUltron(VXLChienBinh chu) {
        if (chu == null) {
            return 0;
        }
        int soLuong = 0;
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.laBanSaoUltron()
                    && chienBinh.chiSoChuBanSaoUltron == chu.chiSo && !chienBinh.chet) {
                soLuong++;
            }
        }
        return soLuong;
    }

    private void batDauChoKetThucSkill(VXLChienBinh nguoiDung, long tre) {
        this.napDanSauHanhDong = VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU;
        this.phatBanDangChoKetThuc = nguoiDung.chiSo;
        this.hanLuot = System.currentTimeMillis() + Math.max(400L, tre);
        this.lapLichHetLuot(nguoiDung.chiSo, this.hanLuot);
    }

    public synchronized boolean dungVatPham(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        return this.xuLyVatPham.xuLy(nguoiChoi, ms);
    }
    public synchronized void kiemTraVaCham(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || this.phatBanDangChoKetThuc != chienBinh.chiSo
                || this.luotHienTai != chienBinh.chiSo || this.daKetThuc) {
            return;
        }
        VXLQuanLyMayChu.log("[FIRE] animation complete player=" + chienBinh.ten
                + " index=" + chienBinh.chiSo);
        this.sangLuot(chienBinh.chiSo);
    }

    public synchronized void boLuot(VXLNguoiChoi nguoiChoi) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (this.coTheHanhDong(chienBinh)) {
            this.sangLuot(chienBinh.chiSo);
        }
    }

    public synchronized void khiNguoiChoiRoi(VXLNguoiChoi nguoiChoi) {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || chienBinh.daRoiTran) {
            return;
        }
        chienBinh.daRoiTran = true;
        if (!chienBinh.chet) {
            chienBinh.chet = true;
            chienBinh.hp = 0;
            nguoiChoi.chet++;
        }
        this.phatCapNhatMau(chienBinh);

        this.xuLyKetThuc.quyetToanRoiTran(chienBinh);
        try {
            this.kiemTraKetThuc();
            if (!this.daKetThuc && this.luotHienTai == chienBinh.chiSo) {
                this.sangLuot(chienBinh.chiSo);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(VXLQuanLyChien.class.getName()).log(Level.WARNING, "Lỗi xử lý sau khi người chơi rời trận.", ex);
        }
    }

    public synchronized void dungBot() {
        this.hanLuot = 0L;
        this.huyTacVuHetLuot();
        this.phatBanDangChoKetThuc = -1;
        this.dieuKhienBot.dung();
    }

    boolean coTheHanhDong(VXLChienBinh chienBinh) {
        return chienBinh != null && !chienBinh.chet && !chienBinh.daRoiTran && !this.daKetThuc
                && chienBinh.chiSo == this.luotHienTai
                && this.phatBanDangChoKetThuc != chienBinh.chiSo
                && this.hanLuot > 0 && System.currentTimeMillis() <= this.hanLuot;
    }
    VXLKetQuaDan xuLyPhatBan(VXLChienBinh nguoiBan, byte loaiDan, short goc, byte luc, int maVatPhamDan) {
        return this.xuLyPhatBan(nguoiBan, loaiDan, goc, luc, (byte)0, maVatPhamDan, false, false);
    }

    VXLKetQuaDan xuLyPhatBan(VXLChienBinh nguoiBan, byte loaiDan, short goc, byte luc,
            byte lucTach, int maVatPhamDan) {
        return this.xuLyPhatBan(nguoiBan, loaiDan, goc, luc, lucTach, maVatPhamDan, false, false);
    }

    private VXLKetQuaDan xuLyPhatBan(VXLChienBinh nguoiBan, byte loaiDan, short goc, byte luc,
            byte lucTach, int maVatPhamDan, boolean kyNangDacBiet, boolean skillRieng) {
        byte avengerDan = maVatPhamDan >= 0 ? 0
                : nguoiBan.kyNangAvenger != null
                        ? nguoiBan.kyNangAvenger.layAvengerDan(nguoiBan.avengerDan)
                        : nguoiBan.avengerDan;
        byte chiMang = (byte)(kyNangDacBiet ? 1 : 0);
        byte gioApDungX = nguoiBan.luotNgungGio > 0 ? 0 : this.gioX;
        byte gioApDungY = nguoiBan.luotNgungGio > 0 ? 0 : this.gioY;
        boolean epXuyenDiaHinh = nguoiBan.luotXuyenDiaHinh > 0;
        VXLHeThongDan.KetQuaPhatBan phatBan = this.tinhDuongDan.taoPhatBan(nguoiBan,
                loaiDan, chiMang, avengerDan, goc, luc, lucTach, gioApDungX, gioApDungY,
                epXuyenDiaHinh);
        VXLQuanLyMayChu.log("[FIRE] path player=" + nguoiBan.ten
                + " paths=" + phatBan.duongX.length
                + " points=" + phatBan.duongX[0].length
                + " from=" + phatBan.duongX[0][0] + ',' + phatBan.duongY[0][0]
                + " to=" + phatBan.duongX[0][phatBan.duongX[0].length - 1] + ','
                + phatBan.duongY[0][phatBan.duongY[0].length - 1]);
        int[] cacChiSoMucTieu = phatBan.layTatCaMucTieuTrung();
        VXLChienBinh[] mucTieuTheoQuyDao = new VXLChienBinh[cacChiSoMucTieu.length];
        int[] satThuongTheoQuyDao = new int[cacChiSoMucTieu.length];
        int heSoDan = VXLCauHinhVatPhamChienDau.layHeSoSatThuong(maVatPhamDan);
        VXLCauHinhVatPhamChienDau.DiemSieuCao diemSieuCao =
                VXLCauHinhVatPhamChienDau.timDiemSieuCao(nguoiBan.y, phatBan.duongX,
                        phatBan.duongY, loaiDan, avengerDan);
        boolean sieuCaoTrungMucTieu = false;
        for (int chiSoMucTieu : cacChiSoMucTieu) {
            if (chiSoMucTieu < 0 || chiSoMucTieu >= this.chienBinhs.length) {
                continue;
            }
            VXLChienBinh mucTieu = this.chienBinhs[chiSoMucTieu];
            if (mucTieu != null && !mucTieu.chet && mucTieu != nguoiBan) {
                sieuCaoTrungMucTieu = diemSieuCao.kichHoat();
                break;
            }
        }
        int heSoTrangThai = VXLCauHinhVatPhamChienDau.layHeSoSatThuongTrangThai(
                sieuCaoTrungMucTieu, kyNangDacBiet);
        int heSoTong = heSoDan * Math.max(100, nguoiBan.heSoPhatBan) / 100
                * heSoTrangThai / 100
                * (100 + nguoiBan.hieuUngClan.phanTramSatThuong(nguoiBan.maVuKhi)) / 100;
        int satThuongCoBan = VXLTinhSatThuong.tinhPhatBan(nguoiBan.tanCong, luc, heSoTong);
        int satThuongMoiVien = VXLCauHinhVatPhamChienDau.tinhSatThuongMoiVien(
                satThuongCoBan, loaiDan, chiMang, avengerDan);
        int tranSatThuong = phatBan.truotRaNgoaiBanDo ? 0 : satThuongCoBan
                * VXLCauHinhVatPhamChienDau.layTranPhanTramSatThuong(loaiDan, avengerDan)
                / 100;
        boolean skillToNhen = skillRieng
                && VXLCauHinhVatPhamChienDau.laDanToNhen(loaiDan);
        if (skillToNhen) {
            satThuongMoiVien = 0;
            tranSatThuong = 0;
        }
        int[] daPhanBoTheoMucTieu = new int[this.chienBinhs.length];
        for (int i = 0; i < cacChiSoMucTieu.length; i++) {
            int chiSoMucTieu = cacChiSoMucTieu[i];
            if (chiSoMucTieu < 0 || chiSoMucTieu >= this.chienBinhs.length) {
                continue;
            }
            VXLChienBinh mucTieu = this.chienBinhs[chiSoMucTieu];
            if (mucTieu == null || mucTieu.chet || mucTieu == nguoiBan) {
                continue;
            }
            if (skillToNhen) {
                mucTieuTheoQuyDao[i] = mucTieu;
                satThuongTheoQuyDao[i] = 0;
                continue;
            }
            int conLai = tranSatThuong - daPhanBoTheoMucTieu[chiSoMucTieu];
            if (conLai <= 0) {
                continue;
            }
            int satThuongVien = Math.min(satThuongMoiVien, conLai);
            mucTieuTheoQuyDao[i] = mucTieu;
            satThuongTheoQuyDao[i] = satThuongVien;
            daPhanBoTheoMucTieu[chiSoMucTieu] += satThuongVien;
        }
        return new VXLKetQuaDan(loaiDan, nguoiBan.x, nguoiBan.y, goc, luc, lucTach,
                chiMang, (byte)(sieuCaoTrungMucTieu ? 1 : 0),
                sieuCaoTrungMucTieu ? diemSieuCao.x() : (short)-1,
                sieuCaoTrungMucTieu ? diemSieuCao.y() : (short)-1,
                phatBan.duongX, phatBan.duongY, phatBan.vaChamDiaHinhX,
                phatBan.vaChamDiaHinhY, mucTieuTheoQuyDao, satThuongTheoQuyDao,
                satThuongMoiVien, tranSatThuong, avengerDan);
    }

    void ghiNhanDiaHinhPhatBan(VXLKetQuaDan ketQua) {
        int loaiDan = ketQua == null ? -1 : Byte.toUnsignedInt(ketQua.loaiDan);
        if (loaiDan == 5 || loaiDan == 13 || loaiDan == 51 || loaiDan == 53
                || loaiDan == 54 || loaiDan == 55 || loaiDan == 57 || loaiDan == 58) {
            return;
        }
        VXLDiaHinhPhatBan.ghiNhanLo(this.map, ketQua);
    }

    int apDungSatThuongPhatBan(VXLChienBinh nguoiBan, VXLKetQuaDan ketQua,
            int maVatPhamDan) throws IOException {
        if (maVatPhamDan == 221 || maVatPhamDan == 236 || maVatPhamDan == 250) {
            return 0;
        }
        Map<VXLChienBinh, Integer> tongTheoMucTieu = new LinkedHashMap<>();
        Set<VXLChienBinh> mucTieuTrucTiep = new HashSet<>();
        for (int i = 0; i < ketQua.mucTieuTheoQuyDao.length; i++) {
            VXLChienBinh mucTieu = ketQua.mucTieuTheoQuyDao[i];
            int satThuong = i < ketQua.satThuongTheoQuyDao.length
                    ? ketQua.satThuongTheoQuyDao[i] : 0;
            if (mucTieu != null
                    && !(nguoiBan.laBanSaoUltron() && this.cungDoi(nguoiBan, mucTieu))) {
                mucTieuTrucTiep.add(mucTieu);
                if (satThuong > 0) {
                    tongTheoMucTieu.merge(mucTieu, satThuong, Integer::sum);
                }
            }
        }
        boolean danNhanVatLao = VXLCauHinhVatPhamChienDau.layHoSoDan(
                ketQua.loaiDan, ketQua.avengerDan).kieuBan()
                == VXLHoSoDan.KieuBan.NHAN_VAT_LAO;
        boolean danCaptain = Byte.toUnsignedInt(ketQua.avengerDan) == 5;
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu.chet || mucTieu.daRoiTran
                    || danNhanVatLao && mucTieu == nguoiBan
                    || nguoiBan.laBanSaoUltron() && this.cungDoi(nguoiBan, mucTieu)) {
                continue;
            }
            int satThuongNo = VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                    ketQua.vaChamDiaHinhX, ketQua.vaChamDiaHinhY, mucTieu.x, mucTieu.y,
                    ketQua.loaiDan, ketQua.avengerDan, ketQua.satThuongMoiVien,
                    ketQua.tranSatThuong);
            if (satThuongNo > 0) {
                tongTheoMucTieu.merge(mucTieu, satThuongNo,
                        danCaptain ? Integer::sum : Math::max);
            }
        }
        int tongSatThuongThucTe = 0;
        for (VXLChienBinh mucTieu : mucTieuTrucTiep) {
            if (mucTieu != null && !mucTieu.chet && mucTieu != nguoiBan) {
                this.apDungHieuUngDan(nguoiBan, mucTieu, maVatPhamDan);
                if (nguoiBan.kyNangAvenger != null) {
                    nguoiBan.kyNangAvenger.apDungHieuUngTrungDan(ketQua, mucTieu);
                }
            }
        }
        for (Map.Entry<VXLChienBinh, Integer> muc : tongTheoMucTieu.entrySet()) {
            VXLChienBinh mucTieu = muc.getKey();
            int satThuongGoc = muc.getValue();
            int satThuongThucTe = this.satThuong(nguoiBan, mucTieu, satThuongGoc,
                    nguoiBan.luotXuyenGiap > 0, false, false);
            if (mucTieu != nguoiBan) {
                tongSatThuongThucTe += satThuongThucTe;
            }
            if (satThuongThucTe > 0 && mucTieu != nguoiBan
                    && mucTieuTrucTiep.contains(mucTieu)) {
                this.apDungSatThuongDienRong(nguoiBan, mucTieu, maVatPhamDan,
                        satThuongGoc);
            }
        }
        return tongSatThuongThucTe;
    }

    private void apDungHieuUngDiemRoi(VXLChienBinh nguoiBan, VXLKetQuaDan ketQua,
            int maVatPhamDan) {
        if (nguoiBan == null || ketQua == null) {
            return;
        }
        short[] diemRoi = this.layDiemCuoiDuongDan(ketQua);
        if (diemRoi == null) {
            return;
        }
        short x = diemRoi[0];
        short y = diemRoi[1];
        switch (maVatPhamDan) {
            case 221:
                if (x >= 0 && x < this.map.getWidth()
                        && y >= 0 && y < this.map.getHeight()) {
                    nguoiBan.x = x;
                    nguoiBan.y = y;
                    this.phatCapNhatXY(nguoiBan);
                }
                break;
            case 236:
                this.tornadoX = x;
                this.tornadoY = y;
                this.luotTornado = SO_LUOT_TORNADO;
                break;
            case 250:
                byte maBom = this.chiSoBomTiepTheo++;
                this.bomHenGios.add(new BomHenGio(maBom, nguoiBan, ketQua.loaiDan,
                        ketQua.avengerDan, x, y, ketQua.satThuongMoiVien,
                        ketQua.tranSatThuong, SO_LUOT_BOM_HEN_GIO));
                this.phatTin.guiDatBomHenGio(maBom, x, y,
                        (byte)SO_LUOT_BOM_HEN_GIO);
                break;
            default:
                break;
        }
    }

    private short[] layDiemCuoiDuongDan(VXLKetQuaDan ketQua) {
        int soQuyDao = Math.min(ketQua.cacDuongX.length, ketQua.cacDuongY.length);
        for (int i = soQuyDao - 1; i >= 0; i--) {
            short[] duongX = ketQua.cacDuongX[i];
            short[] duongY = ketQua.cacDuongY[i];
            int chiSoCuoi = Math.min(duongX.length, duongY.length) - 1;
            if (chiSoCuoi >= 0) {
                return new short[]{duongX[chiSoCuoi], duongY[chiSoCuoi]};
            }
        }
        return null;
    }

    private boolean danNamTrongVungVoiRong(int x, int y) {
        return this.luotTornado > 0
                && x >= this.tornadoX - 10 && x < this.tornadoX + 10
                && y >= -100 && y < this.tornadoY;
    }

    private void capNhatTrangThaiSauLuot(VXLChienBinh chienBinh) {
        if (chienBinh == null) {
            return;
        }
        boolean dangAnHinh = chienBinh.luotTangHinh > 0 || chienBinh.luotVoHinh > 0;
        if (chienBinh.luotTangHinh > 0) {
            chienBinh.luotTangHinh--;
        }
        if (chienBinh.luotVoHinh > 0) {
            chienBinh.luotVoHinh--;
        }
        if (dangAnHinh && chienBinh.luotTangHinh == 0 && chienBinh.luotVoHinh == 0) {
            this.phatTin.guiKetThucTangHinh(chienBinh);
        }
        if (chienBinh.luotDongBang > 0 && --chienBinh.luotDongBang == 0) {
            this.phatTin.guiTrangThaiBatDong(chienBinh, false);
        }
        if (chienBinh.luotMu > 0 && --chienBinh.luotMu == 0) {
            this.phatTin.guiTrangThaiMu(chienBinh, false);
        }
        if (chienBinh.luotMacTo > 0) {
            chienBinh.luotMacTo--;
        }
        if (chienBinh.luotLechDan > 0) {
            chienBinh.luotLechDan--;
        }
    }

    private void capNhatVatPhamTheoLuot() throws IOException {
        if (this.luotTornado > 0 && --this.luotTornado == 0) {
            this.tornadoX = -1;
            this.tornadoY = -1;
        }
        for (int i = this.bomHenGios.size() - 1; i >= 0; i--) {
            BomHenGio bom = this.bomHenGios.get(i);
            bom.y = this.map.timViTriDat(bom.x, bom.y);
            bom.luotConLai--;
            if (bom.luotConLai > 0) {
                this.phatTin.guiCapNhatBomHenGio(bom.ma, (byte)bom.luotConLai);
                continue;
            }
            this.phatTin.guiNoBomHenGio(bom.ma);
            this.map.taoLoTheoMatNa(bom.x, bom.y, "hgrenade.png");
            short[] xNo = new short[]{bom.x};
            short[] yNo = new short[]{bom.y};
            for (VXLChienBinh mucTieu : this.chienBinhs) {
                if (mucTieu == null || mucTieu.chet || mucTieu.daRoiTran) {
                    continue;
                }
                int satThuongNo = VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                        xNo, yNo, mucTieu.x, mucTieu.y, bom.loaiDan, bom.avengerDan,
                        bom.satThuongMoiVien, bom.tranSatThuong);
                if (satThuongNo > 0) {
                    this.satThuong(bom.nguoiDat, mucTieu, satThuongNo,
                            false, false, false);
                }
            }
            this.bomHenGios.remove(i);
            if (this.kiemTraKetThuc()) {
                return;
            }
        }
    }

    int satThuong(VXLChienBinh nguon, VXLChienBinh mucTieu, int satThuongGoc, boolean boQuaGiap, boolean boQuaVoHinh, boolean kiemTraNgay) throws IOException {
        if (mucTieu == null || mucTieu.chet || satThuongGoc <= 0) {
            return 0;
        }
        if (!boQuaVoHinh && mucTieu.luotVoHinh > 0) {
            mucTieu.luotVoHinh = 0;
            this.phatTin.guiKetThucTangHinh(mucTieu);
            VXLQuanLyMayChu.log("[ITEM-STATUS] invisible absorbed target=" + mucTieu.ten);
            if (mucTieu.coPhien()) {
                mucTieu.nguoiChoi.startOKDlg2("Vô hình đã giúp bạn né phát bắn.");
            }
            return 0;
        }
        int satThuong = boQuaGiap ? satThuongGoc : VXLTinhSatThuong.tinhSauGiap(satThuongGoc, mucTieu.giap);
        if (mucTieu.khien > 0) {
            int hapThu = Math.min(mucTieu.khien, satThuong);
            mucTieu.khien -= hapThu;
            satThuong -= hapThu;
        }
        if (satThuong <= 0) {
            return 0;
        }
        int mauTruoc = mucTieu.hp;
        mucTieu.hp = Math.max(0, mucTieu.hp - satThuong);
        int satThuongThucTe = mauTruoc - mucTieu.hp;

        System.out.println(String.format("[FIGHT-DAMAGE] %s -> %s | Sát thương gốc=%d | Giáp=%d | Sát thương thực=%d | HP: %d -> %d/%d",
                (nguon != null ? nguon.ten : "Môi trường"), mucTieu.ten, satThuongGoc, mucTieu.giap,
                satThuongThucTe, mauTruoc, mucTieu.hp, mucTieu.mauToiDa));

        if (nguon != null && nguon != mucTieu) {
            nguon.tongSatThuong += satThuongThucTe;
            mucTieu.nguoiGaySatThuongCuoi = nguon;
            if (!nguon.bot && !mucTieu.bot) {
                nguon.nguoiChoi.ghiNhanSatThuongPvp(satThuongThucTe);
            }
        }
        if (mucTieu.hp > 0 && mucTieu.tangNo(35)) {
            this.phatNo(mucTieu);
        }
        if (mucTieu.hp == 0) {
            this.danhDauChet(nguon, mucTieu);
        }
        this.phatCapNhatMau(mucTieu);
        if (kiemTraNgay) {
            this.kiemTraKetThuc();
        }
        return satThuongThucTe;
    }

    private void danhDauChet(VXLChienBinh nguon, VXLChienBinh mucTieu) {
        if (mucTieu.chet) {
            return;
        }
        mucTieu.chet = true;
        if (mucTieu.laBanSaoUltron()) {
            return;
        }
        if (!mucTieu.bot) {
            mucTieu.nguoiChoi.chet++;
        }
        if (nguon == null || nguon == mucTieu) {
            return;
        }
        nguon.haGucTrongTran++;
        if (!nguon.bot && !mucTieu.bot) {
            nguon.nguoiChoi.kill++;
        }
        if (!nguon.bot && mucTieu.camTu) {
            nguon.haCamTuTrongTran++;
        }
    }

    private void apDungHieuUngDan(VXLChienBinh nguoiBan, VXLChienBinh mucTieu, int maVatPhamDan) {
        switch (maVatPhamDan) {
            case 229:
            case 249:
                mucTieu.luotMacTo = Math.max(mucTieu.luotMacTo, 1);
                VXLQuanLyMayChu.log("[ITEM-STATUS] web target=" + mucTieu.ten);
                break;
            case 243:
            case 248:
                mucTieu.luotDoc = Math.max(mucTieu.luotDoc, 3);
                mucTieu.satThuongDoc = Math.max(mucTieu.satThuongDoc, Math.max(6, mucTieu.mauToiDa * 7 / 100));
                mucTieu.nguonDoc = nguoiBan;
                VXLQuanLyMayChu.log("[ITEM-STATUS] poison target=" + mucTieu.ten
                        + " turns=" + mucTieu.luotDoc);
                break;
            case 244:
                mucTieu.luotMu = Math.max(mucTieu.luotMu, 3);
                this.phatTin.guiTrangThaiMu(mucTieu, true);
                VXLQuanLyMayChu.log("[ITEM-STATUS] blind target=" + mucTieu.ten
                        + " turns=" + mucTieu.luotMu);
                break;
            case 247:
                mucTieu.luotDongBang = Math.max(mucTieu.luotDongBang, 1);
                this.phatTin.guiTrangThaiBatDong(mucTieu, true);
                VXLQuanLyMayChu.log("[ITEM-STATUS] frozen target=" + mucTieu.ten);
                break;
            case 388:
                mucTieu.luotLechDan = Math.max(mucTieu.luotLechDan, 3);
                VXLQuanLyMayChu.log("[ITEM-STATUS] shot-deviation target=" + mucTieu.ten
                        + " turns=" + mucTieu.luotLechDan);
                break;
            default:
                break;
        }
    }

    private void apDungSatThuongDienRong(VXLChienBinh nguoiBan, VXLChienBinh mucTieuChinh, int maVatPhamDan, int satThuongGoc) throws IOException {
        if (maVatPhamDan != 228 && maVatPhamDan != 238 && maVatPhamDan != 240 && maVatPhamDan != 241) {
            return;
        }
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu == mucTieuChinh || mucTieu == nguoiBan || mucTieu.chet) {
                continue;
            }
            int dx = mucTieu.x - mucTieuChinh.x;
            int dy = mucTieu.y - mucTieuChinh.y;
            if (dx * dx + dy * dy <= 130 * 130) {
                this.satThuong(nguoiBan, mucTieu, Math.max(1, satThuongGoc / 2), false, false, false);
            }
        }
    }

    boolean kiemTraKetThuc() throws IOException {
        if (this.daKetThuc) {
            return true;
        }
        if (this.cheDoCamTu) {
            int nguoiSong = this.dichVuHaiToaThap.demNguoiChoiSong();
            int dichSong = this.dichVuHaiToaThap.demDichSong();
            if (nguoiSong <= 0) {
                this.ketThucTran(null, KET_QUA_THUA);
                return true;
            }
            if (dichSong <= 0) {
                this.ketThucTran(null, KET_QUA_THANG);
                return true;
            }
            return false;
        }

        int conSong = 0;
        int nguoiChoiSong = 0;
        VXLChienBinh nguoiThang = null;
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && !chienBinh.chet && !chienBinh.laBanSaoUltron()) {
                conSong++;
                nguoiThang = chienBinh;
                if (!chienBinh.bot) {
                    nguoiChoiSong++;
                }
            }
        }
        if (nguoiChoiSong == 0) {
            this.ketThucTran(nguoiThang, conSong == 0 ? KET_QUA_HOA : KET_QUA_THUA);
            return true;
        }
        if (conSong > 1) {
            return false;
        }
        this.ketThucTran(nguoiThang, conSong == 0 ? KET_QUA_HOA : KET_QUA_THANG);
        return true;
    }

    private void ketThucTran(VXLChienBinh nguoiThang, byte ketQuaDoi) {
        if (this.daKetThuc) {
            return;
        }
        this.daKetThuc = true;
        this.dungBot();
        this.xuLyKetThuc.quyetToanTatCa(nguoiThang, ketQuaDoi);
        this.yeuCauDonTran();
    }
    private void yeuCauDonTran() {
        if (this.daYeuCauDonTran || this.wait == null) {
            return;
        }
        this.daYeuCauDonTran = true;
        this.dieuKhienBot.thucHienBatDongBo(this.wait::ketThucDau);
    }

    synchronized void sangLuot(byte luotDaKetThuc) throws IOException {
        if (this.daKetThuc || this.luotHienTai != luotDaKetThuc) {
            return;
        }
        VXLChienBinh vuaHanhDong = this.chiSoHopLe(luotDaKetThuc)
                ? this.chienBinhs[Byte.toUnsignedInt(luotDaKetThuc)] : null;
        if (vuaHanhDong != null && vuaHanhDong.kyNangAvenger != null
                && !vuaHanhDong.laBanSaoUltron()) {
            vuaHanhDong.kyNangAvenger.ghiNhanKetThucLuot();
        }
        if (vuaHanhDong != null && !vuaHanhDong.chet && !vuaHanhDong.daRoiTran) {
            this.ghiNhanHanhDong(luotDaKetThuc, this.napDanSauHanhDong);
        }
        this.napDanSauHanhDong = -1;
        this.phatBanDangChoKetThuc = -1;
        this.hanLuot = 0L;
        this.huyTacVuHetLuot();
        this.capNhatTrangThaiSauLuot(vuaHanhDong);
        this.capNhatVatPhamTheoLuot();
        if (this.daKetThuc) {
            return;
        }
        this.tangNoTheoDoiLuot();
        this.chuanBiLuotTiepTheo(this.luotHienTai);
    }

    private void tangNoTheoDoiLuot() {
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.tangNo(NO_TANG_MOI_LAN_DOI_LUOT)) {
                this.phatNo(chienBinh);
            }
        }
    }

    private void chuanBiLuotTiepTheo(byte batDauTu) throws IOException {
        byte viTriTruoc = batDauTu;
        for (int lan = 0; lan < this.chienBinhs.length * 2 && !this.daKetThuc; lan++) {
            byte viTri = this.timLuotTheoNapDan(viTriTruoc);
            this.luotHienTai = viTri;
            if (viTri < 0) {
                this.kiemTraKetThuc();
                return;
            }
            viTriTruoc = viTri;
            VXLChienBinh chienBinh = this.chienBinhs[viTri];
            chienBinh.daDungVatPhamTrongLuot = false;
            if (chienBinh.luotDoc > 0) {
                chienBinh.luotDoc--;
                this.satThuong(chienBinh.nguonDoc, chienBinh, chienBinh.satThuongDoc, true, true, false);
                if (this.kiemTraKetThuc()) {
                    return;
                }
                if (chienBinh.chet) {
                    continue;
                }
            }
            this.napDanSauHanhDong = -1;
            this.guiLuotTiepTheo();
            return;
        }
        this.kiemTraKetThuc();
    }

    private void ghiNhanHanhDong(int viTri, int thoiGianNapDan) {
        this.napDan[viTri] = Math.max(VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU,
                thoiGianNapDan >= 0
                        ? thoiGianNapDan : VXLChienBinh.THOI_GIAN_NAP_DAN_TOI_THIEU);
        this.boDemThuTuHanhDongNapDan = VXLHangDoiNapDan.ghiNhanHanhDong(
                this.thuTuHanhDongNapDan, viTri, this.boDemThuTuHanhDongNapDan);
    }

    private byte timLuotTheoNapDan(int sauViTri) {
        if (this.cheDoCamTu) {
            VXLChienBinh vuaHanhDong = sauViTri >= 0 && sauViTri < this.chienBinhs.length
                    ? this.chienBinhs[sauViTri] : null;
            boolean uuTienDich = vuaHanhDong != null
                    && (!vuaHanhDong.bot || vuaHanhDong.laBanSaoUltron());
            int luotUuTien = VXLHangDoiNapDan.timViTriTiepTheo(
                    this.napDan, this.thuTuHanhDongNapDan, sauViTri, viTri -> {
                        VXLChienBinh chienBinh = this.chienBinhs[viTri];
                        boolean laDich = chienBinh != null && chienBinh.bot
                                && !chienBinh.laBanSaoUltron();
                        return chienBinh != null && !chienBinh.chet && !chienBinh.daRoiTran
                                && laDich == uuTienDich;
                    });
            if (luotUuTien >= 0) {
                return (byte)luotUuTien;
            }
        }
        return (byte)VXLHangDoiNapDan.timViTriTiepTheo(
                this.napDan, this.thuTuHanhDongNapDan, sauViTri, viTri -> {
                    VXLChienBinh chienBinh = this.chienBinhs[viTri];
                    return chienBinh != null && !chienBinh.chet && !chienBinh.daRoiTran;
                });
    }

    void ghiNhanNapDanSauPhatBan(VXLChienBinh chienBinh) {
        if (chienBinh != null && chienBinh.chiSo == this.luotHienTai) {
            this.napDanSauHanhDong = chienBinh.layThoiGianNapDan();
        }
    }

    void chuyenLuotBotSauPhatBan(VXLChienBinh bot, VXLKetQuaDan ketQua) {
        if (bot != null && ketQua != null && !this.daKetThuc
                && this.luotHienTai == bot.chiSo) {
            this.batDauChoKetThucPhatBan(bot.chiSo, ketQua);
        }
    }

    void chuyenLuotBotSauHanhDong(VXLChienBinh bot, long tre) {
        if (bot == null || this.daKetThuc || this.luotHienTai != bot.chiSo) {
            return;
        }
        this.phatBanDangChoKetThuc = bot.chiSo;
        this.hanLuot = System.currentTimeMillis() + Math.max(0L, tre);
        this.lapLichHetLuot(bot.chiSo, this.hanLuot);
    }

    private void guiLuotTiepTheo() throws IOException {
        if (this.daKetThuc || !this.chiSoHopLe(this.luotHienTai)) {
            return;
        }
        VXLChienBinh tiepTheo = this.chienBinhs[this.luotHienTai];
        if (tiepTheo.kyNangAvenger != null) {
            tiepTheo.kyNangAvenger.batDauLuot();
        }
        if (tiepTheo.tangNo(10)) {
            this.phatNo(tiepTheo);
        }
        this.taoGioMoi();
        this.phatTin.guiGio(this.gioX, this.gioY);
        this.hanLuot = System.currentTimeMillis() + VXLThoiGianLuot.MILLI_GIAY;
        this.lapLichHetLuot(this.luotHienTai, this.hanLuot);
        this.phatTin.guiLuotTiepTheo(this.luotHienTai, tiepTheo.x, tiepTheo.y,
                this.napDan, this.thuTuHanhDongNapDan,
                (byte)VXLThoiGianLuot.SO_GIAY);
        if (!this.daKetThuc && tiepTheo.coPhien() && tiepTheo.kyNangAvenger != null) {
            int soBanSaoUltron = this.demBanSaoUltron(tiepTheo);
            byte maMenu = tiepTheo.kyNangAvenger.layMaMenuSkill(
                    tiepTheo, soBanSaoUltron);
            if (maMenu >= 0 && tiepTheo.kyNangAvenger.canHienNutSkill(
                    tiepTheo, soBanSaoUltron)) {
                tiepTheo.nguoiChoi.dichVu.guiYeuCauSkill(maMenu);
            }
        }
        VXLQuanLyMayChu.log("[FIGHT] turn index=" + this.luotHienTai
                + " player=" + tiepTheo.ten
                + " x=" + tiepTheo.x + " y=" + tiepTheo.y);

    }

    private void batDauChoKetThucPhatBan(byte chiSoNguoiBan, VXLKetQuaDan ketQua) {
        this.phatBanDangChoKetThuc = chiSoNguoiBan;
        long tre = this.cheDoCamTu ? this.tinhTreKetThucPhatBan(ketQua)
                : THOI_GIAN_CHO_KET_THUC_PHAT_BAN;
        this.hanLuot = System.currentTimeMillis() + tre;
        this.lapLichHetLuot(chiSoNguoiBan, this.hanLuot);
    }

    private long tinhTreKetThucPhatBan(VXLKetQuaDan ketQua) {
        int soDiemLonNhat = 1;
        if (ketQua != null) {
            int soQuyDao = Math.min(ketQua.cacDuongX.length, ketQua.cacDuongY.length);
            for (int i = 0; i < soQuyDao; i++) {
                if (ketQua.cacDuongX[i] == null || ketQua.cacDuongY[i] == null) {
                    continue;
                }
                soDiemLonNhat = Math.max(soDiemLonNhat,
                        Math.min(ketQua.cacDuongX[i].length, ketQua.cacDuongY[i].length));
            }
        }
        long tre = TRE_PHAT_BAN_CO_BAN + soDiemLonNhat * TRE_MOI_DIEM_DUONG_DAN;
        return Math.max(TRE_PHAT_BAN_TOI_THIEU, Math.min(TRE_PHAT_BAN_TOI_DA, tre));
    }

    private void taoGioMoi() {
        VXLGioChienDau.HuongGio gioMoi = VXLGioChienDau.taoMoi(this.gioX, this.gioY);
        this.gioX = gioMoi.x();
        this.gioY = gioMoi.y();
    }

    VXLChienBinh timMucTieuGanNhat(VXLChienBinh nguon) {
        VXLChienBinh ganNhat = null;
        int khoangCachGanNhat = Integer.MAX_VALUE;
        for (VXLChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu == nguon || mucTieu.chet || mucTieu.daRoiTran
                    || this.cungDoi(nguon, mucTieu)) {
                continue;
            }
            int dx = mucTieu.x - nguon.x;
            int dy = mucTieu.y - nguon.y;
            int khoangCach = dx * dx + dy * dy;
            if (khoangCach < khoangCachGanNhat) {
                khoangCachGanNhat = khoangCach;
                ganNhat = mucTieu;
            }
        }
        return ganNhat;
    }

    boolean cungDoi(VXLChienBinh mot, VXLChienBinh hai) {
        if (mot == null || hai == null) {
            return false;
        }
        if (mot == hai) {
            return true;
        }
        if (this.cheDoCamTu) {
            boolean pheNguoiMot = !mot.bot || mot.laBanSaoUltron();
            boolean pheNguoiHai = !hai.bot || hai.laBanSaoUltron();
            return pheNguoiMot == pheNguoiHai;
        }
        int chiSoMot = mot.laBanSaoUltron()
                ? Byte.toUnsignedInt(mot.chiSoChuBanSaoUltron)
                : Byte.toUnsignedInt(mot.chiSo);
        int chiSoHai = hai.laBanSaoUltron()
                ? Byte.toUnsignedInt(hai.chiSoChuBanSaoUltron)
                : Byte.toUnsignedInt(hai.chiSo);
        return chiSoMot % 2 == chiSoHai % 2;
    }

    void phatDiChuyen(VXLChienBinh daDiChuyen) {
        this.phatTin.guiDiChuyen(daDiChuyen);
    }

    private void phatCapNhatXY(VXLChienBinh daDiChuyen) {
        this.phatTin.guiCapNhatXY(daDiChuyen);
    }

    void phatBan(VXLChienBinh nguoiBan, VXLKetQuaDan ketQua, byte soPhat) {
        this.phatTin.guiPhatBan(nguoiBan, ketQua, soPhat);
        this.capNhatViTriSauPhatBan(nguoiBan, ketQua);
    }

    private void capNhatViTriSauPhatBan(VXLChienBinh nguoiBan, VXLKetQuaDan ketQua) {
        if (nguoiBan == null || ketQua == null || ketQua.duongX.length == 0
                || ketQua.duongY.length == 0
                || VXLCauHinhVatPhamChienDau.layHoSoDan(ketQua.loaiDan,
                        ketQua.avengerDan).kieuBan() != VXLHoSoDan.KieuBan.NHAN_VAT_LAO) {
            return;
        }
        int chiSoCuoi = Math.min(ketQua.duongX.length, ketQua.duongY.length) - 1;
        if (ketQua.duongY[chiSoCuoi] > this.map.getHeight()) {
            return;
        }
        nguoiBan.x = ketQua.duongX[chiSoCuoi];
        nguoiBan.y = this.map.timViTriDat(nguoiBan.x, ketQua.duongY[chiSoCuoi]);
        VXLQuanLyMayChu.log("[FIRE] Hulk landed player=" + nguoiBan.ten
                + " x=" + nguoiBan.x + " y=" + nguoiBan.y);
    }

    void phatCapNhatMau(VXLChienBinh mucTieu) {
        this.phatTin.guiMau(mucTieu);
    }

    void phatNo(VXLChienBinh chienBinh) {
        this.phatTin.guiNo(chienBinh);
    }

    void phatDungVatPham(VXLChienBinh nguoiDung, byte maHieuUng, short icon) {
        this.phatTin.guiDungVatPham(nguoiDung, maHieuUng, icon);
    }
    VXLChienBinh layChienBinh(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return null;
        }
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.nguoiChoi == nguoiChoi) {
                return chienBinh;
            }
        }
        return null;
    }

    VXLChienBinh[] layDanhSachChienBinh() {
        return this.chienBinhs;
    }

    int layChieuRongBanDo() {
        return this.map.getWidth();
    }

    private boolean chiSoHopLe(byte chiSo) {
        return chiSo >= 0 && chiSo < this.chienBinhs.length;
    }

    boolean daKetThuc() {
        return this.daKetThuc;
    }

    byte layLuotHienTai() {
        return this.luotHienTai;
    }

    long layHanLuot() {
        return this.hanLuot;
    }

    byte layGioX() {
        return this.gioX;
    }

    byte layGioY() {
        return this.gioY;
    }

    boolean dangChoKetThucPhatBan(byte chiSo) {
        return this.phatBanDangChoKetThuc == chiSo;
    }

    private void lapLichHetLuot(byte chiSoLuot, long hanLuotDuKien) {
        this.huyTacVuHetLuot();
        long phienBan = ++this.phienBanLuot;
        long tre = Math.max(0L, hanLuotDuKien - System.currentTimeMillis());
        this.tacVuHetLuot = BO_LAP_LICH_LUOT.schedule(
                () -> this.xuLyHetGioLuot(phienBan, chiSoLuot, hanLuotDuKien),
                tre, TimeUnit.MILLISECONDS);
    }

    private void huyTacVuHetLuot() {
        if (this.tacVuHetLuot != null) {
            this.tacVuHetLuot.cancel(false);
            this.tacVuHetLuot = null;
        }
        this.phienBanLuot++;
    }

    private synchronized void xuLyHetGioLuot(long phienBan, byte chiSoLuot,
            long hanLuotDuKien) {
        if (this.daKetThuc || phienBan != this.phienBanLuot
                || this.luotHienTai != chiSoLuot || this.hanLuot != hanLuotDuKien) {
            return;
        }
        long hienTai = System.currentTimeMillis();
        if (hienTai < this.hanLuot) {
            this.tacVuHetLuot = BO_LAP_LICH_LUOT.schedule(
                    () -> this.xuLyHetGioLuot(phienBan, chiSoLuot, hanLuotDuKien),
                    this.hanLuot - hienTai, TimeUnit.MILLISECONDS);
            return;
        }
        this.tacVuHetLuot = null;
        VXLChienBinh chienBinh = this.chiSoHopLe(chiSoLuot)
                ? this.chienBinhs[chiSoLuot] : null;
        VXLQuanLyMayChu.log("[FIGHT] turn timeout index=" + chiSoLuot
                + " player=" + (chienBinh != null ? chienBinh.ten : "unknown"));
        try {
            this.sangLuot(chiSoLuot);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLQuanLyChien.class.getName()).log(Level.WARNING,
                    "Khong the chuyen luot khi het thoi gian.", ex);
        }
    }
}
