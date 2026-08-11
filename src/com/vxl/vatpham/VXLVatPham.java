package com.vxl.vatpham;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.tienich.VXLDuLieuJson;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.util.Vector;

public class VXLVatPham {
    private static final int MA_DANG_DUC_LO = 15;
    private static final int MA_LO_NGOC = 16;
    private static final int SO_LO_TOI_DA = 3;
    private static final long MOT_GIO_MILLIS = 60L * 60L * 1000L;

    public VXLMauVatPham mau;
    public int ma;
    public int soLuong;
    public int HP;
    public int chiSo;
    public Vector itemOptions = new Vector();
    public byte nSocket;
    public boolean isSocketing;
    public long socketFinishTime;
    public int nGem;

    public VXLVatPham(int ma) {
        this.ma = ma;
        this.mau = VXLQuanLyMayChu.itemTemplates == null ? null : VXLQuanLyMayChu.itemTemplates.get(ma);
        this.HP = 100;
        this.soLuong = 1;
        this.nSocket = 0;
        this.isSocketing = false;
    }

    public VXLVatPham(JSONObject doiTuong) {
        this.tai(doiTuong);
    }

    public boolean isTypeBody() {
        return this.mau != null && this.mau.loai >= 0 && this.mau.loai < 6;
    }

    public void tai(JSONObject doiTuong) {
        VXLDuLieuJson parse = new VXLDuLieuJson(doiTuong);
        this.ma = parse.getInt("id");
        this.mau = VXLQuanLyMayChu.itemTemplates == null ? null : VXLQuanLyMayChu.itemTemplates.get(this.ma);
        if (this.mau == null) {
            throw new IllegalArgumentException("Không tồn tại mẫu vật phẩm: " + this.ma);
        }
        this.soLuong = Math.max(1, parse.getInt("quantity"));
        this.HP = Math.max(0, Math.min(100, parse.getInt("HP")));
        this.chiSo = parse.getInt("index");
        if (parse.containsKey("options")) {
            JSONArray jArr = parse.getJSONArray("options");
            if (jArr == null) {
                return;
            }
            for (int i = 0; i < jArr.size(); ++i) {
                if (!(jArr.get(i) instanceof JSONObject)) {
                    continue;
                }
                VXLDuLieuJson d = new VXLDuLieuJson((JSONObject)jArr.get(i));
                int ma = d.getInt("id");
                int thamSo = d.getInt("param");
                VXLThuocTinhVatPham thuocTinh = new VXLThuocTinhVatPham(ma, thamSo);
                if (thuocTinh.optionTemplate == null) {
                    continue;
                }
                this.itemOptions.add(thuocTinh);
            }
        }
        this.capNhatThongTinSocket();
        this.hoanTatDucLoNeuDenHan();
    }

    public JSONObject toJSONObject() {
        JSONObject doiTuong = new JSONObject();
        doiTuong.put("id", this.ma);
        doiTuong.put("quantity", this.soLuong);
        doiTuong.put("HP", this.HP);
        doiTuong.put("index", this.chiSo);
        JSONArray thuocTinhs = new JSONArray();
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham op = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (op == null || op.optionTemplate == null) {
                continue;
            }
            JSONObject option = new JSONObject();
            option.put("id", op.optionTemplate.ma);
            option.put("param", op.thamSo);
            thuocTinhs.add((Object)option);
        }
        doiTuong.put("options", thuocTinhs);
        return doiTuong;
    }

    public int getParamById(int ma) {
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham o = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (o.optionTemplate == null || o.optionTemplate.ma != ma) continue;
            return o.thamSo;
        }
        return -1;
    }

    public int tongThamSoTheoMa(int ma) {
        int tong = 0;
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (thuocTinh.optionTemplate != null && thuocTinh.optionTemplate.ma == ma) {
                tong += thuocTinh.thamSo;
            }
        }
        return tong;
    }

    public Vector layThuocTinhHieuLuc() {
        Vector thuocTinhs = new Vector();
        for (Object giaTri : this.itemOptions) {
            if (!(giaTri instanceof VXLThuocTinhVatPham thuocTinh)
                    || thuocTinh.optionTemplate == null) {
                continue;
            }
            thuocTinhs.add(new VXLThuocTinhVatPham(
                    thuocTinh.optionTemplate.ma, thuocTinh.thamSo));
        }
        thuocTinhs.addAll(VXLChiSoNgoc.layThuocTinh(this));
        return thuocTinhs;
    }

    public int tongThamSoHieuLucTheoMa(int ma) {
        return this.tongThamSoTheoMa(ma) + VXLChiSoNgoc.tongThamSo(this, ma);
    }

    public void datThamSoTheoMa(int ma, int thamSo) {
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (thuocTinh.optionTemplate != null && thuocTinh.optionTemplate.ma == ma) {
                thuocTinh.thamSo = thamSo;
                if (ma == 15 || ma == 16) {
                    this.capNhatThongTinSocket();
                }
                return;
            }
        }
        this.itemOptions.add(new VXLThuocTinhVatPham(ma, thamSo));
        if (ma == 15 || ma == 16) {
            this.capNhatThongTinSocket();
        }
    }

    public boolean themLoTrong() {
        if (!this.isTypeBody() || this.nSocket >= SO_LO_TOI_DA || this.isSocketing) {
            return false;
        }
        this.itemOptions.add(new VXLThuocTinhVatPham(MA_LO_NGOC, 0));
        this.capNhatThongTinSocket();
        return true;
    }

    public boolean batDauDucLo(long thoiLuongMillis) {
        if (!this.isTypeBody() || this.nSocket >= SO_LO_TOI_DA || this.isSocketing
                || thoiLuongMillis <= 0L) {
            return false;
        }
        long ketThucMillis = System.currentTimeMillis() + thoiLuongMillis;
        long ketThucGiay = ketThucMillis / 1000L;
        if (ketThucGiay > Integer.MAX_VALUE) {
            return false;
        }
        this.itemOptions.add(new VXLThuocTinhVatPham(MA_DANG_DUC_LO, (int)ketThucGiay));
        this.capNhatThongTinSocket();
        return true;
    }

    public boolean hoanTatDucLoNeuDenHan() {
        return this.isSocketing && this.socketFinishTime <= System.currentTimeMillis()
                && this.hoanTatDucLo();
    }

    public boolean hoanTatDucLoNgay() {
        return this.isSocketing && this.hoanTatDucLo();
    }

    public long layThoiGianDucLoConLaiMillis() {
        if (!this.isSocketing) {
            return 0L;
        }
        return Math.max(0L, this.socketFinishTime - System.currentTimeMillis());
    }

    public int laySoGioDucLoConLai() {
        long conLai = this.layThoiGianDucLoConLaiMillis();
        if (conLai <= 0L) {
            return 0;
        }
        return (int)Math.min(Short.MAX_VALUE, (conLai + MOT_GIO_MILLIS - 1L) / MOT_GIO_MILLIS);
    }

    public boolean dinhNgoc(int maNgoc) {
        if (maNgoc <= 0) {
            return false;
        }
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (thuocTinh.optionTemplate != null && thuocTinh.optionTemplate.ma == MA_LO_NGOC
                    && thuocTinh.thamSo == 0) {
                thuocTinh.thamSo = maNgoc;
                this.capNhatThongTinSocket();
                return true;
            }
        }
        return false;
    }

    public java.util.List<Integer> thaoTatCaNgoc() {
        java.util.ArrayList<Integer> cacMaNgoc = new java.util.ArrayList<>();
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (thuocTinh.optionTemplate != null && thuocTinh.optionTemplate.ma == MA_LO_NGOC
                    && thuocTinh.thamSo > 0) {
                cacMaNgoc.add(thuocTinh.thamSo);
                thuocTinh.thamSo = 0;
            }
        }
        this.capNhatThongTinSocket();
        return cacMaNgoc;
    }

    public java.util.List<Integer> layCacMaNgocDaDinh() {
        java.util.ArrayList<Integer> cacMaNgoc = new java.util.ArrayList<>();
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (thuocTinh.optionTemplate != null && thuocTinh.optionTemplate.ma == MA_LO_NGOC
                    && thuocTinh.thamSo > 0) {
                cacMaNgoc.add(thuocTinh.thamSo);
            }
        }
        return cacMaNgoc;
    }

    public void thayMau(VXLMauVatPham mauMoi) {
        if (mauMoi == null) {
            throw new IllegalArgumentException("Mẫu vật phẩm không hợp lệ.");
        }
        Vector thuocTinhBaoLuu = new Vector();
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (thuocTinh.optionTemplate == null) {
                continue;
            }
            int maThuocTinh = thuocTinh.optionTemplate.ma;
            if (maThuocTinh == 15 || maThuocTinh == 16 || maThuocTinh == 17 || maThuocTinh == 19 || maThuocTinh == 20) {
                thuocTinhBaoLuu.add(new VXLThuocTinhVatPham(maThuocTinh, thuocTinh.thamSo));
            }
        }
        Vector thuocTinhMoi = new Vector();
        for (int i = 0; i < mauMoi.thuocTinhs.size(); ++i) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)mauMoi.thuocTinhs.get(i);
            if (thuocTinh.optionTemplate != null) {
                thuocTinhMoi.add(new VXLThuocTinhVatPham(thuocTinh.optionTemplate.ma, thuocTinh.thamSo));
            }
        }
        thuocTinhMoi.addAll(thuocTinhBaoLuu);
        this.ma = mauMoi.ma;
        this.mau = mauMoi;
        this.itemOptions = thuocTinhMoi;
        this.HP = 100;
        this.capNhatThongTinSocket();
    }

    private void capNhatThongTinSocket() {
        this.nSocket = 0;
        this.nGem = 0;
        this.isSocketing = false;
        this.socketFinishTime = 0L;
        for (Object giaTri : this.itemOptions) {
            if (!(giaTri instanceof VXLThuocTinhVatPham thuocTinh)
                    || thuocTinh.optionTemplate == null) {
                continue;
            }
            if (thuocTinh.optionTemplate.ma == MA_DANG_DUC_LO) {
                this.isSocketing = true;
                this.socketFinishTime = (long)thuocTinh.thamSo * 1000L;
            } else if (thuocTinh.optionTemplate.ma == MA_LO_NGOC) {
                this.nSocket++;
                if (thuocTinh.thamSo != 0) {
                    this.nGem++;
                }
            }
        }
    }

    private boolean hoanTatDucLo() {
        boolean coTienTrinh = false;
        for (int i = this.itemOptions.size() - 1; i >= 0; --i) {
            Object giaTri = this.itemOptions.get(i);
            if (giaTri instanceof VXLThuocTinhVatPham thuocTinh
                    && thuocTinh.optionTemplate != null
                    && thuocTinh.optionTemplate.ma == MA_DANG_DUC_LO) {
                this.itemOptions.remove(i);
                coTienTrinh = true;
            }
        }
        if (!coTienTrinh) {
            return false;
        }
        if (this.nSocket < SO_LO_TOI_DA) {
            this.itemOptions.add(new VXLThuocTinhVatPham(MA_LO_NGOC, 0));
        }
        this.capNhatThongTinSocket();
        return true;
    }
}

