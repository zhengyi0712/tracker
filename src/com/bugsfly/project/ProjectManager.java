package com.bugsfly.project;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bugsfly.exception.BusinessException;
import com.bugsfly.team.TeamManager;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class ProjectManager {

	public void saveProject(ProjectController controller)
			throws BusinessException {
		String teamId = controller.getPara("teamId");
		Record team = TeamManager.getTeam(teamId);
		if (team == null) {
			throw new BusinessException("找不到相关的团队");
		}

		// 权限判断
		if (!TeamManager.checkAdminPrivilege(controller, teamId)) {
			throw new BusinessException("抱歉，您没有权限来执行此操作");
		}

		// 判断，保证同一个团队没有重名项目
		Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {

				return false;
			}
		});
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

}
