package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends Activity {
    private final Timer timer = new Timer();
    int i = 0;    //
    Bundle bundle = null; //
    private View layout;
    private Camera camera;
    private Camera.Parameters parameters = null;
    private TimerTask task;
    private int mCount = 0;
    private int mCaptureCount = 0;
    private LinearLayout linearLayout;
    private List<String> listPath = new ArrayList<String>();
    private String cameraPath;
    private Button takePicture;
    private Bitmap HDRbitmap = null;
    private Paint paint = null;
    private Canvas canvas = null;
    private Uri uri;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        layout = this.findViewById(R.id.buttonLayout);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout_images);
        takePicture = (Button) findViewById(R.id.takepicture);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder()
                .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(176, 144);
        surfaceView.getHolder().setKeepScreenOn(true);//
        surfaceView.getHolder().addCallback(new SurfaceCallback());

        takePicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount = 0;
                if (timer != null) {
                    if (task != null) {
                        task.cancel();
                    }
                }
                task = new MyTimerTask();
                timer.schedule(task, 100, 2000);
            }
        });
    }

    //save image
    public void saveBitmap(Bitmap bm) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmm", Locale.UK);
        Date now = new Date();
        String fileName = formatter.format(now) + ".png";
        File f;
        try {
            cameraPath = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + "Pictures"
                    + File.separator
                    + fileName;
            f = new File(cameraPath);
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bm, null, null));
            //MediaStore.Images.Media.insertImage(this.getContentResolver(), cameraPath, fileName, null);
            //CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cameraPath))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getImageView(byte[] data) {
        mCaptureCount++;
        final View view = getLayoutInflater().inflate(R.layout.image_item, null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.photoshare_item_image);
        imageView.setImageBitmap(getCompressImageBitmap(data));
        linearLayout.addView(view);
        try {
            if (mCount == 1) {
                int width = BitmapFactory.decodeByteArray(data, 0, data.length).getWidth();
                int height = BitmapFactory.decodeByteArray(data, 0, data.length).getHeight();
                HDRbitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                paint = new Paint();
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
                canvas = new Canvas(HDRbitmap);
            }
            canvas.drawBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), 0, 0, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCount == 3) {
            saveBitmap(HDRbitmap);
            Intent intent = new Intent();
            intent.setData(uri);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private Bitmap getCompressImageBitmap(byte[] data) {
        Bitmap bmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        //opts.inSampleSize = ImageTools.computeSampleSize(opts, -1, 150*150);
        opts.inSampleSize = 16;
        opts.inJustDecodeBounds = false;
        try {
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (OutOfMemoryError e) {
        }
        return bmp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (HDRbitmap != null) {
            HDRbitmap.recycle();
            HDRbitmap = null;
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (camera != null && mCount <= 3) {
                parameters = camera.getParameters();
                mCount++;
                if (mCount == 1) {
                    parameters.setExposureCompensation(-2);
                } else if (mCount == 2) {
                    parameters.setExposureCompensation(0);
                } else if (mCount == 3) {
                    parameters.setExposureCompensation(2);
                }
                camera.setParameters(parameters);
                camera.takePicture(null, null, new MyPictureCallback());
            }
        }
    }

    private final class SurfaceCallback implements Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            parameters = camera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPreviewSize(width, height);
            parameters.setPreviewFrameRate(5);
            parameters.setPictureSize(width, height);
            parameters.setJpegQuality(80);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open();
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }

        public int getPreviewDegree(Activity activity) {
            int rotation = activity.getWindowManager().getDefaultDisplay()
                    .getRotation();
            int degree = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degree = 90;
                    break;
                case Surface.ROTATION_90:
                    degree = 0;
                    break;
                case Surface.ROTATION_180:
                    degree = 270;
                    break;
                case Surface.ROTATION_270:
                    degree = 180;
                    break;
            }
            return degree;
        }
    }

    private final class MyPictureCallback implements PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                camera.startPreview();
                getImageView(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}