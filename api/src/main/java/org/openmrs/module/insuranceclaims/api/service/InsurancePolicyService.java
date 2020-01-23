package org.openmrs.module.insuranceclaims.api.service;

import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.dto.InsurancePolicyDTO;

import java.util.Date;
import java.util.List;

public interface InsurancePolicyService extends OpenmrsDataService<InsurancePolicy> {

    InsurancePolicy generateInsurancePolicy(EligibilityResponse response) throws FHIRException;

    String getPolicyIdFromContractReference(Reference contract);

    Date getExpireDateFromContractReference(Reference contract);

    /**
     * Returns the list of {@link InsurancePolicy} for specific person.
     * The results are sorted descending by dateCreated.
     *
     * @param personUuid - value of person UUID
     * @return - list of {@link InsurancePolicy}
     */
    List<InsurancePolicyDTO> getForPerson(String personUuid);

    /**
     * Creates od updates the {@link InsurancePolicy} for specific person.
     * Uses the person UUID and policy number to fetch existing policy.
     *
     * @param personUuid - value of person UUID
     * @param policy - fetched value of policy
     * @return - {@link InsurancePolicy}
     */
    List<InsurancePolicyDTO> addOrUpdatePolicy(String personUuid, InsurancePolicy policy);
}
