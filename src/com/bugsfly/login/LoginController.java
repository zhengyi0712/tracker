package com.bugsfly.login;

import java.util.Date;

import javax.servlet.http.HttpSession;

import com.bugsfly.Webkeys;
import com.bugsfly.util.MD5Util;
import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class LoginController extends Controller {
	@ClearInterceptor(ClearLayer.ALL)
	public void index() {
		render("index.ftl");
	}

	// 用户登录
	@ClearInterceptor(ClearLayer.ALL)
	@Before(LoginValidator.class)
	public void login() {
		String account = getPara("account");
		String pwd = getPara("pwd");

		Record user = Db.findFirst(
				"select * from user where mobile=? or email=?", account,
				account);

		if (user == null) {
			setAttr("msg", "帐号或者密码错误");
			index();
			return;
		}

		String salt = user.getStr("salt");
		if (!user.getStr("md5").equals(MD5Util.encrypt(pwd + salt))) {
			setAttr("msg", "帐号或者密码错误");
			index();
			return;
		}

		if (user.getBoolean("disabled")) {
			setAttr(Webkeys.REQUEST_MESSAGE, "您的帐号已经被管理员禁用了，无法登录系统！");
			renderError(403);
			return;
		}

		// 判断用户是否超级管理员
		boolean isAdmin = Db.findFirst(
				"select * from sys_admin where admin_id=?", user.getStr("id")) != null;
		user.set("isAdmin", isAdmin);

		// 更新登录时间
		Db.update("update user set login_time=? where id=?", new Date(),
				user.getStr("id"));

		// 如果有引用链接，回到登录前的页面，没有就去首页
		HttpSession session = getSession();
		session.setAttribute(Webkeys.SESSION_USER, user);
		Object referer = session.getAttribute(Webkeys.SESSION_REFERER);
		if (referer != null) {
			session.removeAttribute(Webkeys.SESSION_REFERER);
			redirect(String.valueOf(referer));
			return;
		}
		redirect("/");
	}

	@ClearInterceptor(ClearLayer.ALL)
	public void logout() {
		getSession().invalidate();
		index();
	}
}
