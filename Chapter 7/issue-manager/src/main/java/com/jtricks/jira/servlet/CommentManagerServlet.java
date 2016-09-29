package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.bc.issue.comment.CommentService;
import com.atlassian.jira.bc.issue.comment.CommentService.CommentCreateValidationResult;
import com.atlassian.jira.bc.issue.comment.CommentService.CommentUpdateValidationResult;
import com.atlassian.jira.bc.issue.visibility.Visibilities;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;

public class CommentManagerServlet extends HttpServlet {

	private IssueService issueService;
	private CommentService commentService;
	private JiraAuthenticationContext authenticationContext;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		issueService = ComponentAccessor.getIssueService();
		authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
		commentService = ComponentAccessor.getComponent(CommentService.class);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();

		out.println("Creating Issue...");
		out.flush();

		ApplicationUser user = authenticationContext.getLoggedInUser();

		MutableIssue issue = createIssue(out, user);

		out.println("Add Comment");
		Comment comment1 = addComment("My Comment1", issue, user);

		out.println("Add Comment Restricted to role!");
		Comment comment2 = addCommentRole("My Comment2", issue, user, 10002L);

		out.println("Add Comment Restricted to group!");
		Comment comment3 = addCommentGroup("My Comment3", issue, user, "jira-administrators");

		out.println("Editing first comment!");
		editComment("My New Comment", issue, user, comment1);
		
		out.println("Add Comment for delete");
		Comment comment4 = addComment("My Comment4", issue, user);

		out.println("Deleting second comment!");
		deleteComment(comment4, user);

		out.println("\n... And we are done!");
	}

	private void deleteComment(Comment comment, ApplicationUser user) {
		this.commentService.delete(new JiraServiceContextImpl(user), comment, false);
	}

	private void editComment(String modifiedComment, MutableIssue issue, ApplicationUser user, Comment comment) {
		CommentService.CommentParameters commentParams = new CommentService.CommentParameters.CommentParametersBuilder()
				.body(modifiedComment).build();
		CommentUpdateValidationResult commentResult = this.commentService.validateCommentUpdate(user, comment.getId(),
				commentParams);
		this.commentService.update(user, commentResult, true);
	}

	private Comment addCommentRole(String commentString, MutableIssue issue, ApplicationUser user, Long roleId) {
		CommentService.CommentParameters commentParams = new CommentService.CommentParameters.CommentParametersBuilder()
				.issue(issue).body(commentString).visibility(Visibilities.roleVisibility(roleId)).build();
		CommentCreateValidationResult commentResult = this.commentService.validateCommentCreate(user, commentParams);
		return this.commentService.create(user, commentResult, true);
	}

	private Comment addCommentGroup(String commentString, MutableIssue issue, ApplicationUser user, String group) {
		CommentService.CommentParameters commentParams = new CommentService.CommentParameters.CommentParametersBuilder()
				.issue(issue).body(commentString).visibility(Visibilities.groupVisibility(group)).build();
		CommentCreateValidationResult commentResult = this.commentService.validateCommentCreate(user, commentParams);
		return this.commentService.create(user, commentResult, true);
	}

	private Comment addComment(String commentString, MutableIssue issue, ApplicationUser user) {
		CommentService.CommentParameters commentParams = new CommentService.CommentParameters.CommentParametersBuilder()
				.issue(issue).body(commentString).build();
		CommentCreateValidationResult commentResult = this.commentService.validateCommentCreate(user, commentParams);
		return this.commentService.create(user, commentResult, true);
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