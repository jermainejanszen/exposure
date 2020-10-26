package com.exposure.adapters;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatListItem {

    private String uid;
    private Bitmap profileImage;
    private String name;
    private String lastMessage;
    private String date;

    public ChatListItem(String uid) {
        this.uid = uid;
        this.profileImage = null;
        this.name = "First";
        this.lastMessage = "This was the last message that was sent.";
        this.date = "24/10/20";
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

    // TODO : Update the adapter after the fields have been loaded
    private void loadFields() {
        final DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("Profiles")
                .document(this.uid);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(null != document && document.exists()) {
                        name = (String)document.get("nickname");
                    }
                }
            }
        });
    }
}
