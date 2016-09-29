package com.jtricks.ui.tabs;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.atlassian.jira.user.ApplicationUser;
import com.jtricks.ui.tabs.issueaction.JTricksAction;

public class JTricksIssueTabPanel extends AbstractIssueTabPanel {

	public List getActions(Issue issue, ApplicationUser remoteUser) {
		List<JTricksAction> panelActions = new ArrayList<JTricksAction>();
		panelActions.add(new JTricksAction(descriptor));
		return panelActions;
	}

	public boolean showPanel(Issue issue, ApplicationUser remoteUser) {
		return true;
	}

}
