package org.openmrs.module.insuranceclaims.api.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.util.PrivilegeConstants;

/**
 * The InsuranceClaim service which is exposed for other modules.
 * See moduleApplicationContext.xml on how it is wired up.
 */
public interface InsuranceClaimService extends OpenmrsService {

	/**
	 * Returns an InsuranceClaim by id. It can be called by any authenticated user. It is fetched in read
	 * only transaction.
	 *
	 * @param id - value of id
	 * @return fetched InsuranceClaim
	 * @throws APIException - service exception
	 */
	@Authorized()
	InsuranceClaim getInsuranceClaimById(Integer id) throws APIException;

	/**
	 * Returns an InsuranceClaim by uuid. It can be called by any authenticated user. It is fetched in read
	 * only transaction.
	 *
	 * @param uuid - value of uuid
	 * @return fetched InsuranceClaim
	 * @throws APIException - service exception
	 */
	@Authorized()
	InsuranceClaim getInsuranceClaimByUuid(String uuid) throws APIException;

	/**
	 * Saves an InsuranceClaim. It can be called by users with
	 * this module's privilege. It is executed in a transaction.
	 *
	 * @param claim - InsuranceClaim representation
	 * @return saved InsuranceClaim
	 * @throws APIException - service exception
	 */
	@Authorized(PrivilegeConstants.MODULE_PRIVILEGE)
	InsuranceClaim saveInsuranceClaim(InsuranceClaim claim) throws APIException;
}
