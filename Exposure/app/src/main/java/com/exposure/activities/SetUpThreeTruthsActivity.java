package com.exposure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.exposure.R;
import com.exposure.adapters.GameListItem;
import com.exposure.constants.RequestCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetUpThreeTruthsActivity extends AppCompatActivity {

    private EditText truthOneEditText, truthTwoEditText, truthThreeEditText, lieEditText;
    private String truthOne;
    private String truthTwo;
    private String truthThree;
    private String lie;
    private String docRefID;
    private FirebaseFirestore db;
    private String otherUID;
    private String currID;
    private Map<String, String> gameInputs;
    private Map<String, String> otherUserInputs;
    private List<GameListItem> gameInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_input_truths_and_lies);
        super.onCreate(savedInstanceState);

        truthOneEditText = findViewById(R.id.truth_1_text);
        truthTwoEditText = findViewById(R.id.truth_2_text);
        truthThreeEditText = findViewById(R.id.truth_3_text);
        lieEditText = findViewById(R.id.lie_text);

        otherUID = getIntent().getStringExtra("UID");
        currID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currID.compareTo(otherUID) >= 0) {
            docRefID = currID.concat(otherUID);
        } else {
            docRefID = otherUID.concat(currID);
        }

        gameInputs = new HashMap<>();
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

        gameInputs.put("Truth One", truthOne);
        gameInputs.put("Truth Two", truthTwo);
        gameInputs.put("Truth Three", truthThree);
        gameInputs.put("Lie", lie);

        gameInformation.add(new GameListItem(gameInputs, currID));

        uploadResultsToFirebase();
        downloadResultsFromFirebase();

        Intent gameIntent = new Intent(this, ThreeTruthsOneLieActivity.class);
        startActivityForResult(gameIntent, RequestCodes.GAME_RESULT_REQUEST);
    }

    public void uploadResultsToFirebase(){
        db.collection("chats").document(docRefID)
                .update("game inputs", gameInformation);
        }


    public void downloadResultsFromFirebase(){
        db = FirebaseFirestore.getInstance();
        final DocumentReference docRefMessages = db.collection("chats").document(docRefID);
        docRefMessages.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    gameInformation = new ArrayList<>();
                    if (null != document && document.exists()) {
                        ArrayList<Map<String, Object>> gameInfoArray = (ArrayList<Map<String, Object>>) document.get("game inputs");
                        if (null != gameInfoArray) {
                            for(Map<String, Object> gameInput : gameInfoArray) {
                                gameInformation.add(new GameListItem((Map<String, String>) gameInput.get("gameInputs"), (String) gameInput.get("sender")));
                            }
                        }
                    }

                    docRefMessages.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w("MessageListener", "Listen failed.", error);
                                return;
                            }

                            if (null != value && value.exists()) {
                                gameInformation.clear();
                                ArrayList<Map<String, Object>> gameInfoArray = (ArrayList<Map<String, Object>>) value.get("messages");
                                if (null != gameInfoArray) {
                                    for (Map<String, Object> gameInput : gameInfoArray) {
                                        gameInformation.add(new GameListItem((Map<String, String>) gameInput.get("gameInputs"), (String) gameInput.get("sender")));
                                    }
                                }
                            } else {
                                Log.d("MessageListener", "Data = null");
                            }
                        }
                    });
                }
            }
        });
    }

}

