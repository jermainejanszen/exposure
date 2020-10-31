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
import com.exposure.user.ConnectionItem;
import com.exposure.adapters.GridViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.constants.RequestCodes;
import com.exposure.handlers.DateHandler;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.CurrentUser;
import com.exposure.user.OtherUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewOtherProfileActivity extends AppCompatActivity {

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
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_profile);

        currentUser = MainActivity.getCurrentUser();
        if(null == currentUser) {
            finish();
        }

        otherUser = new OtherUser(getIntent().getStringExtra("Uid"));

        studyLocationsRecyclerView = findViewById(R.id.study_locations_recycler_view);
        areasLivedInRecyclerView = findViewById(R.id.areas_lived_in_recycler_view);
        hobbiesRecyclerView = findViewById(R.id.hobbies_recycler_view);
        personalityTypesRecyclerView = findViewById(R.id.personality_types_recycler_view);
        displayNameText = findViewById(R.id.display_name);
        ageText = findViewById(R.id.age);
        preferencesText = findViewById(R.id.preferences);
        profileImage = findViewById(R.id.profile_image);
        gridView = findViewById(R.id.image_grid_view);
        progressBar = findViewById(R.id.other_profile_progress_bar);
        connectButton = findViewById(R.id.connect_button);
        playButton = findViewById(R.id.play_button);

        progressBar.setVisibility(View.VISIBLE);

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

    }

    private void initialiseFields() {

        if (currentUser.isConnected(otherUser.getUid())) {
            playButton.setVisibility(View.VISIBLE);
        } else {
            connectButton.setVisibility(View.VISIBLE);
        }

        //TODO: add conditions based on 'exposed information'

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

            StringBuilder preferencesString = new StringBuilder("Interested in " + preferences.get(0));

            for (int i = 1; i < preferences.size(); i++) {
                preferencesString.append(", ").append(preferences.get(i));
            }

            preferencesText.setText(preferencesString.toString());
        }

        profileByteArray = new byte[1024*1024];

        UserMediaHandler.downloadProfilePhotoFromFirebase(profileByteArray, profileByteArray.length, new OnCompleteCallback() {
            @Override
            public void update(boolean success) {
                if (success){
                    profileImage.setImageBitmap(BitmapFactory.decodeByteArray(profileByteArray, 0, profileByteArray.length));
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void onConnectPressed(View view){
        progressBar.setVisibility(View.VISIBLE);
        List<ConnectionItem> currentUserConnections = currentUser.getConnections();
        currentUserConnections.add(new ConnectionItem(otherUser.getUid(), new ArrayList<String>()));
        currentUser.setConnections(currentUserConnections);

        UserInformationHandler.uploadUserInformationToFirestore(currentUser, new OnCompleteCallback() {
            @Override
            public void update(boolean success) {
                if (success) {
                    UserInformationHandler.addOtherUserConnection(otherUser.getUid(), currentUser.getUid(), new OnCompleteCallback() {
                        @Override
                        public void update(boolean success) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void onPlayPressed(View view){
        Intent gameIntent = new Intent(this, ThreeTruthsOneLieActivity.class);
        gameIntent.putExtra("current user", currentUser);
        gameIntent.putExtra("other user", otherUser);
        startActivityForResult(gameIntent, RequestCodes.GAME_RESULT);
    }
}
