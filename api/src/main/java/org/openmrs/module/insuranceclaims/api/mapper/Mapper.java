package org.openmrs.module.insuranceclaims.api.mapper;

import org.openmrs.BaseOpenmrsData;

import java.util.List;

public interface Mapper<T, R extends BaseOpenmrsData> {

    T toDto(R dao);

    List<T> toDtos(List<R> daos);
}
