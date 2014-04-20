package com.bugsfly.task;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class TaskValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateString("task.title", 3, 50, "msg", "标题必须在3-50字符之间");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson();
	}

}
