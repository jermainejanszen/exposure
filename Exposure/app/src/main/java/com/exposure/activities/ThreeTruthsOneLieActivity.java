package com.exposure.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.exposure.R;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.constants.ResultCodes;
import com.exposure.handlers.UserMediaHandler;
import com.exposure.user.CurrentUser;
import com.exposure.user.OtherUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Activity handling the three truths and one lie game
 */
public class ThreeTruthsOneLieActivity extends AppCompatActivity {

    OtherUser otherUser;
    CurrentUser currentUser;
    List<String> truths;
    List<String> lies;
    Map<String, String> mapTruthsLiesToView;
    Button[] inputFields = new Button[4];
    int indexOfLie;

    /**
     * Upon creating the activity, the view is set up, the truths and lies about the other user are
     * read in and put in a random order, and then presented on the screen for the user to choose
     * the lie from
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_truths_and_lie);

        inputFields[0] = findViewById(R.id.input_1);
        inputFields[1] = findViewById(R.id.input_2);
        inputFields[2] = findViewById(R.id.input_3);
        inputFields[3] = findViewById(R.id.input_4);

        mapTruthsLiesToView = new HashMap<>();

        otherUser = (OtherUser) getIntent().getSerializableExtra("other user");
        currentUser = MainActivity.getCurrentUser();
        if (null == currentUser) {
            finish();
        }

        final CircleImageView profileImage = findViewById(R.id.game_user_image);
        TextView profileName = findViewById(R.id.game_user_name);
        profileName.setText(otherUser.getNickname());

        TextView gameSubtitle = findViewById(R.id.game_subtitle);
        String subtitle = "Guess which one is the lie to learn a little bit more about " +
                otherUser.getNickname() + ".";
        gameSubtitle.setText(subtitle);

        truths = otherUser.getTruths();
        lies = otherUser.getLies();

        /* Randomise order of truths and lies */
        Collections.shuffle(truths);
        Collections.shuffle(lies);

        mapTruthsLiesToView.put("Truth 1", truths.get(0));
        mapTruthsLiesToView.put("Truth 2", truths.get(1));
        mapTruthsLiesToView.put("Truth 3", truths.get(2));
        mapTruthsLiesToView.put("Lie", lies.get(0));

        /* Randomly assign lie and truths to view */
        List mapKeys = new ArrayList(mapTruthsLiesToView.keySet());
        Collections.shuffle(mapKeys);
        int i = 0;
        while (i < 4) {
            for (Object key : mapKeys) {
                /* Access keys/values in a random order */
                inputFields[i].setText(mapTruthsLiesToView.get(key));
                if (key == "Lie"){
                    indexOfLie = i;
                }
                i++;
            }
        }

        /* Set listeners to determine which tile the user clicks on as their guess */
        for (int k = 0; k < 4; k++){
            final int finalK = k;
            inputFields[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalK == indexOfLie){
                        wonGame();
                    } else {
                        lostGame();
                    }
                }
            });
        }

        final byte[] profileByteArray = new byte[1024*1024];
        UserMediaHandler.downloadProfilePhotoFromFirebase(otherUser.getUid(), profileByteArray,
                profileByteArray.length, new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                Bitmap profileBitmap = BitmapFactory.decodeByteArray(profileByteArray, 0,
                        profileByteArray.length);
                profileImage.setImageBitmap(profileBitmap);
            }
        });
    }

    /**
     * Upon correctly choosing the correct lie, the user wins the game and activity finishes
     */
    public void wonGame() {
        setResult(ResultCodes.WON_GAME);
        finish();
    }

    /**
     * Upon correctly choosing the incorrect lie, the user loses the game and activity finishes
     */
    public void lostGame() {
        setResult(ResultCodes.LOST_GAME);
        finish();
    }

    /**
     * Calls the native back pressed button. Used to provide back press functionality to
     * UI back buttons.
     * @param view The current view
     */
    public void onBackPressed(View view) {
        super.onBackPressed();
    }
}
