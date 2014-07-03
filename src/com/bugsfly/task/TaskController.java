package com.bugsfly.task;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.bugsfly.common.Webkeys;
import com.bugsfly.project.Project;
import com.bugsfly.user.User;
import com.bugsfly.util.PageKit;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 任务请求处理类<br>
 * 对于项目，管理员有一切权限（除了删除项目），开发可以被分派任务，将分派给自己的任务设为已解决，测试只能上报任务。<br>
 * 对任务的操作会比较频繁，大部分方法都加了事务，为了防止并发脏读由并发引起的数据错乱。
 */
public class TaskController extends Controller {
	/**
	 * bug列表。<br>
	 * 程序首先会获取用户 参与的项目列表。<br>
	 * 在没有传入项目ID的情况下，程序会从cookie里找到最后一次查看任务列表的项目。<br>
	 * 如果cookie里没有可用的项目信息，就显示第一个项目的任务列表。
	 */
	public void index() {
		User user = getSessionAttr(Webkeys.SESSION_USER);
		Project project = Project.dao.findById(getPara());

		if (project == null) {
			project = Project.dao.findById(getCookie("project"));
			// 从cookie里取出的项目如果不是自己的，把project置空
			if (project != null && project.getRoleOfUser(user.getId()) == null) {
				project = null;
			}
		}

		if (project == null) {
			List<Project> projects = user.getProjects();
			if (projects != null && projects.size() > 0) {
				project = projects.get(0);
			}
		}

		if (project == null) {
			render("index.ftl");
			return;
		}

		setAttr("project", project);

		// 几个查询条件
		String title = getPara("title");
		String[] tagIdArr = getParaValues("tagId");
		String[] statusArr = getParaValues("status");
		String[] assignUserIdArr = getParaValues("assignUserId");

		Page<Task> page = Task.dao.paginate(PageKit.getPn(this),
				project.getId(), title, tagIdArr, statusArr, assignUserIdArr);

		setAttr("list", page.getList());
		setAttr("pageLink", PageKit.generateHTML(getRequest(), page));
		setAttr("tags", Tag.dao.findAll());
		String role = project.getRoleOfUser(user.getId());
		setAttr("role", role);

		// 如果查看的是自己的项目，保存cookie
		if (role != null) {
			setCookie("project", project.getId(), 60 * 60 * 24 * 15);
		}
		// 这里不能用controller的keepara()方法，因为，如果多选只选了一个传到页面不是数组
		setAttr("title", title);
		setAttr("tagIdArr", tagIdArr);
		setAttr("statusArr", statusArr);
		setAttr("assignUserIdArr", assignUserIdArr);

		render("index.ftl");

	}

	public void showTaskDetail() {
		User user = getSessionAttr(Webkeys.SESSION_USER);
		Task task = Task.dao.findById(getPara());
		setAttr("task", task);
		Project project = task.getProject();
		setAttr("project", project);
		String role = project.getRoleOfUser(user.getId());
		setAttr("role", role);
		// 是否可返工判定，条件：已完成状态并且完成不超过3天
		if (Project.ROLE_ADMIN.equals(role)
				&& Task.STATUS_FINISHED.equals(task.getStr("status"))) {
			Date finishTime = task.getTimestamp("finish_time");
			Date now = new Date();
			if (now.getTime() - finishTime.getTime() < 1000 * 60 * 60 * 24 * 3) {
				setAttr("reworkable", true);
			}
		}
		render("showTaskDetail.ftl");

	}

	public void showCreateTask() {
		Project project = Project.dao.findById(getPara());
		setAttr("project", project);
		setAttr("tags", Tag.dao.findAll());
		render("showCreateTask.ftl");
	}

	@Before({ TaskValidator.class, Tx.class })
	public void saveTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task task = getModel(Task.class);
		Project project = task.getProject();
		String role = project.getRoleOfUser(sessionUser.getId());

		if (!Project.ROLE_ADMIN.equals(role)
				&& !Project.ROLE_TESTER.equals(role)) {
			setAttr("msg", "抱歉您无权进行此操作");
			renderJson();
			return;
		}

		String taskId = UUID.randomUUID().toString();
		task.set("id", taskId);
		task.set("update_time", new Date());
		task.set("status", Task.STATUS_CREATED);
		task.set("create_user_id", sessionUser.getId());
		task.set("update_user_id", sessionUser.getId());

		if (!task.save()) {
			throw new IllegalStateException("保存任务失败");
		}
		// 保存标签
		String[] tagIds = getParaValues("tagId");
		if (tagIds != null) {
			for (String tagId : tagIds) {
				if (!task.saveTag(tagId)) {
					throw new IllegalStateException("保存标签失败");
				}
			}
		}
		// 保存日志
		task.log(sessionUser.toHTML() + "创建任务");

		setAttr("ok", true);
		renderJson();
	}

	public void showUpdateTask() {
		Task task = Task.dao.findById(getPara());
		setAttr("task", task);
		setAttr("tags", Tag.dao.findAll());
		render("showUpdateTask.ftl");
	}

	@Before({ TaskValidator.class, Tx.class })
	public void updateTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task taskModel = getModel(Task.class);
		Task oldTask = Task.dao.findById(taskModel.getStr("id"));
		Project project = oldTask.getProject();

		String role = project.getRoleOfUser(sessionUser.getId());

		if (!Project.ROLE_ADMIN.equals(role)
				&& !Project.ROLE_TESTER.equals(role)) {
			setAttr("msg", "抱歉您无权进行此操作");
			renderJson();
			return;
		}

		String status = oldTask.getStr("status");
		if (Task.STATUS_CLOSED.equals(status)
				|| Task.STATUS_FINISHED.equals(status)) {
			setAttr("msg", "该任务处于不能被修改的状态");
			renderJson();
			return;
		}

		taskModel.set("update_time", new Date());
		taskModel.set("update_user_id", sessionUser.get("id"));
		taskModel
				.keep("id", "title", "detail", "update_time", "update_user_id");
		if (!taskModel.update()) {
			throw new IllegalStateException("更新任务失败");
		}
		// 更新标签，为了逻辑简单，直接删除旧的，保存新的
		String[] tagIds = getParaValues("tagId");
		oldTask.deleteAllTags();

		if (tagIds != null) {
			for (String tagId : tagIds) {
				if (!oldTask.saveTag(tagId))
					throw new RuntimeException("保存新的标签失败");
			}

		}
		// 记录日志
		oldTask.log(sessionUser.toHTML() + "更新任务");

		setAttr("ok", true);
		renderJson();

	}

	/**
	 * 分派任务，开启事务防止脏读
	 */
	@Before(Tx.class)
	public void assignTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task task = Task.dao.findById(getPara("id"));
		Project project = task.getProject();
		if (!Project.ROLE_ADMIN.equals(project.getRoleOfUser(sessionUser
				.getId()))) {
			setAttr("msg", "您没有权限分派任务");
			renderJson();
			return;
		}

		User user = User.dao.findById(getPara("userId"));
		String role = project.getRoleOfUser(user.getId());
		if (!Project.ROLE_DEVELOPER.equals(role)
				&& !Project.ROLE_ADMIN.equals(role)) {
			setAttr("msg", "该用户不能被分派任务");
			renderJson();
			return;
		}

		// 任务必须要是新建状态或者已分派状态才能分配
		if (!Task.STATUS_CREATED.equals(task.getStr("status"))
				&& !Task.STATUS_ASSIGNED.equals(task.getStr("status"))) {
			setAttr("msg", "该任务不能被分配");
			renderJson();
			return;
		}
		task.set("assign_user_id", user.getId());
		task.set("status", Task.STATUS_ASSIGNED);
		task.keep("id", "assign_user_id", "status");
		task.update();
		// 记录日志
		task.log(sessionUser.toHTML() + "将任务分派给" + user.toHTML());

		setAttr("ok", true);
		renderJson();

	}

	/**
	 * 完成任，必须要是被分派人自己或者管理员才能执行此操作，且任务处于新建、已分派或返工状态。<br>
	 * 如果任务没有分派，管理员仍然可以将任务完成，程序会将任务分派给管理员自己。
	 */
	@Before(Tx.class)
	public void finishTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task task = Task.dao.findById(getPara());
		Project project = task.getProject();

		if (!Project.ROLE_ADMIN.equals(project.getRoleOfUser(sessionUser
				.getStr("id")))
				&& !sessionUser.getId().equals(task.getStr("assign_user_id"))) {
			setAttr("msg", "您无权进行此操作");
			renderJson();
			return;
		}
		// 必须是新建或者已分派或者返工状态才可以完成
		if (!Task.STATUS_CREATED.equals(task.getStr("status"))
				&& !Task.STATUS_ASSIGNED.equals(task.getStr("status"))
				&& !Task.STATUS_REWORKED.equals(task.getStr("status"))) {
			setAttr("msg", "该任务不能执行完成操作");
			renderJson();
			return;
		}
		User assignUser = task.getAssignUser();
		// 任务没有被分派的情况
		if (assignUser == null) {
			task.set("assign_user_id", sessionUser.getId());
			// 记录自动分派日志
			task.log("系统自动将任务分派给" + sessionUser.toHTML());
		}
		task.set("finish_time", new Date());
		task.set("status", Task.STATUS_FINISHED);
		task.keep("id", "assign_user_id", "finish_time", "status");
		task.update();
		// 完成日志
		task.log(sessionUser.toHTML() + "将任务设置为完成");

		setAttr("ok", true);
		renderJson();

	}

	/**
	 * 返工,只能对已经完成并且不超过3天的任务返工。
	 */
	@Before(Tx.class)
	public void reworkTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task task = Task.dao.findById(getPara());
		Project project = task.getProject();
		if (!Project.ROLE_ADMIN.equals(project.getRoleOfUser(sessionUser
				.getId()))) {
			setAttr("msg", "您无权限将任务返工");
			renderJson();
			return;
		}

		if (!Task.STATUS_FINISHED.equals(task.getStr("status"))) {
			setAttr("msg", "该任务不能被返工");
			renderJson();
			return;
		}

		Date finishTime = task.getTimestamp("finish_time");
		Date now = new Date();
		if (now.getTime() - finishTime.getTime() > 1000 * 60 * 60 * 24 * 3) {
			setAttr("msg", "任务完成已经超过3天，不能再返工");
			renderJson();
			return;
		}

		task.set("finish_time", null);
		task.set("status", Task.STATUS_REWORKED);
		task.keep("id", "status", "finish_time");
		task.update();
		// 记录日志
		task.log(sessionUser.toHTML() + "将任务返工");

		setAttr("ok", true);
		renderJson();

	}

	/**
	 * 关闭任务，任务只要不是已完成状态就可以关闭。
	 */
	@Before(Tx.class)
	public void closeTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task task = Task.dao.findById(getPara());
		Project project = task.getProject();
		if (!Project.ROLE_ADMIN.equals(project.getRoleOfUser(sessionUser
				.getId()))) {
			setAttr("msg", "您无权限关闭任务");
			renderJson();
			return;
		}

		if (Task.STATUS_FINISHED.equals(task.getStr("status"))) {
			setAttr("msg", "任务不能被关闭");
			renderJson();
			return;
		}

		task.set("status", Task.STATUS_CLOSED);
		task.keep("id", "status");
		task.update();

		// 记录日志
		task.log(sessionUser.toHTML() + "将任务关闭");

		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 删除，任务只要不是已完成状态就可以删除，关闭了也可以删除。
	 */
	@Before(Tx.class)
	public void deleteTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task task = Task.dao.findById(getPara());
		Project project = task.getProject();
		if (!Project.ROLE_ADMIN.equals(project.getRoleOfUser(sessionUser
				.getId()))) {
			setAttr("msg", "您无权限删除任务");
			renderJson();
			return;
		}

		if (Task.STATUS_FINISHED.equals(task.getStr("status"))) {
			setAttr("msg", "任务不能被删除");
			renderJson();
			return;
		}
		// 先删除任务关联标签和日志 ，再删除任务
		task.deleteAllTags();
		task.deleteAllLogs();
		task.delete();
		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 显示日志
	 */
	public void showLogList() {
		Task task = Task.dao.findById(getPara());
		setAttr("task", task);
		setAttr("logList", task.getTaskLogList());
		render("showLogList.ftl");
	}
}
