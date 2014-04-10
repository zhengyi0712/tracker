package com.bugsfly.project;

import java.util.List;

import com.bugsfly.user.User;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class Project extends Model<Project> {

	private static final long serialVersionUID = -7046480670173881844L;

	public static final Project dao = new Project();

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
						this.getStr("id"), userId);
	}
}
