package com.bugsfly.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Md5加密工具类
 * 
 */
public class MD5Util {
	private static MessageDigest md = null;
	private static char[] hexChars = new char[] { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("系统不支持MD5加密", e);
		}
	}

	/**
	 * 加密方法
	 * 
	 * @param content 要加密的内容
	 * @return 返回加密后的结果
	 */
	public static String encrypt(String content) {
		// 将要加密的字符串内容转变为数组，然后计算得出十六进制字符串
		byte[] byteArr = null;
		try {
			byteArr = content.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("系统不支持utf-8编码格式", e);
		}
		md.update(byteArr);
		byteArr = md.digest();
		String hexStr = "";
		// 对数组循环拼接字符串
		// 把byte数组里的每一个元素都转换成十六进制形式字符串，拼接起来
		for (int i = 0; i < byteArr.length; i++) {
			int a = (byteArr[i] & 0xff) / 16;
			int b = (byteArr[i] & 0xff) % 16;
			hexStr = hexStr + hexChars[a] + hexChars[b];
		}
		return hexStr;
	}

}