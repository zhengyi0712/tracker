package com.bugsfly.common;

public class RegExp {
	public final static String MOBILE = "^1[3458]\\d{9}$";// 手机
	public final static String PASSWORD = "^[\\w!@#\\$%\\^&\\*\\(\\)\\+\\-=`~]{6,16}$";// 密码
	public final static String ID_CARD = "^[1-9]\\d{13,16}[a-zA-Z0-9]{1}$";// 身份证
	public final static String PHONE = "^(\\d{3,4}\\-?)?\\d{7,8}$";// 固话
	public final static String ZH_NAME = "^[\u4e00-\u9fa5]{2,5}$";
	public final static String EN_NAME = "^[a-zA-Z]{2,20}$";
}
