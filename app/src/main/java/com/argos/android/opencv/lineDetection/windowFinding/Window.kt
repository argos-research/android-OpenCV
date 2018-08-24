package com.argos.android.opencv.lineDetection.windowFinding

import org.opencv.core.Point

class WindowException(override var message: String): Exception(message)


class Window(x: Int, width: Int, y: Int, height: Int, private var mBinaryImage: BinaryImage) {
    private var mX: Int = 0
    private var mY: Int = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var mPixelInWindow: Int = 0

    init {
        setX(x)
        setWidth(width)
        setY(y)
        setHeight(height)
        mPixelInWindow = countPixelInWindow()
    }

    private fun checkAndSetX(x: Int) {
        if (x in 0..(mBinaryImage.getWidth() - mWidth))
            mX = x
        else
            throw WindowException("x='$x' out of boundaries!")
    }

    fun setX(x: Int) {
        checkAndSetX(x)
        mPixelInWindow = countPixelInWindow()
    }

    fun getX(): Int {
        return mX
    }

    fun increaseX() {
        checkAndSetX(mX+1)
        mPixelInWindow += countPixelInColumn(getBorderRight()) - countPixelInColumn(getX()-1)
    }

    fun decreaseX() {
        checkAndSetX(mX-1)
        mPixelInWindow += countPixelInColumn(getX()) - countPixelInColumn(getBorderRight()+1)
    }

    private fun checkAndSetY(y: Int) {
        if (y in 0..(mBinaryImage.getHeight() - mHeight))
            mY = y
        else
            throw WindowException("y='$y' out of boundaries!")
    }

    fun setY(y: Int) {
        checkAndSetY(y)
        mPixelInWindow = countPixelInWindow()
    }

    fun getY(): Int {
        return mY
    }

    fun increaseY() {
        checkAndSetY(mY+1)
        mPixelInWindow += countPixelInRow(getBorderBelow()) - countPixelInRow(getY()-1)
    }

    fun decreaseY() {
        checkAndSetY(mY-1)
        mPixelInWindow += countPixelInRow(getY()) - countPixelInRow(getBorderBelow()+1)
    }

    private fun checkAndSetWidth(width: Int) {
        if (width in 1..(mBinaryImage.getWidth() - mX))
            mWidth = width
        else
            throw WindowException("width='$width' out of boundaries")
    }

    fun setWidth(width: Int) {
        checkAndSetWidth(width)
        mPixelInWindow = countPixelInWindow()
    }

    fun getWidth(): Int {
        return mWidth
    }

    fun increaseWidth() {
        checkAndSetWidth(mWidth+1)
        mPixelInWindow += countPixelInColumn(getBorderRight())
    }

    fun decreaseWidth() {
        checkAndSetWidth(mWidth-1)
        mPixelInWindow -= countPixelInColumn(getBorderRight()+1)
    }

    private fun checkAndSetHeight(height: Int) {
        if (height in 1..(mBinaryImage.getHeight() - mY))
            mHeight = height
        else
            throw WindowException("height='$height' out of boundaries")
    }

    fun setHeight(height: Int) {
        checkAndSetHeight(height)
        mPixelInWindow = countPixelInWindow()
    }

    fun getHeight(): Int {
        return mHeight
    }

    fun increaseHeight() {
        checkAndSetHeight(mHeight+1)
        mPixelInWindow += countPixelInRow(getBorderBelow())
    }

    fun decreaseHeight() {
        checkAndSetHeight(mHeight-1)
        mPixelInWindow -= countPixelInRow(getBorderBelow()+1)
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

    fun getMidpointAbove(): Point {
        if (mHeight < 3)
            throw WindowException("Height must be higher 2")
        val window = Window(mX, mWidth, mY, 1, mBinaryImage)
        minimizeWindowWidth(window)
        return Point(window.getMidpointX().toDouble(), window.getY().toDouble())
    }

    fun getMidpointBelow(): Point {
        if (mHeight < 3)
            throw WindowException("Height must be higher 2")
        val window = Window(mX, mWidth, getBorderBelow(), 1, mBinaryImage)
        minimizeWindowWidth(window)
        return Point(window.getMidpointX().toDouble(), window.getY().toDouble())
    }

    fun getBorderRight(): Int {
        return mX + mWidth - 1
    }

    fun getBorderBelow(): Int {
        return mY + mHeight - 1
    }

    fun getNonZeroPixel(): Int {
        return mPixelInWindow
    }

    private fun countPixelInWindow(): Int {
        var numberPixel = 0
        for (x in getX()..(getX() + getWidth() - 1))
            numberPixel += countPixelInColumn(x)
        return numberPixel
    }

    private fun countPixelInColumn(x: Int): Int {
        var numberPixel = 0
        for (y in getY()..getBorderBelow())
            numberPixel += mBinaryImage[x, y]
        return numberPixel
    }

    private fun countPixelInRow(y: Int): Int{
        var numberPixel = 0
        for (x in getX()..getBorderRight())
            numberPixel += mBinaryImage[x, y]
        return numberPixel
    }

    fun splitWindowInHeight(): Pair<Window, Window> {
        val upperWindow = Window(mX, mWidth, mY, Math.floor(mHeight/2.0).toInt(), mBinaryImage)
        val lowerWindow = Window(mX, mWidth, upperWindow.getBorderBelow() + 1, Math.ceil(mHeight/2.0).toInt(), mBinaryImage)
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