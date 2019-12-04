package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.IdentifierUtil.getUnambiguousElement;

public final class SpecialComponentUtil {

    public static String getSpecialConditionComponentFromCategory(Claim claim, String category) throws FHIRException {
        List<Claim.SpecialConditionComponent> information =
                getConditionsByCategory(claim.getInformation(), category);

        Claim.SpecialConditionComponent component = getUnambiguousElement(information);
        return component != null ? component.getValueStringType().getValue() : null;
    }

    public static String getSpecialConditionComponentBySequenceNumber(Claim claim, int sequenceId) throws FHIRException {
        List<Claim.SpecialConditionComponent> information = claim.getInformation();
        Claim.SpecialConditionComponent requested = information.stream()
                .filter(c -> c.getSequence() == sequenceId)
                .findFirst()
                .orElse(null);

        return requested == null ? null : requested.getValueStringType().getValue();
    }

    public static Claim.SpecialConditionComponent createSpecialComponent(String value, String categoryName) {
        Claim.SpecialConditionComponent information = new Claim.SpecialConditionComponent();
        CodeableConcept category = new CodeableConcept();

        category.setText(categoryName);
        information.setValue(new StringType(value));
        information.setCategory(category);
        return information;
    }

    private static List<Claim.SpecialConditionComponent> getConditionsByCategory(
            List<Claim.SpecialConditionComponent> componentList, String category) {

        return componentList.stream()
                .filter(c -> c.getCategory().getText() != null)
                .filter(c -> c.getCategory().getText().equals(category))
                .collect(Collectors.toList());
    }

    private SpecialComponentUtil() {}
}

