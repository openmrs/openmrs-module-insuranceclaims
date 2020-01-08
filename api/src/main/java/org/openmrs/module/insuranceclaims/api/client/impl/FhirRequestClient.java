package org.openmrs.module.insuranceclaims.api.client.impl;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.ClientHttpEntity;
import org.openmrs.module.fhir.api.client.ClientHttpRequestInterceptor;
import org.openmrs.module.fhir.api.helper.ClientHelper;
import org.openmrs.module.fhir.api.helper.FHIRClientHelper;
import org.openmrs.module.insuranceclaims.api.client.FHIRClient;
import org.openmrs.module.insuranceclaims.api.client.FhirMessageConventer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.insuranceclaims.api.client.ClientConstants.API_LOGIN_PROPERTY;
import static org.openmrs.module.insuranceclaims.api.client.ClientConstants.API_PASSWORD_PROPERTY;
import static org.openmrs.module.insuranceclaims.api.client.ClientConstants.CLIENT_HELPER_USER_AGENT;
import static org.openmrs.module.insuranceclaims.api.client.ClientConstants.USER_AGENT;

public class FhirRequestClient implements FHIRClient {

    private RestTemplate restTemplate = new RestTemplate();

    private ClientHelper fhirClientHelper = new FHIRClientHelper();

    private HttpHeaders headers = new HttpHeaders();

    private AbstractHttpMessageConverter<IBaseResource> conventer = new FhirMessageConventer();

    public <T extends IBaseResource> T getObject(String url, Class<T> objectClass) throws URISyntaxException {
        prepareRestTemplate();
        setRequestHeaders();
        ClientHttpEntity clientHttpEntity = fhirClientHelper.retrieveRequest(url);
        ResponseEntity<T> response = sendRequest(clientHttpEntity, objectClass);
        return response.getBody();
    }

    public <T,K extends IBaseResource> K postObject(String url, T object, Class<K> objectClass) throws URISyntaxException,
            HttpServerErrorException {
        prepareRestTemplate();
        setRequestHeaders();
        ClientHttpEntity clientHttpEntity = createPostClientHttpEntity(url, object);
        ResponseEntity<K> response = sendRequest(clientHttpEntity, objectClass);
        return response.getBody();
    }

    private <L> ResponseEntity<L> sendRequest(ClientHttpEntity clientHttpEntity, Class<L> objectClass)  {
        HttpEntity entity = new HttpEntity(clientHttpEntity.getBody(), headers);
        return restTemplate.exchange(clientHttpEntity.getUrl(), clientHttpEntity.getMethod(), entity, objectClass);
    }

    private void setRequestHeaders() {
        headers = new HttpHeaders();
        String username = Context.getAdministrationService().getGlobalProperty(API_LOGIN_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(API_PASSWORD_PROPERTY);

        for (ClientHttpRequestInterceptor interceptor : fhirClientHelper.getCustomInterceptors(username, password)) {
            interceptor.addToHeaders(headers);
        }

        headers.add(USER_AGENT, CLIENT_HELPER_USER_AGENT);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private <L> ClientHttpEntity createPostClientHttpEntity(String url, L object) throws URISyntaxException {
        ClientHttpEntity clientHttpEntity = fhirClientHelper.createRequest(url, object);
        clientHttpEntity.setMethod(HttpMethod.POST);
        clientHttpEntity.setUrl(new URI(url));
        return clientHttpEntity;
    }

    private void prepareRestTemplate() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>(fhirClientHelper.getCustomMessageConverter());
        converters.add(conventer);
        restTemplate.setMessageConverters(converters);
    }
}
