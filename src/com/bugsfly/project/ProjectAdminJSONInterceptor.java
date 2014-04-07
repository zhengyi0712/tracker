package com.bugsfly.project;

import com.bugsfly.common.Webkeys;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

public class ProjectAdminJSONInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {

		Controller controller = ai.getController();

		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);

		if (user.getBoolean("sysAdmin")) {
			ai.invoke();
			return;
		}

		String projectId = controller.getPara();
		String role = ProjectManager.getRole(projectId, user.getStr("id"));

		if (role == null) {
			projectId = controller.getPara("projectId");
			role = ProjectManager.getRole(projectId, user.getStr("id"));
		}

		if (role == null) {
			controller.setAttr("msg", "无法确定角色");
			controller.renderJson();
			return;
		}

		if (!ProjectManager.ROLE_ADMIN.equals(role)) {
			controller.setAttr("msg", "权限不足");
			return;
		}
		ai.invoke();

	}

}
