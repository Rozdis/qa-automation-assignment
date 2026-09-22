package com.flamingo.qa.ui.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class StudentRegistration {

    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String mobileNumber;
    private LocalDate dateOfBirth;
    private List<String> subjects;
    private List<Hobby> hobbies;
    private String pictureFilePath;
    private String currentAddress;
    private String state;
    private String city;
}
