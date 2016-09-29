package com.jtricks.ui.tabs.context;

import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;

public class ProjectPanelContextProvider implements ContextProvider{

	@Override
	public void init(Map<String, String> params) throws PluginParseException {		
	}

	@Override
	public Map<String, Object> getContextMap(Map<String, Object> context) {
		ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		context.put("user", user != null ? user.getDisplayName() : "Anonymous");
		return context;
	}

}
