package com.argos.android.opencv.Interface;

import com.argos.android.opencv.Model.Feature;

/**
 * Interfacing {@link com.argos.android.opencv.Adapter.FeatureListAdapter} -> {@link com.argos.android.opencv.Activity.MainActivity}
 */

public interface MainActivityCallback
{
    void chooseImage(Feature feature);
    void launchCameraDetection(Feature feature);
}
