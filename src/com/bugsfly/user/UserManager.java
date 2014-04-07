package com.bugsfly.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.bugsfly.common.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.project.ProjectManager;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class UserManager {
	/**
	 * 根据帐号获取用户
	 * 
	 * @param account
	 * @return
	 */
	public Record getUserByAccount(String account) {

		Record user = Db.findFirst(
				"select * from user where mobile=? or email=? ", account,
				account);
		if (user == null) {
			return null;
		}
		boolean sysAdmin = Db.findFirst(
				"select * from sys_admin where admin_id=?", user.getStr("id")) != null;
		user.set("sysAdmin", sysAdmin);
		return user;
	}

	public static Record getUser(String id) {
		return Db.findById("user", id);
	}

	/**
	 * 更新登录时间
	 * 
	 * @param id
	 */
	public void updateLoginTime(String id) {
		String sql = "update user set login_time=? where id=?";
		Db.update(sql, new Date(), id);
	}

	/**
	 * 保存用户
	 * 
	 * @param controller
	 * @throws BusinessException
	 */
	public static void saveUser(UserController controller)
			throws BusinessException {
		Record currentUser = controller.getSessionAttr(Webkeys.SESSION_USER);
		final String projectId = controller.getPara("projectId");
		// 如果是为项目添加用户，必须得是项目管理员或者系统管理员
		// 如果是直接添加用户，必须是系统管理员
		if (!currentUser.getBoolean("sysAdmin")) {
			if (projectId == null) {
				throw new BusinessException("权限不足，无法完成操作");
			}
			String userRole = ProjectManager.getRole(projectId,
					currentUser.getStr("id"));
			if (!ProjectManager.ROLE_ADMIN.equals(userRole)) {
				throw new BusinessException("权限不足，无法完成操作");
			}

		}

		final Record user = new Record();
		user.set("zh_name", controller.getPara("zhName"));
		user.set("en_name", controller.getPara("enName"));
		user.set("email", controller.getPara("email"));
		String mobile = controller.getPara("mobile");
		user.set("mobile", mobile);

		String pwd = mobile.substring(mobile.length() - 6);
		String salt = UUID.randomUUID().toString();

		String md5 = DigestUtils.md5Hex(pwd + salt);

		user.set("salt", salt);
		user.set("md5", md5);
		user.set("create_time", new Date());
		user.set("id", UUID.randomUUID().toString());

		final Record project_user = new Record();
		if (projectId != null) {
			String role = controller.getPara("role");
			if (!ProjectManager.ROLE_ADMIN.equals(role)
					&& !ProjectManager.ROLE_DEVELOPER.equals(role)
					&& !ProjectManager.ROLE_TESTER.equals(role)) {
				throw new BusinessException("未知的角色");
			}
			project_user.set("project_id", projectId);
			project_user.set("user_id", user.get("id"));
			project_user.set("role", role);

		}

		boolean ok = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				if (!Db.save("user", user)) {
					return false;
				}
				if (projectId != null && !Db.save("project_user", project_user)) {
					return false;
				}

				return true;
			}
		});
		if (!ok) {
			throw new BusinessException("保存失败");
		}

	}

	public static void toggleStatus(UserController controller)
			throws BusinessException {
		String userId = controller.getPara("userId");
		Record user = getUser(userId);
		if (user == null) {
			throw new BusinessException("找不到相关的用户 ");
		}

		boolean disabled = user.getBoolean("disabled");
		user.set("disabled", !disabled);

		if (!Db.update("user", user)) {
			throw new BusinessException("保存失败");
		}

	}

	public static Page<Record> getUserPage(UserController controller,
			String projectId) {
		String key = controller.getPara("key");
		List<String> params = new ArrayList<String>();
		StringBuilder sql = new StringBuilder();
		sql.append(" from user u ");

		if (projectId != null) {
			sql.append(" left join project_user pu ");
			sql.append(" on pu.user_id=u.id ");
		}

		sql.append(" where 1=1 ");

		if (projectId != null) {
			sql.append(" and pu.project_id=? ");
			params.add(projectId);
		}

		if (StringKit.notBlank(key)) {
			sql.append(" and (zh_name like ? or en_name like ? ");
			sql.append(" or email like ? or mobile like ? ) ");
			params.add("%" + key + "%");
			params.add("%" + key + "%");
			params.add("%" + key + "%");
			params.add("%" + key + "%");
		}
		sql.append(" order by u.create_time desc ");

		String selectSql = "select u.* ";
		if (projectId != null) {
			selectSql = "select u.*,pu.role";
		}

		Page<Record> page = Db.paginate(
				PaginationUtil.getPageNumber(controller), 10, selectSql,
				sql.toString(), params.toArray());
		return page;
	}

	public static void addUsersToProject(UserController controller)
			throws BusinessException {
		final String projectId = controller.getPara("projectId");
		final String[] userIds = controller.getParaValues("userId");
		Record project = ProjectManager.getProject(projectId);
		if (project == null) {
			throw new BusinessException("相关项目不存在");
		}
		if (userIds == null || userIds.length == 0) {
			throw new BusinessException("未选择要添加的用户");
		}
		final String role = controller.getPara("role");
		if (!ProjectManager.ROLE_ADMIN.equals(role)
				&& !ProjectManager.ROLE_DEVELOPER.equals(role)
				&& !ProjectManager.ROLE_TESTER.equals(role)) {
			throw new BusinessException("未知的角色");
		}
		boolean ok = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				for (String userId : userIds) {
					Record project_user = new Record();
					project_user.set("project_id", projectId);
					project_user.set("user_id", userId);
					project_user.set("role", role);
					if (!Db.save("project_user", project_user)) {
						return false;
					}
				}
				return true;
			}
		});
		if (!ok) {
			throw new BusinessException("保存失败");
		}
	}
}
