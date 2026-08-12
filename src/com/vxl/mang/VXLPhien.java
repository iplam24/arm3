package com.vxl.mang;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.bando.VXLDuLieuBanDo;
import com.vxl.mohinh.VXLNguoiChoi;
import com.vxl.mohinh.VXLNguoiDung;
import com.vxl.mang.VXLDichVuGame;
import com.vxl.mang.IVXLDichVuGame;
import com.vxl.mang.IVXLXuLyTin;
import com.vxl.mang.IVXLPhien;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mang.VXLXuLyTin;
import com.vxl.nhapvai.VXLBanDoRPG;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.concurrent.ScheduledFuture;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VXLPhien
implements IVXLPhien {
    private static final int MAX_PACKET_SIZE = 32 * 1024;
    private static final int MAX_LARGE_PACKET_SIZE = 16 * 1024 * 1024;
    private static final int MAX_RESEND_MESSAGES = 200;
    private final byte[] khoa = new byte[]{0};
    public Channel kenh;
    public int ma;
    public VXLNguoiDung user;
    private IVXLXuLyTin boXuLyTin;
    private IVXLDichVuGame dichVu;
    protected boolean daKetNoi;
    protected boolean dangNhap;
    private byte curR;
    private byte curW;
    protected String phienBan;
    protected byte loaiKhach;
    protected byte mucPhong;
    protected byte nhaCungCap;
    protected boolean heThongXong;
    protected int svReceived_clSended;
    protected int svSended_clReceived;
    protected final List<VXLTinNhan> vResendMessage = new ArrayList<VXLTinNhan>();
    public long timeConnected;
    private static final long SESSION_TTL_MILLIS = TimeUnit.MINUTES.toMillis(5L);
    public static final ConcurrentMap<String, Count> sessions = new ConcurrentHashMap<>();
    private String maPhien;
    private volatile boolean kichHoat = true;
    private ScheduledFuture<?> tacVuGiuKetNoi;

    public VXLPhien(Channel kenh, int ma) {
        this.kenh = kenh;
        this.ma = ma;
        this.datBoXuLy(new VXLXuLyTin(this));
        this.datDichVu(new VXLDichVuGame(this));
    }

    public VXLTinNhan thuGiaiMaTin(ByteBuf in) {
        if (in == null || in.readableBytes() < 1) {
            return null;
        }
        in.markReaderIndex();
        byte oldCurR = this.curR;
        try {
            byte cmd = in.readByte();
            if (this.daKetNoi) {
                cmd = this.docKhoa(cmd);
            }
            if (in.readableBytes() < 2) {
                in.resetReaderIndex();
                this.curR = oldCurR;
                return null;
            }
            int kichThuoc;
            if (this.daKetNoi) {
                byte b1 = in.readByte();
                byte b2 = in.readByte();
                kichThuoc = (this.docKhoa(b1) & 0xFF) << 8 | this.docKhoa(b2) & 0xFF;
            } else {
                kichThuoc = in.readUnsignedShort();
            }
            if (kichThuoc < 0 || kichThuoc > MAX_PACKET_SIZE) {
                throw new CorruptedFrameException("Packet size exceeds " + MAX_PACKET_SIZE + " bytes.");
            }
            if (in.readableBytes() < kichThuoc) {
                in.resetReaderIndex();
                this.curR = oldCurR;
                return null;
            }
            byte[] duLieu = new byte[kichThuoc];
            in.readBytes(duLieu);
            if (this.daKetNoi) {
                for (int i = 0; i < duLieu.length; ++i) {
                    duLieu[i] = this.docKhoa(duLieu[i]);
                }
            }
            return new VXLTinNhan(cmd, duLieu);
        }
        catch (CorruptedFrameException ex) {
            throw ex;
        }
        catch (Exception ex) {
            this.curR = oldCurR;
            throw new CorruptedFrameException("Unable to decode packet.", ex);
        }
    }

    public void maHoaTin(VXLTinNhan m, ByteBuf out) {
        if (m == null || out == null) {
            throw new IllegalArgumentException("Message and output buffer must not be null.");
        }
        byte[] duLieu = m.layDuLieu();
        if (duLieu == null) {
            duLieu = new byte[0];
        }
        byte b = m.layLenh();
        boolean maHoa = this.daKetNoi && b != -27;
        if (maHoa) {
            out.writeByte((int)this.ghiKhoa(b));
        } else {
            out.writeByte((int)b);
        }
        if (this.laTinLon(b)) {
            this.maHoaTinLon(duLieu, out);
            if (!VXLPhien.laTinDacBiet(m)) {
                ++this.svSended_clReceived;
            }
            m.donDep();
            return;
        }
        int kichThuoc = duLieu.length;
        if (kichThuoc > MAX_PACKET_SIZE || kichThuoc > 0xFFFF) {
            throw new IllegalArgumentException("Invalid packet size: " + kichThuoc);
        }
        if (maHoa) {
            out.writeByte((int)this.ghiKhoa((byte)(kichThuoc >> 8)));
            out.writeByte((int)this.ghiKhoa((byte)(kichThuoc & 0xFF)));
            byte[] encrypted = new byte[duLieu.length];
            for (int i = 0; i < duLieu.length; ++i) {
                encrypted[i] = this.ghiKhoa(duLieu[i]);
            }
            out.writeBytes(encrypted);
        } else {
            out.writeByte(kichThuoc >> 8);
            out.writeByte(kichThuoc & 0xFF);
            out.writeBytes(duLieu);
        }
        if (!VXLPhien.laTinDacBiet(m)) {
            ++this.svSended_clReceived;
        }
        m.donDep();
    }

    private void maHoaTinLon(byte[] duLieu, ByteBuf out) {
        if (duLieu == null || duLieu.length > MAX_LARGE_PACKET_SIZE) {
            throw new IllegalArgumentException("Invalid large packet size: " + (duLieu == null ? 0 : duLieu.length));
        }
        int kichThuoc = duLieu.length;
        out.writeByte(kichThuoc >> 24);
        out.writeByte(kichThuoc >> 16);
        out.writeByte(kichThuoc >> 8);
        out.writeByte(kichThuoc & 0xFF);
        out.writeBytes(duLieu);
    }

    public void khiNhanTin(VXLTinNhan tin) {
        if (tin == null) {
            return;
        }
        if (!this.kichHoat) {
            tin.donDep();
            return;
        }
        try {
            if (tin.layLenh() == -27) {
                this.guiKhoa();
            } else if (tin.layLenh() == -127) {
                this.dongBo(tin);
            } else {
                if (!VXLPhien.laTinDacBiet(tin)) {
                    ++this.svReceived_clSended;
                }
                this.boXuLyTin.khiCoTin(tin);
            }
        }
        catch (Exception e) {
            VXLQuanLyMayChu.log("Error handling message from " + String.valueOf(this) + ": " + e.getMessage());
            this.dongTin();
        }
        finally {
            tin.donDep();
        }
    }

    public void khiKenhNgat() {
        if (this.kichHoat) {
            this.dongTin();
        }
    }

    private void dongBo(VXLTinNhan ms) throws IOException {
        byte loai = ms.boDoc().readByte();
        if (loai == 0) {
            String oldSessionId = ms.docUTF(64, "mã phiên");
            int clSended = ms.boDoc().readInt();
            int clReceived = ms.boDoc().readInt();
            if (clSended < 0 || clReceived < 0) {
                throw new IllegalArgumentException("Bộ đếm đồng bộ không hợp lệ.");
            }
            if (oldSessionId.isBlank()) {
                throw new IllegalArgumentException("Session id must not be blank.");
            }
            Count count = sessions.remove(oldSessionId);
            if (count == null || count.hetHan()) {
                this.guiMaPhien(0);
                return;
            }
            this.svReceived_clSended = count.svReceived_clSended;
            this.svSended_clReceived = count.svSended_clReceived;
            synchronized (this.vResendMessage) {
                this.vResendMessage.clear();
                if (count.vResendMessage != null) {
                    this.vResendMessage.addAll(count.vResendMessage);
                }
            }
            this.guiMaPhien(1);
            if (clReceived > this.svSended_clReceived) {
                this.guiMaPhien(0);
                return;
            }
            if (clReceived != this.svSended_clReceived) {
                int soTinThieu = this.svSended_clReceived - clReceived;
                int soTinCoTheGuiLai;
                synchronized (this.vResendMessage) {
                    soTinCoTheGuiLai = this.vResendMessage.size();
                }
                int chiSo = Math.max(0, soTinCoTheGuiLai - soTinThieu);
                this.guiLaiTinTu(chiSo);
            }
        } else if (loai == 2) {
            this.heThongXong = true;
            this.svSended_clReceived = 0;
            this.svReceived_clSended = 0;
            this.vResendMessage.clear();
        }
    }

    public void datLoaiKhach(VXLTinNhan mss) throws IOException {
        this.loaiKhach = mss.boDoc().readByte();
        int mucPhong = Byte.toUnsignedInt(mss.boDoc().readByte());
        if (mucPhong < 1 || mucPhong > VXLQuanLyMayChu.dataSize.length) {
            throw new IllegalArgumentException("Client resource version is invalid: " + mucPhong);
        }
        this.mucPhong = (byte)mucPhong;
        this.phienBan = mss.docUTF(32, "phien ban");
        ((VXLDichVuGame)this.dichVu).hienTaiXuong();
    }

    protected void guiVaChamBanDo() throws IOException {
        VXLTinNhan ms = new VXLTinNhan(92);
        DataOutputStream ds = ms.boGhi();
        ds.writeShort(VXLDuLieuBanDo.undestroyTile.length);
        for (int i = 0; i < VXLDuLieuBanDo.undestroyTile.length; ++i) {
            ds.writeShort(VXLDuLieuBanDo.undestroyTile[i]);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void taiXuong() throws IOException {
        VXLDichVuGame sv = (VXLDichVuGame)this.dichVu;
        sv.taiXuong();
        File[] files = new File("res/data/" + this.mucPhong + "/").listFiles(File::isFile);
        if (files == null) {
            return;
        }
        java.util.Arrays.sort(files, java.util.Comparator.comparing(File::getName));
        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file);){
                byte[] duLieu = fis.readAllBytes();
                String ten = file.getName();
                if (ten.toLowerCase().endsWith(".png")) {
                    ten = ten.substring(0, ten.length() - 4);
                }
                sv.guiTep(ten, duLieu);
            }
        }
    }

    public void datNhaCungCap(VXLTinNhan ms) throws IOException {
        this.nhaCungCap = ms.boDoc().readByte();
    }

    @Override
    public boolean dangKetNoi() {
        return this.daKetNoi && this.kichHoat && this.kenh != null && this.kenh.isActive();
    }

    @Override
    public void datBoXuLy(IVXLXuLyTin boXuLyTin) {
        this.boXuLyTin = boXuLyTin;
    }

    @Override
    public void datDichVu(IVXLDichVuGame dichVu) {
        this.dichVu = dichVu;
    }

    public IVXLDichVuGame layDichVu() {
        return this.dichVu;
    }

    @Override
    public void guiTin(VXLTinNhan tin) {
        if (tin == null || !this.kichHoat) {
            return;
        }
        if (!VXLPhien.laTinDacBiet(tin)) {
            synchronized (this.vResendMessage) {
                if (this.vResendMessage.size() >= MAX_RESEND_MESSAGES) {
                    this.vResendMessage.remove(0);
                }
                this.vResendMessage.add(tin);
            }
        }
        this.dayTin(tin);
    }

    private void dayTin(VXLTinNhan tin) {
        if (this.kenh != null && this.kenh.isActive()) {
            this.kenh.writeAndFlush((Object)tin);
        }
    }

    private static boolean laTinDacBiet(VXLTinNhan tin) {
        return tin != null && (tin.layLenh() == -27 || tin.layLenh() == -127 || tin.layLenh() == -98 || tin.layLenh() == -102);
    }

    private boolean laTinLon(byte cmd) {
        return cmd == -120 || cmd == -31 || cmd == -41 || cmd == -60 || cmd == -92;
    }

    private byte docKhoa(byte b) {
        byte b2 = this.curR;
        this.curR = (byte)(b2 + 1);
        byte ketQua = (byte)(this.khoa[b2 & 0xFF] & 0xFF ^ b & 0xFF);
        if (this.curR >= this.khoa.length) {
            this.curR = (byte)(this.curR % this.khoa.length);
        }
        return ketQua;
    }

    private byte ghiKhoa(byte b) {
        byte b2 = this.curW;
        this.curW = (byte)(b2 + 1);
        byte ketQua = (byte)(this.khoa[b2 & 0xFF] & 0xFF ^ b & 0xFF);
        if (this.curW >= this.khoa.length) {
            this.curW = (byte)(this.curW % this.khoa.length);
        }
        return ketQua;
    }

    @Override
    public synchronized void close() {
        if (!this.kichHoat) {
            return;
        }
        this.kichHoat = false;
        Count count = new Count();
        count.svReceived_clSended = this.svReceived_clSended;
        count.svSended_clReceived = this.svSended_clReceived;
        synchronized (this.vResendMessage) {
            count.vResendMessage = new ArrayList<VXLTinNhan>(this.vResendMessage);
        }
        if (this.maPhien != null) {
            String closedSessionId = this.maPhien;
            sessions.put(closedSessionId, count);
            if (this.kenh != null && this.kenh.eventLoop() != null) {
                this.kenh.eventLoop().schedule(() -> sessions.remove(closedSessionId, count), SESSION_TTL_MILLIS, TimeUnit.MILLISECONDS);
            }
        }
        try {
            if (this.user != null) {
                this.user.close();
            }
        }
        catch (Exception ex) {
            VXLQuanLyMayChu.log("Lỗi đóng người dùng " + this.moTa() + ": " + ex.getMessage());
        }
        try {
            VXLQuanLyMayChu.disconnect(this);
        }
        catch (Exception ex) {
            VXLQuanLyMayChu.log("Lỗi gỡ phiên " + this.moTa() + ": " + ex.getMessage());
        }
        finally {
            this.donMang();
        }
    }

    private void donMang() {
        this.curR = 0;
        this.curW = 0;
        this.daKetNoi = false;
        this.dangNhap = false;
        if (this.tacVuGiuKetNoi != null) {
            this.tacVuGiuKetNoi.cancel(false);
            this.tacVuGiuKetNoi = null;
        }
        if (this.kenh != null) {
            this.kenh.close();
            this.kenh = null;
        }
    }

    public String mayXa() {
        if (this.kenh == null || this.kenh.remoteAddress() == null) {
            return null;
        }
        String raw = this.kenh.remoteAddress().toString();
        if (raw.startsWith("/")) {
            raw = raw.substring(1);
        }
        int colon = raw.lastIndexOf(':');
        if (colon > 0) {
            return raw.substring(0, colon);
        }
        return raw;
    }

    public String moTa() {
        StringBuilder sb = new StringBuilder();
        sb.append('#').append(this.ma);
        String mayChu = this.mayXa();
        if (mayChu != null && !mayChu.isEmpty()) {
            sb.append(" @").append(mayChu);
        }
        if (this.user != null) {
            sb.append(" tk=").append(this.user.toString());
            if (this.user.nguoiChoi != null && this.user.nguoiChoi.ten != null) {
                sb.append(" nv=").append(this.user.nguoiChoi.ten);
            }
        }
        return sb.toString();
    }

    public String toString() {
        if (this.user != null) {
            return this.user.toString();
        }
        return "Client " + this.ma;
    }

    public void guiKhoa() throws IOException {
        this.maPhien = "vxl_" + UUID.randomUUID();
        VXLTinNhan ms = new VXLTinNhan(-27);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(this.khoa.length);
        ds.writeByte(this.khoa[0]);
        for (int i = 1; i < this.khoa.length; ++i) {
            ds.writeByte(this.khoa[i] ^ this.khoa[i - 1]);
        }
        ds.writeUTF(this.maPhien);
        ds.flush();
        this.dayTin(ms);
        this.daKetNoi = true;
        this.timeConnected = System.currentTimeMillis();
        this.batGiuKetNoi();
    }

    private void batGiuKetNoi() {
        if (this.tacVuGiuKetNoi != null) {
            this.tacVuGiuKetNoi.cancel(false);
            this.tacVuGiuKetNoi = null;
        }
        if (this.kenh != null && this.kenh.eventLoop() != null) {
            this.tacVuGiuKetNoi = this.kenh.eventLoop().scheduleAtFixedRate(() -> {
                if (this.dangKetNoi()) {
                    this.guiTin(new VXLTinNhan(-102));
                }
            }, 2L, 2L, TimeUnit.SECONDS);
        }
    }

    public void taiDuLieuXong() throws IOException {
        if (this.user.nguoiChoi != null) {
            this.guiThongTin();
        } else {
            this.user.dichVu.taoNhanVat();
        }
    }

    public void guiThongTin() throws IOException {
        VXLNguoiChoi.players_id.put(this.user.nguoiChoi.ma, this.user.nguoiChoi);
        this.guiVaChamBanDo();
        this.user.dichVu.guiDoTrenNguoi();
        this.user.dichVu.guiTuiDo();
        this.user.dichVu.guiRuongDo();
        this.user.dichVu.guiBalo();
        this.user.dichVu.guiThongTin();
        VXLBanDoRPG.vao(this.user.nguoiChoi);
        this.user.dichVu.capNhatKDVaKDA();
        this.user.dichVu.capNhatAvenger();
        this.user.dichVu.capNhatSucManh();
    }

    public void dangKy(VXLTinNhan ms) throws IOException {
        String tenDangNhap = ms.docUTF(32, "tên đăng nhập");
        String matKhau = ms.docUTF(72, "mật khẩu");
        String tenDangNhapAo = ms.docUTF(32, "tên đăng nhập ảo");
        String loi = VXLNguoiDung.dangKy(tenDangNhap, matKhau, tenDangNhapAo);
        VXLTinNhan phanHoi = new VXLTinNhan(-71);
        DataOutputStream ds = phanHoi.boGhi();
        ds.writeBoolean(loi == null);
        if (loi != null) {
            ds.writeUTF(loi);
        }
        ds.flush();
        this.guiTin(phanHoi);
    }

    public void guiLaiTinTu(int chiSo) {
        List<VXLTinNhan> tinCanGuiLai;
        synchronized (this.vResendMessage) {
            int chiSoAnToan = Math.max(0, Math.min(chiSo, this.vResendMessage.size()));
            tinCanGuiLai = new ArrayList<VXLTinNhan>(this.vResendMessage.subList(chiSoAnToan, this.vResendMessage.size()));
            this.vResendMessage.clear();
        }
        try {
            for (VXLTinNhan tin : tinCanGuiLai) {
                this.dayTin(tin);
            }
            this.guiMaPhien(2);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void guiMaPhien(int loai) {
        if (this.maPhien == null) {
            this.heThongXong = true;
            this.daKetNoi = true;
            return;
        }
        try {
            VXLTinNhan tin = new VXLTinNhan(-127);
            tin.boGhi().write(loai);
            if (loai == 0) {
                tin.boGhi().writeUTF(this.maPhien);
                tin.boGhi().writeInt(this.svReceived_clSended);
                tin.boGhi().writeInt(this.svSended_clReceived);
            }
            this.dayTin(tin);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void dangNhap(VXLTinNhan ms) throws IOException {
        byte loai;
        String phienBan;
        String matKhau;
        if (this.dangNhap) {
            return;
        }
        String tenDangNhap = ms.docUTF(32, "tên đăng nhập");
        if (tenDangNhap.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập rỗng.");
        }
        matKhau = ms.docUTF(72, "mật khẩu");
        phienBan = ms.docUTF(32, "phien ban");
        loai = ms.boDoc().readByte();
        VXLNguoiDung us = VXLNguoiDung.dangNhap(this, tenDangNhap, matKhau, phienBan, loai);
        if (us != null) {
            this.user = us;
            this.dangNhap = true;
            this.user.taiDuLieuNguoiChoi();
            this.user.dichVu.guiPhienBan();
        }
    }

    public void dangNhap2(VXLTinNhan ms) throws IOException {
        String tenDangNhap = ms.docUTF(32, "tên đăng nhập");
        VXLNguoiDung.dangNhap2(this, tenDangNhap);
    }

    public boolean daDangNhap() {
        return this.dangNhap && this.user != null;
    }

    public void dangXuat() {
        this.guiTin(new VXLTinNhan(2));
        this.dongTin();
    }

    public synchronized void dongTin() {
        if (!this.kichHoat) {
            return;
        }
        if (this.boXuLyTin != null) {
            this.boXuLyTin.khiMatKetNoi();
        }
        this.close();
    }

    private static class Count {
        protected int svReceived_clSended;
        protected int svSended_clReceived;
        private final long taoLuc = System.currentTimeMillis();
        private List<VXLTinNhan> vResendMessage = new ArrayList<VXLTinNhan>();

        private boolean hetHan() {
            return System.currentTimeMillis() - this.taoLuc > SESSION_TTL_MILLIS;
        }
    }
}
