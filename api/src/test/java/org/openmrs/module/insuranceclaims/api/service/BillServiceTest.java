package org.openmrs.module.insuranceclaims.api.service;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Patient;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.mother.BillMother;
import org.openmrs.module.insuranceclaims.api.mother.PatientMother;
import org.openmrs.module.insuranceclaims.api.mother.ProvidedItemMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class BillServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ProvidedItemService providedItemService;

    @Autowired
    private BillService billService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ConceptService conceptService;

    private Patient patient;

    @Before
    public void setUp() {
        Location location = locationService.getLocation(TestConstants.TEST_LOCATION_ID);
        PatientIdentifierType identifierType = patientService
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);

        patient = PatientMother.createTestInstance(location, identifierType);
    }

    @Test
    public void getProvidedItems_shouldCorrectlyGetItemsForPatient() {
        List<ProvidedItem> providedItems = new ArrayList<>();

        for (String item : TestConstants.PRICES) {
            providedItems.add(createTestInstanceForProvidedItem(ProcessStatus.ENTERED, item, patient));
        }

        for (ProvidedItem item : providedItems) {
            providedItemService.saveOrUpdate(item);
        }

        List<ProvidedItem> actualProvidedItems = providedItemService.getProvidedEnteredItems(patient.getPatientId());

        Bill actualBill = billService.generateBill(actualProvidedItems);

        List<ProvidedItem> enteredProvidedItems = providedItemService.getProvidedItems(patient.getPatientId(),
                ProcessStatus.ENTERED);

        BigDecimal sumProvideItems = new BigDecimal("100005742.08");

        Bill expectedBill = BillMother.createTestInstanceWithAmount(sumProvideItems, actualBill.getStartDate(),
                actualBill.getEndDate());

        Assert.assertThat(actualBill, is(expectedBill));
        Assert.assertThat(enteredProvidedItems, IsEmptyCollection.empty());
    }

    private ProvidedItem createTestInstanceForProvidedItem(ProcessStatus processStatus, String price, Patient patient) {
        Concept concept = conceptService.getConcept(TestConstants.TEST_CONCEPT_ID);

        return ProvidedItemMother.createTestInstanceForProvidedItem(concept, patient, new BigDecimal(price),
                processStatus);
    }
}
