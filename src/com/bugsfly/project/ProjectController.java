package com.bugsfly.project;

import java.util.ArrayList;
import java.util.List;

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

public class ProjectController extends Controller {

	@Before(SysAdminInterceptor.class)
	public void allProjects() {

		StringBuilder sql = new StringBuilder();
		List<String> params = new ArrayList<String>();
		String name = getPara("name");

		sql.append(" from project p ");
		// 人数子查询
		sql.append(" left join ( ");
		sql.append(" select count(*) u_count,project_id ");
		sql.append("  from project_user ");
		sql.append(" group by project_id ");
		sql.append(" ) pu on pu.project_id=p.id  ");

		if (StringKit.notBlank(name)) {
			sql.append(" where p.name like ? ");
			params.add("%" + name + "%");
		}

		Page<Record> page = Db.paginate(PaginationUtil.getPageNumber(this), 10,
				"select p.*,pu.u_count", sql.toString(), params.toArray());

		setAttr("list", page.getList());
		setAttr("pageLink",
				PaginationUtil.generatePaginateHTML(getRequest(), page));
		keepPara();
		render("allProjects.ftl");
	}

	public void myProjects() {

	}

	/**
	 * 保存项目
	 */
	@Before(SysAdminJSONInterceptor.class)
	public void saveProject() {
		try {
			ProjectManager.saveProject(this);
			setAttr("ok", true);
		} catch (BusinessException e) {
			setAttr("msg", e.getMessage());
		}
		renderJson();
	}
}
