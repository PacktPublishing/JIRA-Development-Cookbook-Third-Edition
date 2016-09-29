package com.jtricks.jira.servlet;

import java.io.File;
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
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.util.AttachmentException;

public class AttachmentManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 7386823734519714291L;

	private IssueService issueService;
	private JiraAuthenticationContext authenticationContext;
	private AttachmentManager attachmentManager;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		issueService = ComponentAccessor.getIssueService();
		authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
		attachmentManager = ComponentAccessor.getAttachmentManager();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();

		out.println("Creating Issue...");
		out.flush();

		ApplicationUser user = authenticationContext.getLoggedInUser();

		MutableIssue issue = createIssue(out, user);

		out.println("Attaches files to Issue..");

		attachFileOnIssue(issue, user, "/Users/jobinkk/test1.txt", "myFile1");
		attachFileOnIssue(issue, user, "/Users/jobinkk/test2.txt", "myFile2");

		printAttachments(issue, out);

		out.println("Deleting myFile...");

		deleteAttachment(issue, "myFile1");

		out.println("\n... And we are done!");
	}

	private void deleteAttachment(MutableIssue issue, String fileName) {
		List<Attachment> attachments = this.attachmentManager.getAttachments(issue);
		Attachment attachmentTBD = null;
		for (Attachment attachment : attachments) {
			if (attachment.getFilename().equals(fileName)) {
				attachmentTBD = attachment;
			}
		}
		try {
			this.attachmentManager.deleteAttachment(attachmentTBD);
		} catch (RemoveException e) {
			e.printStackTrace();
		}
	}

	private void printAttachments(MutableIssue issue, PrintWriter out) {
		List<Attachment> attachments = this.attachmentManager.getAttachments(issue);
		for (Attachment attachment : attachments) {
			out.println("Attachment: " + attachment.getFilename() + " attached by " + attachment.getAuthorKey());
		}
	}

	private void attachFileOnIssue(MutableIssue issue, ApplicationUser user, String fileName, String newFileName) {
		try {
			CreateAttachmentParamsBean attachmentBean = new CreateAttachmentParamsBean.Builder(new File(fileName),
					newFileName, "text/plain", user, issue).build();
			this.attachmentManager.createAttachment(attachmentBean);
		} catch (AttachmentException e) {
			e.printStackTrace();
		}
	}

	private MutableIssue createIssue(PrintWriter out, ApplicationUser user) {
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setProjectId(10000L).setIssueTypeId("10000").setSummary("Test Summary")
				.setReporterId("admin").setAssigneeId("admin").setDescription("Test Description").setStatusId("10000")
				.setPriorityId("2").setFixVersionIds(10000L);

		CreateValidationResult createValidationResult = issueService.validateCreate(user, issueInputParameters);

		MutableIssue issue = create(out, user, createValidationResult);

		return issue;
	}

	public MutableIssue create(PrintWriter out, ApplicationUser user, CreateValidationResult createValidationResult) {
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
