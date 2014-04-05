package com.bugsfly.user;

import java.util.Date;

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
		boolean sysAdmin = Db.findFirst(
				"select * from sys_admin where admin_id=?", user.getStr("id")) != null;
		user.set("sysAdmin", sysAdmin);
		return user;
	}

	public static Record getUser(String id) {
		return Db.findById("user", id);
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

}
