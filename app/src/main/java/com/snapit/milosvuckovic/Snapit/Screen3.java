package com.snapit.milosvuckovic.Snapit;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import id.zelory.compressor.Compressor;

public class Screen3 extends AppCompatActivity {
    private Context context= this;
    String imeFajla;
    int velicina;
    String putanja1;
    private static final int REQUEST_CODE_CAMERA =1 ;
    private CameraDevice mCameraDevice = null;
    private CaptureRequest.Builder mCaptureRequestBuilder = null;
    private CameraCaptureSession mCameraCaptureSession = null;
    private TextureView mTextureView = null;
    private Size mPreviewSize = null;
    private static final int REQUEST_CAMERA_RESULT = 2;
    File compressedImage;
    private ImageButton butTakePicture;
    //OnCreate klasa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fullscreen, sakriven statusbar...
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_screen3);
        //za kameru
        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        //dugme za slikanje
        butTakePicture= findViewById(R.id.button);
        butTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                butTakePicture.setEnabled(false);
                takePicture(view);
            }
        });
    }
    //izlazak iz aplikacije
    public void onClickClose(View view) {
        finish();
        System.exit(0);
    }
    //Add the  following Comparator class
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight()
                    - (long) rhs.getWidth() * rhs.getHeight());
        }
    }
    //Add the following CameraDevice.StateCallback :
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            if (texture == null) {
                return;
            }
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface surface = new Surface(texture);
            try {
                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mCaptureRequestBuilder.addTarget(surface);
            try {
                mCameraDevice.createCaptureSession(Arrays.asList(surface), mPreviewStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onError(CameraDevice camera, int error) {
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
        }
    };
    //Add the following SurfaceTextureListener:
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
        @Override
        public void onSurfaceTextureSizeChanged(
                SurfaceTexture surface, int width, int height) {
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }
    };
    // Add the following CameraCaptureSession.StateCallback:
    private CameraCaptureSession.StateCallback mPreviewStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            startPreview(session);
        }
        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };
    // Add the following methods to override onPause() and onResume() :
    @Override
    protected void onPause() {
        super.onPause();
        butTakePicture.setEnabled(true);
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mTextureView.isAvailable()) {
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }
    //Add the openCamera() method:
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    String cameraId = manager.getCameraIdList()[0];
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
                    manager.openCamera(cameraId, mStateCallback, null);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        //Toast.makeText(this, "No permission to use the camera services", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_RESULT);
                }
            } else {
                String cameraId = manager.getCameraIdList()[0];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
                manager.openCamera(cameraId, mStateCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    //runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_RESULT:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Snapit zahteva pristup kameri!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //Add the startPreview() method:
    private void startPreview(CameraCaptureSession session) {
        mCameraCaptureSession = session;
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread backgroundThread = new HandlerThread("CameraPreview");
        backgroundThread.start();
        Handler backgroundHandler = new Handler(backgroundThread.getLooper());
        try {
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    //Add the getPictureFile() method:
    private File getPictureFile() {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss"). format(System.currentTimeMillis());
        String fileName = androidID+ "_" + timeStamp + ".jpeg";
        return new File(getCacheDir(), fileName);
    }
    //Rotacija slike za 90 stepeni ako je potrebno
    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    static{
        ORIENTATION.append(Surface.ROTATION_0,90);
        ORIENTATION.append(Surface.ROTATION_90,0);
        ORIENTATION.append(Surface.ROTATION_180,270);
        ORIENTATION.append(Surface.ROTATION_270,180);
    }
    // Add the takePicture() that saves the image file:
    protected boolean takePicture(View view) {
        if (null == mCameraDevice) {
            return false;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSize= null;
            if (characteristics != null)
                jpegSize = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            //Slika sa odredjenom velicinom
            int width = 640;
            int height = 480;
            if (jpegSize != null && jpegSize.length > 0) {
                width = jpegSize[0].getWidth();
                height = jpegSize[0].getHeight();
            }
            //StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            //if (configurationMap == null) return;
            // Size largest = Collections.max(Arrays.asList(configurationMap.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //Provera orijentacije
            int rotation= getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        OutputStream output = new FileOutputStream(getPictureFile());
                        Log.d("Nekompresovana slika","Prikaz nekompresovana "+ getPictureFile());
                        output.write(bytes);
                        //  File compressedImageFile = new Compressor(Screen3.this).compressToFile(getPictureFile());
                        String imeDir = "slike";
                        File Cachedir = new File(getCacheDir().getPath()+"/"+imeDir+"/");
                        Cachedir.mkdirs();
                        compressedImage = new Compressor(Screen3.this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(Cachedir.getAbsolutePath())
                                .compressToFile(getPictureFile());
                        putanja1=compressedImage.getAbsolutePath();
                        output.close();
                        String putanja = getPictureFile().getAbsolutePath();
                        Intent i = new Intent(Screen3.this, Screen4.class);
                        i.putExtra("slika", putanja);
                        i.putExtra("kompresovana", putanja1);
                        startActivity(i);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
            };
            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroudHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroudHandler);
            final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //Toast.makeText(Screen3.this, "Slika Sacuvana", Toast.LENGTH_SHORT).show();
                    // startPreview(session);
                }
            };
            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureCallback, backgroudHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, backgroudHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return true;
    }
    //Izlaz iz aplikacije preko android back dugmeta
    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }
}