package com.jtricks.jira.webwork;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.jtricks.jira.component.MyPrivateComponent;
import com.jtricks.jira.component.MyPublicComponent;

@Named
public class RedirectAction extends JiraWebActionSupport {

	private final MyPrivateComponent myPrivateComponent;
	private final JiraAuthenticationContext authenticationContext;
	private final MyPublicComponent myPublicComponent;

	@Inject
	public RedirectAction(MyPrivateComponent myPrivateComponent,
			@ComponentImport JiraAuthenticationContext authenticationContext, MyPublicComponent myPublicComponent) {
		super();
		this.myPrivateComponent = myPrivateComponent;
		this.authenticationContext = authenticationContext;
		this.myPublicComponent = myPublicComponent;
	}

	@Override
	protected String doExecute() throws Exception {
		System.out.println("Action invoked. Doing something important before redirecting to Dashboard!");
		this.myPrivateComponent.doSomething();
		this.myPublicComponent.doSomething();
		ApplicationUser loggedInUser = this.authenticationContext.getLoggedInUser();
		System.out.println("Current User:" + (loggedInUser == null ? "Anonymous" : loggedInUser.getDisplayName()));
		return getRedirect("/secure/Dashboard.jspa");
	}

}
