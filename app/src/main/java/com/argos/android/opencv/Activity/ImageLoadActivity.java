package com.argos.android.opencv.Activity;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import com.argos.android.opencv.Driving.AutoDrive;
import com.argos.android.opencv.R;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.io.IOException;

/**
 * Activity to run the OpenCV algorithm on images
 * To add new images:
 * 1) Add images inside the drawable directory
 * 2) Include drawables in the images array {@link com.argos.android.opencv.Fragment.ChooseImageDialogFragment#images}
 */

public class ImageLoadActivity extends AppCompatActivity
{
    private static final String TAG = "ImageLoadActivity";

    private Mat image;
    private ImageView imageView;

    private @DrawableRes int imageRes;
    private String feature;
    private String cascadeFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_load);

        loadLibraries();
        initExtras();
        initView();
        processImage();
        setImage();
    }

    public void loadLibraries()
    {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("NativeArgOS");
    }

    public void initExtras()
    {
        imageRes = getIntent().getExtras().getInt("image");
        feature = getIntent().getExtras().getString("feature");
        cascadeFilePath = getIntent().getExtras().getString("cascadeFilePath");
    }

    public void initView()
    {
        imageView = (ImageView) findViewById(R.id.image_view);
    }

    public void processImage()
    {
        image = new Mat();

        try
        {
            image = Utils.loadResource(this, imageRes);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(feature.equals(getString(R.string.feature_lane)))
        {
            AutoDrive.drive(image.getNativeObjAddr());
        }
        else
        {
            if (MainActivity.CASCADE_FILE_LOADED)
                AutoDrive.detectVehicle(cascadeFilePath, image.getNativeObjAddr());
            else
                Toast.makeText(this, "Error: Cascade file not loaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void setImage()
    {
        /**
         * OpenCV uses BGR as its default colour order for image
         * See https://stackoverflow.com/questions/39316447/opencv-giving-wrong-color-to-colored-images-on-loading
         */
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);
        Bitmap bitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(image, bitmap);

        imageView.setImageBitmap(bitmap);
    }
}
