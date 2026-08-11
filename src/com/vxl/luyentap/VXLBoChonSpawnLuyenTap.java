package com.vxl.luyentap;

import com.vxl.bando.VXLQuanLyBanDo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

final class VXLBoChonSpawnLuyenTap {
    private static final int LE_BIEN_TOI_THIEU = 70;
    private static final int BUOC_QUET = 24;
    private static final int CHIEU_CAO_NHAN_VAT = 35;
    private static final int NUA_RONG_NHAN_VAT = 8;
    private static final int CHENH_CAO_TOI_DA = 170;

    static final class KetQua {
        final short nguoiChoiX;
        final short nguoiChoiY;
        final short phienQuanX;
        final short phienQuanY;

        private KetQua(Diem nguoiChoi, Diem phienQuan) {
            this.nguoiChoiX = nguoiChoi.x;
            this.nguoiChoiY = nguoiChoi.y;
            this.phienQuanX = phienQuan.x;
            this.phienQuanY = phienQuan.y;
        }
    }

    private static final class Diem {
        private final short x;
        private final short y;

        private Diem(short x, short y) {
            this.x = x;
            this.y = y;
        }
    }

    private VXLBoChonSpawnLuyenTap() {
    }

    static KetQua chon(VXLQuanLyBanDo banDo, boolean tranhLap,
            short nguoiChoiXTruoc, short phienQuanXTruoc) {
        List<Diem> cacDiem = quetDiemAnToan(banDo);
        if (cacDiem.size() < 2) {
            return chonTheoDiemSinhBanDo(banDo, tranhLap,
                    nguoiChoiXTruoc, phienQuanXTruoc);
        }
        Collections.shuffle(cacDiem, ThreadLocalRandom.current());
        int khoangCachToiThieu = Math.min(340, Math.max(220, banDo.getWidth() / 4));
        KetQua duPhong = null;
        for (Diem nguoiChoi : cacDiem) {
            for (Diem phienQuan : cacDiem) {
                if (nguoiChoi == phienQuan
                        || Math.abs(nguoiChoi.x - phienQuan.x) < khoangCachToiThieu
                        || Math.abs(nguoiChoi.y - phienQuan.y) > CHENH_CAO_TOI_DA) {
                    continue;
                }
                KetQua ketQua = new KetQua(nguoiChoi, phienQuan);
                if (duPhong == null) {
                    duPhong = ketQua;
                }
                if (!tranhLap || nguoiChoi.x != nguoiChoiXTruoc
                        || phienQuan.x != phienQuanXTruoc) {
                    return ketQua;
                }
            }
        }
        if (duPhong != null) {
            return duPhong;
        }
        return chonCapXaNhat(cacDiem, banDo, tranhLap,
                nguoiChoiXTruoc, phienQuanXTruoc);
    }

    private static List<Diem> quetDiemAnToan(VXLQuanLyBanDo banDo) {
        List<Diem> ketQua = new ArrayList<>();
        int chieuRong = banDo.getWidth();
        int leBien = Math.max(LE_BIEN_TOI_THIEU, Math.min(120, chieuRong / 10));
        int xBatDau = leBien + ThreadLocalRandom.current().nextInt(BUOC_QUET);
        for (int x = xBatDau; x <= chieuRong - 1 - leBien; x += BUOC_QUET) {
            short xNgan = (short)x;
            short y = banDo.timViTriDat(xNgan, (short)0);
            if (laDiemAnToan(banDo, x, y)) {
                ketQua.add(new Diem(xNgan, y));
            }
        }
        return ketQua;
    }

    private static boolean laDiemAnToan(VXLQuanLyBanDo banDo, int x, int y) {
        if (y < CHIEU_CAO_NHAN_VAT + 8 || y >= banDo.getHeight() - 35
                || !banDo.coVaCham((short)x, (short)(y + 1))) {
            return false;
        }
        int[] cacLechX = new int[]{-NUA_RONG_NHAN_VAT, 0, NUA_RONG_NHAN_VAT};
        int[] cacLechY = new int[]{-1, -CHIEU_CAO_NHAN_VAT / 2, -CHIEU_CAO_NHAN_VAT};
        for (int lechX : cacLechX) {
            int xKiemTra = x + lechX;
            if (xKiemTra < 0 || xKiemTra >= banDo.getWidth()) {
                return false;
            }
            short yDat = banDo.timViTriDat((short)xKiemTra,
                    (short)Math.max(0, y - 6));
            if (Math.abs(yDat - y) > 7) {
                return false;
            }
            for (int lechY : cacLechY) {
                if (banDo.coVaCham((short)xKiemTra, (short)(y + lechY))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static KetQua chonCapXaNhat(List<Diem> cacDiem, VXLQuanLyBanDo banDo,
            boolean tranhLap, short nguoiChoiXTruoc, short phienQuanXTruoc) {
        KetQua totNhat = null;
        int xaNhat = -1;
        for (Diem nguoiChoi : cacDiem) {
            for (Diem phienQuan : cacDiem) {
                int khoangCach = Math.abs(nguoiChoi.x - phienQuan.x);
                if (nguoiChoi == phienQuan || khoangCach <= xaNhat
                        || tranhLap && nguoiChoi.x == nguoiChoiXTruoc
                        && phienQuan.x == phienQuanXTruoc) {
                    continue;
                }
                xaNhat = khoangCach;
                totNhat = new KetQua(nguoiChoi, phienQuan);
            }
        }
        return totNhat != null ? totNhat
                : chonTheoDiemSinhBanDo(banDo, tranhLap,
                        nguoiChoiXTruoc, phienQuanXTruoc);
    }

    private static KetQua chonTheoDiemSinhBanDo(VXLQuanLyBanDo banDo,
            boolean tranhLap, short nguoiChoiXTruoc, short phienQuanXTruoc) {
        ThreadLocalRandom ngauNhien = ThreadLocalRandom.current();
        KetQua duPhong = null;
        for (int lan = 0; lan < 16; lan++) {
            int chiSoNguoiChoi = ngauNhien.nextInt(8);
            int chiSoPhienQuan = (chiSoNguoiChoi + 1 + ngauNhien.nextInt(7)) % 8;
            Diem nguoiChoi = new Diem(banDo.laySinhX(chiSoNguoiChoi),
                    banDo.laySinhY(chiSoNguoiChoi));
            Diem phienQuan = new Diem(banDo.laySinhX(chiSoPhienQuan),
                    banDo.laySinhY(chiSoPhienQuan));
            if (nguoiChoi.x == phienQuan.x) {
                continue;
            }
            KetQua ketQua = new KetQua(nguoiChoi, phienQuan);
            if (duPhong == null) {
                duPhong = ketQua;
            }
            if (!tranhLap || nguoiChoi.x != nguoiChoiXTruoc
                    || phienQuan.x != phienQuanXTruoc) {
                return ketQua;
            }
        }
        if (duPhong != null) {
            return duPhong;
        }
        Diem nguoiChoi = new Diem((short)220,
                banDo.timViTriDat((short)220, (short)250));
        Diem phienQuan = new Diem((short)600,
                banDo.timViTriDat((short)600, (short)250));
        return new KetQua(nguoiChoi, phienQuan);
    }
}