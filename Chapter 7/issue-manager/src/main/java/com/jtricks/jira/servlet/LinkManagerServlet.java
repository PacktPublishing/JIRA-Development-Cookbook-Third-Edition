package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.issue.link.LinkCollection;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;

public class LinkManagerServlet extends HttpServlet {

	private IssueService issueService;
	private JiraAuthenticationContext authenticationContext;
	private IssueLinkManager issueLinkManager;
	private IssueLinkTypeManager issueLinkTypeManager;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		issueService = ComponentAccessor.getIssueService();
		authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
		issueLinkManager = ComponentAccessor.getIssueLinkManager();
		issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();

		out.println("Creating Issue...");
		out.flush();

		ApplicationUser user = authenticationContext.getLoggedInUser();

		MutableIssue issue1 = createIssue(out, user);

		MutableIssue issue2 = createIssue(out, user);

		out.println("Adding duplicate link!");
		// Assuming there is one duplicate link
		IssueLinkType linkType1 = issueLinkTypeManager.getIssueLinkTypesByName("Duplicate").iterator().next();
		try {
			createLink(issue1, issue2, linkType1, user);
		} catch (CreateException e) {
			out.println("Error while creating link!" + e.getMessage());
			e.printStackTrace();
		}

		out.println("Adding enabler link!");
		// Assuming there is one Enabler link
		IssueLinkType linkType2 = issueLinkTypeManager.getIssueLinkTypesByName("Relates").iterator().next();
		try {
			createLink(issue1, issue2, linkType2, user);
		} catch (CreateException e) {
			out.println("Error while creating link!" + e.getMessage());
			e.printStackTrace();
		}

		out.println("Links of " + issue1.getKey());
		printLinks(issue1, out);

		out.println("Links of " + issue2.getKey());
		printLinks(issue2, out);

		out.println("Deletin enabler link");
		try {
			deletelink(issue1, issue2, linkType2, user);
		} catch (RemoveException e) {
			out.println("Error while removing link!" + e.getMessage());
			e.printStackTrace();
		}

		// Print the number of links of first issue once again!
		LinkCollection links = this.issueLinkManager.getLinkCollection(issue1, user);
		out.println(issue1.getKey() + " now has only " + links.getAllIssues().size() + " links");

		out.println("\n... And we are done!");
	}

	private void deletelink(MutableIssue issue1, MutableIssue issue2, IssueLinkType linkType, ApplicationUser user)
			throws RemoveException {
		IssueLink issueLink = issueLinkManager.getIssueLink(issue1.getId(), issue2.getId(), linkType.getId());
		this.issueLinkManager.removeIssueLink(issueLink, user);
	}

	private void printLinks(MutableIssue issue, PrintWriter out) {
		out.println("Inward links!");
		List<IssueLink> links = issueLinkManager.getInwardLinks(issue.getId());
		for (IssueLink issueLink : links) {
			out.println(
					issueLink.getIssueLinkType().getName() + ": Linked from " + issueLink.getSourceObject().getKey());
		}

		out.println("Outward links!");
		links = issueLinkManager.getOutwardLinks(issue.getId());
		for (IssueLink issueLink : links) {
			out.println(issueLink.getIssueLinkType().getName() + ": Linked to "
					+ issueLink.getDestinationObject().getKey());
		}
	}

	private void createLink(MutableIssue issue1, MutableIssue issue2, IssueLinkType linkType, ApplicationUser user)
			throws CreateException {
		this.issueLinkManager.createIssueLink(issue1.getId(), issue2.getId(), linkType.getId(), null, user);
	}

	private MutableIssue createIssue(PrintWriter out, ApplicationUser user) {
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setProjectId(10000L).setIssueTypeId("10000").setSummary("Test Summary")
				.setReporterId("admin").setAssigneeId("admin").setDescription("Test Description").setStatusId("10000")
				.setPriorityId("2").setFixVersionIds(10000L);

		CreateValidationResult createValidationResult = issueService.validateCreate(user, issueInputParameters);

		MutableIssue issue = null;

		if (createValidationResult.isValid()) {
			IssueResult createResult = issueService.create(user, createValidationResult);
			if (createResult.isValid()) {
				issue = createResult.getIssue();
				out.println("Created " + issue.getKey());
				out.flush();
			} else {
				Collection<String> errorMessages = createResult.getErrorCollection().getErrorMessages();
				for (String errorMessage : errorMessages) {
					out.println(errorMessage);
				}
				out.flush();
			}
		} else {
			Collection<String> errorMessages = createValidationResult.getErrorCollection().getErrorMessages();
			for (String errorMessage : errorMessages) {
				out.println(errorMessage);
			}
			Map<String, String> errors = createValidationResult.getErrorCollection().getErrors();
			Set<String> errorKeys = errors.keySet();
			for (String errorKey : errorKeys) {
				out.println(errors.get(errorKey));
			}
			out.flush();
		}

		return issue;
	}
}