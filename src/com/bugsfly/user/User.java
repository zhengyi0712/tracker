package com.bugsfly.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bugsfly.project.Project;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

public class User extends Model<User> {

	private static final long serialVersionUID = 5721481570867001046L;

	public static final User dao = new User();

	public List<Project> getProjects() {
		String sql = "select p.*,pu.role ";
		sql += " from project p ";
		sql += " left join project_user pu on pu.project_id=p.id ";
		sql += " where pu.user_id=? ";
		return Project.dao.find(sql, getId());
	}

	public boolean isSysAdmin() {
		return Db
				.findFirst("select * from sys_admin where admin_id=?", getId()) != null;
	}

	public User getByAccount(String account) {
		return this.findFirst("select * from user where mobile=? or email=? ",
				account, account);
	}

	/**
	 * 更新登录时间，操作只修改数据库，不更改当前对象
	 */
	public void updateLoginTime() {
		Db.update("update user set login_time=? where id=?", new Date(),
				getId());
	}

	public String getId() {
		return this.getStr("id");
	}

	public Page<User> paginate(int pn, String searchKey) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();

		sql.append(" from user u ");
		sql.append(" where 1=1 ");
		if (StringKit.notBlank(searchKey)) {
			sql.append(" and (u.zh_name like ? or u.en_name like ? ");
			sql.append(" or u.email like ? or u.mobile like ? ) ");
			params.add("%" + searchKey + "%");
			params.add("%" + searchKey + "%");
			params.add("%" + searchKey + "%");
			params.add("%" + searchKey + "%");
		}
		sql.append(" order by u.create_time desc ");
		return paginate(pn, 10, "select u.*", sql.toString(), params.toArray());
	}

	/**
	 * 分页查询用户的项目
	 * 
	 * @param pn
	 * @return
	 */
	public Page<Project> paginateProject(int pn, String name) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" from project p ");
		sql.append(" left join project_user pu on pu.project_id=p.id ");
		sql.append(" where pu.user_id=? ");
		params.add(getId());
		if (StringKit.notBlank(name)) {
			sql.append(" and name like ? ");
			params.add("%" + name + "%");
		}
		sql.append(" order by p.create_time desc ");

		return Project.dao.paginate(pn, 10, "select p.*,pu.role",
				sql.toString(), params.toArray());
	}

	public String toHTML() {
		String title = null;
		if (getStr("en_name") != null) {
			title = "英文名：" + getStr("en_name");
		}
		String html = "<span style='color:#a94442' ";
		if (title != null) {
			html += "title='" + title + "' ";
		}
		html += ">" + getStr("zh_name") + "</span>";
		return html;
	}

}
