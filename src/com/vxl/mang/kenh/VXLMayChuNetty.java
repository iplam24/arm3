package com.vxl.mang.kenh;

// Vũ Xuân Lâm đẹp trai VCL
import com.vxl.loi.VXLQuanLyMayChu;
import com.vxl.mang.kenh.VXLKhoiTaoKenhGame;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class VXLMayChuNetty {
    private EventLoopGroup nhomChu;
    private EventLoopGroup nhomTho;
    private Channel kenhMayChu;

    public void batDau(String mayChu, int cong) throws InterruptedException {
        this.nhomChu = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        this.nhomTho = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(nhomChu, nhomTho)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new VXLKhoiTaoKenhGame());
        ChannelFuture bindFuture = bootstrap.bind(mayChu, cong).sync();
        this.kenhMayChu = bindFuture.channel();
        VXLQuanLyMayChu.log("Netty server listening on " + mayChu + ":" + cong);
    }

    public void dung() throws InterruptedException {
        if (this.kenhMayChu != null) {
            this.kenhMayChu.close().sync();
            this.kenhMayChu = null;
        }
        if (this.nhomChu != null) {
            this.nhomChu.shutdownGracefully().sync();
            this.nhomChu = null;
        }
        if (this.nhomTho != null) {
            this.nhomTho.shutdownGracefully().sync();
            this.nhomTho = null;
        }
    }
}

