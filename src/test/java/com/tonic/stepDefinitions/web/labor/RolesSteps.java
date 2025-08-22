package com.tonic.stepDefinitions.web.labor;

import com.tonic.pageObjects.web.login.LoginPage;
import com.tonic.pageObjects.web.labor.RolesPage;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.stepDefinitions.BaseStep;
import com.tonic.utils.ApplicationUtils;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import static org.testng.Assert.*;


public class RolesSteps extends BaseStep {

    public RolesSteps() {
        this.rolesPage = new RolesPage(PlaywrightFactory.getPage());
    }

    private RolesPage rolesPage;
    private String userName;
    private String hourlyWages;
    private LoginPage loginPage;
    private String hourlyWagesWithoutDollar;
    private String updatedHourlyWages;
    private double incrementValue;
    private double decrementValue;
    private double hourlyWagesDouble;
    private double updatedHourlyWagesDouble;
    private String decrementValueHourlyWages;
    private final ApplicationUtils apputils = new ApplicationUtils();


    @And("I am on the Roles page")
    public void iAmOnTheRolesPage() {
        rolesPage.navigateToRoles();
        assertTrue(rolesPage.isRolesPageLoaded(), "Roles page should be loaded");
    }

    @When("I find a user in the roles list")
    public void iFindAUserInTheRolesList() {
        userName = rolesPage.getFirstVisibleUser();
        assertNotNull(userName, "Should find a user");
    }

    @And("I click edit for the user")
    public void iClickEditForTheUser() {
        rolesPage.clickEditForUser(userName);
        assertTrue(rolesPage.isEditFormVisible(), "Edit form should be visible");
    }

    @And("get the current hourly wages of specific user")
    public void  getTheCurrentHourlyWagesOfSpecificUser() {
        String hourlyWages=rolesPage.getHourlyWagesOfSpecificUser(userName);
        LOGGER.info("Hourly wages input " + hourlyWages);
        hourlyWagesWithoutDollar=apputils.removeDollarSymbol(hourlyWages);
        LOGGER.info("Hourly wages input without dollar " + hourlyWagesWithoutDollar);
        hourlyWagesDouble =apputils.convertStringToDouble(hourlyWagesWithoutDollar);
        LOGGER.info("Hourly wages input without dollar in Double " + hourlyWagesDouble);
        incrementValue = hourlyWagesDouble+1 ;
        LOGGER.info("Increment Value " + incrementValue);
        updatedHourlyWages=apputils.convertDoubleToString(incrementValue);

        decrementValue = hourlyWagesDouble-1 ;
        LOGGER.info("Decrease Value " + decrementValue);
        decrementValueHourlyWages=apputils.convertDoubleToString(decrementValue);

    }

    @And("User increasing the hourly wages Amount")
    public void UserIncreasingTheHourlyWagesAmount() {
        rolesPage.setHourlyWages(updatedHourlyWages);
    }

    @And("User decreasing the hourly wages Amount")
    public void userDecreasingTheHourlyWagesAmount() {
        rolesPage.setHourlyWages(decrementValueHourlyWages);
    }

    @And("I save the changes")
    public void iSaveTheChanges() {
        rolesPage.saveChanges();
    }

    @Then("the changes should be saved successfully")
    public void theChangesShouldBeSavedSuccessfully() {
        assertFalse(rolesPage.isEditFormVisible(), "Edit form should not be visible after saving");
    }


    @And("get the updated hourly wages of specific user")
    public void getTheUpdatedHourlyWagesOfSpecificUser() {
        String updatedHourlyWage=rolesPage.getHourlyWagesOfSpecificUser(userName);
        String updatedHourlyWagesWithoutDollar=apputils.removeDollarSymbol(updatedHourlyWage);
        LOGGER.info("Hourly wages input without dollar " + updatedHourlyWagesWithoutDollar);
        updatedHourlyWagesDouble =apputils.convertStringToDouble(updatedHourlyWagesWithoutDollar);
        LOGGER.info("Hourly wages input without dollar in Double " + updatedHourlyWagesDouble);

    }

    @Then("validate hourly wages increased after updated")
    public void validateHourlyWagesIncreasedAfterUpdated() {
        assertTrue(hourlyWagesDouble < updatedHourlyWagesDouble, "Updated value should be Greater than old wages");
    }




    @Then("validate hourly wages decreased after updated")
    public void validateHourlyWagesDecreasedAfterUpdated() {
        assertTrue(hourlyWagesDouble > updatedHourlyWagesDouble, "Updated value should be Greater than old wages");
    }
}