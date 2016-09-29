package com.jtricks.jira.services;

import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.service.AbstractService;
import com.opensymphony.module.propertyset.PropertySet;

public class JTricksService extends AbstractService {
	
	public static final String TUTORIAL = "Tutorial";

	private String tutorial;
	
	@Override
	public void init(PropertySet props) throws ObjectConfigurationException {
		super.init(props);

		if (hasProperty(TUTORIAL)) {
			tutorial = getProperty(TUTORIAL);
		} else {
			tutorial = "I don't like tutorials!";
		}
	}

	@Override
	public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException {
		return getObjectConfiguration("MYNEWSERVICE", "com/jtricks/services/myjtricksservice.xml", null);
	}

	@Override
	public void run() {
		System.out.println("Running the JTricks service!! Tutorial? " + tutorial);
	}
	
	@Override
	public void destroy() {
		System.out.println("Let me do this before destory!");
		super.destroy();
	}

}
