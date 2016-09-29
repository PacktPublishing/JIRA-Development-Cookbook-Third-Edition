package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.issue.search.SearchService.IssueSearchParameters;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.order.SortOrder;

public class JTricksSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		writer.println("Searching for issues in DEMO project, assigned to current user...<br><br>");

		try {
			List<Issue> issues = getIssuesInProject(loggedInUser);
			for (Issue issue : issues) {
				writer.println("Got Issue:" + issue.getKey() + "<br>");
			}
		} catch (SearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("<br>DONE<br><br>");

		writer.println(
				"Searching for issues in multiple projects, with customer name Jobin, assigned to jobin or admin...<br><br>");

		try {
			List<Issue> issues = getIssuesInProjectsForCustomer(loggedInUser);
			for (Issue issue : issues) {
				writer.println("Got Issue:" + issue.getKey() + "<br>");
			}
		} catch (SearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("<br>DONE<br><br>");

		writer.println("Searching for issues in multiple projects, with empty assignee or reporter...<br><br>");

		try {
			List<Issue> issues = getIssuesInProjectsWithEmptyUsers(loggedInUser);
			for (Issue issue : issues) {
				writer.println("Got Issue:" + issue.getKey() + "<br>");
			}
		} catch (SearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("<br>DONE<br><br>");
		
		writer.println("Searching for issues in parsed query...<br><br>");

		try {
			List<Issue> issues = getIssuesInQuery(loggedInUser);
			for (Issue issue : issues) {
				writer.println("Got Issue:" + issue.getKey() + "<br>");
			}
		} catch (SearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("<br>DONE");
	}

	// Search for issues in DEMO project, assigned to current user
	private List<Issue> getIssuesInProject(ApplicationUser user) throws SearchException {
		JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		builder.where().project("DEMO").and().assigneeIsCurrentUser();
		builder.orderBy().assignee(SortOrder.ASC);
		Query query = builder.buildQuery();
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		SearchResults results = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
		return results.getIssues();
	}

	// Search for issues in multiple projects, with customer name Jobin,
	// assigned to jobin or admin
	private List<Issue> getIssuesInProjectsForCustomer(ApplicationUser user) throws SearchException {
		JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		builder.where().project("TEST", "DEMO").and().assignee().in("jobinkk", "admin").and().customField(10000L)
				.eq("jobinkk");
		builder.orderBy().assignee(SortOrder.ASC);
		Query query = builder.buildQuery();
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		SearchResults results = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
		return results.getIssues();
	}

	// Search for issues in multiple projects, with empty assignee or reporter
	private List<Issue> getIssuesInProjectsWithEmptyUsers(ApplicationUser user) throws SearchException {
		JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		builder.where().project("TEST", "DEMO").and().sub().assigneeIsEmpty().or().reporterIsEmpty().endsub();
		builder.orderBy().assignee(SortOrder.ASC);
		Query query = builder.buildQuery();
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		SearchResults results = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
		return results.getIssues();
	}

	// Search for issues with parsed Query
	private List<Issue> getIssuesInQuery(ApplicationUser user) throws SearchException {
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		String jqlQuery = "project = \"DEMO\" and assignee = currentUser()";
		SearchService.ParseResult parseResult = searchService.parseQuery(user, jqlQuery);
		if (parseResult.isValid()) {
			Query query = parseResult.getQuery();
			
			//IssueSearchParameters params = SearchService.IssueSearchParameters.builder().query(query).build();
			//String queryPath = searchService.getIssueSearchPath(user, params);
			//System.out.println("Query Path:"+queryPath);
			
			SearchResults results = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
			return results.getIssues();
		} else {
			System.out.println("Error parsing query:" + jqlQuery);
			return Collections.emptyList();
		}
	}

}
