package com.jtricks.jira.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.core.entity.EntityExpr;
import org.ofbiz.core.entity.EntityFindOptions;
import org.ofbiz.core.entity.EntityOperator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.util.UtilMisc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.ofbiz.OfBizListIterator;
import com.atlassian.jira.util.collect.MapBuilder;

public class DelegatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private OfBizDelegator delegator;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		delegator = ComponentAccessor.getOfBizDelegator();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();

		out.println("Creating Employee records..");

		Map<String, Object> employeeDetails1 = new HashMap<String, Object>();
		employeeDetails1.put("name", "Some Guy1");
		employeeDetails1.put("address", "Some Address1");
		employeeDetails1.put("company", "J-Tricks");
		
		Map<String, Object> employeeDetails2 = new HashMap<String, Object>();
		employeeDetails2.put("name", "Some Guy2");
		employeeDetails2.put("address", "Some Address2");
		employeeDetails2.put("company", "Atlassian");

		GenericValue employee1 = this.delegator.createValue("Employee", employeeDetails1);
		GenericValue employee2 = this.delegator.createValue("Employee", employeeDetails2);

		out.println("Updating the record...");

		employee2.setString("name", "Awesome Guy");
		try {
			employee2.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.println("Get all Employee records...");
		List<GenericValue> employees = delegator.findAll("Employee");
		for (GenericValue employee : employees) {
			out.println("Name:" + employee.getString("name") + ",Company:" + employee.getString("company"));
		}

		out.println("Get employees in J-Tricks...");
		employees = delegator.findByAnd("Employee", MapBuilder.build("company", "J-Tricks"));
		for (GenericValue employee : employees) {
			out.println("Name:" + employee.getString("name"));
		}

		out.println("Get employees in J-Tricks where ID less than 15000...");
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("id");
		fieldList.add("name");
		employees = this.delegator.findByCondition("Employee", new EntityExpr("id", EntityOperator.LESS_THAN, "15000"),
				fieldList);
		for (GenericValue employee : employees) {
			out.println("Name:" + employee.getString("name"));
		}

		EntityFindOptions entityFindOptions = new EntityFindOptions();
		entityFindOptions.scrollInsensitive();
		entityFindOptions.setResultSetConcurrency(ResultSet.CONCUR_READ_ONLY);
		entityFindOptions.setDistinct(true);

		out.println("Get sorted list of employees where ID less than 15000...");
		OfBizListIterator iterator = this.delegator.findListIteratorByCondition("Employee",
				new EntityExpr("id", EntityOperator.LESS_THAN, "15000"), null, UtilMisc.toList("name"),
				UtilMisc.toList("name"), entityFindOptions);
		employees = iterator.getCompleteList();
		iterator.close();
		for (GenericValue employee : employees) {
			out.println("Name:" + employee.getString("name"));
		}

		out.println("\n... And we are done!");
	}
}
