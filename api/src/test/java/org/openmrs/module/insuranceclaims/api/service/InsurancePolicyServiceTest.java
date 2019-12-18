package org.openmrs.module.insuranceclaims.api.service;

import ca.uhn.fhir.util.DateUtils;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.mother.InsurancePolicyMother;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.openmrs.module.insuranceclaims.api.util.TestConstants.TEST_PATIENT_POLICY_NUMBER;

public class InsurancePolicyServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    InsurancePolicyService insurancePolicyService;

    @Test
    public void generateInsurancePolicy_shouldMapEligibilityResponseToPolicy() {
        EligibilityResponse eligibilityResponse = createTestEligibilityResponse();

        InsurancePolicy expected = createTestPolicyInstance();
        expected.setExpiryDate(DateUtils.parseDate(TestConstants.TEST_DATE, new String[]{"yyy-mm-dd hh:mm:ss"}));
        InsurancePolicy actual = insurancePolicyService.generateInsurancePolicy(eligibilityResponse);

        Assert.assertThat(actual.getExpiryDate(), Matchers.equalTo(expected.getExpiryDate()));
        Assert.assertThat(actual.getPolicyNumber(), Matchers.equalTo(expected.getPolicyNumber()));
    }

    @Test
    public void getPolicyIdFromContractReference_shouldReturnProperId() {
        EligibilityResponse eligibilityResponse = createTestEligibilityResponse();

        String actual = insurancePolicyService.getPolicyIdFromContractReference(
                eligibilityResponse.getInsuranceFirstRep().getContract()
        );

        Assert.assertThat(TEST_PATIENT_POLICY_NUMBER, Matchers.equalTo(actual));
    }

    private InsurancePolicy createTestPolicyInstance() {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
        return InsurancePolicyMother.createTestInstance(location, identifierType);
    }

    private EligibilityResponse createTestEligibilityResponse() {
        EligibilityResponse response = new EligibilityResponse();
        EligibilityResponse.InsuranceComponent insuranceComponent = new EligibilityResponse.InsuranceComponent();
        insuranceComponent.setContract(createTestContract());
        response.setInsurance(Collections.singletonList(insuranceComponent));
        return response;
    }

    private Reference createTestContract() {
        String referenceString =  InsuranceClaimConstants.CONTRACT+ "/"
                + TEST_PATIENT_POLICY_NUMBER + "/"
                + TestConstants.TEST_DATE;

        return new Reference(referenceString);
    }
}
