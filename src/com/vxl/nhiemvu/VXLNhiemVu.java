package com.vxl.nhiemvu;

import com.alibaba.fastjson2.JSONObject;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLVatPham;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

public final class VXLNhiemVu {
    private static final ZoneId MUI_GIO_VIET_NAM = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final int NHIEM_VU_NGAY_CAM_TU = 5;
    private static final int NHIEM_VU_NGAY_BOSS = 1;
    private static final int NHIEM_VU_NGAY_PVP = 3;
    private static final int HE_SO_KINH_NGHIEM_CO_BAN = 3;
    private static final int DIEM_TIEM_NANG_MOI_CAP = 3;
    private static final ThanhTich[] CAC_THANH_TICH = new ThanhTich[]{
        new ThanhTich((byte)0, "Đấu trường I", "Thắng 5 trận PvP", 0, 5, 299, 0),
        new ThanhTich((byte)1, "Đấu trường II", "Thắng 25 trận PvP", 0, 25, 304, 1),
        new ThanhTich((byte)2, "Đấu trường III", "Thắng 100 trận PvP", 0, 100, 309, 2),
        new ThanhTich((byte)3, "Khắc tinh cảm tử I", "Hạ 10 cảm tử", 1, 10, 300, 0),
        new ThanhTich((byte)4, "Khắc tinh cảm tử II", "Hạ 50 cảm tử", 1, 50, 305, 1),
        new ThanhTich((byte)5, "Khắc tinh cảm tử III", "Hạ 200 cảm tử", 1, 200, 310, 2),
        new ThanhTich((byte)6, "Thợ săn boss I", "Hạ 1 boss", 2, 1, 301, 0),
        new ThanhTich((byte)7, "Thợ săn boss II", "Hạ 10 boss", 2, 10, 306, 1),
        new ThanhTich((byte)8, "Thợ săn boss III", "Hạ 50 boss", 2, 50, 311, 2)
    };

    private final VXLNguoiChoi nguoiChoi;
    private int tongThangPvp;
    private int tongHaCamTu;
    private int tongHaBoss;
    private int tongSatThuongPvp;
    private int ngayThangPvp;
    private int ngayHaCamTu;
    private int ngayHaBoss;
    private boolean daNhanNgayPvp;
    private boolean daNhanNgayCamTu;
    private boolean daNhanNgayBoss;
    private int thanhTichDaNhan;
    private String ngayNhiemVu = "";
    private long nhanDoiKinhNghiemDen;

    public VXLNhiemVu(VXLNguoiChoi nguoiChoi) {
        this.nguoiChoi = nguoiChoi;
    }

    public synchronized void tai(JSONObject duLieu) {
        this.tongThangPvp = Math.max(0, docInt(duLieu, "pvpWins", 0));
        this.tongHaCamTu = Math.max(0, docInt(duLieu, "kamikazeKills", 0));
        this.tongHaBoss = Math.max(0, docInt(duLieu, "bossKills", 0));
        this.tongSatThuongPvp = Math.max(0, docInt(duLieu, "pvpDamage", 0));
        this.ngayNhiemVu = docString(duLieu, "dailyDate", "");
        this.ngayThangPvp = Math.max(0, docInt(duLieu, "dailyPvpWins", 0));
        this.ngayHaCamTu = Math.max(0, docInt(duLieu, "dailyKamikazeKills", 0));
        this.ngayHaBoss = Math.max(0, docInt(duLieu, "dailyBossKills", 0));
        this.daNhanNgayPvp = docBoolean(duLieu, "dailyPvpClaimed", false);
        this.daNhanNgayCamTu = docBoolean(duLieu, "dailyKamikazeClaimed", false);
        this.daNhanNgayBoss = docBoolean(duLieu, "dailyBossClaimed", false);
        this.thanhTichDaNhan = Math.max(0, docInt(duLieu, "achievementClaimMask", 0));
        if (docBoolean(duLieu, "achievementPvpClaimed", false)) this.thanhTichDaNhan |= 1;
        if (docBoolean(duLieu, "achievementKamikazeClaimed", false)) this.thanhTichDaNhan |= 1 << 3;
        if (docBoolean(duLieu, "achievementBossClaimed", false)) this.thanhTichDaNhan |= 1 << 6;
        this.nhanDoiKinhNghiemDen = docLong(duLieu, "doubleExpUntil", 0L);
        this.datLaiNgayNeuCan();
    }

    public synchronized void ghiVao(JSONObject duLieu) {
        this.datLaiNgayNeuCan();
        duLieu.put("pvpWins", this.tongThangPvp);
        duLieu.put("kamikazeKills", this.tongHaCamTu);
        duLieu.put("bossKills", this.tongHaBoss);
        duLieu.put("pvpDamage", this.tongSatThuongPvp);
        duLieu.put("dailyDate", this.ngayNhiemVu);
        duLieu.put("dailyPvpWins", this.ngayThangPvp);
        duLieu.put("dailyKamikazeKills", this.ngayHaCamTu);
        duLieu.put("dailyBossKills", this.ngayHaBoss);
        duLieu.put("dailyPvpClaimed", this.daNhanNgayPvp);
        duLieu.put("dailyKamikazeClaimed", this.daNhanNgayCamTu);
        duLieu.put("dailyBossClaimed", this.daNhanNgayBoss);
        duLieu.put("achievementClaimMask", this.thanhTichDaNhan);
        duLieu.put("doubleExpUntil", this.nhanDoiKinhNghiemDen);
    }

    public synchronized int congKinhNghiem(int soKinhNghiem) {
        if (soKinhNghiem <= 0) return 0;
        int capCu = this.nguoiChoi.cap;
        long hienTai = System.currentTimeMillis() / 1000L;
        int heSoSuKien = this.nhanDoiKinhNghiemDen > hienTai ? 2 : 1;
        long thucNhan = (long)soKinhNghiem * HE_SO_KINH_NGHIEM_CO_BAN * heSoSuKien;
        int gioiHan = thucNhan > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)thucNhan;
        long kinhNghiemMoi = (long)Math.max(0, this.nguoiChoi.kinhNghiem) + gioiHan;
        this.nguoiChoi.kinhNghiem = (int)Math.min(Integer.MAX_VALUE, kinhNghiemMoi);
        this.nguoiChoi.cap = com.vxl.tienich.VXLTienIch.layCap(this.nguoiChoi.kinhNghiem);
        int soCapTang = Math.max(0, this.nguoiChoi.cap - capCu);
        if (soCapTang > 0) {
            int diemMoi = soCapTang * DIEM_TIEM_NANG_MOI_CAP;
            this.nguoiChoi.point = (short)Math.min(Short.MAX_VALUE,
                    Short.toUnsignedInt(this.nguoiChoi.point) + diemMoi);
            this.nguoiChoi.startOKDlg2("Lên " + soCapTang + " cấp, nhận " + diemMoi + " điểm tiềm năng.");
        }
        if (this.nguoiChoi.dichVu != null) {
            this.nguoiChoi.dichVu.guiKinhNghiem(gioiHan);
        }
        return gioiHan;
    }

    public synchronized boolean kichHoatNhanDoiKinhNghiem() {
        long hienTai = System.currentTimeMillis() / 1000L;
        this.nhanDoiKinhNghiemDen = Math.max(hienTai, this.nhanDoiKinhNghiemDen) + 24L * 60L * 60L;
        this.nguoiChoi.startOKDlg2("Đã kích hoạt x2 EXP trong 24 giờ.");
        return true;
    }

    public synchronized void ghiNhanThangPvp() {
        this.datLaiNgayNeuCan();
        this.tongThangPvp++;
        this.ngayThangPvp++;
        this.kiemTraPhanThuongNgay();
    }

    public synchronized void ghiNhanHaCamTu(int soLuong) {
        if (soLuong <= 0) return;
        this.datLaiNgayNeuCan();
        this.tongHaCamTu += soLuong;
        this.ngayHaCamTu += soLuong;
        this.kiemTraPhanThuongNgay();
    }

    public synchronized void ghiNhanHaBoss(int soLuong) {
        if (soLuong <= 0) return;
        this.datLaiNgayNeuCan();
        this.tongHaBoss += soLuong;
        this.ngayHaBoss += soLuong;
        this.kiemTraPhanThuongNgay();
    }

    public synchronized void ghiNhanSatThuongPvp(int satThuong) {
        if (satThuong > 0) this.tongSatThuongPvp += satThuong;
    }

    public synchronized String tomTat() {
        this.datLaiNgayNeuCan();
        return "Ngày " + this.ngayNhiemVu + " | PvP " + this.ngayThangPvp + "/" + NHIEM_VU_NGAY_PVP
                + " | Cảm tử " + this.ngayHaCamTu + "/" + NHIEM_VU_NGAY_CAM_TU
                + " | Boss " + this.ngayHaBoss + "/" + NHIEM_VU_NGAY_BOSS;
    }

    public synchronized void xuLyThanhTich(VXLTinNhan yeuCau) throws IOException {
        int hanhDong = yeuCau.boDoc().available() > 0 ? yeuCau.boDoc().readUnsignedByte() : 0;
        if (hanhDong == 1 && yeuCau.boDoc().available() > 0) {
            this.nhanThanhTich(yeuCau.boDoc().readUnsignedByte());
        }
        this.guiThanhTich();
    }

    public synchronized void guiThanhTich() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(88);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(CAC_THANH_TICH.length);
        for (ThanhTich thanhTich : CAC_THANH_TICH) {
            int tienDo = this.layTienDo(thanhTich.loai());
            boolean daNhan = (this.thanhTichDaNhan & (1 << Byte.toUnsignedInt(thanhTich.id()))) != 0;
            ds.writeByte(thanhTich.id());
            ds.writeUTF(thanhTich.ten());
            ds.writeUTF(thanhTich.moTa() + ". Thưởng 1 viên ngọc khảm cấp " + (thanhTich.capSao() + 1) + ".");
            ds.writeByte(daNhan ? 2 : (tienDo >= thanhTich.moc() ? 1 : 0));
            ds.writeInt(Math.min(tienDo, thanhTich.moc()));
            ds.writeInt(thanhTich.moc());
            ds.writeInt(1);
            ds.writeByte(thanhTich.capSao());
        }
        ds.flush();
        this.nguoiChoi.dichVu.guiTin(ms);
    }

    private void nhanThanhTich(int maThanhTich) {
        ThanhTich thanhTich = null;
        for (ThanhTich cauHinh : CAC_THANH_TICH) {
            if (Byte.toUnsignedInt(cauHinh.id()) == maThanhTich) {
                thanhTich = cauHinh;
                break;
            }
        }
        if (thanhTich == null) return;
        int bit = 1 << maThanhTich;
        if ((this.thanhTichDaNhan & bit) != 0) {
            this.nguoiChoi.startOKDlg2("Phần thưởng này đã được nhận.");
            return;
        }
        if (this.layTienDo(thanhTich.loai()) < thanhTich.moc()) {
            this.nguoiChoi.startOKDlg2("Thành tích chưa hoàn thành.");
            return;
        }
        VXLVatPham ngocThuong = new VXLVatPham(thanhTich.maNgoc());
        if (!this.nguoiChoi.themVatPhamVaoTui(ngocThuong)) {
            this.nguoiChoi.startOKDlg2("Túi đồ đã đầy, chưa thể nhận ngọc.");
            return;
        }
        this.thanhTichDaNhan |= bit;
        this.nguoiChoi.flushCache();
        this.nguoiChoi.startOKDlg2("Đã nhận " + ngocThuong.mau.ten + ".");
    }

    private int layTienDo(int loai) {
        return switch (loai) {
            case 0 -> this.tongThangPvp;
            case 1 -> this.tongHaCamTu;
            case 2 -> this.tongHaBoss;
            default -> 0;
        };
    }

    public synchronized int layTongThangPvp() { return this.tongThangPvp; }
    public synchronized int layTongHaCamTu() { return this.tongHaCamTu; }
    public synchronized int layTongHaBoss() { return this.tongHaBoss; }
    public synchronized int layTongSatThuongPvp() { return this.tongSatThuongPvp; }
    public synchronized boolean daHoanThanhPvp() { return this.tongThangPvp >= 100; }
    public synchronized boolean daHoanThanhCamTu() { return this.tongHaCamTu >= 200; }
    public synchronized boolean daHoanThanhBoss() { return this.tongHaBoss >= 50; }

    private void datLaiNgayNeuCan() {
        String homNay = LocalDate.now(MUI_GIO_VIET_NAM).toString();
        if (homNay.equals(this.ngayNhiemVu)) return;
        this.ngayNhiemVu = homNay;
        this.ngayThangPvp = 0;
        this.ngayHaCamTu = 0;
        this.ngayHaBoss = 0;
        this.daNhanNgayPvp = false;
        this.daNhanNgayCamTu = false;
        this.daNhanNgayBoss = false;
    }

    private void kiemTraPhanThuongNgay() {
        StringBuilder thongBao = new StringBuilder();
        if (!this.daNhanNgayPvp && this.ngayThangPvp >= NHIEM_VU_NGAY_PVP) {
            this.daNhanNgayPvp = true;
            this.traoThuongNgay(3000, 300, "Nhiệm vụ ngày: thắng 3 trận PvP", thongBao);
        }
        if (!this.daNhanNgayCamTu && this.ngayHaCamTu >= NHIEM_VU_NGAY_CAM_TU) {
            this.daNhanNgayCamTu = true;
            this.traoThuongNgay(1000, 100, "Nhiệm vụ ngày: hạ 5 cảm tử", thongBao);
        }
        if (!this.daNhanNgayBoss && this.ngayHaBoss >= NHIEM_VU_NGAY_BOSS) {
            this.daNhanNgayBoss = true;
            this.traoThuongNgay(2000, 200, "Nhiệm vụ ngày: hạ 1 boss", thongBao);
        }
        if (thongBao.length() > 0) this.nguoiChoi.startOKDlg2(thongBao.toString());
    }

    private void traoThuongNgay(int vang, int kinhNghiem, String tenNhiemVu, StringBuilder thongBao) {
        this.nguoiChoi.updateGold(vang);
        int kinhNghiemThucNhan = this.congKinhNghiem(kinhNghiem);
        if (thongBao.length() > 0) thongBao.append('\n');
        thongBao.append("Hoàn thành ").append(tenNhiemVu).append(": +")
                .append(vang).append(" vàng, +").append(kinhNghiemThucNhan).append(" EXP");
    }

    private static int docInt(JSONObject duLieu, String khoa, int macDinh) {
        Object giaTri = duLieu != null ? duLieu.get(khoa) : null;
        if (giaTri == null) return macDinh;
        try { return Integer.parseInt(giaTri.toString()); }
        catch (NumberFormatException ex) { return macDinh; }
    }

    private static long docLong(JSONObject duLieu, String khoa, long macDinh) {
        Object giaTri = duLieu != null ? duLieu.get(khoa) : null;
        if (giaTri == null) return macDinh;
        try { return Long.parseLong(giaTri.toString()); }
        catch (NumberFormatException ex) { return macDinh; }
    }

    private static String docString(JSONObject duLieu, String khoa, String macDinh) {
        Object giaTri = duLieu != null ? duLieu.get(khoa) : null;
        return giaTri != null ? giaTri.toString() : macDinh;
    }

    private static boolean docBoolean(JSONObject duLieu, String khoa, boolean macDinh) {
        Object giaTri = duLieu != null ? duLieu.get(khoa) : null;
        if (giaTri == null) return macDinh;
        if (giaTri instanceof Boolean) return (Boolean)giaTri;
        String chuoi = giaTri.toString().trim();
        if ("1".equals(chuoi)) return true;
        if ("0".equals(chuoi)) return false;
        return Boolean.parseBoolean(chuoi);
    }

    private record ThanhTich(byte id, String ten, String moTa, int loai, int moc,
            int maNgoc, int capSao) {
    }
}
