package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.ClaimResponse;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class IdentifierUtil {

    public static String getClaimIdentifierValueBySystemCode(Claim claim, String code) throws FHIRException {
        List<Identifier> claimCodeIdentifier = getIdentifierBySystemCode(claim.getIdentifier(), code);
        return validatedIdentifier(claimCodeIdentifier, code);
    }

    public static String getClaimIdentifierValueBySystemCode(ClaimResponse claim, String code) throws FHIRException {
        List<Identifier> claimCodeIdentifier = getIdentifierBySystemCode(claim.getIdentifier(), code);
        return validatedIdentifier(claimCodeIdentifier, code);
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

    private static List<Identifier> getIdentifierBySystemCode(List<Identifier> identifierList, String code) {
        List<Identifier> result = new LinkedList<>();

        for (Identifier i : identifierList) {
            List<String> codes = i.getType().getCoding()
                    .stream()
                    .map(Coding::getCode)
                    .collect(Collectors.toList());

            if (codes.contains(code)) {
                result.add(i);
            }
        }

        return result;
    }

    public static String getIdentifierValueByCode(Claim claim, String code, List<String> errors) {
        try {
            return getClaimIdentifierValueBySystemCode(claim, code);
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
        return null;
    }

    public static String getIdentifierValueByCode(ClaimResponse claim, String code, List<String> errors) {
        try {
            return getClaimIdentifierValueBySystemCode(claim, code);
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
        return null;
    }

    private static String validatedIdentifier(List<Identifier> claimCodeIdentifier, String code) throws FHIRException {
        if (claimCodeIdentifier.size() != 1) {
            throw new FHIRException("Could not found unique identifier for system code "
                    + code);
        }
        return claimCodeIdentifier.get(0).getValue();
    }


}
