package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.ELEMENTS;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.OPENMRS_ID_DEFAULT_IDENTIFIER_SOURCE;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.OPENMRS_ID_DEFAULT_TYPE;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PATIENT_EXTERNAL_ID_IDENTIFIER_UUID;

public final class IdentifierUtil {

    public static String getClaimIdentifierValueBySystemCode(Claim claim, String code) {
        List<Identifier> claimCodeIdentifier = getIdentifierBySystemCode(claim.getIdentifier(), code);
        Identifier identifier = getUnambiguousElement(claimCodeIdentifier);
        return identifier.getValue();
    }

    public static String getClaimIdentifierValueBySystemCode(ClaimResponse claim, String code) {
        List<Identifier> claimCodeIdentifier = getIdentifierBySystemCode(claim.getIdentifier(), code);
        Identifier identifier = getUnambiguousElement(claimCodeIdentifier);
        return identifier.getValue();
    }

    public static String getPatientIdentifierValueBySystemCode(Patient patient, String code) {
        List<Identifier> claimCodeIdentifier = getIdentifierBySystemCode(patient.getIdentifier(), code);
        Identifier identifier = getUnambiguousElement(claimCodeIdentifier);
        return identifier.getValue();
    }

    public static Identifier createIdentifier(String value, String codingCode, String codingSystem) {
        Identifier codeClaimIdentifier = new Identifier();
        codeClaimIdentifier.setValue(value);
        codeClaimIdentifier.setUse(Identifier.IdentifierUse.USUAL);

        CodeableConcept identifierType = new CodeableConcept();
        Coding identifierTypeCoding = new Coding();
        identifierTypeCoding.setCode(codingCode);
        identifierTypeCoding.setSystem(codingSystem);
        identifierType.setCoding(Collections.singletonList(identifierTypeCoding));

        codeClaimIdentifier.setType(identifierType);

        return codeClaimIdentifier;
    }

    public static Reference buildReference(String referenceType, String referenceValue) {
        Reference reference = new Reference();
        String stringReference = referenceType + "/" + referenceValue;
        reference.setReference(stringReference);
        return reference;
    }


    public static String getIdentifierValueByCode(Claim claim, String code, List<String> errors) {
        return getClaimIdentifierValueBySystemCode(claim, code);
    }

    public static String getIdentifierValueByCode(ClaimResponse claim, String code, List<String> errors) {
        return getClaimIdentifierValueBySystemCode(claim, code);
    }

    public static String getIdFromReference(Reference reference) {
        return reference.getReference().split("/")[1];
    }

    public static <T> T getUnambiguousElement(List<T> listOfElements) {
        CollectionUtils.filter(listOfElements, PredicateUtils.notNullPredicate());
        if (CollectionUtils.isEmpty(listOfElements)) {
            return null;
        }
        Set<T> distinctElements = new HashSet<>(listOfElements);
        if (hasOneUniqueElement(distinctElements)) {
            return listOfElements.get(0);
        } else {
            throw new IdentifierNotUniqueException("Could not get unambiguous element of type "
                    + listOfElements.get(0).getClass()
                    + ELEMENTS + ":\n" + listOfElements.toString());
        }
    }

    public static PatientIdentifier createBasicPatientIdentifier() {
        PatientIdentifier omrIdentifier = new PatientIdentifier();
        IdentifierSource idSource = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(OPENMRS_ID_DEFAULT_IDENTIFIER_SOURCE);
        omrIdentifier.setIdentifier(Context.getService(IdentifierSourceService.class).generateIdentifier(idSource, null));
        omrIdentifier.setLocation(Context.getLocationService().getDefaultLocation());
        omrIdentifier.setIdentifierType(Context.getPatientService().getPatientIdentifierTypeByUuid(OPENMRS_ID_DEFAULT_TYPE));
        return omrIdentifier;
    }

    public static PatientIdentifier createExternalIdIdentifier(String identifier) {
        PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(PATIENT_EXTERNAL_ID_IDENTIFIER_UUID);
        PatientIdentifier patientExternalIdentifier = new PatientIdentifier(identifier, identifierType, Context.getUserContext().getLocation());
        patientExternalIdentifier.setPreferred(false);
        return patientExternalIdentifier;
    }

    private static List<Identifier> getIdentifierBySystemCode(List<Identifier> identifierList, String code) {
        List<Identifier> result = new ArrayList<>();

        for (Identifier identifier : identifierList) {
            List<String> codes = identifier.getType().getCoding()
                    .stream()
                    .map(Coding::getCode)
                    .collect(Collectors.toList());

            if (codes.contains(code)) {
                result.add(identifier);
            }
        }
        return result;
    }

    private static <T> boolean hasOneUniqueElement(Set<T> setOfElements) {
        return CollectionUtils.size(setOfElements) == 1;
    }

    private IdentifierUtil() {}
}
