package com.cichon.detection;

import com.cichon.Macierze;
import com.cichon.MainActivity;
import com.cichon.Util;
import com.cichon.ustawienia.UstawieniaAplikacji;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class RozmycieGaussa {

    private final MainActivity main;
    private final UstawieniaAplikacji ustawienia;
    private Mat obrazReferencyjny;
    private Macierze macierze;
    private boolean ustawObrazReferencyjny = false;


    public RozmycieGaussa(MainActivity mainActivity, UstawieniaAplikacji ustawieniaAplikacji) {
        this.main = mainActivity;
        this.ustawienia = ustawieniaAplikacji;
        macierze = new Macierze();
    }

    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        main.mRgba = inputFrame.rgba();

        Imgproc.cvtColor(main.mRgba, frame(), Imgproc.COLOR_BGR2GRAY);

        Mat gauss = rozmycieGaussa();

        Imgproc.GaussianBlur(frame(), gauss, new Size(ustawienia.getSzerokosc(), ustawienia.getWysokosc()), ustawienia.getSigma());

        if ("Rozmycie Gaussa".equals(ustawienia.getObraz())) {
            return gauss;
        }

        main.narysujPunkty(main.mRgba);
        main.narysujKreski(main.mRgba);

        if (this.ustawObrazReferencyjny) {
            ustawObrazReferencyjny = false;
            this.obrazReferencyjny = macierze.dodaj("referenceFrame", gauss.clone());
        }

        if (this.obrazReferencyjny == null) {
            return main.mRgba;
        }


        if ("Obraz Referencyjny".equals(ustawienia.getObraz())) {
            return this.obrazReferencyjny;
        }

        Core.absdiff(obrazReferencyjny, gauss, delta());

        Mat progowanie = progowanie();
        Imgproc.threshold(delta(), progowanie, ustawienia.getProgowanie(), 255, Imgproc.THRESH_BINARY);
        if ("Progowanie Binarne".equals(ustawienia.getObraz())) {
            return progowanie;
        }

        if (!this.main.obszarSprawdzania.obszarUstawiony()) {
            return main.mRgba;
        }
        Mat maska = maska();

//        Mat fin = macierze.pobierz("fin");
        Core.bitwise_and(progowanie, progowanie, finalnyObraz(), maska);

        // ---------------------------------
        // ---------------------------------
        // ---------------------------------
        // ---------------------------------
        // ---------------------------------

        int number = 0;

        ArrayList<MatOfPoint> kontury = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(finalnyObraz(), kontury, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE,
                new Point(0, 0));
        hierarchy.release();

        for (int i = 0; i < kontury.size(); i++) {
            MatOfPoint currentContour = kontury.get(i);
            double currentArea = Imgproc.contourArea(currentContour);
            if (currentArea > ustawienia.getWielkoscSamochodu()) {
                number++;
                Rect rectangle = Imgproc.boundingRect(currentContour);
                if (currentArea > ustawienia.getWielkoscSamochodu() * ustawienia.getPrzelicznikCiezarowki()) {
                    Imgproc.rectangle(main.mRgba, rectangle.tl(), rectangle.br(), Util.zielony, 1);
                } else {
                    Imgproc.rectangle(main.mRgba, rectangle.tl(), rectangle.br(), Util.niebieski, 1);
                }
            }
        }

        main.liczbaSamochodow += Math.max(number - main.poprzedniaLiczbaSamochodow, 0);
        main.poprzedniaLiczbaSamochodow = number;

        Imgproc.rectangle(main.mRgba, new Point(0, 0), new Point(main.mRgba.width(), 40),
                Util.szary, -1);

        main.osobowy.copyTo(main.mRgba.colRange(50, main.osobowy.width() + 50).rowRange(0, main.osobowy.height() + 0));
        main.ciezarowka.copyTo(main.mRgba.colRange(300, main.osobowy.width() + 300).rowRange(0, main.osobowy.height() + 0));


        Imgproc.putText(main.mRgba, " :" + main.liczbaSamochodow, new Point(90, 30), 1, 2, Util.niebieski, 2);
        Imgproc.putText(main.mRgba, " :" + main.liczbaSamochodowCiezarowych, new Point(340, 30), 1, 2, Util.zielony, 2);

        maska.release();

        return main.mRgba;
    }

    private Mat maska() {
        List<MatOfPoint> points = new ArrayList<>();
        MatOfPoint p = new MatOfPoint(main.obszarSprawdzania.pobierzTablicePunktow());
        points.add(p);

        Mat maska = Mat.zeros(main.mRgba.size(), CvType.CV_8UC1);
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
        this.ustawObrazReferencyjny = true;
    }

    public Mat getObrazReferencyjny() {
        return obrazReferencyjny;
    }

    public void resetRef() {
        if (this.obrazReferencyjny != null) {
            this.obrazReferencyjny.release();
        }
        this.obrazReferencyjny = null;
    }
}
