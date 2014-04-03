package com.bugsfly.project;

import com.bugsfly.common.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.team.TeamManager;
import com.bugsfly.user.SysAdminInterceptor;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class ProjectController extends Controller {

	@Before(SysAdminInterceptor.class)
	public void allProjects() {

	}

	public void myProjects() {

	}

	public void projectsOfTeam() {
		Record user = (Record) getSession().getAttribute(Webkeys.SESSION_USER);
		String teamId = getPara();
		Record team = TeamManager.getTeam(teamId);
		if (team == null) {
			setAttr(Webkeys.REQUEST_MESSAGE, "不存在 的团队");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}
		// 只有团队成员和系统管理 员才能查看团队的项目
		String role = TeamManager.getRole(teamId, user.getStr("id"));
		if (role == null && !user.getBoolean("isAdmin")) {
			setAttr(Webkeys.REQUEST_MESSAGE, "抱歉，你无权限进行此操作");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}

		Page<Record> page = ProjectManager.getProjectList(this, teamId, null);
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		setAttr("role", role);
		setAttr("team", team);
		render("projectsOfTeam.ftl");
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
