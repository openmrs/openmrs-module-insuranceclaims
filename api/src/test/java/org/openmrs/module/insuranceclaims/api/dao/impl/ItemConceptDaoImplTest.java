package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.ItemConceptDao;
import org.openmrs.module.insuranceclaims.api.model.ItemConcept;
import org.openmrs.module.insuranceclaims.api.mother.ItemConceptMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class ItemConceptDaoImplTest extends BaseModuleContextSensitiveTest {

	private static final int TEST_CONCEPT_ID = 3;

	@Autowired
	private ItemConceptDao dao;

	@Test
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		Concept concept = Context.getConceptService().getConcept(TEST_CONCEPT_ID);
		ItemConcept itemConcept = ItemConceptMother.createTestInstanceWithConcept(concept);

		dao.saveOrUpdate(itemConcept);

		Context.flushSession();
		Context.clearSession();

		ItemConcept savedItemConcept = dao.getByUuid(itemConcept.getUuid());

		Assert.assertThat(savedItemConcept, hasProperty("uuid", is(itemConcept.getUuid())));
		Assert.assertThat(savedItemConcept, hasProperty("concept", is(itemConcept.getConcept())));
		Assert.assertThat(savedItemConcept, hasProperty("item", is(itemConcept.getItem())));
	}
}
