package com.jtricks.jira.search;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.views.SingleIssueWriter;
import com.atlassian.jira.issue.views.util.SearchRequestViewBodyWriterUtil;
import com.atlassian.jira.issue.views.util.SearchRequestViewUtils;
import com.atlassian.jira.plugin.issueview.AbstractIssueView;
import com.atlassian.jira.plugin.searchrequestview.AbstractSearchRequestView;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestParams;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Scanned
public class SearchRequestHTMLView extends AbstractSearchRequestView {

	private final JiraAuthenticationContext authenticationContext;
	private final SearchRequestViewBodyWriterUtil searchRequestViewBodyWriterUtil;

	public SearchRequestHTMLView(@ComponentImport JiraAuthenticationContext authenticationContext,
			@ComponentImport SearchRequestViewBodyWriterUtil searchRequestViewBodyWriterUtil) {
		this.authenticationContext = authenticationContext;
		this.searchRequestViewBodyWriterUtil = searchRequestViewBodyWriterUtil;
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
			final SingleIssueWriter singleIssueWriter = new SingleIssueWriter() {
				public void writeIssue(final Issue issue, final AbstractIssueView issueView, final Writer writer)
						throws IOException {
					writer.write(issueView.getContent(issue, searchRequestParams));
				}
			};

			final IssueHTMLView htmlView = SearchRequestViewUtils.getIssueView(IssueHTMLView.class);
			searchRequestViewBodyWriterUtil.writeBody(writer, htmlView, searchRequest, singleIssueWriter,
					searchRequestParams.getPagerFilter());

			// Finally, lets write the footer using the user
			writer.write(descriptor.getHtml("footer", MapBuilder.build("user", user.getDisplayName())));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
