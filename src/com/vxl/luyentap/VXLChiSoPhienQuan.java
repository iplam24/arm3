package com.vxl.luyentap;

final class VXLChiSoPhienQuan {
    final int mauToiDa;
    final int tanCong;
    final int giap;
    final short head;
    final short leg;
    final short body;
    final short hat;
    final short wing;
    final short vuKhi;
    final String tenVuKhi;

    VXLChiSoPhienQuan(int mauToiDa, int tanCong, int giap, short head, short leg,
            short body, short hat, short wing, short vuKhi, String tenVuKhi) {
        this.mauToiDa = mauToiDa;
        this.tanCong = tanCong;
        this.giap = giap;
        this.head = head;
        this.leg = leg;
        this.body = body;
        this.hat = hat;
        this.wing = wing;
        this.vuKhi = vuKhi;
        this.tenVuKhi = tenVuKhi;
    }
}