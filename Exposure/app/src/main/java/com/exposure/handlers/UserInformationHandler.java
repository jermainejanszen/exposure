package com.exposure.handlers;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.exposure.callback.OnCompleteCallback;
import com.exposure.user.CurrentUser;
import com.exposure.user.UserField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserInformationHandler {

    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void downloadUserInformation(final Context context, final CurrentUser currentUser, final OnCompleteCallback onCompleteCallback){
        mFirestore.collection("Profiles").document(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        convertDocumentSnapshotToCurrentUser(documentSnapshot, currentUser);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "failed to download" + e, Toast.LENGTH_LONG).show();
                        // Failed to download user information
                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        /* Notify the calling class that the task has been executed */
                        onCompleteCallback.update();
                    }
        });
    }

    private static void convertDocumentSnapshotToCurrentUser(DocumentSnapshot documentSnapshot, CurrentUser currentUser) {
        currentUser.setName((String) documentSnapshot.get(UserField.NAME.toString()));
        currentUser.setNickname((String) documentSnapshot.get(UserField.NICKNAME.toString()));
        currentUser.setEmail((String) documentSnapshot.get(UserField.EMAIL.toString()));
        Timestamp timestamp = (Timestamp) documentSnapshot.get(UserField.BIRTHDAY.toString());
        currentUser.setBirthday(timestamp == null ? null : timestamp.toDate());
        currentUser.setPhone((String) documentSnapshot.get(UserField.PHONE.toString()));

        List<String> preferences = (List<String>) documentSnapshot.get(UserField.PREFERENCES.toString());
        if (preferences != null){
            currentUser.setPreferences(preferences);
        }

        List<String> hobbies = (List<String>) documentSnapshot.get(UserField.HOBBIES.toString());
        if (hobbies != null){
            //TODO: this may cause an error in the recycler view as it is referencing an old list
            currentUser.setHobbies(hobbies);
        }

        List<String> placesLived = (List<String>) documentSnapshot.get(UserField.PLACES_LIVED.toString());
        if (placesLived != null){
            currentUser.setPlacesLived(placesLived);
        }

        List<String> placesStudied = (List<String>) documentSnapshot.get(UserField.PLACES_STUDIED.toString());
        if (placesStudied != null){
            currentUser.setPlacesStudied(placesStudied);
        }

        List<String> personalities = (List<String>) documentSnapshot.get(UserField.PERSONALITIES.toString());
        if (personalities != null) {
            currentUser.setPersonalities(personalities);
        }
    }
}