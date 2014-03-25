package com.bugsfly.user;

import com.jfinal.core.Controller;

public class UserController extends Controller {
	
	public void index() {
		render("index.ftl");
	}
	
}
