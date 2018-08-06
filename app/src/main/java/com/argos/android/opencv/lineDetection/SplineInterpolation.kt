package com.argos.android.opencv.lineDetection

/*
 * https://gist.github.com/lecho/7627739
 */

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Performs spline interpolation given a set of control points.
 *
 */
class SplineInterpolator private constructor(private val mX: List<Float>, private val mY: List<Float>, private val mM: FloatArray) {

    /**
     * Interpolates the value of Y = f(X) for given X. Clamps X to the domain of the spline.
     *
     * @param x
     * The X value.
     * @return The interpolated Y = f(X) value.
     */
    fun interpolate(x: Float): Float {
        // Handle the boundary cases.
        val n = mX.size
        if (java.lang.Float.isNaN(x)) {
            return x
        }
        if (x <= mX[0]) {
            return mY[0]
        }
        if (x >= mX[n - 1]) {
            return mY[n - 1]
        }

        // Find the index 'i' of the last point with smaller X.
        // We know this will be within the spline due to the boundary tests.
        var i = 0
        while (x >= mX[i + 1]) {
            i += 1
            if (x == mX[i]) {
                return mY[i]
            }
        }

        // Perform cubic Hermite spline interpolation.
        val h = mX[i + 1] - mX[i]
        val t = (x - mX[i]) / h
        return (mY[i] * (1 + 2 * t) + h * mM[i] * t) * (1 - t) * (1 - t) + (mY[i + 1] * (3 - 2 * t) + h * mM[i + 1] * (t - 1)) * t * t
    }

    // For debugging.
    override fun toString(): String {
        val str = StringBuilder()
        val n = mX.size
        str.append("[")
        for (i in 0 until n) {
            if (i != 0) {
                str.append(", ")
            }
            str.append("(").append(mX[i])
            str.append(", ").append(mY[i])
            str.append(": ").append(mM[i]).append(")")
        }
        str.append("]")
        return str.toString()
    }

    companion object {

        /**
         * Creates a monotone cubic spline from a given set of control points.
         *
         * The spline is guaranteed to pass through each control point exactly. Moreover, assuming the control points are
         * monotonic (Y is non-decreasing or non-increasing) then the interpolated values will also be monotonic.
         *
         * This function uses the Fritsch-Carlson method for computing the spline parameters.
         * http://en.wikipedia.org/wiki/Monotone_cubic_interpolation
         *
         * @param x
         * The X component of the control points, strictly increasing.
         * @param y
         * The Y component of the control points
         * @return
         *
         * @throws IllegalArgumentException
         * if the X or Y arrays are null, have different lengths or have fewer than 2 values.
         */
        fun createMonotoneCubicSpline(x: List<Float>?, y: List<Float>?): SplineInterpolator {
            if (x == null || y == null || x.size != y.size || x.size < 2) {
                throw IllegalArgumentException("There must be at least two control " + "points and the arrays must be of equal length.")
            }

            val n = x.size
            val d = FloatArray(n - 1) // could optimize this out
            val m = FloatArray(n)

            // Compute slopes of secant lines between successive points.
            for (i in 0 until n - 1) {
                val h = x[i + 1] - x[i]
                if (h <= 0f) {
                    throw IllegalArgumentException("The control points must all " + "have strictly increasing X values.")
                }
                d[i] = (y[i + 1] - y[i]) / h
            }

            // Initialize the tangents as the average of the secants.
            m[0] = d[0]
            for (i in 1 until n - 1) {
                m[i] = (d[i - 1] + d[i]) * 0.5f
            }
            m[n - 1] = d[n - 2]

            // Update the tangents to preserve monotonicity.
            for (i in 0 until n - 1) {
                if (d[i] == 0f) { // successive Y values are equal
                    m[i] = 0f
                    m[i + 1] = 0f
                } else {
                    val a = m[i] / d[i]
                    val b = m[i + 1] / d[i]
                    val h = Math.hypot(a.toDouble(), b.toDouble()).toFloat()
                    if (h > 9f) {
                        val t = 3f / h
                        m[i] = t * a * d[i]
                        m[i + 1] = t * b * d[i]
                    }
                }
            }
            return SplineInterpolator(x, y, m)
        }
    }
}