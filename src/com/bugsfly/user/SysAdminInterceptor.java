package com.bugsfly.user;

import com.bugsfly.common.Webkeys;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
/**
 * 管理员拦截器
 */
public class SysAdminInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController();
		Record user = (Record) controller.getSession().getAttribute(
				Webkeys.SESSION_USER);

		if (!user.getBoolean("sysAdmin")) {
			controller.setAttr(Webkeys.REQUEST_MESSAGE, "抱歉您没有权限进行此操作！");
			controller.render(Webkeys.REQUEST_MESSAGE);
			return;
		}
		ai.invoke();

	}

}
