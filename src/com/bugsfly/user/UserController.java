package com.bugsfly.user;

import com.bugsfly.Webkeys;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

public class UserController extends Controller {
	
	public void index() {
		render("index.ftl");
	}
	
	public void adminMenu(){
		Record user = (Record) getSession().getAttribute(Webkeys.SESSION_USER);
		if(!user.getBoolean("admin")){
			setAttr("message", "您无权访问");
			renderError(403);
			return;
		}
		render("admin_menu.ftl");
	}
}
