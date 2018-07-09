package com.argos.android.opencv.lineDetection

import com.argos.android.opencv.lineDetection.windowFinding.BinaryImageMatWrapper
import com.argos.android.opencv.lineDetection.windowFinding.NoWindowFoundException
import com.argos.android.opencv.lineDetection.windowFinding.Window
import com.argos.android.opencv.lineDetection.windowFinding.WindowFinder
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.*

class LaneFinderException(message: String) : Exception(message)


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
    }

    private val mWindowFinder = WindowFinder(32, 32, 8, 64, 10)

    fun getLanes(image: Mat): Mat {
        val (imageLanes, _) = getLanesAndBinaryImage(image)
        return imageLanes
    }

    fun getLanesAndBinaryImage(image: Mat): Pair<Mat, Mat> {
        checkImage(image)
        val preProcessedImage = preProcessImage(image)
        val imageLanes = Mat(HEIGHT_IMAGE, WIDTH_IMAGE, CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0,1.0))
        drawLines(imageLanes, preProcessedImage)
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
        Imgproc.threshold(croppedImage, croppedImage, 120.0, 255.0, Imgproc.THRESH_BINARY)
        warpImage(croppedImage)
        Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY)
        return croppedImage
    }

    private fun cropImage(image: Mat): Mat {
        val rectCrop = Rect(0, HEIGHT_IMAGE - HEIGHT_CROPPED_IMAGE, WIDTH_IMAGE, HEIGHT_CROPPED_IMAGE)
        return image.submat(rectCrop)
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

    private fun drawLines(imageDst: Mat, binaryImage: Mat) {

        try {
            val (windowsLeft, windowsRight) = mWindowFinder.findWindows(BinaryImageMatWrapper(binaryImage, 250))
            Imgproc.cvtColor(binaryImage, binaryImage, Imgproc.COLOR_GRAY2BGR)
            drawWindows(binaryImage, windowsRight)
            drawWindows(binaryImage, windowsLeft)

            val croppedPart = Mat(HEIGHT_WARPED_IMAGE, WIDTH_WARPED_IMAGE, CvType.CV_8UC3, Scalar(0.0,0.0,0.0))
            drawWindows(croppedPart, windowsLeft)
            drawWindows(croppedPart, windowsRight)
            invWarpImage(croppedPart)
            croppedPart.copyTo(imageDst.submat(Rect(0, HEIGHT_IMAGE - HEIGHT_CROPPED_IMAGE, WIDTH_IMAGE, HEIGHT_CROPPED_IMAGE)))
        } catch (e: NoWindowFoundException) { }
    }

    private fun drawWindows(image: Mat, windows: ArrayList<Window>) {
        for (window in windows)
        Imgproc.rectangle(
                image,
                Point(window.getX().toDouble(), window.getY().toDouble()),
                Point(window.getBorderRight().toDouble(), window.getBorderBelow().toDouble()),
                Scalar(0.0, 255.0, 0.0),
                5)
    }
}
