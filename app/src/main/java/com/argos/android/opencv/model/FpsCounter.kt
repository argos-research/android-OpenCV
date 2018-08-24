package com.argos.android.opencv.model

import org.opencv.core.Core
import java.text.DecimalFormat

class FpsCounter {
    companion object {
        private const val STEP = 5
        private val FPS_FORMAT = DecimalFormat("0.00")
    }

    private var mFramesCounter: Int = 0
    private var mFrequency: Double = 0.toDouble()
    private var mPrevFrameTime: Long = 0
    private var mStrFps: String = ""
    private var mIsInitialized = false

    fun newFrame() {
        if (!mIsInitialized)
            init()
        else
            calculateFps()
    }

    private fun init() {
        mFramesCounter = 0
        mFrequency = Core.getTickFrequency()
        mPrevFrameTime = Core.getTickCount()
        mStrFps = ""

        mIsInitialized = true
    }

    private fun calculateFps() {
        mFramesCounter++
        if (mFramesCounter % STEP == 0) {
            val time = Core.getTickCount()
            val fps = STEP * mFrequency / (time - mPrevFrameTime)
            mPrevFrameTime = time

            mStrFps = FPS_FORMAT.format(fps) + " FPS"
        }
    }

    fun getFps(): String {
        return mStrFps
    }
}