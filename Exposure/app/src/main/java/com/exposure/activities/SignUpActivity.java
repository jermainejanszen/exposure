package com.exposure.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.exposure.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Activity handling the sign up functionality of the application
 */
public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout fullNameField, emailField, passwordField;
    private CheckBox termsOfService;
    private ProgressBar signUpProgressBar;

    /**
     * Upon creating the sign up activity, the view is set up and the instance of the firebase
     * authentication is retrieved
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        fullNameField = findViewById(R.id.full_name_field);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        termsOfService = findViewById(R.id.terms_of_service_checkbox);
        signUpProgressBar = findViewById(R.id.progress_bar);
    }

    /**
     * OnClick listener for when user clicks to sign in to the application
     * @param view the current GUI view
     */
    public void signUpUser(View view) {
        final String fullName = fullNameField.getEditText().getText().toString().trim();
        String email = emailField.getEditText().getText().toString().trim();
        String password = passwordField.getEditText().getText().toString().trim();

        /* Checks user information has been inputted and creates user with the given information */
        if (fullName.isEmpty()) {
            fullNameField.getEditText().setError("Name required");
            fullNameField.getEditText().requestFocus();
        } else if (email.isEmpty()) {
            emailField.getEditText().setError("Email required");
            emailField.getEditText().requestFocus();
        } else if (password.isEmpty()) {
            passwordField.getEditText().setError("Password required");
            passwordField.getEditText().requestFocus();
        } else if (!termsOfService.isChecked()) {
            Toast.makeText(getApplicationContext(), "You must accept the Terms of Service " +
                    "and Privacy Policy to sign up.", Toast.LENGTH_LONG).show();
        } else {
            signUpProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), "Successful sign up.",
                                    Toast.LENGTH_LONG).show();

                            final UserProfileChangeRequest profileUpdates = new
                                    UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(getApplicationContext(),
                                                    MainActivity.class));
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            signUpProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    /**
     * OnClick listener for taking user to the log in activity
     * @param view
     */
    public void startLoginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}