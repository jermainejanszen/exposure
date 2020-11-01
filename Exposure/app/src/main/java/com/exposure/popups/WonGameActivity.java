package com.exposure.popups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.exposure.R;
import com.exposure.activities.ViewOtherProfileActivity;

public class WonGameActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_won_game);

        TextView unlockedFeature = findViewById(R.id.unlocked_feature);
        unlockedFeature.setText(getIntent().getStringExtra("UnlockedField"));
    }

    public void onClickContinue(View view) {
        finish();
    }
}
