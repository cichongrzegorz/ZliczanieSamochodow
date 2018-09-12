package com.cichon;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cichon.detection.Gaussian;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2, View.OnTouchListener {

    private TrybAplikacji trybAplikacji = new TrybAplikacji();
    public ObszarSprawdzania obszarSprawdzania = new ObszarSprawdzania();

    public int lowTreshold = 80;
    public int highTreshold = 200;
    public int rozmiar = 300;
    public boolean skalaSzarosci;

    private CameraBridgeViewBase mOpenCvCameraView;

    public Mat mRgba;
    private Settings settings = new Settings();

    private Gaussian reader;
    private int mGameWidth;
    private int mGameHeight;

    public int liczbaSamochodow = 0;
    public int liczbaSamochodowOsobowych = 0;
    public int liczbaSamochodowCiezarowych = 0;
    public int poprzedniaLiczbaSamochodow = 0;
    public Mat osobowy;


    public Mat ciezarowka;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {

                    osobowy = new Mat();
                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setImageResource(R.raw.car);
                    Util.wczytajMatDlaImageView(osobowy, imageView);

                    ciezarowka = new Mat();
                    imageView = new ImageView(MainActivity.this);
                    imageView.setImageResource(R.raw.truck);
                    Util.wczytajMatDlaImageView(ciezarowka, imageView);

//                    reader = new Rewrite(MainActivity.this);
                    reader = new Gaussian(MainActivity.this);
                    mOpenCvCameraView.enableView();

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        mGameWidth = width;
        mGameHeight = height;

        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        reader.zwolnijObrazy();
        this.osobowy.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return reader.readImage(inputFrame);
    }

    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.show_camera);
        requestPermission(Manifest.permission.CAMERA);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(MainActivity.this);
        mOpenCvCameraView.setOnTouchListener(MainActivity.this);

        settings = new Settings();

    }

    public void openDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.settings, null);

        settings.initialize(alertLayout);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Ustawienia");
        alert.setView(alertLayout);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                zmienionoUstawienia();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void zmienionoUstawienia() {
        lowTreshold = settings.lowTreshold;
        highTreshold = settings.highTreshold;
        skalaSzarosci = settings.isSkalaSzarosci;
        rozmiar = settings.rozmiar;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        if (trybAplikacji.getTryb() == TrybAplikacji.Tryb.LICZENIE) {
            return true;
        }

        if (trybAplikacji.getTryb() == TrybAplikacji.Tryb.OBRAZ_REFERENCYJNY) {
            ((Gaussian) reader).setReferenceFrame();
            this.liczbaSamochodow = 0;
            this.poprzedniaLiczbaSamochodow = 0;
            ustawLiczenie();
            return true;
        }

        if (obszarSprawdzania.obszarUstawiony()) {
            obszarSprawdzania.resetuj();
        }

        float x = event.getX() - ((v.getWidth() - mGameWidth) / 2);
        float y = event.getY() - ((v.getHeight() - mGameHeight) / 2);


        obszarSprawdzania.dodajNastepny(x, y);
        if (obszarSprawdzania.obszarUstawiony()) {
            ustawLiczenie();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tab_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openDialog();
            return true;
        }
        if (id == R.id.zliczanie) {
            ustawLiczenie();
            return true;
        }
        if (id == R.id.obrazReferencyjny) {
            trybAplikacji.ustawObrazReferencyjny();
            setTitle("Proszę ustaw obraz wzorcowy (kliknij na ekranie)");
            return true;
        }
        if (id == R.id.ustawieniePunktow) {
            trybAplikacji.ustawPunkty();
            setTitle("Proszę wybierz obszar na ekranie (4 punkty)");
            return true;
        }
        if (id == R.id.resetuj) {
            this.resetuj();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ustawLiczenie() {
        trybAplikacji.ustawLiczenie();
        if (obszarSprawdzania.obszarUstawiony() && this.reader.getRef() != null) {
            setTitle("Liczenie samochodów");
            return;
        }
        if (!obszarSprawdzania.obszarUstawiony() && this.reader.getRef() == null) {
            setTitle("Proszę ustawić obraz referencyjny i obszar");
            return;
        }
        if (!obszarSprawdzania.obszarUstawiony()) {
            setTitle("Proszę ustawić obszar");
            return;
        }
        setTitle("Proszę ustawić obraz referencyjny");
    }

    public void narysujPunkty(Mat mat) {
        Point[] punkty = obszarSprawdzania.pobierzTablicePunktow();
        for (int i = 0; i < punkty.length; i++) {
            Imgproc.circle(mat, punkty[i], 3, Util.pomaranczowy, -1);
        }
    }

    public void narysujKreski(Mat mat) {

        Point[] punkty = obszarSprawdzania.pobierzTablicePunktow();
        if (punkty.length <= 1) {
            return;
        }


        for (int i = 1; i < punkty.length; i++) {
            Imgproc.line(mat, punkty[i], punkty[i - 1], Util.pomaranczowy, 3);
        }
        if (obszarSprawdzania.obszarUstawiony()) {
            Imgproc.line(mat, punkty[0], punkty[punkty.length - 1], Util.pomaranczowy, 3);
        }
    }

    private void resetuj() {
        this.reader.resetRef();
        this.obszarSprawdzania.resetuj();
        ustawLiczenie();
    }
}
