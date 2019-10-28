package org.openmrs.module.insuranceclaims.util;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;

import java.util.LinkedList;
import java.util.List;

public class FHIRClaimDiagnosisUtil {

    public static Claim.DiagnosisComponent createClaimDiagnosisComponent(InsuranceClaimDiagnosis omrsClaimDiagnosis) {
        Claim.DiagnosisComponent newDiagnosis = new Claim.DiagnosisComponent();

        Concept diagnosisConcepts = omrsClaimDiagnosis.getConcept();
        CodeableConcept diagnosis = FHIRUtils.createCodeableConcept(diagnosisConcepts);

        newDiagnosis.setId(FHIRUtils.extractUuid(omrsClaimDiagnosis.getUuid()));
        newDiagnosis.setDiagnosis(diagnosis);

        return newDiagnosis;
    }

    public static List<Claim.DiagnosisComponent> createClaimDiagnosisComponent(
            List<InsuranceClaimDiagnosis> omrsClaimDiagnosis) {
        List<Claim.DiagnosisComponent> allDiagnosisComponents = new LinkedList<>();

        for (InsuranceClaimDiagnosis insuranceClaimDiagnosis: omrsClaimDiagnosis) {
            Claim.DiagnosisComponent nextDiagnosis = createClaimDiagnosisComponent(insuranceClaimDiagnosis);
            allDiagnosisComponents.add(nextDiagnosis);
        }
        return allDiagnosisComponents;
    }

    public static InsuranceClaimDiagnosis createOmrsClaimDiagnosis(
            Claim.DiagnosisComponent claimDiagnosis, List<String> errors) {

        InsuranceClaimDiagnosis diagnosis = new InsuranceClaimDiagnosis();

        BaseOpenMRSDataUtil.readBaseExtensionFields(diagnosis, claimDiagnosis);

        if (claimDiagnosis.getId() != null) {
            diagnosis.setUuid(FHIRUtils.extractUuid(claimDiagnosis.getId()));
        }

        try {
            diagnosis.setConcept(FHIRUtils.getConceptFromCode(claimDiagnosis.getDiagnosisCodeableConcept(), errors));
        } catch (FHIRException e) {
            errors.add(e.getMessage());
        }

        return diagnosis;
    }

}
