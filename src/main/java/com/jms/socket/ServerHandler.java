package com.jms.socket;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jms.service.SocketService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	public static final Logger log = LoggerFactory.getLogger(SocketService.class);

	private Map<ChannelId, ByteBuf> packetMap = new HashMap();

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ChannelId id = ctx.channel().id();

		if (packetMap.get(id) == null)
			packetMap.put(id, ctx.alloc().buffer());

		ByteBuf now = packetMap.get(id);
		ByteBuf b = (ByteBuf) msg;

		packetMap.get(id).writeBytes(b);
		b.release();

		if (now.readableBytes() >= 50) {
			ByteBuf readbuf = now.readBytes(50);
			byte[] bb = new byte[50];
			int i = 0;
			while (readbuf.isReadable()) {
				bb[i] = readbuf.readByte();
				i++;
			}

			System.out.print(new String(bb));
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Exception : ", cause);
		ctx.close();
	}

}