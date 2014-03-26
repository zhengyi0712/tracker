package com.bugsfly.team;

import com.bugsfly.Webkeys;
import com.bugsfly.util.RegExpUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class TeamController extends Controller {
	private Record user = (Record) getSession().getAttribute(
			Webkeys.SESSION_USER);

	/**
	 * 所有团队
	 */
	public void allTeams() {

		if (!user.getBoolean("isAdmin")) {
			setAttr(Webkeys.REQUEST_MESSAGE, "抱歉您没有权限进行查看所有团队！");
			renderError(403);
			return;
		}
		// 页码
		int pn = 1;
		if (RegExpUtil.checkPositiveInteger(getPara("pn"))) {
			pn = Integer.parseInt(getPara("pn"));
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" from team t ");
		// 项目统计
		sql.append(" left join (  ");
		sql.append(" select team_id,count(*) p_count ");
		sql.append(" from project ");
		sql.append(" group by team_id ");
		sql.append(" ) pc on pc.team_id=t.id ");
		// 用户统计
		sql.append(" left join ( ");
		sql.append(" select team_id,count(*) u_count ");
		sql.append(" from team_user ");
		sql.append(" group by team_id ");
		sql.append(" ) uc on uc.team_id=t.id ");
		// 查询条件，目前暂时没有，以后可能会加
		// 排序
		sql.append(" order by t.create_time desc ");

		Page<Record> page = Db.paginate(pn, 10,
				"select t.*,pc.p_count,uc.u_count ", sql.toString());

		setAttr("page", page);
		render("allTeams.ftl");
	}

	public void createTeam() {
		render("createTeam.ftl");
	}

	public void saveTeamJson() {

	}
}
