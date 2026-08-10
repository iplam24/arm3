package com.vxl.mang;

// V? Xu?n L?m ??p trai VCL
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VXLTinNhan {
    private byte lenh;
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    public DataInputStream dis;
    private byte[] taiDuLieu;

    public VXLTinNhan(int lenh) {
        this((byte)lenh);
    }

    public VXLTinNhan(byte lenh) {
        this.lenh = lenh;
        this.os = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(this.os);
    }

    public VXLTinNhan(byte lenh, byte[] duLieu) {
        this.lenh = lenh;
        this.taiDuLieu = duLieu == null ? new byte[0] : duLieu;
        this.is = new ByteArrayInputStream(this.taiDuLieu);
        this.dis = new DataInputStream(this.is);
    }

    public byte layLenh() {
        return this.lenh;
    }

    public void datLenh(int cmd) {
        this.datLenh((byte)cmd);
    }

    public void datLenh(byte cmd) {
        this.lenh = cmd;
    }

    public byte[] layDuLieu() {
        if (this.taiDuLieu != null) {
            return this.taiDuLieu;
        }
        if (this.os != null) {
            return this.os.toByteArray();
        }
        return new byte[0];
    }

    public DataInputStream boDoc() {
        if (this.dis == null) {
            this.taiDuLieu = this.layDuLieu();
            this.is = new ByteArrayInputStream(this.taiDuLieu);
            this.dis = new DataInputStream(this.is);
        }
        return this.dis;
    }

    public String docUTF(int doDaiToiDa, String tenTruong) throws IOException {
        if (doDaiToiDa < 0) {
            throw new IllegalArgumentException("Maximum length must not be negative.");
        }
        String giaTri = this.boDoc().readUTF();
        if (giaTri.length() > doDaiToiDa) {
            throw new IllegalArgumentException(tenTruong + " exceeds " + doDaiToiDa + " characters.");
        }
        return giaTri;
    }

    public DataOutputStream boGhi() {
        if (this.dos == null) {
            throw new IllegalStateException("This message is read-only.");
        }
        return this.dos;
    }

    public void donDep() {
        try {
            if (this.dis != null) {
                this.dis.close();
            }
            if (this.dos != null) {
                this.dos.close();
            }
        }
        catch (IOException ignored) {
        }
    }
}
