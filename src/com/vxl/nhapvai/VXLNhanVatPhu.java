package com.vxl.nhapvai;

// Vũ Xuân Lâm đẹp trai VCL
public class VXLNhanVatPhu {
    public static final byte BINH_KHI = 0;
    public static final byte PHONG_CU = 1;
    public static final byte TRANG_SUC = 2;
    public static final byte DUOC_PHAM = 3;
    public static final byte TAP_HOA = 4;
    public static final byte THU_KHO = 5;
    public static final byte DA_LUYEN = 6;
    public int npcId;
    public short templateId;
    public short anhDaiDien;
    public byte trangThai;
    public short x;
    public short y;
    public short head;
    public short body;
    public short leg;
    public short wp;
    public String ten;

    public VXLNhanVatPhu(int npcId, int trangThai, short cx, short cy, byte templateId, short anhDaiDien, short head1, short body1, short leg1) {
        this.npcId = npcId;
        this.trangThai = (byte)trangThai;
        this.anhDaiDien = anhDaiDien;
        this.x = cx;
        this.y = cy;
        this.templateId = templateId;
        switch (npcId) {
            case 0: {
                this.ten = "Quân nhu";
                break;
            }
            case 1: {
                this.ten = "Tình báo";
                break;
            }
            case 2: {
                this.ten = "Đội trưởng";
                break;
            }
            case 3: {
                this.ten = "Đại úy";
                break;
            }
            default: {
                this.ten = "Nhân vật phụ";
                break;
            }
        }
        this.head = head1;
        this.body = body1;
        this.leg = leg1;
        this.wp = (short)-1;
    }
}

