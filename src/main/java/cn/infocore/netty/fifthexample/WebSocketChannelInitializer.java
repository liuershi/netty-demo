package cn.infocore.netty.fifthexample;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/13 15:53
 * @instructions
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        /**
         * HttpServerCodec需要与HttpObjectAggregator结合使用，因为对于POST请求来说，
         * 请求参数是保存在消息体中的，而HttpServerCodec只能获取URI的参数，所以不支持POST
         * 请求，而HttpObjectAggregator可以获取到，所以需要结合使用;
         * ChunkedWriteHandler：见名知意，是以块的方式传输数据；
         * WebSocketServerProtocolHandler：作用是处理http请求并升级为websocket，同时还能
         * 处理心跳，判断连接是否断开。
         */
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new WebSocketServerProtocolHandler("/test"));
        pipeline.addLast(new TextWebSocketFrameHandler());
    }
}
