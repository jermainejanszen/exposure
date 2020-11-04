package com.exposure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Recycler view adapter for representing the messages sent between users
 */
public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<MessageListItem> data;

    /**
     * The constructor for the MessageRecylerViewAdapter
     * @param data the list of message items to be used by the recycler view
     */
    public MessagesRecyclerViewAdapter(List<MessageListItem> data) {
        this.data = data;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(0 == viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.sent_message_list_item, parent, false);
            return new SentViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recieved_message_list_item, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    /**
     * Called by the recycler view inorder to display the data at the given position
     * @param holder the ViewHolder to be updated to reflect the item at the given position
     * @param position the given position to display the data at
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(0 == holder.getItemViewType()) {
            ((SentViewHolder)holder).getContent().setText(this.data.get(position).getMessage());
        } else {
            ((ReceivedViewHolder)holder).getContent().setText(this.data.get(position).getMessage());
        }

    }

    /**
     * Return the view type of the item at the given position
     * @param position position to retrieve the view type of the item at
     * @return 0 if the message is sent and 1 if it is received
     */
    @Override
    public int getItemViewType(int position) {
        boolean sender = this.data.get(position).getSender().equals(
                FirebaseAuth.getInstance().getUid());
        return sender ? 0 : 1;
    }

    /**
     * Returns the number of message list items represented in this recycler view
     * @return the number of message list items represented in this recycler view
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * View holder for the sent message items
     */
    static class SentViewHolder extends RecyclerView.ViewHolder {
        private final TextView content;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.message_contents);
        }

        public TextView getContent() {
            return content;
        }
    }

    /**
     * View holder for the received message items
     */
    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        private final TextView content;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.message_contents);
        }

        public TextView getContent() {
            return content;
        }
    }
}
