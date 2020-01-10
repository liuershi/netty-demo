package cn.infocore.netty.secondexample;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/10 14:59
 * @instructions
 */
public class MyClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                    .handler(new MyClientInitializer());
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8899));
            future.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
