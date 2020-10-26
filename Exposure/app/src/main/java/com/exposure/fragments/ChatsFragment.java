package com.exposure.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.ChatListItem;
import com.exposure.adapters.ChatsRecyclerViewAdapter;

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

        /* Placeholders */
        chats = new ArrayList<>();
        chats.add(new ChatListItem("PTIDi7lEIkb7PMOD7S4ihbPTecT2"));
        chatsAdapter = new ChatsRecyclerViewAdapter(getActivity(), chats);

        RecyclerView chatsRecyclerView = view.findViewById(R.id.chat_list);
        chatsRecyclerView.setAdapter(chatsAdapter);

        return view;
    }
}