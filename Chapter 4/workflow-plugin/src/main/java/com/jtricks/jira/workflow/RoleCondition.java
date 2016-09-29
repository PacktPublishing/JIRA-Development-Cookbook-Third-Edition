package com.jtricks.jira.workflow;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.module.propertyset.PropertySet;

@Scanned
public class RoleCondition extends AbstractJiraCondition {
	private static final Logger log = LoggerFactory.getLogger(RoleCondition.class);

	private static final String ROLE = "role";

	private final ProjectRoleManager projectRoleManager;

	public RoleCondition(@ComponentImport ProjectRoleManager projectRoleManager) {
		this.projectRoleManager = projectRoleManager;
	}

	public boolean passesCondition(Map transientVars, Map args, PropertySet ps) {
		Issue issue = getIssue(transientVars);
		ApplicationUser user = getCallerUser(transientVars, args);
		
		Project project = issue.getProjectObject();
		
		String role = (String)args.get(ROLE);
		Long roleId = new Long(role);
		
		return projectRoleManager.isUserInProjectRole(user, projectRoleManager.getProjectRole(roleId), project);
	}
}
