package com.jtricks.jira.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.jtricks.utilities.NumberUtility;

public class MathAction extends JiraWebActionSupport {
	
	private int sum;
	
	@Override
	public String doDefault() throws Exception {
		sum = NumberUtility.add(2, 3);
		return super.doDefault();
	}

	public int getSum() {
		return sum;
	}

}
