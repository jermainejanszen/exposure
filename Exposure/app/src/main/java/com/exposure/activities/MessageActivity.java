package com.exposure.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.MessageListItem;
import com.exposure.adapters.MessagesRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends Activity {

    private List<MessageListItem> messages;
    private MessagesRecyclerViewAdapter messagesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messages = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            boolean sender = true;
            if(i % 2 == 0) {
                sender = false;
            }
            messages.add(new MessageListItem("This is a test message", sender));
        }
        messagesAdapter = new MessagesRecyclerViewAdapter(this, messages);

        RecyclerView messageRecyclerView = findViewById(R.id.messages_recycler_view);
        messageRecyclerView.setAdapter(messagesAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
