package com.example.yang.myphoto4;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Ree on 2015/8/1.
 */
public class BorderAdapter extends BaseAdapter {
    private Context mContext;
    private TypedArray ar;
    private Resources res;


    public BorderAdapter(Context c) {
        this.mContext = c;
    }
    public void setAr(TypedArray thisar){
        ar=thisar;
    }


    public void setRes(Resources resources){
        res=resources;
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
    // references to our images
    private int[] mThumbIds=getImage();

    //public void setImage(int[] imagei){        mThumbIds=imagei;    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    private int[] getImage() {
        TypedArray ar = res.obtainTypedArray(R.array.border);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);
        ar.recycle();
        for(int i:resIds){
            //print(""+i);
        }
        return resIds;
    }





}
