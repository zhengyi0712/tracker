package com.bugsfly.user;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class UserValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateRequired("cnName", "error", "中文名必填");
		validateRequired("email", "error", "邮箱必填");
		validateRequired("mobile", "error", "手机号必填");
		
		validateRegex("cnName", "^[\\u4e00-\\u9fa5]{2,5}$", "error", "中文名不符合要求");
		validateRegex("enName", "^[]{2,20}$", "error","英文名不符合要求");
		validateEmail("email", "error", "邮箱格式不正确");
		validateRegex("mobile", "", "error", "error");
		
	}

	@Override
	protected void handleError(Controller c) {
		
		throw new IllegalArgumentException("部分参数不正确");
	}

}
