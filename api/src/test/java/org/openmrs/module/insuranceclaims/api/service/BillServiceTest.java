package org.openmrs.module.insuranceclaims.api.service;

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
import java.util.Arrays;
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
    public void generateBill_shouldCorrectlyGenerateBill() {
        setAndSaveTestProvidedItems(ProcessStatus.ENTERED, TestConstants.TEST_ENTERED_PRICES);
        setAndSaveTestProvidedItems(ProcessStatus.PROCESSED, TestConstants.TEST_PROCESSED_PRICES);


        List<ProvidedItem> actualProvidedItems = providedItemService.getProvidedEnteredItems(patient.getPatientId());

        Bill actualBill = billService.generateBill(actualProvidedItems);

        List<ProvidedItem> enteredProvidedItems = providedItemService.getProvidedItems(patient.getPatientId(),
                ProcessStatus.ENTERED);
        List<ProvidedItem> processedProvidedItems = providedItemService.getProvidedItems(patient.getPatientId(),
                ProcessStatus.PROCESSED);

        double sumProvidedItemsDouble = Arrays.asList(TestConstants.TEST_ENTERED_PRICES).stream()
                .mapToDouble(s -> Double.parseDouble(s)).sum();
        BigDecimal sumProvidedItems = new BigDecimal(Double.toString(sumProvidedItemsDouble));

        Bill expectedBill = BillMother.createTestInstanceWithAmount(sumProvidedItems);


        Assert.assertThat(actualBill, is(expectedBill));
        Assert.assertTrue(actualBill.getTotalAmount().compareTo(expectedBill.getTotalAmount()) == 0);
        Assert.assertTrue(enteredProvidedItems.isEmpty());
        Assert.assertTrue(processedProvidedItems.size() == (TestConstants.TEST_PROCESSED_PRICES.length
                + TestConstants.TEST_ENTERED_PRICES.length));
    }

    private ProvidedItem createTestInstanceForProvidedItem(ProcessStatus processStatus, String price, Patient patient) {
        Concept concept = conceptService.getConcept(TestConstants.TEST_CONCEPT_ID);

        return ProvidedItemMother.createTestInstanceForProvidedItem(concept, patient, new BigDecimal(price),
                processStatus);
    }

    private List<ProvidedItem> setAndSaveTestProvidedItems(ProcessStatus processStatus, String[] prices) {
        List<ProvidedItem> testProvidedItems = new ArrayList<>();

        for (String item : prices) {
            testProvidedItems.add(createTestInstanceForProvidedItem(processStatus, item, patient));
        }

        saveProvidedItems(testProvidedItems);

        return testProvidedItems;
    }

    private void saveProvidedItems(List<ProvidedItem> providedItems) {
        for (ProvidedItem item : providedItems) {
            providedItemService.saveOrUpdate(item);
        }
    }
}
