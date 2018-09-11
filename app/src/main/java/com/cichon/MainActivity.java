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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cichon.detection.Bitwise;
import com.cichon.detection.ChangedImageReader;
import com.cichon.detection.Gaussian;
import com.cichon.detection.Rewrite;
import com.cichon.gaussian.MixtureOfGaussianBackground;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2, View.OnTouchListener, View.OnLongClickListener {

    public int lowTreshold = 80;
    public int highTreshold = 200;
    public int rozmiar = 300;
    public boolean skalaSzarosci;

    private TextView lowTresholdTV;
    private TextView highTresholdTV;

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    public Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;
    public Mat mIntermediateMat;
    private Mat mGray;
    public Mat hierarchy;
    private RelativeLayout tv;
    private Settings settings = new Settings();
    public MixtureOfGaussianBackground videoProcessor;

    private ChangedImageReader reader;
    private int mGameWidth;
    private int mGameHeight;
    public Mat mRgbaPrev;
    public String wybranaOpcja;
    public int liczbaSamochodow = 0;
    public int poprzedniaLiczbaSamochodow = 0;


    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
//                    Log.i(TAG, "OpenCV loaded successfully");

//                    Environment.
//
//                    InputStream is = getResources().openRawResource(R.raw.object_detector);
//                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//                    mCascadeFile = new File(cascadeDir, "cascade.xml");
//                    FileOutputStream os = new FileOutputStream(mCascadeFile);
//
//
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//
//                    while ((bytesRead = is.read(buffer)) != -1)
//                    {
//                        os.write(buffer, 0, bytesRead);
//                        Log.d(TAG, "buffer: " + buffer.toString());
//                    }
//                    is.close();
//                    os.close();
//// Load the cascade classifier
//                    cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
//                    cascadeClassifier.load(mCascadeFile.getAbsolutePath());
//                    if (cascadeClassifier.empty()) {
//                        Log.e(TAG, "Failed to load cascade classifier");
//                        cascadeClassifier = null;
//                    }

//                    reader = new Rewrite();
//                    reader = new Bitwise(MainActivity.this);
                    reader = new Gaussian(MainActivity.this);


                    videoProcessor = new MixtureOfGaussianBackground(20, 1000);
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
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
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

//        mRgba = new Mat(height, width, CvType.CV_8UC4);
//        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
//        mRgbaT = new Mat(width, width, CvType.CV_8UC4);

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaPrev = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        hierarchy = new Mat();
    }

    public void onCameraViewStopped() {
//        mRgba.release();
        mRgba.release();
        mRgbaPrev.release();
        mGray.release();
        mIntermediateMat.release();
        hierarchy.release();
        reader.zwolnijObrazy();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return reader.readImage(inputFrame);
    }

    public int counter = 0;


    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    0);
        } else {
            // Permission has already been granted
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.show_camera);
        requestPermission(Manifest.permission.CAMERA);
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.settings, null);

//        tv = v.findViewById(R.id.settingsLayout);


//        lowTresholdTV = findViewById(R.id.lowTreshold);
//        highTresholdTV = findViewById(R.id.highTreshold);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(MainActivity.this);
        mOpenCvCameraView.setOnTouchListener(MainActivity.this);
//        mOpenCvCameraView.setOnLongClickListener(MainActivity.this);

//        Button reset = (Button) findViewById(R.id.reset);
//        reset.setText(getFilesDir().getAbsolutePath());
//        reset.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                settings.reset();
//                zmienionoUstawienia();
//            }
//        });

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
        wybranaOpcja = settings.wybranaOpcja;

//        lowTresholdTV.setText("low treshold: " + lowTreshold);
//        highTresholdTV.setText("high treshold: " + highTreshold);
    }

    long then = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            then = (Long) System.currentTimeMillis();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (((Long) System.currentTimeMillis() - then) > 1200) {
                ((Gaussian) reader).setReferenceFrame();
                this.liczbaSamochodow = 0;
                this.poprzedniaLiczbaSamochodow = 0;
                return true;
            }
        }
//        ((Gaussian) reader).resetRef();

        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

//        float x = touchMajor;//event.getX() - touchMajor/2 ;//- ((v.getWidth() - mGameWidth) / 2);
//        float y = touchMinor;//event.getY() - touchMinor/2;//- ((v.getHeight() - mGameHeight) / 2);
        float x = event.getX() - ((v.getWidth() - mGameWidth) / 2);
        float y = event.getY() - ((v.getHeight() - mGameHeight) / 2);
//        float x = event.getX();// -((v.getWidth() - mGameWidth) / 2);
//        float y = event.getY();// -((v.getHeight() - mGameHeight) / 2);


        System.out.println(v.getX());
        System.out.println(v.getY());

        System.out.println(v.getPivotX());
        System.out.println(v.getPivotY());

        System.out.println(v.getRootView().getPaddingLeft());
        System.out.println(v.getRootView().getPaddingTop());

        if (p1x != null && p1y != null && p2x != null && p2y != null && p3x != null && p3y != null && p4x != null && p4y != null) {
            p1x = null;
            p1y = null;
            p2x = null;
            p2y = null;
            p3x = null;
            p3y = null;
            p4x = null;
            p4y = null;
        }

        if (p1x == null && p1y == null) {
            p1x = x;
            p1y = y;
        } else if (p2x == null && p2y == null) {
            p2x = x;
            p2y = y;
        } else if (p3x == null && p3y == null) {
            p3x = x;
            p3y = y;
        } else if (p4x == null && p4y == null) {
            p4x = x;
            p4y = y;
        }
        return true;
    }

    public Float p1x;
    public Float p1y;
    public Float p2x;
    public Float p2y;
    public Float p3x;
    public Float p3y;
    public Float p4x;
    public Float p4y;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean maWszystkiePunkty() {
        return
                p1x != null &&
                        p2x != null &&
                        p3x != null &&
                        p4x != null &&
                        p1y != null &&
                        p2y != null &&
                        p3y != null &&
                        p4y != null;
    }

    @Override
    public boolean onLongClick(View v) {
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        System.out.println("************** REFFFFFFFFf");
        ((Gaussian) reader).setReferenceFrame();
        return true;
    }
}
