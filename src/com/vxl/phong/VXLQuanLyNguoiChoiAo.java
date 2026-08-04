package com.vxl.phong;

import com.vxl.chien.VXLQuanLyChien;
import com.vxl.mohinh.VXLNguoiChoi;
import java.io.IOException;
import java.util.Arrays;

final class VXLQuanLyNguoiChoiAo {
    private static final String[] TEN_BOT_SOLO = new String[]{"Iron Bot", "Hulk Bot", "Thor Bot", "Captain Bot", "Ultron Bot"};
    private static final byte[] AVENGER_BOT_SOLO = new byte[]{1, 2, 3, 5, 8};
    private static final short[] VU_KHI_BOT_SOLO = new short[]{5, 27, 54, 55, 58};
    private final VXLNguoiChoi[] nguoiChois;
    private final int[] maNguoiChoiAo;

    VXLQuanLyNguoiChoiAo(VXLNguoiChoi[] nguoiChois) {
        this.nguoiChois = nguoiChois;
        this.maNguoiChoiAo = new int[nguoiChois.length];
    }

    void boSungChoTran(VXLQuanLyChien tranDau, boolean cheDoCamTu, VXLNguoiChoi chuPhong, VXLPhong phong, byte maBan) throws IOException {
        if (cheDoCamTu) {
            this.themCamTu(tranDau, chuPhong, phong, maBan);
            return;
        }
        this.themBotSolo(tranDau, chuPhong, phong, maBan);
    }

    void xoa(VXLNguoiChoi chuPhong) {
        int maChuPhong = chuPhong != null ? chuPhong.ma : -1;
        for (int maAo : this.maNguoiChoiAo) {
            if (maAo == 0) {
                continue;
            }
            for (VXLNguoiChoi nguoiChoi : this.nguoiChois) {
                if (nguoiChoi != null && nguoiChoi.dichVu != null) {
                    nguoiChoi.dichVu.guiRoiDau(maAo, maChuPhong);
                }
            }
        }
        this.datLai();
    }

    void datLai() {
        Arrays.fill(this.maNguoiChoiAo, 0);
    }

    private void themBotSolo(VXLQuanLyChien tranDau, VXLNguoiChoi chuPhong, VXLPhong phong, byte maBan) throws IOException {
        if (this.demNguoiChoiThat() != 1) {
            return;
        }
        boolean[] daDung = new boolean[this.nguoiChois.length];
        for (int i = 0; i < TEN_BOT_SOLO.length; i++) {
            int o = this.timOTrong(daDung);
            if (o < 0) {
                return;
            }
            VXLNguoiChoi bot = this.taoNguoiChoiAo(-9000 - o, TEN_BOT_SOLO[i], VU_KHI_BOT_SOLO[i], AVENGER_BOT_SOLO[i], o, chuPhong);
            this.maNguoiChoiAo[o] = bot.ma;
            this.phatNguoiChoiAo(bot, chuPhong, phong, maBan);
            tranDau.themBot((byte)o, bot.ten, bot.wp, bot.avenger);
            daDung[o] = true;
        }
    }

    private void themCamTu(VXLQuanLyChien tranDau, VXLNguoiChoi chuPhong, VXLPhong phong, byte maBan) throws IOException {
        boolean[] daDung = new boolean[this.nguoiChois.length];
        int thuTu = 1;
        while (true) {
            int o = this.timOTrong(daDung);
            if (o < 0) {
                return;
            }
            String ten = "Cảm tử " + thuTu++;
            VXLNguoiChoi camTu = this.taoNguoiChoiAo(-9500 - o, ten, (short)58, (byte)0, o, chuPhong);
            this.maNguoiChoiAo[o] = camTu.ma;
            this.phatNguoiChoiAo(camTu, chuPhong, phong, maBan);
            tranDau.themCamTu((byte)o, camTu.ten, camTu.wp, camTu.avenger);
            daDung[o] = true;
        }
    }

    private VXLNguoiChoi taoNguoiChoiAo(int ma, String ten, short vuKhi, byte avenger, int o, VXLNguoiChoi chuPhong) {
        VXLNguoiChoi nguoiChoiAo = new VXLNguoiChoi(null);
        nguoiChoiAo.ma = ma;
        nguoiChoiAo.ten = ten;
        nguoiChoiAo.kinhNghiem = 1000;
        nguoiChoiAo.clan = -1;
        nguoiChoiAo.head = chuPhong != null ? chuPhong.head : 0;
        nguoiChoiAo.leg = chuPhong != null ? chuPhong.leg : 10;
        nguoiChoiAo.body = chuPhong != null ? chuPhong.body : 35;
        nguoiChoiAo.hat = chuPhong != null ? chuPhong.hat : 60;
        nguoiChoiAo.wing = chuPhong != null ? chuPhong.wing : 0;
        nguoiChoiAo.wp = vuKhi;
        nguoiChoiAo.avenger = avenger;
        nguoiChoiAo.chiSo = o;
        nguoiChoiAo.pointSeat = (byte)o;
        return nguoiChoiAo;
    }

    private void phatNguoiChoiAo(VXLNguoiChoi nguoiChoiAo, VXLNguoiChoi chuPhong, VXLPhong phong, byte maBan) throws IOException {
        for (VXLNguoiChoi nguoiChoi : this.nguoiChois) {
            if (nguoiChoi != null && nguoiChoi.dichVu != null) {
                nguoiChoi.dichVu.guiNguoiChoiVaoDau(nguoiChoiAo, chuPhong, phong.ma, maBan);
            }
        }
    }

    private int demNguoiChoiThat() {
        int soLuong = 0;
        for (VXLNguoiChoi nguoiChoi : this.nguoiChois) {
            if (nguoiChoi != null) {
                soLuong++;
            }
        }
        return soLuong;
    }

    private int timOTrong(boolean[] daDung) {
        for (int i = 0; i < this.nguoiChois.length; i++) {
            if (this.nguoiChois[i] == null && !daDung[i]) {
                return i;
            }
        }
        return -1;
    }
}