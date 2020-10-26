package com.exposure.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.exposure.callback.OnCompleteCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserMediaHandler {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static StorageReference mStorage = FirebaseStorage.getInstance().getReference();


    public static void uploadProfilePhotoToFirebase(Bitmap profilePhoto){
        final StorageReference mProfilePics = mStorage.child("Profile Photos" + "/" + FirebaseAuth.getInstance().getUid());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profilePhoto.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mProfilePics.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload succeeded
            }
        });
    }

    public static void downloadProfilePhotoFromFirebase(final byte[] profilePicture, final long photoSize, final OnCompleteCallback onCompleteCallback){
        final StorageReference mProfilePics = mStorage.child("Profile Photos" + "/" + FirebaseAuth.getInstance().getUid());

        mProfilePics.getBytes(photoSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                System.arraycopy(bytes, 0, profilePicture, 0, bytes.length);
                onCompleteCallback.update(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteCallback.update(false);
                //failed
            }
        });
    }





}
