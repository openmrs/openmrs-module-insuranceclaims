package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import org.hl7.fhir.dstu3.model.Claim;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.List;
import java.util.stream.Collectors;

public final class SpecialComponentUtil {

    public static String getSpecialConditionComponentFromCategory(Claim claim, String category) throws FHIRException {
        List<Claim.SpecialConditionComponent> information =
                getConditionsByCategory(claim.getInformation(), category);

        if (information.size() != 1) {
            throw new FHIRException("Could not found unique SpecialConditionComponent with "
                    + category + " category");
        }
        Claim.SpecialConditionComponent component = information.get(0);
        return component.getValueStringType().getValue();
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
}

