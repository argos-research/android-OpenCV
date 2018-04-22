package com.argos.android.opencv.interfaces;

import com.argos.android.opencv.model.Feature;

/**
 * Interfacing {@link com.argos.android.opencv.adapter.FeatureListAdapter} -> {@link com.argos.android.opencv.activity.MainActivity}
 */

public interface MainActivityCallback
{
    void chooseImage(Feature feature);
    void launchCameraDetection(Feature feature);
}
