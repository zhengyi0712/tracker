package com.bugsfly.log4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

public class CoufigurationListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		String filePath = evt.getServletContext().getRealPath("/WEB-INF/config/log4j.properties");
		// 通过properties文件配置log4j
		PropertyConfigurator.configure(filePath);

	}

}
