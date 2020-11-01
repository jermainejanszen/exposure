package com.exposure.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.exposure.callback.OnCompleteCallback;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.UserField;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents a user item in seen in the map fragment of the application
 */
public class MapListItem {

    private final String uid;
    private Bitmap profileImage;
    private String name;
    private final OnCompleteCallback completeCallback;

    /**
     * Constructor for user item in map fragment
     * @param uid uid of user represented by the item
     * @param completeCallback called on completion of the downloading profile photo from
     *                            firebase to indicate success or failure of the action
     */
    public MapListItem(String uid, OnCompleteCallback completeCallback) {
        this.uid = uid;
        this.completeCallback = completeCallback;
        this.profileImage = null;
        this.name = "";
        loadFields();
    }

    /**
     * Returns the profile image of the user represented by the map list item
     * @return Profile image of the user represented by the map list item
     */
    public Bitmap getProfileImage() {
        return profileImage;
    }

    /**
     * Returns the name of the user represented by the map list item
     * @return the name of the user represented by the map list item
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the uid of the user represented by the map list item
     * @return the uid of the user represented by the map list item
     */
    public String getUid(){
        return uid;
    }

    /**
     * Loads the profile image and nickname of the user represented by the map list item
     */
    protected void loadFields() {
        final int imageSize = 1024 * 1024;
        final byte[] bytes = new byte[imageSize];

        FirebaseFirestore.getInstance().collection("Profiles").document(uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            name = (String) documentSnapshot.get(UserField.NICKNAME.toString());
                            UserMediaHandler.downloadProfilePhotoFromFirebase(uid, bytes, imageSize,
                                    new OnCompleteCallback() {
                                        @Override
                                        public void update(boolean success, String message) {
                                            profileImage = BitmapFactory.decodeByteArray(bytes,
                                                    0, imageSize);
                                            completeCallback.update(true,
                                                    "Success");
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                        /* Failed to download user information */
                        completeCallback.update(false, e.getMessage());
                }
            });
    }
}
