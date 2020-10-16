package com.exposure.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private List<String> studyLocations, areasLivedIn, hobbies, personalityTypes;
    private RecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalityTypesAdapter;

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

        personalityTypes = new ArrayList<>();
        personalityTypes.add("Introvert");
        personalityTypes.add("Memer");
        personalityTypes.add("Gamer");
        personalityTypes.add("Musician");

        studyLocationsAdapter = new RecyclerViewAdapter(this, studyLocations);
        areasLivedInAdapter = new RecyclerViewAdapter(this, areasLivedIn);
        hobbiesAdapter = new RecyclerViewAdapter(this, hobbies);
        personalityTypesAdapter = new RecyclerViewAdapter(this, personalityTypes);

        RecyclerView studyLocationsRecyclerView = findViewById(R.id.study_locations_recycler_view);
        RecyclerView areasLivedInRecyclerView = findViewById(R.id.areas_lived_in_recycler_view);
        RecyclerView hobbiesRecyclerView = findViewById(R.id.hobbies_recycler_view);
        RecyclerView personalityTypesRecyclerView = findViewById(R.id.personality_types_recycler_view);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalityTypesAdapter);
    }
}