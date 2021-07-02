package com.jms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.jms.socket.ServerHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Service
public class SocketService implements ApplicationListener<ApplicationStartedEvent> {

	private Environment env;
	public static final Logger log = LoggerFactory.getLogger(SocketService.class);

	private int port;
	private Bootstrap cb = null;
	private ServerBootstrap sb = null;
	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;

	public SocketService(Environment env) {
		this.env = env;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onApplicationEvent(ApplicationStartedEvent event) {
		port = Integer.parseInt(env.getProperty("custom.socket.server.port"));

		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();

		sb = new ServerBootstrap();
		sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
				.childOption(ChannelOption.TCP_NODELAY, true).handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ServerHandler());
					}
				});

		try {
			ChannelFuture cf = sb.bind(port).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("InterruptedException : ", e);
		} finally {
			sb.config().group().shutdownGracefully();
			sb.config().childGroup().shutdownGracefully();
		}
	}

}