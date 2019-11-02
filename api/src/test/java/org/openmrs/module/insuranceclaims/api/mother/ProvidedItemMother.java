package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;

import java.math.BigDecimal;
import java.util.Date;

public class ProvidedItemMother {

    private static final String EXAMPLE_PRICE = "123456.78";

    public static ProvidedItem createTestInstance(Concept concept, Location location,
                                                  PatientIdentifierType patientIdentifierType) {

        Patient patient = PatientMother.createTestInstance(location, patientIdentifierType);
        Bill bill = BillMother.createTestInstance(concept);
        ProvidedItem item = new ProvidedItem();
        item.setPrice(new BigDecimal(EXAMPLE_PRICE));
        item.setDateOfServed(new Date());
        item.setItem(concept);
        item.setPatient(patient);
        item.setStatus(ProcessStatus.ENTERED);
        item.setBill(bill);
        return item;
    }
}
