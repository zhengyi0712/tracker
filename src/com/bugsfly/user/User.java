package com.bugsfly.user;

import java.util.List;

import com.bugsfly.project.Project;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class User extends Model<User> {

	private static final long serialVersionUID = 5721481570867001046L;

	public static final User dao = new User();

	public List<Project> getProjects() {
		String sql = "select p.*,pu.role ";
		sql += " from project p ";
		sql += " left join project_user pu on pu.project_id=p.id ";
		sql += " where pu.user_id=? ";
		return Project.dao.find(sql, this.getStr("id"));
	}

	public boolean isSysAdmin() {
		return Db.findFirst("select * from sys_admin where admin_id=?",
				this.getStr("id")) != null;
	}

}
