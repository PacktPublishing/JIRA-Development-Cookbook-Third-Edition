package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.bc.issue.IssueService.UpdateValidationResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.user.ApplicationUser;

public class JTricksServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String ISSUE_KEY = "DEMO-1";
	private static final String CF_NAME = "Test Field";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		Issue issue = issueManager.getIssueObject(ISSUE_KEY);

		writer.println("Getting current value...<br><br>");
		CustomField customField = customFieldManager.getCustomFieldObjectByName(CF_NAME);
		String value = (String) issue.getCustomFieldValue(customField);
		writer.println("Current value:" + value + "<br><br>");

		writer.println("Setting new value without update event...<br><br>");
		ModifiedValue modifiedValue = new ModifiedValue(value, "New Value");
		FieldLayoutManager fieldLayoutManager = ComponentAccessor.getFieldLayoutManager();
		FieldLayoutItem fieldLayoutItem = fieldLayoutManager.getFieldLayout(issue).getFieldLayoutItem(customField);
		customField.updateValue(fieldLayoutItem, issue, modifiedValue, new DefaultIssueChangeHolder());
		writer.println("Value Set!<br><br>");
		
		writer.println("Setting new value with update event...<br><br>");
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.addCustomFieldValue(customField.getIdAsLong(), "New Value with event");
		ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		UpdateValidationResult validationResult = issueService.validateUpdate(loggedInUser, issue.getId(),
				issueInputParameters);
		if (validationResult.isValid()) {
			IssueResult result = issueService.update(loggedInUser, validationResult);
			if (result.isValid()) {
				writer.println("New value Set!<br><br>");
			}
		}
	}

}