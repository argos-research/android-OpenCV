package com.argos.android.opencv.lineDetection.windowFinding

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class LaneFinderException(message: String) : Exception(message)


class LaneFinder {
    companion object {
        const val WIDTH_IMAGE = 1280
        const val HEIGHT_IMAGE = 720

        private const val HEIGHT_CROPPED_IMAGE = 370

        private const val WIDTH_WARPED_IMAGE = 720
        private const val HEIGHT_WARPED_IMAGE = 720
    }

    private val mWindowFinder = WindowFinder(64, 64, 16, 128, 20)

    fun getLanes(image: Mat): Mat {
        checkImageSize(image)
        val imageLines = Mat(HEIGHT_IMAGE, WIDTH_IMAGE, CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0))
        drawLines(imageLines, preProcessImage(image))
        return imageLines
    }

    private fun checkImageSize(image: Mat) {
        if (image.width() != WIDTH_IMAGE)
            throw LaneFinderException("Wrong image width")
        if (image.height() != HEIGHT_IMAGE)
            throw LaneFinderException("Wrong image height")
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
        val rectCrop = Rect(0, HEIGHT_IMAGE-HEIGHT_CROPPED_IMAGE, WIDTH_IMAGE, HEIGHT_CROPPED_IMAGE)
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
        val leftTop = Point(600.0, 0.0)
        val rightTop = Point(WIDTH_IMAGE-600.0, 0.0)
        val rightBottom = Point(WIDTH_IMAGE-100.0, HEIGHT_CROPPED_IMAGE.toDouble())
        val leftBottom = Point(100.0, HEIGHT_CROPPED_IMAGE.toDouble())
        return MatOfPoint2f(leftTop, rightTop, rightBottom, leftBottom)
    }

    private fun getDstMatrix(): Mat {
        val leftTop = Point(170.0, 0.0)
        val rightTop = Point(WIDTH_WARPED_IMAGE-170.0, 0.0)
        val rightBottom = Point(WIDTH_WARPED_IMAGE-170.0, HEIGHT_WARPED_IMAGE.toDouble())
        val leftBottom = Point(170.0, HEIGHT_WARPED_IMAGE.toDouble())
        return MatOfPoint2f(leftTop, rightTop, rightBottom, leftBottom)
    }

    private fun drawLines(imageDst: Mat, binaryImage: Mat) {
        try {
            val (windowsLeft, windowsRight) = mWindowFinder.findWindows(BinaryImageMatWrapper(binaryImage, 250))

            val croppedPart = Mat(HEIGHT_WARPED_IMAGE, WIDTH_WARPED_IMAGE, CvType.CV_8UC3, Scalar(0.0,0.0,0.0))
            drawWindows(croppedPart, windowsLeft)
            drawWindows(croppedPart, windowsRight)
            invWarpImage(croppedPart)
            croppedPart.copyTo(imageDst.submat(Rect(0, HEIGHT_IMAGE-HEIGHT_CROPPED_IMAGE, WIDTH_IMAGE, HEIGHT_CROPPED_IMAGE)))
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
