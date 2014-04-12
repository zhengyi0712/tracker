package com.bugsfly.task;

import java.util.List;

import com.bugsfly.common.Webkeys;
import com.bugsfly.project.Project;
import com.bugsfly.user.User;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Page;

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
}
