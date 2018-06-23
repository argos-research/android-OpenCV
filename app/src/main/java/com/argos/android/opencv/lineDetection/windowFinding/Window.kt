package com.argos.android.opencv.lineDetection.windowFinding

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
        mX = x
    }

    fun getX(): Int {
        return mX
    }

    fun increaseX() {
        setX(mX + 1)
    }

    fun decreaseX() {
        setX(mX - 1)
    }

    fun setY(y: Int) {
        mY = y
    }

    fun getY(): Int {
        return mY
    }

    fun increaseY() {
        setY(mY + 1)
    }

    fun decreaseY() {
        setY(mY - 1)
    }

    fun setWidth(width: Int) {
        if (width <= 0)
            throw WindowException("'width' must be greater 0")
        mWidth = width
    }

    fun getWidth(): Int {
        return mWidth
    }

    fun increaseWidth() {
        setWidth(mWidth+1)
    }

    fun decreaseWidth() {
        setWidth(mWidth-1)
    }

    fun setHeight(height: Int) {
        if (height <= 0)
            throw WindowException("'height' must be greater 0")
        mHeight = height
    }

    fun getHeight(): Int {
        return mHeight
    }

    fun increaseHeight() {
        setHeight(mHeight+1)
    }

    fun decreaseHeight() {
        setHeight(mHeight-1)
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

    fun getBorderRight(): Int {
        return mX + mWidth - 1
    }

    fun getBorderBelow(): Int {
        return mY + mHeight - 1
    }

    fun splitWindowInHeight(): Pair<Window, Window> {
        val upperWindow = Window(mX, mWidth, mY, Math.floor(mHeight/2.0).toInt())
        val lowerWindow = Window(mX, mWidth, upperWindow.getBorderBelow() + 1, Math.ceil(mHeight/2.0).toInt())
        return Pair(upperWindow, lowerWindow)
    }

    fun equals(other: Window): Boolean {
        if (other.getX() != mX)
            return false
        if (other.getY() != mY)
            return false
        if (other.getWidth() != mWidth)
            return false
        if (other.getHeight() != mHeight)
            return false
        return true
    }

    override fun toString(): String {
        return "x: $mX, width: $mWidth, y: $mY, height: $mHeight"
    }
}