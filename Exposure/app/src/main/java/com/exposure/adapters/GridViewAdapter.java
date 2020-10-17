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

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Bitmap> bitmaps;

    public GridViewAdapter(Context context, List<Bitmap> bitmaps) {
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (null == inflater) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (null == view) {
            view = inflater.inflate(R.layout.grid_view_item, null);
        }
        ImageView imageView = view.findViewById(R.id.grid_view_item);
        imageView.setImageBitmap(bitmaps.get(position));

        return view;
    }
}