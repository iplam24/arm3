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
        this.mau = VXLQuanLyMayChu.itemTemplates.get(ma);
        this.HP = 100;
        this.soLuong = 1;
        this.nSocket = 0;
        this.isSocketing = false;
    }

    public VXLVatPham(JSONObject doiTuong) {
        this.tai(doiTuong);
    }

    public boolean isTypeBody() {
        return this.mau.loai >= 0 && this.mau.loai < 6;
    }

    public void tai(JSONObject doiTuong) {
        VXLDuLieuJson parse = new VXLDuLieuJson(doiTuong);
        this.ma = parse.getInt("id");
        this.mau = VXLQuanLyMayChu.itemTemplates.get(this.ma);
        this.soLuong = parse.getInt("quantity");
        this.HP = parse.getInt("HP");
        this.chiSo = parse.getByte("index");
        if (parse.containsKey("options")) {
            JSONArray jArr = parse.getJSONArray("options");
            for (int i = 0; i < jArr.size(); ++i) {
                VXLDuLieuJson d = new VXLDuLieuJson((JSONObject)jArr.get(i));
                int ma = d.getInt("id");
                int thamSo = d.getInt("param");
                if (ma == 15) {
                    this.isSocketing = true;
                    this.socketFinishTime = thamSo * 1000;
                } else if (ma == 16) {
                    this.nSocket = (byte)(this.nSocket + 1);
                    if (thamSo != 0) {
                        ++this.nGem;
                    }
                }
                this.itemOptions.add(new VXLThuocTinhVatPham(ma, thamSo));
            }
        }
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

    public void datThamSoTheoMa(int ma, int thamSo) {
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            VXLThuocTinhVatPham thuocTinh = (VXLThuocTinhVatPham)this.itemOptions.get(i);
            if (thuocTinh.optionTemplate != null && thuocTinh.optionTemplate.ma == ma) {
                thuocTinh.thamSo = thamSo;
                return;
            }
        }
        this.itemOptions.add(new VXLThuocTinhVatPham(ma, thamSo));
    }

    public void thayMau(VXLMauVatPham mauMoi) {
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
    }
}

