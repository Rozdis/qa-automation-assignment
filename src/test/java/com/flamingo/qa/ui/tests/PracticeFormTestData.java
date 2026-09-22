package com.flamingo.qa.ui.tests;

import com.flamingo.qa.ui.model.Gender;
import com.flamingo.qa.ui.model.Hobby;
import com.flamingo.qa.ui.model.StudentRegistration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PracticeFormTestData {

    static StudentRegistration fullRegistration(String pictureFilePath) {
        return StudentRegistration.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .gender(Gender.MALE)
                .mobileNumber("9876543210")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .subjects(List.of("Maths", "Physics"))
                .hobbies(List.of(Hobby.SPORTS, Hobby.READING))
                .pictureFilePath(pictureFilePath)
                .currentAddress("123 Main Street, Springfield")
                .state("NCR")
                .city("Delhi")
                .build();
    }

    static StudentRegistration requiredFieldsOnlyRegistration() {
        return StudentRegistration.builder()
                .firstName("Jane")
                .lastName("Smith")
                .gender(Gender.FEMALE)
                .mobileNumber("9123456780")
                .build();
    }
}
