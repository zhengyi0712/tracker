package com.bugsfly.project;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.bugsfly.common.Webkeys;
import com.bugsfly.user.SysAdminInterceptor;
import com.bugsfly.user.SysAdminJSONInterceptor;
import com.bugsfly.user.User;
import com.bugsfly.user.UserValidator;
import com.bugsfly.util.PageKit;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

public class ProjectController extends Controller {

	@Before(SysAdminInterceptor.class)
	public void all() {
		Page<Project> page = Project.dao.paginate(PageKit.getPn(this),
				getPara("name"));
		setAttr("list", page.getList());
		setAttr("pageLink", PageKit.generateHTML(getRequest(), page));
		keepPara();
		render("all.ftl");
	}

	public void myProjects() {
		User user = getSessionAttr(Webkeys.SESSION_USER);
		Page<Project> page = user.paginateProject(PageKit.getPn(this),
				getPara("name"));
		setAttr("list", page.getList());
		setAttr("pageLink", PageKit.generateHTML(getRequest(), page));
		keepPara();
		render("myProjects.ftl");
	}

	/**
	 * 保存项目
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void save() {
		Project project = getModel(Project.class);
		project.set("id", UUID.randomUUID().toString());
		project.set("create_time", new Date());
		project.save();
		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 修改简介，进入修改页面
	 */
	public void modifyIntro() {
		setAttr("project", Project.dao.findById(getPara()));
		render("modifyIntro.ftl");
	}

	/**
	 * 更新简介
	 */
	@Before(ProjectAdminJSONInterceptor.class)
	public void updateIntro() {
		Project project = getModel(Project.class);
		project.keep("id", "intro");
		project.update();
		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 删除项目
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void deleteProject() {
		Project project = Project.dao.findById(getPara());
		if (project == null) {
			setAttr("msg", "找不到要删除的项目");
		} else if (project.getUserCount() > 0) {
			setAttr("msg", "要删除的项目已经有成员存在不能删除");
		} else if (project.getTaskCount() > 0) {
			setAttr("msg", "要删除的项目已经在任务存在，不能删除");
		} else {
			project.delete();
			setAttr("ok", true);
		}
		renderJson();
	}

	public void checkName() {
		String name = getPara("project.name");
		if (Project.dao.findByName(name) != null) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	/**
	 * 显示项目的成员，只有该项目的成员或者系统管理员才可以查看
	 */
	public void showUsers() {
		Project project = Project.dao.findById(getPara());
		User user = getSessionAttr(Webkeys.SESSION_USER);
		if (project == null) {
			setAttr(Webkeys.REQUEST_MESSAGE, "要查看的项目不存在");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}
		String role = project.getRoleOfUser(user.getId());
		if (role == null && !user.isSysAdmin()) {
			setAttr(Webkeys.REQUEST_MESSAGE, "你无权查看该项目的成员");
			render(Webkeys.PROMPT_PAGE_PATH);
			return;
		}
		if (Project.ROLE_ADMIN.equals(role) || user.isSysAdmin()) {
			setAttr("projectAdmin", true);
		}

		setAttr("project", project);
		Page<User> page = project.paginateUser(PageKit.getPn(this),
				getPara("key"));
		setAttr("list", page.getList());
		setAttr("pageLink", PageKit.generateHTML(getRequest(), page));
		render("showUsers.ftl");

	}

	/**
	 * 添加现有的用户
	 */
	@Before({ ProjectAdminJSONInterceptor.class, Tx.class })
	public void addCurrentUsers() {
		Project project = Project.dao.findById(getPara("project.id"));
		String[] userIds = getParaValues("userId");

		if (userIds == null || userIds.length == 0) {
			setAttr("msg", "没有选择任何用户");
			renderJson();
			return;
		}

		final String role = getPara("project.role");
		if (!Project.checkRole(role)) {
			setAttr("msg", "未知的角色");
			renderJson();
			return;
		}

		for (String userId : userIds) {
			if (project.isMemberExist(userId)) {
				continue;
			}
			if (!project.saveMember(userId, role)) {
				throw new RuntimeException("save member fail");
			}
		}
		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 踢人
	 */
	@Before({ ProjectAdminJSONInterceptor.class, Tx.class })
	public void kickUser() {
		Project project = Project.dao.findById(getPara("project.id"));
		String userId = getPara("user.id");
		String projectRole = project.getRoleOfUser(userId);

		if (projectRole == null) {
			setAttr("msg", "用户与项目不存在关联");
			renderJson();
			return;
		}

		if (Project.ROLE_ADMIN.equals(projectRole)) {
			setAttr("msg", "项目的管理员不能被踢除");
			renderJson();
			return;
		}

		if (!project.deleteMember(userId)) {
			setAttr("msg", "保存失败");
			renderJson();
			return;
		}
		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 设置角色
	 */
	@Before(ProjectAdminJSONInterceptor.class)
	public void setRole() {
		Project project = Project.dao.findById(getPara("project.id"));
		String userId = getPara("user.id");
		String role = getPara("project.role");

		if (project.getRoleOfUser(userId) == null) {
			setAttr("msg", "用户与项目不存在关联");
			renderJson();
			return;
		}

		if (!Project.checkRole(role)) {
			setAttr("msg", "未知的角色");
			renderJson();
			return;
		}
		if (!project.setRole(userId, role)) {
			setAttr("msg", "保存失败");
			renderJson();
			return;
		}

		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 添加用户
	 */
	public void addUser() {
		Project project = Project.dao.findById(getPara());
		setAttr("project", project);
		render("addUser.ftl");
	}

	/**
	 * 保存用户
	 */
	@Before({ ProjectAdminJSONInterceptor.class, UserValidator.class, Tx.class })
	public void saveUser() {
		Project project = Project.dao.findById(getPara("project.id"));
		String role = getPara("project.role");
		if (!Project.checkRole(role)) {
			setAttr("msg", "未知的角色");
			renderJson();
			return;
		}
		User user = getModel(User.class);
		String userId = UUID.randomUUID().toString();
		user.set("id", userId);
		user.set("create_time", new Date());
		String mobile = user.getStr("mobile");
		String pwd = mobile.substring(mobile.length() - 6);
		String salt = UUID.randomUUID().toString();
		String md5 = DigestUtils.md5Hex(pwd + salt);
		user.set("salt", salt);
		user.set("md5", md5);
		if (!user.save()) {
			throw new RuntimeException("save user fail");
		}
		if (!project.saveMember(userId, role)) {
			throw new RuntimeException("save user to project fail");
		}
		setAttr("ok", true);
		renderJson();
	}
}
