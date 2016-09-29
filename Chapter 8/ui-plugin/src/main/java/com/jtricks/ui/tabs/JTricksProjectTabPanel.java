package com.jtricks.ui.tabs;

import java.util.Map;

import com.atlassian.jira.plugin.projectpanel.impl.AbstractProjectTabPanel;
import com.atlassian.jira.project.browse.BrowseContext;

public class JTricksProjectTabPanel extends AbstractProjectTabPanel {

	public boolean showPanel(BrowseContext ctx) {
		return true;
	}

	@Override
	protected Map<String, Object> createVelocityParams(BrowseContext ctx) {
		Map<String, Object> params = super.createVelocityParams(ctx);
		params.put("user", ctx.getUser() != null ? ctx.getUser().getDisplayName() : "Anonymous");
		return params;
	}

}
