package com.bugsfly.config;

import java.io.File;
import java.util.Properties;

import com.bugsfly.IndexController;
import com.bugsfly.login.LoginController;
import com.bugsfly.login.LoginInterceptor;
import com.bugsfly.um.UMController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.FreeMarkerRender;

import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;

public class BugsFlyConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants constants) {
		// 开发者模式，正式部署时关闭
		constants.setDevMode(true);
		constants.setBaseViewPath("/WEB-INF/freemarker");
		// 设置常用错误页面
		constants.setError401View("/WEB-INF/freemarker/errorPage/401.ftl");
		constants.setError403View("/WEB-INF/freemarker/errorPage/403.ftl");
		constants.setError404View("/WEB-INF/freemarker/errorPage/404.ftl");
		constants.setError500View("/WEB-INF/freemarker/errorPage/500.ftl");
		// 设置下载目录
		constants.setUploadedFileSaveDirectory(PathKit.getWebRootPath()
				+ File.separator + "upload");
		// 设置默认下载文件大小为5m
		constants.setMaxPostSize(1024 * 1024 * 5);
		Configuration freeMarkerConfig = FreeMarkerRender.getConfiguration();
		try {
			// 将contentPath设置为freemarker共享变量
			freeMarkerConfig.setSharedVariable("ctx", JFinal.me()
					.getServletContext().getContextPath());
		} catch (TemplateModelException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void configHandler(Handlers handlers) {

	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
		interceptors.add(new LoginInterceptor());
	}

	@Override
	public void configPlugin(Plugins plugins) {
		// 加载数据库和连接池相关的配置文件
		// 使用c3p0插件
		Properties properties = loadPropertyFile("config" + File.separator
				+ "c3p0.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(properties);
		plugins.add(c3p0Plugin);
		ActiveRecordPlugin aPlugin = new ActiveRecordPlugin(c3p0Plugin);
		aPlugin.setDialect(new MysqlDialect());
		plugins.add(aPlugin);
	}

	@Override
	public void configRoute(Routes routes) {
		routes.add("/", IndexController.class);
		routes.add("/um", UMController.class);
		routes.add("/login", LoginController.class, "/login");

	}

}
