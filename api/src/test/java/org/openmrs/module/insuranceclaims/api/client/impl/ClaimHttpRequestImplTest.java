package org.openmrs.module.insuranceclaims.api.client.impl;

import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.client.ClaimHttpRequest;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.mother.InsuranceClaimMother;
import org.openmrs.module.insuranceclaims.api.service.InsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimResponseService;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PATIENT_EXTERNAL_ID_IDENTIFIER_UUID;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.EXTERNAL_ID_DATASET_PATH;

@RunWith(SpringJUnit4ClassRunner.class)
public class ClaimHttpRequestImplTest extends BaseModuleContextSensitiveTest {
    static String BASE_URL = "http://localhost:8000/api_fhir";
    static String CLAIM_RESPONSE_URL = BASE_URL + "/" + "ClaimResponse";
    static String CLAIM_URL = BASE_URL + "/" + "Claim";
    static String TEST_CODE = "clCode";

    @Mock
    private FhirRequestClient client;

    @InjectMocks
    @Autowired
    private ClaimHttpRequestImpl request;

    @Autowired
    private ClaimHttpRequest autowiredRequest;

    @Autowired
    private InsuranceClaimService insuranceClaimService;

    @Autowired
    private FHIRClaimResponseService claimResponseService;

    @Autowired
    private FHIRInsuranceClaimService fhirClaimService;

    private InsuranceClaim testInsuranceClaim;

    @Before
    public void setUpClass() throws Exception {
        executeDataSet(EXTERNAL_ID_DATASET_PATH);

        testInsuranceClaim = createTestInstance();

        insuranceClaimService.saveOrUpdate(testInsuranceClaim);
    }

    @Before
    public void setup() throws Exception {
        String identifierSource = "test_externalId_attribute_types.xml";
        executeDataSet(identifierSource);
    }

    public <T extends IBaseResource> void setupGetRequestMock(Class<T> expectedClass, String url, Object returnValue)
    throws URISyntaxException {
        when(client.getObject(eq(url), eq(expectedClass))).thenReturn((T) returnValue);
    }

    public <T, K extends IBaseResource> void setupPostRequestMock(Class<T> expectedObjectToSend, K response, String url)
    throws URISyntaxException {
        when(client.postObject(eq(url), any(expectedObjectToSend), eq(response.getClass())))
                .thenAnswer(i -> response);
    }

    @Test
    public void autowiredRequest_shouldNotBeNull() {
        Assert.assertThat(autowiredRequest, is(notNullValue()));
    }

//    @Test
//    public void getClaimRequest_shouldRequestProperClaim() throws URISyntaxException, FHIRException {
//        String expectedUrlCall = CLAIM_URL + "/" + TEST_CODE;
//
//        Claim claimToReturn = fhirClaimService.generateClaim(testInsuranceClaim);
//        InsuranceClaim expected = fhirClaimService.generateOmrsClaim(claimToReturn, new ArrayList<>());
//        setupGetRequestMock(ClaimResponse.class, expectedUrlCall, claimToReturn);
//
//        ClaimHttpRequest x = request;
//        ClaimRequestWrapper result = x.getClaimRequest(CLAIM_URL, TEST_CODE);
//
//        Assert.assertThat(result.getInsuranceClaim(), Matchers.samePropertyValuesAs(expected));
//        Assert.assertThat(result.getDiagnosis(), Matchers.hasSize(0));
//    }

    @Test
    public void getClaimResponseRequest_shouldRequestProperClaim() throws URISyntaxException {
        String expectedUrlCall = CLAIM_RESPONSE_URL + "/" + TEST_CODE;

        ClaimResponse claimToReturn = claimResponseService.generateClaimResponse(testInsuranceClaim);
        setupGetRequestMock(ClaimResponse.class, expectedUrlCall, claimToReturn);

        ClaimHttpRequest x = request;
        ClaimResponse result = x.getClaimResponse(CLAIM_RESPONSE_URL, TEST_CODE);

        Assert.assertThat(result, Matchers.samePropertyValuesAs(claimToReturn));
    }

//    @Test
//    public void sendClaimRequest_shouldPassFhirObjectToClient() throws URISyntaxException, FHIRException {
//        String expectedUrlCall = CLAIM_URL + "/";
//        InsuranceClaim claim = testInsuranceClaim;
//        Claim claimToSend = fhirClaimService.generateClaim(claim);
//
//        setupPostRequestMock(claimToSend.getClass(), claimToSend, expectedUrlCall);
//        ClaimHttpRequest x = request;
//        ClaimResponse result = x.sendClaimRequest(CLAIM_URL, claim);
//        Assert.assertThat(result, Matchers.samePropertyValuesAs(claimToSend));
//    }

    private InsuranceClaim createTestInstance() {
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        Provider provider = Context.getProviderService().getProvider(TestConstants.TEST_PROVIDER_ID);
        VisitType visitType = Context.getVisitService().getVisitType(TestConstants.TEST_VISIT_TYPE_ID);
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierTypeByUuid(PATIENT_EXTERNAL_ID_IDENTIFIER_UUID);
        return InsuranceClaimMother.createTestInstance(location, provider, visitType, identifierType);
    }
}
