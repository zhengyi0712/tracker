package com.bugsfly.task;

import java.util.ArrayList;
import java.util.List;

import com.bugsfly.project.Project;
import com.bugsfly.user.User;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 任务
 */
public class Task extends Model<Task> {

	private static final long serialVersionUID = -6028747546313596103L;

	public static final Task dao = new Task();

	public static final String STATUS_CREATED = "CREATED";// 新建
	public static final String STATUS_ASSIGNED = "ASSIGNED";// 已分派
	public static final String STATUS_FINISHED = "FINISHED";// 已解决
	public static final String STATUS_REWORKED = "REWORKED";// 已打回
	public static final String STATUS_CLOSED = "CLOSED";// 已关闭

	public List<Tag> getTags() {
		String sql = "select tag.* from task_tag tt ";
		sql += " left join tag on tag.id=tt.tag_id ";
		sql += " where tt.task_id=? ";
		return Tag.dao.find(sql, getStr("id"));

	}

	public Project getProject() {
		return Project.dao.findById(this.getStr("project_id"));
	}

	public User getCreateUser() {
		return User.dao.findById(this.getStr("create_user_id"));
	}

	public User getAssignUser() {
		return User.dao.findById(this.getStr("assign_user_id"));
	}

	public User getUpdateUser() {
		return User.dao.findById(this.getStr("update_user_id"));
	}

	/**
	 * 分页查询任务
	 * 
	 * @param pn
	 * @param projectId
	 * @return
	 */
	public Page<Task> paginate(int pn, String projectId, String title,
			String[] tagIdArr, String[] statusArr, String[] assignUserIdArr) {
		StringBuilder sqlExceptSelect = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		sqlExceptSelect.append(" from (  ");
		
		sqlExceptSelect.append(" select distinct t.* ");
		sqlExceptSelect.append(" ,case when t.finish_time is null then 0 else 1 end finished ");
		sqlExceptSelect.append(" from task t ");
		sqlExceptSelect.append(" left join task_tag tt on tt.task_id=t.id ");
		sqlExceptSelect.append(" where project_id=? ");
		params.add(projectId);
		// 标题查询
		if (StringKit.notBlank(title)) {
			sqlExceptSelect.append(" and t.title like ? ");
			params.add("%" + title + "%");
		}

		// 标签查询
		if (tagIdArr != null && tagIdArr.length > 0) {
			sqlExceptSelect.append(" and tt.tag_id in ( ");
			for (int i = 0; i < tagIdArr.length; i++) {
				if (i != tagIdArr.length - 1) {
					sqlExceptSelect.append(" ?, ");
				} else {
					sqlExceptSelect.append(" ? ");
				}
				params.add(tagIdArr[i]);
			}
			sqlExceptSelect.append(" ) ");
		}
		// 状态查询条件
		if (statusArr != null && statusArr.length > 0) {
			sqlExceptSelect.append(" and t.status in ( ");
			for (int i = 0; i < statusArr.length; i++) {
				if (i != statusArr.length - 1) {
					sqlExceptSelect.append(" ?, ");
				} else {
					sqlExceptSelect.append(" ? ");
				}
				params.add(statusArr[i]);
			}
			sqlExceptSelect.append(" ) ");
		}
		// 分派人查询
		if (assignUserIdArr != null && assignUserIdArr.length > 0) {
			sqlExceptSelect.append(" and t.assign_user_id in ( ");
			for (int i = 0; i < assignUserIdArr.length; i++) {
				if (i != assignUserIdArr.length - 1) {
					sqlExceptSelect.append(" ?, ");
				} else {
					sqlExceptSelect.append(" ? ");
				}
				params.add(assignUserIdArr[i]);
			}
			sqlExceptSelect.append(" ) ");
		}
		sqlExceptSelect.append(" order by finished asc,t.update_time desc ");
		
		sqlExceptSelect.append(" ) s ");

		return paginate(pn, 20, "select s.*",
				sqlExceptSelect.toString(), params.toArray());
	}
}
