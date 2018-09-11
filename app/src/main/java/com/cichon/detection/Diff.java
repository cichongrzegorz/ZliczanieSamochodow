package com.cichon.detection;

import com.cichon.MainActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Diff implements ChangedImageReader {

    private final MainActivity data;

    public Diff(MainActivity mainActivity) {
        this.data = mainActivity;
    }

    public static void main(String[] args) {
        System.out.println("test");
    }


    @Override
    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        if (data.mRgba == null && data.mRgbaPrev == null) {
            data.mRgba = data.skalaSzarosci ? inputFrame.gray() : inputFrame.rgba();
            return data.mRgba;
        }

        data.mRgbaPrev = data.mRgba.clone();
        data.mRgba = data.skalaSzarosci ? inputFrame.gray() : inputFrame.rgba();


        Mat gray1 = new Mat();
        Imgproc.cvtColor(data.mRgba, gray1, Imgproc.COLOR_BGR2GRAY);
        Mat gray2 = new Mat();
        Imgproc.cvtColor(data.mRgbaPrev, gray2, Imgproc.COLOR_BGR2GRAY);

        Mat diff = new Mat();
        Core.absdiff(gray1, gray2, diff);

        Mat diff2 = diff.clone();
//    Imgproc.Canny(diff, diff2, 200, 400);

        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(diff2, contours, hierarchy, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE,
                new Point(0, 0));

        hierarchy.release();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);
            double currentArea = Imgproc.contourArea(currentContour);
            Rect rectangle = Imgproc.boundingRect(currentContour);
            if (currentArea > 100) {
                Imgproc.rectangle(diff, rectangle.tl(), rectangle.br(), new Scalar(255, 255, 255), 1);
            }
        }

        return diff;
    }

    @Override
    public void zwolnijObrazy() {

    }
}
