package com.exposure.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exposure.R;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.popups.LostGameActivity;
import com.exposure.popups.WonGameActivity;
import com.exposure.user.CurrentUser;
import com.exposure.user.OtherUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ThreeTruthsOneLieActivity extends AppCompatActivity {

    OtherUser otherUser;
    CurrentUser currentUser;
    List<String> truths;
    List<String> lies;
    Map<String, String> mapTruthsLiesToView;
    Button[] inputFields = new Button[4];
    int indexOfLie;

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
        currentUser = (CurrentUser) getIntent().getSerializableExtra("current user");
        truths = otherUser.getTruths();
        lies = otherUser.getLies();

        /** randomise order of truths and lies **/
        Collections.shuffle(truths);
        Collections.shuffle(lies);

        mapTruthsLiesToView.put("Truth 1", truths.get(0));
        mapTruthsLiesToView.put("Truth 2", truths.get(1));
        mapTruthsLiesToView.put("Truth 3", truths.get(2));
        mapTruthsLiesToView.put("Lie", lies.get(0));

        int lieIndex = -1;

        /** randomly assign lie and truths to view **/
        List mapKeys = new ArrayList(mapTruthsLiesToView.keySet());
        Collections.shuffle(mapKeys);
        int i = 0;
        while (i < 4) {
            for (Object key : mapKeys) {
                // Access keys/values in a random order
                inputFields[i].setText(mapTruthsLiesToView.get(key));
                if (key == "Lie"){
                    indexOfLie = i;
                }
                i++;
            }
        }

        for (int k = 0; k < 4; k++){
            final int finalK = k;
            inputFields[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalK == indexOfLie){
                        inputFields[finalK].setBackgroundColor(Color.RED);
                        wonGame();
                    } else {
                        lostGame();
                    }
                }
            });

        }
    }

    public void wonGame(){
        Intent wonIntent = new Intent(this, WonGameActivity.class);
        startActivity(wonIntent);
    }

    public void lostGame(){
        Intent lostIntent = new Intent(this, LostGameActivity.class);
        startActivity(lostIntent);
    }
}
