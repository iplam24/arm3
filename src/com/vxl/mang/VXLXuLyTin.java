package com.vxl.mang;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.cuahang.VXLCuaHang;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.phong.VXLQuanLyPhong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLXuLyTin
implements IVXLXuLyTin {
    private final VXLPhien khach;

    public VXLXuLyTin(VXLPhien khach) {
        this.khach = khach;
    }

    @Override
    public void khiCoTin(VXLTinNhan mss) {
        if (mss != null) {
            try {
                byte lenh = mss.layLenh();
                if (!this.laLenhTruocDangNhap(lenh) && !this.khach.daDangNhap()) {
                    throw new IllegalStateException("Lenh " + lenh + " yêu cầu đăng nhập.");
                }
                if (this.lenhCanNhanVat(lenh) && (this.khach.user == null || this.khach.user.nguoiChoi == null)) {
                    VXLQuanLyMayChu.log("Bo qua lenh " + lenh + " khi nhan vat chua san sang: " + this.khach.moTa());
                    return;
                }
                switch (lenh) {
                    case 1:
                        this.khach.dangNhap(mss);
                        break;
                    case 2:
                    case -4:
                        this.khach.dangXuat();
                        break;
                    case -58:
                        this.khach.dangNhap2(mss);
                        break;
                    case -98:
                        this.khach.user.nguoiChoi.banDoRPG(mss);
                        break;
                    case -28:
                        VXLQuanLyPhong.guiPhongTisEmpty(this.khach.user.nguoiChoi);
                        break;
                    case 6:
                        VXLQuanLyPhong.yeuCauDanhSachPhong(this.khach.user.nguoiChoi);
                        break;
                    case 7:
                        VXLQuanLyPhong.yeuCauDanhSachBan(this.khach.user.nguoiChoi, mss);
                        break;
                    case 8:
                        VXLQuanLyPhong.vaoBan(this.khach.user.nguoiChoi, mss);
                        break;
                    case 15:
                        if (!this.khach.user.nguoiChoi.roiLuyenTapNeuCan()) {
                            VXLQuanLyPhong.roiBanCho(this.khach.user.nguoiChoi);
                        }
                        break;
                    case 16:
                        VXLQuanLyPhong.sanSang(this.khach.user.nguoiChoi, mss);
                        break;
                    case 20:
                        VXLQuanLyPhong.batDau(this.khach.user.nguoiChoi);
                        break;
                    case 23:
                        VXLQuanLyPhong.dauKiemTraVaCham(this.khach.user.nguoiChoi, mss);
                        break;
                    case 49:
                        VXLQuanLyPhong.boLuot(this.khach.user.nguoiChoi);
                        break;
                    case 53:
                        VXLQuanLyPhong.dauCapNhatXY(this.khach.user.nguoiChoi, mss);
                        break;
                    case 69:
                        if (this.khach.user.nguoiChoi.inTraining) {
                            this.khach.user.nguoiChoi.xuLyDoiSungLuyenTap(mss);
                        } else {
                            VXLQuanLyPhong.dauDoiSung(this.khach.user.nguoiChoi, mss);
                        }
                        break;
                    case 75:
                        VXLQuanLyPhong.chonBanDo(this.khach.user.nguoiChoi, mss);
                        break;
                    case 5:
                        this.khach.user.nguoiChoi.chatTo(mss);
                        break;
                    case 58:
                        this.khach.datNhaCungCap(mss);
                        break;
                    case 114:
                        this.khach.datLoaiKhach(mss);
                        break;
                    case -102:
                        this.khach.guiTin(new VXLTinNhan(-102));
                        break;
                    case -60:
                        this.khach.taiXuong();
                        break;
                    case -41:
                        this.khach.user.dichVu.yeuCauIcon(mss);
                        break;
                    case -38:
                        this.khach.user.dichVu.guiBanDo();
                        break;
                    case -37:
                        this.khach.taiDuLieuXong();
                        break;
                    case -32:
                        this.khach.user.dichVu.guiVatPham();
                        break;
                    case -71:
                        this.khach.dangKy(mss);
                        break;
                    case -99:
                        this.khach.user.taoNhanVat(mss);
                        break;
                    case -31:
                        this.khach.user.dichVu.guiDuLieu();
                        break;
                    case 103:
                        this.khach.user.nguoiChoi.xemCuaHang(VXLCuaHang.SHOP_EQUIP);
                        break;
                    case -43:
                        this.khach.user.nguoiChoi.requestTab(mss);
                        break;
                    case 72:
                        this.khach.user.nguoiChoi.yeuCauMuaVatPham(mss);
                        break;
                    case 26:
                        if (this.khach.user.nguoiChoi.inTraining) {
                            this.khach.user.nguoiChoi.xuLyVatPhamLuyenTap(mss);
                        } else if (!VXLQuanLyPhong.dungVatPhamTrongTran(this.khach.user.nguoiChoi, mss)) {
                            this.khach.user.nguoiChoi.dungVatPham(mss);
                        }
                        break;
                    case -44:
                        this.khach.user.nguoiChoi.chuyenVatPham(mss);
                        break;
                    case -25:
                        this.khach.user.nguoiChoi.thucHien(mss);
                        break;
                    case -48:
                        this.khach.user.nguoiChoi.yeuCauBanVatPham(mss);
                        break;
                    case -33:
                        this.khach.user.nguoiChoi.xemCuaHang(VXLCuaHang.SHOP_ITEM);
                        break;
                    case -46:
                        this.khach.user.nguoiChoi.nangCapNhanVat(mss);
                        break;
                    case 88:
                        this.khach.user.thanhTich(mss);
                        break;
                    case -126:
                        this.khach.user.nguoiChoi.viewPlayerInfo(mss);
                        break;
                    case 126:
                        short materialId = mss.boDoc().readShort();
                        this.khach.user.dichVu.yeuCauNguyenLieu(materialId);
                        break;
                    case -40:
                        this.khach.user.dichVu.yeuCauDanLuyenTap(mss);
                        break;
                    case 83:
                        this.khach.user.nguoiChoi.vaoLuyenTap();
                        System.out.println("Client gửi lệnh vào luyện tập");
                        break;
                    case 21:
                        if (this.khach.user.nguoiChoi.inTraining) {
                            this.khach.user.nguoiChoi.handleTrainingMove(mss);
                        } else {
                            VXLQuanLyPhong.dauDiChuyen(this.khach.user.nguoiChoi, mss);
                        }
                        break;
                    case 84:
                        this.khach.user.nguoiChoi.xuLyBanLuyenTap(mss);
                        break;
                    case 22:
                        if (this.khach.user.nguoiChoi.inTraining) {
                            this.khach.user.nguoiChoi.xuLyBanLuyenTap(mss);
                        } else {
                            VXLQuanLyPhong.dauBan(this.khach.user.nguoiChoi, mss);
                        }
                        break;
                    case 79:
                        if (this.khach.user.nguoiChoi.inTraining) {
                            this.khach.user.nguoiChoi.xuLyVaChamLuyenTap(mss);
                        } else {
                            VXLQuanLyPhong.dauKiemTraVaCham(this.khach.user.nguoiChoi, mss);
                        }
                        break;
                    case -92:
                        this.khach.user.nguoiChoi.handleTrainingHoleRequest(mss);
                        break;
                    case -91:
                        this.khach.user.nguoiChoi.xuLyFocusSkill(mss);
                        break;
                    case -67:
                        VXLQuanLyMayChu.log("[FIGHT] client-ready " + this.khach.moTa()
                                + " training=" + this.khach.user.nguoiChoi.inTraining);
                        this.khach.user.nguoiChoi.handleTrainingClientReady();
                        break;
                    default:
                        if (mss.layLenh() != -98) {
                            System.out.println("CMD: " + mss.layLenh());
                        }
                        break;
                }
            }
            catch (Exception ex) {
                Logger.getLogger(VXLXuLyTin.class.getName()).log(Level.WARNING, "Gói tin không hợp lệ, lệnh=" + mss.layLenh() + " từ " + this.khach.moTa(), ex);
                this.khach.dongTin();
            }
        }
    }

    private boolean lenhCanNhanVat(byte lenh) {
        switch (lenh) {
            case -98:
            case -28:
            case 5:
            case 6:
            case 7:
            case 8:
            case 15:
            case 16:
            case 20:
            case 21:
            case 22:
            case 23:
            case 26:
            case 49:
            case 53:
            case 69:
            case 72:
            case 75:
            case 79:
            case 83:
            case 84:
            case 103:
            case -126:
            case -92:
            case -91:
            case -48:
            case -46:
            case -44:
            case -43:
            case -33:
            case -25:
            case -67:
                return true;
            default:
                return false;
        }
    }

    private boolean laLenhTruocDangNhap(byte lenh) {
        return lenh == 1 || lenh == 2 || lenh == -4 || lenh == -58 || lenh == -71
                || lenh == 58 || lenh == 114 || lenh == -102 || lenh == -60;
    }

    @Override
    public void khiKetNoiLoi() {
        System.out.println("Client " + this.khach.ma + ": Kết nối thất bại!");
    }

    @Override
    public void khiMatKetNoi() {
        System.out.println("Client " + this.khach.ma + ": Mất kết nối!");
    }

    @Override
    public void khiKetNoiThanhCong() {
        System.out.println("Client " + this.khach.ma + ": Kết nối thành công!");
    }
}
