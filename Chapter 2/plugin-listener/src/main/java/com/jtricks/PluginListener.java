package com.jtricks;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@ExportAsService ({PluginListener.class})
@Named ("pluginListener")
public class PluginListener implements InitializingBean, DisposableBean {

	private static final String TEST_TEXT_CF = "Test Text CF";
	
	@ComponentImport
	private final CustomFieldManager customFieldManager;
	
	@ComponentImport
	private final FieldScreenManager fieldScreenManager;

	@Inject
	public PluginListener(CustomFieldManager customFieldManager, FieldScreenManager fieldScreenManager) {
		this.customFieldManager = customFieldManager;
		this.fieldScreenManager = fieldScreenManager;
	}

	@Override
	public void destroy() throws Exception {
		//Get the already installed custom field by name
		CustomField cField = this.customFieldManager.getCustomFieldObjectByName(TEST_TEXT_CF);
		//Remove if not null
		if (cField != null) {
			this.customFieldManager.removeCustomField(cField);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//Create a list of issue types for which the custom field needs to be available
		List<IssueType> issueTypes = new ArrayList<IssueType>();
		issueTypes.add(null);

		//Create a list of project contexts for which the custom field needs to be available
		List<JiraContextNode> contexts = new ArrayList<JiraContextNode>();
		contexts.add(GlobalIssueContext.getInstance());

		//Add custom field
		CustomField cField = this.customFieldManager.createCustomField(TEST_TEXT_CF, "A Sample Text Field",
				this.customFieldManager
						.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:textfield"),
				this.customFieldManager
						.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:textsearcher"),
				contexts, issueTypes);

		// Add field to default Screen
		FieldScreen defaultScreen = fieldScreenManager.getFieldScreen(FieldScreen.DEFAULT_SCREEN_ID);
		if (!defaultScreen.containsField(cField.getId())) {
			FieldScreenTab firstTab = defaultScreen.getTab(0);
			firstTab.addFieldScreenLayoutItem(cField.getId());
		}
	}
}
