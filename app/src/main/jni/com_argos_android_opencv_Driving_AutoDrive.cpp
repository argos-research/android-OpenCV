#include "com_argos_android_opencv_driving_AutoDrive.h"
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <android/log.h>
#include "lanefinder.h"
#include "carfinder.h"

/**
 * Implementation of the native functions
 */

using namespace std;
using namespace cv;

/**
 * Perform all operations on the processed matrix and only draw the detections on the original source matrix
 */
Mat processed;
/**
 * Hardcoded values to set the ROI considering dimensions as 640x480
 */
Point pts[6] = {
    Point(0, 480),
    Point(0, 250),
    Point(240, 200),
    Point(400, 200),
    Point(640, 250),
    Point(640, 480)
};

void processImage(Mat);
void setROI();
void drawDebugLines(Mat&);

JNIEXPORT jstring JNICALL Java_com_argos_android_opencv_driving_AutoDrive_drive
        (JNIEnv* env, jclass, jlong srcMat)
{
    Mat& original = *(Mat*) srcMat;

    processImage(original);
    setROI();
    drawDebugLines(original);

    LaneFinder laneFinder(processed, original);
    return env->NewStringUTF(laneFinder.find());
}

void processImage(Mat image)
{
    cvtColor(image, processed, CV_RGBA2GRAY);
    GaussianBlur(processed, processed, Size(5,5), 0, 0);
    Canny(processed, processed, 200, 300, 3);
}

void setROI()
{
    Mat mask(processed.size(), CV_8U);
    fillConvexPoly(mask, pts, 6, Scalar(255));
    bitwise_and(mask, processed, processed);
}

void drawDebugLines(Mat& original)
{
    line(original, pts[0], pts[1], Scalar(255, 0, 0), 1, CV_AA);
    line(original, pts[1], pts[2], Scalar(255, 0, 0), 1, CV_AA);
    line(original, pts[2], pts[3], Scalar(255, 0, 0), 1, CV_AA);
    line(original, pts[3], pts[4], Scalar(255, 0, 0), 1, CV_AA);
    line(original, pts[4], pts[5], Scalar(255, 0, 0), 1, CV_AA);
    line(original, pts[5], pts[0], Scalar(255, 0, 0), 1, CV_AA);
}

JNIEXPORT jstring JNICALL Java_com_argos_android_opencv_driving_AutoDrive_detectVehicle
        (JNIEnv* env, jclass, jstring cascadeFilePath, jlong srcMat)
{
    Mat& original = *(Mat*) srcMat;
    const char* javaString = env->GetStringUTFChars(cascadeFilePath, NULL);
    string cascadeFile(javaString);

    CarFinder carFinder(original, original, cascadeFile);
    return env->NewStringUTF(carFinder.find());
}