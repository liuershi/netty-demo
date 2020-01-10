package cn.infocore.netty.secondexample;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/10 14:54
 * @instructions
 */
public class MyServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext chc, String string) throws Exception {
        System.out.println(chc.channel().remoteAddress() + " : " + string);
        Thread.sleep(1000);
        chc.channel().writeAndFlush("form server : " + UUID.randomUUID());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
