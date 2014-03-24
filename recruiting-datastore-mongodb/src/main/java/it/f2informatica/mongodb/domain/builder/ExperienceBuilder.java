package it.f2informatica.mongodb.domain.builder;

import it.f2informatica.mongodb.domain.Experience;

import java.util.Date;

public class ExperienceBuilder {

	private Experience experience = new Experience();

	public static ExperienceBuilder experience() {
		return new ExperienceBuilder();
	}

	public ExperienceBuilder withId(String id) {
		this.experience.setId(id);
		return this;
	}

	public ExperienceBuilder inCompany(String companyName) {
		this.experience.setCompanyName(companyName);
		return this;
	}

	public ExperienceBuilder fromPeriod(Date periodFrom) {
		this.experience.setPeriodFrom(periodFrom);
		return this;
	}

	public ExperienceBuilder toPeriod(Date periodTo) {
		this.experience.setPeriodTo(periodTo);
		this.experience.setCurrent(periodTo == null);
		return this;
	}

	public ExperienceBuilder isThisTheCurrentJob(boolean isCurrent) {
		if (isCurrent) {
			return thisIsTheCurrentJob();
		}
		this.experience.setCurrent(false);
		return this;
	}

	public ExperienceBuilder thisIsTheCurrentJob() {
		this.experience.setPeriodTo(null);
		return this;
	}

	public ExperienceBuilder withPosition(String position) {
		this.experience.setPosition(position);
		return this;
	}

	public ExperienceBuilder locatedAt(String location) {
		this.experience.setLocation(location);
		return this;
	}

	public ExperienceBuilder withDescription(String description) {
		this.experience.setDescription(description);
		return this;
	}

	public Experience build() {
		return experience;
	}

}