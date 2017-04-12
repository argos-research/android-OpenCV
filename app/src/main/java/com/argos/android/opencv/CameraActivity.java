package com.argos.android.opencv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2
{
    private static final String TAG = "CameraActivity";
    private View decorView;
    private CameraBridgeViewBase cameraView;
    private Mat rgbMat;
    private BaseLoaderCallback loader = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case BaseLoaderCallback.SUCCESS:
                {
                    Log.d(TAG, "OpenCV successfully loaded");
                    System.loadLibrary("native-lib");
                    cameraView.enableView();
                    break;
                }

                default:
                {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initView();
        initListener();
    }

    public void initView()
    {
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility
        (
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        cameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        cameraView.setVisibility(View.VISIBLE);
    }

    public void initListener()
    {
        cameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if(cameraView != null)
            cameraView.disableView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(cameraView != null)
            cameraView.disableView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(OpenCVLoader.initDebug())
        {
            Log.d(TAG, "OpenCV successfully loaded");
            loader.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else
        {
            Log.d(TAG, "OpenCV load failed");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, loader);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height)
    {

    }

    @Override
    public void onCameraViewStopped()
    {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        rgbMat = inputFrame.rgba();
        return rgbMat;
    }
}
