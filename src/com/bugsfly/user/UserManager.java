package com.bugsfly.user;

import java.util.Date;

import com.bugsfly.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.team.TeamManager;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class UserManager {
	/**
	 * 根据帐号获取用户
	 * 
	 * @param account
	 * @return
	 */
	public Record getUserByAccount(String account) {

		Record user = Db.findFirst(
				"select * from user where mobile=? or email=? ", account,
				account);
		if (user == null) {
			return null;
		}
		boolean isAdmin = Db.findFirst(
				"select * from sys_admin where admin_id=?", user.getStr("id")) != null;
		user.set("isAdmin", isAdmin);
		return user;
	}

	/**
	 * 更新登录时间
	 * 
	 * @param id
	 */
	public void updateLoginTime(String id) {
		String sql = "update user set login_time=? where id=?";
		Db.update(sql, new Date(), id);
	}

	public void addUserOfTeam(UserController controller)
			throws BusinessException {
		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);
		String teamId = controller.getPara();
		TeamManager teamManager = new TeamManager();

		Record team = teamManager.getTeam(teamId);
		if (team == null) {
			throw new BusinessException("不存在的团队");
		}

		// 如果不是管理员，要判断角色
		if (!user.getBoolean("isAdmin")) {
			String role = teamManager.getRoleOfUser(teamId, user.getStr("id"));
			if (!TeamManager.ROLE_ADMIN.equals(role)) {
				throw new BusinessException("抱歉，您滑 权限进行此操作");
			}
		}
		controller.setAttr("team", team);
	}

}
