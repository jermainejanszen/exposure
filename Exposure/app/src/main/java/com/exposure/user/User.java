package com.exposure.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract User class to hold all of a user's details.
 */
public abstract class User implements Serializable {

    /* Server Details */
    private final String uid;

    /* Personal Details */
    private String name;
    private String nickname;
    private Date birthday;

    /* Location Details */
    private final Map<String, Double> location = new HashMap<>();

    /* Contact Details */
    private String email;
    private String phone;

    /* User Details */
    private List<String> placesStudied = new ArrayList<>();
    private List<String> placesLived = new ArrayList<>();
    private List<String> hobbies = new ArrayList<>();
    private List<String> personalities = new ArrayList<>();
    private List<String> preferences = new ArrayList<>();
    private List<String> truths = new ArrayList<>();
    private List<String> lies = new ArrayList<>();

    /* Constructor */
    public User(String uid) {
        this.uid = uid;
    }

    /**
     * Returns the user's id
     * @return the user's id
     */
    public String getUid() { return uid; }

    /**
     * Returns the user's name
     * @return the user's name
     */
    public String getName() { return name; }

    /**
     * Returns the user's nickname
     * @return the user's nickname
     */
    public String getNickname() { return nickname; }

    /**
     * Returns the user's birthday
     * @return the user's birthday
     */
    public Date getBirthday() { return birthday; }

    /**
     * Returns the user's location
     * @return the user's location
     */
    public Map<String, Double> getLocation() { return location; }

    /**
     * Returns the user's email
     * @return the user's email
     */
    public String getEmail() { return email; }

    /**
     * Returns the user's phone number
     * @return the user's phone number
     */
    public String getPhone() { return phone; }

    /**
     * Returns the places that the user has studied
     * @return the places that the user has studied
     */
    public List<String> getPlacesStudied() { return placesStudied; }

    /**
     * Returns the places the user has lived
     * @return the places the user has lived
     */
    public List<String> getPlacesLived() { return placesLived; }

    /**
     * Returns the user's hobbies
     * @return the user's hobbies
     */
    public List<String> getHobbies() { return hobbies; }

    /**
     * Returns the user's personality traits
     * @return the user's personality traits
     */
    public List<String> getPersonalities() { return personalities; }

    /**
     * Returns the user's romantic gender preferences
     * @return the user's romantic gender preferences
     */
    public List<String> getPreferences() { return preferences; }

    /**
     * Returns the truths stored about the user
     * @return the truths stored about the user
     */
    public List<String> getTruths(){ return truths; }

    /**
     * Returns the lies stored about the user
     * @return the lies stored about the user
     */
    public List<String> getLies(){ return lies; }

    /**
     * Sets the user's name
     * @param name the user's name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets the user's nickname
     * @param nickname the user's nickname
     */
    public void setNickname(String nickname) { this.nickname = nickname; }

    /**
     * Sets the user's birthday
     * @param birthday the user's birthday
     */
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    /**
     * Sets the user's location
     * @param lat their latitude
     * @param lon their longitude
     */
    public void setLocation(double lat, double lon) {
        this.location.put("Latitude", lat);
        this.location.put("Longitude", lon);
    }

    /**
     * Sets the user's email
     * @param email the user's email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Sets the user's phone number
     * @param phone the user's phone number
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Sets the places the user has studied
     * @param placesStudied the places the user has studied
     */
    public void setPlacesStudied(List<String> placesStudied) {
        this.placesStudied = placesStudied;
    }

    /**
     * Sets the places the user has lived
     * @param placesLived the places the user has lived
     */
    public void setPlacesLived(List<String> placesLived) { this.placesLived = placesLived; }

    /**
     * Sets the user's hobbies
     * @param hobbies the user's hobbies
     */
    public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }

    /**
     * Sets the user's personality traits
     * @param personalities the user's personality traits
     */
    public void setPersonalities(List<String> personalities) { this.personalities = personalities; }

    /**
     * Sets the user's romantic gender preferences
     * @param preferences the user's romantic gender preferences
     */
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }

    /**
     * Sets the truths about the user
     * @param truths the truths about the user
     */
    public void setTruths(List<String> truths) { this.truths = truths; }

    /**
     * Sets the lies about the user
     * @param lies the lies about the user
     */
    public void setLies(List<String> lies) { this.lies = lies; }

    /**
     * Ensures the user's information is all valid, giving us a valid user state
     * @return true if valid, else false
     */
    public boolean validState() {
        return !(null == name || null == birthday || 0 == preferences.size() || null == email ||
                location.size() != 2 || truths.size() < 3 || lies.size() < 1);
    }
}
