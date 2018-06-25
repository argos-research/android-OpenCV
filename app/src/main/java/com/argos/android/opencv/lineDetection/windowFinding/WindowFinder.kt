package com.argos.android.opencv.lineDetection.windowFinding

import org.opencv.core.Point
import kotlin.math.roundToInt

class WindowFinderException(override var message: String): Exception(message)

class LastWindowFoundException: Exception()

class WindowOutOfImagePositionedException: Exception()

class WindowFinder(
    private var mWithWindow: Int,
    private var mHeightWindow: Int,
    private var mWidthStartWindow: Int,
    private var mHeightStartWindow: Int,
    private var mMinNumberPixelForWindow: Int) {

    private lateinit var mImage: BinaryImage

    private lateinit var mWindowOptimizer: WindowOptimizer

    private var mWindowsLeft: ArrayList<Window> = ArrayList()
    private var mWindowsRight: ArrayList<Window> = ArrayList()

    fun findWindows(image: BinaryImage): Pair<ArrayList<Window>, ArrayList<Window>> {
        mImage = image
        mWindowOptimizer = WindowOptimizer(mImage)

        findWindowsLeft()
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
            mWindowOptimizer.maximizeWindowWidthEnlargeLeft(lowerStartWindow)
            mWindowOptimizer.minimizeWindowWidthDecreaseRight(lowerStartWindow)
            mWindowOptimizer.minimizeWindowHeight(lowerStartWindow)
            mWindowsLeft.add(lowerStartWindow)
        } catch (e: NoWindowFoundException) { }

        try {
            moveWindowLeftUntilMinNumberPixelInWindowFound(upperStartWindow)
            mWindowOptimizer.maximizeWindowWidthEnlargeLeft(upperStartWindow)
            mWindowOptimizer.minimizeWindowWidthDecreaseRight(upperStartWindow)
            mWindowOptimizer.minimizeWindowHeight(upperStartWindow)
            mWindowsLeft.add(upperStartWindow)
        } catch (e: NoWindowFoundException) {
            if (mWindowsLeft.isEmpty())
                throw NoWindowFoundException("No start-windows found on left side")
        }
    }

    private fun findBigStartWindowLeft(): Window {
        val startWindow = Window(mImage.getWidth()/2 - mWidthStartWindow, mWidthStartWindow, mImage.getHeight() - mHeightStartWindow, mHeightStartWindow)
        moveWindowLeftUntilMinNumberPixelInWindowFound(startWindow)
        return startWindow
    }

    private fun moveWindowLeftUntilMinNumberPixelInWindowFound(window: Window) {
        var numberPixelInWindow = mWindowOptimizer.countPixelInWindow(window)
        while(numberPixelInWindow < mMinNumberPixelForWindow) {
            window.decreaseX()
            if (window.getX() < 0)
                throw NoWindowFoundException("No position with enough pixel into the window")
            numberPixelInWindow += mWindowOptimizer.countPixelInColumn(window.getX(), window.getY(), window.getBorderBelow())
            numberPixelInWindow -= mWindowOptimizer.countPixelInColumn(window.getBorderRight() + 1, window.getY(), window.getBorderBelow())
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
            mWindowOptimizer.maximizeWindowWidthEnlargeRight(lowerStartWindow)
            mWindowOptimizer.minimizeWindowWidthDecreaseLeft(lowerStartWindow)
            mWindowOptimizer.minimizeWindowHeight(lowerStartWindow)
            mWindowsRight.add(lowerStartWindow)
        } catch (e: NoWindowFoundException) {}

        try {
            moveWindowRightUntilMinNumberPixelInWindowFound(upperStartWindow)
            mWindowOptimizer.maximizeWindowWidthEnlargeRight(upperStartWindow)
            mWindowOptimizer.minimizeWindowWidthDecreaseLeft(upperStartWindow)
            mWindowOptimizer.minimizeWindowHeight(upperStartWindow)
            mWindowsRight.add(upperStartWindow)
        } catch (e: NoWindowFoundException) {
            if (mWindowsRight.isEmpty())
                throw NoWindowFoundException("No start-windows found on left side")
        }
    }

    private fun findBigStartWindowRight(): Window {
        val startWindow = Window(mImage.getWidth()/2, mWidthStartWindow, mImage.getHeight() - mHeightStartWindow, mHeightStartWindow)
        moveWindowRightUntilMinNumberPixelInWindowFound(startWindow)
        return startWindow
    }

    private fun moveWindowRightUntilMinNumberPixelInWindowFound(window: Window) {
        var numberPixelInWindow = mWindowOptimizer.countPixelInWindow(window)
        while(numberPixelInWindow < mMinNumberPixelForWindow) {
            window.increaseX()
            if (window.getBorderRight() >= mImage.getWidth())
                throw NoWindowFoundException("No position with enough pixel into the window")
            numberPixelInWindow += mWindowOptimizer.countPixelInColumn(window.getBorderRight(), window.getY(), window.getBorderBelow())
            numberPixelInWindow -= mWindowOptimizer.countPixelInColumn(window.getX()-1, window.getY(), window.getBorderBelow())
        }
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
            try {
                addNextWindow(windows, 1)
            } catch (e: NoWindowFoundException) {
                throw LastWindowFoundException()
            }
        }
    }

    private fun addNextWindow(windows: ArrayList<Window>, iteration: Int) {
        val window = getWindowAtNextPosition(windows, iteration)
        mWindowOptimizer.maximizeWindowWidth(window)
        mWindowOptimizer.minimizeWindowWidth(window)
        mWindowOptimizer.minimizeWindowHeight(window)
        windows.add(window)

        if (isWindowTouchingTheImageBorder(window)) {
            val windowBorder = Window(window.getX(), window.getWidth(), window.getY()-1, 1)
            try {
                mWindowOptimizer.minimizeWindowWidth(windowBorder)
                mWindowOptimizer.maximizeWindowHeightEnlargeAbove(windowBorder)
                windows.add(windowBorder)
            } catch (e: NoWindowFoundException) { }
            throw LastWindowFoundException()
        }
    }

    private fun getWindowAtNextPosition(windows: ArrayList<Window>, iteration: Int): Window {
        val slope = getSlopFromWindows(windows)
        val window = if (slope == 0.0)
            getWindowAtNextPositionWithoutSlope(windows[windows.lastIndex], iteration)
        else
            getWindowAtNextPositionWithSlope(windows[windows.lastIndex], slope, iteration)
        resizeWindowIfItsOutsideTheImage(window)
        return window
    }

    private fun getSlopFromWindows(windows: ArrayList<Window>): Double {
        return if (windows.size >= 2) {
            getSlope(windows[windows.lastIndex].getMidpoint(), windows[windows.lastIndex-1].getMidpoint())
        } else {
            val (upperWindow, lowerWindow) = windows[0].splitWindowInHeight()
            try {
                mWindowOptimizer.minimizeWindowWidth(upperWindow)
                mWindowOptimizer.minimizeWindowHeight(upperWindow)
                mWindowOptimizer.minimizeWindowWidth(lowerWindow)
                mWindowOptimizer.minimizeWindowHeight(lowerWindow)
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
        return Window(x.roundToInt(), mWithWindow, y, mHeightWindow)
    }

    private fun getWindowAtNextPositionWithSlope(previousWindow: Window, slope: Double, iteration: Int): Window {
        val y = previousWindow.getY() - (iteration*mHeightWindow) - mHeightWindow
        val deltaX = (previousWindow.getHeight()/2.0 + (iteration*mHeightWindow) + (mHeightWindow/2.0)) * slope
        val x = previousWindow.getMidpointX() + deltaX - (mWithWindow/2.0)
        return Window(x.roundToInt(), mWithWindow, y, mHeightWindow)
    }

    private fun resizeWindowIfItsOutsideTheImage(window: Window) {
        if (window.getX() < 0) {
            if (window.getBorderRight() >= 0) {
                window.setWidth(window.getWidth() + window.getX())
                window.setX(0)
            } else
                throw WindowOutOfImagePositionedException()
        }
        if (window.getBorderRight() > mImage.getWidth()-1) {
            if (window.getX() <= mImage.getWidth()-1) {
                window.setWidth(mImage.getWidth()-window.getX())
            } else
                throw WindowOutOfImagePositionedException()
        }
        if (window.getY() < 0) {
            if (window.getBorderBelow() >= 0) {
                window.setHeight(window.getHeight() + window.getY())
                window.setY(0)
            } else
                throw WindowOutOfImagePositionedException()
        }
        if (window.getBorderBelow() > mImage.getHeight()-1) {
            if (window.getY() <= mImage.getHeight()) {
                window.setHeight(mImage.getHeight() - window.getY())
            } else
                throw WindowOutOfImagePositionedException()
        }
    }

    private fun isWindowTouchingTheImageBorder(window: Window): Boolean {
        return (window.getX() == 0 || window.getBorderRight() == mImage.getWidth()-1 || window.getY() == 0 || window.getBorderBelow() == mImage.getHeight()-1)
    }
}
