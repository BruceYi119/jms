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

	@NonNull
	private Environment env;
	private Map<ChannelId, SocketModel> models = new HashMap<ChannelId, SocketModel>();

	private void initModel(ChannelHandlerContext ctx) {
		SocketModel model = new SocketModel();

		model.setPacket(ctx.alloc().buffer());
		model.setSb(new StringBuffer());
		model.setPath(Paths.get(env.getProperty("custom.file.upload.path")));

		models.put(ctx.channel().id(), model);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().pipeline().addLast(new ReadTimeoutHandler(30));
		ctx.channel().pipeline().addLast(new WriteTimeoutHandler(30));

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
		SocketModel model = models.get(ctx.channel().id());

		model.getSb().setLength(0);

		// ??????
		model.getSb().append("W 0000000000");

		ByteBuf upBuf = Unpooled.buffer();
		upBuf.writeBytes(model.getSb().toString().getBytes());
		ctx.writeAndFlush(upBuf);

		clearModel(ctx);

		log.error("Exception : ", cause);

		ctx.close();
	}

	private void process(ChannelHandlerContext ctx) {
		SocketModel model = models.get(ctx.channel().id());
		ByteBuf packet = model.getPacket();
		String msgType = "S";

		model.getSb().setLength(0);

		// ?????? 5byte??? ??????
		while (packet.readableBytes() >= 5) {
			if (!model.isRecvHeaderRead()) {
				// ???????????? 1
				model.setRecvType((char) packet.readByte());
				// ???????????? 4
				byte[] recvSizeBytes = new byte[4];

				packet.readBytes(recvSizeBytes, 0, recvSizeBytes.length);

				int recvSize = Integer.parseInt(new String(recvSizeBytes));

				model.setMsgSize(recvSize);
				model.setRecvHeaderRead(true);
			}

			if (packet.readableBytes() < (model.getMsgSize() - 5))
				break;

			if (packet.readableBytes() >= (model.getMsgSize() - 5)) {
				switch (model.getRecvType()) {
				// ?????? ??????
				case 'I':
					// ????????? 20
					byte[] fileNm = new byte[20];
					packet.readBytes(fileNm, 0, fileNm.length);
					model.setFileNm(new String(fileNm).trim());

					// ???????????? 10
					byte[] fileSize = new byte[10];
					packet.readBytes(fileSize, 0, fileSize.length);
					model.setFileSize(Integer.parseInt(new String(fileSize)));

					// ?????? ?????????
					model.setFileExt(model.getFileNm().substring(model.getFileNm().lastIndexOf(".") + 1));
					model.setFile(new File(model.getPath().toString(), model.getFileNm()));

					// ????????????
					if (model.getFile().exists()) {
						log.info(String.format("ch-%s : ????????????", ctx.channel().id()));
						try {
							model.setFos(new FileOutputStream(model.getFile(), true));
							model.setRecvFileSize((int) model.getFile().length());
						} catch (FileNotFoundException e) {
							log.error("FileNotFoundException : ", e);
						}

						if (model.getRecvFileSize() >= model.getFileSize())
							msgType = "E";

						model.setSendType("I");
						// ????????????
					} else {
						log.info(String.format("ch-%s : ????????????", ctx.channel().id()));
						try {
							model.setFos(new FileOutputStream(model.getFile()));
						} catch (FileNotFoundException e) {
							log.error("FileNotFoundException : ", e);
						}

						model.setSendType("N");
					}

					model.setBos(new BufferedOutputStream(model.getFos()));
					model.setRecvHeaderRead(false);
					log.info(String.format("ch-%s : ??????", ctx.channel().id()));
					break;
				// ????????????
				case 'E':
					try {
						model.getBos().flush();
					} catch (IOException e) {
						log.error("IOException : ", e);
					}

					// ???????????? ??????
					msgType = "E";
					log.info(String.format("ch-%s : ????????????", ctx.channel().id()));
					break;
				// ??????
				default:
					model.getPacket().readerIndex(model.getPacket().readerIndex() + 30);

					if (model.getMsgSize() - 35 > 0) {
						byte[] recvBytes = new byte[model.getMsgSize() - 35];

						packet.readBytes(recvBytes, 0, (model.getMsgSize() - 35));

						try {
							model.getBos().write(recvBytes);
						} catch (IOException e) {
							log.error("IOException : ", e);
						}

						model.setRecvFileSize(model.getRecvFileSize() + (model.getMsgSize() - 35));
					}

					// ???????????? ??????
					model.setRecvHeaderRead(false);
					log.info(String.format("ch-%s : ????????????", ctx.channel().id()));
					break;
				}

				// ???????????? 1 S : ????????????/E : ????????????/W : ??????
				model.getSb().append(msgType);
				// ???????????? 1 N : ??????/I : ????????????
				model.getSb().append(model.getSendType());
				// ?????????????????? 10
				model.getSb().append(Telegram.numPad(model.getRecvFileSize(), 10));

				ByteBuf upBuf = Unpooled.buffer();
				upBuf.writeBytes(model.getSb().toString().getBytes());
				ctx.writeAndFlush(upBuf);

				if (msgType.equals("E"))
					clearModel(ctx);
			}
		}
	}

	private void clearModel(ChannelHandlerContext ctx) {
		if (models.get(ctx.channel().id()) != null) {
			SocketModel model = models.get(ctx.channel().id());

			if (model.getPacket() != null) {
				model.getPacket().readerIndex(model.getPacket().writerIndex());
				while (model.getPacket().refCnt() > 0)
					model.getPacket().release();
			}

			try {
				if (model.getBos() != null)
					model.getBos().close();
				if (model.getFos() != null)
					model.getFos().close();
			} catch (IOException e) {
				log.error("IOException : ", e);
			}

			models.remove(ctx.channel().id());
		}
	}

}