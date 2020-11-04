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
    private final Context context;
    private LayoutInflater inflater;
    private final Map<String, Bitmap> bitmaps;
    private final List<String> imagePaths;

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
     * @return number of images
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
     * Get the view for a given position
     * @param position position
     * @param view view for the grid item
     * @param viewGroup the view group of the grid
     * @return view of the individual grid item
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
        if (null != bitmaps.get(getItem(position))) {
            imageView.setImageBitmap(bitmaps.get(getItem(position)));
        } else {
            imageView.setImageDrawable(context.getDrawable(R.drawable.unexposed_image));
        }
        return view;
    }
}