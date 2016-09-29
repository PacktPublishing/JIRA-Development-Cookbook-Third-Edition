package com.jtricks.jira.search;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.plugin.searchrequestview.AbstractSearchRequestView;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestParams;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Scanned
public class SimpleSearchRequestHTMLView extends AbstractSearchRequestView {

	private final JiraAuthenticationContext authenticationContext;
	private final SearchService searchService;

	public SimpleSearchRequestHTMLView(@ComponentImport JiraAuthenticationContext authenticationContext,
			@ComponentImport SearchService searchService) {
		this.authenticationContext = authenticationContext;
		this.searchService = searchService;
	}

	@Override
	public void writeSearchResults(final SearchRequest searchRequest, final SearchRequestParams searchRequestParams,
			final Writer writer) throws SearchException {

		try {
			// Write the header using filter name and user name
			final Map<String, String> headerParams = new HashMap<String, String>();
			headerParams.put("filtername", searchRequest.getName());

			ApplicationUser user = authenticationContext.getLoggedInUser();
			headerParams.put("user", user.getDisplayName());

			// Write the header using headerParams
			writer.write(descriptor.getHtml("header", headerParams));

			// Write the body using issue details
			SearchResults results = this.searchService.search(user, searchRequest.getQuery(),
					PagerFilter.getUnlimitedFilter());
			List<Issue> issues = results.getIssues();
			for (Issue issue : issues) {
				writer.write(descriptor.getHtml("body", MapBuilder.build("issue", issue)));
			}

			// Finally, lets write the footer using the user
			writer.write(descriptor.getHtml("footer", MapBuilder.build("user", user.getDisplayName())));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
