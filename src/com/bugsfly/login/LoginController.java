package com.bugsfly.login;

import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
import com.jfinal.core.Controller;

public class LoginController extends Controller {
	@ClearInterceptor(ClearLayer.ALL)
	public void index() {
		render("index.ftl");
	}

	// 用户登录
	public void login() {

	}
}
