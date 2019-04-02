package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.ItemConceptDao;
import org.openmrs.module.insuranceclaims.api.model.ItemConcept;
import org.openmrs.module.insuranceclaims.api.mother.ItemConceptMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class ItemConceptDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private ItemConceptDao dao;

	@Test
	public void saveItemConcept_shouldSaveAllPropertiesInDb() {
		ItemConcept itemConcept = ItemConceptMother.createTestInstance();

		dao.saveItemConcept(itemConcept);

		Context.flushSession();
		Context.clearSession();

		ItemConcept savedItemConcept = dao.getItemConceptByUuid(itemConcept.getUuid());

		Assert.assertThat(savedItemConcept, hasProperty("uuid", is(itemConcept.getUuid())));
		Assert.assertThat(savedItemConcept, hasProperty("concept", is(itemConcept.getConcept())));
		Assert.assertThat(savedItemConcept, hasProperty("item", is(itemConcept.getItem())));

		ItemConcept sameItemConcept = dao.getItemConceptById(savedItemConcept.getId());
		Assert.assertEquals(savedItemConcept, sameItemConcept);
	}
}
