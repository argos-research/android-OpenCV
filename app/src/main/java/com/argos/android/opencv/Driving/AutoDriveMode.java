package com.argos.android.opencv.Driving;

import org.opencv.core.Mat;

public class AutoDriveMode
{

    public static int steerDirection;

    public AutoDriveMode()
    {
        steerDirection = 0;
    }

    public Mat processImage(Mat input)
    {
        Mat outputMat = new Mat();
        AutoDrive.drive(input.getNativeObjAddr(), outputMat.getNativeObjAddr());

        return outputMat;
    }
}
