package com.exposure.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import com.exposure.activities.MainActivity;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.containers.LastMessageContainer;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.UserField;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a particular conversation between two users as it is represented in the list of chats
 */
public class ChatListItem {

    private final String uid;
    private Bitmap profileImage;
    private String name;
    private String lastMessage;
    private String date;
    private long time;

    /**
     * Initialises a chat list item
     * @param uid uid of the other user that has been connected with
     */
    public ChatListItem(String uid) {
        this.uid = uid;
        this.profileImage = null;
        this.name = "";
        this.date = "";
        this.lastMessage = "Send your first message.";
    }

    /**
     * Returns the uid of the other user that has been connected with
     * @return uid of the other user
     */
    public String getUid() {
        return uid;
    }

    /**
     * Returns the profile image of the other user that has been connected with
     * @return the profile image of the other user
     */
    public Bitmap getProfileImage() {
        return profileImage;
    }

    /**
     * Returns the nickname of the other user that has been connected with
     * @return the nickname of the other user
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the most recent message sent between the current user and the other user
     * @return the most recent message sent between the current user and the other user
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * Returns the time that the most recent message was sent between current user and other user
     * @return the time that the most recent message was sent
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the date that the most recent message was sent between current user and other user
     * @return the date that the most recent message was sent
     */
    public String getDate() {
        return date;
    }

    /**
     * Loads the fields of the chat list item with the media and information stored on firestore
     * @param onCompleteCallback called on completion of the downloading profile photo from
     *                           firebase to indicate success or failure of the action
     */
    protected void loadFields(final OnCompleteCallback onCompleteCallback) {
        final int imageSize = 1024 * 1024;
        final byte[] bytes = new byte[imageSize];

        /* load in nickname from firebase firestore */
        FirebaseFirestore.getInstance().collection("Profiles").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = (String) documentSnapshot.get(UserField.NICKNAME.toString());
                        final LastMessageContainer container = new LastMessageContainer();
                        /* load in last chat message and time from firebase firestore */
                        UserInformationHandler.loadLastChatMessageAndTime
                                (MainActivity.getCurrentUser().getUid(), uid, container,
                                        new OnCompleteCallback() {
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
                                            date = new SimpleDateFormat("h:mm aa",
                                                    Locale.ENGLISH).format(latestChatDate);
                                        } else {
                                            date = new SimpleDateFormat("dd/MM/yy",
                                                    Locale.ENGLISH).format(latestChatDate);
                                        }
                                    }
                                    /* load in profile photo from firebase firestore */
                                    UserMediaHandler.downloadProfilePhotoFromFirebase(uid, bytes,
                                            imageSize, new OnCompleteCallback() {
                                        @Override
                                        public void update(boolean success, String message) {
                                            profileImage = BitmapFactory.decodeByteArray(bytes,
                                                    0, imageSize);
                                            onCompleteCallback.update(true,
                                                    "Success");
                                        }
                                    });
                                } else {
                                    onCompleteCallback.update(false, "Failed to download last chat message and time");
                                }
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
