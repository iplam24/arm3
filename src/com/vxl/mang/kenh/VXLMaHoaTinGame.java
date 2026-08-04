package com.vxl.mang.kenh;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.mang.VXLTinNhan;
import com.vxl.mang.VXLPhien;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class VXLMaHoaTinGame
extends MessageToByteEncoder<VXLTinNhan> {
    private final VXLPhien phien;

    public VXLMaHoaTinGame(VXLPhien phien) {
        this.phien = phien;
    }

    protected void encode(ChannelHandlerContext ctx, VXLTinNhan msg, ByteBuf out) {
        this.phien.maHoaTin(msg, out);
    }
}

