package com.exposure.user;

/**
 * Enum of all the different user detail fields.
 */
public enum UserField {
    UID("uid"),
    NAME("name"),
    NICKNAME("nickname"),
    BIRTHDAY("birthday"),
    LOCATION("location"),
    BIO("bio"),
    EMAIL("email"),
    PHONE("phone"),
    PLACES_STUDIED("placesStudied"),
    PLACES_LIVED("placesLived"),
    HOBBIES("hobbies"),
    PERSONALITIES("personalities"),
    PREFERENCES("preferences"),
    TRUTHS("truths"),
    LIES("lies"),
    CONNECTIONS("connections");

    private final String name;

    /**
     * Creates new user field
     * @param name name of new user field
     */
    UserField(String name) {
        this.name = name;
    }

    /**
     * Converts the enum to a string
     * @return enum in string format 
     */
    @Override
    public String toString() {
        return this.name;
    }
}
