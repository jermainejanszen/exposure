package com.exposure.popups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.exposure.R;
import com.exposure.constants.RequestCodes;

/**
 * Activity allows users to retrieve an image to include in their profile
 */
public class RetrieveImageActivity extends Activity {

    /**
     * Upon creating the activity, the view is set
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_library_selection);
    }

    /**
     * On returning to this activity, this is called to handle the actions required if once an
     * image has been retrieved
     * @param requestCode the request code signifying what action has occurred
     * @param resultCode the result of the action that has occurred
     * @param data the data returned from the previous activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RequestCodes.CHOOSE_FROM_LIBRARY_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                data.putExtra("Source", "Library");
                setResult(RESULT_OK, data);
                finish();
            }
        }

        if (RequestCodes.TAKE_PHOTO_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                data.putExtra("Source", "Image Capture");
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    /**
     * OnClick listener called when user wishes to chose a photo from their photo library
     * @param view the current GUI view
     */
    public void chooseFromLibrary(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, RequestCodes.CHOOSE_FROM_LIBRARY_REQUEST);
    }

    /**
     * OnClick listener called when user wishes to take a photo to add to their profile
     * @param view the current GUI view
     */
    public void takePhoto(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, RequestCodes.TAKE_PHOTO_REQUEST);
    }

    /**
     * OnClick listener if the user wishes to cancel and no longer retrieve an image
     * @param view the current GUI view
     */
    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
