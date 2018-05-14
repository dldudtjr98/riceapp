package com.dldud.riceapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dldud.riceapp.MainActivity.navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment{

    static String picturefilename;
    static String videofilename;

    boolean recording = false;

    final int CAMERA_FRAGMENT_PERMISSION = 1111;

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    private static Context myContext;
    private Camera mCamera;
    private CameraPreview mPreview;

    private MediaRecorder recorder;
    private Button captureBtn;
    private Button recordBtn;
    private Button gotoFeed;
    private Button gotoMap;
    private String folderPath;
    private OrientationEventListener orientationEventListener;
    private String orientationCamera;


    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        myContext = getActivity();


        if (Build.VERSION.SDK_INT >= 23){
            checkPermissions();
        }




        folderPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        createFolder();

        LinearLayout camera_preview;
        camera_preview = (LinearLayout) v.findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(myContext, mCamera);
        camera_preview.addView(mPreview);
        mCamera = Camera.open();
        mPreview.refreshCamera(mCamera);

        recordBtn = (Button) v.findViewById(R.id.recordBtn);
        recordBtn.setOnClickListener(videoListener);

        captureBtn = (Button) v.findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(captureListener);
        captureBtn.setOnLongClickListener(longcaptureListener);

        gotoFeed = (Button) v.findViewById(R.id.gotoFeed);
        gotoFeed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigation.setSelectedItemId(R.id.navigation_feed);
            }
        });

        gotoMap = (Button) v.findViewById(R.id.gotoMap);
        gotoMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigation.setSelectedItemId(R.id.navigation_map);
            }
        });
        return v;
    }

    View.OnClickListener captureListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            picturefilename = "lis_capture_" + GetRandName();
            mCamera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);

        }
    };

    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = Camera.open();
            mPreview.refreshCamera(mCamera);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

    }

    View.OnLongClickListener longcaptureListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            captureBtn.setVisibility(View.GONE);
            recordBtn.setVisibility(View.VISIBLE);
            videofilename= "lis_video_" + GetRandName();
            final String mp4Route = "/storage/emulated/0/LIS/" + videofilename + ".mp4";

            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                try{
                    if(recorder == null){
                        recorder = new MediaRecorder();
                    }
                    final Intent video = new Intent(getActivity(),VideoHolderActivity.class);
                    mCamera.unlock();
                    startVideoRecording();
                    recorder.setOutputFile(mp4Route);
                    recorder.prepare();
                    recorder.start();
                    recording = true;
                    //release when max duration
                    recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                        @Override
                        public void onInfo(MediaRecorder mr, int what, int extra) {
                            recorder.stop();
                            recorder.reset();
                            recorder.release();
                            mCamera.lock();
                            recorder = null;
                            recording = false;
                            startActivity(video);
                        }
                    });
                    Toast.makeText(getActivity(),"촬영시작",Toast.LENGTH_LONG).show();
                } catch (Exception e){
                    e.printStackTrace();
                    recorder.release();
                    recorder = null;
                    recording = false;
                }
                }
            });
            return true;
        }
    };

    View.OnClickListener videoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        if(recording){
            Intent video_end = new Intent(getActivity(),VideoHolderActivity.class);
            recorder.stop();
            recorder.reset();
            recorder.release();
            mCamera.lock();
            recorder = null;
            recording = false;
            captureBtn.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.GONE);
            startActivity(video_end);
        }
        }
    };

    private boolean checkPermissions(){
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions){
            result = ContextCompat.checkSelfPermission(getActivity(),pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }
        if(!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(getActivity(),
                    permissionList.toArray(new String[permissionList.size()]),
                    CAMERA_FRAGMENT_PERMISSION);
            return false;
        }
        return true;
    }



    private void createFolder(){
        String title = "/LIS";
        folderPath += title;
        File cameraDir = new File(folderPath);
        cameraDir.mkdirs();
    }

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        captureBtn.setEnabled(true);
        }
    };

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    //button callback_raw
    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    //button callback_jpg
    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);

        String jpgRoute = "/storage/emulated/0/lis/"+ picturefilename + ".jpg";
        File copyFile = new File(jpgRoute);
        final Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmapPicture = Bitmap.createBitmap(bitmapPicture,0,0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), matrix, true);

        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(copyFile);
            bitmapPicture.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            Intent pic = new Intent(getActivity(),PictureHolderActivity.class);
            startActivity(pic);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        }
    };

    private void startVideoRecording(){

        recorder.setCamera(mCamera);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        recorder.setMaxDuration(9000);
        recorder.setOrientationHint(90);
        recorder.setMaxFileSize(0);
        recorder.setPreviewDisplay(mPreview.mHolder.getSurface());
    }

    public static String GetRandName(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        String getTime = sdf.format(date);
        int RandNum = (int)(Math.random()*100000);
        String getNum = String.valueOf(RandNum);
        String GetName = getTime + getNum;

        return GetName;
    }

}




