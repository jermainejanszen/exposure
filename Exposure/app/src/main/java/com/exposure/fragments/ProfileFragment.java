package com.exposure.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.GridViewAdapter;
import com.exposure.adapters.RecyclerViewAdapter;
import com.exposure.dialogs.UploadPhotoDialog;
import com.exposure.handlers.DateHandler;
import com.exposure.user.CurrentUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private RecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private List<Bitmap> bitmaps;
    private GridViewAdapter gridViewAdapter;
    private CurrentUser currentUser;

    public static ProfileFragment newInstance(CurrentUser currentUser) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args =  new Bundle();
        args.putSerializable("current user", currentUser);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = (CurrentUser) getArguments().getSerializable("current user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        assert null != getActivity();

        studyLocationsAdapter = new RecyclerViewAdapter(getActivity(), currentUser.getPlacesStudied(), false);
        areasLivedInAdapter = new RecyclerViewAdapter(getActivity(), currentUser.getPlacesLived(), false);
        hobbiesAdapter = new RecyclerViewAdapter(getActivity(), currentUser.getHobbies(), false);
        personalitiesAdapter = new RecyclerViewAdapter(getActivity(), currentUser.getPersonalities(), false);

        RecyclerView studyLocationsRecyclerView = view.findViewById(R.id.study_locations_recycler_view);
        RecyclerView areasLivedInRecyclerView = view.findViewById(R.id.areas_lived_in_recycler_view);
        RecyclerView hobbiesRecyclerView = view.findViewById(R.id.hobbies_recycler_view);
        RecyclerView personalityTypesRecyclerView = view.findViewById(R.id.personality_types_recycler_view);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        bitmaps = new ArrayList<>();

        gridViewAdapter = new GridViewAdapter(getContext(), bitmaps);
        GridView gridView = view.findViewById(R.id.image_grid_view);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete Image")
                        .setMessage("Are you sure you want to delete this image?")
                        .setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bitmaps.remove(position);
                                        gridViewAdapter.notifyDataSetChanged();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        /* Do nothing */
                                    }
                                })
                        .create()
                        .show();

                return true;
            }
        });

        gridView.setAdapter(gridViewAdapter);

        /* Set the display name to the nickname if it exists, otherwise just use the users name */
        TextView displayNameText = view.findViewById(R.id.display_name);
        displayNameText.setText(
                currentUser.getNickname() == null ? currentUser.getName() : currentUser.getNickname());

        /* Set the users age if they have entered their birthday */
        if (null != currentUser.getBirthday()) {
            TextView ageText = view.findViewById(R.id.age);
            ageText.setText(
                    String.format(Locale.ENGLISH,
                            "Age %d",
                            DateHandler.yearsBetween(currentUser.getBirthday(), new Date())
                    )
            );
        }

        /* Check if the user has entered any preferences */
        List<String> preferences = currentUser.getPreferences();

        if (!preferences.isEmpty()) {
            Collections.sort(preferences);

            String preferencesString = "Interested in " + preferences.get(0);

            for (int i = 1; i < preferences.size(); i++) {
                preferencesString += ", " + preferences.get(i);
            }

            TextView preferencesText = view.findViewById(R.id.preferences);
            preferencesText.setText(preferencesString);
        }

        return view;
    }

    /* Will need to refactor this */
    public void addBitmap(Bitmap bitmap) {
        bitmaps.add(0, bitmap);
        gridViewAdapter.notifyDataSetChanged();
    }
}