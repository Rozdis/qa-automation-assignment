package com.flamingo.qa.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class SubmissionModal {

    private final Locator title;
    private final Locator closeButton;
    private final Locator rows;

    public SubmissionModal(Page page) {
        this.title = page.locator("#example-modal-sizes-title-lg");
        this.closeButton = page.locator("#closeLargeModal");
        this.rows = page.locator(".table-responsive tbody tr");
    }

    public void waitUntilDisplayed() {
        title.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
    }

    public boolean isDisplayed() {
        return title.isVisible();
    }

    public String getStudentName() {
        return rowValue("Student Name");
    }

    public String getStudentEmail() {
        return rowValue("Student Email");
    }

    public String getGender() {
        return rowValue("Gender");
    }

    public String getMobile() {
        return rowValue("Mobile");
    }

    public String getSubjects() {
        return rowValue("Subjects");
    }

    public String getHobbies() {
        return rowValue("Hobbies");
    }

    public String getAddress() {
        return rowValue("Address");
    }

    public String getStateAndCity() {
        return rowValue("State and City");
    }

    public void close() {
        closeButton.click();
    }

    private String rowValue(String label) {
        return rows.filter(new Locator.FilterOptions().setHasText(label))
                .locator("td")
                .nth(1)
                .innerText();
    }
}
