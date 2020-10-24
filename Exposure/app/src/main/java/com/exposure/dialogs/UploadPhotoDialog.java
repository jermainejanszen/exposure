package com.exposure.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.exposure.R;
import com.exposure.constants.RequestCodes;

import org.w3c.dom.Text;

public class UploadPhotoDialog extends Dialog {



    public UploadPhotoDialog(Activity activity) {
        super(activity);
        setOwnerActivity(activity);


        /* Place the dialog at the bottom of the screen */
        getWindow().getAttributes().gravity = Gravity.BOTTOM;
    }

    public void displayPopup() {
        setContentView(R.layout.activity_camera_library_selection);

        TextView takePhoto = findViewById(R.id.take_photo);
        TextView chooseFromLibrary = findViewById(R.id.choose_from_library);
        TextView cancel = findViewById(R.id.cancel);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        chooseFromLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromLibrary();
                dismiss();
            }
        });

        show();
    }

    private void chooseFromLibrary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getOwnerActivity().startActivityForResult(intent, RequestCodes.CHOOSE_FROM_LIBRARY_REQUEST);
    }

    /* onClick handler for the profile fragment */
    public void takePhoto() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        getOwnerActivity().startActivityForResult(cameraIntent, RequestCodes.TAKE_PHOTO_REQUEST);
    }
}
