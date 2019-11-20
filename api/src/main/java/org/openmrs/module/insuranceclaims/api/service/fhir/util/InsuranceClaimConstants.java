package org.openmrs.module.insuranceclaims.api.service.fhir.util;

public final class InsuranceClaimConstants {
    public static final String CATEGORY_SERVICE = "service";
    public static final String CATEGORY_ITEM = "item";

    public static final String CLAIM_REFERENCE = "Claim";
    public static final String COMMUNICATION_REQUEST = "CommunicationRequest";
    public static final String DEFAULT_ERROR_CODE = "0"; // Used when no error occurs
    public static final String PERIOD_FROM = "from";
    public static final String PERIOD_TO = "to";

    public static final String MEDICAL_RECORD_NUMBER = "MR";
    public static final String ACCESSION_ID = "ACSN";
    public static final String HL7_VALUESET_SYSTEM = "https://hl7.org/fhir/valueset-identifier-type.html";
    public static final int ENUMERATION_FROM = 1;


    public static final String PROVIDER_EXTERNAL_ID_ATTRIBUTE_UUID = "bbdf67e8-c020-40ff-8ad6-74ba34893882";
    public static final String LOCATION_EXTERNAL_ID_ATTRIBUTE_UUID = "217da59b-6003-43b9-9595-b5c1349f1152";
    public static final String PATIENT_EXTERNAL_ID_IDENTIFIER_UUID = "ee8e82c4-1563-43aa-8c73-c3e4e88cb79b";

    public static final String IS_SERVICE_CONCEPT_ATTRIBUTE_UUID = "925e4987-3104-4d74-989b-3ec96197b532";
    public static final String CONCEPT_PRICE_ATTRIBUTE_UUID = "ddc082c8-db30-4796-890e-f0d487fb9085";
    public static final String EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_UUID = "e730f72a-2789-4d82-8e71-d7707babc0e6";
    public static final String EXTERNAL_SYSTEM_CODE_SOURCE_MAPPING_NAME = "External Code";
    public static final String PRIMARY_DIAGNOSIS_MAPPING = "icd_0"; //TODO: Get from global value

    private InsuranceClaimConstants() {}
}