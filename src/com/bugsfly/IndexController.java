package com.bugsfly;

import com.jfinal.core.Controller;

public class IndexController extends Controller{
	public void index() {
		render("index.ftl");

	}
}
