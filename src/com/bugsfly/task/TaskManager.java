package com.bugsfly.task;

import java.util.HashSet;
import java.util.Set;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class TaskManager {
	public static final Set<String> tags = new HashSet<String>();

	static {
		tags.add("错误");
		tags.add("优化");
		tags.add("改善");
		tags.add("新功能");
	}

	public static final String STATUS_CREATED = "created";// 新建
	public static final String STATUS_ASSIGNED = "assigned";// 已分派
	public static final String STATUS_SOLVED = "solved";// 已解决
	public static final String STATUS_REWORKED = "reworked";// 已打回
	public static final String STATUS_CLOSED = "closed";// 已关闭

	/**
	 * 任务列表
	 * 
	 * @param controller
	 * @return
	 */
	public Page<Record> getTaskListPage(TaskController controller) {
		
		return null;
	}

}
