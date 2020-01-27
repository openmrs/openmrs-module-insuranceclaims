package org.openmrs.module.insuranceclaims.api.service.impl;

import org.hibernate.Criteria;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.insuranceclaims.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.insuranceclaims.api.service.OpenmrsDataService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Transactional
public class BaseOpenmrsDataService<T extends BaseOpenmrsData> extends BaseOpenmrsService implements OpenmrsDataService<T> {

	private BaseOpenmrsDataDao<T> dao;

	public BaseOpenmrsDataService() {
	}

	public BaseOpenmrsDataService(BaseOpenmrsDataDao<T> dao) {
		this.dao = dao;
	}

	public void setDao(BaseOpenmrsDataDao<T> dao) {
		this.dao = dao;
	}

	@Override
	@Transactional(readOnly = true)
	public T getById(Serializable id) throws APIException {
		return dao.getById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public T getByUuid(String uuid) throws APIException {
		return dao.getByUuid(uuid);
	}

	@Override
	public void delete(T persistent) throws APIException {
		dao.delete(persistent);
	}

	@Override
	public T saveOrUpdate(T newOrPersisted) throws APIException {
		return dao.saveOrUpdate(newOrPersisted);
	}

	@Override
	public List<T> getAll(boolean includeVoided) throws APIException {
		return dao.getAll(includeVoided);
	}

	@Override
	public List<T> getAll(boolean includeVoided, Integer firstResult, Integer maxResults) throws APIException {
		return dao.getAll(includeVoided, firstResult, maxResults);
	}

	@Override
	public int getAllCount(boolean includeVoided) throws APIException {
		return dao.getAllCount(includeVoided);
	}

	@Override
	public List<T> getAllByCriteria(Criteria criteria, boolean includeVoided) {
		return dao.findAllByCriteria(criteria, includeVoided);
	}

	@Override
	public T getByCriteria(Criteria criteria, boolean includeVoided) {
		return dao.findByCriteria(criteria, includeVoided);
	}

	@Override
	public Criteria createCriteria() {
		return dao.createCriteria();
	}
}
