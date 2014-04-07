package com.bugsfly.user;

import com.bugsfly.common.Webkeys;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

public class SysAdminJSONInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController();
		Record user = controller.getSessionAttr(Webkeys.SESSION_USER);

		if (!user.getBoolean("sysAdmin")) {
			controller.setAttr("ok", false);
			controller.setAttr("msg", "权限不足，无法完成操作");
			controller.renderJson();
			return;
		}
		ai.invoke();

	}

}
