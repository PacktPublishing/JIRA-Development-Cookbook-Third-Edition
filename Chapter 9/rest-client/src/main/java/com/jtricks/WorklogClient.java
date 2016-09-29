package com.jtricks;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.UserRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.api.domain.Visibility.Type;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput.AdjustEstimate;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class WorklogClient {

	public static void main(String[] args) throws URISyntaxException, InterruptedException, ExecutionException {

		// Initialize REST Client
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final URI uri = new URI("http://localhost:8080/jira");
		final JiraRestClient jiraRestClient = factory.createWithBasicHttpAuthentication(uri, "jobinkk", "****");

		// Get specific client instances
		IssueRestClient issueClient = jiraRestClient.getIssueClient();
		UserRestClient userClient = jiraRestClient.getUserClient();

		// Retrieve an issue
		final Promise<Issue> issue = issueClient.getIssue("DEMO-4");
		Issue browsedIssue = issue.get();
		User jobin = userClient.getUser("jobinkk").get();

		// Add worklog
		WorklogInput worklogInput = new WorklogInput(null, browsedIssue.getSelf(), jobin, null, "Some Comment1",
				new DateTime(), 60, null);

		// Add worklog, with visibility set to jira-developers
		// AdjustEstimate will be AUTO
		WorklogInput worklogInputWithVisibility = new WorklogInput(null, browsedIssue.getSelf(), jobin, null,
				"Some Comment2", new DateTime(), 60, new Visibility(Type.GROUP, "jira-developers"));

		// Add worklog without adjusting remaining estimate
		WorklogInput adjustedEstimateLeaveLog = new WorklogInput(null, browsedIssue.getSelf(), jobin, null,
				"Some Comment3", new DateTime(), 60, null, AdjustEstimate.LEAVE, null);

		// Add worklog, with remaining estimate as 4 hours
		WorklogInput adjustedEstimateNewLog = new WorklogInput(null, browsedIssue.getSelf(), jobin, null,
				"Some Comment4", new DateTime(), 60, null, AdjustEstimate.NEW, "240");

		// Add worklog and reduce remaining estimate by 30 minuts
		WorklogInput adjustedEstimateManualLog = new WorklogInput(null, browsedIssue.getSelf(), jobin, null, "Some Comment5",
				new DateTime(), 60, null, AdjustEstimate.MANUAL, "30");
		
		issueClient.addWorklog(browsedIssue.getWorklogUri(),worklogInput).claim();
		issueClient.addWorklog(browsedIssue.getWorklogUri(),worklogInputWithVisibility).claim();
		issueClient.addWorklog(browsedIssue.getWorklogUri(),adjustedEstimateLeaveLog).claim();
		issueClient.addWorklog(browsedIssue.getWorklogUri(),adjustedEstimateNewLog).claim();
		issueClient.addWorklog(browsedIssue.getWorklogUri(),adjustedEstimateManualLog).claim();

	}

}
