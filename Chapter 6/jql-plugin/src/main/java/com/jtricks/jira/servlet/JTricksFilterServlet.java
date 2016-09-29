package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.FilterSubscriptionService;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.sharing.SharePermissionUtils;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.query.Query;

public class JTricksFilterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		writer.println("Creating filter...<br><br>");
		
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		String jqlQuery = "project = \"DEMO\" and assignee = currentUser()";
		SearchService.ParseResult parseResult = searchService.parseQuery(loggedInUser, jqlQuery);
		if (parseResult.isValid()) {
			Query query = parseResult.getQuery();
			SearchRequest searchRequest = new SearchRequest(query, loggedInUser, "Test Filter", "Test Description");
			JiraServiceContext ctx = new JiraServiceContextImpl(loggedInUser);
			SearchRequestService searchRequestService = ComponentAccessor.getComponent(SearchRequestService.class);

			final SearchRequest newSearchRequest = searchRequestService.createFilter(ctx, searchRequest, true);
			
			writer.println("Created filter with ID:"+newSearchRequest.getId().toString()+"<br><br>");

			writer.println("Updating filter...<br><br>");
			newSearchRequest.setName("Updated Filter");
			try {
				newSearchRequest.setPermissions(SharePermissionUtils.fromJsonArrayString("[{\"type\":\"global\"}]"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SearchRequest updatedFilter = searchRequestService.updateFilter(ctx, newSearchRequest, false);
			
			writer.println("Filter Updated with ID:"+updatedFilter.getId().toString()+"<br><br>");
			
			writer.println("Subscribing to filter...<br><br>");
			FilterSubscriptionService filterSubscriptionService = ComponentAccessor.getComponent(FilterSubscriptionService.class);
			String cronExpression = "0 0/15 * * * ? *"; // Denotes every 15 minutes
			String groupName = "jira-administrators";
			boolean mailOnEmpty = true;
			filterSubscriptionService.validateCronExpression(ctx, cronExpression);
			if (!ctx.getErrorCollection().hasAnyErrors()){
				filterSubscriptionService.storeSubscription(ctx, updatedFilter.getId(), groupName, cronExpression, mailOnEmpty);
			}			
			writer.println("Subscribed to filter...<br><br>");
		} else {
			writer.println("Invalid JQL Query:"+jqlQuery+"<br><br>");
		}
	}

}
