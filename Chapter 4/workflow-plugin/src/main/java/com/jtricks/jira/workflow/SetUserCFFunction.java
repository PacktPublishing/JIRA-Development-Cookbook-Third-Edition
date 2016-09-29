package com.jtricks.jira.workflow;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * This is the post-function class that gets executed at the end of the
 * transition. Any parameters that were saved in your factory class will be
 * available in the transientVars Map.
 */

@Scanned
public class SetUserCFFunction extends AbstractJiraFunctionProvider {
	private static final Logger log = LoggerFactory.getLogger(SetUserCFFunction.class);
	private final CustomFieldManager customFieldManager;
	private final JiraAuthenticationContext authContext;
	private final UserManager userManager;

	public SetUserCFFunction(@ComponentImport CustomFieldManager customFieldManager, @ComponentImport JiraAuthenticationContext authContext,
			@ComponentImport UserManager userManager) {
		this.customFieldManager = customFieldManager;
		this.authContext = authContext;
		this.userManager = userManager;
	}

	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
		MutableIssue issue = getIssue(transientVars);

		ApplicationUser user = null;

		log.info("User from args:" + args.get("user"));

		if (args.get("user") != null) {
			String userName = (String) args.get("user");
			if (userName.equals("Current User")) {
				// Set the current user here!
				user = authContext.getLoggedInUser();
			} else {
				user = userManager.getUserByName(userName);
			}
		} else {
			// Set the current user here!
			user = authContext.getLoggedInUser();
		}

		log.info("user Set:" + user);

		// Now set the user value to the custom field
		CustomField userField = customFieldManager.getCustomFieldObjectByName("Test User");
		if (userField != null) {
			setUserValue(issue, user, userField);
		}

		log.info("Done!");
	}

	private void setUserValue(MutableIssue issue, ApplicationUser user, CustomField userField) {
		issue.setCustomFieldValue(userField, user);

		Map modifiedFields = issue.getModifiedFields();
		FieldLayoutItem fieldLayoutItem = ComponentAccessor.getFieldLayoutManager().getFieldLayout(issue)
				.getFieldLayoutItem(userField);
		DefaultIssueChangeHolder issueChangeHolder = new DefaultIssueChangeHolder();
		final ModifiedValue modifiedValue = (ModifiedValue) modifiedFields.get(userField.getId());

		userField.updateValue(fieldLayoutItem, issue, modifiedValue, issueChangeHolder);
	}
}