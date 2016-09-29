package com.jtricks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.AttachmentInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class AttachmentClient {

	public static void main(String[] args) throws URISyntaxException, FileNotFoundException, InterruptedException, ExecutionException {
		
		// Initialize REST Client
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final URI uri = new URI("http://localhost:8080/jira");
		final JiraRestClient jiraRestClient = factory.createWithBasicHttpAuthentication(uri, "jobinkk", "*****");

		// Get specific client instances
		IssueRestClient issueClient = jiraRestClient.getIssueClient();
		
		// Read file into input stream
		InputStream in = new FileInputStream("/Users/jobinkk/Desktop/test.txt");
		final Promise<Issue> issue = issueClient.getIssue("DEMO-1");
		Issue browsedIssue = issue.get();
		
		URI attachmentURI = browsedIssue.getAttachmentsUri();
		
		// Using input stream
		issueClient.addAttachment(attachmentURI , in, "file.txt").claim();

		// Using AttachmentInput
		InputStream in1 = new FileInputStream("/Users/jobinkk/Desktop/test.txt");
		AttachmentInput input = new AttachmentInput("file1.txt", in1);
		issueClient.addAttachments(attachmentURI , input).claim();
		
		//Using File
		File file = new File("/Users/jobinkk/Desktop/test.txt");
		issueClient.addAttachments(attachmentURI , file).claim();

		// Browse Attachments
		final Promise<Issue> attachedIssue = issueClient.getIssue("DEMO-1");
		Iterable<Attachment> attachments = attachedIssue.get().getAttachments();
		for (Attachment attachment : attachments) {
		  System.out.println("Name:" + attachment.getFilename() + ", added by:"
		    + attachment.getAuthor().getDisplayName() + ", URI:" + attachment.getSelf());
		}

	}

}
