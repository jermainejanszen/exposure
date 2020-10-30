package com.exposure.handlers;

import androidx.annotation.NonNull;

import com.exposure.callback.OnCompleteCallback;
import com.exposure.user.CurrentUser;
import com.exposure.user.UserField;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class UserInformationHandler {

    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Download user information as document snapshot from firestore
     * @param currentUser The user whose information we are retrieving from firestore
     * @param onCompleteCallback Notifies the calling class that the task has been executed
     */
    public static void downloadUserInformation(final CurrentUser currentUser, final OnCompleteCallback onCompleteCallback) {
        mFirestore.collection("Profiles").document(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        convertDocumentSnapshotToCurrentUser(documentSnapshot, currentUser);
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
     * @param currentUser Current user to set fields of
     */
    private static void convertDocumentSnapshotToCurrentUser(DocumentSnapshot documentSnapshot, CurrentUser currentUser) {
        currentUser.setName((String) documentSnapshot.get(UserField.NAME.toString()));
        currentUser.setNickname((String) documentSnapshot.get(UserField.NICKNAME.toString()));
        currentUser.setEmail((String) documentSnapshot.get(UserField.EMAIL.toString()));
        Timestamp timestamp = (Timestamp) documentSnapshot.get(UserField.BIRTHDAY.toString());
        currentUser.setBirthday(timestamp == null ? null : timestamp.toDate());
        currentUser.setPhone((String) documentSnapshot.get(UserField.PHONE.toString()));

        Map<String, Double> location = (Map<String, Double>) documentSnapshot.get(UserField.LOCATION.toString());
        List<String> preferences = (List<String>) documentSnapshot.get(UserField.PREFERENCES.toString());
        List<String> hobbies = (List<String>) documentSnapshot.get(UserField.HOBBIES.toString());
        List<String> placesLived = (List<String>) documentSnapshot.get(UserField.PLACES_LIVED.toString());
        List<String> placesStudied = (List<String>) documentSnapshot.get(UserField.PLACES_STUDIED.toString());
        List<String> personalities = (List<String>) documentSnapshot.get(UserField.PERSONALITIES.toString());

        if (null != location) {
            if (null != location.get("Latitude") && null != location.get("Longitude")) {
                currentUser.setLocation(location.get("Latitude"), location.get("Longitude"));
            }
        }

        if (preferences != null) {
            currentUser.setPreferences(preferences);
        }

        if (hobbies != null) {
            currentUser.setHobbies(hobbies);
        }

        if (placesLived != null) {
            currentUser.setPlacesLived(placesLived);
        }

        if (placesStudied != null) {
            currentUser.setPlacesStudied(placesStudied);
        }

        if (personalities != null) {
            currentUser.setPersonalities(personalities);
        }
    }

    /**
     * Uploads the data associated with current user to the firestore
     * @param currentUser The user whose information is being uploaded to the firestore
     * @param onCompleteCallback Notifies the calling class that the task has been executed
     */
    public static void uploadUserInformationToFirestore(CurrentUser currentUser, final OnCompleteCallback onCompleteCallback) {
        String userID = currentUser.getUid();

        FirebaseFirestore.getInstance().collection("Profiles").document(userID).set(currentUser)
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
