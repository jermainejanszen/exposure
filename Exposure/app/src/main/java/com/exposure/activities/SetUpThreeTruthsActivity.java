package com.exposure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exposure.R;
import com.exposure.constants.RequestCodes;

public class SetUpThreeTruthsActivity extends AppCompatActivity {

    private EditText truthOneEditText, truthTwoEditText, truthThreeEditText, lieEditText;
    private String truthOne;
    private String truthTwo;
    private String truthThree;
    private String lie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_input_truths_and_lies);
        super.onCreate(savedInstanceState);

        truthOneEditText = findViewById(R.id.truth_1_text);
        truthTwoEditText = findViewById(R.id.truth_2_text);
        truthThreeEditText = findViewById(R.id.truth_3_text);
        lieEditText = findViewById(R.id.lie_text);
    }

    public void onClickPlay(){
        truthOne = truthOneEditText.getText().toString();
        truthTwo = truthTwoEditText.getText().toString();
        truthThree = truthThreeEditText.getText().toString();
        lie = lieEditText.getText().toString();


        /* Invalid edit checking */
        if (truthOne.isEmpty()) {
            Toast.makeText(this, "First truth required", Toast.LENGTH_LONG).show();
            truthOneEditText.requestFocus();
            return;
        } else if (truthTwo.isEmpty()) {
            /* Need to check if this email is valid in firebase */
            Toast.makeText(this, "Second truth required", Toast.LENGTH_LONG).show();
            truthTwoEditText.requestFocus();
            return;
        } else if (truthThree.isEmpty()) {
            Toast.makeText(this, "Third truth required", Toast.LENGTH_LONG).show();
            truthThreeEditText.requestFocus();
            return;
        } else if (lie.isEmpty()) {
            Toast.makeText(this, "A lie is required", Toast.LENGTH_LONG).show();
            lieEditText.requestFocus();
            return;
        }

        Intent gameIntent = new Intent(this, ThreeTruthsOneLieActivity.class);

        gameIntent.putExtra("Truth One", truthOne);
        gameIntent.putExtra("Truth Two", truthTwo);
        gameIntent.putExtra("Thruth Three", truthThree);
        gameIntent.putExtra("Lie One", lie);

        startActivityForResult(gameIntent, RequestCodes.GAME_RESULT_REQUEST);

    }
}
