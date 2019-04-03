package org.openmrs.module.insuranceclaims.api.dao.converter;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DateTimestampConverter implements AttributeConverter<Date, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(Date date) {
		return date != null ? new Timestamp(date.getTime()) : null;
	}

	@Override
	public Date convertToEntityAttribute(Timestamp timestamp) {
		return timestamp != null ? new Date(timestamp.getTime()) : null;
	}
}
