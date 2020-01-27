package org.openmrs.module.insuranceclaims.api.service;

import ca.uhn.fhir.util.DateUtils;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.EligibilityResponse;
import org.hl7.fhir.dstu3.model.Money;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.dto.InsurancePolicyDTO;
import org.openmrs.module.insuranceclaims.api.mother.InsurancePolicyMother;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.module.insuranceclaims.util.ConstantValues;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.TEST_PATIENT_POLICY_NUMBER;

public class InsurancePolicyServiceTest extends BaseModuleContextSensitiveTest {

    private static final String XML_DATA_SET = "test_insurance_policy.xml";

    private static final BigDecimal ALLOWED_MONEY = new BigDecimal("100.00");

    private static final BigDecimal USED_MONEY = new BigDecimal("90.00");

    private static final String PERSON_UUID = "41c2283e-549e-4e0e-acdb-3540040effef";

    private static final int EXPECTED_SIZE = 3;

    private static final String FIRST_POLICY_NUMBER = "f1211f8a-7496-4556-bdd1-c0f555b88755";

    private static final String SECOND_POLICY_NUMBER = "c949ba67-454b-4b3c-8f01-01390e4f8599";

    private static final String THIRD_POLICY_NUMBER = "25540bd0-8888-4e06-9cf7-215042cdb033";

    @Autowired
    @Qualifier("insuranceclaims.InsurancePolicyService")
    private InsurancePolicyService insurancePolicyService;

    @Autowired
    private PersonService personService;

    @Before
    public void setUp() throws Exception {
        executeDataSet(XML_DATA_SET);
    }

    @Test
    public void generateInsurancePolicy_shouldMapEligibilityResponseToPolicy() throws FHIRException {
        EligibilityResponse eligibilityResponse = createTestEligibilityResponse();

        InsurancePolicy expected = createTestPolicyInstance();
        expected.setExpiryDate(DateUtils.parseDate(TestConstants.TEST_DATE, new String[]{"yyy-MM-dd hh:mm:ss"}));
        InsurancePolicy actual = insurancePolicyService.generateInsurancePolicy(eligibilityResponse);

        Assert.assertThat(actual.getExpiryDate(), Matchers.equalTo(expected.getExpiryDate()));
        Assert.assertThat(actual.getPolicyNumber(), Matchers.equalTo(expected.getPolicyNumber()));
        Assert.assertThat(actual.getAllowedMoney(), Matchers.equalTo(ALLOWED_MONEY));
        Assert.assertThat(actual.getUsedMoney(), Matchers.equalTo(USED_MONEY));
    }

    @Test
    public void getPolicyIdFromContractReference_shouldReturnProperId() {
        EligibilityResponse eligibilityResponse = createTestEligibilityResponse();

        String actual = insurancePolicyService.getPolicyIdFromContractReference(
                eligibilityResponse.getInsuranceFirstRep().getContract()
        );

        Assert.assertThat(TEST_PATIENT_POLICY_NUMBER, Matchers.equalTo(actual));
    }

    @Test
    public void getForPerson_shouldReturnExpectedResultInRightOrder() {
        List<InsurancePolicyDTO> actual = insurancePolicyService.getForPerson(PERSON_UUID);
        Assert.assertThat(actual, is(notNullValue()));
        Assert.assertThat(actual.size(), is(EXPECTED_SIZE));
        Assert.assertThat(actual.get(0).getPolicyNumber(), is(FIRST_POLICY_NUMBER));
        Assert.assertThat(actual.get(1).getPolicyNumber(), is(SECOND_POLICY_NUMBER));
        Assert.assertThat(actual.get(2).getPolicyNumber(), is(THIRD_POLICY_NUMBER));
    }

    @Test
    public void addOrUpdatePolicy_shouldAddNewValueAndChangeActualPolicyNumberAttribute() {
        Person person = personService.getPersonByUuid(PERSON_UUID);
        List<InsurancePolicyDTO> actual = insurancePolicyService.addOrUpdatePolicy(PERSON_UUID,
                createTestPolicyInstance(person));
        Assert.assertThat(actual, is(notNullValue()));
        Assert.assertThat(actual.get(0).getPolicyNumber(), is(TEST_PATIENT_POLICY_NUMBER));
        Assert.assertThat(person.getAttribute(ConstantValues.POLICY_NUMBER_ATTRIBUTE_TYPE_NAME).getValue(),
                is(TEST_PATIENT_POLICY_NUMBER));
    }

    private InsurancePolicy createTestPolicyInstance(Person person) {
        return InsurancePolicyMother.createTestInstance(person);
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
        insuranceComponent.setBenefitBalance(createTestBenefitCategory());
        response.setInsurance(Collections.singletonList(insuranceComponent));
        return response;
    }

    private Reference createTestContract() {
        String referenceString =  InsuranceClaimConstants.CONTRACT+ "/"
                + TEST_PATIENT_POLICY_NUMBER + "/"
                + TestConstants.TEST_DATE;

        return new Reference(referenceString);
    }

    private List<EligibilityResponse.BenefitsComponent> createTestBenefitCategory() {
        EligibilityResponse.BenefitsComponent component = new EligibilityResponse.BenefitsComponent();
        component.setFinancial(createTestFinancialComponent());
        return Collections.singletonList(component);
    }

    public List<EligibilityResponse.BenefitComponent> createTestFinancialComponent() {
        EligibilityResponse.BenefitComponent  financial = new EligibilityResponse.BenefitComponent();
        Money allowed = new Money();
        allowed.setValue(ALLOWED_MONEY);
        Money used = new Money();
        used.setValue(USED_MONEY);
        financial.setUsed(used);
        financial.setAllowed(allowed);
        return Collections.singletonList(financial);
    }
}
