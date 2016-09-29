package com.jtricks;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.codehaus.jettison.json.JSONException;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.VersionRestClient;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.VersionInput;
import com.atlassian.jira.rest.client.api.domain.input.VersionPosition;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class VersionClient {

	public static void main(String[] args)
			throws URISyntaxException, InterruptedException, ExecutionException, JSONException {
		// Initialize REST Client
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final URI uri = new URI("http://localhost:8080/jira");
		final JiraRestClient jiraRestClient = factory.createWithBasicHttpAuthentication(uri, "jobinkk", "*****");

		// Get specific client instances
		VersionRestClient versionClient = jiraRestClient.getVersionRestClient();

		// Create Version
		VersionInput versionInput = new VersionInput("DEMO", "JRJC", "Test", new DateTime(), false, false);
		Promise<Version> version = versionClient.createVersion(versionInput);
		Version versionObj = version.get();
		System.out.println("Created Version:" + versionObj.getName() + " with ID:" + versionObj.getId().toString());

		// Browse Version
		Promise<Version> browsedVersion = versionClient.getVersion(versionObj.getSelf());
		Version browsedVersionObj = browsedVersion.get();
		System.out.println("Retrived Version:" + browsedVersionObj.getName() + " with ID:"
				+ browsedVersionObj.getId().toString() + " and URI:" + browsedVersionObj.getSelf().toString());

		// Release Version using Update
		versionClient.updateVersion(browsedVersionObj.getSelf(),
				new VersionInput("DEMO", "JRJC", "Test", new DateTime(), false, true));

		// Move version to the first one
		versionClient.moveVersion(browsedVersionObj.getSelf(), VersionPosition.FIRST);

		// Get number of unresolved issues
		Promise<Integer> number = versionClient.getNumUnresolvedIssues(browsedVersionObj.getSelf());
		System.out.println("Got " + number.get().toString() + " unresolved issues");
		
		versionClient.removeVersion(browsedVersionObj.getSelf(), null, null).claim();
		System.out.println("Deleted version");

	}

}
