package com.raphaelstary;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class JSUGWebSocketHandler extends ChannelInboundMessageHandlerAdapter<TextWebSocketFrame> {
    private final ChannelGroup channels;

    public JSUGWebSocketHandler(ChannelGroup channels) {
        this.channels = channels;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        System.out.println(msg.text());

        // example to show the thread model of netty - simulate blocking handler
        if (msg.text().contains("freeze")) {
            Thread.sleep(5000);
        }

        // sends echo back to the client
        //  ctx.channel().write(new TextWebSocketFrame(msg.text().toUpperCase()));

        // sends msg to all connected clients
        ChannelGroupFuture future = channels.write(new TextWebSocketFrame(msg.text()));

        // WRONG System.out.println("write to channels successful");
        // async RIGHT:
        future.addListener(new GenericFutureListener<Future<Void>>() {
            @Override
            public void operationComplete(Future<Void> future) throws Exception {
                System.out.println("write to channels successful");
            }
        });

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // registers channels in our data structure
        channels.add(ctx.channel());
        super.channelActive(ctx);
    }
}
