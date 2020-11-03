package com.exposure.handlers;

import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.exposure.activities.MainActivity;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.containers.LastMessageContainer;
import com.exposure.user.ConnectionItem;
import com.exposure.user.CurrentUser;
import com.exposure.user.OtherUser;
import com.exposure.user.UserField;
import com.exposure.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
                        if (null == docConnections) {
                            onCompleteCallback.update(true, "no connections");
                            return;
                        }
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
                                otherUser.addConnection(new OtherUser(uidToAdd).toConnectionItem());
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

    public static void loadLastChatMessageAndTime(final String uid1,
                                                  final String uid2,
                                                  final LastMessageContainer messageContainer,
                                                  final OnCompleteCallback onCompleteCallback) {

        String docRefID;
        if (uid1.compareTo(uid2) >= 0) {
            docRefID = uid1.concat(uid2);
        } else {
            docRefID = uid2.concat(uid1);
        }

        final DocumentReference docRefMessages = mFirestore.collection("chats").document(docRefID);
        docRefMessages.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains("messages")) {
                    List<Object> messages = (ArrayList<Object>) documentSnapshot.get("messages");
                    if (messages.size() > 0) {
                        Map<String, Object> lastMessage = (Map<String, Object>) messages.get(messages.size() - 1);
                        messageContainer.setMessage((String) lastMessage.get("message"));
                        messageContainer.setTime((Long) lastMessage.get("time"));
                    }
                    onCompleteCallback.update(true, "success");
                } else {
                    onCompleteCallback.update(true, "no chat");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteCallback.update(false, e.getMessage());
            }
        });
    }

    /**
     * Convert document snapshot information from firestore to current user
     * @param documentSnapshot Document snapshot containing user information from firestore
     * @param user Current user to set fields of
     */
    public static void convertDocumentSnapshotToUser(DocumentSnapshot documentSnapshot, User user) {
        user.setNickname((String) documentSnapshot.get(UserField.NICKNAME.toString()));
        Timestamp timestamp = (Timestamp) documentSnapshot.get(UserField.BIRTHDAY.toString());
        user.setBirthday(timestamp == null ? null : timestamp.toDate());
        user.setPhone((String) documentSnapshot.get(UserField.PHONE.toString()));

        if (mAuth.getCurrentUser().getUid().equals(user.getUid())) {
            user.setName(mAuth.getCurrentUser().getDisplayName());
            user.setEmail(mAuth.getCurrentUser().getEmail());
        } else {
            user.setName((String) documentSnapshot.get(UserField.NAME.toString()));
            user.setEmail((String) documentSnapshot.get(UserField.EMAIL.toString()));
        }

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

        // If we are updating the currently logged in users information, update their
        // Display name and email as well. Otherwise just upload other information.
        if (mAuth.getCurrentUser().getUid().equals(user.getUid())) {

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

        } else {

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
    }

    public static void downloadOtherUsers(final List<CurrentUser> destination, final OnCompleteCallback onCompleteCallback) {
        mFirestore.collection("Profiles").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        destination.clear();
                        for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.getId().equals(mAuth.getCurrentUser().getUid())) {
                                continue;
                            }
                            CurrentUser user = new CurrentUser(snapshot.getId());
                            convertDocumentSnapshotToUser(snapshot, user);
                            destination.add(user);
                        }
                        onCompleteCallback.update(true, "success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onCompleteCallback.update(false, "failed to download all user data");
                    }
                });
    }
}
