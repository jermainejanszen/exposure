package com.exposure.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<ChatListItem> data;
    private boolean editable;

    public ChatsRecyclerViewAdapter(Context context, List<ChatListItem> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // holder.getProfileImage().setImageBitmap(data.get(position).getProfileImage());
        holder.getName().setText(data.get(position).getName());
        holder.getLastMessage().setText(data.get(position).getLastMessage());
        holder.getDate().setText(data.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profileImage;
        private final TextView name;
        private final TextView lastMessage;
        private final ImageButton openButton;
        private final TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.chat_profile_image);
            name = itemView.findViewById(R.id.chat_name);
            lastMessage = itemView.findViewById(R.id.chat_last_message);
            openButton = itemView.findViewById(R.id.chat_open_button);
            date = itemView.findViewById(R.id.chat_date);
        }

        public CircleImageView getProfileImage() {
            return profileImage;
        }

        public TextView getName() {
            return name;
        }

        public TextView getLastMessage() {
            return lastMessage;
        }

        public ImageButton getOpenButton() {
            return openButton;
        }

        public TextView getDate() {
            return date;
        }
    }
}