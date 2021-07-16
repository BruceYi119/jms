package com.jms.socket;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.jms.component.Telegram;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor
public class ServerHandler extends ChannelInboundHandlerAdapter {

	public static final Logger log = LoggerFactory.getLogger(SocketServer.class);

	private boolean isDir = false;
	@NonNull
	private Environment env;
	private Map<ChannelId, SocketModel> models = new HashMap<ChannelId, SocketModel>();

	private void initModel(ChannelHandlerContext ctx) {
		SocketModel model = new SocketModel();

		model.setPacket(ctx.alloc().buffer());
		model.setSb(new StringBuffer());
		model.setPath(Paths
				.get(System.getProperty("user.dir") + File.separator + env.getProperty("custom.file.upload.path")));

		if (!isDir) {
			if (model.getPath().toFile().exists())
				isDir = true;
			else
				model.getPath().toFile().mkdirs();
		}

		models.put(ctx.channel().id(), model);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().pipeline().addLast(new ReadTimeoutHandler(300));
		ctx.channel().pipeline().addLast(new WriteTimeoutHandler(300));

		initModel(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		SocketModel model = models.get(ctx.channel().id());

		if (model.getPacket() == null)
			model.setPacket(ctx.alloc().buffer());

		ByteBuf b = (ByteBuf) msg;

		model.getPacket().writeBytes(b);
		b.release();

		process(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clearModel(ctx);

		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		clearModel(ctx);

		log.error("Exception : ", cause);

		ctx.close();
	}

	private void process(ChannelHandlerContext ctx) {
		SocketModel model = models.get(ctx.channel().id());
		ByteBuf packet = model.getPacket();

		model.getSb().setLength(0);

		if (packet.readableBytes() >= 35) {
			switch ((char) packet.readByte()) {
			// 개시 전문
			case 'I':
				// 전문길이 4
				byte[] teleSize = new byte[4];
				packet.readBytes(teleSize, 0, teleSize.length);
				model.setTeleSize(Integer.parseInt(new String(teleSize)));

				// 파일명 20
				byte[] fileNm = new byte[20];
				packet.readBytes(fileNm, 0, fileNm.length);
				model.setFileNm(new String(fileNm).trim());

				// 파일크기 10
				byte[] fileSize = new byte[10];
				packet.readBytes(fileSize, 0, fileSize.length);
				model.setFileSize(Integer.parseInt(new String(fileSize)));
				model.setData(new byte[model.getFileSize()]);

				// 파일 확장자
				model.setFileExt(model.getFileNm().substring(model.getFileNm().lastIndexOf(".") + 1));

				model.setFile(new File(model.getPath().toString(), model.getFileNm()));

				// 전문타입 1 R : 송신요청/S : 송신완료
				model.getSb().append("S");

				// 송신타입 1 N : 신규/I : 이어받기
				if (model.getFile().exists()) {
					try {
						model.setFos(new FileOutputStream(model.getFile(), true));
					} catch (FileNotFoundException e) {
						log.error("FileNotFoundException : ", e);
					}
					model.getSb().append("I");
				} else {
					try {
						model.setFos(new FileOutputStream(model.getFile()));
					} catch (FileNotFoundException e) {
						log.error("FileNotFoundException : ", e);
					}
					model.getSb().append("N");
				}

				model.setBos(new BufferedOutputStream(model.getFos()));

				// 받은파일크기 10
				model.getSb().append(Telegram.numPad(0, 10));

				break;
			// 마지막 전송
			case 'L':
				break;
			// 전송
			default:

				break;
			}

			ByteBuf upBuf = Unpooled.buffer();
			upBuf.writeBytes(model.getSb().toString().getBytes());
			ctx.writeAndFlush(upBuf);

			model.getSb().setLength(0);

			packet.release();
		}
	}

	private void clearModel(ChannelHandlerContext ctx) {
		if (models.get(ctx.channel().id()) != null) {
			SocketModel model = models.get(ctx.channel().id());

			if (model.getPacket() != null) {
				while (model.getPacket().refCnt() > 0)
					model.getPacket().release();
			}

			try {
				if (model.getFos() != null)
					model.getFos().close();
				if (model.getBos() != null)
					model.getBos().close();
			} catch (IOException e) {
				log.error("IOException : ", e);
			}

			models.remove(ctx.channel().id());
		}
	}

}