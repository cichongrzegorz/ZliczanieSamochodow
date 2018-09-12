package com.cichon.detection;

import android.support.annotation.NonNull;

import com.cichon.Macierze;
import com.cichon.MainActivity;
import com.cichon.Util;

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

public class Gaussian {

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

    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        data.mRgba = inputFrame.rgba();

        Imgproc.cvtColor(data.mRgba, frame(), Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(frame(), rozmycieGaussa(), new Size(21, 21), 0);

        data.narysujPunkty(data.mRgba);
        data.narysujKreski(data.mRgba);

        if (this.setReferenceFrame) {
            setReferenceFrame = false;
            this.ref = macierze.dodaj("referenceFrame", rozmycieGaussa().clone());
        }

        if (this.ref == null) {
            return data.mRgba;
        }
        if (!this.data.obszarSprawdzania.obszarUstawiony()) {
            return data.mRgba;
        }

        Core.absdiff(ref, rozmycieGaussa(), delta());

        Imgproc.threshold(delta(), progowanie(), 70, 255, Imgproc.THRESH_BINARY);

        Mat maska = maska();

//        Mat fin = macierze.pobierz("fin");
        Core.bitwise_and(progowanie(), progowanie(), finalnyObraz(), maska);

        // ---------------------------------
        // ---------------------------------
        // ---------------------------------
        // ---------------------------------
        // ---------------------------------

        int number = 0;

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(finalnyObraz(), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE,
                new Point(0, 0));
        hierarchy.release();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);
            double currentArea = Imgproc.contourArea(currentContour);
            if (currentArea > 500) {
                number++;
                Rect rectangle = Imgproc.boundingRect(currentContour);
                Imgproc.rectangle(data.mRgba, rectangle.tl(), rectangle.br(), Util.niebieski, 1);
            }
        }

        data.liczbaSamochodow += Math.max(number - data.poprzedniaLiczbaSamochodow, 0);
        data.poprzedniaLiczbaSamochodow = number;

        Imgproc.rectangle(data.mRgba, new Point(0, 0), new Point(data.mRgba.width(), 40),
                Util.szary, -1);

        data.osobowy.copyTo(data.mRgba.colRange(50, data.osobowy.width() + 50).rowRange(0, data.osobowy.height() + 0));
        data.ciezarowka.copyTo(data.mRgba.colRange(300, data.osobowy.width() + 300).rowRange(0, data.osobowy.height() + 0));


        Imgproc.putText(data.mRgba, " :" + data.liczbaSamochodow, new Point(90, 30), 1, 2, Util.niebieski, 2);
        Imgproc.putText(data.mRgba, " :" + data.liczbaSamochodowCiezarowych, new Point(340, 30), 1, 2, Util.zielony, 2);

        maska.release();

        return data.mRgba;
    }

    private Mat maska() {
        List<MatOfPoint> points = new ArrayList<>();
        MatOfPoint p = new MatOfPoint(data.obszarSprawdzania.pobierzTablicePunktow());
        points.add(p);

        Mat maska = Mat.zeros(data.mRgba.size(), CvType.CV_8UC1);
        Imgproc.fillPoly(maska, points, Util.bialy);
        return maska;
    }

    private Mat frame() {
        return this.macierze.pobierzLubDodajIPobierz("frame");
    }

    private Mat rozmycieGaussa() {
        return this.macierze.pobierzLubDodajIPobierz("rozmycieGaussa");
    }

    private Mat delta() {
        return this.macierze.pobierzLubDodajIPobierz("delta");
    }

    private Mat progowanie() {
        return this.macierze.pobierzLubDodajIPobierz("progowanie");
    }

    private Mat finalnyObraz() {
        return this.macierze.pobierzLubDodajIPobierz("finalnyObraz");
    }

    public void zwolnijObrazy() {
        macierze.zwolnijWszystkie();
    }

    public void setReferenceFrame() {
        this.setReferenceFrame = true;
    }

    public Mat getRef() {
        return ref;
    }

    public void resetRef() {
        if (this.ref != null) {
            this.ref.release();
        }
        this.ref = null;
    }
}
