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
        if (data.obszarSprawdzania.obszarUstawiony()) {
            MatOfPoint p = new MatOfPoint(data.obszarSprawdzania.pobierzTablicePunktow());
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

        data.narysujPunkty(data.mRgba);
        data.narysujKreski(data.mRgba);

        int number = 0;
        if (data.obszarSprawdzania.obszarUstawiony()) {


            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(fin, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE,
                    new Point(0, 0));
            hierarchy.release();

            for (int i = 0; i < contours.size(); i++) {
                MatOfPoint currentContour = contours.get(i);
                double currentArea = Imgproc.contourArea(currentContour);
                if (currentArea > 500) {
                    number++;
                    Rect rectangle = Imgproc.boundingRect(currentContour);
                    Imgproc.rectangle(data.mRgba, rectangle.tl(), rectangle.br(), new Scalar(255, 0, 0), 1);
                }
            }
        }

        data.liczbaSamochodow += Math.max(number - data.poprzedniaLiczbaSamochodow, 0);
        data.poprzedniaLiczbaSamochodow = number;

        Imgproc.putText(data.mRgba, "Liczba samochodow: " + data.liczbaSamochodow, new Point(0, 30), 1, 1.4, new Scalar(255, 255, 0), 1);

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
