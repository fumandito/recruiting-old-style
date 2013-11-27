package it.f2informatica.acceptance.page.user;

import it.f2informatica.acceptance.page.Page;
import org.openqa.selenium.WebDriver;

public class UserDetailsPage extends Page {
	private static final String USERNAME_INPUT_XPATH = "//input[@id='username']";
	private static final String EDIT_USER_BUTTON_XPATH = "//input[@id='editUser']";
	private static final String DELETE_USER_BUTTON_XPATH = "//input[@id='deleteUser']";

	private String userId;

	public UserDetailsPage(WebDriver driver, String baseUrl, String userId) {
		super(driver, baseUrl, "/user/findUser/" + userId);
		this.userId = userId;
	}

	public String getUsername() {
		return getValue(findElement(USERNAME_INPUT_XPATH));
	}

	public UserEditPage clickOnEditUserButton() {
		click(findElement(EDIT_USER_BUTTON_XPATH));
		String path = "/user/editUser/" + userId;
		driver.get(baseUrl + path);
		return new UserEditPage(driver, baseUrl, path);
	}

	public UserManagementPage clickOnDeleteUserButton() {
		click(findElement(DELETE_USER_BUTTON_XPATH));
		String path = "/user/deleteUser/" + userId;
		driver.get(baseUrl + path);
		return new UserManagementPage(driver, baseUrl);
	}
}