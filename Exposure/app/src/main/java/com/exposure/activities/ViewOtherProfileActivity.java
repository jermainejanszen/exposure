package com.exposure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.ChipsRecyclerViewAdapter;
import com.exposure.adapters.GridViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.handlers.DateHandler;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.CurrentUser;
import com.exposure.user.OtherUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewOtherProfileActivity extends AppCompatActivity {

    private String otherUserUid = "A7Q6DbHMoLZrffITQ7Gou2eJXCB2";

    private ChipsRecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private RecyclerView studyLocationsRecyclerView, areasLivedInRecyclerView, hobbiesRecyclerView, personalityTypesRecyclerView;
    private TextView displayNameText, ageText, preferencesText;
    private ImageView profileImage;
    private GridView gridView;
    private Map<String, Bitmap> bitmaps;
    private GridViewAdapter gridViewAdapter;
    private List<String> imagePaths;
    private byte[] profileByteArray;
    private ProgressBar progressBar;
    private OtherUser otherUser;
    private CurrentUser currentUser;
    private Button connectButton;
    private Button gameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_profile);

        currentUser = (CurrentUser) getIntent().getSerializableExtra("current user");

        //TODO: link this up
        //otherUserUid = getIntent().getStringExtra("Other User Uid");
        otherUser = new OtherUser(otherUserUid);
        currentUser = MainActivity.getCurrentUser();

        studyLocationsRecyclerView = findViewById(R.id.study_locations_recycler_view);
        areasLivedInRecyclerView = findViewById(R.id.areas_lived_in_recycler_view);
        hobbiesRecyclerView = findViewById(R.id.hobbies_recycler_view);
        personalityTypesRecyclerView = findViewById(R.id.personality_types_recycler_view);
        displayNameText = findViewById(R.id.display_name);
        ageText = findViewById(R.id.age);
        preferencesText = findViewById(R.id.preferences);
        profileImage = findViewById(R.id.profile_image);
        gridView = findViewById(R.id.image_grid_view);
        progressBar = findViewById(R.id.progress_bar);
        connectButton = findViewById(R.id.connect_with_user);
        gameButton = findViewById(R.id.play_game);

        if (currentUser.getConnections().contains(otherUser)){
            connectButton.setText("Connected");
        }

        bitmaps = new HashMap<>();
        imagePaths = new ArrayList<>();
        gridViewAdapter = new GridViewAdapter(this, bitmaps, imagePaths);
        gridView.setAdapter(gridViewAdapter);

        UserInformationHandler.downloadUserInformation(otherUser,
                new OnCompleteCallback() {
                    @Override
                    public void update(boolean success) {
                        /* Once the user information has downloaded (either success of failure), we can
                           safely start initializing all of the fields */
                        // progressBar.setVisibility(View.INVISIBLE);
                        initialiseFields();
                    }
                });

        boolean connected = updateUserConnection();
        
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               connectWithOtherUser();
            }
        });

        if (connected){
            gameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Takes you to the game
                }
            });
        }

    }

    private void initialiseFields() {

        studyLocationsAdapter = new ChipsRecyclerViewAdapter(this, otherUser.getPlacesStudied(), false);
        areasLivedInAdapter = new ChipsRecyclerViewAdapter(this, otherUser.getPlacesLived(), false);
        hobbiesAdapter = new ChipsRecyclerViewAdapter(this, otherUser.getHobbies(), false);
        personalitiesAdapter = new ChipsRecyclerViewAdapter(this, otherUser.getPersonalities(), false);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        /* If we haven't already downloaded the user's images from firebase, do so */
        if (null != bitmaps && null != imagePaths) {
            UserMediaHandler.downloadImagesFromFirebase(bitmaps, imagePaths, new OnCompleteCallback() {
                @Override
                public void update(boolean success) {
                    gridViewAdapter.notifyDataSetChanged();
                }
            });
        }

        /* Set the display name to the nickname if it exists, otherwise just use the users name */
        displayNameText.setText(
                otherUser.getNickname() == null ? otherUser.getName() : otherUser.getNickname());

        /* Set the users age if they have entered their birthday */
        if (null != otherUser.getBirthday()) {
            ageText.setText(
                    String.format(Locale.ENGLISH,
                            "Age %d",
                            DateHandler.yearsBetween(otherUser.getBirthday(), new Date())
                    )
            );
        }

        /* Check if the user has entered any preferences */
        List<String> preferences = otherUser.getPreferences();

        if (!preferences.isEmpty()) {
            Collections.sort(preferences);

            String preferencesString = "Interested in " + preferences.get(0);

            for (int i = 1; i < preferences.size(); i++) {
                preferencesString += ", " + preferences.get(i);
            }

            preferencesText.setText(preferencesString);
        }

        profileByteArray = new byte[1024*1024];

        UserMediaHandler.downloadProfilePhotoFromFirebase(profileByteArray, profileByteArray.length, new OnCompleteCallback() {
            @Override
            public void update(boolean success) {
                if (success){
                    profileImage.setImageBitmap(BitmapFactory.decodeByteArray(profileByteArray, 0, profileByteArray.length));
                }
            }
        });
    }

    private boolean updateUserConnection(){
        //TODO: this is not working correctly...
        if (currentUser.getConnections().contains(otherUser) && otherUser.getConnections().contains(currentUser)){
            connectButton.setText("CONNECTED");
            return true;
        } else if (currentUser.getConnections().contains(otherUser)){
            connectButton.setText("PENDING");
        }
        return false;
    }

    private void connectWithOtherUser(){
        List<OtherUser> connections = currentUser.getConnections();
        connections.add(otherUser);
        currentUser.setConnections(connections);
        updateUserConnection();

        UserInformationHandler.uploadUserInformationToFirestore(currentUser, new OnCompleteCallback() {
            @Override
            public void update(boolean success) {

            }
        });
    }
}
