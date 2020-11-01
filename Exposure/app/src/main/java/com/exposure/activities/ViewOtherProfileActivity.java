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
import android.widget.RelativeLayout;
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
import com.exposure.user.ConnectionItem;
import com.exposure.adapters.GridViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.constants.RequestCodes;
import com.exposure.handlers.DateHandler;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.CurrentUser;
import com.exposure.user.OtherUser;
import com.exposure.user.UserField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewOtherProfileActivity extends AppCompatActivity {

    private ChipsRecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private ChipsRecyclerViewAdapter unknownStudyLocationsAdapter, unknownAreasLivedInAdapter, unknownHobbiesAdapter, unknownPersonalitiesAdapter;
    private RecyclerView studyLocationsRecyclerView, areasLivedInRecyclerView, hobbiesRecyclerView, personalityTypesRecyclerView;
    private TextView displayNameText, ageText, preferencesText;
    private ImageView profileImage;
    private GridView gridView;
    private Map<String, Bitmap> bitmaps;
    private GridViewAdapter gridViewAdapter;
    private GridViewAdapter unexposedGridViewAdapter;
    private List<String> imagePaths;
    private byte[] profileByteArray;
    private ProgressBar progressBar;
    private RelativeLayout progressCover;
    private OtherUser otherUser;
    private ConnectionItem otherUserConnection;
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

        otherUserConnection = currentUser.getConnection(getIntent().getStringExtra("Uid"));
        if (null != otherUserConnection) {
            otherUser = new OtherUser(otherUserConnection);
        } else {
            otherUser = new OtherUser(getIntent().getStringExtra("Uid"));
        }

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
        progressCover = findViewById(R.id.other_profile_white_cover);
        connectButton = findViewById(R.id.connect_button);
        playButton = findViewById(R.id.play_button);

        progressBar.setVisibility(View.VISIBLE);
        progressCover.setVisibility(View.VISIBLE);

        bitmaps = new HashMap<>();
        imagePaths = new ArrayList<>();
        gridViewAdapter = new GridViewAdapter(getApplicationContext(), bitmaps, imagePaths);
        gridView.setAdapter(gridViewAdapter);

        UserInformationHandler.downloadUserInformation(otherUser,
                new OnCompleteCallback() {
                    @Override
                    public void update(boolean success, String message) {
                        /* Once the user information has downloaded (either success of failure), we can
                           safely start initializing all of the fields */
                        initialiseFields();
                    }
                });

    }

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

    private void initialiseFields() {

        if (currentUser.isConnected(otherUser.getUid())) {
            playButton.setVisibility(View.VISIBLE);
        } else {
            connectButton.setVisibility(View.VISIBLE);
        }

        studyLocationsAdapter = new ChipsRecyclerViewAdapter(this, otherUser.getPlacesStudied(), false);
        unknownStudyLocationsAdapter = new ChipsRecyclerViewAdapter(this,
                initialiseUnexposedArrayList(otherUser.getPlacesStudied().size()),
                false);

        areasLivedInAdapter = new ChipsRecyclerViewAdapter(this, otherUser.getPlacesLived(), false);
        unknownAreasLivedInAdapter = new ChipsRecyclerViewAdapter(this,
                initialiseUnexposedArrayList(otherUser.getPlacesLived().size()),
                false);

        hobbiesAdapter = new ChipsRecyclerViewAdapter(this, otherUser.getHobbies(), false);
        unknownHobbiesAdapter = new ChipsRecyclerViewAdapter(this,
                initialiseUnexposedArrayList(otherUser.getHobbies().size()),
                false);

        personalitiesAdapter = new ChipsRecyclerViewAdapter(this, otherUser.getPersonalities(), false);
        unknownPersonalitiesAdapter = new ChipsRecyclerViewAdapter(this,
                initialiseUnexposedArrayList(otherUser.getPersonalities().size()),
                false);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        /* If we haven't already downloaded the user's images from firebase, do so */
        UserMediaHandler.downloadImagesFromFirebase(otherUser.getUid(), bitmaps, imagePaths, new OnCompleteCallback() {
                @Override
                public void update(boolean success, String message) {
                    if (!otherUser.checkDetailExposed(UserField.GALLERY)) {
                        unexposedGridViewAdapter = initialiseUnexposedGridViewAdapter();
                        gridView.setAdapter(unexposedGridViewAdapter);
                    } else {
                        gridViewAdapter.notifyDataSetChanged();
                    }
                }
            });


        displayNameText.setText(otherUser.getName());

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

        UserMediaHandler.downloadProfilePhotoFromFirebase(otherUser.getUid(), profileByteArray, profileByteArray.length, new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success){
                    profileImage.setImageBitmap(BitmapFactory.decodeByteArray(profileByteArray, 0, profileByteArray.length));
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

                // Set the unexposed items to invisible
                for (UserField field : UserField.values()) {
                    if (!otherUser.checkDetailExposed(field)) {
                        hideInfo(field);
                    }
                }

                progressBar.setVisibility(View.INVISIBLE);
                progressCover.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void onConnectPressed(View view) {
        progressBar.setVisibility(View.VISIBLE);
        currentUser.addConnection(otherUser.getUid());

        UserInformationHandler.uploadUserInformationToFirestore(currentUser, new OnCompleteCallback() {
            public void update(boolean success, String message) {
                if (success) {
                    UserInformationHandler.addOtherUserConnection(otherUser.getUid(), currentUser.getUid(), new OnCompleteCallback() {
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

    public void onPlayPressed(View view) {
        Intent gameIntent = new Intent(this, ThreeTruthsOneLieActivity.class);
        gameIntent.putExtra("current user", currentUser);
        gameIntent.putExtra("other user", otherUser);
        startActivityForResult(gameIntent, RequestCodes.GAME_RESULT);
    }

    private void hideInfo(UserField field) {
        switch (field) {
            case PROFILE_IMAGE:
                profileImage.setImageDrawable(getDrawable(R.drawable.unexposed_image));
                break;
            case NAME:
                displayNameText.setText(otherUser.getNickname());
                break;
            case BIRTHDAY:
                ageText.setText("Age ??");
                break;
            case PREFERENCES:
                preferencesText.setText("Interested in ????");
                break;
            case PLACES_STUDIED:
                studyLocationsRecyclerView.setAdapter(unknownStudyLocationsAdapter);
                break;
            case PLACES_LIVED:
                areasLivedInRecyclerView.setAdapter(unknownAreasLivedInAdapter);
                break;
            case HOBBIES:
                hobbiesRecyclerView.setAdapter(unknownHobbiesAdapter);
                break;
            case PERSONALITIES:
                personalityTypesRecyclerView.setAdapter(unknownPersonalitiesAdapter);
                break;
        }
    }

    private ArrayList<String> initialiseUnexposedArrayList(int length) {
        ArrayList<String> newList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            newList.add("??????");
        }
        return newList;
    }

    private GridViewAdapter initialiseUnexposedGridViewAdapter() {
        Map<String, Bitmap> emptyBitmaps = new HashMap<>();
        for (String path : imagePaths) {
            emptyBitmaps.put(path, null);
        }
        return new GridViewAdapter(getApplicationContext(), emptyBitmaps, imagePaths);
    }
}
