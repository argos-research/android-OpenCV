package com.argos.android.opencv.lineDetection

import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs
import kotlin.math.exp


class TestSplineInterpolator {

    @Test
    fun testStraightLine() {
        val x = arrayListOf(0f, 1f, 2f, 4f, 8f)
        val y = arrayListOf(0f, 1f, 2f, 4f, 8f)
        val splineInterpolator = SplineInterpolator.createMonotoneCubicSpline(x, y)
        assertEquals(3f, splineInterpolator.interpolate(3f))
        assertEquals(7f, splineInterpolator.interpolate(7f))
    }

    @Test
    fun testExp() {
        val x = arrayListOf(-4f, -3f, -1f, 0f, 1f)
        val y = arrayListOf(exp(-4f), exp(-3f), exp(-1f), exp(0f), exp(1f))
        val splineInterpolator = SplineInterpolator.createMonotoneCubicSpline(x, y)
        assertTrue(getDifferenceInPercent(exp(-2f), splineInterpolator.interpolate(-2f)) < 0.05)
        assertTrue(getDifferenceInPercent(exp(-0.5f), splineInterpolator.interpolate(-0.5f)) < 0.05)
    }

    private fun getDifferenceInPercent(first: Float, second: Float): Float {
        return abs(first/second -1)
    }

    @Test
    fun testBoundaries() {
        val x = arrayListOf(0f, 1f, 2f)
        val y = arrayListOf(0f, 1f, 2f)
        val splineInterpolator = SplineInterpolator.createMonotoneCubicSpline(x, y)
        assertEquals(2f, splineInterpolator.interpolate(3f))
        assertEquals(0f, splineInterpolator.interpolate(-2f))
    }
}