package com.exposure.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exposure.R;
import com.exposure.callback.OnCompleteCallback;

import java.util.List;

/**
 * Adapter for the recycler view used for the user profile fields
 */
public class ChipsRecyclerViewAdapter extends
        RecyclerView.Adapter<ChipsRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private final List<String> data;
    private final boolean editable;
    private final OnCompleteCallback onDeleteCallback;

    /**
     * Constructor for the ChipsRecyclerViewAdapter
     * @param context context for the recycler view adapter
     * @param data the field data used by the adapter
     * @param editable indicates whether or not the field is editable by the user
     */
    public ChipsRecyclerViewAdapter(Context context, List<String> data, boolean editable,
                                    OnCompleteCallback onDeleteCallback) {
        this.context = context;
        this.data = data;
        this.editable = editable;
        this.onDeleteCallback = onDeleteCallback;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chips_list_item,
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
        holder.getTextView().setText(data.get(position));
        if (editable) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Item")
                            .setMessage(String.format("Are you sure you want to delete the item" +
                                    " '%s'?", data.get(position)))
                            .setPositiveButton("Delete",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            data.remove(position);
                                            onDeleteCallback.update(true, "Deleted item");
                                            notifyDataSetChanged();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            /* Do nothing */
                                        }
                                    })
                            .create()
                            .show();
                    return true;
                }
            });
        }
    }

    /**
     * Returns the number of items in the field list
     * @return the number of items in the field list
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder for each chips item in the chips list
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        /**
         * Constructor for ViewHolder
         * @param itemView item view for ViewHolder
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item);
        }

        /**
         * Gets the text view stored in ViewHolder
         * @return the text view stored in the ViewHolder
         */
        public TextView getTextView() {
            return textView;
        }
    }
}
