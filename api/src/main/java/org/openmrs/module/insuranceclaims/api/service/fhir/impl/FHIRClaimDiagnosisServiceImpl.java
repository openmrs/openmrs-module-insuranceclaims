package org.openmrs.module.insuranceclaims.api.service.fhir.impl;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
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

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.getUnambiguousElement;

@Transactional
public class FHIRClaimDiagnosisServiceImpl implements FHIRClaimDiagnosisService {

    @Autowired
    private InsuranceClaimDiagnosisDao diagnosisDao;

    @Autowired
    private ConceptService conceptService;

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
    public List<Claim.DiagnosisComponent> generateClaimDiagnosisComponent(InsuranceClaim omrsInsuranceClaim)
    throws FHIRException {
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
            diagnosis.setConcept(getConceptByCodeableConcept(claimDiagnosis.getDiagnosisCodeableConcept(), errors));
        } catch (FHIRException e) {
            errors.add(e.getMessage());
        }

        return diagnosis;
    }

    @Override
    public void setDiagnosisDao(InsuranceClaimDiagnosisDao diagnosisDao) {
        this.diagnosisDao = diagnosisDao;
    }

    private void addCodingToDiagnosis(List<Claim.DiagnosisComponent>  diagnosisComponents) throws FHIRException {
        for (Claim.DiagnosisComponent diagnosis: diagnosisComponents) {
            setDiagnosisPrimaryCoding(diagnosis);
            List<Coding> coding = diagnosis.getDiagnosisCodeableConcept().getCoding();
            List<CodeableConcept> diagnosisType = coding.stream()
                    .map(Coding::getSystem)
                    .map(systemName -> new CodeableConcept().setText(systemName))
                    .collect(Collectors.toList());
            diagnosis.setType(diagnosisType);
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

    private void validateDiagnosisCodingSystem(Claim.DiagnosisComponent diagnosis) throws FHIRException {
        //Method assigns diagnosis system (ICD-10, CIEL, etc.) from type to the concept coding if it don't have assigned system
        if (isEmpty(diagnosis.getType())) {
            return;
        }
        String codingSystem = diagnosis.getTypeFirstRep().getText();
        CodeableConcept concept = diagnosis.getDiagnosisCodeableConcept();
        for (Coding coding : concept.getCoding()) {
            if (coding.getSystem() == null) {
                coding.setSystem(codingSystem);
            }
        }
    }

    private Concept getConceptByCodeableConcept(CodeableConcept codeableConcept, List<String> errors) {
        List<Coding> diagnosisCoding = codeableConcept.getCoding();
        List<Concept> concept = getConceptByFHIRCoding(diagnosisCoding);

        Concept uniqueConcept = getUnambiguousElement(concept);
        if (uniqueConcept == null) {
            errors.add("No matching concept found for the given codings: \n" + diagnosisCoding);
            return null;
        } else {
            return uniqueConcept;
        }
    }

    private List<Concept> getConceptByFHIRCoding(List<Coding> coding) {
        return coding.stream()
                .map(this::getConceptByFHIRCoding)
                .collect(Collectors.toList());
    }

    private Concept getConceptByFHIRCoding(Coding coding) {
        String conceptCode = coding.getCode();
        String systemName = coding.getSystem();

        Concept concept = null;
        if (FHIRConstants.OPENMRS_URI.equals(systemName)) {
            concept = conceptService.getConceptByUuid(conceptCode);
        } else {
            if (isNotEmpty(systemName)) {
                List<Concept> concepts = conceptService.getConceptsByMapping(conceptCode, systemName);
                concept = getUnambiguousElement(concepts);
            }
        }
        return concept;
    }
}
