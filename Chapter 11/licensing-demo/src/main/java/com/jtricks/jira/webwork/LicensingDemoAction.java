package com.jtricks.jira.webwork;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.PluginLicense;

@Named
public class LicensingDemoAction extends JiraWebActionSupport {

	private boolean licensed;
	private String licenseError;

	private final PluginLicenseManager pluginLicenseManager;

	@Inject
	public LicensingDemoAction(@ComponentImport PluginLicenseManager pluginLicenseManager) {
		this.pluginLicenseManager = pluginLicenseManager;
	}

	@Override
	public String doDefault() throws Exception {
		try {
			// Check if there is a license installed
			if (pluginLicenseManager.getLicense().isDefined()) {
				PluginLicense pluginLicense = this.pluginLicenseManager.getLicense().get();
				// Check if the installed license has an error
				if (pluginLicense.getError().isDefined()) {
					// An invalid license installed. (e.g. expired or user count mismatch)
					licensed = false;
					licenseError = pluginLicense.getError().get().name();
				} else {
					// A valid license installed.
					licensed = true;
				}
			} else {
				// No license installed
				licensed = false;
				licenseError = "No license installed";
			}
		} catch (Exception e) {
			// Any unexpected license related error
			licensed = false;
			licenseError = "Error retrieving license:" + e.getMessage();
			e.printStackTrace();
		}
		return super.doDefault();
	}

	public boolean isLicensed() {
		return licensed;
	}

	public String getLicenseError() {
		return licenseError;
	}
}
