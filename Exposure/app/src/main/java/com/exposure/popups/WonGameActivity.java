package com.exposure.popups;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.exposure.R;

/**
 * Activity entered upon winning a game with another user
 */
public class WonGameActivity extends Activity {

    /**
     * Upon creating the activity, the view is set
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_won_game);

        TextView unlockedFeature = findViewById(R.id.unlocked_feature);
        unlockedFeature.setText(getIntent().getStringExtra("UnlockedField"));
    }

    /**
     * OnClick listener for the continue button
     * @param view the current GUI view
     */
    public void onClickContinue(View view) {
        finish();
    }
}
