package com.bugsfly.project;

import com.bugsfly.user.SysAdminInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

public class ProjectController extends Controller {
	
	@Before(SysAdminInterceptor.class)
	public void allProjects() {

	}

	public void myProjects() {

	}

	public void projectsOfTeam() {
		String teamId = getPara();
		// 只有团队管理员和系统管理 员才能查看团队的项目
		StringBuilder sql = new StringBuilder();

	}
}
