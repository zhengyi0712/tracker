package com.bugsfly.project;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bugsfly.common.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class ProjectManager {

	public final static String ROLE_ADMIN = "admin";
	public final static String ROLE_DEVELOPER = "developer";

	public static Record getProject(String id) {
		return Db.findById("project", id);
	}

	public static void saveProject(ProjectController controller)
			throws BusinessException {
	}

	public static Page<Record> getProjectList(ProjectController controller,
			String teamId, String userId) {
		StringBuilder sql = new StringBuilder();
		List<String> params = new ArrayList<String>();

		sql.append(" from project p ");
		// 子查询，项目人数
		sql.append(" left join ( ");
		sql.append(" select count(*) u_count,project_id id ");
		sql.append(" from project_user ");
		sql.append(" group by project_id ");
		sql.append(" ) pc  on pc.id=p.id ");
		// 关联用户
		if (userId != null) {
			sql.append(" left join project_user pu on pu.project_id=p.id ");
		}

		sql.append(" where 1=1 ");

		if (userId != null) {
			sql.append(" and pu.user_id=? ");
			params.add(userId);
		}

		if (teamId != null) {
			sql.append(" and p.team_id=? ");
			params.add(teamId);
		}

		String name = controller.getPara("name");
		if (StringKit.notBlank(name)) {
			sql.append(" and p.name like ? ");
			params.add("%" + name + "%");
		}

		String selectSql = "select p.*,pc.u_count ";
		if (userId != null) {
			selectSql = "select p.*,pc.u_count,pu.role ";
		}
		Page<Record> page = Db.paginate(
				PaginationUtil.getPageNumber(controller), 10, selectSql,
				sql.toString(), params.toArray());
		controller.keepPara();
		return page;
	}

	/**
	 * 删除项目
	 * 
	 * @param projectId
	 * @throws BusinessException
	 */
	public static void deleteProject(String projectId) throws BusinessException {
		final Record project = getProject(projectId);
		if (project == null) {
			throw new BusinessException("要删除的项目找不到");
		}

		int userCount = Db.queryInt(
				"select count(*) from project_user where project_id=? ",
				projectId);
		if (userCount != 0) {
			throw new BusinessException("要删除的项目已经有用户存在");
		}

		int bugCount = Db.queryInt(
				"select count(*) from bug where project_id=?", projectId);
		if (bugCount != 0) {
			throw new BusinessException("要删除的项目有相关BUG数据存在，不能删除");
		}

		boolean ok = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				return Db.delete("project", project);
			}
		});

		if (!ok) {
			throw new BusinessException("删除失败");
		}
	}

	public static String getRole(String projectId, String userId) {
		return Db
				.queryStr(
						"select role from project_user where project_id=? and user_id=?",
						projectId, userId);
	}

	/**
	 * 检查项目管理员权限
	 * 
	 * @param controller
	 * @return
	 */
	public static boolean checkAdminPrivilege(Controller controller,
			String projectId) {
		Record project = getProject(projectId);
		if (project == null) {
			return false;
		}

		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);

		String role = getRole(projectId, user.getStr("id"));
		if (!ROLE_ADMIN.equals(role)) {
			return false;
		}

		return true;
	}
}
