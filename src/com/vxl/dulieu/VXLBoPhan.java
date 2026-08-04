package com.vxl.dulieu;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.dulieu.VXLAnhBoPhan;

public class VXLBoPhan {
    public VXLAnhBoPhan[] pi;
    public int loai;

    public VXLBoPhan(int loai) {
        this.loai = loai;
        if (loai == 0) {
            this.pi = new VXLAnhBoPhan[4];
        }
        if (loai == 1) {
            this.pi = new VXLAnhBoPhan[10];
        }
        if (loai == 2) {
            this.pi = new VXLAnhBoPhan[10];
        }
        if (loai == 3) {
            this.pi = new VXLAnhBoPhan[7];
        }
        if (loai == 4) {
            this.pi = new VXLAnhBoPhan[2];
        }
        if (loai == 5) {
            this.pi = new VXLAnhBoPhan[1];
        }
    }
}

