package com.bugsfly.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.bugsfly.common.Webkeys;
import com.bugsfly.project.Project;
import com.bugsfly.user.User;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
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
		String projectId = getPara();
		Project project = null;

		if (StringKit.isBlank(projectId)) {
			projectId = getCookie("project");
		}

		if (StringKit.notBlank(projectId)) {
			project = Project.dao.findById(projectId);
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

		Page<Task> page = Task.dao.paginate(PaginationUtil.getPageNumber(this),
				project.getId());

		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));

		// 保存cookie
		if (getCookie("project") == null) {
			setCookie("project", project.getId(), 60 * 60 * 24 * 15);
		}

		render("index.ftl");

	}

	public void showTaskDetail() {
		Task task = Task.dao.findById(getPara());
		setAttr("task", task);
		render("showTaskDetail.ftl");

	}

	public void showCreateTask() {
		Project project = Project.dao.findById(getPara());
		setAttr("project", project);
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

		task.set("create_time", new Date());
		String taskId = UUID.randomUUID().toString();
		task.set("id", taskId);
		task.set("status", Task.STATUS_CREATED);

		if (!task.save()) {
			throw new IllegalStateException("保存任务失败");
		}
		// 保存标签
		String[] tags = getParaValues("tag");
		if (tags != null && tags.length > 0) {
			String sql = "insert into tag(id,name,task_id)values(?,?,?)";
			for (String tag : tags) {
				if (Db.update(sql, UUID.randomUUID().toString(), tag, taskId) != 1) {
					throw new IllegalStateException("保存标签失败");
				}
			}
		}
		setAttr("ok", true);
		renderJson();
	}

	public void showUpdateTask() {
		Task task = Task.dao.findById(getPara());
		setAttr("task", task);
		renderJson("showUpdateTask.ftl");
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

		taskModel.keep("id", "title", "detail");
		if (!taskModel.update()) {
			throw new IllegalStateException("更新任务失败");
		}
		// 更新标签，为了逻辑简单，直接删除旧的，保存新的
		String[] tags = getParaValues("tag");
		// 防止空指针，如果是没有传值就拿空集合去比较
		List<String> newTags = new ArrayList<>();
		if (tags != null && tags.length > 0) {
			newTags = Arrays.asList(tags);
		}
		List<String> oldTags = oldTask.getTags();
		// 相同就不作任何操作了
		if (newTags.equals(oldTags)) {
			setAttr("ok", true);
			renderJson();
			return;
		}
		// 删除旧的，保存新的
		String saveSql = "insert into tag(id,name.task_id)values(?,?,?)";
		String deleteSql = "delete from tag where task_id=?";
		if (oldTags != null && !oldTags.isEmpty()) {
			if (Db.update(deleteSql, oldTask.getStr("id")) <= 0) {
				throw new IllegalStateException("保存标签失败");
			}
		}
		if (!newTags.isEmpty()) {
			for (String t : newTags) {
				if (Db.update(saveSql, UUID.randomUUID().toString(), t,
						oldTask.getStr("id")) != 1) {
					throw new IllegalStateException("保存标签失败");
				}
			}
		}

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
		}
		task.set("finish_time", new Date());
		task.set("status", Task.STATUS_FINISHED);
		task.keep("id", "assign_user_id", "finish_time");
		task.update();
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

		Date finishTime = task.getDate("finish_time");
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
		task.delete();
		setAttr("ok", true);
		renderJson();
	}

}
