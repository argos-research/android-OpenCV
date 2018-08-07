package com.argos.android.opencv.model

import android.support.annotation.DrawableRes

class Feature(var featureName: String?, @field:DrawableRes var featureThumbnail: Int) {

    companion object {
        const val LANE_DETECTION = "Lane Detection"
        const val OVERTAKING = "Overtaking"
    }

}
