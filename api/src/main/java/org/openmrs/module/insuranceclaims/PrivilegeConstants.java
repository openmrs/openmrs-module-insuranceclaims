package org.openmrs.module.insuranceclaims;

import org.openmrs.annotation.AddOnStartup;
import org.openmrs.annotation.HasAddOnStartupPrivileges;

/**
 * Contains module's privilege constants.
 */
@HasAddOnStartupPrivileges
public final class PrivilegeConstants {

	@AddOnStartup(description = "Allows user to access Insurance Claims module pages/functions")
	public static final String MODULE_PRIVILEGE = "Insurance Claims Privilege";

	private PrivilegeConstants() {
	}
}
