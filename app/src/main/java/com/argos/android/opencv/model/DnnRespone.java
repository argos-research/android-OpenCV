package com.argos.android.opencv.model;

import org.opencv.core.Mat;

public class DnnRespone {

    Mat mat;
    double distance;

    public DnnRespone(Mat mat,double distance){
        this.mat = mat;
        this.distance = distance;
    }

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}
