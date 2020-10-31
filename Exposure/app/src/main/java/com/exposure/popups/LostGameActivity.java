package com.exposure.popups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.exposure.R;
import com.exposure.activities.ViewOtherProfileActivity;

public class LostGameActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_game);
    }

    public void onClickContinue(View view) {
        finish();
    }
}
