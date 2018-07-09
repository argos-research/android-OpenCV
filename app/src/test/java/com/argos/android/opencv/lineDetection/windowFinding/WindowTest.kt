package com.argos.android.opencv.lineDetection.windowFinding

import org.junit.Test
import org.junit.Assert.*
import org.opencv.core.Point

class WindowTest {

    @Test
    fun testNewWindow() {
        val window = Window(0, 1, 2, 3)

        assertEquals(0, window.getX())
        assertEquals(1, window.getWidth())
        assertEquals(2, window.getY())
        assertEquals(3, window.getHeight())
    }

    @Test
    fun testSetX() {
        val window = Window(1, 1, 1, 1)
        window.setX(3)

        assertEquals(3, window.getX())
    }

    @Test
    fun testIncreaseX() {
        val window = Window(2, 1, 1, 1)
        window.increaseX()

        assertEquals(3, window.getX())
    }

    @Test
    fun testDecreaseX() {
        val window = Window(3, 1, 1, 1)
        window.decreaseX()

        assertEquals(2, window.getX())
    }

    @Test
    fun testSetY() {
        val window = Window(1, 1, 1, 1)
        window.setY(3)

        assertEquals(3, window.getY())
    }

    @Test
    fun testIncreaseY() {
        val window = Window(1, 1, 2, 1)
        window.increaseY()

        assertEquals(3, window.getY())
    }

    @Test
    fun testDecreaseY() {
        val window = Window(1, 1, 3, 1)
        window.decreaseY()

        assertEquals(2, window.getY())
    }

    @Test
    fun testSetWidth() {
        val window = Window(1, 1, 1, 1)
        window.setWidth(3)

        assertEquals(3, window.getWidth())
    }

    @Test(expected = WindowException::class)
    fun testSetWidthZero() {
        val window = Window(1, 1, 1, 1)
        window.setWidth(0)
    }

    @Test(expected = WindowException::class)
    fun testSetWidthNegative() {
        val window = Window(1, 1, 1, 1)
        window.setWidth(-1)
    }

    @Test
    fun testIncreaseWidth() {
        val window = Window(1, 2, 1, 1)
        window.increaseWidth()

        assertEquals(3, window.getWidth())
    }

    @Test
    fun testDecreaseWidth() {
        val window = Window(1, 3, 1, 1)
        window.decreaseWidth()

        assertEquals(2, window.getWidth())
    }

    @Test(expected = WindowException::class)
    fun testSetHeightZero() {
        val window = Window(1, 1, 1, 1)
        window.setHeight(0)
    }

    @Test(expected = WindowException::class)
    fun testSetHeightNegative() {
        val window = Window(1, 1, 1, 1)
        window.setHeight(-1)
    }

    @Test
    fun testIncreaseHeight() {
        val window = Window(1, 1, 1, 2)
        window.increaseHeight()

        assertEquals(3, window.getHeight())
    }

    @Test
    fun testDecreaseHeight() {
        val window = Window(1, 1, 1, 3)
        window.decreaseHeight()

        assertEquals(2, window.getHeight())
    }

    @Test
    fun testGetMidpoint() {
        val window = Window(2, 4, 1, 3)
        assertEquals(Point(4.0, 2.5), window.getMidpoint())
    }

    @Test
    fun testGetMidpointX() {
        val window = Window(2, 4, 1, 3)
        assertEquals(4, window.getMidpointX())
    }

    @Test
    fun testGetMidpointY() {
        val window = Window(2, 4, 1, 3)
        assertEquals(2, window.getMidpointY())
    }

    @Test
    fun testGetBorderRight() {
        val window = Window(2, 2, 1, 1)
        assertEquals(3, window.getBorderRight())
    }

    @Test
    fun testGetBorderBelow() {
        val window = Window(2, 2, 3, 3)
        assertEquals(5, window.getBorderBelow())
    }


    @Test
    fun testSplitWindowInHeightEven() {
        val window = Window(0, 2, 0, 10)

        val expectedUpperWindow = Window(0, 2, 0, 5)
        val expectedLowerWindow = Window(0, 2, 5, 5)
        val (actualUpperWindow, actualLowerWindow) = window.splitWindowInHeight()

        assertTrue(expectedUpperWindow.equals(actualUpperWindow))
        assertTrue(expectedLowerWindow.equals(actualLowerWindow))
    }

    @Test
    fun testSplitWindowInHeightOdd() {
        val window = Window(0, 2, 0, 11)

        val expectedUpperWindow = Window(0, 2, 0, 5)
        val expectedLowerWindow = Window(0, 2, 5, 6
        )
        val (actualUpperWindow, actualLowerWindow) = window.splitWindowInHeight()

        assertTrue(expectedUpperWindow.equals(actualUpperWindow))
        assertTrue(expectedLowerWindow.equals(actualLowerWindow))
    }

    @Test
    fun testEqual() {
        assertTrue(Window(2, 4, 1, 3).equals(Window(2, 4, 1, 3)))
        assertFalse(Window(2, 4, 1, 3).equals(Window(2, 4, 1, 2)))
    }

    @Test
    fun testToString() {
        val window = Window(1, 2, 3, 4)

        assertEquals("x: 1, width: 2, y: 3, height: 4", window.toString())
    }
}