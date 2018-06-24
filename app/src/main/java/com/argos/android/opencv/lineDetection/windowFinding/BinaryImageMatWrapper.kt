package com.argos.android.opencv.lineDetection.windowFinding

import org.opencv.core.Mat

class BinaryImageMatWrapper(private val mGrayImage: Mat, private val mThresh: Int): BinaryImage {
    override fun getWidth(): Int {
        return mGrayImage.width()
    }

    override fun getHeight(): Int {
        return mGrayImage.height()
    }

    override fun get(x: Int, y: Int): Int {
        return if (mGrayImage.get(x, y)[0] < mThresh.toDouble())
            0
        else 1
    }
}
