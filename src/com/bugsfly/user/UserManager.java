package com.bugsfly.user;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.bugsfly.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.team.TeamManager;
import com.bugsfly.util.RegExpUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
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
		boolean isAdmin = Db.findFirst(
				"select * from sys_admin where admin_id=?", user.getStr("id")) != null;
		user.set("isAdmin", isAdmin);
		return user;
	}

	public Record getUser(String id) {
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

	public void addUserToTeam(UserController controller)
			throws BusinessException {
		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);
		String teamId = controller.getPara();
		TeamManager teamManager = new TeamManager();

		Record team = teamManager.getTeam(teamId);
		if (team == null) {
			throw new BusinessException("不存在的团队");
		}

		// 如果不是管理员，要判断角色
		if (!user.getBoolean("isAdmin")) {
			String role = teamManager.getRoleOfUser(teamId, user.getStr("id"));
			if (!TeamManager.ROLE_ADMIN.equals(role)) {
				throw new BusinessException("抱歉，您滑 权限进行此操作");
			}
		}
		controller.setAttr("team", team);
	}

	/**
	 * 保存用户 到团队
	 * 
	 * @param controller
	 * @throws BusinessException
	 */
	public void saveUserToTeam(UserController controller)
			throws BusinessException {
		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);
		final String teamId = controller.getPara("teamId");
		String zhName = controller.getPara("zhName");
		String enName = controller.getPara("enName");
		String email = controller.getPara("email");
		String mobile = controller.getPara("mobile");

		TeamManager teamManager = new TeamManager();
		Record team = teamManager.getTeam(teamId);
		if (team == null) {
			throw new BusinessException("找不到相关的团队");
		}

		String userRole = teamManager.getRoleOfUser(teamId, user.getStr("id"));
		if (!TeamManager.ROLE_ADMIN.equals(userRole)
				&& !user.getBoolean("isAdmin")) {
			throw new BusinessException("抱歉，您无权限进行此操作");
		}

		if (zhName == null || !zhName.matches("^[\u4e00-\u9fa5]{2,5}$")) {
			throw new BusinessException("不正确的中文名");
		}

		if (enName == null || !enName.matches("^[a-zA-Z]{2,20}$")) {
			throw new BusinessException("不正确的英文名");
		}

		if (!RegExpUtil.checkMail(email)) {
			throw new BusinessException("邮箱填写不正确");
		}

		if (!RegExpUtil.checkMobile(mobile)) {
			throw new BusinessException("手机号填写不正确");
		}

		final Record newUser = new Record();
		newUser.set("id", UUID.randomUUID().toString().replace("-", ""));
		newUser.set("zh_name", zhName);
		newUser.set("en_name", enName);
		newUser.set("email", email);
		newUser.set("mobile", mobile);
		// 初始密码为手机号后六位
		// 生成盐值
		String salt = UUID.randomUUID().toString().replace("-", "");
		newUser.set("salt", salt);
		String pwd = mobile.substring(mobile.length() - 6);
		newUser.set("md5", DigestUtils.md5Hex(pwd + salt));
		// 创建日期
		newUser.set("create_time", new Date());

		boolean succeed = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				UserManager manager = new UserManager();
				if (manager.isEmailExist(newUser.getStr("email"))) {
					return false;
				}
				if (manager.isMobileExist(newUser.getStr("mobile"))) {
					return false;
				}
				Record team_user = new Record();
				team_user.set("team_id", teamId);
				team_user.set("user_id", newUser.getStr("id"));
				System.err.println("user_id:" + newUser.getStr("id"));
				team_user.set("role", TeamManager.ROLE_ORDINARY);
				boolean count1 = Db.save("user", newUser);
				boolean count2 = Db.save("team_user", team_user);
				return count1 && count2;
			}
		});

		if (!succeed) {
			throw new BusinessException("添加成员失败");

		}

	}

	public boolean isEmailExist(String email) {
		return Db.findFirst("select 1 from user where email=?", email) != null;
	}

	public boolean isMobileExist(String mobile) {
		return Db.findFirst("select 1 from user where mobile=?", mobile) != null;
	}

	/**
	 * 将用户设置为团队成员
	 * 
	 * @param controller
	 * @throws BusinessException
	 */
	public void setCurrentUserToTeam(UserController controller)
			throws BusinessException {
		String teamId = controller.getPara("teamId");
		String userId = controller.getPara("userId");
		TeamManager teamManager = new TeamManager();
		Record team = teamManager.getTeam(teamId);
		if (team == null) {
			throw new BusinessException("找不到相关的团队");
		}

		Record addUser = getUser(userId);
		if (addUser == null) {
			throw new BusinessException("要添加的用户不存在");
		}

		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);
		String teamRole = teamManager.getRoleOfUser(teamId, user.getStr("id"));
		if (!TeamManager.ROLE_ADMIN.equals(teamRole)
				&& !user.getBoolean("isAdmin")) {
			throw new BusinessException("抱歉，您无权限进行此操作");
		}

		Record team_user = Db.findFirst(
				"select * from team_user where team_id=? and user_id=?",
				teamId, userId);
		if (team_user != null) {
			throw new BusinessException("该用户已经是团队成员了");
		}

		team_user = new Record();
		team_user.set("team_id", teamId);
		team_user.set("user_id", userId);
		team_user.set("role", TeamManager.ROLE_ORDINARY);

		if (!Db.save("team_user", team_user)) {
			throw new BusinessException("保存失败");
		}

	}
}
