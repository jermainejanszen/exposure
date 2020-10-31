package com.exposure.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.activities.ViewOtherProfileActivity;
import com.exposure.callback.OnItemPressedCallback;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapRecyclerViewAdapter extends RecyclerView.Adapter<MapRecyclerViewAdapter.ViewHolder> implements Serializable {
    private final OnItemPressedCallback callback;
    private final List<MapListItem> data;

    public MapRecyclerViewAdapter(List<MapListItem> data, OnItemPressedCallback callback) {
        this.callback = callback;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maps_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if(null != data.get(position).getProfileImage()) {
            holder.getProfileImage().setImageBitmap(data.get(position).getProfileImage());
        }
        holder.getName().setText(data.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    callback.onPress(data.get(position).getUid());
                }
            });
        }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<MapListItem> getData() {
        return this.data;
    }

    public void syncData() {
        for (MapListItem item : data) {
            item.loadFields();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profileImage;
        private final TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.map_profile_image);
            name = itemView.findViewById(R.id.map_name);
        }

        public CircleImageView getProfileImage() {
            return profileImage;
        }

        public TextView getName() {
            return name;
        }
    }
}
