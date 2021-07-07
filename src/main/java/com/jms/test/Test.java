package com.jms.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Test {

	public static void main(String[] args) throws IOException {
		boolean flag = true;

		try {
			int readPos = 0;
			int readSize = 5;
			RandomAccessFile raf = new RandomAccessFile("D:/download/test.txt", "r");
			int fileSize = (int) raf.length();

			while (flag) {
				if (fileSize > readPos) {
					int remainingSize = fileSize - readPos;

					if (remainingSize < readSize)
						readSize = remainingSize;

					byte[] b = new byte[readSize];

					raf.seek(readPos);
					raf.read(b);

					System.out.println(new String(b));
					readPos += readSize;
				} else {
					flag = false;
				}
			}

			System.out.println("END!#@");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}