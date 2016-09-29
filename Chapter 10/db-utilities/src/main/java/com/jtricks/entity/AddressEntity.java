package com.jtricks.entity;

import net.java.ao.Entity;

public interface AddressEntity extends Entity {
	
	public String getName();

	public void setName(String name);

	public String getState();

	public void setState(String state);

	public String getCountry();

	public void setCountry(String country);
}
