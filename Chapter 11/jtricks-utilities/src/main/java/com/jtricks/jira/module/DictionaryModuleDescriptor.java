package com.jtricks.jira.module;

import javax.inject.Inject;
import javax.inject.Named;

import org.dom4j.Element;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Named
public class DictionaryModuleDescriptor extends AbstractModuleDescriptor<Dictionary> {

	private String language;

	@Inject
	public DictionaryModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
		super(moduleFactory);
	}

	@Override
	public void init(Plugin plugin, Element element) throws PluginParseException {
		super.init(plugin, element);
		language = element.attributeValue("lang");
	}

	@Override
	public Dictionary getModule() {
		return moduleFactory.createModule(moduleClassName, this);
	}

	public String getLanguage() {
		return language;
	}
}
