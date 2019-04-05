package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.ItemCodeDao;
import org.openmrs.module.insuranceclaims.api.model.ItemCode;
import org.openmrs.module.insuranceclaims.api.mother.ItemCodeMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class ItemCodeDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private ItemCodeDao dao;

	@Test
	public void saveOrUpdate_shouldSaveAllPropertiesInDb() {
		ItemCode itemCode = ItemCodeMother.createTestInstance();

		dao.saveOrUpdate(itemCode);

		Context.flushSession();
		Context.clearSession();

		ItemCode savedItemCode = dao.getByUuid(itemCode.getUuid());

		Assert.assertThat(savedItemCode, hasProperty("uuid", is(itemCode.getUuid())));
		Assert.assertThat(savedItemCode, hasProperty("code", is(itemCode.getCode())));
		Assert.assertThat(savedItemCode, hasProperty("item", is(itemCode.getItem())));
	}
}
