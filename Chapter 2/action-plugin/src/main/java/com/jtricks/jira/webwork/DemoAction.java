package com.jtricks.jira.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;

public class DemoAction extends JiraWebActionSupport {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(DemoAction.class);

	private String userName;
	private String modifiedName;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String doDefault() throws Exception {
		log.info("Preparing to recieve inputs");
		return INPUT;
	}

	@Override
	@RequiresXsrfCheck
	protected String doExecute() throws Exception {
		log.info("The user Name I got from input view:" + userName);
		if (TextUtils.stringSet(userName)) {
			this.modifiedName = "Hi, " + userName;
			return SUCCESS;
		} else {
			return ERROR;
		}
	}

	public String getModifiedName() {
		return modifiedName;
	}
}
