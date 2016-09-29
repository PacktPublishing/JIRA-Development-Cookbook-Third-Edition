package com.jtricks;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.codehaus.jettison.json.JSONException;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class SearchClient {

	public static void main(String[] args)
			throws URISyntaxException, InterruptedException, ExecutionException, JSONException {
		// Initialize REST Client
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final URI uri = new URI("http://localhost:8080/jira");
		final JiraRestClient jiraRestClient = factory.createWithBasicHttpAuthentication(uri, "jobinkk", "*****");

		// Get specific client instances
		SearchRestClient searchClient = jiraRestClient.getSearchClient();

		// Get list of favorite filters
		Promise<Iterable<Filter>> filters = searchClient.getFavouriteFilters();
		Iterable<Filter> filterList = filters.get();
		for (Filter filter : filterList) {
			System.out.println("Got filter:" + filter.getName() + " with JQL:" + filter.getJql());
		}
		
		Promise<SearchResult> result = searchClient.searchJql("project = DEMO");
		SearchResult resultObject = result.get();
		Iterable<Issue> issues = resultObject.getIssues();
		for (Issue issue : issues) {
			System.out.println("Got issue:"+issue.getKey());
		}
		
		Set<String> fields = new HashSet<String>();
		fields.add("*all");
		Promise<SearchResult> result1 = searchClient.searchJql("project = DEMO", 1, 0, fields);
		SearchResult resultObject1 = result1.get();
		Iterable<Issue> issues1 = resultObject1.getIssues();
		for (Issue issue : issues1) {
			System.out.println("Got issue:"+issue.getKey());
		}
		
	}

}
