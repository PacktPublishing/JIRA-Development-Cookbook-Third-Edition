package com.jtricks.web.action;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.jtricks.entity.AddressEntity;

import net.java.ao.Query;

@Named
public class ManageActiveObjects extends JiraWebActionSupport {

	private ActiveObjects ao;

	@Inject
	public ManageActiveObjects(@ComponentImport ActiveObjects ao) {
		this.ao = ao;
	}

	@Override
	protected String doExecute() throws Exception {
		System.out.println("Creating entity");
		addAddress("Jobin Kuruvilla", "VA", "USA");

		System.out.println("Getting user details");
		AddressEntity addressEntity = getAddress("Jobin Kuruvilla");
		System.out.println("Name:" + addressEntity.getName() + ", State:" + addressEntity.getState() + ", Country:"
				+ addressEntity.getCountry());

		System.out.println("Editing State...");
		editState(addressEntity, "Virginia");

		//System.out.println("Deleting...");
		//deleteAddress(addressEntity);

		System.out.println("\n... And we are done!");
		return SUCCESS;
	}

	private void deleteAddress(AddressEntity address) {
		ao.delete(address);
	}

	private void editState(AddressEntity address, String newState) {
		address.setState(newState);
		address.save();
	}

	private AddressEntity getAddress(String name) {
		AddressEntity[] addressEntities = ao.find(AddressEntity.class, Query.select().where("name = ?", name));
		return addressEntities[0];
	}

	private void addAddress(String name, String state, String country) {
		AddressEntity addressEntity = ao.create(AddressEntity.class);
		addressEntity.setName(name);
		addressEntity.setState(state);
		addressEntity.setCountry(country);
		addressEntity.save();
	}

}
