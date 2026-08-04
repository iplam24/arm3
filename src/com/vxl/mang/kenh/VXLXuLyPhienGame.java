package com.vxl.mang.kenh;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.VXLTinNhan;
import com.vxl.mang.VXLPhien;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class VXLXuLyPhienGame
extends SimpleChannelInboundHandler<VXLTinNhan> {
    private final VXLPhien phien;

    public VXLXuLyPhienGame(VXLPhien phien) {
        this.phien = phien;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        VXLQuanLyMayChu.onClientConnected(this.phien);
    }

    protected void channelRead0(ChannelHandlerContext ctx, VXLTinNhan tin) {
        this.phien.khiNhanTin(tin);
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        this.phien.khiKenhNgat();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        VXLQuanLyMayChu.logConnection("Loi kenh " + this.phien.moTa() + ": " + cause.getMessage());
        this.phien.dongTin();
    }
}

