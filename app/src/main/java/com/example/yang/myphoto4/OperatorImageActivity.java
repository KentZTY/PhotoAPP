package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class OperatorImageActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private ToneLayer mToneLayer;
    private ImageView mImageView;
    private Bitmap mBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operator_activity);

        init();
    }

    private void init()
    {
        mToneLayer = new ToneLayer(this);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mImageView = (ImageView) findViewById(R.id.img_view);
        mImageView.setImageBitmap(mBitmap);
        ((LinearLayout) findViewById(R.id.tone_view)).addView(mToneLayer.getParentView());

        ArrayList<SeekBar> seekBars = mToneLayer.getSeekBars();
        for (int i = 0, size = seekBars.size(); i < size; i++)
        {
            seekBars.get(i).setOnSeekBarChangeListener(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);

                cursor.close();
                BitmapFactory.Options opts=new BitmapFactory.Options();
                opts.inSampleSize=2;
                mBitmap = BitmapFactory.decodeFile(imgDecodableString, opts);
                mImageView.setImageBitmap(mBitmap);

            } else {
                Toast.makeText(this, "您未选择图片！",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "未知错误，请重试！（只能传本地图片）", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        int flag = (Integer) seekBar.getTag();
        switch (flag)
        {
            case ToneLayer.FLAG_SATURATION:
                mToneLayer.setSaturation(progress);
                break;
            case ToneLayer.FLAG_LUM:
                mToneLayer.setLum(progress);
                break;
            case ToneLayer.FLAG_HUE:
                mToneLayer.setHue(progress);
                break;
        }

        mImageView.setImageBitmap(mToneLayer.handleImage(mBitmap, flag));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.operator_chosenBtn){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "请选择头像"), 10000);
        }
    }
}
