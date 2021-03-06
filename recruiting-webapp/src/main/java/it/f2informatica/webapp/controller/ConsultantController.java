/*
 * =============================================================================
 *
 *   Copyright (c) 2014, Fernando Aspiazu
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package it.f2informatica.webapp.controller;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import it.f2informatica.core.model.ConsultantModel;
import it.f2informatica.core.model.EducationModel;
import it.f2informatica.core.model.ExperienceModel;
import it.f2informatica.core.model.LanguageModel;
import it.f2informatica.core.model.query.ConsultantSearchCriteria;
import it.f2informatica.core.services.ConsultantService;
import it.f2informatica.core.validator.ConsultantEducationValidator;
import it.f2informatica.core.validator.ConsultantExperienceValidator;
import it.f2informatica.core.validator.ConsultantPersonalDetailsValidator;
import it.f2informatica.core.validator.utils.ValidationResponse;
import it.f2informatica.core.validator.utils.ValidationResponseHandler;
import it.f2informatica.webapp.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static it.f2informatica.webapp.utils.MediaTypeUTF8.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/consultant")
@SessionAttributes({"consultantId"})
public class ConsultantController {

	@Autowired
	private Gson gson;

	@Autowired
	private MonthHelper monthHelper;

	@Autowired
	private PeriodParser periodParser;

	@Autowired
	private HttpRequest httpRequest;

	@Autowired
	private ConsultantService consultantService;

	@Autowired
	private ConsultantEducationValidator educationValidator;

	@Autowired
	private ConsultantExperienceValidator experienceValidator;

	@Autowired
	private ValidationResponseHandler validationResponseHandler;

	@Autowired
	private ConsultantPersonalDetailsValidator personalDetailsValidator;

	@ModelAttribute("months")
	public List<Month> loadMonths() {
		return monthHelper.getMonths();
	}

	@RequestMapping(value = "/search", method = POST)
	public String searchConsultants(@ModelAttribute("searchCriteria") ConsultantSearchCriteria searchCriteria,
	                                Pageable pageable, ModelMap model) {

		Pageable pageRequest = new PageRequest(pageable.getPageNumber(), 5, Sort.Direction.DESC, "registrationDate");
		model.addAttribute("page", consultantService.paginateConsultants(searchCriteria, pageRequest));
		model.addAttribute("searchCriteria", new ConsultantSearchCriteria());
		return "consultant/consultants";
	}

	@RequestMapping(value = "/save-personal-details", method = POST)
	public String savePersonalDetails(@ModelAttribute("consultantModel") ConsultantModel consultantModel) {
		consultantService.savePersonalDetails(consultantModel);
		return "redirect:/consultants";
	}

	@RequestMapping(value = "/edit-personal-details", method = GET)
	public String editPersonalDetails(@RequestParam("consultantId") String consultantId, ModelMap model) {
		Optional<ConsultantModel> consultant = consultantService.findConsultantById(consultantId);
		if (consultant.isPresent()) {
			model.addAttribute("edit", true);
			model.addAttribute("consultantId", consultantId);
			model.addAttribute("consultantModel", consultant.get());
			return "consultant/consultantForm";
		}
		return pageNotFound();
	}

	@RequestMapping(value = "/update-personal-details", method = POST)
	public String updatePersonalDetails(@ModelAttribute("consultantModel") ConsultantModel consultantModel) {
		consultantService.updatePersonalDetails(consultantModel, consultantModel.getId());
		return "redirect:/consultants";
	}

	@RequestMapping(value = "/validate-personal-details", method = POST, produces = JSON_UTF_8)
	@ResponseBody
	public ValidationResponse validatePersonalDetails(@ModelAttribute("consultantModel") ConsultantModel consultantModel, BindingResult result) {
		personalDetailsValidator.validate(consultantModel, result);
		if (result.hasErrors()) {
			return validationResponseHandler.validationFail(result, httpRequest.getLocale());
		}
		return validationResponseHandler.validationSuccess();
	}

	@RequestMapping(value = "/profile", method = GET)
	public String profilePage(@RequestParam String consultantId, ModelMap model) {
		Optional<ConsultantModel> consultant = consultantService.findConsultantById(consultantId);
		if (consultant.isPresent()) {
			setTotalTimeOfPeriodWhichHasElapsed(consultant.get());
			model.addAttribute("consultantId", consultantId);
			model.addAttribute("consultantModel", consultant.get());
			model.addAttribute("experienceModel", new ExperienceModel());
			model.addAttribute("educationModel", new EducationModel());
			return "consultant/profileForm";
		}
		return pageNotFound();
	}

	private void setTotalTimeOfPeriodWhichHasElapsed(ConsultantModel consultantModel) {
		for (ExperienceModel experienceModel : consultantModel.getExperiences()) {
			Date from = experienceModel.getPeriodFrom();
			Date to = experienceModel.getPeriodTo();
			experienceModel.setTotalPeriodElapsed(periodParser.printTotalTimeOfPeriodWhichHasElapsed(from, to));
		}
	}

	@RequestMapping(value = "/edit-experience", method = GET, produces = JSON_UTF_8)
	@ResponseBody
	public String editExperience(@ModelAttribute("consultantId") String consultantId,
	                             @RequestParam String experienceId) {

		Optional<ExperienceModel> experience = consultantService.findExperience(consultantId, experienceId);
		if (experience.isPresent()) {
			formatDateByMonthNameAndYear(experience.get());
			return gson.toJson(experience.get());
		}
		return pageNotFound();
	}

	private void formatDateByMonthNameAndYear(ExperienceModel experienceModel) {
		experienceModel.setFormattedPeriodFrom(periodParser.formatDateByMonthNameAndYear(experienceModel.getPeriodFrom()));
		if (!experienceModel.isCurrent()) {
			experienceModel.setFormattedPeriodTo(periodParser.formatDateByMonthNameAndYear(experienceModel.getPeriodTo()));
		}
	}

	@RequestMapping(value = "/save-experience", method = POST)
	public String saveExperience(@ModelAttribute("experienceModel") ExperienceModel experienceModel,
	                             @ModelAttribute("consultantId") String consultantId) {

		setExperiencePeriods(experienceModel);
		consultantService.addConsultantExperience(experienceModel, consultantId);
		return "redirect:/consultant/profile";
	}

	@RequestMapping(value = "/update-experience", method = POST)
	public String updateExperience(@ModelAttribute("experienceModel") ExperienceModel experienceModel,
	                               @ModelAttribute("consultantId") String consultantId) {

		setExperiencePeriods(experienceModel);
		consultantService.updateConsultantExperience(experienceModel, consultantId);
		return "redirect:/consultant/profile";
	}

	private void setExperiencePeriods(ExperienceModel experienceModel) {
		String monthFrom = experienceModel.getMonthFrom();
		String yearFrom = experienceModel.getYearFrom();
		Date periodFrom = periodParser.resolveDateByMonthAndYear(monthFrom, yearFrom);
		experienceModel.setPeriodFrom(periodFrom);
		if (!experienceModel.isCurrent()) {
			Calendar calendar = Calendar.getInstance();
			String monthTo = experienceModel.getMonthTo();
			String yearTo = experienceModel.getYearTo();
			Date dateTo = periodParser.resolveDateByMonthAndYear(monthTo, yearTo);
			calendar.setTime(dateTo);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			experienceModel.setPeriodTo(calendar.getTime());
		}
	}

	@RequestMapping(value = "/validate-experience", method = POST, produces = JSON_UTF_8)
	@ResponseBody
	public ValidationResponse validateProfile(@ModelAttribute("experienceModel") ExperienceModel experienceModel,
	                                          BindingResult result) {

		experienceValidator.validate(experienceModel, result);
		if (result.hasErrors()) {
			return validationResponseHandler.validationFail(result, httpRequest.getLocale());
		}
		return validationResponseHandler.validationSuccess();
	}

	@RequestMapping(value = "/delete-experience", method = GET)
	public String deleteExperience(@ModelAttribute("consultantId") String consultantId,
	                               @RequestParam String experienceId) {

		consultantService.removeExperience(consultantId, experienceId);
		return "redirect:/consultant/profile";
	}

	@RequestMapping(value = "/save-languages", method = POST)
	public String saveLanguages(@ModelAttribute("consultantModel") ConsultantModel consultantModel,
	                            @ModelAttribute("consultantId") String consultantId) {

		LanguageModel[] languages = Iterables.toArray(consultantModel.getLanguages(), LanguageModel.class);
		consultantService.addLanguages(languages, consultantId);
		return "redirect:/consultant/profile";
	}

	@RequestMapping(value = "/save-skill", method = POST)
	public String saveSkill(@ModelAttribute("consultantId") String consultantId,
	                        @RequestParam("skill") String skill) {

		consultantService.addSkills(skill, consultantId);
		return "redirect:/consultant/profile";
	}

	@RequestMapping(value = "/save-education", method = POST)
	public String saveEducation(@ModelAttribute("educationModel") EducationModel educationModel,
	                            @ModelAttribute("consultantId") String consultantId) {

		consultantService.addConsultantEducation(educationModel, consultantId);
		return "redirect:/consultant/profile";
	}

	@RequestMapping(value = "/edit-education", method = GET, produces = JSON_UTF_8)
	@ResponseBody
	public String editEducation(@ModelAttribute("consultantId") String consultantId,
	                            @RequestParam String educationId) {

		Optional<EducationModel> education = consultantService.findEducation(consultantId, educationId);
		if (education.isPresent()) {
			return gson.toJson(education.get());
		}
		return pageNotFound();
	}

	@RequestMapping(value = "/update-education", method = POST)
	public String updateEducation(@ModelAttribute("educationModel") EducationModel educationModel,
	                              @ModelAttribute("consultantId") String consultantId) {

		consultantService.updateConsultantEducation(educationModel, consultantId);
		return "redirect:/consultant/profile";
	}

	@RequestMapping(value = "/delete-education", method = GET)
	public String deleteEducation(@ModelAttribute("consultantId") String consultantId,
	                              @RequestParam String educationId) {

		consultantService.removeEducation(consultantId, educationId);
		return "redirect:/consultant/profile";
	}

	@RequestMapping(value = "/validate-education", method = POST, produces = JSON_UTF_8)
	@ResponseBody
	public ValidationResponse validateEducation(@ModelAttribute("educationModel") EducationModel educationModel, BindingResult result) {
		educationValidator.validate(educationModel, result);
		if (result.hasErrors()) {
			return validationResponseHandler.validationFail(result, httpRequest.getLocale());
		}
		return validationResponseHandler.validationSuccess();
	}

	private static String pageNotFound() {
		return "404";
	}

}
