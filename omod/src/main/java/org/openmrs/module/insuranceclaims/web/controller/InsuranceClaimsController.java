package org.openmrs.module.insuranceclaims.web.controller;

import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * This class configured as controller using annotation and mapped with the URL of
 * 'module/insuranceclaims/insuranceclaimsLink.form'.
 */
@Controller("${rootrootArtifactId}.InsuranceClaimsController")
@RequestMapping(value = "module/insuranceclaims/insuranceclaims.form")
public class InsuranceClaimsController {

	/**
	 * Logger for this class and subclasses
	 */
	private static final Logger LOG = LoggerFactory.getLogger(InsuranceClaimsController.class);

	@Autowired
	private UserService userService;

	/**
	 * Success form view name
	 */
	private static final String VIEW = "/module/insuranceclaims/insuranceclaims";

	/**
	 * Initially called after the getUsers method to get the landing form name
	 *
	 * @return String form view name
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String onGet() {
		return VIEW;
	}

	/**
	 * All the parameters are optional based on the necessity
	 *
	 * @param httpSession - request session
	 * @param anyRequestObject - requested object
	 * @param errors - set of errors
	 * @return - url to redirect
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String onPost(HttpSession httpSession, @ModelAttribute("anyRequestObject") Object anyRequestObject,
			BindingResult errors) {

		if (errors.hasErrors()) {
			LOG.error("The error view should be return.");
			// return error view
		}

		return VIEW;
	}

	/**
	 * This class returns the form backing object. This can be a string, a boolean, or a normal java
	 * pojo. The bean name defined in the ModelAttribute annotation and the type can be just defined
	 * by the return type of this method
	 */
	@ModelAttribute("users")
	public List<User> getUsers() throws APIException {
		List<User> users = userService.getAllUsers();

		// this object will be made available to the jsp page under the variable name
		// that is defined in the @ModuleAttribute tag
		return users;
	}

}
