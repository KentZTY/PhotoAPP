package com.example.yang.myphoto4;

import com.example.yang.myphoto4.util.myUtil;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class Iris extends Activity {
    FaceDetector faceDetector = null;
    FaceDetector.Face[] face;
    Button redEyeBtn = null;
    final int N_MAX = 2;
    ProgressBar progressBar = null;
    private ImageView myIrisImage = null;
    int myBlack = 40;
    int myWhite = 150;
    Bitmap myLeftIris = null;
    Bitmap myRightIris = null;
    int leftEyeWidth, rightEyeWidth, leftEyeHeight, rightEyeHeight, myLeft, myTop, mLeft, mTop;

    Bitmap srcImg = null;
    Bitmap srcFace = null;
    Thread checkFaceThread = new Thread(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Bitmap faceBitmap = detectFace();
            mainHandler.sendEmptyMessage(2);
            Message m = new Message();
            m.what = 0;
            m.obj = faceBitmap;
            mainHandler.sendMessage(m);
        }

    };

    Handler mainHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Bitmap b = (Bitmap) msg.obj;
                    myIrisImage.setImageBitmap(b);
                    break;
                case 1:
                    showProcessBar();
                    break;
                case 2:
                    progressBar.setVisibility(View.GONE);
                    //redEyeBtn.setClickable(false);
                    break;
                default:
                    break;
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iris);
        initUI();
        initFaceDetect();
        mainHandler.sendEmptyMessage(1);
        checkFaceThread.start();

        (findViewById(R.id.redEye))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {


                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_iris, menu);
        return true;
    }
    public void initUI(){
        redEyeBtn = (Button)findViewById(R.id.redEye);
        myIrisImage =(ImageView)findViewById(R.id.mIrisImage);
        LayoutParams params = myIrisImage.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        //      int h = dm.heightPixels;
        createBack();
        int h = srcImg.getHeight();
        int w = srcImg.getWidth();
        float r = (float)h/(float)w;
        params.width = w_screen;
        params.height = (int)(params.width * r);
        myIrisImage.setImageBitmap(srcImg);
    }

    public void initFaceDetect(){
        this.srcFace = srcImg.copy(Config.RGB_565, true);
        int w = srcFace.getWidth();
        int h = srcFace.getHeight();
        faceDetector = new FaceDetector(w, h, N_MAX);
        face = new FaceDetector.Face[N_MAX];
    }
    public boolean checkFace(Rect rect){
        int w = rect.width();
        int h = rect.height();
        int s = w*h;
        if(s < 10000){
            return false;
        }
        else{
            return true;
        }
    }
    public Bitmap detectFace(){
        //      Drawable d = getResources().getDrawable(R.drawable.face_2);
        //      Log.i(tag, "Drawable尺寸 w = " + d.getIntrinsicWidth() + "h = " + d.getIntrinsicHeight());
        //      BitmapDrawable bd = (BitmapDrawable)d;
        //      Bitmap srcFace = bd.getBitmap();

        int nFace = faceDetector.findFaces(srcFace, face);

        for(int i=0; i<nFace; i++){
            Face f  = face[i];
            PointF midPoint = new PointF();
            float dis = f.eyesDistance();
            f.getMidPoint(midPoint);
            int dd = (int)(dis);
            Point eyeLeft = new Point((int)(midPoint.x - dis/2), (int)midPoint.y);
            Point eyeRight = new Point((int)(midPoint.x + dis/2), (int)midPoint.y);
            Rect faceRect = new Rect((int)(midPoint.x - dd), (int)(midPoint.y - dd), (int)(midPoint.x + dd), (int)(midPoint.y + dd));

            Bitmap mBitmap = toGrayScale(srcFace);
            Color myColor = new Color();
            int myRight, myBottom, myLeftEyeX, myLeftEyeY;

            myLeftEyeY = 0;
            myLeft = eyeLeft.x;
            myRight = eyeLeft.x;
            myBottom = eyeLeft.y;
            myTop = eyeLeft.y;
            Boolean isLeftTrue = true;
            Boolean isRightTrue = true;
            Boolean isBottomTrue = true;
            Boolean isTopTrue = true;

            Boolean notLeftEye = true;

            if(myColor.red(mBitmap.getPixel(eyeLeft.x, eyeLeft.y))>myWhite || myColor.red(mBitmap.getPixel(eyeLeft.x, eyeLeft.y+2))>myWhite || myColor.red(mBitmap.getPixel(eyeLeft.x, eyeLeft.y-2))>myWhite
                    || myColor.red(mBitmap.getPixel(eyeLeft.x-2, eyeLeft.y))>myWhite || myColor.red(mBitmap.getPixel(eyeLeft.x+2, eyeLeft.y))>myWhite) {
                myLeft = myLeft+8;
                myRight = myLeft;
                myLeftEyeX = myLeft;
                for (i = 0; i < 300; i++) {
                    if (notLeftEye) {
                        if (myColor.red(mBitmap.getPixel(myLeft, myTop)) > myWhite || myColor.red(mBitmap.getPixel(myLeft+1, myTop)) > myWhite || myColor.red(mBitmap.getPixel(myLeft-1, myTop)) > myWhite
                                || myColor.red(mBitmap.getPixel(myLeft, myTop+1)) > myWhite || myColor.red(mBitmap.getPixel(myLeft, myTop-1)) > myWhite) {
                            myTop--;
                        } else {
                            if ( myColor.red(mBitmap.getPixel(myLeft+2, myTop)) < myBlack && myColor.red(mBitmap.getPixel(myLeft-2, myTop)) < myBlack
                                    && myColor.red(mBitmap.getPixel(myLeft, myTop+2)) < myBlack && myColor.red(mBitmap.getPixel(myLeft, myTop-2)) < myBlack){
                                if ( myColor.red(mBitmap.getPixel(myLeft, myTop+5)) < myBlack && myColor.red(mBitmap.getPixel(myLeft, myTop-5)) < myBlack) {
                                    myBottom = myTop;
                                    myLeftEyeY = myTop;
                                    notLeftEye = false;
                                }
                                else{
                                    myTop--;
                                }
                            }
                            else{
                                myTop--;
                            }
                        }
                    }
                }
            }else{
                myLeftEyeX = myLeft;
                myLeftEyeY = myTop;
                notLeftEye = false;
            }

            if(!notLeftEye) {
                for (i = 0; i < srcFace.getWidth(); i++) {
                    if (isLeftTrue) {
                        if (myColor.red(mBitmap.getPixel(myLeft, myLeftEyeY)) < myBlack) {
                            myLeft--;
                        } else {
                            if(myColor.red(mBitmap.getPixel(myLeft-3, myLeftEyeY)) < myBlack){
                                myLeft--;
                            }else{
                            myLeft++;
                            isLeftTrue = false;
                            }
                        }
                    }
                }

                for (i = 0; i < srcFace.getWidth(); i++) {
                    if (isRightTrue) {
                        if (myColor.red(mBitmap.getPixel(myRight, myLeftEyeY)) < myBlack) {
                            myRight++;
                        } else {
                            if(myColor.red(mBitmap.getPixel(myRight+3, myLeftEyeY)) < myBlack){
                                myRight++;
                            }else{
                            myRight--;
                            isRightTrue = false;}
                        }
                    }
                }

                for (i = 0; i < srcFace.getHeight(); i++) {
                    if (isBottomTrue) {
                        if (myColor.red(mBitmap.getPixel(myLeftEyeX, myBottom)) < myBlack) {
                            myBottom++;
                        } else {
                            if(myColor.red(mBitmap.getPixel(myLeftEyeX, myBottom+3)) < myBlack){
                                myBottom++;
                            }else{
                                myBottom--;
                            isBottomTrue = false;}
                        }
                    }
                }

                for (i = 0; i < srcFace.getHeight(); i++) {
                    if (isTopTrue) {
                        if (myColor.red(mBitmap.getPixel(myLeftEyeX, myTop)) < myBlack) {
                            myTop--;
                        } else {
                            if(myColor.red(mBitmap.getPixel(myLeftEyeX, myTop-3)) < myBlack){
                                myTop--;
                            }else{
                            myTop++;
                            isTopTrue = false;}
                        }
                    }
                }
            }
            float myMidX = (myLeft + myRight) / 2;
            float myMidY = (myBottom + myTop) / 2;

            int mRight, mBottom, mRightEyeX, mRightEyeY;
            mRightEyeY = 0;
            mLeft = eyeRight.x;
            mRight = eyeRight.x;
            mTop = eyeRight.y;
            mBottom = eyeRight.y;
            Boolean leftTrue = true;
            Boolean rightTrue = true;
            Boolean topTrue = true;
            Boolean bottomTrue = true;
            Boolean notRightEye = true;

            if(myColor.red(mBitmap.getPixel(eyeRight.x, eyeRight.y))>myWhite || myColor.red(mBitmap.getPixel(eyeRight.x, eyeRight.y+2))>myWhite || myColor.red(mBitmap.getPixel(eyeRight.x, eyeRight.y-2))>myWhite
                    || myColor.red(mBitmap.getPixel(eyeRight.x-2, eyeRight.y))>myWhite || myColor.red(mBitmap.getPixel(eyeRight.x+2, eyeRight.y))>myWhite) {
                mLeft = mLeft - 8;
                mRightEyeX = mLeft;
                for (i = 0; i < 300; i++) {
                    if (notRightEye) {
                        if (myColor.red(mBitmap.getPixel(mLeft, mTop)) > myWhite || myColor.red(mBitmap.getPixel(mLeft+1, mTop)) > myWhite || myColor.red(mBitmap.getPixel(mLeft-1, mTop)) > myWhite
                                || myColor.red(mBitmap.getPixel(mLeft, mTop+1)) > myWhite || myColor.red(mBitmap.getPixel(mLeft, mTop-1)) > myWhite) {
                            mTop--;
                        } else {
                            if (myColor.red(mBitmap.getPixel(mLeft+2, mTop)) < myBlack && myColor.red(mBitmap.getPixel(mLeft-2, mTop)) < myBlack
                                    && myColor.red(mBitmap.getPixel(mLeft, mTop+2)) < myBlack && myColor.red(mBitmap.getPixel(mLeft, mTop-2)) < myBlack){
                                if (myColor.red(mBitmap.getPixel(mLeft, mTop+5)) < myBlack && myColor.red(mBitmap.getPixel(mLeft, mTop-5)) < myBlack){
                                    mRightEyeY = mTop;
                                    mBottom = mTop;
                                    notRightEye = false;
                                }else{
                                    mTop--;


                                }
                            }
                            else{
                                mTop--;
                            }
                        }
                    }
                }
            }else{
                mRightEyeX = mLeft;
                mRightEyeY = mTop;
                notRightEye = false;
            }

            if(!notRightEye) {
                for (i = 0; i < srcFace.getWidth(); i++) {
                    if (leftTrue) {
                        if (myColor.red(mBitmap.getPixel(mLeft, mRightEyeY)) < myBlack) {
                            mLeft--;
                        } else {
                            if(myColor.red(mBitmap.getPixel(mLeft-3, mRightEyeY)) < myBlack){
                                mLeft--;
                            }else{
                            mLeft++;
                            leftTrue = false;}
                        }
                    }
                }

                for (i = 0; i < srcFace.getWidth(); i++) {
                    if (rightTrue) {
                        if (myColor.red(mBitmap.getPixel(mRight, mRightEyeY)) < myBlack) {
                            mRight++;
                        } else {
                            if(myColor.red(mBitmap.getPixel(mRight+3, mRightEyeY)) < myBlack){
                                mRight++;
                            }else{
                            mRight--;
                            rightTrue = false;}
                        }
                    }
                }

                for (i = 0; i < srcFace.getHeight(); i++) {
                    if (bottomTrue) {
                        if (myColor.red(mBitmap.getPixel(mRightEyeX, mBottom)) < myBlack) {
                            mBottom++;
                        } else {
                            if(myColor.red(mBitmap.getPixel(mRightEyeX, mBottom+3)) < myBlack){
                                mBottom++;
                            }else{
                            mBottom--;
                            bottomTrue = false;}
                        }
                    }
                }

                for (i = 0; i < srcFace.getHeight(); i++) {
                    if (topTrue) {
                        if (myColor.red(mBitmap.getPixel(mRightEyeX, mTop)) < myBlack) {
                            mTop--;
                        } else {
                            if(myColor.red(mBitmap.getPixel(mRightEyeX, mTop-3)) < myBlack){
                                mTop--;
                            }else{
                            mTop++;
                            topTrue = false;}
                        }
                    }
                }
            }

            leftEyeWidth = myRight - myLeft;
            leftEyeHeight = myBottom - myTop;
            rightEyeWidth = mRight - mLeft;
            rightEyeHeight = mBottom - mTop;

            float mMidX = (mLeft + mRight)/2;
            float mMidY = (mBottom + mTop)/2;

            if(checkFace(faceRect)){
                Canvas canvas = new Canvas(srcFace);
                Paint p = new Paint();
                p.setAntiAlias(true);
                p.setStrokeWidth(8);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.GREEN);

                canvas.drawCircle(myMidX, myMidY, 20, p);
                canvas.drawCircle(mMidX, mMidY, 20, p);
            }
        }
        return srcFace;
    }

    private void drawEye(Bitmap leftIrisBitmap, Bitmap rightIrisBitmap){
        Canvas canvas = new Canvas(srcFace);
        Paint p = new Paint();
        canvas.drawBitmap(leftIrisBitmap, myLeft, myTop, p);
        canvas.drawBitmap(rightIrisBitmap, mLeft, mTop, p);
    }

    private Bitmap redIris(int eyeWidth, int eyeHeight){
        Bitmap redIris = BitmapFactory.decodeResource(getResources(), R.drawable.green);
        return Bitmap.createBitmap(redIris,0,0,eyeWidth, eyeHeight);
    }

    public void showProcessBar(){
        RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.irisView);
        progressBar = new ProgressBar(Iris.this, null, android.R.attr.progressBarStyleLargeInverse); //ViewGroup.LayoutParams.WRAP_CONTENT
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setLayoutParams(params);
        mainLayout.addView(progressBar, params);
    }

    public static Bitmap toGrayScale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Uri createBack() {
        final Uri uri = getIntent().getData();
        String filePath = getPath(uri);
        System.out.print(filePath);
        srcImg = myUtil.getBitmap(filePath);
        return uri;
    }
}