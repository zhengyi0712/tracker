package com.bugsfly.project;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class ProjectValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateString("project.name", true, 2, 30, "msg", "名称必须在2-30字符之间");
		validateString("project.intro", false, 0, 200, "msg", "简介不能超过200字");

	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson();

	}

}
