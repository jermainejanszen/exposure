package com.exposure.activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.exposure.R;

import java.util.Random;

public class ThreeTruthsOneLieActivity extends AppCompatActivity {

    private String[] truthsAndLie = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_truths_and_lie);

        truthsAndLie[0] = getIntent().getStringExtra("Truth One");
        truthsAndLie[1]  = getIntent().getStringExtra("Truth Two");
        truthsAndLie[2]  = getIntent().getStringExtra("Truth Three");
        truthsAndLie[3]  = getIntent().getStringExtra("Lie");

        shuffleOrder(truthsAndLie);

        Button input1 = findViewById(R.id.input_1);
        Button input2 = findViewById(R.id.input_2);
        Button input3 = findViewById(R.id.input_3);
        Button input4 = findViewById(R.id.input_4);

        input1.setText(truthsAndLie[0]);
        input2.setText(truthsAndLie[1]);
        input3.setText(truthsAndLie[2]);
        input4.setText(truthsAndLie[3]);
    }

    public void shuffleOrder(String[] array){

        Random rand = new Random();

        for (int i = 0; i < array.length; i++) {
            int randomIndexToSwap = rand.nextInt(array.length);
            String temp = array[randomIndexToSwap];
            array[randomIndexToSwap] = array[i];
            array[i] = temp;
        }
    }
}
