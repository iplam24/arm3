package com.vxl.chien;

import com.vxl.bando.VXLQuanLyBanDo;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mang.VXLTinNhan;
import com.vxl.phong.VXLChoDau;
import com.vxl.vatpham.VXLVatPham;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLQuanLyChien {
    public static final byte MA_BAN_DO_HAI_TOA_THAP = 51;
    private static final byte KET_QUA_THUA = 0;
    private static final byte KET_QUA_THANG = 1;
    private static final byte KET_QUA_HOA = 2;
    private static final int MAX_FIGHTERS = 8;
    private static final int TURN_SECONDS = 25;
    private static final int NO_TANG_MOI_LAN_DOI_LUOT = 10;
    private final VXLChoDau wait;
    private final VXLChienBinh[] chienBinhs = new VXLChienBinh[MAX_FIGHTERS];
    private final VXLQuanLyBanDo map;
    private final boolean cheDoCamTu;
    private final VXLXuLyVatPhamTrongTran xuLyVatPham;
    private final VXLXuLyKetThucTranDau xuLyKetThuc;
    private final VXLTinhDuongDan tinhDuongDan;
    private final VXLPhatTinTranDau phatTin;
    private final VXLDieuKhienBotTranDau dieuKhienBot;
    private byte luotHienTai = -1;
    private boolean daKetThuc;
    private boolean daYeuCauDonTran;
    private long hanLuot;
    private byte gioX;
    private byte gioY;

    public VXLQuanLyChien(VXLChoDau wait, VXLNguoiChoi[] nguoiChois, byte maBanDo) {
        this.wait = wait;
        this.map = new VXLQuanLyBanDo(maBanDo);
        this.cheDoCamTu = maBanDo == MA_BAN_DO_HAI_TOA_THAP;
        this.xuLyVatPham = new VXLXuLyVatPhamTrongTran(this);
        this.xuLyKetThuc = new VXLXuLyKetThucTranDau(this.cheDoCamTu, this.chienBinhs);
        this.tinhDuongDan = new VXLTinhDuongDan(this.map, this.chienBinhs);
        this.phatTin = new VXLPhatTinTranDau(this.chienBinhs);
        this.dieuKhienBot = new VXLDieuKhienBotTranDau(this, this.chienBinhs, this.map, this.tinhDuongDan);
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
    }

    public synchronized void themBot(byte chiSo, String ten, short maVuKhi, byte avenger) {
        if (!this.chiSoHopLe(chiSo) || this.chienBinhs[chiSo] != null) {
            return;
        }
        this.chienBinhs[chiSo] = new VXLChienBinh(chiSo, this.map.laySinhX(chiSo), this.map.laySinhY(chiSo), ten, maVuKhi, avenger);
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
        if (this.kiemTraKetThuc()) {
            return;
        }
        this.chuanBiLuotTiepTheo((byte)-1);
        this.dieuKhienBot.batDau();
    }

    public synchronized void diChuyen(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (!this.coTheHanhDong(chienBinh) || chienBinh.luotDongBang > 0) {
            return;
        }
        short xYeuCau = ms.boDoc().readShort();
        short yYeuCau = ms.boDoc().readShort();
        int tamDiChuyen = chienBinh.layTamDiChuyen(180);
        short[] toaDo = this.tinhDuongDan.gioiHanDiChuyen(chienBinh.x, chienBinh.y, xYeuCau, yYeuCau, tamDiChuyen);
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
        short xYeuCau = ms.boDoc().readShort();
        short yYeuCau = ms.boDoc().readShort();
        short[] toaDo = this.tinhDuongDan.gioiHanDiChuyen(chienBinh.x, chienBinh.y, xYeuCau, yYeuCau,
                Math.max(this.map.getWidth(), this.map.getHeight()));
        chienBinh.x = toaDo[0];
        chienBinh.y = toaDo[1];
        this.phatCapNhatXY(chienBinh);
    }

    public synchronized void doiSung(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        int chiSoBalo = ms.boDoc().readUnsignedByte();
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
        if (!this.coTheHanhDong(chienBinh) || chienBinh.avenger > 0) {
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
        short[] viTriBan = this.tinhDuongDan.gioiHanDiChuyen(nguoiBan.x, nguoiBan.y, x, y, 120);
        nguoiBan.x = viTriBan[0];
        nguoiBan.y = viTriBan[1];

        int maVatPhamDan = nguoiBan.vatPhamDanDacBiet;
        nguoiBan.vatPhamDanDacBiet = -1;
        soPhat = Math.max(soPhat, nguoiBan.soPhatToiThieu);
        byte loaiDan = maVatPhamDan >= 0
                ? VXLCauHinhVatPhamChienDau.layLoaiDan(maVatPhamDan, loaiDanKhachGui)
                : nguoiBan.avenger > 0
                        ? VXLCauHinhVatPhamChienDau.layLoaiDanTheoAvenger(
                                nguoiBan.avenger, loaiDanKhachGui)
                        : VXLCauHinhVatPhamChienDau.layLoaiDanTheoVuKhi(
                                nguoiBan.maVuKhi, loaiDanKhachGui);
        nguoiBan.batDauNapDan();
        if (nguoiBan.luotMu > 0) {
            nguoiBan.luotMu--;
            goc = (short)((goc + 14 + nguoiBan.chiSo * 3) % 360);
        }

        boolean kyNangDacBiet = nguoiBan.kyNangDacBiet;
        VXLKetQuaDan ketQua = this.xuLyPhatBan(nguoiBan, loaiDan, goc, luc, lucTach,
                maVatPhamDan, kyNangDacBiet);
        VXLKetQuaDan ketQuaHienThi = ketQua.nhanBanDuongDanHienThi(soPhat);
        this.phatBan(nguoiBan, ketQuaHienThi, (byte)soPhat);
        int satThuongThucTe = this.apDungSatThuongPhatBan(nguoiBan, ketQua, maVatPhamDan);
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
        if (!this.kiemTraKetThuc()) {
            this.sangLuot();
        }
    }

    public synchronized boolean dungVatPham(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        return this.xuLyVatPham.xuLy(nguoiChoi, ms);
    }
    public synchronized void kiemTraVaCham(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
    }

    public synchronized void boLuot(VXLNguoiChoi nguoiChoi) throws IOException {
        VXLChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (this.coTheHanhDong(chienBinh)) {
            this.sangLuot();
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
                this.sangLuot();
            }
        }
        catch (IOException ex) {
            Logger.getLogger(VXLQuanLyChien.class.getName()).log(Level.WARNING, "Lỗi xử lý sau khi người chơi rời trận.", ex);
        }
    }

    public synchronized void dungBot() {
        this.dieuKhienBot.dung();
    }

    boolean coTheHanhDong(VXLChienBinh chienBinh) {
        return chienBinh != null && !chienBinh.chet && !chienBinh.daRoiTran && !this.daKetThuc
                && chienBinh.chiSo == this.luotHienTai
                && (this.hanLuot <= 0 || System.currentTimeMillis() <= this.hanLuot + 3000L);
    }
    VXLKetQuaDan xuLyPhatBan(VXLChienBinh nguoiBan, byte loaiDan, short goc, byte luc, int maVatPhamDan) {
        return this.xuLyPhatBan(nguoiBan, loaiDan, goc, luc, (byte)0, maVatPhamDan, false);
    }

    VXLKetQuaDan xuLyPhatBan(VXLChienBinh nguoiBan, byte loaiDan, short goc, byte luc,
            byte lucTach, int maVatPhamDan) {
        return this.xuLyPhatBan(nguoiBan, loaiDan, goc, luc, lucTach, maVatPhamDan, false);
    }

    private VXLKetQuaDan xuLyPhatBan(VXLChienBinh nguoiBan, byte loaiDan, short goc, byte luc,
            byte lucTach, int maVatPhamDan, boolean kyNangDacBiet) {
        byte avengerDan = maVatPhamDan >= 0 ? 0 : nguoiBan.avenger;
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
                * heSoTrangThai / 100;
        int satThuongCoBan = VXLTinhSatThuong.tinhPhatBan(nguoiBan.tanCong, luc, heSoTong);
        int satThuongMoiVien = VXLCauHinhVatPhamChienDau.tinhSatThuongMoiVien(
                satThuongCoBan, loaiDan, chiMang, avengerDan);
        int tranSatThuong = phatBan.truotRaNgoaiBanDo ? 0 : satThuongCoBan
                * VXLCauHinhVatPhamChienDau.layTranPhanTramSatThuong(loaiDan, avengerDan)
                / 100;
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
                phatBan.duongX, phatBan.duongY, mucTieuTheoQuyDao, satThuongTheoQuyDao,
                satThuongMoiVien, tranSatThuong, avengerDan);
    }

    int apDungSatThuongPhatBan(VXLChienBinh nguoiBan, VXLKetQuaDan ketQua,
            int maVatPhamDan) throws IOException {
        Map<VXLChienBinh, Integer> tongTheoMucTieu = new LinkedHashMap<>();
        for (int i = 0; i < ketQua.mucTieuTheoQuyDao.length; i++) {
            VXLChienBinh mucTieu = ketQua.mucTieuTheoQuyDao[i];
            int satThuong = i < ketQua.satThuongTheoQuyDao.length
                    ? ketQua.satThuongTheoQuyDao[i] : 0;
            if (mucTieu != null && satThuong > 0) {
                tongTheoMucTieu.merge(mucTieu, satThuong, Integer::sum);
            }
        }
        int satThuongNguoc = VXLCauHinhVatPhamChienDau.tinhSatThuongNoTaiViTri(
                ketQua.cacDuongX, ketQua.cacDuongY, nguoiBan.x, nguoiBan.y,
                ketQua.loaiDan, ketQua.avengerDan, ketQua.satThuongMoiVien,
                ketQua.tranSatThuong);
        if (satThuongNguoc > 0) {
            tongTheoMucTieu.merge(nguoiBan, satThuongNguoc, Math::max);
        }
        int tongSatThuongThucTe = 0;
        for (Map.Entry<VXLChienBinh, Integer> muc : tongTheoMucTieu.entrySet()) {
            VXLChienBinh mucTieu = muc.getKey();
            int satThuongGoc = muc.getValue();
            int satThuongThucTe = this.satThuong(nguoiBan, mucTieu, satThuongGoc,
                    nguoiBan.luotXuyenGiap > 0, false, false);
            if (mucTieu != nguoiBan) {
                tongSatThuongThucTe += satThuongThucTe;
            }
            if (satThuongThucTe > 0 && !mucTieu.chet) {
                if (mucTieu != nguoiBan) {
                    this.apDungHieuUngDan(nguoiBan, mucTieu, maVatPhamDan);
                }
            }
            if (satThuongThucTe > 0 && mucTieu != nguoiBan) {
                this.apDungSatThuongDienRong(nguoiBan, mucTieu, maVatPhamDan,
                        satThuongGoc);
            }
        }
        return tongSatThuongThucTe;
    }

    int satThuong(VXLChienBinh nguon, VXLChienBinh mucTieu, int satThuongGoc, boolean boQuaGiap, boolean boQuaVoHinh, boolean kiemTraNgay) throws IOException {
        if (mucTieu == null || mucTieu.chet || satThuongGoc <= 0) {
            return 0;
        }
        if (!boQuaVoHinh && mucTieu.luotVoHinh > 0) {
            mucTieu.luotVoHinh = 0;
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
            case 243:
            case 248:
                mucTieu.luotDoc = Math.max(mucTieu.luotDoc, 3);
                mucTieu.satThuongDoc = Math.max(mucTieu.satThuongDoc, Math.max(6, mucTieu.mauToiDa * 7 / 100));
                mucTieu.nguonDoc = nguoiBan;
                break;
            case 244:
                mucTieu.luotMu = Math.max(mucTieu.luotMu, 3);
                break;
            case 247:
                mucTieu.luotDongBang = Math.max(mucTieu.luotDongBang, 1);
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
            int nguoiSong = 0;
            int camTuSong = 0;
            for (VXLChienBinh chienBinh : this.chienBinhs) {
                if (chienBinh == null || chienBinh.chet) {
                    continue;
                }
                if (chienBinh.camTu) {
                    camTuSong++;
                } else if (!chienBinh.bot) {
                    nguoiSong++;
                }
            }
            if (nguoiSong > 0 && camTuSong > 0) {
                return false;
            }
            byte ketQuaDoi = nguoiSong > 0 ? KET_QUA_THANG : (camTuSong > 0 ? KET_QUA_THUA : KET_QUA_HOA);
            this.ketThucTran(null, ketQuaDoi);
            return true;
        }

        int conSong = 0;
        int nguoiChoiSong = 0;
        VXLChienBinh nguoiThang = null;
        for (VXLChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && !chienBinh.chet) {
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
        this.hanLuot = 0;
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

    void sangLuot() throws IOException {
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
        byte viTri = batDauTu;
        for (int lan = 0; lan < this.chienBinhs.length * 2 && !this.daKetThuc; lan++) {
            viTri = this.nguoiSongTiepTu(viTri);
            this.luotHienTai = viTri;
            if (viTri < 0) {
                this.kiemTraKetThuc();
                return;
            }
            VXLChienBinh chienBinh = this.chienBinhs[viTri];
            chienBinh.daDungVatPhamTrongLuot = false;
            if (chienBinh.luotVoHinh > 0) {
                chienBinh.luotVoHinh--;
            }
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
            if (chienBinh.luotDongBang > 0) {
                chienBinh.luotDongBang--;
                if (chienBinh.coPhien()) {
                    chienBinh.nguoiChoi.startOKDlg2("Bạn đang bị đóng băng và mất lượt.");
                }
                continue;
            }
            this.guiLuotTiepTheo();
            return;
        }
        this.kiemTraKetThuc();
    }

    private byte nguoiSongTiepTu(byte from) {
        for (int buoc = 1; buoc <= this.chienBinhs.length; buoc++) {
            int chiSo = (from + buoc + this.chienBinhs.length) % this.chienBinhs.length;
            VXLChienBinh chienBinh = this.chienBinhs[chiSo];
            if (chienBinh != null && !chienBinh.chet) {
                return (byte)chiSo;
            }
        }
        return -1;
    }
    private void guiLuotTiepTheo() throws IOException {
        if (this.daKetThuc || !this.chiSoHopLe(this.luotHienTai)) {
            return;
        }
        VXLChienBinh tiepTheo = this.chienBinhs[this.luotHienTai];
        if (tiepTheo.tangNo(10)) {
            this.phatNo(tiepTheo);
        }
        this.taoGioMoi();
        this.phatTin.guiGio(this.gioX, this.gioY);
        this.hanLuot = System.currentTimeMillis() + TURN_SECONDS * 1000L;
        this.phatTin.guiLuotTiepTheo(this.luotHienTai, tiepTheo.x, tiepTheo.y, (byte)TURN_SECONDS);
        VXLQuanLyMayChu.log("[FIGHT] turn index=" + this.luotHienTai
                + " player=" + tiepTheo.ten
                + " x=" + tiepTheo.x + " y=" + tiepTheo.y);

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
        nguoiBan.x = ketQua.duongX[chiSoCuoi];
        nguoiBan.y = ketQua.duongY[chiSoCuoi];
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
}
