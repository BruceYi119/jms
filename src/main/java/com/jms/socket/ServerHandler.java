package com.jms.socket;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	public static final Logger log = LoggerFactory.getLogger(SocketServer.class);

	private Map<ChannelId, ByteBuf> packetMap = new HashMap<ChannelId, ByteBuf>();

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().pipeline().addLast(new ReadTimeoutHandler(30));
		ctx.channel().pipeline().addLast(new WriteTimeoutHandler(30));
		packetMap.put(ctx.channel().id(), ctx.alloc().buffer());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf now = packetMap.get(ctx.channel().id());
		ByteBuf b = (ByteBuf) msg;

		now.writeBytes(b);
		b.release();

		if (now.readableBytes() >= 50) {
			ByteBuf readbuf = now.readBytes(50);
			byte[] bb = new byte[50];
			int i = 0;
			while (readbuf.isReadable()) {
				bb[i] = readbuf.readByte();
				i++;
			}

			System.out.println(bb.length);
			System.out.println(new String(bb));
			System.out.flush();

			ByteBuf wb = Unpooled.buffer();
			wb.writeBytes(bb);
			ctx.writeAndFlush(wb);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelReadComplete");
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Exception : ", cause);
		ctx.close();
	}

}