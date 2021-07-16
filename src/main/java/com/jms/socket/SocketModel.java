package com.jms.socket;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class SocketModel {

	// 파일
	private File file;
	// 파일명
	private String fileNm;
	// 전문 사이즈
	private int teleSize;
	// 파일 확장자
	private String fileExt;
	// 파일 사이즈
	private int fileSize;
	// 받은 파일 파일 데이터
	private byte[] data;
	// 파일 dir
	@NonNull
	private Path path;
	// ctx.alloc().buffer()
	private ByteBuf packet;
	private StringBuffer sb;
	private FileOutputStream fos;
	private BufferedOutputStream bos;

}