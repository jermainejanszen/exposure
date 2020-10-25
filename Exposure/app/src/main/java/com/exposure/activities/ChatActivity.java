package com.exposure.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.exposure.R;

public class ChatActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
