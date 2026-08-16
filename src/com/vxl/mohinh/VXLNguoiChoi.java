package com.vxl.mohinh;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.quantri.VXLBoLenhQuanTri;
import com.vxl.luyentap.VXLQuanLyLuyenTap;
import com.vxl.vatpham.VXLDichVuNangCapVatPham;
import com.vxl.vatpham.VXLDichVuNgocTrangBi;
import com.vxl.vatpham.VXLTienTrinhDucLo;
import com.vxl.vatpham.VXLVatPham;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.mang.VXLDichVuGame;
import com.vxl.mang.VXLTinNhan;
import com.vxl.nhapvai.VXLBanDoRPG;
import com.vxl.nhiemvu.VXLNhiemVu;
import com.vxl.nhapvai.VXLKhu;
import com.vxl.phong.VXLQuanLyPhong;
import com.vxl.cuahang.VXLTrang;
import com.vxl.cuahang.VXLCuaHang;
import com.vxl.tienich.VXLTienIch;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLNguoiChoi {

    public static Map<Integer, VXLNguoiChoi> players_id = new ConcurrentHashMap<>();
    public int ma;
    public String ten;
    public int vang;
    public int ngoc;
    public int kinhNghiem;
    public int cup;
    public int cap;
    public boolean quanTri;
    public int clan = -1;
    public short clanIcon = -1;
    public byte clanRole = 2;
    public byte power;
    public byte busyHammer;
    public byte nHammer;
    public byte trainingSuccess;
    public boolean inTraining;
    public int trainingHits;
    public short[] pointAdd;
    public short point;
    public byte zoneId = (byte)-1;
    public VXLKhu zone;
    public short x;
    public short y;
    public short head;
    public short hat;
    public short body;
    public short leg;
    public short wp;
    public short wing;
    public VXLVatPham[] itemBag = new VXLVatPham[20];
    public VXLVatPham[] itemBody = new VXLVatPham[6];
    public int[] itemBalo = new int[0];
    public VXLVatPham[] itemBox = new VXLVatPham[20];

    private static void kiemTraChiSo(int chiSo, int doDai, String tenMang) {
        if (chiSo < 0 || chiSo >= doDai) {
            throw new IllegalArgumentException("Chỉ số " + tenMang + " không hợp lệ: " + chiSo);
        }
    }
    public boolean isReady;
    public byte pointSeat;
    public int chiSo = -1;
    public VXLDichVuGame dichVu;
    public int kill = 1;
    public int chet;
    public int assist;
    public final VXLNhiemVu nhiemVu;
    private final VXLQuanLyLuyenTap luyenTap;
    public byte powerAvenger;
    public byte avenger;
    private VXLCuaHang store;

    public VXLNguoiChoi(VXLDichVuGame dichVu) {
        this.dichVu = dichVu;
        this.kinhNghiem = VXLTienIch.layKinhNghiemCapMot();
        this.cap = VXLTienIch.layCap(this.kinhNghiem);
        this.pointAdd = new short[]{1000, 0, 0, 0, 0, 0};
        this.head = -1;
        this.hat = -1;
        this.body = -1;
        this.leg = -1;
        this.wp = -1;
        this.wing = -1;
        this.nhiemVu = new VXLNhiemVu(this);
        this.luyenTap = new VXLQuanLyLuyenTap(this);
    }

    public void taiTienTrinhGame(JSONObject duLieu) {
        this.nhiemVu.tai(duLieu);
        this.luyenTap.tai(duLieu);
    }

    public int congKinhNghiem(int soKinhNghiem) {
        return this.nhiemVu.congKinhNghiem(soKinhNghiem);
    }

    public boolean kichHoatNhanDoiKinhNghiem() {
        return this.nhiemVu.kichHoatNhanDoiKinhNghiem();
    }

    public void ghiNhanThangPvp() {
        this.nhiemVu.ghiNhanThangPvp();
    }

    public void ghiNhanHaCamTu(int soLuong) {
        this.nhiemVu.ghiNhanHaCamTu(soLuong);
    }

    public void ghiNhanHaBoss(int soLuong) {
        this.nhiemVu.ghiNhanHaBoss(soLuong);
    }

    public void ghiNhanSatThuongPvp(int satThuong) {
        this.nhiemVu.ghiNhanSatThuongPvp(satThuong);
    }

    public String tomTatNhiemVu() {
        return this.nhiemVu.tomTat();
    }

    public boolean daHoanThanhThanhTichPvp() {
        return this.nhiemVu.daHoanThanhPvp();
    }

    public boolean daHoanThanhThanhTichCamTu() {
        return this.nhiemVu.daHoanThanhCamTu();
    }

    public boolean daHoanThanhThanhTichBoss() {
        return this.nhiemVu.daHoanThanhBoss();
    }
    public float layKD() {
        return this.chet <= 0 ? this.kill : (float)this.kill / (float)this.chet;
    }

    public float layKDA() {
        return this.chet <= 0 ? this.kill + this.assist : (float)(this.kill + this.assist) / (float)this.chet;
    }

    public static VXLNguoiChoi layNguoiChoiTheoMa(int ma) {
        return players_id.get(ma);
    }

    public static VXLNguoiChoi layNguoiChoiTheoTen(String ten) {
        if (ten == null || ten.isBlank()) {
            return null;
        }
        for (VXLNguoiChoi nguoiChoi : players_id.values()) {
            if (nguoiChoi != null && nguoiChoi.ten != null
                    && nguoiChoi.ten.equalsIgnoreCase(ten.trim())) {
                return nguoiChoi;
            }
        }
        return null;
    }

    public static void xoa(int ma) {
        players_id.remove(ma);
    }

    public static void guiMayChu(VXLTinNhan ms) {
        for (VXLNguoiChoi pl : players_id.values()) {
            if (pl == null) continue;
            pl.dichVu.guiTin(ms);
        }
    }

    public void nangCapNhanVat(VXLTinNhan ms) throws IOException {
        VXLDichVuNangCapVatPham.xuLy(this, ms);
    }
    public void banDoRPG(VXLTinNhan ms) throws IOException {
        byte b = ms.boDoc().readByte();
        switch (b) {
            case 0: {
                this.luyenTap.dong();
                VXLQuanLyPhong.roiBanCho(this);
                VXLBanDoRPG.roi(this);
                VXLBanDoRPG.vao(this);
                break;
            }
            case 2: {
                this.diChuyen(ms);
                break;
            }
            case 3: {
                this.chat(ms);
                break;
            }
            case 7: {
                this.moKhu();
                break;
            }
            case 8: {
                this.doiKhu(ms);
                break;
            }
            case 9: {
                VXLGiaoDich.xuLy(this, ms);
                break;
            }
            case 11: {
                this.moMenu(ms);
                break;
            }
            default: {
                System.out.println("b: " + b);
                break;
            }
        }
    }

    public void moMenu(VXLTinNhan ms) throws IOException {
        short npcId = ms.boDoc().readShort();
        switch (npcId) {
            case 2: {
                com.vxl.nhapvai.VXLMenuDoiTruong.mo(this);
                break;
            }
            case 3: {
                this.npcDaiUy();
                break;
            }
            default: {
                break;
            }
        }
    }

    public void npcDaiUy() throws IOException {
        VXLQuanLyPhong.guiPhongTisEmpty(this);
    }

    public void doiKhu(VXLTinNhan ms) throws IOException {
        int zone = ms.boDoc().readUnsignedByte();
        if (zone < 0 || zone >= VXLBanDoRPG.zones.size()) {
            this.moHopThoaiOK("Khu vực không hợp lệ.");
            return;
        }
        if (this.zone != null) {
            VXLBanDoRPG.roi(this);
        }
        VXLBanDoRPG.vao(zone, this);
    }

    public int layOTrongTuiDo() {
        if (this.itemBag == null) {
            return 0;
        }
        int number = 0;
        for (VXLVatPham vatPham : this.itemBag) {
            if (vatPham != null) continue;
            ++number;
        }
        return number;
    }

    public int layOTrongBalo() {
        if (this.itemBalo == null) {
            return 0;
        }
        int number = 0;
        for (int chiSo : this.itemBalo) {
            if (chiSo != -1) continue;
            ++number;
        }
        return number;
    }

    public int layOTrongRuong() {
        if (this.itemBox == null) {
            return 0;
        }
        int number = 0;
        for (VXLVatPham vatPham : this.itemBox) {
            if (vatPham != null) continue;
            ++number;
        }
        return number;
    }

    public void thucHien(VXLTinNhan ms) throws IOException {
        byte action = ms.boDoc().readByte();
        int ma = ms.boDoc().readInt();
        if (ma >= 11000) {
            int chiSo = ma - 11000;
            kiemTraChiSo(chiSo, this.itemBag.length, "tui do");
            VXLVatPham vatPham = this.itemBag[chiSo];
            if (vatPham != null && vatPham.mau != null && vatPham.soLuong > 0
                    && !this.vatPhamCoTrongBalo(vatPham)) {
                int vang = 0;
                vang = vatPham.mau.buyGold > 0 ? vatPham.mau.buyGold / 2 : (vatPham.mau.buyGem > 0 ? vatPham.mau.buyGem * 100 : 1);
                vang *= vatPham.soLuong;
                this.updateGold(vang);
                this.removeItem(chiSo, vatPham.soLuong);
                this.startOKDlg2("Bán vật phẩm thành công.");
            } else {
                this.startOKDlg2("Bán vật phẩm thất bại.");
            }
        }
    }

    public void yeuCauBanVatPham(VXLTinNhan ms) throws IOException {
        byte chiSo = ms.boDoc().readByte();
        kiemTraChiSo(chiSo, this.itemBag.length, "tui do");
        VXLVatPham vatPham = this.itemBag[chiSo];
        if (vatPham != null && vatPham.mau != null && vatPham.soLuong > 0) {
            if (this.vatPhamCoTrongBalo(vatPham)) {
                this.startOKDlg2("Vật phẩm đã gắn vào Balo.");
                return;
            }
            int vang = 0;
            vang = vatPham.mau.buyGold > 0 ? vatPham.mau.buyGold / 2 : (vatPham.mau.buyGem > 0 ? vatPham.mau.buyGem * 100 : 1);
            VXLTinNhan mss = new VXLTinNhan(-25);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(11000 + chiSo);
            ds.writeUTF("Bạn có chắc muốn bán " + vatPham.mau.ten + " với giá " + VXLTienIch.dinhDangTien(vang *= vatPham.soLuong) + " Vàng");
            ds.flush();
            this.dichVu.guiTin(mss);
        } else {
            this.startOKDlg2("Bạn không có vật phẩm này.");
        }
    }

    public void yeuCauMuaVatPham(VXLTinNhan ms) throws IOException {
        byte loai = ms.boDoc().readByte();
        int ma = ms.boDoc().readUnsignedShort();
        VXLMauVatPham vatPham = VXLQuanLyMayChu.itemTemplates.get(ma);
        if (vatPham == null) {
            this.startOKDlg2("Có lỗi xảy ra.");
            return;
        }
        if ((loai == 0 && vatPham.buyGold > 0) || (loai == 1 && vatPham.buyGem > 0)) {
            if (this.layOTrongTuiDo() == 0 && !this.coTheGopVatPham(vatPham)) {
                this.startOKDlg2("Túi đã đầy.");
                return;
            }
            if (loai == 0) {
                if (vatPham.buyGold > this.vang) {
                    this.startOKDlg2("Bạn không đủ vàng.");
                    return;
                }
                this.updateGold(-vatPham.buyGold);
            } else {
                if (vatPham.buyGem > this.ngoc) {
                    this.startOKDlg2("Bạn không đủ ngọc.");
                    return;
                }
                this.updateGem(-vatPham.buyGem);
            }
        } else {
            this.moHopThoaiOK("Có lỗi xảy ra.");
            return;
        }
        VXLVatPham add = new VXLVatPham(ma);
        add.thayMau(vatPham);
        if (!this.themVatPhamVaoTui(add)) {
            if (loai == 0) {
                this.updateGold(vatPham.buyGold);
            } else {
                this.updateGem(vatPham.buyGem);
            }
            this.moHopThoaiOK("Không thể thêm vật phẩm vào túi.");
            return;
        }
        this.moHopThoaiOK("Bạn mua thành công " + vatPham.ten);
    }

    public void datTrangBiChoNhanVat(VXLVatPham vatPham) {
        if (vatPham == null || vatPham.mau == null) {
            return;
        }
        int ma = vatPham.ma;
        this.avenger = 0;
        if (ma == 391) {
            this.head = (short)204;
            this.body = (short)205;
            this.leg = (short)206;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = 1;
        } else if (ma == 392) {
            this.head = (short)220;
            this.body = (short)221;
            this.leg = (short)222;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)2;
        } else if (ma == 393) {
            this.head = (short)219;
            this.body = (short)217;
            this.leg = (short)218;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)3;
        } else if (ma == 394) {
            this.head = (short)198;
            this.body = (short)211;
            this.leg = (short)212;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)4;
        } else if (ma == 395) {
            this.head = (short)197;
            this.body = (short)207;
            this.leg = (short)208;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)5;
        } else if (ma == 396) {
            this.head = (short)203;
            this.body = (short)213;
            this.leg = (short)214;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)6;
        } else if (ma == 397) {
            this.head = (short)202;
            this.body = (short)215;
            this.leg = (short)216;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)7;
        } else if (ma == 398) {
            this.head = (short)199;
            this.body = (short)209;
            this.leg = (short)210;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)8;
        } else if (ma == 413) {
            this.head = (short)278;
            this.body = (short)279;
            this.leg = (short)280;
            this.wp = (short)283;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)0;
        } else {
            VXLVatPham t = this.itemBody[5];
            if (t == null || t.ma != 413 && (t.ma < 391 || t.ma > 400)) {
                byte loai = vatPham.mau.loai;
                short part = vatPham.mau.part;
                if (loai == 0) {
                    this.head = part;
                } else if (loai == 1) {
                    this.leg = part;
                } else if (loai == 2) {
                    this.body = part;
                } else if (loai == 3) {
                    this.hat = part;
                } else if (loai == 4) {
                    this.wing = part;
                } else if (loai == 5) {
                    this.wp = part;
                }
            }
        }
    }

    public void dongBoTrangBiNhanVat() {
        this.lamMoiTrangBiNhanVat();
    }

    private void lamMoiTrangBiNhanVat() {
        this.head = -1;
        this.hat = -1;
        this.body = -1;
        this.leg = -1;
        this.wp = -1;
        this.wing = -1;
        this.avenger = 0;
        for (VXLVatPham vatPham : this.itemBody) {
            this.datTrangBiChoNhanVat(vatPham);
        }
    }

    public boolean vatPhamCoTrongBalo(VXLVatPham vatPham) {
        if (vatPham == null || this.itemBalo == null) {
            return false;
        }
        for (int chiSo : this.itemBalo) {
            if (chiSo != vatPham.chiSo) continue;
            return true;
        }
        return false;
    }

    public void dungVatPham(VXLTinNhan ms) throws IOException {
        byte chiSo = ms.boDoc().readByte();
        if (ms.boDoc().available() > 0) {
            byte loai = ms.boDoc().readByte();
            if (loai == 1) {
                kiemTraChiSo(chiSo, this.itemBag.length, "tui do");
                VXLVatPham vatPham = this.itemBag[chiSo];
                if (vatPham != null && vatPham.mau != null && vatPham.soLuong > 0) {
                    byte t = vatPham.mau.loai;
                    int ma = vatPham.ma;
                    if (ma == VXLTienTrinhDucLo.MA_BUA_DUC_LO) {
                        this.kichHoatBua(Byte.toUnsignedInt(chiSo));
                        return;
                    }
                    if (t == 12) {
                        this.startOKDlg2("Bạn có muốn nhập 5 viên ngọc này, hãy vào menu Bắt dầu -> ghép ngọc");
                        return;
                    }
                    if (t <= 5) {
                        VXLDichVuNgocTrangBi.moMenu(this, true, Byte.toUnsignedInt(chiSo), vatPham);
                    } else if (ma == 256) {
                        this.ensurePointAdd();
                        this.point = (short)Math.min(Short.MAX_VALUE, this.point + (this.pointAdd[0] - 1000) / 10
                                + this.pointAdd[1] + this.pointAdd[2] + this.pointAdd[3] + this.pointAdd[4] + this.pointAdd[5]);
                        this.pointAdd[0] = 1000;
                        this.pointAdd[1] = 0;
                        this.pointAdd[2] = 0;
                        this.pointAdd[3] = 0;
                        this.pointAdd[4] = 0;
                        this.pointAdd[5] = 0;
                        this.removeItem(chiSo, 1);
                        this.startOKDlg2("Tẩy điểm thành công.");
                    } else if (ma == 257) {
                        if (this.kichHoatNhanDoiKinhNghiem()) {
                            this.removeItem(chiSo, 1);
                            this.flushCache();
                        }
                    } else if (vatPham.mau.loai == 11) {
                        this.startOKDlg2("Không thể sử dụng.");
                    } else {
                        this.startOKDlg2("Không thể sử dụng.");
                    }
                } else {
                    this.startOKDlg2("Không tìm thấy vật phẩm này. Vui lòng đăng nhập lại để kiểm tra.");
                }
            } else {
                kiemTraChiSo(chiSo, this.itemBody.length, "trang bi");
                VXLVatPham vatPham = this.itemBody[chiSo];
                if (vatPham != null) {
                    VXLDichVuNgocTrangBi.moMenu(this, false, Byte.toUnsignedInt(chiSo), vatPham);
                } else {
                    this.startOKDlg2("Không tìm thấy vật phẩm này. Vui lòng đăng nhập lại để kiểm tra.");
                }
            }
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public void chuyenVatPham(VXLTinNhan ms) throws IOException {
        VXLVatPham vatPham;
        byte loai = ms.boDoc().readByte();
        int chiSo = ms.boDoc().readUnsignedByte();
        if (loai == 4) {
            kiemTraChiSo(chiSo, this.itemBag.length, "tui do");
            VXLVatPham item2 = this.itemBag[chiSo];
            if (item2 == null || item2.mau == null) return;
            if (this.vatPhamCoTrongBalo(item2)) {
                this.moHopThoaiOK("Vật phẩm đã gắn vào Balo.");
                return;
            }
            int capYeuCau = Math.max(0, Byte.toUnsignedInt(item2.mau.cap) - 1);
            if (this.cap < capYeuCau) {
                this.moHopThoaiOK("Trình độ không đạt yêu cầu.");
                return;
            }
            byte t = item2.mau.loai;
            if (t > 5) {
                this.moHopThoaiOK("Trang bị không phù hợp.");
                return;
            }
            if (t == 5 && this.isFlyAvenger() && this.y > 360) {
                this.moHopThoaiOK("Không thể thay đổi trang phục khi đang ở dưới đất.");
                return;
            }
            this.y = (short)360;
            if (this.itemBody[t] != null && this.layOTrongTuiDo() == 0) {
                this.moHopThoaiOK("Túi đồ đã đầy.");
                return;
            }
            int sucChuaMoi = t == 4 ? Math.max(0, item2.getParamById(13)) : this.itemBalo.length;
            if (t == 4 && this.soVatPhamTrongBalo() > sucChuaMoi) {
                this.moHopThoaiOK("Balo mới không đủ chỗ cho vật phẩm đang gắn.");
                return;
            }
            if (this.itemBody[t] != null) {
                VXLVatPham item3 = this.itemBody[t];
                if (!this.themVatPhamVaoTui(item3)) {
                    this.moHopThoaiOK("Không thể chuyển trang bị cũ vào túi.");
                    return;
                }
                item2.chiSo = t;
                this.itemBody[t] = item2;
                this.itemBag[chiSo] = null;
            } else {
                item2.chiSo = t;
                this.itemBody[t] = item2;
                this.itemBag[chiSo] = null;
            }
            if (t == 4) {
                int[] arrIndex = {};
                if (this.itemBody[t] != null && this.itemBalo != null) {
                    arrIndex = this.itemBalo;
                }
                this.itemBalo = new int[sucChuaMoi];
                for (int i = 0; i < this.itemBalo.length; i++) {
                    this.itemBalo[i] = -1;
                }
                for (int i = 0; i < Math.min(arrIndex.length, this.itemBalo.length); i++) {
                    this.itemBalo[i] = arrIndex[i];
                }
                this.dichVu.guiBalo();
            }
            this.lamMoiTrangBiNhanVat();
            this.dichVu.guiTuiDo();
            this.dichVu.guiDoTrenNguoi();
            this.dichVu.doiTrangBi();
            this.guiCapNhatTrangBiChoKhu();
            return;
        }
        if (loai == 5) {
            kiemTraChiSo(chiSo, this.itemBody.length, "trang bi");
            VXLVatPham item4 = this.itemBody[chiSo];
            if (item4 == null || item4.mau == null) return;
            byte t = item4.mau.loai;
            if (t < 0 || t > 5) {
                this.moHopThoaiOK("Không thể tháo trang bị này.");
                return;
            }
            if (t == 4 && this.soVatPhamTrongBalo() > 0) {
                this.moHopThoaiOK("Balo đang chứa vật phẩm.");
                return;
            }
            if (this.layOTrongTuiDo() == 0) {
                this.moHopThoaiOK("Túi đồ đã đầy.");
                return;
            }
            if (!this.themVatPhamVaoTui(item4)) {
                this.moHopThoaiOK("Không thể chuyển trang bị vào túi.");
                return;
            }
            this.itemBody[chiSo] = null;
            if (t == 4) {
                this.itemBalo = new int[0];
                this.dichVu.guiBalo();
            }
            this.lamMoiTrangBiNhanVat();
            this.dichVu.guiTuiDo();
            this.dichVu.guiDoTrenNguoi();
            this.dichVu.doiTrangBi();
            this.guiCapNhatTrangBiChoKhu();
            return;
        }
        if (loai == 1) {
            kiemTraChiSo(chiSo, this.itemBag.length, "tui do");
            VXLVatPham item5 = this.itemBag[chiSo];
            if (item5 == null || item5.mau == null) return;
            if (this.vatPhamCoTrongBalo(item5)) {
                this.moHopThoaiOK("Vật phẩm đã gắn vào Balo.");
                return;
            }
            if (this.layOTrongRuong() == 0 && !this.coTheGopVatPhamTrongRuong(item5)) {
                this.moHopThoaiOK("Rương đã đầy.");
                return;
            }
            if (!this.themVatPhamVaoRuong(item5)) {
                this.moHopThoaiOK("Không thể chuyển vật phẩm vào rương.");
                return;
            }
            this.itemBag[chiSo] = null;
            this.dichVu.guiTuiDo();
            return;
        }
        if (loai == 6) {
            kiemTraChiSo(chiSo, this.itemBag.length, "tui do");
            vatPham = this.itemBag[chiSo];
            if (vatPham == null || vatPham.mau == null) return;
            byte t = vatPham.mau.loai;
            if (t != 10 && t != 5) {
                this.moHopThoaiOK("Không thể cho vật phẩm này vào balo.");
                return;
            }
            int n = this.layOTrongBalo();
            if (n == 0) {
                this.moHopThoaiOK("Balo đã đầy.");
                return;
            }
        } else {
            if (loai != 0) {
                if (loai != 7) return;
                kiemTraChiSo(chiSo, this.itemBalo.length, "balo");
                this.itemBalo[chiSo] = -1;
                this.dichVu.guiBalo();
                return;
            }
            kiemTraChiSo(chiSo, this.itemBox.length, "ruong do");
            VXLVatPham item6 = this.itemBox[chiSo];
            if (item6 == null || item6.mau == null) return;
            if (this.layOTrongTuiDo() == 0 && !this.coTheGopVatPham(item6.mau)) {
                this.moHopThoaiOK("Túi đã đầy.");
                return;
            }
            if (!this.themVatPhamVaoTui(item6)) {
                this.moHopThoaiOK("Không thể chuyển vật phẩm vào túi.");
                return;
            }
            this.itemBox[chiSo] = null;
            this.dichVu.guiRuongDo();
            return;
        }
        for (int i = 0; i < this.itemBalo.length; ++i) {
            if (this.itemBalo[i] != -1) continue;
            this.itemBalo[i] = vatPham.chiSo;
            break;
        }
        this.dichVu.guiBalo();
    }

    public int soVatPhamTrongBalo() {
        int number = 0;
        if (this.itemBalo != null) {
            for (int chiSo : this.itemBalo) {
                if (chiSo == -1) continue;
                ++number;
            }
        }
        return number;
    }

    public VXLVatPham layVuKhiTrongBalo(int chiSoBalo) {
        if (this.itemBalo == null || chiSoBalo < 0 || chiSoBalo >= this.itemBalo.length) {
            return null;
        }
        int chiSoTui = this.itemBalo[chiSoBalo];
        if (this.itemBag == null || chiSoTui < 0 || chiSoTui >= this.itemBag.length) {
            return null;
        }
        VXLVatPham vuKhi = this.itemBag[chiSoTui];
        if (vuKhi == null || vuKhi.mau == null || vuKhi.mau.loai != 5
                || vuKhi.HP <= 0 || vuKhi.soLuong <= 0 || vuKhi.mau.part < 0) {
            return null;
        }
        return vuKhi;
    }

    public synchronized VXLVatPham doiVuKhiTrongBalo(int chiSoBalo) {
        VXLVatPham vuKhiMoi = this.layVuKhiTrongBalo(chiSoBalo);
        if (vuKhiMoi == null || this.itemBody == null || this.itemBody.length <= 5) {
            return null;
        }
        VXLVatPham vuKhiCu = this.itemBody[5];
        if (vuKhiCu == null || vuKhiCu.mau == null || vuKhiCu == vuKhiMoi) {
            return null;
        }
        int chiSoTui = this.itemBalo[chiSoBalo];
        vuKhiCu.chiSo = chiSoTui;
        vuKhiMoi.chiSo = 5;
        this.itemBody[5] = vuKhiMoi;
        this.itemBag[chiSoTui] = vuKhiCu;
        this.lamMoiTrangBiNhanVat();
        return vuKhiCu;
    }

    private void kichHoatBua(int chiSo) throws IOException {
        if (chiSo < 0 || chiSo >= this.itemBag.length) {
            return;
        }
        VXLVatPham bua = this.itemBag[chiSo];
        if (bua == null || bua.ma != VXLTienTrinhDucLo.MA_BUA_DUC_LO || bua.soLuong <= 0) {
            this.startOKDlg2("Không tìm thấy Búa chuyên dụng.");
            return;
        }
        VXLTienTrinhDucLo.capNhat(this);
        int tongBua = Byte.toUnsignedInt(this.nHammer);
        if (tongBua >= VXLTienTrinhDucLo.SO_BUA_TOI_DA) {
            this.startOKDlg2("Bạn đã kích hoạt tối đa 3 búa.");
            return;
        }
        this.nHammer = (byte)(tongBua + 1);
        this.removeItem(chiSo, 1);
        VXLTienTrinhDucLo.capNhatThongTinBua(this);
        this.dichVu.capNhat();
        this.flushCache();
        this.startOKDlg2("Kích hoạt Búa chuyên dụng thành công. Tổng búa: "
                + Byte.toUnsignedInt(this.nHammer) + "/" + VXLTienTrinhDucLo.SO_BUA_TOI_DA + ".");
    }
    private boolean gioiHanSoLuongBuaNhan(VXLVatPham vatPham) {
        if (vatPham == null || vatPham.ma != VXLTienTrinhDucLo.MA_BUA_DUC_LO) {
            return true;
        }
        int soLuongCoTheNhan = VXLTienTrinhDucLo.laySoLuongBuaCoTheNhan(this, vatPham);
        if (soLuongCoTheNhan <= 0) {
            return false;
        }
        vatPham.soLuong = Math.min(vatPham.soLuong, soLuongCoTheNhan);
        return vatPham.soLuong > 0;
    }
    public boolean themVatPhamVaoTui(VXLVatPham vatPham) {
        if (vatPham == null || vatPham.mau == null || vatPham.soLuong <= 0 || this.itemBag == null) {
            return false;
        }
        if (!this.gioiHanSoLuongBuaNhan(vatPham)) {
            return false;
        }
        try {
            int i;
            byte loai = vatPham.mau.loai;
            if (loai > 5) {
                for (i = 0; i < this.itemBag.length; ++i) {
                    if (this.itemBag[i] == null || this.itemBag[i].ma != vatPham.ma) continue;
                    this.itemBag[i].soLuong += vatPham.soLuong;
                    this.dichVu.capNhatTuiDo(i, this.itemBag[i].soLuong);
                    return true;
                }
            }
            for (i = 0; i < this.itemBag.length; ++i) {
                if (this.itemBag[i] != null) continue;
                vatPham.chiSo = i;
                this.itemBag[i] = vatPham;
                this.dichVu.guiTuiDo();
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean themVatPhamVaoRuong(VXLVatPham vatPham) {
        if (vatPham == null || vatPham.mau == null || vatPham.soLuong <= 0 || this.itemBox == null) {
            return false;
        }
        if (!this.gioiHanSoLuongBuaNhan(vatPham)) {
            return false;
        }
        try {
            int i;
            byte loai = vatPham.mau.loai;
            if (loai > 5) {
                for (i = 0; i < this.itemBox.length; ++i) {
                    if (this.itemBox[i] == null || this.itemBox[i].ma != vatPham.ma) continue;
                    this.itemBox[i].soLuong += vatPham.soLuong;
                    this.dichVu.capNhatRuongDo(i, this.itemBox[i].soLuong);
                    return true;
                }
            }
            for (i = 0; i < this.itemBox.length; ++i) {
                if (this.itemBox[i] != null) continue;
                vatPham.chiSo = i;
                this.itemBox[i] = vatPham;
                this.dichVu.guiRuongDo();
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void moKhu() throws IOException {
        VXLTinNhan mss = new VXLTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(7);
        ds.writeByte(VXLBanDoRPG.zones.size());
        for (VXLKhu z : VXLBanDoRPG.zones) {
            ds.writeByte(z.zoneId);
            ds.writeByte(z.pts);
            ds.writeByte(z.numPlayer);
            ds.writeByte(z.maxPlayer);
        }
        ds.flush();
        this.dichVu.guiTin(mss);
    }

    public void chat(VXLTinNhan ms) throws IOException {
        String noiDung = ms.docUTF(200, "nội dung chat");
        if (VXLBoLenhQuanTri.xuLy(this, noiDung)) {
            return;
        }
        if (noiDung != null) {
            String trimmed = noiDung.trim();
            String lower = trimmed.toLowerCase();
            if (lower.startsWith("/code ") || lower.startsWith("/giftcode ")
                    || lower.equals("/code") || lower.equals("/giftcode")) {
                String code = "";
                if (lower.startsWith("/code ")) {
                    code = trimmed.substring(6).trim();
                } else if (lower.startsWith("/giftcode ")) {
                    code = trimmed.substring(10).trim();
                }
                if (code.isEmpty()) {
                    this.moHopThoaiOK("Cú pháp không hợp lệ. Hãy gõ:\n/code <mã>\nVí dụ: /code VIP2026");
                    return;
                }
                com.vxl.giftcode.VXLGiftcodeService.GiftResult res =
                        com.vxl.giftcode.VXLGiftcodeService.redeem(this.layUserId(), code);
                if (res.success) {
                    if (res.gold > 0) this.updateGold(res.gold);
                    if (res.gem > 0) this.updateGem(res.gem);
                    if (res.items != null && !res.items.isEmpty()) {
                        StringBuilder sb = new StringBuilder(res.message);
                        if (res.gold > 0) sb.append("\n+").append(res.gold).append(" vàng");
                        if (res.gem > 0) sb.append("\n+").append(res.gem).append(" ngọc");
                        for (com.vxl.giftcode.VXLGiftcodeService.GiftItem gi : res.items) {
                            VXLVatPham vp = new VXLVatPham(gi.id);
                            vp.soLuong = gi.quantity;
                            this.themVatPhamVaoTui(vp);
                            String tenVp = vp.mau != null ? vp.mau.ten : ("Vật phẩm #" + gi.id);
                            sb.append("\n+").append(gi.quantity).append(" ").append(tenVp);
                        }
                        this.moHopThoaiOK(sb.toString());
                        return;
                    }
                }
                this.moHopThoaiOK(res.message);
                return;
            }
        }
        VXLTinNhan mss = new VXLTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(3);
        ds.writeByte(this.chiSo);
        ds.writeUTF(noiDung);
        ds.flush();
        this.zone.guiTatCaNguoiChoi(mss);
    }

    public int layUserId() {
        if (this.dichVu != null && this.dichVu.layKhach() != null && this.dichVu.layKhach().user != null) {
            return this.dichVu.layKhach().user.getUserId();
        }
        return this.ma;
    }

    public void diChuyen(VXLTinNhan ms) throws IOException {
        this.x = ms.boDoc().readShort();
        this.y = ms.boDoc().readShort();
        if (!this.isFlyAvenger() && this.y != 360) {
            this.y = (short)360;
        }
        VXLTinNhan mss = new VXLTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(2);
        ds.writeByte(this.chiSo);
        ds.writeShort(this.x);
        ds.writeShort(this.y);
        ds.flush();
        this.zone.guiTatCaNguoiChoi(mss);
    }

    public void xemCuaHang(VXLCuaHang store) throws IOException {
        this.store = store;
        this.dichVu.xemCuaHang(this.store);
    }

    public void removeItem(int chiSo, int soLuong) {
        if (chiSo < 0 || chiSo >= this.itemBag.length || soLuong <= 0) {
            return;
        }
        try {
            VXLVatPham vatPham = this.itemBag[chiSo];
            if (vatPham != null) {
                vatPham.soLuong -= soLuong;
                if (vatPham.soLuong > 0) {
                    this.itemBag[chiSo].soLuong = vatPham.soLuong;
                    this.dichVu.capNhatTuiDo(chiSo, vatPham.soLuong);
                } else {
                    this.itemBag[chiSo] = null;
                    this.xoaKhoiBalo(chiSo);
                    this.dichVu.capNhatTuiDo(chiSo, 0);
                }
            } else {
                this.dichVu.capNhatTuiDo(chiSo, 0);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void tieuThuVatPhamTrongBalo(int chiSoBalo) throws IOException {
        kiemTraChiSo(chiSoBalo, this.itemBalo.length, "balo");
        int chiSoTui = this.itemBalo[chiSoBalo];
        if (chiSoTui < 0) {
            return;
        }
        kiemTraChiSo(chiSoTui, this.itemBag.length, "túi đồ");
        VXLVatPham vatPham = this.itemBag[chiSoTui];
        if (vatPham == null || vatPham.soLuong <= 0) {
            this.itemBalo[chiSoBalo] = -1;
            this.dichVu.guiBalo();
            return;
        }
        vatPham.soLuong--;
        if (vatPham.soLuong <= 0) {
            this.itemBag[chiSoTui] = null;
            this.itemBalo[chiSoBalo] = -1;
            this.dichVu.capNhatTuiDo(chiSoTui, 0);
            this.dichVu.guiBalo();
        } else {
            this.dichVu.capNhatTuiDo(chiSoTui, vatPham.soLuong);
            this.dichVu.guiBalo();
        }
    }

    public void updateGold(int vang) {
        this.vang = (int)Math.max(0L, Math.min(Integer.MAX_VALUE, (long)this.vang + vang));
        this.dichVu.capNhat();
    }

    public void updateGem(int ngoc) {
        this.ngoc = (int)Math.max(0L, Math.min(Integer.MAX_VALUE, (long)this.ngoc + ngoc));
        this.dichVu.capNhat();
    }

    public void requestTab(VXLTinNhan ms) throws IOException {
        if (this.store == null) {
            return;
        }
        byte chiSo = ms.boDoc().readByte();
        byte page = ms.boDoc().readByte();
        if (chiSo < 0 || page < 0 || chiSo >= this.store.tabs.size() || page >= this.store.tabs.get(chiSo).size()) {
            this.moHopThoaiOK("Có lỗi xảy ra.");
            return;
        }
        ms = new VXLTinNhan(-43);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeByte(page);
        ArrayList<VXLTrang> pages = this.store.tabs.get(chiSo);
        ds.writeByte(pages.size());
        VXLTrang p = pages.get(page);
        if (p == null) {
            this.moHopThoaiOK("Có lỗi xảy ra.");
            return;
        }
        int soVatPhamHopLe = 0;
        for (VXLMauVatPham t : p.vatPhams) {
            if (t != null) {
                soVatPhamHopLe++;
            }
        }
        ds.writeByte(soVatPhamHopLe);
        for (VXLMauVatPham t : p.vatPhams) {
            if (t == null) {
                continue;
            }
            ds.writeShort(t.ma);
            ds.writeInt(t.buyGold);
            ds.writeInt(t.buyGem);
            int numberOption = 0;
            for (Object giaTri : t.thuocTinhs) {
                if (giaTri instanceof VXLThuocTinhVatPham option && option.optionTemplate != null) {
                    numberOption++;
                }
            }
            ds.writeByte(numberOption);
            for (Object giaTri : t.thuocTinhs) {
                if (!(giaTri instanceof VXLThuocTinhVatPham option) || option.optionTemplate == null) {
                    continue;
                }
                if (option == null || option.optionTemplate == null) {
                    continue;
                }
                ds.writeByte(option.optionTemplate.ma);
                ds.writeShort(option.thamSo);
            }
        }
        ds.flush();
        this.dichVu.guiTin(ms);
    }

    public void moHopThoaiOK(String noiDung) {
        try {
            this.dichVu.moHopThoaiOK(noiDung);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startOKDlg2(String noiDung) {
        try {
            this.dichVu.baoLoiTien(noiDung);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isFlyAvenger() {
        return this.avenger == 1 || this.avenger == 8;
    }

    public byte layAvengerDan() {
        return this.head == 278 && this.body == 279 && this.leg == 280
                ? (byte)9 : this.avenger;
    }

    public static void onChatFromToAllPlayer(String ten, String noiDung) {
        try {
            VXLTinNhan mss = new VXLTinNhan(5);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(-1);
            ds.writeUTF(ten);
            ds.writeUTF(noiDung);
            ds.flush();
            VXLNguoiChoi.guiMayChu(mss);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void chatTo(VXLTinNhan ms) throws IOException {
        int ma = ms.boDoc().readInt();
        String noiDung = ms.docUTF(200, "nội dung chat");
        if (VXLBoLenhQuanTri.xuLy(this, noiDung)) {
            return;
        }
        if (ma == -1) {
            if (this.ngoc < 10) {
                this.moHopThoaiOK("Bạn không đủ ngọc để chat thế giới.");
                return;
            }
            this.updateGem(-10);
            VXLNguoiChoi.onChatFromToAllPlayer(this.ten, noiDung);
        } else {
            VXLNguoiChoi pl = VXLNguoiChoi.layNguoiChoiTheoMa(ma);
            if (pl != null) {
                VXLTinNhan mss = new VXLTinNhan(5);
                DataOutputStream ds = mss.boGhi();
                ds.writeInt(this.ma);
                ds.writeUTF(this.ten);
                ds.writeUTF(noiDung);
                ds.flush();
                pl.dichVu.guiTin(mss);
            }
        }
    }

    public void viewPlayerInfo(VXLTinNhan ms) throws IOException {
        int ma = ms.boDoc().readInt();
        VXLNguoiChoi pl = VXLNguoiChoi.layNguoiChoiTheoMa(ma);
        if (pl != null) {
            VXLTinNhan mss = new VXLTinNhan(-126);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(pl.ma);
            ds.writeUTF(pl.ten);
            ds.writeShort(pl.head);
            ds.writeShort(pl.hat);
            ds.writeShort(pl.body);
            ds.writeShort(pl.leg);
            ds.writeShort(pl.wing);
            ds.writeShort(pl.wp);
            ds.writeInt(pl.kinhNghiem);
            ds.writeByte(1);
            ds.writeShort(pl.clanIcon);
            ds.flush();
            this.dichVu.guiTin(mss);
        }
    }

    public synchronized void flushCache() {
        try {
            VXLCoSoDuLieu.withTransaction(conn -> this.luuTrangThaiGiaoDich(conn, this.vang, this.itemBag));
        }
        catch (SQLException | RuntimeException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void luuTrangThaiGiaoDich(java.sql.Connection conn, int vangMoi, VXLVatPham[] tuiMoi) throws SQLException {
        if (conn == null || tuiMoi == null) {
            throw new IllegalArgumentException("Kết nối và túi đồ cần lưu không được rỗng.");
        }
        this.ensurePointAdd();
        JSONObject duLieu = new JSONObject();
        duLieu.put("power", this.power);
        duLieu.put("avenger", this.powerAvenger);
        duLieu.put("kill", this.kill);
        duLieu.put("dead", this.chet);
        duLieu.put("assist", this.assist);
        duLieu.put("trainingSuccess", this.trainingSuccess);
        duLieu.put("busyHammer", this.busyHammer);
        duLieu.put("nHammer", this.nHammer);
        duLieu.put("exp", this.kinhNghiem);
        duLieu.put("point", this.point);
        this.nhiemVu.ghiVao(duLieu);
        this.luyenTap.ghiVao(duLieu);
        JSONArray pointAdds = new JSONArray();
        for (short pointValue : this.pointAdd) {
            pointAdds.add(pointValue);
        }
        duLieu.put("pointAdd", pointAdds);

        JSONArray balo = new JSONArray();
        for (int chiSo : this.itemBalo) {
            balo.add(chiSo);
        }

        String sql = "UPDATE `players` SET `gold` = ?, `cup` = ?, `gem` = ?, `stats_json` = ?, `equipped_json` = ?, `inventory_json` = ?, `pocket_json` = ?, `storage_json` = ? WHERE `id` = ? LIMIT 1;";
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vangMoi);
            stmt.setInt(2, this.cup);
            stmt.setInt(3, this.ngoc);
            stmt.setString(4, duLieu.toJSONString());
            stmt.setString(5, taoMangVatPhamJson(this.itemBody).toJSONString());
            stmt.setString(6, taoMangVatPhamJson(tuiMoi).toJSONString());
            stmt.setString(7, balo.toJSONString());
            stmt.setString(8, taoMangVatPhamJson(this.itemBox).toJSONString());
            stmt.setInt(9, this.ma);
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Không tìm thấy người chơi có mã=" + this.ma + " để lưu.");
            }
        }
    }

    private static JSONArray taoMangVatPhamJson(VXLVatPham[] vatPhams) {
        JSONArray ketQua = new JSONArray();
        if (vatPhams == null) {
            return ketQua;
        }
        for (VXLVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                ketQua.add(vatPham.toJSONObject());
            }
        }
        return ketQua;
    }

    private void ensurePointAdd() {
        if (this.pointAdd == null || this.pointAdd.length != 6) {
            short[] pointAddMoi = new short[]{1000, 0, 0, 0, 0, 0};
            if (this.pointAdd != null) {
                System.arraycopy(this.pointAdd, 0, pointAddMoi, 0, Math.min(this.pointAdd.length, pointAddMoi.length));
            }
            this.pointAdd = pointAddMoi;
        }
    }

    private boolean coTheGopVatPham(VXLMauVatPham mau) {
        if (mau == null || mau.loai <= 5 || this.itemBag == null) {
            return false;
        }
        for (VXLVatPham vatPham : this.itemBag) {
            if (vatPham != null && vatPham.mau != null && vatPham.ma == mau.ma) {
                return true;
            }
        }
        return false;
    }

    private boolean coTheGopVatPhamTrongRuong(VXLVatPham vatPham) {
        if (vatPham == null || vatPham.mau == null || vatPham.mau.loai <= 5 || this.itemBox == null) {
            return false;
        }
        for (VXLVatPham item : this.itemBox) {
            if (item != null && item.mau != null && item.ma == vatPham.ma) {
                return true;
            }
        }
        return false;
    }

    private void guiCapNhatTrangBiChoKhu() {
        if (this.zone == null) {
            return;
        }
        for (VXLNguoiChoi player : this.zone.players_id.values()) {
            if (player != null && player != this) {
                try {
                    player.dichVu.vaoCho(this);
                }
                catch (IOException ex) {
                    Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.FINE,
                            "Không thể cập nhật trang bị cho người chơi trong khu.", ex);
                }
            }
        }
    }

    private void xoaKhoiBalo(int chiSoTui) {
        if (this.itemBalo == null) {
            return;
        }
        boolean thayDoi = false;
        for (int i = 0; i < this.itemBalo.length; i++) {
            if (this.itemBalo[i] == chiSoTui) {
                this.itemBalo[i] = -1;
                thayDoi = true;
            }
        }
        if (thayDoi && this.dichVu != null) {
            try {
                this.dichVu.guiBalo();
            }
            catch (IOException ex) {
                Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.FINE, "Không thể cập nhật Balo.", ex);
            }
        }
    }

    public void close() {
        this.luyenTap.dong();
        try {
            VXLQuanLyPhong.roiBanCho(this);
        }
        catch (Exception ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.WARNING, "Lỗi rời phòng khi đóng người chơi " + this.ma, ex);
        }
        try {
            VXLBanDoRPG.roi(this);
        }
        catch (Exception ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.WARNING, "Lỗi rời bản đồ khi đóng người chơi " + this.ma, ex);
        }
        try {
            this.flushCache();
        }
        finally {
            VXLNguoiChoi.xoa(this.ma);
        }
    }
    public void vaoLuyenTap() {
        VXLQuanLyPhong.roiBanCho(this);
        VXLBanDoRPG.roi(this);
        this.luyenTap.vao();
    }

    public boolean roiLuyenTapNeuCan() {
        return this.luyenTap.roiNeuDangLuyenTap();
    }

    public synchronized void datLaiTienDoPhienQuanHangNgay() {
        this.luyenTap.datLaiHangNgay();
        this.flushCache();
    }
    public void handleTrainingMove(VXLTinNhan ms) throws IOException {
        this.luyenTap.diChuyen(ms);
    }

    public void xuLyCapNhatXYSauRoiLuyenTap(VXLTinNhan ms) throws IOException {
        this.luyenTap.capNhatXYSauRoi(ms);
    }

    public void xuLyBanLuyenTap(VXLTinNhan ms) throws IOException {
        this.luyenTap.ban(ms);
    }

    public void xuLyVaChamLuyenTap(VXLTinNhan ms) throws IOException {
        this.luyenTap.xuLyVaCham(ms);
    }

    public void handleTrainingHoleRequest(VXLTinNhan ms) throws IOException {
        this.luyenTap.yeuCauDatLaiHo();
    }

    public void handleTrainingClientReady() throws IOException {
        this.luyenTap.sanSang();
    }

    public void xuLyFocusSkill(VXLTinNhan ms) throws IOException {
        this.luyenTap.xuLyFocusSkill(ms);
    }

    public void xuLyDoiSungLuyenTap(VXLTinNhan ms) throws IOException {
        this.luyenTap.doiSung(ms);
    }

    public void xuLyVatPhamLuyenTap(VXLTinNhan ms) throws IOException {
        this.luyenTap.dungVatPham(ms);
    }

    public synchronized void datCapPhienQuan(int cap) {
        this.trainingSuccess = (byte)Math.max(1, Math.min(255, cap));
        this.flushCache();
    }
}