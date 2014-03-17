package com.bugsfly;

import java.io.File;

import org.apache.log4j.Logger;

import com.jfinal.core.Controller;

public class IndexController extends Controller{
	public void index() {
		Logger.getLogger(this.getClass()).info("【测试】");
		render("login"+File.separator+"index.ftl");

	}
}
