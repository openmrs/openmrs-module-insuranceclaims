package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.ItemDao;
import org.openmrs.module.insuranceclaims.api.model.Item;
import org.openmrs.module.insuranceclaims.api.mother.ItemMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

/**
 * It is an integration test (extends BaseModuleContextSensitiveTest), which verifies DAO methods
 * against the in-memory H2 database. The database is initially loaded with data from
 * standardTestDataset.xml in openmrs-api. All test methods are executed in transactions, which are
 * rolled back by the end of each test method.
 */
public class ItemDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private ItemDao dao;

	@Test
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		Item item = ItemMother.createTestInstance();

		dao.saveOrUpdate(item);

		Context.flushSession();
		Context.clearSession();

		Item savedItem = dao.getByUuid(item.getUuid());

		Assert.assertThat(savedItem, hasProperty("uuid", is(item.getUuid())));
		Assert.assertThat(savedItem, hasProperty("name", is(item.getName())));
		Assert.assertThat(savedItem, hasProperty("description", is(item.getDescription())));
		Assert.assertThat(savedItem, hasProperty("careService", is(item.isCareService())));
	}
}
