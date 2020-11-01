package com.exposure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exposure.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity allows user to log in to the app with their email and password
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout emailField, passwordField;
    private ProgressBar loginProgressBar;

    /**
     * Upon creating the activity, the view is set... TODO: finish this
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        loginProgressBar = findViewById(R.id.progress_bar);

        passwordField.getEditText().setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (EditorInfo.IME_ACTION_DONE == actionId) {
                            loginUser(null);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
    }

    /**
     * Upon clicking to login to the app, email and password are checked, user is authenticated and
     * if successful, user is signed in
     * @param view the current GUI view
     */
    public void loginUser(View view) {
        String email = emailField.getEditText().getText().toString().trim();
        String password = passwordField.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            emailField.getEditText().setError("Email required.", null);
            emailField.getEditText().requestFocus();
        } else if (password.isEmpty()) {
            passwordField.getEditText().setError("Password required.", null);
            passwordField.getEditText().requestFocus();
        } else {
            loginProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), "Successful login.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loginProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    /**
     * Upon clicking to sign up for the application, user is redirected to the sign up activity
     * @param view the current GUI view
     */
    public void startSignUpActivity(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }
}
