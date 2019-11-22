package org.openmrs.module.insuranceclaims.api.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.mother.PatientMother;
import org.openmrs.module.insuranceclaims.api.mother.ProvidedItemMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class ProvidedItemServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ProvidedItemService providedItemService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ConceptService conceptService;

    private Patient patient;
    private List<ProvidedItem> expectedProvidedItems;

    @Before
    public void setUp() {
        Location location = locationService.getLocation(TestConstants.TEST_LOCATION_ID);
        PatientIdentifierType identifierType = patientService
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);

        patient = PatientMother.createTestInstance(location, identifierType);
    }

    @Test
    public void getProvidedItems_shouldCorrectlyGetItemsForPatient() {
        setAndSaveTestProvidedItems(ProcessStatus.PROCESSED);

        List<ProvidedItem> actualProvidedItems = providedItemService.getProvidedItems(patient.getPatientId(),
                ProcessStatus.PROCESSED);

        Assert.assertThat(actualProvidedItems.size(), is(expectedProvidedItems.size()));
        for (ProvidedItem item : actualProvidedItems) {
            Assert.assertThat(item, hasProperty("status", is(ProcessStatus.PROCESSED)));
        }

        expectedProvidedItems.sort(Comparator.comparing(ProvidedItem::getPrice));
        actualProvidedItems.sort(Comparator.comparing(ProvidedItem::getPrice));
        for (int i = 0; i < expectedProvidedItems.size(); i++) {
            Assert.assertThat(actualProvidedItems.get(i), is(expectedProvidedItems.get(i)));
        }
    }

    @Test
    public void getProvidedEnteredItems_shouldCorrectlyGetItemsForPatient() {
        setAndSaveTestProvidedItems(ProcessStatus.ENTERED);

        List<ProvidedItem> actualProvidedItems = providedItemService.getProvidedEnteredItems(patient.getPatientId());

        Assert.assertThat(actualProvidedItems.size(), is(expectedProvidedItems.size()));
        for (ProvidedItem item : actualProvidedItems) {
            Assert.assertThat(item, hasProperty("status", is(ProcessStatus.ENTERED)));
        }

        expectedProvidedItems.sort(Comparator.comparing(ProvidedItem::getPrice));
        actualProvidedItems.sort(Comparator.comparing(ProvidedItem::getPrice));
        for (int i = 0; i < expectedProvidedItems.size(); i++) {
            Assert.assertThat(expectedProvidedItems.get(i), is(actualProvidedItems.get(i)));
        }
    }

    private ProvidedItem createTestInstanceForProvidedItem(ProcessStatus processStatus, String price, Patient patient) {
        Concept concept = conceptService.getConcept(TestConstants.TEST_CONCEPT_ID);

        return ProvidedItemMother.createTestInstanceForProvidedItem(concept, patient, new BigDecimal(price),
                processStatus);
    }

    private void setAndSaveTestProvidedItems(ProcessStatus processStatus) {
        List<ProvidedItem> testProvidedItems = new ArrayList<>();

        for (String item : TestConstants.PRICES) {
            testProvidedItems.add(createTestInstanceForProvidedItem(processStatus, item, patient));
        }

        expectedProvidedItems = testProvidedItems;

        saveProvidedItems();
    }

    private void saveProvidedItems() {
        for (ProvidedItem item : expectedProvidedItems) {
            providedItemService.saveOrUpdate(item);
        }
    }
}
