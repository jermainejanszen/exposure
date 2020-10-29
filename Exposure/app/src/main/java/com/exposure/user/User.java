package com.exposure.user;

import com.exposure.adapters.ConnectionItem;

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
    private List<ConnectionItem> connections = new ArrayList<>();

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
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<String> getPlacesStudied() { return placesStudied; }
    public List<String> getPlacesLived() { return placesLived; }
    public List<String> getHobbies() { return hobbies; }
    public List<String> getPersonalities() { return personalities; }
    public List<String> getPreferences() { return preferences; }
    public List<String> getTruths(){ return truths; }
    public List<String> getLies(){ return lies; }
    public List<ConnectionItem> getConnections(){ return connections; }


    /* Setters */
    public void setName(String name) { this.name = name; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPlacesStudied(List<String> placesStudied) {
        this.placesStudied = placesStudied;
    }
    public void setPlacesLived(List<String> placesLived) { this.placesLived = placesLived; }
    public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }
    public void setPersonalities(List<String> personalities) { this.personalities = personalities; }
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }
    public void setTruths(List<String> truths) { this.truths = truths; }
    public void setLies(List<String> lies) { this.lies = lies; }
    public void setConnections (List<ConnectionItem> connections) { this.connections = connections; }

    public boolean validState() {
        return !(null == name || null == birthday || 0 == preferences.size()|| null == email);
    }
}
