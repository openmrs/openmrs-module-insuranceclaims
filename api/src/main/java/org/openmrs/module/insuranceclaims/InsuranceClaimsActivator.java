package org.openmrs.module.insuranceclaims;

import org.openmrs.module.BaseModuleActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the logic that is run every time this module is either started or stopped
 */
public class InsuranceClaimsActivator extends BaseModuleActivator {

	private static final Logger LOG = LoggerFactory.getLogger(InsuranceClaimsActivator.class);

	/**
	 * @see #started()
	 */
	@Override
	public void started() {
		LOG.info("Started Insurance Claims");
	}

	/**
	 * @see #stopped()
	 */
	@Override
	public void stopped() {
		LOG.info("Stopped Insurance Claims");
	}

}
