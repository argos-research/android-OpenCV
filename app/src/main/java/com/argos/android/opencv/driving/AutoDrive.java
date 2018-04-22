package com.argos.android.opencv.Driving;

public class AutoDrive
{
    /**
     * Native functions  are placed here. To add a new native function:
     * 1) Declare the function inside this class
     * 2) Build the project
     * 3) Run $javah -d jni -classpath ../../build/intermediates/classes/debug com.argos.android.opencv.Driving.AutoDrive from android-OpenCV/app/src/main directory
     * 4) Use the new function signature generated inside the com_argos_android_opencv_Driving_AutoDrive.h file in com_argos_android_opencv_Driving_AutoDrive.cpp
     */

    /**
     * Run lane detection algorithm
     * @param srcMat input frame
     * @return direction {"S"->Straight, "L"->Left, "R"->Right}
     */
    public static native String drive(long srcMat);

    /**
     * Run vehicle detection algorithm
     * TODO: Implement distant detection and return distance from the detected vehicle
     * @param cascadeFilePath path to cascade.xml used for classification
     * @param srcMat input frame
     * @return for debugging "S"-> if vehicle is found, "N"-> if not
     */
    public static native String detectVehicle(String cascadeFilePath, long srcMat);
}
