package com.jms.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {

	private static String str;
	private static int num;

	public static void main(String[] args) throws IOException {
		boolean flag = true;

		System.out.println(Test.str);
		System.out.println(Test.num);

		String txt = "한그입니다아아아!";

		byte[] bt = txt.getBytes();

		File file = new File("D:/download/test.txt");
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

		bos.write(bt, 0, 25);
		bos.close();
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