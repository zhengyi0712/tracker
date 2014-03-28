package com.bugsfly.user;

import com.jfinal.core.Controller;

public class UserController extends Controller {

	public void index() {
		render("index.ftl");
	}

	/**
	 * 用户信息
	 */
	public void userinfo() {
		render("userinfo.ftl");
	}
}
