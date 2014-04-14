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

	@Before({ TaskValidator.class, Tx.class })
	public void saveTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task task = getModel(Task.class);
		Project project = Project.dao.findById(task.getStr("project_id"));
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

	@Before({ TaskValidator.class, Tx.class })
	public void updateTask() {
		User sessionUser = getSessionAttr(Webkeys.SESSION_USER);
		Task taskModel = getModel(Task.class);
		Task oldTask = Task.dao.findById(taskModel.getStr("id"));
		Project project = Project.dao.findById(oldTask.getStr("project_id"));

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
	 * 分派任务
	 */
	public void assignTask() {

	}

	/**
	 * 完成任务
	 */
	public void finishTask() {

	}

	/**
	 * 返工
	 */
	public void rework() {

	}

}
