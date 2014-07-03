package com.bugsfly.project;

import java.util.ArrayList;
import java.util.List;

import com.bugsfly.user.User;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class Project extends Model<Project> {

	private static final long serialVersionUID = -7046480670173881844L;

	public static final Project dao = new Project();

	public final static String ROLE_ADMIN = "ADMIN";
	public final static String ROLE_DEVELOPER = "DEVELOPER";
	public final static String ROLE_TESTER = "TESTER";

	/**
	 * 检查角色是否正确
	 * 
	 * @param role
	 */
	public static boolean checkRole(String role) {
		return ROLE_ADMIN.equals(role) || ROLE_DEVELOPER.equals(role)
				|| ROLE_TESTER.equals(role);
	}

	public List<User> getUsers() {
		String sql = "select u.*,pu.role ";
		sql += " from user u ";
		sql += " left join project_user pu on pu.user_id=u.id ";
		sql += " where pu.project_id=? ";
		return User.dao.find(sql, this.getStr("id"));
	}

	/**
	 * 获取项目开发人员（包括管理员）
	 * 
	 * @return
	 */
	public List<User> getDevelopers() {
		String sql = "select u.*,pu.role ";
		sql += " from user u ";
		sql += " left join project_user pu on pu.user_id=u.id ";
		sql += " where pu.project_id=? and ( pu.role=? or pu.role=? ) ";
		return User.dao
				.find(sql, this.getStr("id"), ROLE_ADMIN, ROLE_DEVELOPER);
	}

	public String getRoleOfUser(String userId) {
		return Db
				.queryStr(
						"select role from project_user where project_id=? and user_id=?",
						getId(), userId);
	}

	public String getId() {
		return this.getStr("id");
	}

	/**
	 * 获取用户数量
	 * 
	 * @return
	 */
	public long getUserCount() {
		return Db
				.queryLong(
						"select count(*) from project_user where project_id=?",
						getId());
	}

	public long getTaskCount() {
		return Db.queryLong("select count(*) from task where project_id=?",
				getId());
	}

	public Page<Project> paginate(int pn, String name) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" from project p ");
		if (StringKit.notBlank(name)) {
			sql.append(" where name like ? ");
			params.add("%" + name + "%");
		}
		sql.append(" order by create_time desc ");

		return this.paginate(pn, 10, "select p.*", sql.toString(),
				params.toArray());
	}

	/**
	 * 分页查询项目的成员用户
	 * 
	 * @param pn
	 * @param searchKey
	 * @return
	 */
	public Page<User> paginateUser(int pn, String searchKey) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();

		sql.append(" from user u ");
		sql.append(" left join project_user pu on pu.user_id=u.id ");
		sql.append(" where pu.project_id=? ");
		params.add(getId());
		if (StringKit.notBlank(searchKey)) {
			sql.append(" and (u.zh_name like ? or u.en_name like ? ");
			sql.append(" or u.email like ? or u.mobile like ? ) ");
			params.add("%" + searchKey + "%");
			params.add("%" + searchKey + "%");
			params.add("%" + searchKey + "%");
			params.add("%" + searchKey + "%");
		}
		sql.append(" order by u.zh_name ");
		return User.dao.paginate(pn, 10, "select u.*,pu.role", sql.toString(),
				params.toArray());
	}

	/**
	 * 保存用户
	 * 
	 * @return
	 */
	public boolean saveUser(String userId, String role) {
		Record pu = new Record();
		pu.set("provate_id", getStr("id"));
		pu.set("user_id", userId);
		pu.set("role", role);
		return Db.update("project_user", pu);
	}
}
