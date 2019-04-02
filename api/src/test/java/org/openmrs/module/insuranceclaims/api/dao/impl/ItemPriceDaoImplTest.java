package org.openmrs.module.insuranceclaims.api.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.dao.ItemPriceDao;
import org.openmrs.module.insuranceclaims.api.model.ItemPrice;
import org.openmrs.module.insuranceclaims.api.mother.ItemPriceMother;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class ItemPriceDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private ItemPriceDao dao;

	@Test
	public void saveItemPrice_shouldSaveAllPropertiesInDb() {
		ItemPrice price = ItemPriceMother.createTestInstance();

		dao.saveItemPrice(price);

		Context.flushSession();
		Context.clearSession();

		ItemPrice savedPrice = dao.getItemPriceByUuid(price.getUuid());

		Assert.assertThat(savedPrice, hasProperty("uuid", is(price.getUuid())));
		Assert.assertThat(savedPrice, hasProperty("price", is(price.getPrice())));
		Assert.assertThat(savedPrice, hasProperty("name", is(price.getName())));
		Assert.assertThat(savedPrice, hasProperty("item", is(price.getItem())));

		ItemPrice samePrice = dao.getItemPriceById(savedPrice.getId());
		Assert.assertEquals(savedPrice, samePrice);
	}
}
