package com.bugsfly.user;

import java.util.ArrayList;
import java.util.List;

import com.bugsfly.common.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.project.ProjectAdminJSONInterceptor;
import com.bugsfly.project.ProjectManager;
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
	 * 搜索用户，返回json数据。<br>
	 * 结果不会包含已经禁用的用户，如果有projectId传入，那么对应项目的用户也会过滤掉。
	 */
	public void searchUserJSON() {
		String projectId = getPara("projectId");
		StringBuilder sql = new StringBuilder();
		List<String> params = new ArrayList<String>();

		sql.append(" from user u ");

		if (StringKit.notBlank(projectId)) {
			sql.append(" left join project_user pu ");
			sql.append(" on pu.user_id=u.id ");
		}

		sql.append(" where u.disabled=0 ");

		if (StringKit.notBlank(projectId)) {
			sql.append(" and (pu.project_id is null or pu.project_id!=?) ");
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
		Page<Record> page = Db.paginate(1, 10, "select distinct u.* ", sql.toString(),
				params.toArray());
		System.err.println("SQL:" + sql.toString());
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
		Page<Record> page = UserManager.getUserPage(this, null);
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

	/**
	 * 项目成员，必须是该项目的成员或者系统管理员才可以查看
	 */
	public void usersOfProject() {
		Record user = getSessionAttr(Webkeys.SESSION_USER);

		String projectId = getPara();
		Record project = ProjectManager.getProject(projectId);
		if (project == null) {
			setAttr(Webkeys.REQUEST_MESSAGE, "要查看的项目不存在");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}
		setAttr("project", project);

		String role = ProjectManager.getRole(projectId, user.getStr("id"));
		if (role == null && !user.getBoolean("sysAdmin")) {
			setAttr(Webkeys.REQUEST_MESSAGE, "您无权查看此项目的成员");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}

		if (user.getBoolean("sysAdmin")
				|| ProjectManager.ROLE_ADMIN.equals(role)) {
			setAttr("projectAdmin", true);
		}

		Page<Record> page = UserManager.getUserPage(this, projectId);
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		render("usersOfProject.ftl");
	}

	/**
	 * 添加用户
	 */
	public void addUser() {
		String projectId = getPara("projectId");
		if (StringKit.notBlank(projectId)) {
			Record project = ProjectManager.getProject(projectId);
			setAttr("project", project);
		}
		render("addUser.ftl");
	}

	/**
	 * 添加用户到项目
	 */
	@Before(ProjectAdminJSONInterceptor.class)
	public void addUsersToProject() {
		try {
			UserManager.addUsersToProject(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}
}
