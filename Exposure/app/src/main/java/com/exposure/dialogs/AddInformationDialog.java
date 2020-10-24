package com.exposure.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exposure.R;
import com.exposure.constants.RequestCodes;
import com.exposure.user.UserField;
import com.google.firebase.firestore.auth.User;

public class AddInformationDialog extends Dialog {

    private EditText addedField;
    private TextView type;
    private String fieldType;

    public AddInformationDialog(Activity activity) {
        super(activity);
        setOwnerActivity(activity);


        /* Place the dialog at the bottom of the screen */
        getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;
    }

    public void displayPopup(String message, UserField fieldType) {
        this.fieldType = fieldType.toString();
        setContentView(R.layout.activity_add_user_field_popup);
        this.type = findViewById(R.id.field);
        type.setText(message);
        addedField = findViewById(R.id.add_field);

        TextView saveText = findViewById(R.id.save);

        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveField();
                dismiss();
            }
        });
        show();
    }

    public void saveField() {
        Intent fieldIntent = new Intent();
        fieldIntent.putExtra("Field Type", fieldType);
        fieldIntent.putExtra("Field Value", addedField.getText().toString());
        getOwnerActivity().startActivityForResult(fieldIntent, RequestCodes.SAVE_FIELD_REQUEST);

    }
}
