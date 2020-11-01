package com.exposure.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Represents the container for an item in a the photo grid view
 */
public class GridViewItem extends androidx.appcompat.widget.AppCompatImageView {

    /**
     * Constructor for GridViewItem given context
     * @param context context of grid view item
     */
    public GridViewItem(Context context) {
        super(context);
    }

    /**
     * Constructor for GridViewItem given context and attribute set
     * @param context context for grid view item
     * @param attrs attribute set for grid view item
     */
    public GridViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor for GridViewItem given context, attribute and style
     * @param context context for grid view item
     * @param attrs attribute set for grid view item
     * @param defStyle style of grid view item
     */
    public GridViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * TODO
     * Ensures the height is always equivalent to the width of the grid view item
     * @param widthMeasureSpec width to set the grid view item
     * @param heightMeasureSpec height to set the grid view item
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}