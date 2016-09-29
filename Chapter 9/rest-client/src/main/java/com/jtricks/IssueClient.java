package com.jtricks;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.UserRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class IssueClient {

	public static void main(String[] args)
			throws URISyntaxException, InterruptedException, ExecutionException, JSONException {
		// Initialize REST Client
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final URI uri = new URI("http://localhost:8080/jira");
		final JiraRestClient jiraRestClient = factory.createWithBasicHttpAuthentication(uri, "jobinkk", "*****");

		// Get specific client instances
		IssueRestClient issueClient = jiraRestClient.getIssueClient();
		UserRestClient userClient = jiraRestClient.getUserClient();

		// Initiate the IssueInputBuilder
		IssueInputBuilder issueInputBuilder = new IssueInputBuilder("DEMO", 1L, "Test Summary");

		// Set standard fields
		issueInputBuilder.setPriorityId(2L).setComponentsNames(Arrays.asList("Apples", "Oranges"))
				.setAffectedVersionsNames(Arrays.asList("1.0", "1.1")).setReporterName("test")
				.setAssigneeName("jobinkk").setDueDate(new DateTime());

		// Set text custom field
		issueInputBuilder.setFieldValue("customfield_10205", "Test text Val");
		// Set multi user custom field
		User jobin = userClient.getUser("jobinkk").get();
		User testUser = userClient.getUser("test").get();
		issueInputBuilder.setFieldValue("customfield_10300", Arrays.asList(jobin, testUser));
		// Set select field
		issueInputBuilder.setFieldValue("customfield_10200", ComplexIssueInputFieldValue.with("value", "Three"));
		// Set multi select
		List<ComplexIssueInputFieldValue> fieldList = new ArrayList<ComplexIssueInputFieldValue>();
		String[] valuesList = new String[] { "Alpha", "Gamma" };
		for (String aValue : valuesList) {
			Map<String, Object> mapValues = new HashMap<String, Object>();
			mapValues.put("value", aValue);
			ComplexIssueInputFieldValue fieldValue = new ComplexIssueInputFieldValue(mapValues);
			fieldList.add(fieldValue);
		}
		issueInputBuilder.setFieldValue("customfield_10201", fieldList);
		// Set Cascading
		Map<String, Object> cascadingValues = new HashMap<String, Object>();
		cascadingValues.put("value", "Parent 2");
		cascadingValues.put("child", ComplexIssueInputFieldValue.with("value", "Child 22"));
		issueInputBuilder.setFieldValue("customfield_10301", new ComplexIssueInputFieldValue(cascadingValues));

		// Build the IssueInput
		IssueInput issueInput = issueInputBuilder.build();

		// Create issue
		Promise<BasicIssue> createdIssue = jiraRestClient.getIssueClient().createIssue(issueInput);
		BasicIssue issue = createdIssue.get();
		String issueKey = issue.getKey();
		System.out.println("Created issue:" + issueKey);

		// Update Issue
		IssueInputBuilder updateInputBuilder = new IssueInputBuilder();
		updateInputBuilder.setAssignee(testUser);
		issueClient.updateIssue(issueKey, updateInputBuilder.build());
		System.out.println("Updated issue:" + issueKey);

		final Promise<Issue> newIssue = jiraRestClient.getIssueClient().getIssue(issueKey);
		Issue browsedIssue = newIssue.get();
		System.out.println("Retrieved issue:" + browsedIssue.getKey());

		// Browse Issue values
		Iterable<IssueField> fields = browsedIssue.getFields();
		for (IssueField field : fields) {
			System.out.println("Field:" + field.getName());
			if (field.getName().equals("Test Multi User")) {
				JSONArray array = (JSONArray) field.getValue();
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					System.out.println("Value:" + obj.getString("displayName"));
				}
			} else if (field.getName().equals("Test Multi Select")) {
				JSONArray array = (JSONArray) field.getValue();
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					System.out.println("Value:" + obj.getString("value"));
				}
			} else if (field.getName().equals("Test Select")) {
				JSONObject obj = (JSONObject) field.getValue();
				System.out.println("Value:" + obj.getString("value"));
			} else if (field.getName().equals("Test Cascading")) {
				JSONObject obj = (JSONObject) field.getValue();
				System.out.println("Parent Val:" + obj.getString("value"));
				System.out.println("Child Val:" + obj.getJSONObject("child").getString("value"));
			} else {
				System.out.println("Value:" + field.getValue());
			}
		}

		// Progress Issue in Workflow
		Promise<Iterable<Transition>> transitions = jiraRestClient.getIssueClient()
				.getTransitions(browsedIssue.getTransitionsUri());
		Transition resolveTransition = getTransitionByName(transitions.get(), "Go to Closed");
		Collection<FieldInput> fieldInputs = Arrays
				.asList(new FieldInput("resolution", ComplexIssueInputFieldValue.with("name", "Fixed")));
		Comment comment = Comment.valueOf("Resolving issue using JRJC");
		TransitionInput transitionInput = new TransitionInput(resolveTransition.getId(), fieldInputs, comment);
		issueClient.transition(browsedIssue.getTransitionsUri(), transitionInput).claim();
		System.out.println("Closed issue:" + browsedIssue.getKey());
	}

	private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName) {
		for (Transition transition : transitions) {
			if (transition.getName().equals(transitionName)) {
				return transition;
			}
		}
		return null;
	}

}
