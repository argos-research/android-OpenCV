package com.argos.android.opencv.util;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class SquareCameraView extends AppCompatImageView {

    public SquareCameraView(Context context) {
        super(context);
    }

    public SquareCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        setMeasuredDimension(height, height);
    }

}