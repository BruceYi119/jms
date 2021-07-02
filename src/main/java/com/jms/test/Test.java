package com.jms.test;

public class Test {

	public static void main(String[] args) {
		String str = "ABBDBBOTBP";
		byte[] b = new byte[10];

		b = str.getBytes();

		System.out.println(new String(b));
	}

}