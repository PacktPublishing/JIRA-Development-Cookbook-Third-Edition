AJS.$(document).ready(function() {

	var jtriggerPlaceholder = AJS.$("#panel-subnav-trigger");
	var jcontentPlaceholder = AJS.$("#panel-subnav-content");
	var jtitlePlaceholder = AJS.$("#panel-subnav-header");
	
	var selectedPanelId = "com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel";
	var url = window.location.href;
	if (url.indexOf("jtricksnavigationpanel1") > -1) {
		selectedPanelId = "com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel1";
	} else if (url.indexOf("jtricksnavigationpanel2") > -1) {
		selectedPanelId = "com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel2";
	} 	
	
	var jtricksnavigator = new JIRA.Projects.Subnavigator({
	    id: "jtricksPanel",                       
	    triggerPlaceholder: jtriggerPlaceholder,
	    contentPlaceholder: jcontentPlaceholder,
	    titlePlaceholder: jtitlePlaceholder,   
	    itemGroups: [
	    	[{      
		        id: "com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel1",                                 
		        label: "Panel One",
		        description: "Sample description for panel one",
		        link: AJS.contextPath() + "/projects/" + JIRA.API.Projects.getCurrentProjectKey() + "?selectedItem=com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel1"
	    	}, 
	    	{      
		        id: "com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel2",                                 
		        label: "Panel Two",
		        description: "Sample description for panel two",
		        link: AJS.contextPath() + "/projects/" + JIRA.API.Projects.getCurrentProjectKey() + "?selectedItem=com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel2"
	    	}],
	    	[{      
		        id: "com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel",                                 
		        label: "All Panels",
		        description: "Sample description for panels",
		        link: AJS.contextPath() + "/projects/" + JIRA.API.Projects.getCurrentProjectKey() + "?selectedItem=com.atlassian.jira.jira-projects-plugin:jtricksnavigationpanel"
	    	}]
	    ],
	    selectedItem: selectedPanelId,
	    changeViewText: "Select Panel",
	    hideSelectedItem: false
	});
	
	AJS.$("#subnav-trigger-jtricksPanel").removeClass("subnav-trigger");
	jtricksnavigator.show();
});