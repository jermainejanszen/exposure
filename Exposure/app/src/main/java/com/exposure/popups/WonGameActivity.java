package com.exposure.popups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.exposure.R;
import com.exposure.activities.ViewOtherProfileActivity;

public class WonGameActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_won_game);
    }

    public void onClickContinue(View view){
        Intent continueIntent = new Intent(this, ViewOtherProfileActivity.class);
       startActivity(continueIntent);
    }
}
