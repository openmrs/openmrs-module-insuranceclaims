package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.openmrs.Concept;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimDiagnosisService;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

public class FHIRClaimDiagnosisServiceImpl implements FHIRClaimDiagnosisService {

    private InsuranceClaimDiagnosisDao diagnosisDao;

    public FHIRClaimDiagnosisServiceImpl(InsuranceClaimDiagnosisDao diagnosisDao) {
        this.diagnosisDao = diagnosisDao;
    }

    public Claim.DiagnosisComponent createClaimDiagnosisComponent(InsuranceClaimDiagnosis omrsClaimDiagnosis) {
        Claim.DiagnosisComponent newDiagnosis = new Claim.DiagnosisComponent();

        Concept diagnosisConcepts = omrsClaimDiagnosis.getConcept();
        CodeableConcept diagnosis = FHIRUtils.createCodeableConcept(diagnosisConcepts);

        newDiagnosis.setId(FHIRUtils.extractUuid(omrsClaimDiagnosis.getUuid()));
        newDiagnosis.setDiagnosis(diagnosis);

        return newDiagnosis;
    }

    public List<Claim.DiagnosisComponent> createClaimDiagnosisComponent(
            List<InsuranceClaimDiagnosis> omrsClaimDiagnosis) {
        List<Claim.DiagnosisComponent> allDiagnosisComponents = new LinkedList<>();
        diagnosisDao.hashCode();
        for (InsuranceClaimDiagnosis insuranceClaimDiagnosis: omrsClaimDiagnosis) {
            Claim.DiagnosisComponent nextDiagnosis = createClaimDiagnosisComponent(insuranceClaimDiagnosis);
            allDiagnosisComponents.add(nextDiagnosis);
        }
        return allDiagnosisComponents;
    }

    public List<Claim.DiagnosisComponent> createClaimDiagnosisComponent(InsuranceClaim omrsInsuranceClaim) {
        List<InsuranceClaimDiagnosis> claimDiagnoses = diagnosisDao.findInsuranceClaimDiagnosis(omrsInsuranceClaim.getId());

        return createClaimDiagnosisComponent(claimDiagnoses);
    }

    @Transactional
    public InsuranceClaimDiagnosis createOmrsClaimDiagnosis(
            Claim.DiagnosisComponent claimDiagnosis, List<String> errors) {

        InsuranceClaimDiagnosis diagnosis = new InsuranceClaimDiagnosis();

        BaseOpenMRSDataUtil.readBaseExtensionFields(diagnosis, claimDiagnosis);

        if (claimDiagnosis.getId() != null) {
            diagnosis.setUuid(FHIRUtils.extractUuid(claimDiagnosis.getId()));
        }

        try {
            diagnosis.setConcept(FHIRUtils.getConceptFromCode(claimDiagnosis.getDiagnosisCodeableConcept(), errors));
        } catch (Exception e) {
            errors.add(e.getMessage());
        }

        return diagnosis;
    }

    public void setDiagnosisDao(InsuranceClaimDiagnosisDao diagnosisDao) {
        this.diagnosisDao = diagnosisDao;
    }
}
