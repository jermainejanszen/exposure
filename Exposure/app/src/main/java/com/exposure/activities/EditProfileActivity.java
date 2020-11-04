package com.exposure.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.constants.RequestCodes;
import com.exposure.fragments.ProfileFragment;
import com.exposure.handlers.DateHandler;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.popups.AddUserFieldActivity;
import com.exposure.popups.RetrieveImageActivity;
import com.exposure.user.CurrentUser;
import com.exposure.user.UserField;
import com.exposure.adapters.ChipsRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity allows user to edit their profile information and photos
 */
public class EditProfileActivity extends AppCompatActivity {
    private ChipsRecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter,
            personalitiesAdapter, truthsAdapter, liesAdapter;
    private ImageView profileImage;
    private EditText nameEditText, nicknameEditText, emailEditText, phoneEditText, birthdayEditText;
    private CheckBox malesCheckBox, femalesCheckBox, othersCheckBox;
    private ProgressBar progressBar;
    private CurrentUser currentUser;
    private Bitmap profileBitmap;
    private LocationManager lm;

    //TODO: add comment in here
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new
                    ActivityResultCallback<Boolean>() {
                @RequiresApi(api = Build.VERSION_CODES.R)
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        setUserLocation();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });

    /**
     * Upon creating the activity, the view is set, user information fields are downloaded and
     * initialised, and the user's profile photos are downloaded from firebase firestore
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        currentUser = (CurrentUser) getIntent().getSerializableExtra("current user");
        initialiseFields();

        profileBitmap = ProfileFragment.getProfileImageBitmap();
        profileImage = findViewById(R.id.profile_image);
        profileImage.setImageBitmap(profileBitmap);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Upon returning from activity, request codes are handled and action is taken accordingly
     * @param requestCode request code signifying what action is required
     * @param resultCode result code indicating the result of the returning activity
     * @param data data returned from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RequestCodes.RETRIEVE_IMAGE_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                String source = data.getStringExtra("Source");
                if ("Image Capture".equals(source)) {
                    profileBitmap = (Bitmap) data.getExtras().get("data");
                    ProfileFragment.setProfileImageBitmap(profileBitmap);
                    profileImage.setImageBitmap(profileBitmap);
                } else if ("Library".equals(source)) {
                    try {
                        profileBitmap = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(), data.getData());
                        ProfileFragment.setProfileImageBitmap(profileBitmap);
                        profileImage.setImageBitmap(profileBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /* Upon changing the user's places lived, updates user's profile with the new details */
        if (RequestCodes.SAVE_PLACE_LIVED_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getPlacesLived().add(data.getStringExtra("New Field"));
                areasLivedInAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's places studied, updates user's profile with the new details */
        if (RequestCodes.SAVE_PLACE_STUDIED_AT_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getPlacesStudied().add(data.getStringExtra("New Field"));
                studyLocationsAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's hobbies, updates user's profile with the new details */
        if (RequestCodes.SAVE_HOBBY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getHobbies().add(data.getStringExtra("New Field"));
                hobbiesAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's personality traits, updates user's profile with new details */
        if (RequestCodes.SAVE_PERSONALITY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getPersonalities().add(data.getStringExtra("New Field"));
                personalitiesAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's truths, updates user's profile with the new details */
        if (RequestCodes.SAVE_TRUTH_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getTruths().add(data.getStringExtra("New Field"));
                truthsAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's lies, updates user's profile with the new details */
        if (RequestCodes.SAVE_LIE_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getLies().add(data.getStringExtra("New Field"));
                liesAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * The field's of the user's profile are initialised and set as visible on the profile screen
     */
    private void initialiseFields() {

        /* Creates new recycler views for user profile fields */
        studyLocationsAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getPlacesStudied(), true);
        areasLivedInAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getPlacesLived(), true);
        hobbiesAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getHobbies(), true);
        personalitiesAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getPersonalities(), true);
        truthsAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getTruths(), true);
        liesAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getLies(), true);

        /* Sets elements of the view to each recycler view */
        RecyclerView studyLocationsRecyclerView = findViewById(R.id.study_locations_recycler_view);
        RecyclerView areasLivedInRecyclerView = findViewById(R.id.areas_lived_in_recycler_view);
        RecyclerView hobbiesRecyclerView = findViewById(R.id.hobbies_recycler_view);
        RecyclerView truthsRecyclerView = findViewById(R.id.truths_recycler_view);
        RecyclerView liesRecyclerView = findViewById(R.id.lies_recycler_view);
        RecyclerView personalityTypesRecyclerView =
                findViewById(R.id.personality_types_recycler_view);

        /* Sets the adapter for each recycler view */
        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        truthsRecyclerView.setAdapter(truthsAdapter);
        liesRecyclerView.setAdapter(liesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        /* Sets elements of the view for all remaining fields of the user profile */
        profileImage = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.name_edit_text);
        nicknameEditText = findViewById(R.id.nickname_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        birthdayEditText = findViewById(R.id.birthday_edit_text);
        malesCheckBox = findViewById(R.id.male_checkbox);
        femalesCheckBox = findViewById(R.id.female_checkbox);
        othersCheckBox = findViewById(R.id.other_checkbox);
        progressBar = findViewById(R.id.progress_bar);

        /* Sets the text shown on the profile page for each field according to the information
        stored in the current user object */
        if (null != currentUser.getName()) {
            nameEditText.setText(currentUser.getName());
        }

        if (null != currentUser.getNickname()) {
            nicknameEditText.setText(currentUser.getNickname());
        }

        if (null != currentUser.getEmail()) {
            emailEditText.setText(currentUser.getEmail());
        }

        if (null != currentUser.getPhone()) {
            phoneEditText.setText(currentUser.getPhone());
        }

        if (null != currentUser.getBirthday()) {
            birthdayEditText.setText(DateHandler.convertToString(currentUser.getBirthday()));
        }

        if (null != currentUser.getPreferences()) {
            List<String> preferences = currentUser.getPreferences();
            malesCheckBox.setChecked(preferences.contains("Males"));
            femalesCheckBox.setChecked(preferences.contains("Females"));
            othersCheckBox.setChecked(preferences.contains("Others"));
        }

        /* If the current user object is in an invalid state, hide the cancel button */
        if (!currentUser.validState()) {
            TextView cancel = findViewById(R.id.cancel);
            cancel.setVisibility(View.GONE);
        }
    }

    /**
     * Upon clicking to add a hobby to their profile, starts an intent to let user input new hobby
     * @param view the current GUI view
     */
    public void addHobby(View view) {
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.HOBBIES.toString());
        startActivityForResult(intent, RequestCodes.SAVE_HOBBY_REQUEST);
    }

    /**
     * Upon clicking to add a place they have studied to their profile, starts an intent to let
     * user input new place studied
     * @param view the current GUI view
     */
    public void addPlaceStudiedAt(View view) {
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.PLACES_STUDIED.toString());
        startActivityForResult(intent, RequestCodes.SAVE_PLACE_STUDIED_AT_REQUEST);
    }

    /**
     * Upon clicking to add a place they have lived to their profile, starts an intent to let
     * user input new place lived
     * @param view the current GUI view
     */
    public void addPlaceLived(View view) {
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.PLACES_LIVED.toString());
        startActivityForResult(intent, RequestCodes.SAVE_PLACE_LIVED_REQUEST);
    }

    /**
     * Upon clicking to add a personality trait, starts an intent to let user input new personality
     * trait
     * @param view the current GUI view
     */
    public void addPersonalityTrait(View view) {
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.PERSONALITIES.toString());
        startActivityForResult(intent, RequestCodes.SAVE_PERSONALITY_REQUEST);
    }

    /**
     * Upon clicking to add a truth, starts an intent to let user input a truth about themselves
     * @param view the current GUI view
     */
    public void addTruth(View view){
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.TRUTHS.toString());
        startActivityForResult(intent, RequestCodes.SAVE_TRUTH_REQUEST);
    }

    /**
     * Upon clicking to add a lie, starts an intent to let user input a lie about themselves
     * @param view the current GUI view.
     */
    public void addLie(View view){
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.LIES.toString());
        startActivityForResult(intent, RequestCodes.SAVE_LIE_REQUEST);
    }

    /**
     * Upon clicking to change their profile picture, starts an intent to allow user to take a new
     * profile picture or upload a new profile picture
     * @param view the current GUI view
     */
    public void onChangeProfileImageClick(View view) {
        Intent intent = new Intent(this, RetrieveImageActivity.class);
        startActivityForResult(intent, RequestCodes.RETRIEVE_IMAGE_REQUEST);
    }

    //TODO: check this java doc
    /**
     * Upon clicking to update their location, accesses the users location and updates it on the
     * interface
     * @param view the current GUI view
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onUpdateLocationClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            setUserLocation();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Sets the user's location based on GPS
     * TODO needs better commenting here
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void setUserLocation() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,
                500.0f, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (0 != location.getLatitude() && 0 != location.getLongitude()) {
                    currentUser.setLocation(location.getLatitude(), location.getLongitude());
                    Toast.makeText(getApplicationContext(), "Successfully updated location.",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Upon clicking save after editing one's profile, retrieves the information entered by the
     * user, checks mandatory fields have been filled out, ensures information entries are valid,
     * updates the current user object and uploads the profile information to the firebase firestore
     * @param view the current GUI view
     */
    public void onSaveClick(View view) {

        /* The user must upload a profile picture */
        if (null == profileBitmap) {
            Toast.makeText(this, "You must upload a profile picture",
                    Toast.LENGTH_LONG).show();
            return;
        }

        /* Retrieve any edits from edit texts and set the current user data */
        String name = nameEditText.getText().toString();
        String nickname = nicknameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String birthday = birthdayEditText.getText().toString();

        /* Invalid edit checking */
        if (name.isEmpty()) {
            Toast.makeText(this, "Name required", Toast.LENGTH_LONG).show();
            nameEditText.requestFocus();
            return;
        } else if (nickname.isEmpty()) {
            /* Need to check if this email is valid in firebase */
            Toast.makeText(this, "Nickname required", Toast.LENGTH_LONG).show();
            nicknameEditText.requestFocus();
            return;
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Email required", Toast.LENGTH_LONG).show();
            emailEditText.requestFocus();
            return;
        } else if (birthday.contains("-")) {
            Toast.makeText(this, "Birthday must be of the format dd/MM/yyyy",
                    Toast.LENGTH_LONG).show();
            birthdayEditText.requestFocus();
            return;
        }

        Date birthdayDate;

        /* If birthday isn't formatted correctly, create error message */
        try {
            birthdayDate = DateHandler.convertToDate(birthday);
        } catch (ParseException e) {
            Toast.makeText(this, "Birthday must be of the format dd/MM/yyyy",
                    Toast.LENGTH_LONG).show();
            birthdayEditText.requestFocus();
            return;
        }

        /* If the user has specified an invalid age, create error message */
        int age = DateHandler.yearsBetween(birthdayDate, new Date());

        if (age < 18 || age > 120) {
            String ageErrorString =
                    String.format(
                            Locale.ENGLISH,
                            "Age specified is %d but must between 18 and 120",
                            age
                    );
            Toast.makeText(this, ageErrorString, Toast.LENGTH_LONG).show();
            birthdayEditText.requestFocus();
            return;
        }

        /* If the user hasn't selected a preference, create error message */
        if (!malesCheckBox.isChecked() && !femalesCheckBox.isChecked() &&
                !othersCheckBox.isChecked()) {
            Toast.makeText(this, "You must select at least one preference",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (currentUser.getLocation().size() != 2) {
            Toast.makeText(this, "You must update your current location",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (currentUser.getTruths().size() < 3) {
            Toast.makeText(this, "You must enter at least three truths about " +
                    "yourself", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentUser.getLies().size() < 1) {
            Toast.makeText(this, "You must enter at least one lie about yourself",
                    Toast.LENGTH_LONG).show();
            return;
        }

        /* Error checking has been performed so we can now manipulate the data */
        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setBirthday(birthdayDate);

        /* If no nickname provided, set it to null for data consistency and edit text hints */
        currentUser.setNickname(nickname.isEmpty() ? null : nickname);

        /* If no phone number is provided, set to null for data consistency and edit text hints */
        currentUser.setPhone(phone.isEmpty() ? null : phone);

        List<String> preferences = currentUser.getPreferences();

        if (malesCheckBox.isChecked() && !preferences.contains("Males")) {
            preferences.add("Males");
        } else if (!malesCheckBox.isChecked()){
            preferences.remove("Males");
        }

        if (femalesCheckBox.isChecked() && !preferences.contains("Females")) {
            preferences.add("Females");
        } else if (!femalesCheckBox.isChecked()) {
            preferences.remove("Females");
        }

        if (othersCheckBox.isChecked() && !preferences.contains("Others")) {
            preferences.add("Others");
        } else if (!othersCheckBox.isChecked()) {
            preferences.remove("Others");
        }

        /* Pass serialized user object to the intent data */
        Intent data = new Intent();
        data.putExtra("current user", currentUser);
        setResult(RESULT_OK, data);

        /* Progress bar to indicate we are performing a database operation */
        progressBar.setVisibility(View.VISIBLE);

        /* Logic for finishing activity inside upload user information to firestore method */
        UserInformationHandler.uploadUserInformationToFirestore(currentUser,
                new OnCompleteCallback() {
                    @Override
                    public void update(boolean success, String message) {
                        if (success) {
                            /* Uploaded user data successfully. Now, upload profile picture if
                            one has been specified */
                            if (null != profileBitmap) {
                                UserMediaHandler.uploadProfilePhotoToFirebase(profileBitmap, new
                                        OnCompleteCallback() {
                                    @Override
                                    public void update(boolean success, String message) {
                                        /* Finish activity regardless of success */
                                        finish();
                                    }
                                });
                            } else {
                                finish();
                            }
                        } else {
                            /* Couldn't download user data, so don't finish the activity */
                            Toast.makeText(getApplicationContext(), message,
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    /**
     * Upon clicking to cancel profile edits, finishes the activity and does not update the current
     * user object or firebase firestore profile information
     * @param view the current GUI view
     */
    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    /**
     * On clicking to log out of account, signs the user out and finishes the activity
     * @param view the current GUI view
     */
    public void onLogoutPress(View view) {
        FirebaseAuth.getInstance().signOut();
        ProfileFragment.setProfileImageBitmap(null);
        finish();
    }
}