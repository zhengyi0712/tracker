package com.bugsfly.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bugsfly.common.Webkeys;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.kit.StringKit;

public class LoginInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		HttpServletRequest request = ai.getController().getRequest();
		HttpSession session = ai.getController().getSession();
		/**
		 * 对于session里没有用户对象的进行拦截
		 */
		if (session.getAttribute(Webkeys.SESSION_USER) != null) {
			ai.invoke();
			return;
		}
		/**
		 * 如果是post请求或者异步请求保存来源页面地址
		 */
		if (request.getHeader("x-requested-with") != null) {
			session.setAttribute(Webkeys.SESSION_REFERER,
					request.getHeader("Referer"));
			ai.getController().getResponse().setHeader("login", "unLogin");
			ai.getController().getResponse().setStatus(401);
			return;
		} else if ("POST".equals(request.getMethod())) {
			session.setAttribute(Webkeys.SESSION_REFERER,
					request.getHeader("Referer"));
		} else {// 非异步的get请求保存请求地址
			String queryString = StringKit.isBlank(request.getQueryString()) ? ""
					: "?" + request.getQueryString();
			session.setAttribute(Webkeys.SESSION_REFERER,
					request.getRequestURL() + queryString);
		}
		ai.getController().redirect("/login");
		return;

	}

}
