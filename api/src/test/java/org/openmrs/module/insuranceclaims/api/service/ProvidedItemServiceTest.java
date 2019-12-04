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
import java.util.List;

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
    public void getProvidedProcessedItems_shouldCorrectlyGetItemsForPatient() {
        expectedProvidedItems = getAndSaveTestProvidedItems(ProcessStatus.PROCESSED,
                TestConstants.TEST_PROCESSED_PRICES);

        List<ProvidedItem> actualProvidedItems = providedItemService.getProvidedItems(patient.getPatientId(),
                ProcessStatus.PROCESSED);

        Assert.assertThat(actualProvidedItems.size(), is(expectedProvidedItems.size()));
        Assert.assertArrayEquals(expectedProvidedItems.toArray(), actualProvidedItems.toArray());
    }

    @Test
    public void getProvidedEnteredItems_shouldCorrectlyGetItemsForPatient() {
        expectedProvidedItems = getAndSaveTestProvidedItems(ProcessStatus.ENTERED, TestConstants.TEST_ENTERED_PRICES);

        List<ProvidedItem> actualProvidedItems = providedItemService.getProvidedEnteredItems(patient.getPatientId());

        Assert.assertThat(actualProvidedItems.size(), is(expectedProvidedItems.size()));
        Assert.assertArrayEquals(expectedProvidedItems.toArray(), actualProvidedItems.toArray());
    }

    private ProvidedItem createTestInstanceForProvidedItem(ProcessStatus processStatus, String price, Patient patient) {
        Concept concept = conceptService.getConcept(TestConstants.TEST_CONCEPT_ID);

        return ProvidedItemMother.createTestInstanceForProvidedItem(concept, patient, new BigDecimal(price),
                processStatus);
    }

    private List<ProvidedItem> getAndSaveTestProvidedItems(ProcessStatus processStatus, double[] prices) {
        List<ProvidedItem> testProvidedItems = new ArrayList<>();

        for (double item : prices) {
            testProvidedItems.add(createTestInstanceForProvidedItem(processStatus, String.valueOf(item), patient));
        }

        saveProvidedItems(testProvidedItems);

        return testProvidedItems;
    }

    private void saveProvidedItems(List<ProvidedItem> providedItems) {
        for (ProvidedItem item : providedItems) {
            providedItemService.getProvidedItemDao().saveOrUpdate(item);
        }
    }
}
