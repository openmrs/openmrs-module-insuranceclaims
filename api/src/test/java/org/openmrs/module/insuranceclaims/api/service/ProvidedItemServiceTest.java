package org.openmrs.module.insuranceclaims.api.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
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
import static org.hamcrest.core.IsNull.nullValue;

public class ProvidedItemServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ProvidedItemService providedItemService;

    @Test
    public void getProvidedItems_shouldCorrectlyGetItemsForPatient() {

        Patient patient = Context.getPatientService().getAllPatients().get(0);

        List<ProvidedItem> providedItems = new ArrayList<>();

        providedItems.add(createTestInstanceForProvidedItem(ProcessStatus.PROCESSED, TestConstants.PRICES[0], patient));
        providedItems.add(createTestInstanceForProvidedItem(ProcessStatus.PROCESSED, TestConstants.PRICES[1], patient));
        providedItems.add(createTestInstanceForProvidedItem(ProcessStatus.PROCESSED, TestConstants.PRICES[2], patient));
        providedItems.add(createTestInstanceForProvidedItem(ProcessStatus.PROCESSED, TestConstants.PRICES[2], patient));

        for (ProvidedItem item : providedItems) {
            providedItemService.saveOrUpdate(item);
        }

        providedItemService.saveOrUpdate(createTestInstanceForProvidedItem(ProcessStatus.ENTERED,
                TestConstants.PRICES[2], patient));
        providedItemService.saveOrUpdate(createTestInstanceForProvidedItem(ProcessStatus.ENTERED,
                TestConstants.PRICES[1], patient));

        List<ProvidedItem> providedItemsDb = providedItemService.getProvidedItems(patient.getPatientId(),
                ProcessStatus.PROCESSED);


        Assert.assertThat(providedItemsDb.size(), is(providedItems.size()));
        for (ProvidedItem item : providedItemsDb) {
            Assert.assertThat(item, hasProperty("status", is(ProcessStatus.PROCESSED)));
        }

        providedItems.sort(Comparator.comparing(ProvidedItem::getPrice));
        providedItemsDb.sort(Comparator.comparing(ProvidedItem::getPrice));
        for (int i = 0; i < providedItems.size(); i++) {
            Assert.assertThat(providedItems.get(i), is(providedItemsDb.get(i)));
        }
    }

    @Test
    public void getProvidedEnteredItems_shouldCorrectlyGetItemsForPatient() {

        Patient patient = Context.getPatientService().getAllPatients().get(0);

        List<ProvidedItem> providedItems = new ArrayList<>();

        for (String item : TestConstants.PRICES) {
            providedItems.add(createTestInstanceForProvidedItem(ProcessStatus.ENTERED, item, patient));
        }

        for (ProvidedItem item : providedItems) {
            providedItemService.saveOrUpdate(item);
        }

        providedItemService.saveOrUpdate(createTestInstanceForProvidedItem(ProcessStatus.PROCESSED,
                TestConstants.PRICES[0], patient));
        providedItemService.saveOrUpdate(createTestInstanceForProvidedItem(ProcessStatus.PROCESSED,
                TestConstants.PRICES[1], patient));

        List<ProvidedItem> providedEnteredItems = providedItemService.getProvidedEnteredItems(patient.getPatientId());


        Assert.assertThat(providedEnteredItems.size(), is(providedItems.size()));
        for (ProvidedItem item : providedEnteredItems) {
            Assert.assertThat(item, hasProperty("status", is(ProcessStatus.ENTERED)));
            Assert.assertThat(item, hasProperty("bill", is(nullValue())));
        }

        providedItems.sort(Comparator.comparing(ProvidedItem::getPrice));
        providedEnteredItems.sort(Comparator.comparing(ProvidedItem::getPrice));
        for (int i = 0; i < providedItems.size(); i++) {
            Assert.assertThat(providedItems.get(i), is(providedEnteredItems.get(i)));
        }
    }

    private ProvidedItem createTestInstanceForProvidedItem(ProcessStatus processStatus, String price, Patient patient) {
        Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);

        return ProvidedItemMother.createTestInstanceForProvidedItem(concept, patient, new BigDecimal(price),
                processStatus);
    }
}
