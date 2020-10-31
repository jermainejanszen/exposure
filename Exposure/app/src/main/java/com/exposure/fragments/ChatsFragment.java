package com.exposure.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.activities.MainActivity;
import com.exposure.activities.MessageActivity;
import com.exposure.adapters.ChatListItem;
import com.exposure.adapters.ChatsRecyclerViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.callback.OnItemPressedCallback;
import com.exposure.user.ConnectionItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private List<ChatListItem> chats;
    private ChatsRecyclerViewAdapter chatsAdapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        assert null != getActivity();

        final RecyclerView chatsRecyclerView = view.findViewById(R.id.chat_list);

        chats = new ArrayList<>();

        OnCompleteCallback notifyCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    chatsAdapter.notifyDataSetChanged();
                }
            }
        };

        OnItemPressedCallback pressedCallback = new OnItemPressedCallback() {
            @Override
            public void onPress(String uid) {
                onChatItemPressed(uid);
            }
        };

        if (null == chatsAdapter) {
            chatsAdapter = new ChatsRecyclerViewAdapter(chats, pressedCallback);
        } else {
            chats = chatsAdapter.getData();
            chatsAdapter.syncData();
        }

        for (ConnectionItem connection : MainActivity.getCurrentUser().getConnections()) {
            if (!containsUid(connection.getUid())) {
                chats.add(new ChatListItem(connection.getUid(), notifyCallback));
            }
        }

        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsAdapter.notifyDataSetChanged();

        return view;
    }

    private void onChatItemPressed(String uid) {
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.putExtra("UID", uid);
        getContext().startActivity(intent);
    }

    private boolean containsUid(String uid) {
        for (ChatListItem chat : chats) {
            if (0 == chat.getUid().compareTo(uid)) {
                return true;
            }
        }
        return false;
    }
}