package com.flamingo.qa.ui.tests;

import com.flamingo.qa.ui.base.BaseUiTest;
import com.flamingo.qa.ui.steps.PracticeFormSteps;
import com.flamingo.qa.ui.model.Hobby;
import com.flamingo.qa.ui.model.StudentRegistration;
import com.flamingo.qa.ui.pages.PracticeFormPage;
import com.flamingo.qa.ui.pages.SubmissionModal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PracticeFormTest extends BaseUiTest {

    @Autowired
    PracticeFormSteps practiceFormSteps;

    @Test
    @DisplayName("Submit registration form with all fields and verify the success modal")
    void shouldSubmitFormWithAllFieldsAndShowCorrectDataInModal() throws URISyntaxException {
        StudentRegistration registrationData = PracticeFormTestData.fullRegistration(sampleUploadFilePath());

        SubmissionModal modal = practiceFormSteps.fill(PracticeFormPage.open(page, uiProperties.getBaseUrl()), registrationData)
                .submit();

        String expectedName = fullName(registrationData);
        String expectedGender = registrationData.getGender().getLabel();
        List<String> expectedHobbies = registrationData.getHobbies().stream().map(Hobby::getLabel).toList();

        assertThat(modal.isDisplayed()).as("success modal should appear after submitting a fully filled form").isTrue();
        assertThat(modal.getStudentName()).as("modal should echo back the submitted first and last name").isEqualTo(expectedName);
        assertThat(modal.getStudentEmail()).as("modal should echo back the submitted email address").isEqualTo(registrationData.getEmail());
        assertThat(modal.getGender()).as("modal should echo back the selected gender").isEqualTo(expectedGender);
        assertThat(modal.getMobile()).as("modal should echo back the submitted mobile number").isEqualTo(registrationData.getMobileNumber());
        assertThat(modal.getSubjects()).as("modal should list every subject that was added").contains(registrationData.getSubjects().toArray(new String[0]));
        assertThat(modal.getHobbies()).as("modal should list every hobby that was checked").contains(expectedHobbies.toArray(new String[0]));
        assertThat(modal.getAddress()).as("modal should echo back the submitted current address").isEqualTo(registrationData.getCurrentAddress());
        assertThat(modal.getStateAndCity()).as("modal should echo back the selected state and city")
                .contains(registrationData.getState(), registrationData.getCity());
    }

    @Test
    @DisplayName("Submit registration form with only required fields")
    void shouldSubmitFormWithOnlyRequiredFields() {
        StudentRegistration registrationData = PracticeFormTestData.requiredFieldsOnlyRegistration();

        SubmissionModal modal = practiceFormSteps.fill(PracticeFormPage.open(page, uiProperties.getBaseUrl()), registrationData)
                .submit();

        String expectedName = fullName(registrationData);
        String expectedGender = registrationData.getGender().getLabel();

        assertThat(modal.isDisplayed()).as("success modal should appear even with only the required fields filled").isTrue();
        assertThat(modal.getStudentName()).as("modal should echo back the submitted first and last name").isEqualTo(expectedName);
        assertThat(modal.getGender()).as("modal should echo back the selected gender").isEqualTo(expectedGender);
        assertThat(modal.getMobile()).as("modal should echo back the submitted mobile number").isEqualTo(registrationData.getMobileNumber());
    }

    @Test
    @DisplayName("Submitting without required fields shows validation errors and does not submit")
    void shouldNotSubmitWhenRequiredFieldsAreMissing() {
        PracticeFormPage formPage = PracticeFormPage.open(page, uiProperties.getBaseUrl());

        formPage.clickSubmit();

        assertThat(formPage.isFirstNameInvalid()).as("browser should flag the empty first name as invalid via native HTML5 validation").isTrue();
        assertThat(formPage.isLastNameInvalid()).as("browser should flag the empty last name as invalid via native HTML5 validation").isTrue();
        assertThat(formPage.isMobileNumberInvalid()).as("browser should flag the empty mobile number as invalid via native HTML5 validation").isTrue();
        assertThat(formPage.getSubmissionModal().isDisplayed()).as("form should not submit while required fields are invalid, so no success modal should appear").isFalse();
    }

    @Test
    @Tag("known-issue")
    @DisplayName("Closing the success modal hides it")
    void shouldCloseSubmissionModal() {
        StudentRegistration registrationData = PracticeFormTestData.requiredFieldsOnlyRegistration();

        SubmissionModal modal = practiceFormSteps.fill(PracticeFormPage.open(page, uiProperties.getBaseUrl()), registrationData)
                .submit();

        modal.close();

        assertThat(modal.isDisplayed()).as("clicking the Close button should hide the success modal").isFalse();
    }

    private static String fullName(StudentRegistration registrationData) {
        return registrationData.getFirstName() + " " + registrationData.getLastName();
    }

    private String sampleUploadFilePath() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("testdata/sample-picture.txt").toURI());
        return path.toString();
    }
}
