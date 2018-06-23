package com.argos.android.opencv.lineDetection.windowFinding


class WindowOptimizer(var mPixel: Array<IntArray>) {

    fun maximizeWindowWidth(window: Window) {
        maximizeWindowWidthEnlargeLeft(window)
        maximizeWindowWidthEnlargeRight(window)
    }

    fun maximizeWindowWidthEnlargeLeft(window: Window) {
        val numberPixel = countPixelInWindow(window)
        if (numberPixel == 0)
            throw NoWindowFoundException("There aren't any Pixel in Window")

        while (window.getX() > 0){
            val numberPixelDecreasedWindow = numberPixel + countPixelInColumn(window.getX() - 1, window.getY(), window.getBorderBelow())
            if (numberPixel < numberPixelDecreasedWindow) {
                window.increaseWidth()
                window.decreaseX()
            }else
                break
        }
    }

    fun maximizeWindowWidthEnlargeRight(window: Window) {
        val numberPixel = countPixelInWindow(window)
        if (numberPixel == 0)
            throw NoWindowFoundException("There aren't any Pixel in Window")
        while (window.getBorderRight() < mPixel.lastIndex){
            val numberPixelDecreasedWindow = numberPixel + countPixelInColumn(window.getBorderRight() + 1, window.getY(), window.getBorderBelow())
            if (numberPixel < numberPixelDecreasedWindow)
                window.increaseWidth()
            else
                break
        }
    }

    fun maximizeWindowHeightEnlargeAbove(window: Window) {
        val numberPixel = countPixelInWindow(window)
        if (numberPixel == 0)
            throw NoWindowFoundException("There aren't any pixel in the window")
        while (window.getY() > 0) {
            val numberPixelDecreasedWindow = numberPixel + countPixelInRow(window.getY()-1, window.getX(), window.getBorderRight())
            if (numberPixelDecreasedWindow > numberPixel) {
                window.decreaseY()
                window.increaseHeight()
            } else
                break
        }
    }

    fun minimizeWindowWidth(window: Window) {
        minimizeWindowWidthDecreaseLeft(window)
        minimizeWindowWidthDecreaseRight(window)
    }

    fun minimizeWindowWidthDecreaseLeft(window: Window){
        val numberPixel = countPixelInWindow(window)
        if (numberPixel == 0)
            throw NoWindowFoundException("There aren't any pixel in window")

        do{
            val numberPixelDecreasedWindow = numberPixel - countPixelInColumn(window.getX(), window.getY(), window.getBorderBelow())
            if (numberPixel == numberPixelDecreasedWindow) {
                window.increaseX()
                window.decreaseWidth()
            }
        } while (numberPixel == numberPixelDecreasedWindow)
    }

    fun minimizeWindowWidthDecreaseRight(window: Window) {
        val numberPixel = countPixelInWindow(window)
        if (numberPixel == 0)
            throw NoWindowFoundException("There aren't any Pixel in Window")

        do{
            val numberPixelDecreasedWindow = numberPixel - countPixelInColumn(window.getBorderRight(), window.getY(), window.getBorderBelow())
            if (numberPixel == numberPixelDecreasedWindow) {
                window.decreaseWidth()
            }
        } while (numberPixel == numberPixelDecreasedWindow)
    }

    fun minimizeWindowHeight(window: Window) {
        minimizeWindowHeightDecreaseAbove(window)
        minimizeWindowHeightDecreaseBelow(window)
    }

    fun minimizeWindowHeightDecreaseAbove(window: Window) {
        val numberPixel = countPixelInWindow(window)
        if (numberPixel == 0)
            throw NoWindowFoundException("There aren't any Pixel in Window")

        do{
            val numberPixelDecreasedWindow = numberPixel - countPixelInRow(window.getY(), window.getX(), window.getBorderRight())
            if (numberPixel == numberPixelDecreasedWindow) {
                window.increaseY()
                window.decreaseHeight()
            }
        } while (numberPixel == numberPixelDecreasedWindow)
    }

    fun minimizeWindowHeightDecreaseBelow(window: Window) {
        val numberPixel = countPixelInWindow(window)
        if (numberPixel == 0)
            throw NoWindowFoundException("There aren't any Pixel in the Window")

        do{
            val numberPixelDecreasedWindow = numberPixel - countPixelInRow(window.getBorderBelow(), window.getX(), window.getBorderRight())
            if (numberPixel == numberPixelDecreasedWindow)
                window.decreaseHeight()
        } while (numberPixel == numberPixelDecreasedWindow)
    }

    fun countPixelInWindow(window: Window): Int {
        var numberPixel = 0
        for (x in window.getX()..(window.getX() + window.getWidth() - 1))
            numberPixel += countPixelInColumn(x, window.getY(), window.getBorderBelow())
        return numberPixel
    }

    fun countPixelInColumn(x: Int, yMin: Int, yMax: Int): Int {
        var numberPixel = 0
        for (y in yMin..yMax)
            numberPixel += mPixel[x][y]
        return numberPixel
    }

    fun countPixelInRow(y: Int, xMin: Int, xMax: Int): Int{
        var numberPixel = 0
        for (x in xMin..xMax)
            numberPixel += mPixel[x][y]
        return numberPixel
    }
}