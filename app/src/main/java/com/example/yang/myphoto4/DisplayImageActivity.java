package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayImageActivity extends Activity{
    private int screenWidth;
    private int screenHeight;
    private int stickerNumber;
    private int i;
    private ImageView currentImage = null;
    private ImageView[] imageView;
    RelativeLayout mainLayout;
    private ImageView myImage;
    private ImageView borderImage;
    private static final int sticker = 1;
    private static final int border = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        mainLayout = (RelativeLayout)findViewById(R.id.stickerView);
        myImage =(ImageView)findViewById(R.id.imageView);
        borderImage =(ImageView)findViewById(R.id.borderView);
        borderImage.setImageDrawable(null);
        i = 0;
        stickerNumber = 10;
        imageView = new ImageView[stickerNumber+1];
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 100;
        currentImage = myImage;
        myImage.setOnTouchListener(movingEventListener);

        (findViewById(R.id.sticker))
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
        createBack();

        /*
        Send image to the next activity.
         */
        (findViewById(R.id.button04))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        shareImage();
                        finish();
                    }
                });

        }
    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //print(""+requestCode);
        //print(""+resultCode);
        switch (requestCode){
            case RESULT_CANCELED:
            break;
            case sticker:
                Bundle stickerBundle = data.getExtras();
                //print(stickerBundle.toString());
                String stickerPosition;
                if (stickerBundle == null) {
                    Bundle extras = getIntent().getExtras();
                    if(extras == null) {
                        stickerPosition= null;
                    } else {
                        stickerPosition= extras.getString("id");
                    }
                } else {
                    stickerPosition= (String) stickerBundle.getSerializable("id");
                }
                testAddSticker(stickerPosition);
                break;
            case border:
                Bundle boarderBundle = data.getExtras();
                //print(stickerBundle.toString());
                String boarderPosition;
                if (boarderBundle == null) {
                    Bundle extras = getIntent().getExtras();
                    if(extras == null) {
                        boarderPosition= null;
                    } else {
                        boarderPosition= extras.getString("id");
                    }
                } else {
                    boarderPosition= (String) boarderBundle.getSerializable("id");
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
     /*
         * Receive image uri. Get image path. Display image.
         **/

    private Uri createBack(){
        final Uri uri = getIntent().getData();
        String filePath = getPath(uri);
        System.out.print(filePath);
        myImage.setImageBitmap(getBitmap(filePath));
        return uri;
    }

    //Creat a intent to choose stickers
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
    public Bitmap outputImage (ImageView[] imageView){
        Bitmap output=null;
        output = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(output);
        myImage.draw(c);
        for(int a= i;a>0;a--){
            int left = ((RelativeLayout.LayoutParams)imageView[a].getLayoutParams()).leftMargin;
            int top = ((RelativeLayout.LayoutParams)imageView[a].getLayoutParams()).topMargin;
            int width = ((RelativeLayout.LayoutParams)imageView[a].getLayoutParams()).width;
            int height = ((RelativeLayout.LayoutParams)imageView[a].getLayoutParams()).height;
            imageView[a].getDrawable().setBounds(left, top, width + left, height + top);
            imageView[a].getDrawable().draw(c);
        }
        /*
        if(borderImage.getDrawable()!=null){
            borderImage.getDrawable().setBounds(0,0,0,0);
            borderImage.getDrawable().draw(c);
        }
        */
        return output;
    }


    //clear all stickers
    public void clearStickers(){
        for(int a= i;a>0;a--){
            imageView[a].setImageDrawable(null);
            mainLayout.removeView(imageView[a]);
        }
        i = 0;
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
    public void testAddSticker(String name){
        int a = Integer.parseInt(name)+1;
        i++;
        imageView[i] = new ImageView(this);
        Bitmap mBitmap = getResource(a);
        imageView[i].setImageBitmap(mBitmap);
        imageView[i].setOnTouchListener(movingEventListener);
        //imageView[i].setBackgroundResource(R.drawable.border);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp1.height = 500;
        lp1.width = 500;
        //lp1.addRule(RelativeLayout.ALIGN_TOP);
        //lp1.setMargins(200,400,0,0);//(int left, int top, int right, int bottom)
        mainLayout.addView(imageView[i],lp1);

    }

    //test method
    public void addBorder(String name){
        int a = Integer.parseInt(name)+1;
        Bitmap mBitmap = getBorderResource(a);
        borderImage.setImageBitmap(mBitmap);
    }

    //get the bitmap from sticker id
    public Bitmap getResource(int i){
        String name="a"+i;
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
        //return null;
    }

    //get the bitmap from border id
    public Bitmap getBorderResource(int i){
        String name="b"+i;
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
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bm,null,null));
        intent.setData(uri);
        startActivity(intent);
        setContentView(R.layout.null_layout);
        finish();
    }

    public void saveBitmap(Bitmap bm) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        String fileName = formatter.format(now) + ".png";
        File f = new File("/sdcard/DCIM/Screenshots", fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
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

    }




    private OnTouchListener movingEventListener = new OnTouchListener() {
        int lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RelativeLayout.LayoutParams myLayout = (RelativeLayout.LayoutParams) v.getLayoutParams();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(v!=myImage){
                        currentImage.setBackground(null);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        v.setBackgroundResource(R.drawable.border);
                        currentImage =(ImageView) v;}
                    if(v == myImage){
                        currentImage.setBackground(null);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;

                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }

                    myLayout.setMargins(left, top, 0, 0);
                    v.setLayoutParams(myLayout);


                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };

    //print debug info
    public void print(String info){
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

}


