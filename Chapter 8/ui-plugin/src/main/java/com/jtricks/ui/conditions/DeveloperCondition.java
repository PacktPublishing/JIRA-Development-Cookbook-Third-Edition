package com.jtricks.ui.conditions;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;

public class DeveloperCondition extends AbstractWebCondition{

	@Override
	public boolean shouldDisplay(ApplicationUser user, JiraHelper helper) {
		return user != null  && ComponentAccessor.getGroupManager().getGroupNamesForUser(user).contains("jira-developers");
	}

}
