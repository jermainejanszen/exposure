package com.exposure.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private List<String> studyLocations, areasLivedIn, hobbies, personalityTypes;
    private RecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalityTypesAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        assert null != getActivity();

        /* Placeholder values for the profile page */
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

        studyLocationsAdapter = new RecyclerViewAdapter(getActivity(), studyLocations);
        areasLivedInAdapter = new RecyclerViewAdapter(getActivity(), areasLivedIn);
        hobbiesAdapter = new RecyclerViewAdapter(getActivity(), areasLivedIn);
        personalityTypesAdapter = new RecyclerViewAdapter(getActivity(), personalityTypes);

        RecyclerView studyLocationsRecyclerView = view.findViewById(R.id.study_locations_recycler_view);
        RecyclerView areasLivedInRecyclerView = view.findViewById(R.id.areas_lived_in_recycler_view);
        RecyclerView hobbiesRecyclerView = view.findViewById(R.id.hobbies_recycler_view);
        RecyclerView personalityTypesRecyclerView = view.findViewById(R.id.personality_types_recycler_view);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalityTypesAdapter);

        return view;
    }
}