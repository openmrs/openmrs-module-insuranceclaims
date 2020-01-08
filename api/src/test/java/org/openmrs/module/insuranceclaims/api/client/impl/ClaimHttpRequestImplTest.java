package org.openmrs.module.insuranceclaims.api.client.impl;

import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRInsuranceClaimService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ClaimHttpRequestImplTest extends BaseModuleContextSensitiveTest {
    private static String BASE_URL = "http://localhost:8000/api_fhir";
    private static String CLAIM_RESPONSE_URL = BASE_URL + "/" + "ClaimResponse";
    private static String CLAIM_URL = BASE_URL + "/" + "Claim";
    private static String TEST_CODE = "clCode";

    @Mock
    private FhirRequestClient client;

    @Mock
    private FHIRInsuranceClaimService fhirInsuranceClaimService;

    @InjectMocks
    private ClaimHttpRequestImpl request;

    @Test
    public void getClaimResponseRequest_shouldRequestProperClaim() throws URISyntaxException {
        String expectedUrlCall = CLAIM_RESPONSE_URL + "/" + TEST_CODE;
        ClaimResponse claimToReturn = new ClaimResponse();
        when(client.getObject(eq(expectedUrlCall), eq(ClaimResponse.class))).thenReturn(claimToReturn);

        ClaimResponse result = request.getClaimResponse(CLAIM_RESPONSE_URL, TEST_CODE);

        Assert.assertThat(result, Matchers.samePropertyValuesAs(claimToReturn));
    }

    @Test
    public void sendClaimRequest_shouldPassFhirObjectToClient() throws URISyntaxException, FHIRException {
        String expectedUrlCall = CLAIM_URL + "/";
        InsuranceClaim claim = new InsuranceClaim();
        Claim claimToSend = new Claim();
        ClaimResponse toBeReturned = new ClaimResponse();

        when(client.postObject(eq(expectedUrlCall), eq(claimToSend), eq(toBeReturned.getClass()))).thenAnswer(i -> toBeReturned);
        when(fhirInsuranceClaimService.generateClaim(claim)).thenReturn(claimToSend);
        ClaimResponse result = request.sendClaimRequest(CLAIM_URL, claim);

        Assert.assertThat(result, Matchers.samePropertyValuesAs(toBeReturned));
    }
}
