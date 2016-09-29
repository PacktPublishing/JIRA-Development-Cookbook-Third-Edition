package com.jtricks.jira.reports;

import java.util.LinkedHashMap;
import java.util.Map;

import com.atlassian.configurable.ValuesGenerator;

public class CascadingValuesGenerator implements ValuesGenerator<String> {

	public Map getValues(Map arg0) {
		Map allValues = new LinkedHashMap();
		
		allValues.put("One1", new ValueClassHolder("First Val1", "key1"));
		allValues.put("Two1", new ValueClassHolder("Second Val1", "key1"));
		allValues.put("Three1", new ValueClassHolder("Third Val1", "key1"));
		allValues.put("One2", new ValueClassHolder("First Val2", "key2"));
		allValues.put("Two2", new ValueClassHolder("Second Val2", "key2"));
		allValues.put("One3", new ValueClassHolder("First Val3", "key3"));
		
		return allValues;
	}

	private static class ValueClassHolder {
		private String value;
		private String className;

		public ValueClassHolder(String value, String className) {
			this.value = value;
			this.className = className;
		}

		public String getValue() {
			return value;
		}

		public String getClassName() {
			return className;
		}

		public String toString() {
			return value;
		}
	}

}
