package com.jtricks.jira.component;

import org.springframework.stereotype.Component;

@Component
public class MyPrivateComponentImp implements MyPrivateComponent {

	@Override
	public void doSomething() {
		System.out.println("Do something inside the private component!");
	}

}
