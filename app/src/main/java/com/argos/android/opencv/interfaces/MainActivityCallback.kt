package com.argos.android.opencv.interfaces

import com.argos.android.opencv.model.Feature

/**
 * Interfacing [com.argos.android.opencv.adapter.FeatureListAdapter] -> [com.argos.android.opencv.activity.MainActivity]
 */

interface MainActivityCallback {
    fun chooseImage(feature: Feature)
    fun launchCameraDetection(feature: Feature)
}
