package com.argos.android.opencv.lineDetection.windowFinding

class BinaryImageEmptyWrapper(private val mWidth: Int, private val mHeight: Int): BinaryImage {

    override fun getWidth(): Int {
        return mWidth
    }

    override fun getHeight(): Int {
        return mHeight
    }

    override fun get(x: Int, y: Int): Int {
        if(x in 0..(mWidth - 1) && y in 0..(mHeight - 1))
            return 0
        throw IndexOutOfBoundsException()
    }
}