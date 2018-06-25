package com.argos.android.opencv.lineDetection.windowFinding

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class LaneFinder {
    companion object {
        private const val WIDTH_IMAGE = 1280
        private const val HEIGHT_IMAGE = 720

        private const val HEIGHT_CROPPED_IMAGE = 370

        private const val WIDTH_WARPED_IMAGE = 720
        private const val HEIGHT_WARPED_IMAGE = 720
    }

    private val mWindowFinder = WindowFinder(64, 64, 16, 128, 20)

    fun getLanes(image: Mat): Mat {
        Imgproc.resize(image, image, Size(WIDTH_IMAGE.toDouble(), HEIGHT_IMAGE.toDouble()))
        val croppedImage = cropImage(image)
        Imgproc.GaussianBlur(croppedImage, croppedImage, Size(3.0, 3.0), 0.0)
        Imgproc.threshold(croppedImage, croppedImage, 120.0, 255.0, Imgproc.THRESH_BINARY)
        warpImage(croppedImage)
        Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY)
        try {
            val (windowsLeft, windowsRight) = mWindowFinder.findWindows(BinaryImageMatWrapper(croppedImage, 250))
            drawWindows(croppedImage, windowsLeft)
            drawWindows(croppedImage, windowsRight)
        } catch (e: NoWindowFoundException) { }
        // create image with lines
        // inv warp
        // un crop
        Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_GRAY2BGR)
        return croppedImage
    }

    private fun cropImage(image: Mat): Mat {
        val rectCrop = Rect(0, HEIGHT_IMAGE-HEIGHT_CROPPED_IMAGE, WIDTH_IMAGE, HEIGHT_CROPPED_IMAGE)
        return image.submat(rectCrop)
    }

    private fun warpImage(image: Mat) {
        val m = Imgproc.getPerspectiveTransform(getSrcMatrix(), getDstMatrix())
        Imgproc.warpPerspective(image, image, m, Size(720.0, 720.0))
    }

    private fun invWarpImage(image: Mat) {
        val m = Imgproc.getPerspectiveTransform(getSrcMatrix(), getDstMatrix())
        Imgproc.warpPerspective(image, image, m, Size(720.0, 720.0))
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

    private fun drawWindows(image: Mat, windows: ArrayList<Window>) {
        for (window in windows)
        Imgproc.rectangle(
                image,
                Point(window.getX().toDouble(), window.getY().toDouble()),
                Point(window.getBorderRight().toDouble(), window.getBorderBelow().toDouble()),
                Scalar(255.0, 255.0, 0.0))
    }
}
