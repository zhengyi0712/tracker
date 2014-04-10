package com.bugsfly.task;

import java.util.List;

import com.bugsfly.project.Project;
import com.bugsfly.user.User;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/**
 * 任务
 */
public class Task extends Model<Task> {

	private static final long serialVersionUID = -6028747546313596103L;

	public static final Task dao = new Task();

	public List<Record> getTags() {
		return Db.find("select * from tag where task_id=?", this.getStr("id"));
	}
	
	public Project getProject(){
		return Project.dao.findById(this.getStr("project_id"));
	}
	
	public User getAssignUser(){
		return User.dao.findById(this.getStr("assign_user_id"));
	}
}
