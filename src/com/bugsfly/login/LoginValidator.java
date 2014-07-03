package com.bugsfly.login;

import com.bugsfly.common.RegExp;
import com.bugsfly.util.RegExpUtil;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class LoginValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		// 验证密码
		validateRegex("pwd", RegExp.PASSWORD, "msg", "密码不规范");
		// 验证帐号
		String account = c.getPara("account");
		if (!RegExpUtil.checkMobile(account) && !RegExpUtil.checkMail(account)) {
			addError("msg", "请输入格式正确的手机号或者邮箱");
		}

	}

	@Override
	protected void handleError(Controller c) {
		c.render("index.ftl");
	}

}
