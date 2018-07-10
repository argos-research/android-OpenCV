package com.argos.android.opencv.lineDetection.windowFinding

import org.junit.Test
import org.junit.Assert.*


class WindowOptimizerTest {

    @Test
    fun testMaximizeWindowWidth() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0), // 1
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))
        val windowActual = Window(3, 1, 0, 4, image)
        maximizeWindowWidth(windowActual)

        assertTrue(Window(1, 4, 0, 4, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMaximizeWindowWidthNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(0, 9, 4, 2, image)
        maximizeWindowWidth(windowActual)

        assertTrue(Window(1, 2, 2, 2, image).equals(windowActual))
    }

    @Test
    fun testMaximizeWindowWidthBorderConditionsLeft() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))
        val windowActual = Window(2,2, 2, 3, image)
        maximizeWindowWidth(windowActual)

        val windowExpected = Window(0, 4, 2, 3, image)

        assertTrue(windowExpected.equals(windowActual))
    }

    @Test
    fun testMaximizeWindowWidthBorderConditionsRight() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))
        val windowActual = Window(7,2, 2, 3, image)
        maximizeWindowWidth(windowActual)

        val windowExpected = Window(7, 4, 2, 3, image)

        assertTrue(windowExpected.equals(windowActual))
    }

    @Test
    fun testMaximizeWindowWidthEnlargeLeft() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(5, 2, 0, 4, image)
        maximizeWindowWidthEnlargeLeft(windowActual)

        assertTrue(Window(3, 4, 0, 4, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMaximizeWindowWidthEnlargeLeftNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(0, 9, 4, 2, image)
        maximizeWindowWidthEnlargeLeft(windowActual)

        assertTrue(Window(1, 2, 2, 2, image).equals(windowActual))
    }

    @Test
    fun testMaximizeWindowWidthEnlargeLeftBorderConditions() {
        val image = createBinaryImage(arrayOf(
            //         0  1  2  3  4  5  6  7  8  9  0
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 2
            intArrayOf(1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 3
            intArrayOf(1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 4
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(2,2, 2, 3, image)
        maximizeWindowWidthEnlargeLeft(windowActual)

        val windowExpected = Window(0, 4, 2, 3, image)

        assertTrue(windowExpected.equals(windowActual))
    }

    @Test
    fun testMaximizeWindowWidthEnlargeRight() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0), // 1
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(0, 2, 0, 4, image)
        maximizeWindowWidthEnlargeRight(windowActual)

        assertTrue(Window(0, 5, 0, 4, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMaximizeWindowWidthEnlargeRightNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(0, 9, 4, 2, image)
        maximizeWindowWidthEnlargeRight(windowActual)
    }

    @Test
    fun testMaximizeWindowWidthEnlargeRightBorderConditions() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 1, 1, 1), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 1, 1, 1), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 5
        ))

        val windowActual = Window(6,2, 2, 3, image)
        maximizeWindowWidthEnlargeRight(windowActual)

        val windowExpected = Window(6, 4, 2, 3, image)

        assertTrue(windowExpected.equals(windowActual))
    }

    @Test
    fun testMaximizeWindowHeightEnlargeAbove() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(3,3, 5, 1, image)
        maximizeWindowHeightEnlargeAbove(windowActual)

        val windowExpected = Window(3, 3, 2, 4, image)

        assertTrue(windowExpected.equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMaximizeWindowHeightEnlargeAboveNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(2,2, 2, 3, image)
        maximizeWindowHeightEnlargeAbove(windowActual)
    }

    @Test
    fun testMaximizeWindowHeightEnlargeAboveBorderConditions() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))

        val windowActual = Window(2,2, 3, 3, image)
        maximizeWindowHeightEnlargeAbove(windowActual)

        val windowExpected = Window(2, 2, 0, 6, image)

        assertTrue(windowExpected.equals(windowActual))
    }

    @Test
    fun testMinimizeWindowWidth() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 1, 1, 1, 0, 1, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 5
        ))

        val windowActual = Window(0, 10, 0, 3, image)
        minimizeWindowWidth(windowActual)

        assertTrue(Window(2, 5, 0, 3, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMinimizeWindowWidthNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 1, 1, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 5
        ))

        val windowActual = Window(0, 9, 4, 2, image)
        minimizeWindowWidth(windowActual)

        assertTrue(Window(1, 2, 2, 2, image).equals(windowActual))
    }

    @Test
    fun testMinimizeWindowWidthDecreaseLeft() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 1, 1, 1, 0, 1, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 5
        ))

        val windowActual = Window(0, 10, 0, 3, image)
        minimizeWindowWidthDecreaseLeft(windowActual)

        assertTrue(Window(2, 8, 0, 3, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMinimizeWindowWidthDecreaseLeftNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 1, 1, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 5
        ))

        val windowActual = Window(0, 9, 4, 2, image)
        minimizeWindowWidthDecreaseLeft(windowActual)

        assertTrue(Window(1, 2, 2, 2, image).equals(windowActual))
    }

    @Test
    fun testMinimizeWindowWidthDecreaseRight() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 1, 1, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 5
        ))

        val windowActual = Window(0, 10, 0, 6, image)
        minimizeWindowWidthDecreaseRight(windowActual)

        assertTrue(Window(0, 5, 0, 6, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMinimizeWindowWidthDecreaseRightNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 1, 1, 0, 0, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 5
        ))

        val windowActual = Window(0, 9, 4, 2, image)
        minimizeWindowWidthDecreaseRight(windowActual)

        assertTrue(Window(1, 2, 2, 2, image).equals(windowActual))
    }

    @Test
    fun testMinimizeWindowHeight() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5
                intArrayOf(0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 1, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0), // 6
                intArrayOf(0, 0, 0, 0, 0, 0), // 7
                intArrayOf(0, 0, 0, 0, 0, 0), // 8
                intArrayOf(0, 0, 0, 0, 0, 0)  // 9
        ))

        val windowActual = Window(1, 2, 0, 8, image)
        minimizeWindowHeight(windowActual)

        assertTrue(Window(1, 2, 2, 2, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMinimizeWindowHeightNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5
                intArrayOf(0, 0, 0, 0, 0, 0), // 0
                intArrayOf(1, 0, 1, 1, 1, 0), // 1
                intArrayOf(0, 1, 1, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0), // 6
                intArrayOf(0, 0, 0, 0, 0, 0), // 7
                intArrayOf(0, 0, 0, 0, 0, 0), // 8
                intArrayOf(0, 0, 0, 0, 0, 0)  // 9
        ))
        val windowActual = Window(4, 2, 2, 8, image)
        minimizeWindowHeight(windowActual)
    }

    @Test
    fun testMinimizeWindowHeightDecreaseAbove() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5
                intArrayOf(0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0), // 1
                intArrayOf(1, 0, 1, 1, 1, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0), // 6
                intArrayOf(0, 0, 0, 0, 0, 0), // 7
                intArrayOf(0, 0, 0, 0, 0, 0), // 8
                intArrayOf(0, 0, 0, 0, 0, 0)  // 9
        ))
        val windowActual = Window(4, 2, 0, 8, image)
        minimizeWindowHeightDecreaseAbove(windowActual)

        assertTrue(Window(4, 2, 2, 6, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMinimizeWindowHeightDecreaseAboveNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5
                intArrayOf(0, 0, 0, 0, 0, 0), // 0
                intArrayOf(1, 0, 1, 1, 1, 0), // 1
                intArrayOf(0, 1, 1, 0, 0, 0), // 2
                intArrayOf(0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0), // 6
                intArrayOf(0, 0, 0, 0, 0, 0), // 7
                intArrayOf(0, 0, 0, 0, 0, 0), // 8
                intArrayOf(0, 0, 0, 0, 0, 0)  // 9
        ))
        val windowActual = Window(4, 2, 2, 8, image)
        minimizeWindowHeightDecreaseAbove(windowActual)
    }

    @Test
    fun testMinimizeWindowHeightDecreaseBelow() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5
                intArrayOf(0, 1, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 1, 0, 0), // 1
                intArrayOf(0, 1, 0, 1, 0, 0), // 2
                intArrayOf(0, 1, 0, 0, 0, 0), // 3
                intArrayOf(0, 1, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0), // 6
                intArrayOf(0, 0, 0, 0, 0, 0), // 7
                intArrayOf(0, 0, 0, 0, 0, 0), // 8
                intArrayOf(0, 0, 0, 0, 0, 0)  // 9
        ))
        val windowActual = Window(3, 2, 2, 8, image)
        minimizeWindowHeightDecreaseBelow(windowActual)

        assertTrue(Window(3, 2, 2, 1, image).equals(windowActual))
    }

    @Test(expected = NoWindowFoundException::class)
    fun testMinimizeWindowHeightDecreaseBelowNoWindowFound() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5
                intArrayOf(0, 1, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 1, 0, 0), // 1
                intArrayOf(0, 1, 0, 1, 0, 0), // 2
                intArrayOf(0, 1, 0, 0, 0, 0), // 3
                intArrayOf(0, 1, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0), // 6
                intArrayOf(0, 0, 0, 0, 0, 0), // 7
                intArrayOf(0, 0, 0, 0, 0, 0), // 8
                intArrayOf(0, 0, 0, 0, 0, 0)  // 9
        ))
        val windowActual = Window(4, 2, 2, 8, image)
        minimizeWindowHeightDecreaseBelow(windowActual)
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
