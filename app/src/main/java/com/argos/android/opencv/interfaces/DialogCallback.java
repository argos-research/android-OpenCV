package com.argos.android.opencv.Interface;

import android.support.annotation.DrawableRes;

/**
 * Interfacing {@link com.argos.android.opencv.adapter.ImageListAdapter} -> {@link com.argos.android.opencv.activity.MainActivity}
 */

public interface DialogCallback
{
    void launchImageDetection(String feature, @DrawableRes int image);
}
