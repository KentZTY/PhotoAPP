package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.Display;
import android.R.anim;


import static android.R.anim.*;

public class DisplayImageActivity extends Activity {
    private int screenWidth;
    private int screenHeight;
    private int i;
    private myImageView currentImage;
    private myImageView[] imageView;
    RelativeLayout mainLayout;
    private ImageView myImage;
    private ImageView borderImage;
    private static final int sticker = 1;
    private static final int border = 2;
    private static final int NONE = 3;
    private static final int DRAG = 4;
    private static final int ZOOM_OR_ROTATE = 5;
    private static final int DELETE = 6;
    int mode = NONE;
    Paint paint;
    String myPath;
    public static DisplayImageActivity instance = null;
    private Button stickerButton, clearButton, borderButton, openButton, saveButton;
    private Animation animationTranslate, animationRotate, animationScale;
    private static int width, height;
    private RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
    private RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(0, 0);
    private static Boolean isClick = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        mainLayout = (RelativeLayout)findViewById(R.id.stickerView);
        myImage =(ImageView)findViewById(R.id.imageView);
        borderImage =(ImageView)findViewById(R.id.borderView);
        borderImage.setImageDrawable(null);
        i = 0;
        myPath = null;
        instance = this;
        imageView = new myImageView[100];
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 100;
        currentImage = null;
        paint = new Paint();
        myImage.setOnTouchListener(movingEventListener);
        initialButton();
        createBack();


        /*(findViewById(R.id.sticker))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        chooseSticker();
                    }
                });
        (findViewById(R.id.clear))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        clearStickers();
                    }
                });
        (findViewById(R.id.border))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        chooseBorder();
                    }
                });


        /*
        (findViewById(R.id.button))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        test();
                    }
                });
                */
        /*
         Send image to the next activity.
         */
        /*(findViewById(R.id.save))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        shareImage();
                    }
                });*/
        }

    private void initialButton()
    {
        // TODO Auto-generated method stub
        Display display = getWindowManager().getDefaultDisplay();
        height = display.getHeight();
        width = display.getWidth();
        //Log.v("width  & height is:", String.valueOf(width) + ", " + String.valueOf(height));

        params.height = 200;
        params.width = 200;
        params2.height = 200;
        params2.width = 200;
        //ÉèÖÃ±ß¾à  (int left, int top, int right, int bottom)
        params.setMargins(50, height -300, 0, 0);
        params2.setMargins(width - 250, height - 300, 0, 0);

        saveButton = (Button) findViewById(R.id.save);
        saveButton.setLayoutParams(params2);

        clearButton = (Button) findViewById(R.id.clear);
        clearButton.setLayoutParams(params);

        stickerButton = (Button) findViewById(R.id.sticker);
        stickerButton.setLayoutParams(params);

        borderButton = (Button) findViewById(R.id.border);
        borderButton.setLayoutParams(params);

        openButton = (Button) findViewById(R.id.open);
        openButton.setLayoutParams(params);

        openButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isClick == false) {
                    isClick = true;
                    openButton.startAnimation(animRotate(90f, 0.5f, 0.45f));
                    stickerButton.startAnimation(animTranslate(60, -400, 60, height - 700, stickerButton, 80));
                    borderButton.startAnimation(animTranslate(330, -260, 330, height - 560, borderButton, 100));
                    clearButton.startAnimation(animTranslate(400, 0, 440, height - 300, clearButton, 120));

                } else {
                   moveBack();
                }

            }
        });
        stickerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stickerButton.startAnimation(setAnimScale(2, 2));
                borderButton.startAnimation(setAnimScale(0.0f, 0.0f));
                clearButton.startAnimation(setAnimScale(0.0f, 0.0f));
                openButton.startAnimation(setAnimScale(0.0f, 0.0f));
                saveButton.startAnimation(setAnimScale(0.0f, 0.0f));
                chooseSticker();
                moveBack();
            }
        });
        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                saveButton.startAnimation(setAnimScale(2, 2));
                stickerButton.startAnimation(setAnimScale(0, 0));
                borderButton.startAnimation(setAnimScale(0.0f, 0.0f));
                clearButton.startAnimation(setAnimScale(0.0f, 0.0f));
                openButton.startAnimation(setAnimScale(0.0f, 0.0f));
                if(isClick == true){
                    moveBack();
                }
                shareImage();
            }
        });
        borderButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                borderButton.startAnimation(setAnimScale(2, 2));
                stickerButton.startAnimation(setAnimScale(0.0f, 0.0f));
                clearButton.startAnimation(setAnimScale(0.0f, 0.0f));
                openButton.startAnimation(setAnimScale(0.0f, 0.0f));
                saveButton.startAnimation(setAnimScale(0.0f, 0.0f));
                chooseBorder();
                moveBack();
            }
        });
        clearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                clearButton.startAnimation(setAnimScale(2, 2));
                stickerButton.startAnimation(setAnimScale(0.0f, 0.0f));
                borderButton.startAnimation(setAnimScale(0.0f, 0.0f));
                openButton.startAnimation(setAnimScale(0.0f, 0.0f));
                saveButton.startAnimation(setAnimScale(0.0f, 0.0f));
                clearStickers();
            }
        });
    }

    private void moveBack(){
        isClick = false;
        openButton.startAnimation(animRotate(0, 0.5f, 0.45f));
        stickerButton.startAnimation(animTranslate(0, 300, 50, height - 300, stickerButton, 180));
        borderButton.startAnimation(animTranslate(-200, 200, 50, height - 300, borderButton, 160));
        clearButton.startAnimation(animTranslate(-300, 0, 50, height - 300, clearButton, 140));
    }

    protected Animation setAnimScale(float toX, float toY)
    {
        // TODO Auto-generated method stub
        animationScale = new ScaleAnimation(1f, toX, 1f, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.45f);
        animationScale.setInterpolator(DisplayImageActivity.this, anim.accelerate_decelerate_interpolator);
        animationScale.setDuration(500);
        animationScale.setFillAfter(false);
        return animationScale;

    }

    protected Animation animRotate(float toDegrees, float pivotXValue, float pivotYValue)
    {
        // TODO Auto-generated method stub
        animationRotate = new RotateAnimation(0, toDegrees, Animation.RELATIVE_TO_SELF, pivotXValue, Animation.RELATIVE_TO_SELF, pivotYValue);
        animationRotate.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                // TODO Auto-generated method stub
                animationRotate.setFillAfter(true);
            }
        });
        return animationRotate;
    }

    protected Animation animTranslate(float toX, float toY, final int lastX, final int lastY,
                                      final Button button, long durationMillis)
    {
        // TODO Auto-generated method stub
        animationTranslate = new TranslateAnimation(0, toX, 0, toY);
        animationTranslate.setAnimationListener(new AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation animation)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                // TODO Auto-generated method stub
                params = new RelativeLayout.LayoutParams(0, 0);
                params.height = 200;
                params.width = 200;
                params.setMargins(lastX, lastY, 0, 0);
                button.setLayoutParams(params);
                button.clearAnimation();

            }
        });
        animationTranslate.setDuration(durationMillis);
        return animationTranslate;
    }



    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //print(""+requestCode);
        //print(""+resultCode);
        if(data!=null) {
            switch (requestCode) {
                case RESULT_CANCELED:
                    break;
                case sticker:
                    Bundle stickerBundle = data.getExtras();
                    //print(stickerBundle.toString());
                    String stickerPosition;
                    if (stickerBundle == null) {
                        Bundle extras = getIntent().getExtras();
                        if (extras == null) {
                            stickerPosition = null;
                        } else {
                            stickerPosition = extras.getString("id");
                        }
                    } else {
                        stickerPosition = (String) stickerBundle.getSerializable("id");
                    }
                    AddSticker(stickerPosition);
                    break;
                case border:
                    Bundle boarderBundle = data.getExtras();
                    //print(stickerBundle.toString());
                    String boarderPosition;
                    if (boarderBundle == null) {
                        Bundle extras = getIntent().getExtras();
                        if (extras == null) {
                            boarderPosition = null;
                        } else {
                            boarderPosition = extras.getString("id");
                        }
                    } else {
                        boarderPosition = (String) boarderBundle.getSerializable("id");
                    }
                    addBorder(boarderPosition);
                    //print(boarderPosition);
                    break;
            /*
            default:
                createBack();
                break;
            */
            }
        }
    }
     /*
         * Receive image uri. Get image path. Display image.
         **/

    private Uri createBack() {
        final Uri uri = getIntent().getData();
        String filePath = getPath(uri);
        System.out.print(filePath);
        myImage.setImageBitmap(getBitmap(filePath));
        return uri;
    }

    //Create a intent to choose stickers
    private void chooseSticker() {
        Intent intent = new Intent();
        intent.setClass(DisplayImageActivity.this, Sticker_Selector.class);
        //intent.putExtra("type", "sticker");
        startActivityForResult(intent, sticker);
    }

    //Creat a intent to choose boarders
    private void chooseBorder() {
        Intent intent = new Intent();
        intent.setClass(DisplayImageActivity.this, Border_Selector.class);
        //intent.putExtra("type", "border");
        startActivityForResult(intent, border);
        //print("success");
    }

    //get the bitmap from filepath
    public Bitmap getBitmap(String filePath){
        int degree = readPictureDegree(filePath);
        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inSampleSize=2;
        Bitmap bitmapOld = BitmapFactory.decodeFile(filePath, opts);
        return rotatingImageView(degree, bitmapOld);
    }

    //get the filepath from uri
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    //combine all the layers into a bitmap
    public Bitmap outputImage (myImageView[] imageView){
        Bitmap output;
        Bitmap background = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        output = Bitmap.createBitmap(background,0,0,screenWidth, screenHeight);
        Canvas c = new Canvas(output);
        myImage.draw(c);
        borderImage.draw(c);
        for(int a= i;a>0;a--){
            paint.reset();
            c.translate(imageView[a].viewL, imageView[a].viewT);
            Bitmap bm=imageView[a].getBitmap();
            Matrix mx=imageView[a].getMyMatrix();
            c.drawBitmap(bm,mx,paint);
            c.translate(-imageView[a].viewL, -imageView[a].viewT);
        }
        return output;
    }



    //clear all stickers
    public void clearStickers(){
        for(int a= i;a>0;a--){
            imageView[a].setImageDrawable(null);
            mainLayout.removeView(imageView[a]);
        }
        i = 0;
        borderImage.setImageDrawable(null);
        //mainLayout.removeView(borderImage);
    }

    //delete sticker
    public void deleteSticker(myImageView mimageView){
        mimageView.setImageBitmap(getResource(1), new Point(0, 0), 0, 0);
        mainLayout.removeView(mimageView);
    }

    //add sticker
    public void AddSticker(String name){
        int a = Integer.parseInt(name);
        i++;
        imageView[i] = new myImageView(this, getResource(a));
        //imageView[i].setImageBitmap(mBitmap);
        imageView[i].setOnTouchListener(movingEventListener);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp1.height = 200;
        lp1.width = 200;
        mainLayout.addView(imageView[i], lp1);
    }


    //test method
    public void addBorder(String name) {
        int a = Integer.parseInt(name);
        Bitmap mBitmap = getBorderResource(a);
        borderImage.setImageBitmap(mBitmap);
    }

    //get the bitmap from sticker id
    public Bitmap getResource(int i){
        TypedArray ar = getResources().obtainTypedArray(R.array.sticker);
        Bitmap bm=BitmapFactory.decodeResource(getResources(), ar.getResourceId(i, 0));
        ar.recycle();
        return bm;
    }

    //get the bitmap from border id
    public Bitmap getBorderResource(int i){
        TypedArray ar = getResources().obtainTypedArray(R.array.border);
        Bitmap bm=BitmapFactory.decodeResource(getResources(), ar.getResourceId(i, 0));
        ar.recycle();
        return bm;
        //return null;
    }

    /*
     * Get image rotate degree
     **/
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * Rotate image
     **/
    public static Bitmap rotatingImageView(int angle , Bitmap bitmap) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        return Bitmap.createBitmap(bitmap, 0, 0,
                bWidth, bHeight, matrix, true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_image, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void shareImage(){
        Intent intent = new Intent();
        intent.setClass(DisplayImageActivity.this, ShareImageActivity.class);
        Bitmap bm = outputImage(imageView);
        saveBitmap(bm);
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bm,null,null));
        intent.setData(uri);
        intent.putExtra("myPath", myPath);
        startActivity(intent);
        //setContentView(R.layout.null_layout);
    }

    //save image
    public void saveBitmap(Bitmap bm) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmm", Locale.UK);
        Date now = new Date();
        String fileName = formatter.format(now) + ".png";
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/Pictures/", fileName);
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            myPath = Environment.getExternalStorageDirectory().getPath()+"/Pictures/" + fileName;
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private OnTouchListener movingEventListener = new OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    modeChooser(v, event);
                    break;
                case MotionEvent.ACTION_UP:
                    if(mode == DELETE){
                    deleteSticker((myImageView)v);
                }
                    break;
                case MotionEvent.ACTION_POINTER_UP:

                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == ZOOM_OR_ROTATE) {
                        zoomAndRotate(v, event);
                    }
                    if (mode == DRAG) {
                        drag(v, event);
                    }
                    break;
            }
            return true;
        }
    };

    private void modeChooser(View v, MotionEvent event){
        if(v==myImage){
            if(currentImage != null){
                currentImage.setEditable(false);
                mode = NONE;}
            if(currentImage == null){
                mode = NONE;
            }
        }
        if(v!=myImage){
            if(currentImage != null){
                currentImage.setEditable(false);
                ((myImageView)v).setEditable(true);
                currentImage = (myImageView)v;}
            if(currentImage == null){
                ((myImageView)v).setEditable(true);
                currentImage = (myImageView)v;
            }
            ((myImageView)v).pA.set(event.getX() + ((myImageView)v).viewL, event.getY() + ((myImageView)v).viewT);
            if (((myImageView)v).isactiondownicon((int) event.getX(), (int) event.getY()) == 2) {
                mode = ZOOM_OR_ROTATE;
            }
            if (((myImageView)v).isactiondownicon((int) event.getX(), (int) event.getY()) == 1) {
                mode = DELETE;
            }

            if (((myImageView)v).isactiondownicon((int) event.getX(), (int) event.getY()) == 0) {
                mode = DRAG;
            }
        }
    }

    private void zoomAndRotate(View v, MotionEvent event){
        float sf;
        ((myImageView)v).pB.set(event.getX() + ((myImageView)v).viewL, event.getY() + ((myImageView)v).viewT);
        float realL = (float) Math.sqrt((float) (((myImageView)v).mBitmap.getWidth()
                * ((myImageView)v).mBitmap.getWidth() + ((myImageView)v).mBitmap.getHeight()
                * ((myImageView)v).mBitmap.getHeight()) / 4);
        float newL = (float) Math.sqrt((((myImageView)v).pB.x - (float) ((myImageView)v).cpoint.x)
                * (((myImageView)v).pB.x - (float) ((myImageView)v).cpoint.x) + (((myImageView)v).pB.y - (float) ((myImageView)v).cpoint.y)
                * (((myImageView)v).pB.y - (float) ((myImageView)v).cpoint.y));

        sf = newL / realL;
        double a = ((myImageView)v).spacing(((myImageView)v).pA.x, ((myImageView)v).pA.y, (float) ((myImageView)v).cpoint.x,
                (float) ((myImageView)v).cpoint.y);
        double b = ((myImageView)v).spacing(((myImageView)v).pB.x, ((myImageView)v).pB.y, ((myImageView)v).pA.x, ((myImageView)v).pA.y);
        double c = ((myImageView)v).spacing(((myImageView) v).pB.x, ((myImageView) v).pB.y, (float) ((myImageView) v).cpoint.x,
                (float) ((myImageView) v).cpoint.y);
        double cosB = (a * a + c * c - b * b) / (2 * a * c);
        if (cosB > 1) {
            cosB = 1f;
        }
        double angleB = Math.acos(cosB);
        float newAngle = (float) (angleB / Math.PI * 180);

        float p1x = ((myImageView)v).pA.x - (float) ((myImageView)v).cpoint.x;
        float p2x = ((myImageView)v).pB.x - (float) ((myImageView)v).cpoint.x;
        float p1y = ((myImageView)v).pA.y - (float) ((myImageView)v).cpoint.y;
        float p2y = ((myImageView)v).pB.y - (float) ((myImageView)v).cpoint.y;

        if (p1x == 0) {
            if (p2x > 0 && p1y >= 0 && p2y >= 0) {
                newAngle = -newAngle;
            } else if (p2x < 0 && p1y < 0 && p2y < 0) {
                newAngle = -newAngle;
            }
        } else if (p2x == 0) {
            if (p1x < 0 && p1y >= 0 && p2y >= 0) {
                newAngle = -newAngle;
            } else if (p1x > 0 && p1y < 0 && p2y < 0) {
                newAngle = -newAngle;
            }
        } else if ( p1y / p1x < p2y / p2x) {
            if (p1x < 0 && p2x > 0 && p1y >= 0 && p2y >= 0) {
                newAngle = -newAngle;
            } else if (p2x < 0 && p1x > 0 && p1y < 0 && p2y < 0) {
                newAngle = -newAngle;
            }
        } else {
                newAngle = -newAngle;
        }
        ((myImageView)v).pA.x = ((myImageView)v).pB.x;
        ((myImageView)v).pA.y = ((myImageView)v).pB.y;
        if (sf == 0) {
            sf = 0.1f;
        } else if (sf >= 3) {
            sf = 3f;
        }
        ((myImageView)v).setImageBitmap(((myImageView)v).mBitmap, ((myImageView)v).cpoint, ((myImageView)v).angle + newAngle, sf);

    }

    private void drag(View v, MotionEvent event){
        ((myImageView)v).pB.set(event.getX() + ((myImageView)v).viewL, event.getY() + ((myImageView) v).viewT);
        ((myImageView)v).cpoint.x += ((myImageView)v).pB.x - ((myImageView)v).pA.x;
        ((myImageView)v).cpoint.y += ((myImageView)v).pB.y - ((myImageView)v).pA.y;
        ((myImageView)v).pA.x = ((myImageView)v).pB.x;
        ((myImageView)v).pA.y = ((myImageView)v).pB.y;
        ((myImageView)v).setCPoint(((myImageView) v).cpoint);

    }

    //print debug info
    public void print(String info){
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

}


