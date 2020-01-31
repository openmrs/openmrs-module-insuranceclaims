package org.openmrs.module.insuranceclaims.api.service.exceptions;

public class PatientRequestException extends Exception {

    public PatientRequestException(String message) {
        super(message);
    }

    public PatientRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
