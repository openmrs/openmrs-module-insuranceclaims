package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimDiagnosisDao;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaimDiagnosis;
import org.openmrs.module.insuranceclaims.api.service.fhir.FHIRClaimDiagnosisService;
import org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class FHIRClaimDiagnosisServiceImpl implements FHIRClaimDiagnosisService {

    @Autowired
    private InsuranceClaimDiagnosisDao diagnosisDao;

    @Override
    public Claim.DiagnosisComponent generateClaimDiagnosisComponent(InsuranceClaimDiagnosis omrsClaimDiagnosis) {
        Claim.DiagnosisComponent newDiagnosis = new Claim.DiagnosisComponent();

        Concept diagnosisConcepts = omrsClaimDiagnosis.getConcept();
        CodeableConcept diagnosis = FHIRUtils.createCodeableConcept(diagnosisConcepts);

        newDiagnosis.setId(FHIRUtils.extractUuid(omrsClaimDiagnosis.getUuid()));
        newDiagnosis.setDiagnosis(diagnosis);

        return newDiagnosis;
    }

    @Override
    public List<Claim.DiagnosisComponent> generateClaimDiagnosisComponent(
            List<InsuranceClaimDiagnosis> omrsClaimDiagnosis) {
        List<Claim.DiagnosisComponent> allDiagnosisComponents = new ArrayList<>();

        for (InsuranceClaimDiagnosis insuranceClaimDiagnosis : omrsClaimDiagnosis) {
            Claim.DiagnosisComponent nextDiagnosis = generateClaimDiagnosisComponent(insuranceClaimDiagnosis);
            allDiagnosisComponents.add(nextDiagnosis);
        }
        return allDiagnosisComponents;
    }

    @Override
    public List<Claim.DiagnosisComponent> generateClaimDiagnosisComponent(InsuranceClaim omrsInsuranceClaim) {
        List<InsuranceClaimDiagnosis> claimDiagnoses = diagnosisDao.findInsuranceClaimDiagnosis(omrsInsuranceClaim.getId());
        List<Claim.DiagnosisComponent> fhirDiagnosisComponent = generateClaimDiagnosisComponent(claimDiagnoses);
        addCodingToDiagnosis(fhirDiagnosisComponent);
        return fhirDiagnosisComponent;
    }

    @Override
    public InsuranceClaimDiagnosis createOmrsClaimDiagnosis(
            Claim.DiagnosisComponent claimDiagnosis, List<String> errors) {

        InsuranceClaimDiagnosis diagnosis = new InsuranceClaimDiagnosis();

        BaseOpenMRSDataUtil.readBaseExtensionFields(diagnosis, claimDiagnosis);

        if (claimDiagnosis.getId() != null) {
            diagnosis.setUuid(FHIRUtils.extractUuid(claimDiagnosis.getId()));
        }
        try {
            validateDiagnosisCodingSystem(claimDiagnosis);
            diagnosis.setConcept(getConceptFromCode(claimDiagnosis.getDiagnosisCodeableConcept(), errors));
        } catch (FHIRException e) {
            errors.add(e.getMessage());
        }

        return diagnosis;
    }

    public void setDiagnosisDao(InsuranceClaimDiagnosisDao diagnosisDao) {
        this.diagnosisDao = diagnosisDao;
    }

    private void addCodingToDiagnosis(List<Claim.DiagnosisComponent>  diagnosisComponents) {
        for (Claim.DiagnosisComponent diagnosis: diagnosisComponents) {
            try {
                setDiagnosisPrimaryCoding(diagnosis);
                List<Coding> coding = diagnosis.getDiagnosisCodeableConcept().getCoding();
                List<CodeableConcept> diagnosisType = coding.stream()
                        .map(Coding::getSystem)
                        .map(systemName -> new CodeableConcept().setText(systemName))
                        .collect(Collectors.toList());

                diagnosis.setType(diagnosisType);
            } catch (FHIRException exception) {
                continue;
            }
        }
    }

    private void setDiagnosisPrimaryCoding(Claim.DiagnosisComponent diagnosis) throws FHIRException {
        String primaryCoding = InsuranceClaimConstants.PRIMARY_DIAGNOSIS_MAPPING;

        List<Coding> diagnosisCoding = diagnosis.getDiagnosisCodeableConcept().getCoding();
        for (Coding c : diagnosisCoding) {
            if (c.getSystem().equals(primaryCoding)) {
                Collections.swap(diagnosisCoding, 0, diagnosisCoding.indexOf(c));
                break;
            }
        }
    }

    private void validateDiagnosisCodingSystem(Claim.DiagnosisComponent diagnosis) {
        //Method assigns diagnosis system (ICD-10, CIEL, etc.) from type to the concept coding if it don't have assigned system
        if (diagnosis.getType().size() == 0) {
            return;
        }

        try {
            String sys = diagnosis.getType().get(0).getText();
            CodeableConcept concept = diagnosis.getDiagnosisCodeableConcept();
            for (Coding coding : concept.getCoding()) {
                if (coding.getSystem() == null) {
                    coding.setSystem(sys);
                }
            }
        } catch (FHIRException e) {
            return;
        }
    }

    private static Concept getConceptFromCode(CodeableConcept codeableConcept, List<String> errors) {
        String conceptCode;
        String system;
        Concept concept = null;
        List<Coding> dts = codeableConcept.getCoding();

        for (Coding coding : dts) {
            conceptCode = coding.getCode();
            system = coding.getSystem();
            if (FHIRConstants.OPENMRS_URI.equals(system)) {
                concept = Context.getConceptService().getConceptByUuid(conceptCode);
            } else {
                String systemName = system;
                if (systemName != null && !systemName.isEmpty()) {
                    List<Concept> concepts = Context.getConceptService().getConceptsByMapping(conceptCode, systemName);
                    if (concepts.size() == 1) {
                        concept = concepts.get(FHIRConstants.FIRST);
                    }
                }
            }
            if (concept != null) {
                break;
            }
        }
        if (concept == null) {
            errors.add("No matching concept found for the given codings");
            return null;
        } else {
            return concept;
        }
    }
}
