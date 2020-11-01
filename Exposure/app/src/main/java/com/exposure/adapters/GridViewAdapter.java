package com.exposure.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.exposure.R;

import java.util.List;
import java.util.Map;

/**
 * Adapter for the grid view in which the user photos are displayed
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Map<String, Bitmap> bitmaps;
    private List<String> imagePaths;

    /**
     * Constructor for the grid view adapter
     * @param context the content for the grid view
     * @param bitmaps the bitmaps to be displayed in the grid view
     * @param imagePaths the image paths of the images to be displayed in the grid view
     */
    public GridViewAdapter(Context context, Map<String, Bitmap> bitmaps, List<String> imagePaths) {
        this.context = context;
        this.bitmaps = bitmaps;
        this.imagePaths = imagePaths;
    }

    /**
     * Returns the number of bitmaps stored in the grid view
     * @return
     */
    @Override
    public int getCount() {
        return bitmaps.size();
    }

    /**
     * Returns the image path at the given position
     * @param position the position of the image path to retrieve
     * @return the image path at the given position
     */
    @Override
    public String getItem(int position) {
        return imagePaths.get(position);
    }

    /**
     * Gets the item id at the given position
     * @param position the position of the item id to retrieve
     * @return the item id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * TODO
     * Get the view for a given position
     * @param position position
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (null == inflater) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (null == view) {
            view = inflater.inflate(R.layout.grid_view_item, null);
        }
        ImageView imageView = view.findViewById(R.id.grid_view_item);
        imageView.setImageBitmap(bitmaps.get(getItem(position)));

        return view;
    }
}