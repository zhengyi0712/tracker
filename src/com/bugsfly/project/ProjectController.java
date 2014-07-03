package com.bugsfly.project;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import com.bugsfly.common.Webkeys;
import com.bugsfly.task.Task;
import com.bugsfly.user.SysAdminInterceptor;
import com.bugsfly.user.SysAdminJSONInterceptor;
import com.bugsfly.user.User;
import com.bugsfly.util.PageKit;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class ProjectController extends Controller {

	@Before(SysAdminInterceptor.class)
	public void allProjects() {
		Page<Project> page = Project.dao.paginate(
				PageKit.getPn(this), getPara("name"));
		setAttr("list", page.getList());
		setAttr("pageLink",
				PageKit.generateHTML(getRequest(), page));
		keepPara();
		render("allProjects.ftl");
	}

	public void myProjects() {
		User user = getSessionAttr(Webkeys.SESSION_USER);
		Page<Project> page = user.paginateProject(
				PageKit.getPn(this), getPara("name"));
		setAttr("list", page.getList());
		setAttr("pageLink",
				PageKit.generateHTML(getRequest(), page));
		keepPara();
		render("myProjects.ftl");
	}

	/**
	 * 保存项目
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void saveProject() {
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

	public void checkNameExist() {
		String name = getPara("project.name");
		if (Db.findFirst("select 1 from project where name=?", name) != null) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	/**
	 * 显示项目的成员
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
		Page<User> page = project.paginateUser(
				PageKit.getPn(this), getPara("key"));
		setAttr("list", page.getList());
		setAttr("pageLink",
				PageKit.generateHTML(getRequest(), page));
		render("showUsers.ftl");

	}

	/**
	 * 添加现有的用户
	 */
	@Before(ProjectAdminJSONInterceptor.class)
	public void addCurrentUsers() {
		final Project project = Project.dao.findById(getPara("project.id"));
		final String[] userIds = getParaValues("userId");

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

		boolean ok = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				for (String userId : userIds) {
					Record project_user = new Record();
					project_user.set("project_id", project.getId());
					project_user.set("user_id", userId);
					project_user.set("role", role);
					if (!Db.save("project_user", project_user)) {
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

		String sql = "select count(*) from task where project_id=? and assign_user_id=? and status !=? ";
		if (Db.queryLong(sql, project.getId(), userId, Task.STATUS_FINISHED) > 0) {
			setAttr("msg", "该成员有已分派且未完成的任务，不能踢除");
			renderJson();
			return;
		}

		sql = "delete from project_user where project_id=? and user_id=?";
		if (Db.update(sql, project.getId(), userId) != 1) {
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
		String sql = "update project_user set role=? where project_id=? and user_id=?";
		if (Db.update(sql, role, project.getId(), userId) != 1) {
			setAttr("msg", "保存失败");
			renderJson();
			return;
		}

		setAttr("ok", true);
		renderJson();
	}

}
