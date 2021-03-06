package com.exposure.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private OnCompleteCallback locationChangeCallback;
    private OnCompleteCallback truthAddedCallback;
    private OnCompleteCallback lieAddedCallback;
    private OnCompleteCallback profileImageAddedCallback;

    /* Result launcher to request user permission to read current location */
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

        profileBitmap = ProfileFragment.getProfileImageBitmap();
        profileImage = findViewById(R.id.profile_image);

        if (null != profileBitmap) {
            profileImage.setImageBitmap(profileBitmap);
        }

        initialiseFields();

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
                if (null == data) return;
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
                profileImageAddedCallback.update(true, "Added profile image");
            }
        }

        /* Upon changing the user's places lived, updates user's profile with the new details */
        if (RequestCodes.SAVE_PLACE_LIVED_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                if (null == data) return;
                currentUser.getPlacesLived().add(data.getStringExtra("New Field"));
                areasLivedInAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's places studied, updates user's profile with the new details */
        if (RequestCodes.SAVE_PLACE_STUDIED_AT_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                if (null == data) return;
                currentUser.getPlacesStudied().add(data.getStringExtra("New Field"));
                studyLocationsAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's hobbies, updates user's profile with the new details */
        if (RequestCodes.SAVE_HOBBY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                if (null == data) return;
                currentUser.getHobbies().add(data.getStringExtra("New Field"));
                hobbiesAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's personality traits, updates user's profile with new details */
        if (RequestCodes.SAVE_PERSONALITY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                if (null == data) return;
                currentUser.getPersonalities().add(data.getStringExtra("New Field"));
                personalitiesAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's truths, updates user's profile with the new details */
        if (RequestCodes.SAVE_TRUTH_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                if (null == data) return;
                currentUser.getTruths().add(data.getStringExtra("New Field"));
                truthAddedCallback.update(true, "Added truth");
                truthsAdapter.notifyDataSetChanged();
            }
        }
        /* Upon changing the user's lies, updates user's profile with the new details */
        if (RequestCodes.SAVE_LIE_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                if (null == data) return;
                currentUser.getLies().add(data.getStringExtra("New Field"));
                lieAddedCallback.update(true, "Added lie");
                liesAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * The field's of the user's profile are initialised and set as visible on the profile screen
     */
    private void initialiseFields() {

        /* Set field labels for required fields */
        final TextView nameLabel = findViewById(R.id.name_text);
        final TextView nicknameLabel = findViewById(R.id.nickname_text);
        final TextView emailLabel = findViewById(R.id.email_text);
        final TextView birthdayLabel = findViewById(R.id.birthday_text);
        final TextView preferencesLabel = findViewById(R.id.preferences_text);
        final TextView locationLabel = findViewById(R.id.location_subsection_text);
        final TextView truthsLabel = findViewById(R.id.truths_text);
        final TextView liesLabel = findViewById(R.id.lies_text);
        final TextView profileImageLabel = findViewById(R.id.change_profile_image_button);

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

        /* Creates new recycler views for user profile fields */
        OnCompleteCallback onDeleteCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    if (checkTruthsValid()) {
                        truthsLabel.setTextColor(getColor(R.color.Text));
                    } else {
                        truthsLabel.setTextColor(getColor(R.color.Red));
                    }

                    if (checkLiesValid()) {
                        liesLabel.setTextColor(getColor(R.color.Text));
                    } else {
                        liesLabel.setTextColor(getColor(R.color.Red));
                    }
                }
            }
        };
        studyLocationsAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getPlacesStudied(), true, onDeleteCallback);
        areasLivedInAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getPlacesLived(), true, onDeleteCallback);
        hobbiesAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getHobbies(), true, onDeleteCallback);
        personalitiesAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getPersonalities(), true, onDeleteCallback);
        truthsAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getTruths(), true, onDeleteCallback);
        liesAdapter = new ChipsRecyclerViewAdapter(this,
                currentUser.getLies(), true, onDeleteCallback);

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

        /* Add listeners to required fields */
        if (!checkNameValid(nameEditText.getText().toString())) {
            nameLabel.setTextColor(getColor(R.color.Red));
        }
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (checkNameValid(charSequence.toString())) {
                    nameLabel.setTextColor(getColor(R.color.Text));
                } else {
                    nameLabel.setTextColor(getColor(R.color.Red));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (!checkNicknameValid(nicknameEditText.getText().toString())) {
            nicknameLabel.setTextColor(getColor(R.color.Red));
        }
        nicknameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (checkNicknameValid(charSequence.toString())) {
                    nicknameLabel.setTextColor(getColor(R.color.Text));
                } else {
                    nicknameLabel.setTextColor(getColor(R.color.Red));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (!checkEmailValid(emailEditText.getText().toString())) {
            emailLabel.setTextColor(getColor(R.color.Red));
        }
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (checkEmailValid(charSequence.toString())) {
                    emailLabel.setTextColor(getColor(R.color.Text));
                } else {
                    emailLabel.setTextColor(getColor(R.color.Red));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (!checkBirthdayValid(birthdayEditText.getText().toString())) {
            birthdayLabel.setTextColor(getColor(R.color.Red));
        }
        birthdayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (checkBirthdayValid(charSequence.toString())) {
                    birthdayLabel.setTextColor(getColor(R.color.Text));
                } else {
                    birthdayLabel.setTextColor(getColor(R.color.Red));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (!checkPreferencesValid()) {
            preferencesLabel.setTextColor(getColor(R.color.Red));
        }
        CompoundButton.OnCheckedChangeListener preferencesChangeListener =
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkPreferencesValid()) {
                    preferencesLabel.setTextColor(getColor(R.color.Text));
                } else {
                    preferencesLabel.setTextColor(getColor(R.color.Red));
                }
            }
        };
        malesCheckBox.setOnCheckedChangeListener(preferencesChangeListener);
        femalesCheckBox.setOnCheckedChangeListener(preferencesChangeListener);
        othersCheckBox.setOnCheckedChangeListener(preferencesChangeListener);

        if (!checkLocationValid()) {
            locationLabel.setTextColor(getColor(R.color.Red));
        }
        locationChangeCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    if (checkLocationValid()) {
                        locationLabel.setTextColor(getColor(R.color.Text));
                    } else {
                        locationLabel.setTextColor(getColor(R.color.Red));
                    }
                }
            }
        };

        if (!checkTruthsValid()) {
            truthsLabel.setTextColor(getColor(R.color.Red));
        }
        truthAddedCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    if (checkTruthsValid()) {
                        truthsLabel.setTextColor(getColor(R.color.Text));
                    } else {
                        truthsLabel.setTextColor(getColor(R.color.Red));
                    }
                }
            }
        };

        if (!checkLiesValid()) {
            liesLabel.setTextColor(getColor(R.color.Red));
        }
        lieAddedCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    if (checkLiesValid()) {
                        liesLabel.setTextColor(getColor(R.color.Text));
                    } else {
                        liesLabel.setTextColor(getColor(R.color.Red));
                    }
                }
            }
        };

        if (!checkProfileImageValid()) {
            profileImageLabel.setTextColor(getColor(R.color.Red));
            profileImageLabel.setText(getText(R.string.add_profile_picture));
        }
        profileImageAddedCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    if (checkProfileImageValid()) {
                        profileImageLabel.setTextColor(getColor(R.color.Primary400));
                        profileImageLabel.setText(getText(R.string.change_profile_picture));
                    } else {
                        profileImageLabel.setTextColor(getColor(R.color.Red));
                        profileImageLabel.setText(getText(R.string.add_profile_picture));
                    }
                }
            }
        };

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

    /**
     * Upon clicking to update their location, accesses the users location and updates it on the
     * interface. If the user hasn't granted access for location, request it.
     * @param view the current GUI view
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onUpdateLocationClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            /* Permission is granted to read location */
            setUserLocation();
        } else {
            /* Request user permission for fine location access */
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Sets the user's location using the devices recorded location through GPS
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void setUserLocation() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,
                500.0f, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (0 != location.getLatitude() && 0 != location.getLongitude()) {
                    currentUser.setLocation(location.getLatitude(), location.getLongitude());
                    locationChangeCallback.update(true, "Updated location");
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    locationChangeCallback.update(false, "Failed to get location");
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
        if (!checkProfileImageValid()) {
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
        if (!checkNameValid(name)) {
            Toast.makeText(this, "Name required", Toast.LENGTH_LONG).show();
            nameEditText.requestFocus();
            return;
        } else if (!checkNicknameValid(nickname)) {
            /* Need to check if this email is valid in firebase */
            Toast.makeText(this, "Nickname required", Toast.LENGTH_LONG).show();
            nicknameEditText.requestFocus();
            return;
        } else if (!checkEmailValid(email)) {
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
        if (!checkPreferencesValid()) {
            Toast.makeText(this, "You must select at least one preference",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkLocationValid()) {
            Toast.makeText(this, "You must update your current location",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkTruthsValid()) {
            Toast.makeText(this, "You must enter at least three truths about " +
                    "yourself", Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkLiesValid()) {
            Toast.makeText(this, "You must enter at least one lie about yourself",
                    Toast.LENGTH_LONG).show();
            return;
        }

        /* Error checking has been performed so we can now manipulate the data */
        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setBirthday(birthdayDate);

        /* If no nickname provided, set it to null for data consistency and edit text hints */
        currentUser.setNickname(nickname.trim().isEmpty() ? null : nickname);

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
     * Checks whether the user has a profile image uploaded
     * @return true if the user has a profile image
     */
    private boolean checkProfileImageValid() {
        return null != profileBitmap;
    }

    /**
     * Checks if the given name is valid
     * @param name the name to check
     * @return true if the name is valid
     */
    private boolean checkNameValid(String name) {
        return !name.isEmpty();
    }

    /**
     * Checks if the given nickname is valid
     * @param nickname the nickname to check
     * @return true if the name is valid
     */
    private boolean checkNicknameValid(String nickname) {
        return !nickname.isEmpty();
    }

    /**
     * Checks if the given email is valid
     * @param email the email to check
     * @return true if the email is valid
     */
    private boolean checkEmailValid(String email) {
        Pattern emailRegex =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailRegex.matcher(email);
        return !email.isEmpty() && matcher.find();
    }

    /**
     * Checks if the given birthday is valid
     * @param birthday the birthday to check
     * @return true if the birthday is valid
     */
    private boolean checkBirthdayValid(String birthday) {
        if (birthday.contains("-")) {
            return false;
        }

        Date birthdayDate;

        /* If birthday isn't formatted correctly, create error message */
        try {
            birthdayDate = DateHandler.convertToDate(birthday);
        } catch (ParseException e) {
            return false;
        }

        /* If the user has specified an invalid age, create error message */
        int age = DateHandler.yearsBetween(birthdayDate, new Date());
        if (age < 18 || age > 120) {
            return false;
        }

        return !birthday.isEmpty();
    }

    /**
     * Checks if the preferences entered are valid
     * @return true if the preferences are valid
     */
    private boolean checkPreferencesValid() {
        return !(!malesCheckBox.isChecked() && !femalesCheckBox.isChecked() &&
                !othersCheckBox.isChecked());
    }

    /**
     * Checks if the users location is valid
     * @return true if the location is valid
     */
    private boolean checkLocationValid() {
        return currentUser.getLocation().size() == 2;
    }

    /**
     * Checks if the truths list is valid
     * @return true if the truth list is valid
     */
    private boolean checkTruthsValid() {
        return currentUser.getTruths().size() >= 3;
    }

    /**
     * Checks if the lies list is valid
     * @return true if the lie list is valid
     */
    private boolean checkLiesValid() {
        return currentUser.getLies().size() >= 1;
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