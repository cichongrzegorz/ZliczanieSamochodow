package com.cichon.detection;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public interface ChangedImageReader {
    Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame);

    void zwolnijObrazy();
}
