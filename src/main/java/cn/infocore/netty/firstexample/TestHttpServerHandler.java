package cn.infocore.netty.firstexample;

import com.sun.jndi.toolkit.url.Uri;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/10 14:03
 * @instructions
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext chx, HttpObject httpObject) throws Exception {
        System.out.println(httpObject.getClass());
        System.out.println(chx.channel().remoteAddress());

        Thread.sleep(8000);

        if (httpObject instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)httpObject;
            System.out.println("请求方法名：" + request.method().name());

            Uri uri = new Uri(request.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("请求为：/favicon.ico");
                return;
            }

            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);
            FullHttpResponse response =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

            chx.writeAndFlush(response);
            chx.channel().close();
        }
    }
}
