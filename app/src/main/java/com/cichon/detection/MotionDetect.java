package com.cichon.detection;

import com.cichon.MainActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class MotionDetect implements ChangedImageReader {

    CascadeClassifier cascadeClassifier = new CascadeClassifier("/storage/emulated/0/data/apps/cars.xml");

    private final MainActivity data;

    public MotionDetect(MainActivity mainActivity) {
        this.data = mainActivity;

        System.out.println("************");
//        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
//        System.out.println(mainActivity.getFilesDir().getAbsolutePath() + "/cars.xml");
//        cascadeClassifier.load(mainActivity.getFilesDir().getAbsolutePath() + "/cars.xml");
        System.out.println("************");
        System.out.println("************");
        System.out.println("************");
    }

    @Override
    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        data.counter++;
//        if (data.counter % 3 == 0) {
//            return data.mRgba;
//        }
        data.mRgba = data.skalaSzarosci ? inputFrame.gray() : inputFrame.rgba();

        Mat gray = inputFrame.gray();

        MatOfRect cars = new MatOfRect();
        cascadeClassifier.detectMultiScale(gray, cars, 1.1, 2);

        for (Rect r : cars.toList()) {
            Imgproc.rectangle(data.mRgba, r.tl(), r.br(), new Scalar(0, 0, 255), 1);
        }

        return data.mRgba;
    }

    @Override
    public void zwolnijObrazy() {

    }
}
