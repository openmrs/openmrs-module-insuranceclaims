package org.openmrs.module.insuranceclaims.api.service.impl;

import ca.uhn.fhir.util.DateUtils;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicyStatus;
import org.openmrs.module.insuranceclaims.api.service.InsurancePolicyService;

import java.util.Calendar;
import java.util.Date;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONTRACT_DATE_PATTERN;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONTRACT_EXPIRE_DATE_ORDINAL;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONTRACT_POLICY_ID_ORDINAL;

public class InsurancePolicyServiceImpl extends BaseOpenmrsDataService<InsurancePolicy> implements InsurancePolicyService {

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

    @Override
    public String getPolicyIdFromContractReference(Reference contract) {
        return getElementFromContract(contract.getReference(), CONTRACT_POLICY_ID_ORDINAL);
    }

    @Override
    public Date getExpireDateFromContractReference(Reference contract) {
        String dateString = getElementFromContract(contract.getReference(), CONTRACT_EXPIRE_DATE_ORDINAL);
        String[] patterns = CONTRACT_DATE_PATTERN.toArray(new String[0]);
        return DateUtils.parseDate(dateString,patterns);
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
        Date today = Calendar.getInstance().getTime();
        return policyExpireDate.after(today);
    }
}
