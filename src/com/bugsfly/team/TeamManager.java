package com.bugsfly.team;

import com.bugsfly.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class TeamManager {

	public final static String ROLE_ADMIN = "admin";
	public final static String ROLE_ORDINARY = "ordinary";

	public Record getTeam(String id) {
		return Db.findById("team", id);
	}

	public String getRoleOfUser(String teamId, String userId) {
		return Db.queryStr(
				"select role from team_user where team_id=? and user_id=?",
				teamId, userId);
	}

	public void setUserRole(TeamController controller) throws BusinessException {

		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);

		String teamId = controller.getPara("teamId");
		String userId = controller.getPara("userId");
		String role = controller.getPara("role");
		if (!ROLE_ADMIN.equals(role) && !ROLE_ORDINARY.equals(role)) {
			throw new BusinessException("未知的角色");
		}
		Record team_user = Db.findFirst(
				"select * from team_user where team_id=? and user_id=?",
				teamId, userId);

		if (team_user == null) {
			throw new BusinessException("不存在的团队或用户");
		}

		if (!TeamManager.ROLE_ADMIN.equals(getRoleOfUser(teamId,
				user.getStr("id")))
				&& !user.getBoolean("isAdmin")) {
			throw new BusinessException("抱歉，您无权限进行此操作");
		}

		team_user.set("role", role);

		if (!Db.update("team_user", team_user)) {
			throw new BusinessException("保存失败");
		}
	}

	/**
	 * 团队踢除用户
	 * 
	 * @param controller
	 * @throws BusinessException
	 */
	public void kickUser(TeamController controller) throws BusinessException {
		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);
		String teamId = controller.getPara("teamId");
		String userId = controller.getPara("userId");

		Record team_user = Db.findFirst(
				"select * from team_user where team_id=? and user_id=?",
				teamId, userId);
		if (team_user == null) {
			throw new BusinessException("不存在的用户或团队");
		}

		// 判断权限，必须要是团队管理员或者系统管理员
		if (!TeamManager.ROLE_ADMIN.equals(getRoleOfUser(teamId,
				user.getStr("id")))
				&& !user.getBoolean("isAdmin")) {
			throw new BusinessException("抱歉，您无权限进行此操作");
		}
		// 判断要踢除的用户是否已经参与团队的项目
		
		// TODO 未完
		Db.delete("team_user", team_user);
	}

}
