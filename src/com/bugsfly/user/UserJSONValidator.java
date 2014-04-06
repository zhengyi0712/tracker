package com.bugsfly.user;

import com.bugsfly.common.RegExp;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.validate.Validator;

public class UserJSONValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateEmail("email", "msg", "请填写格式正确的邮箱");
		validateRegex("mobile", RegExp.MOBILE, "msg", "手机号码格式不正确");
		validateRegex("zhName", RegExp.ZH_NAME, "msg", "中文名只接收2-5个汉字，不支持生僻字");
		// 英文名非必填
		String enName = c.getPara("enName");
		if (StringKit.notBlank(enName) && !enName.matches(RegExp.EN_NAME)) {
			addError("msg", "英文名只接收2-20个英文字母");
		}

		if (Db.findFirst("select 1 from user where mobile=?",
				c.getPara("mobile")) != null) {
			addError("msg", "手机号已经存在");
		}

		if (Db.findFirst("select 1 from user where email=?", c.getPara("email")) != null) {
			addError("msg", "邮箱已经存在");
		}

	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson();
	}

}
