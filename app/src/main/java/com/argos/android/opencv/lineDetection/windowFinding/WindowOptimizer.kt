package com.argos.android.opencv.lineDetection.windowFinding


fun maximizeWindowWidth(window: Window) {
    maximizeWindowWidthEnlargeLeft(window)
    maximizeWindowWidthEnlargeRight(window)
}

fun maximizeWindowWidthEnlargeLeft(window: Window) {
    if (window.getPixelInWindow() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    var prevPixelInWindow = window.getPixelInWindow()
    while (true){
        try {
            window.decreaseX()
            window.increaseWidth()
            if (window.getPixelInWindow() > prevPixelInWindow) {
                prevPixelInWindow = window.getPixelInWindow()
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

fun maximizeWindowWidthEnlargeRight(window: Window) {
    if (window.getPixelInWindow() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    var prevPixelInWindow = window.getPixelInWindow()
    while (true) {
        try {
            window.increaseWidth()
            if (window.getPixelInWindow() > prevPixelInWindow) {
                prevPixelInWindow = window.getPixelInWindow()
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
    if (window.getPixelInWindow() == 0)
        throw NoWindowFoundException("There aren't any pixel in the window")

    var prevPixelInWindow = window.getPixelInWindow()
    while (true) {
        try {
            window.decreaseY()
            window.increaseHeight()
            if (window.getPixelInWindow() > prevPixelInWindow) {
                prevPixelInWindow = window.getPixelInWindow()
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
    if (window.getPixelInWindow() == 0)
        throw NoWindowFoundException("There aren't any pixel in window")

    val prevPixelInWindow = window.getPixelInWindow()
    while (true) {
        try {
            window.decreaseWidth()
            window.increaseX()
            if (prevPixelInWindow > window.getPixelInWindow()) {
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
    if (window.getPixelInWindow() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    val prevPixelInWindow = window.getPixelInWindow()
    while (true) {
        try {
            window.decreaseWidth()
            if (prevPixelInWindow > window.getPixelInWindow()) {
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
    if (window.getPixelInWindow() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    val prevPixelInWindow = window.getPixelInWindow()
    while (true) {
        try {
            window.decreaseHeight()
            window.increaseY()
            if (prevPixelInWindow > window.getPixelInWindow()) {
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
    if (window.getPixelInWindow() == 0)
        throw NoWindowFoundException("There aren't any Pixel in Window")

    val prevPixelInWindow = window.getPixelInWindow()
    while (true) {
        try {
            window.decreaseHeight()
            if (prevPixelInWindow > window.getPixelInWindow()) {
                window.increaseHeight()
                break
            }
        } catch (e: WindowException) {
            break
        }
    }
}
