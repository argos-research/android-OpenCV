#ifndef ANDROID_OPENCV_LANEFINDER_H
#define ANDROID_OPENCV_LANEFINDER_H

#include "linedefinition.h"
#include <numeric>
#include <math.h>
#include <map>

/**
 * Lane detection approach:
 * 0) Pre process with canny edge detection
 * 1) Run probabilistic hough transform to detect all lines
 * 2) Find the 2 most common slopes from all the detected lines
 * 3) Draw lanes through the calculated minY and maxY
 * 4) Return direction, steer percentage based on the slope of the lanes and their mid-points
 */

using namespace std;
using namespace cv;

char* TAG = "LaneFinder";
char* DIRECTION_TAG = "Directionlog";

class LaneFinder
{

private:

    Mat image;
    Mat output;

    /*
     * y coordinates of the detected lines
     * horizonY -> minY smallest detected y coordinate
     * bottomY -> maxY edge of frame
     */
    int horizonY;
    int bottomY;

    /**
     * Slopes of the final detected lanes
     */
    double m1;
    double m2;

    /**
     * To calculate steer percentage by comparing mid point of the distance between the 2 lanes to the actual center of the frame
     */
    int laneMidpoint;
    int trueMidPoint;

    double steerPercentage;

public:

    LaneFinder(Mat processed, Mat original)
    {
        image = processed;
        output = original;

        horizonY = 300;
        bottomY = 480;

        m1 = 0;
        m2 = 0;

        laneMidpoint = 320;
        trueMidPoint = 320;

        steerPercentage = 1.0;
    };

    char* find()
    {
        /**
         * 1) Run probabilistic hough transform to detect all lines
         */
        vector<Vec4i> houghLines;
        HoughLinesP(image, houghLines, 1, CV_PI / 180, 100, 100, 50);
        int size = houghLines.size();
        vector<Line> lines;

        __android_log_print(ANDROID_LOG_INFO, TAG, "No. of lines %d", size);

        /**
         * Save all detected lines to vector
         */
        for (int i = 0; i < size; i++)
        {
            Vec4i points = houghLines[i];

            if (points[1] < horizonY)
                horizonY = points[1];

            line(output, Point(points[0], points[1]), Point(points[2], points[3]), Scalar(0, 0, 255), 1, CV_AA);

            switch (i)
            {
                case 0:  line(output, Point(points[0], points[1]), Point(points[2], points[3]), Scalar(255, 0, 0), 1, CV_AA); //red
                    break;
                case 1:  line(output, Point(points[0], points[1]), Point(points[2], points[3]), Scalar(0, 255, 0), 1, CV_AA); //green
                    break;
                case 2:  line(output, Point(points[0], points[1]), Point(points[2], points[3]), Scalar(0, 0, 255), 1, CV_AA); //blue
                    break;
                case 3:  line(output, Point(points[0], points[1]), Point(points[2], points[3]), Scalar(255, 0, 255), 1, CV_AA); //pink
                    break;
            }

            lines.push_back(Line(points, horizonY, bottomY));
        }

        /**
         * Map<slope, vector<Line>> lanes
         * slope of the line
         * Line object
         */
        map<double, vector<Line> > lanes;
        map<double, vector<Line> >::iterator it = lanes.begin();

        /**
         * 2) Find the 2 most common slopes from all the detected lines
         * 2.1) Iterate through each line
         * 2.2) Group them based on their slope with an offset of +-30%
         * 2.3) Sort them based on the no. of the lines, pick the first 2
         */
        for (int i = 0; i < size; i++)
        {
            double m = lines[i].m;
            double c = lines[i].c;
            Vec4i coordinates = lines[i].coordinates;
            it = lanes.begin();

            __android_log_print(ANDROID_LOG_INFO, TAG, "Line %d Slope %f intercept %f", (i + 1), lines[i].m, lines[i].c);

            if (lanes.size() == 0)
            {
                __android_log_print(ANDROID_LOG_INFO, TAG, "Adding new Line with slope %f and intercept %f", lines[0].m, lines[0].c);
                lanes.insert(make_pair(m, vector<Line> (1, lines[0])));
            }
            else
            {
                int laneFound = 0;

                while (it != lanes.end())
                {
                    double m_ = it->first;
                    double c_ = it->second[0].c; //TODO: Take average intercept of collected lines

                    if ((abs(m_ * 0.7) < abs(m)) && (abs(m) < abs(m_ * 1.3)))
                    {
                        if ((abs(c_ * 0.7) < abs(c)) && (abs(c) < abs(c_ * 1.3)))
                        {
                            __android_log_print(ANDROID_LOG_INFO, TAG, "Adding %f slope line with %f slope Line", m, m_);
                            it->second.push_back(lines[i]);
                            laneFound = 1;
                            break;
                        }
                        else
                        {
                            __android_log_print(ANDROID_LOG_INFO, TAG, "Intercept doesn't match with line %f %f %f %f", (m_), (c_ * 0.7), (c), (c_ * 1.2));
                        }
                    }
                    it++;
                }

                if (!laneFound)
                {
                    __android_log_print(ANDROID_LOG_INFO, TAG, "Adding new Line with slope %f and intercept %f", m, c);
                    lanes.insert(make_pair(m, vector<Line> (1, lines[i])));
                }

            }
        }

        it = lanes.begin();

        while (it != lanes.end())
        {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Lane Slope %f hits %d", it->first, it->second.size());
            it++;
        }

        vector<pair<double, vector<Line> > > finalLanes(lanes.begin(), lanes.end());
        sort(finalLanes.begin(), finalLanes.end(), CommonSlope<double, vector<Line> >());

        __android_log_print(ANDROID_LOG_INFO, TAG, "After sort");

        for (int i = 0; i < finalLanes.size(); i++)
        {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Lane %d Slope %f hits %d", i, finalLanes[i].first, finalLanes[i].second.size());
        }

        /**
         *  3) Draw lanes through the calculated minY and maxY
         */
        for (int i = 0; i < finalLanes.size() && finalLanes.size() < 3; i++)
        {
            Vec4i lane = getMeanCoordinate(finalLanes[i].second);

            line(output, Point(lane[0], lane[1]), Point(lane[2], lane[3]), Scalar(0, 255, 0), 6, CV_AA);
        }

        /**
         * 4) Return direction, steer percentage based on the slope of the lanes and their mid-points
         * Note: Left lanes have negative slope
         */
        switch (finalLanes.size())
        {
            case 0:
            {
                steerPercentage = 0.0;
                __android_log_print(ANDROID_LOG_INFO, DIRECTION_TAG, "straight %f", steerPercentage);
                return "S";
            }

            case 1:
            {
                m1 = finalLanes[0].first;

                if (m1 < 0)
                {
                    __android_log_print(ANDROID_LOG_INFO, DIRECTION_TAG, "right %f", steerPercentage);
                    return "R";
                }
                else
                {
                    __android_log_print(ANDROID_LOG_INFO, DIRECTION_TAG, "left %f", steerPercentage);
                    return "L";
                }
            }

            default:
            {
                m1 = finalLanes[0].first;
                m2 = finalLanes[1].first;

                if ((m1 < 0) && (m2 < 0))
                {
                    __android_log_print(ANDROID_LOG_INFO, DIRECTION_TAG, "right %f", steerPercentage);
                    return "R";
                }
                else if ((m1 > 0) && (m2 > 0))
                {
                    __android_log_print(ANDROID_LOG_INFO, DIRECTION_TAG, "left %f", steerPercentage);
                    return "L";
                }
                else
                {
                    Vec4i lane1 = getMeanCoordinate(finalLanes[0].second);
                    Vec4i lane2 = getMeanCoordinate(finalLanes[1].second);
                    laneMidpoint = lane1[0] + (lane2[0] - lane1[0])/2 ;
                    __android_log_print(ANDROID_LOG_INFO, TAG, "Final lanes x coordinates %d %d Lane midpoint %d ", lane1[0], lane2[0], laneMidpoint);

                    if((trueMidPoint * 0.9) <= laneMidpoint <= (trueMidPoint * 1.1))
                    {
                        steerPercentage = 0.0;
                        __android_log_print(ANDROID_LOG_INFO, DIRECTION_TAG, "straight %f", steerPercentage);
                        return "S";
                    }
                    else if ((trueMidPoint * 0.9) > laneMidpoint)
                    {
                        steerPercentage = 1 - laneMidpoint / trueMidPoint;
                        __android_log_print(ANDROID_LOG_INFO, DIRECTION_TAG, "right %f", steerPercentage);
                        return "R";
                    }
                    else
                    {
                        steerPercentage = 1 - trueMidPoint / laneMidpoint;
                        __android_log_print(ANDROID_LOG_INFO, DIRECTION_TAG, "left %f", steerPercentage);
                        return "L";
                    }
                }
            }
        }
    }

    /*
     * Each common slope is associated with a group of lines, take their mean coordinates for the lane coordinates
     */
    Vec4i getMeanCoordinate(vector<Line> lines)
    {
        Vec4i meanCoordinate;

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < lines.size(); j++)
            {
                meanCoordinate[i] += (lines[j].coordinates[i] - meanCoordinate[i]) / (j + 1);
            }
        }
        return meanCoordinate;
    }

    /**
     * Sort each item of Map<slope, vector<Line>> based on the size of vector<Line> to find the most common slope
     */
    template<typename T1, typename T2>
    struct CommonSlope
    {
        typedef pair<T1, T2> type;
        bool operator() (type const& a, type const& b) const
        {
            return a.second.size() > b.second.size();
        }
    };
};

#endif
