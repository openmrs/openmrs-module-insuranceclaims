package org.openmrs.module.insuranceclaims.api.service.fhir.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class InsuranceClaimConstants {
    public static final String CATEGORY_SERVICE = "service";
    public static final String CATEGORY_ITEM = "item";

    public static final String CLAIM_REFERENCE = "Claim";
    public static final String COMMUNICATION_REQUEST = "CommunicationRequest";
    public static final String DEFAULT_ERROR_CODE = "0"; // Used when no error occurs
    public static final String PERIOD_FROM = "from";
    public static final String PERIOD_TO = "to";
    public static final String MISSING_DATE_FROM = "Date 'from' is missing";
    public static final String MISSING_DATE_TO = "Date 'to' is missing";
    public static final String MISSING_DATE_CREATED = "Date 'to' is missing";

    public static final String MEDICAL_RECORD_NUMBER = "MR";
    public static final String ACCESSION_ID = "ACSN";
    public static final String HL7_VALUESET_SYSTEM = "https://hl7.org/fhir/valueset-identifier-type.html";

    public static final int ENUMERATION_FROM = 1;
    public static final int SEQUENCE_FIRST = 1;
    public static final int NEXT_SEQUENCE = 1;

    public static final String ITEM_ADJUDICATION_GENERAL_CATEGORY = "general";
    public static final String ITEM_ADJUDICATION_REJECTION_REASON_CATEGORY = "rejected_reason";

    public static final String PROVIDER_EXTERNAL_ID_ATTRIBUTE_UUID = "bbdf67e8-c020-40ff-8ad6-74ba34893882";
    public static final String LOCATION_EXTERNAL_ID_ATTRIBUTE_UUID = "217da59b-6003-43b9-9595-b5c1349f1152";
    public static final String PATIENT_EXTERNAL_ID_IDENTIFIER_UUID = "ee8e82c4-1563-43aa-8c73-c3e4e88cb79b";

    public static final String ELEMENTS = "Elements";

    public static final String IS_SERVICE_CONCEPT_ATTRIBUTE_UUID = "925e4987-3104-4d74-989b-3ec96197b532";
    public static final String CONCEPT_PRICE_ATTRIBUTE_UUID = "ddc082c8-db30-4796-890e-f0d487fb9085";
    public static final String EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME = "ExternalCode";
    public static final String PRIMARY_DIAGNOSIS_MAPPING = "insuranceclaims.diagnosisPrimaryCode"; //TODO: Get from global value

    public static final String GUARANTEE_ID_CATEGORY = "guarantee_id";
    public static final String EXPLANATION_CATEGORY = "explanation";
    public static final String ITEM_EXPLANATION_CATEGORY = "item_explanation";

    public static final String CONTRACT = "Contract";
    public static final int CONTRACT_POLICY_ID_ORDINAL = 1;
    public static final int CONTRACT_EXPIRE_DATE_ORDINAL = 2;

    public static final String EXPECTED_DATE_PATTERN = "yyyy-MM-dd";
    public static final List<String> CONTRACT_DATE_PATTERN = Collections.unmodifiableList(
            Arrays.asList("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd"));

    public static final String CONSUMED_ITEMS_CONCEPT_NAME = "Items consumed";
    public static final String CONSUMED_ITEMS_CONCEPT_UUID = "907519e6-4b90-473e-b1db-5167352ddcd0";

    public static final String CONSUMABLES_LIST_CONCEPT_NAME = "CONSUMABLES LIST";
    public static final String CONSUMABLES_LIST_ITEMS_CONCEPT_UUID = "df3f4aab-0e18-43cb-89bf-03ec347faa4a";

    public static final String QUANTITY_CONSUMED_CONCEPT_NAME = "QUANTITY CONSUMED";
    public static final String QUANTITY_CONSUMED_CONCEPT_UUID = "dd75407b-bbb3-465b-976d-023b4d79ac54";
    public static final double ABSOULUTE_LOW_CONSUMED_ITEMS = 1.0;
    public static final double ABSOULUTE_HI_CONSUMED_ITEMS = 1.0;

    public static final String CONSUMED_ITEMS_FORM_NAME = "Consumed Items";
    public static final String CONSUMED_ITEMS_FORM_DESCRIPTION = "Used to add information about services and items consumed by the patient.";
    public static final String CONSUMED_ITEMS_FORM_UUID = "2da13321-5829-41d3-b11c-68520b5e4da4";

    public static final String CONSUMED_ITEM_STRATEGY_PROPERTY = "insuranceclaims.consumeditem.strategy";

    public static final String OPENMRS_ID_DEFAULT_IDENTIFIER_SOURCE = "691eed12-c0f1-11e2-94be-8c13b969e334";
    public static final String OPENMRS_ID_DEFAULT_TYPE = "05a29f94-c0ed-11e2-94be-8c13b969e334";

    private InsuranceClaimConstants() {}
}