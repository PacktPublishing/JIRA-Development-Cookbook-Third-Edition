package com.jtricks.ui.context;

import java.util.Map;

import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.collect.MapBuilder;

public class UserContextProvider extends AbstractJiraContextProvider {

	@Override
	public Map getContextMap(ApplicationUser user, JiraHelper helper) {
		return MapBuilder.build("userName", user.getDisplayName());
	}

}
