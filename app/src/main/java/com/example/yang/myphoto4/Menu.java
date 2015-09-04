package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Menu extends Activity {
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CAPTURE_CAMERA = 2;
    private static final int REQUEST_CAMERA_IRIS = 3;
    private String selectedImagePath1;
    private Uri uri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getBooleanExtra("close", false)) {
            Display_Image.instance.finish();
        }

        /*
         * Select image button. Use intent to open gallery and select image.
         **/
        (findViewById(R.id.photoButton))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                    }
                });

        (findViewById(R.id.sync))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent();
                        intent.setClass(Menu.this, Sticker_Login.class);
                        //intent.putExtra("close", true);
                        startActivity(intent);
                    }
                });

        (findViewById(R.id.helpmain))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent help = new Intent();
                        help.setClass(Menu.this, Help.class);
                        startActivity(help);
                    }
                });

        /*
         * Camera function button. Check out SD card.
         * Use intent to open local applications for camera(image capture).
         * Create image file.
         **/
        (findViewById(R.id.cameraButton))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                            File file=getOutputMediaFile(1);
                            uri = Uri.fromFile(file); // create
                            i.putExtra(MediaStore.EXTRA_OUTPUT,uri); // set the image file

                            startActivityForResult(i, REQUEST_CAMERA_IRIS);
                        } else {
                            Toast.makeText(getApplicationContext(), "Make sure you've inserted SD card.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        (findViewById(R.id.irisButton))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                            File file = getOutputMediaFile(1);
                            uri = Uri.fromFile(file); // create
                            i.putExtra(MediaStore.EXTRA_OUTPUT, uri); // set the image file

                            startActivityForResult(i, REQUEST_CAPTURE_CAMERA);
                        } else {
                            Toast.makeText(getApplicationContext(), "Make sure you've inserted SD card.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



    /*
     * Get image data(uri) and image path.
     * Deliver image Uri to Display_Image.
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            if (requestCode != REQUEST_CAMERA_IRIS) {
                intent.setClass(Menu.this, Display_Image.class);
                switch (requestCode) {
                    case SELECT_PICTURE:
                        uri = data.getData();
                        selectedImagePath1 = getPath(uri);
                        System.out.println("Image Path : " + selectedImagePath1);
                        break;
                    case REQUEST_CAPTURE_CAMERA:
                        break;
                    default:
                        break;
                }
            }
            if (requestCode == REQUEST_CAMERA_IRIS) {
                intent.setClass(Menu.this, Iris.class);
            }
            intent.setData(uri);
            startActivityForResult(intent, 0);
            //finish();
        }
    }
        /*
         * Get path function.
         **/

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        return filePath;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    /** Create a File for saving an image */
    private  File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyApplication");

        /**Create the storage directory if it does not exist*/
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        /**Create a media file name*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }
}
