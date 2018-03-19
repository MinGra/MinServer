package com.http.utils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
	private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	private CommonUtil() {
		throw new AssertionError("不允许实例化工具类CommonUtil");
	}
	
	public static byte[] mergeByteArray(byte[] a, byte[] b) {
		byte[] c = new byte[a.length+b.length];
		System.arraycopy(a,0,c,0,a.length);
		System.arraycopy(b,0,c,a.length,b.length);
		return c;
	}
//	public static String byteBuffer2String(ByteBuffer buffer) {
//		// buffer修改limit和position
//		buffer.flip();
//		Charset charset = Charset.forName("UTF-8");
//		CharsetDecoder decoder = charset.newDecoder();
//		CharBuffer charBuffer = null;
//		try {
//			charBuffer = decoder.decode(buffer);
//			return charBuffer.toString();
//		} catch (CharacterCodingException e) {
//			logger.error("ByteBuffer转String时发生CharacterCodingException");
//			return null;
//		}
//		
//	}
}
