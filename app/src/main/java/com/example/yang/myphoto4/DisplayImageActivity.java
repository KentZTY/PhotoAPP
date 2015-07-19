package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class DisplayImageActivity extends Activity implements View.OnTouchListener, OnGestureListener{

    private ImageView myImg = null;
    private ImageView[] imageView = new ImageView[11];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        myImg = (ImageView)findViewById(R.id.imageView);
        imageView[1]=(ImageView)findViewById(R.id.imageView);//origin image
        imageView[2]=(ImageView)findViewById(R.id.imageView2);//enhanced image 1
        imageView[3]=(ImageView)findViewById(R.id.imageView3);//enhanced image 2
        imageView[4]=(ImageView)findViewById(R.id.imageView4);//enhanced image 3
        imageView[5]=(ImageView)findViewById(R.id.imageView5);//combined stickers layer
        imageView[6]=(ImageView)findViewById(R.id.imageView6);//sticker layer 1
        imageView[7]=(ImageView)findViewById(R.id.imageView7);//sticker layer 2
        imageView[8]=(ImageView)findViewById(R.id.imageView8);//sticker layer 3
        imageView[9]=(ImageView)findViewById(R.id.imageView9);//sticker layer 4
        imageView[10]=(ImageView)findViewById(R.id.imageView10);//border layer
        /*
        for (int i=1;i<11;i++){
            imageView[i].setBackgroundColor(Color.TRANSPARENT);
        }
        */

        (findViewById(R.id.add))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        testAdd();
                    }
                });
        (findViewById(R.id.undo))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        testUndo();
                    }
                });
        (findViewById(R.id.export))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        testExport();
                    }
                });
        (findViewById(R.id.clear))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        clearStickers();
                    }
                });

        /*
         * Receive image uri. Get image path.
         **/
        final Uri uri = getIntent().getData();
        String[] projection = { MediaStore.Images.Media.DATA };
        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        System.out.print(filePath);

        /*
         * Get image orientation.
         **/
        int degree = readPictureDegree(filePath);
        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inSampleSize=2;
        Bitmap bitmapOld = BitmapFactory.decodeFile(filePath,opts);
        Bitmap bitmapNew = rotatingImageView(degree, bitmapOld);
        myImg.setImageBitmap(bitmapNew);

        (findViewById(R.id.button04))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        shareImage(uri);
                    }
                });

    }

    //add a new sticker in layer 9, high layers imported to lower one and import the newest into layer 9
    public void newSticker(Bitmap nS){
        if(imageView[9]==null){
            imageView[9].setImageBitmap(nS);
        }else{
            imageView[5].setImageBitmap(combine(imageView[5],imageView[6]));
            for (int i=6;i<9;i++){
                imageView[i].setImageDrawable(imageView[i+1].getDrawable());

            }
            imageView[9].setImageBitmap(nS);
        }
    }

    //combine two imageView and output a bitmap
    public Bitmap combine (ImageView a, ImageView b){
        Bitmap combined=null;
        try{
            //output resolution needed _Ree
            combined = Bitmap.createBitmap(2560, 1440, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(combined);
            Resources res = getResources();
            //a.setBounds(100, 100, 400, 400);
            //b.setBounds(150, 150, 350, 350);
            a.getDrawable().draw(c);
            b.getDrawable().draw(c);

        }
        catch (Exception e){
        }
        return combined;
    }

    //combine all the layers into a bitmap
    public Bitmap outputImage (ImageView[] imageView){
        Bitmap output=null;
        output = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(output);
        for(int i=0;i<=10;i++){
            imageView[i].getDrawable().draw(c);
        }
        return output;
    }

    //undo a sticker
    public void undoSticker(){
        for(int i=9;i>5;i--){
            if(imageView[i].getDrawable()== null){
                print(i+" is null");

            }else{
                imageView[i].setImageDrawable(null);
                break;
            }
            if(i==6){
                //print("Can not undo");
                Toast.makeText(getApplicationContext(), "Can not undo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //clear all stickers
    public void clearStickers(){
        for(int i=9;i>4;i--){
            imageView[i].setImageDrawable(null);
        }
    }

    //print debug info
    public void print(String info){
        //Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

    //save bitmap to local
    public void saveBitmap(Bitmap bmp, String fileName){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //test method
    public void testAdd(){
        Random random=new Random();
        int i=random.nextInt();
        i=random.nextInt(4)+1;
        newSticker(getResource(i));
        // print("test add");

    }
    public void testUndo(){
        undoSticker();
        //print("test undo");
    }
    public void testExport(){
        saveBitmap(outputImage(imageView),"testFile");
        print("test export");
    }

    public Bitmap getResource(int i){
        String name="a"+i;
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(),resID);
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

        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bWidth, bHeight, matrix, true);
        return resizedBitmap;
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
    public void shareImage(Uri uri){
            Intent intent = new Intent();
            intent.setClass(DisplayImageActivity.this, ShareImageActivity.class);
        intent.setData(uri);
            startActivity(intent);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
