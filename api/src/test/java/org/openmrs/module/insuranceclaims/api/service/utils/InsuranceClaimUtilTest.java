package org.openmrs.module.insuranceclaims.api.service.utils;

import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimUtil;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PATIENT_EXTERNAL_ID_IDENTIFIER_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PERIOD_FROM;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PERIOD_TO;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.EXTERNAL_ID_DATASET_PATH;

public class InsuranceClaimUtilTest extends BaseModuleContextSensitiveTest {

    private InsuranceClaim testClaim;

    private Claim mappedClaim;

    private static InsuranceClaimUtil utils;

    private static final String TEST_UUID = "T3STUU1D";

    @Autowired
    private InsuranceClaimService insuranceClaimService;

    @Autowired
    private FHIRInsuranceClaimService fhirInsuranceClaimService;

    @Before
    public void setUp() throws Exception {
        executeDataSet(EXTERNAL_ID_DATASET_PATH);
        testClaim = createTestClaim();
        insuranceClaimService.saveOrUpdate(testClaim);
        mappedClaim = fhirInsuranceClaimService.generateClaim(testClaim);
    }

    @Test
    public void createClaimVisitType_shouldCreateValidCodeableConcept() {
        CodeableConcept visitConcept = InsuranceClaimUtil.createClaimVisitType(testClaim);
        Assert.assertThat(visitConcept.getText(), Matchers.equalTo(testClaim.getVisitType().getName()));
    }


    @Test
    public void getClaimBillablePeriod_shouldReturnValidFromToDates() {
        List<String> errors = new ArrayList<>();
        Map<String, Date> billablePeriod = InsuranceClaimUtil.getClaimBillablePeriod(mappedClaim, errors);

        Assert.assertThat(billablePeriod.get(PERIOD_FROM), Matchers.equalTo(testClaim.getDateFrom()));
        Assert.assertThat(billablePeriod.get(PERIOD_TO), Matchers.equalTo(testClaim.getDateTo()));
    }

    @Test
    public void getClaimExplanation_shouldReturnClaimExplanation() {
        List<String> errors = new ArrayList<>();
        String explanation = InsuranceClaimUtil.getClaimExplanation(mappedClaim, errors);

        Assert.assertThat(explanation, Matchers.equalTo(testClaim.getExplanation()));
    }

    @Test
    public void getClaimGuaranteeId_shouldReturnGuaranteeId() {
        List<String> errors = new ArrayList<>();
        String guaranteeId = InsuranceClaimUtil.getClaimGuaranteeId(mappedClaim, errors);

        Assert.assertThat(guaranteeId, Matchers.equalTo(testClaim.getGuaranteeId()));
    }

    @Test
    public void getClaimCode_shouldReturnClaimCode() {
        List<String> errors = new ArrayList<>();
        String claimCode = InsuranceClaimUtil.getClaimCode(mappedClaim, errors);

        Assert.assertThat(claimCode, Matchers.equalTo(testClaim.getClaimCode()));
    }

    @Test
    public void getClaimUuid_shouldReturnClaimUuid() {
        List<String> errors = new ArrayList<>();
        String claimUuid = InsuranceClaimUtil.getClaimUuid(mappedClaim, errors);
        Assert.assertThat(claimUuid, Matchers.equalTo(testClaim.getUuid()));
    }

    @Test
    public void getClaimResponseId_shouldReturnResponseUuid() {
        ClaimResponse response = new ClaimResponse();
        response.setId("ClaimRepsonse/"+TEST_UUID);
        String uuid = InsuranceClaimUtil.getClaimResponseId(response);
        Assert.assertThat(uuid, Matchers.equalTo(TEST_UUID));
    }

    @Test
    public void getClaimDateCreated() {
        List<String> errors = new ArrayList<>();
        Date dateCreated = InsuranceClaimUtil.getClaimDateCreated(mappedClaim, errors);

        Assert.assertThat(dateCreated, Matchers.equalTo(testClaim.getDateCreated()));
    }

    private InsuranceClaim createTestClaim() {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
        VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierTypeByUuid(PATIENT_EXTERNAL_ID_IDENTIFIER_UUID);
        return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
    }
}
