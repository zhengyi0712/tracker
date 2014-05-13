package com.bugsfly.login;

import javax.servlet.http.HttpServletRequest;

import com.bugsfly.common.Webkeys;
import com.bugsfly.user.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;

public class LoginInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController();
		HttpServletRequest request = ai.getController().getRequest();
		/**
		 * 对于session里没有用户对象的进行拦截
		 */
		if (controller.getSessionAttr(Webkeys.SESSION_USER) != null) {
			ai.invoke();
			return;
		}
		// 尝试从cookie里找userid
		String userId = controller.getCookie(Webkeys.COOKIE_USER_ID);
		User user = null;
		if (StringKit.notBlank(userId)) {
			user = User.dao.findById(userId);
		}
		if (user != null) {
			controller.setSessionAttr(Webkeys.SESSION_USER, user);
			ai.invoke();
			return;
		}
		/**
		 * 如果是post请求或者异步请求保存来源页面地址
		 */
		if (request.getHeader("x-requested-with") != null) {
			controller.setSessionAttr(Webkeys.SESSION_REFERER,
					request.getHeader("Referer"));
			ai.getController().getResponse().setHeader("login", "unLogin");
			ai.getController().getResponse().setStatus(401);
			return;
		} else if ("POST".equals(request.getMethod())) {
			controller.setSessionAttr(Webkeys.SESSION_REFERER,
					request.getHeader("Referer"));
		} else {// 非异步的get请求保存请求地址
			String queryString = StringKit.isBlank(request.getQueryString()) ? ""
					: "?" + request.getQueryString();
			controller.setSessionAttr(Webkeys.SESSION_REFERER,
					request.getRequestURL() + queryString);
		}
		ai.getController().redirect("/login");
		return;

	}

}
