package com.bugsfly.user;

import com.bugsfly.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.team.TeamManager;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class UserController extends Controller {

	public void index() {
		render("index.ftl");
	}

	/**
	 * 用户信息
	 */
	public void userinfo() {
		render("userinfo.ftl");
	}

	/**
	 * 团队所有成员只有团队成员和管理员可以看，团队管理员还可以做一些操作
	 */
	public void usersOfTeam() {
		Record user = (Record) getSession().getAttribute(Webkeys.SESSION_USER);

		TeamManager teamManager = new TeamManager();
		String teamId = getPara();
		Record team = teamManager.getTeam(teamId);

		if (team == null) {
			setAttr(Webkeys.REQUEST_MESSAGE, "要查看的团队不存在");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}

		String role = teamManager.getRoleOfUser(teamId, user.getStr("id"));
		if (role == null && !user.getBoolean("isAdmin")) {
			setAttr(Webkeys.REQUEST_MESSAGE, "抱歉，你无权限进行此操作");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}

		setAttr("role", role);
		setAttr("team", team);

		StringBuilder sql = new StringBuilder();
		sql.append(" from user u ");
		sql.append(" left join team_user tu on u.id=tu.user_id ");
		sql.append(" where tu.team_id=? ");

		int pn = PaginationUtil.getPageNumber(this);
		Page<Record> page = Db.paginate(pn, 10, " select u.*,tu.role ",
				sql.toString(), teamId);
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		render("usersOfTeam.ftl");

	}
	/**
	 * 为团队添加用户
	 */
	public void addUserToTeam(){
		UserManager userManager = new UserManager();
		try {
			userManager.addUserOfTeam(this);
			render("addUserOfTeam.ftl");
		} catch (BusinessException e) {
			setAttr(Webkeys.REQUEST_MESSAGE, e.getMessage());
			render(Webkeys.PROMPT_PAGE_PATH);
		}
	}
	public void saveUserToTeam(){
		
	}
	
	
}
