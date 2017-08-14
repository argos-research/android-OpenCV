package com.argos.android.opencv.Interface;

import android.support.annotation.DrawableRes;

/**
 * Interfacing {@link com.argos.android.opencv.Adapter.ImageListAdapter} -> {@link com.argos.android.opencv.Activity.MainActivity}
 */

public interface DialogCallback
{
    void launchImageDetection(String feature, @DrawableRes int image);
}
