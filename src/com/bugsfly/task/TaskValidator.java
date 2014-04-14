package com.bugsfly.task;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class TaskValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateString("task.title", 3, 50, "msg", "标题必须在3-50字符之间");
		String[] tags = c.getParaValues("tag");
		if (tags != null && tags.length > 0) {
			for (String tag : tags) {
				if (!Task.TAGS.contains(tag)) {
					addError("msg", "请求包含未知的标签");
				}

			}
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson();

	}

}
