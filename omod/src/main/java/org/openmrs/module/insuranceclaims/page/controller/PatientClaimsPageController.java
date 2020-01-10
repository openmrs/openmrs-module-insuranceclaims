package org.openmrs.module.insuranceclaims.page.controller;

import org.openmrs.Patient;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.insuranceclaims.api.model.InsuranceClaim;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class PatientClaimsPageController {

	/**
	 * Success form view name
	 */
	private static final String VIEW = "patientClaims";


	public String get(PageModel model,
					  @RequestParam(value = "patientId", required = true) int patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		List<InsuranceClaim> patientClaim = new ArrayList<>();
		model.addAttribute("patientName", patient.getPersonName());
		model.addAttribute("patientId", patient.getId());
		model.addAttribute("patientClaims", patientClaim);
		return VIEW;
	}
}
