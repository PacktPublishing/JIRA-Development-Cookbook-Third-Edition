package com.jtricks.ui.renderer;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.jira.issue.link.RemoteIssueLink;
import com.atlassian.jira.plugin.issuelink.AbstractIssueLinkRenderer;
import com.atlassian.jira.util.I18nHelper;
import com.google.common.collect.ImmutableMap;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterLinkRenderer extends AbstractIssueLinkRenderer {

	public static final String DEFAULT_ICON_URL = "/download/resources/com.jtricks.ui-plugin:ui-plugin-resources/images/twitter.jpg";

	@Override
	public Map<String, Object> getInitialContext(RemoteIssueLink twitterLink, Map<String, Object> context) {
		final I18nHelper i18n = getValue(context, "i18n", I18nHelper.class);
		final String baseUrl = getValue(context, "baseurl", String.class);
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("***************")
				.setOAuthConsumerSecret("***************")
				.setOAuthAccessToken("***************")
				.setOAuthAccessTokenSecret("***************");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		String status;
		try {
			ResponseList<Status> response = twitter.getUserTimeline(twitterLink.getGlobalId());
			status = response.get(0).getText();// Get latest status
		} catch (TwitterException e) {
			status = "Could not reach twitter!";
			e.printStackTrace();
		}

		return createContext(twitterLink, i18n, baseUrl, status);
	}

	@Override
	public Map<String, Object> getFinalContext(RemoteIssueLink twitterLink, Map<String, Object> context) {
		final I18nHelper i18n = getValue(context, "i18n", I18nHelper.class);
		final String baseUrl = getValue(context, "baseurl", String.class);
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("***************")
				.setOAuthConsumerSecret("***************")
				.setOAuthAccessToken("***************")
				.setOAuthAccessTokenSecret("***************");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		String status;
		try {
			ResponseList<Status> response = twitter.getUserTimeline(twitterLink.getGlobalId());
			status = response.get(0).getText();
		} catch (TwitterException e) {
			status = "Could not reach twitter!";
			e.printStackTrace();
		}

		ImmutableMap.Builder<String, Object> contextBuilder = ImmutableMap.builder();
		contextBuilder.putAll(createContext(twitterLink, i18n, baseUrl, status));

		try {
			putMap(contextBuilder, "followers", twitter.getFollowersIDs(-1).getIDs().length);
		} catch (TwitterException e) {
			putMap(contextBuilder, "followers", "?");
			e.printStackTrace();
		}

		return contextBuilder.build();
	}

	private <T> T getValue(Map<String, Object> context, String key, Class<T> klass) {
		Object obj = context.get(key);
		if (obj == null) {
			throw new IllegalArgumentException(String.format("Expected '%s' to exist in the context map", key));
		}
		return klass.cast(obj);
	}

	private static Map<String, Object> createContext(RemoteIssueLink remoteIssueLink, I18nHelper i18n, String baseUrl,
			String status) {
		ImmutableMap.Builder<String, Object> contextBuilder = ImmutableMap.builder();

		String tooltip = getTooltip(remoteIssueLink.getApplicationName(), remoteIssueLink.getSummary(),
				remoteIssueLink.getTitle());
		final String iconUrl = StringUtils.defaultIfEmpty(remoteIssueLink.getIconUrl(), baseUrl + DEFAULT_ICON_URL);
		final String iconTooltip = getIconTooltip(remoteIssueLink, i18n);

		putMap(contextBuilder, "id", remoteIssueLink.getId());
		putMap(contextBuilder, "url", remoteIssueLink.getUrl());
		putMap(contextBuilder, "title", remoteIssueLink.getTitle());
		putMap(contextBuilder, "iconUrl", iconUrl);
		putMap(contextBuilder, "iconTooltip", iconTooltip);
		putMap(contextBuilder, "tooltip", tooltip);
		putMap(contextBuilder, "status", status.length() > 50 ? status.substring(0, 50) + "..." : status);
		return contextBuilder.build();
	}

	private static void putMap(ImmutableMap.Builder<String, Object> mapBuilder, String key, Object value) {
		if (value != null) {
			mapBuilder.put(key, value);
		}
	}

	private static String getIconTooltip(RemoteIssueLink remoteIssueLink, I18nHelper i18n) {
		final boolean hasApplicationName = StringUtils.isNotEmpty(remoteIssueLink.getApplicationName());
		final boolean hasIconText = StringUtils.isNotEmpty(remoteIssueLink.getIconTitle());

		if (hasApplicationName && hasIconText) {
			return "[" + remoteIssueLink.getApplicationName() + "] " + remoteIssueLink.getIconTitle();
		} else if (hasApplicationName) {
			return "[" + remoteIssueLink.getApplicationName() + "]";
		} else if (hasIconText) {
			return remoteIssueLink.getIconTitle();
		} else {
			return i18n.getText("issuelinking.remote.link.weblink.title");
		}
	}

	private static String getTooltip(String appliationName, String summary, String title) {
		final boolean hasApplicationName = StringUtils.isNotEmpty(appliationName);
		final boolean hasSummary = StringUtils.isNotEmpty(summary);

		if (hasApplicationName && hasSummary) {
			return "[" + appliationName + "] " + title + ": " + summary;
		} else if (hasApplicationName) {
			return "[" + appliationName + "] " + title;
		} else if (hasSummary) {
			return title + ": " + summary;
		} else {
			return title;
		}
	}

	@Override
	public boolean requiresAsyncLoading(RemoteIssueLink remoteIssueLink) {
		return true;
	}

}
