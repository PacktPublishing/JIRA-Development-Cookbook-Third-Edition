package com.jtricks.jira.webwork;

import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class TranslateAction extends JiraWebActionSupport {

	private final I18nHelper i18nHelper;

	public TranslateAction(I18nHelper i18nHelper) {
		super();
		this.i18nHelper = i18nHelper;
	}

	@Override
	public String doDefault() throws Exception {
		System.out.println("Translated text:" + this.i18nHelper.getText("welcome.demo.message"));
		return super.doDefault();
	}

}
