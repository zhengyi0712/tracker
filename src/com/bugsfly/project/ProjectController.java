package com.bugsfly.project;

import com.bugsfly.exception.BusinessException;
import com.bugsfly.user.SysAdminInterceptor;
import com.bugsfly.user.SysAdminJSONInterceptor;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class ProjectController extends Controller {

	@Before(SysAdminInterceptor.class)
	public void allProjects() {

		Page<Record> page = ProjectManager.getProjectList(this, null);
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		keepPara();
		render("allProjects.ftl");
	}

	public void myProjects() {

	}

	/**
	 * 保存项目
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void saveProject() {
		try {
			ProjectManager.saveProject(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}

	/**
	 * 删除项目
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void deleteProject() {
		try {
			ProjectManager.deleteProject(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}

	public void checkNameExist() {
		String name = getPara("name");
		if (Db.findFirst("select 1 from project where name=?", name) != null) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}
}
