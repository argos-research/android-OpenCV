package com.argos.android.opencv.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.argos.android.opencv.R
import com.argos.android.opencv.driving.DnnHelper
import com.argos.android.opencv.lineDetection.LaneFinder
import com.argos.android.opencv.model.Feature
import kotlinx.android.synthetic.main.activity_image_load.*
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.IOException
import kotlin.math.max

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

    private var mShowDebug = false

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

        when (feature) {
            Feature.LANE_DETECTION -> mSwitchDebugImage.visibility = View.VISIBLE
        }
    }

    private fun initView() {
        imageView = findViewById(R.id.imageView)

        mSwitchDebugImage.setOnCheckedChangeListener { _, isChecked ->
            mShowDebug = isChecked
            processImage()
            setImage()
        }
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
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGRA2BGR)
        Imgproc.resize(img, img, Size(LaneFinder.WIDTH_IMAGE.toDouble(), LaneFinder.HEIGHT_IMAGE.toDouble()))
        val (imageLanes, binaryImage) = laneFinder.getLanesAndBinaryImage(img.clone())

        Core.addWeighted(img, 1.0, imageLanes, 0.5, 0.0, imageLanes)
        image = if (mShowDebug) {
            val displayedImage = Mat(Size((imageLanes.width() + binaryImage.width()).toDouble(), max(imageLanes.height(), binaryImage.height()).toDouble()), CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0))
            imageLanes.copyTo(displayedImage.submat(Rect(0, 0, imageLanes.width(), imageLanes.height())))
            binaryImage.copyTo(displayedImage.submat(Rect(imageLanes.width(), 0, binaryImage.width(), binaryImage.height())))
            displayedImage
        } else {
            imageLanes
        }
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
