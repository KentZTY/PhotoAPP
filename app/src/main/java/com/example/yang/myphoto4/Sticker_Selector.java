package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Ree on 2015/7/31.
 */
public class Sticker_Selector extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticker_selector);
        GridView gridView=(GridView)findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(Sticker_Selector.this, DisplayImageActivity.class);

                intent.putExtra("id", position + "");
                setResult(RESULT_OK, intent);

                setContentView(R.layout.null_layout);
                //finishActivity(1);
                finish();


            }
        });

    }
    public class ImageAdapter extends BaseAdapter {
        int size=300;
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return mThumbIds[position];
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            getImages();
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(size,size));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            getImages();
            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds=new Integer[getC()];


        public void getImages() {
            TypedArray ar = getResources().obtainTypedArray(R.array.sticker);
            int len = ar.length();
            int[] resIds = new int[len];
            //Integer[] temp = new Integer[len++];
            for (int i = 0; i < len; i++){
                resIds[i] = ar.getResourceId(i, 0);
                mThumbIds[i]=resIds[i];}
            ar.recycle();
        }

        private int getC(){
            TypedArray ar = getResources().obtainTypedArray(R.array.sticker);
            int len = ar.length();
            return len;
        }
    }
}
