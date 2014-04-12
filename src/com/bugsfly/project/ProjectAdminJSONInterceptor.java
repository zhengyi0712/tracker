package com.bugsfly.project;

import com.bugsfly.common.Webkeys;
import com.bugsfly.user.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;

public class ProjectAdminJSONInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {

		Controller controller = ai.getController();

		User user = controller.getSessionAttr(Webkeys.SESSION_USER);

		Project project = Project.dao.findById(controller.getPara());

		if (project == null) {
			project = Project.dao.findById(controller.getPara("project.id"));
		}

		if (project == null) {
			controller.setAttr("msg", "相关项目不存在 ");
			controller.renderJson();
			return;
		}

		if (user.isSysAdmin()) {
			ai.invoke();
			return;
		}

		if (!Project.ROLE_ADMIN.equals(project.getRoleOfUser(user.getId()))) {
			controller.setAttr("msg", "权限不足");
			return;
		}
		ai.invoke();

	}

}
