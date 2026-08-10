import sys
with open('src/com/vxl/luyentap/VXLQuanLyLuyenTap.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()

new_content = ''.join(lines[:338])
new_content += '''                if (this.tacVuKetThucPhienQuan != null) {
                    this.tacVuKetThucPhienQuan.cancel(false);
                }
                this.tacVuKetThucPhienQuan = BO_LAP_LICH.schedule(this::ketThucLuotPhienQuan, 1100L, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi Phiến quân luyện tập bắn trả.", ex);
        }
    }

    private void ketThucLuotPhienQuan() {
        try {
            synchronized (this) {
                this.tacVuKetThucPhienQuan = null;
                if (!this.nguoiChoi.inTraining) {
                    this.phienQuanDangHoatDong = false;
                    return;
                }
                this.phienQuanDangHoatDong = false;
                this.nguoiChoi.dichVu.guiLuotLuyenTapTiep((byte)0, this.nguoiChoiX, this.nguoiChoiY);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLQuanLyLuyenTap.class.getName()).log(Level.SEVERE, "Lỗi kết thúc lượt Phiến quân luyện tập.", ex);
        }
    }

    private void phienQuanBanTra() throws IOException {
        int chiSoPhienQuan = this.layPhienQuanTiepTheo();
        if (chiSoPhienQuan < 0) {
            this.hoanThanhPhienQuan();
            return;
        }
        this.diChuyenPhienQuan(chiSoPhienQuan);
        this.phienQuanDungVatPhamNeuCan(chiSoPhienQuan);
        byte loaiDan = 0;
        byte luc = 18;
        short goc = this.tinhDuongDan.gocDanDaoToiMucTieu(this.phienQuanX[chiSoPhienQuan],
                this.phienQuanY[chiSoPhienQuan], this.nguoiChoiX, this.nguoiChoiY, luc);
        short[][] duongDan = this.tinhDuongDan.taoDuongDanCong(this.phienQuanX[chiSoPhienQuan],
                this.phienQuanY[chiSoPhienQuan], goc, luc);

        System.out.println(String.format("[TRAINING-BOT-FIRE] Phiến quân %s bắn trả | Goc=%d | Luc=%d | Pos=(%d,%d)",
                VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), goc, luc,
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan]));

        this.nguoiChoi.dichVu.guiKetQuaBanLuyenTap((byte)(chiSoPhienQuan + 1), loaiDan,
                this.phienQuanX[chiSoPhienQuan], this.phienQuanY[chiSoPhienQuan], goc, luc,
                duongDan[0], duongDan[1]);
        if (this.tinhDuongDan.duongDanTrungNguoiChoi(duongDan[0], duongDan[1], this.nguoiChoiX, this.nguoiChoiY)) {
            int heSoDan = this.danManhPhienQuan
                    ? VXLCauHinhPhienQuan.HE_SO_DAN_MANH
                    : VXLCauHinhPhienQuan.HE_SO_DAN_THUONG;
            int satThuongGoc = VXLTinhSatThuong.tinhPhatBan(this.tanCongPhienQuan, luc, heSoDan);
            int satThuong = VXLTinhSatThuong.tinhSauGiap(satThuongGoc, this.chiSoNguoiChoi.giap);
            this.danManhPhienQuan = false;
            int mauTruoc = this.mauNguoiChoi;
            this.mauNguoiChoi = Math.max(0, this.mauNguoiChoi - satThuong);
            if (this.mauNguoiChoi <= 0) {
                this.mauNguoiChoi = this.chiSoNguoiChoi.mauToiDa;
            }

            System.out.println(String.format("[TRAINING-BOT-HIT] Phiến quân %s bắn trúng %s | Sát thương gốc=%d | Giáp người chơi=%d | Sát thương thực=%d | HP người chơi: %d -> %d/%d",
                    VXLCauHinhPhienQuan.layTen(this.capPhienQuanHienTai), this.nguoiChoi.ten,
                    satThuongGoc, this.chiSoNguoiChoi.giap, satThuong, mauTruoc,
                    this.mauNguoiChoi, this.chiSoNguoiChoi.mauToiDa));

            this.nguoiChoi.dichVu.guiCapNhatMauLuyenTap((byte)0, this.mauNguoiChoi,
                    this.chiSoNguoiChoi.mauToiDa, (byte)0);
        }
    }

    private int layPhienQuanTiepTheo() {
        for (int buoc = 1; buoc <= SO_PHIEN_QUAN; buoc++) {
            int chiSo = (this.luotPhienQuan + buoc + SO_PHIEN_QUAN) % SO_PHIEN_QUAN;
            if (!this.phienQuanDaChet[chiSo]) {
                this.luotPhienQuan = chiSo;
                return chiSo;
            }
        }
        return -1;
    }

    private boolean daHaPhienQuan() {
        for (boolean daChet : this.phienQuanDaChet) {
            if (!daChet) {
                return false;
            }
        }
        return true;
    }

    private void hoanThanhPhienQuan() throws IOException {
        if (!this.nguoiChoi.inTraining) {
            return;
        }
        int capDaHa = this.capPhienQuanHienTai;
        this.soPhienQuanDaHa = Math.max(this.soPhienQuanDaHa, capDaHa);
        this.nguoiChoi.trainingSuccess = (byte)Math.min(VXLCauHinhPhienQuan.CAP_TOI_DA, this.soPhienQuanDaHa + 1);
        this.nguoiChoi.ghiNhanHaBoss(1);
        this.resetTrangThai();
        this.dungTacVu();

'''

start_idx = -1
for i, line in enumerate(lines):
    if 'int kinhNghiem = 100 + capDaHa * 15;' in line:
        start_idx = i
        break

if start_idx != -1:
    new_content += ''.join(lines[start_idx:])
else:
    print('Error: Could not find kinhNghiem line to resume')
    sys.exit(1)

with open('src/com/vxl/luyentap/VXLQuanLyLuyenTap.java', 'w', encoding='utf-8') as f:
    f.write(new_content)
print('File restored and modified successfully.')
