package com.argos.android.opencv.lineDetection.windowFinding


class WindowWithToBigException: Exception()


fun maximizeWindowWidth(window: Window, maxWidth: Int = Int.MAX_VALUE) {
    maximizeWindowWidthEnlargeLeft(window, maxWidth)
    maximizeWindowWidthEnlargeRight(window, maxWidth)
}

fun maximizeWindowWidthEnlargeLeft(window: Window, maxWidth: Int = Int.MAX_VALUE) {
    if (window.getNonZeroPixel() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    var prevPixelInWindow = window.getNonZeroPixel()
    while (true) {
        if (window.getWidth() > maxWidth)
            throw WindowWithToBigException()
        try {
            window.decreaseX()
            window.increaseWidth()
            if (window.getNonZeroPixel() > prevPixelInWindow) {
                prevPixelInWindow = window.getNonZeroPixel()
            } else {
                window.decreaseWidth()
                window.increaseX()
                break
            }
        } catch (e: WindowException) {
            break
        }
    }
}

fun maximizeWindowWidthEnlargeRight(window: Window, maxWidth: Int = Int.MAX_VALUE) {
    if (window.getNonZeroPixel() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    var prevPixelInWindow = window.getNonZeroPixel()
    while (true) {
        if (window.getWidth() > maxWidth)
            throw WindowWithToBigException()
        try {
            window.increaseWidth()
            if (window.getNonZeroPixel() > prevPixelInWindow) {
                prevPixelInWindow = window.getNonZeroPixel()
            } else {
                window.decreaseWidth()
                break
            }
        } catch (e: WindowException) {
            break
        }
    }
}

fun maximizeWindowHeightEnlargeAbove(window: Window) {
    if (window.getNonZeroPixel() == 0)
        throw NoWindowFoundException("There aren't any pixel in the window")

    var prevPixelInWindow = window.getNonZeroPixel()
    while (true) {
        try {
            window.decreaseY()
            window.increaseHeight()
            if (window.getNonZeroPixel() > prevPixelInWindow) {
                prevPixelInWindow = window.getNonZeroPixel()
            } else {
                window.increaseY()
                window.decreaseHeight()
                break
            }
        } catch (e: WindowException) {
            break
        }
    }
}

fun minimizeWindowWidth(window: Window) {
    minimizeWindowWidthDecreaseLeft(window)
    minimizeWindowWidthDecreaseRight(window)
}

fun minimizeWindowWidthDecreaseLeft(window: Window){
    if (window.getNonZeroPixel() == 0)
        throw NoWindowFoundException("There aren't any pixel in window")

    val prevPixelInWindow = window.getNonZeroPixel()
    while (true) {
        try {
            window.decreaseWidth()
            window.increaseX()
            if (prevPixelInWindow > window.getNonZeroPixel()) {
                window.decreaseX()
                window.increaseWidth()
                break
            }
        } catch (e: WindowException) {
            break
        }
    }
}

fun minimizeWindowWidthDecreaseRight(window: Window) {
    if (window.getNonZeroPixel() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    val prevPixelInWindow = window.getNonZeroPixel()
    while (true) {
        try {
            window.decreaseWidth()
            if (prevPixelInWindow > window.getNonZeroPixel()) {
                window.increaseWidth()
                break
            }
        } catch (e: WindowException) {
            break
        }
    }
}

fun minimizeWindowHeight(window: Window) {
    minimizeWindowHeightDecreaseAbove(window)
    minimizeWindowHeightDecreaseBelow(window)
}

fun minimizeWindowHeightDecreaseAbove(window: Window) {
    if (window.getNonZeroPixel() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    val prevPixelInWindow = window.getNonZeroPixel()
    while (true) {
        try {
            window.decreaseHeight()
            window.increaseY()
            if (prevPixelInWindow > window.getNonZeroPixel()) {
                window.decreaseY()
                window.increaseHeight()
                break
            }
        } catch (e: WindowException) {
            break
        }

    }
}

fun minimizeWindowHeightDecreaseBelow(window: Window) {
    if (window.getNonZeroPixel() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    val prevPixelInWindow = window.getNonZeroPixel()
    while (true) {
        try {
            window.decreaseHeight()
            if (prevPixelInWindow > window.getNonZeroPixel()) {
                window.increaseHeight()
                break
            }
        } catch (e: WindowException) {
            break
        }
    }
}
