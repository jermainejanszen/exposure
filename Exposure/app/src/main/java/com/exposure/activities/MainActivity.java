package com.exposure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.exposure.R;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.constants.RequestCodes;
import com.exposure.dialogs.UploadPhotoDialog;
import com.exposure.fragments.ChatsFragment;
import com.exposure.fragments.MapFragment;
import com.exposure.fragments.ProfileFragment;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.CurrentUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private ProfileFragment profileFragment;
    private MapFragment mapFragment;
    private ChatsFragment chatsFragment;
    private CurrentUser currentUser;
    private UploadPhotoDialog uploadPhotoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        }

        final ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.fragment_profile);

        currentUser = new CurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

        UserInformationHandler.downloadUserInformation(currentUser,
                new OnCompleteCallback() {
                    @Override
                    public void update(boolean success) {
                        /* Once the user information has downloaded (either success of failure), we can
                           safely start initializing all of the fields */
                        setup();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });


    }

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

        /* When the user first signs up, send them to the edit profile page to fill in necessary details */
        if (!currentUser.validState()) {
            onEditProfileClick(null);
        }

        uploadPhotoDialog = new UploadPhotoDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Taken a new image */
        if (RequestCodes.TAKE_PHOTO_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                profileFragment.addBitmap((Bitmap) data.getExtras().get("data"));
            }
        }

        /* Selected image from photo library */
        if (RequestCodes.CHOOSE_FROM_LIBRARY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                try {
                    profileFragment.addBitmap(
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData())
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                };
            }
        }

        /* Edited profile details */
        if (RequestCodes.EDIT_PROFILE_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser = (CurrentUser) data.getSerializableExtra("current user");
                profileFragment = ProfileFragment.newInstance(currentUser);
                setFragment(profileFragment);
            }
        }
    }

    /* onClick handler for the profile fragment */
    public void onUploadImageClick(View view) {
        uploadPhotoDialog.displayPopup();
    }

    /* onClick handler for the profile fragment */
    public void onEditProfileClick(View view) {
        Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
        editProfileIntent.putExtra("current user", currentUser);
        startActivityForResult(editProfileIntent, RequestCodes.EDIT_PROFILE_REQUEST);
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,
                fragment).commit();
    }
}