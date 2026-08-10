package com.vxl.vatpham;

import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.phong.VXLChoDau;
import com.vxl.phong.VXLQuanLyPhong;
import com.vxl.tienich.VXLTienIch;
import java.io.DataOutputStream;
import java.io.IOException;

public final class VXLDichVuNangCapVatPham {
    private VXLDichVuNangCapVatPham() {
    }

    public static void xuLy(VXLNguoiChoi nguoiChoi, VXLTinNhan ms) throws IOException {
        byte loai = ms.boDoc().readByte();
        if (loai == 0) {
            guiBangCongDiem(nguoiChoi);
            return;
        }
        if (loai == 1) {
            congDiemNhanVat(nguoiChoi, ms.boDoc().readByte());
            return;
        }
        if (loai < 2 || loai > 5) {
            nguoiChoi.moHopThoaiOK("Loại nâng cấp không hợp lệ.");
            return;
        }
        VXLChoDau banCho = VXLQuanLyPhong.layBanCho(nguoiChoi);
        if (banCho != null && banCho.started) {
            nguoiChoi.moHopThoaiOK("Không thể nâng cấp vật phẩm khi đang trong trận.");
            return;
        }
        int chiSoVatPham = Byte.toUnsignedInt(ms.boDoc().readByte());
        boolean trenNguoi = loai == 4 || loai == 5;
        boolean nangCapMau = loai == 3 || loai == 5;
        VXLVatPham vatPham = layVatPham(nguoiChoi, trenNguoi, chiSoVatPham);
        if (vatPham == null || vatPham.mau == null || !vatPham.isTypeBody()) {
            nguoiChoi.moHopThoaiOK("Không tìm thấy trang bị cần nâng cấp.");
            return;
        }
        boolean thanhCong = nangCapMau ? nangCapMau(nguoiChoi, vatPham) : cuongHoa(nguoiChoi, vatPham);
        if (!thanhCong) {
            return;
        }
        if (trenNguoi) {
            nguoiChoi.datTrangBiChoNhanVat(vatPham);
            nguoiChoi.dichVu.guiDoTrenNguoi();
            nguoiChoi.dichVu.doiTrangBi();
        } else {
            nguoiChoi.dichVu.guiTuiDo();
        }
        nguoiChoi.dichVu.capNhat();
        nguoiChoi.flushCache();
    }

    private static void guiBangCongDiem(VXLNguoiChoi nguoiChoi) throws IOException {
        byte[] tiSo = new byte[]{10, 1, 1, 1, 1, 1};
        VXLTinNhan msg = new VXLTinNhan(-46);
        DataOutputStream ds = msg.boGhi();
        ds.writeByte(0);
        ds.writeShort(nguoiChoi.point);
        for (byte giaTri : tiSo) {
            ds.writeByte(giaTri);
        }
        for (short diem : nguoiChoi.pointAdd) {
            ds.writeShort(diem);
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(msg);
    }

    private static void congDiemNhanVat(VXLNguoiChoi nguoiChoi, byte chiSo) throws IOException {
        if (nguoiChoi.point <= 0) {
            nguoiChoi.moHopThoaiOK("Không đủ điểm cộng.");
            return;
        }
        if (chiSo < 0 || chiSo > 5) {
            nguoiChoi.moHopThoaiOK("Chỉ số nâng cấp không hợp lệ.");
            return;
        }
        if (chiSo == 0) {
            nguoiChoi.pointAdd[0] = (short)(nguoiChoi.pointAdd[0] + 10);
        } else {
            nguoiChoi.pointAdd[chiSo] = (short)(nguoiChoi.pointAdd[chiSo] + 1);
        }
        nguoiChoi.point = (short)(nguoiChoi.point - 1);
        nguoiChoi.flushCache();
        guiBangCongDiem(nguoiChoi);
        nguoiChoi.dichVu.capNhat();
    }

    private static VXLVatPham layVatPham(VXLNguoiChoi nguoiChoi, boolean trenNguoi, int chiSo) {
        if (trenNguoi) {
            return chiSo >= 0 && chiSo < nguoiChoi.itemBody.length ? nguoiChoi.itemBody[chiSo] : null;
        }
        return chiSo >= 0 && chiSo < nguoiChoi.itemBag.length ? nguoiChoi.itemBag[chiSo] : null;
    }

    private static boolean cuongHoa(VXLNguoiChoi nguoiChoi, VXLVatPham vatPham) {
        int capHienTai = Math.max(0, vatPham.getParamById(17));
        if (capHienTai >= 15) {
            nguoiChoi.moHopThoaiOK("Trang bị đã đạt cường hóa tối đa +15.");
            return false;
        }
        long chiPhiTinhToan = (long)(capHienTai + 1) * (capHienTai + 1) * Math.max(1, vatPham.mau.cap) * 1000L;
        int chiPhi = (int)Math.min(Integer.MAX_VALUE, Math.max(1000L, chiPhiTinhToan));
        if (nguoiChoi.vang < chiPhi) {
            nguoiChoi.moHopThoaiOK("Không đủ vàng. Cần " + VXLTienIch.dinhDangTien(chiPhi) + " vàng.");
            return false;
        }
        nguoiChoi.vang -= chiPhi;
        vatPham.datThamSoTheoMa(17, capHienTai + 1);
        nguoiChoi.moHopThoaiOK("Cường hóa thành công " + vatPham.mau.ten + " lên +" + (capHienTai + 1) + ".");
        return true;
    }

    private static boolean nangCapMau(VXLNguoiChoi nguoiChoi, VXLVatPham vatPham) {
        VXLMauVatPham mauTiepTheo = null;
        for (VXLMauVatPham mau : VXLQuanLyMayChu.itemTemplates.values()) {
            if (mau.loai != vatPham.mau.loai || mau.part != vatPham.mau.part || mau.gioiTinh != vatPham.mau.gioiTinh
                    || mau.cap != vatPham.mau.cap + 1) {
                continue;
            }
            if (mauTiepTheo == null || mau.ma < mauTiepTheo.ma) {
                mauTiepTheo = mau;
            }
        }
        if (mauTiepTheo == null) {
            nguoiChoi.moHopThoaiOK("Trang bị đã đạt cấp tối đa.");
            return false;
        }
        int chiPhi = Math.max(5000, mauTiepTheo.buyGold > 0 ? mauTiepTheo.buyGold / 2 : mauTiepTheo.cap * 10000);
        if (nguoiChoi.vang < chiPhi) {
            nguoiChoi.moHopThoaiOK("Không đủ vàng. Cần " + VXLTienIch.dinhDangTien(chiPhi) + " vàng.");
            return false;
        }
        nguoiChoi.vang -= chiPhi;
        String tenCu = vatPham.mau.ten;
        vatPham.thayMau(mauTiepTheo);
        nguoiChoi.moHopThoaiOK("Nâng cấp thành công " + tenCu + " thành " + mauTiepTheo.ten + ".");
        return true;
    }
}