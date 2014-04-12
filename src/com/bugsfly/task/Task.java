package com.bugsfly.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bugsfly.project.Project;
import com.bugsfly.user.User;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 任务
 */
public class Task extends Model<Task> {

	private static final long serialVersionUID = -6028747546313596103L;

	public static final Task dao = new Task();

	public static final Set<String> tags = new HashSet<String>();
	public static final String STATUS_CREATED = "created";// 新建
	public static final String STATUS_ASSIGNED = "assigned";// 已分派
	public static final String STATUS_SOLVED = "solved";// 已解决
	public static final String STATUS_REWORKED = "reworked";// 已打回
	public static final String STATUS_CLOSED = "closed";// 已关闭
	static {
		tags.add("错误");
		tags.add("优化");
		tags.add("改善");
		tags.add("新功能");
	}

	public static boolean checkStatus(String status) {
		return STATUS_CREATED.equals(status) || STATUS_ASSIGNED.equals(status)
				|| STATUS_SOLVED.equals(status)
				|| STATUS_REWORKED.equals(status)
				|| STATUS_CLOSED.equals(status);
	}

	public List<Record> getTags() {
		return Db.find("select * from tag where task_id=?", this.getStr("id"));
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
