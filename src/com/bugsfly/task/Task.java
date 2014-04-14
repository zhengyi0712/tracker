package com.bugsfly.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bugsfly.project.Project;
import com.bugsfly.user.User;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 任务
 */
public class Task extends Model<Task> {

	private static final long serialVersionUID = -6028747546313596103L;

	public static final Task dao = new Task();

	public static final Set<String> TAGS = new HashSet<String>();
	public static final String STATUS_CREATED = "CREATED";// 新建
	public static final String STATUS_ASSIGNED = "ASSIGNED";// 已分派
	public static final String STATUS_FINISHED = "FINISHED";// 已解决
	public static final String STATUS_REWORKED = "REWORKED";// 已打回
	public static final String STATUS_CLOSED = "CLOSED";// 已关闭
	static {
		TAGS.add("错误");
		TAGS.add("优化");
		TAGS.add("改善");
		TAGS.add("新功能");
	}

	public static boolean checkStatus(String status) {
		return STATUS_CREATED.equals(status) || STATUS_ASSIGNED.equals(status)
				|| STATUS_FINISHED.equals(status)
				|| STATUS_REWORKED.equals(status)
				|| STATUS_CLOSED.equals(status);
	}

	public List<String> getTags() {
		return Db.query("select name from tag where task_id=?", this.getStr("id"));
	}
	
	public Project getProject() {
		return Project.dao.findById(this.getStr("project_id"));
	}

	public User getAssignUser() {
		return User.dao.findById(this.getStr("assign_user_id"));
	}

	public Page<Task> paginate(int pn, String projectId) {
		String sqlExceptSelect = " from task where project_id=? ";
		return paginate(pn, 20, "select *", sqlExceptSelect,projectId);
	}
}
