package com.jtricks.jira.jql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.JiraDataType;
import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.plugin.jql.function.AbstractJqlFunction;
import com.atlassian.jira.plugin.jql.function.ClauseSanitisingJqlFunction;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.jira.util.MessageSetImpl;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.FunctionOperand;

@Scanned
public class ProjectsFunction extends AbstractJqlFunction implements ClauseSanitisingJqlFunction {
	private static final Logger log = LoggerFactory.getLogger(ProjectsFunction.class);
	
	private final ProjectManager projectManager;
	private final PermissionManager permissionManager;
	
	public ProjectsFunction(@ComponentImport ProjectManager projectManager, @ComponentImport PermissionManager permissionManager) {
		this.projectManager = projectManager;
		this.permissionManager = permissionManager;
	}

	public int getMinimumNumberOfExpectedArguments() {
		return 1;
	}

	public JiraDataType getDataType() {
		return JiraDataTypes.PROJECT;
	}

	@Override
	public MessageSet validate(ApplicationUser user, FunctionOperand operand, TerminalClause terminalClause) {
		List<String> projectKeys = operand.getArgs();
		MessageSet messages = new MessageSetImpl();
		if (projectKeys.isEmpty()) {
			messages.addErrorMessage("Atleast one project key needed");
		} else {
			for (String projectKey : projectKeys) {
				if (projectManager.getProjectObjByKey(projectKey) == null) {
					messages.addErrorMessage("Invalid Project Key:" + projectKey);
				}
			}
		}
		return messages;
	}
	
	@Override
	public List<QueryLiteral> getValues(QueryCreationContext context, FunctionOperand operand,
			TerminalClause terminalClause) {
		List<QueryLiteral> literals = new LinkedList<QueryLiteral>();
		List<String> projectKeys = operand.getArgs();
		for (String projectKey : projectKeys) {
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project != null) {
				literals.add(new QueryLiteral(operand, project.getId()));
			}
		}
		return literals;
	}
	
	@Override
	public FunctionOperand sanitiseOperand(ApplicationUser user, FunctionOperand functionOperand) {
		final List<String> pKeys = functionOperand.getArgs();

		boolean argChanged = false;
		final List<String> newArgs = new ArrayList<String>(pKeys.size());

		for (final String pKey : pKeys) {
			Project project = projectManager.getProjectObjByKey(pKey);
			if (project != null && !permissionManager.hasPermission(ProjectPermissions.BROWSE_PROJECTS, project, user)) {
				newArgs.add(project.getId().toString());
				argChanged = true;
			} else {
				newArgs.add(pKey);
			}
		}

		if (argChanged) {
			return new FunctionOperand(functionOperand.getName(), newArgs);
		} else {
			return functionOperand;
		}
	}
}