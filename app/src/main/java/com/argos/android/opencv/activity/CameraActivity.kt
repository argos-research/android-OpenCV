package com.argos.android.opencv.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.EditText
import com.android.volley.Request
import com.argos.android.opencv.R
import com.argos.android.opencv.camera.*
import com.argos.android.opencv.driving.DnnHelper
import com.argos.android.opencv.model.Feature
import com.argos.android.opencv.model.FpsCounter
import com.argos.android.opencv.network.APIController
import com.argos.android.opencv.network.ServiceVolley
import kotlinx.android.synthetic.main.activity_camera.*
import org.json.JSONObject
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.text.DecimalFormat
import kotlin.concurrent.thread
import kotlin.math.max
import android.widget.CompoundButton
import android.widget.Switch




class CameraActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2, CameraFrameMangerCaller {
    companion object {
        const val SCREEN_WIDTH = 1280
        const val SCREEN_HEIGHT = 720
    }

    private var decorView: View? = null

    private var cameraView: CameraBridgeViewBase? = null

    private lateinit var mFeatureString: String
    private var cascadeFilePath: String? = null

    //Overtaking scenario
    private var dnnHelper: DnnHelper = DnnHelper()
    private val service = ServiceVolley()
    private val apiController = APIController(service)
    private var mServerString: String? = "http://10.0.0.3:9080"
    private var mSpeed = 44.0
    private val mSlowSpeed = 20.0
    private val mFastSpeed = 50.0
    private var mDistance = 0.0
    private var isOvertaking = false
    private var hasPassed = false

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

    private var mFpsCounter = FpsCounter()
    private lateinit var mCurrentFrame: Mat
    private var mShowDebug = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        initExtras()
        initView()
        initListener()
    }

    private fun initExtras() {
        mFeatureString = intent.getStringExtra("feature")
        cascadeFilePath = intent.extras.getString("cascadeFilePath")

        when (mFeatureString) {
            Feature.OVERTAKING -> showInputDialogue()
            Feature.LANE_DETECTION -> mSwitchDebug.visibility = View.VISIBLE
        }
    }

    private fun initView() {
        decorView = window.decorView
        decorView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        cameraView = findViewById(R.id.opencvCameraView)
        cameraView!!.visibility = SurfaceView.VISIBLE
        cameraView!!.setMaxFrameSize(SCREEN_WIDTH, SCREEN_HEIGHT)

        mSwitchDebug.setOnCheckedChangeListener { _, isChecked -> mShowDebug = isChecked }
    }

    private fun initListener() {
        cameraView!!.setCvCameraViewListener(this)
    }

    override fun onPause() {
        super.onPause()

        if (cameraView != null)
            cameraView!!.disableView()

        mCameraFrameManager.finish()
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

        mCameraFrameManager = CameraFrameManager(this, mFeatureString, dnnHelper)
        mCameraFrameManager.start()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        cameraView!!.enableFpsMeter()
        dnnHelper.onCameraViewStarted(this)

    }

    override fun onCameraViewStopped() {


    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        mCurrentFrame = inputFrame.rgba()
        val image = mCurrentFrame.clone()
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2BGR)

        try {
            val frameInfo = mCameraFrameManager.getFrameInfo()
            Core.addWeighted(image, 1.0, frameInfo, 0.7, 0.0, image)
        } catch (e: NoCameraFrameInfoAvailableException) {
        }

        if (mShowDebug)
            setImage(createDebugImage(image))
        else
            setImage(image)

        setFps()

        if (mFeatureString == Feature.OVERTAKING) {
            getCurrentLane()
            setCurrentSpeed()
        }
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

    private fun setFps() {
        runOnUiThread {
            txtFps.text = mFpsCounter.getFps()
        }
    }

    override fun getCopyOfCurrentFrame(): Mat {
        mFpsCounter.newFrame()
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

    private fun showInputDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter server ip")

        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_NUMBER
        input.setText("10.0.0.3")
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK", { dialog, which ->
            mServerString = "http://" + input.text.toString() + ":9080"
            val path = "/setSpeed/"
            val params = JSONObject()
            params.put("speed", mSlowSpeed)
            apiController.request(mServerString + path, params, { response ->
                mSpeed = mSlowSpeed
            }, Request.Method.POST)


            params.put("speed", mFastSpeed)
            val handler = Handler()
            handler.postDelayed({
                apiController.request(mServerString + path, params, { response ->
                    mSpeed = mFastSpeed
                }, Request.Method.POST)
            }, 7000)


        })
        builder.setNegativeButton("Cancel", { dialog, which -> dialog.cancel() })

        builder.show()
    }

    var distanceTreshHold = 0.0

    @SuppressLint("SetTextI18n")
    override fun setDistance(distance: Double) {
        runOnUiThread {
            if (distance < 0.5)
                txtDistance.text = "-"
            else
                txtDistance.text = "${distance}m"
        }

        checkOverTaking(distance)

    }

    private fun checkOverTaking(distance: Double) {
        if (distance < 10 && mSpeed >= mFastSpeed && mDistance != distance && !isOvertaking) {
            distanceTreshHold++
        }
        mDistance = distance
        if (distanceTreshHold >= 12) {
            distanceTreshHold = 0.0

            val path = "/moveLeft/"
            val params = JSONObject()
            apiController.request(mServerString + path, params, { response ->
                txtCurrentLane.setText("Lane: " + response?.getInt("lane"))
            }, Request.Method.GET)

            isOvertaking = true
            var timePassed = 0
            thread(start = true) {
                while (isOvertaking) {

                    Thread.sleep(250)
                    timePassed +=250

                    val path = "/getSensor/1"
                    apiController.request(mServerString + path, JSONObject(), { response ->
                        if (response != null) {
                            val sensorDistance = response.getDouble("distance")
                            if (sensorDistance < 20 && sensorDistance >5 || timePassed > 20*1000){
                                isOvertaking = false

                            }
                        }
                    }, Request.Method.GET)
                }
                Thread.sleep(3000)

                val path2 = "/moveRight/"
                apiController.request(mServerString + path2, JSONObject(), { _ ->
                }, Request.Method.GET)

            }
        }

    }

    var i = 0
    private fun getCurrentLane() {
        if (i % 30 == 0 && !mServerString.isNullOrEmpty()) {
            val path = "/getLane/"
            val params = JSONObject()
            apiController.request(mServerString + path, params, { response ->
                txtCurrentLane.setText("Lane: " + response?.getInt("lane"))
            }, Request.Method.GET)
            i = 0

        }
        i++
    }

    private fun setCurrentSpeed() {
        runOnUiThread { txtCurrentSpeed.setText("" + mSpeed + " km/h") }
    }
}
