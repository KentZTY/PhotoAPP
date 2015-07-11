package com.example.yang.myphoto4;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;


/**
 * Created by Ree on 2015/7/9.
 */
public class ImageOperation extends Activity {
    private ImageView imageView;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;
    private ImageView imageView7;
    private ImageView imageView8;
    private ImageView imageView9;
    private ImageView imageView10;
    private Bitmap photoBitmap, stickerBitmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView=(ImageView)findViewById(R.id.imageView);
        imageView2=(ImageView)findViewById(R.id.imageView2);
        imageView3=(ImageView)findViewById(R.id.imageView3);
        imageView4=(ImageView)findViewById(R.id.imageView4);
        imageView5=(ImageView)findViewById(R.id.imageView5);
        imageView6=(ImageView)findViewById(R.id.imageView6);
        imageView7=(ImageView)findViewById(R.id.imageView7);
        imageView8=(ImageView)findViewById(R.id.imageView8);
        imageView9=(ImageView)findViewById(R.id.imageView9);
        imageView10=(ImageView)findViewById(R.id.imageView10);

    }
    public ImageView combine (ImageView a, ImageView b){
        ImageView combined;
        Bitmap bitA = ((BitmapDrawable)a.getDrawable()).getBitmap();
        Bitmap bitB = ((BitmapDrawable)b.getDrawable()).getBitmap();






        combined=a;
        return combined;

    }
}

