package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.PropertiesManager;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

public class PropertySetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();

		out.println("Creating boolean property..");

		PropertySet propertySet = ComponentAccessor.getComponent(PropertiesManager.class).getPropertySet();
		propertySet.setBoolean("jtricks.custom.key1", new Boolean(true));
		out.println("Set property:" + (propertySet.getBoolean("jtricks.custom.key1") ? "True" : "False"));
		
		out.println("Creating user address property...");
		Map<String, Object> entityDetails = new HashMap<String, Object>();
		entityDetails.put("delegator.name", "default");
		entityDetails.put("entityName", "User");
		entityDetails.put("entityId", new Long(10000));
		PropertySet userProperties = PropertySetManager.getInstance("ofbiz", entityDetails);

		userProperties.setString("state", "Kerala");
		userProperties.setString("country", "India");

		System.out.println("Set Address:" + userProperties.getString("state") + ", "
				+ userProperties.getString("country"));

		out.println("\n... And we are done!");
	}
}
