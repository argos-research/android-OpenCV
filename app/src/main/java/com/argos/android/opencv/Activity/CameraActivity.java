package com.argos.android.opencv.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.argos.android.opencv.Driving.AutoDrive;
import com.argos.android.opencv.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

/**
 * Activity to run the OpenCV algorithm on Camera
 */

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2
{
    private static final String TAG = "CameraActivity";
    private View decorView;
    private CameraBridgeViewBase cameraView;
    private ImageView directionView;
    private int[] directionDrawable = {R.drawable.straight, R.drawable.left, R.drawable.right};

    /**
     * 640x480 produced the best FPS on moderate smart phones.
     * TODO: Implement choosing screen width and height within the app.
     * Note that the ROI is also hardcoded considering dimensions as 640x480, you might need to change that too!
     */
    private final int SCREEN_WIDTH = 640;
    private final int SCREEN_HEIGHT = 480;

    private String feature;
    private String cascadeFilePath;

    private BaseLoaderCallback loader = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    System.loadLibrary("opencv_java3");
                    System.loadLibrary("NativeArgOS");
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

        initExtras();
        initView();
        initListener();
    }

    public void initExtras()
    {
        feature = getIntent().getExtras().getString("feature");
        cascadeFilePath = getIntent().getExtras().getString("cascadeFilePath");
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
        cameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_cameraview);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setMaxFrameSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        directionView = (ImageView) findViewById(R.id.imageview_direction);
    }

    public void initListener()
    {
        cameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (OpenCVLoader.initDebug())
        {
            Log.d(TAG, "OpenCV successfully loaded");
            loader.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else
        {
            Log.d(TAG, "OpenCV load failed");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, loader);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height)
    {
        cameraView.enableFpsMeter();
    }

    @Override
    public void onCameraViewStopped()
    {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        Mat srcMat = inputFrame.rgba();

        if (feature.equals(getString(R.string.feature_lane)))
        {
            changeDirection(AutoDrive.drive(srcMat.getNativeObjAddr()));
        } else
        {
            removeDirectionView();
            if (MainActivity.CASCADE_FILE_LOADED)
                AutoDrive.detectVehicle(cascadeFilePath, srcMat.getNativeObjAddr());
            else
                Toast.makeText(this, "Error: Cascade file not loaded", Toast.LENGTH_SHORT).show();
        }

        return srcMat;
    }

    public void changeDirection(final String direction)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                directionView.setVisibility(View.VISIBLE);
                switch (direction)
                {
                    case "S":
                    {
                        directionView.setImageDrawable(getResources().getDrawable(directionDrawable[0]));
                        break;
                    }
                    case "L":
                    {
                        directionView.setImageDrawable(getResources().getDrawable(directionDrawable[1]));
                        break;
                    }
                    case "R":
                    {
                        directionView.setImageDrawable(getResources().getDrawable(directionDrawable[2]));
                        break;
                    }
                }
            }
        });
    }

    public void removeDirectionView()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                directionView.setVisibility(View.INVISIBLE);
            }
        });
    }
}
