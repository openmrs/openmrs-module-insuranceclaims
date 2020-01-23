package org.openmrs.module.insuranceclaims.fragment.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.module.insuranceclaims.api.mapper.InsurancePolicyMapper;
import org.openmrs.module.insuranceclaims.api.model.InsurancePolicy;
import org.openmrs.module.insuranceclaims.api.model.dto.InsurancePolicyDTO;
import org.openmrs.module.insuranceclaims.api.service.InsurancePolicyService;
import org.openmrs.module.insuranceclaims.api.service.exceptions.EligibilityRequestException;
import org.openmrs.module.insuranceclaims.api.service.request.ExternalApiRequest;
import org.openmrs.module.insuranceclaims.util.ConstantValues;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InsuranceValidationFragmentController {

    private static final String WIDGET_MODE_KEY = "widgetMode";

    private static final String INITIAL_VALUE_KEY = "initialValue";

    private static final String PERSON_UUID_KEY = "personUuid";

    private static final String POLICY_NUMBER_KEY = "policyNumber";

    private static final String RESULTS_KEY = "results";

    /**
     * Used to initialize the InsuranceValidation fragment
     * @param model - the fragment model
     * @param config - the fragment configuration
     * @param personService - auto injected personService Spring bean
     * @param personUuid - optional value of person Uuid - used to distinguish registration form from a patient dashboard
     */
    public void controller(FragmentModel model, FragmentConfiguration config,
            @SpringBean(value = "personService") PersonService personService,
            @RequestParam(value = "patientId", required = false) String personUuid) {
        model.put(WIDGET_MODE_KEY, false);
        model.put(PERSON_UUID_KEY, personUuid);
        if (StringUtils.isNotBlank(personUuid)) {
            Person person = personService.getPersonByUuid(personUuid);
            PersonAttribute attribute = person.getAttribute(ConstantValues.POLICY_NUMBER_ATTRIBUTE_TYPE_NAME);
            if (attribute != null) {
                config.put(INITIAL_VALUE_KEY, attribute.getValue());
            }
            model.put(WIDGET_MODE_KEY, true);
        }
    }

    public SimpleObject verify(
            @SpringBean(value = "insuranceclaims.InsurancePolicyService") InsurancePolicyService policyService,
            @SpringBean(value = "insuranceclaims.insurancePolicyMapper") InsurancePolicyMapper insurancePolicyMapper,
            @SpringBean(value = "insuranceclaims.ExternalApiRequest") ExternalApiRequest externalApiRequest,
            @RequestParam(value = POLICY_NUMBER_KEY, required = false) String policyNumber,
            @RequestParam(value = PERSON_UUID_KEY, required = false) String personUuid) throws EligibilityRequestException {
        SimpleObject requestResponse = new SimpleObject();
        List<InsurancePolicyDTO> results = new ArrayList<>();
        InsurancePolicy policy = externalApiRequest.getPatientPolicy(policyNumber);
        if (policy != null) {
            results = Collections.singletonList(insurancePolicyMapper.toDto(policy));
        }
        if (StringUtils.isNotBlank(personUuid)) {
            results = policyService.addOrUpdatePolicy(personUuid, policy);
        }
        requestResponse.put(RESULTS_KEY, results);
        return requestResponse;
    }

    public SimpleObject actualPolices(
            @SpringBean(value = "insuranceclaims.InsurancePolicyService") InsurancePolicyService policyService,
            @RequestParam(value = PERSON_UUID_KEY, required = false) String personUuid) {
        SimpleObject requestResponse = new SimpleObject();
        List<InsurancePolicyDTO> results = new ArrayList<>();
        if (StringUtils.isNotBlank(personUuid)) {
            results = policyService.getForPerson(personUuid);
        }
        requestResponse.put(RESULTS_KEY, results);
        return requestResponse;
    }

}
