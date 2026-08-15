package com.vxl.chien;

public record VXLHoSoDan(String ten, byte loaiClient, KieuBan kieuBan,
        int soVienThuong, int soVienChiMang, double khoangLechGoc,
        VatLy vatLy,
        boolean xuyenDiaHinh, boolean xuyenNguoi,
        int phanTramSatThuongMoiVien, int tranPhanTramSatThuong,
        QuayVe quayVe, Tarzan tarzan) {

    public VXLHoSoDan(String ten, byte loaiClient, KieuBan kieuBan,
            int soVienThuong, int soVienChiMang, double khoangLechGoc,
            VatLy vatLy, boolean xuyenDiaHinh, boolean xuyenNguoi,
            int phanTramSatThuongMoiVien, int tranPhanTramSatThuong) {
        this(ten, loaiClient, kieuBan, soVienThuong, soVienChiMang, khoangLechGoc,
                vatLy, xuyenDiaHinh, xuyenNguoi, phanTramSatThuongMoiVien,
                tranPhanTramSatThuong, null, null);
    }

    public VXLHoSoDan(String ten, byte loaiClient, KieuBan kieuBan,
            int soVienThuong, int soVienChiMang, double khoangLechGoc,
            VatLy vatLy, boolean xuyenDiaHinh, boolean xuyenNguoi,
            int phanTramSatThuongMoiVien, int tranPhanTramSatThuong,
            QuayVe quayVe) {
        this(ten, loaiClient, kieuBan, soVienThuong, soVienChiMang, khoangLechGoc,
                vatLy, xuyenDiaHinh, xuyenNguoi, phanTramSatThuongMoiVien,
                tranPhanTramSatThuong, quayVe, null);
    }

    public record VatLy(double trongLuong, double heSoTrongLuc, double heSoGio,
            double heSoTocDoTheoKhung, double giaTocTrongLucTheoKhung,
            double heSoGioTheoKhung) {

        public VatLy(double trongLuong, double heSoTrongLuc, double heSoGio) {
            this(trongLuong, heSoTrongLuc, heSoGio,
                    Double.NaN, Double.NaN, Double.NaN);
        }

        public boolean dungVatLyTheoKhung() {
            return Double.isFinite(this.heSoTocDoTheoKhung)
                    && Double.isFinite(this.giaTocTrongLucTheoKhung)
                    && Double.isFinite(this.heSoGioTheoKhung);
        }
    }

    public record QuayVe(double tamBayCoBan, double tamBayTheoLuc,
            double thoiGianBayRaToiDa, double heSoTocDoQuayVe,
            double tocDoXoayDoMoiGiay, int banKinhThuVe) {
    }

    public record Tarzan(double giaTocNgoatBanDau, double giaTocNgoatLienTuc,
            int leNgoaiBanDo) {
    }

    public enum KieuBan {
        DAN_DON,
        DAN_KEP,
        DAN_CHUM,
        LIEN_THANH,
        DAN_TACH,
        VONG_TARZAN,
        QUAY_VE,
        MAGENTA,
        LASER,
        NHAN_VAT_LAO
    }

    public int laySoVien(byte chiMang) {
        return chiMang == 0 ? this.soVienThuong : this.soVienChiMang;
    }

    public int laySoVienGaySatThuong(byte chiMang) {
        int soVien = this.laySoVien(chiMang);
        if (Byte.toUnsignedInt(this.loaiClient) == 17) {
            return Math.max(1, soVien - 1);
        }
        return Math.max(1, soVien);
    }

    public int tinhSatThuongMoiVien(int tongSatThuong, byte chiMang) {
        int tong = Math.max(1, tongSatThuong);
        return Math.max(1, (tong * this.phanTramSatThuongMoiVien + 99) / 100);
    }

    public double[] taoDoLechGoc(byte chiMang) {
        int soVien = this.laySoVien(chiMang);
        double[] ketQua = new double[soVien];
        if (this.khoangLechGoc <= 0D) {
            return ketQua;
        }
        double tam = (soVien - 1) / 2D;
        for (int i = 0; i < soVien; i++) {
            ketQua[i] = (i - tam) * this.khoangLechGoc;
        }
        return ketQua;
    }

    public boolean dungTrongLuc() {
        return this.vatLy.dungVatLyTheoKhung()
                ? this.vatLy.giaTocTrongLucTheoKhung() > 0D
                : this.vatLy.heSoTrongLuc() > 0D;
    }

    public boolean dungGio() {
        return this.vatLy.dungVatLyTheoKhung()
                ? this.vatLy.heSoGioTheoKhung() > 0D
                : this.vatLy.heSoGio() > 0D;
    }

    public double trongLuong() {
        return this.vatLy.trongLuong();
    }

    public double heSoTrongLuc() {
        return this.vatLy.heSoTrongLuc();
    }

    public double heSoGio() {
        return this.vatLy.heSoGio();
    }
}
