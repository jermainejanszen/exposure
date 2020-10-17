package com.exposure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.RecyclerViewAdapter;
import com.exposure.handlers.DateHandler;
import com.exposure.user.CurrentUser;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    private static int GALLERY_REQUEST = 101;
    private RecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private ImageView profileImage;
    private EditText nameEditText, nicknameEditText, emailEditText, phoneEditText, birthdayEditText;
    private CheckBox maleCheckBox, femaleCheckBox, otherCheckBox;
    private CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        currentUser = (CurrentUser) getIntent().getSerializableExtra("current user");

        studyLocationsAdapter = new RecyclerViewAdapter(this, currentUser.getPlacesStudied(), true);
        areasLivedInAdapter = new RecyclerViewAdapter(this, currentUser.getPlacesLived(), true);
        hobbiesAdapter = new RecyclerViewAdapter(this, currentUser.getHobbies(), true);
        personalitiesAdapter = new RecyclerViewAdapter(this, currentUser.getPersonalities(), true);

        RecyclerView studyLocationsRecyclerView = findViewById(R.id.study_locations_recycler_view);
        RecyclerView areasLivedInRecyclerView = findViewById(R.id.areas_lived_in_recycler_view);
        RecyclerView hobbiesRecyclerView = findViewById(R.id.hobbies_recycler_view);
        RecyclerView personalityTypesRecyclerView = findViewById(R.id.personality_types_recycler_view);

        studyLocationsRecyclerView.setAdapter(studyLocationsAdapter);
        areasLivedInRecyclerView.setAdapter(areasLivedInAdapter);
        hobbiesRecyclerView.setAdapter(hobbiesAdapter);
        personalityTypesRecyclerView.setAdapter(personalitiesAdapter);

        profileImage = findViewById(R.id.profile_image);

        initialiseFields();
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

    @Override
    public void onBackPressed() {

        /* Retrieve any edits from edit texts and set the current user data */
        String name = nameEditText.getText().toString();
        String nickname = nicknameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String birthday = birthdayEditText.getText().toString();

        /* Invalid edit checking */
        if (name.isEmpty()) {
            nameEditText.setError("Name can't be empty.");
            nameEditText.requestFocus();
            return;
        } else if (email.isEmpty()) {
            /* Need to check if this email is valid in firebase */
            emailEditText.setError("Email required.");
            emailEditText.requestFocus();
            return;
        } else if (birthday.contains("-")) {
            birthdayEditText.setError("Birthday must be of the format dd/MM/yyyy");
            birthdayEditText.requestFocus();
            return;
        }

        Date birthdayDate;

        /* If birthday isn't formatted correctly, create error message */
        try {
            birthdayDate = DateHandler.convertToDate(birthday);
        } catch (ParseException e) {
            birthdayEditText.setError("Birthday must be of the format dd/MM/yyyy");
            birthdayEditText.requestFocus();
            return;
        }

        /* If the user has specified an invalid age, create error message */
        int age = DateHandler.yearsBetween(birthdayDate, new Date());

        if (age < 18 || age > 120) {
            birthdayEditText.setError(
                    String.format(
                            Locale.ENGLISH,
                            "Age specified is %d but must between 18 and 120",
                            age
                    ));
            birthdayEditText.requestFocus();
            return;
        }

        currentUser.setName(name);
        currentUser.setEmail(email);

        /* If no nickname provided, need to set it to null for data consistency and edit text hints */
        currentUser.setNickname(nickname.isEmpty() ? null : nickname);

        /* If no phone number is provided, need to set it to null for data consistency and edit text hints */
        currentUser.setPhone(phone.isEmpty() ? null : phone);

        List<String> preferences = currentUser.getPreferences();

        /* Set the preferences data */
        if (maleCheckBox.isChecked() && !preferences.contains("Males")) {
            preferences.add("Males");
        } else if (!maleCheckBox.isChecked()){
            preferences.remove("Males");
        }

        if (femaleCheckBox.isChecked() && !preferences.contains("Females")) {
            preferences.add("Females");
        } else if (!femaleCheckBox.isChecked()) {
            preferences.remove("Females");
        }

        if (otherCheckBox.isChecked() && !preferences.contains("Others")) {
            preferences.add("Others");
        } else if (!otherCheckBox.isChecked()) {
            preferences.remove("Others");
        }

        /* Pass serialized user object to the intent data */
        Intent data = new Intent();
        data.putExtra("current user", currentUser);
        setResult(RESULT_OK, data);
        finish();
    }

    private void initialiseFields() {
        nameEditText = findViewById(R.id.name_edit_text);
        nicknameEditText = findViewById(R.id.nickname_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        birthdayEditText = findViewById(R.id.birthday_edit_text);

        maleCheckBox = findViewById(R.id.male_checkbox);
        femaleCheckBox = findViewById(R.id.female_checkbox);
        otherCheckBox = findViewById(R.id.other_checkbox);

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
            maleCheckBox.setChecked(preferences.contains("Males"));
            femaleCheckBox.setChecked(preferences.contains("Females"));
            otherCheckBox.setChecked(preferences.contains("Others"));
        }
    }

    public void onChangeProfileImageClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST);
    }
}