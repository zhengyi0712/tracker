package com.bugsfly.project;

import java.util.ArrayList;
import java.util.List;

import com.bugsfly.user.User;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

public class Project extends Model<Project> {

	private static final long serialVersionUID = -7046480670173881844L;

	public static final Project dao = new Project();
	
	public final static String ROLE_ADMIN = "ADMIN";
	public final static String ROLE_DEVELOPER = "DEVELOPER";
	public final static String ROLE_TESTER = "TESTER";

	public List<User> getUsers() {
		String sql = "select u.*,pu.role ";
		sql += " from user u ";
		sql += " left join project_user pu on pu.user_id=u.id ";
		sql += " where pu.project_id=? ";
		return User.dao.find(sql, this.getStr("id"));
	}

	public String getRoleOfUser(String userId) {
		return Db
				.queryStr(
						"select role from project_user where project_id=? and user_id=?",
						getId(), userId);
	}

	public String getId() {
		return this.getStr("id");
	}

	public Page<Project> paginate(int pn, String name) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" from project p ");
		// 子查询，项目人数
		sql.append(" left join ( ");
		sql.append(" select count(*) u_count,project_id id ");
		sql.append(" from project_user ");
		sql.append(" group by project_id ");
		sql.append(" ) pc  on pc.id=p.id ");
		if (StringKit.notBlank(name)) {
			sql.append(" where name like ? ");
			params.add("%" + name + "%");
		}
		sql.append(" order by create_time desc ");

		return this.paginate(pn, 10, "select p.*,pc.u_count", sql.toString());
	}

}
