package com.exposure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout fullNameField, emailField, passwordField;
    private CheckBox termsOfService;
    private ProgressBar signUpProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        /**
         * If the user is already logged in, navigate to the map activity page.
         * I'm leaving this as a comment until we actually need to use it.
         *
         * if (null != mAuth.getCurrentUser()) {
         *     startActivity(new Intent(this, MapActivity.class));
         * }
         */

        fullNameField = findViewById(R.id.full_name_field);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        termsOfService = findViewById(R.id.terms_of_service_checkbox);
        signUpProgressBar = findViewById(R.id.sign_up_progress_bar);
    }

    public void signUpUser(View view) {
        String fullName = fullNameField.getEditText().getText().toString().trim();
        String email = emailField.getEditText().getText().toString().trim();
        String password = passwordField.getEditText().getText().toString().trim();

        if (fullName.isEmpty()) {
            fullNameField.getEditText().setError("Full name required.", null);
            fullNameField.getEditText().requestFocus();
        } else if (email.isEmpty()) {
            emailField.getEditText().setError("Email required.", null);
            emailField.getEditText().requestFocus();
        } else if (password.isEmpty()) {
            passwordField.getEditText().setError("Password required.", null);
            passwordField.getEditText().requestFocus();
        } else if (!termsOfService.isChecked()) {
            Toast.makeText(getApplicationContext(), "You must accept the Terms of Service and Privacy Policy to sign up.", Toast.LENGTH_SHORT).show();
        } else {
            signUpProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), "Successful sign up.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            signUpProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    public void startLoginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}