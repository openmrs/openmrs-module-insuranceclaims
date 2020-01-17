package org.openmrs.module.insuranceclaims.api.service.exceptions;

public class EligibilityRequestException extends Exception {

    public EligibilityRequestException(String message) {
        super(message);
    }

    public EligibilityRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
