package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PATIENT_EXTERNAL_ID_IDENTIFIER_UUID;

public final class PatientUtil {

    public static Reference buildPatientReference(InsuranceClaim claim) {
        Patient patient = claim.getPatient();
        Reference patientReference = FHIRUtils.buildPractitionerReference(claim.getProvider());

        String patientId = patient.getActiveIdentifiers()
                .stream()
                .filter(c -> c.getIdentifierType().getUuid().equals(PATIENT_EXTERNAL_ID_IDENTIFIER_UUID))
                .findFirst()
                .map(PatientIdentifier::getIdentifier)
                .orElse(patient.getUuid());

        String reference = FHIRConstants.PATIENT + "/" + patientId;
        patientReference.setReference(reference);

        return patientReference;
    }

    private PatientUtil() {}
}
