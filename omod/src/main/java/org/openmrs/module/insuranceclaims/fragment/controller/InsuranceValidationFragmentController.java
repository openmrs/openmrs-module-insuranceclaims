package org.openmrs.module.insuranceclaims.fragment.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.module.insuranceclaims.util.ConstantValues;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

public class InsuranceValidationFragmentController {

    private static final String WIDGET_MODE_KEY = "widgetMode";

    private static final String INITIAL_VALUE_KEY = "initialValue";

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
        if (StringUtils.isNotBlank(personUuid)) {
            Person person = personService.getPersonByUuid(personUuid);
            PersonAttribute attribute = person.getAttribute(ConstantValues.POLICY_NUMBER_ATTRIBUTE_TYPE_NAME);
            if (attribute != null) {
                config.put(INITIAL_VALUE_KEY, attribute.getValue());
            }
            model.put(WIDGET_MODE_KEY, true);
        }
    }

}
