package com.jtricks.jira.module;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ModuleType;

@ModuleType(ListableModuleDescriptorFactory.class)
@Component
public class DictionaryModuleTypeFactory extends SingleModuleDescriptorFactory<DictionaryModuleDescriptor> {

	@Inject
	public DictionaryModuleTypeFactory(HostContainer hostContainer) {
		super(hostContainer, "dictionary", DictionaryModuleDescriptor.class);
	}

}
