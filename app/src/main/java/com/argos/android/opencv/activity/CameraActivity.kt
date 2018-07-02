package com.argos.android.opencv.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceView
import android.view.View
import com.argos.android.opencv.R
import com.argos.android.opencv.camera.*
import com.argos.android.opencv.driving.DnnHelper
import com.argos.android.opencv.model.DnnRespone
import com.argos.android.opencv.model.Feature
import com.argos.android.opencv.model.FeatureLaneDetection
import kotlinx.android.synthetic.main.activity_camera.*
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.text.DecimalFormat
import kotlin.math.max


class CameraActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2, CameraFrameMangerCaller {
    companion object {
//        private const val SCREEN_WIDTH = 640
//        private const val SCREEN_HEIGHT = 480

        private const val SCREEN_WIDTH = 1280
        private const val SCREEN_HEIGHT = 720
    }

    private var decorView: View? = null

    private var cameraView: CameraBridgeViewBase? = null

//    private var feature: String? = null
    private lateinit var mFeature: Feature
    private var cascadeFilePath: String? = null

    private var dnnHelper: DnnHelper = DnnHelper()

    private val loader = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    System.loadLibrary("opencv_java3")
                    cameraView!!.enableView()
                }
                else -> super.onManagerConnected(status)
            }
        }
    }

    private lateinit var mCameraFrameManager: CameraFrameManager

    private lateinit var mCurrentFrame: Mat
    private var mShowDebug = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        initExtras()
        initView()
        initListener()
    }

    private fun initExtras() {
        mFeature = intent.extras!!.getParcelable("feature")
        cascadeFilePath = intent.extras!!.getString("cascadeFilePath")
    }

    private fun initView() {
        decorView = window.decorView
        decorView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        cameraView = findViewById(R.id.opencvCameraView)
        cameraView!!.visibility = SurfaceView.VISIBLE
        cameraView!!.setMaxFrameSize(SCREEN_WIDTH, SCREEN_HEIGHT)
    }

    private fun initListener() {
        cameraView!!.setCvCameraViewListener(this)
    }

    override fun onPause() {
        super.onPause()

        if (cameraView != null)
            cameraView!!.disableView()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (cameraView != null)
            cameraView!!.disableView()
    }

    override fun onResume() {
        super.onResume()

        if (OpenCVLoader.initDebug()) {
            Log.d(CameraActivity::class.java.simpleName, "OpenCV successfully loaded")
            loader.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        } else {
            Log.d(CameraActivity::class.java.simpleName, "OpenCV load failed")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, loader)
        }

        mCameraFrameManager = CameraFrameManager(this, mFeature)
        mCameraFrameManager.start()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        cameraView!!.enableFpsMeter()
        dnnHelper.onCameraViewStarted(this)
    }

    override fun onCameraViewStopped() {
        mCameraFrameManager.finish()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        mCurrentFrame = inputFrame.rgba()
        val image = mCurrentFrame.clone()
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2BGR)

        try {
            val frameInfo = mCameraFrameManager.getFrameInfo()
            Core.addWeighted(image, 1.0, frameInfo, 0.7, 0.0, image)
        } catch (e: NoCameraFrameInfoAvailableException) { }

        if (mShowDebug)
            setImage(createDebugImage(image))
        else
            setImage(image)

        setFps()
        return mCurrentFrame
    }

    private fun createDebugImage(image: Mat): Mat {
        return try {
            val debugImage = mCameraFrameManager.getDebugImage()
            val displayedImage = Mat(Size((image.width() + debugImage.width()).toDouble(), max(image.height(), debugImage.height()).toDouble()), CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0))
            image.copyTo(displayedImage.submat(Rect(0, 0, image.width(), image.height())))
            debugImage.copyTo(displayedImage.submat(Rect(image.width(), 0, debugImage.width(), debugImage.height())))
            displayedImage
        } catch (e: NoDebugImageAvailableException) {
            image
        }
    }

    override fun getCopyOfCurrentFrame(): Mat {
        try {
            return mCurrentFrame.clone()
        } catch (e: UninitializedPropertyAccessException) {
            throw NoCurrentFrameAvailableException("Current frame not initialized")
        }
    }

    private fun setImage(image: Mat) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB)
        val bitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(image, bitmap)

        runOnUiThread {
            imageView!!.setImageBitmap(bitmap)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDistance(distance: Double) {
        runOnUiThread {
            if(distance < 0.5)
                txtDistance.text = "-"
            else
                txtDistance.text = "${distance}m"
        }
    }

    // FPS counter CODE
    private fun setFps(){
        runOnUiThread {
            measure()
            txtFps.text = mStrfps
        }
    }

    private var mFramesCouner: Int = 0
    private var mFrequency: Double = 0.toDouble()
    private var mprevFrameTime: Long = 0
    private var mStrfps: String? = null
    private var mIsInitialized = false
    private val FPS_FORMAT = DecimalFormat("0.00")
    private val STEP = 20

    private fun init() {
        mFramesCouner = 0
        mFrequency = Core.getTickFrequency()
        mprevFrameTime = Core.getTickCount()
        mStrfps = ""

    }

    private fun measure() {
        if (!mIsInitialized) {
            init()
            mIsInitialized = true
        } else {
            mFramesCouner++
            if (mFramesCouner % STEP == 0) {
                val time = Core.getTickCount()
                val fps = STEP * mFrequency / (time - mprevFrameTime)
                mprevFrameTime = time

                mStrfps = FPS_FORMAT.format(fps) + " FPS"
                Log.i(CameraActivity::class.java.simpleName, mStrfps)
            }
        }
    }
}
