package com.vxl.vatpham;

// Vũ Xuân Lâm đẹp trai VCL
import java.util.Vector;

public class VXLMauVatPham {
    public short ma;
    public byte loai;
    public byte gioiTinh;
    public String ten;
    public String[] subName;
    public String moTa;
    public byte cap;
    public short iconID;
    public short part;
    public boolean isUpToUp;
    public boolean noTrade;
    public int strRequire;
    public Vector thuocTinhs;
    public int buyGold;
    public int buyGem;

    public VXLMauVatPham(short templateID, byte loai, byte gioiTinh, String ten, String moTa, byte cap, int strRequire, short iconID, short part, boolean isUpToUp) {
        this.ma = templateID;
        this.loai = loai;
        this.gioiTinh = gioiTinh;
        this.ten = ten;
        this.moTa = moTa;
        this.cap = cap;
        this.strRequire = strRequire;
        this.iconID = iconID;
        this.part = part;
        this.isUpToUp = isUpToUp;
        this.noTrade = false;
        this.thuocTinhs = new Vector();
    }
}

