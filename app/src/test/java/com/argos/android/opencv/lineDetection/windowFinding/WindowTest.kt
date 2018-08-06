package com.argos.android.opencv.lineDetection.windowFinding

import org.junit.Test
import org.junit.Assert.*
import org.opencv.core.Point


class WindowTest {

    @Test
    fun testNewWindow() {
        val window = Window(0, 1, 2, 3, BinaryImageEmptyWrapper(10, 10))

        assertEquals(0, window.getX())
        assertEquals(1, window.getWidth())
        assertEquals(2, window.getY())
        assertEquals(3, window.getHeight())
    }

    @Test
    fun testSetX() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setX(3)

        assertEquals(3, window.getX())
    }

    @Test(expected = WindowException::class)
    fun testSetXOutOfBoundariesLeft() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setX(-1)
    }

    @Test(expected = WindowException::class)
    fun testSetXOutOfBoundariesRight() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setX(10)
    }

    @Test
    fun testIncreaseX() {
        val window = Window(2, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.increaseX()

        assertEquals(3, window.getX())
    }

    @Test
    fun testDecreaseX() {
        val window = Window(3, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.decreaseX()

        assertEquals(2, window.getX())
    }

    @Test
    fun testSetY() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setY(3)

        assertEquals(3, window.getY())
    }

    @Test(expected = WindowException::class)
    fun testSetYOutOfBoundariesAbove() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setY(-1)
    }

    @Test(expected = WindowException::class)
    fun testSetYOutOfBoundariesBelow() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setY(10)
    }

    @Test
    fun testIncreaseY() {
        val window = Window(1, 1, 2, 1, BinaryImageEmptyWrapper(10, 10))
        window.increaseY()

        assertEquals(3, window.getY())
    }

    @Test
    fun testDecreaseY() {
        val window = Window(1, 1, 3, 1, BinaryImageEmptyWrapper(10, 10))
        window.decreaseY()

        assertEquals(2, window.getY())
    }

    @Test
    fun testSetWidth() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setWidth(3)

        assertEquals(3, window.getWidth())
    }

    @Test(expected = WindowException::class)
    fun testSetWidthZero() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setWidth(0)
    }

    @Test(expected = WindowException::class)
    fun testSetWidthNegative() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setWidth(-1)
    }

    @Test(expected = WindowException::class)
    fun testSetWidthOutOfBoundaries() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setWidth(10)
    }

    @Test
    fun testIncreaseWidth() {
        val window = Window(1, 2, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.increaseWidth()

        assertEquals(3, window.getWidth())
    }

    @Test
    fun testDecreaseWidth() {
        val window = Window(1, 3, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.decreaseWidth()

        assertEquals(2, window.getWidth())
    }

    @Test(expected = WindowException::class)
    fun testSetHeightZero() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setHeight(0)
    }

    @Test(expected = WindowException::class)
    fun testSetHeightNegative() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setHeight(-1)
    }

    @Test(expected = WindowException::class)
    fun testSetHeightOutOfBoundaries() {
        val window = Window(1, 1, 1, 1, BinaryImageEmptyWrapper(10, 10))
        window.setHeight(10)
    }

    @Test
    fun testIncreaseHeight() {
        val window = Window(1, 1, 1, 2, BinaryImageEmptyWrapper(10, 10))
        window.increaseHeight()

        assertEquals(3, window.getHeight())
    }

    @Test
    fun testDecreaseHeight() {
        val window = Window(1, 1, 1, 3, BinaryImageEmptyWrapper(10, 10))
        window.decreaseHeight()

        assertEquals(2, window.getHeight())
    }

    @Test
    fun testGetMidpoint() {
        val window = Window(2, 4, 1, 3, BinaryImageEmptyWrapper(10, 10))
        assertEquals(Point(4.0, 2.5), window.getMidpoint())
    }

    @Test
    fun testGetMidpointX() {
        val window = Window(2, 4, 1, 3, BinaryImageEmptyWrapper(10, 10))
        assertEquals(4, window.getMidpointX())
    }

    @Test
    fun testGetMidpointY() {
        val window = Window(2, 4, 1, 3, BinaryImageEmptyWrapper(10, 10))
        assertEquals(2, window.getMidpointY())
    }

    @Test
    fun testGetMidpointAbove() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7
                intArrayOf(0, 0, 0, 1, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 0, 0, 1, 1, 1, 0), // 2
                intArrayOf(0, 0, 0, 0, 1, 1, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 1, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 1, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))
        val window = Window(2, 6, 0, 7, image)
        assertEquals(Point(3.0, 0.0), window.getMidpointAbove())
    }

    @Test
    fun testGetMidpointBelow() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7
                intArrayOf(0, 0, 0, 1, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 0, 0, 1, 1, 1, 0), // 2
                intArrayOf(0, 0, 0, 0, 1, 1, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 1, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 1, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 1, 0)  // 6
        ))
        val window = Window(2, 6, 0, 7, image)
        assertEquals(Point(6.0, 6.0), window.getMidpointBelow())
    }

    @Test
    fun testGetBorderRight() {
        val window = Window(2, 2, 1, 1, BinaryImageEmptyWrapper(10, 10))
        assertEquals(3, window.getBorderRight())
    }

    @Test
    fun testGetBorderBelow() {
        val window = Window(2, 2, 3, 3, BinaryImageEmptyWrapper(10, 10))
        assertEquals(5, window.getBorderBelow())
    }

    @Test
    fun testSplitWindowInHeightEven() {
        val window = Window(0, 2, 0, 10, BinaryImageEmptyWrapper(10, 10))

        val expectedUpperWindow = Window(0, 2, 0, 5, BinaryImageEmptyWrapper(10, 10))
        val expectedLowerWindow = Window(0, 2, 5, 5, BinaryImageEmptyWrapper(10, 10))
        val (actualUpperWindow, actualLowerWindow) = window.splitWindowInHeight()

        assertTrue(expectedUpperWindow.equals(actualUpperWindow))
        assertTrue(expectedLowerWindow.equals(actualLowerWindow))
    }

    @Test
    fun testSplitWindowInHeightOdd() {
        val window = Window(0, 2, 0, 11, BinaryImageEmptyWrapper(10, 12))

        val expectedUpperWindow = Window(0, 2, 0, 5, BinaryImageEmptyWrapper(10, 12))
        val expectedLowerWindow = Window(0, 2, 5, 6, BinaryImageEmptyWrapper(10, 12))
        val (actualUpperWindow, actualLowerWindow) = window.splitWindowInHeight()

        assertTrue(expectedUpperWindow.equals(actualUpperWindow))
        assertTrue(expectedLowerWindow.equals(actualLowerWindow))
    }

    @Test
    fun testGetPixelInWindowX() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0), // 1
                intArrayOf(0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 3
        ))
        val window = Window(2, 4, 1, 2, image)
        assertEquals(5, window.getNonZeroPixel())

        window.increaseX()
        assertEquals(6, window.getNonZeroPixel())

        window.decreaseX()
        assertEquals(5, window.getNonZeroPixel())

        window.setX(0)
        assertEquals(3, window.getNonZeroPixel())
    }

    @Test
    fun testGetPixelInWindowY() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7
                intArrayOf(0, 0, 0, 1, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 0, 1, 1, 1, 0), // 2
                intArrayOf(0, 1, 0, 0, 1, 1, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 1, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 1, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))
        val window = Window(2, 4, 2, 2, image)
        assertEquals(5, window.getNonZeroPixel())

        window.increaseY()
        assertEquals(3, window.getNonZeroPixel())

        window.decreaseY()
        assertEquals(5, window.getNonZeroPixel())

        window.setY(0)
        assertEquals(1, window.getNonZeroPixel())
    }

    @Test
    fun testGetPixelInWindowWidth() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0), // 1
                intArrayOf(0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 3
        ))
        val window = Window(2, 4, 1, 2, image)
        assertEquals(5, window.getNonZeroPixel())

        window.increaseWidth()
        assertEquals(7, window.getNonZeroPixel())

        window.decreaseWidth()
        assertEquals(5, window.getNonZeroPixel())

        window.setWidth(1)
        assertEquals(1, window.getNonZeroPixel())
    }

    @Test
    fun testGetPixelInWindowHeight() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7
                intArrayOf(0, 0, 0, 1, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 0, 1, 1, 1, 0), // 2
                intArrayOf(0, 1, 0, 0, 1, 1, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 1, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 1, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))
        val window = Window(2, 4, 2, 2, image)
        assertEquals(5, window.getNonZeroPixel())

        window.increaseHeight()
        assertEquals(6, window.getNonZeroPixel())

        window.decreaseHeight()
        assertEquals(5, window.getNonZeroPixel())

        window.setHeight(1)
        assertEquals(3, window.getNonZeroPixel())
    }

    @Test
    fun testEqual() {
        assertTrue(Window(2, 4, 1, 3, BinaryImageEmptyWrapper(10, 10)).equals(Window(2, 4, 1, 3, BinaryImageEmptyWrapper(10, 10))))
        assertFalse(Window(2, 4, 1, 3, BinaryImageEmptyWrapper(10, 10)).equals(Window(2, 4, 1, 2, BinaryImageEmptyWrapper(10, 10))))
    }

    @Test
    fun testToString() {
        val window = Window(1, 2, 3, 4, BinaryImageEmptyWrapper(10, 10))

        assertEquals("x: 1, width: 2, y: 3, height: 4", window.toString())
    }

    private fun createBinaryImage(array: Array<IntArray>): BinaryImage {
        return BinaryImageArrayWrapper(invertTwoDimensionalArray(array))
    }

    private fun invertTwoDimensionalArray(array: Array<IntArray>): Array<IntArray> {
        val invArray = Array(array[0].size) { IntArray(array.size) }
        for (x in 0..array[0].lastIndex)
            for (y in 0..array.lastIndex)
                invArray[x][y] = array[y][x]
        return invArray
    }
}