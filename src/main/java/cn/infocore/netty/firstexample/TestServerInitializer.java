package cn.infocore.netty.firstexample;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/10 14:08
 * @instructions 初始化通道
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 根据传进来的SocketChannel获取到对应的channel管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 在管道中加入需要的handler处理器
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("testHttpServerHandler", new TestHttpServerHandler());
    }
}
