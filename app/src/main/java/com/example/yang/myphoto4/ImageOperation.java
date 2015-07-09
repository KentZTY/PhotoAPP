package com.example.yang.myphoto4;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;


/**
 * Created by Ree on 2015/7/9.
 */
public class ImageOperation extends Activity {
    private ImageView photo;
    private ImageView sticker;
    private Bitmap photoBitmap, stickerBitmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stickers_add_image);

    }

}

