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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.ChipsRecyclerViewAdapter;
import com.exposure.constants.ResultCodes;
import com.exposure.popups.LostGameActivity;
import com.exposure.popups.WonGameActivity;
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

/**
 * Activity allowing current user to view another user's profile
 */
public class ViewOtherProfileActivity extends AppCompatActivity {

    private ChipsRecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter,
            personalitiesAdapter;
    private RecyclerView studyLocationsRecyclerView, areasLivedInRecyclerView, hobbiesRecyclerView,
            personalityTypesRecyclerView;
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

    /**
     * On creating this activity, the view is set and the profile information about the other user
     * is downloaded.
     * @param savedInstanceState saved instance state for the activity
     */
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
        gridViewAdapter = new GridViewAdapter(getApplicationContext(), bitmaps, imagePaths);
        gridView.setAdapter(gridViewAdapter);

        /* Download user information from firebase firestore */
        UserInformationHandler.downloadUserInformation(otherUser,
                new OnCompleteCallback() {
                    @Override
                    public void update(boolean success, String message) {
                        /* Once the user information has downloaded (either success of failure),
                            we can safely start initializing all of the fields */
                        initialiseFields();
                    }
                });

    }

    /**
     * On returning from the game, this is called to handle the actions required if the participant
     * either wins or looses the game
     * @param requestCode the request code signifying what action has occurred
     * @param resultCode the result code denoting whether the participant won or lost the game
     * @param data the data returned from the game activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RequestCodes.GAME_RESULT == requestCode) {
            if (ResultCodes.WON_GAME == resultCode) {
                Intent intent = new Intent(this, WonGameActivity.class);
                startActivity(intent);
            } else if (ResultCodes.LOST_GAME == resultCode) {
                Intent intent = new Intent(this, LostGameActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * Initialise the fields of the other user's profile
     */
    private void initialiseFields() {

        if (currentUser.isConnected(otherUser.getUid())) {
            playButton.setVisibility(View.VISIBLE);
        } else {
            connectButton.setVisibility(View.VISIBLE);
        }

        //TODO: add conditions based on 'exposed information'

        studyLocationsAdapter = new ChipsRecyclerViewAdapter(this,
                otherUser.getPlacesStudied(), false);
        areasLivedInAdapter = new ChipsRecyclerViewAdapter(this,
                otherUser.getPlacesLived(), false);
        hobbiesAdapter = new ChipsRecyclerViewAdapter(this,
                otherUser.getHobbies(), false);
        personalitiesAdapter = new ChipsRecyclerViewAdapter(this,
                otherUser.getPersonalities(), false);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        /* If we haven't already downloaded the user's images from firebase, do so */
        UserMediaHandler.downloadImagesFromFirebase(otherUser.getUid(), bitmaps, imagePaths, new
                OnCompleteCallback() {
                @Override
                public void update(boolean success, String message) {
                    gridViewAdapter.notifyDataSetChanged();
                }
            });

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

            StringBuilder preferencesString = new StringBuilder("Interested in " +
                    preferences.get(0));

            for (int i = 1; i < preferences.size(); i++) {
                preferencesString.append(", ").append(preferences.get(i));
            }

            preferencesText.setText(preferencesString.toString());
        }

        profileByteArray = new byte[1024*1024];

        /* download the profile photo of the other user from firebase */
        UserMediaHandler.downloadProfilePhotoFromFirebase(otherUser.getUid(), profileByteArray,
                profileByteArray.length, new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success){
                    profileImage.setImageBitmap(BitmapFactory.decodeByteArray(profileByteArray,
                            0, profileByteArray.length));
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * OnClick handler for when the current user choose to connect with the other user
     * @param view the current GUI view
     */
    public void onConnectPressed(View view) {
        progressBar.setVisibility(View.VISIBLE);
        currentUser.addConnection(otherUser.getUid());

        /* upload the user information to firestore after having added a new connection */
        UserInformationHandler.uploadUserInformationToFirestore(currentUser,
                new OnCompleteCallback() {
            public void update(boolean success, String message) {
                if (success) {
                    UserInformationHandler.addOtherUserConnection(otherUser.getUid(),
                            currentUser.getUid(), new OnCompleteCallback() {
                        @Override
                        public void update(boolean success, String message) {
                            if (success) {
                                connectButton.setVisibility(View.INVISIBLE);
                                playButton.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * OnClick handler for the play game button
     * @param view the current GUI view
     */
    public void onPlayPressed(View view) {
        Intent gameIntent = new Intent(this, ThreeTruthsOneLieActivity.class);
        gameIntent.putExtra("current user", currentUser);
        gameIntent.putExtra("other user", otherUser);
        startActivityForResult(gameIntent, RequestCodes.GAME_RESULT);
    }
}
