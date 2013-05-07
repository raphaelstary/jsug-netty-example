package com.raphaelstary;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class WebSocketServer {
    private final int port;
    private final ChannelGroup channels = new DefaultChannelGroup();

    public WebSocketServer(int port) {
        this.port = port;
    }

    public void run() {
        // param(1) just example to show the thread model with 2 connected clients while live coding
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // param(1) just example to show the thread model with 2 connected clients while live coding
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {

            // additional thread pool for blocking handler
            final EventExecutorGroup executorGroup = new DefaultEventExecutorGroup(8);

            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new HttpRequestDecoder(),
                                    new HttpObjectAggregator(65536),
                                    new HttpResponseEncoder(),
                                    new WebSocketServerProtocolHandler("/websocket"));
//                                    new JSUGWebSocketHandler(channels)); // normal example without another thread pool

                            // register blocking or long lasting handler to additional thread pool
                            ch.pipeline().addLast(executorGroup, new JSUGWebSocketHandler(channels));
                        }
                    });

            final Channel channel;
            channel = bootstrap.bind(port).sync().channel();

            System.out.println("server started on port: " + port);
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            bossGroup.shutdown();
            workerGroup.shutdown();
        }
    }
}
