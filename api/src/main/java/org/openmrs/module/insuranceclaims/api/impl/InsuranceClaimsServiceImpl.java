/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.insuranceclaims.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.insuranceclaims.Item;
import org.openmrs.module.insuranceclaims.api.InsuranceClaimsService;
import org.openmrs.module.insuranceclaims.api.dao.InsuranceClaimsDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class InsuranceClaimsServiceImpl extends BaseOpenmrsService implements InsuranceClaimsService {
	
	private InsuranceClaimsDao dao;
	
	private UserService userService;

	public InsuranceClaimsServiceImpl() { }

	public  InsuranceClaimsServiceImpl(InsuranceClaimsDao dao, UserService userService) {
		this.dao = dao;
		this.userService = userService;
	}

	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(InsuranceClaimsDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Item getItemByUuid(String uuid) throws APIException {
		return dao.getItemByUuid(uuid);
	}
	
	@Override
	public Item saveItem(Item item) throws APIException {
		if (item.getOwner() == null) {
			item.setOwner(userService.getUser(1));
		}
		
		return dao.saveItem(item);
	}
}
