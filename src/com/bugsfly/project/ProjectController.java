package com.bugsfly.project;

import com.bugsfly.common.Webkeys;
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
		Page<Project> page = Project.dao.paginate(
				PaginationUtil.getPageNumber(this), getPara("name"));
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		keepPara();
		render("allProjects.ftl");
	}

	public void myProjects() {
		Record user = getSessionAttr(Webkeys.SESSION_USER);
		Page<Record> page = ProjectManager.getProjectListPage(this,
				user.getStr("id"));
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		keepPara();
		render("myProjects.ftl");
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
	 * 修改简介，进入修改页面
	 */
	public void modifyIntro() {
		Record project = ProjectManager.getProject(getPara());
		setAttr("project", project);
		render("modifyIntro.ftl");
	}

	/**
	 * 更新简介
	 */
	@Before(ProjectAdminJSONInterceptor.class)
	public void updateIntro() {
		try {
			ProjectManager.updateIntro(this);
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

	/**
	 * 踢人
	 */
	@Before(ProjectAdminJSONInterceptor.class)
	public void kickUser() {
		try {
			ProjectManager.kickUser(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}

	/**
	 * 设置角色
	 */
	@Before(ProjectAdminJSONInterceptor.class)
	public void setRole() {
		try {
			ProjectManager.setRole(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}

}
