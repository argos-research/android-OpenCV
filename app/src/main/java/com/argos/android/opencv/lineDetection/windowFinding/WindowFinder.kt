package com.argos.android.opencv.lineDetection.windowFinding

import android.util.Log
import org.opencv.core.Point
import kotlin.math.roundToInt


class LastWindowFoundException: Exception()

class WindowOutOfImagePositionedException: Exception()

class WindowFinder(
    private var mWithWindow: Int,
    private var mHeightWindow: Int,
    private var mWidthStartWindow: Int,
    private var mHeightStartWindow: Int,
    private var mMinNumberPixelForWindow: Int,
    private var mMaxWindowWith: Int) {

    private lateinit var mImage: BinaryImage

    private var mWindowsLeft: ArrayList<Window> = ArrayList()
    private var mWindowsRight: ArrayList<Window> = ArrayList()

    fun findWindows(image: BinaryImage): Pair<ArrayList<Window>, ArrayList<Window>> {
        mImage = image

        mWindowsLeft = ArrayList()
        findWindowsLeft()
        mWindowsRight = ArrayList()
        findWindowsRight()

        return Pair(mWindowsLeft, mWindowsRight)
    }

    private fun findWindowsLeft() {
        findStartWindowsLeft()
        findNextWindows(mWindowsLeft)
    }

    private fun findStartWindowsLeft() {
        val bigStartWindow = findBigStartWindowLeft()

        val (upperStartWindow, lowerStartWindow) = bigStartWindow.splitWindowInHeight()

        try {
            moveWindowLeftUntilMinNumberPixelInWindowFound(lowerStartWindow)
            maximizeWindowWidthEnlargeLeft(lowerStartWindow)
            minimizeWindowWidthDecreaseRight(lowerStartWindow)
            minimizeWindowHeight(lowerStartWindow)
            if (lowerStartWindow.getWidth() <= mMaxWindowWith)
                mWindowsLeft.add(lowerStartWindow)
        } catch (e: NoWindowFoundException) { }

        try {
            moveWindowLeftUntilMinNumberPixelInWindowFound(upperStartWindow)
            maximizeWindowWidthEnlargeLeft(upperStartWindow)
            minimizeWindowWidthDecreaseRight(upperStartWindow)
            minimizeWindowHeight(upperStartWindow)
            if (upperStartWindow.getWidth() > mMaxWindowWith)
                splitWindowAndAddToList(upperStartWindow, mWindowsLeft)
            else
                mWindowsLeft.add(upperStartWindow)
        } catch (e: NoWindowFoundException) { }

        if (mWindowsLeft.isEmpty())
            throw NoWindowFoundException("No start-windows found on left side")
    }

    private fun findBigStartWindowLeft(): Window {
        val startWindow = Window(mImage.getWidth()/2 - mWidthStartWindow, mWidthStartWindow, mImage.getHeight() - mHeightStartWindow, mHeightStartWindow, mImage)
        moveWindowLeftUntilMinNumberPixelInWindowFound(startWindow)
        return startWindow
    }

    private fun moveWindowLeftUntilMinNumberPixelInWindowFound(window: Window) {
        while(window.getNonZeroPixel() < mMinNumberPixelForWindow) {
            try {
                window.decreaseX()
            } catch (e: WindowException) {
                throw NoWindowFoundException("No position with enough pixel into the window")
            }
        }
    }

    private fun findWindowsRight() {
        findStartWindowsRight()
        findNextWindows(mWindowsRight)
    }

    private fun findStartWindowsRight() {
        val bigStartWindow = findBigStartWindowRight()

        val (upperStartWindow, lowerStartWindow) = bigStartWindow.splitWindowInHeight()

        try {
            moveWindowRightUntilMinNumberPixelInWindowFound(lowerStartWindow)
            maximizeWindowWidthEnlargeRight(lowerStartWindow)
            minimizeWindowWidthDecreaseLeft(lowerStartWindow)
            minimizeWindowHeight(lowerStartWindow)
            if (lowerStartWindow.getWidth() <= mMaxWindowWith)
                mWindowsRight.add(lowerStartWindow)
        } catch (e: NoWindowFoundException) {}

        try {
            moveWindowRightUntilMinNumberPixelInWindowFound(upperStartWindow)
            maximizeWindowWidthEnlargeRight(upperStartWindow)
            minimizeWindowWidthDecreaseLeft(upperStartWindow)
            minimizeWindowHeight(upperStartWindow)
            if (upperStartWindow.getWidth() > mMaxWindowWith)
                splitWindowAndAddToList(upperStartWindow, mWindowsRight)
            else
                mWindowsRight.add(upperStartWindow)
        } catch (e: NoWindowFoundException) { }
            
        if (mWindowsRight.isEmpty())
            throw NoWindowFoundException("No start-windows found on left side")
    }

    private fun findBigStartWindowRight(): Window {
        val startWindow = Window(mImage.getWidth()/2, mWidthStartWindow, mImage.getHeight() - mHeightStartWindow, mHeightStartWindow, mImage)
        moveWindowRightUntilMinNumberPixelInWindowFound(startWindow)
        return startWindow
    }

    private fun moveWindowRightUntilMinNumberPixelInWindowFound(window: Window) {
        while(window.getNonZeroPixel() < mMinNumberPixelForWindow) {
            try {
                window.increaseX()
            } catch (e: WindowException) {
                throw NoWindowFoundException("No position with enough pixel into the window")
            }
        }
    }

    private fun splitWindowAndAddToList(window: Window, list: ArrayList<Window>) {
        val (upperWindow, lowerWindow) = window.splitWindowInHeight()
        minimizeWindowWidth(upperWindow)
        minimizeWindowHeight(upperWindow)
        if (upperWindow.getWidth() <= mMaxWindowWith)
            list.add(upperWindow)
        minimizeWindowWidth(lowerWindow)
        minimizeWindowHeight(lowerWindow)
        if (lowerWindow.getWidth() <= mMaxWindowWith)
            list.add(lowerWindow)
    }

    private fun findNextWindows(windows: ArrayList<Window>) {
        while (true) {
            try {
                findNextWindow(windows)
            } catch (e: LastWindowFoundException) {
                break
            } catch (e: WindowOutOfImagePositionedException) {
                break
            }
        }
    }

    private fun findNextWindow(windows: ArrayList<Window>) {
        try {
            addNextWindow(windows, 0)
        } catch (e: NoWindowFoundException) {
            if (windows.minBy { window -> window.getY() }!!.getY() > mImage.getHeight()/2)
                try {
                    addNextWindow(windows, 1)
                } catch (e: NoWindowFoundException) {
                    throw LastWindowFoundException()
                }
            else
                throw LastWindowFoundException()
        }
    }

    private fun addNextWindow(windows: ArrayList<Window>, iteration: Int) {
        var window = getWindowAtNextPosition(windows, iteration)
        try {
            maximizeWindowWidth(window, mMaxWindowWith)
        } catch (e: WindowWithToBigException) {
            Log.d(WindowFinder::class.java.simpleName, "WindowWithToBig")
            window = getWindowAtNextPosition(windows, iteration)
            window.setHeight(window.getHeight()/2)
            window.setY(window.getY() + window.getHeight())
            try {
                maximizeWindowWidth(window, mMaxWindowWith)
            } catch (e: WindowWithToBigException) {
                throw LastWindowFoundException()
            }
        }
        minimizeWindowWidth(window)
        minimizeWindowHeight(window)
        windows.add(window)

        if (isWindowTouchingTheImageBorder(window)) {
            try {
                val windowBorder = Window(window.getX(), window.getWidth(), window.getY() - 1, 1, mImage)
                try {
                    minimizeWindowWidth(windowBorder)
                    maximizeWindowHeightEnlargeAbove(windowBorder)
                    windows.add(windowBorder)
                } catch (e: NoWindowFoundException) { }
            } catch (e: WindowException) { }
            throw LastWindowFoundException()
        }
    }

    private fun getWindowAtNextPosition(windows: ArrayList<Window>, iteration: Int): Window {
        val slope = getSlopFromWindows(windows)
        return if (slope == 0.0)
            getWindowAtNextPositionWithoutSlope(windows[windows.lastIndex], iteration)
        else
            getWindowAtNextPositionWithSlope(windows[windows.lastIndex], slope, iteration)
    }

    private fun getSlopFromWindows(windows: ArrayList<Window>): Double {
        return if (windows.size >= 2) {
            getSlope(windows[windows.lastIndex].getMidpoint(), windows[windows.lastIndex-1].getMidpoint())
        } else {
            val (upperWindow, lowerWindow) = windows[0].splitWindowInHeight()
            try {
                minimizeWindowWidth(upperWindow)
                minimizeWindowHeight(upperWindow)
                minimizeWindowWidth(lowerWindow)
                minimizeWindowHeight(lowerWindow)
                getSlope(upperWindow.getMidpoint(), lowerWindow.getMidpoint())
            } catch (e: NoWindowFoundException) {
                0.0
            }
        }
    }

    private fun getSlope(point1: Point, point2: Point): Double {
        return -(point1.x - point2.x) / (point1.y - point2.y)
    }

    private fun getWindowAtNextPositionWithoutSlope(previousWindow: Window, iteration: Int): Window {
        val y = previousWindow.getY() - (iteration*mHeightWindow) - mHeightWindow
        val x = previousWindow.getMidpointX() - (mWithWindow/2.0)
        return resizeWindowIfItsOutsideTheImage(x.roundToInt(), mWithWindow, y, mHeightWindow)
    }

    private fun getWindowAtNextPositionWithSlope(previousWindow: Window, slope: Double, iteration: Int): Window {
        val y = previousWindow.getY() - (iteration*mHeightWindow) - mHeightWindow
        val deltaX = (previousWindow.getHeight()/2.0 + (iteration*mHeightWindow) + (mHeightWindow/2.0)) * slope
        val x = previousWindow.getMidpointX() + deltaX - (mWithWindow/2.0)
        return resizeWindowIfItsOutsideTheImage(x.roundToInt(), mWithWindow, y, mHeightWindow)
    }

    private fun resizeWindowIfItsOutsideTheImage(x: Int, width: Int, y: Int, height: Int): Window {
        var xWindow = x
        var widthWindow = width
        var yWindow = y
        var heightWindow = height

        if (x < 0) {
            if (x + width - 1 >= 0) {
                xWindow = 0
                widthWindow = width + x
            } else
                throw WindowOutOfImagePositionedException()
        }
        if (x + width - 1 > mImage.getWidth()-1) {
            if (x <= mImage.getWidth()-1) {
                widthWindow = mImage.getWidth() - x
            } else
                throw WindowOutOfImagePositionedException()
        }
        if (y < 0) {
            if (y + height -1 >= 0) {
                yWindow = 0
                heightWindow = height + y
            } else
                throw WindowOutOfImagePositionedException()
        }
        if (y + height -1 > mImage.getHeight()-1) {
            if (y <= mImage.getHeight()) {
                heightWindow = mImage.getHeight() - y
            } else
                throw WindowOutOfImagePositionedException()
        }

        return Window(xWindow, widthWindow, yWindow, heightWindow, mImage)
    }

    private fun isWindowTouchingTheImageBorder(window: Window): Boolean {
        return (window.getX() == 0 || window.getBorderRight() == mImage.getWidth()-1 || window.getY() == 0 || window.getBorderBelow() == mImage.getHeight()-1)
    }
}
