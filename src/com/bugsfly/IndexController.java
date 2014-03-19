package com.bugsfly;

import java.io.File;

import com.jfinal.core.Controller;

public class IndexController extends Controller{
	public void index() {
		render("login"+File.separator+"index.ftl");

	}
}
