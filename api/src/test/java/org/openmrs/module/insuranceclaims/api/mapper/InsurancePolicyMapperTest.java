package org.openmrs.module.insuranceclaims.api.mapper;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicyStatus;
import org.openmrs.module.insuranceclaims.api.model.dto.InsurancePolicyDTO;
import org.openmrs.module.insuranceclaims.api.mother.InsurancePolicyMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class InsurancePolicyMapperTest extends BaseModuleContextSensitiveTest {

    private static final String FIRST_POLICY_NUMBER = TestConstants.TEST_PATIENT_POLICY_NUMBER;

    private static final String SECOND_POLICY_NUMBER = "b6edd7a2-eb03-4b08-bce6-444b45a55486";

    private static final int EXPECTED_SIZE = 2;

    @Autowired
    @Qualifier("insuranceclaims.insurancePolicyMapper")
    private InsurancePolicyMapper insurancePolicyMapper;

    @Test
    public void toDto_shouldMapAsExpected() {
        InsurancePolicyDTO actual = insurancePolicyMapper.toDto(createTestPolicyInstance(FIRST_POLICY_NUMBER));
        assertPolicy(actual, FIRST_POLICY_NUMBER);
    }

    @Test
    public void toDtos_shouldMapAsExpected() {
        List<InsurancePolicyDTO> actual = insurancePolicyMapper.toDtos(
                Arrays.asList(createTestPolicyInstance(FIRST_POLICY_NUMBER),
                        createTestPolicyInstance(SECOND_POLICY_NUMBER)));
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(EXPECTED_SIZE));
        assertPolicy(actual.get(0), FIRST_POLICY_NUMBER);
        assertPolicy(actual.get(1), SECOND_POLICY_NUMBER);
    }

    private void assertPolicy(InsurancePolicyDTO actual, String policyNumber) {
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getExpiryDate(), is(TestConstants.TEST_PATIENT_POLICY_EXPIRY_DATE));
        assertThat(actual.getStatus(), is(InsurancePolicyStatus.ACTIVE.name()));
        assertThat(actual.getPolicyNumber(), is(policyNumber));
        assertThat(actual.getAllowedMoney(), is(TestConstants.TEST_PATIENT_POLICY_ALLOWED_MONEY));
    }

    private InsurancePolicy createTestPolicyInstance(String policyNumber) {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
        InsurancePolicy policy = InsurancePolicyMother.createTestInstance(location, identifierType);
        policy.setPolicyNumber(policyNumber);
        return policy;
    }
}
