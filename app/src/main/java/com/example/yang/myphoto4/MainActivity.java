package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity
{
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CAPTURE_CAMERA = 1;
    private String selectedImagePath1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (findViewById(R.id.button01))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                    }
                });
        (findViewById(R.id.button02))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(getImageByCamera, REQUEST_CAPTURE_CAMERA);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Make sure you've insert SD card.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath1 = getPath(selectedImageUri);
                System.out.println("Image Path : " + selectedImagePath1);
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, DisplayImageActivity.class);
                intent1.setData(selectedImageUri);
                startActivity(intent1);
                this.finish();
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
