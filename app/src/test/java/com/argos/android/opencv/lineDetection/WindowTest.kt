package com.argos.android.opencv.lineDetection

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

    @Test(expected = WindowException::class)
    fun testSetXNegative() {
        val window = Window(1, 1, 1, 1)
        window.setX(-1)
    }

    @Test
    fun testSetY() {
        val window = Window(1, 1, 1, 1)
        window.setY(3)

        assertEquals(3, window.getY())
    }

    @Test(expected = WindowException::class)
    fun testSetYNegative() {
        val window = Window(1, 1, 1, 1)
        window.setY(-1)
    }

    @Test
    fun testSetWidth() {
        val window = Window(1, 1, 1,1)
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
}