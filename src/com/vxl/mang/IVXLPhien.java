package com.vxl.mang;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.mang.IVXLDichVuGame;
import com.vxl.mang.IVXLXuLyTin;
import com.vxl.mang.VXLTinNhan;

public interface IVXLPhien {
    public boolean dangKetNoi();

    public void datBoXuLy(IVXLXuLyTin var1);

    public void guiTin(VXLTinNhan var1);

    public void datDichVu(IVXLDichVuGame var1);

    public void close();
}

