package com.bugsfly.team;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import com.bugsfly.common.Webkeys;
import com.bugsfly.exception.BusinessException;
import com.bugsfly.user.SysAdminInterceptor;
import com.bugsfly.user.SysAdminJSONInterceptor;
import com.bugsfly.util.PaginationUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class TeamController extends Controller {

	/**
	 * 所有团队
	 */
	@Before(SysAdminInterceptor.class)
	public void allTeams() {

		Page<Record> page = TeamManager.getTeamList(this, null);
		setAttr("page", page);
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		render("allTeams.ftl");
	}

	public void createTeam() {
		render("createTeam.ftl");
	}

	/**
	 * 保存团队
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void saveTeamJson() {

		String name = getPara("name");
		if (StringKit.isBlank(name)) {
			setAttr("msg", "请填写团队名称");
			renderJson();
			return;
		}
		// 团队名称可以是汉字字母数字组成，允许使用空格，首尾空格程序会给去掉
		name = name.trim();
		String regex = "^[a-zA-Z0-9\\s\\u4e00-\\u9fa5]{2,50}$";
		if (!Pattern.matches(regex, name)) {
			setAttr("msg", "团队名称只能由汉字字母和数字组成，2-50字符，中间可以有空格。");
			renderJson();
			return;
		}

		Record team = Db.findFirst("select * from team where name=? ", name);
		if (team != null) {
			setAttr("msg", "团队已经存在，请更换名称");
			renderJson();
			return;
		}

		team = new Record();
		team.set("id", UUID.randomUUID().toString().replace("-", ""));
		team.set("name", name);
		team.set("create_time", new Date());

		if (!Db.save("team", team)) {
			setAttr("msg", "创建团队失败");
			renderJson();
			return;
		}

		setAttr("ok", true);
		renderJson();
	}

	/**
	 * 用户的团队
	 */
	public void myTeams() {
		Record user = (Record) getSession().getAttribute(Webkeys.SESSION_USER);

		Page<Record> page = TeamManager.getTeamList(this, user.getStr("id"));
		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		render("myTeams.ftl");
	}

	/**
	 * 设置用户的角色
	 */
	public void setRole() {
		TeamManager teamManager = new TeamManager();
		try {
			teamManager.setRole(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}

	/**
	 * 踢人
	 */
	public void kickUser() {
		try {
			TeamManager.kickUser(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}

	@Before(SysAdminJSONInterceptor.class)
	public void deleteTeam() {
		String teamId = getPara("teamId");
		try {
			TeamManager.deleteTeam(teamId);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}
}
