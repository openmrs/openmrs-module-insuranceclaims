package org.openmrs.module.insuranceclaims.forms;

public class PatientImportForm {
    private String externalPatientId;

    public void setExternalPatientId(String externalPatientId) {
        this.externalPatientId = externalPatientId;
    }

    public String getExternalPatientId() {
        return externalPatientId;
    }
}
