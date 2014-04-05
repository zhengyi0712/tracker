package com.bugsfly.project;

import com.bugsfly.exception.BusinessException;
import com.bugsfly.user.SysAdminInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

public class ProjectController extends Controller {

	@Before(SysAdminInterceptor.class)
	public void allProjects() {

	}

	public void myProjects() {

	}

	/**
	 * 保存项目
	 */
	public void saveProject() {
		try {
			ProjectManager.saveProject(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}

}
