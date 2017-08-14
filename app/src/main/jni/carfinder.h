#ifndef ANDROID_OPENCV_CARFINDER_H
#define ANDROID_OPENCV_CARFINDER_H

#include <opencv2/objdetect.hpp>

/**
 * Run cascade classifier to detect vehicles using cascade.xml file supplied.
 */

using namespace std;
using namespace cv;

class CarFinder
{

private:

    Mat image;
    Mat output;

    vector<Rect> cars;
    string cascadeFile;

public:

    CarFinder(Mat processed, Mat original, string cascadeFilePath)
    {
        image = processed;
        output = original;
        cascadeFile = cascadeFilePath;
    }

    char* find()
    {
        CascadeClassifier cascadeClassifier;
        cascadeClassifier.load(cascadeFile);

        cascadeClassifier.detectMultiScale(image, cars);

        if(cars.size() > 0)
        {
            Rect car;

            for(int i = 0; i < cars.size(); i++)
            {
                car = cars[i];
                rectangle(output, car, Scalar(0, 255, 0), 3, CV_AA);
            }

            return "S";
        }
        else
            return "N";

    }

};
#endif //ANDROID_OPENCV_CARFINDER_H
