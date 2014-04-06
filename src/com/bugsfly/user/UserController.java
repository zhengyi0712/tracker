package com.bugsfly.user;

import java.util.ArrayList;
import java.util.List;

import com.bugsfly.exception.BusinessException;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
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
		System.out.println("调用用户信息");
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
		String key = getPara("key");
		List<String> params = new ArrayList<String>();
		String sql = " from user ";
		if (StringKit.notBlank(key)) {
			sql += " where zh_name like ? or en_name like ? ";
			sql += " or email like ? or mobile like ? ";
			params.add("%" + key + "%");
			params.add("%" + key + "%");
			params.add("%" + key + "%");
			params.add("%" + key + "%");
		}
		sql += " order by create_time desc ";

		Page<Record> page = Db.paginate(PaginationUtil.getPageNumber(this), 10,
				"select *", sql, params.toArray());
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		keepPara("key");
		render("allUsers.ftl");
	}

	@Before(UserJSONValidator.class)
	public void saveUser() {
		try {
			UserManager.saveUser(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}

	/**
	 * 切换用户状态
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void toggleStatus() {
		try {
			UserManager.toggleStatus(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}
}
