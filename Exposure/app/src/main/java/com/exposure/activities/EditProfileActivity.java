package com.exposure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.RecyclerViewAdapter;
import com.exposure.constants.RequestCodes;
import com.exposure.dialogs.AddInformationDialog;
import com.exposure.dialogs.UploadPhotoDialog;
import com.exposure.handlers.DateHandler;
import com.exposure.user.CurrentUser;
import com.exposure.user.UserField;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private RecyclerViewAdapter studyLocationsAdapter, areasLivedInAdapter, hobbiesAdapter, personalitiesAdapter;
    private ImageView profileImage;
    private EditText nameEditText, nicknameEditText, emailEditText, phoneEditText, birthdayEditText;
    private CheckBox malesCheckBox, femalesCheckBox, othersCheckBox;
    private CurrentUser currentUser;
    private UploadPhotoDialog uploadPhotoDialog;
    private AddInformationDialog addInformationDialog;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
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

        uploadPhotoDialog = new UploadPhotoDialog(this);
        addInformationDialog = new AddInformationDialog(this);

    }

    private void uploadUserInformationToFirestore(){
        userID = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put(UserField.NAME.toString(), currentUser.getName());
        user.put(UserField.EMAIL.toString(), currentUser.getEmail());
        user.put(UserField.NICKNAME.toString(), currentUser.getNickname());
        user.put(UserField.BIRTHDAY.toString(), currentUser.getBirthday());
        user.put(UserField.PHONE.toString(), currentUser.getPhone());
        user.put(UserField.PLACES_LIVED.toString(), currentUser.getPlacesLived());
        user.put(UserField.PLACES_STUDIED.toString(), currentUser.getPlacesStudied());
        user.put(UserField.HOBBIES.toString(), currentUser.getHobbies());

        firebaseFirestore.collection("Profiles").document(userID).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfileActivity.this, "Successful upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Failed to upload user information" + e, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Taken a new image */
        if (RequestCodes.TAKE_PHOTO_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                profileImage.setImageBitmap((Bitmap) data.getExtras().get("data"));
            }
        }

        if (RequestCodes.SAVE_FIELD_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                String fieldType = data.getStringExtra("Field Type");
                String fieldValue = data.getStringExtra("Field Value");

                if (fieldType.equals(UserField.HOBBIES.toString())) {
                    currentUser.getHobbies().add(fieldValue);
                    hobbiesAdapter.notifyDataSetChanged();
                } else if (fieldType.equals(UserField.PERSONALITY.toString())) {
                    currentUser.getPersonalities().add(fieldValue);
                    personalitiesAdapter.notifyDataSetChanged();
                } else if (fieldType.equals(UserField.PLACES_LIVED.toString())) {
                    currentUser.getPlacesLived().add(fieldValue);
                    areasLivedInAdapter.notifyDataSetChanged();
                } else if (fieldType.equals(UserField.PLACES_STUDIED.toString())) {
                    currentUser.getPlacesStudied().add(fieldValue);
                    studyLocationsAdapter.notifyDataSetChanged();
                }
            }

        }

        /* Choose image from library */
        if (RequestCodes.CHOOSE_FROM_LIBRARY_REQUEST == requestCode) {
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

    private void initialiseFields() {
        nameEditText = findViewById(R.id.name_edit_text);
        nicknameEditText = findViewById(R.id.nickname_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        birthdayEditText = findViewById(R.id.birthday_edit_text);

        malesCheckBox = findViewById(R.id.male_checkbox);
        femalesCheckBox = findViewById(R.id.female_checkbox);
        othersCheckBox = findViewById(R.id.other_checkbox);

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
        Toast.makeText(EditProfileActivity.this, "Clicked add info button", Toast.LENGTH_SHORT).show();
        addInformationDialog.displayPopup("Add a hobby!", UserField.HOBBIES);
    }

    public void addPlaceStudiedAt(View view){
        Toast.makeText(EditProfileActivity.this, "Clicked add info button", Toast.LENGTH_SHORT).show();
        addInformationDialog.displayPopup("Add a place you've studied at...", UserField.PLACES_STUDIED);
    }

    public void addPlaceLived(View view){
        Toast.makeText(EditProfileActivity.this, "Clicked add info button", Toast.LENGTH_SHORT).show();
        addInformationDialog.displayPopup("Add a place you've lived!", UserField.PLACES_LIVED);
    }

    public void addPersonalityTrait(View view){
        Toast.makeText(EditProfileActivity.this, "Click add info button", Toast.LENGTH_SHORT).show();
        addInformationDialog.displayPopup("What adjective best sums up your personality?", UserField.PERSONALITY);
    }


    public void onChangeProfileImageClick(View view) {
        uploadPhotoDialog.displayPopup();
    }

    public void onSaveClick(View view) {

        uploadUserInformationToFirestore();
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

        /* Set the preferences data */
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
        finish();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}