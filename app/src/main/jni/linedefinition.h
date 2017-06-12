#ifndef ANDROID_OPENCV_LINEDEFINITION_H
#define ANDROID_OPENCV_LINEDEFINITION_H

using namespace std;
using namespace cv;

class Line
{

public:

    Vec4i coordinates;

    double m;
    double c;

    Line();

    Line(Vec4i coordinates, int minY, int maxY)
    {
        this->coordinates = coordinates;

        m = getSlope();
        c = getIntercept();

        /*
         * coordinate[0] = x1
         * coordinate[1] = y1
         * coordinate[2] = x2
         * coordinate[3] = y2
         */

        /*
         * Why this doesn't work?
         * Because the below parameters require the value of c and m which is only found later with
         * the provided coordinates.
         */

        this->coordinates[0] = (minY - c) / m;
        this->coordinates[1] = minY;
        this->coordinates[2] = (maxY - c) / m;
        this->coordinates[3] = maxY;
    }

    double getSlope()
    {
        return (double) (coordinates[3] - coordinates[1]) / (coordinates[2] - coordinates[0]);
    }

    double getIntercept()
    {
        return (double) coordinates[1] - m * coordinates[0];
    }
};

#endif //ANDROID_OPENCV_LINEDEFINITION_H
