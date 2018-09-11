package com.cichon.detection;

import com.cichon.Macierze;
import com.cichon.MainActivity;
import com.cichon.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Bitwise implements ChangedImageReader {

    private Macierze macierze;

    private Mat last;


    private final MainActivity data;

    public Bitwise(MainActivity mainActivity) {
        this.data = mainActivity;
        macierze = new Macierze();
        macierze.dodajPusta("frame");
//        macierze.dodajPusta("clone");
        macierze.dodajPusta("clahe");
        macierze.dodajPusta("canny");
        macierze.dodajPusta("cannyInv");
        macierze.dodajPusta("blur");
        macierze.dodajPusta("bilateral");
        macierze.dodajPusta("threshold");
        macierze.dodajPusta("fin");
        macierze.dodajPusta("hierarchy");
    }

    int counter = 0;

    @Override
    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        counter++;
        if (last != null && counter % 3 != 0) {
            return last;
        }
        counter = counter % 3;

        data.mRgba = data.skalaSzarosci ? inputFrame.gray() : inputFrame.rgba();

        Mat frame = macierze.pobierz("frame");
        Imgproc.cvtColor(data.mRgba, frame, Imgproc.COLOR_RGB2GRAY);


//    points.add(p);
//    points.add(p);
//    points.add(p);


        Mat clahe = macierze.pobierz("clahe");
        Mat canny = macierze.pobierz("canny");
        Mat cannyInv = macierze.pobierz("cannyInv");

        CLAHE c = Imgproc.createCLAHE(2.0, new Size(8, 8));
        c.apply(frame, clahe);

        if (data.getString(R.string.clahe).equals(data.wybranaOpcja)) {
            last = clahe.clone();
            return last;
        }

        Imgproc.Canny(frame, canny, 50, 70);

        if (data.getString(R.string.canny).equals(data.wybranaOpcja)) {
            last = canny.clone();
            return last;
        }

//    cannyInv = canny.inv(Core.DECOMP_CHOLESKY);

        Core.bitwise_not(canny, cannyInv);

        Mat blur = macierze.pobierz("blur");
        Imgproc.blur(cannyInv, blur, new Size(5, 5));

        if (data.getString(R.string.blur).equals(data.wybranaOpcja)) {
            last = blur.clone();
            return last;
        }

        Mat bilateral = macierze.pobierz("bilateral");
        Imgproc.bilateralFilter(blur, bilateral, 9, 200, 200);

        if (data.getString(R.string.bilateral).equals(data.wybranaOpcja)) {
            last = bilateral.clone();
            return last;
        }

        Mat threshold = macierze.pobierz("threshold");
        Imgproc.threshold(bilateral, threshold, 230, 255, Imgproc.THRESH_BINARY);

        if (data.getString(R.string.threshold).equals(data.wybranaOpcja)) {
            last = threshold.clone();
            return last;
        }

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
        Core.bitwise_and(threshold, threshold, fin, clone);

        if (data.getString(R.string.withMask).equals(data.wybranaOpcja)) {
            last = fin.clone();
            clone.release();
            return last;
        }

        if (data.p1x != null && data.p1y != null) {
            Imgproc.circle(data.mRgba, new Point(data.p1x, data.p1y), 3, new Scalar(255, 255, 0));
        }
        if (data.p2x != null && data.p2y != null) {
            Imgproc.circle(data.mRgba, new Point(data.p2x, data.p2y), 3, new Scalar(255, 255, 0));
        }
        if (data.p3x != null && data.p3y != null) {
            Imgproc.circle(data.mRgba, new Point(data.p3x, data.p3y), 3, new Scalar(255, 255, 0));
        }
        if (data.p4x != null && data.p4y != null) {
            Imgproc.circle(data.mRgba, new Point(data.p4x, data.p4y), 3, new Scalar(255, 255, 0));
        }

        if (data.p1x != null && data.p1y != null && data.p2x != null && data.p2y != null) {
            Imgproc.line(data.mRgba, new Point(data.p1x, data.p1y), new Point(data.p2x, data.p2y), new Scalar(0, 255, 255), 3);
        }
        if (data.p1x != null && data.p1y != null && data.p4x != null && data.p4y != null) {
            Imgproc.line(data.mRgba, new Point(data.p1x, data.p1y), new Point(data.p4x, data.p4y), new Scalar(0, 255, 255), 3);
        }
        if (data.p3x != null && data.p3y != null && data.p4x != null && data.p4y != null) {
            Imgproc.line(data.mRgba, new Point(data.p3x, data.p3y), new Point(data.p4x, data.p4y), new Scalar(0, 255, 255), 3);
        }
        if (data.p3x != null && data.p3y != null && data.p2x != null && data.p2y != null) {
            Imgproc.line(data.mRgba, new Point(data.p3x, data.p3y), new Point(data.p2x, data.p2y), new Scalar(0, 255, 255), 3);
        }

        int number = 0;
        if (data.maWszystkiePunkty()) {

            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(fin, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE,
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
                if (currentArea > 300) {
                    number++;
                    Rect rectangle = Imgproc.boundingRect(currentContour);
                    Imgproc.rectangle(data.mRgba, rectangle.tl(), rectangle.br(), new Scalar(255, 0, 0), 2);
                }
            }
        }

        data.liczbaSamochodow += Math.max(number - data.poprzedniaLiczbaSamochodow, 0);
        data.poprzedniaLiczbaSamochodow = number;
        System.out.println("**********************");
        System.out.println("poprzednia " + data.poprzedniaLiczbaSamochodow);
        System.out.println("liczba " + data.liczbaSamochodow);
        System.out.println("number " + number);
        System.out.println("wyliczenie " + Math.max(number - data.poprzedniaLiczbaSamochodow, 0));
        System.out.println("**********************");

        Imgproc.putText(data.mRgba, "Liczba samochodow: " + data.liczbaSamochodow, new Point(0, 30), 1, 1.4, new Scalar(255, 255, 0), 1);

        last = data.mRgba.clone();
        clone.release();

        return data.mRgba;

//        if (data.p1x != null && data.p1y != null && data.p2x != null && data.p2y != null && data.p3x != null && data.p3y != null && data.p4x != null && data.p4y != null) {
//            return fin;
//        } else {
//            return data.mRgba;
//        }
    }

    @Override
    public void zwolnijObrazy() {
        macierze.zwolnijWszystkie();
    }
}
