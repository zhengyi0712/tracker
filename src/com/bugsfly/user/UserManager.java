package com.bugsfly.user;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.bugsfly.exception.BusinessException;
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

	/**
	 * 保存用户
	 * 
	 * @param controller
	 * @throws BusinessException
	 */
	public static void saveUser(UserController controller)
			throws BusinessException {
		Record user = new Record();
		user.set("zh_name", controller.getPara("zhName"));
		user.set("en_name", controller.getPara("enName"));
		user.set("email", controller.getPara("email"));
		String mobile = controller.getPara("mobile");
		user.set("mobile", mobile);

		String pwd = mobile.substring(mobile.length() - 6);
		String salt = UUID.randomUUID().toString();

		String md5 = DigestUtils.md5Hex(pwd + salt);

		user.set("salt", salt);
		user.set("md5", md5);
		user.set("create_time", new Date());
		user.set("id", UUID.randomUUID().toString());

		if (!Db.save("user", user)) {
			throw new BusinessException("保存失败");
		}
	}

	public static void toggleStatus(UserController controller)
			throws BusinessException {
		String userId = controller.getPara("userId");
		Record user = getUser(userId);
		if (user == null) {
			throw new BusinessException("找不到相关的用户 ");
		}
		
		boolean disabled = user.getBoolean("disabled");
		user.set("disabled", !disabled);

		if (!Db.update("user", user)) {
			throw new BusinessException("保存失败");
		}

	}
}
