package com.argos.android.opencv.camera

import android.util.Log
import com.argos.android.opencv.lineDetection.windowFinding.LaneFinder
import com.argos.android.opencv.model.Feature
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class CameraFrameManager(private val mCaller: CameraFrameMangerCaller, private val mFeature: Feature): Thread() {

    private var mLaneFinder = LaneFinder()
    private lateinit var mFrameInfo: Mat
    private lateinit var mDebugImage: Mat
    private var mRunning: Boolean = true

    fun finish() {
        mRunning = false
    }

    override fun run() {
        while (mRunning) {
            try {
                laneDetection(mCaller.getCopyOfCurrentFrame())
            } catch (e: NoCurrentFrameAvailableException) {
                sleep(50)
            }
        }
    }

    private fun laneDetection(frame: Mat) {
//        val (frameInfo, binaryImage) = mFeature.getFrameInfoAndDebugImage(frame)
        val (frameInfo, binaryImage) = mLaneFinder.getLanesAndBinaryImage(frame)
        Imgproc.cvtColor(frameInfo, frameInfo, Imgproc.COLOR_RGB2BGR)
        setFrameInfo(frameInfo.clone())
        binaryImage?.let { setDebugImage(binaryImage.clone()) }
    }

    @Synchronized
    private fun setFrameInfo(image: Mat) {
        mFrameInfo = image
    }

    @Synchronized
    fun getFrameInfo(): Mat {
        try {
            return mFrameInfo
        } catch (e: UninitializedPropertyAccessException) {
            throw NoCameraFrameInfoAvailableException("Frame info not initialized")
        }
    }

    @Synchronized
    private fun setDebugImage(image: Mat) {
        mDebugImage = image
    }

    @Synchronized
    fun getDebugImage(): Mat {
        try {
            return mDebugImage
        } catch (e: UninitializedPropertyAccessException) {
            throw NoDebugImageAvailableException("Debug image not initialized")
        }
    }
}
