#include <com_argos_android_opencv_Native.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <stdio.h>

using namespace std;
using namespace cv;


JNIEXPORT void JNICALL Java_com_argos_android_opencv_Native_convertGray (JNIEnv *, jclass,
                                                                            jlong srcMat,
                                                                            jlong outMat)
{

    Mat& input = *(Mat*) srcMat;
    Mat& output = *(Mat*) outMat;

    cvtColor(input, output, CV_RGBA2GRAY);
}
