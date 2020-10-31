package com.exposure.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.exposure.activities.MainActivity;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.containers.LastMessageContainer;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.UserField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ChatListItem {

    private final String uid;
    private Bitmap profileImage;
    private String name;
    private String lastMessage;
    private String date;

    private final OnCompleteCallback completeCallback;

    public ChatListItem(String uid, OnCompleteCallback completeCallback) {
        this.uid = uid;
        this.completeCallback = completeCallback;
        this.profileImage = null;
        this.name = "";
        this.lastMessage = "Send your first message.";
        this.date = "";
        loadFields();
    }

    public String getUid() {
        return uid;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getDate() {
        return date;
    }

    protected void loadFields() {
        final int imageSize = 1024 * 1024;
        final byte[] bytes = new byte[imageSize];

        FirebaseFirestore.getInstance().collection("Profiles").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = (String) documentSnapshot.get(UserField.NICKNAME.toString());
                        final LastMessageContainer container = new LastMessageContainer();
                        UserInformationHandler.loadLastChatMessageAndTime(MainActivity.getCurrentUser().getUid(), uid, container, new OnCompleteCallback() {
                            @Override
                            public void update(boolean success, String message) {
                                if (success) {
                                    if (null != container.getMessage()) {
                                        lastMessage = container.getMessage();
                                    }
                                    if (null != container.getTime()) {
                                        Date time = new Date(container.getTime());
                                        date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(time);
                                    }
                                    UserMediaHandler.downloadProfilePhotoFromFirebase(uid, bytes, imageSize, new OnCompleteCallback() {
                                        @Override
                                        public void update(boolean success, String message) {
                                            profileImage = BitmapFactory.decodeByteArray(bytes, 0, imageSize);
                                            completeCallback.update(true, "Success");
                                        }
                                    });
                                }
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
