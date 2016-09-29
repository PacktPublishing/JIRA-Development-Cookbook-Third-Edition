package com.jtricks.ui.tabs.issueaction;

import java.util.Date;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;

public class JTricksAction extends AbstractIssueAction {

	public JTricksAction(IssueTabPanelModuleDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	public Date getTimePerformed() {
		return new Date();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void populateVelocityParams(Map params) {
		params.put("user", ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser().getDisplayName());
	}

}
