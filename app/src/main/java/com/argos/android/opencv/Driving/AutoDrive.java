package com.argos.android.opencv.Driving;

public class AutoDrive
{
    public static native void reset();
    public static native void drive(long srcMat, long outMat);
}
