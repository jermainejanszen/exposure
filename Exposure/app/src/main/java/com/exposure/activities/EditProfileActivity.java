package com.exposure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.constants.RequestCodes;
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

public class EditProfileActivity extends AppCompatActivity {
    private ChipsRecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private ImageView profileImage;
    private EditText nameEditText, nicknameEditText, emailEditText, phoneEditText, birthdayEditText;
    private CheckBox malesCheckBox, femalesCheckBox, othersCheckBox;
    private ProgressBar progressBar;
    private CurrentUser currentUser;
    private Bitmap profileBitmap;
    private byte[] profileByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        currentUser = (CurrentUser) getIntent().getSerializableExtra("current user");
        initialiseFields();
        profileByteArray = new byte[1024*1024];
        profileImage = findViewById(R.id.profile_image);

        UserMediaHandler.downloadProfilePhotoFromFirebase(profileByteArray, profileByteArray.length, new OnCompleteCallback() {
            @Override
            public void update(boolean success) {
                if (success){
                    profileImage.setImageBitmap(BitmapFactory.decodeByteArray(profileByteArray, 0, profileByteArray.length));
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RequestCodes.RETRIEVE_IMAGE_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                String source = data.getStringExtra("Source");
                if ("Image Capture".equals(source)) {
                    profileImage.setImageBitmap((Bitmap) data.getExtras().get("data"));
                } else if ("Library".equals(source)) {
                    try {
                        profileImage.setImageBitmap(
                                MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (RequestCodes.SAVE_PLACE_LIVED_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getPlacesLived().add(data.getStringExtra("New Field"));
                areasLivedInAdapter.notifyDataSetChanged();
            }
        }

        if (RequestCodes.SAVE_PLACE_STUDIED_AT_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getPlacesStudied().add(data.getStringExtra("New Field"));
                studyLocationsAdapter.notifyDataSetChanged();
            }
        }

        if (RequestCodes.SAVE_HOBBY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getHobbies().add(data.getStringExtra("New Field"));
                hobbiesAdapter.notifyDataSetChanged();
            }
        }

        if (RequestCodes.SAVE_PERSONALITY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                currentUser.getPersonalities().add(data.getStringExtra("New Field"));
                personalitiesAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initialiseFields() {
        studyLocationsAdapter = new ChipsRecyclerViewAdapter(this, currentUser.getPlacesStudied(), true);
        areasLivedInAdapter = new ChipsRecyclerViewAdapter(this, currentUser.getPlacesLived(), true);
        hobbiesAdapter = new ChipsRecyclerViewAdapter(this, currentUser.getHobbies(), true);
        personalitiesAdapter = new ChipsRecyclerViewAdapter(this, currentUser.getPersonalities(), true);

        RecyclerView studyLocationsRecyclerView = findViewById(R.id.study_locations_recycler_view);
        RecyclerView areasLivedInRecyclerView = findViewById(R.id.areas_lived_in_recycler_view);
        RecyclerView hobbiesRecyclerView = findViewById(R.id.hobbies_recycler_view);
        RecyclerView personalityTypesRecyclerView = findViewById(R.id.personality_types_recycler_view);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

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

    public void addHobby(View view){
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.HOBBIES.toString());
        startActivityForResult(intent, RequestCodes.SAVE_HOBBY_REQUEST);
    }

    public void addPlaceStudiedAt(View view){
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.PLACES_STUDIED.toString());
        startActivityForResult(intent, RequestCodes.SAVE_PLACE_STUDIED_AT_REQUEST);
    }

    public void addPlaceLived(View view){
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.PLACES_LIVED.toString());
        startActivityForResult(intent, RequestCodes.SAVE_PLACE_LIVED_REQUEST);
    }

    public void addPersonalityTrait(View view){
        Intent intent = new Intent(this, AddUserFieldActivity.class);
        intent.putExtra("Field Type", UserField.PERSONALITIES.toString());
        startActivityForResult(intent, RequestCodes.SAVE_PERSONALITY_REQUEST);
    }

    public void onChangeProfileImageClick(View view) {
        Intent intent = new Intent(this, RetrieveImageActivity.class);
        startActivityForResult(intent, RequestCodes.RETRIEVE_IMAGE_REQUEST);
    }

    public void onSaveClick(View view) {

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
        } else if (email.isEmpty()) {
            /* Need to check if this email is valid in firebase */
            Toast.makeText(this, "Email required", Toast.LENGTH_LONG).show();
            emailEditText.requestFocus();
            return;
        } else if (birthday.contains("-")) {
            Toast.makeText(this, "Birthday must be of the format dd/MM/yyyy", Toast.LENGTH_LONG).show();
            birthdayEditText.requestFocus();
            return;
        }

        Date birthdayDate;

        /* If birthday isn't formatted correctly, create error message */
        try {
            birthdayDate = DateHandler.convertToDate(birthday);
        } catch (ParseException e) {
            Toast.makeText(this, "Birthday must be of the format dd/MM/yyyy", Toast.LENGTH_LONG).show();
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
        if (!malesCheckBox.isChecked() && !femalesCheckBox.isChecked() && !othersCheckBox.isChecked()) {
            Toast.makeText(this, "You must select at least one preference", Toast.LENGTH_LONG).show();
            return;
        }

        /* Error checking has been performed so we can now manipulate the data */
        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setBirthday(birthdayDate);

        /* If no nickname provided, need to set it to null for data consistency and edit text hints */
        currentUser.setNickname(nickname.isEmpty() ? null : nickname);

        /* If no phone number is provided, need to set it to null for data consistency and edit text hints */
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
                    public void update(boolean success) {
                        if (success) {
                            /* Uploaded user data successfully. Now, upload profile picture if one has been specified */
                            if (null != profileBitmap) {
                                UserMediaHandler.uploadProfilePhotoToFirebase(profileBitmap, new OnCompleteCallback() {
                                    @Override
                                    public void update(boolean success) {
                                        /* Finish activity regardless of success */
                                        finish();
                                    }
                                });
                            } else {
                                finish();
                            }
                        } else {
                            /* Couldn't download user data, so don't finish the activity */
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    public void onLogoutPress(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}