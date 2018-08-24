package com.argos.android.opencv.lineDetection.windowFinding

class BinaryImageArrayWrapper(private val image: Array<IntArray>): BinaryImage {
    override fun getWidth(): Int {
        return image.size
    }

    override fun getHeight(): Int {
        return image[0].size
    }

    override fun get(x: Int, y: Int): Int {
        return image[x][y]
    }
}
