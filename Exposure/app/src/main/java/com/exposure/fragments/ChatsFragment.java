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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment representing the list of existing chats the user has with their matches
 */
public class ChatsFragment extends Fragment {

    private List<ChatListItem> chats;
    private static ChatsRecyclerViewAdapter chatsAdapter;
    private ProgressBar progressBar;
    private RecyclerView chatsRecyclerView;
    private EditText searchBar;

    /**
     * Empty public constructor for the chats fragment
     */
    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Call upon creating the chats fragment
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * //TODO: this
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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

                    Log.d("Finished callback", "Executed");
                    searchBar.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            filterChats(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    chatsAdapter.filterChats(chats);
                    chatsAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                    chatsRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        };

        if (null == chatsAdapter) {
            chats = new ArrayList<>();
            chatsAdapter = new ChatsRecyclerViewAdapter(chats, pressedCallback,
                                intermediateCallback, finishedCallback);
        }

        chatsRecyclerView.setAdapter(chatsAdapter);

        UserInformationHandler.downloadCurrentUserConnections(MainActivity.getCurrentUser(),
                new OnCompleteCallback() {
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

    @Override
    public void onResume() {
        super.onResume();
        searchBar.setText("");
    }

    // TODO : Javadocs
    private void filterChats(String text) {
        text = text.toLowerCase();
        List<ChatListItem> filteredList = new ArrayList<>();

        for (ChatListItem item: chats) {
            if (item.getName().toLowerCase().contains(text)) {
                filteredList.add(item);
            }
        }

        chatsAdapter.filterChats(filteredList);
    }

    /**
     * Upon clicking on a chat with another user in the chat list, the user is taken to a new
     * activity where they will be able to converse with the other user
     * @param uid uid of the other user
     * @param name name of the other user
     * @param profileImage profile image of the other user
     */

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

    /**
     * Determine whether there is an existing chat between the current user and the user with the
     * given user id
     * @param uid the user id of the other user
     * @return true if the chat exists, else false
     */
    private boolean containsUid(String uid) {
        if (null == uid || null == chats) {
            return false;
        }
        for (ChatListItem chat : chats) {
            if (0 == chat.getUid().compareTo(uid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Synchronise all chats in the adapter with the firebase firestore
     */
    public static void syncChatsAdapter() {
        chatsAdapter.syncData();
    }

    /**
     * Clear all chats in the adapter
     */
    public void clearChats() {
        chatsAdapter = null;
    }
}