package com.exposure.user;

/**
 * Enum of all the different user detail fields.
 */
public enum UserField {
    UID("uid"),
    NAME("name"),
    NICKNAME("nickname"),
    BIRTHDAY("birthday"),
    BIO("bio"),
    EMAIL("email"),
    PHONE("phone"),
    PLACES_STUDIED("placesstudied"),
    PLACES_LIVED("placeslived"),
    HOBBIES("hobbies"),
    PERSONALITY("personality"),
    PREFERENCES("preferences");

    private final String name;

    UserField(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
