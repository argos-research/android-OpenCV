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
        val windowOptimizer = WindowOptimizer(image)
        val windowActual = Window(3, 1, 0, 4)
        windowOptimizer.maximizeWindowWidth(windowActual)

        assertTrue(Window(1, 4, 0, 4).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 9, 4, 2)
        windowOptimizer.maximizeWindowWidth(windowActual)

        assertTrue(Window(1, 2, 2, 2).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(2,2, 2, 3)
        windowOptimizer.maximizeWindowWidth(windowActual)

        val windowExpected = Window(0, 4, 2, 3)

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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(7,2, 2, 3)
        windowOptimizer.maximizeWindowWidth(windowActual)

        val windowExpected = Window(7, 4, 2, 3)

        assertTrue(windowExpected.equals(windowActual))
    }

    @Test
    fun testMaximizeWindowWidthEnlargeLeft() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6  7  8  9  0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 1
                intArrayOf(0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0), // 2
                intArrayOf(0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 4
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 5
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)  // 6
        ))
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(5, 2, 0, 4)
        windowOptimizer.maximizeWindowWidthEnlargeLeft(windowActual)

        assertTrue(Window(1, 6, 0, 4).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 9, 4, 2)
        windowOptimizer.maximizeWindowWidthEnlargeLeft(windowActual)

        assertTrue(Window(1, 2, 2, 2).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(2,2, 2, 3)
        windowOptimizer.maximizeWindowWidthEnlargeLeft(windowActual)

        val windowExpected = Window(0, 4, 2, 3)

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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 2, 0, 4)
        windowOptimizer.maximizeWindowWidthEnlargeRight(windowActual)

        assertTrue(Window(0, 5, 0, 4).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 9, 4, 2)
        windowOptimizer.maximizeWindowWidthEnlargeRight(windowActual)
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(6,2, 2, 3)
        windowOptimizer.maximizeWindowWidthEnlargeRight(windowActual)

        val windowExpected = Window(6, 4, 2, 3)

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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(3,3, 5, 1)
        windowOptimizer.maximizeWindowHeightEnlargeAbove(windowActual)

        val windowExpected = Window(3, 3, 2, 4)

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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(2,2, 2, 3)
        windowOptimizer.maximizeWindowHeightEnlargeAbove(windowActual)
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(2,2, 3, 3)
        windowOptimizer.maximizeWindowHeightEnlargeAbove(windowActual)

        val windowExpected = Window(2, 2, 0, 6)

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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 10, 0, 3)
        windowOptimizer.minimizeWindowWidth(windowActual)

        assertTrue(Window(2, 5, 0, 3).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 9, 4, 2)
        windowOptimizer.minimizeWindowWidth(windowActual)

        assertTrue(Window(1, 2, 2, 2).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 10, 0, 3)
        windowOptimizer.minimizeWindowWidthDecreaseLeft(windowActual)

        assertTrue(Window(2, 8, 0, 3).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 9, 4, 2)
        windowOptimizer.minimizeWindowWidthDecreaseLeft(windowActual)

        assertTrue(Window(1, 2, 2, 2).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 10, 0, 6)
        windowOptimizer.minimizeWindowWidthDecreaseRight(windowActual)

        assertTrue(Window(0, 5, 0, 6).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(0, 9, 4, 2)
        windowOptimizer.minimizeWindowWidthDecreaseRight(windowActual)

        assertTrue(Window(1, 2, 2, 2).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(1, 2, 0, 8)
        windowOptimizer.minimizeWindowHeight(windowActual)

        assertTrue(Window(1, 2, 2, 2).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(4, 2, 2, 8)
        windowOptimizer.minimizeWindowHeight(windowActual)
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(4, 2, 0, 8)
        windowOptimizer.minimizeWindowHeightDecreaseAbove(windowActual)

        assertTrue(Window(4, 2, 2, 6).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(4, 2, 2, 8)
        windowOptimizer.minimizeWindowHeightDecreaseAbove(windowActual)
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(3, 2, 2, 8)
        windowOptimizer.minimizeWindowHeightDecreaseBelow(windowActual)

        assertTrue(Window(3, 2, 2, 1).equals(windowActual))
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
        val windowOptimizer = WindowOptimizer(image)

        val windowActual = Window(4, 2, 2, 8)
        windowOptimizer.minimizeWindowHeightDecreaseBelow(windowActual)
    }

    @Test
    fun testCountPixelInWindow() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5
                intArrayOf(0, 1, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 1, 0, 0), // 1
                intArrayOf(0, 1, 0, 1, 0, 0), // 2
                intArrayOf(0, 1, 0, 0, 0, 0), // 3
                intArrayOf(0, 1, 0, 0, 0, 0)  // 4
        ))
        val windowOptimizer = WindowOptimizer(image)

        assertEquals(6, windowOptimizer.countPixelInWindow(Window(0, 6, 0, 5)))
        assertEquals(4, windowOptimizer.countPixelInWindow(Window(1, 3, 2, 3)))
        assertEquals(0, windowOptimizer.countPixelInWindow(Window(5, 1, 4, 1)))
    }

    @Test
    fun testCountPixelInColumn() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6
                intArrayOf(0, 1, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 1, 0, 0, 0), // 1
                intArrayOf(0, 1, 0, 1, 0, 0, 0), // 2
                intArrayOf(0, 1, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 1, 0, 0, 0, 0, 0)  // 4
        ))
        val windowOptimizer = WindowOptimizer(image)

        assertEquals(4, windowOptimizer.countPixelInColumn(1, 0, 4))
        assertEquals(3, windowOptimizer.countPixelInColumn(1, 1, 4))
        assertEquals(0, windowOptimizer.countPixelInColumn(0, 1, 4))
    }

    @Test
    fun testCountPixelInRow() {
        val image = createBinaryImage(arrayOf(
                //         0  1  2  3  4  5  6
                intArrayOf(0, 0, 0, 0, 0, 0, 0), // 0
                intArrayOf(0, 0, 0, 1, 0, 0, 0), // 1
                intArrayOf(0, 1, 0, 1, 0, 1, 0), // 2
                intArrayOf(0, 1, 0, 0, 0, 0, 0), // 3
                intArrayOf(0, 1, 0, 0, 0, 0, 0)  // 4
        ))
        val windowOptimizer = WindowOptimizer(image)

        assertEquals(1, windowOptimizer.countPixelInRow(1, 0, 4))
        assertEquals(2, windowOptimizer.countPixelInRow(2, 1, 4))
        assertEquals(0, windowOptimizer.countPixelInRow(0, 0, 4))
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
