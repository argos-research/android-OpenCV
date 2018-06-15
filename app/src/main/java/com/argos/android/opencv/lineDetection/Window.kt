package com.argos.android.opencv.lineDetection

import org.opencv.core.Point

class WindowException(override var message: String): Exception(message)


class Window(x: Int, width: Int, y: Int, height: Int) {
    private var mX: Int = 0
    private var mY: Int = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    init {
        setX(x)
        setWidth(width)
        setY(y)
        setHeight(height)
    }

    fun setX(x: Int) {
        if(x < 0)
            throw WindowException("'x' cannot be be negative")
        mX = x
    }

    fun getX(): Int {
        return mX
    }

    fun setY(y: Int) {
        if (y < 0)
            throw WindowException("'y' cannot be negative")
        mY = y
    }

    fun getY(): Int {
        return mY
    }

    fun setWidth(width: Int) {
        if (width <= 0)
            throw WindowException("'width' must be greater 0")
        mWidth = width
    }

    fun getWidth(): Int {
        return mWidth
    }

    fun setHeight(height: Int) {
        if (height <= 0)
            throw WindowException("'height' must be greater 0")
        mHeight = height
    }

    fun getHeight(): Int {
        return mHeight
    }

    fun getMidpoint(): Point {
        return Point(getMidpointXDouble(), getMidpointYDouble())
    }

    fun getMidpointX(): Int {
        return getMidpointXDouble().toInt()
    }

    fun getMidpointY(): Int {
        return getMidpointYDouble().toInt()
    }

    private fun getMidpointXDouble(): Double {
        return mX + (mWidth.toDouble()/2)
    }

    private fun getMidpointYDouble(): Double {
        return mY + (mHeight.toDouble()/2)
    }
}