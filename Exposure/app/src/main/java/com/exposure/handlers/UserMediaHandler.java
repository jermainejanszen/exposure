package com.exposure.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.exposure.callback.OnCompleteCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Handles all media related to the users of the application
 */
public class UserMediaHandler {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    /**
     * Upload user profile photo to firebase
     * @param profilePhoto the profile photo to be uploaded
     * @param onCompleteCallback notifies the calling class that the task has been executed
     */
    public static void uploadProfilePhotoToFirebase(Bitmap profilePhoto, final OnCompleteCallback
            onCompleteCallback){
        String path = "Profile Photos/" + FirebaseAuth.getInstance().getUid();

        final StorageReference mProfilePics = mStorage.child(path);
        Log.d("Upload", path);

        byte[] data = compress(profilePhoto);


        UploadTask uploadTask = mProfilePics.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Upload", "Success!");
                onCompleteCallback.update(true, "success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteCallback.update(false, e.getMessage());
            }
        });
    }

    /**
     * Upload a user's images to firebase
     * @param id the id of the image
     * @param image the image to be uploaded
     * @param onCompleteCallback notifies the calling class that the task has been executed
     */
    public static void uploadImageToFirebase(String id, Bitmap image, final OnCompleteCallback
            onCompleteCallback) {
        String refPath = mAuth.getCurrentUser().getUid() + "/Images/" + id;

        final StorageReference imageRef = mStorage.child(refPath);

        byte[] data = compress(image);

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteCallback.update(false, e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                onCompleteCallback.update(true, "success");
            }
        });
    }

    /**
     * Download a user's images from firebase
     * @param uid the users id
     * @param bitmaps the bitmaps to be added to upon downloading images from firebase
     * @param imagePaths the image paths to be added to upon downloading images from firebase
     * @param onCompleteCallback notifies the calling class that the task has been executed
     */
    public static void downloadImagesFromFirebase(final String uid, final Map<String, Bitmap>
            bitmaps, final List<String> imagePaths, final OnCompleteCallback onCompleteCallback) {
        String path = uid + "/Images/";

        Log.d("UserMedia", path);

        final StorageReference mImages = mStorage.child(path);

        mImages.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(final ListResult listResult) {
                        final List<StorageReference> imageRefs = listResult.getItems();
                        final int size = 1024 * 1024;

                        Log.d("SIZE", "" + listResult.getItems().size());

                        if (listResult.getItems().size() == 0) {
                            onCompleteCallback.update(true, "finished");
                            return;
                        }

                        for (int i = 0; i < listResult.getItems().size(); i++) {
                            final StorageReference imageRef = listResult.getItems().get(i);
                            imageRef.getBytes(size)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,
                                                    0, bytes.length);
                                            bitmaps.put(imageRef.getName(), bitmap);
                                            imagePaths.add(imageRef.getName());

                                            if (imagePaths.size() == listResult.getItems().size()) {
                                                onCompleteCallback.update(true, "finished");
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("FAILED", e.getMessage());
                                        }
                                    });
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
     * Deletes an image stored in firebase
     * @param id the id of the image to delete from firebase
     */
    public static void deleteImageFromFirebase(String id) {
        String refPath = mAuth.getCurrentUser().getUid() + "/Images/" + id;

        final StorageReference imageRef = mStorage.child(refPath);

        imageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /* Successfully deleted image */
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /* Failed to delete image */
                    }
                });
    }

    /**
     * Download a user's profile image from firebase
     * @param uid the user id of the user whose profile image we want to download from firebase
     * @param profilePicture the profile picture of the user that will be updated upon downloading
     *                       from firebase
     * @param photoSize the size of the profile image to be downloaded from firebase
     * @param onCompleteCallback notifies the calling class that the task has been executed
     */
    public static void downloadProfilePhotoFromFirebase(String uid, final byte[] profilePicture,
                                                        final long photoSize, final
                                                        OnCompleteCallback onCompleteCallback){
        final StorageReference mProfilePics = mStorage.child("Profile Photos" + "/" + uid);

        mProfilePics.getBytes(photoSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                System.arraycopy(bytes, 0, profilePicture, 0, bytes.length);
                onCompleteCallback.update(true, "success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteCallback.update(false, e.getMessage());
                //failed
            }
        });
    }

    // TODO : Javadocs
    private static byte[] compress(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int qualityCompression = 90;
        int imageSize = bitmap.getByteCount();
        int megabyte = 1048576;

        Log.d("COMPRESSING", "IMAGE SIZE IS " + imageSize);

        // Quality compression depends on the original size of the photo
        if (imageSize > 2 * megabyte) {
            qualityCompression = 10;
        } else if (imageSize > megabyte) {
            qualityCompression = 20;
        } else if (imageSize > 0.5 * megabyte) {
            qualityCompression = 40;
        } else if (imageSize > 0.25 * megabyte) {
            qualityCompression = 50;
        } else if (imageSize > 0.125 * megabyte) {
            qualityCompression = 60;
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, qualityCompression, baos);
        byte[] data =  baos.toByteArray();

        Log.d("COMPRESSED", "IMAGE SIZE IS " + data.length);

        return data;
    }
}
