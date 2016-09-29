package com.jtricks.ui.links;

import java.util.List;
import java.util.Map;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.web.api.WebItem;
import com.atlassian.plugin.web.api.model.WebFragmentBuilder;
import com.atlassian.plugin.web.api.provider.WebItemProvider;
import com.google.common.collect.Lists;

public class FavouritesLinkProvider implements WebItemProvider {

	@Override
	public Iterable<WebItem> getItems(Map<String, Object> context) {
		final ApplicationUser user = (ApplicationUser) context.get("user");
		final List<WebItem> links = Lists.newArrayList();

		if (user != null) {
			links.add(new WebFragmentBuilder(10).id("issue_lnk_id1").label("Favourites 1").title("My Favourite One")
					.webItem("favourites-menu/favourites-section").url("http://www.google.com").build());
			links.add(new WebFragmentBuilder(20).id("issue_lnk_id2").label("Favourites 2").title("My Favourite Two")
					.webItem("favourites-menu/favourites-section").url("http://www.j-tricks.com").build());
		} else {
			links.add(new WebFragmentBuilder(10).id("issue_lnk_id1").label("Favourite Link").title("My Default Favourite")
					.webItem("favourites-menu/favourites-section").url("http://www.google.com").build());
		}
		
		return links;
	}

}
