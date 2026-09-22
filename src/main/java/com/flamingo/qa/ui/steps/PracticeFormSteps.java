package com.flamingo.qa.ui.steps;

import com.flamingo.qa.ui.model.StudentRegistration;
import com.flamingo.qa.ui.pages.PracticeFormPage;
import org.springframework.stereotype.Component;

@Component
public final class PracticeFormSteps {

    public PracticeFormPage fill(PracticeFormPage formPage, StudentRegistration registrationData) {
        if (registrationData.getFirstName() != null) {
            formPage.fillFirstName(registrationData.getFirstName());
        }
        if (registrationData.getLastName() != null) {
            formPage.fillLastName(registrationData.getLastName());
        }
        if (registrationData.getEmail() != null) {
            formPage.fillEmail(registrationData.getEmail());
        }
        if (registrationData.getGender() != null) {
            formPage.selectGender(registrationData.getGender());
        }
        if (registrationData.getMobileNumber() != null) {
            formPage.fillMobileNumber(registrationData.getMobileNumber());
        }
        if (registrationData.getDateOfBirth() != null) {
            formPage.setDateOfBirth(registrationData.getDateOfBirth());
        }
        if (registrationData.getSubjects() != null) {
            formPage.addSubjects(registrationData.getSubjects());
        }
        if (registrationData.getHobbies() != null) {
            formPage.selectHobbies(registrationData.getHobbies());
        }
        if (registrationData.getPictureFilePath() != null) {
            formPage.uploadPicture(registrationData.getPictureFilePath());
        }
        if (registrationData.getCurrentAddress() != null) {
            formPage.fillCurrentAddress(registrationData.getCurrentAddress());
        }
        if (registrationData.getState() != null) {
            formPage.selectState(registrationData.getState());
        }
        if (registrationData.getCity() != null) {
            formPage.selectCity(registrationData.getCity());
        }
        return formPage;
    }
}
