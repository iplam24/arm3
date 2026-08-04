package com.vxl.mang.kenh;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.mang.VXLTinNhan;
import com.vxl.mang.VXLPhien;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class VXLGiaiMaTinGame
extends ByteToMessageDecoder {
    private final VXLPhien phien;

    public VXLGiaiMaTinGame(VXLPhien phien) {
        this.phien = phien;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (true) {
            int readerIndex = in.readerIndex();
            VXLTinNhan tin = this.phien.thuGiaiMaTin(in);
            if (tin == null) {
                in.readerIndex(readerIndex);
                return;
            }
            out.add(tin);
        }
    }
}

