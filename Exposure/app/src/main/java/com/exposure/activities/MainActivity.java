package com.exposure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.exposure.R;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.constants.RequestCodes;
import com.exposure.fragments.ChatsFragment;
import com.exposure.fragments.MapFragment;
import com.exposure.fragments.ProfileFragment;
import com.exposure.handlers.DateHandler;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.popups.RetrieveImageActivity;
import com.exposure.user.CurrentUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main activity of the application. TODO: finish
 */
public class MainActivity extends AppCompatActivity {
    private static CurrentUser currentUser;
    private static Map<String, Bitmap> bitmaps;
    private static List<String> imagePaths;
    private BottomNavigationView navigationView;
    private ProfileFragment profileFragment;
    private MapFragment mapFragment;
    private ChatsFragment chatsFragment;
    private ProgressBar progressBar;

    /**
     * On creating this activity, the view is set, user is directed to sign in and user information
     * is downloaded from the firebase firestore
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        }

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.fragment_profile);

        currentUser = new CurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        bitmaps = new HashMap<>();
        imagePaths = new ArrayList<>();

        UserInformationHandler.downloadUserInformation(currentUser,
                new OnCompleteCallback() {
                    @Override
                    public void update(boolean success, String message) {
                        /* Once the user information has downloaded (either success of failure),
                            we can safely start initializing all of the fields */
                        if (success) {
                            UserInformationHandler.downloadCurrentUserConnections(currentUser,
                                    new OnCompleteCallback() {
                                        @Override
                                        public void update(boolean success, String message) {
                                            if (success) {
                                                UserMediaHandler.downloadImagesFromFirebase
                                                        (currentUser.getUid(), bitmaps,
                                                                imagePaths, new OnCompleteCallback() {
                                                    @Override
                                                    public void update(boolean success, String message) {
                                                        setup();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                            } else {
                                                /* Failed to download current user connections */
                                                finish();
                                            }
                                        }
                                    });
                        } else {
                            /* Failed to download user information */
                            finish();
                        }
                    }
                });
    }

    /**
     * Returns the current user object
     * @return current user object
     */
    public static CurrentUser getCurrentUser(){
        return currentUser;
    }

    /**
     * Returns the image paths for each of the pictures in the current user's profile
     * @return image paths for each image in current user's profile
     */
    public static List<String> getImagePaths() {
        return imagePaths;
    }

    /**
     * Returns the bitmaps corresponding to each of the pictures in the current user's profile
     * @return bitmaps for each image in current user's profile
     */
    public static Map<String, Bitmap> getBitmaps() {
        return bitmaps;
    }

    /**
     * Sets up the maps, chats and profile fragments of the application and initiates the navigation
     * view listener
     */
    private void setup() {
        mapFragment = new MapFragment();
        chatsFragment = new ChatsFragment();
        profileFragment = ProfileFragment.newInstance(currentUser);

        /* Set profile fragment as the default fragment */
        setFragment(profileFragment);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                if(R.id.fragment_map == item.getItemId()) {
                    fragment = mapFragment;
                } else if(R.id.fragment_chats == item.getItemId()) {
                    fragment = chatsFragment;
                } else if(R.id.fragment_profile == item.getItemId()) {
                    fragment = profileFragment;
                }

                setFragment(fragment);
                return true;
            }
        });

        /* When user first signs up, send them to edit profile page to fill in necessary details */
        if (!currentUser.validState()) {
            onEditProfileClick(null);
        }
    }

    /**
     * Upon returning to the main activity from another activity, performs actions according to
     * returning request code
     * @param requestCode request code signifying what action is required
     * @param resultCode result code indicating the result of the returning activity
     * @param data data returned from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Upon retrieving an image from camera or library, generate time stamp and upload image to
        * firebase firestore */
        if (RequestCodes.RETRIEVE_IMAGE_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                profileFragment.setProgressBarVisibility(View.VISIBLE);
                String source = data.getStringExtra("Source");

                final String id;
                final Bitmap bitmap;

                if ("Image Capture".equals(source)) {
                    id = DateHandler.generateTimestamp();
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else if ("Library".equals(source)) {
                    id = DateHandler.generateTimestamp();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                data.getData());
                    } catch (IOException e) {
                        return;
                    }
                } else {
                    return;
                }

                /* Upload image to firebase firestore */
                UserMediaHandler.uploadImageToFirebase(id, bitmap, new OnCompleteCallback() {
                    @Override
                    public void update(boolean success, String message) {
                        /* Only display image locally if it successfully uploads to firebase */
                        if (success) {
                            profileFragment.addBitmap(
                                    id,
                                    bitmap
                            );
                        } else {
                            Toast.makeText(getApplicationContext(), message,
                                    Toast.LENGTH_SHORT).show();
                        }
                        profileFragment.setProgressBarVisibility(View.INVISIBLE);
                    }
                });
            }
        }

        /* Edited profile details */
        if (RequestCodes.EDIT_PROFILE_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser = (CurrentUser) data.getSerializableExtra("current user");
                profileFragment.updateCurrentUser(currentUser);
                setFragment(profileFragment);
            }
        }
    }

    /**
     * OnClick handler for uploading an image to the application
     * @param view the current GUI view
     */
    public void onUploadImageClick(View view) {
        Intent intent = new Intent(this, RetrieveImageActivity.class);
        startActivityForResult(intent, RequestCodes.RETRIEVE_IMAGE_REQUEST);
    }

    /**
     * OnClick handler for clicking on the edit profile fragment
     * @param view the current GUI view
     */
    public void onEditProfileClick(View view) {
        Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
        editProfileIntent.putExtra("current user", currentUser);
        startActivityForResult(editProfileIntent, RequestCodes.EDIT_PROFILE_REQUEST);
    }

    /**
     * TODO: this
     * @param fragment
     */
    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,
                fragment).commit();
    }
}