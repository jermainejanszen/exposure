package com.exposure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.callback.OnMapItemPressedCallback;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Recycler view adapter for representing users on the map fragment
 */
public class MapRecyclerViewAdapter extends RecyclerView.Adapter<MapRecyclerViewAdapter.ViewHolder>
{
    private final OnMapItemPressedCallback callback;
    private final List<MapListItem> data;

    /**
     * The constructor for the MapRecyclerViewAdapter
     * @param data the list of map list items
     * @param callback notifies the calling class that the task has been executed
     */
    public MapRecyclerViewAdapter(List<MapListItem> data, OnMapItemPressedCallback callback) {
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

    /**
     * Returns the number of map list items in the list
     * @return the number of map list items in the list
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Returns the list of map list items
     * @return the list of map list items
     */
    public List<MapListItem> getData() {
        return this.data;
    }

    /**
     * View holder for each of the map recycler view items
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profileImage;
        private final TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.map_profile_image);
            name = itemView.findViewById(R.id.map_name);
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
    }
}
