package com.bugsfly.login;

import com.bugsfly.util.RegExpUtil;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class LoginValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		//验证密码
		if(!RegExpUtil.checkPassword(c.getPara("pwd"))){
			addError("msg", "请输入符合规范的密码");
		}
		validateRequired("pwd", "msg", "请输入密码");
		// 验证帐号
		String account = c.getPara("account");
		if (!RegExpUtil.checkMobile(account) && !RegExpUtil.checkMail(account)) {
			addError("msg", "请输入格式正确的手机号或者邮箱");
		}
		validateRequired("account", "msg", "请输入手机号或者邮箱");

	}

	@Override
	protected void handleError(Controller c) {
		c.render("index.ftl");

	}

}
