package org.openmrs.module.insuranceclaims.api.service.exceptions;

public class ConsumedItemException extends Exception {

    public ConsumedItemException(String message) {
        super(message);
    }

    public ConsumedItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
