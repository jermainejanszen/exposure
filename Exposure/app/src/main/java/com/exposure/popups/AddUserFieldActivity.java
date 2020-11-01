package com.exposure.popups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.exposure.R;
import com.exposure.user.UserField;

/**
 * Allows user to enter a new entry for a particular user field
 */
public class AddUserFieldActivity extends Activity {
    private EditText addedField;

    /**
     * On creating the activity, the view is set and a listener is set to wait for the user to
     * enter the new field information
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_field);

        addedField = findViewById(R.id.add_field);
        addedField.requestFocus();

        addedField.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (EditorInfo.IME_ACTION_DONE == actionId) {
                            save();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );

        final TextView saveText = findViewById(R.id.save);

        final String userField = getIntent().getStringExtra("Field Type");

        String message = "";

        if (userField.equals(UserField.PLACES_STUDIED.toString())) {
            message = "Studied at";
        } else if (userField.equals(UserField.PLACES_LIVED.toString())) {
            message = "Lived in";
        } else if (userField.equals(UserField.HOBBIES.toString())) {
            message = "Hobby";
        } else if (userField.equals(UserField.PERSONALITIES.toString())) {
            message = "Personality trait";
        } else if (userField.equals(UserField.TRUTHS.toString())) {
            message = "Enter a truth about yourself";
        } else if (userField.equals(UserField.LIES.toString())) {
            message = "Enter a lie about yourself";
        }

        TextView type = findViewById(R.id.field);
        type.setText(message);

        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    /**
     * Allows user to save their entered field information, or cancel it if they have not
     * entered any information
     */
    private void save() {
        if (addedField.getText().toString().trim().isEmpty()) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("New Field", addedField.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
