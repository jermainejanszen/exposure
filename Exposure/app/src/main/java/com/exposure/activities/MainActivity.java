package com.exposure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.exposure.R;
import com.exposure.fragments.ChatsFragment;
import com.exposure.fragments.MapFragment;
import com.exposure.fragments.ProfileFragment;
import com.exposure.handlers.DateHandler;
import com.exposure.user.CurrentUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static int CAMERA_REQUEST = 101;
    private static int GALLERY_REQUEST = 102;
    private static int EDIT_PROFILE_REQUEST = 103;

    private BottomNavigationView navigationView;
    private ProfileFragment profileFragment;
    private MapFragment mapFragment;
    private ChatsFragment chatsFragment;
    private CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        }

        currentUser = new CurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

        /* Temporarily creating date instance for birthday */
        try {
            currentUser.setBirthday(DateHandler.convertToDate("02/10/1998"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        currentUser.setEmail("bgane@live.com.au");
        currentUser.setName("Ben");
        currentUser.setPhone("0432250691");
        currentUser.setNickname("Benji");
        currentUser.setHobbies(new ArrayList<>(Arrays.asList("Guitar", "Piano")));
        currentUser.setPersonalities(new ArrayList<>(Arrays.asList("Memer", "Introvert")));
        currentUser.setPlacesLived(new ArrayList<>(Arrays.asList("Sydney", "Melbourne")));
        currentUser.setPlacesStudied(new ArrayList<>(Arrays.asList("The University of Sydney")));
        currentUser.setPreferences(new ArrayList<>(Arrays.asList("Females")));

        mapFragment = new MapFragment();
        chatsFragment = new ChatsFragment();

        profileFragment = ProfileFragment.newInstance(currentUser);

        // Set map as the default fragment
        setFragment(mapFragment);

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.fragment_map:
                        fragment = mapFragment;
                        break;
                    case R.id.fragment_chats:
                        fragment = chatsFragment;
                        break;
                    case R.id.fragment_profile:
                        fragment = profileFragment;
                        break;
                }
                setFragment(fragment);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Taken a new image */
        if (CAMERA_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                profileFragment.addBitmap((Bitmap) data.getExtras().get("data"));
            }
        }

        /* Selected image from photo gallery */
        if (GALLERY_REQUEST == requestCode) {
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
        if (EDIT_PROFILE_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser = (CurrentUser) data.getSerializableExtra("current user");
                profileFragment = ProfileFragment.newInstance(currentUser);
                setFragment(profileFragment);
            }
        }
    }

    /* onClick handler for the profile fragment */
    public void onAddImageButtonClick(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    /* onClick handler for the profile fragment */
    public void onEditProfileClick(View view) {
        Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
        editProfileIntent.putExtra("current user", currentUser);
        startActivityForResult(editProfileIntent, EDIT_PROFILE_REQUEST);
    }

    /* onClick handler for the profile fragment */
    public void onGalleryClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,
                fragment).commit();
    }
}