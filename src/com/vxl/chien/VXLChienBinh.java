package com.vxl.chien;

import com.vxl.clan.VXLClanService;
import com.vxl.clan.VXLHieuUngClan;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import com.vxl.vatpham.VXLVatPham;

public class VXLChienBinh {
    public static final byte LOAI_BOSS_KHONG_CO = 0;
    public static final byte LOAI_BOSS_RUA = 1;
    public static final byte LOAI_BOSS_RONG = 2;
    private static final int GIOI_HAN_CHI_SO = 30000;
    private static final int MA_THUOC_TINH_THOI_GIAN_NAP_DAN = 14;
    public static final int THOI_GIAN_NAP_DAN_TOI_THIEU = 100;
    public static final int THOI_GIAN_NAP_DAN_MAC_DINH = 300;
    public static final int THOI_GIAN_NAP_DAN_TOI_DA = Short.MAX_VALUE;
    public final VXLNguoiChoi nguoiChoi;
    public final byte chiSo;
    public final boolean bot;
    public final boolean camTu;
    public final byte loaiBossDacBiet;
    public final String ten;
    public final int ma;
    public short maVuKhi;
    public final byte avenger;
    public final byte avengerDan;
    public final VXLKyNangAvenger kyNangAvenger;
    public final VXLHieuUngClan hieuUngClan;
    public int tanCong;
    public final int giap;
    public int mayMan;
    public int dongDoi;
    public int tocDo;
    public int satThuongChongTang;
    public int satThuongSungTruong;
    public int satThuongTieuLien;
    public int satThuongChuoi;
    public int satThuongHoaCai;
    public short x;
    public short y;
    public int hp;
    public int mauToiDa;
    public int khien;
    public int tongSatThuong;
    public int haGucTrongTran;
    public int haCamTuTrongTran;
    public int luotDoc;
    public int satThuongDoc;
    public int luotDongBang;
    public int luotMu;
    public int luotTangHinh;
    public int luotVoHinh;
    public int luotMacTo;
    public int luotLechDan;
    public int luotMaCaRong;
    public int vatPhamDanDacBiet = -1;
    public int heSoPhatBan = 100;
    public int heSoDiChuyen = 100;
    public int heSoTangNo = 100;
    public int luotNgungGio;
    public int luotNapNhanh;
    public int luotXuyenGiap;
    public int luotXuyenDiaHinh;
    public int heSoGoBom = 100;
    public int soPhatToiThieu = 1;
    private int heSoDiChuyenTrangBi = 100;
    public int no;
    public boolean daDungVatPhamTrongLuot;
    public boolean kyNangDacBiet;
    public boolean skillRiengPhatToi;
    public boolean chet;
    public boolean daRoiTran;
    public boolean daQuyetToan;
    public boolean coDinh;
    public int soLuotBossDaHanhDong;
    public byte chiSoChuBanSaoUltron = -1;
    public VXLChienBinh nguoiGaySatThuongCuoi;
    public VXLChienBinh nguonDoc;
    public long thoiDiemSanSangBan;

    public VXLChienBinh(VXLNguoiChoi nguoiChoi, byte chiSo, short x, short y) {
        nguoiChoi.dongBoTrangBiNhanVat();
        this.nguoiChoi = nguoiChoi;
        this.chiSo = chiSo;
        this.bot = false;
        this.camTu = false;
        this.loaiBossDacBiet = LOAI_BOSS_KHONG_CO;
        this.ten = nguoiChoi.ten;
        this.ma = nguoiChoi.ma;
        this.maVuKhi = nguoiChoi.wp;
        this.avenger = nguoiChoi.avenger;
        this.avengerDan = nguoiChoi.layAvengerDan();
        this.kyNangAvenger = VXLKyNangAvenger.tao(this.avengerDan);
        this.hieuUngClan = VXLClanService.layHieuUngClan(nguoiChoi.clan);
        this.x = x;
        this.y = y;

        int hpGoc = Math.max(100, layDiemCong(nguoiChoi, 0, 100));
        int tanCongGoc = 20 + Math.max(0, layDiemCong(nguoiChoi, 1, 0));
        int giapGoc = Math.max(0, layDiemCong(nguoiChoi, 2, 0));
        int mayManGoc = Math.max(0, layDiemCong(nguoiChoi, 3, 0));
        int dongDoiGoc = Math.max(0, layDiemCong(nguoiChoi, 4, 0));
        int tocDoGoc = Math.max(0, layDiemCong(nguoiChoi, 5, 0));

        long hpCong = 0;
        long tanCongCong = 0;
        long giapCong = 0;
        long mayManCong = 0;
        long dongDoiCong = 0;
        long tocDoCong = 0;

        long hpPhanTram = 0;
        long tanCongPhanTram = 0;
        long giapPhanTram = 0;
        long mayManPhanTram = 0;
        long dongDoiPhanTram = 0;
        long tocDoPhanTram = 0;
        long tatCaPhanTram = 0;
        long diChuyenPhanTram = 0;

        long chongTangPhanTram = 0;
        long sungTruongPhanTram = 0;
        long tieuLienPhanTram = 0;
        long chuoiPhanTram = 0;
        long hoaCaiPhanTram = 0;

        java.util.List<Integer> phanTramTanCongTungVi = new java.util.ArrayList<>();
        VXLVatPham[] itemBody = nguoiChoi.itemBody;
        if (itemBody != null) {
            for (VXLVatPham vatPham : itemBody) {
                if (vatPham == null || vatPham.mau == null || vatPham.HP <= 0) {
                    continue;
                }
                hpCong += vatPham.tongThamSoHieuLucTheoMa(0);
                tanCongCong += vatPham.tongThamSoHieuLucTheoMa(1);
                giapCong += vatPham.tongThamSoHieuLucTheoMa(2);
                mayManCong += vatPham.tongThamSoHieuLucTheoMa(3);
                dongDoiCong += vatPham.tongThamSoHieuLucTheoMa(4);
                tocDoCong += vatPham.tongThamSoHieuLucTheoMa(5);

                hpPhanTram += vatPham.tongThamSoHieuLucTheoMa(6);
                tanCongPhanTram += vatPham.tongThamSoHieuLucTheoMa(7);
                giapPhanTram += vatPham.tongThamSoHieuLucTheoMa(8);
                mayManPhanTram += vatPham.tongThamSoHieuLucTheoMa(9);
                dongDoiPhanTram += vatPham.tongThamSoHieuLucTheoMa(10);
                tocDoPhanTram += vatPham.tongThamSoHieuLucTheoMa(11);

                // Thu thập % tấn công từ option đồ
                for (int i = 0; i < vatPham.itemOptions.size(); i++) {
                    VXLThuocTinhVatPham op = (VXLThuocTinhVatPham)vatPham.itemOptions.get(i);
                    if (op != null && op.optionTemplate != null && op.optionTemplate.ma == 7 && op.thamSo > 0) {
                        phanTramTanCongTungVi.add(op.thamSo);
                    }
                }
                // Thu thập % tấn công từ từng viên ngọc đính trên đồ
                java.util.Vector ngocThuocTinhs = com.vxl.vatpham.VXLChiSoNgoc.layThuocTinh(vatPham);
                for (Object obj : ngocThuocTinhs) {
                    if (obj instanceof VXLThuocTinhVatPham op && op.optionTemplate != null && op.optionTemplate.ma == 7 && op.thamSo > 0) {
                        phanTramTanCongTungVi.add(op.thamSo);
                    }
                }

                tatCaPhanTram += vatPham.tongThamSoHieuLucTheoMa(18);
                tatCaPhanTram += Math.max(0, vatPham.tongThamSoHieuLucTheoMa(17)) * 2L;
                diChuyenPhanTram += vatPham.tongThamSoHieuLucTheoMa(26);

                chongTangPhanTram += vatPham.tongThamSoHieuLucTheoMa(21);
                sungTruongPhanTram += vatPham.tongThamSoHieuLucTheoMa(22);
                tieuLienPhanTram += vatPham.tongThamSoHieuLucTheoMa(23);
                chuoiPhanTram += vatPham.tongThamSoHieuLucTheoMa(24);
                hoaCaiPhanTram += vatPham.tongThamSoHieuLucTheoMa(25);
            }
        }

        this.mauToiDa = gioiHan((hpGoc + hpCong)
                * (100L + hpPhanTram + tatCaPhanTram + this.hieuUngClan.phanTramSinhLuc()) / 100L,
                100, GIOI_HAN_CHI_SO);

        /*
         * ====================================================================================
         * CÁC CÔNG THỨC CŨ ĐƯỢC LƯU LẠI ĐỂ THAM KHẢO / CHUYỂN ĐỔI KHI CẦN:
         *
         * [CÔNG THỨC 1 - TÁCH RỜI 1 + 2 + 3]:
         * long dameDo = tanCongCong;
         * long dameNenTang = tanCongGoc + tanCongCong;
         * long dameNgocPhanTram = (dameNenTang * tanCongPhanTram) / 100L;
         * long dame2 = tanCongGoc + dameNgocPhanTram;
         * long dameTatCaPhanTram = (dameNenTang * (tatCaPhanTram + this.hieuUngClan.phanTramHoaLuc())) / 100L;
         * long dame3 = tanCongGoc + dameTatCaPhanTram;
         * long tongTanCong = dameDo + dame2 + dame3;
         * this.tanCong = gioiHan(tongTanCong, 1, GIOI_HAN_CHI_SO);
         *
         * [CÔNG THỨC 2 - CHUẨN TEAMOBI CỘNG DỒN % TUYẾN TÍNH]:
         * this.tanCong = gioiHan((tanCongGoc + tanCongCong)
         *         * (100L + tanCongPhanTram + tatCaPhanTram + this.hieuUngClan.phanTramHoaLuc()) / 100L,
         *         1, GIOI_HAN_CHI_SO);
         * ====================================================================================
         */

        // [CÔNG THỨC 3 - NHÂN LŨY TIẾN / LÃI KÉP THEO TỪNG VIÊN NGỌC]:
        // Khởi đầu = Dame Nền Tảng (Dame Gốc tiềm năng + Dame Đồ cộng thẳng)
        double dameLuyTien = (double)(tanCongGoc + tanCongCong);
        // Mỗi viên ngọc nhân dồn tiếp nối: Dame = Dame + (Dame * %Ngọc / 100)
        for (int pt : phanTramTanCongTungVi) {
            dameLuyTien = dameLuyTien + (dameLuyTien * (double)pt / 100.0);
        }
        // Áp dụng % Tất Cả Chỉ Số
        if (tatCaPhanTram > 0) {
            dameLuyTien = dameLuyTien + (dameLuyTien * (double)tatCaPhanTram / 100.0);
        }
        // Áp dụng % Hỏa Lực Clan
        int clanHoaLuc = this.hieuUngClan.phanTramHoaLuc();
        if (clanHoaLuc > 0) {
            dameLuyTien = dameLuyTien + (dameLuyTien * (double)clanHoaLuc / 100.0);
        }

        this.tanCong = gioiHan((long)Math.round(dameLuyTien), 1, GIOI_HAN_CHI_SO);
        this.giap = gioiHan((giapGoc + giapCong)
                * (100L + giapPhanTram + tatCaPhanTram + this.hieuUngClan.phanTramPhongThu()) / 100L,
                0, GIOI_HAN_CHI_SO);
        this.mayMan = gioiHan((mayManGoc + mayManCong)
                * (100L + mayManPhanTram + tatCaPhanTram) / 100L,
                0, GIOI_HAN_CHI_SO);
        this.dongDoi = gioiHan((dongDoiGoc + dongDoiCong)
                * (100L + dongDoiPhanTram + tatCaPhanTram) / 100L,
                0, GIOI_HAN_CHI_SO);
        this.tocDo = gioiHan((tocDoGoc + tocDoCong)
                * (100L + tocDoPhanTram + tatCaPhanTram) / 100L,
                0, GIOI_HAN_CHI_SO);

        this.satThuongChongTang = (int)chongTangPhanTram;
        this.satThuongSungTruong = (int)sungTruongPhanTram;
        this.satThuongTieuLien = (int)tieuLienPhanTram;
        this.satThuongChuoi = (int)chuoiPhanTram;
        this.satThuongHoaCai = (int)hoaCaiPhanTram;

        long tocDoBonusDiChuyen = (long)this.tocDo * 2L;
        this.heSoDiChuyenTrangBi = gioiHan(100L + diChuyenPhanTram + tocDoBonusDiChuyen
                + this.hieuUngClan.phanTramTocDo(), 100, 400);
        this.hp = this.mauToiDa;
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger) {
        this(chiSo, x, y, ten, maVuKhi, avenger, false);
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger, boolean camTu) {
        this(chiSo, x, y, ten, maVuKhi, avenger, camTu,
                camTu ? 140 : 220, camTu ? 45 : 35, camTu ? 8 : 15);
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger,
            int mauToiDa, int tanCong, int giap) {
        this(chiSo, x, y, ten, maVuKhi, avenger, false, mauToiDa, tanCong, giap);
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger,
            boolean camTu, int mauToiDa, int tanCong, int giap) {
        this(chiSo, x, y, ten, -9000 - Byte.toUnsignedInt(chiSo), maVuKhi, avenger,
                camTu, LOAI_BOSS_KHONG_CO, mauToiDa, tanCong, giap);
    }

    public VXLChienBinh(byte chiSo, short x, short y, String ten, int ma, short maVuKhi,
            byte avenger, boolean camTu, byte loaiBossDacBiet,
            int mauToiDa, int tanCong, int giap) {
        this.nguoiChoi = null;
        this.chiSo = chiSo;
        this.bot = true;
        this.camTu = camTu;
        this.loaiBossDacBiet = loaiBossDacBiet;
        this.ten = ten;
        this.ma = ma;
        this.maVuKhi = maVuKhi;
        this.avenger = avenger;
        this.avengerDan = avenger;
        this.kyNangAvenger = VXLKyNangAvenger.tao(this.avengerDan);
        this.hieuUngClan = VXLHieuUngClan.KHONG_CO;
        this.x = x;
        this.y = y;
        this.mauToiDa = gioiHan(mauToiDa, 1, GIOI_HAN_CHI_SO);
        this.tanCong = gioiHan(tanCong, 1, GIOI_HAN_CHI_SO);
        this.giap = gioiHan(giap, 0, GIOI_HAN_CHI_SO);
        this.mayMan = 0;
        this.dongDoi = 0;
        this.tocDo = 0;
        this.hp = this.mauToiDa;
    }

    public static VXLChienBinh taoBanSaoUltron(byte chiSo, byte chiSoChu,
            short x, short y, String ten, int mauToiDa, int tanCong, int giap) {
        VXLChienBinh banSao = new VXLChienBinh(chiSo, x, y, ten, (short)-1,
                VXLKyNangAvenger.MA_ULTRON, false, mauToiDa, tanCong, giap);
        banSao.chiSoChuBanSaoUltron = chiSoChu;
        return banSao;
    }

    public boolean coPhien() {
        return this.nguoiChoi != null && this.nguoiChoi.dichVu != null;
    }

    public boolean laBanSaoUltron() {
        return this.chiSoChuBanSaoUltron >= 0;
    }

    public void capNhatTanCongTheoTrangBi() {
        if (this.nguoiChoi == null || this.bot) {
            return;
        }
        int tanCongGoc = 20 + Math.max(0, layDiemCong(this.nguoiChoi, 1, 0));
        long tanCongCong = 0;
        long tatCaPhanTram = 0;
        java.util.List<Integer> phanTramTanCongTungVi = new java.util.ArrayList<>();
        VXLVatPham[] itemBody = this.nguoiChoi.itemBody;
        if (itemBody != null) {
            for (VXLVatPham vatPham : itemBody) {
                if (vatPham == null || vatPham.mau == null || vatPham.HP <= 0) {
                    continue;
                }
                tanCongCong += vatPham.tongThamSoHieuLucTheoMa(1);
                for (int i = 0; i < vatPham.itemOptions.size(); i++) {
                    VXLThuocTinhVatPham op = (VXLThuocTinhVatPham)vatPham.itemOptions.get(i);
                    if (op != null && op.optionTemplate != null && op.optionTemplate.ma == 7 && op.thamSo > 0) {
                        phanTramTanCongTungVi.add(op.thamSo);
                    }
                }
                java.util.Vector ngocThuocTinhs = com.vxl.vatpham.VXLChiSoNgoc.layThuocTinh(vatPham);
                for (Object obj : ngocThuocTinhs) {
                    if (obj instanceof VXLThuocTinhVatPham op && op.optionTemplate != null && op.optionTemplate.ma == 7 && op.thamSo > 0) {
                        phanTramTanCongTungVi.add(op.thamSo);
                    }
                }
                tatCaPhanTram += vatPham.tongThamSoHieuLucTheoMa(18);
                tatCaPhanTram += Math.max(0, vatPham.tongThamSoHieuLucTheoMa(17)) * 2L;
            }
        }

        /*
         * [CÔNG THỨC 1 - TÁCH RỜI 1 + 2 + 3]:
         * long dameDo = tanCongCong;
         * long dameNenTang = tanCongGoc + tanCongCong;
         * long dameNgocPhanTram = (dameNenTang * tanCongPhanTram) / 100L;
         * long dame2 = tanCongGoc + dameNgocPhanTram;
         * long dameTatCaPhanTram = (dameNenTang * (tatCaPhanTram + this.hieuUngClan.phanTramHoaLuc())) / 100L;
         * long dame3 = tanCongGoc + dameTatCaPhanTram;
         * long tongTanCong = dameDo + dame2 + dame3;
         * this.tanCong = gioiHan(tongTanCong, 1, GIOI_HAN_CHI_SO);
         *
         * [CÔNG THỨC 2 - CHUẨN TEAMOBI CỘNG DỒN % TUYẾN TÍNH]:
         * this.tanCong = gioiHan((tanCongGoc + tanCongCong)
         *         * (100L + tanCongPhanTram + tatCaPhanTram + this.hieuUngClan.phanTramHoaLuc()) / 100L,
         *         1, GIOI_HAN_CHI_SO);
         */

        // [CÔNG THỨC 3 - NHÂN LŨY TIẾN / LÃI KÉP THEO TỪNG VIÊN NGỌC]:
        double dameLuyTien = (double)(tanCongGoc + tanCongCong);
        for (int pt : phanTramTanCongTungVi) {
            dameLuyTien = dameLuyTien + (dameLuyTien * (double)pt / 100.0);
        }
        if (tatCaPhanTram > 0) {
            dameLuyTien = dameLuyTien + (dameLuyTien * (double)tatCaPhanTram / 100.0);
        }
        int clanHoaLuc = this.hieuUngClan.phanTramHoaLuc();
        if (clanHoaLuc > 0) {
            dameLuyTien = dameLuyTien + (dameLuyTien * (double)clanHoaLuc / 100.0);
        }
        this.tanCong = gioiHan((long)Math.round(dameLuyTien), 1, GIOI_HAN_CHI_SO);
    }

    public int batDauNapDan() {
        int thoiGianNap = this.layThoiGianNapDan();
        this.thoiDiemSanSangBan = System.currentTimeMillis() + thoiGianNap;
        return thoiGianNap;
    }

    public void ketThucPhatBan() {
        if (this.luotNapNhanh > 0) {
            this.luotNapNhanh--;
        }
        if (this.luotNgungGio > 0) {
            this.luotNgungGio--;
        }
        if (this.luotXuyenGiap > 0) {
            this.luotXuyenGiap--;
        }
        if (this.luotXuyenDiaHinh > 0) {
            this.luotXuyenDiaHinh--;
        }
        this.soPhatToiThieu = 1;
    }

    public int layThoiGianNapDan() {
        if (this.bot) {
            return THOI_GIAN_NAP_DAN_MAC_DINH;
        }
        int thoiGianNap = layThoiGianNapDanTheoMaVuKhi(this.maVuKhi);
        if (this.nguoiChoi != null && this.nguoiChoi.itemBody != null
                && this.nguoiChoi.itemBody.length > 5) {
            VXLVatPham vuKhi = this.nguoiChoi.itemBody[5];
            if (vuKhi != null && vuKhi.mau != null && vuKhi.HP > 0) {
                int theoVatPham = layThamSoNapDan(vuKhi.itemOptions);
                int theoMau = layThamSoNapDan(vuKhi.mau.thuocTinhs);
                int theoCauHinh = theoVatPham > 0 ? theoVatPham : theoMau;
                thoiGianNap = theoCauHinh > 0
                        ? gioiHanNapDan(theoCauHinh) : THOI_GIAN_NAP_DAN_MAC_DINH;
            }
        }
        if (this.tocDo > 0) {
            long giamTheoTocDo = Math.min(250L, (long)this.tocDo * 2L);
            thoiGianNap = (int)Math.max(THOI_GIAN_NAP_DAN_TOI_THIEU, thoiGianNap - giamTheoTocDo);
        }
        if (this.luotNapNhanh > 0) {
            thoiGianNap = gioiHanNapDan((long)thoiGianNap * 80L / 100L);
        }
        return thoiGianNap;
    }

    public int layPhanTramSatThuongVuKhi(short maVuKhi) {
        byte nhom = VXLCauHinhVatPhamChienDau.layNhomSungClientTheoVuKhi(maVuKhi);
        int satThuong = switch (nhom) {
            case 0 -> laSungChongTang(maVuKhi) ? this.satThuongChongTang : 0;
            case 1 -> this.satThuongSungTruong;
            case 2 -> this.satThuongHoaCai;
            case 3 -> this.satThuongChuoi;
            case 5 -> this.satThuongTieuLien;
            default -> 0;
        };
        return satThuong + this.hieuUngClan.phanTramSatThuong(maVuKhi);
    }

    private static boolean laSungChongTang(short maVuKhi) {
        return switch (maVuKhi) {
            case 5, 31, 57, 134, 135 -> true;
            default -> false;
        };
    }

    public static int layThoiGianNapDanTheoMaVuKhi(short maVuKhi) {
        if (VXLQuanLyMayChu.itemTemplates == null) {
            return THOI_GIAN_NAP_DAN_MAC_DINH;
        }
        for (VXLMauVatPham mau : VXLQuanLyMayChu.itemTemplates.values()) {
            if (mau == null || mau.loai != 5 || mau.part != maVuKhi) {
                continue;
            }
            int theoMau = layThamSoNapDan(mau.thuocTinhs);
            return theoMau > 0 ? gioiHanNapDan(theoMau) : THOI_GIAN_NAP_DAN_MAC_DINH;
        }
        return THOI_GIAN_NAP_DAN_MAC_DINH;
    }

    private static int layThamSoNapDan(java.util.Vector thuocTinhs) {
        if (thuocTinhs == null) {
            return -1;
        }
        for (Object giaTri : thuocTinhs) {
            if (giaTri instanceof VXLThuocTinhVatPham thuocTinh
                    && thuocTinh.optionTemplate != null
                    && thuocTinh.optionTemplate.ma == MA_THUOC_TINH_THOI_GIAN_NAP_DAN
                    && thuocTinh.thamSo > 0) {
                return thuocTinh.thamSo;
            }
        }
        return -1;
    }

    private static int gioiHanNapDan(long giaTri) {
        if (giaTri <= 0L) {
            return THOI_GIAN_NAP_DAN_MAC_DINH;
        }
        return (int)Math.max(THOI_GIAN_NAP_DAN_TOI_THIEU,
                Math.min(THOI_GIAN_NAP_DAN_TOI_DA, giaTri));
    }

    public int layTamDiChuyen(int tamCoBan) {
        long tam = (long)Math.max(0, tamCoBan) * this.heSoDiChuyenTrangBi
                * Math.max(100, this.heSoDiChuyen) / 10000L;
        return gioiHan(tam, 0, 2000);
    }

    public short layIconKyNangDacBiet() {
        if (this.nguoiChoi != null && this.nguoiChoi.itemBody != null
                && this.nguoiChoi.itemBody.length > 5) {
            VXLVatPham vuKhi = this.nguoiChoi.itemBody[5];
            if (vuKhi != null && vuKhi.mau != null) {
                return vuKhi.mau.iconID;
            }
        }
        return 0;
    }

    public boolean daNapDan() {
        return System.currentTimeMillis() >= this.thoiDiemSanSangBan;
    }

    public byte phanTramMau() {
        if (this.mauToiDa <= 0) {
            return 0;
        }
        return (byte)Math.max(0, Math.min(100, (long)this.hp * 100L / this.mauToiDa));
    }

    public boolean tangNo(int giaTri) {
        if (this.bot || this.chet || giaTri <= 0
                || this.kyNangAvenger != null && this.kyNangAvenger.laSkillRieng()) {
            return false;
        }
        int truoc = this.no;
        int giaTriThuc = Math.max(1, giaTri * Math.max(100, this.heSoTangNo) / 100);
        this.no = Math.max(0, Math.min(100, this.no + giaTriThuc));
        return this.no != truoc;
    }

    public boolean kichHoatKyNangDacBiet() {
        if (this.bot || this.chet || this.kyNangDacBiet) {
            return false;
        }
        if (this.kyNangAvenger != null && this.kyNangAvenger.laSkillRieng()) {
            return false;
        }
        if (this.no < 100) {
            return false;
        }
        this.no = 0;
        this.kyNangDacBiet = true;
        return true;
    }

    public int hoiMau(int soMau) {
        if (this.chet || soMau <= 0) {
            return 0;
        }
        int truoc = Math.max(0, Math.min(this.mauToiDa, this.hp));
        this.hp = (int)Math.min(this.mauToiDa, (long)truoc + soMau);
        return this.hp - truoc;
    }

    public int themKhien(int soKhien) {
        if (this.chet || soKhien <= 0) {
            return 0;
        }
        int gioiHanKhien = Math.max(1, this.mauToiDa / 2);
        int truoc = Math.max(0, Math.min(gioiHanKhien, this.khien));
        this.khien = (int)Math.min(gioiHanKhien, (long)truoc + soKhien);
        return this.khien - truoc;
    }

    private static int layDiemCong(VXLNguoiChoi nguoiChoi, int chiSo, int macDinh) {
        if (nguoiChoi.pointAdd == null || chiSo < 0 || chiSo >= nguoiChoi.pointAdd.length) {
            return macDinh;
        }
        return nguoiChoi.pointAdd[chiSo];
    }

    private static int gioiHan(long giaTri, int nhoNhat, int lonNhat) {
        return (int)Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }
}
