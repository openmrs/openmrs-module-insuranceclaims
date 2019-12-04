package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import ca.uhn.fhir.util.DateUtils;
import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicyStatus;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIREligibilityService;

import java.util.Date;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.buildReference;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONTRACT_EXPIRE_DATE_ORDINAL;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONTRACT_POLICY_ID_ORDINAL;

public class FHIREligibilityServiceImpl implements FHIREligibilityService {

    @Override
    public EligibilityRequest generateEligibilityRequest(String  policyId) {
        EligibilityRequest request = new EligibilityRequest();
        Reference patient = buildReference(FHIRConstants.PATIENT, policyId);
        request.setPatient(patient);

        return request;
    }

    @Override
    public InsurancePolicy generateInsurancePolicy(EligibilityResponse response) {
        Reference contract = response.getInsuranceFirstRep().getContract();
        InsurancePolicy policy = new InsurancePolicy();

        Date expireDate = getExpireDateFromContractReference(contract);
        policy.setExpiryDate(expireDate);
        policy.setStatus(getPolicyStatus(policy));
        policy.setPolicyNumber(getPolicyIdFromContractReference(contract));

        return policy;
    }

    public String getPolicyIdFromContractReference(Reference contract) {
        return getElementFromContract(contract.getReference(), CONTRACT_POLICY_ID_ORDINAL);
    }

    public Date getExpireDateFromContractReference(Reference contract) {
        String  dateString = getElementFromContract(contract.getReference(), CONTRACT_EXPIRE_DATE_ORDINAL);
        return DateUtils.parseDate(dateString);
    }

    private String getElementFromContract(String contract, int ordinal) {
        String[] contractParts = splitContract(contract);
        return contractParts[ordinal];
    }

    private String[] splitContract(String contract) {
        //Contract should have format: "Contract/policyId/expireDate
        return contract.split("/");
    }

    private InsurancePolicyStatus getPolicyStatus(InsurancePolicy policy) {
        Date policyExpireDate = policy.getExpiryDate();
        return isPolicyActive(policyExpireDate) ? InsurancePolicyStatus.ACTIVE : InsurancePolicyStatus.EXPIRED;
    }

    private boolean isPolicyActive(Date policyExpireDate) {
        return policyExpireDate.after(new Date());
    }
}