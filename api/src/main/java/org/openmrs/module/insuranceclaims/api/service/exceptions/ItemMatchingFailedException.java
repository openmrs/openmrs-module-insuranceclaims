package org.openmrs.module.insuranceclaims.api.service.exceptions;

public class ItemMatchingFailedException extends Exception {

    public ItemMatchingFailedException(String message) {
        super(message);
    }

    public ItemMatchingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
