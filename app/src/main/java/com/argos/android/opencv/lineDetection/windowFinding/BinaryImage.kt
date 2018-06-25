package com.argos.android.opencv.lineDetection.windowFinding

interface BinaryImage {
    fun getWidth(): Int
    fun getHeight(): Int
    operator fun get(x: Int, y: Int): Int
}
