package com.bugsfly.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.bugsfly.common.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class ProjectManager {

	public final static String ROLE_ADMIN = "ADMIN";
	public final static String ROLE_DEVELOPER = "DEVELOPER";
	public final static String ROLE_TESTER = "TESTER";

	public static Record getProject(String id) {
		return Db.findById("project", id);
	}

	public static void saveProject(ProjectController controller)
			throws BusinessException {
		String name = controller.getPara("name");
		String intro = controller.getPara("intro");

		if (StringKit.isBlank(name)) {
			throw new BusinessException("名称不能为空");
		}
		name = name.trim();
		if (name.length() < 2 || name.length() > 30) {
			throw new BusinessException("名称必须在2-30字符之间");
		}

		if (Db.findFirst("select 1 from project where name=?", name) != null) {
			throw new BusinessException("项目名称已经存在，请更换一个试试");
		}

		if (StringKit.notBlank(intro)) {
			if (intro.length() > 200) {
				throw new BusinessException("简介不能超过200字");
			}
		}
		Record project = new Record();
		project.set("id", UUID.randomUUID().toString());
		project.set("name", name);
		project.set("intro", intro);
		project.set("create_time", new Date());

		if (!Db.save("project", project)) {
			throw new BusinessException("保存失败");
		}

	}

	public static Page<Record> getProjectList(ProjectController controller,
			String userId) {
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
	public static void deleteProject(ProjectController controller)
			throws BusinessException {
		String projectId = controller.getPara();
		Record project = getProject(projectId);
		if (project == null) {
			throw new BusinessException("要删除的项目找不到");
		}

		long userCount = Db.queryLong(
				"select count(*) from project_user where project_id=? ",
				projectId);
		if (userCount != 0) {
			throw new BusinessException("要删除的项目已经有用户存在，无法完成操作");
		}

		long issueCount = Db.queryLong(
				"select count(*) from issue where project_id=?", projectId);
		if (issueCount != 0) {
			throw new BusinessException("要删除的项目已经有相关问题存在，无法完成操作");
		}

		if (!Db.deleteById("project", projectId)) {
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
