package com.argos.android.opencv.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.argos.android.opencv.R
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgproc.Imgproc
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DnnActivity2 : AppCompatActivity(), CvCameraViewListener2 {

    // Initialize OpenCV manager.
    private val loader = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    System.loadLibrary("opencv_java3")
                    System.loadLibrary("NativeArgOS")
                    mOpenCvCameraView!!.enableView()
                }

                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    private var net: Net? = null
    private var mOpenCvCameraView: CameraBridgeViewBase? = null

    override fun onResume() {
        super.onResume()

        if (OpenCVLoader.initDebug()) {
            Log.d("DnnActivity", "OpenCV successfully loaded")
            loader.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        } else {
            Log.d("DnnActivity", "OpenCV load failed")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, loader)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dnn)

        // Set up camera listener.
        mOpenCvCameraView = findViewById(R.id.CameraView)
        mOpenCvCameraView!!.visibility = CameraBridgeViewBase.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(this)
    }

    // Load a network.
    override fun onCameraViewStarted(width: Int, height: Int) {
        val config = getPath("ssd_mobilenet_v1_coco_2017_11_17.pbtxt", this)
        val model = getPath("frozen_inference_graph.pb", this)
        net = Dnn.readNetFromTensorflow(model, config)
        Log.i(TAG, "Network loaded successfully")
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        val IN_WIDTH = 300
        val IN_HEIGHT = 300
        val WH_RATIO = IN_WIDTH.toFloat() / IN_HEIGHT
        val IN_SCALE_FACTOR = 0.007843
        val MEAN_VAL = 127.5
        val THRESHOLD = 0.2


        // Get a new frame
        val frame = inputFrame.rgba()

        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB)


        // Forward image through network.
        val blob = Dnn.blobFromImage(frame, IN_SCALE_FACTOR,
                Size(IN_WIDTH.toDouble(), IN_HEIGHT.toDouble()),
                Scalar(MEAN_VAL, MEAN_VAL, MEAN_VAL), false, true)
        net!!.setInput(blob)
        var detections = net!!.forward()

        var cols = frame.cols()
        var rows = frame.rows()

        val cropSize: Size
        cropSize = if (cols.toFloat() / rows > WH_RATIO) {
            Size((rows * WH_RATIO).toDouble(), rows.toDouble())
        } else {
            Size(cols.toDouble(), (cols / WH_RATIO).toDouble())
        }

        val y1 = (rows - cropSize.height).toInt() / 2
        val y2 = (y1 + cropSize.height).toInt()
        val x1 = (cols - cropSize.width).toInt() / 2
        val x2 = (x1 + cropSize.width).toInt()
        val subFrame = frame.submat(y1, y2, x1, x2)

        cols = subFrame.cols()
        rows = subFrame.rows()

        detections = detections.reshape(1, detections.total().toInt() / 7)

        for (i in 0 until detections.rows()) {
            val confidence = detections.get(i, 2)[0]
            if (confidence > THRESHOLD) {
                val classId = detections.get(i, 1)[0].toInt()

                val xLeftBottom = (detections.get(i, 3)[0] * cols).toInt()
                val yLeftBottom = (detections.get(i, 4)[0] * rows).toInt()
                val xRightTop = (detections.get(i, 5)[0] * cols).toInt()
                val yRightTop = (detections.get(i, 6)[0] * rows).toInt()

                // Draw rectangle around detected object.
                Imgproc.rectangle(subFrame, Point(xLeftBottom.toDouble(), yLeftBottom.toDouble()),
                        Point(xRightTop.toDouble(), yRightTop.toDouble()),
                        Scalar(0.0, 255.0, 0.0))
                // val label = classNames[classId] + ": " + confidence
                val label = "classid: " + classId + " confidence: " + confidence
                val baseLine = IntArray(1)
                val labelSize = Imgproc.getTextSize(label, Core.FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine)

                // Draw background for label.
                Imgproc.rectangle(subFrame, Point(xLeftBottom.toDouble(), yLeftBottom - labelSize.height),
                        Point(xLeftBottom + labelSize.width, (yLeftBottom + baseLine[0]).toDouble()),
                        Scalar(255.0, 255.0, 255.0), Core.FILLED)

                // Write class name and confidence.
                Imgproc.putText(subFrame, label, Point(xLeftBottom.toDouble(), yLeftBottom.toDouble()),
                        Core.FONT_HERSHEY_SIMPLEX, 0.5, Scalar(0.0, 0.0, 0.0))
            }
        }
        return frame


        /* val frame = inputFrame.rgba()
         val blob = Dnn.blobFromImage(frame, IN_SCALE_FACTOR,
                 Size(IN_WIDTH.toDouble(), IN_HEIGHT.toDouble()),
                 Scalar(MEAN_VAL, MEAN_VAL, MEAN_VAL), false, true)

         return frame
      */

    }

    override fun onCameraViewStopped() {}

    companion object {

        // Upload file to storage and return a path.
        private fun getPath(file: String, context: Context): String {
            val assetManager = context.assets

            var inputStream: BufferedInputStream? = null
            try {
                // Read data from assets.
                inputStream = BufferedInputStream(assetManager.open(file))
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                inputStream.close()

                // Create copy file in storage.
                val outFile = File(context.filesDir, file)
                val os = FileOutputStream(outFile)
                os.write(data)
                os.close()
                // Return a path to file which may be read in common way.
                return outFile.absolutePath
            } catch (ex: IOException) {
                Log.i(TAG, "Failed to upload a file")
            }

            return ""
        }

        private val TAG = "OpenCV/Sample/MobileNet"
        private val classNames = arrayOf("background", "aeroplane", "bicycle", "bird", "boat", "bottle", "bus", "car", "cat", "chair", "cow", "diningtable", "dog", "horse", "motorbike", "person", "pottedplant", "sheep", "sofa", "train", "tvmonitor")
    }
}
