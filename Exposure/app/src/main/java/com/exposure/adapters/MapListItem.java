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

public class MapListItem {

    private String uid;
    private Bitmap profileImage;
    private String name;

    private OnCompleteCallback completeCallback;

    public MapListItem(String uid, OnCompleteCallback completeCallback) {
        this.uid = uid;
        this.completeCallback = completeCallback;
        this.profileImage = null;
        this.name = "";
        loadFields();
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public String getName() {
        return name;
    }

    public String getUid(){
        return uid;
    }

    private void loadFields() {
        final int imageSize = 1024 * 1024;
        final byte[] bytes = new byte[imageSize];

        FirebaseFirestore.getInstance().collection("Profiles").document(uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            name = (String) documentSnapshot.get(UserField.NICKNAME.toString());
                            UserMediaHandler.downloadProfilePhotoFromFirebase(uid, bytes, imageSize, new OnCompleteCallback() {
                                        @Override
                                        public void update(boolean success, String message) {
                                            profileImage = BitmapFactory.decodeByteArray(bytes, 0, imageSize);
                                            completeCallback.update(true, "Success");
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
