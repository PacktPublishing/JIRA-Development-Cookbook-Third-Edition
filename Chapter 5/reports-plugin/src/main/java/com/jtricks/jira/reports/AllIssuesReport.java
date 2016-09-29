package com.jtricks.jira.reports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;

import webwork.action.ActionContext;

@Scanned
public class AllIssuesReport extends AbstractReport {

	private final JiraAuthenticationContext authContext;
	private final UserUtil userUtil;
	private final SearchService searchService;
	private final ProjectManager projectManager;

	public AllIssuesReport(@ComponentImport JiraAuthenticationContext authContext, @ComponentImport UserUtil userUtil,
			@ComponentImport SearchService searchService, @ComponentImport ProjectManager projectManager) {
		super();
		this.authContext = authContext;
		this.userUtil = userUtil;
		this.searchService = searchService;
		this.projectManager = projectManager;
	}

	@Override
	public void validate(ProjectActionSupport action, Map reqParams) {
		// Do your validation here if you have any!
		final String projectid = (String) reqParams.get("projectId");
		final Long pid = new Long(projectid);
		if (this.projectManager.getProjectObj(pid) == null) {
			action.addError("projectId", "No project with id:" + pid + " exists!");
		}

		super.validate(action, reqParams);
	}

	public String generateReportHtml(ProjectActionSupport action, Map reqParams) throws Exception {
		// Examples of Object Configurable Properties
		//printRequestValues(reqParams); 
		final Map<String, Object> velocityParams = getVelocityParams(action, reqParams);
		return descriptor.getHtml("view", velocityParams);
	}

	@Override
	public String generateReportExcel(ProjectActionSupport action, Map reqParams) throws Exception {
		final Map<String, Object> velocityParams = getVelocityParams(action, reqParams);
		final StringBuilder contentDispositionValue = new StringBuilder(50);
		contentDispositionValue.append("attachment;filename=\"");
		contentDispositionValue.append(getDescriptor().getName()).append(".xls\";");
		final HttpServletResponse response = ActionContext.getResponse();
		response.addHeader("content-disposition", contentDispositionValue.toString());
		return descriptor.getHtml("excel", velocityParams);
	}

	@Override
	public boolean isExcelViewSupported() {
		return true;
	}

	private Map<String, Object> getVelocityParams(ProjectActionSupport action, Map reqParams) throws SearchException {
		final String projectid = (String) reqParams.get("projectId");
		final Long pid = new Long(projectid);

		final Map<String, Object> velocityParams = new HashMap<String, Object>();
		velocityParams.put("report", this);
		velocityParams.put("action", action);
		velocityParams.put("issues", getIssuesFromProject(pid));
		return velocityParams;
	}

	List<Issue> getIssuesFromProject(Long pid) throws SearchException {
		JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		builder.where().project(pid);
		Query query = builder.buildQuery();

		SearchResults results = this.searchService
				.search(this.authContext.getLoggedInUser(), query, PagerFilter.getUnlimitedFilter());
		return results.getIssues();
	}

	@Override
	public boolean showReport() {
		ApplicationUser user = this.authContext.getLoggedInUser();
		return this.userUtil.getJiraAdministrators().contains(user);
	}
	
	private void printRequestValues(Map reqParams) {
		// TODO Auto-generated method stub
		final String testString = (String) reqParams.get("testString");
		final String testLong = (String) reqParams.get("testLong");
		final String testHidden = (String) reqParams.get("testHidden");
		final String testDate = (String) reqParams.get("testDate");
		final String testUser = (String) reqParams.get("testUser");
		final String testGroup = (String) reqParams.get("testGroup");
		final String testText = (String) reqParams.get("testText");
		final String[] testMultiSelect = (String[]) reqParams.get("testMultiSelect");
		final String testCheckBox = (String) reqParams.get("testCheckBox");
		final String testFilterPicker = (String) reqParams.get("testFilterPicker");
		final String testFilterProjectPicker = (String) reqParams.get("testFilterProjectPicker");
		final String testSelect = (String) reqParams.get("testSelect");
		final String testCascadingSelect = (String) reqParams.get("testCascadingSelect");

		System.out.println("Object Configurable Properties Demo");
		System.out.println("***********************************");
		System.out.println("Test String:" + testString);
		System.out.println("Test Long:" + testLong);
		System.out.println("Test Hidden:" + testHidden);
		System.out.println("Test Date:" + testDate);
		System.out.println("Test User:" + testUser);
		System.out.println("Test Group:" + testGroup);
		System.out.println("Test Text:" + testText);
		System.out.println("Test Multi Select:" + Arrays.asList(testMultiSelect));
		System.out.println("Test Checkbox:" + testCheckBox);
		System.out.println("Test Filter Picker:" + testFilterPicker);
		System.out.println("Test Filter Project Picker:" + testFilterProjectPicker);
		System.out.println("Test Select:" + testSelect);
		System.out.println("Test Cascading Select:" + testCascadingSelect);
		System.out.println("***********************************");
	}

}
