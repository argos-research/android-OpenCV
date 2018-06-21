package com.argos.android.opencv.lineDetection.windowFinding

class WindowFinderException(override var message: String): Exception(message)

class WindowFinder(
        private var mWithWindow: Int,
        private var mHeightWindow: Int,
        private var mWidthStartWindow: Int,
        private var mHeightStartWindow: Int,
        private var mMinNumberPixelForWindow: Int) {

    private lateinit var mImage: Array<IntArray>
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0

    private lateinit var mWindowOptimizer: WindowOptimizer

    private var mWindowsLeft: ArrayList<Window> = ArrayList()
    private var mWindowsRight: ArrayList<Window> = ArrayList()

    fun findWindows(image: Array<IntArray>): Pair<ArrayList<Window>, ArrayList<Window>> {
        setImage(image)
        mWindowOptimizer = WindowOptimizer(mImage)

        findWindowsLeft()
        findWindowsRight()

        return Pair(mWindowsLeft, mWindowsRight)
    }

    private fun setImage(image: Array<IntArray>) {
        if (image.isEmpty())
            throw WindowFinderException("Image is empty (width is 0)")
        if (image[0].isEmpty())
            throw WindowFinderException("Image is empty (height is 0)")

        mImage = image
        mImageWidth = mImage.size
        mImageHeight = mImage[0].size
    }

    private fun findWindowsLeft() {
        findStartWindowsLeft()
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
        val startWindow = Window(mImageWidth/2 - mWidthStartWindow, mWidthStartWindow, mImageHeight - mHeightStartWindow, mHeightStartWindow)
        moveWindowLeftUntilMinNumberPixelInWindowFound(startWindow)
        return startWindow
    }

    private fun moveWindowLeftUntilMinNumberPixelInWindowFound(window: Window) {
        var numberPixelInWindow = mWindowOptimizer.countPixelInWindow(window)
        while(numberPixelInWindow < mMinNumberPixelForWindow)
            try {
                window.decreaseX()
                numberPixelInWindow += mWindowOptimizer.countPixelInColumn(window.getX(), window.getY(), window.getBorderBelow())
                numberPixelInWindow -= mWindowOptimizer.countPixelInColumn(window.getBorderRight()+1, window.getY(), window.getBorderBelow())
            } catch (e: WindowException) {
                throw NoWindowFoundException("No position with enough pixel into the window")
            }
    }

    private fun findWindowsRight() {
        findStartWindowsRight()
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
        val startWindow = Window(mImageWidth/2, mWidthStartWindow, mImageHeight - mHeightStartWindow, mHeightStartWindow)
        moveWindowRightUntilMinNumberPixelInWindowFound(startWindow)
        return startWindow
    }

    private fun moveWindowRightUntilMinNumberPixelInWindowFound(window: Window) {
        var numberPixelInWindow = mWindowOptimizer.countPixelInWindow(window)
        while(numberPixelInWindow < mMinNumberPixelForWindow) {
            window.increaseX()
            if (window.getBorderRight() >= mImageWidth)
                throw NoWindowFoundException("No position with enough pixel into the window")
            numberPixelInWindow += mWindowOptimizer.countPixelInColumn(window.getBorderRight(), window.getY(), window.getBorderBelow())
            numberPixelInWindow -= mWindowOptimizer.countPixelInColumn(window.getX()-1, window.getY(), window.getBorderBelow())
        }
    }
}