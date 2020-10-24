package com.exposure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.ChipsRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private static int GALLERY_REQUEST = 101;
    private List<String> studyLocations, areasLivedIn, hobbies, personalities;
    private ChipsRecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        studyLocations = new ArrayList<>();
        studyLocations.add("University of Sydney");
        studyLocations.add("Highschool");
        studyLocations.add("Harvard University");

        areasLivedIn = new ArrayList<>();
        areasLivedIn.add("Sydney, Australia");
        areasLivedIn.add("Abu Dhabi, UAE");
        areasLivedIn.add("Melbourne, Australia");

        hobbies = new ArrayList<>();
        hobbies.add("Beach");
        hobbies.add("Partying");
        hobbies.add("Photography");
        hobbies.add("Dancing");
        hobbies.add("Guitar");
        hobbies.add("Piano");

        personalities = new ArrayList<>();
        personalities.add("Introvert");
        personalities.add("Memer");
        personalities.add("Gamer");
        personalities.add("Musician");

        studyLocationsAdapter = new ChipsRecyclerViewAdapter(this, studyLocations, true);
        areasLivedInAdapter = new ChipsRecyclerViewAdapter(this, areasLivedIn, true);
        hobbiesAdapter = new ChipsRecyclerViewAdapter(this, hobbies, true);
        personalitiesAdapter = new ChipsRecyclerViewAdapter(this, personalities, true);

        RecyclerView studyLocationsRecyclerView = findViewById(R.id.study_locations_recycler_view);
        RecyclerView areasLivedInRecyclerView = findViewById(R.id.areas_lived_in_recycler_view);
        RecyclerView hobbiesRecyclerView = findViewById(R.id.hobbies_recycler_view);
        RecyclerView personalityTypesRecyclerView = findViewById(R.id.personality_types_recycler_view);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        final TextView changeProfileImageButton = findViewById(R.id.change_profile_image_button);

        changeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfileImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
                    }
                });
            }
        });

        profileImage = findViewById(R.id.profile_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (GALLERY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                try {
                    profileImage.setImageBitmap(
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onLogoutPress(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}