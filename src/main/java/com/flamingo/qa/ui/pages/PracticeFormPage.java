package com.flamingo.qa.ui.pages;

import com.flamingo.qa.ui.model.Gender;
import com.flamingo.qa.ui.model.Hobby;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class PracticeFormPage {

    private final Page page;

    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator emailInput;
    private final Locator mobileNumberInput;
    private final Locator dateOfBirthInput;
    private final Locator subjectsInput;
    private final Locator uploadPictureInput;
    private final Locator currentAddressInput;
    private final Locator stateDropdown;
    private final Locator cityDropdown;
    private final Locator submitButton;

    private PracticeFormPage(Page page) {
        this.page = page;
        this.firstNameInput = page.locator("#firstName");
        this.lastNameInput = page.locator("#lastName");
        this.emailInput = page.locator("#userEmail");
        this.mobileNumberInput = page.locator("#userNumber");
        this.dateOfBirthInput = page.locator("#dateOfBirthInput");
        this.subjectsInput = page.locator("#subjectsInput");
        this.uploadPictureInput = page.locator("#uploadPicture");
        this.currentAddressInput = page.locator("#currentAddress");
        this.stateDropdown = page.locator("#state");
        this.cityDropdown = page.locator("#city");
        this.submitButton = page.locator("#submit");
    }

    public static PracticeFormPage open(Page page, String baseUrl) {
        page.navigate(baseUrl + "/automation-practice-form");
        return new PracticeFormPage(page);
    }

    public PracticeFormPage fillFirstName(String firstName) {
        firstNameInput.fill(firstName);
        return this;
    }

    public PracticeFormPage fillLastName(String lastName) {
        lastNameInput.fill(lastName);
        return this;
    }

    public PracticeFormPage fillEmail(String email) {
        emailInput.fill(email);
        return this;
    }

    public PracticeFormPage selectGender(Gender gender) {
        page.locator("label[for='gender-radio-" + gender.getRadioIndex() + "']").click();
        return this;
    }

    public PracticeFormPage fillMobileNumber(String mobileNumber) {
        mobileNumberInput.fill(mobileNumber);
        return this;
    }

    public PracticeFormPage setDateOfBirth(LocalDate date) {
        dateOfBirthInput.click();

        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        page.locator(".react-datepicker__month-select").selectOption(new SelectOption().setLabel(monthName));
        page.locator(".react-datepicker__year-select").selectOption(String.valueOf(date.getYear()));

        page.locator(".react-datepicker__day:not(.react-datepicker__day--outside-month)",
                new Page.LocatorOptions().setHasText(Pattern.compile("^" + date.getDayOfMonth() + "$"))).click();
        return this;
    }

    public PracticeFormPage addSubjects(List<String> subjects) {
        for (String subject : subjects) {
            subjectsInput.fill(subject);
            page.locator(".subjects-auto-complete__option", new Page.LocatorOptions().setHasText(subject))
                    .first()
                    .click();
        }
        return this;
    }

    public PracticeFormPage selectHobbies(List<Hobby> hobbies) {
        for (Hobby hobby : hobbies) {
            page.locator("label[for='hobbies-checkbox-" + hobby.getCheckboxIndex() + "']").click();
        }
        return this;
    }

    public PracticeFormPage uploadPicture(String filePath) {
        uploadPictureInput.setInputFiles(Path.of(filePath));
        return this;
    }

    public PracticeFormPage fillCurrentAddress(String address) {
        currentAddressInput.fill(address);
        return this;
    }

    public PracticeFormPage selectState(String state) {
        stateDropdown.click();
        stateDropdown.getByText(state, new Locator.GetByTextOptions().setExact(true)).click();
        return this;
    }

    public PracticeFormPage selectCity(String city) {
        cityDropdown.click();
        cityDropdown.getByText(city, new Locator.GetByTextOptions().setExact(true)).click();
        return this;
    }

    public SubmissionModal submit() {
        clickSubmit();
        SubmissionModal modal = new SubmissionModal(page);
        modal.waitUntilDisplayed();
        return modal;
    }

    public PracticeFormPage clickSubmit() {
        removeAdBanner();
        submitButton.click();
        return this;
    }

    public SubmissionModal getSubmissionModal() {
        return new SubmissionModal(page);
    }

    public boolean isFirstNameInvalid() {
        return isFieldInvalid(firstNameInput);
    }

    public boolean isLastNameInvalid() {
        return isFieldInvalid(lastNameInput);
    }

    public boolean isMobileNumberInvalid() {
        return isFieldInvalid(mobileNumberInput);
    }

    private boolean isFieldInvalid(Locator locator) {
        boolean valid = (boolean) locator.evaluate("el => el.checkValidity()");
        return !valid;
    }

    private void removeAdBanner() {
        Locator adBanner = page.locator("#fixedban");
        if (adBanner.count() > 0) {
            adBanner.evaluate("el => el.remove()");
        }
    }
}
