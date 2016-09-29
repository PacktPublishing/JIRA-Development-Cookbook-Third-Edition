package com.jtricks.jira.dictionary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugin.PluginAccessor;
import com.jtricks.jira.module.DictionaryModuleDescriptor;

public class DictionaryServlet extends HttpServlet {

	private PluginAccessor pluginAccessor;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pluginAccessor = ComponentAccessor.getPluginAccessor();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();

		out.println("<br>Invoking the servlet...");

		Map<String, String> results = getJIRADescriptions();
		Set<String> keys = results.keySet();
		for (String key : keys) {
			out.println("<br>JIRA in " + key + ":" + results.get(key));
		}

		out.println("<br>For uk-english JIRA means:" + getJIRADescription("uk-english"));

		out.println("<br>Done!");
	}

	private Map<String, String> getJIRADescriptions() {
		// To get all the enabled modules of this module descriptor
		List<DictionaryModuleDescriptor> dictionaryModuleDescriptors = pluginAccessor
				.getEnabledModuleDescriptorsByClass(DictionaryModuleDescriptor.class);
		// Now we'll use each one to get a map of languages to translations of
		// the word "JIRA"
		Map<String, String> results = new HashMap<String, String>();
		for (DictionaryModuleDescriptor dictionaryModuleDescriptor : dictionaryModuleDescriptors) {
			results.put(dictionaryModuleDescriptor.getLanguage(),
					dictionaryModuleDescriptor.getModule().getDefinition("JIRA"));
		}
		return results;
	}

	private String getJIRADescription(String key) {
		// To get all the enabled modules of this module descriptor
		List<DictionaryModuleDescriptor> dictionaryModuleDescriptors = pluginAccessor
				.getEnabledModuleDescriptorsByClass(DictionaryModuleDescriptor.class);
		for (DictionaryModuleDescriptor dictionaryModuleDescriptor : dictionaryModuleDescriptors) {
			if (dictionaryModuleDescriptor.getLanguage().equals(key)) {
				return dictionaryModuleDescriptor.getModule().getDefinition("JIRA");
			}
		}
		return "Not Found";
	}

}