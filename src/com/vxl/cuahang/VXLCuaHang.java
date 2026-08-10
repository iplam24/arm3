package com.vxl.cuahang;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.cuahang.VXLTrang;
import java.util.ArrayList;

public class VXLCuaHang {
    public static final VXLCuaHang SHOP_EQUIP = new VXLCuaHang();
    public static final VXLCuaHang SHOP_ITEM = new VXLCuaHang();
    public static final int MAX_NUMBER_IN_PAGE = 20;
    public ArrayList<ArrayList<VXLTrang>> tabs = new ArrayList<>();
    public byte typeShop;
    public ArrayList<String> shopTabName = new ArrayList<>();

    public void datLoaiCuaHang(byte loai) {
        this.typeShop = loai;
    }

    public void themTab(String tabName, ArrayList<VXLMauVatPham> vatPhams) {
        this.shopTabName.add(tabName == null ? "" : tabName);
        ArrayList<VXLTrang> tab = new ArrayList<VXLTrang>();
        if (vatPhams == null || vatPhams.isEmpty()) {
            this.tabs.add(tab);
            return;
        }
        ArrayList<VXLMauVatPham> vatPhamsHopLe = new ArrayList<>();
        for (VXLMauVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                vatPhamsHopLe.add(vatPham);
            }
        }
        int num = vatPhamsHopLe.size();
        int t = 0;
        while (num > 0) {
            int temp;
            int n = Math.min(MAX_NUMBER_IN_PAGE, num);
            VXLTrang page = new VXLTrang();
            for (int i = temp = t * MAX_NUMBER_IN_PAGE; i < temp + n; ++i) {
                page.vatPhams.add(vatPhamsHopLe.get(i));
            }
            tab.add(page);
            num -= n;
            ++t;
        }
        this.tabs.add(tab);
    }
}

