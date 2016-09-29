package com.jtricks.jira.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class JTricksServletListener implements ServletContextListener{

	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("Test Servlet Context is destroyed!");
	}

	public void contextInitialized(ServletContextEvent event) {
		System.out.println("Test Servlet Context is initialized!");
	}

}
