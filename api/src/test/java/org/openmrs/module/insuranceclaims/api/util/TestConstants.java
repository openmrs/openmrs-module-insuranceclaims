package org.openmrs.module.insuranceclaims.api.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class TestConstants {

	public static final int TEST_LOCATION_ID = 10547108;

	public static final int TEST_PROVIDER_ID = 912093;

	public static final int TEST_VISIT_TYPE_ID = 1;

	public static final int TEST_IDENTIFIER_TYPE_ID = 2;

	public static final int TEST_CONCEPT_ID = 3;

	public static final String EXTERNAL_ID_DATASET_PATH = "test_externalId_attribute_types.xml";

	public static final String INSURANCE_CLAIM_TEST_ITEM_CONCEPT_DATASET = "test_item_concept.xml";

	public static final String INSURANCE_CLAIM_TEST_DIAGNOSIS_DATASET = "test_malaria_concept.xml";

	public static final String TEST_SERVICE_UUID = "160148BAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

	public static final String TEST_SERVICE_CODE = "M9";

	public static final String TEST_PATIENT_POLICY_NUMBER = "1234567890";

	public static final String TEST_DATE = "2010-10-10 00:00:00";

	public static final double[] TEST_ENTERED_PRICES = {30, 150, 0, 20, 500, 100};

	public static final double[] TEST_PROCESSED_PRICES = {100, 500, 50, 0};

	public static final String TEST_URL = "http://example.com/that";

	public static final BigDecimal TEST_PATIENT_POLICY_ALLOWED_MONEY = new BigDecimal(1253221);

	public static final Date TEST_PATIENT_POLICY_EXPIRY_DATE = new GregorianCalendar(2010, Calendar.NOVEMBER, 16).getTime();

	private TestConstants() {
	}
}
