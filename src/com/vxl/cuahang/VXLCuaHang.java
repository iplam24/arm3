package com.vxl.cuahang;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.cuahang.VXLTrang;
import java.util.ArrayList;

public class VXLCuaHang {
    public static final VXLCuaHang SHOP_EQUIP = new VXLCuaHang();
    public static final VXLCuaHang SHOP_ITEM = new VXLCuaHang();
    public static final int MAX_NUMBER_IN_PAGE = 20;
    public ArrayList<ArrayList<VXLTrang>> tabs = new ArrayList();
    public byte typeShop;
    public ArrayList<String> shopTabName = new ArrayList();

    public void datLoaiCuaHang(byte loai) {
        this.typeShop = loai;
    }

    public void themTab(String tabName, ArrayList<VXLMauVatPham> vatPhams) {
        this.shopTabName.add(tabName);
        ArrayList<VXLTrang> tab = new ArrayList<VXLTrang>();
        int num = vatPhams.size();
        int t = 0;
        while (num > 0) {
            int temp;
            int n = num > 20 ? 20 : num;
            VXLTrang page = new VXLTrang();
            for (int i = temp = t * 20; i < temp + n; ++i) {
                page.vatPhams.add(vatPhams.get(i));
            }
            tab.add(page);
            num -= n;
            ++t;
        }
        this.tabs.add(tab);
    }
}

