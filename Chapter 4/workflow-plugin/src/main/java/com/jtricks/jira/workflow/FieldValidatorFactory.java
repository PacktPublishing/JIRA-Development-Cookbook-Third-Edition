package com.jtricks.jira.workflow;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

@Scanned
public class FieldValidatorFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {
	private static final String FIELD_NAME = "field";
	private static final String FIELDS = "fields";
	private static final String NOT_DEFINED = "Not Defined";

	private final CustomFieldManager customFieldManager;

	public FieldValidatorFactory(@ComponentImport CustomFieldManager customFieldManager) {
		this.customFieldManager = customFieldManager;
	}

	@Override
	protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put(FIELD_NAME, getFieldName(descriptor));
		velocityParams.put(FIELDS, getCFFields());
	}

	@Override
	protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
		velocityParams.put(FIELDS, getCFFields());
	}

	@Override
	protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put(FIELD_NAME, getFieldName(descriptor));
	}

	public Map<String, String> getDescriptorParams(Map<String, Object> conditionParams) {
		if (conditionParams != null && conditionParams.containsKey(FIELD_NAME)) {
			return MapBuilder.build(FIELD_NAME, extractSingleParam(conditionParams, FIELD_NAME));
		}

		// Create a 'hard coded' parameter
		return MapBuilder.emptyMap();
	}

	private String getFieldName(AbstractDescriptor descriptor) {
		if (!(descriptor instanceof ValidatorDescriptor)) {
			throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
		}

		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;

		String field = (String) validatorDescriptor.getArgs().get(FIELD_NAME);
		if (field != null && field.trim().length() > 0)
			return field;
		else
			return NOT_DEFINED;
	}

	private Collection<CustomField> getCFFields() {
		List<CustomField> customFields = customFieldManager.getCustomFieldObjects();
		return customFields;
	}
}
