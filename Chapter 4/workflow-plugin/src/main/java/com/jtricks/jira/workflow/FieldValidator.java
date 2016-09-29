package com.jtricks.jira.workflow;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;

@Scanned
public class FieldValidator implements Validator {
	private static final Logger log = LoggerFactory.getLogger(FieldValidator.class);
	private final CustomFieldManager customFieldManager;

	private static final String FIELD_NAME = "field";

	public FieldValidator(@ComponentImport CustomFieldManager customFieldManager) {
		this.customFieldManager = customFieldManager;
	}

	public void validate(Map transientVars, Map args, PropertySet ps) throws InvalidInputException {
		Issue issue = (Issue) transientVars.get("issue");
		String field = (String) args.get(FIELD_NAME);	
		
		CustomField customField = customFieldManager.getCustomFieldObjectByName(field);
		
		if (customField!=null){
			//Check if the custom field value is NULL
			if (issue.getCustomFieldValue(customField) == null){
				throw new InvalidInputException("The field:"+field+" is required!");
			}
		}
	}
}
