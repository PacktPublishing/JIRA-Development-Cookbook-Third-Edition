package com.jtricks.jira.search;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issueview.AbstractIssueView;
import com.atlassian.jira.plugin.issueview.IssueViewRequestParams;
import com.atlassian.jira.util.collect.MapBuilder;

public class IssueHTMLView extends AbstractIssueView {

	@Override
	public String getBody(Issue issue, IssueViewRequestParams params) {
		return descriptor.getHtml("body", MapBuilder.build("issue", issue));
	}

	@Override
	public String getContent(Issue issue, IssueViewRequestParams params) {
		String header = getHeader(issue);
        String body = getBody(issue, params);
        String footer = getFooter(issue);
        return header + body + footer;
	}
	
	public String getHeader(Issue issue) {
		return descriptor.getHtml("header", MapBuilder.build("issue", issue));
	}
	
	public String getFooter(Issue issue) {
		return descriptor.getHtml("footer", MapBuilder.build("issue", issue));
	}

}
