package com.jtricks;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.codehaus.jettison.json.JSONException;

import com.atlassian.jira.rest.client.api.ComponentRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.AssigneeType;
import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.api.domain.input.ComponentInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class ComponentClient {

	public static void main(String[] args)
			throws URISyntaxException, InterruptedException, ExecutionException, JSONException {
		// Initialize REST Client
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final URI uri = new URI("http://localhost:8080/jira");
		final JiraRestClient jiraRestClient = factory.createWithBasicHttpAuthentication(uri, "jobinkk", "******");

		// Get specific client instances
		ComponentRestClient componentClient = jiraRestClient.getComponentClient();

		// Create Component
		ComponentInput componentInput = new ComponentInput("JRJC", "Test", "jobinkk", AssigneeType.COMPONENT_LEAD);
		Promise<Component> component = componentClient.createComponent("DEMO", componentInput);
		Component componentObj = component.get();
		System.out.println("Created component:" + componentObj.getName() + " with ID:" + componentObj.getId().toString()
				+ " and URI:" + componentObj.getSelf().toString());

		// Browse Component
		Promise<Component> browsedComponent = componentClient.getComponent(componentObj.getSelf());
		Component browsedComponentObj = browsedComponent.get();
		System.out.println("Browsed component:" + browsedComponentObj.getName() + " with ID:"
				+ browsedComponentObj.getId().toString());
		
		// Change lead to 'test' user
		componentClient.updateComponent(browsedComponentObj.getSelf(), new ComponentInput("JRJC", "Test", "test", AssigneeType.COMPONENT_LEAD)); 
		
		// Get list of issues
		Promise<Integer> number = componentClient.getComponentRelatedIssuesCount(browsedComponentObj.getSelf());
		System.out.println("Got "+number.get().toString()+" issues in the component");

		// Delete Component
		componentClient.removeComponent(browsedComponentObj.getSelf(), null);
		System.out.println("Deleted component");

	}
}
