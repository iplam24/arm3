package com.vxl.xephang;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLMauVatPham;

public record VXLHinhDangNguoiChoi(short dau, short mu, short than, short chan, short canh, short vuKhi) {
    public static VXLHinhDangNguoiChoi tuNguoiChoi(VXLNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return rong();
        }
        return new VXLHinhDangNguoiChoi(nguoiChoi.head, nguoiChoi.hat, nguoiChoi.body,
                nguoiChoi.leg, nguoiChoi.wing, nguoiChoi.wp);
    }

    public static VXLHinhDangNguoiChoi tuJson(String equippedJson) {
        short dau = -1;
        short mu = -1;
        short than = -1;
        short chan = -1;
        short canh = -1;
        short vuKhi = -1;
        try {
            JSONArray trangBi = JSON.parseArray(equippedJson == null || equippedJson.isBlank() ? "[]" : equippedJson);
            for (int i = 0; i < trangBi.size(); i++) {
                JSONObject vatPham = trangBi.getJSONObject(i);
                if (vatPham == null) {
                    continue;
                }
                int ma = vatPham.getIntValue("id");
                if (ma == 413) {
                    dau = 278;
                    than = 279;
                    chan = 280;
                    vuKhi = 283;
                    mu = -1;
                    canh = -1;
                    continue;
                }
                short[] avenger = layAvenger(ma);
                if (avenger != null) {
                    dau = avenger[0];
                    than = avenger[1];
                    chan = avenger[2];
                    mu = -1;
                    canh = -1;
                    vuKhi = -1;
                    continue;
                }
                VXLMauVatPham mau = VXLQuanLyMayChu.itemTemplates == null
                        ? null : VXLQuanLyMayChu.itemTemplates.get(ma);
                if (mau == null) {
                    continue;
                }
                switch (mau.loai) {
                    case 0 -> dau = mau.part;
                    case 1 -> chan = mau.part;
                    case 2 -> than = mau.part;
                    case 3 -> mu = mau.part;
                    case 4 -> canh = mau.part;
                    case 5 -> vuKhi = mau.part;
                    default -> {
                    }
                }
            }
        } catch (RuntimeException ignored) {
        }
        return new VXLHinhDangNguoiChoi(dau, mu, than, chan, canh, vuKhi);
    }

    private static VXLHinhDangNguoiChoi rong() {
        return new VXLHinhDangNguoiChoi((short)-1, (short)-1, (short)-1,
                (short)-1, (short)-1, (short)-1);
    }

    private static short[] layAvenger(int ma) {
        return switch (ma) {
            case 391 -> new short[]{204, 205, 206};
            case 392 -> new short[]{220, 221, 222};
            case 393 -> new short[]{219, 217, 218};
            case 394 -> new short[]{198, 211, 212};
            case 395 -> new short[]{197, 207, 208};
            case 396 -> new short[]{203, 213, 214};
            case 397 -> new short[]{202, 215, 216};
            case 398 -> new short[]{199, 209, 210};
            default -> null;
        };
    }
}