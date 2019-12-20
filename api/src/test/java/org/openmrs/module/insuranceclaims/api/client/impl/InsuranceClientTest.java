package org.openmrs.module.insuranceclaims.api.client.impl;

import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Claim;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.openmrs.module.insuranceclaims.api.util.TestConstants.TEST_URL;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RunWith(MockitoJUnitRunner.class)
public class InsuranceClientTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    FhirRequestClient client = new FhirRequestClient();

    @Test
    public void getClient_shouldReturnNotNullService() {
        Assert.assertThat(client, is(notNullValue()));
    }

    @Test
    public void shouldSendGetRequestForClaimObject_shouldReturnClaim() throws URISyntaxException {
        Claim expected = createTestClaim();
        setupGetRequestMock(Claim.class,  expected, TEST_URL);
        Claim actual = client.getObject(TEST_URL, Claim.class);
        Assert.assertThat(expected, Matchers.equalTo(actual));
    }

    @Test
    public void shouldSendPostRequestWithClaimObject_shouldReturnClaim() throws URISyntaxException {
        Claim expected = createTestClaim();
        setupPostRequestMock(Claim.class,  expected, TEST_URL);
        Claim actual = client.postObject(TEST_URL, expected, Claim.class);
        Assert.assertThat(expected, Matchers.equalTo(actual));
    }

    private Claim createTestClaim() {
        return new Claim();
    }

    public <T> void setupGetRequestMock(Class<T> returnedObjectClass, T objectToReturn, String url)
    throws URISyntaxException {
        createRequestMock(url, HttpMethod.GET, returnedObjectClass, objectToReturn);
    }

    public <T> void setupPostRequestMock(Class<T> returnedObjectClass, T objectToReturn,  String url)
    throws URISyntaxException {
        createRequestMock(url, HttpMethod.POST, returnedObjectClass, objectToReturn);
    }

    public <T> void createRequestMock(String url, HttpMethod method, Class<T> requestedObjectClass, T objectToReturn)
    throws URISyntaxException {
        given(restTemplate.exchange(
                eq(new URI(url)),
                eq(method),
                any(HttpEntity.class),
                eq(requestedObjectClass)))
                .willReturn(mockResponseEntityBody(objectToReturn));
    }

    private <T> ResponseEntity mockResponseEntityBody(T objectToReturn) {
        ResponseEntity responseEntity = new ResponseEntity<T>(objectToReturn, ACCEPTED);
        return responseEntity;
    }

}
