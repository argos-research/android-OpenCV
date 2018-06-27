package com.argos.android.opencv.camera

import org.opencv.core.Mat

interface CameraFrameMangerCaller {
    fun getCopyOfCurrentFrame(): Mat
}

class NoCameraFrameInfoAvailableException(override val message: String): Exception(message)

class NoDebugImageAvailableException(override val message: String): Exception(message)

class NoCurrentFrameAvailableException(override val message: String): Exception(message)
