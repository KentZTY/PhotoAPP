package com.example.yang.myphoto4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CAPTURE_CAMERA = 2;
    private static final int REQUEST_CAMERA_IRIS = 3;
    private String selectedImagePath1;
    private Uri uri;

    private int isSysCamera = 0;
    private String[] selectItem = new String[]{"SystemCamera", "MyCamera"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getBooleanExtra("close", false)) {
            DisplayImageActivity.instance.finish();
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
                        intent.setClass(MainActivity.this, Login.class);
                        //intent.putExtra("close", true);
                        startActivity(intent);
                    }
                });

        (findViewById(R.id.helpmain))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent help = new Intent();
                        help.setClass(MainActivity.this, Help.class);
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
                            Dialog dialog =new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Camera")
                                    .setSingleChoiceItems(selectItem, 0, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            isSysCamera=which;
                                        }
                                    })
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (isSysCamera==0){
                                                Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                                startActivityForResult(intent, REQUEST_CAPTURE_CAMERA);

                                            }else {
                                                Intent intent=new Intent();
                                                intent.setClass(MainActivity.this,CameraActivity.class);
                                                startActivityForResult(intent, REQUEST_CAPTURE_CAMERA);
                                            }
                                            dialog.dismiss();
                                            isSysCamera=0;
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Make sure you've inserted SD card.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        (findViewById(R.id.irisButton))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA_IRIS);
                        } else {
                            Toast.makeText(getApplicationContext(), "Make sure you've inserted SD card.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /*
     * Get image data(uri) and image path.
     * Deliver image Uri to DisplayImageActivity.
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            if (requestCode != REQUEST_CAMERA_IRIS) {
                intent.setClass(MainActivity.this, DisplayImageActivity.class);
                switch (requestCode) {
                    case SELECT_PICTURE:
                        uri = data.getData();
                        selectedImagePath1 = getPath(uri);
                        System.out.println("Image Path : " + selectedImagePath1);
                        break;
                    case REQUEST_CAPTURE_CAMERA:
                        uri = data.getData();
                        break;
                    default:
                        break;
                }
            }
            if (requestCode == REQUEST_CAMERA_IRIS) {
                intent.setClass(MainActivity.this, Iris.class);
                uri = data.getData();
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
}
