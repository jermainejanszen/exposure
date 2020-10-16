package com.exposure.user;

import java.util.ArrayList;
import java.util.Date;

/**
 * Abstract User class to hold all of a user's details.
 */
abstract class User {

    /* Server Details */
    private final String uid;

    /* Personal Details */
    private String name;
    private String nickname;
    private Date birthday;
    private String bio;

    /* Contact Details */
    private String email;
    private String phone;

    /* User Details */
    private ArrayList<String> placesStudied = new ArrayList<>();
    private ArrayList<String> placesLived = new ArrayList<>();
    private ArrayList<String> hobbies = new ArrayList<>();

    /* Constructor */
    public User(String uid) {
        this.uid = uid;

        // TODO: Load fields from Firebase
    }

    /* Getters */
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public Date getBirthday() { return birthday; }
    public String getBio() { return bio; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public ArrayList<String> getPlacesStudied() { return placesStudied; }
    public ArrayList<String> getPlacesLived() { return placesLived; }
    public ArrayList<String> getHobbies() { return hobbies; }

    /* Setters */
    public void setName(String name) { this.name = name; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public void setBio(String bio) { this.bio = bio; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPlacesStudied(ArrayList<String> placesStudied) {
        this.placesStudied = placesStudied;
    }
    public void setPlacesLived(ArrayList<String> placesLived) { this.placesLived = placesLived; }
    public void setHobbies(ArrayList<String> hobbies) { this.hobbies = hobbies; }

}
