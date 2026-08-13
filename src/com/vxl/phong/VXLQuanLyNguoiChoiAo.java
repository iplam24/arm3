package com.vxl.phong;

import com.vxl.chien.VXLQuanLyChien;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLMauVatPham;
import java.io.IOException;
import java.util.Arrays;

final class VXLQuanLyNguoiChoiAo {
    private static final int GIOI_HAN_CHI_SO_BOT = 30000;
    private static final int ELO_MOI_BAC = 3;
    private static final int MAU_BOT_MAC_DINH = 1000;
    private static final int MAU_TANG_MOI_BAC = 100;
    private static final int TAN_CONG_BOT_MAC_DINH = 35;
    private static final int TAN_CONG_TANG_MOI_BAC = 4;
    private static final int GIAP_BOT_MAC_DINH = 15;
    private static final int GIAP_TANG_MOI_BAC = 2;
    private static final String[] TEN_BOT_SOLO = new String[]{"Iron Bot", "Hulk Bot", "Thor Bot", "Captain Bot", "Ultron Bot"};
    private static final byte[] AVENGER_BOT_SOLO = new byte[]{1, 2, 3, 5, 8};
    private static final short[] VU_KHI_BOT_SOLO = new short[]{5, 27, 54, 55, 58};
    private static final int[] MA_MAU_TOC_BOT = new int[]{0, 1, 2, 3, 4};
    private static final short[] PART_TOC_DU_PHONG = new short[]{0, 6, 11, 16, 21};
    private final VXLNguoiChoi[] nguoiChois;
    private final int[] maNguoiChoiAo;

    VXLQuanLyNguoiChoiAo(VXLNguoiChoi[] nguoiChois) {
        this.nguoiChois = nguoiChois;
        this.maNguoiChoiAo = new int[nguoiChois.length];
    }

    void boSungChoTran(VXLQuanLyChien tranDau, boolean cheDoCamTu, VXLNguoiChoi chuPhong,
            VXLPhong phong, byte maBan, int gioiHanNguoiChoi) throws IOException {
        if (cheDoCamTu) {
            return;
        }
        this.themBotTheoElo(tranDau, chuPhong, phong, maBan, gioiHanNguoiChoi);
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

    private void themBotTheoElo(VXLQuanLyChien tranDau, VXLNguoiChoi chuPhong, VXLPhong phong,
            byte maBan, int gioiHanNguoiChoi) throws IOException {
        int eloCaoNhat = this.layEloCaoNhat();
        if (eloCaoNhat < 0) {
            return;
        }
        int bacElo = eloCaoNhat / ELO_MOI_BAC;
        int mauToiDa = gioiHanChiSo(MAU_BOT_MAC_DINH + (long)bacElo * MAU_TANG_MOI_BAC);
        int tanCong = gioiHanChiSo(TAN_CONG_BOT_MAC_DINH + (long)bacElo * TAN_CONG_TANG_MOI_BAC);
        int giap = gioiHanChiSo(GIAP_BOT_MAC_DINH + (long)bacElo * GIAP_TANG_MOI_BAC);
        boolean[] daDung = new boolean[this.nguoiChois.length];
        for (int i = 0; i < TEN_BOT_SOLO.length; i++) {
            int o = this.timOTrong(daDung, gioiHanNguoiChoi);
            if (o < 0) {
                return;
            }
            VXLNguoiChoi bot = this.taoNguoiChoiAo(-9000 - o, TEN_BOT_SOLO[i],
                    VU_KHI_BOT_SOLO[i], AVENGER_BOT_SOLO[i], o, i, chuPhong);
            bot.cup = eloCaoNhat;
            this.maNguoiChoiAo[o] = bot.ma;
            this.phatNguoiChoiAo(bot, chuPhong, phong, maBan);
            tranDau.themBot((byte)o, bot.ten, bot.wp, bot.avenger, mauToiDa, tanCong, giap);
            daDung[o] = true;
        }
    }

    private VXLNguoiChoi taoNguoiChoiAo(int ma, String ten, short vuKhi, byte avenger,
            int o, int mauBot, VXLNguoiChoi chuPhong) {
        VXLNguoiChoi nguoiChoiAo = new VXLNguoiChoi(null);
        nguoiChoiAo.ma = ma;
        nguoiChoiAo.ten = ten;
        nguoiChoiAo.kinhNghiem = 1000;
        nguoiChoiAo.clan = -1;
        nguoiChoiAo.head = this.layPartToc(mauBot);
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

    private int layEloCaoNhat() {
        int eloCaoNhat = -1;
        for (VXLNguoiChoi nguoiChoi : this.nguoiChois) {
            if (nguoiChoi != null) {
                eloCaoNhat = Math.max(eloCaoNhat, Math.max(0, nguoiChoi.cup));
            }
        }
        return eloCaoNhat;
    }

    private short layPartToc(int mauBot) {
        int chiSo = Math.floorMod(mauBot, MA_MAU_TOC_BOT.length);
        if (VXLQuanLyMayChu.itemTemplates != null) {
            VXLMauVatPham mauToc = VXLQuanLyMayChu.itemTemplates.get(MA_MAU_TOC_BOT[chiSo]);
            if (mauToc != null && mauToc.part >= 0) {
                return mauToc.part;
            }
        }
        return PART_TOC_DU_PHONG[chiSo];
    }

    private int timOTrong(boolean[] daDung, int gioiHanNguoiChoi) {
        int gioiHan = Math.min(Math.max(0, gioiHanNguoiChoi), this.nguoiChois.length);
        for (int i = 0; i < gioiHan; i++) {
            if (this.nguoiChois[i] == null && !daDung[i]) {
                return i;
            }
        }
        return -1;
    }

    private static int gioiHanChiSo(long giaTri) {
        return (int)Math.max(0, Math.min(GIOI_HAN_CHI_SO_BOT, giaTri));
    }
}
