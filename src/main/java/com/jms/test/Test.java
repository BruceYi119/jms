package com.jms.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Test {

	private static String str;
	private static int num;

	public static void main(String[] args) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar c1 = Calendar.getInstance();
		String nowDate = sdf.format(c1.getTime());
		
		System.out.println(nowDate);
		
//		boolean flag = true;
//
//		System.out.println(Test.str);
//		System.out.println(Test.num);
//
//		String txt = "한그입니다아아아!";
//
//		byte[] bt = txt.getBytes();
//
//		File file = new File("D:/download/test.txt");
//		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//
//		bos.write(bt, 0, 25);
//		bos.close();
//		try {
//			int readPos = 0;
//			int readSize = 5;
//			RandomAccessFile raf = new RandomAccessFile("D:/download/test.txt", "r");
//			int fileSize = (int) raf.length();
//
//			while (flag) {
//				if (fileSize > readPos) {
//					int remainingSize = fileSize - readPos;
//
//					if (remainingSize < readSize)
//						readSize = remainingSize;
//
//					byte[] b = new byte[readSize];
//
//					raf.seek(readPos);
//					raf.read(b);
//
//					System.out.println(new String(b));
//					readPos += readSize;
//				} else {
//					flag = false;
//				}
//			}
//
//			System.out.println("END!#@");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}

}