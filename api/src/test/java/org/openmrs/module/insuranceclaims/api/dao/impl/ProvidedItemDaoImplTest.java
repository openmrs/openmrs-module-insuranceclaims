package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.ProvidedItemDao;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.mother.ProvidedItemMother;
import org.openmrs.module.insuranceclaims.api.util.TestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class ProvidedItemDaoImplTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ProvidedItemDao providedItemDao;

    @Test
    public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
        ProvidedItem providedItem = createTestInstance();

        providedItemDao.saveOrUpdate(providedItem);

        Context.flushSession();
        Context.clearSession();

        ProvidedItem savedProvidedItem = providedItemDao.getByUuid(providedItem.getUuid());

        Assert.assertThat(savedProvidedItem, hasProperty("uuid", is(providedItem.getUuid())));
        Assert.assertThat(savedProvidedItem, hasProperty("price", is(providedItem.getPrice())));
        Assert.assertThat(savedProvidedItem, hasProperty("dateOfServed", is(providedItem.getDateOfServed())));
        Assert.assertThat(savedProvidedItem, hasProperty("item", is(providedItem.getItem())));
        Assert.assertThat(savedProvidedItem, hasProperty("patient", is(providedItem.getPatient())));
        Assert.assertThat(savedProvidedItem, hasProperty("status", is(providedItem.getStatus())));
        Assert.assertThat(savedProvidedItem, hasProperty("bill", is(providedItem.getBill())));
    }

    private ProvidedItem createTestInstance() {
        Concept concept = Context.getConceptService().getConcept(TestConstants.TEST_CONCEPT_ID);
        Location location = Context.getLocationService().getLocation(TestConstants.TEST_LOCATION_ID);
        PatientIdentifierType patientIdentifierType = Context.getPatientService()
                .getPatientIdentifierType(TestConstants.TEST_IDENTIFIER_TYPE_ID);
        return ProvidedItemMother.createTestInstance(concept, location, patientIdentifierType);
    }
}
