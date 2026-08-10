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

    public synchronized void batDau(String mayChu, int cong) throws InterruptedException {
        if (this.kenhMayChu != null || this.nhomChu != null || this.nhomTho != null) {
            throw new IllegalStateException("Netty server is already started.");
        }
        this.nhomChu = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        this.nhomTho = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        try {
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
        catch (InterruptedException | RuntimeException ex) {
            if (this.kenhMayChu != null) {
                this.kenhMayChu.close();
                this.kenhMayChu = null;
            }
            if (this.nhomChu != null) {
                this.nhomChu.shutdownGracefully();
                this.nhomChu = null;
            }
            if (this.nhomTho != null) {
                this.nhomTho.shutdownGracefully();
                this.nhomTho = null;
            }
            throw ex;
        }
    }

    public synchronized void dung() throws InterruptedException {
        Channel mayChuHienTai = this.kenhMayChu;
        EventLoopGroup nhomChuHienTai = this.nhomChu;
        EventLoopGroup nhomThoHienTai = this.nhomTho;
        this.kenhMayChu = null;
        this.nhomChu = null;
        this.nhomTho = null;
        if (mayChuHienTai != null) {
            mayChuHienTai.close().sync();
        }
        if (nhomChuHienTai != null) {
            nhomChuHienTai.shutdownGracefully().sync();
        }
        if (nhomThoHienTai != null) {
            nhomThoHienTai.shutdownGracefully().sync();
        }
    }

    public void choDong() throws InterruptedException {
        Channel kenh = this.kenhMayChu;
        if (kenh != null) {
            kenh.closeFuture().sync();
        }
    }
}

