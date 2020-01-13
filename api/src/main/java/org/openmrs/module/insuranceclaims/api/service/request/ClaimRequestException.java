package org.openmrs.module.insuranceclaims.api.service.request;

public class ClaimRequestException extends Exception {

    public ClaimRequestException(String message) {
        super(message);
    }

    public ClaimRequestException(String message, Throwable cause) { super(message, cause); }

}
