package com.jtricks.jira.workflow;

import java.util.Map;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.util.collect.MapBuilder;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/**
 * This is the factory class responsible for dealing with the UI for the
 * post-function. This is typically where you put default values into the
 * velocity context and where you store user input.
 */

public class SetUserCFFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	private static final String USER_NAME = "user";
	private static final String CURRENT_USER = "Current User";

	@Override
	protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put(USER_NAME, getUserName(descriptor));
	}

	@Override
	protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
		velocityParams.put(USER_NAME, CURRENT_USER);
	}

	@Override
	protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put(USER_NAME, getUserName(descriptor));
	}

	public Map<String, String> getDescriptorParams(Map<String, Object> conditionParams) {
		if (conditionParams != null && conditionParams.containsKey(USER_NAME)) {
			return MapBuilder.build(USER_NAME, extractSingleParam(conditionParams, USER_NAME));
		}

		// Create a 'hard coded' parameter
		return MapBuilder.build(USER_NAME, CURRENT_USER);
	}

	private String getUserName(AbstractDescriptor descriptor) {
		if (!(descriptor instanceof FunctionDescriptor)) {
			throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
		}

		FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;

		String user = (String) functionDescriptor.getArgs().get(USER_NAME);
		if (user != null && user.trim().length() > 0)
			return user;
		else
			return CURRENT_USER;
	}

}