package com.cichon.detection;

import com.cichon.Macierze;
import com.cichon.MainActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Gaussian implements ChangedImageReader {

    private final MainActivity data;
    private Mat ref;
    private Macierze macierze;
    private boolean setReferenceFrame = false;


    public Gaussian(MainActivity mainActivity) {
        this.data = mainActivity;
        macierze = new Macierze();
        macierze.dodajPusta("frame");
        macierze.dodajPusta("frame2");
        this.ref = macierze.dodaj("referenceFrame", null);
        macierze.dodajPusta("frameDelta");
        macierze.dodajPusta("frameThresh");
        macierze.dodajPusta("fin");
    }

    @Override
    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        data.mRgba = inputFrame.rgba();

        Mat frame = macierze.pobierz("frame");
        Mat frame2 = macierze.pobierz("frame2");
        Mat delta = macierze.pobierz("frameDelta");
        Mat thresh = macierze.pobierz("frameThresh");
        Imgproc.cvtColor(data.mRgba, frame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(frame, frame2, new Size(21, 21), 0);

        if (this.setReferenceFrame) {
            setReferenceFrame = false;
            this.ref = macierze.dodaj("referenceFrame", frame2.clone());
        }

        if (this.ref == null) {
            return frame2;
        }

        Core.absdiff(ref, frame2, delta);

        Imgproc.threshold(delta, thresh, 70, 255, Imgproc.THRESH_BINARY);

        //        Mat clone = macierze.dodaj("clone", frame.clone());
        List<MatOfPoint> points = new ArrayList<>();

//        double h = frame.size().height;
//        double w = frame.size().width;

        if (data.p1x != null && data.p1y != null && data.p2x != null && data.p2y != null && data.p3x != null && data.p3y != null && data.p4x != null && data.p4y != null) {
            MatOfPoint p = new MatOfPoint(
                    new Point(data.p1x, data.p1y),
                    new Point(data.p2x, data.p2y),
                    new Point(data.p3x, data.p3y),
                    new Point(data.p4x, data.p4y));
            points.add(p);
        } else {

        }
        Mat clone = Mat.zeros(frame.size(), CvType.CV_8UC1);
        Imgproc.fillPoly(clone, points, new Scalar(255, 255, 255));

        Mat fin = macierze.pobierz("fin");
        Core.bitwise_and(thresh, thresh, fin, clone);

        // ---------------------------------
        // ---------------------------------
        // ---------------------------------
        // ---------------------------------
        // ---------------------------------

        int number = 0;
//        if (data.maWszystkiePunkty()) {

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE,
                new Point(0, 0));
    /* Mat drawing = Mat.zeros( mIntermediateMat.size(), CvType.CV_8UC3 );
     for( int i = 0; i< contours.size(); i++ )
     {
    Scalar color =new Scalar(Math.random()*255, Math.random()*255, Math.random()*255);
     Imgproc.drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, new Point() );
     }*/
        hierarchy.release();


        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);
            double currentArea = Imgproc.contourArea(currentContour);
//                double currentArea2 = Imgproc.contourArea(points.get(0));
//            System.out.println(currentArea);
//            System.out.println(currentArea2);
//            System.out.println(currentArea2-currentArea3);
            if (currentArea > 500) {
                number++;
                Rect rectangle = Imgproc.boundingRect(currentContour);
                Imgproc.rectangle(data.mRgba, rectangle.tl(), rectangle.br(), new Scalar(255, 0, 0), -1);
            }
        }
//        }

        data.liczbaSamochodow += Math.max(number - data.poprzedniaLiczbaSamochodow, 0);
        data.poprzedniaLiczbaSamochodow = number;
        System.out.println("**********************");
        System.out.println("poprzednia " + data.poprzedniaLiczbaSamochodow);
        System.out.println("liczba " + data.liczbaSamochodow);
        System.out.println("number " + number);
        System.out.println("wyliczenie " + Math.max(number - data.poprzedniaLiczbaSamochodow, 0));
        System.out.println("**********************");

        Imgproc.putText(data.mRgba, "Liczba samochodow: " + data.liczbaSamochodow, new Point(0, 30), 1, 1.4, new Scalar(255, 255, 0), 1);

//        last = data.mRgba.clone();
        clone.release();

        return data.mRgba;
    }

    @Override
    public void zwolnijObrazy() {
        macierze.zwolnijWszystkie();
    }

    public void setReferenceFrame() {
        this.setReferenceFrame = true;
    }

    public void resetRef() {
        this.setReferenceFrame = false;
        this.ref = macierze.dodaj("referenceFrame", null);
    }
}
