package com.jtricks.jira.listeners;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@ExportAsService({ JTricksListener.class })
@Named("jtricksListener")
public class JTricksListener implements InitializingBean, DisposableBean {

	private final EventPublisher eventPublisher;

	@Inject
	public JTricksListener(@ComponentImport EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@EventListener
	public void onIssueEvent(IssueEvent issueEvent) {
		Long eventTypeId = issueEvent.getEventTypeId();
		Issue issue = issueEvent.getIssue();

		if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
			System.out.println("Created issue:" + issue.getKey());
		} else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
			System.out.println("Resolved issue:" + issue.getKey());
		} else {
			System.out.println("Event:" + issueEvent.getEventTypeId() + " thrown on issue:" + issue.getKey());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
	}

	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

}
