package org.openmrs.module.insuranceclaims.api.service.impl;

import ca.uhn.fhir.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.NullPrecedence;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.api.ValidationException;
import org.openmrs.module.insuranceclaims.api.mapper.InsurancePolicyMapper;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicyStatus;
import org.openmrs.module.insuranceclaims.api.model.dto.InsurancePolicyDTO;
import org.openmrs.module.insuranceclaims.api.service.InsurancePolicyService;
import org.openmrs.module.insuranceclaims.util.ConstantValues;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONTRACT_DATE_PATTERN;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONTRACT_EXPIRE_DATE_ORDINAL;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.CONTRACT_POLICY_ID_ORDINAL;

public class InsurancePolicyServiceImpl extends BaseOpenmrsDataService<InsurancePolicy> implements InsurancePolicyService {

    private PersonService personService;

    private InsurancePolicyMapper insurancePolicyMapper;

    @Override
    public InsurancePolicy generateInsurancePolicy(EligibilityResponse response) throws FHIRException {
        Reference contract = response.getInsuranceFirstRep().getContract();
        InsurancePolicy policy = new InsurancePolicy();
        Date expireDate = getExpireDateFromContractReference(contract);

        policy.setExpiryDate(expireDate);
        policy.setStatus(getPolicyStatus(policy));
        policy.setPolicyNumber(getPolicyIdFromContractReference(contract));

        policy.setUsedMoney(getUsedMoney(response));
        policy.setAllowedMoney(getAllowedMoney(response));

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

    @Override
    public List<InsurancePolicyDTO> getForPerson(String personUuid) {
        if (StringUtils.isBlank(personUuid)) {
            throw new ValidationException(String.format("Wrong value of personUuid: %s", personUuid));
        }
        Criteria criteria = createOrderedQuery(personUuid);
        return insurancePolicyMapper.toDtos(getAllByCriteria(criteria, false));
    }

    @Override
    public List<InsurancePolicyDTO> addOrUpdatePolicy(String personUuid, InsurancePolicy policy) {
        if (StringUtils.isBlank(personUuid) || policy == null) {
            throw new ValidationException(String.format("Wrong value of personUuid: %s or InsurancePolicy %s",
                    personUuid, policy));
        }
        InsurancePolicy actualPolicy = getByPatientAndPolicyNumber(personUuid, policy.getPolicyNumber());
        Person person = personService.getPersonByUuid(personUuid);
        updatePersonAttributes(person, policy);
        assignPersonToPolicyIfMissing(policy, person);
        saveOrUpdate(updatePolicyAttributes(actualPolicy, policy));
        return getForPerson(personUuid);
    }

    private void assignPersonToPolicyIfMissing(InsurancePolicy policy, Person person) {
        if (policy.getPatient() == null && person instanceof Patient) {
            policy.setPatient((Patient) person);
        }
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setInsurancePolicyMapper(
            InsurancePolicyMapper insurancePolicyMapper) {
        this.insurancePolicyMapper = insurancePolicyMapper;
    }

    /**
     * Updates only the fields which should be changed but if actual policy does not exit then newPolicy will be returned
     * @param oldPolicy - the actual value of policy
     * @param newPolicy - the new value of policy
     */
    private InsurancePolicy updatePolicyAttributes(InsurancePolicy oldPolicy, InsurancePolicy newPolicy) {
        if (oldPolicy != null) {
            oldPolicy.setStartDate(newPolicy.getStartDate());
            oldPolicy.setExpiryDate(newPolicy.getExpiryDate());
            oldPolicy.setAllowedMoney(newPolicy.getAllowedMoney());
            oldPolicy.setUsedMoney(newPolicy.getUsedMoney());
            oldPolicy.setStatus(newPolicy.getStatus());
            return oldPolicy;
        } else {
            return newPolicy;
        }
    }

    private PersonAttribute createInsuranceNumberAttribute(String policyNumber) {
        return new PersonAttribute(
                personService.getPersonAttributeTypeByName(ConstantValues.POLICY_NUMBER_ATTRIBUTE_TYPE_NAME),
                policyNumber);
    }

    private Criteria createOrderedQuery(String personUuid) {
        Criteria criteria = createCriteria();
        criteria.createAlias("patient", "p")
                .add(Restrictions.eq("p.uuid", personUuid))
                .addOrder(Order.desc("dateCreated"))
                .addOrder(Order.desc("dateChanged").nulls(NullPrecedence.LAST));
        return criteria;
    }

    private InsurancePolicy getByPatientAndPolicyNumber(String personUuid, String policyNumber) {
        Criteria criteria = createCriteria();
        criteria.createAlias("patient", "p")
                .add(Restrictions.eq("p.uuid", personUuid))
                .add(Restrictions.eq("policyNumber", policyNumber));
        return getByCriteria(criteria, false);
    }

    private void updatePersonAttributes(Person person, InsurancePolicy policy) {
        person.addAttribute(createInsuranceNumberAttribute(policy.getPolicyNumber()));
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

    private BigDecimal getAllowedMoney(EligibilityResponse response) throws FHIRException {
        EligibilityResponse.BenefitsComponent benefit = response.getInsuranceFirstRep().getBenefitBalanceFirstRep();
        Money allowedMoney = benefit.getFinancialFirstRep().getAllowedMoney();
        return allowedMoney.getValue();
    }

    private BigDecimal getUsedMoney(EligibilityResponse response) throws FHIRException {
        EligibilityResponse.BenefitsComponent benefit = response.getInsuranceFirstRep().getBenefitBalanceFirstRep();
        Money usedMoney = benefit.getFinancialFirstRep().getUsedMoney();
        return usedMoney.getValue();
    }
}
