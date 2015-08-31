package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.yang.myphoto4.util.myUtil;

public class Iris extends Activity {
    static final String tag = "eye";
    FaceDetector faceDetector = null;
    FaceDetector.Face[] face;
    Button redEyeBtn = null;
    final int N_MAX = 2;
    private int w_screen;
    private int h_screen;
    float myMidX, myMidY, mMidX, mMidY;

    ProgressBar progressBar = null;
    private ImageView myIrisImage = null;
    int myBlack = 140;
    Bitmap myLeftEye = null;
    Bitmap myRightEye = null;
    int leftEyeWidth, rightEyeWidth, leftEyeHeight, rightEyeHeight, myLeft, myTop, mLeft, mTop;
    RelativeLayout mainLayout;
    private ImageView[] imageViews;

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
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mainLayout = (RelativeLayout)findViewById(R.id.irisView);
        imageViews = new ImageView[2];
        w_screen = dm.widthPixels;
        h_screen = dm.heightPixels;
        initUI();
        initFaceDetect();
        mainHandler.sendEmptyMessage(1);
        checkFaceThread.start();

        (findViewById(R.id.redEye))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        drawEye(redIris((int) (leftEyeHeight * 1.2), leftEyeHeight), redIris((int) (rightEyeHeight * 1.2), rightEyeHeight));
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
        //LayoutParams params = myIrisImage.getLayoutParams();
        createBack();
        //int h = srcImg.getHeight();
        //int w = srcImg.getWidth();
        //float r = (float)h/(float)w;
        //params.width = w_screen;
        //params.height = (int)(params.width * r);
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
            int myRight, myBottom, myLeftEyeX, myLeftEyeY,finalLeftEyeX, myTotalX, myTotalY, finalLeftEyeY;
            myTotalX = 0;
            myTotalY = 0;
            int myCount = 0;
            int myFactor;
            finalLeftEyeX = 0;
            finalLeftEyeY = 0;
            myLeftEyeX = eyeLeft.x -20;
            myLeftEyeY = eyeLeft.y +20;
            myLeft = eyeLeft.x;
            myRight = eyeLeft.x;
            myBottom = eyeLeft.y;
            myTop = eyeLeft.y;
            Boolean isLeftTrue = true;
            Boolean isRightTrue = true;
            Boolean isBottomTrue = true;
            Boolean isTopTrue = true;
            Boolean notLeftEye = true;
            Boolean secAlgo = false;
            Boolean isFinal = false;

            for(i=0;i<=4000;i++){
                if(!isFinal){
                    if(myLeftEyeX<eyeLeft.x +40) {
                        if (myLeftEyeY > eyeLeft.y - 40) {
                            if (myColor.red(mBitmap.getPixel(myLeftEyeX, myLeftEyeY)) < myBlack) {
                                myFactor = 255 - myColor.red(mBitmap.getPixel(myLeftEyeX, myLeftEyeY));
                                myTotalX += myLeftEyeX*myFactor;
                                myTotalY += myLeftEyeY*myFactor;
                                myCount+= myFactor;
                                myLeftEyeY--;
                            } else {
                                myLeftEyeY--;
                            }
                        } else {
                            myLeftEyeY = eyeLeft.y + 20;
                            myLeftEyeX++;
                        }
                    }else{
                        if(myCount>0){
                        finalLeftEyeX = myTotalX/myCount;
                        finalLeftEyeY = myTotalY/myCount;
                        myLeft = finalLeftEyeX;
                        myRight = finalLeftEyeX;
                        myBottom = finalLeftEyeY;
                        myTop = finalLeftEyeY;
                        isFinal = true;
                            notLeftEye = false;
                        }else{
                            secAlgo = true;
                        }
                    }
                }
            }

            if(secAlgo){
            if(myColor.red(mBitmap.getPixel(eyeLeft.x, eyeLeft.y))>myBlack || myColor.red(mBitmap.getPixel(eyeLeft.x, eyeLeft.y+2))>myBlack || myColor.red(mBitmap.getPixel(eyeLeft.x, eyeLeft.y-2))>myBlack
                    || myColor.red(mBitmap.getPixel(eyeLeft.x-2, eyeLeft.y))>myBlack || myColor.red(mBitmap.getPixel(eyeLeft.x+2, eyeLeft.y))>myBlack) {
                myLeft = myLeft+8;
                myRight = myLeft;
                for (i = 0; i < 300; i++) {
                    if (notLeftEye) {
                        if (myColor.red(mBitmap.getPixel(myLeft, myTop)) > myBlack|| myColor.red(mBitmap.getPixel(myLeft+1, myTop)) >myBlack || myColor.red(mBitmap.getPixel(myLeft-1, myTop)) > myBlack
                                || myColor.red(mBitmap.getPixel(myLeft, myTop+1)) > myBlack || myColor.red(mBitmap.getPixel(myLeft, myTop-1)) > myBlack) {
                            myTop--;
                        } else {
                            if ( myColor.red(mBitmap.getPixel(myLeft+2, myTop)) < myBlack && myColor.red(mBitmap.getPixel(myLeft-2, myTop)) < myBlack
                                    && myColor.red(mBitmap.getPixel(myLeft, myTop+2)) < myBlack && myColor.red(mBitmap.getPixel(myLeft, myTop-2)) < myBlack){
                                if ( myColor.red(mBitmap.getPixel(myLeft, myTop+5)) < myBlack && myColor.red(mBitmap.getPixel(myLeft, myTop-5)) < myBlack) {
                                    myTop = myTop -8;
                                    myBottom = myTop;
                                    finalLeftEyeY = myTop;
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
                finalLeftEyeX = myLeft;
                finalLeftEyeY = myTop-8;
                notLeftEye = false;
            }
            }

            if(isFinal && !notLeftEye) {
                for (i = 0; i < 20; i++) {
                    if (isLeftTrue) {
                        if (myColor.red(mBitmap.getPixel(myLeft, finalLeftEyeY)) < myBlack) {
                            myLeft--;
                        } else {
                            if(myColor.red(mBitmap.getPixel(myLeft-7, finalLeftEyeY)) < myBlack){
                                myLeft--;
                            }else{
                            myLeft++;
                            isLeftTrue = false;
                            }
                        }
                    }
                }

                for (i = 0; i < 20; i++) {
                    if (isRightTrue) {
                        if (myColor.red(mBitmap.getPixel(myRight, finalLeftEyeY)) < myBlack) {
                            myRight++;
                        } else {
                            if(myColor.red(mBitmap.getPixel(myRight+7, finalLeftEyeY)) < myBlack){
                                myRight++;
                            }else{
                            myRight--;
                            isRightTrue = false;}

                        }
                    }
                }

                for (i = 0; i < 20; i++) {
                    if (isBottomTrue) {
                        if (myColor.red(mBitmap.getPixel(finalLeftEyeX, myBottom)) < myBlack) {
                            myBottom++;
                        } else {
                            if(myColor.red(mBitmap.getPixel(finalLeftEyeX, myBottom+7)) < myBlack){
                                myBottom++;
                            }else{
                                myBottom--;
                            isBottomTrue = false;}
                        }
                    }
                }

                for (i = 0; i < 20; i++) {
                    if (isTopTrue) {
                        if (myColor.red(mBitmap.getPixel(finalLeftEyeX, myTop)) < myBlack) {
                            myTop--;
                        } else {
                            if(myColor.red(mBitmap.getPixel(finalLeftEyeX, myTop-7)) < myBlack){
                                myTop--;
                            }else{
                            myTop++;
                            isTopTrue = false;}
                        }
                    }
                }
            }


            myMidX = (myLeft + myRight) / 2;
            myMidY = (myBottom + myTop) / 2;

            int mRight, mBottom, mRightEyeX, mRightEyeY,finalRightEyeX, mTotalX, mTotalY, finalRightEyeY;
            mTotalX = 0;
            mTotalY = 0;
            int mCount = 0;
            int mFactor;
            finalRightEyeX = 0;
            finalRightEyeY = 0;
            mRightEyeX = eyeRight.x -20;
            mRightEyeY = eyeRight.y +20;
            mLeft = eyeRight.x;
            mRight = eyeRight.x;
            mBottom = eyeRight.y;
            mTop = eyeRight.y;
            Boolean leftTrue = true;
            Boolean rightTrue = true;
            Boolean topTrue = true;
            Boolean bottomTrue = true;
            Boolean notRightEye = true;
            Boolean isFinish = false;
            Boolean secondAlgo = false;

            for(i=0;i<=4000;i++){
                if(!isFinish){
                    if(mRightEyeX<eyeRight.x +40) {
                        if (mRightEyeY > eyeRight.y - 40) {
                            if (myColor.red(mBitmap.getPixel(mRightEyeX, mRightEyeY)) < myBlack) {
                            mFactor = 255 - myColor.red(mBitmap.getPixel(mRightEyeX, mRightEyeY));
                            mTotalX += mRightEyeX*mFactor;
                            mTotalY += mRightEyeY*mFactor;
                            mCount+= mFactor;
                            mRightEyeY--;
                             } else {
                            mRightEyeY--;
                             }
                        } else {
                            mRightEyeY = eyeRight.y + 20;
                            mRightEyeX++;
                        }
                    }else{
                        if(mCount>0){
                            finalRightEyeX = mTotalX/mCount;
                            finalRightEyeY = mTotalY/mCount;
                            mLeft = finalRightEyeX;
                            mRight = finalRightEyeX;
                            mBottom = finalRightEyeY;
                            mTop = finalRightEyeY;
                            isFinish = true;
                            notRightEye = false;
                        } else{
                            secondAlgo = true;
                        }
                    }
                }
            }

            if(secondAlgo){
                if(myColor.red(mBitmap.getPixel(eyeRight.x, eyeRight.y))>myBlack || myColor.red(mBitmap.getPixel(eyeRight.x, eyeRight.y+2))>myBlack || myColor.red(mBitmap.getPixel(eyeRight.x, eyeRight.y-2))>myBlack
                        || myColor.red(mBitmap.getPixel(eyeRight.x-2, eyeRight.y))>myBlack || myColor.red(mBitmap.getPixel(eyeRight.x+2, eyeRight.y))>myBlack) {
                    mLeft = mLeft - 8;
                    mRight = mLeft;
                    for (i = 0; i < 300; i++) {
                        if (notRightEye) {
                            if (myColor.red(mBitmap.getPixel(mLeft, mTop)) > myBlack || myColor.red(mBitmap.getPixel(mLeft+1, mTop)) >myBlack || myColor.red(mBitmap.getPixel(mLeft-1, mTop)) > myBlack
                                    || myColor.red(mBitmap.getPixel(mLeft, mTop+1)) > myBlack || myColor.red(mBitmap.getPixel(mLeft, mTop-1)) > myBlack) {
                                mTop--;
                            } else {
                                if (myColor.red(mBitmap.getPixel(mLeft+2, mTop)) < myBlack && myColor.red(mBitmap.getPixel(mLeft-2, mTop)) < myBlack
                                        && myColor.red(mBitmap.getPixel(mLeft, mTop+2)) < myBlack && myColor.red(mBitmap.getPixel(mLeft, mTop-2)) < myBlack){
                                    if (myColor.red(mBitmap.getPixel(mLeft, mTop+5)) < myBlack && myColor.red(mBitmap.getPixel(mLeft, mTop-5)) < myBlack){
                                        mTop = mTop - 8;
                                        finalRightEyeY = mTop;
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
                    finalRightEyeX = mLeft;
                    finalLeftEyeY = mTop-8;
                    notRightEye = false;
                }
            }


            if(isFinish && !notRightEye) {
                for (i = 0; i < 20; i++) {
                    if (leftTrue) {
                        if (myColor.red(mBitmap.getPixel(mLeft, finalRightEyeY)) < myBlack) {
                            mLeft--;
                        } else {
                            if(myColor.red(mBitmap.getPixel(mLeft-7, finalRightEyeY)) < myBlack){
                                mLeft--;
                            }else{
                            mLeft++;
                            leftTrue = false;}
                        }
                    }
                }

                for (i = 0; i < 20; i++) {
                    if (rightTrue) {
                        if (myColor.red(mBitmap.getPixel(mRight, finalRightEyeY)) < myBlack) {
                            mRight++;
                        } else {
                            if(myColor.red(mBitmap.getPixel(mRight+7, finalRightEyeY)) < myBlack){
                                mRight++;
                            }else{
                            mRight--;
                            rightTrue = false;}
                        }
                    }
                }

                for (i = 0; i < 20; i++) {
                    if (bottomTrue) {
                        if (myColor.red(mBitmap.getPixel(finalRightEyeX, mBottom)) < myBlack) {
                            mBottom++;
                        } else {
                            if(myColor.red(mBitmap.getPixel(finalRightEyeX, mBottom+7)) < myBlack){
                                mBottom++;
                            }else{
                            mBottom--;
                            bottomTrue = false;}
                        }
                    }
                }

                for (i = 0; i < 20; i++) {
                    if (topTrue) {
                        if (myColor.red(mBitmap.getPixel(finalRightEyeX, mTop)) < myBlack) {
                            mTop--;
                        } else {
                            if(myColor.red(mBitmap.getPixel(finalRightEyeX, mTop-7)) < myBlack){
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

            Log.i(tag, "Left eye L: "+myLeft+" Left eye R: "+myRight);
            Log.i(tag, "Left eye T: "+myTop+" Left eye B: "+myBottom);
            Log.i(tag, "Right eye L: "+mLeft+" Right eye R: "+mRight);
            Log.i(tag, "Right eye T: "+mTop+" Right eye B: "+mBottom);
            Log.i(tag, "Left eye X: "+eyeLeft.x+" Left eye Y: "+eyeLeft.y);
            Log.i(tag, "Right eye X: "+eyeRight.x+" Right eye Y: "+eyeRight.y);
            Log.i(tag, "myTotalX: "+myTotalX+" myTotalY: "+myTotalY+" myCount: "+myCount);
            Log.i(tag, "finalX: "+finalLeftEyeX+" finalY: "+finalLeftEyeY);
            Log.i(tag, "Color int: " + myColor.red(mBitmap.getPixel(eyeLeft.x, eyeLeft.y)));

            mMidX = (mLeft + mRight)/2;
            mMidY = (mBottom + mTop)/2;

            if(checkFace(faceRect)){
                /*Canvas canvas = new Canvas(srcFace);
                Paint p = new Paint();
                p.setAntiAlias(true);
                p.setStrokeWidth(3);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.GREEN);

                canvas.drawCircle(myMidX, myMidY, 20, p);
                canvas.drawCircle(mMidX, mMidY, 20, p);
                canvas.drawBitmap(redIris(20,20),400,400,p);*/

            }
        }
        return srcFace;
    }

    private void drawEye(Bitmap leftIrisBitmap, Bitmap rightIrisBitmap){
        Canvas canvas = new Canvas(srcFace);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.GREEN);
        canvas.drawBitmap(leftIrisBitmap, (int)(myMidX- ((leftEyeHeight*1.2)/2)), myMidY-(leftEyeHeight/2), p);
        canvas.drawBitmap(rightIrisBitmap, (int)(mMidX- ((rightEyeHeight*1.2)/2)), mMidY-(rightEyeHeight/2),p);
        //canvas.drawCircle(myMidX, myMidY, 20, p);
        //canvas.drawCircle(mMidX, mMidY, 20, p);

    }

    private Bitmap redIris(int eyeWidth, int eyeHeight){
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.eye_green);
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) eyeWidth)/ width;
        float scaleHeight = ((float) eyeHeight) / height;
        Matrix mx = new Matrix();
        mx.postScale(scaleWidth,scaleHeight);
        return Bitmap.createBitmap(bm,0,0,width,height,mx,true);
    }

    public void showProcessBar(){
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

    private void myGreen(){
        imageViews[0] = new ImageView(this);
        imageViews[0].setImageBitmap(redIris(leftEyeWidth,leftEyeHeight));
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp1.height = myTop;
        lp1.width = myLeft;
        mainLayout.addView(imageViews[0], lp1);
    }
}