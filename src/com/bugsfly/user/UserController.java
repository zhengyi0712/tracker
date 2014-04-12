package com.bugsfly.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.bugsfly.common.Webkeys;
import com.bugsfly.project.Project;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
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
	 * 搜索用户，返回json数据。<br>
	 * 结果不会包含已经禁用的用户，如果有projectId传入，那么对应项目的用户也会过滤掉。
	 */
	public void searchUserJSON() {
		String projectId = getPara("projectId");
		StringBuilder sql = new StringBuilder();
		List<String> params = new ArrayList<String>();

		sql.append(" from user u ");

		sql.append(" where u.disabled=0 ");

		if (StringKit.notBlank(projectId)) {
			sql.append(" and u.id not in ( ");
			sql.append(" select user_id from project_user ");
			sql.append(" where project_id=?) ");
			params.add(projectId);

		}

		String key = getPara("key");
		if (StringKit.notBlank(key)) {
			sql.append(" and(zh_name like ?  ");
			sql.append(" or en_name like ? or mobile like ? or email like ?) ");
			sql.append(" order by login_time desc ");
			params.add("%" + key + "%");
			params.add("%" + key + "%");
			params.add("%" + key + "%");
			params.add("%" + key + "%");
		}
		Page<Record> page = Db.paginate(1, 10, "select distinct u.* ",
				sql.toString(), params.toArray());
		System.err.println("SQL:" + sql.toString());
		setAttr("list", page.getList());
		renderJson();
	}

	/**
	 * 检查邮箱是否存在
	 */
	public void checkEmailExist() {
		String email = getPara("user.email");
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
		String mobile = getPara("user.mobile");
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
		Page<User> page = User.dao.paginate(PaginationUtil.getPageNumber(this),
				getPara("key"));
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		keepPara("key");
		render("allUsers.ftl");
	}

	/**
	 * 保存用户
	 */
	@Before(UserValidator.class)
	public void saveUser() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		final Project project = Project.dao.findById(getPara("project.id"));
		// 如果是添加用户到项目，需要是项目管理员。否则必须是系统管理员
		if (project == null) {
			if (!sessionUser.isSysAdmin()) {
				setAttr("msg", "抱歉，您无权限进行些操作");
				renderJson();
				return;
			}
		} else {
			if (!Project.ROLE_ADMIN.equals(project.getRoleOfUser(sessionUser
					.getId())) && !sessionUser.isSysAdmin()) {
				setAttr("msg", "抱歉，您无权限进行些操作");
				renderJson();
				return;
			}
		}

		final User user = getModel(User.class);
		user.set("id", UUID.randomUUID().toString());
		user.set("create_time", new Date());
		String mobile = user.getStr("mobile");
		String pwd = mobile.substring(mobile.length() - 6);
		String salt = UUID.randomUUID().toString();
		String md5 = DigestUtils.md5Hex(pwd + salt);
		user.set("salt", salt);
		user.set("md5", md5);

		final String role = getPara("project.role");
		boolean ok = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				if (!user.save()) {
					return false;
				}
				if (project != null) {
					String sql = "insert into project_user(project_id,user_id,role) ";
					sql += " values(?,?,?) ";
					if (Db.update(sql, project.getId(), user.getId(), role) != 1) {
						return false;
					}
				}

				return true;
			}
		});

		if (ok) {
			setAttr("ok", true);
		} else {
			setAttr("msg", "保存失败");
		}
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
	public void addUser() {
		setAttr("project", Project.dao.findById(getPara()));
		render("addUser.ftl");
	}
}
