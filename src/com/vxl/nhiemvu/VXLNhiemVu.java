package com.vxl.nhiemvu;

import com.alibaba.fastjson2.JSONObject;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

public final class VXLNhiemVu {
    private static final ZoneId MUI_GIO_VIET_NAM = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final int NHIEM_VU_NGAY_CAM_TU = 5;
    private static final int NHIEM_VU_NGAY_BOSS = 1;
    private static final int NHIEM_VU_NGAY_PVP = 3;
    private static final int THANH_TICH_CAM_TU = 50;
    private static final int THANH_TICH_BOSS = 10;
    private static final int THANH_TICH_PVP = 25;
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
    private boolean daNhanThanhTichPvp;
    private boolean daNhanThanhTichCamTu;
    private boolean daNhanThanhTichBoss;
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
        this.daNhanThanhTichPvp = docBoolean(duLieu, "achievementPvpClaimed", false);
        this.daNhanThanhTichCamTu = docBoolean(duLieu, "achievementKamikazeClaimed", false);
        this.daNhanThanhTichBoss = docBoolean(duLieu, "achievementBossClaimed", false);
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
        duLieu.put("achievementPvpClaimed", this.daNhanThanhTichPvp);
        duLieu.put("achievementKamikazeClaimed", this.daNhanThanhTichCamTu);
        duLieu.put("achievementBossClaimed", this.daNhanThanhTichBoss);
        duLieu.put("doubleExpUntil", this.nhanDoiKinhNghiemDen);
    }

    public synchronized int congKinhNghiem(int soKinhNghiem) {
        if (soKinhNghiem <= 0) {
            return 0;
        }
        long hienTai = System.currentTimeMillis() / 1000L;
        int heSo = this.nhanDoiKinhNghiemDen > hienTai ? 2 : 1;
        long thucNhan = (long)soKinhNghiem * heSo;
        int gioiHan = thucNhan > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)thucNhan;
        long kinhNghiemMoi = (long)Math.max(0, this.nguoiChoi.kinhNghiem) + gioiHan;
        this.nguoiChoi.kinhNghiem = (int)Math.min(Integer.MAX_VALUE, kinhNghiemMoi);
        this.nguoiChoi.cap = com.vxl.tienich.VXLTienIch.layCap(this.nguoiChoi.kinhNghiem);
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
        this.kiemTraPhanThuong();
    }

    public synchronized void ghiNhanHaCamTu(int soLuong) {
        if (soLuong <= 0) {
            return;
        }
        this.datLaiNgayNeuCan();
        this.tongHaCamTu += soLuong;
        this.ngayHaCamTu += soLuong;
        this.kiemTraPhanThuong();
    }

    public synchronized void ghiNhanHaBoss(int soLuong) {
        if (soLuong <= 0) {
            return;
        }
        this.datLaiNgayNeuCan();
        this.tongHaBoss += soLuong;
        this.ngayHaBoss += soLuong;
        this.kiemTraPhanThuong();
    }

    public synchronized void ghiNhanSatThuongPvp(int satThuong) {
        if (satThuong > 0) {
            this.tongSatThuongPvp += satThuong;
        }
    }

    public synchronized String tomTat() {
        this.datLaiNgayNeuCan();
        return "Ngày " + this.ngayNhiemVu
                + " | PvP " + this.ngayThangPvp + "/" + NHIEM_VU_NGAY_PVP
                + " | Cảm tử " + this.ngayHaCamTu + "/" + NHIEM_VU_NGAY_CAM_TU
                + " | Boss " + this.ngayHaBoss + "/" + NHIEM_VU_NGAY_BOSS;
    }

    public synchronized void guiThanhTich() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(88);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeUTF("🎖 BẢNG THÀNH TÍCH CÁ NHÂN\n" + this.tomTat());
        ds.writeInt(this.tongThangPvp);
        ds.writeInt(this.tongHaCamTu);
        ds.writeInt(this.tongHaBoss);
        ds.writeInt(this.tongSatThuongPvp);
        ds.writeByte(3);
        ds.writeByte(1);
        ds.writeBoolean(this.daHoanThanhPvp());
        ds.writeByte(2);
        ds.writeBoolean(this.daHoanThanhCamTu());
        ds.writeByte(3);
        ds.writeBoolean(this.daHoanThanhBoss());
        ds.flush();
        this.nguoiChoi.dichVu.guiTin(ms);
    }

    public synchronized int layTongThangPvp() {
        return this.tongThangPvp;
    }

    public synchronized int layTongHaCamTu() {
        return this.tongHaCamTu;
    }

    public synchronized int layTongHaBoss() {
        return this.tongHaBoss;
    }

    public synchronized int layTongSatThuongPvp() {
        return this.tongSatThuongPvp;
    }

    public synchronized boolean daHoanThanhPvp() {
        return this.tongThangPvp >= THANH_TICH_PVP;
    }

    public synchronized boolean daHoanThanhCamTu() {
        return this.tongHaCamTu >= THANH_TICH_CAM_TU;
    }

    public synchronized boolean daHoanThanhBoss() {
        return this.tongHaBoss >= THANH_TICH_BOSS;
    }

    private void datLaiNgayNeuCan() {
        String homNay = LocalDate.now(MUI_GIO_VIET_NAM).toString();
        if (homNay.equals(this.ngayNhiemVu)) {
            return;
        }
        this.ngayNhiemVu = homNay;
        this.ngayThangPvp = 0;
        this.ngayHaCamTu = 0;
        this.ngayHaBoss = 0;
        this.daNhanNgayPvp = false;
        this.daNhanNgayCamTu = false;
        this.daNhanNgayBoss = false;
    }

    private void kiemTraPhanThuong() {
        StringBuilder thongBao = new StringBuilder();
        if (!this.daNhanNgayPvp && this.ngayThangPvp >= NHIEM_VU_NGAY_PVP) {
            this.daNhanNgayPvp = true;
            this.traoThuong(3000, 300, 0, "Nhiệm vụ ngày: thắng 3 trận PvP", thongBao);
        }
        if (!this.daNhanNgayCamTu && this.ngayHaCamTu >= NHIEM_VU_NGAY_CAM_TU) {
            this.daNhanNgayCamTu = true;
            this.traoThuong(1000, 100, 0, "Nhiệm vụ ngày: hạ 5 cảm tử", thongBao);
        }
        if (!this.daNhanNgayBoss && this.ngayHaBoss >= NHIEM_VU_NGAY_BOSS) {
            this.daNhanNgayBoss = true;
            this.traoThuong(2000, 200, 0, "Nhiệm vụ ngày: hạ 1 boss", thongBao);
        }
        if (!this.daNhanThanhTichPvp && this.tongThangPvp >= THANH_TICH_PVP) {
            this.daNhanThanhTichPvp = true;
            this.traoThuong(5000, 500, 20, "Thành tích: thắng 25 trận PvP", thongBao);
        }
        if (!this.daNhanThanhTichCamTu && this.tongHaCamTu >= THANH_TICH_CAM_TU) {
            this.daNhanThanhTichCamTu = true;
            this.traoThuong(5000, 500, 10, "Thành tích: hạ 50 cảm tử", thongBao);
        }
        if (!this.daNhanThanhTichBoss && this.tongHaBoss >= THANH_TICH_BOSS) {
            this.daNhanThanhTichBoss = true;
            this.traoThuong(8000, 800, 20, "Thành tích: hạ 10 boss", thongBao);
        }
        if (thongBao.length() > 0) {
            this.nguoiChoi.startOKDlg2(thongBao.toString());
        }
    }

    private void traoThuong(int vang, int kinhNghiem, int ngoc, String tenNhiemVu, StringBuilder thongBao) {
        this.nguoiChoi.updateGold(vang);
        this.nguoiChoi.updateGem(ngoc);
        int kinhNghiemThucNhan = this.congKinhNghiem(kinhNghiem);
        if (thongBao.length() > 0) {
            thongBao.append('\n');
        }
        thongBao.append("Hoàn thành ").append(tenNhiemVu)
                .append(": +").append(vang).append(" vàng, +")
                .append(kinhNghiemThucNhan).append(" EXP");
        if (ngoc > 0) {
            thongBao.append(", +").append(ngoc).append(" ngọc");
        }
    }

    private static int docInt(JSONObject duLieu, String khoa, int macDinh) {
        Object giaTri = duLieu != null ? duLieu.get(khoa) : null;
        if (giaTri == null) {
            return macDinh;
        }
        try {
            return Integer.parseInt(giaTri.toString());
        }
        catch (NumberFormatException ex) {
            return macDinh;
        }
    }

    private static long docLong(JSONObject duLieu, String khoa, long macDinh) {
        Object giaTri = duLieu != null ? duLieu.get(khoa) : null;
        if (giaTri == null) {
            return macDinh;
        }
        try {
            return Long.parseLong(giaTri.toString());
        }
        catch (NumberFormatException ex) {
            return macDinh;
        }
    }

    private static String docString(JSONObject duLieu, String khoa, String macDinh) {
        Object giaTri = duLieu != null ? duLieu.get(khoa) : null;
        return giaTri != null ? giaTri.toString() : macDinh;
    }

    private static boolean docBoolean(JSONObject duLieu, String khoa, boolean macDinh) {
        Object giaTri = duLieu != null ? duLieu.get(khoa) : null;
        if (giaTri == null) {
            return macDinh;
        }
        if (giaTri instanceof Boolean) {
            return (Boolean)giaTri;
        }
        String chuoi = giaTri.toString().trim();
        if ("1".equals(chuoi)) {
            return true;
        }
        if ("0".equals(chuoi)) {
            return false;
        }
        return Boolean.parseBoolean(chuoi);
    }
}
