package com.jtricks.jira.webwork;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService;
import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService.RemoteIssueLinkResult;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.link.RemoteIssueLink;
import com.atlassian.jira.issue.link.RemoteIssueLinkBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserUtils;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Named
public class TwitterLinkAction extends JiraWebActionSupport {

	private String key;

	private final IssueManager issueManager;
	private final RemoteIssueLinkService remoteIssueLinkService;

	@Inject
	public TwitterLinkAction(@ComponentImport IssueManager issueManager, @ComponentImport RemoteIssueLinkService remoteIssueLinkService) {
		this.issueManager = issueManager;
		this.remoteIssueLinkService = remoteIssueLinkService;
	}

	@Override
	protected String doExecute() throws Exception {
		Issue issue = this.issueManager.getIssueObject(getKey());
		createRemoteTwitterLink(issue, "jobinkk", UserUtils.getUser("admin"));
		return getRedirect("/browse/" + getKey());
	}

	private void createRemoteTwitterLink(Issue issue, String twitterId, ApplicationUser jiraAdmin)
			throws TwitterException {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("***************")
				.setOAuthConsumerSecret("***************")
				.setOAuthAccessToken("***************")
				.setOAuthAccessTokenSecret("***************");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		final RemoteIssueLink remoteIssueLink = new RemoteIssueLinkBuilder().url("https://twitter.com/" + twitterId)
				.title(twitter.showUser(twitterId).getName()).globalId(twitterId).issueId(issue.getId())
				.relationship("Twitter Link").applicationName("Twitter").applicationType("Twitter").build();

		RemoteIssueLinkService.CreateValidationResult validationResult = this.remoteIssueLinkService
				.validateCreate(jiraAdmin, remoteIssueLink);
		if (validationResult.isValid()) {
			RemoteIssueLinkResult result = this.remoteIssueLinkService.create(jiraAdmin, validationResult);
			if (!result.isValid()) {
				logErrors(result.getErrorCollection());
			}
		} else {
			logErrors(validationResult.getErrorCollection());
		}
	}

	private void logErrors(ErrorCollection errors) {
		Collection<String> errorMessages = errors.getErrorMessages();
		for (String errorMessage : errorMessages) {
			System.out.println(errorMessage);
		}
		Map<String, String> errorMap = errors.getErrors();
		for (String errorKey : errorMap.keySet()) {
			System.out.println("Error for " + errorKey + ": " + errorMap.get(errorKey));
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}