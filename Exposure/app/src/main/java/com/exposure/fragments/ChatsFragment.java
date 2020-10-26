package com.exposure.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.adapters.ChatListItem;
import com.exposure.adapters.ChatsRecyclerViewAdapter;
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
    private FirebaseFirestore db;

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

        String currID = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
        final DocumentReference profileRef = db.collection("Profiles").document(currID);
        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    chats = new ArrayList<>();
                    if(null != document && document.exists()) {
                        ArrayList<String> connections = (ArrayList<String>) document.get("connections");
                        if(null != connections) {
                            for(String uid : connections) {
                                chats.add(new ChatListItem(uid));
                            }
                        }
                    }

                    chatsAdapter = new ChatsRecyclerViewAdapter(getActivity(), chats);
                    chatsRecyclerView.setAdapter(chatsAdapter);
                    chatsAdapter.notifyDataSetChanged();
                }
            }
        });


        return view;
    }
}