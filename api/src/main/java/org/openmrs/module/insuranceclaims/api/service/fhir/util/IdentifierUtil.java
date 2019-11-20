package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.api.IdentifierNotUniqueException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class IdentifierUtil {

    public static String getClaimIdentifierValueBySystemCode(Claim claim, String code) throws FHIRException {
        List<Identifier> claimCodeIdentifier = getIdentifierBySystemCode(claim.getIdentifier(), code);
        Identifier identifier = getUnambiguousElement(claimCodeIdentifier);
        return identifier.getValue();
    }

    public static String getClaimIdentifierValueBySystemCode(ClaimResponse claim, String code) throws FHIRException {
        List<Identifier> claimCodeIdentifier = getIdentifierBySystemCode(claim.getIdentifier(), code);
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

    public static String getIdentifierValueByCode(Claim claim, String code, List<String> errors) {
        try {
            return getClaimIdentifierValueBySystemCode(claim, code);
        } catch (FHIRException e) {
            errors.add(e.getMessage());
        }
        return null;
    }

    public static String getIdentifierValueByCode(ClaimResponse claim, String code, List<String> errors) {
        try {
            return getClaimIdentifierValueBySystemCode(claim, code);
        } catch (FHIRException e) {
            errors.add(e.getMessage());
        }
        return null;
    }

    public static String getIdFromReference(Reference reference) {
        try {
            //References are in format: Category/referenceCode
            return reference.getReference().split("/")[1];
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static <T> T getUnambiguousElement(List<T> listOfElements) {
        if (listOfElements.isEmpty()) {
            return null;
        }
        Set<T> set = new HashSet<>(listOfElements);
        if (set.size() == 1) {
            return listOfElements.get(0);
        } else {
            throw new IdentifierNotUniqueException("Could not get unambiguous element of type "
                    + listOfElements.get(0).getClass()
                    + " Elements:\n" + listOfElements.toString());
        }
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

    private IdentifierUtil() {}
}
