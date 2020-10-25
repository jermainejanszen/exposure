package com.exposure.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.exposure.R;
import com.exposure.callback.DialogCallback;
import com.exposure.user.UserField;

public class AddInformationDialog extends Dialog {
    private final DialogCallback dialogCallback;

    public AddInformationDialog(Activity activity, DialogCallback dialogCallback) {
        super(activity);
        this.dialogCallback = dialogCallback;

        /* Just in case we need it later on */
        setOwnerActivity(activity);

        /* Place the dialog at the bottom of the screen */
        getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;
    }

    public void displayPopup(String message, final UserField fieldType) {
        setContentView(R.layout.activity_add_user_field_popup);

        final EditText addedField = findViewById(R.id.add_field);
        final TextView saveText = findViewById(R.id.save);

        TextView type = findViewById(R.id.field);
        type.setText(message);

        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCallback.send(fieldType, addedField.getText().toString());
                dismiss();
            }
        });

        show();
    }
}
