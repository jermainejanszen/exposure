package com.exposure.handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.exposure.callback.OnCompleteCallback;
import com.exposure.user.ConnectionItem;
import com.exposure.user.CurrentUser;
import com.exposure.user.UserField;
import com.exposure.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserInformationHandler {

    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
                        convertDocumentSnapshotToUser(documentSnapshot, user);

                        /* Temporary */
                        user.setName(mAuth.getCurrentUser().getDisplayName());
                        user.setEmail(mAuth.getCurrentUser().getEmail());

                        onCompleteCallback.update(true, "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /* Failed to download user information */
                        onCompleteCallback.update(false, e.getMessage());
                    }
                });
    }

    public static void downloadCurrentUserConnections(final CurrentUser user, final OnCompleteCallback onCompleteCallback) {
        mFirestore.collection("Profiles").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<Map<String, Object>> docConnections = (List<Map<String, Object>>) documentSnapshot.get(UserField.CONNECTIONS.toString());
                        List<ConnectionItem> connections = new ArrayList<>();
                        for (Map<String, Object> connection : docConnections) {
                            connections.add(new ConnectionItem((String) connection.get("uid"), (List<String>) connection.get("exposedInfo")));
                        }

                        user.setConnections(connections);
                        onCompleteCallback.update(true, "success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /* Failed to download user information */
                        onCompleteCallback.update(false, e.getMessage());
                    }
                });
    }

    public static void addOtherUserConnection(final String userUid, final String uidToAdd, final OnCompleteCallback onCompleteCallback) {
        final CurrentUser otherUser = new CurrentUser(userUid);
        downloadUserInformation(otherUser, new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    downloadCurrentUserConnections(otherUser, new OnCompleteCallback() {
                        @Override
                        public void update(boolean success, String message) {
                            if (success) {
                                otherUser.addConnection(uidToAdd);
                                uploadUserInformationToFirestore(otherUser, new OnCompleteCallback() {
                                    @Override
                                    public void update(boolean success, String message) {
                                        onCompleteCallback.update(success, message);
                                    }
                                });
                            } else {
                                onCompleteCallback.update(false, message);
                            }
                        }
                    });
                } else {
                    onCompleteCallback.update(false, message);
                }
            }
        });
    }

    /**
     * Convert document snapshot information from firestore to current user
     * @param documentSnapshot Document snapshot containing user information from firestore
     * @param user Current user to set fields of
     */
    private static void convertDocumentSnapshotToUser(DocumentSnapshot documentSnapshot, User user) {
        user.setName((String) documentSnapshot.get(UserField.NAME.toString()));
        user.setNickname((String) documentSnapshot.get(UserField.NICKNAME.toString()));
        user.setEmail((String) documentSnapshot.get(UserField.EMAIL.toString()));
        Timestamp timestamp = (Timestamp) documentSnapshot.get(UserField.BIRTHDAY.toString());
        user.setBirthday(timestamp == null ? null : timestamp.toDate());
        user.setPhone((String) documentSnapshot.get(UserField.PHONE.toString()));

        Map<String, Double> location = (Map<String, Double>) documentSnapshot.get(UserField.LOCATION.toString());
        List<String> preferences = (List<String>) documentSnapshot.get(UserField.PREFERENCES.toString());
        List<String> hobbies = (List<String>) documentSnapshot.get(UserField.HOBBIES.toString());
        List<String> placesLived = (List<String>) documentSnapshot.get(UserField.PLACES_LIVED.toString());
        List<String> placesStudied = (List<String>) documentSnapshot.get(UserField.PLACES_STUDIED.toString());
        List<String> personalities = (List<String>) documentSnapshot.get(UserField.PERSONALITIES.toString());
        List<String> truths = (List<String>) documentSnapshot.get(UserField.TRUTHS.toString());
        List<String> lies = (List<String>) documentSnapshot.get(UserField.LIES.toString());

        if (null != location) {
            if (null != location.get("Latitude") && null != location.get("Longitude")) {
                user.setLocation(location.get("Latitude"), location.get("Longitude"));
            }
        }

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

    }

    /**
     * Uploads the data associated with current user to the firestore
     * @param user The user whose information is being uploaded to the firestore
     * @param onCompleteCallback Notifies the calling class that the task has been executed
     */
    public static void uploadUserInformationToFirestore(final User user, final OnCompleteCallback onCompleteCallback) {

        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getName())
                .build();

        mAuth.getCurrentUser().updateEmail(user.getEmail())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mAuth.getCurrentUser().updateProfile(profileUpdates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFirestore.collection("Profiles").document(user.getUid()).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        onCompleteCallback.update(true, "success");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        onCompleteCallback.update(false, e.getMessage());
                                                    }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        onCompleteCallback.update(false, e.getMessage());
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onCompleteCallback.update(false, e.getMessage());
                    }
                });
    }
}
