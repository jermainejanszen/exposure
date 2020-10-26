package com.exposure.adapters;

import android.content.Context;
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

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MessageListItem> data;

    public MessagesRecyclerViewAdapter(Context context, List<MessageListItem> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(0 == viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message_list_item, parent, false);
            return new SentViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieved_message_list_item, parent, false);
            return new RecievedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(0 == holder.getItemViewType()) {
            ((SentViewHolder)holder).getContent().setText(this.data.get(position).getMessage());
        } else {
            ((RecievedViewHolder)holder).getContent().setText(this.data.get(position).getMessage());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return this.data.get(position).getSender() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

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

    static class RecievedViewHolder extends RecyclerView.ViewHolder {
        private final TextView content;

        public RecievedViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.message_contents);
        }

        public TextView getContent() {
            return content;
        }
    }
}
