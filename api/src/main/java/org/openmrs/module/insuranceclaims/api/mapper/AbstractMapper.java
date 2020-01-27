package org.openmrs.module.insuranceclaims.api.mapper;

import org.openmrs.BaseOpenmrsData;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMapper<T, R extends BaseOpenmrsData> implements Mapper<T, R> {

    public List<T> toDtos(List<R> daos) {
        List<T> dtos = new ArrayList<T>();
        for (R dao : daos) {
            dtos.add(toDto(dao));
        }
        return dtos;
    }
}
