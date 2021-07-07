package com.jms.socket;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Component
public class SocketServer implements ApplicationListener<ApplicationStartedEvent> {

	public static final Logger log = LoggerFactory.getLogger(SocketServer.class);

	private Environment env;

	private int port;
	private ServerBootstrap sb = null;
	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	private InitHandler handlers = null;

	public SocketServer(Environment env) {
		ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		handlers.add(new ServerHandler());

		this.env = env;
		this.handlers = new InitHandler(handlers);
	}

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		port = Integer.parseInt(env.getProperty("custom.socket.server.port"));

		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();

		sb = new ServerBootstrap();
		sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO)).childHandler(this.handlers);

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