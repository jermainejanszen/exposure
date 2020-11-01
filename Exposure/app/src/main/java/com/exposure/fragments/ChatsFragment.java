package com.exposure.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.activities.MainActivity;
import com.exposure.activities.MessageActivity;
import com.exposure.adapters.ChatListItem;
import com.exposure.adapters.ChatsRecyclerViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.callback.OnItemPressedCallback;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.user.ConnectionItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatsFragment extends Fragment {

    private List<ChatListItem> chats;
    private ChatsRecyclerViewAdapter chatsAdapter;
    private ProgressBar progressBar;
    private RecyclerView chatsRecyclerView;

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

        chatsRecyclerView = view.findViewById(R.id.chat_list);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        chatsRecyclerView.setVisibility(View.INVISIBLE);

        chats = new ArrayList<>();

        OnItemPressedCallback pressedCallback = new OnItemPressedCallback() {
            @Override
            public void onPress(String uid) {
                onChatItemPressed(uid);
            }
        };

        final OnCompleteCallback intermediateCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                chatsAdapter.notifyDataSetChanged();
            }
        };

        final OnCompleteCallback finishedCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    Collections.sort(chats, new Comparator<ChatListItem>() {
                        @Override
                        public int compare(ChatListItem o1, ChatListItem o2) {
                            return (int) (o2.getTime() - o1.getTime());
                        }
                    });
                    chatsAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.INVISIBLE);
                chatsRecyclerView.setVisibility(View.VISIBLE);
            }
        };

        if (null == chatsAdapter) {
            chatsAdapter = new ChatsRecyclerViewAdapter(chats, pressedCallback,
                                intermediateCallback, finishedCallback);
        } else {
            chats = chatsAdapter.getData();
            chatsAdapter.syncData();
        }

        chatsRecyclerView.setAdapter(chatsAdapter);

        UserInformationHandler.downloadCurrentUserConnections(MainActivity.getCurrentUser(), new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                for (ConnectionItem connection : MainActivity.getCurrentUser().getConnections()) {
                    if (!containsUid(connection.getUid())) {
                        chats.add(new ChatListItem(connection.getUid()));
                    }
                }
                chatsAdapter.syncData();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        chatsRecyclerView.setVisibility(View.INVISIBLE);
        chatsAdapter.syncData();
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