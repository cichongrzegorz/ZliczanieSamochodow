package com.cichon.detection;

import com.cichon.MainActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class CurrentReader implements ChangedImageReader {

    private final MainActivity data;

    public CurrentReader(MainActivity mainActivity) {
        this.data = mainActivity;
    }

    @Override
    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if (data.p1x != null && data.p1y != null && data.p2x != null && data.p2y != null) {
            Imgproc.line(data.mRgba, new Point(data.p1x, data.p1y), new Point(data.p2x, data.p2y), new Scalar(0, 255, 255), 3);
        }

        data.counter++;
        if (data.counter % 3 == 0) {
            return data.mRgba;
        }
        data.mRgba = data.skalaSzarosci ? inputFrame.gray() : inputFrame.rgba();
        resize();


//        Imgproc.drawContours(mRgba, contours, -1, new Scalar(0, 0, 255));//, 2, 8, hierarchy, 0, new Point());
        // Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
        return data.mRgba;
    }

    private void resize2() {
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        data.hierarchy = new Mat();

        Imgproc.Canny(data.mRgba, data.mIntermediateMat, data.lowTreshold, data.highTreshold);
        Imgproc.findContours(data.mIntermediateMat, contours, data.hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
    /* Mat drawing = Mat.zeros( mIntermediateMat.size(), CvType.CV_8UC3 );
     for( int i = 0; i< contours.size(); i++ )
     {
    Scalar color =new Scalar(Math.random()*255, Math.random()*255, Math.random()*255);
     Imgproc.drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, new Point() );
     }*/
        data.hierarchy.release();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);
            double currentArea = Imgproc.contourArea(currentContour);
            if (currentArea > data.rozmiar) {
                Rect rectangle = Imgproc.boundingRect(currentContour);
                Imgproc.rectangle(data.mRgba, rectangle.tl(), rectangle.br(), new Scalar(0, 0, 255), 1);
            }
        }
    }

    void resize() {
//        resize(currentImage, currentImage, new Size(640, 360));
        Mat foregroundImage = data.mRgba.clone();
        foregroundImage = data.videoProcessor.process(foregroundImage);

        Mat foregroundClone = foregroundImage.clone();
        Imgproc.bilateralFilter(foregroundClone, foregroundImage, 2, 1600, 400);

//        if (isBGSview) {
//            resize(foregroundImage, ImageBGS, new Size(430, 240));
//            BGSview.setIcon(new ImageIcon(imageProcessor.toBufferedImage(ImageBGS)));
//        }


        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        data.hierarchy = new Mat();



//        Imgproc.Canny(mRgba, mIntermediateMat, lowTreshold, highTreshold);
        Imgproc.findContours(foregroundImage, contours, data.hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
    /* Mat drawing = Mat.zeros( mIntermediateMat.size(), CvType.CV_8UC3 );
     for( int i = 0; i< contours.size(); i++ )
     {
    Scalar color =new Scalar(Math.random()*255, Math.random()*255, Math.random()*255);
     Imgproc.drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, new Point() );
     }*/
        data.hierarchy.release();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);
            double currentArea = Imgproc.contourArea(currentContour);
            if (currentArea > data.rozmiar) {
                Rect rectangle = Imgproc.boundingRect(currentContour);
                Imgproc.rectangle(data.mRgba, rectangle.tl(), rectangle.br(), new Scalar(0, 0, 255), 1);
            }
        }

//        CountVehicles countVehicles = new CountVehicles(areaThreshold, vehicleSizeThreshold, lineCount1, lineCount2, lineSpeed1, lineSpeed2, crossingLine, crossingSpeedLine);
//        countVehicles.findAndDrawContours(currentImage, foregroundImage);
    }

    @Override
    public void zwolnijObrazy() {

    }
}
