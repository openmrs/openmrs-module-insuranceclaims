package org.openmrs.module.insuranceclaims.page.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.insuranceclaims.api.service.exceptions.PatientRequestException;
import org.openmrs.module.insuranceclaims.api.service.request.ExternalApiRequest;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class ImportPatientPageController {

	private static final String VIEW = "importPatient";

	@RequestMapping(method = RequestMethod.GET)
	public String get(PageModel model,
					  @SpringBean ExternalApiRequest externalApiRequest,
					  @RequestParam(value = "externalPatientId", required = false) String externalPatientId) {
		addBaseModelAttributes(model);
		model.addAttribute("externalPatientId", externalPatientId);

		if (!StringUtils.isEmpty(externalPatientId)) {
			try {
				Patient patient = externalApiRequest.getPatient(externalPatientId);
				model.addAttribute("patient", patient);
				model.addAttribute("valid", true);
			} catch (PatientRequestException patientReuqestException) {
				model.addAttribute("exception", patientReuqestException.getMessage());
			}
		}

		return VIEW;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(PageModel model,
					   @SpringBean ExternalApiRequest externalApiRequest,
					   @RequestParam(value = "externalPatientId", required = false) String externalPatientId) {
		addBaseModelAttributes(model);
		model.addAttribute("externalPatientId", externalPatientId);

		if (!StringUtils.isEmpty(externalPatientId)) {
			try {
				Patient patient  = externalApiRequest.importPatient(externalPatientId);
				model.addAttribute("patient", patient);
				model.addAttribute("created", true);
			} catch (PatientRequestException patientRequestException) {
				model.addAttribute("exception", patientRequestException.getMessage());
			}
		}

		return VIEW;
	}

	private void addBaseModelAttributes(PageModel model) {
		model.addAttribute("valid", false);
		model.addAttribute("created", false);
		model.addAttribute("patient", null);
	}
}
