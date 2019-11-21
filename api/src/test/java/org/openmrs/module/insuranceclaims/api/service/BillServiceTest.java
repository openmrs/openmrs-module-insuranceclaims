package org.openmrs.module.insuranceclaims.api.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.mother.BillMother;
import org.openmrs.module.insuranceclaims.api.mother.ProvidedItemMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;

public class BillServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ProvidedItemService providedItemService;

    @Autowired
    private BillService billService;

    @Test
    public void getProvidedItems_shouldCorrectlyGetItemsForPatient() {

        Patient patient = Context.getPatientService().getAllPatients().get(0);

        List<ProvidedItem> providedItems = new ArrayList<>();

        for (String item : TestConstants.PRICES) {
            providedItems.add(createTestInstanceForProvidedItem(ProcessStatus.ENTERED, item, patient));
        }

        for (ProvidedItem item : providedItems) {
            providedItemService.saveOrUpdate(item);
        }

        List<ProvidedItem> providedEnteredItems = providedItemService.getProvidedEnteredItems(patient.getPatientId());

        Bill billDb = billService.generateBill(providedEnteredItems);

        List<ProvidedItem> providedEnteredItemsDb = providedItemService.getProvidedItems(patient.getPatientId(),
                ProcessStatus.ENTERED);

        BigDecimal sumProvideItems = new BigDecimal("100005741.99");
        Bill bill = createTestInstanceWithAmount(sumProvideItems);

        Assert.assertThat(billDb.getPaymentStatus(), is(bill.getPaymentStatus()));
        Assert.assertThat(billDb.getTotalAmount(), is(bill.getTotalAmount()));
        Assert.assertThat(providedEnteredItemsDb.size(), is(0));
    }

    private Bill createTestInstanceWithAmount(BigDecimal totalAmount) {
        return BillMother.createTestInstanceWithAmount(totalAmount);
    }

    private ProvidedItem createTestInstanceForProvidedItem(ProcessStatus processStatus, String price, Patient patient) {
        Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);

        return ProvidedItemMother.createTestInstanceForProvidedItem(concept, patient, new BigDecimal(price),
                processStatus);
    }
}
