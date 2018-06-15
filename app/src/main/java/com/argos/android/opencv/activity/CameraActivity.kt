package com.argos.android.opencv.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.argos.android.opencv.R
import com.argos.android.opencv.driving.AutoDrive
import com.argos.android.opencv.driving.DnnHelper
import com.argos.android.opencv.model.DnnRespone
import kotlinx.android.synthetic.main.activity_camera.*
import org.opencv.android.*
import org.opencv.core.Core
import org.opencv.core.Mat
import java.text.DecimalFormat

/**
 * Activity to run the OpenCV algorithm on Camera
 */

class CameraActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {
    private var decorView: View? = null

    private var cameraView: CameraBridgeViewBase? = null
    private var directionView: ImageView? = null
    private val directionDrawable = intArrayOf(R.drawable.straight, R.drawable.left, R.drawable.right)

    /**
     * 640x480 produced the best FPS on moderate smart phones.
     * TODO: Implement choosing screen width and height within the app.
     * Note that the ROI is also hardcoded considering dimensions as 640x480, you might need to change that too!
     */
    private val SCREEN_WIDTH = 640
    private val SCREEN_HEIGHT = 480

    private var feature: String? = null
    private var cascadeFilePath: String? = null

    private var dnnHelper: DnnHelper = DnnHelper()


    private val loader = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    System.loadLibrary("opencv_java3")
                    System.loadLibrary("NativeArgOS")
                    cameraView!!.enableView()
                }

                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        initExtras()
        initView()
        initListener()
    }

    private fun initExtras() {
        feature = intent.extras!!.getString("feature")
        cascadeFilePath = intent.extras!!.getString("cascadeFilePath")
    }

    private fun initView() {
        decorView = window.decorView
        decorView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        cameraView = findViewById(R.id.opencv_cameraview)
        cameraView!!.visibility = SurfaceView.VISIBLE

        cameraView!!.setMaxFrameSize(SCREEN_WIDTH, SCREEN_HEIGHT)

        directionView = findViewById(R.id.imageview_direction)

        when (feature) {
            getString(R.string.feature_lane), getString(R.string.feature_vehicle) -> {
                blackBackground.visibility = View.GONE
                image_view.visibility = View.GONE
            }
            getString(R.string.feature_overtaking) -> {}
        }
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
            Log.d(TAG, "OpenCV successfully loaded")
            loader.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        } else {
            Log.d(TAG, "OpenCV load failed")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, loader)
        }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        cameraView!!.enableFpsMeter()
        dnnHelper.onCameraViewStarted(this)
    }

    override fun onCameraViewStopped() {

    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        var srcMat = inputFrame.rgba()
        val dnnResponse : DnnRespone?

        when (feature) {
            getString(R.string.feature_lane) -> changeDirection(AutoDrive.drive(srcMat.nativeObjAddr))
            getString(R.string.feature_vehicle) -> findVehicle(srcMat)
            getString(R.string.feature_overtaking) -> {

                removeDirectionView()
                dnnResponse = dnnHelper.onCameraFrame(inputFrame)
                srcMat = dnnResponse.mat
                setImage(srcMat)
                setDistance(dnnResponse.distance)
            }
        }
        setFps()
        return srcMat
    }

    private fun findVehicle(srcMat: Mat) {
        removeDirectionView()
        if (MainActivity.CASCADE_FILE_LOADED)
            AutoDrive.detectVehicle(cascadeFilePath!!, srcMat.nativeObjAddr)
        else
            Toast.makeText(this, "Error: Cascade file not loaded", Toast.LENGTH_SHORT).show()
    }

    private fun changeDirection(direction: String) {
        runOnUiThread {
            directionView!!.visibility = View.VISIBLE
            when (direction) {
                "S" -> {
                    directionView!!.setImageResource((directionDrawable[0]))
                }
                "L" -> {
                    directionView!!.setImageResource((directionDrawable[1]))
                }
                "R" -> {
                    directionView!!.setImageResource((directionDrawable[2]))
                }
            }
        }
    }

    private fun removeDirectionView() {
        runOnUiThread { directionView!!.visibility = View.INVISIBLE }
    }

    fun setImage(image:Mat?) {
        /**
         * OpenCV uses BGR as its default colour order for image
         * See https://stackoverflow.com/questions/39316447/opencv-giving-wrong-color-to-colored-images-on-loading
         */
       // Imgproc.cvtColor(image!!, image!!, Imgproc.COLOR_BGR2RGB)
        val bitmap = Bitmap.createBitmap(image!!.width(), image!!.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(image, bitmap)

        runOnUiThread {
            image_view!!.setImageBitmap(bitmap)
        }

    }

    private fun setDistance(distance: Double) {
        runOnUiThread {
            if(distance < 0.5)
                txtDistance.text = "-"
            else
                txtDistance.text = ""+distance + "m"
        }
    }

    // FPS counter CODE

    fun setFps(){
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

    fun init() {
        mFramesCouner = 0
        mFrequency = Core.getTickFrequency()
        mprevFrameTime = Core.getTickCount()
        mStrfps = ""

    }

    fun measure() {
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
                Log.i(TAG, mStrfps)
            }
        }
    }

    companion object {
        private const val TAG = "CameraActivity"
    }
}
