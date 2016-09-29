package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.bc.issue.visibility.Visibilities;
import com.atlassian.jira.bc.issue.worklog.WorklogAdjustmentAmountInputParameters;
import com.atlassian.jira.bc.issue.worklog.WorklogAdjustmentAmountResult;
import com.atlassian.jira.bc.issue.worklog.WorklogInputParametersImpl;
import com.atlassian.jira.bc.issue.worklog.WorklogNewEstimateInputParameters;
import com.atlassian.jira.bc.issue.worklog.WorklogNewEstimateResult;
import com.atlassian.jira.bc.issue.worklog.WorklogResult;
import com.atlassian.jira.bc.issue.worklog.WorklogService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;

public class WorklogManagerServlet extends HttpServlet {

	private IssueService issueService;
	private JiraAuthenticationContext authenticationContext;
	private WorklogService worklogService;
	private IssueManager issueManager;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		issueService = ComponentAccessor.getIssueService();
		authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
		worklogService = ComponentAccessor.getComponent(WorklogService.class);
		issueManager = ComponentAccessor.getIssueManager();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();

		out.println("Creating Issue...");
		out.flush();

		ApplicationUser user = authenticationContext.getLoggedInUser();

		// Original estimate = 2h
		MutableIssue issue = createIssue(out, user);
		String key = issue.getKey();

		// Spent an hour and adjust automatically - Original 2h, Spent 1h,
		// Remaining 1h
		out.println("Logging 1h...");
		Worklog worklog = createWorklog(out, getRefreshedIssue(key), "1h", user);
		// Spent 30m, change remaining estimate to 2h - Original 2h, Spent 1h
		// 30m , Remaining 2h
		out.println("Logging 30m and change remaining to 2h...");
		worklog = createWorklogWithNewEstimate(out, getRefreshedIssue(key), "30m", "2h", user);
		// Spent 1h, Retain remaining estimate as 2h - Original 2h, Spent 2h,
		// 30m , Remaining 2h
		out.println("Logging 1h and retain remaining as 2h...");
		worklog = createWorklogRetainingEstimate(out, getRefreshedIssue(key), "1h", user);
		// Spent 1h and reduce remaining estimate by 30m - Original 2h, Spent
		// 3h, 30m , Remaining 1h 30m
		out.println("Logging 1h and reduce remaining by 30m...");
		worklog = createWorklogReduceRemainingEstimate(out, getRefreshedIssue(key), "1h", "30m", user);
		// Change 60m to 90m - Original 2h, Spent 4h , Remaining 1h
		out.println("Modifying last worklog to enter 90m");
		worklog = updateWorklog(getRefreshedIssue(key), user, worklog, "90m");
		// Instead of the auto 90 minutes, let us make it 120 spent & 120
		// minutes remaining! - Original 2h, Spent 4h 30m , Remaining 2h
		out.println("Modifying again to enter 120m with Rem Est 120m");
		worklog = updateWorklogWithNewRemainingEstimate(getRefreshedIssue(key), user, worklog, "120m", "120m");

		out.println("Creating new worklog with 30m and delete it");
		worklog = createWorklog(out, getRefreshedIssue(key), "30m", user);
		deleteWorklog(getRefreshedIssue(key), worklog, user);

		out.println("\n... And we are done!");
	}

	private void deleteWorklog(MutableIssue refreshedIssue, Worklog worklog, ApplicationUser user) {
		JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
		WorklogResult worklogResult = worklogService.validateDelete(jiraServiceContext, worklog.getId());
		worklogService.deleteAndAutoAdjustRemainingEstimate(jiraServiceContext, worklogResult, true);
	}

	public MutableIssue getRefreshedIssue(String key) {
		return issueManager.getIssueObject(key);
	}

	private Worklog createWorklogReduceRemainingEstimate(PrintWriter out, MutableIssue issue, String timeSpent,
			String estimateToReduce, ApplicationUser user) {
		JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
		final WorklogInputParametersImpl.Builder builder = WorklogInputParametersImpl.issue(issue).timeSpent(timeSpent)
				.startDate(new Date()).comment(null).visibility(Visibilities.publicVisibility());
		final WorklogAdjustmentAmountInputParameters params = builder.adjustmentAmount(estimateToReduce)
				.buildAdjustmentAmount();
		WorklogResult result = worklogService.validateCreateWithManuallyAdjustedEstimate(jiraServiceContext, params);
		Worklog worklog = this.worklogService.createWithManuallyAdjustedEstimate(jiraServiceContext,
				(WorklogAdjustmentAmountResult) result, false);
		return worklog;
	}

	private Worklog createWorklogRetainingEstimate(PrintWriter out, MutableIssue issue, String timeSpent,
			ApplicationUser user) {
		JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
		final WorklogInputParametersImpl.Builder builder = WorklogInputParametersImpl.issue(issue).timeSpent(timeSpent)
				.startDate(new Date()).comment(null).visibility(Visibilities.publicVisibility());

		WorklogResult result = this.worklogService.validateCreate(jiraServiceContext, builder.build());
		Worklog worklog = this.worklogService.createAndRetainRemainingEstimate(jiraServiceContext, result, false);
		return worklog;
	}

	private Worklog createWorklogWithNewEstimate(PrintWriter out, MutableIssue issue, String timeSpent,
			String newEstimate, ApplicationUser user) {
		JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
		final WorklogInputParametersImpl.Builder builder = WorklogInputParametersImpl.issue(issue).timeSpent(timeSpent)
				.startDate(new Date()).comment(null).visibility(Visibilities.publicVisibility());

		final WorklogNewEstimateInputParameters params = builder.newEstimate(newEstimate).buildNewEstimate();

		WorklogResult result = this.worklogService.validateCreateWithNewEstimate(jiraServiceContext, params);
		Worklog worklog = this.worklogService.createWithNewRemainingEstimate(jiraServiceContext,
				(WorklogNewEstimateResult) result, false);
		return worklog;
	}

	private Worklog updateWorklogWithNewRemainingEstimate(MutableIssue issue2, ApplicationUser user, Worklog worklog,
			String timeSpent, String newEstimate) {
		JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);

		final WorklogInputParametersImpl.Builder builder = WorklogInputParametersImpl.timeSpent(timeSpent)
				.worklogId(worklog.getId()).startDate(new Date()).comment(null).visibility(Visibilities.publicVisibility());

		final WorklogNewEstimateInputParameters params = builder.newEstimate(newEstimate).buildNewEstimate();

		WorklogResult result = this.worklogService.validateUpdateWithNewEstimate(jiraServiceContext, params);
		Worklog updatedLog = this.worklogService.updateWithNewRemainingEstimate(jiraServiceContext,
				(WorklogNewEstimateResult) result, false);
		return updatedLog;
	}

	private Worklog updateWorklog(MutableIssue issue, ApplicationUser user, Worklog worklog, String timeSpent) {
		JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);

		final WorklogInputParametersImpl.Builder builder = WorklogInputParametersImpl.timeSpent(timeSpent)
				.worklogId(worklog.getId()).startDate(new Date()).comment(null).visibility(Visibilities.publicVisibility());

		WorklogResult result = this.worklogService.validateUpdate(jiraServiceContext, builder.build());
		Worklog updatedLog = this.worklogService.updateAndAutoAdjustRemainingEstimate(jiraServiceContext, result,
				false);
		return updatedLog;
	}

	private Worklog createWorklog(PrintWriter out, MutableIssue issue, String timeSpent, ApplicationUser user) {
		JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
		final WorklogInputParametersImpl.Builder builder = WorklogInputParametersImpl.issue(issue).timeSpent(timeSpent)
				.startDate(new Date()).comment(null).visibility(Visibilities.publicVisibility());

		WorklogResult result = this.worklogService.validateCreate(jiraServiceContext, builder.build());
		Worklog worklog = this.worklogService.createAndAutoAdjustRemainingEstimate(jiraServiceContext, result, false);
		return worklog;
	}

	private MutableIssue createIssue(PrintWriter out, ApplicationUser user) {
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setProjectId(10000L).setIssueTypeId("10000").setSummary("Test Summary")
				.setReporterId("admin").setAssigneeId("admin").setDescription("Test Description").setStatusId("10000")
				.setPriorityId("2").setFixVersionIds(10000L).setOriginalEstimate(7200L);

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
