package com.exposure.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.MessageListItem;
import com.exposure.adapters.MessagesRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends Activity {

    private String otherUID;
    private String currID;
    private String docRefID;
    private FirebaseFirestore db;

    private List<MessageListItem> messages;
    private MessagesRecyclerViewAdapter messagesAdapter;
    private TextInputLayout messageInput;
    private ImageButton sendButton;
    private RecyclerView messageRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Check if user is logged in */
        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            finish();
            return;
        }

        otherUID = getIntent().getStringExtra("UID");
        currID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currID.compareTo(otherUID) >= 0) {
            docRefID = currID.concat(otherUID);
        } else {
            docRefID = otherUID.concat(currID);
        }

        Log.d("REF", docRefID);

        setContentView(R.layout.activity_message);

        messageInput = findViewById(R.id.chat_input_field);
        sendButton = findViewById(R.id.send_button);
        messageRecyclerView = findViewById(R.id.messages_recycler_view);

        messageInput.getEditText().setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (EditorInfo.IME_ACTION_SEND == actionId) {
                            onSendPressed(null);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );

        db = FirebaseFirestore.getInstance();
        final DocumentReference docRefMessages = db.collection("chats").document(docRefID);
        docRefMessages.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    messages = new ArrayList<>();
                    if (null != document && document.exists()) {
                        ArrayList<Map<String, Object>> messageArray = (ArrayList<Map<String, Object>>) document.get("messages");
                        if (null != messageArray) {
                           for(Map<String, Object> message : messageArray) {
                               messages.add(new MessageListItem((String)message.get("message"), (String)message.get("sender")));
                           }
                        }
                    }
                    messagesAdapter = new MessagesRecyclerViewAdapter(getApplicationContext(), messages);
                    messageRecyclerView.setAdapter(messagesAdapter);

                    docRefMessages.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w("MessageListener", "Listen failed.", error);
                                return;
                            }

                            if (null != value && value.exists()) {
                                messages.clear();
                                ArrayList<Map<String, Object>> messageArray = (ArrayList<Map<String, Object>>) value.get("messages");
                                if (null != messageArray) {
                                    for (Map<String, Object> message : messageArray) {
                                        messages.add(new MessageListItem((String) message.get("message"), (String) message.get("sender")));
                                    }
                                }
                                messagesAdapter.notifyDataSetChanged();
                                messageRecyclerView.scrollToPosition(messages.size() - 1);
                            } else {
                                Log.d("MessageListener", "Data = null");
                            }
                        }
                    });
                }
            }
        });
    }

    public void onSendPressed(View view) {
        String text = messageInput.getEditText().getText().toString().trim();
        if(text.length() > 0) {
            messages.add(new MessageListItem(text, currID));
            messageInput.getEditText().setText("");

            if(messages.size() > 400) {
                messages = messages.subList(messages.size() - 400, messages.size());
            }
            db.collection("chats").document(docRefID)
                    .update("messages", messages);
        }
    }

}