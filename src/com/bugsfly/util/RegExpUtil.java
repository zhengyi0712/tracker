package com.bugsfly.util;

import java.util.regex.Pattern;

/**
 * 参数验证
 * 
 */
public class RegExpUtil {
	/**
	 * 验证整数
	 * 
	 * @param integer
	 * @return
	 */
	public static boolean checkInteger(String integer) {
		if (integer == null) {
			return false;
		}
		String regex = "^-?\\d+$";
		return Pattern.matches(regex, integer);

	}

	/**
	 * 验证正整数，不包含0
	 * 
	 * @param integer
	 * @return
	 */
	public static boolean checkPositiveInteger(String integer) {
		if (integer == null) {
			return false;
		}
		String regex = "^[1-9]\\d*$";
		return Pattern.matches(regex, integer);
	}

	/**
	 * 验证数字，包含小数
	 * 
	 * @param decimal
	 * @return
	 */
	public static boolean checkDecimal(String decimal) {
		if (decimal == null) {
			return false;
		}
		String regex = "^-?\\d+(\\.\\d+)?$";
		return Pattern.matches(regex, decimal);

	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean checkMobile(String mobile) {
		if (mobile == null) {
			return false;
		}
		String regex = "^1[3458]\\d{9}$";
		return Pattern.matches(regex, mobile);

	}

	/**
	 * 验证邮编
	 * 
	 * @param postcode
	 * @return
	 */
	public static boolean checkPostcode(String postcode) {
		if (postcode == null) {
			return false;
		}
		String regex = "^\\d{6}$";
		return Pattern.matches(regex, postcode);

	}

	/**
	 * 验证邮箱
	 * 
	 * @param mail
	 * @return
	 */
	public static boolean checkMail(String mail) {
		if (mail == null) {
			return false;
		}
		String regex = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
		return Pattern.matches(regex, mail);
	}

	/**
	 * 验证身份证
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean checkIdCard(String idCard) {
		if (idCard == null) {
			return false;
		}
		String regex = "^[1-9]\\d{13,16}[a-zA-Z0-9]{1}$";
		return Pattern.matches(regex, idCard);
	}

	/**
	 * 验证固话
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkPhone(String phone) {
		if (phone == null) {
			return false;
		}
		String regex = "^(\\d{3,4}\\-?)?\\d{7,8}$";
		return Pattern.matches(regex, phone);
	}

	/**
	 * 验证密码，合法的符号加字母和数字6-16位
	 * 
	 * @param pwd
	 * @return
	 */
	public static boolean checkPassword(String pwd) {
		if (pwd == null) {
			return false;
		}
		String regex = "^[\\w!@#\\$%\\^&\\*\\(\\)\\+\\-=`~]{6,16}$";
		return Pattern.matches(regex, pwd);
	}

	/**
	 * 验证用户名，用户由字母数字下划线汉字组成，5-25个字符， 一个汉字算两个，不支持生僻字,不能以下划线或者数字开头
	 * 
	 * @param username
	 * @return
	 */
	public static boolean checkUsername(String username) {
		if (username == null) {
			return false;
		}
		String regex = "^[a-zA-Z\\u4e00-\\u9fa5][\\u4e00-\\u9fa5\\w]{2,24}$";
		String str = username.replaceAll("[\\u4e00-\\u9fa5]", "XX");
		return Pattern.matches(regex, username) && str.length() >= 5
				&& str.length() <= 25;

	}
}
