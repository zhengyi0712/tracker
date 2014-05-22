package com.bugsfly.task;

import com.jfinal.plugin.activerecord.Model;

/**
 * 任务日志
 */
public class TaskLog extends Model<TaskLog> {
	
	private static final long serialVersionUID = -7700429068379611167L;
	
	public static TaskLog dao = new TaskLog();
}
