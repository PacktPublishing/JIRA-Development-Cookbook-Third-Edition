package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;

public class JTricksOptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String ISSUE_KEY = "DEMO-1";
	private static final String CF_NAME = "Test Select";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		OptionsManager optionsManager = ComponentAccessor.getOptionsManager();
		
		Issue issue = issueManager.getIssueObject(ISSUE_KEY);

		writer.println("Adding new options...<br><br>");
		
		CustomField customField = customFieldManager.getCustomFieldObjectByName(CF_NAME);
		
		/*
		//Use this if there is no issue object
		
		FieldConfigSchemeManager fieldConfigSchemeManager = ComponentAccessor.getComponent(FieldConfigSchemeManager.class);
		List<FieldConfigScheme> schemes = fieldConfigSchemeManager.getConfigSchemesForField(customField);
		
		//Assuming only one config scheme
		FieldConfig config = schemes.get(0).getOneAndOnlyConfig();*/
		
		FieldConfig config = customField.getRelevantConfig(issue);
		Options options = optionsManager.getOptions(config);
		List<Option> existingOptions = options.getRootOptions();
		writer.println("Existing options...<br>");
		for (Option option : existingOptions) {
			writer.println(option.toString()+"<br>");
		}
		writer.println("<br>Adding new option<br><br>");
		Option option = optionsManager.createOption(config, null, 100L, "new option");
		options.add(option);
		optionsManager.updateOptions(options);
		writer.println("Done<br><br>");
	}

}