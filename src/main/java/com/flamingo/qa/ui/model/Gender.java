package com.flamingo.qa.ui.model;

import lombok.Getter;

@Getter
public enum Gender {

    MALE(1, "Male"),
    FEMALE(2, "Female"),
    OTHER(3, "Other");

    private final int radioIndex;
    private final String label;

    Gender(int radioIndex, String label) {
        this.radioIndex = radioIndex;
        this.label = label;
    }
}
