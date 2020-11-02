package com.exposure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.callback.OnChatItemPressedCallback;
import com.exposure.callback.OnCompleteCallback;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for the recycler view used for the chat list items
 */
public class ChatsRecyclerViewAdapter extends
        RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder> {
    private OnChatItemPressedCallback callback;
    private List<ChatListItem> data;
    private OnCompleteCallback intermediateCallback, finishedCallback;

    /**
     * Constructor for chats recycler view adapter object
     * @param data the chat list items to be used in the chat recycler view
     * @param callback TODO these
     * @param intermediateCallback
     * @param finishedCallback
     */
    public ChatsRecyclerViewAdapter(List<ChatListItem> data, OnChatItemPressedCallback callback,
                                    OnCompleteCallback intermediateCallback,
                                    OnCompleteCallback finishedCallback) {
        this.callback = callback;
        this.data = data;
        this.intermediateCallback = intermediateCallback;
        this.finishedCallback = finishedCallback;
    }

    /**
     * Called when the recycler view needs a new ViewHolder of the given type to represent an item
     * @param parent the ViewGroup to which the new View will be added after being bound to an
     *               adapter position
     * @param viewType the view type of the new view
     * @return the resulting new ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item,
                parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by the recycler view inorder to display the data at the given position
     * @param holder the ViewHolder to be updated to reflect the item at the given position
     * @param position the given position to display the data at
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(null != data.get(position).getProfileImage()) {
            holder.getProfileImage().setImageBitmap(data.get(position).getProfileImage());
        }
        holder.getName().setText(data.get(position).getName());
        holder.getLastMessage().setText(data.get(position).getLastMessage());
        holder.getDate().setText(data.get(position).getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPress(
                        data.get(position).getUid(),
                        data.get(position).getName(),
                        data.get(position).getProfileImage());
            }
        });
    }

    /**
     * Returns the number of chat list items
     * @return the number of chat list items
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Returns the lsit of chat list items
     * @return
     */
    public List<ChatListItem> getData() {
        return this.data;
    }

    /**
     * Sync fields with the firebase firestore
     */
    public void syncData() {
        if (0 == data.size()) {
            finishedCallback.update(true, "Success");
        }

        for (int i = 0; i < data.size(); i++) {
            ChatListItem item = data.get(i);
            if (i == data.size() - 1) {
                item.loadFields(finishedCallback);
            } else {
                item.loadFields(intermediateCallback);
            }
        }
    }

    //TODO
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

        /**
         * Gets the circle profile image of the other user
         * @return The circle profile image of the other user
         */
        public CircleImageView getProfileImage() {
            return profileImage;
        }

        /**
         * Gets the name of the other user
         * @return the name of the other user
         */
        public TextView getName() {
            return name;
        }

        /**
         * Gets the last message sent between the current user and other user
         * @return the last message sent between the current user and other user
         */
        public TextView getLastMessage() {
            return lastMessage;
        }

        /**
         * Gets the date of the most recent message sent between the current user and the other user
         * @return the date of the most recent message sent between the current user and the other
         * user
         */
        public TextView getDate() {
            return date;
        }
    }
}
