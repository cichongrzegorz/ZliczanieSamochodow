package com.cichon.detection;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public class Rewrite implements ChangedImageReader {
    @Override
    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.gray();
    }

    @Override
    public void zwolnijObrazy() {

    }
}
