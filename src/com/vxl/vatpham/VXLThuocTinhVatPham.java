package com.vxl.vatpham;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLMauThuocTinhVatPham;

public class VXLThuocTinhVatPham {
    public byte kichHoat;
    public boolean isCompareOption;
    public int num;
    public VXLMauThuocTinhVatPham optionTemplate;
    public int thamSo;

    public VXLThuocTinhVatPham(int optionTemplateId, int thamSo) {
        this.thamSo = thamSo;
        this.optionTemplate = VXLQuanLyMayChu.iOptionTemplates.get(optionTemplateId);
    }
}

