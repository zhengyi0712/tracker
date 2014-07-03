package com.bugsfly.user;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.bugsfly.common.RegExp;
import com.bugsfly.common.Webkeys;
import com.bugsfly.util.PageKit;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Page;

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
	 * 搜索用户，返回json数据。<br>
	 * 结果不会包含已经禁用的用户，如果有projectId传入，那么对应项目的用户也会过滤掉。
	 */
	public void searchUserJSON() {
		Page<User> page = User.dao.paginateQuery(PageKit.getPn(this), 10,
				getPara("criteria"), false);
		setAttr("list", page.getList());
		renderJson();
	}

	/**
	 * 检查邮箱是否存在
	 */
	public void checkEmail() {
		String email = getPara("user.email");
		if (User.dao.findByEmail(email) != null) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	/**
	 * 检查手机号是否存在
	 */
	public void checkMobile() {
		String mobile = getPara("user.mobile");
		if (User.dao.findByMobile(mobile) != null) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	/**
	 * 所有用户
	 */
	@Before(SysAdminInterceptor.class)
	public void all() {
		Page<User> page = User.dao.paginateQuery(PageKit.getPn(this), 10,
				getPara("criteria"), null);
		setAttr("list", page.getList());
		setAttr("pageLink", PageKit.generateHTML(getRequest(), page));
		keepPara("criteria");
		render("all.ftl");
	}

	/**
	 * 保存用户
	 */
	@Before({ SysAdminJSONInterceptor.class, UserValidator.class })
	public void save() {
		User user = getModel(User.class);
		user.set("id", UUID.randomUUID().toString());
		user.set("create_time", new Date());
		String mobile = user.getStr("mobile");
		String pwd = mobile.substring(mobile.length() - 6);
		String salt = UUID.randomUUID().toString();
		String md5 = DigestUtils.md5Hex(pwd + salt);
		user.set("salt", salt);
		user.set("md5", md5);

		user.save();
		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 切换用户状态
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void toggleStatus() {
		User user = User.dao.findById(getPara("userId"));
		if (user == null) {
			setAttr("msg", "找不到相关的用户");
			renderJson();
			return;
		}

		user.set("disabled", !user.getBoolean("disabled"));
		user.keep("id", "disabled");
		user.update();
		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 添加用户
	 */
	public void add() {
		render("add.ftl");
	}

	/**
	 * 显示修改密码页面
	 */
	public void showChpwd() {
		render("showChpwd.ftl");
	}

	/**
	 * 更新密码
	 */
	public void updatePwdJSON() {
		User user = getSessionAttr(Webkeys.SESSION_USER);
		String oldPwd = getPara("oldPwd");
		String newPwd1 = getPara("newPwd1");
		String newPwd2 = getPara("newPwd2");

		String salt = user.getStr("salt");
		if (!DigestUtils.md5Hex(oldPwd + salt).equals(user.getStr("md5"))) {
			setAttr("msg", "原密码不正确");
			renderJson();
			return;
		}
		if (StringKit.isBlank(newPwd1) || !newPwd1.matches(RegExp.PASSWORD)) {
			setAttr("msg", "新密码不符合规范");
			renderJson();
			return;
		}
		if (!newPwd1.equals(newPwd2)) {
			setAttr("msg", "两次输入密码不一致");
			renderJson();
			return;
		}
		// 更改密码
		user.set("md5", DigestUtils.md5Hex(newPwd1 + salt));
		user.keep("id", "md5");
		user.update();
		getSession().invalidate();
		setAttr("ok", true);
		renderJson();

	}

	/**
	 * 重置密码，密码会变为手机号后六位
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void resetPwd() {
		User user = User.dao.findById(getPara());
		String mobile = user.getStr("mobile");
		String newPwd = mobile.substring(mobile.length() - 6);
		String salt = user.getStr("salt");

		String md5 = DigestUtils.md5Hex(newPwd + salt);
		user.set("md5", md5);

		user.keep("id", "md5");
		user.update();

		setAttr("ok", true);
		renderJson();
	}
}
