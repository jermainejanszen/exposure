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

public class AddUserFieldActivity extends Activity {
    private EditText addedField;

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

    private void save() {
        Intent intent = new Intent();
        intent.putExtra("New Field", addedField.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
