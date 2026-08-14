package com.vxl.clan;

import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.xephang.VXLHinhDangNguoiChoi;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class VXLClanService {
    private static final int SO_CLAN_TOI_DA = 30;
    private static final int SO_TIN_TOI_DA = 20;
    private static final int SO_THANH_VIEN_MAC_DINH = 50;
    private static final int SO_VAT_PHAM_MOI_TRANG = 8;
    private static final byte MA_THUOC_TINH_TRANG_THAI = 31;
    private static final int CLAN_LOGO_FIRST_IMAGE = 1617;
    private static final int CLAN_LOGO_LAST_IMAGE = 1693;
    private static final short CLAN_VIETNAM_IMAGE = 2129;
    private static final VXLBieuTuongClan[] BIEU_TUONG_CLAN = taoDanhSachBieuTuongClan();
    private static final VXLVatPhamClan[] VAT_PHAM_CLAN = new VXLVatPhamClan[]{
        new VXLVatPhamClan((short)364, (byte)0, (byte)1, 100_000, 10, (byte)6, (short)5),
        new VXLVatPhamClan((short)365, (byte)1, (byte)2, 180_000, 18, (byte)7, (short)5),
        new VXLVatPhamClan((short)366, (byte)2, (byte)3, 260_000, 26, (byte)8, (short)5),
        new VXLVatPhamClan((short)367, (byte)3, (byte)4, 350_000, 35, (byte)9, (short)5),
        new VXLVatPhamClan((short)368, (byte)4, (byte)5, 450_000, 45, (byte)10, (short)3),
        new VXLVatPhamClan((short)369, (byte)5, (byte)6, 550_000, 55, (byte)11, (short)5),
        new VXLVatPhamClan((short)370, (byte)6, (byte)7, 700_000, 70, (byte)18, (short)2),
        new VXLVatPhamClan((short)371, (byte)7, (byte)5, 500_000, 50, (byte)21, (short)5),
        new VXLVatPhamClan((short)372, (byte)8, (byte)5, 500_000, 50, (byte)22, (short)5),
        new VXLVatPhamClan((short)373, (byte)0, (byte)8, 900_000, 90, (byte)6, (short)10),
        new VXLVatPhamClan((short)374, (byte)1, (byte)9, 1_100_000, 110, (byte)7, (short)10),
        new VXLVatPhamClan((short)375, (byte)2, (byte)10, 1_300_000, 130, (byte)8, (short)10),
        new VXLVatPhamClan((short)376, (byte)3, (byte)11, 1_500_000, 150, (byte)9, (short)10),
        new VXLVatPhamClan((short)377, (byte)4, (byte)12, 1_800_000, 180, (byte)10, (short)6),
        new VXLVatPhamClan((short)378, (byte)5, (byte)13, 2_100_000, 210, (byte)11, (short)10),
        new VXLVatPhamClan((short)379, (byte)6, (byte)14, 2_500_000, 250, (byte)18, (short)4),
        new VXLVatPhamClan((short)380, (byte)7, (byte)12, 1_800_000, 180, (byte)21, (short)10),
        new VXLVatPhamClan((short)381, (byte)8, (byte)12, 1_800_000, 180, (byte)22, (short)10),
        new VXLVatPhamClan((short)382, (byte)9, (byte)5, 500_000, 50, (byte)23, (short)5),
        new VXLVatPhamClan((short)383, (byte)10, (byte)5, 500_000, 50, (byte)24, (short)5),
        new VXLVatPhamClan((short)384, (byte)11, (byte)5, 500_000, 50, (byte)25, (short)5),
        new VXLVatPhamClan((short)385, (byte)9, (byte)12, 1_800_000, 180, (byte)23, (short)10),
        new VXLVatPhamClan((short)386, (byte)10, (byte)12, 1_800_000, 180, (byte)24, (short)10),
        new VXLVatPhamClan((short)387, (byte)11, (byte)12, 1_800_000, 180, (byte)25, (short)10)
    };

    private VXLClanService() {
    }

    public static void taiChoNguoiChoi(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        nguoiChoi.clan = -1;
        nguoiChoi.clanIcon = -1;
        nguoiChoi.clanRole = 2;
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
            if (clan != null) {
                nguoiChoi.clan = clan.ma;
                nguoiChoi.clanIcon = clan.bieuTuong;
                nguoiChoi.clanRole = docVaiTro(ketNoi, clan.ma, nguoiChoi.ma);
            }
        } catch (SQLException ex) {
            VXLQuanLyMayChu.log("Khong the tai clan cho " + nguoiChoi.ten + ": " + ex.getMessage());
        }
    }

    public static void guiThongTinClan(VXLNguoiChoi nguoiChoi) throws Exception {
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
            VXLTinNhan tin = new VXLTinNhan(-108);
            DataOutputStream ghi = tin.boGhi();
            if (clan == null) {
                nguoiChoi.clan = -1;
                nguoiChoi.clanIcon = -1;
                nguoiChoi.clanRole = 2;
                ghi.writeInt(-1);
            } else {
                byte vaiTro = docVaiTro(ketNoi, clan.ma, nguoiChoi.ma);
                List<VXLThanhVienClan> thanhViens = docThanhVien(ketNoi, clan.ma);
                List<VXLTinClan> cacTin = docTinClan(ketNoi, clan.ma, vaiTro);
                nguoiChoi.clan = clan.ma;
                nguoiChoi.clanIcon = clan.bieuTuong;
                nguoiChoi.clanRole = vaiTro;
                ghi.writeInt(clan.ma);
                ghi.writeUTF(clan.ten);
                ghi.writeUTF(clan.khauHieu);
                ghi.writeShort(clan.bieuTuong);
                ghi.writeUTF(hienThiSucManh(clan.tongCup));
                ghi.writeUTF(clan.tenTruongClan);
                ghi.writeByte(gioiHanByte(thanhViens.size()));
                ghi.writeByte(gioiHanByte(clan.soThanhVienToiDa));
                ghi.writeByte(vaiTro);
                for (VXLThanhVienClan thanhVien : thanhViens) {
                    ghiThanhVien(ghi, thanhVien);
                }
                ghi.writeByte(gioiHanByte(cacTin.size()));
                for (VXLTinClan tinClan : cacTin) {
                    ghiTinClan(ghi, tinClan);
                }
                ghi.writeByte(gioiHanByte(clan.cap));
            }
            ghi.flush();
            nguoiChoi.dichVu.guiTin(tin);
        }
    }

    public static void guiTopClan(VXLNguoiChoi nguoiChoi) throws Exception {
        List<VXLClan> danhSach;
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            danhSach = docDanhSachClan(ketNoi, "", true);
        }
        VXLTinNhan tin = new VXLTinNhan(-117);
        DataOutputStream ghi = tin.boGhi();
        ghi.writeByte(gioiHanByte(danhSach.size()));
        for (int i = 0; i < danhSach.size(); i++) {
            VXLClan clan = danhSach.get(i);
            ghi.writeInt(i + 1);
            ghi.writeInt(clan.ma);
            ghi.writeUTF(clan.ten);
            ghi.writeUTF(khauHieuChoTop(clan.khauHieu));
            ghi.writeShort(clan.bieuTuong);
            ghi.writeByte(gioiHanByte(clan.soThanhVien));
            ghi.writeByte(gioiHanByte(clan.soThanhVienToiDa));
            ghi.writeUTF(hienThiSucManh(clan.tongCup));
            ghi.writeInt(clan.ngayTao);
            ghi.writeUTF(clan.tenTruongClan);
            ghi.writeByte(gioiHanByte(clan.cap));
            ghi.writeByte(tinhPhanTramCap(clan.kinhNghiem));
        }
        ghi.flush();
        nguoiChoi.dichVu.guiTin(tin);
    }

    public static void timClan(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        String tuKhoa = yeuCau.docUTF(50, "ten clan").trim();
        List<VXLClan> danhSach;
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            danhSach = docDanhSachClan(ketNoi, tuKhoa, false);
        }
        VXLTinNhan tin = new VXLTinNhan(-113);
        DataOutputStream ghi = tin.boGhi();
        ghi.writeByte(gioiHanByte(danhSach.size()));
        for (VXLClan clan : danhSach) {
            ghi.writeInt(clan.ma);
            ghi.writeUTF(clan.ten);
            ghi.writeUTF(clan.khauHieu);
            ghi.writeShort(clan.bieuTuong);
            ghi.writeUTF(hienThiSucManh(clan.tongCup));
            ghi.writeUTF(clan.tenTruongClan);
            ghi.writeByte(gioiHanByte(clan.soThanhVien));
            ghi.writeByte(gioiHanByte(clan.soThanhVienToiDa));
            ghi.writeInt(clan.ngayTao);
        }
        ghi.flush();
        nguoiChoi.dichVu.guiTin(tin);
    }

    public static void xuLyClan(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        DataInputStream doc = yeuCau.boDoc();
        byte hanhDong = doc.readByte();
        if (hanhDong == 1 || hanhDong == 3) {
            doc.readByte();
            guiBieuTuongClan(nguoiChoi, hanhDong);
            return;
        }
        if (hanhDong == 2) {
            taoClan(nguoiChoi, doc.readShort(), yeuCau.docUTF(50, "ten clan").trim());
            return;
        }
        if (hanhDong == 4) {
            capNhatClan(nguoiChoi, doc.readShort(), yeuCau.docUTF(200, "khau hieu clan").trim());
        }
    }

    public static void guiThanhVienClan(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        DataInputStream doc = yeuCau.boDoc();
        int maClan;
        if (doc.available() >= 4) {
            maClan = doc.readInt();
        } else if (doc.available() >= 3) {
            doc.readByte();
            maClan = doc.readUnsignedShort();
        } else {
            maClan = nguoiChoi.clan;
        }
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            VXLClan clan = docClan(ketNoi, maClan);
            List<VXLThanhVienClan> thanhViens = clan == null ? Collections.emptyList() : docThanhVien(ketNoi, maClan);
            VXLTinNhan tin = new VXLTinNhan(-105);
            DataOutputStream ghi = tin.boGhi();
            ghi.writeInt(clan == null ? -1 : clan.ma);
            ghi.writeByte(gioiHanByte(thanhViens.size()));
            for (VXLThanhVienClan thanhVien : thanhViens) {
                ghiThanhVien(ghi, thanhVien);
            }
            ghi.flush();
            nguoiChoi.dichVu.guiTin(tin);
        }
    }

    public static void xuLyTinClan(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        byte loai = yeuCau.boDoc().readByte();
        if (loai == 0) {
            String noiDung = yeuCau.docUTF(200, "tin nhan clan").trim();
            if (!noiDung.isEmpty()) {
                guiChatClan(nguoiChoi, noiDung);
            }
        } else if (loai == 2) {
            guiYeuCauGiaNhap(nguoiChoi, yeuCau.boDoc().readInt());
        }
    }

    public static void xuLyDuyetGiaNhap(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        int maTin = yeuCau.boDoc().readInt();
        byte hanhDong = yeuCau.boDoc().readByte();
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            ketNoi.setAutoCommit(false);
            try {
                VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
                if (clan == null || docVaiTro(ketNoi, clan.ma, nguoiChoi.ma) != 0) {
                    throw new XuLyClanException("Chỉ trưởng clan được duyệt thành viên.");
                }
                int maUngVien;
                try (PreparedStatement lenh = ketNoi.prepareStatement(
                        "SELECT player_id FROM clan_messages WHERE id=? AND clan_id=? AND message_type=2 AND resolved=0 FOR UPDATE")) {
                    lenh.setInt(1, maTin);
                    lenh.setInt(2, clan.ma);
                    try (ResultSet duLieu = lenh.executeQuery()) {
                        if (!duLieu.next()) {
                            throw new XuLyClanException("Yêu cầu gia nhập không còn hiệu lực.");
                        }
                        maUngVien = duLieu.getInt(1);
                    }
                }
                if (hanhDong == 0) {
                    if (docClanTheoNguoiChoi(ketNoi, maUngVien) != null) {
                        throw new XuLyClanException("Người chơi đã vào clan khác.");
                    }
                    if (demThanhVien(ketNoi, clan.ma) >= clan.soThanhVienToiDa) {
                        throw new XuLyClanException("Clan đã đủ thành viên.");
                    }
                    try (PreparedStatement lenh = ketNoi.prepareStatement(
                            "INSERT INTO clan_members(clan_id,player_id,member_role) VALUES(?,?,2)")) {
                        lenh.setInt(1, clan.ma);
                        lenh.setInt(2, maUngVien);
                        lenh.executeUpdate();
                    }
                }
                try (PreparedStatement lenh = ketNoi.prepareStatement(
                        "UPDATE clan_messages SET resolved=1 WHERE id=?")) {
                    lenh.setInt(1, maTin);
                    lenh.executeUpdate();
                }
                ketNoi.commit();
                VXLNguoiChoi ungVien = VXLNguoiChoi.layNguoiChoiTheoMa(maUngVien);
                if (ungVien != null) {
                    taiChoNguoiChoi(ungVien);
                    ungVien.moHopThoaiOK(hanhDong == 0
                            ? "Bạn đã gia nhập clan " + clan.ten + "."
                            : "Yêu cầu gia nhập clan đã bị từ chối.");
                    guiThongTinClan(ungVien);
                }
                lamMoiClanOnline(clan.ma);
            } catch (Exception ex) {
                ketNoi.rollback();
                throw ex;
            } finally {
                ketNoi.setAutoCommit(true);
            }
        } catch (XuLyClanException ex) {
            nguoiChoi.moHopThoaiOK(ex.getMessage());
        }
    }

    public static void roiClan(VXLNguoiChoi nguoiChoi) throws Exception {
        int clanCu = nguoiChoi.clan;
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            ketNoi.setAutoCommit(false);
            try {
                VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
                if (clan == null) {
                    throw new XuLyClanException("Bạn chưa tham gia clan.");
                }
                byte vaiTro = docVaiTro(ketNoi, clan.ma, nguoiChoi.ma);
                int soThanhVien = demThanhVien(ketNoi, clan.ma);
                if (vaiTro == 0 && soThanhVien > 1) {
                    throw new XuLyClanException("Hãy chuyển chức trưởng clan trước khi rời.");
                }
                if (soThanhVien <= 1) {
                    xoaDuLieuClan(ketNoi, clan.ma);
                } else {
                    try (PreparedStatement lenh = ketNoi.prepareStatement(
                            "DELETE FROM clan_members WHERE player_id=?")) {
                        lenh.setInt(1, nguoiChoi.ma);
                        lenh.executeUpdate();
                    }
                }
                ketNoi.commit();
                nguoiChoi.clan = -1;
                nguoiChoi.clanIcon = -1;
                nguoiChoi.clanRole = 2;
                guiThongTinClan(nguoiChoi);
                capNhatNguoiChoiTaiSanh(nguoiChoi);
                nguoiChoi.moHopThoaiOK(soThanhVien <= 1 ? "Đã giải tán clan." : "Đã rời clan.");
                if (soThanhVien > 1) {
                    lamMoiClanOnline(clanCu);
                }
            } catch (Exception ex) {
                ketNoi.rollback();
                throw ex;
            } finally {
                ketNoi.setAutoCommit(true);
            }
        } catch (XuLyClanException ex) {
            nguoiChoi.moHopThoaiOK(ex.getMessage());
        }
    }

    public static void quanLyThanhVien(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        int maThanhVien = yeuCau.boDoc().readInt();
        byte vaiTroMoi = yeuCau.boDoc().readByte();
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            ketNoi.setAutoCommit(false);
            try {
                VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
                if (clan == null) {
                    throw new XuLyClanException("Bạn chưa tham gia clan.");
                }
                byte vaiTroNguoiThucHien = docVaiTro(ketNoi, clan.ma, nguoiChoi.ma);
                byte vaiTroHienTai = docVaiTro(ketNoi, clan.ma, maThanhVien);
                if (vaiTroHienTai < 0 || maThanhVien == nguoiChoi.ma) {
                    throw new XuLyClanException("Thành viên không hợp lệ.");
                }
                if (vaiTroMoi == -1) {
                    if (vaiTroNguoiThucHien >= vaiTroHienTai) {
                        throw new XuLyClanException("Bạn không có quyền loại thành viên này.");
                    }
                    try (PreparedStatement lenh = ketNoi.prepareStatement(
                            "DELETE FROM clan_members WHERE clan_id=? AND player_id=?")) {
                        lenh.setInt(1, clan.ma);
                        lenh.setInt(2, maThanhVien);
                        lenh.executeUpdate();
                    }
                } else if (vaiTroMoi == 0) {
                    if (vaiTroNguoiThucHien != 0) {
                        throw new XuLyClanException("Chỉ trưởng clan được chuyển chức.");
                    }
                    try (PreparedStatement haCap = ketNoi.prepareStatement(
                            "UPDATE clan_members SET member_role=1 WHERE clan_id=? AND player_id=?");
                         PreparedStatement thangCap = ketNoi.prepareStatement(
                            "UPDATE clan_members SET member_role=0 WHERE clan_id=? AND player_id=?");
                         PreparedStatement doiTruong = ketNoi.prepareStatement(
                            "UPDATE clans SET leader_player_id=? WHERE id=?")) {
                        haCap.setInt(1, clan.ma);
                        haCap.setInt(2, nguoiChoi.ma);
                        haCap.executeUpdate();
                        thangCap.setInt(1, clan.ma);
                        thangCap.setInt(2, maThanhVien);
                        thangCap.executeUpdate();
                        doiTruong.setInt(1, maThanhVien);
                        doiTruong.setInt(2, clan.ma);
                        doiTruong.executeUpdate();
                    }
                } else if (vaiTroMoi == 1 || vaiTroMoi == 2) {
                    if (vaiTroNguoiThucHien != 0) {
                        throw new XuLyClanException("Chỉ trưởng clan được đổi chức vụ.");
                    }
                    try (PreparedStatement lenh = ketNoi.prepareStatement(
                            "UPDATE clan_members SET member_role=? WHERE clan_id=? AND player_id=?")) {
                        lenh.setByte(1, vaiTroMoi);
                        lenh.setInt(2, clan.ma);
                        lenh.setInt(3, maThanhVien);
                        lenh.executeUpdate();
                    }
                } else {
                    throw new XuLyClanException("Chức vụ không hợp lệ.");
                }
                ketNoi.commit();
                VXLNguoiChoi thanhVien = VXLNguoiChoi.layNguoiChoiTheoMa(maThanhVien);
                if (thanhVien != null && vaiTroMoi == -1) {
                    thanhVien.clan = -1;
                    thanhVien.clanIcon = -1;
                    thanhVien.clanRole = 2;
                    thanhVien.moHopThoaiOK("Bạn đã bị loại khỏi clan " + clan.ten + ".");
                    guiThongTinClan(thanhVien);
                }
                lamMoiClanOnline(clan.ma);
            } catch (Exception ex) {
                ketNoi.rollback();
                throw ex;
            } finally {
                ketNoi.setAutoCommit(true);
            }
        } catch (XuLyClanException ex) {
            nguoiChoi.moHopThoaiOK(ex.getMessage());
        }
    }

    public static void xuLyMoiClan(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        byte hanhDong = yeuCau.boDoc().readByte();
        if (hanhDong == 0) {
            moiVaoClan(nguoiChoi, yeuCau.boDoc().readInt());
            return;
        }
        int maClan = yeuCau.boDoc().readInt();
        int maMoi = yeuCau.boDoc().readInt();
        traLoiLoiMoi(nguoiChoi, maClan, maMoi, hanhDong == 1);
    }

    public static void guiTrangThaiClan(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        DataInputStream doc = yeuCau.boDoc();
        if (doc.available() >= 3) {
            byte hanhDong = doc.readByte();
            if (hanhDong == 1) {
                kichHoatVatPhamClan(nguoiChoi, doc.readUnsignedShort());
                return;
            }
        }
        guiTrangThaiClan(nguoiChoi);
    }

    public static void guiCuaHangClan(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        int trang = 0;
        if (yeuCau.boDoc().available() > 0) {
            trang = yeuCau.boDoc().readUnsignedByte();
        }
        guiCuaHangClan(nguoiChoi, trang);
    }

    public static boolean muaVatPhamClanNeuCan(VXLNguoiChoi nguoiChoi, VXLTinNhan yeuCau) throws Exception {
        byte[] duLieu = yeuCau.layDuLieu();
        if (duLieu.length < 3) {
            return false;
        }
        int maVatPham = (duLieu[1] & 0xff) << 8 | duLieu[2] & 0xff;
        VXLVatPhamClan vatPham = timVatPhamClan(maVatPham);
        if (vatPham == null) {
            return false;
        }
        DataInputStream doc = yeuCau.boDoc();
        int loaiTien = doc.readUnsignedByte();
        doc.readUnsignedShort();
        int soLuong = doc.available() > 0 ? doc.readUnsignedByte() : 1;
        muaVatPhamClan(nguoiChoi, vatPham, loaiTien, soLuong);
        return true;
    }

    public static VXLHieuUngClan layHieuUngClan(int maClan) {
        if (maClan < 0) {
            return VXLHieuUngClan.KHONG_CO;
        }
        VXLHieuUngClan hieuUng = new VXLHieuUngClan();
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(
                     "SELECT item_id FROM clan_items WHERE clan_id=? AND active=1")) {
            lenh.setInt(1, maClan);
            try (ResultSet duLieu = lenh.executeQuery()) {
                while (duLieu.next()) {
                    VXLVatPhamClan vatPham = timVatPhamClan(duLieu.getInt(1));
                    if (vatPham != null) {
                        apDungHieuUng(hieuUng, vatPham);
                    }
                }
            }
        } catch (SQLException ex) {
            VXLQuanLyMayChu.log("Khong the tai hieu ung clan " + maClan + ": " + ex.getMessage());
            return VXLHieuUngClan.KHONG_CO;
        }
        return hieuUng;
    }

    private static void guiTrangThaiClan(VXLNguoiChoi nguoiChoi) throws IOException, SQLException {
        List<VXLVatPhamClanDaMua> daMua = new ArrayList<>();
        if (nguoiChoi.clan >= 0) {
            try (Connection ketNoi = VXLCoSoDuLieu.getConnection();
                 PreparedStatement lenh = ketNoi.prepareStatement(
                         "SELECT item_id,active FROM clan_items WHERE clan_id=? ORDER BY item_id")) {
                lenh.setInt(1, nguoiChoi.clan);
                try (ResultSet duLieu = lenh.executeQuery()) {
                    while (duLieu.next()) {
                        VXLVatPhamClan vatPham = timVatPhamClan(duLieu.getInt("item_id"));
                        if (vatPham != null) {
                            daMua.add(new VXLVatPhamClanDaMua(vatPham, duLieu.getBoolean("active")));
                        }
                    }
                }
            }
        }
        VXLTinNhan tin = new VXLTinNhan(-119);
        DataOutputStream ghi = tin.boGhi();
        ghi.writeByte(gioiHanByte(daMua.size()));
        for (VXLVatPhamClanDaMua muc : daMua) {
            ghi.writeShort(muc.vatPham().ma());
            ghi.writeByte(2);
            ghi.writeByte(muc.vatPham().maThuocTinh());
            ghi.writeShort(muc.vatPham().giaTri());
            ghi.writeByte(MA_THUOC_TINH_TRANG_THAI);
            ghi.writeShort(muc.dangDung() ? 1 : 0);
        }
        ghi.flush();
        nguoiChoi.dichVu.guiTin(tin);
    }

    private static void muaVatPhamClan(VXLNguoiChoi nguoiChoi, VXLVatPhamClan vatPham,
            int loaiTien, int soLuong) throws Exception {
        if (soLuong != 1 || loaiTien < 0 || loaiTien > 1) {
            nguoiChoi.moHopThoaiOK("Y\u00eau c\u1ea7u mua v\u1eadt ph\u1ea9m \u0111\u1ed9i kh\u00f4ng h\u1ee3p l\u1ec7.");
            return;
        }
        int vangConLai;
        int ngocConLai;
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            ketNoi.setAutoCommit(false);
            try {
                VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
                if (clan == null) {
                    throw new XuLyClanException("B\u1ea1n ch\u01b0a tham gia \u0111\u1ed9i.");
                }
                byte vaiTro = docVaiTro(ketNoi, clan.ma, nguoiChoi.ma);
                if (vaiTro < 0 || vaiTro > 1) {
                    throw new XuLyClanException("Ch\u1ec9 tr\u01b0\u1edfng ho\u1eb7c ph\u00f3 \u0111\u1ed9i \u0111\u01b0\u1ee3c mua v\u1eadt ph\u1ea9m \u0111\u1ed9i.");
                }
                if (clan.cap < vatPham.capYeuCau()) {
                    throw new XuLyClanException("\u0110\u1ed9i c\u1ea7n \u0111\u1ea1t c\u1ea5p " + vatPham.capYeuCau()
                            + " \u0111\u1ec3 mua v\u1eadt ph\u1ea9m n\u00e0y.");
                }
                try (PreparedStatement daCo = ketNoi.prepareStatement(
                        "SELECT 1 FROM clan_items WHERE clan_id=? AND item_id=? LIMIT 1")) {
                    daCo.setInt(1, clan.ma);
                    daCo.setShort(2, vatPham.ma());
                    try (ResultSet duLieu = daCo.executeQuery()) {
                        if (duLieu.next()) {
                            throw new XuLyClanException("\u0110\u1ed9i \u0111\u00e3 s\u1edf h\u1eefu v\u1eadt ph\u1ea9m n\u00e0y.");
                        }
                    }
                }
                try (PreparedStatement khoaTien = ketNoi.prepareStatement(
                        "SELECT gold,gem FROM players WHERE id=? FOR UPDATE")) {
                    khoaTien.setInt(1, nguoiChoi.ma);
                    try (ResultSet duLieu = khoaTien.executeQuery()) {
                        if (!duLieu.next()) {
                            throw new SQLException("Kh\u00f4ng t\u00ecm th\u1ea5y ng\u01b0\u1eddi mua v\u1eadt ph\u1ea9m \u0111\u1ed9i.");
                        }
                        int vang = duLieu.getInt("gold");
                        int ngoc = duLieu.getInt("gem");
                        int gia = loaiTien == 0 ? vatPham.vang() : vatPham.ngoc();
                        if (gia <= 0) {
                            throw new XuLyClanException("Kh\u00f4ng h\u1ed7 tr\u1ee3 lo\u1ea1i ti\u1ec1n n\u00e0y.");
                        }
                        if (loaiTien == 0 && vang < gia) {
                            throw new XuLyClanException("B\u1ea1n kh\u00f4ng \u0111\u1ee7 v\u00e0ng.");
                        }
                        if (loaiTien == 1 && ngoc < gia) {
                            throw new XuLyClanException("B\u1ea1n kh\u00f4ng \u0111\u1ee7 ng\u1ecdc.");
                        }
                        vangConLai = loaiTien == 0 ? vang - gia : vang;
                        ngocConLai = loaiTien == 1 ? ngoc - gia : ngoc;
                    }
                }
                try (PreparedStatement truTien = ketNoi.prepareStatement(
                        "UPDATE players SET gold=?,gem=? WHERE id=?")) {
                    truTien.setInt(1, vangConLai);
                    truTien.setInt(2, ngocConLai);
                    truTien.setInt(3, nguoiChoi.ma);
                    truTien.executeUpdate();
                }
                try (PreparedStatement them = ketNoi.prepareStatement(
                        "INSERT INTO clan_items(clan_id,item_id,purchased_by) VALUES(?,?,?)")) {
                    them.setInt(1, clan.ma);
                    them.setShort(2, vatPham.ma());
                    them.setInt(3, nguoiChoi.ma);
                    them.executeUpdate();
                }
                ketNoi.commit();
            } catch (Exception ex) {
                ketNoi.rollback();
                throw ex;
            } finally {
                ketNoi.setAutoCommit(true);
            }
        } catch (XuLyClanException ex) {
            nguoiChoi.moHopThoaiOK(ex.getMessage());
            return;
        }
        nguoiChoi.vang = vangConLai;
        nguoiChoi.ngoc = ngocConLai;
        nguoiChoi.dichVu.capNhat();
        guiTrangThaiClanChoClan(nguoiChoi.clan);
        guiCuaHangClan(nguoiChoi, 0);
        nguoiChoi.moHopThoaiOK("Mua v\u1eadt ph\u1ea9m \u0111\u1ed9i th\u00e0nh c\u00f4ng.");
    }

    private static void kichHoatVatPhamClan(VXLNguoiChoi nguoiChoi, int maVatPham) throws Exception {
        VXLVatPhamClan vatPham = timVatPhamClan(maVatPham);
        if (vatPham == null) {
            nguoiChoi.moHopThoaiOK("V\u1eadt ph\u1ea9m \u0111\u1ed9i kh\u00f4ng h\u1ee3p l\u1ec7.");
            return;
        }
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            ketNoi.setAutoCommit(false);
            try {
                VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
                if (clan == null) {
                    throw new XuLyClanException("B\u1ea1n ch\u01b0a tham gia \u0111\u1ed9i.");
                }
                byte vaiTro = docVaiTro(ketNoi, clan.ma, nguoiChoi.ma);
                if (vaiTro < 0 || vaiTro > 1) {
                    throw new XuLyClanException("Ch\u1ec9 tr\u01b0\u1edfng ho\u1eb7c ph\u00f3 \u0111\u1ed9i \u0111\u01b0\u1ee3c \u0111\u1ed5i v\u1eadt ph\u1ea9m \u0111ang d\u00f9ng.");
                }
                if (clan.cap < vatPham.capYeuCau()) {
                    throw new XuLyClanException("\u0110\u1ed9i ch\u01b0a \u0111\u1ee7 c\u1ea5p \u0111\u1ec3 d\u00f9ng v\u1eadt ph\u1ea9m n\u00e0y.");
                }
                try (PreparedStatement daCo = ketNoi.prepareStatement(
                        "SELECT 1 FROM clan_items WHERE clan_id=? AND item_id=? LIMIT 1 FOR UPDATE")) {
                    daCo.setInt(1, clan.ma);
                    daCo.setShort(2, vatPham.ma());
                    try (ResultSet duLieu = daCo.executeQuery()) {
                        if (!duLieu.next()) {
                            throw new XuLyClanException("\u0110\u1ed9i ch\u01b0a s\u1edf h\u1eefu v\u1eadt ph\u1ea9m n\u00e0y.");
                        }
                    }
                }
                try (PreparedStatement tat = ketNoi.prepareStatement(
                        "UPDATE clan_items SET active=0 WHERE clan_id=? AND item_id IN (?,?)")) {
                    tat.setInt(1, clan.ma);
                    short[] cungNhom = layMaVatPhamCungNhom(vatPham.nhom());
                    tat.setShort(2, cungNhom[0]);
                    tat.setShort(3, cungNhom[1]);
                    tat.executeUpdate();
                }
                try (PreparedStatement bat = ketNoi.prepareStatement(
                        "UPDATE clan_items SET active=1,activated_at=CURRENT_TIMESTAMP WHERE clan_id=? AND item_id=?")) {
                    bat.setInt(1, clan.ma);
                    bat.setShort(2, vatPham.ma());
                    bat.executeUpdate();
                }
                ketNoi.commit();
            } catch (Exception ex) {
                ketNoi.rollback();
                throw ex;
            } finally {
                ketNoi.setAutoCommit(true);
            }
        } catch (XuLyClanException ex) {
            nguoiChoi.moHopThoaiOK(ex.getMessage());
            return;
        }
        guiTrangThaiClanChoClan(nguoiChoi.clan);
        nguoiChoi.moHopThoaiOK("\u0110\u00e3 \u0111\u1ed5i v\u1eadt ph\u1ea9m \u0111ang d\u00f9ng.");
    }

    private static void guiTrangThaiClanChoClan(int maClan) {
        for (VXLNguoiChoi online : VXLNguoiChoi.players_id.values()) {
            if (online == null || online.clan != maClan) {
                continue;
            }
            try {
                guiTrangThaiClan(online);
            } catch (Exception ex) {
                VXLQuanLyMayChu.log("Khong the cap nhat trang thai clan cho " + online.ten + ": " + ex.getMessage());
            }
        }
    }

    private static VXLVatPhamClan timVatPhamClan(int maVatPham) {
        for (VXLVatPhamClan vatPham : VAT_PHAM_CLAN) {
            if (vatPham.ma() == maVatPham) {
                return vatPham;
            }
        }
        return null;
    }

    private static short[] layMaVatPhamCungNhom(byte nhom) {
        short capMot = -1;
        short capHai = -1;
        for (VXLVatPhamClan vatPham : VAT_PHAM_CLAN) {
            if (vatPham.nhom() != nhom) {
                continue;
            }
            if (capMot == -1) {
                capMot = vatPham.ma();
            } else {
                capHai = vatPham.ma();
            }
        }
        return new short[]{capMot, capHai == -1 ? capMot : capHai};
    }

    private static void apDungHieuUng(VXLHieuUngClan hieuUng, VXLVatPhamClan vatPham) {
        int giaTri = vatPham.giaTri();
        switch (vatPham.nhom()) {
            case 0 -> hieuUng.sinhLuc = Math.max(hieuUng.sinhLuc, giaTri);
            case 1 -> hieuUng.hoaLuc = Math.max(hieuUng.hoaLuc, giaTri);
            case 2 -> hieuUng.phongThu = Math.max(hieuUng.phongThu, giaTri);
            case 3 -> hieuUng.mayMan = Math.max(hieuUng.mayMan, giaTri);
            case 4 -> hieuUng.dongDoi = Math.max(hieuUng.dongDoi, giaTri);
            case 5 -> hieuUng.tocDo = Math.max(hieuUng.tocDo, giaTri);
            case 6 -> hieuUng.tatCa = Math.max(hieuUng.tatCa, giaTri);
            case 7 -> hieuUng.danChongTang = Math.max(hieuUng.danChongTang, giaTri);
            case 8 -> hieuUng.danSungTruong = Math.max(hieuUng.danSungTruong, giaTri);
            case 9 -> hieuUng.danTieuLien = Math.max(hieuUng.danTieuLien, giaTri);
            case 10 -> hieuUng.danChuoi = Math.max(hieuUng.danChuoi, giaTri);
            case 11 -> hieuUng.danHoaCai = Math.max(hieuUng.danHoaCai, giaTri);
            default -> {
            }
        }
    }

    private static void guiCuaHangClan(VXLNguoiChoi nguoiChoi, int trangYeuCau) throws IOException, SQLException {
        List<VXLVatPhamClan> moKhoa = new ArrayList<>();
        int capClan = 0;
        if (nguoiChoi.clan >= 0) {
            try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
                VXLClan clan = docClan(ketNoi, nguoiChoi.clan);
                if (clan != null) {
                    capClan = clan.cap;
                    List<Integer> daMua = new ArrayList<>();
                    try (PreparedStatement lenh = ketNoi.prepareStatement(
                            "SELECT item_id FROM clan_items WHERE clan_id=?")) {
                        lenh.setInt(1, clan.ma);
                        try (ResultSet duLieu = lenh.executeQuery()) {
                            while (duLieu.next()) {
                                daMua.add(duLieu.getInt(1));
                            }
                        }
                    }
                    for (VXLVatPhamClan vatPham : VAT_PHAM_CLAN) {
                        if (vatPham.capYeuCau() <= capClan && !daMua.contains((int)vatPham.ma())) {
                            moKhoa.add(vatPham);
                        }
                    }
                }
            }
        }
        int soTrang = Math.max(1, (moKhoa.size() + SO_VAT_PHAM_MOI_TRANG - 1) / SO_VAT_PHAM_MOI_TRANG);
        int trang = Math.max(0, Math.min(soTrang - 1, trangYeuCau));
        int batDau = trang * SO_VAT_PHAM_MOI_TRANG;
        int ketThuc = Math.min(moKhoa.size(), batDau + SO_VAT_PHAM_MOI_TRANG);
        VXLTinNhan tin = new VXLTinNhan(-118);
        DataOutputStream ghi = tin.boGhi();
        ghi.writeByte(trang);
        ghi.writeByte(soTrang);
        ghi.writeByte(ketThuc - batDau);
        for (int i = batDau; i < ketThuc; i++) {
            VXLVatPhamClan vatPham = moKhoa.get(i);
            ghi.writeShort(vatPham.ma());
            ghi.writeInt(vatPham.vang());
            ghi.writeInt(vatPham.ngoc());
            ghi.writeByte(1);
            ghi.writeByte(vatPham.maThuocTinh());
            ghi.writeShort(vatPham.giaTri());
        }
        ghi.flush();
        nguoiChoi.dichVu.guiTin(tin);
    }

    private static void guiBieuTuongClan(VXLNguoiChoi nguoiChoi, byte hanhDong) throws Exception {
        int capClan = 1;
        if (hanhDong == 3) {
            try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
                VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
                if (clan != null) {
                    capClan = clan.cap;
                }
            }
        }
        int soBieuTuong = soBieuTuongMoKhoa(capClan);
        VXLTinNhan tin = new VXLTinNhan(-103);
        DataOutputStream ghi = tin.boGhi();
        ghi.writeByte(hanhDong);
        ghi.writeByte(0);
        ghi.writeByte(1);
        ghi.writeByte(soBieuTuong);
        for (int i = 0; i < soBieuTuong; i++) {
            VXLBieuTuongClan bieuTuong = BIEU_TUONG_CLAN[i];
            ghi.writeShort(bieuTuong.ma());
            ghi.writeUTF(bieuTuong.ten());
            ghi.writeInt(0);
            ghi.writeInt(0);
        }
        ghi.flush();
        nguoiChoi.dichVu.guiTin(tin);
    }

    private static void taoClan(VXLNguoiChoi nguoiChoi, short bieuTuong, String ten) throws Exception {
        bieuTuong = chuanHoaBieuTuong(bieuTuong);
        if (!bieuTuongMoKhoa(bieuTuong, 1)) {
            nguoiChoi.moHopThoaiOK("Biểu tượng clan không hợp lệ.");
            return;
        }
        if (ten.length() < 3 || ten.length() > 20) {
            nguoiChoi.moHopThoaiOK("Tên clan phải từ 3 đến 20 ký tự.");
            return;
        }
        int maClan;
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            ketNoi.setAutoCommit(false);
            try {
                if (docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma) != null) {
                    throw new XuLyClanException("Bạn đã tham gia clan.");
                }
                if (nguoiChoi.vang < VXLQuanLyMayChu.clanCreateGold) {
                    throw new XuLyClanException("Bạn không đủ vàng để tạo clan.");
                }
                try (PreparedStatement trungTen = ketNoi.prepareStatement(
                        "SELECT id FROM clans WHERE LOWER(name)=LOWER(?) LIMIT 1")) {
                    trungTen.setString(1, ten);
                    try (ResultSet duLieu = trungTen.executeQuery()) {
                        if (duLieu.next()) {
                            throw new XuLyClanException("Tên clan đã tồn tại.");
                        }
                    }
                }
                try (PreparedStatement lenh = ketNoi.prepareStatement(
                        "INSERT INTO clans(name,icon_id,leader_player_id,level,exp,clan_gold,max_members,slogan) "
                                + "VALUES(?,?,?,1,0,0,?, '')", Statement.RETURN_GENERATED_KEYS)) {
                    lenh.setString(1, ten);
                    lenh.setShort(2, bieuTuong);
                    lenh.setInt(3, nguoiChoi.ma);
                    lenh.setInt(4, SO_THANH_VIEN_MAC_DINH);
                    lenh.executeUpdate();
                    try (ResultSet khoa = lenh.getGeneratedKeys()) {
                        if (!khoa.next()) {
                            throw new SQLException("Không lấy được mã clan mới.");
                        }
                        maClan = khoa.getInt(1);
                    }
                }
                try (PreparedStatement lenh = ketNoi.prepareStatement(
                        "INSERT INTO clan_members(clan_id,player_id,member_role) VALUES(?,?,0)")) {
                    lenh.setInt(1, maClan);
                    lenh.setInt(2, nguoiChoi.ma);
                    lenh.executeUpdate();
                }
                int vangConLai = nguoiChoi.vang - VXLQuanLyMayChu.clanCreateGold;
                try (PreparedStatement lenh = ketNoi.prepareStatement(
                        "UPDATE players SET gold=? WHERE id=?")) {
                    lenh.setInt(1, vangConLai);
                    lenh.setInt(2, nguoiChoi.ma);
                    lenh.executeUpdate();
                }
                ketNoi.commit();
                nguoiChoi.vang = vangConLai;
                nguoiChoi.clan = maClan;
                nguoiChoi.clanIcon = bieuTuong;
                nguoiChoi.clanRole = 0;
                nguoiChoi.dichVu.capNhat();
                guiThongTinClan(nguoiChoi);
                capNhatNguoiChoiTaiSanh(nguoiChoi);
                nguoiChoi.moHopThoaiOK("Tạo clan " + ten + " thành công.");
            } catch (Exception ex) {
                ketNoi.rollback();
                throw ex;
            } finally {
                ketNoi.setAutoCommit(true);
            }
        } catch (XuLyClanException ex) {
            nguoiChoi.moHopThoaiOK(ex.getMessage());
        }
    }

    private static void capNhatClan(VXLNguoiChoi nguoiChoi, short bieuTuong, String khauHieu) throws Exception {
        bieuTuong = chuanHoaBieuTuong(bieuTuong);
        if (!bieuTuongHopLe(bieuTuong)) {
            nguoiChoi.moHopThoaiOK("Biểu tượng clan không hợp lệ.");
            return;
        }
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
            if (clan == null || docVaiTro(ketNoi, clan.ma, nguoiChoi.ma) != 0) {
                nguoiChoi.moHopThoaiOK("Chỉ trưởng clan được sửa thông tin.");
                return;
            }
            if (!bieuTuongMoKhoa(bieuTuong, clan.cap)) {
                nguoiChoi.moHopThoaiOK("Bi\u1ec3u t\u01b0\u1ee3ng n\u00e0y ch\u01b0a m\u1edf kh\u00f3a \u1edf c\u1ea5p \u0111\u1ed9i hi\u1ec7n t\u1ea1i.");
                return;
            }
            String khauHieuMoi = khauHieu.isEmpty() ? clan.khauHieu : khauHieu;
            try (PreparedStatement lenh = ketNoi.prepareStatement(
                    "UPDATE clans SET icon_id=?,slogan=? WHERE id=?")) {
                lenh.setShort(1, bieuTuong);
                lenh.setString(2, khauHieuMoi);
                lenh.setInt(3, clan.ma);
                lenh.executeUpdate();
            }
            for (VXLNguoiChoi online : VXLNguoiChoi.players_id.values()) {
                if (online != null && online.clan == clan.ma) {
                    online.clanIcon = bieuTuong;
                }
            }
            VXLTinNhan tin = new VXLTinNhan(-103);
            DataOutputStream ghi = tin.boGhi();
            ghi.writeByte(4);
            ghi.writeShort(bieuTuong);
            ghi.writeUTF(khauHieuMoi);
            ghi.flush();
            nguoiChoi.dichVu.guiTin(tin);
            lamMoiClanOnline(clan.ma);
        }
    }

    private static void guiChatClan(VXLNguoiChoi nguoiChoi, String noiDung) throws Exception {
        if (nguoiChoi.clan < 0) {
            nguoiChoi.moHopThoaiOK("Bạn chưa tham gia clan.");
            return;
        }
        VXLTinClan tinClan = new VXLTinClan();
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(
                     "INSERT INTO clan_messages(clan_id,message_type,player_id,message_text,message_color) "
                             + "VALUES(?,0,?,?,0)", Statement.RETURN_GENERATED_KEYS)) {
            lenh.setInt(1, nguoiChoi.clan);
            lenh.setInt(2, nguoiChoi.ma);
            lenh.setString(3, noiDung);
            lenh.executeUpdate();
            try (ResultSet khoa = lenh.getGeneratedKeys()) {
                if (khoa.next()) {
                    tinClan.ma = khoa.getInt(1);
                }
            }
        }
        tinClan.loai = 0;
        tinClan.maNguoiChoi = nguoiChoi.ma;
        tinClan.tenNguoiChoi = nguoiChoi.ten;
        tinClan.vaiTro = nguoiChoi.clanRole;
        tinClan.thoiGian = thoiGianHienTai();
        tinClan.noiDung = noiDung;
        guiTinDenClan(nguoiChoi.clan, tinClan);
    }

    private static void guiYeuCauGiaNhap(VXLNguoiChoi nguoiChoi, int maClan) throws Exception {
        if (nguoiChoi.clan >= 0) {
            nguoiChoi.moHopThoaiOK("Bạn đã tham gia clan.");
            return;
        }
        int maTin;
        int maTruongClan;
        String tenClan;
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            VXLClan clan = docClan(ketNoi, maClan);
            if (clan == null) {
                nguoiChoi.moHopThoaiOK("Không tìm thấy clan.");
                return;
            }
            if (clan.soThanhVien >= clan.soThanhVienToiDa) {
                nguoiChoi.moHopThoaiOK("Clan đã đủ thành viên.");
                return;
            }
            try (PreparedStatement cu = ketNoi.prepareStatement(
                    "SELECT id FROM clan_messages WHERE clan_id=? AND player_id=? AND message_type=2 AND resolved=0 LIMIT 1")) {
                cu.setInt(1, maClan);
                cu.setInt(2, nguoiChoi.ma);
                try (ResultSet duLieu = cu.executeQuery()) {
                    if (duLieu.next()) {
                        nguoiChoi.moHopThoaiOK("Bạn đã gửi yêu cầu gia nhập clan này.");
                        return;
                    }
                }
            }
            try (PreparedStatement lenh = ketNoi.prepareStatement(
                    "INSERT INTO clan_messages(clan_id,message_type,player_id) VALUES(?,2,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                lenh.setInt(1, maClan);
                lenh.setInt(2, nguoiChoi.ma);
                lenh.executeUpdate();
                try (ResultSet khoa = lenh.getGeneratedKeys()) {
                    if (!khoa.next()) {
                        throw new SQLException("Không lấy được mã yêu cầu clan.");
                    }
                    maTin = khoa.getInt(1);
                }
            }
            maTruongClan = clan.maTruongClan;
            tenClan = clan.ten;
        }
        nguoiChoi.moHopThoaiOK("Đã gửi yêu cầu gia nhập clan " + tenClan + ".");
        VXLNguoiChoi truongClan = VXLNguoiChoi.layNguoiChoiTheoMa(maTruongClan);
        if (truongClan != null) {
            VXLTinClan tinClan = new VXLTinClan();
            tinClan.ma = maTin;
            tinClan.loai = 2;
            tinClan.maNguoiChoi = nguoiChoi.ma;
            tinClan.tenNguoiChoi = nguoiChoi.ten;
            tinClan.vaiTro = 2;
            tinClan.thoiGian = thoiGianHienTai();
            guiMotTinClan(truongClan, tinClan);
        }
    }

    private static void moiVaoClan(VXLNguoiChoi nguoiChoi, int maNguoiDuocMoi) throws Exception {
        if (maNguoiDuocMoi < 0) {
            nguoiChoi.moHopThoaiOK("Không xác định được người cần mời.");
            return;
        }
        VXLNguoiChoi nguoiDuocMoi = VXLNguoiChoi.layNguoiChoiTheoMa(maNguoiDuocMoi);
        if (nguoiDuocMoi == null) {
            nguoiChoi.moHopThoaiOK("Người chơi hiện không trực tuyến.");
            return;
        }
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            VXLClan clan = docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma);
            if (clan == null || docVaiTro(ketNoi, clan.ma, nguoiChoi.ma) > 1) {
                nguoiChoi.moHopThoaiOK("Bạn không có quyền mời thành viên.");
                return;
            }
            if (docClanTheoNguoiChoi(ketNoi, maNguoiDuocMoi) != null) {
                nguoiChoi.moHopThoaiOK("Người chơi đã tham gia clan.");
                return;
            }
            int maMoi = ThreadLocalRandom.current().nextInt(100000, Integer.MAX_VALUE);
            try (PreparedStatement lenh = ketNoi.prepareStatement(
                    "INSERT INTO clan_invites(clan_id,player_id,invite_code,inviter_player_id) VALUES(?,?,?,?)")) {
                lenh.setInt(1, clan.ma);
                lenh.setInt(2, maNguoiDuocMoi);
                lenh.setInt(3, maMoi);
                lenh.setInt(4, nguoiChoi.ma);
                lenh.executeUpdate();
            }
            VXLTinNhan tin = new VXLTinNhan(-111);
            DataOutputStream ghi = tin.boGhi();
            ghi.writeUTF(nguoiChoi.ten + " mời bạn gia nhập clan " + clan.ten + ". Đồng ý?");
            ghi.writeInt(clan.ma);
            ghi.writeInt(maMoi);
            ghi.flush();
            nguoiDuocMoi.dichVu.guiTin(tin);
        }
    }

    private static void traLoiLoiMoi(VXLNguoiChoi nguoiChoi, int maClan, int maMoi, boolean dongY) throws Exception {
        try (Connection ketNoi = VXLCoSoDuLieu.getConnection()) {
            ketNoi.setAutoCommit(false);
            try {
                try (PreparedStatement lenh = ketNoi.prepareStatement(
                        "SELECT id FROM clan_invites WHERE clan_id=? AND player_id=? AND invite_code=? AND resolved=0 FOR UPDATE")) {
                    lenh.setInt(1, maClan);
                    lenh.setInt(2, nguoiChoi.ma);
                    lenh.setInt(3, maMoi);
                    try (ResultSet duLieu = lenh.executeQuery()) {
                        if (!duLieu.next()) {
                            throw new XuLyClanException("Lời mời không còn hiệu lực.");
                        }
                    }
                }
                if (dongY) {
                    if (docClanTheoNguoiChoi(ketNoi, nguoiChoi.ma) != null) {
                        throw new XuLyClanException("Bạn đã tham gia clan.");
                    }
                    VXLClan clan = docClan(ketNoi, maClan);
                    if (clan == null || demThanhVien(ketNoi, maClan) >= clan.soThanhVienToiDa) {
                        throw new XuLyClanException("Clan không còn chỗ trống.");
                    }
                    try (PreparedStatement lenh = ketNoi.prepareStatement(
                            "INSERT INTO clan_members(clan_id,player_id,member_role) VALUES(?,?,2)")) {
                        lenh.setInt(1, maClan);
                        lenh.setInt(2, nguoiChoi.ma);
                        lenh.executeUpdate();
                    }
                }
                try (PreparedStatement lenh = ketNoi.prepareStatement(
                        "UPDATE clan_invites SET resolved=1 WHERE clan_id=? AND player_id=? AND invite_code=?")) {
                    lenh.setInt(1, maClan);
                    lenh.setInt(2, nguoiChoi.ma);
                    lenh.setInt(3, maMoi);
                    lenh.executeUpdate();
                }
                ketNoi.commit();
                if (dongY) {
                    taiChoNguoiChoi(nguoiChoi);
                    guiThongTinClan(nguoiChoi);
                    lamMoiClanOnline(maClan);
                }
            } catch (Exception ex) {
                ketNoi.rollback();
                throw ex;
            } finally {
                ketNoi.setAutoCommit(true);
            }
        } catch (XuLyClanException ex) {
            nguoiChoi.moHopThoaiOK(ex.getMessage());
        }
    }

    private static List<VXLClan> docDanhSachClan(Connection ketNoi, String tuKhoa, boolean top) throws SQLException {
        String sql = "SELECT c.id,c.name,c.icon_id,COALESCE(c.slogan,'') slogan,c.leader_player_id," 
                + "COALESCE(lp.name,'') leader_name,c.level,c.exp,c.clan_gold,c.max_members," 
                + "UNIX_TIMESTAMP(c.created_at) created_at," 
                + "(SELECT COUNT(*) FROM clan_members cm WHERE cm.clan_id=c.id) member_count," 
                + "(SELECT COALESCE(SUM(p.cup),0) FROM clan_members cm JOIN players p ON p.id=cm.player_id WHERE cm.clan_id=c.id) total_cup "
                + "FROM clans c LEFT JOIN players lp ON lp.id=c.leader_player_id "
                + (top ? "" : "WHERE LOWER(c.name) LIKE ? ESCAPE '!' ")
                + "ORDER BY total_cup DESC,c.level DESC,c.exp DESC,c.id ASC LIMIT ?";
        List<VXLClan> ketQua = new ArrayList<>();
        try (PreparedStatement lenh = ketNoi.prepareStatement(sql)) {
            int viTri = 1;
            if (!top) {
                lenh.setString(viTri++, "%" + thoatLike(tuKhoa.toLowerCase()) + "%");
            }
            lenh.setInt(viTri, SO_CLAN_TOI_DA);
            try (ResultSet duLieu = lenh.executeQuery()) {
                while (duLieu.next()) {
                    ketQua.add(taoClan(duLieu));
                }
            }
        }
        return ketQua;
    }

    private static VXLClan docClanTheoNguoiChoi(Connection ketNoi, int maNguoiChoi) throws SQLException {
        String sql = "SELECT c.id,c.name,c.icon_id,COALESCE(c.slogan,'') slogan,c.leader_player_id," 
                + "COALESCE(lp.name,'') leader_name,c.level,c.exp,c.clan_gold,c.max_members," 
                + "UNIX_TIMESTAMP(c.created_at) created_at," 
                + "(SELECT COUNT(*) FROM clan_members x WHERE x.clan_id=c.id) member_count," 
                + "(SELECT COALESCE(SUM(p.cup),0) FROM clan_members x JOIN players p ON p.id=x.player_id WHERE x.clan_id=c.id) total_cup "
                + "FROM clan_members cm JOIN clans c ON c.id=cm.clan_id "
                + "LEFT JOIN players lp ON lp.id=c.leader_player_id WHERE cm.player_id=? LIMIT 1";
        try (PreparedStatement lenh = ketNoi.prepareStatement(sql)) {
            lenh.setInt(1, maNguoiChoi);
            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() ? taoClan(duLieu) : null;
            }
        }
    }

    private static VXLClan docClan(Connection ketNoi, int maClan) throws SQLException {
        String sql = "SELECT c.id,c.name,c.icon_id,COALESCE(c.slogan,'') slogan,c.leader_player_id," 
                + "COALESCE(lp.name,'') leader_name,c.level,c.exp,c.clan_gold,c.max_members," 
                + "UNIX_TIMESTAMP(c.created_at) created_at," 
                + "(SELECT COUNT(*) FROM clan_members x WHERE x.clan_id=c.id) member_count," 
                + "(SELECT COALESCE(SUM(p.cup),0) FROM clan_members x JOIN players p ON p.id=x.player_id WHERE x.clan_id=c.id) total_cup "
                + "FROM clans c LEFT JOIN players lp ON lp.id=c.leader_player_id WHERE c.id=? LIMIT 1";
        try (PreparedStatement lenh = ketNoi.prepareStatement(sql)) {
            lenh.setInt(1, maClan);
            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() ? taoClan(duLieu) : null;
            }
        }
    }

    private static VXLClan taoClan(ResultSet duLieu) throws SQLException {
        VXLClan clan = new VXLClan();
        clan.ma = duLieu.getInt("id");
        clan.ten = chuoi(duLieu.getString("name"));
        clan.bieuTuong = chuanHoaBieuTuong(duLieu.getShort("icon_id"));
        clan.khauHieu = chuoi(duLieu.getString("slogan"));
        clan.maTruongClan = duLieu.getInt("leader_player_id");
        clan.tenTruongClan = chuoi(duLieu.getString("leader_name"));
        clan.cap = Math.max(1, duLieu.getInt("level"));
        clan.kinhNghiem = Math.max(0, duLieu.getInt("exp"));
        clan.vangClan = Math.max(0, duLieu.getInt("clan_gold"));
        clan.soThanhVien = Math.max(0, duLieu.getInt("member_count"));
        clan.soThanhVienToiDa = Math.max(1, duLieu.getInt("max_members"));
        clan.tongCup = Math.max(0L, duLieu.getLong("total_cup"));
        clan.ngayTao = Math.max(0, duLieu.getInt("created_at"));
        return clan;
    }

    private static List<VXLThanhVienClan> docThanhVien(Connection ketNoi, int maClan) throws SQLException {
        String sql = "SELECT cm.player_id,p.name,p.cup,p.equipped_json,cm.member_role,cm.donated," 
                + "cm.received_donate,cm.clan_point,UNIX_TIMESTAMP(cm.joined_at) joined_at "
                + "FROM clan_members cm JOIN players p ON p.id=cm.player_id "
                + "WHERE cm.clan_id=? ORDER BY cm.member_role ASC,cm.clan_point DESC,cm.joined_at ASC";
        List<VXLThanhVienClan> ketQua = new ArrayList<>();
        try (PreparedStatement lenh = ketNoi.prepareStatement(sql)) {
            lenh.setInt(1, maClan);
            try (ResultSet duLieu = lenh.executeQuery()) {
                while (duLieu.next()) {
                    VXLThanhVienClan thanhVien = new VXLThanhVienClan();
                    thanhVien.maNguoiChoi = duLieu.getInt("player_id");
                    thanhVien.ten = chuoi(duLieu.getString("name"));
                    VXLNguoiChoi online = VXLNguoiChoi.layNguoiChoiTheoMa(thanhVien.maNguoiChoi);
                    VXLHinhDangNguoiChoi hinhDang = online == null
                            ? VXLHinhDangNguoiChoi.tuJson(duLieu.getString("equipped_json"))
                            : VXLHinhDangNguoiChoi.tuNguoiChoi(online);
                    thanhVien.dau = hinhDang.dau();
                    thanhVien.chan = hinhDang.chan();
                    thanhVien.than = hinhDang.than();
                    thanhVien.vaiTro = duLieu.getByte("member_role");
                    thanhVien.cup = online == null ? duLieu.getInt("cup") : online.cup;
                    thanhVien.daDongGop = duLieu.getInt("donated");
                    thanhVien.daNhan = duLieu.getInt("received_donate");
                    thanhVien.diemClan = duLieu.getInt("clan_point");
                    thanhVien.ngayGiaNhap = duLieu.getInt("joined_at");
                    ketQua.add(thanhVien);
                }
            }
        }
        return ketQua;
    }

    private static List<VXLTinClan> docTinClan(Connection ketNoi, int maClan, byte vaiTro) throws SQLException {
        String sql = "SELECT m.id,m.message_type,m.player_id,p.name,COALESCE(cm.member_role,2) member_role," 
                + "UNIX_TIMESTAMP(m.created_at) created_at,m.message_text,m.message_color "
                + "FROM clan_messages m JOIN players p ON p.id=m.player_id "
                + "LEFT JOIN clan_members cm ON cm.player_id=m.player_id AND cm.clan_id=m.clan_id "
                + "WHERE m.clan_id=? AND m.resolved=0 AND (m.message_type=0 OR (?=0 AND m.message_type=2)) "
                + "ORDER BY m.id DESC LIMIT ?";
        List<VXLTinClan> ketQua = new ArrayList<>();
        try (PreparedStatement lenh = ketNoi.prepareStatement(sql)) {
            lenh.setInt(1, maClan);
            lenh.setByte(2, vaiTro);
            lenh.setInt(3, SO_TIN_TOI_DA);
            try (ResultSet duLieu = lenh.executeQuery()) {
                while (duLieu.next()) {
                    VXLTinClan tin = new VXLTinClan();
                    tin.ma = duLieu.getInt("id");
                    tin.loai = duLieu.getByte("message_type");
                    tin.maNguoiChoi = duLieu.getInt("player_id");
                    tin.tenNguoiChoi = chuoi(duLieu.getString("name"));
                    tin.vaiTro = duLieu.getByte("member_role");
                    tin.thoiGian = duLieu.getInt("created_at");
                    tin.noiDung = chuoi(duLieu.getString("message_text"));
                    tin.mau = duLieu.getByte("message_color");
                    ketQua.add(tin);
                }
            }
        }
        return ketQua;
    }

    private static byte docVaiTro(Connection ketNoi, int maClan, int maNguoiChoi) throws SQLException {
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "SELECT member_role FROM clan_members WHERE clan_id=? AND player_id=? LIMIT 1")) {
            lenh.setInt(1, maClan);
            lenh.setInt(2, maNguoiChoi);
            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() ? duLieu.getByte(1) : (byte)-1;
            }
        }
    }

    private static int demThanhVien(Connection ketNoi, int maClan) throws SQLException {
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "SELECT COUNT(*) FROM clan_members WHERE clan_id=?")) {
            lenh.setInt(1, maClan);
            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() ? duLieu.getInt(1) : 0;
            }
        }
    }

    private static void xoaDuLieuClan(Connection ketNoi, int maClan) throws SQLException {
        String[] cacLenh = new String[]{
            "DELETE FROM clan_invites WHERE clan_id=?",
            "DELETE FROM clan_messages WHERE clan_id=?",
            "DELETE FROM clan_items WHERE clan_id=?",
            "DELETE FROM clan_members WHERE clan_id=?",
            "DELETE FROM clans WHERE id=?"
        };
        for (String sql : cacLenh) {
            try (PreparedStatement lenh = ketNoi.prepareStatement(sql)) {
                lenh.setInt(1, maClan);
                lenh.executeUpdate();
            }
        }
    }

    private static void ghiThanhVien(DataOutputStream ghi, VXLThanhVienClan thanhVien) throws IOException {
        ghi.writeInt(thanhVien.maNguoiChoi);
        ghi.writeShort(thanhVien.dau);
        ghi.writeShort(thanhVien.chan);
        ghi.writeShort(thanhVien.than);
        ghi.writeUTF(thanhVien.ten);
        ghi.writeByte(thanhVien.vaiTro);
        ghi.writeUTF(thanhVien.cup + " Cúp");
        ghi.writeInt(thanhVien.daDongGop);
        ghi.writeInt(thanhVien.daNhan);
        ghi.writeInt(thanhVien.diemClan);
        ghi.writeInt(thanhVien.ngayGiaNhap);
    }

    private static void ghiTinClan(DataOutputStream ghi, VXLTinClan tin) throws IOException {
        ghi.writeByte(tin.loai);
        ghi.writeInt(tin.ma);
        ghi.writeInt(tin.maNguoiChoi);
        ghi.writeUTF(tin.tenNguoiChoi);
        ghi.writeByte(tin.vaiTro);
        ghi.writeInt(Math.max(0, tin.thoiGian - 1_000_000_000));
        if (tin.loai == 0) {
            ghi.writeUTF(tin.noiDung);
            ghi.writeByte(tin.mau);
        }
    }

    private static void guiTinDenClan(int maClan, VXLTinClan tinClan) {
        for (VXLNguoiChoi online : VXLNguoiChoi.players_id.values()) {
            if (online != null && online.clan == maClan) {
                guiMotTinClan(online, tinClan);
            }
        }
    }

    private static void guiMotTinClan(VXLNguoiChoi nguoiChoi, VXLTinClan tinClan) {
        try {
            VXLTinNhan tin = new VXLTinNhan(-106);
            ghiTinClan(tin.boGhi(), tinClan);
            tin.boGhi().flush();
            nguoiChoi.dichVu.guiTin(tin);
        } catch (IOException ignored) {
        }
    }

    private static void lamMoiClanOnline(int maClan) {
        List<VXLNguoiChoi> online = new ArrayList<>(VXLNguoiChoi.players_id.values());
        for (VXLNguoiChoi nguoiChoi : online) {
            if (nguoiChoi == null || nguoiChoi.clan != maClan) {
                continue;
            }
            try {
                taiChoNguoiChoi(nguoiChoi);
                guiThongTinClan(nguoiChoi);
                capNhatNguoiChoiTaiSanh(nguoiChoi);
            } catch (Exception ex) {
                VXLQuanLyMayChu.log("Khong the lam moi clan cho " + nguoiChoi.ten + ": " + ex.getMessage());
            }
        }
    }

    private static boolean bieuTuongHopLe(short bieuTuong) {
        return chiSoBieuTuong(bieuTuong) >= 0;
    }

    private static boolean bieuTuongMoKhoa(short bieuTuong, int capClan) {
        int chiSo = chiSoBieuTuong(bieuTuong);
        return chiSo >= 0 && chiSo < soBieuTuongMoKhoa(capClan);
    }

    private static int soBieuTuongMoKhoa(int capClan) {
        return BIEU_TUONG_CLAN.length;
    }

    private static VXLBieuTuongClan[] taoDanhSachBieuTuongClan() {
        List<VXLBieuTuongClan> danhSach = new ArrayList<>();
        for (int ma = CLAN_LOGO_FIRST_IMAGE; ma <= CLAN_LOGO_LAST_IMAGE; ma++) {
            danhSach.add(new VXLBieuTuongClan((short)ma, "Bieu tuong clan " + (ma - CLAN_LOGO_FIRST_IMAGE + 1)));
        }
        danhSach.add(new VXLBieuTuongClan(CLAN_VIETNAM_IMAGE, "Co Viet Nam"));
        return danhSach.toArray(VXLBieuTuongClan[]::new);
    }

    private static short chuanHoaBieuTuong(short bieuTuong) {
        int chiSo = chiSoBieuTuong(bieuTuong);
        return chiSo < 0 ? bieuTuong : BIEU_TUONG_CLAN[chiSo].ma();
    }

    private static int chiSoBieuTuong(short bieuTuong) {
        if (bieuTuong <= -1 && bieuTuong >= -BIEU_TUONG_CLAN.length) {
            return -bieuTuong - 1;
        }
        for (int i = 0; i < BIEU_TUONG_CLAN.length; i++) {
            if (BIEU_TUONG_CLAN[i].ma() == bieuTuong) {
                return i;
            }
        }
        return -1;
    }

    private static void capNhatNguoiChoiTaiSanh(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi != null && nguoiChoi.zone != null) {
            nguoiChoi.zone.guiCapNhatNguoiChoi(nguoiChoi);
        }
    }

    private static int gioiHanByte(int giaTri) {
        return Math.max(0, Math.min(255, giaTri));
    }

    private static byte tinhPhanTramCap(int kinhNghiem) {
        return (byte)Math.max(0, Math.min(100, kinhNghiem % 1000 / 10));
    }

    private static int thoiGianHienTai() {
        return (int)(System.currentTimeMillis() / 1000L);
    }

    private static String hienThiSucManh(long tongCup) {
        return tongCup + " Cúp";
    }

    private static String khauHieuChoTop(String khauHieu) {
        String giaTri = chuoi(khauHieu);
        if (giaTri.length() >= 80) {
            return giaTri;
        }
        return giaTri + " ".repeat(80 - giaTri.length());
    }

    private static String thoatLike(String giaTri) {
        return giaTri.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }

    private static String chuoi(String giaTri) {
        return giaTri == null ? "" : giaTri;
    }

    private static final class XuLyClanException extends Exception {
        XuLyClanException(String noiDung) {
            super(noiDung);
        }
    }

    private record VXLVatPhamClan(short ma, byte nhom, byte capYeuCau, int vang, int ngoc,
            byte maThuocTinh, short giaTri) {
    }

    private record VXLVatPhamClanDaMua(VXLVatPhamClan vatPham, boolean dangDung) {
    }

    private record VXLBieuTuongClan(short ma, String ten) {
    }
}
