package com.bugsfly.team;

import java.util.ArrayList;
import java.util.List;

import com.bugsfly.common.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class TeamManager {

	public final static String ROLE_ADMIN = "admin";
	public final static String ROLE_ORDINARY = "ordinary";

	public static Record getTeam(String id) {
		return Db.findById("team", id);
	}

	public static String getRole(String teamId, String userId) {
		return Db.queryStr(
				"select role from team_user where team_id=? and user_id=?",
				teamId, userId);
	}

	/**
	 * 设置角色
	 * 
	 * @param controller
	 * @throws BusinessException
	 */
	public void setRole(TeamController controller) throws BusinessException {

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

		if (!checkAdminPrivilege(controller, teamId)) {
			throw new BusinessException("抱歉，您无权限进行此操作");
		}

		team_user.set("role", role);

		if (Db.update(
				"update team_user set role=? where team_id=? and user_id=?",
				role, teamId, userId) != 1) {
			throw new BusinessException("保存失败");
		}
	}

	/**
	 * 团队踢除用户
	 * 
	 * @param controller
	 * @throws BusinessException
	 */
	public static void kickUser(TeamController controller)
			throws BusinessException {
		String teamId = controller.getPara("teamId");
		String userId = controller.getPara("userId");

		Record team_user = Db.findFirst(
				"select * from team_user where team_id=? and user_id=?",
				teamId, userId);
		if (team_user == null) {
			throw new BusinessException("不存在的用户或团队");
		}

		// 判断权限，必须要是团队管理员或者系统管理员
		if (!checkAdminPrivilege(controller, teamId)) {
			throw new BusinessException("抱歉，您无权限进行此操作");
		}

		if (ROLE_ADMIN.equals(getRole(teamId, userId))) {
			throw new BusinessException("团队的管理员不能被移出");
		}

		// 判断要踢除的用户是否已经参与团队的项目
		String sql = "select 1 from project_user pu ";
		sql += " left join project p on p.id=pu.project_id ";
		sql += " where pu.user_id=? and p.team_id=? ";
		boolean isJoinInProject = Db.findFirst(sql, userId, teamId) != null;
		if (isJoinInProject) {
			throw new BusinessException("该成员已经参与了团队的项目，不能移出");
		}
		// 删除相关数据
		if (Db.update("delete from team_user where team_id=? and user_id=?",
				teamId, userId) != 1) {
			throw new BusinessException("保存不成功");
		}
	}

	/**
	 * 检查管理员权限
	 * 
	 * @param controller
	 * @param teamId
	 * @return
	 */
	public static boolean checkAdminPrivilege(Controller controller, String teamId) {
		Record user = (Record) controller.getSession()
				.getAttribute(Webkeys.SESSION_USER);
		if (user.getBoolean("isAdmin")) {
			return true;
		}
		String role = getRole(teamId, user.getStr("id"));
		if (ROLE_ADMIN.equals(role)) {
			return true;
		}
		return false;
	}

	/**
	 * 团队列表
	 * 
	 * @param controller
	 * @param userId
	 * @return
	 */
	public static Page<Record> getTeamList(TeamController controller,
			String userId) {
		StringBuilder sql = new StringBuilder();
		List<String> params = new ArrayList<String>();
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
		// 用户关联
		if (userId != null) {
			sql.append(" left join team_user tu on tu.team_id=t.id ");
		}
		sql.append(" where 1=1 ");
		if (userId != null) {
			sql.append(" and tu.user_id=? ");
			params.add(userId);
		}
		// 查询条件
		String name = controller.getPara("name");
		if (StringKit.notBlank(name)) {
			sql.append(" and t.name like ? ");
			params.add("%" + name + "%");
		}

		// 排序
		sql.append(" order by t.create_time desc ");

		String selectSql = "select t.*,pc.p_count,uc.u_count ";
		if (userId != null) {
			selectSql = "select t.*,pc.p_count,uc.u_count,tu.role ";
		}
		Page<Record> page = Db.paginate(
				PaginationUtil.getPageNumber(controller), 10, selectSql,
				sql.toString(), params.toArray());
		controller.keepPara();
		return page;
	}
}
