package com.exposure.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.activities.EditProfileActivity;
import com.exposure.adapters.GridViewAdapter;
import com.exposure.adapters.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static int CAMERA_REQUEST = 101;
    public static int GALLERY_REQUEST = 102;

    private List<String> studyLocations, areasLivedIn, hobbies, personalities;
    private RecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private Button editProfileButton;
    private ImageButton addImageButton, galleryButton;
    private List<Bitmap> bitmaps;
    private GridViewAdapter gridViewAdapter;

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

        personalities = new ArrayList<>();
        personalities.add("Introvert");
        personalities.add("Memer");
        personalities.add("Gamer");
        personalities.add("Musician");

        studyLocationsAdapter = new RecyclerViewAdapter(getActivity(), studyLocations, false);
        areasLivedInAdapter = new RecyclerViewAdapter(getActivity(), areasLivedIn, false);
        hobbiesAdapter = new RecyclerViewAdapter(getActivity(), hobbies, false);
        personalitiesAdapter = new RecyclerViewAdapter(getActivity(), personalities, false);

        RecyclerView studyLocationsRecyclerView = view.findViewById(R.id.study_locations_recycler_view);
        RecyclerView areasLivedInRecyclerView = view.findViewById(R.id.areas_lived_in_recycler_view);
        RecyclerView hobbiesRecyclerView = view.findViewById(R.id.hobbies_recycler_view);
        RecyclerView personalityTypesRecyclerView = view.findViewById(R.id.personality_types_recycler_view);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        editProfileButton = view.findViewById(R.id.edit_profile_button);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), EditProfileActivity.class), 0);
            }
        });

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

        addImageButton = view.findViewById(R.id.add_image_button);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        galleryButton = view.findViewById(R.id.gallery_button);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
            }
        });

        return view;
    }

    /* Will need to refactor this */
    public void addBitmap(Bitmap bitmap) {
        bitmaps.add(0, bitmap);
        gridViewAdapter.notifyDataSetChanged();
    }
}