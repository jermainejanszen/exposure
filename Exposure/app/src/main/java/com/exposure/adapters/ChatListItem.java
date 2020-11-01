package com.exposure.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
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
    private long time;

    public ChatListItem(String uid) {
        this.uid = uid;
        this.profileImage = null;
        this.name = "";
        this.date = "";
        this.lastMessage = "Send your first message.";
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

    public long getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    protected void loadFields(final OnCompleteCallback onCompleteCallback) {
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
                                        time = container.getTime();
                                        Date latestChatDate = new Date(time);

                                        if (DateUtils.isToday(time)) {
                                            date = new SimpleDateFormat("h:mm aa", Locale.ENGLISH).format(latestChatDate);
                                        } else {
                                            date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(latestChatDate);
                                        }
                                    }
                                    UserMediaHandler.downloadProfilePhotoFromFirebase(uid, bytes, imageSize, new OnCompleteCallback() {
                                        @Override
                                        public void update(boolean success, String message) {
                                            profileImage = BitmapFactory.decodeByteArray(bytes, 0, imageSize);
                                            onCompleteCallback.update(true, "Success");
                                        }
                                    });
                                }
                                onCompleteCallback.update(false, "failed");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                /* Failed to download user information */
                onCompleteCallback.update(false, e.getMessage());
            }
        });
    }
}
