package com.bugsfly.user;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class UserManager {
	/**
	 * 依据几点号获取用户，帐号可以是手机号或者邮箱
	 * 
	 * @param account
	 * @return
	 */
	public Record getUserByAccount(String account) {
		Record user = Db.findFirst(
				"select * from user where mobile=? or email=?", account,
				account);

		if (user == null) {
			return null;
		}
		// 得到用户的项目列表，用于导航条生成菜单
		String sql = "select p.*,pu.role from project p ";
		sql += " left join project_user pu on pu.project_id=p.id ";
		sql += " where pu.user_id=?";
		List<Record> projects = Db.find(sql, user.getStr("id"));
		
		user.set("projects", projects);
		// 判断用户是否超级管理员
		boolean isAdmin = Db.findFirst(
				"select * from sys_admin where admin_id=?", user.getStr("id")) != null;
		user.set("admin", isAdmin);

		return user;
	}
}
