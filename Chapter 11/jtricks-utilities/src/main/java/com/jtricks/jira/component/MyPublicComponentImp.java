package com.jtricks.jira.component;

import org.springframework.stereotype.Component;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;

@ExportAsService
@Component
public class MyPublicComponentImp implements MyPublicComponent {

	@Override
	public void doSomething() {
		System.out.println("Do something inside the public component!");
	}

}
