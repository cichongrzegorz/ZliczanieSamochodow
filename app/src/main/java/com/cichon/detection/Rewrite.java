package com.cichon.detection;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.cichon.MainActivity;
import com.cichon.R;
import com.cichon.Util;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Rewrite {
    private final MainActivity data;

    Mat im;

    public Rewrite(MainActivity mainActivity) {
        this.data = mainActivity;
        this.im = new Mat();
    }

    public Mat readImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        ImageView imageView = new ImageView(this.data);
        imageView.setImageResource(R.mipmap.app);
        return Util.wczytajMatDlaImageView(this.im, imageView);
    }

    public void zwolnijObrazy() {

    }
}
