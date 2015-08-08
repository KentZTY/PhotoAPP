package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    protected int stickerNumber;
    private int i;
    private myImageView currentImage = null;
    private myImageView[] imageView;
    RelativeLayout mainLayout;
    private ImageView myImage;
    private ImageView borderImage;
    private static final int sticker = 1;
    private static final int border = 2;
    private static final int NONE = 3;// 初始状态
    private static final int DRAG = 4;// 拖动
    private static final int ZOOM_OR_ROTATE = 5; // 缩放或旋转
    private static final int DELETE = 6;
    int mode = NONE;




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
        imageView = new myImageView[stickerNumber+1];
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 100;
        currentImage = null;
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
        (findViewById(R.id.save))
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
    public Bitmap outputImage (myImageView[] imageView){
        Bitmap output=null;
        output = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(output);
        myImage.draw(c);
        borderImage.draw(c);
        for(int a= i;a>0;a--){
            int left = ((RelativeLayout.LayoutParams)imageView[a].getLayoutParams()).leftMargin;
            int top = ((RelativeLayout.LayoutParams)imageView[a].getLayoutParams()).topMargin;
            int width = ((RelativeLayout.LayoutParams)imageView[a].getLayoutParams()).width;
            int height = ((RelativeLayout.LayoutParams)imageView[a].getLayoutParams()).height;
            //imageView[a].getDrawable().setBounds(left, top, width + left, height + top);
            //imageView[a].getDrawable().draw(c);
            Bitmap bm=imageView[a].getBitmap();
            Paint paint = new Paint();
            c.drawBitmap(bm,left,top,paint);

        }

        /*
        if(borderImage.getDrawable()!=null){
            print("success");
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
        borderImage.setImageDrawable(null);
        mainLayout.removeView(borderImage);
    }

    //delete sticker
    public void deleteSticker(ImageView imageView){
        mainLayout.removeView(imageView);
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


    //add sticker
    public void AddSticker(String name){
        int a = Integer.parseInt(name)+1;
        i++;
        imageView[i] = new myImageView(this, getResource(a));
        //imageView[i].setID(i);
        //imageView[i].setImageBitmap(mBitmap);
        imageView[i].setOnTouchListener(movingEventListener);
        //imageView[i].setBackgroundResource(R.drawable.border);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp1.height = 200;
        lp1.width = 200;
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




    private OnTouchListener movingEventListener;

    {
        movingEventListener = new OnTouchListener() {
            /*int lastX, lastY;

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
            }*/
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    // 主点按下
                    case MotionEvent.ACTION_DOWN:
                        if (v == myImage) {
                            if (currentImage != null) {
                                currentImage.setEditable(false);
                                mode = NONE;
                            }
                            if (currentImage == null) {
                                mode = NONE;
                            }
                        }
                        if (v != myImage) {
                            if (currentImage != null) {
                                currentImage.setEditable(false);
                                ((myImageView) v).setEditable(true);
                                currentImage = (myImageView) v;
                            }
                            if (currentImage == null) {
                                ((myImageView) v).setEditable(true);
                                currentImage = (myImageView) v;
                            }
                            ((myImageView) v).pA.set(event.getX() + ((myImageView) v).viewL, event.getY() + ((myImageView) v).viewT);
                            // pB.set(event.getX(), event.getY());
                            if (((myImageView) v).isactiondownicon((int) event.getX(), (int) event.getY()) == 2) {
                                mode = ZOOM_OR_ROTATE;
                            }
                            if (((myImageView) v).isactiondownicon((int) event.getX(), (int) event.getY()) == 1) {
                                mode = DELETE;
                            }

                            if (((myImageView) v).isactiondownicon((int) event.getX(), (int) event.getY()) == 0) {
                                mode = DRAG;
                            }

                            break;
                        }
                        // 副点按下
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // if (event.getActionIndex() > 1)
                        // break;
                        // dist = spacing(event.getX(0), event.getY(0), event.getX(1),
                        // event.getY(1));
                        // // 如果连续两点距离大于10，则判定为多点模式
                        // if (dist > 10f) {
                        // savedMatrix.set(matrix);
                        // pA.set(event.getX(0), event.getY(0));
                        // pB.set(event.getX(1), event.getY(1));
                        // mid.set((event.getX(0) + event.getX(1)) / 2,
                        // (event.getY(0) + event.getY(1)) / 2);
                        // mode = ZOOM_OR_ROTATE;
                        // }
                        // break;
                    case MotionEvent.ACTION_UP:
                        if (mode == DELETE) {
                            deleteSticker((myImageView) v);
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:

                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (mode == ZOOM_OR_ROTATE) {// 进行 放大缩小
                            float sf = 1f;
                            // float newx=event.getX();
                            // float newy=event.getY();
                            ((myImageView) v).pB.set(event.getX() + ((myImageView) v).viewL, event.getY() + ((myImageView) v).viewT);
                            float realL = (float) Math.sqrt((float) (((myImageView) v).mBitmap.getWidth()
                                    * ((myImageView) v).mBitmap.getWidth() + ((myImageView) v).mBitmap.getHeight()
                                    * ((myImageView) v).mBitmap.getHeight()) / 4);
                            float newL = (float) Math.sqrt((((myImageView) v).pB.x - (float) ((myImageView) v).cpoint.x)
                                    * (((myImageView) v).pB.x - (float) ((myImageView) v).cpoint.x) + (((myImageView) v).pB.y - (float) ((myImageView) v).cpoint.y)
                                    * (((myImageView) v).pB.y - (float) ((myImageView) v).cpoint.y));
                            // System.out.println("r,n:"+realL+"|"+newL);
                            sf = newL / realL;
                            // 角度
                            double a = ((myImageView) v).spacing(((myImageView) v).pA.x, ((myImageView) v).pA.y, (float) ((myImageView) v).cpoint.x,
                                    (float) ((myImageView) v).cpoint.y);
                            double b = ((myImageView) v).spacing(((myImageView) v).pB.x, ((myImageView) v).pB.y, ((myImageView) v).pA.x, ((myImageView) v).pA.y);
                            double c = ((myImageView) v).spacing(((myImageView) v).pB.x, ((myImageView) v).pB.y, (float) ((myImageView) v).cpoint.x,
                                    (float) ((myImageView) v).cpoint.y);
                            double cosB = (a * a + c * c - b * b) / (2 * a * c);
                            if (cosB > 1) {// 浮点运算的时候 cosB 有可能大于1.
                                System.out.println(" sf:" + sf + " cosB:" + cosB);
                                cosB = 1f;
                            }
                            double angleB = Math.acos(cosB);
                            float newAngle = (float) (angleB / Math.PI * 180);
                            // newAngle=0f;

                            float p1x = ((myImageView) v).pA.x - (float) ((myImageView) v).cpoint.x;
                            float p2x = ((myImageView) v).pB.x - (float) ((myImageView) v).cpoint.x;

                            float p1y = ((myImageView) v).pA.y - (float) ((myImageView) v).cpoint.y;
                            float p2y = ((myImageView) v).pB.y - (float) ((myImageView) v).cpoint.y;
                            // 正反向。
                            if (p1x == 0) {
                                if (p2x > 0 && p1y >= 0 && p2y >= 0) {// 由 第4-》第3
                                    newAngle = -newAngle;
                                } else if (p2x < 0 && p1y < 0 && p2y < 0) {// 由 第2-》第1
                                    newAngle = -newAngle;
                                }
                            } else if (p2x == 0) {
                                if (p1x < 0 && p1y >= 0 && p2y >= 0) {// 由 第4-》第3
                                    newAngle = -newAngle;
                                } else if (p1x > 0 && p1y < 0 && p2y < 0) {// 由 第2-》第1
                                    newAngle = -newAngle;
                                }
                            } else if (p1x != 0 && p2x != 0 && p1y / p1x < p2y / p2x) {
                                if (p1x < 0 && p2x > 0 && p1y >= 0 && p2y >= 0) {// 由 第4-》第3
                                    newAngle = -newAngle;
                                } else if (p2x < 0 && p1x > 0 && p1y < 0 && p2y < 0) {// 由
                                    // 第2-》第1
                                    newAngle = -newAngle;
                                } else {

                                }
                            } else {
                                if (p2x < 0 && p1x > 0 && p1y >= 0 && p2y >= 0) {// 由 第3-》第4

                                } else if (p2x > 0 && p1x < 0 && p1y < 0 && p2y < 0) {// 由
                                    // 第1-》第2

                                } else {
                                    newAngle = -newAngle;
                                }
                            }
                            ((myImageView) v).pA.x = ((myImageView) v).pB.x;
                            ((myImageView) v).pA.y = ((myImageView) v).pB.y;
                            if (sf == 0) {
                                sf = 0.1f;
                            } else if (sf >= 3) {
                                sf = 3f;
                            }
                            ((myImageView) v).setImageBitmap(((myImageView) v).mBitmap, ((myImageView) v).cpoint, ((myImageView) v).angle + newAngle, sf);

                        }

                        if (mode == DRAG) {

                            ((myImageView) v).pB.set(event.getX() + ((myImageView) v).viewL, event.getY() + ((myImageView) v).viewT);
                            // 修改中心点
                            ((myImageView) v).cpoint.x += ((myImageView) v).pB.x - ((myImageView) v).pA.x;
                            ((myImageView) v).cpoint.y += ((myImageView) v).pB.y - ((myImageView) v).pA.y;
                            ((myImageView) v).pA.x = ((myImageView) v).pB.x;
                            ((myImageView) v).pA.y = ((myImageView) v).pB.y;
                            // this.setImageBitmap(this.mBitmap, cpoint, jd, sfxs);
                            ((myImageView) v).setCPoint(((myImageView) v).cpoint);

                        }
                        break;
                }
                return true;
            }
        };
    }

    //print debug info
    public void print(String info){
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

}


