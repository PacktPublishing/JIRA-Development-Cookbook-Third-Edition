package com.jtricks.jira.webfragment.conditions;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;

public class UserCondition extends AbstractWebCondition {

	@Override
	public boolean shouldDisplay(ApplicationUser user, JiraHelper helper) {
		return user!= null && user.getName().equals("admin");
	}

}
