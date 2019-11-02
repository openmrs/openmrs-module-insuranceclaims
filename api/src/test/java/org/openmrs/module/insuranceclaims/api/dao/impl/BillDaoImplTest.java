package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.BillDao;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.mother.BillMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class BillDaoImplTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private BillDao billDao;

    @Test
    public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
        Bill bill = createTestInstance();

        billDao.saveOrUpdate(bill);

        Context.flushSession();
        Context.clearSession();

        Bill savedBill = billDao.getByUuid(bill.getUuid());

        Assert.assertThat(savedBill, hasProperty("uuid", is(bill.getUuid())));
        Assert.assertThat(savedBill, hasProperty("startDate", is(bill.getStartDate())));
        Assert.assertThat(savedBill, hasProperty("endDate", is(bill.getEndDate())));
        Assert.assertThat(savedBill, hasProperty("totalAmount", is(bill.getTotalAmount())));
        Assert.assertThat(savedBill, hasProperty("paymentStatus", is(bill.getPaymentStatus())));
        Assert.assertThat(savedBill, hasProperty("paymentType", is(bill.getPaymentType())));
        Assert.assertThat(savedBill, hasProperty("diagnosis", is(bill.getDiagnosis())));
    }

    private Bill createTestInstance() {
        Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
        return BillMother.createTestInstance(concept);
    }

}
