package com.jtricks.jira.user;

import static com.atlassian.jira.template.TemplateSources.file;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.jira.plugin.profile.UserFormat;
import com.atlassian.jira.template.VelocityTemplatingEngine;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Named("TwitterUserFormat")
public class TwitterUserFormat implements UserFormat {

	private final VelocityTemplatingEngine templatingEngine;

	@Inject
	public TwitterUserFormat(@ComponentImport VelocityTemplatingEngine templatingEngine) {
		this.templatingEngine = templatingEngine;
	}

	public String format(String username, String id) {
		final Map<String, Object> params = getInitialParams(username, id);
		return templatingEngine.render(file("templates/user/twitterLink.vm")).applying(params).asHtml();
	}

	public String format(String username, String id, Map<String, Object> params) {
		final Map<String, Object> velocityParams = getInitialParams(username, id);
		velocityParams.putAll(params);

		return templatingEngine.render(file("templates/user/twitterLink.vm")).applying(velocityParams).asHtml();
	}

	private Map<String, Object> getInitialParams(final String username, final String id) {
		final Map<String, Object> params = MapBuilder.<String, Object> newBuilder().add("username", username)
				.toMutableMap();
		return params;
	}

}
