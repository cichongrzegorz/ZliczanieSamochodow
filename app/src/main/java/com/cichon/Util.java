package com.cichon;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Util {

    public static final Scalar szary = new Scalar(238, 238, 238);
    public static final Scalar zielony = new Scalar(181, 229, 29);
    public static final Scalar niebieski = new Scalar(0, 163, 232);
    public static final Scalar pomaranczowy = new Scalar(255, 127, 38);
    public static final Scalar bialy = new Scalar(255, 255, 255);

    public static Mat wczytajMatDlaImageView(Mat mat, ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        return mat;
    }
}
