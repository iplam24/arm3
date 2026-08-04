package com.vxl.mang;

// Vũ Xuân Lâm đẹp trai VCL
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
        this.taiDuLieu = duLieu;
        this.is = new ByteArrayInputStream(duLieu);
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
        return this.os.toByteArray();
    }

    public DataInputStream boDoc() {
        return this.dis;
    }

    public String docUTF(int doDaiToiDa, String tenTruong) throws IOException {
        String giaTri = this.dis.readUTF();
        if (giaTri.length() > doDaiToiDa) {
            throw new IllegalArgumentException(tenTruong + " vượt quá " + doDaiToiDa + " ký tự.");
        }
        return giaTri;
    }

    public DataOutputStream boGhi() {
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
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

