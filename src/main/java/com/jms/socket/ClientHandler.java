package com.jms.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	public static final Logger log = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().pipeline().addLast(new ReadTimeoutHandler(30));
		ctx.channel().pipeline().addLast(new WriteTimeoutHandler(30));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf b = (ByteBuf) msg;

		System.out.println(b.readableBytes());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("exceptionCaught : ", cause);
		ctx.close();
	}

}