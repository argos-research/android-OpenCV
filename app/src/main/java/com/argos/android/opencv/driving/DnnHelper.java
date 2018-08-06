package com.argos.android.opencv.driving;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.argos.android.opencv.model.DnnRespone;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DnnHelper {

    double distance;

    double knownHeight = 1300;
    double focalLength = 250;

    public DnnHelper() {

    }

    // Load a network.
    public void onCameraViewStarted(Context context) {
        String config = getPath("opt_graph.pbtxt", context);
        String model = getPath("opt_graph.pb", context);
        net = Dnn.readNetFromTensorflow(model, config);
        Log.i(TAG, "Network loaded successfully");
    }

    public DnnRespone onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return processMat(inputFrame.rgba());
    }

    public DnnRespone processMat(Mat frame) {

        final int IN_WIDTH = 300;
        final int IN_HEIGHT = 300;
        final float WH_RATIO = (float) IN_WIDTH / IN_HEIGHT;
        final double IN_SCALE_FACTOR = 0.007843;
        final double MEAN_VAL = 127.5;
        final double THRESHOLD = 0.2;

        // Get a new frame
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

        // Forward image through network.
        Mat blob = Dnn.blobFromImage(frame, IN_SCALE_FACTOR,
                new Size(IN_WIDTH, IN_HEIGHT),
                new Scalar(MEAN_VAL, MEAN_VAL, MEAN_VAL), false, true);
        net.setInput(blob);
        Mat detections = net.forward();

        blob.release();

        int cols = frame.cols();
        int rows = frame.rows();

        Size cropSize;
        if ((float) cols / rows > WH_RATIO) {
            cropSize = new Size(rows * WH_RATIO, rows);
        } else {
            cropSize = new Size(cols, cols / WH_RATIO);
        }

        int y1 = (int) (rows - cropSize.height) / 2;
        int y2 = (int) (y1 + cropSize.height);
        int x1 = (int) (cols - cropSize.width) / 2;
        int x2 = (int) (x1 + cropSize.width);

        Mat retFrame = new Mat(rows, cols, CvType.CV_8UC3, new Scalar(0,0,0, 0));
        Mat subFrame = retFrame.submat(y1, y2, x1, x2);


        cols = subFrame.cols();
        rows = subFrame.rows();

        detections = detections.reshape(1, (int) detections.total() / 7);

        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 2)[0];
            if (confidence > THRESHOLD) {
                int classId = (int) detections.get(i, 1)[0];

                int xLeftBottom = (int) (detections.get(i, 3)[0] * cols);
                int yLeftBottom = (int) (detections.get(i, 4)[0] * rows);
                int xRightTop = (int) (detections.get(i, 5)[0] * cols);
                int yRightTop = (int) (detections.get(i, 6)[0] * rows);

                //only count distance for lynx
                if(classId == 1) {
                    distance = getDistanceToCamera(yRightTop - yLeftBottom);
                }

                // Draw rectangle around detected object.
                Imgproc.rectangle(subFrame, new Point(xLeftBottom, yLeftBottom),
                        new Point(xRightTop, yRightTop),
                        new Scalar(0, 255, 0));
                // String label = classNames[classId] + ": " + confidence;
                confidence = (double)((int)(confidence*10000))/100;
                String label =  getNameForClass(classId) + " " + confidence+"%";

                int[] baseLine = new int[1];
                Size labelSize = Imgproc.getTextSize(label, Core.FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);

                // Draw background for label.
                Imgproc.rectangle(subFrame, new Point(xLeftBottom, yLeftBottom - labelSize.height),
                        new Point(xLeftBottom + labelSize.width, yLeftBottom + baseLine[0]),
                        new Scalar(255, 255, 255), Core.FILLED);

                // Write class name and confidence.
                Imgproc.putText(subFrame, label, new Point(xLeftBottom, yLeftBottom),
                        Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0));
            }
        }
        subFrame.release();
        return new DnnRespone(retFrame,distance/1000);
    }

    private double getDistanceToCamera(int pixelHeight){
        return (knownHeight * focalLength) / pixelHeight;
    }

    private String getNameForClass(int classId) {
        switch(classId){
            case 1:{
                return "Lynx220";
            }
            case 2:{
                return "Cavallo360";
            }
            case 3:{
                return "Spirit300";
            }
            case 4:{
                return "FMC_GT4";
            }
            case 5:{
                return "Boxer96";
            }
        }

        return "Unknown";
    }


    // Upload file to storage and return a path.
    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();

        BufferedInputStream inputStream = null;
        try {
            // Read data from assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Return a path to file which may be read in common way.
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }


    private static final String TAG = "OpenCV/MobileNet";
    private Net net;
}
