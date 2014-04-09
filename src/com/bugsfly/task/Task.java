package com.bugsfly.task;

import com.jfinal.plugin.activerecord.Model;
/**
 * 任务
 */
public class Task extends Model<Task> {

	private static final long serialVersionUID = -6028747546313596103L;

	public static final Task dao = new Task();
}
