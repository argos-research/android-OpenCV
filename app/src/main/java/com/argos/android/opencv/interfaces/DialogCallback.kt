package com.argos.android.opencv.interfaces

import android.support.annotation.DrawableRes

/**
 * Interfacing [com.argos.android.opencv.adapter.ImageListAdapter] -> [com.argos.android.opencv.activity.MainActivity]
 */

interface DialogCallback {
    fun launchImageDetection(feature: String, @DrawableRes image: Int)
}
