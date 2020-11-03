package com.exposure.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.exposure.callback.OnChatItemPressedCallback;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.user.ConnectionItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatsFragment extends Fragment {

    private List<ChatListItem> chats;
    private static ChatsRecyclerViewAdapter chatsAdapter;
    private ProgressBar progressBar;
    private RecyclerView chatsRecyclerView;
    private EditText searchBar;

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

        OnChatItemPressedCallback pressedCallback = new OnChatItemPressedCallback() {
            @Override
            public void onPress(String uid, String name, Bitmap profileImage) {
                onChatItemPressed(uid, name, profileImage);
            }
        };

        final OnCompleteCallback intermediateCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                chatsAdapter.filterChats(chats);
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
                            if (0 == o1.getTime()) {
                                return 1;
                            } else if (0 == o2.getTime()) {
                                return -1;
                            }
                            return (int) (o2.getTime() - o1.getTime());
                        }
                    });

                    Log.d("ADD TEXT CHANGED", "ADDED TEXT CHANGE LISTENER");
                    searchBar.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            filterChats(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
                filterChats(searchBar.getText().toString());
                chatsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
                chatsRecyclerView.setVisibility(View.VISIBLE);
            }
        };

        if (null == chatsAdapter) {
            chats = new ArrayList<>();
            chatsAdapter = new ChatsRecyclerViewAdapter(chats, pressedCallback,
                                intermediateCallback, finishedCallback);
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

        searchBar = view.findViewById(R.id.chat_search_bar_text);

        return view;
    }

    private void filterChats(String text) {

        Log.d("filtering", "data");
        text = text.toLowerCase();
        List<ChatListItem> filteredList = new ArrayList<>();

        for (ChatListItem item: chats) {
            if (item.getName().toLowerCase().contains(text)) {
                filteredList.add(item);
            }
        }

        chatsAdapter.filterChats(filteredList);
    }

    private void onChatItemPressed(String uid, String name, Bitmap profileImage) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("UID", uid);
        intent.putExtra("Name", name);

        if (null != profileImage) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            profileImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("ProfileImage", byteArray);
        }

        startActivity(intent);
    }

    private boolean containsUid(String uid) {
        for (ChatListItem chat : chats) {
            if (0 == chat.getUid().compareTo(uid)) {
                return true;
            }
        }
        return false;
    }

    public static void syncChatsAdapter() {
        chatsAdapter.syncData();
    }

    public void clearChats() {
        chatsAdapter = null;
    }
}