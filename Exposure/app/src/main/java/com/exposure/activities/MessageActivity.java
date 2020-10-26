package com.exposure.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.MessageListItem;
import com.exposure.adapters.MessagesRecyclerViewAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends Activity {

    private List<MessageListItem> messages;
    private MessagesRecyclerViewAdapter messagesAdapter;
    private TextInputLayout messageInput;
    private ImageButton sendButton;
    private RecyclerView messageRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageInput = findViewById(R.id.chat_input_field);
        sendButton = findViewById(R.id.send_button);

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

        /* Test data for recycler view */
        messages = new ArrayList<>();
        /*for(int i = 0; i < 30; i++) {
            boolean sender = true;
            if(i % 2 == 0) {
                sender = false;
            }
            messages.add(new MessageListItem("This is test message " + String.valueOf(i), sender));
        }*/
        messagesAdapter = new MessagesRecyclerViewAdapter(this, messages);

        messageRecyclerView = findViewById(R.id.messages_recycler_view);
        messageRecyclerView.setAdapter(messagesAdapter);



    }

    public void onSendPressed(View view) {
        String text = messageInput.getEditText().getText().toString().trim();
        if(text.length() > 0) {
            messages.add(new MessageListItem(text, true));
            messages.add(new MessageListItem("You said: " + text, false));
            messagesAdapter.notifyDataSetChanged();
            messageInput.getEditText().setText("");
            messageRecyclerView.scrollToPosition(messages.size() - 1);
        }
    }
}
