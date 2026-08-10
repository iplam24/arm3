package com.vxl.tienich;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.dulieu.VXLTieuDeCap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Random;

public class VXLTienIch {
    public static final int KINH_NGHIEM_CAP_MOT_MAC_DINH = 10214;
    private static final long K = 1024L;
    private static final long M = 0x100000L;
    private static final long G = 0x40000000L;
    private static final long T = 0x10000000000L;
    public static Random ngauNhien = new Random();

    public static String doiThanhChuoiRutGon(long giaTri) {
        long[] dividers = new long[]{0x10000000000L, 0x40000000L, 0x100000L, 1024L, 1L};
        String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};
        if (giaTri < 0L) {
            throw new IllegalArgumentException("Invalid file size: " + giaTri);
        }
        String ketQua = null;
        for (int i = 0; i < dividers.length; ++i) {
            long soChia = dividers[i];
            if (giaTri < soChia) continue;
            ketQua = VXLTienIch.format(giaTri, soChia, units[i]);
            break;
        }
        return ketQua == null ? "0B" : ketQua;
    }

    private static String format(long giaTri, long soChia, String donVi) {
        double ketQua = soChia > 1L ? (double)giaTri / (double)soChia : (double)giaTri;
        return new DecimalFormat("#,##0.#").format(ketQua) + donVi;
    }

    public static int getShort(int off, byte[] duLieu) {
        return VXLTienIch.byteSangInt(duLieu[off]) << 8 | VXLTienIch.byteSangInt(duLieu[off + 1]);
    }

    public static boolean trongVung(int x, int y, int x0, int y0, int w, int h) {
        return x >= x0 && x < x0 + w && y >= y0 && y < y0 + h;
    }

    public static boolean giaoVung(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 + w1 >= x2 && x1 <= x2 + w2 && y1 + h1 >= y2 && y1 <= y2 + h2;
    }

    public static boolean khongTrongSuat(int rgb) {
        return rgb >> 24 != 0;
    }

    public static int byteSangInt(byte b) {
        return b & 0xFF;
    }

    public static int layCap(int kinhNghiem) {
        int cap = 0;
        for (java.util.Map.Entry<Integer, VXLTieuDeCap> muc : VXLTieuDeCap.levels.entrySet()) {
            VXLTieuDeCap tieuDeCap = muc.getValue();
            if (tieuDeCap == null || kinhNghiem < tieuDeCap.kinhNghiem) continue;
            cap = Math.max(cap, muc.getKey());
        }
        return cap;
    }

    public static int layKinhNghiemCapMot() {
        VXLTieuDeCap capMot = VXLTieuDeCap.levels.get(1);
        if (capMot != null && capMot.kinhNghiem > 0) {
            return capMot.kinhNghiem;
        }
        int mocThapNhat = Integer.MAX_VALUE;
        for (VXLTieuDeCap tieuDeCap : VXLTieuDeCap.levels.values()) {
            if (tieuDeCap != null && tieuDeCap.kinhNghiem > 0) {
                mocThapNhat = Math.min(mocThapNhat, tieuDeCap.kinhNghiem);
            }
        }
        return mocThapNhat == Integer.MAX_VALUE ? KINH_NGHIEM_CAP_MOT_MAC_DINH : mocThapNhat;
    }

    public static int chuanHoaKinhNghiemKhoiTao(int kinhNghiem) {
        return Math.max(layKinhNghiemCapMot(), kinhNghiem);
    }

    public static byte[] layTep(String url) {
        try {
            return Files.readAllBytes(Path.of(url));
        }
        catch (IOException | RuntimeException ex) {
            return null;
        }
    }

    public static boolean trongHinhChuNhat(int x, int y, int xrect, int yrect, int chieuRong, int chieuCao) {
        return x >= xrect && x < xrect + chieuRong && y >= yrect && y < yrect + chieuCao;
    }

    public static boolean laDat(int mau) {
        return mau != 0xFFFFFF;
    }

    public static String dinhDangTien(long m) {
        String noiDung = "";
        long num = m / 1000L + 1L;
        int num2 = 0;
        while ((long)num2 < num) {
            if (m < 1000L) {
                noiDung = m + noiDung;
                break;
            }
            long num3 = m % 1000L;
            if (num3 == 0L) {
                noiDung = ".000" + noiDung;
            } else if (num3 < 10L) {
                noiDung = ".00" + num3 + noiDung;
            } else if (num3 < 100L) {
                noiDung = ".0" + num3 + noiDung;
            } else {
                noiDung = "." + num3 + noiDung;
            }
            m /= 1000L;
            ++num2;
        }
        return noiDung;
    }

    public static void luuTep(String url, byte[] ab) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int soNgauNhien(int nhoNhat, int lonNhat) {
        if (nhoNhat >= lonNhat) {
            return lonNhat;
        }
        return nhoNhat + ngauNhien.nextInt(lonNhat - nhoNhat);
    }
}
