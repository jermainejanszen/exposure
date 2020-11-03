package com.exposure.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.activities.MainActivity;
import com.exposure.adapters.GridViewAdapter;
import com.exposure.adapters.ChipsRecyclerViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.handlers.DateHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.CurrentUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private ChipsRecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private RecyclerView studyLocationsRecyclerView, areasLivedInRecyclerView, hobbiesRecyclerView, personalityTypesRecyclerView;
    private TextView displayNameText, ageText, preferencesText;
    private ImageView profileImage;
    private GridView gridView;
    private Map<String, Bitmap> bitmaps;
    private GridViewAdapter gridViewAdapter;
    private CurrentUser currentUser;
    private List<String> imagePaths;
    private byte[] profileByteArray;
    private ProgressBar progressBar;

    private static Bitmap profileImageBitmap;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = MainActivity.getCurrentUser();
        bitmaps = MainActivity.getBitmaps();
        imagePaths = MainActivity.getImagePaths();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        assert null != getActivity();

        studyLocationsRecyclerView = view.findViewById(R.id.study_locations_recycler_view);
        areasLivedInRecyclerView = view.findViewById(R.id.areas_lived_in_recycler_view);
        hobbiesRecyclerView = view.findViewById(R.id.hobbies_recycler_view);
        personalityTypesRecyclerView = view.findViewById(R.id.personality_types_recycler_view);
        displayNameText = view.findViewById(R.id.display_name);
        ageText = view.findViewById(R.id.age);
        preferencesText = view.findViewById(R.id.preferences);
        profileImage = view.findViewById(R.id.profile_image);
        gridView = view.findViewById(R.id.image_grid_view);
        progressBar = view.findViewById(R.id.progress_bar);

        gridViewAdapter = new GridViewAdapter(getContext(), bitmaps, imagePaths);
        gridView.setAdapter(gridViewAdapter);

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
                                        UserMediaHandler.deleteImageFromFirebase(gridViewAdapter.getItem(position));
                                        bitmaps.remove(gridViewAdapter.getItem(position));
                                        imagePaths.remove(position);
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

        initialiseFields();

        return view;
    }

    public void updateCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
        initialiseFields();
    }


    private void initialiseFields() {
        studyLocationsAdapter = new ChipsRecyclerViewAdapter(getActivity(), currentUser.getPlacesStudied(), false);
        areasLivedInAdapter = new ChipsRecyclerViewAdapter(getActivity(), currentUser.getPlacesLived(), false);
        hobbiesAdapter = new ChipsRecyclerViewAdapter(getActivity(), currentUser.getHobbies(), false);
        personalitiesAdapter = new ChipsRecyclerViewAdapter(getActivity(), currentUser.getPersonalities(), false);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        /* Set the display name to the nickname if it exists, otherwise just use the users name */
        displayNameText.setText(
                currentUser.getNickname() == null ? currentUser.getName() : currentUser.getNickname());

        /* Set the users age if they have entered their birthday */
        if (null != currentUser.getBirthday()) {
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

            preferencesText.setText(preferencesString);
        }

        if (null == profileImageBitmap) {
            profileByteArray = new byte[1024 * 1024];
            UserMediaHandler.downloadProfilePhotoFromFirebase(currentUser.getUid(), profileByteArray, profileByteArray.length, new OnCompleteCallback() {
                @Override
                public void update(boolean success, String message) {
                    if (success) {
                        profileImageBitmap = BitmapFactory.decodeByteArray(profileByteArray, 0, profileByteArray.length);
                        profileImage.setImageBitmap(profileImageBitmap);
                    } else {
                        Toast.makeText(getContext(), "Failed to download profile image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            profileImage.setImageBitmap(profileImageBitmap);
        }
    }

    /* Will need to refactor this */
    public void addBitmap(String id, Bitmap bitmap) {
        bitmaps.put(id, bitmap);
        imagePaths.add(id);
        gridViewAdapter.notifyDataSetChanged();
    }

    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    public static Bitmap getProfileImageBitmap() {
        return profileImageBitmap;
    }

    public static void setProfileImageBitmap(Bitmap bitmap) {
        profileImageBitmap = bitmap;
    }
}