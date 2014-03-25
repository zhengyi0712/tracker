package com.bugsfly.user;

import java.util.Date;
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
		// 判断用户是否超级管理员
		boolean isAdmin = Db.findFirst(
				"select * from sys_admin where admin_id=?", user.getStr("id")) != null;
		user.set("isAdmin", isAdmin);

		return user;
	}
	/**
	 * 更新登录时间
	 * @param id
	 */
	public void updateLoginTime(String id) {
		Db.update("update user set login_time=? where id=?",new Date(),id);
		
	}

}
