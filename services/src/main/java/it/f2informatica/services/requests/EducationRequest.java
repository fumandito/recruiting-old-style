package it.f2informatica.services.requests;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class EducationRequest {

	private String school;

	private int startYear;

	private int endYear;

	private String schoolDegree;

	private String schoolFieldOfStudy;

	private String schoolGrade;

	private String schoolActivities;

	private boolean isCurrent;

	private String description;

}
