package com.bugsfly.login;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;

import com.bugsfly.common.Webkeys;
import com.bugsfly.user.User;
import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class LoginController extends Controller {
	@ClearInterceptor(ClearLayer.ALL)
	@ActionKey("/")
	public void index() {
		render("index.ftl");
	}

	// 用户登录
	@ClearInterceptor(ClearLayer.ALL)
	@Before(LoginValidator.class)
	public void login() {
		String account = getPara("account");
		String pwd = getPara("pwd");

		 User user = User.dao.getByAccount(account);

		if (user == null) {
			setAttr("msg", "帐号或者密码错误");
			index();
			return;
		}

		String salt = user.getStr("salt");
		if (!user.getStr("md5").equals(DigestUtils.md5Hex(pwd + salt))) {
			setAttr("msg", "帐号或者密码错误");
			index();
			return;
		}

		if (user.getBoolean("disabled")) {
			setAttr(Webkeys.REQUEST_MESSAGE, "您的帐号已经被管理员禁用了，无法登录系统！");
			renderError(403);
			return;
		}

		// 更新登录时间
		user.updateLoginTime();

		// 如果有引用链接，回到登录前的页面，没有就去首页
		HttpSession session = getSession();
		session.setAttribute(Webkeys.SESSION_USER, user);
		Object referer = session.getAttribute(Webkeys.SESSION_REFERER);
		if (referer != null) {
			session.removeAttribute(Webkeys.SESSION_REFERER);
			redirect(String.valueOf(referer));
			return;
		}
		redirect("/user");
	}

	@ClearInterceptor(ClearLayer.ALL)
	public void logout() {
		getSession().invalidate();
		index();
	}
}
