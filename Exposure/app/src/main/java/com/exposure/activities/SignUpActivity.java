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

        fullNameField = findViewById(R.id.full_name_field);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        termsOfService = findViewById(R.id.terms_of_service_checkbox);
        signUpProgressBar = findViewById(R.id.progress_bar);
    }

    public void signUpUser(View view) {
        String fullName = fullNameField.getEditText().getText().toString().trim();
        String email = emailField.getEditText().getText().toString().trim();
        String password = passwordField.getEditText().getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Full name required", Toast.LENGTH_LONG).show();
            fullNameField.getEditText().requestFocus();
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Email required", Toast.LENGTH_LONG).show();
            emailField.getEditText().requestFocus();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password required", Toast.LENGTH_LONG).show();
            passwordField.getEditText().requestFocus();
        } else if (!termsOfService.isChecked()) {
            Toast.makeText(getApplicationContext(), "You must accept the Terms of Service and Privacy Policy to sign up.", Toast.LENGTH_LONG).show();
        } else {
            signUpProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), "Successful sign up.", Toast.LENGTH_LONG).show();
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