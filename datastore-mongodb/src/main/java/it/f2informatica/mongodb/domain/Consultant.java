package it.f2informatica.mongodb.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document
public class Consultant extends Identifiable<String> {

	@Indexed(unique = true)
	private String consultantNo;

	@Indexed(direction = IndexDirection.DESCENDING)
	private Date registrationDate;

	private String fiscalCode;

	private String email;

	private String firstName;

	private String lastName;

	private Gender gender;

	private String phoneNumber;

	private String mobileNumber;

	private Date birthDate;

	private String birthCity;

	private String birthCountry;

	private String nationality;

	private String identityCardNo;

	private String passportNo;

	private MaritalStatus maritalStatus;

	private Profile profile;

	private Address residence;

	private Address domicile;

	private String curriculum; // TODO: GridFSFile

	private String photo; // TODO: GridFSFile

}
