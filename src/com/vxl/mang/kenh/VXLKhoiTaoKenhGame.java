package com.vxl.mang.kenh;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.VXLPhien;
import com.vxl.mang.kenh.VXLGiaiMaTinGame;
import com.vxl.mang.kenh.VXLMaHoaTinGame;
import com.vxl.mang.kenh.VXLXuLyPhienGame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class VXLKhoiTaoKenhGame
extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel ch) {
        int ma = VXLQuanLyMayChu.nextClientId();
        VXLPhien phien = new VXLPhien(ch, ma);
        ch.pipeline().addLast("decoder", new VXLGiaiMaTinGame(phien)).addLast("encoder", new VXLMaHoaTinGame(phien)).addLast("handler", new VXLXuLyPhienGame(phien));
    }
}

