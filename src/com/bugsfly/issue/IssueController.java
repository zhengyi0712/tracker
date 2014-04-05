package com.bugsfly.issue;

import com.jfinal.core.Controller;

public class IssueController extends Controller {
	/**
	 * bug列表。<br>
	 * 程序首先会获取用户 参与的项目列表。<br>
	 * 在没有传入项目ID的情况下，程序会从cookie里找到最后一次查看bugs列表的项目。<br>
	 * 如果cookie里没有可用的项目信息，就显示第一个项目的bug列表。
	 */
	public void index() {

		render("index.ftl");

	}
}
