package com.flamingo.qa.ui.model;

import lombok.Getter;

@Getter
public enum Hobby {

    SPORTS(1, "Sports"),
    READING(2, "Reading"),
    MUSIC(3, "Music");

    private final int checkboxIndex;
    private final String label;

    Hobby(int checkboxIndex, String label) {
        this.checkboxIndex = checkboxIndex;
        this.label = label;
    }
}
