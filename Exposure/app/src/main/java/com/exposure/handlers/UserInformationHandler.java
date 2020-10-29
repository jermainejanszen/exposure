package com.exposure.handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.exposure.adapters.ConnectionItem;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.user.UserField;
import com.exposure.user.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class UserInformationHandler {

    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    /**
     * Download user information as document snapshot from firestore
     * @param user The user whose information we are retrieving from firestore
     * @param onCompleteCallback Notifies the calling class that the task has been executed
     */
    public static void downloadUserInformation(final User user, final OnCompleteCallback onCompleteCallback) {
        mFirestore.collection("Profiles").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        convertDocumentSnapshotToCurrentUser(documentSnapshot, user);
                        onCompleteCallback.update(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /* Failed to download user information */
                        onCompleteCallback.update(false);
                    }
                });
    }

    /**
     * Convert document snapshot information from firestore to current user
     * @param documentSnapshot Document snapshot containing user information from firestore
     * @param user Current user to set fields of
     */
    private static void convertDocumentSnapshotToCurrentUser(DocumentSnapshot documentSnapshot, User user) {
        user.setName((String) documentSnapshot.get(UserField.NAME.toString()));
        user.setNickname((String) documentSnapshot.get(UserField.NICKNAME.toString()));
        user.setEmail((String) documentSnapshot.get(UserField.EMAIL.toString()));
        Timestamp timestamp = (Timestamp) documentSnapshot.get(UserField.BIRTHDAY.toString());
        user.setBirthday(timestamp == null ? null : timestamp.toDate());
        user.setPhone((String) documentSnapshot.get(UserField.PHONE.toString()));

        List<String> preferences = (List<String>) documentSnapshot.get(UserField.PREFERENCES.toString());
        List<String> hobbies = (List<String>) documentSnapshot.get(UserField.HOBBIES.toString());
        List<String> placesLived = (List<String>) documentSnapshot.get(UserField.PLACES_LIVED.toString());
        List<String> placesStudied = (List<String>) documentSnapshot.get(UserField.PLACES_STUDIED.toString());
        List<String> personalities = (List<String>) documentSnapshot.get(UserField.PERSONALITIES.toString());
        List<String> truths = (List<String>) documentSnapshot.get(UserField.TRUTHS.toString());
        List<String> lies = (List<String>) documentSnapshot.get(UserField.LIES.toString());

       // List<ConnectionItem> connections = (List<ConnectionItem>) documentSnapshot.get(UserField.CONNECTIONS.toString());

        if (preferences != null) {
            user.setPreferences(preferences);
        }

        if (hobbies != null) {
            user.setHobbies(hobbies);
        }

        if (placesLived != null) {
            user.setPlacesLived(placesLived);
        }

        if (placesStudied != null) {
            user.setPlacesStudied(placesStudied);
        }

        if (personalities != null) {
            user.setPersonalities(personalities);
        }

        if (truths != null){
            user.setTruths(truths);
        }

        if (lies != null){
            user.setLies(lies);
        }

//        if (connections != null){
//            user.setConnections(connections);
//        }
    }

    /**
     * Uploads the data associated with current user to the firestore
     * @param user The user whose information is being uploaded to the firestore
     * @param onCompleteCallback Notifies the calling class that the task has been executed
     */
    public static void uploadUserInformationToFirestore(User user, final OnCompleteCallback onCompleteCallback) {
        String userID = user.getUid();

        FirebaseFirestore.getInstance().collection("Profiles").document(userID).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onCompleteCallback.update(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onCompleteCallback.update(false);
                    }
                });
    }
}
