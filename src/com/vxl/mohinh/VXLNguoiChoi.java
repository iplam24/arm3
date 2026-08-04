package com.vxl.mohinh;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLCoSoDuLieu;
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.vatpham.VXLVatPham;
import com.vxl.vatpham.VXLThuocTinhVatPham;
import com.vxl.vatpham.VXLMauVatPham;
import com.vxl.mang.VXLDichVuGame;
import com.vxl.mang.VXLTinNhan;
import com.vxl.nhapvai.VXLBanDoRPG;
import com.vxl.nhapvai.VXLKhu;
import com.vxl.phong.VXLQuanLyPhong;
import com.vxl.cuahang.VXLTrang;
import com.vxl.cuahang.VXLCuaHang;
import com.vxl.tienich.VXLTienIch;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VXLNguoiChoi {
    private static final int TRAINING_BOT_COUNT = 5;
    private static final String[] TRAINING_BOT_NAMES = new String[]{"Iron Bot", "Hulk Bot", "Thor Bot", "Captain Bot", "Ultron Bot"};
    private static final byte[] TRAINING_BOT_AVENGERS = new byte[]{1, 2, 3, 5, 8};
    private static final short[] TRAINING_BOT_WEAPONS = new short[]{5, 27, 54, 55, 58};
    private static final short[] TRAINING_BOT_SPAWN_X = new short[]{600, 720, 840, 500, 960};
    private static final short[] TRAINING_BOT_SPAWN_Y = new short[]{300, 280, 320, 260, 300};
    private static final ScheduledExecutorService TRAINING_BOT_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "training-bot");
        thread.setDaemon(true);
        return thread;
    });
    public static HashMap<Integer, VXLNguoiChoi> players_id = new HashMap();
    public int ma;
    public String ten;
    public int vang;
    public int ngoc;
    public int kinhNghiem;
    public int cup;
    public int cap;
    public int clan = -1;
    public byte power;
    public byte busyHammer;
    public byte nHammer;
    public byte trainingSuccess;
    public boolean inTraining;
    public int trainingHits;
    private int trainingDummyHp = 100;
    private int trainingPlayerHp = 100;
    private short trainingPlayerX = 220;
    private short trainingPlayerY = 300;
    private short trainingDummyX = 600;
    private short trainingDummyY = 300;
    private int[] trainingBotHp = new int[TRAINING_BOT_COUNT];
    private short[] trainingBotX = new short[TRAINING_BOT_COUNT];
    private short[] trainingBotY = new short[TRAINING_BOT_COUNT];
    private boolean[] trainingBotDead = new boolean[TRAINING_BOT_COUNT];
    private int trainingPendingHitBot = -1;
    private int trainingBotTurn = -1;
    private long lastTrainingFire;
    private boolean trainingWaitingShotEnd;
    private boolean trainingPendingHitDummy;
    private boolean trainingFirstTurnSent;
    private boolean trainingBotAnimating;
    private boolean trainingBossShield;
    private boolean trainingBossPowerShot;
    private int trainingBotTurnCount;
    private ScheduledFuture<?> trainingBotTask;
    private ScheduledFuture<?> trainingBotReturnTask;
    private ScheduledFuture<?> trainingPlayerResolveTask;
    public short[] pointAdd;
    public short point;
    public byte zoneId = (byte)-1;
    public VXLKhu zone;
    public short x;
    public short y;
    public short head;
    public short hat;
    public short body;
    public short leg;
    public short wp;
    public short wing;
    public VXLVatPham[] itemBag = new VXLVatPham[20];
    public VXLVatPham[] itemBody = new VXLVatPham[6];
    public int[] itemBalo = new int[0];
    public VXLVatPham[] itemBox = new VXLVatPham[20];
    public boolean isReady;
    public byte pointSeat;
    public int chiSo = -1;
    public VXLDichVuGame dichVu;
    public int kill = 1;
    public int chet;
    public int assist;
    public byte powerAvenger;
    public byte avenger;
    private VXLCuaHang store;

    public VXLNguoiChoi(VXLDichVuGame dichVu) {
        this.dichVu = dichVu;
    }

    public float layKD() {
        return (float)this.kill / (float)this.chet;
    }

    public float layKDA() {
        return (float)(this.kill + this.assist) / (float)this.chet;
    }

    public static VXLNguoiChoi layNguoiChoiTheoMa(int ma) {
        return players_id.get(ma);
    }

    public static void xoa(int ma) {
        players_id.remove(ma);
    }

    public static void guiMayChu(VXLTinNhan ms) {
        for (VXLNguoiChoi pl : players_id.values()) {
            if (pl == null) continue;
            pl.dichVu.guiTin(ms);
        }
    }

    public void nangCapNhanVat(VXLTinNhan ms) throws IOException {
        byte loai = ms.boDoc().readByte();
        if (loai == 0) {
            int i;
            byte[] tiso = new byte[]{10, 1, 1, 1, 1, 1};
            VXLTinNhan msg = new VXLTinNhan(-46);
            DataOutputStream ds = msg.boGhi();
            ds.writeByte(0);
            ds.writeShort(this.point);
            for (i = 0; i < 6; ++i) {
                ds.writeByte(tiso[i]);
            }
            for (i = 0; i < 6; ++i) {
                ds.writeShort(this.pointAdd[i]);
            }
            ds.flush();
            this.dichVu.guiTin(msg);
        } else {
            byte chiSo = ms.boDoc().readByte();
            if (this.point > 0) {
                if (chiSo < 0 || chiSo > 5) {
                    this.moHopThoaiOK("Có lỗi xảy ra.");
                } else {
                    if (chiSo == 0) {
                        this.pointAdd[0] = (short)(this.pointAdd[0] + 10);
                    } else {
                        byte by = chiSo;
                        this.pointAdd[by] = (short)(this.pointAdd[by] + 1);
                    }
                    this.point = (short)(this.point - 1);
                }
            } else {
                this.moHopThoaiOK("Không đủ điểm cộng.");
            }
        }
    }

    public void banDoRPG(VXLTinNhan ms) throws IOException {
        byte b = ms.boDoc().readByte();
        switch (b) {
            case 2: {
                this.diChuyen(ms);
                break;
            }
            case 3: {
                this.chat(ms);
                break;
            }
            case 7: {
                this.moKhu();
                break;
            }
            case 8: {
                this.doiKhu(ms);
                break;
            }
            case 11: {
                this.moMenu(ms);
                break;
            }
            default: {
                System.out.println("b: " + b);
                break;
            }
        }
    }

    public void moMenu(VXLTinNhan ms) throws IOException {
        short npcId = ms.boDoc().readShort();
        switch (npcId) {
            case 3: {
                this.npcDaiUy();
                System.out.println("npcId: " + npcId);
                break;
            }
            default: {
                break;
            }
        }
    }

    public void npcDaiUy() throws IOException {
        VXLQuanLyPhong.yeuCauDanhSachPhong(this);
    }

    public void doiKhu(VXLTinNhan ms) throws IOException {
        byte zone = ms.boDoc().readByte();
        VXLBanDoRPG.roi(this);
        VXLBanDoRPG.vao(zone, this);
    }

    public int layOTrongTuiDo() {
        int number = 0;
        for (VXLVatPham vatPham : this.itemBag) {
            if (vatPham != null) continue;
            ++number;
        }
        return number;
    }

    public int layOTrongBalo() {
        int number = 0;
        for (int chiSo : this.itemBalo) {
            if (chiSo != -1) continue;
            ++number;
        }
        return number;
    }

    public int layOTrongRuong() {
        int number = 0;
        for (VXLVatPham vatPham : this.itemBox) {
            if (vatPham != null) continue;
            ++number;
        }
        return number;
    }

    public void thucHien(VXLTinNhan ms) throws IOException {
        byte action = ms.boDoc().readByte();
        int ma = ms.boDoc().readInt();
        if (ma >= 11000) {
            int chiSo = ma - 11000;
            VXLVatPham vatPham = this.itemBag[chiSo];
            if (vatPham != null) {
                int vang = 0;
                vang = vatPham.mau.buyGold > 0 ? vatPham.mau.buyGold / 2 : (vatPham.mau.buyGem > 0 ? vatPham.mau.buyGem * 100 : 1);
                vang *= vatPham.soLuong;
                this.updateGold(vang);
                this.itemBag[chiSo] = null;
                this.dichVu.capNhatTuiDo(chiSo, 0);
                this.startOKDlg2("Bán vật phẩm thành công.");
            } else {
                this.startOKDlg2("Bán vật phẩm thất bại.");
            }
        }
    }

    public void yeuCauBanVatPham(VXLTinNhan ms) throws IOException {
        byte chiSo = ms.boDoc().readByte();
        VXLVatPham vatPham = this.itemBag[chiSo];
        if (vatPham != null) {
            if (this.vatPhamCoTrongBalo(vatPham)) {
                this.startOKDlg2("Vật phẩm đã gắn vào Balo.");
                return;
            }
            int vang = 0;
            vang = vatPham.mau.buyGold > 0 ? vatPham.mau.buyGold / 2 : (vatPham.mau.buyGem > 0 ? vatPham.mau.buyGem * 100 : 1);
            VXLTinNhan mss = new VXLTinNhan(-25);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(11000 + chiSo);
            ds.writeUTF("Bạn có chắc muốn bán " + vatPham.mau.ten + " với giá " + VXLTienIch.dinhDangTien(vang *= vatPham.soLuong) + " Vàng");
            ds.flush();
            this.dichVu.guiTin(mss);
        } else {
            this.startOKDlg2("Bạn không có vật phẩm này.");
        }
    }

    public void yeuCauMuaVatPham(VXLTinNhan ms) throws IOException {
        byte loai = ms.boDoc().readByte();
        short ma = ms.boDoc().readShort();
        VXLMauVatPham vatPham = VXLQuanLyMayChu.itemTemplates.get(ma);
        if (vatPham == null) {
            this.startOKDlg2("Có lỗi xảy ra.");
            return;
        }
        if (loai == 0 && vatPham.buyGold > 0 || loai == 1 && vatPham.buyGem > 0) {
            if (this.layOTrongTuiDo() == 0) {
                this.startOKDlg2("Túi đã đầy.");
                return;
            }
            if (loai == 0) {
                if (vatPham.buyGold > this.vang) {
                    this.startOKDlg2("Bạn không đủ vàng.");
                    return;
                }
                this.updateGold(-vatPham.buyGold);
            } else {
                if (vatPham.buyGem > this.ngoc) {
                    this.startOKDlg2("Bạn không đủ ngọc.");
                    return;
                }
                this.updateGem(-vatPham.buyGem);
            }
        } else {
            this.moHopThoaiOK("Có lỗi xảy ra.");
            return;
        }
        VXLVatPham add = new VXLVatPham(ma);
        add.HP = 100;
        add.itemOptions = vatPham.thuocTinhs;
        this.themVatPhamVaoTui(add);
        this.moHopThoaiOK("Bạn mua thành công " + vatPham.ten);
    }

    public void datTrangBiChoNhanVat(VXLVatPham vatPham) {
        int ma = vatPham.ma;
        this.avenger = 0;
        if (ma == 391) {
            this.head = (short)204;
            this.body = (short)205;
            this.leg = (short)206;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = 1;
        } else if (ma == 392) {
            this.head = (short)220;
            this.body = (short)221;
            this.leg = (short)222;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)2;
        } else if (ma == 393) {
            this.head = (short)219;
            this.body = (short)217;
            this.leg = (short)218;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)3;
        } else if (ma == 394) {
            this.head = (short)198;
            this.body = (short)211;
            this.leg = (short)212;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)4;
        } else if (ma == 395) {
            this.head = (short)197;
            this.body = (short)207;
            this.leg = (short)208;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)5;
        } else if (ma == 396) {
            this.head = (short)203;
            this.body = (short)213;
            this.leg = (short)214;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)6;
        } else if (ma == 397) {
            this.head = (short)202;
            this.body = (short)215;
            this.leg = (short)216;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)7;
        } else if (ma == 398) {
            this.head = (short)199;
            this.body = (short)209;
            this.leg = (short)210;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)8;
        } else {
            VXLVatPham t = this.itemBody[5];
            if (t == null || t.ma < 391 || t.ma > 400) {
                byte loai = vatPham.mau.loai;
                short part = vatPham.mau.part;
                if (loai == 0) {
                    this.head = part;
                } else if (loai == 1) {
                    this.leg = part;
                } else if (loai == 2) {
                    this.body = part;
                } else if (loai == 3) {
                    this.hat = part;
                } else if (loai == 4) {
                    this.wing = part;
                } else if (loai == 5) {
                    this.wp = part;
                }
            }
        }
    }

    public boolean vatPhamCoTrongBalo(VXLVatPham vatPham) {
        for (int chiSo : this.itemBalo) {
            if (chiSo != vatPham.chiSo) continue;
            return true;
        }
        return false;
    }

    public void dungVatPham(VXLTinNhan ms) throws IOException {
        byte chiSo = ms.boDoc().readByte();
        if (ms.boDoc().available() > 0) {
            byte loai = ms.boDoc().readByte();
            if (loai == 1) {
                VXLVatPham vatPham = this.itemBag[chiSo];
                if (vatPham != null && vatPham.soLuong > 0) {
                    byte t = vatPham.mau.loai;
                    int ma = vatPham.ma;
                    if (t == 12) {
                        this.startOKDlg2("Bạn có muốn nhập 5 viên ngọc này, hãy vào menu Bắt dầu -> ghép ngọc");
                        return;
                    }
                    if (t <= 5) {
                        Vector<String> vector = new Vector<String>();
                        if (vatPham.nSocket < 3) {
                            vector.add("Đục lỗ");
                        }
                        if (vatPham.nGem < vatPham.nSocket) {
                            vector.add("Đính ngọc");
                        }
                        if (vatPham.nGem > 0) {
                            vector.add("Tháo ngọc");
                        }
                        this.dichVu.moDanhSach("Bạn muốn làm gì?", vector);
                    } else if (ma == 256) {
                        this.point = (short)(this.point + (short)((this.pointAdd[0] - 1000) / 10 + this.pointAdd[1] + this.pointAdd[2] + this.pointAdd[3] + this.pointAdd[4] + this.pointAdd[5]));
                        this.pointAdd[0] = 1000;
                        this.pointAdd[1] = 0;
                        this.pointAdd[2] = 0;
                        this.pointAdd[3] = 0;
                        this.pointAdd[4] = 0;
                        this.pointAdd[5] = 0;
                        this.removeItem(chiSo, 1);
                        this.startOKDlg2("Tẩy điểm thành công.");
                    } else if (vatPham.mau.loai == 11) {
                        this.startOKDlg2("Không thể sử dụng.");
                    } else {
                        this.startOKDlg2("Không thể sử dụng.");
                    }
                } else {
                    this.startOKDlg2("Không tìm thấy vật phẩm này. Vui lòng đăng nhập lại để kiểm tra.");
                }
            } else {
                VXLVatPham vatPham = this.itemBody[chiSo];
                if (vatPham != null) {
                    Vector<String> vector = new Vector<String>();
                    if (vatPham.nSocket < 3) {
                        vector.add("Đục lỗ");
                    }
                    if (vatPham.nGem < vatPham.nSocket) {
                        vector.add("Đính ngọc");
                    }
                    if (vatPham.nGem > 0) {
                        vector.add("Tháo ngọc");
                    }
                    this.dichVu.moDanhSach("Bạn muốn làm gì?", vector);
                } else {
                    this.startOKDlg2("Không tìm thấy vật phẩm này. Vui lòng đăng nhập lại để kiểm tra.");
                }
            }
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public void chuyenVatPham(VXLTinNhan ms) throws IOException {
        VXLVatPham vatPham;
        byte loai = ms.boDoc().readByte();
        byte chiSo = ms.boDoc().readByte();
        System.out.println("type: " + loai);
        if (loai == 4) {
            VXLVatPham item2 = this.itemBag[chiSo];
            if (item2 == null) return;
            if (this.vatPhamCoTrongBalo(item2)) {
                this.moHopThoaiOK("Vật phẩm đã gắn vào Balo.");
                return;
            }
            if (this.cap < item2.mau.cap) {
                this.moHopThoaiOK("Trình độ không đạt yêu cầu.");
                return;
            }
            byte t = item2.mau.loai;
            if (t > 5) {
                this.moHopThoaiOK("Trang bị không phù hợp.");
                return;
            }
            if (t == 5 && this.isFlyAvenger() && this.y > 360) {
                this.moHopThoaiOK("Không thể thay đổi trang phục khi đang ở dưới đất.");
                return;
            }
            this.y = (short)360;
            if (this.itemBody[t] != null) {
                VXLVatPham item3 = this.itemBody[t];
                item2.chiSo = t;
                this.itemBody[t] = item2;
                this.itemBag[chiSo] = null;
                this.themVatPhamVaoTui(item3);
            } else {
                item2.chiSo = t;
                this.itemBody[t] = item2;
                this.itemBag[chiSo] = null;
            }
            if (t == 4) {
                int[] arrIndex = {};
                if (this.itemBody[t] != null) {
                    arrIndex = this.itemBalo;
                }
                int thamSo = item2.getParamById(13);
                this.itemBalo = new int[thamSo];
                for (int i = 0; i < this.itemBalo.length; i++) {
                    this.itemBalo[i] = -1;
                }
                for (int i = 0; i < arrIndex.length; i++) {
                    this.itemBalo[i] = arrIndex[i];
                }
                this.dichVu.guiBalo();
            }
            if (t == 5) {
                for (VXLVatPham ite : this.itemBody) {
                    this.datTrangBiChoNhanVat(ite);
                }
            } else {
                this.datTrangBiChoNhanVat(item2);
            }
            this.dichVu.guiTuiDo();
            this.dichVu.guiDoTrenNguoi();
            this.dichVu.doiTrangBi();
            Iterator<VXLNguoiChoi> iterator = this.zone.players_id.values().iterator();
            while (iterator.hasNext()) {
                VXLNguoiChoi p = iterator.next();
                if (p.equals(this)) continue;
                p.dichVu.vaoCho(this);
            }
            return;
        }
        if (loai == 5) {
            int param2;
            VXLVatPham item4 = this.itemBody[chiSo];
            if (item4 == null) return;
            byte t = item4.mau.loai;
            if (t != 0 && t != 4) {
                this.moHopThoaiOK("Không thể tháo trang bị này.");
                return;
            }
            int n = this.layOTrongTuiDo();
            if (t == 0) {
                if (n == 0) {
                    this.moHopThoaiOK("Túi đồ đã đầy.");
                    return;
                }
            } else if (t == 4 && n < (param2 = item4.getParamById(13)) + 1) {
                this.moHopThoaiOK("Túi đồ đã đầy.");
                return;
            }
            this.themVatPhamVaoTui(item4);
            this.itemBody[chiSo] = null;
            if (t == 0) {
                this.head = 0;
            } else {
                this.itemBalo = new int[0];
                this.wing = 0;
                this.dichVu.guiBalo();
            }
            this.dichVu.guiTuiDo();
            this.dichVu.guiDoTrenNguoi();
            if (this.itemBody[5] != null) {
                this.datTrangBiChoNhanVat(this.itemBody[5]);
            }
            this.dichVu.doiTrangBi();
            Iterator<VXLNguoiChoi> playerIt = this.zone.players_id.values().iterator();
            while (playerIt.hasNext()) {
                VXLNguoiChoi p = playerIt.next();
                if (p.equals(this)) continue;
                p.dichVu.vaoCho(this);
            }
            return;
        }
        if (loai == 1) {
            VXLVatPham item5 = this.itemBag[chiSo];
            if (item5 == null) return;
            if (this.vatPhamCoTrongBalo(item5)) {
                this.moHopThoaiOK("Vật phẩm đã gắn vào Balo.");
                return;
            }
            int slotNull = this.layOTrongRuong();
            if (slotNull == 0) {
                this.moHopThoaiOK("Rương đã đầy.");
                return;
            }
            this.themVatPhamVaoRuong(item5);
            this.itemBag[chiSo] = null;
            this.dichVu.guiTuiDo();
            return;
        }
        if (loai == 6) {
            vatPham = this.itemBag[chiSo];
            if (vatPham == null) return;
            byte t = vatPham.mau.loai;
            if (t != 10 && t != 5) {
                this.moHopThoaiOK("Không thể cho vật phẩm này vào balo.");
                return;
            }
            int n = this.layOTrongBalo();
            if (n == 0) {
                this.moHopThoaiOK("Balo đã đầy.");
                return;
            }
        } else {
            if (loai != 0) {
                if (loai != 7) return;
                this.itemBalo[chiSo] = -1;
                this.dichVu.guiBalo();
                return;
            }
            VXLVatPham item6 = this.itemBox[chiSo];
            if (item6 == null) return;
            byte t = item6.mau.loai;
            int n = this.layOTrongTuiDo();
            if (n == 0) {
                this.moHopThoaiOK("Túi đã đầy.");
                return;
            }
            this.themVatPhamVaoTui(item6);
            this.itemBox[chiSo] = null;
            this.dichVu.guiRuongDo();
            return;
        }
        for (int i = 0; i < this.itemBalo.length; ++i) {
            if (this.itemBalo[i] != -1) continue;
            this.itemBalo[i] = vatPham.chiSo;
            break;
        }
        this.dichVu.guiBalo();
    }

    public int soVatPhamTrongBalo() {
        int number = 0;
        if (this.itemBalo != null) {
            for (int chiSo : this.itemBalo) {
                if (chiSo == -1) continue;
                ++number;
            }
        }
        return number;
    }

    public boolean themVatPhamVaoTui(VXLVatPham vatPham) {
        try {
            int i;
            byte loai = vatPham.mau.loai;
            if (loai > 5) {
                for (i = 0; i < this.itemBag.length; ++i) {
                    if (this.itemBag[i] == null || this.itemBag[i].ma != vatPham.ma) continue;
                    this.itemBag[i].soLuong += vatPham.soLuong;
                    this.dichVu.capNhatTuiDo(i, this.itemBag[i].soLuong);
                    return true;
                }
            }
            for (i = 0; i < this.itemBag.length; ++i) {
                if (this.itemBag[i] != null) continue;
                vatPham.chiSo = i;
                this.itemBag[i] = vatPham;
                this.dichVu.guiTuiDo();
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void themVatPhamVaoRuong(VXLVatPham vatPham) {
        try {
            int i;
            byte loai = vatPham.mau.loai;
            if (loai > 5) {
                for (i = 0; i < this.itemBox.length; ++i) {
                    if (this.itemBox[i] == null || this.itemBox[i].ma != vatPham.ma) continue;
                    this.itemBox[i].soLuong += vatPham.soLuong;
                    this.dichVu.capNhatRuongDo(i, this.itemBox[i].soLuong);
                    return;
                }
            }
            for (i = 0; i < this.itemBox.length; ++i) {
                if (this.itemBox[i] != null) continue;
                vatPham.chiSo = i;
                this.itemBox[i] = vatPham;
                this.dichVu.guiRuongDo();
                return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moKhu() throws IOException {
        VXLTinNhan mss = new VXLTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(7);
        ds.writeByte(VXLBanDoRPG.zones.size());
        for (VXLKhu z : VXLBanDoRPG.zones) {
            ds.writeByte(z.zoneId);
            ds.writeByte(z.pts);
            ds.writeByte(z.numPlayer);
            ds.writeByte(z.maxPlayer);
        }
        ds.flush();
        this.dichVu.guiTin(mss);
    }

    public void chat(VXLTinNhan ms) throws IOException {
        String noiDung = ms.boDoc().readUTF();
        VXLTinNhan mss = new VXLTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(3);
        ds.writeByte(this.chiSo);
        ds.writeUTF(noiDung);
        ds.flush();
        this.zone.guiTatCaNguoiChoi(mss);
    }

    public void diChuyen(VXLTinNhan ms) throws IOException {
        this.x = ms.boDoc().readShort();
        this.y = ms.boDoc().readShort();
        if (!this.isFlyAvenger() && this.y != 360) {
            this.y = (short)360;
        }
        VXLTinNhan mss = new VXLTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(2);
        ds.writeByte(this.chiSo);
        ds.writeShort(this.x);
        ds.writeShort(this.y);
        ds.flush();
        this.zone.guiTatCaNguoiChoi(mss);
    }

    public void xemCuaHang(VXLCuaHang store) throws IOException {
        this.store = store;
        this.dichVu.xemCuaHang(this.store);
    }

    public void removeItem(int chiSo, int soLuong) {
        try {
            VXLVatPham vatPham = this.itemBag[chiSo];
            if (vatPham != null) {
                vatPham.soLuong -= soLuong;
                if (vatPham.soLuong > 0) {
                    this.itemBag[chiSo].soLuong = vatPham.soLuong;
                    this.dichVu.capNhatTuiDo(chiSo, vatPham.soLuong);
                } else {
                    this.itemBag[chiSo] = null;
                    this.dichVu.capNhatTuiDo(chiSo, 0);
                }
            } else {
                this.dichVu.capNhatTuiDo(chiSo, 0);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateGold(int vang) {
        this.vang += vang;
        this.dichVu.capNhat();
    }

    public void updateGem(int ngoc) {
        this.ngoc += ngoc;
        this.dichVu.capNhat();
    }

    public void requestTab(VXLTinNhan ms) throws IOException {
        if (this.store == null) {
            return;
        }
        byte chiSo = ms.boDoc().readByte();
        byte page = ms.boDoc().readByte();
        if (chiSo < 0 || page < 0 || chiSo >= this.store.tabs.size() || page >= this.store.tabs.get(chiSo).size()) {
            this.moHopThoaiOK("Co loi xay ra.");
            return;
        }
        ms = new VXLTinNhan(-43);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeByte(page);
        ArrayList<VXLTrang> pages = this.store.tabs.get(chiSo);
        ds.writeByte(pages.size());
        VXLTrang p = pages.get(page);
        ds.writeByte(p.vatPhams.size());
        for (VXLMauVatPham t : p.vatPhams) {
            ds.writeShort(t.ma);
            ds.writeInt(t.buyGold);
            ds.writeInt(t.buyGem);
            int numberOption = t.thuocTinhs.size();
            ds.writeByte(numberOption);
            for (int b = 0; b < numberOption; ++b) {
                VXLThuocTinhVatPham option = (VXLThuocTinhVatPham)t.thuocTinhs.get(b);
                ds.writeByte(option.optionTemplate.ma);
                ds.writeShort(option.thamSo);
            }
        }
        ds.flush();
        this.dichVu.guiTin(ms);
    }

    public void moHopThoaiOK(String noiDung) {
        try {
            this.dichVu.moHopThoaiOK(noiDung);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startOKDlg2(String noiDung) {
        try {
            this.dichVu.baoLoiTien(noiDung);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isFlyAvenger() {
        return this.avenger == 1 || this.avenger == 8;
    }

    public static void onChatFromToAllPlayer(String ten, String noiDung) {
        try {
            VXLTinNhan mss = new VXLTinNhan(5);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(-1);
            ds.writeUTF(ten);
            ds.writeUTF(noiDung);
            ds.flush();
            VXLNguoiChoi.guiMayChu(mss);
        }
        catch (IOException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void chatTo(VXLTinNhan ms) throws IOException {
        int ma = ms.boDoc().readInt();
        String noiDung = ms.boDoc().readUTF();
        if (ma == -1) {
            if (this.ngoc < 10) {
                this.moHopThoaiOK("Bạn không đủ ngọc để chat thế giới.");
                return;
            }
            this.updateGem(-10);
            VXLNguoiChoi.onChatFromToAllPlayer(this.ten, noiDung);
        } else {
            VXLNguoiChoi pl = VXLNguoiChoi.layNguoiChoiTheoMa(ma);
            if (pl != null) {
                VXLTinNhan mss = new VXLTinNhan(5);
                DataOutputStream ds = mss.boGhi();
                ds.writeInt(this.ma);
                ds.writeUTF(this.ten);
                ds.writeUTF(noiDung);
                ds.flush();
                pl.dichVu.guiTin(mss);
            }
        }
    }

    public void viewPlayerInfo(VXLTinNhan ms) throws IOException {
        int ma = ms.boDoc().readInt();
        VXLNguoiChoi pl = VXLNguoiChoi.layNguoiChoiTheoMa(ma);
        if (pl != null) {
            VXLTinNhan mss = new VXLTinNhan(-126);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(pl.ma);
            ds.writeUTF(pl.ten);
            ds.writeShort(pl.head);
            ds.writeShort(pl.hat);
            ds.writeShort(pl.body);
            ds.writeShort(pl.leg);
            ds.writeShort(pl.wing);
            ds.writeShort(pl.wp);
            ds.writeInt(pl.kinhNghiem);
            ds.writeByte(1);
            ds.writeShort(0);
            ds.flush();
            this.dichVu.guiTin(mss);
        }
    }

    public void flushCache() {
        try (java.sql.Connection conn = VXLCoSoDuLieu.getConnection()) {
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE `players` SET `gold` = ?, `cup` = ?, `gem` = ? WHERE `id` = ? LIMIT 1;")) {
                stmt.setInt(1, this.vang);
                stmt.setInt(2, this.cup);
                stmt.setInt(3, this.ngoc);
                stmt.setInt(4, this.ma);
                stmt.execute();
            }
            JSONObject duLieu = new JSONObject();
            duLieu.put("power", this.power);
            duLieu.put("avenger", this.powerAvenger);
            duLieu.put("kill", this.kill);
            duLieu.put("dead", this.chet);
            duLieu.put("assist", this.assist);
            duLieu.put("trainingSuccess", this.trainingSuccess);
            duLieu.put("busyHammer", this.busyHammer);
            duLieu.put("nHammer", this.nHammer);
            duLieu.put("exp", this.kinhNghiem);
            duLieu.put("point", this.point);
            JSONArray pointAdds = new JSONArray();
            for (short s : this.pointAdd) {
                pointAdds.add(s);
            }
            duLieu.put("pointAdd", pointAdds);
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE `players` SET `stats_json` = ? WHERE `id` = ? LIMIT 1;")) {
                stmt.setString(1, duLieu.toJSONString());
                stmt.setInt(2, this.ma);
                stmt.execute();
            }
            JSONArray body = new JSONArray();
            for (VXLVatPham vatPham : this.itemBody) {
                if (vatPham != null) {
                    body.add(vatPham.toJSONObject());
                }
            }
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE `players` SET `equipped_json` = ? WHERE `id` = ? LIMIT 1;")) {
                stmt.setString(1, body.toJSONString());
                stmt.setInt(2, this.ma);
                stmt.execute();
            }
            JSONArray bag = new JSONArray();
            for (VXLVatPham vatPham : this.itemBag) {
                if (vatPham != null) {
                    bag.add(vatPham.toJSONObject());
                }
            }
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE `players` SET `inventory_json` = ? WHERE `id` = ? LIMIT 1;")) {
                stmt.setString(1, bag.toJSONString());
                stmt.setInt(2, this.ma);
                stmt.execute();
            }
            JSONArray balo = new JSONArray();
            for (int chiSo : this.itemBalo) {
                balo.add(chiSo);
            }
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE `players` SET `pocket_json` = ? WHERE `id` = ? LIMIT 1;")) {
                stmt.setString(1, balo.toJSONString());
                stmt.setInt(2, this.ma);
                stmt.execute();
            }
            JSONArray box = new JSONArray();
            for (VXLVatPham vatPham : this.itemBox) {
                if (vatPham != null) {
                    box.add(vatPham.toJSONObject());
                }
            }
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE `players` SET `storage_json` = ? WHERE `id` = ? LIMIT 1;")) {
                stmt.setString(1, box.toJSONString());
                stmt.setInt(2, this.ma);
                stmt.execute();
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        this.dungVongBotLuyenTap();
        this.inTraining = false;
        VXLQuanLyPhong.roiBanCho(this);
        VXLBanDoRPG.roi(this);
        VXLNguoiChoi.xoa(this.ma);
        this.flushCache();
    }

    public void vaoLuyenTap() {
        try {
            this.trainingSuccess = 1;
            this.isReady = true;
            this.chiSo = 0;
            this.pointSeat = 0;
            this.inTraining = true;
            this.trainingDummyHp = 100;
            this.trainingPlayerHp = 100;
            this.trainingWaitingShotEnd = false;
            this.trainingPendingHitDummy = false;
            this.trainingFirstTurnSent = false;
            this.trainingBotAnimating = false;
            this.trainingBossShield = false;
            this.trainingBossPowerShot = false;
            this.trainingBotTurnCount = 0;
            this.dungVongBotLuyenTap();
            this.trainingPlayerX = 220;
            this.trainingPlayerY = 300;
            this.trainingDummyX = 600;
            this.trainingDummyY = 300;
            this.trainingPendingHitBot = -1;
            this.trainingBotTurn = -1;
            for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
                this.trainingBotHp[i] = 100;
                this.trainingBotX[i] = TRAINING_BOT_SPAWN_X[i];
                this.trainingBotY[i] = TRAINING_BOT_SPAWN_Y[i];
                this.trainingBotDead[i] = false;
            }
            byte maBanDo = 1;
            short trainingWeapon = this.wp;
            if (trainingWeapon <= 0) {
                trainingWeapon = 5;
            }
            this.wp = trainingWeapon;
            this.dichVu.guiThongTinLuyenTap();
            this.dichVu.guiChonBanDoLuyenTap(maBanDo);
            this.dichVu.guiNguoiChoiLuyenTap((byte)0, this.ma, this.ten, this.head, this.leg, this.body,
                    this.hat, this.wing, trainingWeapon, this.avenger, this.ma);
            for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
                this.dichVu.guiNguoiChoiLuyenTap((byte)(i + 1), -9999 - i, TRAINING_BOT_NAMES[i], this.head, this.leg, this.body,
                        this.hat, this.wing, TRAINING_BOT_WEAPONS[i], TRAINING_BOT_AVENGERS[i], this.ma);
            }
            this.dichVu.guiBatDauLuyenTap(maBanDo, trainingWeapon, this.trainingBotX, this.trainingBotY, this.trainingBotHp, TRAINING_BOT_WEAPONS);
        }
        catch (Exception ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void handleTrainingMove(VXLTinNhan ms) throws IOException {
        short moveX = ms.boDoc().readShort();
        short moveY = ms.boDoc().readShort();
        if (!this.inTraining) {
            return;
        }
        this.trainingPlayerX = this.kepShort(moveX, 0, 1200);
        this.trainingPlayerY = this.kepShort(moveY, 0, 700);
        this.dichVu.guiCapNhatXYLuyenTap((byte)0, this.trainingPlayerX, this.trainingPlayerY);
    }

    public void xuLyBanLuyenTap(VXLTinNhan ms) throws IOException {
        if (!this.inTraining) {
            System.out.println("[Training] Ignore player fire: not in training player=" + this.ten);
            return;
        }
        if (this.trainingBotAnimating) {
            System.out.println("[Training] Ignore player fire: bot animating player=" + this.ten);
            return;
        }
        this.cancelTrainingBotTask();
        long now = System.currentTimeMillis();
        if (now - this.lastTrainingFire < 250L) {
            System.out.println("[Training] Ignore player fire: too fast player=" + this.ten);
            return;
        }
        this.lastTrainingFire = now;
        byte loaiDan = ms.boDoc().readByte();
        short fireX = ms.boDoc().readShort();
        short fireY = ms.boDoc().readShort();
        short goc = ms.boDoc().readShort();
        byte luc = ms.boDoc().readByte();
        if (this.isDoubleTrainingBullet(loaiDan)) {
            ms.boDoc().readByte();
        }
        ms.boDoc().readByte();
        if (luc <= 0) {
            luc = 10;
        }
        if (luc > 30) {
            luc = 30;
        }
        System.out.println("[Training] Player fire name=" + this.ten + " bullet=" + loaiDan + " x=" + fireX + " y=" + fireY + " angle=" + goc + " force=" + luc);
        this.trainingPlayerX = this.kepShort(fireX, 0, 1200);
        this.trainingPlayerY = this.kepShort(fireY, 0, 700);
        short[][] duongDan = this.taoDuongDanLuyenTap(this.trainingPlayerX, this.trainingPlayerY, goc, luc);
        short[] duongX = duongDan[0];
        short[] duongY = duongDan[1];
        this.trainingPendingHitBot = this.layBotTrungDuongLuyenTap(duongX, duongY);
        this.trainingPendingHitDummy = this.trainingPendingHitBot >= 0;
        this.trainingWaitingShotEnd = true;
        this.dichVu.guiKetQuaBanLuyenTap((byte)0, this.layLoaiDanLuyenTapAnToan(loaiDan), this.trainingPlayerX, this.trainingPlayerY, goc, luc, duongX, duongY);
        this.scheduleTrainingPlayerResolve(this.trainingPendingHitDummy, 1500L);
    }

    public void xuLyVaChamLuyenTap(VXLTinNhan ms) throws IOException {
        boolean hitDummyByClientExplosion = false;
        int validExplodeCount = 0;
        try {
            byte n = ms.boDoc().readByte();
            for (int i = 0; i < n; ++i) {
                int boomX = ms.boDoc().readInt();
                int boomY = ms.boDoc().readInt();
                if (boomX >= 0 && boomY >= 0) {
                    ++validExplodeCount;
                    if (this.diemLuyenTapTrungBia(boomX, boomY)) {
                        hitDummyByClientExplosion = true;
                    }
                }
            }
        }
        catch (Exception ignored) {
        }
        if (!this.inTraining || !this.trainingWaitingShotEnd) {
            return;
        }
        boolean hit = hitDummyByClientExplosion;
        if (!hit && validExplodeCount == 0) {
            hit = this.trainingPendingHitDummy;
        }
        System.out.println("[Training] Player cross name=" + this.ten + " hit=" + hit + " explosions=" + validExplodeCount);
        this.xuLyPhatBanNguoiChoiLuyenTap(hit);
    }

    private synchronized void scheduleTrainingPlayerResolve(boolean hit, long delayMs) {
        if (this.trainingPlayerResolveTask != null) {
            this.trainingPlayerResolveTask.cancel(false);
        }
        this.trainingPlayerResolveTask = TRAINING_BOT_EXECUTOR.schedule(() -> {
            try {
                System.out.println("[Training] Fallback resolve player fire name=" + this.ten + " hit=" + hit);
                this.xuLyPhatBanNguoiChoiLuyenTap(hit);
            }
            catch (Exception ex) {
                Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    private synchronized void xuLyPhatBanNguoiChoiLuyenTap(boolean hit) throws IOException {
        if (!this.inTraining || !this.trainingWaitingShotEnd) {
            return;
        }
        if (this.trainingPlayerResolveTask != null) {
            this.trainingPlayerResolveTask.cancel(false);
            this.trainingPlayerResolveTask = null;
        } else {
            System.out.println("[Training] Resolve player fire by fallback name=" + this.ten + " hit=" + hit);
        }
        if (hit) {
            int satThuong = this.trainingBossShield ? 8 : 20;
            this.trainingBossShield = false;
            int botIndex = this.trainingPendingHitBot >= 0 ? this.trainingPendingHitBot : this.layBotLuyenTapSongGanNhat();
            if (botIndex >= 0) {
                this.trainingBotHp[botIndex] -= satThuong;
                if (this.trainingBotHp[botIndex] <= 0) {
                    this.trainingBotHp[botIndex] = 0;
                    this.trainingBotDead[botIndex] = true;
                }
                this.dichVu.guiCapNhatMauLuyenTap((byte)(botIndex + 1), this.trainingBotHp[botIndex], this.trainingBotDead[botIndex] ? (byte)2 : (byte)0);
            }
        }
        this.trainingWaitingShotEnd = false;
        this.trainingPendingHitDummy = false;
        this.trainingPendingHitBot = -1;
        this.scheduleTrainingBotShot(700L);
    }

    public void handleTrainingHoleRequest(VXLTinNhan ms) throws IOException {
        if (this.inTraining) {
            this.dichVu.guiDatLaiHoLuyenTap();
        }
    }

    public void handleTrainingClientReady() throws IOException {
        this.dichVu.guiHienManHinhGameLuyenTap();
        if (this.inTraining && !this.trainingFirstTurnSent) {
            this.trainingFirstTurnSent = true;
            this.dichVu.guiLuotLuyenTapTiep((byte)0, this.trainingPlayerX, this.trainingPlayerY);
        }
    }

    private short kepShort(short giaTri, int nhoNhat, int lonNhat) {
        int v = giaTri;
        if (v < nhoNhat) {
            v = nhoNhat;
        }
        if (v > lonNhat) {
            v = lonNhat;
        }
        return (short)v;
    }

    private boolean isDoubleTrainingBullet(byte loaiDan) {
        return loaiDan == 17 || loaiDan == 19;
    }

    private byte layLoaiDanLuyenTapAnToan(byte loaiDan) {
        switch (loaiDan) {
            case 0:
            case 7:
            case 8:
            case 13:
            case 21:
            case 22:
            case 25:
            case 30:
            case 34:
            case 35:
            case 42:
            case 45:
            case 50:
            case 51:
            case 52:
            case 54:
            case 55:
            case 57:
            case 58:
                return loaiDan;
            default:
                return 0;
        }
    }

    private short[][] taoDuongDanLuyenTap(short batDauX, short batDauY, short goc, byte luc) {
        final int maxPoints = 36;
        short[] xs = new short[maxPoints];
        short[] ys = new short[maxPoints];
        double rad = Math.toRadians(goc);
        double speed = Math.max(8, luc) * 0.85D;
        double gravity = 0.33D;
        int len = 0;
        for (int i = 0; i < maxPoints; ++i) {
            double t = i;
            int px = (int)Math.round(batDauX + Math.cos(rad) * speed * t);
            int py = (int)Math.round(batDauY - Math.sin(rad) * speed * t + gravity * t * t);
            boolean outOfBounds = px < 0 || px > 1200 || py < 0 || py > 700;
            px = Math.max(0, Math.min(1200, px));
            py = Math.max(0, Math.min(700, py));
            xs[i] = (short)px;
            ys[i] = (short)py;
            len = i + 1;
            if (outOfBounds || this.diemLuyenTapTrungBia(px, py)) {
                break;
            }
        }
        return this.trimTrainingPath(xs, ys, len);
    }

    private short[][] taoDuongDanThangLuyenTap(short batDauX, short batDauY, short targetX, short targetY) {
        int dx = targetX - batDauX;
        int dy = targetY - batDauY;
        int steps = Math.max(8, Math.min(24, Math.max(Math.abs(dx), Math.abs(dy)) / 24));
        short[] xs = new short[steps];
        short[] ys = new short[steps];
        for (int i = 0; i < steps; i++) {
            double t = (double)i / (double)(steps - 1);
            xs[i] = (short)Math.round(batDauX + dx * t);
            ys[i] = (short)Math.round(batDauY + dy * t);
        }
        return new short[][]{xs, ys};
    }

    private short[][] trimTrainingPath(short[] xs, short[] ys, int len) {
        len = Math.max(1, Math.min(len, xs.length));
        short[] trimX = new short[len];
        short[] trimY = new short[len];
        System.arraycopy(xs, 0, trimX, 0, len);
        System.arraycopy(ys, 0, trimY, 0, len);
        return new short[][]{trimX, trimY};
    }

    private boolean duongLuyenTapTrungBia(short[] xs, short[] ys) {
        return this.layBotTrungDuongLuyenTap(xs, ys) >= 0;
    }

    private int layBotTrungDuongLuyenTap(short[] xs, short[] ys) {
        for (int i = 0; i < xs.length; ++i) {
            int bot = this.layBotTrungDiemLuyenTap(xs[i], ys[i]);
            if (bot >= 0) {
                return bot;
            }
        }
        return -1;
    }

    private boolean diemLuyenTapTrungBia(int pointX, int pointY) {
        return this.layBotTrungDiemLuyenTap(pointX, pointY) >= 0;
    }

    private int layBotTrungDiemLuyenTap(int pointX, int pointY) {
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            if (this.trainingBotDead[i]) {
                continue;
            }
            int dx = pointX - this.trainingBotX[i];
            int dy = pointY - this.trainingBotY[i];
            if (dx * dx + dy * dy <= 42 * 42) {
                return i;
            }
        }
        return -1;
    }

    private int layBotLuyenTapSongGanNhat() {
        int best = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            if (this.trainingBotDead[i]) {
                continue;
            }
            int dx = this.trainingBotX[i] - this.trainingPlayerX;
            int dy = this.trainingBotY[i] - this.trainingPlayerY;
            int distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
    }

    private synchronized void scheduleTrainingBotShot(long delayMs) {
        if (!this.inTraining) {
            return;
        }
        this.cancelTrainingBotTask();
        this.trainingBotTask = TRAINING_BOT_EXECUTOR.schedule(this::runTrainingBotShot, delayMs, TimeUnit.MILLISECONDS);
    }

    private synchronized void cancelTrainingBotTask() {
        if (this.trainingBotTask != null) {
            this.trainingBotTask.cancel(false);
            this.trainingBotTask = null;
        }
    }

    private synchronized void dungVongBotLuyenTap() {
        this.cancelTrainingBotTask();
        if (this.trainingBotReturnTask != null) {
            this.trainingBotReturnTask.cancel(false);
            this.trainingBotReturnTask = null;
        }
        if (this.trainingPlayerResolveTask != null) {
            this.trainingPlayerResolveTask.cancel(false);
            this.trainingPlayerResolveTask = null;
        }
        this.trainingBotAnimating = false;
    }

    private void runTrainingBotShot() {
        try {
            synchronized (this) {
                this.trainingBotTask = null;
                if (!this.inTraining || this.trainingWaitingShotEnd || this.trainingBotAnimating) {
                    return;
                }
                this.trainingBotAnimating = true;
                this.botLuyenTapBanTra();
                if (this.trainingBotReturnTask != null) {
                    this.trainingBotReturnTask.cancel(false);
                }
                this.trainingBotReturnTask = TRAINING_BOT_EXECUTOR.schedule(this::finishTrainingBotShot, 1100L, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void finishTrainingBotShot() {
        try {
            synchronized (this) {
                this.trainingBotReturnTask = null;
                if (!this.inTraining) {
                    this.trainingBotAnimating = false;
                    return;
                }
                this.trainingBotAnimating = false;
                this.dichVu.guiLuotLuyenTapTiep((byte)0, this.trainingPlayerX, this.trainingPlayerY);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(VXLNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void botLuyenTapBanTra() throws IOException {
        int botIndex = this.botLuyenTapTiep();
        if (botIndex < 0) {
            this.datLaiBotLuyenTap();
            botIndex = this.botLuyenTapTiep();
            if (botIndex < 0) {
                return;
            }
        }
        this.diChuyenBotLuyenTap(botIndex);
        this.bossLuyenTapDungVatPhamNeuCan(botIndex);
        byte loaiDan = 0;
        byte luc = 18;
        short goc = this.gocToiMucTieu(this.trainingBotX[botIndex], this.trainingBotY[botIndex], this.trainingPlayerX, this.trainingPlayerY);
        short[][] duongDan = this.taoDuongDanThangLuyenTap(this.trainingBotX[botIndex], this.trainingBotY[botIndex], this.trainingPlayerX, this.trainingPlayerY);
        this.dichVu.guiKetQuaBanLuyenTap((byte)(botIndex + 1), loaiDan, this.trainingBotX[botIndex], this.trainingBotY[botIndex], goc, luc, duongDan[0], duongDan[1]);
        if (this.duongLuyenTapTrungNguoiChoi(duongDan[0], duongDan[1])) {
            int satThuong = this.trainingBossPowerShot ? 18 : 10;
            this.trainingBossPowerShot = false;
            this.trainingPlayerHp -= satThuong;
            if (this.trainingPlayerHp <= 0) {
                this.trainingPlayerHp = 100;
            }
            this.dichVu.guiCapNhatMauLuyenTap((byte)0, this.trainingPlayerHp, (byte)0);
        }
    }

    private int botLuyenTapTiep() {
        for (int step = 1; step <= TRAINING_BOT_COUNT; step++) {
            int chiSo = (this.trainingBotTurn + step + TRAINING_BOT_COUNT) % TRAINING_BOT_COUNT;
            if (!this.trainingBotDead[chiSo]) {
                this.trainingBotTurn = chiSo;
                return chiSo;
            }
        }
        return -1;
    }

    private void datLaiBotLuyenTap() throws IOException {
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            this.trainingBotHp[i] = 100;
            this.trainingBotDead[i] = false;
            this.dichVu.guiCapNhatMauLuyenTap((byte)(i + 1), this.trainingBotHp[i], (byte)0);
        }
    }

    private void diChuyenBotLuyenTap(int botIndex) throws IOException {
        int shift = botIndex % 2 == 0 ? 22 : -22;
        this.trainingBotX[botIndex] = this.kepShort((short)(this.trainingBotX[botIndex] + shift), 80, 1120);
        this.dichVu.guiCapNhatXYLuyenTap((byte)(botIndex + 1), this.trainingBotX[botIndex], this.trainingBotY[botIndex]);
    }

    private void bossLuyenTapDungVatPhamNeuCan(int botIndex) throws IOException {
        this.trainingBotTurnCount++;
        if (this.trainingBotHp[botIndex] <= 60 && this.trainingBotTurnCount % 2 == 1) {
            this.trainingBotHp[botIndex] = Math.min(100, this.trainingBotHp[botIndex] + 25);
            this.dichVu.guiDungVatPhamLuyenTap((byte)(botIndex + 1), (byte)10, (short)0);
            this.dichVu.guiCapNhatMauLuyenTap((byte)(botIndex + 1), this.trainingBotHp[botIndex], (byte)0);
            return;
        }
        if (this.trainingBotTurnCount % 3 == 0) {
            this.trainingBossShield = true;
            this.dichVu.guiDungVatPhamLuyenTap((byte)(botIndex + 1), (byte)0, (short)0);
            return;
        }
        if (this.trainingBotTurnCount % 2 == 0) {
            this.trainingBossPowerShot = true;
            this.dichVu.guiDungVatPhamLuyenTap((byte)(botIndex + 1), (byte)5, (short)0);
        }
    }

    private short gocToiMucTieu(short batDauX, short batDauY, short targetX, short targetY) {
        double radians = Math.atan2(batDauY - targetY, targetX - batDauX);
        int degrees = (int)Math.round(Math.toDegrees(radians));
        if (degrees < 0) {
            degrees += 360;
        }
        return (short)degrees;
    }

    private boolean duongLuyenTapTrungNguoiChoi(short[] xs, short[] ys) {
        for (int i = 0; i < xs.length; ++i) {
            int dx = xs[i] - this.trainingPlayerX;
            int dy = ys[i] - this.trainingPlayerY;
            if (dx * dx + dy * dy <= 42 * 42) {
                return true;
            }
        }
        return false;
    }
}
