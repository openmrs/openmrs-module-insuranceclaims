package org.openmrs.module.insuranceclaims.api.service.fhir;

import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.mother.InsurancePolicyMother;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.insuranceclaims.api.util.TestConstants.TEST_PATIENT_POLICY_NUMBER;

public class FHIREligibilityServiceImpl extends BaseModuleContextSensitiveTest {

    @Autowired
    private FHIREligibilityService fhirEligibilityService;

    @Test
    public void generateEligibilityRequest_shouldMapPatientToEligibilityRequest() {
        EligibilityRequest test = fhirEligibilityService.generateEligibilityRequest(TEST_PATIENT_POLICY_NUMBER);

        Reference generatedPatientReference = test.getPatient();
        Reference expectedReference = getExpectedReference();

        Assert.assertThat(generatedPatientReference, Matchers.samePropertyValuesAs(expectedReference));
    }

    @Test
    public void generateEligibilityRequest_shouldMapEligibilityResponseToPolicy() {
        EligibilityResponse test = createTestEligibilityResponse();

        InsurancePolicy expected = createTestPolicyInstance();
        InsurancePolicy actual = fhirEligibilityService.generateInsurancePolicy(test);

        Assert.assertThat(actual.getExpiryDate(), Matchers.equalTo(expected.getExpiryDate()));
        Assert.assertThat(actual.getPolicyNumber(), Matchers.equalTo(expected.getPolicyNumber()));
    }

    @Test
    public void getPolicyIdFromContractReference_shouldReturnProperId() {
        EligibilityResponse test = createTestEligibilityResponse();

        String actual = fhirEligibilityService.getPolicyIdFromContractReference(
                test.getInsuranceFirstRep().getContract()
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

        return response;
    }

    private Reference createTestContract() {
        String referenceString =  InsuranceClaimConstants.CONTRACT+ "/"
                + TEST_PATIENT_POLICY_NUMBER + "/"
                + TestConstants.TEST_DATE;

        return new Reference(referenceString);
    }
    private Reference getExpectedReference() {
        Reference expected = new Reference();

        expected.setReference(FHIRConstants.PATIENT + "/" + TEST_PATIENT_POLICY_NUMBER);
        return expected;
    }
}
