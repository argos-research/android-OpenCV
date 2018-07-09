package com.argos.android.opencv.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import com.argos.android.opencv.R
import com.argos.android.opencv.driving.DnnHelper
import com.argos.android.opencv.lineDetection.windowFinding.LaneFinder
import com.argos.android.opencv.model.Feature
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.IOException

/**
 * Activity to run the OpenCV algorithm on images
 * To add new images:
 * 1) Add images inside the drawable directory
 * 2) Include drawables in the images array [com.argos.android.opencv.fragment.ChooseImageDialogFragment.images]
 */
class ImageLoadActivity : AppCompatActivity() {
    private var image: Mat? = null
    private var imageView: ImageView? = null

    @DrawableRes
    private var imageRes: Int = 0
    private var feature: String? = null
    private var cascadeFilePath: String? = null

    private var dnnHelper: DnnHelper = DnnHelper()
    private var laneFinder = LaneFinder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_load)

        loadLibraries()
        initExtras()
        initView()
        processImage()
        setImage()
    }

    private fun loadLibraries() {
        System.loadLibrary("opencv_java3")
    }

    private fun initExtras() {
        imageRes = intent.extras!!.getInt("image")
        feature = intent.extras!!.getString("feature")
        cascadeFilePath = intent.extras!!.getString("cascadeFilePath")
        dnnHelper.onCameraViewStarted(this)

    }

    private fun initView() {
        imageView = findViewById(R.id.imageView)
    }

    private fun processImage() {
        image = Mat()

        try {
            image = Utils.loadResource(this, imageRes)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        when (feature) {
            Feature.OVERTAKING ->{
                val imageOvertaking = dnnHelper.processMat(image!!).mat
                Core.addWeighted(image, 1.0, imageOvertaking, 0.7, 0.0, image)

            }
            Feature.LANE_DETECTION-> processImageLaneDetection(image!!)
        }
    }

    private fun processImageLaneDetection(img: Mat) {
        Imgproc.resize(img, img, Size(LaneFinder.WIDTH_IMAGE.toDouble(), LaneFinder.HEIGHT_IMAGE.toDouble()))
        val imageLanes = laneFinder.getLanes(img.clone())
        Core.addWeighted(img, 1.0, imageLanes, 0.7, 0.0, image)
    }

    private fun setImage() {
        /**
         * OpenCV uses BGR as its default colour order for image
         * See https://stackoverflow.com/questions/39316447/opencv-giving-wrong-color-to-colored-images-on-loading
         */
        Imgproc.cvtColor(image!!, image!!, Imgproc.COLOR_BGR2RGB)
        val bitmap = Bitmap.createBitmap(image!!.width(), image!!.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(image, bitmap)

        imageView!!.setImageBitmap(bitmap)
    }
}
