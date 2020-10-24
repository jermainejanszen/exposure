package com.exposure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private static int GALLERY_REQUEST = 101;
    private List<String> studyLocations, areasLivedIn, hobbies, personalities;
    private RecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private ImageView profileImage;
    FirebaseFirestore db;
    private static final String NAME = "Name";
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        uploadNewUserInformation();

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

        studyLocationsAdapter = new RecyclerViewAdapter(this, studyLocations, true);
        areasLivedInAdapter = new RecyclerViewAdapter(this, areasLivedIn, true);
        hobbiesAdapter = new RecyclerViewAdapter(this, hobbies, true);
        personalitiesAdapter = new RecyclerViewAdapter(this, personalities, true);

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

    private void uploadNewUserInformation(){
        Map<String, Object> newUser = new HashMap<>();
        newUser.put(NAME, "John");
        newUser.put(EMAIL, "j@gmail.com");
        db.collection("Users").document("User Information").set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfileActivity.this, "User information uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "ERROR, could not uploaded user information", Toast.LENGTH_SHORT).show();
            }
        });
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