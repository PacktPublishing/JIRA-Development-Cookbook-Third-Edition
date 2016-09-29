package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.jira.issue.history.ChangeLogUtils;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;

public class ChangeLogManagerServlet extends HttpServlet {

	private IssueService issueService;
	private JiraAuthenticationContext authenticationContext;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		issueService = ComponentAccessor.getIssueService();
		authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();

		out.println("Creating Issue...");
		out.flush();

		ApplicationUser user = authenticationContext.getLoggedInUser();

		MutableIssue issue = createIssue(out, user);

		out.println("Creating dummy summary change history");
		addChangeHistoryForSummary(issue, user);

		out.println("Creating dummy status change history for yesterday!");
		addChangeHistoryForStatus(issue, user);

		out.println("Creating Custom change histroy");
		addCustomChangeHistory(issue, user);

		out.println("\n... And we are done!");
	}

	private void addCustomChangeHistory(MutableIssue issue, ApplicationUser user) {
		ChangeItemBean changeBean = new ChangeItemBean(ChangeItemBean.CUSTOM_FIELD, "Some Heading", "Some Old Value",
				"Some New Value");

		createChangeGroup(issue, user, changeBean);
	}

	private void addChangeHistoryForStatus(MutableIssue issue, ApplicationUser user) {
		ChangeItemBean changeBean = new ChangeItemBean(ChangeItemBean.STATIC_FIELD, IssueFieldConstants.STATUS, "10000",
				"Open", "3", "In Progress");

		createChangeGroup(issue, user, changeBean);
	}

	private void addChangeHistoryForSummary(MutableIssue issue, ApplicationUser user) {

		ChangeItemBean changeBean = new ChangeItemBean(ChangeItemBean.STATIC_FIELD, IssueFieldConstants.SUMMARY,
				"Old Summary", "New Summary");

		createChangeGroup(issue, user, changeBean);
	}

	private void createChangeGroup(MutableIssue issue, ApplicationUser user, ChangeItemBean changeBean) {
		IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();
		changeHolder.addChangeItem(changeBean);

		// create and store the changelog for this whole process
		GenericValue updateLog = ChangeLogUtils.createChangeGroup(user, issue.getGenericValue(),
				issue.getGenericValue(), changeHolder.getChangeItems(), false);
	}

	private MutableIssue createIssue(PrintWriter out, ApplicationUser user) {
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setProjectId(10000L).setIssueTypeId("10000").setSummary("Test Summary")
				.setReporterId("admin").setAssigneeId("admin").setDescription("Test Description").setStatusId("10000")
				.setPriorityId("2").setFixVersionIds(10000L);

		CreateValidationResult createValidationResult = issueService.validateCreate(user, issueInputParameters);

		MutableIssue issue = null;

		if (createValidationResult.isValid()) {
			IssueResult createResult = issueService.create(user, createValidationResult);
			if (createResult.isValid()) {
				issue = createResult.getIssue();
				out.println("Created " + issue.getKey());
				out.flush();
			} else {
				Collection<String> errorMessages = createResult.getErrorCollection().getErrorMessages();
				for (String errorMessage : errorMessages) {
					out.println(errorMessage);
				}
				out.flush();
			}
		} else {
			Collection<String> errorMessages = createValidationResult.getErrorCollection().getErrorMessages();
			for (String errorMessage : errorMessages) {
				out.println(errorMessage);
			}
			Map<String, String> errors = createValidationResult.getErrorCollection().getErrors();
			Set<String> errorKeys = errors.keySet();
			for (String errorKey : errorKeys) {
				out.println(errors.get(errorKey));
			}
			out.flush();
		}

		return issue;
	}
}
