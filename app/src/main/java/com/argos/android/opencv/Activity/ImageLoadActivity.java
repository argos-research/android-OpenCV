package com.argos.android.opencv.Activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.argos.android.opencv.Driving.AutoDriveMode;
import com.argos.android.opencv.R;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import java.io.IOException;

public class ImageLoadActivity extends AppCompatActivity
{
    private static final String TAG = "ImageLoadActivity";
    private Mat image;
    private ImageView imageView;
    private AutoDriveMode driveMode = new AutoDriveMode();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_load);

        loadLibraries();
        initView();
        processImage();
        setImage();
    }

    public void loadLibraries()
    {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("NativeArgOS");
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
            image = Utils.loadResource(this, R.drawable.scene_2);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        driveMode.processImage(image);
    }

    public void setImage()
    {
        Bitmap bitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(image, bitmap);

        imageView.setImageBitmap(bitmap);
    }
}
