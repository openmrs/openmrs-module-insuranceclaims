package org.openmrs.module.insuranceclaims.api.client;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.web.client.HttpServerErrorException;

import java.net.URISyntaxException;

public interface FHIRClient {
    <T,K extends IBaseResource> K postObject(String url, T object, Class<K> typeOfReturnedObject) throws URISyntaxException,
            HttpServerErrorException;

    <T extends IBaseResource> T getObject(String url, Class<T> objectClass) throws URISyntaxException;
}
