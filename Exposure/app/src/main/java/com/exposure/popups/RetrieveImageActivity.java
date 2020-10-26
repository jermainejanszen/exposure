package com.exposure.popups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.exposure.R;
import com.exposure.constants.RequestCodes;

public class RetrieveImageActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_library_selection);
    }

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

    public void chooseFromLibrary(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, RequestCodes.CHOOSE_FROM_LIBRARY_REQUEST);
    }

    public void takePhoto(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, RequestCodes.TAKE_PHOTO_REQUEST);
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
