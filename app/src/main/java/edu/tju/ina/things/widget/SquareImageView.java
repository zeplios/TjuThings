/**
 * MyImageView.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package edu.tju.ina.things.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * make the ImageView square
 * @author Zhang
 *
 */
public class SquareImageView extends ImageView {

    /**
     * 记录控件的宽和高
     */
    private Point mPoint = new Point();

    public SquareImageView(Context context) {
        super(context);
    	this.setScaleType(ScaleType.CENTER_CROP);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    	this.setScaleType(ScaleType.CENTER_CROP);
    }
    
    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
    	this.setScaleType(ScaleType.CENTER_CROP);
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 因为GridView要平均分布各列，因此以宽度为准
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        mPoint.x = getMeasuredWidth();
        mPoint.y = getMeasuredHeight();
    }

    public Point getPoint() {
        return mPoint;
    }
}
