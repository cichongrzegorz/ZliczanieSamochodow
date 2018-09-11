package com.cichon.hejo;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Hejo {

    static {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        Mat im1 = Imgcodecs.imread("/tmp/im/original_03.png");
        Mat im2 = Imgcodecs.imread("/tmp/im/modified_03.png");

        Mat gray1 = new Mat();
        Imgproc.cvtColor(im1, gray1, Imgproc.COLOR_BGR2GRAY);
        Mat gray2 = new Mat();
        Imgproc.cvtColor(im2, gray2, Imgproc.COLOR_BGR2GRAY);

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

        Imgcodecs.imwrite("/tmp/im/03.png", im1);
        Imgcodecs.imwrite("/tmp/im/04.png", diff);

    }
}
