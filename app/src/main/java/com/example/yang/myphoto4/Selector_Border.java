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
public class Selector_Border extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new BorderAdapter(this));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(Selector_Border.this, Display_Image.class);
                intent.putExtra("id", position + "");
                setResult(RESULT_OK, intent);
                setContentView(R.layout.null_layout);
                //finishActivity(1);
                finish();


            }
        });

    }

    public class BorderAdapter extends BaseAdapter {

        int size = 300;
        private Context mContext;
        // references to our images
        private Integer[] mThumbIds = new Integer[getC()];

        public BorderAdapter(Context c) {
            mContext = c;
            getImages();
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

            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(size, size));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        public void getImages() {
            TypedArray ar = getResources().obtainTypedArray(R.array.border_cr);
            int len = ar.length();
            int[] resIds = new int[len];
            //Integer[] temp = new Integer[len++];
            for (int i = 0; i < len; i++) {
                resIds[i] = ar.getResourceId(i, 0);
                mThumbIds[i] = resIds[i];
            }
            ar.recycle();
        }

        private int getC() {
            TypedArray ar = getResources().obtainTypedArray(R.array.border_cr);
            int len = ar.length();
            ar.recycle();
            return len;
        }
    }
}

