package com.exposure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private TextInputLayout fullNameField, emailField, passwordField, repeatPasswordField;
    private CheckBox termsOfService;
    private ProgressBar signUpProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullNameField = findViewById(R.id.full_name_field);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        repeatPasswordField = findViewById(R.id.repeat_password_field);
        termsOfService = findViewById(R.id.terms_of_service_checkbox);
        signUpProgressBar = findViewById(R.id.sign_up_progress_bar);

        mAuth = FirebaseAuth.getInstance();
    }

    public void registerUser(View view) {
        String fullName = fullNameField.getEditText().getText().toString().trim();
        String email = emailField.getEditText().getText().toString().trim();
        String password = passwordField.getEditText().getText().toString().trim();
        String repeatPassword = repeatPasswordField.getEditText().getText().toString().trim();

        if (fullName.isEmpty()) {
            fullNameField.getEditText().setError("Full name required.");
            fullNameField.getEditText().requestFocus();
        } else if (email.isEmpty()) {
            emailField.getEditText().setError("Email required.");
            emailField.getEditText().requestFocus();
        } else if (password.isEmpty()) {
            passwordField.getEditText().setError("Password required.");
            passwordField.getEditText().requestFocus();
        } else if (repeatPassword.isEmpty()) {
            repeatPasswordField.getEditText().setError("Password confirmation required.");
            repeatPasswordField.getEditText().requestFocus();
        } else if (!password.equals(repeatPassword)) {
            repeatPasswordField.getEditText().setError("Passwords don't match.");
        } else if (!termsOfService.isChecked()) {
            Toast.makeText(getApplicationContext(), "You must accept the Terms of Service and Privacy Policy to sign up.", Toast.LENGTH_SHORT).show();
        } else {
            signUpProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), "Successful sign up.", Toast.LENGTH_SHORT).show();
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
}