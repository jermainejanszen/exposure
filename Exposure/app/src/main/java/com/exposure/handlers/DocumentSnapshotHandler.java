package com.exposure.handlers;

import com.exposure.user.CurrentUser;
import com.exposure.user.UserField;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentSnapshotHandler {

    public static CurrentUser convertDocumentSnapshotToCurrentUser(DocumentSnapshot documentSnapshot, CurrentUser currentUser) {

        currentUser.setName((String) documentSnapshot.get(UserField.NAME.toString()));
        currentUser.setNickname((String) documentSnapshot.get(UserField.NICKNAME.toString()));
        currentUser.setEmail((String) documentSnapshot.get(UserField.EMAIL.toString()));
        Timestamp timestamp = (Timestamp) documentSnapshot.get(UserField.BIRTHDAY.toString());
        currentUser.setBirthday(timestamp.toDate());
        currentUser.setPhone((String) documentSnapshot.get(UserField.PHONE.toString()));

        List<String> preferences = (List<String>) documentSnapshot.get(UserField.PREFERENCES.toString());
        if (preferences != null){
            currentUser.setPreferences(preferences);
        }

        List<String> hobbies = (List<String>) documentSnapshot.get(UserField.HOBBIES.toString());
        if (hobbies != null){
            //TODO: this may cause an error in the recycler view as it is referencing an old list
            currentUser.setHobbies(hobbies);
        }


        List<String> placesLived = (List<String>) documentSnapshot.get(UserField.PLACES_LIVED.toString());
        if (placesLived != null){
            currentUser.setPlacesLived(placesLived);
        }


        List<String> placesStudied = (List<String>) documentSnapshot.get(UserField.PLACES_STUDIED.toString());
        if (placesStudied != null){
            currentUser.setPlacesStudied(placesStudied);
        }
        return currentUser;
    }
}
