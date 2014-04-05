package com.bugsfly.user;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class UserController extends Controller {

	public void index() {
		render("index.ftl");
	}

	/**
	 * 用户信息
	 */
	public void userinfo() {
		render("userinfo.ftl");
	}


	/**
	 * 搜索用户，返回json数据
	 */
	public void searchUserJSON() {
		String key = getPara("key");
		if (key == null) {
			renderJson("{list:null}");
			return;
		}
		String sql = " from user where disabled=0 and(zh_name like ? ";
		sql += " or en_name like ? or mobile like ? or email like ?) ";
		sql += " order by login_time ";
		key = "%" + key + "%";
		Page<Record> page = Db.paginate(1, 5, "select * ", sql, key, key, key,
				key);
		setAttr("list", page.getList());
		renderJson();
	}

	/**
	 * 检查邮箱是否存在
	 */
	public void checkEmailExist() {
		String email = getPara("email");
		boolean isExist = Db.findFirst("select 1 from user where email=?",
				email) != null;
		if (isExist) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	/**
	 * 检查手机号是否存在
	 */
	public void checkMobileExist() {
		String mobile = getPara("mobile");
		boolean isExist = Db.findFirst("select 1 from user where mobile=?",
				mobile) != null;
		if (isExist) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	/**
	 * 所有用户
	 */
	@Before(SysAdminInterceptor.class)
	public void allUsers() {

	}
	
}
