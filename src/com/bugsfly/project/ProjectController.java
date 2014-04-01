package com.bugsfly.project;

import com.bugsfly.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.team.TeamManager;
import com.bugsfly.user.SysAdminInterceptor;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
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
		TeamManager teamManager = new TeamManager();
		Record team = teamManager.getTeam(teamId);
		if (team == null) {
			setAttr(Webkeys.REQUEST_MESSAGE, "不存在 的团队");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}
		// 只有团队管理员和系统管理 员才能查看团队的项目
		String role = teamManager.getRoleOfUser(teamId, user.getStr("id"));
		if (role == null && !user.getBoolean("isAdmin")) {
			setAttr(Webkeys.REQUEST_MESSAGE, "抱歉，你无权限进行此操作");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" from project p ");
		// 子查询，项目人数
		sql.append(" left join ( ");
		sql.append(" select count(*) u_count,project_id id ");
		sql.append(" from project_user ");
		sql.append(" group by project_id ");
		sql.append(" ) pu  on pu.id=p.id ");

		sql.append(" where p.team_id=? ");
		int pn = PaginationUtil.getPageNumber(this);
		Page<Record> page = Db.paginate(pn, 10, "select p.*,pu.u_count ",
				sql.toString(), teamId);
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		setAttr("role", role);
		setAttr("team", team);
		render("projectsOfTeam.ftl");
	}
	
	public void saveProject(){
		ProjectManager manager = new ProjectManager();
		try {
			manager.saveProject(this);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
