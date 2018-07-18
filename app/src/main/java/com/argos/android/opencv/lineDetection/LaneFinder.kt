package com.argos.android.opencv.lineDetection

import com.argos.android.opencv.lineDetection.windowFinding.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.collections.ArrayList

class LaneFinderException(message: String) : Exception(message)

/*
 ToDo: Auf einigen Tracks sind die Abstände bei den Linen weiter entfernt. Neue Justierung überlegen
 ToDo: Im Camera-Modus gibt es einen Fehler, wenn man den Screen sperrt und dann wieder in die App geht
 ToDo: Canny-Edge-Detection verwenden, da es bei stark verschmutzten strecken schwer ist etwas zu erkennen mit Threshold
 */
class LaneFinder {
    companion object {
        const val WIDTH_IMAGE = 640
        const val HEIGHT_IMAGE = 360

        private const val HEIGHT_CROPPED_IMAGE = 185

        private const val WIDTH_WARPED_IMAGE = 360
        private const val HEIGHT_WARPED_IMAGE = 360

        private const val WARPED_SRC_TOP = 300
        private const val WARPED_SRC_BOTTOM = 50
        private const val WARPED_DST_TOP = 85
        private const val WARPED_DST_BOTTOM = 85

        private const val WIDTH_RECT_THRESH = 181
        private const val HEIGHT_RECT_THRESH = 101

        private const val THRESH_OVER_AVG = 10
    }

    private val mWindowFinder = WindowFinder(32, 32, 8, 64, 10, 64)

    fun getLanesAndBinaryImage(image: Mat): Pair<Mat, Mat> {
        checkImage(image)
        val preProcessedImage = preProcessImage(image)
        val imageLanes = Mat(HEIGHT_IMAGE, WIDTH_IMAGE, CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0, 1.0))
        try {
            val windows = mWindowFinder.findWindows(BinaryImageMatWrapper(preProcessedImage.clone(), 250))
            drawLinesOnPreprocessedImage(preProcessedImage, windows)
            drawWindows(preProcessedImage, windows.first)
            drawWindows(preProcessedImage, windows.second)
            drawLinesOnBlackOriginalImage(imageLanes, windows)
        } catch (e: NoWindowFoundException) {
            Imgproc.cvtColor(preProcessedImage, preProcessedImage, Imgproc.COLOR_GRAY2BGR)
        }
        return Pair(imageLanes, preProcessedImage)
    }

    private fun checkImage(image: Mat) {
        if (image.width() != WIDTH_IMAGE)
            throw LaneFinderException("Wrong image width")
        if (image.height() != HEIGHT_IMAGE)
            throw LaneFinderException("Wrong image height")
        if (!(image.type() == CvType.CV_8UC3 || image.type() == CvType.CV_8UC4))
            throw LaneFinderException("Wrong image type")
    }

    private fun preProcessImage(image: Mat): Mat {
        val croppedImage = cropImage(image)
        Imgproc.GaussianBlur(croppedImage, croppedImage, Size(3.0, 3.0), 0.0)
        warpImage(croppedImage)
        Imgproc.threshold(croppedImage, croppedImage, getThreshValue(croppedImage), 255.0, Imgproc.THRESH_BINARY)
        Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY)
        return croppedImage
    }

    private fun cropImage(image: Mat): Mat {
        val rectCrop = Rect(0, HEIGHT_IMAGE - HEIGHT_CROPPED_IMAGE, WIDTH_IMAGE, HEIGHT_CROPPED_IMAGE)
        return image.submat(rectCrop)
    }

    private fun getThreshValue(image: Mat): Double {
        val values = ArrayList<Double>()
        val subImage = image.submat(Rect(WIDTH_WARPED_IMAGE/2 - WIDTH_RECT_THRESH/2, HEIGHT_WARPED_IMAGE- HEIGHT_RECT_THRESH, WIDTH_RECT_THRESH, HEIGHT_RECT_THRESH))
        for (x in 0..(subImage.width()-1) step 10)
            for (y in 0..(subImage.height()-1) step 10)
                values.add(subImage.get(y, x).average())

        values.sort()
        for (i in 0..values.size/30)
            values.removeAt(values.lastIndex)
        for (i in 0..values.size*3/4)
            values.removeAt(0)

        return values.average() + THRESH_OVER_AVG
    }

    private fun warpImage(image: Mat) {
        val m = Imgproc.getPerspectiveTransform(getSrcMatrix(), getDstMatrix())
        Imgproc.warpPerspective(image, image, m, Size(WIDTH_WARPED_IMAGE.toDouble(), HEIGHT_WARPED_IMAGE.toDouble()))
    }

    private fun invWarpImage(image: Mat) {
        val m = Imgproc.getPerspectiveTransform(getDstMatrix(), getSrcMatrix())
        Imgproc.warpPerspective(image, image, m, Size(WIDTH_IMAGE.toDouble(), HEIGHT_CROPPED_IMAGE.toDouble()))
    }

    private fun getSrcMatrix(): Mat {
        val leftTop = Point(WARPED_SRC_TOP.toDouble(), 0.0)
        val rightTop = Point((WIDTH_IMAGE - WARPED_SRC_TOP).toDouble(), 0.0)
        val rightBottom = Point((WIDTH_IMAGE - WARPED_SRC_BOTTOM).toDouble(), HEIGHT_CROPPED_IMAGE.toDouble())
        val leftBottom = Point(WARPED_SRC_BOTTOM.toDouble(), HEIGHT_CROPPED_IMAGE.toDouble())
        return MatOfPoint2f(leftTop, rightTop, rightBottom, leftBottom)
    }

    private fun getDstMatrix(): Mat {
        val leftTop = Point(WARPED_DST_TOP.toDouble(), 0.0)
        val rightTop = Point((WIDTH_WARPED_IMAGE - WARPED_DST_TOP).toDouble(), 0.0)
        val rightBottom = Point((WIDTH_WARPED_IMAGE - WARPED_DST_BOTTOM).toDouble(), HEIGHT_WARPED_IMAGE.toDouble())
        val leftBottom = Point(WARPED_DST_BOTTOM.toDouble(), HEIGHT_WARPED_IMAGE.toDouble())
        return MatOfPoint2f(leftTop, rightTop, rightBottom, leftBottom)
    }

    private fun drawLinesOnPreprocessedImage(preProcessedImage: Mat, windows: Pair<ArrayList<Window>, ArrayList<Window>>) {
        val croppedPart = Mat(HEIGHT_WARPED_IMAGE, WIDTH_WARPED_IMAGE, CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0))
        drawLane(croppedPart, windows.first, windows.second)

        Imgproc.cvtColor(preProcessedImage, preProcessedImage, Imgproc.COLOR_GRAY2BGR)
        Core.addWeighted(preProcessedImage, 1.0, croppedPart, 0.7, 0.0, preProcessedImage)
    }

    private fun drawLinesOnBlackOriginalImage(image: Mat, windows: Pair<ArrayList<Window>, ArrayList<Window>>) {
        val croppedPart = Mat(HEIGHT_WARPED_IMAGE, WIDTH_WARPED_IMAGE, CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0))
        drawLane(croppedPart, windows.first, windows.second)
        invWarpImage(croppedPart)
        croppedPart.copyTo(image.submat(Rect(0, HEIGHT_IMAGE - HEIGHT_CROPPED_IMAGE, WIDTH_IMAGE, HEIGHT_CROPPED_IMAGE)))
    }

    private fun drawLane(image: Mat, windowsLeft: ArrayList<Window>, windowsRight: ArrayList<Window>) {
        val lineLeft = getLine(windowsLeft)
        lineLeft.sortBy { point -> point.y }
        val lineRight = getLine(windowsRight)
        lineRight.sortByDescending { point -> point.y }
        val points = MatOfPoint(*(lineLeft+lineRight).toTypedArray())
        try { Imgproc.fillConvexPoly(image, points, Scalar(0.0, 255.0, 0.0)) } catch (e: CvException) {}
    }

    private fun getLine(windows: ArrayList<Window>): ArrayList<Point> {
        val points = getPoints(windows)
        val line = ArrayList<Point>()
        if (points.size >= 2) {
            val x = points.map { point -> point.x.toFloat() }
            val y = points.map { point -> point.y.toFloat() }
            val cubicSpline = SplineInterpolator.createMonotoneCubicSpline(y, x)
            for (i in (y.min()!!.toInt()..y.max()!!.toInt()))
                line.add(Point(cubicSpline.interpolate(i.toFloat()).toDouble(), i.toDouble()))
            line.add(Point(cubicSpline.interpolate(y.max()!!.toFloat()).toDouble(), HEIGHT_WARPED_IMAGE.toDouble()))
        }
        return line
    }

    private fun getPoints(windows: ArrayList<Window>): ArrayList<Point> {
        windows.sortBy { window -> window.getMidpointY() }
        val points = ArrayList<Point>()

        for (i in 0..(windows.lastIndex-1))
            points.add(windows[i].getMidpoint())

        try { points.add(windows[windows.lastIndex].getMidpointAbove()) } catch (e: WindowException) { }
        points.add(windows[windows.lastIndex].getMidpoint())
        try { points.add(windows[windows.lastIndex].getMidpointBelow()) } catch (e: WindowException) { }

        return points
    }

    private fun drawWindows(image: Mat, windows: ArrayList<Window>) {
        for (window in windows)
            Imgproc.rectangle(
                    image,
                    Point(window.getX().toDouble(), window.getY().toDouble()),
                    Point(window.getBorderRight().toDouble(), window.getBorderBelow().toDouble()),
                    Scalar(0.0, 0.0, 255.0))
    }
}
