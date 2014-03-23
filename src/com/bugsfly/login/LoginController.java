package com.bugsfly.login;

import javax.servlet.http.HttpSession;

import com.bugsfly.Webkeys;
import com.bugsfly.user.UserManager;
import com.bugsfly.util.MD5Util;
import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
import com.jfinal.core.Controller;
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
		UserManager userManager = new UserManager();
		Record user = userManager.getUserByAccount(account);
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
		
		//更新登录时间
		userManager.updateLoginTime(user.getStr("id"));
		
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
