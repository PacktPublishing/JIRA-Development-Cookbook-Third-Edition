package com.jtricks;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.UserRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.api.domain.Visibility.Type;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class CommentClient {

	public static void main(String[] args) throws URISyntaxException, InterruptedException, ExecutionException {

		// Initialize REST Client
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final URI uri = new URI("http://localhost:8080/jira");
		final JiraRestClient jiraRestClient = factory.createWithBasicHttpAuthentication(uri, "jobinkk", "*****");

		// Get specific client instances
		IssueRestClient issueClient = jiraRestClient.getIssueClient();
		UserRestClient userClient = jiraRestClient.getUserClient();

		// Retrieve an issue
		final Promise<Issue> issue = issueClient.getIssue("DEMO-1");
		Issue browsedIssue = issue.get();
		User jobin = userClient.getUser("jobinkk").get();
		
		System.out.println("Got issue:"+browsedIssue.getKey());

		Comment comment = new Comment(null, "Test Comment", jobin, null, new DateTime(), null,
				new Visibility(Type.GROUP, "jira-developers"), null);
		issueClient.addComment(browsedIssue.getCommentsUri(), comment).claim();
		
		issueClient.addComment(browsedIssue.getCommentsUri(), Comment.valueOf("Simple Comment")).claim();

	}

}
