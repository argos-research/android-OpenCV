package com.argos.android.opencv.model

import android.support.annotation.DrawableRes
import android.util.Log
import com.argos.android.opencv.lineDetection.windowFinding.LaneFinder
import org.opencv.core.Mat

class FeatureLaneDetection(override var featureName: String?, @field:DrawableRes override var featureThumbnail: Int): Feature(featureName, featureThumbnail) {
    private val mLaneFinder = LaneFinder()

    override fun getFrameInfoAndDebugImage(currentFrame: Mat): Pair<Mat, Mat?> {
        Log.d(FeatureLaneDetection::class.java.simpleName, "LANE-DETECTION")
        return mLaneFinder.getLanesAndBinaryImage(currentFrame)
    }
}