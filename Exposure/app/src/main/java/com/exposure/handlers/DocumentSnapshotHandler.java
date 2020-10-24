package com.exposure.handlers;

import com.exposure.user.CurrentUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.List;

public class DocumentSnapshotHandler {

    public static CurrentUser convertDocumentSnapshotToCurrentUser(DocumentSnapshot documentSnapshot, String uid) {
        CurrentUser currentUser = new CurrentUser(uid);

        currentUser.setName((String) documentSnapshot.get("name"));
        currentUser.setNickname((String) documentSnapshot.get("nickname"));
        currentUser.setEmail((String) documentSnapshot.get("email"));
        currentUser.setBirthday((Date) documentSnapshot.get("birthday"));
        currentUser.setPhone((String) documentSnapshot.get("phone"));

        currentUser.setPreferences((List<String>) documentSnapshot.get("preferences"));
        currentUser.setPreferences((List<String>) documentSnapshot.get("hobbies"));
        currentUser.setPreferences((List<String>) documentSnapshot.get("placesLived"));
        currentUser.setPreferences((List<String>) documentSnapshot.get("placesStudied"));
        currentUser.setPreferences((List<String>) documentSnapshot.get("personalities"));

        return currentUser;
    }
}
