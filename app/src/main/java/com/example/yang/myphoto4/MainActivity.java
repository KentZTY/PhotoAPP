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

public class MainActivity extends Activity implements View.OnClickListener{
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CAPTURE_CAMERA = 2;
    private String selectedImagePath1;
    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Select image button. Use intent to open gallery and select image.
         **/
        (findViewById(R.id.button01))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                    }
                });

        /*
         * Camera function button. Check out SD card.
         * Use intent to open local applications for camera(image capture).
         * Create image file.
         **/
        (findViewById(R.id.button02))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                                /*try {
                                    File dir=new File(Environment.getExternalStorageDirectory() + "/"+ "localTempImgDir");
                                    if(!dir.exists()) {
                                        dir.mkdirs();
                                    }*/
                                    Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                   /*File f=new File(dir, "localTempImgFileName");
                                   Uri u=Uri.fromFile(f);
                                   intent.putExtra(MediaStore.EXTRA_OUTPUT, u);*/
                                    startActivityForResult(intent, REQUEST_CAPTURE_CAMERA);
                                /*} catch (ActivityNotFoundException e) {
                                    Toast.makeText(MainActivity.this, "No storage directory.",Toast.LENGTH_LONG).show();
                                }*/
                        }
                        else {
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
            intent.setClass(MainActivity.this, DisplayImageActivity.class);
            switch (requestCode){
                case SELECT_PICTURE:
                    uri = data.getData();
                    selectedImagePath1 = getPath(uri);
                    System.out.println("Image Path : " + selectedImagePath1);
                    break;
                /*case REQUEST_CAPTURE_CAMERA:
                    File f=new File(Environment.getExternalStorageDirectory()
                            +"/"+"localTempImgDir"+"/"+"localTempImgFileName");
                    try {
                        uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                        f.getAbsolutePath(), null, null));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;*/
                case REQUEST_CAPTURE_CAMERA:
                    uri = data.getData();
                    break;
                default:
                    break;
            }
            intent.setData(uri);
            startActivityForResult(intent,0);
        }
    }
        /*
         * Get path function.
         **/

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button03){
            startActivity(new Intent(MainActivity.this, OperatorImageActivity.class));
        }
    }
}
